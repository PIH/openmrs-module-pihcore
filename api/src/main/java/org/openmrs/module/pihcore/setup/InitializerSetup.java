package org.openmrs.module.pihcore.setup;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.context.Context;
import org.openmrs.module.initializer.Domain;
import org.openmrs.module.initializer.api.InitializerService;
import org.openmrs.module.initializer.api.loaders.Loader;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.openmrs.module.initializer.Domain.DRUGS;
import static org.openmrs.module.initializer.Domain.HTML_FORMS;
import static org.openmrs.module.initializer.Domain.ORDER_FREQUENCIES;
import static org.openmrs.module.initializer.Domain.PROGRAMS;
import static org.openmrs.module.initializer.Domain.PROGRAM_WORKFLOWS;
import static org.openmrs.module.initializer.Domain.PROGRAM_WORKFLOW_STATES;

public class InitializerSetup {

    protected static Log log = LogFactory.getLog(InitializerSetup.class);

    public static List<Domain> getDomainsToLoadAfterConcepts() {
        return Arrays.asList(
                PROGRAMS,
                PROGRAM_WORKFLOWS,
                PROGRAM_WORKFLOW_STATES,
                DRUGS,
                ORDER_FREQUENCIES,
                HTML_FORMS
        );
    }

    public static void loadPreConceptDomains() {
        try {
            List<String> excludeList = new ArrayList<>();
            for (Domain domain : getDomainsToLoadAfterConcepts()) {
                excludeList.add(domain.getName());
            }
            for (Loader loader : Context.getService(InitializerService.class).getLoaders()) {
                if (!excludeList.contains(loader.getDomainName())) {
                    log.warn("Loading from Initializer: " + loader.getDomainName());
                    loader.loadUnsafe(new ArrayList<>(), true);
                }
            }
        }
        catch (Exception e) {
            throw new IllegalStateException("An error occurred while loading from initializer", e);
        }
    }

    public static void loadPostConceptDomains() {
        try {
            for (Domain domain : getDomainsToLoadAfterConcepts()) {
                // TODO: Remove this if check once we fix core.  See: UHM-5573
                if (domain != ORDER_FREQUENCIES || Context.getOrderService().getOrderFrequencies(true).isEmpty()) {
                    installDomain(domain);
                }
            }
        }
        catch (Exception e) {
            throw new IllegalStateException("An error occurred while loading from initializer", e);
        }
    }

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
