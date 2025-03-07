package org.openmrs.module.pihcore.htmlformentry.action;

import org.joda.time.DateTime;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.openmrs.Concept;
import org.openmrs.Encounter;
import org.openmrs.Location;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.PatientProgram;
import org.openmrs.PatientState;
import org.openmrs.Program;
import org.openmrs.api.context.Context;
import org.openmrs.module.htmlformentry.FormEntrySession;
import org.openmrs.module.pihcore.PihCoreContextSensitiveTest;

import java.util.Date;
import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TransitionToPrenatalGroupActionTest extends PihCoreContextSensitiveTest {

    private FormEntrySession mockSession;

    private TransitionToPrenatalGroupAction transitionToPrenatalGroupAction;

    private static String MCH_PROGRAM_UUID = "41a2715e-8a14-11e8-9a94-a6cf71072f73";

    private static String TYPE_OF_TREATMENT_WORKFLOW_UUID = "41a277d0-8a14-11e8-9a94-a6cf71072f73";

    private static String PRENATAL_GROUP_STATE_UUID = "41a2753c-8a14-11e8-9a94-a6cf71072f73";

    private static String PRENATAL_INDIVIDUAL_STATE_UUID = "41a27d48-8a14-11e8-9a94-a6cf71072f73";


    @BeforeEach
    public void setUp() {
        executeDataSet("mchProgramTestDataset.xml");

        mockSession = mock(FormEntrySession.class);
        transitionToPrenatalGroupAction = new TransitionToPrenatalGroupAction();
    }

    @Test
    public void transitionToPrenatalGroupAction_shouldTransitionPatientToPrenatalGroup() {
        Program mchProgram = Context.getProgramWorkflowService().getProgramByUuid(MCH_PROGRAM_UUID);
        Location location = Context.getLocationService().getLocation("Child Location");
        Date now = new DateTime().withMillisOfSecond(0).toDate();
        Date oneYearAgo  = new DateTime(now).minusYears(1).toDate();

        Patient patient = Context.getPatientService().getPatient(7); // patient from standard test dataset


        // enroll the patient in the MCH program with an prenatal individual state
        PatientProgram existingPatientProgram = new PatientProgram();
        existingPatientProgram.setProgram(mchProgram);
        existingPatientProgram.setPatient(patient);
        existingPatientProgram.setDateEnrolled(oneYearAgo);
        existingPatientProgram.transitionToState(Context.getProgramWorkflowService().getStateByUuid(PRENATAL_INDIVIDUAL_STATE_UUID), oneYearAgo);
        Context.getProgramWorkflowService().savePatientProgram(existingPatientProgram);

        // sanity check, patient *is* enrolled
        List<PatientProgram> patientPrograms = Context.getProgramWorkflowService().getPatientPrograms(patient, mchProgram, null, null, null, null, false);
        Assertions.assertEquals(1, patientPrograms.size());

        Encounter encounter = new Encounter();
        encounter.setPatient(patient);
        encounter.setEncounterDatetime(now);
        encounter.setLocation(location);

        // add the that triggers the state change
        Concept treatmentType = Context.getConceptService().getConceptByMapping("11698", "PIH");
        Concept prenatalGroup = Context.getConceptService().getConceptByMapping("11699", "PIH");
        Obs obs = new Obs();
        obs.setConcept(treatmentType);
        obs.setValueCoded(prenatalGroup);
        encounter.addObs(obs);

        when(mockSession.getPatient()).thenReturn(patient);
        when(mockSession.getEncounter()).thenReturn(encounter);
        transitionToPrenatalGroupAction.applyAction(mockSession);

        patientPrograms = Context.getProgramWorkflowService().getPatientPrograms(patient, mchProgram, null, null, null, null, false);
        Assertions.assertEquals(1, patientPrograms.size());

        Assertions.assertEquals(oneYearAgo, patientPrograms.get(0).getDateEnrolled());
        Assertions.assertNull(patientPrograms.get(0).getDateCompleted());
        Assertions.assertNull(patientPrograms.get(0).getOutcome());
        Assertions.assertEquals(2, patientPrograms.get(0).getStates().size());
        Assertions.assertEquals(1, patientPrograms.get(0).getCurrentStates().size());
        PatientState currentState = patientPrograms.get(0).getCurrentStates().iterator().next();
        Assertions.assertEquals(PRENATAL_GROUP_STATE_UUID, currentState.getState().getUuid());
        Assertions.assertEquals(now, currentState.getStartDate());
    }

    @Test
    public void transitionToPrenatalGroupAction_shouldNotTransitionPatientIfCurrentStateOnEncounterDateIsNotMostRecentStatus() {
        Program mchProgram = Context.getProgramWorkflowService().getProgramByUuid(MCH_PROGRAM_UUID);
        Location location = Context.getLocationService().getLocation("Child Location");
        Date now = new DateTime().withMillisOfSecond(0).toDate();
        Date oneMonthAgo = new DateTime(now).minusMonths(1).toDate();
        Date twoMonthsAgo = new DateTime(now).minusMonths(2).toDate();
        Date oneYearAgo  = new DateTime(now).minusYears(1).toDate();

        Patient patient = Context.getPatientService().getPatient(7); // patient from standard test dataset

        // enroll the patient in the MCH program with an prenatal individual state, then also transition to prenatal group state one months ago
        PatientProgram existingPatientProgram = new PatientProgram();
        existingPatientProgram.setProgram(mchProgram);
        existingPatientProgram.setPatient(patient);
        existingPatientProgram.setDateEnrolled(oneYearAgo);
        existingPatientProgram.transitionToState(Context.getProgramWorkflowService().getStateByUuid(PRENATAL_INDIVIDUAL_STATE_UUID), oneYearAgo);
        existingPatientProgram.transitionToState(Context.getProgramWorkflowService().getStateByUuid(PRENATAL_GROUP_STATE_UUID), oneMonthAgo);
        Context.getProgramWorkflowService().savePatientProgram(existingPatientProgram);

        // sanity check, patient *is* enrolled
        List<PatientProgram> patientPrograms = Context.getProgramWorkflowService().getPatientPrograms(patient, mchProgram, null, null, null, null, false);
        Assertions.assertEquals(1, patientPrograms.size());

        // set the encounter to two months ago (before the transition to the other state)
        Encounter encounter = new Encounter();
        encounter.setPatient(patient);
        encounter.setEncounterDatetime(twoMonthsAgo);
        encounter.setLocation(location);

        // add the that triggers the state change
        Concept treatmentType = Context.getConceptService().getConceptByMapping("11698", "PIH");
        Concept prenatalGroup = Context.getConceptService().getConceptByMapping("11699", "PIH");
        Obs obs = new Obs();
        obs.setConcept(treatmentType);
        obs.setValueCoded(prenatalGroup);
        encounter.addObs(obs);

        when(mockSession.getPatient()).thenReturn(patient);
        when(mockSession.getEncounter()).thenReturn(encounter);
        transitionToPrenatalGroupAction.applyAction(mockSession);

        patientPrograms = Context.getProgramWorkflowService().getPatientPrograms(patient, mchProgram, null, null, null, null, false);
        Assertions.assertEquals(1, patientPrograms.size());

        // nothing should have changed
        Assertions.assertEquals(oneYearAgo, patientPrograms.get(0).getDateEnrolled());
        Assertions.assertNull(patientPrograms.get(0).getDateCompleted());
        Assertions.assertNull(patientPrograms.get(0).getOutcome());
        Assertions.assertEquals(2, patientPrograms.get(0).getStates().size());
        Assertions.assertEquals(1, patientPrograms.get(0).getCurrentStates().size());
        PatientState currentState = patientPrograms.get(0).getCurrentStates().iterator().next();
        Assertions.assertEquals(PRENATAL_GROUP_STATE_UUID, currentState.getState().getUuid());
        Assertions.assertEquals(oneMonthAgo, currentState.getStartDate());
    }

    @Test
    public void transitionToPrenatalGroupAction_shouldNotTransitionIfPatientNotInProgramOnEncounterDate() {
        Program mchProgram = Context.getProgramWorkflowService().getProgramByUuid(MCH_PROGRAM_UUID);
        Location location = Context.getLocationService().getLocation("Child Location");
        Date now = new DateTime().withMillisOfSecond(0).toDate();
        Date twoMonthsAgo = new DateTime(now).minusMonths(2).toDate();

        Patient patient = Context.getPatientService().getPatient(7); // patient from standard test dataset

        // enroll the patient in the MCH program
        PatientProgram existingPatientProgram = new PatientProgram();
        existingPatientProgram.setProgram(mchProgram);
        existingPatientProgram.setPatient(patient);
        existingPatientProgram.setDateEnrolled(now);
        Context.getProgramWorkflowService().savePatientProgram(existingPatientProgram);

        // sanity check, patient *is* enrolled
        List<PatientProgram> patientPrograms = Context.getProgramWorkflowService().getPatientPrograms(patient, mchProgram, null, null, null, null, false);
        Assertions.assertEquals(1, patientPrograms.size());

        // set the encounter to two months ago (before the enrollment)
        Encounter encounter = new Encounter();
        encounter.setPatient(patient);
        encounter.setEncounterDatetime(twoMonthsAgo);
        encounter.setLocation(location);

        // add the that triggers the state change
        Concept treatmentType = Context.getConceptService().getConceptByMapping("11698", "PIH");
        Concept prenatalGroup = Context.getConceptService().getConceptByMapping("11699", "PIH");
        Obs obs = new Obs();
        obs.setConcept(treatmentType);
        obs.setValueCoded(prenatalGroup);
        encounter.addObs(obs);

        when(mockSession.getPatient()).thenReturn(patient);
        when(mockSession.getEncounter()).thenReturn(encounter);
        transitionToPrenatalGroupAction.applyAction(mockSession);

        patientPrograms = Context.getProgramWorkflowService().getPatientPrograms(patient, mchProgram, null, null, null, null, false);
        Assertions.assertEquals(1, patientPrograms.size());

        // nothing should have changed
        Assertions.assertEquals(now, patientPrograms.get(0).getDateEnrolled());
        Assertions.assertNull(patientPrograms.get(0).getDateCompleted());
        Assertions.assertNull(patientPrograms.get(0).getOutcome());
        Assertions.assertEquals(0, patientPrograms.get(0).getStates().size());
    }


}

