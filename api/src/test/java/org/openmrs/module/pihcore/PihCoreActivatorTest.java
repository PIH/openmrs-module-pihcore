package org.openmrs.module.pihcore;

import org.junit.Before;
import org.junit.Test;
import org.openmrs.Concept;
import org.openmrs.api.AdministrationService;
import org.openmrs.module.metadatadeploy.MetadataUtils;
import org.openmrs.module.metadatadeploy.api.MetadataDeployService;
import org.openmrs.module.metadatadeploy.bundle.MetadataBundle;
import org.openmrs.module.pihcore.deploy.bundle.CommonConcepts;
import org.openmrs.module.pihcore.deploy.bundle.SocioEconomicConcepts;
import org.openmrs.module.pihcore.deploy.bundle.VersionedPihMetadataBundle;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.openmrs.test.SkipBaseSetup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.List;

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
    @Qualifier("adminService")
    private AdministrationService administrationService;

    @Autowired
    private List<MetadataBundle> bundles;

    @Before
    public void setUp() throws Exception {
        initializeInMemoryDatabase();
        executeDataSet("openmrsCoreDataset.xml");
        authenticate();
    }

    @Test
    public void testAllMetadataBundles() throws Exception {
        deployService.installBundles(bundles);

        // test a few random concepts
        assertThat(MetadataUtils.existing(Concept.class, CommonConcepts.Concepts.YES).getName().getName(), is("Yes"));

        Concept mainActivity = MetadataUtils.existing(Concept.class, SocioEconomicConcepts.Concepts.MAIN_ACTIVITY);
        assertThat(mainActivity.getDatatype().getName(), is("Coded"));
        assertThat(mainActivity.getAnswers().size(), greaterThan(5));

        // make sure everything installed at the version we expect
        for (MetadataBundle bundle : bundles) {
            if (bundle instanceof VersionedPihMetadataBundle) {
                VersionedPihMetadataBundle versionedBundle = (VersionedPihMetadataBundle) bundle;
                assertThat(administrationService.getGlobalProperty("metadatadeploy.bundle.version." + bundle.getClass().getName()),
                        is("" + versionedBundle.getVersion()));
            }
        }
    }
}