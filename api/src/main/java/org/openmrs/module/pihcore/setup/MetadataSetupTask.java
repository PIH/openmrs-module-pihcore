package org.openmrs.module.pihcore.setup;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.context.Context;
import org.openmrs.module.metadatadeploy.api.MetadataDeployService;
import org.openmrs.module.pihcore.config.Config;
import org.openmrs.module.pihcore.config.ConfigDescriptor;
import org.openmrs.module.pihcore.deploy.bundle.haiti.HaitiMetadataToInstallAfterConceptsBundle;
import org.openmrs.module.pihcore.deploy.bundle.liberia.LiberiaMetadataToInstallAfterConceptsBundle;
import org.openmrs.module.pihcore.deploy.bundle.mexico.MexicoMetadataToInstallAfterConceptsBundle;
import org.openmrs.module.pihcore.deploy.bundle.sierraLeone.SierraLeoneMetadataToInstallAfterConceptsBundle;

public class MetadataSetupTask implements Runnable {

    protected Log log = LogFactory.getLog(getClass());

    private Config config;

    private Boolean testingContext = false;

    public MetadataSetupTask(Config config, Boolean testingContext) {
        this.config = config;
        this.testingContext = testingContext;
    }

    @Override
    public void run()  {

        try {
            MetadataSharingSetup.installMetadataSharingPackages();
            log.info("Metadata sharing packages loaded");
        }
        catch (Exception e) {
            log.error("Aborting Metadata Setup Task: error installing Metadata Sharing Packages", e);
            throw e;
        }

        // hack so that we can disable this during testing, because we are not currently installing MDS packages as part of test
        if (!this.testingContext) {
            try {
                installMetadataBundlesThatDependOnMDSPackages(config);
                log.info("Metadata bundles dependant on MDS packages loaded");
            }
            catch (Exception e) {
                log.error("Aborting Metadata Setup Task: error installing dependant Metadata bundles", e);
                throw e;
            }
        }

        try {
            DrugListSetup.installDrugList();
            log.info("Drug list loaded");
        }
        catch (Exception e) {
            log.error("Aborting Metadata Setup Task: error installing drug list", e);
            throw e;
        }

        try {
            HtmlFormSetup.loadHtmlForms(testingContext); // this must happen *after* MDS packages are installed, so that forms defined in code/github take precedent
            log.info("HTML forms loaded");
        }
        catch (Exception e) {
            log.error("Aborting Metadata Setup Task: error installing HTML forms", e);
            throw e;
        }
    }

    private void installMetadataBundlesThatDependOnMDSPackages(Config config) {
        MetadataDeployService deployService = Context.getService(MetadataDeployService.class);
        // make this more dynamic, less dependent on if-thens
        if (config.getCountry().equals(ConfigDescriptor.Country.HAITI)) {
            deployService.installBundle(Context.getRegisteredComponents(HaitiMetadataToInstallAfterConceptsBundle.class).get(0));
        } else if (config.getCountry().equals(ConfigDescriptor.Country.LIBERIA)) {
            deployService.installBundle(Context.getRegisteredComponents(LiberiaMetadataToInstallAfterConceptsBundle.class).get(0));
        } else if (config.getCountry().equals(ConfigDescriptor.Country.MEXICO)) {
            deployService.installBundle(Context.getRegisteredComponents(MexicoMetadataToInstallAfterConceptsBundle.class).get(0));
        } else if (config.getCountry().equals(ConfigDescriptor.Country.SIERRA_LEONE)) {
            deployService.installBundle(Context.getRegisteredComponents(SierraLeoneMetadataToInstallAfterConceptsBundle.class).get(0));
        }
    }


}
