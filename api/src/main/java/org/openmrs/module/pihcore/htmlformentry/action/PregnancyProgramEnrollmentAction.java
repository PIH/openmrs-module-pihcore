package org.openmrs.module.pihcore.htmlformentry.action;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Encounter;
import org.openmrs.Patient;
import org.openmrs.PatientProgram;
import org.openmrs.Program;
import org.openmrs.ProgramWorkflowState;
import org.openmrs.api.context.Context;
import org.openmrs.module.htmlformentry.CustomFormSubmissionAction;
import org.openmrs.module.htmlformentry.FormEntryContext;
import org.openmrs.module.htmlformentry.FormEntrySession;
import org.openmrs.module.pihcore.SierraLeoneConfigConstants;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static org.openmrs.module.pihcore.htmlformentry.action.util.PregnancyProgramActionUtils.endEnrollment;
import static org.openmrs.module.pihcore.htmlformentry.action.util.PregnancyProgramActionUtils.enrollInPregnancyProgram;
import static org.openmrs.module.pihcore.htmlformentry.action.util.PregnancyProgramActionUtils.getTypeOfTreatmentStateOnDate;

/**
 * Does the following when a form is saved:
 * - If the Patient is not enrolled in the Pregnancy Program at the time of the encounter, enrolls the Patient in the Pregnancy Program with the state of Antenatal
 * - If the Patient is already enrolled in the Pregnancy Program with the state of postpartum, end the current enrollment and enroll the Patient in the Pregnancy Program with the state of Antenatal
 * Intended to be added to the following forms:
 * - ANC Initial
 * - ANC Followup
 * - Labour Progress
 * NOTE: this only works for the *Sierra Leone* implementation of the Pregnancy Program, and relies on constants from SierraLeoneConfigConstants (the generic Pregnancy Program has slightly different states)
 */
public class PregnancyProgramEnrollmentAction implements CustomFormSubmissionAction {

    protected final Log log = LogFactory.getLog(getClass());

    @Override
    public void applyAction(FormEntrySession formEntrySession) {
        Program pregnancyProgram = Context.getProgramWorkflowService().getProgramByUuid(SierraLeoneConfigConstants.PROGRAM_PREGNANCY_UUID);
        ProgramWorkflowState antenatalState = Context.getProgramWorkflowService().getStateByUuid(SierraLeoneConfigConstants.PROGRAMWORKFLOW_PREGNANCYPROGRAMTYPEOFTREATMENT_STATE_ANTENATAL_UUID);

        Patient patient = formEntrySession.getPatient();
        Encounter encounter = formEntrySession.getEncounter();
        // get all patient programs completed on or after encounter date (or not yet completed) and sort by date enrolled
        List<PatientProgram> patientPregnancyPrograms = Context.getProgramWorkflowService().getPatientPrograms(patient, pregnancyProgram, null, null, encounter.getEncounterDatetime(), null, false);
        patientPregnancyPrograms.sort(Comparator.comparing(PatientProgram::getDateEnrolled));
        // see if any are active on encounter date
        List<PatientProgram> activePatientPregnancyPrograms = patientPregnancyPrograms.stream().filter(patientProgram -> patientProgram.getActive(encounter.getEncounterDatetime())).collect(Collectors.toList());

        if (activePatientPregnancyPrograms.isEmpty()) {
            // enroll if no active program
            enrollInPregnancyProgram(patient, antenatalState, encounter, patientPregnancyPrograms);
        } else {
            if (activePatientPregnancyPrograms.size() > 1) {
                log.warn("Patient " + patient.getUuid() + " is enrolled in multiple active pregnancy programs, likely a data error. Operating on the most recent one.");
            }
            // if the patient is enrolled, but in postpartum state, end the current enrollment and enroll in new program
            PatientProgram activePregnancyProgram = activePatientPregnancyPrograms.get(activePatientPregnancyPrograms.size() - 1);
            if (SierraLeoneConfigConstants.PROGRAMWORKFLOW_PREGNANCYPROGRAMTYPEOFTREATMENT_STATE_POSTPARTUM_UUID
                    .equals(getTypeOfTreatmentStateOnDate(activePregnancyProgram.getStates(), encounter.getEncounterDatetime()).map(patientState -> patientState.getState().getUuid()).orElse(null))) {
                endEnrollment(activePregnancyProgram, encounter);
                enrollInPregnancyProgram(patient, antenatalState, encounter, patientPregnancyPrograms);
            }
            // otherwise, do nothing
        }
    }
}
