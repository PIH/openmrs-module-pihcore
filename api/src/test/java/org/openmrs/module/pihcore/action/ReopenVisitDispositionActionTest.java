package org.openmrs.module.pihcore.action;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openmrs.Encounter;
import org.openmrs.api.context.Context;
import org.openmrs.module.emrapi.encounter.EncounterDomainWrapper;
import org.openmrs.module.pihcore.PihCoreContextSensitiveTest;
import org.openmrs.test.jupiter.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

public class ReopenVisitDispositionActionTest extends PihCoreContextSensitiveTest {

    @Autowired
    private ReopenVisitDispositionAction reopenVisitDispositionAction;

    @BeforeEach
    public void setup() {
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
