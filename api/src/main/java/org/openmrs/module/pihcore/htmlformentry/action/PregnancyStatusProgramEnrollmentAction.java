package org.openmrs.module.pihcore.htmlformentry.action;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Concept;
import org.openmrs.Encounter;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.PatientProgram;
import org.openmrs.Program;
import org.openmrs.ProgramWorkflowState;
import org.openmrs.api.ConceptService;
import org.openmrs.api.context.Context;
import org.openmrs.module.htmlformentry.CustomFormSubmissionAction;
import org.openmrs.module.htmlformentry.FormEntrySession;
import org.openmrs.module.pihcore.SierraLeoneConfigConstants;

import java.util.Comparator;
import java.util.Date;
import java.util.List;

import static org.openmrs.module.pihcore.htmlformentry.action.util.PregnancyProgramActionUtils.enrollInPregnancyProgram;

/**
 * Enrolls the patient in the Pregnancy Program based on the pregnancy status questions of the form, but only if the patient
 * is not already enrolled in the Pregnancy Program on the encounter date (an existing enrollment is never changed):
 * - "Is patient pregnant?" (CIEL:5272) = Yes: enroll in the Antenatal state
 * - "Is patient pregnant?" = No and "Delivered within the last 6 weeks" (PIH:21869) = Yes: enroll in the Delivered (postpartum) state
 * - otherwise (including neither question answered): do nothing
 * The enrollment starts on the encounter date, at the visit location of the encounter location.
 * Intended to be added to the following forms:
 * - MCOE Triage
 * - Vitals with glucose (the questions are only shown at MCH OPD)
 * NOTE: this only works for the *Sierra Leone* implementation of the Pregnancy Program, and relies on constants from SierraLeoneConfigConstants
 */
public class PregnancyStatusProgramEnrollmentAction implements CustomFormSubmissionAction {

    protected final Log log = LogFactory.getLog(getClass());

    private static final String PREGNANCY_STATUS_CONCEPT_CIEL = "5272";
    private static final String DELIVERED_LAST_6_WEEKS_CONCEPT_PIH = "21869";
    private static final String YES_CONCEPT_CIEL = "1065";

    @Override
    public void applyAction(FormEntrySession formEntrySession) {
        ConceptService conceptService = Context.getConceptService();
        Concept pregnancyStatus = conceptService.getConceptByMapping(PREGNANCY_STATUS_CONCEPT_CIEL, "CIEL");
        Concept deliveredLast6Weeks = conceptService.getConceptByMapping(DELIVERED_LAST_6_WEEKS_CONCEPT_PIH, "PIH");
        Concept yes = conceptService.getConceptByMapping(YES_CONCEPT_CIEL, "CIEL");
        if (pregnancyStatus == null || deliveredLast6Weeks == null || yes == null) {
            log.warn("Pregnancy status concepts (CIEL:5272, PIH:21869, CIEL:1065) not found, not enrolling in the Pregnancy Program");
            return;
        }

        Encounter encounter = formEntrySession.getEncounter();
        Boolean pregnant = getYesNoAnswer(encounter, pregnancyStatus, yes);
        if (pregnant == null) {
            return;
        }

        String stateUuid;
        if (pregnant) {
            stateUuid = SierraLeoneConfigConstants.PROGRAMWORKFLOW_PREGNANCYPROGRAMTYPEOFTREATMENT_STATE_ANTENATAL_UUID;
        } else if (Boolean.TRUE.equals(getYesNoAnswer(encounter, deliveredLast6Weeks, yes))) {
            stateUuid = SierraLeoneConfigConstants.PROGRAMWORKFLOW_PREGNANCYPROGRAMTYPEOFTREATMENT_STATE_POSTPARTUM_UUID;
        } else {
            return;
        }

        Program pregnancyProgram = Context.getProgramWorkflowService().getProgramByUuid(SierraLeoneConfigConstants.PROGRAM_PREGNANCY_UUID);
        ProgramWorkflowState state = Context.getProgramWorkflowService().getStateByUuid(stateUuid);
        Patient patient = formEntrySession.getPatient();
        Date encounterDate = encounter.getEncounterDatetime();

        // all enrollments completed on or after the encounter date (or not yet completed), sorted by date enrolled
        List<PatientProgram> patientPregnancyPrograms = Context.getProgramWorkflowService().getPatientPrograms(patient, pregnancyProgram, null, null, encounterDate, null, false);
        patientPregnancyPrograms.sort(Comparator.comparing(PatientProgram::getDateEnrolled));

        boolean activelyEnrolled = patientPregnancyPrograms.stream().anyMatch(patientProgram -> patientProgram.getActive(encounterDate));
        if (!activelyEnrolled) {
            enrollInPregnancyProgram(patient, state, encounterDate, encounter.getLocation(), patientPregnancyPrograms);
        }
    }

    /**
     * @return true if the question was answered Yes, false if answered with anything else, null if not answered
     */
    private Boolean getYesNoAnswer(Encounter encounter, Concept question, Concept yes) {
        for (Obs obs : encounter.getAllFlattenedObs(false)) {
            if (obs.getConcept().equals(question) && obs.getValueCoded() != null) {
                return obs.getValueCoded().equals(yes);
            }
        }
        return null;
    }
}
