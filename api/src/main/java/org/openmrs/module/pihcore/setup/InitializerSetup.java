package org.openmrs.module.pihcore.setup;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.context.Context;
import org.openmrs.module.initializer.Domain;
import org.openmrs.module.initializer.api.InitializerService;
import org.openmrs.module.initializer.api.loaders.Loader;

import java.util.ArrayList;

public class InitializerSetup {

    protected static Log log = LogFactory.getLog(InitializerSetup.class);

    /**
     * Explicitly load from a given Domain with no exclusions, and throw an exception on failures
     * Note:  This is _different_ from the built-in Initializer loading, which suppresses errors
     */
    public static void installDomain(Domain domain) throws Exception {
        for (Loader loader : Context.getService(InitializerService.class).getLoaders()) {
            if (loader.getDomainName().equalsIgnoreCase(domain.getName())) {
                log.warn("Loading from Initializer: " + loader.getDomainName());
                loader.loadUnsafe(new ArrayList<>(), true);
            }
        }
    }
}
