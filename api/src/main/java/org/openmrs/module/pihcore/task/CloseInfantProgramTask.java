package org.openmrs.module.pihcore.task;

import org.joda.time.DateTime;
import org.openmrs.Concept;
import org.openmrs.PatientProgram;
import org.openmrs.Program;
import org.openmrs.api.context.Context;
import org.openmrs.module.pihcore.PihEmrConfigConstants;
import org.openmrs.module.pihcore.config.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.List;

/**
 * Patients should be unenrolled automatically from the infant program after they are 6 weeks old.
 * The program outcome should be Patient Aged Out.
 */
public class CloseInfantProgramTask implements  Runnable {

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
                Program infantProgram = Context.getProgramWorkflowService().getProgramByUuid(PihEmrConfigConstants.PROGRAM_INFANT_UUID);
                Concept patientAgedOut = Context.getConceptService().getConceptByMapping("20492", "PIH");
                Date sixWeeksAgo = new DateTime().minusWeeks(6).toDate();

                // there didn't seem to be a way to all programs without an outcome, so we hack it by setting the min completion date to tomorrow
                List<PatientProgram> patientInfantPrograms = Context.getProgramWorkflowService().getPatientPrograms(null, infantProgram, null, null, new DateTime().plusDays(1).toDate(), null, false);
                for (PatientProgram patientInfantProgram : patientInfantPrograms) {
                    if (patientInfantProgram.getDateCompleted() == null && patientInfantProgram.getPatient().getBirthdate() != null && patientInfantProgram.getPatient().getBirthdate().before(sixWeeksAgo)) {
                        patientInfantProgram.setDateCompleted(new Date());
                        patientInfantProgram.setOutcome(patientAgedOut);
                        Context.getProgramWorkflowService().savePatientProgram(patientInfantProgram);
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
