package org.openmrs.module.pihcore.htmlformentry.action;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.joda.time.DateTime;
import org.openmrs.Concept;
import org.openmrs.Encounter;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.PatientState;
import org.openmrs.Program;
import org.openmrs.PatientProgram;
import org.openmrs.ProgramWorkflow;
import org.openmrs.ProgramWorkflowState;
import org.openmrs.api.ProgramWorkflowService;
import org.openmrs.api.context.Context;
import org.openmrs.module.htmlformentry.CustomFormSubmissionAction;
import org.openmrs.module.htmlformentry.FormEntrySession;
import org.openmrs.module.htmlformentry.HtmlFormEntryUtil;

import java.util.Date;
import java.util.List;

public class TransitionToPrenatalGroupAction implements CustomFormSubmissionAction {

    protected final Log log = LogFactory.getLog(getClass());
    private static String MCH_PROGRAM_UUID = "41a2715e-8a14-11e8-9a94-a6cf71072f73";
    private static String TYPE_OF_TREATMENT_WORKFLOW_UUID = "41a277d0-8a14-11e8-9a94-a6cf71072f73";


    @Override
    public void applyAction(FormEntrySession formEntrySession) {

        Concept treatmentType = Context.getConceptService().getConceptByMapping("11698", "PIH");
        Concept prenatalGroup = Context.getConceptService().getConceptByMapping("11699", "PIH");

        Encounter encounter = formEntrySession.getEncounter();
        for (Obs topObs : encounter.getObsAtTopLevel(false)) {
            if (topObs.getConcept().equals(treatmentType)) {
                if (topObs.getValueCoded().equals(prenatalGroup)) {
                    addPatientToPrenatalGroup(formEntrySession, Context.getProgramWorkflowService(), prenatalGroup);
                }
            }
        }
    }

    private void addPatientToPrenatalGroup(
            FormEntrySession formEntrySession,
            ProgramWorkflowService programWorkflowService,
            Concept prenatalGroup) {

        Program mchProgram = programWorkflowService.getProgramByUuid(MCH_PROGRAM_UUID);

        Patient patient = formEntrySession.getPatient();
        Encounter encounter = formEntrySession.getEncounter();

        List<PatientProgram> candidates = programWorkflowService.getPatientPrograms(patient, mchProgram, null,
                encounter.getEncounterDatetime(), encounter.getEncounterDatetime(), null, false);

        if (candidates != null) {
            if (candidates.size() > 1 ) {
                log.warn("More than one MCH program enrollment for patient " + patient.getUuid()
                        + " on date " + encounter.getEncounterDatetime() + ". Change the treatment group to all of them.");
            }
            ProgramWorkflow typeOfTreatmentWorkflow = programWorkflowService.getWorkflowByUuid(TYPE_OF_TREATMENT_WORKFLOW_UUID);
            ProgramWorkflowState prenatalState = typeOfTreatmentWorkflow.getState(prenatalGroup);

            // TODO: check to make sure current state is the *current* state, not just the state on the encounter date
            // In the unlikely event that the patient has more than enrollment in the MCH program on the encounter date, update all of them
            for (PatientProgram patientProgram : candidates) {
                PatientState stateOnEncounterDate = HtmlFormEntryUtil.getPatientStateOnDate(patientProgram, typeOfTreatmentWorkflow, encounter.getEncounterDatetime());
                PatientState currentState = HtmlFormEntryUtil.getPatientStateOnDate(patientProgram, typeOfTreatmentWorkflow, new Date());
                // if the patient is not already in the prenatal group, transition them, assuming the state on the encounter date is the current state
                if (stateOnEncounterDate == null || (!stateOnEncounterDate.getState().equals(prenatalState) && stateOnEncounterDate.equals(currentState))) {
                    patientProgram.transitionToState(prenatalState, encounter.getEncounterDatetime());
                    programWorkflowService.savePatientProgram(patientProgram);
                }
            }
        }
    }
}
