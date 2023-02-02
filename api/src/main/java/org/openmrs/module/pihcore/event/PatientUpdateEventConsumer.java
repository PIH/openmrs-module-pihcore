package org.openmrs.module.pihcore.event;

import org.apache.commons.dbutils.handlers.ScalarHandler;
import org.apache.commons.lang.BooleanUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openmrs.module.dbevent.Database;
import org.openmrs.module.dbevent.DatabaseTable;
import org.openmrs.module.dbevent.DbEvent;
import org.openmrs.module.dbevent.DbEventSourceConfig;
import org.openmrs.module.dbevent.EventConsumer;
import org.openmrs.module.dbevent.Operation;
import org.openmrs.module.dbevent.Rocks;
import org.openmrs.module.dbevent.SqlBuilder;

import java.io.File;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Consumes patient-related events
 * For performance reasons on larger databases, this does not use a Debezium-generated snapshot, but rather
 * computes a custom snapshot that is then updated as needed from change events.
 * When used, this should be configured with "snapshot.mode = schema_only"
 */
public class PatientUpdateEventConsumer implements EventConsumer {

    private static final Logger log = LogManager.getLogger(PatientUpdateEventConsumer.class);

    private final DbEventSourceConfig config;
    private final Database database;
    private final Map<String, String> patientKeys = new LinkedHashMap<>();
    private Rocks statusDb;
    private boolean snapshotInitialized = false;

    public PatientUpdateEventConsumer(DbEventSourceConfig config) {
        this.config = config;
        this.database = config.getContext().getDatabase();
        patientKeys.put("person_id", "person");
        patientKeys.put("person_a", "person");
        patientKeys.put("person_b", "person");
        patientKeys.put("order_id", "orders");
        patientKeys.put("patient_program_id", "patient_program");
        patientKeys.put("encounter_id", "encounter");
        patientKeys.put("visit_id", "visit");
        patientKeys.put("allergy_id", "allergy");
        patientKeys.put("appointment_id", "appointmentscheduling_appointment");
    }

    @Override
    public void startup() {
        try {
            log.warn(getClass().getSimpleName() + ": startup initiated");
            statusDb = new Rocks(new File(config.getContext().getModuleDataDir(), "status.db"));
            snapshotInitialized = BooleanUtils.isTrue(statusDb.get("snapshotInitialized"));
            if (!snapshotInitialized) {
                performInitialSnapshot();
                statusDb.put("snapshotInitialized", Boolean.TRUE);
                snapshotInitialized = true;
            }
            log.warn(getClass().getSimpleName() + ": startup completed");
        }
        catch (Exception e) {
            throw new RuntimeException("An error occurred starting up", e);
        }
    }

    @Override
    public void shutdown() {
        statusDb.close();
    }

    @Override
    public void accept(DbEvent event) {
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
                String keyTable = patientKeys.get(key);
                Integer value = event.getValues().getInteger(key);
                if (value != null) {
                    SqlBuilder sql = new SqlBuilder();
                    sql.select("p.patient_id").from("patient", "p");
                    if (keyTable.equals("person")) {
                        sql.where("p.patient_id = ?");
                    } else {
                        sql.innerJoin(keyTable, "x", "patient_id", "p", "patient_id");
                        sql.where("x." + key + " = ?");
                    }
                    patientId = database.executeQuery(sql.toString(), new ScalarHandler<>(1), value);
                    if (patientId == null) {
                        if (!keyTable.equals("person")) {
                            throw new RuntimeException("Unable to retrieve patient_id for: " + event);
                        }
                    } else {
                        ret.add(patientId);
                        if (!event.getTable().equals("relationship") || ret.size() == 2) {
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
        if (!snapshotInitialized) {
            log.warn("Performing initial snapshot into dbevent_patient from patient");
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
                        log.warn("Skipping initial snapshot into dbevent_patient from " + tableName + " as no date columns found");
                    } else {
                        for (String key : patientKeys.keySet()) {
                            if (columns.contains(key)) {
                                String keyTable = patientKeys.get(key);
                                SqlBuilder sql = new SqlBuilder();
                                sql.append("update dbevent_patient p");
                                if (keyTable.equals("person")) {
                                    sql.innerJoin(tableName, "t", key, "p", "patient_id");
                                } else {
                                    sql.innerJoin(keyTable, "x", "patient_id", "p", "patient_id");
                                    sql.innerJoin(tableName, "t", key, "x", key);
                                }
                                sql.append(" set p.last_updated = greatest(p.last_updated, ").append(String.join(",", dateCols)).append(")");

                                log.warn("Performing initial snapshot into dbevent_patient from: " + tableName + "." + key);
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
        }
    }
}
