package org.openmrs.module.pihcore.task;

import org.joda.time.DateTime;
import org.openmrs.Concept;
import org.openmrs.PatientProgram;
import org.openmrs.PatientState;
import org.openmrs.Program;
import org.openmrs.ProgramWorkflow;
import org.openmrs.ProgramWorkflowState;
import org.openmrs.api.context.Context;
import org.openmrs.module.pihcore.config.Config;
import org.openmrs.module.pihcore.config.ConfigDescriptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Date;
import java.util.List;

/**
 * Patients should be unenrolled automatically from the pregnancy program if they have been in the Antenatal Treatment State for 11 months. The program outcome should be Lost to Follow Up.
 * Patients should be unenrolled automatically from the pregnancy program 6 weeks after the Delivered treatment state start date with an outcome of Treatment Completed
 */
public class ClosePregnancyProgramTask implements Runnable {

    private final Logger log = LoggerFactory.getLogger(getClass());
    private static boolean isExecuting = false;

    private static final String PREGNANCY_PROGRAM_UUID = "6a5713c2-3fd5-46e7-8f25-36a0f7871e12";

    private static final String PREGNANCY_PROGRAM_TREATMENT_STATUS_WORKFLOW_UUID = "9a3f8252-1588-4f7b-b02c-9e99c437d4ef";

    private static final String ANTENATAL_PROGRAM_STATE_UUID = "a83896bf-9094-4a3c-b843-e75509a52b32";

    private static final String POSTPARTUM_PROGRAM_STATE_UUID = "a735b5f6-0b63-4d9a-ae2e-70d08c947aed";

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
                Program pregnancyProgram = Context.getProgramWorkflowService().getProgramByUuid(PREGNANCY_PROGRAM_UUID);
                ProgramWorkflow treatmentStatusWorkflow = Context.getProgramWorkflowService().getWorkflowByUuid(PREGNANCY_PROGRAM_TREATMENT_STATUS_WORKFLOW_UUID);
                ProgramWorkflowState antenatalState = Context.getProgramWorkflowService().getStateByUuid(ANTENATAL_PROGRAM_STATE_UUID);
                ProgramWorkflowState postpartumState = Context.getProgramWorkflowService().getStateByUuid(POSTPARTUM_PROGRAM_STATE_UUID);
                Concept treatmentCompleteConcept = Context.getConceptService().getConceptByMapping("1714", "PIH");
                Concept lostToFollowup = Context.getConceptService().getConceptByMapping("5240", "PIH");
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
                                patientPregnancyProgram.setOutcome(lostToFollowup);
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
