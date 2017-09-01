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
import org.openmrs.module.haiticore.metadata.AddressComponent;
import org.openmrs.module.haiticore.metadata.HaitiPersonAttributeTypes;
import org.openmrs.module.haiticore.metadata.bundles.HaitiAddressBundle;
import org.openmrs.module.metadatadeploy.api.MetadataDeployService;
import org.openmrs.module.metadatamapping.MetadataSource;
import org.openmrs.module.metadatamapping.api.MetadataMappingService;
import org.openmrs.module.pihcore.config.Config;
import org.openmrs.module.pihcore.config.ConfigDescriptor;
import org.openmrs.module.pihcore.deploy.bundle.core.EncounterTypeBundle;
import org.openmrs.module.pihcore.deploy.bundle.haiti.PihHaitiPatientIdentifierTypeBundle;
import org.openmrs.module.pihcore.deploy.bundle.haiti.mirebalais.MirebalaisLocationsBundle;
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

;

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
    protected MetadataDeployService deployService;

    @Autowired
    protected HaitiAddressBundle haitiAddressBundle;

    @Autowired
    protected PihHaitiPatientIdentifierTypeBundle pihHaitiPatientIdentifierTypeBundle;

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
        executeDataSet("org/openmrs/module/pihcore/coreMetadata.xml");
        authenticate();
        deployService.installBundle(encounterTypeBundle);
        deployService.installBundle(pihHaitiPatientIdentifierTypeBundle);
        deployService.installBundle(mirebalaisLocationsBundle);
        deployService.installBundle(haitiAddressBundle);
        createEmrApiMappingSource(metadataMappingService);
        MetadataMappingsSetup.setupGlobalMetadataMappings(metadataMappingService,locationService, encounterService, visitService);
        MetadataMappingsSetup.setupPrimaryIdentifierTypeBasedOnCountry(metadataMappingService, patientService, getConfig());
        LocationTagSetup.setupLocationTags(locationService, getConfig());
    }

    protected Config getConfig() {
        ConfigDescriptor d = new ConfigDescriptor();
        d.setCountry(ConfigDescriptor.Country.HAITI);
        d.setSite(ConfigDescriptor.Site.MIREBALAIS);
        return new Config(d);
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
        address(pb, haitiAddressBundle.getAddressComponents(), "USA", "MA", "Boston", "JP", "Pondside", "");
        pb.identifier(Metadata.lookup(PihHaitiPatientIdentifierTypes.ZL_EMR_ID), identifier, Metadata.lookup(MirebalaisLocations.MIREBALAIS_CDI_PARENT));
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
