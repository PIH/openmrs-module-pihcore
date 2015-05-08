package org.openmrs.module.pihcore.reporting;

import org.junit.Before;
import org.openmrs.api.LocationService;
import org.openmrs.contrib.testdata.TestDataManager;
import org.openmrs.module.emrapi.EmrApiProperties;
import org.openmrs.module.metadatadeploy.api.MetadataDeployService;
import org.openmrs.module.pihcore.config.Config;
import org.openmrs.module.pihcore.config.ConfigDescriptor;
import org.openmrs.module.pihcore.deploy.bundle.core.EncounterTypeBundle;
import org.openmrs.module.pihcore.deploy.bundle.haiti.mirebalais.MirebalaisLocationsBundle;
import org.openmrs.module.pihcore.setup.LocationTagSetup;
import org.openmrs.module.reporting.report.definition.service.ReportDefinitionService;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.openmrs.test.SkipBaseSetup;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Sets up basic Mirebalais metadata (instead of the standardTestDataset.xml from openmrs-core)
 */
@SkipBaseSetup // because of TRUNK-4051, this annotation will not be picked up, and you need to declare this on your concrete subclass
public abstract class BaseReportTest extends BaseModuleContextSensitiveTest {

    @Autowired
    protected ReportDefinitionService reportDefinitionService;

    @Autowired
    protected EmrApiProperties emrApiProperties;

    @Autowired
    protected TestDataManager data;

    @Autowired
    EncounterTypeBundle encounterTypeBundle;

    @Autowired
    MirebalaisLocationsBundle mirebalaisLocationsBundle;

    @Autowired
    LocationService locationService;

    @Autowired
    private MetadataDeployService deployService;

    @Before
    public void setup() throws Exception {
        executeDataSet("org/openmrs/module/pihcore/coreMetadata.xml");
        authenticate();
        deployService.installBundle(encounterTypeBundle);
        deployService.installBundle(mirebalaisLocationsBundle);
        LocationTagSetup.setupLocationTags(locationService, getConfig());
    }

    protected Config getConfig() {
        ConfigDescriptor d = new ConfigDescriptor();
        d.setCountry(ConfigDescriptor.Country.HAITI);
        d.setSite(ConfigDescriptor.Site.MIREBALAIS);
        return new Config(d);
    }
}
