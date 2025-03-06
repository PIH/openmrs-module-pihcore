package org.openmrs.module.pihcore.htmlformentry.action;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Concept;
import org.openmrs.Encounter;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.PatientProgram;
import org.openmrs.PatientState;
import org.openmrs.Program;
import org.openmrs.ProgramWorkflowState;
import org.openmrs.api.context.Context;
import org.openmrs.module.htmlformentry.CustomFormSubmissionAction;
import org.openmrs.module.htmlformentry.FormEntrySession;
import org.openmrs.module.pihcore.SierraLeoneConfigConstants;

import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static org.openmrs.module.pihcore.htmlformentry.action.util.PregnancyProgramActionUtils.enrollInPregnancyProgram;
import static org.openmrs.module.pihcore.htmlformentry.action.util.PregnancyProgramActionUtils.getTypeOfTreatmentCurrentState;
import static org.openmrs.module.pihcore.htmlformentry.action.util.PregnancyProgramActionUtils.getTypeOfTreatmentStateOnDate;

/**
 * When saving form, transitions the patient program into the postpartum state (or enrolls the patient in the program in that state, if necessary)
 * Intended to be added to the following forms:
 * - Labor/Delivery Summary
 * - NCD Delivery
 * - Postpartum Daily Progress
 * - Maternal Discharge
 *
 * The date of the transition is:
 * - the delivery date as recorded on the form, if present
 * - if multiple delivery dates (e.g. twins), use the latest delivery date
 * - otherwise, if no delivery date present, use the encounter date
 *
 *  NOTE: this only works for the *Sierra Leone* implementation of the Pregnancy Program, and relies on constants from SierraLeoneConfigConstants (the generic Pregnancy Program has slightly different states)
 */
public class PregnancyProgramPostpartumTransitionAction implements CustomFormSubmissionAction {

    protected final Log log = LogFactory.getLog(getClass());

    private static final String DELIVERY_DATE_CONCEPT_MAPPING = "5599";

    @Override
    public void applyAction(FormEntrySession formEntrySession) {
        Program pregnancyProgram = Context.getProgramWorkflowService().getProgramByUuid(SierraLeoneConfigConstants.PROGRAM_PREGNANCY_UUID);
        ProgramWorkflowState postpartumState = Context.getProgramWorkflowService().getStateByUuid(SierraLeoneConfigConstants.PROGRAMWORKFLOW_PREGNANCYPROGRAMTYPEOFTREATMENT_STATE_POSTPARTUM_UUID);

        Patient patient = formEntrySession.getPatient();
        Encounter encounter = formEntrySession.getEncounter();
        Date deliveryDate = getDeliveryDate(encounter);
        // get all patient programs completed on or after delivery date (or not yet completed) and sort by date enrolled
        List<PatientProgram> patientPregnancyPrograms = Context.getProgramWorkflowService().getPatientPrograms(patient, pregnancyProgram, null, null, deliveryDate, null, false);
        patientPregnancyPrograms.sort(Comparator.comparing(PatientProgram::getDateEnrolled));
        // see if any are active on delivery date
        List<PatientProgram> activePatientPregnancyPrograms = patientPregnancyPrograms.stream().filter(patientProgram -> patientProgram.getActive(deliveryDate)).collect(Collectors.toList());

        if (activePatientPregnancyPrograms.isEmpty()) {
            // enroll if no active program, in the postpartum state
            enrollInPregnancyProgram(patient, postpartumState, deliveryDate, encounter.getLocation(), patientPregnancyPrograms);
        } else {
            if (activePatientPregnancyPrograms.size() > 1) {
                log.warn("Patient " + patient.getUuid() + " is enrolled in multiple active pregnancy programs, likely a data error. Operating on the most recent one.");
            }
            // if the patient state on the delivery is the most recent, and antenatal, transition to postpartum
            PatientProgram activePregnancyProgram = activePatientPregnancyPrograms.get(activePatientPregnancyPrograms.size() - 1);
            PatientState patientStateOnDeliverDate = getTypeOfTreatmentStateOnDate(activePregnancyProgram.getStates(), deliveryDate).orElse(null);
            PatientState patientStateCurrent = getTypeOfTreatmentCurrentState(activePregnancyProgram.getStates()).orElse(null);
            if (patientStateOnDeliverDate != null && patientStateOnDeliverDate.equals(patientStateCurrent) &&
                    SierraLeoneConfigConstants.PROGRAMWORKFLOW_PREGNANCYPROGRAMTYPEOFTREATMENT_STATE_ANTENATAL_UUID
                    .equals(patientStateOnDeliverDate.getState().getUuid())) {

                activePregnancyProgram.transitionToState(postpartumState, deliveryDate);
                Context.getProgramWorkflowService().savePatientProgram(activePregnancyProgram);
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

