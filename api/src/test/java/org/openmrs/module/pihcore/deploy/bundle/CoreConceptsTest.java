package org.openmrs.module.pihcore.deploy.bundle;

import org.junit.Before;
import org.junit.Test;
import org.openmrs.Concept;
import org.openmrs.api.AdministrationService;
import org.openmrs.module.metadatadeploy.MetadataUtils;
import org.openmrs.module.metadatadeploy.api.MetadataDeployService;
import org.openmrs.module.metadatadeploy.bundle.MetadataBundle;
import org.openmrs.module.pihcore.deploy.bundle.CoreConcepts.Concepts;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.openmrs.test.SkipBaseSetup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.Arrays;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

@SkipBaseSetup
public class CoreConceptsTest extends BaseModuleContextSensitiveTest {

    @Autowired
    private CoreConcepts coreConcepts;

    @Autowired
    private MetadataDeployService deployService;

    @Autowired
    @Qualifier("adminService")
    private AdministrationService administrationService;

    @Before
    public void setUp() throws Exception {
        initializeInMemoryDatabase();
        executeDataSet("openmrsCoreDataset.xml");
        authenticate();
    }

    @Test
    public void testInstall() throws Exception {
        deployService.installBundles(Arrays.<MetadataBundle>asList(coreConcepts));

        assertThat(MetadataUtils.existing(Concept.class, Concepts.YES).getName().getName(), is("Yes"));

        Concept construct = MetadataUtils.existing(Concept.class, Concepts.PAST_MEDICAL_HISTORY_CONSTRUCT);
        assertThat(construct.getUuid(), is(Concepts.PAST_MEDICAL_HISTORY_CONSTRUCT));
        assertThat(construct.getConceptSets().size(), is(3));

        construct = MetadataUtils.existing(Concept.class, Concepts.FAMILY_HISTORY_CONSTRUCT);
        assertThat(construct.getUuid(), is(Concepts.FAMILY_HISTORY_CONSTRUCT));
        assertThat(construct.getConceptSets().size(), is(4));

        assertThat(administrationService.getGlobalProperty("metadatadeploy.bundle.version." + coreConcepts.getClass().getName()),
                is("" + coreConcepts.getVersion()));
    }

}