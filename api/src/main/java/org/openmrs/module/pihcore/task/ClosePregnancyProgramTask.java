package org.openmrs.module.pihcore.task;

import org.joda.time.DateTime;
import org.openmrs.Concept;
import org.openmrs.PatientProgram;
import org.openmrs.PatientState;
import org.openmrs.Program;
import org.openmrs.ProgramWorkflow;
import org.openmrs.ProgramWorkflowState;
import org.openmrs.api.context.Context;
import org.openmrs.module.pihcore.PihEmrConfigConstants;
import org.openmrs.module.pihcore.config.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Date;
import java.util.List;

/**
 * Patients should be unenrolled automatically from the pregnancy program if they have been in the Antenatal Treatment State for 11 months. The program outcome should be Lost to Follow Up.
 * Patients should be unenrolled automatically from the pregnancy program 6 weeks after the Delivered treatment state start date with an outcome of Treatment Completed
 * For any programs with a completion date within the last year, if the final state is Miscarried and there is no outcome, the outcome should be set to Treatment Completed
 */
public class ClosePregnancyProgramTask implements Runnable {

    private final Logger log = LoggerFactory.getLogger(getClass());
    private static boolean isExecuting = false;

    private Config config;

    @Override
    public void run() {
        if (isExecuting) {
            log.info(getClass() + " is still executing, not running again");
            return;
        }
        isExecuting = true;
        try {

            if (config == null) {
                config = Context.getRegisteredComponent("config", Config.class);
            }

            // logic is for Sierra Leone only
            if (config != null && config.isSierraLeone()) {
                Program pregnancyProgram = Context.getProgramWorkflowService().getProgramByUuid(PihEmrConfigConstants.PROGRAM_PREGNANCY_UUID);
                ProgramWorkflow treatmentStatusWorkflow = Context.getProgramWorkflowService().getWorkflowByUuid(PihEmrConfigConstants.PROGRAMWORKFLOW_PREGNANCYPROGRAMTYPEOFTREATMENT_UUID);
                ProgramWorkflowState antenatalState = Context.getProgramWorkflowService().getStateByUuid(PihEmrConfigConstants.PROGRAMWORKFLOW_PREGNANCYPROGRAMTYPEOFTREATMENT_STATE_ANTENATAL_UUID);
                ProgramWorkflowState postpartumState = Context.getProgramWorkflowService().getStateByUuid(PihEmrConfigConstants.PROGRAMWORKFLOW_PREGNANCYPROGRAMTYPEOFTREATMENT_STATE_POSTPARTUM_UUID);
                ProgramWorkflowState miscarriedState = Context.getProgramWorkflowService().getStateByUuid(PihEmrConfigConstants.PROGRAMWORKFLOW_PREGNANCYPROGRAMTYPEOFTREATMENT_STATE_MISCARRIED_UUID);
                Concept treatmentCompleteConcept = Context.getConceptService().getConceptByMapping("1714", "PIH");
                Concept lostToFollowupConcept = Context.getConceptService().getConceptByMapping("5240", "PIH");
                Date sixWeeksAgo = new DateTime().minusWeeks(6).toDate();
                Date elevenMonthsAgo = new DateTime().minusMonths(11).toDate();

                // there didn't seem to be a way to all programs without an outcome, so we hack it by setting the min completion date to tomorrow
                List<PatientProgram> patientPregnancyPrograms = Context.getProgramWorkflowService().getPatientPrograms(null, pregnancyProgram, null, null, new DateTime().plusDays(1).toDate(), null, false);
                for (PatientProgram patientPregnancyProgram : patientPregnancyPrograms) {
                    if (patientPregnancyProgram.getDateCompleted() == null) {
                        PatientState currentState = patientPregnancyProgram.getCurrentState(treatmentStatusWorkflow);
                        if (currentState != null) {
                            if (currentState.getState().equals(postpartumState) && currentState.getStartDate().before(sixWeeksAgo)) {
                                patientPregnancyProgram.setDateCompleted(new Date());
                                patientPregnancyProgram.setOutcome(treatmentCompleteConcept);
                                Context.getProgramWorkflowService().savePatientProgram(patientPregnancyProgram);
                            } else if (currentState.getState().equals(antenatalState) && currentState.getStartDate().before(elevenMonthsAgo)) {
                                patientPregnancyProgram.setDateCompleted(new Date());
                                patientPregnancyProgram.setOutcome(lostToFollowupConcept);
                                Context.getProgramWorkflowService().savePatientProgram(patientPregnancyProgram);
                            }
                        }
                    }
                }

                // now fetch all pregnancy programs that *have* been completed within the last year, and if their final state is miscarried, set the outcome to treatment complete
                // note: when back entering information from more than a year ago, this will not work as expected, as for performance reasons it will only look at the last year
                patientPregnancyPrograms = Context.getProgramWorkflowService().getPatientPrograms(null, pregnancyProgram, null, null, new DateTime().minusYears(1).toDate(), new Date(), true);
                for (PatientProgram patientPregnancyProgram : patientPregnancyPrograms) {
                    if (patientPregnancyProgram.getDateCompleted() != null && patientPregnancyProgram.getOutcome() == null) {
                        for (PatientState patientState : patientPregnancyProgram.getMostRecentStateInEachWorkflow()) {
                            if (patientState.getState().equals(miscarriedState)) {
                                patientPregnancyProgram.setOutcome(treatmentCompleteConcept);
                                Context.getProgramWorkflowService().savePatientProgram(patientPregnancyProgram);
                            }
                        }
                    }
                }
            }
        }
        finally {
            isExecuting = false;
        }
    }

    // to allow mocking in tests
    public void setConfig(Config config) {
        this.config = config;
    }
}
