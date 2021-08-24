package org.openmrs.module.pihcore.action;

import org.junit.Before;
import org.junit.Test;
import org.openmrs.Encounter;
import org.openmrs.api.context.Context;
import org.openmrs.module.emrapi.encounter.EncounterDomainWrapper;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class ReopenVisitDispositionActionTest extends BaseModuleContextSensitiveTest {

    @Autowired
    private ReopenVisitDispositionAction reopenVisitDispositionAction;

    @Before
    public void setUp() {
        executeDataSet("reopenVisitActionTestDataset.xml");
    }


    @Test
    public void reopenVisitAction_shouldReopenVisitIfNoSubsequentVisitAtSameLocation() {
        Encounter encounter = Context.getEncounterService().getEncounter(1002);

        // sanity check
        assertNotNull(encounter.getVisit().getStopDatetime());

        reopenVisitDispositionAction.action(new EncounterDomainWrapper(encounter), null, null);
        assertNull(encounter.getVisit().getStopDatetime());

    }

    @Test
    public void reopenVisitAction_shouldNotReopenVisitIfSubsequentVisitAtSameLocation() {
        Encounter encounter = Context.getEncounterService().getEncounter(1001);

        // sanity check
        assertNotNull(encounter.getVisit().getStopDatetime());

        reopenVisitDispositionAction.action(new EncounterDomainWrapper(encounter), null, null);
        assertNotNull(encounter.getVisit().getStopDatetime());

    }


}
