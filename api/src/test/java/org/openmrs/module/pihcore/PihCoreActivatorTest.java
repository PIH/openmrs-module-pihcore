package org.openmrs.module.pihcore;

import org.junit.Before;
import org.junit.Test;
import org.openmrs.module.initializer.Domain;
import org.openmrs.module.metadatadeploy.bundle.MetadataBundle;
import org.openmrs.module.metadatadeploy.bundle.Requires;
import org.openmrs.module.pihcore.setup.CloseStaleVisitsSetup;
import org.openmrs.scheduler.SchedulerService;
import org.openmrs.scheduler.TaskDefinition;
import org.openmrs.test.SkipBaseSetup;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.openmrs.module.pihcore.PihCoreConstants.TASK_CLOSE_STALE_VISITS_NAME;

/**
 * This is an integration test that loads all metadata bundles together
 */
@SkipBaseSetup
public class PihCoreActivatorTest extends PihCoreContextSensitiveTest {

    @Autowired
    private SchedulerService schedulerService;

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
        loadFromInitializer(Domain.CONCEPT_SOURCES, "conceptSources.csv");
        loadFromInitializer(Domain.RELATIONSHIP_TYPES, "pih.csv");
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

    @Test
    public void testSetupCloseStateVisits() throws Exception {
        CloseStaleVisitsSetup.setupCloseStaleVisitsTask();

        // verify scheduled task is started
        TaskDefinition closeStaleVisitsTask = schedulerService.getTaskByName(TASK_CLOSE_STALE_VISITS_NAME);
        assertThat(closeStaleVisitsTask, is(notNullValue()));
        assertThat(closeStaleVisitsTask.getStarted(), is(true));
        assertThat(closeStaleVisitsTask.getStartOnStartup(), is(true));
        assertTrue(closeStaleVisitsTask.getSecondsUntilNextExecutionTime() <= 300);
    }


}
