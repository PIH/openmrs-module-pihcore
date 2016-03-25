package org.openmrs.module.pihcore.reporting;

import org.junit.Before;
import org.openmrs.Patient;
import org.openmrs.PersonName;
import org.openmrs.api.LocationService;
import org.openmrs.contrib.testdata.TestDataManager;
import org.openmrs.contrib.testdata.builder.PatientBuilder;
import org.openmrs.module.emrapi.EmrApiProperties;
import org.openmrs.module.metadatadeploy.api.MetadataDeployService;
import org.openmrs.module.pihcore.config.Config;
import org.openmrs.module.pihcore.config.ConfigDescriptor;
import org.openmrs.module.pihcore.deploy.bundle.core.EncounterTypeBundle;
import org.openmrs.module.pihcore.deploy.bundle.haiti.HaitiAddressBundle;
import org.openmrs.module.pihcore.deploy.bundle.haiti.mirebalais.MirebalaisLocationsBundle;
import org.openmrs.module.pihcore.metadata.Metadata;
import org.openmrs.module.pihcore.metadata.haiti.HaitiPatientIdentifierTypes;
import org.openmrs.module.pihcore.metadata.haiti.mirebalais.MirebalaisLocations;
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
    protected EncounterTypeBundle encounterTypeBundle;

    @Autowired
    protected MirebalaisLocationsBundle mirebalaisLocationsBundle;

    @Autowired
    protected LocationService locationService;

    @Autowired
    protected MetadataDeployService deployService;

    @Autowired
    protected HaitiAddressBundle haitiAddressBundle;


    @Before
    public void setup() throws Exception {
        executeDataSet("org/openmrs/module/pihcore/coreMetadata.xml");
        authenticate();
        deployService.installBundle(encounterTypeBundle);
        deployService.installBundle(mirebalaisLocationsBundle);
        LocationTagSetup.setupLocationTags(locationService, getConfig());
        haitiAddressBundle.installAddressTemplate();
    }

    protected Config getConfig() {
        ConfigDescriptor d = new ConfigDescriptor();
        d.setCountry(ConfigDescriptor.Country.HAITI);
        d.setSite(ConfigDescriptor.Site.MIREBALAIS);
        return new Config(d);
    }

    protected Patient createPatient() {
        PatientBuilder pb = data.patient();
        pb.name(new PersonName("John", "Smitty", "Smith"));
        pb.birthdate("1977-11-23").birthdateEstimated(false);
        pb.male();
        pb.identifier(Metadata.lookup(HaitiPatientIdentifierTypes.ZL_EMR_ID), "X3XK71", Metadata.lookup(MirebalaisLocations.MIREBALAIS_CDI_PARENT));
        return pb.save();
    }
}
