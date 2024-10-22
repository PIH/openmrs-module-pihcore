package org.openmrs.module.pihcore.task;

import org.joda.time.DateTime;
import org.joda.time.Minutes;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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

public class ClosePregnancyProgramTaskTest extends PihCoreContextSensitiveTest {

    private static final String ANOTHER_PROGRAM_UUID = "550e8400-e29b-41d4-a716-446655440000";

    private ClosePregnancyProgramTask task;

    @Override
    public String getPihConfig() {
        return "sierraLeone";
    }

    @BeforeEach
    public void setUp() {
        executeDataSet("pregnancyProgramTestDataset.xml");
        task = new ClosePregnancyProgramTask();
        Config config = mock(Config.class);
        when(config.isSierraLeone()).thenReturn(true);
        task.setConfig(config);
    }

    @Test
    public void shouldClosePregnancyProgramWithStateTreatmentCompleteSixWeeksAfterDelivery() {
        Program pregnancyProgram = Context.getProgramWorkflowService().getProgramByUuid(PihEmrConfigConstants.PROGRAM_PREGNANCY_UUID);
        Date now = new DateTime().withMillisOfSecond(0).toDate();  // date enrolled loses millisecond for some reason, so make sure "now" doesn't have millisecond component
        Date sevenWeeksAgo  = new DateTime(now).minusWeeks(7).toDate();
        Date tenMonthsAgo  = new DateTime(now).minusMonths(10).toDate();
        Concept treatmentCompleteConcept = Context.getConceptService().getConceptByMapping("1714", "PIH");
        Assertions.assertNotNull(treatmentCompleteConcept);  // sanity check

        Patient patient = Context.getPatientService().getPatient(7); // patient from standard test dataset

        // enroll the patient in the program with a postpartum state
        PatientProgram existingPatientPregnancyProgram = new PatientProgram();
        existingPatientPregnancyProgram.setProgram(pregnancyProgram);
        existingPatientPregnancyProgram.setPatient(patient);
        existingPatientPregnancyProgram.setDateEnrolled(tenMonthsAgo);
        existingPatientPregnancyProgram.transitionToState(Context.getProgramWorkflowService().getStateByUuid(PihEmrConfigConstants.PROGRAMWORKFLOW_PREGNANCYPROGRAMTYPEOFTREATMENT_STATE_ANTENATAL_UUID), tenMonthsAgo);
        existingPatientPregnancyProgram.transitionToState(Context.getProgramWorkflowService().getStateByUuid(PihEmrConfigConstants.PROGRAMWORKFLOW_PREGNANCYPROGRAMTYPEOFTREATMENT_STATE_POSTPARTUM_UUID), sevenWeeksAgo);
        Context.getProgramWorkflowService().savePatientProgram(existingPatientPregnancyProgram);

        // sanity check, patient program exists
        List<PatientProgram> patientPregnancyPrograms = Context.getProgramWorkflowService().getPatientPrograms(patient,pregnancyProgram, null, null, null, null, false);
        Assertions.assertEquals(1, patientPregnancyPrograms.size());
        task.run();

        // should no longer be enrolled
        patientPregnancyPrograms = Context.getProgramWorkflowService().getPatientPrograms(patient,pregnancyProgram, null, null, null, null, false);
        Assertions.assertEquals(1, patientPregnancyPrograms.size());
        Assertions.assertEquals(treatmentCompleteConcept, patientPregnancyPrograms.get(0).getOutcome()); 
        Assertions.assertTrue(Minutes.minutesBetween(new DateTime(now), new DateTime(patientPregnancyPrograms.get(0).getDateCompleted())).isLessThan(Minutes.minutes(1)));
    }
    
    @Test
    public void shouldNotClosePregnancyProgramAfterSixWeeksIfInAntenatalState() {
        Program pregnancyProgram = Context.getProgramWorkflowService().getProgramByUuid(PihEmrConfigConstants.PROGRAM_PREGNANCY_UUID);
        Date now = new DateTime().withMillisOfSecond(0).toDate();  // date enrolled loses millisecond for some reason, so make sure "now" doesn't have millisecond component
        Date sevenWeeksAgo  = new DateTime(now).minusWeeks(7).toDate();
        Date tenMonthsAgo  = new DateTime(now).minusMonths(10).toDate();
        Concept treatmentCompleteConcept = Context.getConceptService().getConceptByMapping("1714", "PIH");
        Assertions.assertNotNull(treatmentCompleteConcept);  // sanity check

        Patient patient = Context.getPatientService().getPatient(7); // patient from standard test dataset

        // enroll the patient in the program with a postpartum state
        PatientProgram existingPatientPregnancyProgram = new PatientProgram();
        existingPatientPregnancyProgram.setProgram(pregnancyProgram);
        existingPatientPregnancyProgram.setPatient(patient);
        existingPatientPregnancyProgram.setDateEnrolled(tenMonthsAgo);
        existingPatientPregnancyProgram.transitionToState(Context.getProgramWorkflowService().getStateByUuid(PihEmrConfigConstants.PROGRAMWORKFLOW_PREGNANCYPROGRAMTYPEOFTREATMENT_STATE_ANTENATAL_UUID), sevenWeeksAgo);
        Context.getProgramWorkflowService().savePatientProgram(existingPatientPregnancyProgram);

        // sanity check, patient program exists
        List<PatientProgram> patientPregnancyPrograms = Context.getProgramWorkflowService().getPatientPrograms(patient,pregnancyProgram, null, null, null, null, false);
        Assertions.assertEquals(1, patientPregnancyPrograms.size());
        task.run();

        // should still be enrolled
        patientPregnancyPrograms = Context.getProgramWorkflowService().getPatientPrograms(patient,pregnancyProgram, null, null, null, null, false);
        Assertions.assertEquals(1, patientPregnancyPrograms.size());
        Assertions.assertNull(patientPregnancyPrograms.get(0).getOutcome()); 
        Assertions.assertNull(patientPregnancyPrograms.get(0).getDateCompleted());
    }
    
    @Test
    public void shouldNotClosePregnancyProgramIfLessThanSixWeeksFromDelivery() {
        Program pregnancyProgram = Context.getProgramWorkflowService().getProgramByUuid(PihEmrConfigConstants.PROGRAM_PREGNANCY_UUID);
        Date now = new DateTime().withMillisOfSecond(0).toDate();  // date enrolled loses millisecond for some reason, so make sure "now" doesn't have millisecond component
        Date fiveWeeksAgo = new DateTime(now).minusWeeks(5).toDate();
        Date tenMonthsAgo  = new DateTime(now).minusMonths(10).toDate();
        Concept treatmentCompleteConcept = Context.getConceptService().getConceptByMapping("1714", "PIH");
        Assertions.assertNotNull(treatmentCompleteConcept);  // sanity check

        Patient patient = Context.getPatientService().getPatient(7); // patient from standard test dataset

        // enroll the patient in the program with a postpartum state
        PatientProgram existingPatientPregnancyProgram = new PatientProgram();
        existingPatientPregnancyProgram.setProgram(pregnancyProgram);
        existingPatientPregnancyProgram.setPatient(patient);
        existingPatientPregnancyProgram.setDateEnrolled(tenMonthsAgo);
        existingPatientPregnancyProgram.transitionToState(Context.getProgramWorkflowService().getStateByUuid(PihEmrConfigConstants.PROGRAMWORKFLOW_PREGNANCYPROGRAMTYPEOFTREATMENT_STATE_ANTENATAL_UUID), tenMonthsAgo);
        existingPatientPregnancyProgram.transitionToState(Context.getProgramWorkflowService().getStateByUuid(PihEmrConfigConstants.PROGRAMWORKFLOW_PREGNANCYPROGRAMTYPEOFTREATMENT_STATE_POSTPARTUM_UUID), fiveWeeksAgo);
        Context.getProgramWorkflowService().savePatientProgram(existingPatientPregnancyProgram);

        // sanity check, patient program exists
        List<PatientProgram> patientPregnancyPrograms = Context.getProgramWorkflowService().getPatientPrograms(patient,pregnancyProgram, null, null, null, null, false);
        Assertions.assertEquals(1, patientPregnancyPrograms.size());
        task.run();

        // should still be enrolled
        patientPregnancyPrograms = Context.getProgramWorkflowService().getPatientPrograms(patient,pregnancyProgram, null, null, null, null, false);
        Assertions.assertEquals(1, patientPregnancyPrograms.size());
        Assertions.assertNull(patientPregnancyPrograms.get(0).getOutcome()); 
        Assertions.assertNull(patientPregnancyPrograms.get(0).getDateCompleted());
    }

    @Test
    public void shouldNotUpdateDateCompletedOrOutcomeIfProgramAlreadyClosed() {
        Program pregnancyProgram = Context.getProgramWorkflowService().getProgramByUuid(PihEmrConfigConstants.PROGRAM_PREGNANCY_UUID);
        Date now = new DateTime().withMillisOfSecond(0).toDate();  // date enrolled loses millisecond for some reason, so make sure "now" doesn't have millisecond component
        Date sevenWeeksAgo  = new DateTime(now).minusWeeks(7).toDate();
        Date tenMonthsAgo  = new DateTime(now).minusMonths(10).toDate();
        Concept treatmentCompleteConcept = Context.getConceptService().getConceptByMapping("1714", "PIH");
        Assertions.assertNotNull(treatmentCompleteConcept);  // sanity check

        Patient patient = Context.getPatientService().getPatient(7); // patient from standard test dataset

        // enroll the patient in the program, and transition to miscarried, which is a *terminal state*
        PatientProgram existingPatientPregnancyProgram = new PatientProgram();
        existingPatientPregnancyProgram.setProgram(pregnancyProgram);
        existingPatientPregnancyProgram.setPatient(patient);
        existingPatientPregnancyProgram.setDateEnrolled(tenMonthsAgo);
        existingPatientPregnancyProgram.transitionToState(Context.getProgramWorkflowService().getStateByUuid(PihEmrConfigConstants.PROGRAMWORKFLOW_PREGNANCYPROGRAMTYPEOFTREATMENT_STATE_MISCARRIED_UUID), sevenWeeksAgo);
        Context.getProgramWorkflowService().savePatientProgram(existingPatientPregnancyProgram);

        // sanity check, patient program exists
        List<PatientProgram> patientPregnancyPrograms = Context.getProgramWorkflowService().getPatientPrograms(patient,pregnancyProgram, null, null, null, null, false);
        Assertions.assertEquals(1, patientPregnancyPrograms.size());
        task.run();

        // program was closed seven weeks ago (by transitioning to miscarried state), so outcome and completion data should not be updated
        patientPregnancyPrograms = Context.getProgramWorkflowService().getPatientPrograms(patient,pregnancyProgram, null, null, null, null, false);
        Assertions.assertEquals(1, patientPregnancyPrograms.size());
        Assertions.assertNull(patientPregnancyPrograms.get(0).getOutcome());
        Assertions.assertEquals(sevenWeeksAgo, patientPregnancyPrograms.get(0).getDateCompleted());
    }

    @Test
    public void shouldNotCloseAnotherProgram() {
        Program anotherProgram = Context.getProgramWorkflowService().getProgramByUuid(ANOTHER_PROGRAM_UUID);
        Date now = new DateTime().withMillisOfSecond(0).toDate();  // date enrolled loses millisecond for some reason, so make sure "now" doesn't have millisecond component
        Date tenMonthsAgo  = new DateTime(now).minusMonths(10).toDate();
        Concept treatmentCompleteConcept = Context.getConceptService().getConceptByMapping("1714", "PIH");
        Assertions.assertNotNull(treatmentCompleteConcept);  // sanity check

        Patient patient = Context.getPatientService().getPatient(7); // patient from standard test dataset

        // enroll the patient in the program with a postpartum state
        PatientProgram existingPatientProgram = new PatientProgram();
        existingPatientProgram.setProgram(anotherProgram);
        existingPatientProgram.setPatient(patient);
        existingPatientProgram.setDateEnrolled(tenMonthsAgo);
        Context.getProgramWorkflowService().savePatientProgram(existingPatientProgram);

        // sanity check, patient program exists
        List<PatientProgram> patientPrograms = Context.getProgramWorkflowService().getPatientPrograms(patient,anotherProgram, null, null, null, null, false);
        Assertions.assertEquals(1, patientPrograms.size());
        task.run();

        // should still be enrolled
        patientPrograms = Context.getProgramWorkflowService().getPatientPrograms(patient,anotherProgram, null, null, null, null, false);
        Assertions.assertEquals(1, patientPrograms.size());
        Assertions.assertNull(patientPrograms.get(0).getOutcome());
        Assertions.assertNull(patientPrograms.get(0).getDateCompleted());
    }

    @Test
    public void shouldClosePregnancyProgramWithStateLostToFollowUpIfInAntenatalStateForMoreThanElevenMonths() {
        Program pregnancyProgram = Context.getProgramWorkflowService().getProgramByUuid(PihEmrConfigConstants.PROGRAM_PREGNANCY_UUID);
        Date now = new DateTime().withMillisOfSecond(0).toDate();  // date enrolled loses millisecond for some reason, so make sure "now" doesn't have millisecond component
        Date twelveMonthsAgo  = new DateTime(now).minusMonths(12).toDate();
        Concept lostToFollowupConcept = Context.getConceptService().getConceptByMapping("5240", "PIH");
        Assertions.assertNotNull(lostToFollowupConcept);  // sanity check

        Patient patient = Context.getPatientService().getPatient(7); // patient from standard test dataset

        // enroll the patient in the program with an antenatal state
        PatientProgram existingPatientPregnancyProgram = new PatientProgram();
        existingPatientPregnancyProgram.setProgram(pregnancyProgram);
        existingPatientPregnancyProgram.setPatient(patient);
        existingPatientPregnancyProgram.setDateEnrolled(twelveMonthsAgo);
        existingPatientPregnancyProgram.transitionToState(Context.getProgramWorkflowService().getStateByUuid(PihEmrConfigConstants.PROGRAMWORKFLOW_PREGNANCYPROGRAMTYPEOFTREATMENT_STATE_ANTENATAL_UUID), twelveMonthsAgo);
        Context.getProgramWorkflowService().savePatientProgram(existingPatientPregnancyProgram);

        // sanity check, patient program exists
        List<PatientProgram> patientPregnancyPrograms = Context.getProgramWorkflowService().getPatientPrograms(patient,pregnancyProgram, null, null, null, null, false);
        Assertions.assertEquals(1, patientPregnancyPrograms.size());
        task.run();

        // should no longer be enrolled
        patientPregnancyPrograms = Context.getProgramWorkflowService().getPatientPrograms(patient,pregnancyProgram, null, null, null, null, false);
        Assertions.assertEquals(1, patientPregnancyPrograms.size());
        Assertions.assertEquals(lostToFollowupConcept, patientPregnancyPrograms.get(0).getOutcome());
        Assertions.assertTrue(Minutes.minutesBetween(new DateTime(now), new DateTime(patientPregnancyPrograms.get(0).getDateCompleted())).isLessThan(Minutes.minutes(1)));
    }

    @Test
    public void shouldNotClosePregnancyProgramWithStateLostToFollowUpIfInAntenatalStateForLessThanElevenMonths() {
        Program pregnancyProgram = Context.getProgramWorkflowService().getProgramByUuid(PihEmrConfigConstants.PROGRAM_PREGNANCY_UUID);
        Date now = new DateTime().withMillisOfSecond(0).toDate();  // date enrolled loses millisecond for some reason, so make sure "now" doesn't have millisecond component
        Date tenMonthsAgo  = new DateTime(now).minusMonths(10).toDate();
        Concept lostToFollowupConcept = Context.getConceptService().getConceptByMapping("5240", "PIH");
        Assertions.assertNotNull(lostToFollowupConcept);  // sanity check

        Patient patient = Context.getPatientService().getPatient(7); // patient from standard test dataset

        // enroll the patient in the program with an antenatal state
        PatientProgram existingPatientPregnancyProgram = new PatientProgram();
        existingPatientPregnancyProgram.setProgram(pregnancyProgram);
        existingPatientPregnancyProgram.setPatient(patient);
        existingPatientPregnancyProgram.setDateEnrolled(tenMonthsAgo);
        existingPatientPregnancyProgram.transitionToState(Context.getProgramWorkflowService().getStateByUuid(PihEmrConfigConstants.PROGRAMWORKFLOW_PREGNANCYPROGRAMTYPEOFTREATMENT_STATE_ANTENATAL_UUID), tenMonthsAgo);
        Context.getProgramWorkflowService().savePatientProgram(existingPatientPregnancyProgram);

        // sanity check, patient program exists
        List<PatientProgram> patientPregnancyPrograms = Context.getProgramWorkflowService().getPatientPrograms(patient,pregnancyProgram, null, null, null, null, false);
        Assertions.assertEquals(1, patientPregnancyPrograms.size());
        task.run();

        // should still be enrolled
        patientPregnancyPrograms = Context.getProgramWorkflowService().getPatientPrograms(patient,pregnancyProgram, null, null, null, null, false);
        Assertions.assertEquals(1, patientPregnancyPrograms.size());
        Assertions.assertNull(patientPregnancyPrograms.get(0).getOutcome());
        Assertions.assertNull(patientPregnancyPrograms.get(0).getDateCompleted());
    }

    // TODO: test some cases with both antenatal and postpartum states

}
