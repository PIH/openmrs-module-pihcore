package org.openmrs.module.pihcore.setup;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.module.dbevent.DbEventSource;
import org.openmrs.module.dbevent.DbEventSourceConfig;
import org.openmrs.module.dbevent.EventContext;
import org.openmrs.module.pihcore.event.PatientUpdateEventConsumer;

import java.util.Set;

public class DbEventSetup {

    protected static Log log = LogFactory.getLog(DbEventSetup.class);

    /**
     * Setup debezium event sources and consumers
     */
    public static void setup() {
        EventContext ctx = new EventContext();
        DbEventSourceConfig config = new DbEventSourceConfig(100002,"PatientChangeSource", ctx);

        // This configures no initial data snapshot to occur, as this is done as a part of the consumer startup
        config.setProperty("snapshot.mode", "schema_only");

        // Configure this source to monitor all patient-related tables
        Set<String> patientTables = ctx.getDatabase().getMetadata().getPatientTableNames();
        config.configureTablesToInclude(patientTables);

        DbEventSource eventSource = new DbEventSource(config);
        eventSource.setEventConsumer(new PatientUpdateEventConsumer(config));
        eventSource.start();
    }
}
