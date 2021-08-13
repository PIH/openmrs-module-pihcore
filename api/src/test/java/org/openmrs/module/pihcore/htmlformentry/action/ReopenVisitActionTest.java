package org.openmrs.module.pihcore.htmlformentry.action;

import org.junit.Before;
import org.junit.Test;
import org.openmrs.Encounter;
import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.module.htmlformentry.FormEntryContext;
import org.openmrs.module.htmlformentry.FormEntrySession;
import org.openmrs.test.BaseModuleContextSensitiveTest;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ReopenVisitActionTest extends BaseModuleContextSensitiveTest {

    private FormEntrySession mockSession;

    private FormEntryContext mockContext;

    private ReopenVisitAction reopenVisitAction;

    @Before
    public void setUp() {
        executeDataSet("reopenVisitActionTestDataset.xml");

        mockSession = mock(FormEntrySession.class);
        mockContext = mock(FormEntryContext.class);
        when(mockSession.getContext()).thenReturn(mockContext);
        when(mockContext.getMode()).thenReturn(FormEntryContext.Mode.ENTER);

        Patient patient = Context.getPatientService().getPatient(7);
        when(mockSession.getPatient()).thenReturn(patient);

        reopenVisitAction = new ReopenVisitAction();
    }

    @Test
    public void reopenVisitAction_shouldReopenVisitIfNoSubsequentVisitAtSameLocation() {
        Encounter encounter = Context.getEncounterService().getEncounter(1002);

        // sanity check
        assertNotNull(encounter.getVisit().getStopDatetime());

        when(mockSession.getEncounter()).thenReturn(encounter);
        reopenVisitAction.applyAction(mockSession);
        assertNull(encounter.getVisit().getStopDatetime());

    }

    @Test
    public void reopenVisitAction_shouldNotReopenVisitIfSubsequentVisitAtSameLocation() {
        Encounter encounter = Context.getEncounterService().getEncounter(1001);

        // sanity check
        assertNotNull(encounter.getVisit().getStopDatetime());

        when(mockSession.getEncounter()).thenReturn(encounter);
        reopenVisitAction.applyAction(mockSession);
        assertNotNull(encounter.getVisit().getStopDatetime());

    }

}
