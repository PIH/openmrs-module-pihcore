package org.openmrs.module.pihcore;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Concept;
import org.openmrs.api.AdministrationService;
import org.openmrs.layout.web.address.AddressSupport;
import org.openmrs.layout.web.address.AddressTemplate;
import org.openmrs.module.emrapi.EmrApiConstants;
import org.openmrs.module.metadatadeploy.MetadataUtils;
import org.openmrs.module.metadatadeploy.api.MetadataDeployService;
import org.openmrs.module.metadatadeploy.bundle.MetadataBundle;
import org.openmrs.module.metadatamapping.MetadataSource;
import org.openmrs.module.metadatamapping.api.MetadataMappingService;
import org.openmrs.module.pacsintegration.PacsIntegrationConstants;
import org.openmrs.module.pihcore.config.Config;
import org.openmrs.module.pihcore.config.ConfigDescriptor;
import org.openmrs.module.pihcore.deploy.bundle.ConceptsFromMetadataSharing;
import org.openmrs.module.pihcore.deploy.bundle.core.concept.ClinicalConsultationConcepts;
import org.openmrs.module.pihcore.deploy.bundle.core.concept.CommonConcepts;
import org.openmrs.module.pihcore.deploy.bundle.core.concept.CoreConceptMetadataBundle;
import org.openmrs.module.pihcore.deploy.bundle.core.concept.SocioEconomicConcepts;
import org.openmrs.module.pihcore.metadata.haiti.HaitiPatientIdentifierTypes;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.openmrs.test.SkipBaseSetup;
import org.openmrs.ui.framework.UiFrameworkConstants;
import org.openmrs.util.OpenmrsConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * This is an integration test that tests setting up the Mirebalais environment
 */
@SkipBaseSetup
public class MirebalaisActivatorTest extends BaseModuleContextSensitiveTest {

    @Autowired
    private MetadataDeployService deployService;

    @Autowired
    private MetadataMappingService metadataMappingService;

    @Autowired
    @Qualifier("adminService")
    private AdministrationService adminService;

    @Autowired
    private List<MetadataBundle> bundles;

    @Autowired
    private ConceptsFromMetadataSharing conceptsFromMetadataSharing;

    private PihCoreActivator activator;

    @Override
    public Properties getRuntimeProperties() {
        Properties p = super.getRuntimeProperties();
        p.setProperty("pih.config", "pihcore");
        return p;
    }

    @Before
    public void setUp() throws Exception {
        initializeInMemoryDatabase();
        executeDataSet("requiredDataTestDataset.xml");
        authenticate();

        deployService.installBundle(conceptsFromMetadataSharing);
        createEmrApiMappingSource(metadataMappingService);

        activator = new PihCoreActivator();
        Config config = mock(Config.class);
        when(config.getCountry()).thenReturn(ConfigDescriptor.Country.HAITI);
        when(config.getSite()).thenReturn(ConfigDescriptor.Site.MIREBALAIS);
        activator.setConfig(config);
        activator.started();
    }

    protected void createEmrApiMappingSource(MetadataMappingService metadataMappingService) {
        MetadataSource source = new MetadataSource();
        source.setName(EmrApiConstants.EMR_METADATA_SOURCE_NAME);
        metadataMappingService.saveMetadataSource(source);
    }


    @Test
    public void testThatActivatorDoesAllSetupForMirebalais() throws Exception {
        verifyMirebalaisMetadataInstalled();
        verifyAddressTemplateConfigured();
        verifyGlobalPropertiesConfigured();
        verifyDatetimeFormatting();
        verifyPacsIntegrationGlobalPropertiesConfigured();
    }

    private void verifyMirebalaisMetadataInstalled() throws Exception {
        // test a few random concepts
        assertThat(MetadataUtils.existing(Concept.class, CommonConcepts.Concepts.YES).getName().getName(), is("Oui"));

        Concept mainActivity = MetadataUtils.existing(Concept.class, SocioEconomicConcepts.Concepts.MAIN_ACTIVITY);
        assertThat(mainActivity.getDatatype().getName(), is("Coded"));
        assertThat(mainActivity.getAnswers().size(), greaterThan(5));

        Concept construct = MetadataUtils.existing(Concept.class, ClinicalConsultationConcepts.Concepts.PAST_MEDICAL_HISTORY_CONSTRUCT);
        assertThat(construct.getUuid(), is(ClinicalConsultationConcepts.Concepts.PAST_MEDICAL_HISTORY_CONSTRUCT));
        assertThat(construct.getConceptSets().size(), is(3));

        construct = MetadataUtils.existing(Concept.class, ClinicalConsultationConcepts.Concepts.FAMILY_HISTORY_CONSTRUCT);
        assertThat(construct.getUuid(), is(ClinicalConsultationConcepts.Concepts.FAMILY_HISTORY_CONSTRUCT));
        assertThat(construct.getConceptSets().size(), is(4));
    }

    private void verifyAddressTemplateConfigured() throws Exception {
        AddressTemplate at = AddressSupport.getInstance().getDefaultLayoutTemplate();
        assertEquals("mirebalais.address.country", at.getNameMappings().get("country"));
        assertEquals("mirebalais.address.stateProvince", at.getNameMappings().get("stateProvince"));
        assertEquals("mirebalais.address.cityVillage", at.getNameMappings().get("cityVillage"));
        assertEquals("mirebalais.address.neighborhoodCell", at.getNameMappings().get("address3"));
        assertEquals("mirebalais.address.address1", at.getNameMappings().get("address1"));
        assertEquals("mirebalais.address.address2", at.getNameMappings().get("address2"));
        assertEquals("40", at.getSizeMappings().get("country"));
        assertEquals("40", at.getSizeMappings().get("stateProvince"));
        assertEquals("40", at.getSizeMappings().get("cityVillage"));
        assertEquals("60", at.getSizeMappings().get("address3"));
        assertEquals("60", at.getSizeMappings().get("address1"));
        assertEquals("60", at.getSizeMappings().get("address2"));
        assertEquals("Haiti", at.getElementDefaults().get("country"));
        assertEquals("address2", at.getLineByLineFormat().get(0));
        assertEquals("address1", at.getLineByLineFormat().get(1));
        assertEquals("address3, cityVillage", at.getLineByLineFormat().get(2));
        assertEquals("stateProvince, country", at.getLineByLineFormat().get(3));
    }

    private void verifyGlobalPropertiesConfigured() throws Exception {
        assertEquals("fr", adminService.getGlobalProperty(OpenmrsConstants.GLOBAL_PROPERTY_DEFAULT_LOCALE));
    }

    private void verifyDatetimeFormatting() {
        DateFormat datetimeFormat = new SimpleDateFormat(adminService.getGlobalProperty(UiFrameworkConstants.GP_FORMATTER_DATETIME_FORMAT));
        DateFormat dateFormat = new SimpleDateFormat(adminService.getGlobalProperty(UiFrameworkConstants.GP_FORMATTER_DATE_FORMAT));
        Date sampleDate = new DateTime(2012, 2, 22, 14, 23, 22).toDate();
        assertEquals("22 Feb 2012 2:23 PM", datetimeFormat.format(sampleDate));
        assertEquals("22 Feb 2012", dateFormat.format(sampleDate));
    }

    private void verifyPacsIntegrationGlobalPropertiesConfigured() throws Exception {
        assertEquals(HaitiPatientIdentifierTypes.ZL_EMR_ID.uuid(), adminService.getGlobalProperty(PacsIntegrationConstants.GP_PATIENT_IDENTIFIER_TYPE_UUID));
        assertEquals("en", adminService.getGlobalProperty(PacsIntegrationConstants.GP_DEFAULT_LOCALE));
        assertEquals("Mirebalais", adminService.getGlobalProperty(PacsIntegrationConstants.GP_SENDING_FACILITY));
        assertEquals(CoreConceptMetadataBundle.ConceptSources.LOINC, adminService.getGlobalProperty(PacsIntegrationConstants.GP_PROCEDURE_CODE_CONCEPT_SOURCE_UUID));
    }

}