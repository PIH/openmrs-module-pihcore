package org.openmrs.module.pihcore.reporting;

import org.junit.Before;
import org.openmrs.Patient;
import org.openmrs.PersonAddress;
import org.openmrs.PersonName;
import org.openmrs.api.EncounterService;
import org.openmrs.api.FormService;
import org.openmrs.api.LocationService;
import org.openmrs.api.PatientService;
import org.openmrs.api.VisitService;
import org.openmrs.contrib.testdata.TestDataManager;
import org.openmrs.contrib.testdata.builder.PatientBuilder;
import org.openmrs.module.emrapi.EmrApiConstants;
import org.openmrs.module.emrapi.EmrApiProperties;
import org.openmrs.module.haiticore.metadata.HaitiPatientIdentifierTypes;
import org.openmrs.module.haiticore.metadata.HaitiPersonAttributeTypes;
import org.openmrs.module.haiticore.metadata.bundles.HaitiPatientIdentifierTypeBundle;
import org.openmrs.module.metadatadeploy.api.MetadataDeployService;
import org.openmrs.module.metadatamapping.MetadataSource;
import org.openmrs.module.metadatamapping.api.MetadataMappingService;
import org.openmrs.module.pihcore.TestAddressBundle;
import org.openmrs.module.pihcore.config.Config;
import org.openmrs.module.pihcore.config.ConfigLoader;
import org.openmrs.module.pihcore.deploy.bundle.AddressComponent;
import org.openmrs.module.pihcore.deploy.bundle.core.EncounterTypeBundle;
import org.openmrs.module.pihcore.deploy.bundle.haiti.PihHaitiPatientIdentifierTypeBundle;
import org.openmrs.module.pihcore.metadata.Metadata;
import org.openmrs.module.pihcore.metadata.haiti.PihHaitiPatientIdentifierTypes;
import org.openmrs.module.pihcore.metadata.haiti.mirebalais.MirebalaisLocations;
import org.openmrs.module.pihcore.setup.LocationTagSetup;
import org.openmrs.module.pihcore.setup.MetadataMappingsSetup;
import org.openmrs.module.reporting.common.ReflectionUtil;
import org.openmrs.module.reporting.report.definition.service.ReportDefinitionService;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.openmrs.test.SkipBaseSetup;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.UUID;

;

/**
 * Sets up basic Mirebalais metadata (instead of the standardTestDataset.xml from openmrs-core)
 */
@SkipBaseSetup
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
    protected MetadataDeployService deployService;

    @Autowired
    protected TestAddressBundle testAddressBundle;

    @Autowired
    protected PihHaitiPatientIdentifierTypeBundle pihHaitiPatientIdentifierTypeBundle;

    @Autowired
    protected HaitiPatientIdentifierTypeBundle haitiPatientIdentifierTypeBundle;

    @Autowired
    protected MetadataMappingService metadataMappingService;

    @Autowired
    protected VisitService visitService;

    @Autowired
    protected PatientService patientService;

    @Autowired
    protected EncounterService encounterService;

    @Autowired
    protected LocationService locationService;

    @Autowired
    protected FormService formService;

    @Before
    public void setup() throws Exception {
        Config config = getConfig();
        setAutoIncrementOnTablesWithNativeIfNotAssignedIdentityGenerator();
        executeDataSet("org/openmrs/module/pihcore/coreMetadata.xml");
        authenticate();
        deployService.installBundle(encounterTypeBundle);
        deployService.installBundle(haitiPatientIdentifierTypeBundle);
        deployService.installBundle(pihHaitiPatientIdentifierTypeBundle);
        deployService.installBundle(testAddressBundle);
        createEmrApiMappingSource(metadataMappingService);
        MetadataMappingsSetup.setupGlobalMetadataMappings(metadataMappingService,locationService, encounterService, visitService);
        MetadataMappingsSetup.setupPrimaryIdentifierTypeBasedOnCountry(metadataMappingService, patientService, config);
        LocationTagSetup.setupLocationTags(locationService, config);
    }

    protected Config getConfig() {
        return new Config(ConfigLoader.load("mirebalais"));
    }

    protected void createEmrApiMappingSource(MetadataMappingService metadataMappingService) {
        MetadataSource source = new MetadataSource();
        source.setName(EmrApiConstants.EMR_METADATA_SOURCE_NAME);
        metadataMappingService.saveMetadataSource(source);
    }

    protected Patient createPatient() {
        PatientBuilder pb = data.patient();
        pb.name(new PersonName("John", "Smitty", "Smith"));
        pb.birthdate("1977-11-23").birthdateEstimated(false);
        pb.male();
        pb.identifier(Metadata.lookup(PihHaitiPatientIdentifierTypes.ZL_EMR_ID), "X3XK71", Metadata.lookup(MirebalaisLocations.MIREBALAIS_CDI_PARENT));
        return pb.save();
    }

    protected Patient createPatient(String identifier) {
        PatientBuilder pb = data.patient();
        pb.name(new PersonName("John", "Smitty", "Smith"));
        pb.birthdate("1977-11-23").birthdateEstimated(false);
        pb.male();
        pb.personAttribute(Metadata.lookup(HaitiPersonAttributeTypes.TELEPHONE_NUMBER), "555-1234");
        pb.personAttribute(Metadata.lookup(HaitiPersonAttributeTypes.UNKNOWN_PATIENT), "false");
        pb.personAttribute(Metadata.lookup(HaitiPersonAttributeTypes.MOTHERS_FIRST_NAME), "Isabel");
        address(pb, testAddressBundle.getAddressComponents(), "USA", "MA", "Boston", "JP", "Pondside", "");
        pb.identifier(Metadata.lookup(PihHaitiPatientIdentifierTypes.ZL_EMR_ID), identifier, Metadata.lookup(MirebalaisLocations.MIREBALAIS_CDI_PARENT));
        pb.identifier(Metadata.lookup(HaitiPatientIdentifierTypes.BIOMETRIC_REF_NUMBER), UUID.randomUUID().toString(), Metadata.lookup(MirebalaisLocations.MIREBALAIS_CDI_PARENT));
        return pb.save();
    }

    protected PatientBuilder address(PatientBuilder pb, List<AddressComponent> addressComponents, String... values) {
        PersonAddress a = new PersonAddress();
        int index = 0;
        for (AddressComponent property : addressComponents) {
            ReflectionUtil.setPropertyValue(a, property.getField().getName(), values[index]);
            index++;
        }
        return pb.address(a);
    }


}
