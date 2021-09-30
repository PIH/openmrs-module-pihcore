package org.openmrs.module.pihcore.setup;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.context.Context;
import org.openmrs.module.pacsintegration.api.PacsIntegrationService;
import org.openmrs.module.pihcore.config.Components;
import org.openmrs.module.pihcore.config.Config;

public class PacIntegrationSetup {

    protected static final Log log = LogFactory.getLog(PacIntegrationSetup.class);

    public static void setup(Config config) {

        if (config.isComponentEnabled(Components.PACS_INTEGRATION)) {
            PacsIntegrationService service = Context.getService(PacsIntegrationService.class);
            if (service.isHL7ListenerRunning()) {
                log.warn("HL7 Listener is already running, not re-initializing");
            }
            else {
                service.initializeHL7Listener();
            }
        }

    }
}
