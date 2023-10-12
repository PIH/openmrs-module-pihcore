package org.openmrs.module.pihcore.setup;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.context.Context;
import org.openmrs.module.initializer.Domain;
import org.openmrs.module.initializer.api.ConfigDirUtil;
import org.openmrs.module.initializer.api.InitializerService;
import org.openmrs.module.initializer.api.loaders.BaseFileLoader;
import org.openmrs.module.initializer.api.loaders.Loader;
import org.openmrs.module.pihcore.config.Config;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.openmrs.module.initializer.Domain.CONCEPTS;
import static org.openmrs.module.initializer.Domain.CONCEPT_SETS;
import static org.openmrs.module.initializer.Domain.DRUGS;
import static org.openmrs.module.initializer.Domain.HTML_FORMS;
import static org.openmrs.module.initializer.Domain.ORDER_FREQUENCIES;
import static org.openmrs.module.initializer.Domain.PROGRAMS;
import static org.openmrs.module.initializer.Domain.PROGRAM_WORKFLOWS;
import static org.openmrs.module.initializer.Domain.PROGRAM_WORKFLOW_STATES;
import static org.openmrs.module.initializer.Domain.QUEUES;


public class InitializerSetup {

    protected static Log log = LogFactory.getLog(InitializerSetup.class);

    public static List<Domain> getDomainsToLoadAfterConcepts() {
        return Arrays.asList(
                CONCEPTS,
                CONCEPT_SETS,
                PROGRAMS,
                PROGRAM_WORKFLOWS,
                PROGRAM_WORKFLOW_STATES,
                DRUGS,
                ORDER_FREQUENCIES,
                HTML_FORMS,
                QUEUES
        );
    }

    public static void loadPreConceptDomains(Config config) {
        try {
            List<String> excludeList = new ArrayList<>();
            for (Domain domain : getDomainsToLoadAfterConcepts()) {
                excludeList.add(domain.getName());
            }
            for (Loader loader : getInitializerService().getLoaders()) {
                if (!excludeList.contains(loader.getDomainName())) {
                    if (loader.getDomainName().equalsIgnoreCase(Domain.LOCATION_TAG_MAPS.getName())) {
                        log.warn("Deleting checksums for: " + loader.getDomainName());
                        deleteChecksumsForDomains(Domain.LOCATION_TAG_MAPS);
                    }
                    log.warn("Loading from Initializer: " + loader.getDomainName());
                    List<String> exclusionsForLoader = getExclusionsForLoader(loader, config);
                    loader.loadUnsafe(exclusionsForLoader, true);
                }
            }
        }
        catch (Exception e) {
            throw new IllegalStateException("An error occurred while loading from initializer", e);
        }
    }

    /**
     * The purpose of this method is to determine if there are any site-specific configuration files,
     * and if so, to exclude them if they are not intended for the specific config in use.
     * Any config files that contain a "-site-", and which do not end with the site name, are excluded
     */
    public static List<String> getExclusionsForLoader(Loader loader, Config config) {
        List<String> exclusions = new ArrayList<>();
        if (loader instanceof BaseFileLoader) {
            BaseFileLoader ll = (BaseFileLoader) loader;
            String site = config.getSite() == null ? "" : config.getSite().toLowerCase();
            for (File f : ll.getDirUtil().getFiles("csv")) {
                String filename = f.getName().toLowerCase();
                if (filename.contains("-site-") && !filename.endsWith("-site-" + site + ".csv")) {
                    log.debug("Excluding site-specific configuration file: " + filename);
                    exclusions.add(filename);
                }
            }
        }
        return exclusions;
    }

    public static void loadPostConceptDomains(Config config) {
        try {
            for (Domain domain : getDomainsToLoadAfterConcepts()) {
                installDomain(domain, config);
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
    public static void installDomain(Domain domain, Config config) throws Exception {
        for (Loader loader : getInitializerService().getLoaders()) {
            if (loader.getDomainName().equalsIgnoreCase(domain.getName())) {
                log.warn("Loading from Initializer: " + loader.getDomainName());
                List<String> exclusionsForLoader = getExclusionsForLoader(loader, config);
                loader.loadUnsafe(exclusionsForLoader, true);
            }
        }
    }

    /**
     * Deletes the checksum files for the given domains
     * @param domains the domains for which to delete the checksum files
     */
    public static void deleteChecksumsForDomains(Domain... domains) {
        String configDirPath = getInitializerService().getConfigDirPath();
        String checksumsDirPath = getInitializerService().getChecksumsDirPath();
        for (Domain domain : domains) {
            ConfigDirUtil util = new ConfigDirUtil(configDirPath, checksumsDirPath, domain.getName(), false);
            util.deleteChecksums();
        }
    }

    protected static InitializerService getInitializerService() {
        return Context.getService(InitializerService.class);
    }
}
