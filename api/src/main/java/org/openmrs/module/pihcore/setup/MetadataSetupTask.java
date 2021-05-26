package org.openmrs.module.pihcore.setup;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.context.Context;
import org.openmrs.module.initializer.Domain;
import org.openmrs.module.metadatadeploy.api.MetadataDeployService;
import org.openmrs.module.pihcore.config.Config;

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

        // We load these initializer domains here rather than in the normal iniz process, since each of these
        // could depend upon Concepts loaded above
        try {
            InitializerSetup.installDomain(Domain.PROGRAMS);
            InitializerSetup.installDomain(Domain.PROGRAM_WORKFLOWS);
            InitializerSetup.installDomain(Domain.PROGRAM_WORKFLOW_STATES);
            InitializerSetup.installDomain(Domain.DRUGS);
            InitializerSetup.installDomain(Domain.ORDER_FREQUENCIES);
        }
        catch (Exception e) {
            log.error("Aborting Metadata Setup Task: error installing initializer domain", e);
            throw new RuntimeException(e);
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
}
