package org.openmrs.module.pihcore.task;

import org.joda.time.DateTime;
import org.joda.time.Minutes;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.openmrs.Concept;
import org.openmrs.Patient;
import org.openmrs.PatientProgram;
import org.openmrs.Program;
import org.openmrs.api.context.Context;
import org.openmrs.module.pihcore.PihCoreContextSensitiveTest;
import org.openmrs.module.pihcore.PihEmrConfigConstants;
import org.openmrs.module.pihcore.config.Config;

import java.util.Date;
import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CloseInfantProgramTaskTest extends PihCoreContextSensitiveTest {

    private static final String ANOTHER_PROGRAM_UUID = "550e8400-e29b-41d4-a716-446655440000";

    private CloseInfantProgramTask task;

    @BeforeEach
    public void setUp() {
        executeDataSet("infantProgramTestDataset.xml");
        task = new CloseInfantProgramTask();
        Config config = mock(Config.class);
        when(config.isSierraLeone()).thenReturn(true);
        task.setConfig(config);
    }

    @Test
    public void shouldCloseInfantProgramWithStatePatientAgedOutAfterSixWeeks() {
        Program infantProgram = Context.getProgramWorkflowService().getProgramByUuid(PihEmrConfigConstants.PROGRAM_INFANT_UUID);
        Date now = new DateTime().withMillisOfSecond(0).toDate();  // date enrolled loses millisecond for some reason, so make sure "now" doesn't have millisecond component
        Date sevenWeeksAgo  = new DateTime(now).minusWeeks(7).toDate();

        Patient patient = Context.getPatientService().getPatient(7); // patient from standard test dataset

        Concept patientAgedOut = Context.getConceptService().getConceptByMapping("20492", "PIH");
        PatientProgram existingPatientInfantProgram = new PatientProgram();
        existingPatientInfantProgram.setProgram(infantProgram);
        existingPatientInfantProgram.setPatient(patient);
        existingPatientInfantProgram.setDateEnrolled(sevenWeeksAgo);
        Context.getProgramWorkflowService().savePatientProgram(existingPatientInfantProgram);

        // sanity check, patient program exists
        List<PatientProgram> patientInfantPrograms = Context.getProgramWorkflowService().getPatientPrograms(patient,infantProgram, null, null, null, null, false);
        Assertions.assertEquals(1, patientInfantPrograms.size());

        task.run();

        // should no longer be enrolled
        patientInfantPrograms = Context.getProgramWorkflowService().getPatientPrograms(patient,infantProgram, null, null, null, null, false);
        Assertions.assertEquals(1, patientInfantPrograms.size());
        Assertions.assertEquals(patientAgedOut, patientInfantPrograms.get(0).getOutcome());
        Assertions.assertTrue(Minutes.minutesBetween(new DateTime(now), new DateTime(patientInfantPrograms.get(0).getDateCompleted())).isLessThan(Minutes.minutes(1)));
    }

    @Test
    public void shouldNotCloseInfantProgramIfEnrolledLessThanSixWeeks() {
        Program infantProgram = Context.getProgramWorkflowService().getProgramByUuid(PihEmrConfigConstants.PROGRAM_INFANT_UUID);
        Date now = new DateTime().withMillisOfSecond(0).toDate();  // date enrolled loses millisecond for some reason, so make sure "now" doesn't have millisecond component
        Date fiveWeeksAgo  = new DateTime(now).minusWeeks(5).toDate();

        Patient patient = Context.getPatientService().getPatient(7); // patient from standard test dataset

        PatientProgram existingPatientInfantProgram = new PatientProgram();
        existingPatientInfantProgram.setProgram(infantProgram);
        existingPatientInfantProgram.setPatient(patient);
        existingPatientInfantProgram.setDateEnrolled(fiveWeeksAgo);
        Context.getProgramWorkflowService().savePatientProgram(existingPatientInfantProgram);

        // sanity check, patient program exists
        List<PatientProgram> patientInfantPrograms = Context.getProgramWorkflowService().getPatientPrograms(patient,infantProgram, null, null, null, null, false);
        Assertions.assertEquals(1, patientInfantPrograms.size());

        task.run();

        // should not be unenrolled
        patientInfantPrograms = Context.getProgramWorkflowService().getPatientPrograms(patient,infantProgram, null, null, null, null, false);
        Assertions.assertEquals(1, patientInfantPrograms.size());
        Assertions.assertNull(patientInfantPrograms.get(0).getOutcome());
        Assertions.assertNull(patientInfantPrograms.get(0).getDateCompleted());
    }

    @Test
    public void shouldNotCloseAnotherProgramEvenIfEnrolledMoreThanSixWeeks() {
        Program anotherProgram = Context.getProgramWorkflowService().getProgramByUuid(ANOTHER_PROGRAM_UUID);
        Date now = new DateTime().withMillisOfSecond(0).toDate();  // date enrolled loses millisecond for some reason, so make sure "now" doesn't have millisecond component
        Date sevenWeeksAgo  = new DateTime(now).minusWeeks(5).toDate();

        Patient patient = Context.getPatientService().getPatient(7); // patient from standard test dataset

        PatientProgram existingPatientAnotherProgram = new PatientProgram();
        existingPatientAnotherProgram.setProgram(anotherProgram);
        existingPatientAnotherProgram.setPatient(patient);
        existingPatientAnotherProgram.setDateEnrolled(sevenWeeksAgo);
        Context.getProgramWorkflowService().savePatientProgram(existingPatientAnotherProgram);

        // sanity check, patient program exists
        List<PatientProgram> patientAnotherPrograms = Context.getProgramWorkflowService().getPatientPrograms(patient,anotherProgram, null, null, null, null, false);
        Assertions.assertEquals(1, patientAnotherPrograms.size());

        task.run();

        // should not be unenrolled
        patientAnotherPrograms = Context.getProgramWorkflowService().getPatientPrograms(patient,anotherProgram, null, null, null, null, false);
        Assertions.assertEquals(1, patientAnotherPrograms.size());
        Assertions.assertNull(patientAnotherPrograms.get(0).getOutcome());
        Assertions.assertNull(patientAnotherPrograms.get(0).getDateCompleted());
    }


    @Test
    public void shouldNotChangeOutcomeOrCloseDateIfAlreadySet() {
        Program infantProgram = Context.getProgramWorkflowService().getProgramByUuid(PihEmrConfigConstants.PROGRAM_INFANT_UUID);
        Date now = new DateTime().withMillisOfSecond(0).toDate();  // date enrolled loses millisecond for some reason, so make sure "now" doesn't have millisecond component
        Date sevenWeeksAgo  = new DateTime(now).minusWeeks(7).toDate();
        Date fourWeeksAgo  = new DateTime(now).minusWeeks(4).toDate();


        Patient patient = Context.getPatientService().getPatient(7); // patient from standard test dataset

        Concept anotherOutcome = Context.getConceptService().getConcept(3);  // just another concept from the standard test dataset
        PatientProgram existingPatientInfantProgram = new PatientProgram();
        existingPatientInfantProgram.setProgram(infantProgram);
        existingPatientInfantProgram.setPatient(patient);
        existingPatientInfantProgram.setDateEnrolled(sevenWeeksAgo);
        existingPatientInfantProgram.setDateCompleted(fourWeeksAgo);
        existingPatientInfantProgram.setOutcome(anotherOutcome);
        Context.getProgramWorkflowService().savePatientProgram(existingPatientInfantProgram);

        // sanity check, patient program exists
        List<PatientProgram> patientInfantPrograms = Context.getProgramWorkflowService().getPatientPrograms(patient,infantProgram, null, null, null, null, false);
        Assertions.assertEquals(1, patientInfantPrograms.size());

        task.run();

        // outcome and date completed should not be updated
        patientInfantPrograms = Context.getProgramWorkflowService().getPatientPrograms(patient,infantProgram, null, null, null, null, false);
        Assertions.assertEquals(1, patientInfantPrograms.size());
        Assertions.assertEquals(anotherOutcome, patientInfantPrograms.get(0).getOutcome());
        Assertions.assertEquals(fourWeeksAgo, patientInfantPrograms.get(0).getDateCompleted());
    }

}
