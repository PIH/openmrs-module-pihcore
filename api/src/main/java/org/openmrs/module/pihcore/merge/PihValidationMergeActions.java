package org.openmrs.module.pihcore.merge;

import org.openmrs.Patient;
import org.openmrs.PatientProgram;
import org.openmrs.api.ProgramWorkflowService;
import org.openmrs.messagesource.MessageSourceService;
import org.openmrs.module.emrapi.merge.PatientMergeAction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

/**
 * Custom actions to perform additional validation before merging patients
 */
@Component("pihValidationMergeActions")
public class PihValidationMergeActions implements PatientMergeAction {

    private final ProgramWorkflowService programWorkflowService;

    private final MessageSourceService messageSourceService;

    @Autowired
    public PihValidationMergeActions(ProgramWorkflowService programWorkflowService, MessageSourceService messageSourceService) {
        this.programWorkflowService = programWorkflowService;
        this.messageSourceService = messageSourceService;
    }

    @Override
    public void beforeMergingPatients(Patient preferred, Patient notPreferred) {
        if (patientsHaveOverlappingProgramEnrollments(programWorkflowService, preferred, notPreferred)) {
            String message = messageSourceService.getMessage("pihcore.mergePatients.error.overlappingPrograms");
            throw new IllegalStateException(message);
        }
    }

    @Override
    public void afterMergingPatients(Patient preferred, Patient notPreferred) {
        // No operation
    }

    /**
     * @return true if patient1 and patient1 have any enrollments for the same program whose dates overlap
     */
    public boolean patientsHaveOverlappingProgramEnrollments(ProgramWorkflowService pws, Patient patient1, Patient patient2) {
        List<PatientProgram> patient1Programs = pws.getPatientPrograms(patient1, null, null, null, null, null, false);
        List<PatientProgram> patient2Programs = pws.getPatientPrograms(patient2, null, null, null, null, null, false);
        for (PatientProgram patient1Program : patient1Programs) {
            for (PatientProgram patient2Program : patient2Programs) {
                if (patient1Program.getProgram().equals(patient2Program.getProgram())) {
                    Date startDate1 = patient1Program.getDateEnrolled();
                    Date endDate1 = patient1Program.getDateCompleted();
                    Date startDate2 = patient2Program.getDateEnrolled();
                    Date endDate2 = patient2Program.getDateCompleted();
                    boolean overlappingPrograms = false;
                    // If neither has ended, then they are overlapping
                    if (endDate1 == null && endDate2 == null) {
                        overlappingPrograms = true;
                    }
                    // If the first has not ended, and started before the second one ended, they are overlapping
                    else if (endDate1 == null) {
                        overlappingPrograms = endDate2.after(startDate1);
                    }
                    // If the second has not ended, and started before the first one ended, they are overlapping
                    else if (endDate2 == null) {
                        overlappingPrograms = endDate1.after(startDate2);
                    }
                    // If both have ended, if neither ends before the start of the other
                    else {
                        boolean firstOverlapsSecond = startDate1.before(endDate2) && endDate1.after(startDate2);
                        boolean secondOverlapsFirst = startDate2.before(endDate1) && endDate2.after(startDate1);
                        overlappingPrograms = firstOverlapsSecond || secondOverlapsFirst ;
                    }
                    if (overlappingPrograms) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
