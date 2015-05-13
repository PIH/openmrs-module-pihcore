package org.openmrs.module.pihcore;

import org.junit.Before;
import org.junit.Test;
import org.openmrs.Concept;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.context.Context;
import org.openmrs.module.metadatadeploy.MetadataUtils;
import org.openmrs.module.metadatadeploy.api.MetadataDeployService;
import org.openmrs.module.metadatadeploy.bundle.MetadataBundle;
import org.openmrs.module.metadatadeploy.bundle.Requires;
import org.openmrs.module.pihcore.deploy.bundle.VersionedPihMetadataBundle;
import org.openmrs.module.pihcore.deploy.bundle.core.concept.ClinicalConsultationConcepts;
import org.openmrs.module.pihcore.deploy.bundle.core.concept.CommonConcepts;
import org.openmrs.module.pihcore.deploy.bundle.core.concept.SocioEconomicConcepts;
import org.openmrs.module.pihcore.deploy.bundle.haiti.HaitiMetadataBundle;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.openmrs.test.SkipBaseSetup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

/**
 * This is an integration test that loads all metadata bundles together
 */
@SkipBaseSetup
public class PihCoreActivatorTest extends BaseModuleContextSensitiveTest {

    @Autowired
    private MetadataDeployService deployService;

    @Autowired
    HaitiMetadataBundle haitiMetadataBundle;

    @Autowired
    @Qualifier("adminService")
    private AdministrationService administrationService;

    @Override
    public Properties getRuntimeProperties() {
        Properties p = super.getRuntimeProperties();
        p.setProperty("pih.config", "pihcore");
        return p;
    }

    @Before
    public void setUp() throws Exception {
        initializeInMemoryDatabase();
        executeDataSet("openmrsCoreDataset.xml");
        authenticate();
    }

    @Test
    public void testMetadataBundles() throws Exception {

        deployService.installBundle(haitiMetadataBundle);

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

        // make sure everything installed at the version we expect
        for (Class<? extends MetadataBundle> bundleType : getExpectedBundles(haitiMetadataBundle.getClass())) {
            if (VersionedPihMetadataBundle.class.isAssignableFrom(bundleType)) {
                VersionedPihMetadataBundle bundle = (VersionedPihMetadataBundle)Context.getRegisteredComponents(bundleType).get(0);
                String gpValue = administrationService.getGlobalProperty("metadatadeploy.bundle.version." + bundle.getClass().getName());
                assertThat(gpValue, is("" + bundle.getVersion()));
            }
        }
    }

    protected List<Class<? extends MetadataBundle>> getExpectedBundles(Class<? extends MetadataBundle> type) {
        List<Class<? extends MetadataBundle>> bundles = new ArrayList<Class<? extends MetadataBundle>>();
        bundles.add(type);
        Requires requires = type.getAnnotation(Requires.class);
        if (requires != null) {
            for (Class<? extends MetadataBundle> requiredBundle : requires.value()) {
                bundles.addAll(getExpectedBundles(requiredBundle));
            }
        }
        return bundles;
    }
}
