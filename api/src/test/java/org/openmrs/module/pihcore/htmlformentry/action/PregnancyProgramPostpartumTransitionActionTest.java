package org.openmrs.module.pihcore.htmlformentry.action;

import org.joda.time.DateTime;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openmrs.Encounter;
import org.openmrs.Location;
import org.openmrs.Patient;
import org.openmrs.PatientProgram;
import org.openmrs.PatientState;
import org.openmrs.Program;
import org.openmrs.api.context.Context;
import org.openmrs.module.htmlformentry.FormEntryContext;
import org.openmrs.module.htmlformentry.FormEntrySession;
import org.openmrs.module.pihcore.PihCoreContextSensitiveTest;

import java.util.Comparator;
import java.util.Date;
import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class PregnancyProgramPostpartumTransitionActionTest extends PihCoreContextSensitiveTest {

    private FormEntrySession mockSession;

    private FormEntryContext mockContext;

    private PregnancyProgramPostpartumTransitionAction pregnancyProgramPostpartumTransitionAction;

    private static final String PREGNANCY_PROGRAM_UUID = "6a5713c2-3fd5-46e7-8f25-36a0f7871e12";

    private static final String ANTENATAL_PROGRAM_STATE_UUID = "a83896bf-9094-4a3c-b843-e75509a52b32";

    private static final String POSTPARTUM_PROGRAM_STATE_UUID = "a735b5f6-0b63-4d9a-ae2e-70d08c947aed";

    @BeforeEach
    public void setUp() {
        // contains mock pregnancy program and locations
        executeDataSet("pregnancyProgramActionTestDataset.xml");

        mockSession = mock(FormEntrySession.class);
        mockContext = mock(FormEntryContext.class);
        when(mockSession.getContext()).thenReturn(mockContext);
        pregnancyProgramPostpartumTransitionAction = new PregnancyProgramPostpartumTransitionAction();
    }

    @Test
    public void setPregnancyProgramPostpartumTransitionAction_shouldEnrollPatientInPregnancyProgramIfNotEnrolled() {
        Program pregnancyProgram = Context.getProgramWorkflowService().getProgramByUuid(PREGNANCY_PROGRAM_UUID);

        Location location = Context.getLocationService().getLocation("Child Location");
        Date now = new DateTime().withMillisOfSecond(0).toDate();  // date enrolled loses millisecond for some reason, so make sure "now" doesn't have millisecond component

        Patient patient = Context.getPatientService().getPatient(7); // patient from standard test dataset
        // sanity check, patient is not enrolled
        List<PatientProgram> patientPregnancyPrograms = Context.getProgramWorkflowService().getPatientPrograms(patient,pregnancyProgram, null, null, now, null, false);
        Assertions.assertEquals(0, patientPregnancyPrograms.size());

        Encounter encounter = new Encounter();
        encounter.setPatient(patient);
        encounter.setEncounterDatetime(now);
        encounter.setLocation(location);

        when(mockSession.getPatient()).thenReturn(patient);
        when(mockSession.getEncounter()).thenReturn(encounter);
        when(mockContext.getMode()).thenReturn(FormEntryContext.Mode.ENTER);
        pregnancyProgramPostpartumTransitionAction.applyAction(mockSession);

        patientPregnancyPrograms = Context.getProgramWorkflowService().getPatientPrograms(patient,pregnancyProgram, null, null, null, null, false);
        Assertions.assertEquals(1, patientPregnancyPrograms.size());
        Assertions.assertEquals(now, patientPregnancyPrograms.get(0).getDateEnrolled());
        Assertions.assertNull(patientPregnancyPrograms.get(0).getDateCompleted());
        Assertions.assertEquals("Parent Location", patientPregnancyPrograms.get(0).getLocation().getName());  // location should be set to parent *visit* location
        Assertions.assertEquals(1, patientPregnancyPrograms.get(0).getStates().size());
        Assertions.assertEquals(POSTPARTUM_PROGRAM_STATE_UUID, patientPregnancyPrograms.get(0).getStates().stream().findFirst().get().getState().getUuid());
    }

    @Test
    public void setPregnancyProgramPostpartumTransitionAction_shouldSetEndDateOfNewProgramIfAnotherPregnancyProgramInTheFuture() {
        Program pregnancyProgram = Context.getProgramWorkflowService().getProgramByUuid(PREGNANCY_PROGRAM_UUID);

        Location location = Context.getLocationService().getLocation("Child Location");
        Date now = new DateTime().withMillisOfSecond(0).toDate();  // date enrolled loses millisecond for some reason, so make sure "now" doesn't have millisecond component
        Date sixMonthsAgo  = new DateTime(now).minusMonths(6).toDate();
        Date oneYearAgo  = new DateTime(now).minusYears(1).toDate();

        Patient patient = Context.getPatientService().getPatient(7); // patient from standard test dataset

        // enroll the patient in the program, starting six months ago
        PatientProgram existingPatientPregnancyProgram = new PatientProgram();
        existingPatientPregnancyProgram.setProgram(pregnancyProgram);
        existingPatientPregnancyProgram.setPatient(patient);
        existingPatientPregnancyProgram.setDateEnrolled(sixMonthsAgo);
        Context.getProgramWorkflowService().savePatientProgram(existingPatientPregnancyProgram);

        // sanity check, patient *is* enrolled
        List<PatientProgram> patientPregnancyPrograms = Context.getProgramWorkflowService().getPatientPrograms(patient,pregnancyProgram, null, null, null, null, false);
        Assertions.assertEquals(1, patientPregnancyPrograms.size());

        Encounter encounter = new Encounter();
        encounter.setPatient(patient);
        encounter.setEncounterDatetime(oneYearAgo); // retro data entry, one year ago
        encounter.setLocation(location);

        when(mockSession.getPatient()).thenReturn(patient);
        when(mockSession.getEncounter()).thenReturn(encounter);
        when(mockContext.getMode()).thenReturn(FormEntryContext.Mode.ENTER);
        pregnancyProgramPostpartumTransitionAction.applyAction(mockSession);

        patientPregnancyPrograms = Context.getProgramWorkflowService().getPatientPrograms(patient,pregnancyProgram, null, null, null, null, false);
        Assertions.assertEquals(2, patientPregnancyPrograms.size());

        patientPregnancyPrograms.sort(Comparator.comparing(PatientProgram::getDateEnrolled));

        Assertions.assertEquals(oneYearAgo, patientPregnancyPrograms.get(0).getDateEnrolled());
        Assertions.assertEquals(sixMonthsAgo, patientPregnancyPrograms.get(0).getDateCompleted());
        Assertions.assertEquals("Parent Location", patientPregnancyPrograms.get(0).getLocation().getName());  // location should be set to parent *visit* location

        Assertions.assertEquals(sixMonthsAgo, patientPregnancyPrograms.get(1).getDateEnrolled());
        Assertions.assertNull(patientPregnancyPrograms.get(1).getDateCompleted());
    }

    @Test
    public void setPregnancyProgramPostpartumTransitionAction_shouldTransitionToPostpartumState() {
        Program pregnancyProgram = Context.getProgramWorkflowService().getProgramByUuid(PREGNANCY_PROGRAM_UUID);

        Location location = Context.getLocationService().getLocation("Child Location");
        Date now = new DateTime().withMillisOfSecond(0).toDate();  // date enrolled loses millisecond for some reason, so make sure "now" doesn't have millisecond component;
        Date oneYearAgo  = new DateTime(now).minusYears(1).toDate();

        Patient patient = Context.getPatientService().getPatient(7); // patient from standard test dataset

        // enroll the patient in the program with an antenatal state
        PatientProgram existingPatientPregnancyProgram = new PatientProgram();
        existingPatientPregnancyProgram.setProgram(pregnancyProgram);
        existingPatientPregnancyProgram.setPatient(patient);
        existingPatientPregnancyProgram.setDateEnrolled(oneYearAgo);
        existingPatientPregnancyProgram.transitionToState(Context.getProgramWorkflowService().getStateByUuid(ANTENATAL_PROGRAM_STATE_UUID), oneYearAgo);
        Context.getProgramWorkflowService().savePatientProgram(existingPatientPregnancyProgram);

        // sanity check, patient *is* enrolled
        List<PatientProgram> patientPregnancyPrograms = Context.getProgramWorkflowService().getPatientPrograms(patient,pregnancyProgram, null, null, null, null, false);
        Assertions.assertEquals(1, patientPregnancyPrograms.size());

        Encounter encounter = new Encounter();
        encounter.setPatient(patient);
        encounter.setEncounterDatetime(now);
        encounter.setLocation(location);

        when(mockSession.getPatient()).thenReturn(patient);
        when(mockSession.getEncounter()).thenReturn(encounter);
        when(mockContext.getMode()).thenReturn(FormEntryContext.Mode.ENTER);
        pregnancyProgramPostpartumTransitionAction.applyAction(mockSession);

        patientPregnancyPrograms = Context.getProgramWorkflowService().getPatientPrograms(patient,pregnancyProgram, null, null, null, null, false);
        Assertions.assertEquals(1, patientPregnancyPrograms.size());

        Assertions.assertEquals(oneYearAgo, patientPregnancyPrograms.get(0).getDateEnrolled());
        Assertions.assertNull(patientPregnancyPrograms.get(0).getDateCompleted());
        Assertions.assertEquals(2, patientPregnancyPrograms.get(0).getStates().size());
        Assertions.assertEquals(1, patientPregnancyPrograms.get(0).getCurrentStates().size());
        PatientState currentState = patientPregnancyPrograms.get(0).getCurrentStates().iterator().next();
        Assertions.assertEquals(POSTPARTUM_PROGRAM_STATE_UUID, currentState.getState().getUuid());
        Assertions.assertEquals(now,currentState.getStartDate());
    }

    @Test
    public void setPregnancyProgramPostpartumTransitionAction_shouldDoNothingIfAlreadyInPostpartumState() {
        Program pregnancyProgram = Context.getProgramWorkflowService().getProgramByUuid(PREGNANCY_PROGRAM_UUID);

        Location location = Context.getLocationService().getLocation("Child Location");
        Date now = new DateTime().withMillisOfSecond(0).toDate();  // date enrolled loses millisecond for some reason, so make sure "now" doesn't have millisecond component;
        Date sixMonthsAgo  = new DateTime(now).minusMonths(6).toDate();
        Date oneYearAgo  = new DateTime(now).minusYears(1).toDate();

        Patient patient = Context.getPatientService().getPatient(7); // patient from standard test dataset

        // enroll the patient in the program with a prenatal state
        PatientProgram existingPatientPregnancyProgram = new PatientProgram();
        existingPatientPregnancyProgram.setProgram(pregnancyProgram);
        existingPatientPregnancyProgram.setPatient(patient);
        existingPatientPregnancyProgram.setDateEnrolled(oneYearAgo);
        existingPatientPregnancyProgram.transitionToState(Context.getProgramWorkflowService().getStateByUuid(ANTENATAL_PROGRAM_STATE_UUID), oneYearAgo);
        existingPatientPregnancyProgram.transitionToState(Context.getProgramWorkflowService().getStateByUuid(POSTPARTUM_PROGRAM_STATE_UUID), sixMonthsAgo);
        Context.getProgramWorkflowService().savePatientProgram(existingPatientPregnancyProgram);

        // sanity check, patient *is* enrolled
        List<PatientProgram> patientPregnancyPrograms = Context.getProgramWorkflowService().getPatientPrograms(patient,pregnancyProgram, null, null, null, null, false);
        Assertions.assertEquals(1, patientPregnancyPrograms.size());

        Encounter encounter = new Encounter();
        encounter.setPatient(patient);
        encounter.setEncounterDatetime(now);
        encounter.setLocation(location);

        when(mockSession.getPatient()).thenReturn(patient);
        when(mockSession.getEncounter()).thenReturn(encounter);
        when(mockContext.getMode()).thenReturn(FormEntryContext.Mode.ENTER);
        pregnancyProgramPostpartumTransitionAction.applyAction(mockSession);

        patientPregnancyPrograms = Context.getProgramWorkflowService().getPatientPrograms(patient,pregnancyProgram, null, null, null, null, false);
        Assertions.assertEquals(1, patientPregnancyPrograms.size());

        Assertions.assertEquals(oneYearAgo, patientPregnancyPrograms.get(0).getDateEnrolled());
        Assertions.assertNull(patientPregnancyPrograms.get(0).getDateCompleted());
        Assertions.assertEquals(2, patientPregnancyPrograms.get(0).getStates().size());
        Assertions.assertEquals(1, patientPregnancyPrograms.get(0).getCurrentStates().size());
        PatientState currentState = patientPregnancyPrograms.get(0).getCurrentStates().iterator().next();
        Assertions.assertEquals(POSTPARTUM_PROGRAM_STATE_UUID, currentState.getState().getUuid());
        Assertions.assertEquals(sixMonthsAgo,currentState.getStartDate());
    }

    @Test
    public void setPregnancyProgramPostpartumTransitionAction_shouldDoNothingInEditMode() {
        Program pregnancyProgram = Context.getProgramWorkflowService().getProgramByUuid(PREGNANCY_PROGRAM_UUID);
        Date now = new DateTime().withMillisOfSecond(0).toDate();  // date enrolled loses millisecond for some reason, so make sure "now" doesn't have millisecond component
        Patient patient = Context.getPatientService().getPatient(7); // patient from standard test dataset
        // sanity check, patient is not enrolled
        List<PatientProgram> patientPregnancyPrograms = Context.getProgramWorkflowService().getPatientPrograms(patient,pregnancyProgram, null, null, now, null, false);
        Assertions.assertEquals(0, patientPregnancyPrograms.size());

        when(mockContext.getMode()).thenReturn(FormEntryContext.Mode.EDIT);
        pregnancyProgramPostpartumTransitionAction.applyAction(mockSession);

        patientPregnancyPrograms = Context.getProgramWorkflowService().getPatientPrograms(patient,pregnancyProgram, null, null, null, null, false);
        Assertions.assertEquals(0, patientPregnancyPrograms.size());
    }
}