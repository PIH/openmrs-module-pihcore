package org.openmrs.module.pihcore.setup;

import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.module.dbevent.DbEventListenerConfig;
import org.openmrs.module.pihcore.config.Components;
import org.openmrs.module.pihcore.config.Config;
import org.openmrs.module.pihcore.event.PatientUpdateEventListener;
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
            PatientUpdateEventListener patientUpdateEventListener = new PatientUpdateEventListener();
            String sourceName = "PatientChangeSource";
            DbEventListenerConfig listenerConfig = new DbEventListenerConfig(100002, sourceName);

            // This configures no initial data snapshot to occur, as this is done as a part of the consumer startup
            listenerConfig.setDebeziumProperty("snapshot.mode", "schema_only");
            listenerConfig.setEnabled(isEnabled(config));

            // Configure this source to monitor all patient-related tables
            Set<String> patientTables = listenerConfig.getContext().getDatabase().getMetadata().getPatientTableNames();
            listenerConfig.configureTablesToInclude(patientTables);
            patientUpdateEventListener.init(listenerConfig);
        }
    }
}
