package org.openmrs.module.pihcore.htmlformentry.action;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.*;
import org.openmrs.api.context.Context;
import org.openmrs.module.htmlformentry.CustomFormSubmissionAction;
import org.openmrs.module.htmlformentry.FormEntrySession;
import org.openmrs.module.pihcore.SierraLeoneConfigConstants;

import java.util.*;
import java.util.stream.Collectors;

import static org.openmrs.module.pihcore.htmlformentry.action.util.PregnancyProgramActionUtils.endEnrollment;
import static org.openmrs.module.pihcore.htmlformentry.action.util.PregnancyProgramActionUtils.enrollInPregnancyProgram;
import static org.openmrs.module.pihcore.htmlformentry.action.util.PregnancyProgramActionUtils.getTypeOfTreatmentCurrentState;
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
    private static final String DELIVERY_DATE_CONCEPT_MAPPING = "5599";

    @Override
    public void applyAction(FormEntrySession formEntrySession) {
        Program pregnancyProgram = Context.getProgramWorkflowService().getProgramByUuid(SierraLeoneConfigConstants.PROGRAM_PREGNANCY_UUID);
        ProgramWorkflowState antenatalState = Context.getProgramWorkflowService().getStateByUuid(SierraLeoneConfigConstants.PROGRAMWORKFLOW_PREGNANCYPROGRAMTYPEOFTREATMENT_STATE_ANTENATAL_UUID);
        ProgramWorkflowState postpartumState = Context.getProgramWorkflowService().getStateByUuid(SierraLeoneConfigConstants.PROGRAMWORKFLOW_PREGNANCYPROGRAMTYPEOFTREATMENT_STATE_POSTPARTUM_UUID);

        Patient patient = formEntrySession.getPatient();
        Encounter encounter = formEntrySession.getEncounter();
        // get all patient programs completed on or after encounter date (or not yet completed) and sort by date enrolled
        List<PatientProgram> patientPregnancyPrograms = Context.getProgramWorkflowService().getPatientPrograms(patient, pregnancyProgram, null, null, encounter.getEncounterDatetime(), null, false);
        patientPregnancyPrograms.sort(Comparator.comparing(PatientProgram::getDateEnrolled));
        // see if any are active on encounter date
        List<PatientProgram> activePatientPregnancyPrograms = patientPregnancyPrograms.stream().filter(patientProgram -> patientProgram.getActive(encounter.getEncounterDatetime())).collect(Collectors.toList());

        if (activePatientPregnancyPrograms.isEmpty()) {
            if (encounter.getEncounterType() != null && encounter.getEncounterType().getUuid().equals(SierraLeoneConfigConstants.ENCOUNTERTYPE_POSTNATALFOLLOWUP_UUID)){
                enrollInPregnancyProgram(patient, postpartumState, encounter.getEncounterDatetime(), encounter.getLocation(), patientPregnancyPrograms);
            } else {
                // enroll if no active program
                enrollInPregnancyProgram(patient, antenatalState, encounter.getEncounterDatetime(), encounter.getLocation(), patientPregnancyPrograms);
            }
        } else {
            if (activePatientPregnancyPrograms.size() > 1) {
                log.warn("Patient " + patient.getUuid() + " is enrolled in multiple active pregnancy programs, likely a data error. Operating on the most recent one.");
            }

            PatientProgram activePregnancyProgram = activePatientPregnancyPrograms.get(activePatientPregnancyPrograms.size() - 1);
            if (encounter.getEncounterType() != null && encounter.getEncounterType().getUuid().equals(SierraLeoneConfigConstants.ENCOUNTERTYPE_POSTNATALFOLLOWUP_UUID)) {
                PatientState patientStateCurrent = getTypeOfTreatmentCurrentState(activePregnancyProgram.getStates()).orElse(null);
                if (patientStateCurrent.getState().getUuid().equals(SierraLeoneConfigConstants.PROGRAMWORKFLOW_PREGNANCYPROGRAMTYPEOFTREATMENT_STATE_ANTENATAL_UUID)) {
                    //since this is a Postnatal Followup encounter, and the active program state is ANTENATAL we need to transition to POSTPARTUM state
                    Date deliveryDate = getDeliveryDate(encounter); // first look for Delivery date obs
                    activePregnancyProgram.transitionToState(postpartumState, deliveryDate);
                    Context.getProgramWorkflowService().savePatientProgram(activePregnancyProgram);
                }
            } else if (SierraLeoneConfigConstants.PROGRAMWORKFLOW_PREGNANCYPROGRAMTYPEOFTREATMENT_STATE_POSTPARTUM_UUID
                    .equals(getTypeOfTreatmentStateOnDate(activePregnancyProgram.getStates(), encounter.getEncounterDatetime()).map(patientState -> patientState.getState().getUuid()).orElse(null))) {
                // if the patient is enrolled, but in postpartum state, end the current enrollment and enroll in new program
                endEnrollment(activePregnancyProgram, encounter);
                enrollInPregnancyProgram(patient, antenatalState, encounter.getEncounterDatetime(), encounter.getLocation(), patientPregnancyPrograms);
            }
            // otherwise, do nothing
        }
    }

    private Date getDeliveryDate(Encounter encounter) {
        Concept concept = Context.getConceptService().getConceptByMapping(DELIVERY_DATE_CONCEPT_MAPPING, "PIH");
        Date deliveryDate = null;
        // look for any delivery date concepts and get the latest one
        for (Obs obs : encounter.getAllFlattenedObs(false)) {
            if (obs.getConcept().equals(concept)) {
                if (deliveryDate == null || obs.getValueDatetime().after(deliveryDate)) {
                    deliveryDate = obs.getValueDatetime();
                }
            }
        }

        // if no delivery date concept found, use the encounter date
        return deliveryDate != null ? deliveryDate : encounter.getEncounterDatetime();
    }
}
