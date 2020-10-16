package org.openmrs.module.pihcore;

import org.junit.Before;
import org.junit.Test;
import org.openmrs.Concept;
import org.openmrs.api.AdministrationService;
import org.openmrs.module.emrapi.EmrApiConstants;
import org.openmrs.module.metadatadeploy.MetadataUtils;
import org.openmrs.module.metadatadeploy.api.MetadataDeployService;
import org.openmrs.module.metadatadeploy.bundle.MetadataBundle;
import org.openmrs.module.metadatamapping.MetadataSource;
import org.openmrs.module.metadatamapping.api.MetadataMappingService;
import org.openmrs.module.pihcore.config.Config;
import org.openmrs.module.pihcore.config.ConfigDescriptor;
import org.openmrs.module.pihcore.config.registration.BiometricsConfigDescriptor;
import org.openmrs.module.pihcore.deploy.bundle.ConceptsFromMetadataSharing;
import org.openmrs.module.pihcore.deploy.bundle.core.concept.ClinicalConsultationConcepts;
import org.openmrs.module.pihcore.deploy.bundle.core.concept.CommonConcepts;
import org.openmrs.module.pihcore.deploy.bundle.core.concept.SocioEconomicConcepts;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.openmrs.test.SkipBaseSetup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.List;
import java.util.Properties;

import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.core.Is.is;
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
        p.setProperty("pih.config", "default");
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
        when(config.getSite()).thenReturn("MIREBALAIS");
        when(config.getBiometricsConfig()).thenReturn(new BiometricsConfigDescriptor());
        activator.setConfig(config);
        activator.setTestingContext(true);
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
    }

    private void verifyMirebalaisMetadataInstalled() throws Exception {
        // test a few random concepts
        assertThat(MetadataUtils.existing(Concept.class, CommonConcepts.Concepts.YES).getName().getName(), is("Yes"));

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

}
