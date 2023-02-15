package org.openmrs.module.pihcore.setup;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.module.dbevent.DbEventSource;
import org.openmrs.module.dbevent.DbEventSourceConfig;
import org.openmrs.module.dbevent.EventContext;
import org.openmrs.module.pihcore.config.Components;
import org.openmrs.module.pihcore.config.Config;
import org.openmrs.module.pihcore.event.PatientUpdateEventConsumer;

import java.util.Set;

public class DbEventSetup {

    protected static Log log = LogFactory.getLog(DbEventSetup.class);

    /**
     * Setup debezium event sources and consumers
     */
    public static void setup(Config config) {
        if (config.isComponentEnabled(Components.DB_EVENT)) {
            DbEventSource eventSource = getEventSource(new EventContext());
            eventSource.reset(); // For now, while we test, reset the event source every time we start up
            eventSource.start();
        }
    }

    public static DbEventSource getEventSource(EventContext eventContext) {
        String sourceName = "PatientChangeSource";
        DbEventSourceConfig eventSourceConfig = new DbEventSourceConfig(100002, sourceName, eventContext);

        // This configures no initial data snapshot to occur, as this is done as a part of the consumer startup
        eventSourceConfig.setProperty("snapshot.mode", "schema_only");

        // Configure this source to monitor all patient-related tables
        Set<String> patientTables = eventContext.getDatabase().getMetadata().getPatientTableNames();
        eventSourceConfig.configureTablesToInclude(patientTables);

        DbEventSource eventSource = new DbEventSource(eventSourceConfig);
        eventSource.setEventConsumer(new PatientUpdateEventConsumer(eventSourceConfig));
        return eventSource;
    }
}
