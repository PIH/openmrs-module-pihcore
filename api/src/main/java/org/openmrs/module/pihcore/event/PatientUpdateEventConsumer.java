package org.openmrs.module.pihcore.event;

import org.apache.commons.dbutils.handlers.ScalarHandler;
import org.apache.commons.lang.BooleanUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openmrs.module.dbevent.Database;
import org.openmrs.module.dbevent.DatabaseTable;
import org.openmrs.module.dbevent.DbEvent;
import org.openmrs.module.dbevent.DbEventLog;
import org.openmrs.module.dbevent.DbEventSourceConfig;
import org.openmrs.module.dbevent.EventConsumer;
import org.openmrs.module.dbevent.Operation;
import org.openmrs.module.dbevent.Rocks;
import org.openmrs.module.dbevent.SqlBuilder;

import java.io.File;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Consumes patient-related events
 * For performance reasons on larger databases, this does not use a Debezium-generated snapshot, but rather
 * computes a custom snapshot that is then updated as needed from change events.
 * When used, this should be configured with "snapshot.mode = schema_only"
 * The following tables have references to patient in some way but are not handled
 * <br/>
 * address_hierarchy_address_to_entry_map (references person_address)
 * paperrecord_paper_record (references patient_identifier)
 * paperrecord_paper_record_request (references paperrecord_paper_record)
 * paperrecord_paper_record_merge_request (references paperrecord_paper_record x 2)
 * name_phonetics (references person_name)
 * logic_rule_token (references person via creator)
 * logic_rule_token_tag (references logic_rule_token)
 * concept_proposal (references encounter and/or obs)
 * concept_proposal_tag_map (references concept_proposal)
 */
public class PatientUpdateEventConsumer implements EventConsumer {

    private static final Logger log = LogManager.getLogger(PatientUpdateEventConsumer.class);

    private final DbEventSourceConfig config;
    private final Database database;
    private final Map<String, String> patientKeys = new LinkedHashMap<>();
    private final Map<String, String> nonStandardKeys = new HashMap<>();
    private final Map<String, Integer> multipleColumnTables = new HashMap<>();
    private Rocks statusDb;
    private boolean snapshotInitialized = false;

    public PatientUpdateEventConsumer(DbEventSourceConfig config) {
        this.config = config;
        this.database = config.getContext().getDatabase();
        patientKeys.put("patient_id", "patient");
        patientKeys.put("person_id", "person");
        patientKeys.put("person_a", "person");
        patientKeys.put("person_b", "person");
        patientKeys.put("winner_person_id", "person");
        patientKeys.put("loser_person_id", "person");
        patientKeys.put("order_id", "orders");
        patientKeys.put("patient_program_id", "patient_program");
        patientKeys.put("encounter_id", "encounter");
        patientKeys.put("visit_id", "visit");
        patientKeys.put("allergy_id", "allergy");
        patientKeys.put("diagnosis_id", "encounter_diagnosis");
        patientKeys.put("order_group_id", "order_group");
        patientKeys.put("obs_id", "obs");
        patientKeys.put("appointment_id", "appointmentscheduling_appointment");
        patientKeys.put("diagnostic_report_id", "fhir_diagnostic_report");
        nonStandardKeys.put("obs", "person_id");
        nonStandardKeys.put("fhir_diagnostic_report", "subject_id");
        multipleColumnTables.put("relationship", 2);
        multipleColumnTables.put("person_merge_log", 2);
    }

    @Override
    public void startup() {
        try {
            log.warn(getClass().getSimpleName() + ": startup initiated");
            statusDb = new Rocks(new File(config.getContext().getModuleDataDir(), "status.db"));
            performInitialSnapshot();
            log.warn(getClass().getSimpleName() + ": startup completed");
        } catch (Exception e) {
            throw new RuntimeException("An error occurred starting up", e);
        }
    }

    @Override
    public void shutdown() {
        statusDb.close();
    }

    @Override
    public void accept(DbEvent event) {
        while (!snapshotInitialized) {
            log.warn("Waiting to accept streaming events until snapshot is initialized...");
            try {
                TimeUnit.SECONDS.sleep(1);
            }
            catch (Exception e) {}
        }
        Set<Integer> patientIds = getPatientIdsForEvent(event);
        if (patientIds.isEmpty()) {
            log.debug("Not handling event as not mapped to patient: " + event);
        }
        else {
            for (Integer patientId : patientIds) {
                handlePatientEvent(patientId, event);
            }
        }
    }

    /**
     * For the given event, returns the associated patient ids
     * @param event the event to retrieve the related patient ids
     * @return the patient ids associated with the given event
     */
    public Set<Integer> getPatientIdsForEvent(DbEvent event) {
        Set<Integer> ret = new HashSet<>();
        Integer patientId = event.getValues().getInteger("patient_id");
        if (patientId != null) {
            ret.add(patientId);
        }
        else {
            for (String key : patientKeys.keySet()) {
                Integer value = event.getValues().getInteger(key);
                if (value != null) {
                    String keyTable = patientKeys.get(key);
                    SqlBuilder sql = new SqlBuilder();
                    sql.select("p.patient_id").from("patient", "p");
                    if (keyTable.equals("person")) {
                        sql.where("p.patient_id = ?");
                    }
                    else {
                        String fromColumn = nonStandardKeys.getOrDefault(keyTable, "patient_id");
                        sql.innerJoin(keyTable, "x", fromColumn, "p", "patient_id");
                        sql.where("x." + key + " = ?");
                    }
                    patientId = database.executeQuery(sql.toString(), new ScalarHandler<>(1), value);
                    if (patientId != null) {
                        ret.add(patientId);
                        int numExpected = multipleColumnTables.getOrDefault(event.getTable(), 1);
                        if (numExpected == ret.size()) {
                            break;
                        }
                    }
                }
            }
        }
        return ret;
    }

    /**
     * Once a particular event is connected ot a particular patient, this tracks this change for the patient
     * @param patientId the patient id related to the event
     * @param event the event to track
     */
    public void handlePatientEvent(Integer patientId, DbEvent event) {
        if (log.isDebugEnabled()) {
            log.debug("Patient Event: (patient_id=" + patientId + ") - " + event);
        }
        Timestamp lastUpdated = new Timestamp(event.getTimestamp());
        String sql = "insert into dbevent_patient (patient_id, last_updated) values (?, ?) on duplicate key update last_updated = ?";
        database.executeUpdate(sql, patientId, lastUpdated, lastUpdated);
        if (event.getTable().equals("patient")) {
            if (event.getOperation() == Operation.DELETE || event.getValues().getBoolean("voided")) {
                sql = "update dbevent_patient set deleted = true where patient_id = ? and deleted = false";
                database.executeUpdate(sql, patientId);
            }
            else if (!event.getValues().getBoolean("voided")) {
                sql = "update dbevent_patient set deleted = false where patient_id = ? and deleted = true";
                database.executeUpdate(sql, patientId);
            }
        }
    }

    /**
     * Rather than stream all initial READ events one by one, it is much more efficient to set up the initial state
     * by querying the database directly to record the most recent date updated and deleted status for all patients.
     * This method performs this operation, and returns the max last_updated in the database
     */
    protected synchronized void performInitialSnapshot() {
        snapshotInitialized = BooleanUtils.isTrue(statusDb.get("snapshotInitialized"));
        if (!snapshotInitialized) {
            log.warn("Snapshot has not yet been initialized, will perform initial snapshot");
            Executors.newSingleThreadExecutor().execute(() -> {
                while (!isConnectedToBinlog()) {
                    log.warn("Waiting until consumer is connected to the binlog...");
                    try {
                        TimeUnit.SECONDS.sleep(1);
                    }
                    catch (Exception e) {}
                }
                log.warn("Consumer is connected to binlog, executing snapshot queries");
                log.warn("Snapshotting from patient table");
                database.executeUpdate(
                        "insert ignore into dbevent_patient (patient_id, last_updated, deleted) " +
                                "select patient_id, greatest(date_created, ifnull(date_changed, date_created), ifnull(date_voided, date_created)), voided from patient"
                );
                for (DatabaseTable table : config.getMonitoredTables()) {
                    String tableName = table.getTableName();
                    if (config.isIncluded(table) && !tableName.equals("patient")) {
                        Set<String> columns = table.getColumns().keySet();

                        List<String> dateCols = new ArrayList<>();
                        if (columns.contains("date_created")) {
                            dateCols.add("ifnull(t.date_created, p.last_updated)");
                        }
                        if (columns.contains("date_changed")) {
                            dateCols.add("ifnull(t.date_changed, p.last_updated)");
                        }
                        if (columns.contains("date_voided")) {
                            dateCols.add("ifnull(t.date_voided, p.last_updated)");
                        }

                        if (dateCols.isEmpty()) {
                            log.warn("Omitting snapshot: " + tableName + " (no date columns)");
                        } else {
                            for (String key : patientKeys.keySet()) {
                                if (columns.contains(key)) {
                                    String keyTable = patientKeys.get(key);
                                    SqlBuilder sql = new SqlBuilder();
                                    sql.append("update dbevent_patient p");
                                    if (keyTable.equals("person")) {
                                        sql.innerJoin(tableName, "t", key, "p", "patient_id");
                                    } else {
                                        String fromColumn = nonStandardKeys.getOrDefault(keyTable, "patient_id");
                                        sql.innerJoin(keyTable, "x", fromColumn, "p", "patient_id");
                                        sql.innerJoin(tableName, "t", key, "x", key);
                                    }
                                    sql.append(" set p.last_updated = greatest(p.last_updated, ").append(String.join(",", dateCols)).append(")");

                                    log.warn("Snapshotting: " + tableName);
                                    database.executeUpdate(sql.toString());

                                    // Once we find a match, break, except for relationship table
                                    if (!tableName.equals("relationship") || key.equals("person_b")) {
                                        break;
                                    }
                                }
                            }
                        }
                    }
                }
                statusDb.put("snapshotInitialized", Boolean.TRUE);
                snapshotInitialized = true;
            });
        }
    }

    /**
     * @return true if the snapshot has been initialized
     */
    public boolean isSnapshotInitialized() {
        return snapshotInitialized;
    }

    /**
     * @return true if the consumer is connected to the binlog
     */
    public boolean isConnectedToBinlog() {
        Map<String, Object> attributes = DbEventLog.getStreamingMonitoringAttributes(config.getSourceName());
        Boolean value = (Boolean) attributes.get("Connected");
        return BooleanUtils.isTrue(value);
    }
}
