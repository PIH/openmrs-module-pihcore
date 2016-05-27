package org.openmrs.module.pihcore.setup;

import org.openmrs.api.context.Context;
import org.openmrs.module.pacsintegration.api.PacsIntegrationService;
import org.openmrs.module.pihcore.config.Components;
import org.openmrs.module.pihcore.config.Config;

public class PacIntegrationSetup {

    public static void setup(Config config) {

        if (config.isComponentEnabled(Components.PACS_INTEGRATION)) {
            Context.getService(PacsIntegrationService.class).initializeHL7Listener();
        }

    }
}
