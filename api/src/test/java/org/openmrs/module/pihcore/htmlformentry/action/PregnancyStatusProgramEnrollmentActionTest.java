package org.openmrs.module.pihcore.htmlformentry.action;

import org.joda.time.DateTime;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openmrs.Concept;
import org.openmrs.Encounter;
import org.openmrs.Location;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.PatientProgram;
import org.openmrs.Program;
import org.openmrs.api.context.Context;
import org.openmrs.module.htmlformentry.FormEntrySession;
import org.openmrs.module.pihcore.PihCoreContextSensitiveTest;

import java.util.Comparator;
import java.util.Date;
import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.lenient;

public class PregnancyStatusProgramEnrollmentActionTest extends PihCoreContextSensitiveTest {

    private static final String PREGNANCY_PROGRAM_UUID = "6a5713c2-3fd5-46e7-8f25-36a0f7871e12";
    private static final String ANTENATAL_STATE_UUID = "a83896bf-9094-4a3c-b843-e75509a52b32";
    private static final String POSTPARTUM_STATE_UUID = "a735b5f6-0b63-4d9a-ae2e-70d08c947aed";

    private FormEntrySession mockSession;
    private PregnancyStatusProgramEnrollmentAction action;
    private Program pregnancyProgram;
    private Patient patient;
    private Location location;
    private Concept pregnancyStatus;
    private Concept deliveredLast6Weeks;
    private Concept yes;
    private Concept no;
    // date enrolled loses the milliseconds, so make sure "now" doesn't have any
    private final Date now = new DateTime().withMillisOfSecond(0).toDate();

    @BeforeEach
    public void setUp() {
        // mock pregnancy program, locations and the PIH source
        executeDataSet("pregnancyProgramTestDataset.xml");
        // CIEL source, pregnancy status, delivered within the last 6 weeks, yes, no
        executeDataSet("pregnancyStatusTestDataset.xml");

        mockSession = mock(FormEntrySession.class);
        action = new PregnancyStatusProgramEnrollmentAction();
        pregnancyProgram = Context.getProgramWorkflowService().getProgramByUuid(PREGNANCY_PROGRAM_UUID);
        patient = Context.getPatientService().getPatient(7); // patient from standard test dataset
        location = Context.getLocationService().getLocation("Child Location");
        pregnancyStatus = Context.getConceptService().getConcept(1101);
        deliveredLast6Weeks = Context.getConceptService().getConcept(1102);
        yes = Context.getConceptService().getConcept(1103);
        no = Context.getConceptService().getConcept(1104);

        // sanity check, patient is not enrolled
        Assertions.assertEquals(0, getEnrollments().size());
    }

    @Test
    public void shouldEnrollInAntenatalStateWhenPregnant() {
        applyAction(encounter(now, yes, null));

        List<PatientProgram> enrollments = getEnrollments();
        Assertions.assertEquals(1, enrollments.size());
        PatientProgram enrollment = enrollments.get(0);
        Assertions.assertEquals(now, enrollment.getDateEnrolled());
        Assertions.assertNull(enrollment.getDateCompleted());
        Assertions.assertEquals("Parent Location", enrollment.getLocation().getName()); // the visit location
        assertOnlyState(enrollment, ANTENATAL_STATE_UUID, now);
    }

    @Test
    public void shouldEnrollInDeliveredStateWhenNotPregnantAndDeliveredInTheLast6Weeks() {
        applyAction(encounter(now, no, yes));

        List<PatientProgram> enrollments = getEnrollments();
        Assertions.assertEquals(1, enrollments.size());
        Assertions.assertEquals(now, enrollments.get(0).getDateEnrolled());
        Assertions.assertNull(enrollments.get(0).getDateCompleted());
        assertOnlyState(enrollments.get(0), POSTPARTUM_STATE_UUID, now);
    }

    @Test
    public void shouldNotEnrollWhenNotPregnantAndNotDeliveredInTheLast6Weeks() {
        applyAction(encounter(now, no, no));
        Assertions.assertEquals(0, getEnrollments().size());
    }

    @Test
    public void shouldNotEnrollWhenNotPregnantAndDeliveredNotAnswered() {
        applyAction(encounter(now, no, null));
        Assertions.assertEquals(0, getEnrollments().size());
    }

    @Test
    public void shouldNotEnrollWhenThePregnancyQuestionIsNotAnswered() {
        applyAction(encounter(now, null, null));
        applyAction(encounter(now, null, yes));
        Assertions.assertEquals(0, getEnrollments().size());
    }

    @Test
    public void shouldNotChangeAnActiveAntenatalEnrollment() {
        Date enrolled = new DateTime(now).minusMonths(2).toDate();
        enroll(enrolled, ANTENATAL_STATE_UUID);

        applyAction(encounter(now, yes, null));
        applyAction(encounter(now, no, yes));

        List<PatientProgram> enrollments = getEnrollments();
        Assertions.assertEquals(1, enrollments.size());
        Assertions.assertNull(enrollments.get(0).getDateCompleted());
        assertOnlyState(enrollments.get(0), ANTENATAL_STATE_UUID, enrolled);
    }

    @Test
    public void shouldNotChangeAnActiveDeliveredEnrollmentWhenPregnant() {
        Date enrolled = new DateTime(now).minusWeeks(3).toDate();
        enroll(enrolled, POSTPARTUM_STATE_UUID);

        applyAction(encounter(now, yes, null));

        List<PatientProgram> enrollments = getEnrollments();
        Assertions.assertEquals(1, enrollments.size());
        Assertions.assertNull(enrollments.get(0).getDateCompleted());
        assertOnlyState(enrollments.get(0), POSTPARTUM_STATE_UUID, enrolled);
    }

    @Test
    public void shouldEnrollWhenThePreviousEnrollmentEndedBeforeTheEncounter() {
        PatientProgram previous = enroll(new DateTime(now).minusYears(1).toDate(), POSTPARTUM_STATE_UUID);
        previous.setDateCompleted(new DateTime(now).minusMonths(6).toDate());
        Context.getProgramWorkflowService().savePatientProgram(previous);

        applyAction(encounter(now, yes, null));

        List<PatientProgram> enrollments = getEnrollments();
        Assertions.assertEquals(2, enrollments.size());
        Assertions.assertEquals(now, enrollments.get(1).getDateEnrolled());
        Assertions.assertNull(enrollments.get(1).getDateCompleted());
        assertOnlyState(enrollments.get(1), ANTENATAL_STATE_UUID, now);
    }

    @Test
    public void shouldEndAPastEnrollmentWhenTheNextEnrollmentStarts() {
        Date nextEnrollment = new DateTime(now).minusMonths(1).toDate();
        enroll(nextEnrollment, ANTENATAL_STATE_UUID);
        Date pastEncounter = new DateTime(now).minusMonths(3).toDate();

        applyAction(encounter(pastEncounter, no, yes));

        List<PatientProgram> enrollments = getEnrollments();
        Assertions.assertEquals(2, enrollments.size());
        Assertions.assertEquals(pastEncounter, enrollments.get(0).getDateEnrolled());
        Assertions.assertEquals(nextEnrollment, enrollments.get(0).getDateCompleted());
        Assertions.assertEquals(POSTPARTUM_STATE_UUID, enrollments.get(0).getStates().iterator().next().getState().getUuid());
    }

    private Encounter encounter(Date date, Concept pregnant, Concept delivered) {
        Encounter encounter = new Encounter();
        encounter.setPatient(patient);
        encounter.setEncounterDatetime(date);
        encounter.setLocation(location);
        if (pregnant != null) {
            encounter.addObs(codedObs(pregnancyStatus, pregnant, date));
        }
        if (delivered != null) {
            encounter.addObs(codedObs(deliveredLast6Weeks, delivered, date));
        }
        return encounter;
    }

    private Obs codedObs(Concept question, Concept answer, Date date) {
        Obs obs = new Obs();
        obs.setPerson(patient);
        obs.setConcept(question);
        obs.setValueCoded(answer);
        obs.setObsDatetime(date);
        return obs;
    }

    // lenient: the action does not read the patient when it has nothing to enroll
    private void applyAction(Encounter encounter) {
        lenient().when(mockSession.getPatient()).thenReturn(patient);
        lenient().when(mockSession.getEncounter()).thenReturn(encounter);
        action.applyAction(mockSession);
    }

    private PatientProgram enroll(Date date, String stateUuid) {
        PatientProgram enrollment = new PatientProgram();
        enrollment.setProgram(pregnancyProgram);
        enrollment.setPatient(patient);
        enrollment.setDateEnrolled(date);
        enrollment.setLocation(location);
        enrollment.transitionToState(Context.getProgramWorkflowService().getStateByUuid(stateUuid), date);
        return Context.getProgramWorkflowService().savePatientProgram(enrollment);
    }

    private List<PatientProgram> getEnrollments() {
        List<PatientProgram> enrollments = Context.getProgramWorkflowService().getPatientPrograms(patient, pregnancyProgram, null, null, null, null, false);
        enrollments.sort(Comparator.comparing(PatientProgram::getDateEnrolled));
        return enrollments;
    }

    private void assertOnlyState(PatientProgram enrollment, String stateUuid, Date startDate) {
        Assertions.assertEquals(1, enrollment.getStates().size());
        Assertions.assertEquals(stateUuid, enrollment.getStates().iterator().next().getState().getUuid());
        Assertions.assertEquals(startDate, enrollment.getStates().iterator().next().getStartDate());
    }
}
