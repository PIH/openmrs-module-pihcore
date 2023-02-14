package org.openmrs.module.pihcore.event;

import org.apache.commons.dbutils.handlers.ScalarHandler;
import org.apache.commons.lang.BooleanUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openmrs.module.dbevent.Database;
import org.openmrs.module.dbevent.DatabaseColumn;
import org.openmrs.module.dbevent.DatabaseJoin;
import org.openmrs.module.dbevent.DatabaseJoinPath;
import org.openmrs.module.dbevent.DatabaseQuery;
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
import java.util.Arrays;
import java.util.Collections;
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
 */
public class PatientUpdateEventConsumer implements EventConsumer {

    private static final Logger log = LogManager.getLogger(PatientUpdateEventConsumer.class);

    public static final String[] DATE_CHANGED_COLUMNS = {
            "date_created", "date_changed", "date_voided", "date_status_changed"  // date_status_changed is found on the paperrecord tables
    };

    private final DbEventSourceConfig config;
    private final Database database;
    private Rocks statusDb;
    private boolean snapshotInitialized = false;
    private final Map<String, List<String>> snapshotStatements;
    private final Map<String, List<DatabaseQuery>> patientQueries;

    public PatientUpdateEventConsumer(DbEventSourceConfig config) {
        this.config = config;
        this.database = config.getContext().getDatabase();
        this.snapshotStatements = constructSnapshotStatements();
        this.patientQueries = constructPatientQueries();
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
            catch (Exception e) {
                log.trace("An error occurred while waiting for the snapshot to initialize");
            }
        }
        Set<Integer> patientIds = getPatientIdsForEvent(event);
        if (patientIds.isEmpty()) {
            if (log.isDebugEnabled()) {
                log.debug("Not handling event as not mapped to patient: " + event);
            }
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
            List<DatabaseQuery> databaseQueries = patientQueries.get(event.getTable());
            if (databaseQueries != null) {
                for (DatabaseQuery query : databaseQueries) {
                    Object[] params = new Object[query.getParameterNames().size()];
                    for (int i=0; i<query.getParameterNames().size(); i++) {
                        params[i] = event.getValues().get(query.getParameterNames().get(i));
                    }
                    patientId = database.executeQuery(query.getSql(), new ScalarHandler<>(), params);
                    if (patientId != null){
                        ret.add(patientId);
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
                    catch (Exception e) {
                        log.trace("An error occurred while waiting for the consumer to connect to the binlog");
                    }
                }
                log.warn("Consumer is connected to binlog, executing snapshot queries");
                for (String tableName : snapshotStatements.keySet()) {
                    for (String statement : snapshotStatements.get(tableName)) {
                        log.warn("Snapshotting table: " + tableName);
                        log.trace(statement);
                        database.executeUpdate(statement);
                    }
                }
                statusDb.put("snapshotInitialized", Boolean.TRUE);
                snapshotInitialized = true;
            });
        }
    }

    /**
     * @return a Map from table name to a list of the paths to person id from that table that need to be monitored
     */
    public Map<String, List<DatabaseJoinPath>> getPathsToPerson() {
        Map<String, List<DatabaseJoinPath>> ret = new LinkedHashMap<>();
        DatabaseColumn personId = database.getMetadata().getTable("person").getColumn("person_id");
        List<String> tableExclusions = Arrays.asList("users", "provider", "logic_rule_token");
        for (DatabaseTable table : database.getMetadata().getTables().values()) {
            if (!tableExclusions.contains(table.getTableName())) {
                ret.put(table.getTableName(), database.getMetadata().getPathsToColumn(table, personId, tableExclusions));
            }
        }
        return ret;
    }

    /**
     * @param tableName the name of the table
     * @param defaultColumn the default column to be used if the table column is null
     * @return the columns from the given table to use in the initial snapshot query for that table
     */
    public List<String> getColumnsForTable(String tableName, String defaultColumn) {
        List<String> dateCols = new ArrayList<>();
        Set<String> columns = database.getMetadata().getTable(tableName).getColumns().keySet();
        for (String col : DATE_CHANGED_COLUMNS) {
            if (columns.contains(col)) {
                if (defaultColumn.equals(tableName + "." + col)) {
                    dateCols.add(defaultColumn);
                }
                else {
                    dateCols.add("ifnull(" + tableName + "." + col + ", " + defaultColumn + ")");
                }
            }
        }
        return dateCols;
    }

    /**
     * @return a Map from table name to a List of statements to run to initialize the most recent update date for
     * each patient record in the database.  This is intended to be executed at first invocation prior to streaming
     */
    public Map<String, List<String>> constructSnapshotStatements() {
        Map<String, List<String>> ret = new LinkedHashMap<>();

        SqlBuilder sb = new SqlBuilder();
        sb.append("insert ignore into dbevent_patient (patient_id, last_updated, deleted)");
        sb.append("select patient.patient_id, greatest(");
        sb.append(String.join(", ", getColumnsForTable("patient", "patient.date_created")));
        sb.append("), patient.voided from patient patient");
        ret.put("patient", Collections.singletonList(sb.toString()));

        sb = new SqlBuilder();
        sb.append("update dbevent_patient p inner join person person on p.patient_id = person.person_id");
        sb.append("set p.last_updated = greatest( p.last_updated,");
        sb.append(String.join(", ", getColumnsForTable("person", "p.last_updated")));
        sb.append(")");
        ret.put("person", Collections.singletonList(sb.toString()));

        Map<String, List<DatabaseJoinPath>> paths = getPathsToPerson();
        for (String tableName : paths.keySet()) {
            // Patient and Person are handled specially above
            if (tableName.equals("patient") || tableName.equals("person")) {
                continue;
            }

            List<String> statementsForTable = new ArrayList<>();
            List<String> dateCols = getColumnsForTable(tableName, "p.last_updated");
            if (!dateCols.isEmpty()) {
                List<DatabaseJoinPath> tableJoinPaths = paths.get(tableName);
                for (DatabaseJoinPath joinPath : tableJoinPaths) {
                    SqlBuilder sql = new SqlBuilder();
                    sql.append("update dbevent_patient p");
                    for (int i = joinPath.size() - 1; i >= 0; i--) {
                        DatabaseJoin join = joinPath.get(i);
                        String fkTable = join.getForeignKey().getTableName();
                        String fkCol = join.getForeignKey().getColumnName();
                        String pkTable = join.getPrimaryKey().getTableName();
                        String pkCol = join.getPrimaryKey().getColumnName();
                        if (pkTable.equals("person")) {
                            if (i > 0 && joinPath.get(i - 1).getPrimaryKey().getTableName().equals("patient")) {
                                continue;
                            }
                        }
                        if (pkTable.equals("person") || pkTable.equals("patient")) {
                            pkTable = "p";
                            pkCol = "patient_id";
                        }
                        sql.innerJoin(fkTable, fkTable, fkCol, pkTable, pkCol);
                    }
                    sql.append(" set p.last_updated = greatest(p.last_updated,").append(String.join(", ", dateCols)).append(")");
                    statementsForTable.add(sql.toString());
                }
            }
            else {
                log.debug("Not adding statements for " + tableName + ". No date columns.");
            }

            ret.put(tableName, statementsForTable);
        }
        return ret;
    }

    /**
     * This method is used to retrieve the queries used to identify which patient ids are associated with which
     * rows in tables that relate to person or patient but which do not directly have a patient_id column
     * @return a Map from table name to a List of DatabaseQuery for retrieving relevant patients ids for that table
     */
    public Map<String, List<DatabaseQuery>> constructPatientQueries() {
        Map<String, List<DatabaseQuery>> ret = new LinkedHashMap<>();

        Map<String, List<DatabaseJoinPath>> paths = getPathsToPerson();
        for (String tableName : paths.keySet()) {
            DatabaseTable table = database.getMetadata().getTable(tableName);
            List<DatabaseQuery> queries = new ArrayList<>();
            if (tableName.equals("patient")) {
                log.trace("Skipping patient table");
            }
            else if (tableName.equals("person")) {
                List<String> args = Collections.singletonList("person_id");
                queries.add(new DatabaseQuery("select p.patient_id from patient p where p.patient_id = ?", args));
            }
            else {
                for (DatabaseJoinPath joinPath : paths.get(tableName)) {
                    SqlBuilder sql = new SqlBuilder();
                    sql.append("select p.patient_id from patient p");
                    for (int i = joinPath.size() - 1; i >= 0; i--) {
                        DatabaseJoin join = joinPath.get(i);
                        String fkTable = join.getForeignKey().getTableName();
                        String fkCol = join.getForeignKey().getColumnName();
                        String pkTable = join.getPrimaryKey().getTableName();
                        String pkCol = join.getPrimaryKey().getColumnName();
                        if (pkTable.equals("person")) {
                            if (i > 0 && joinPath.get(i - 1).getPrimaryKey().getTableName().equals("patient")) {
                                continue;
                            }
                        }
                        if (pkTable.equals("person") || pkTable.equals("patient")) {
                            pkTable = "p";
                            pkCol = "patient_id";
                        }

                        // The last join column becomes the where clause of the statement
                        if (i == 0) {
                            sql.append("where " + pkTable + "." + pkCol + " = ?");
                            List<String> args = Collections.singletonList(join.getForeignKey().getColumnName());
                            queries.add(new DatabaseQuery(sql.toString(), args));
                        }
                        else {
                            sql.innerJoin(fkTable, fkTable, fkCol, pkTable, pkCol);
                        }
                    }
                }
            }
            ret.put(tableName, queries);
        }
        return ret;
    }

    /**
     * @return true if the consumer is connected to the binlog
     */
    public boolean isConnectedToBinlog() {
        Map<String, Object> attributes = DbEventLog.getStreamingMonitoringAttributes(config.getSourceName());
        Boolean value = (Boolean) attributes.get("Connected");
        return BooleanUtils.isTrue(value);
    }

    /**
     * @return true if the snapshot has been initialized
     */
    public boolean isSnapshotInitialized() {
        return snapshotInitialized;
    }

    /**
     * @return the snapshot statements to execute by table
     */
    public Map<String, List<String>> getSnapshotStatements() {
        return snapshotStatements;
    }

    /**
     * @return the patient id queries to execute by table
     */
    public Map<String, List<DatabaseQuery>> getPatientQueries() {
        return patientQueries;
    }
}
