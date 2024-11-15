package org.openmrs.module.pihcore.htmlformentry.action.util;

import org.openmrs.Encounter;
import org.openmrs.Location;
import org.openmrs.Patient;
import org.openmrs.PatientProgram;
import org.openmrs.PatientState;
import org.openmrs.Program;
import org.openmrs.ProgramWorkflow;
import org.openmrs.ProgramWorkflowState;
import org.openmrs.api.context.Context;
import org.openmrs.module.emrapi.adt.AdtService;
import org.openmrs.module.pihcore.SierraLeoneConfigConstants;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class PregnancyProgramActionUtils {

    public static void endEnrollment(PatientProgram patientPregnancyProgram, Encounter encounter) {
        patientPregnancyProgram.setDateCompleted(encounter.getEncounterDatetime());
        Context.getProgramWorkflowService().savePatientProgram(patientPregnancyProgram);
    }

    public static void enrollInPregnancyProgram(Patient patient, ProgramWorkflowState state, Date enrollmentDate, Location enrollmentLocation, List<PatientProgram> existingPatientPregnancyPrograms) {
        Program pregnancyProgram = Context.getProgramWorkflowService().getProgramByUuid(SierraLeoneConfigConstants.PROGRAM_PREGNANCY_UUID);
        // see if there is any subsequent program enrollment
        // TODO right now if there is a subsequent program enrollment, we still create a new enrollment, but just make sure it stops before the next enrollment... we may have further discussion about the correct behavior here
        Optional<PatientProgram> nextPatientProgram = existingPatientPregnancyPrograms.stream().filter((patientProgram) -> patientProgram.getDateEnrolled().after(enrollmentDate)).findFirst();

        PatientProgram newPatientProgram = new PatientProgram();
        newPatientProgram.setProgram(pregnancyProgram);
        newPatientProgram.setPatient(patient);
        newPatientProgram.setDateEnrolled(enrollmentDate);
        newPatientProgram.setDateCompleted(nextPatientProgram.map(PatientProgram::getDateEnrolled).orElse(null)); // see TODO above
        newPatientProgram.setLocation(Context.getService(AdtService.class).getLocationThatSupportsVisits(enrollmentLocation));
        newPatientProgram.transitionToState(state, enrollmentDate);
        Context.getProgramWorkflowService().savePatientProgram(newPatientProgram);
    }

    public static Optional<PatientState> getTypeOfTreatmentStateOnDate(Set<PatientState> states, Date date) {
        ProgramWorkflow pregnancyProgramWorkflow = Context.getProgramWorkflowService().getWorkflowByUuid(SierraLeoneConfigConstants.PROGRAMWORKFLOW_PREGNANCYPROGRAMTYPEOFTREATMENT_UUID);
        return states.stream().filter(patientState -> patientState.getState().getProgramWorkflow().equals(pregnancyProgramWorkflow)).filter(patientState -> patientState.getActive(date)).findFirst();
    }

}
