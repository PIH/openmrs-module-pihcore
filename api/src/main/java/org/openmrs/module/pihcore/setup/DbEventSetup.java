package org.openmrs.module.pihcore.setup;

import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.module.dbevent.DbEventSource;
import org.openmrs.module.dbevent.DbEventSourceConfig;
import org.openmrs.module.dbevent.EventContext;
import org.openmrs.module.pihcore.config.Components;
import org.openmrs.module.pihcore.config.Config;
import org.openmrs.module.pihcore.event.PatientUpdateEventConsumer;
import org.openmrs.util.ConfigUtil;

import java.util.Set;

import static org.openmrs.module.pihcore.PihCoreConstants.DBEVENT_ENABLED_PROPERTY;

public class DbEventSetup {

    protected static Log log = LogFactory.getLog(DbEventSetup.class);

    /**
     * @param config the pih config
     * @return true if the DB Event handling is enabled on this instance
     */
    public static boolean isEnabled(Config config) {
        if (config.isComponentEnabled(Components.DB_EVENT)) {
            String enabled = ConfigUtil.getProperty(DBEVENT_ENABLED_PROPERTY, "true");
            return BooleanUtils.toBoolean(enabled);
        }
        return false;
    }

    /**
     * Setup debezium event sources and consumers
     */
    public static void setup(Config config) {
        if (isEnabled(config)) {
            EventContext context = new EventContext();
            DbEventSource eventSource = getEventSource(context);
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
