package org.openmrs.module.pihcore;

import org.apache.commons.lang.StringUtils;
import org.openmrs.Concept;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.PatientProgram;
import org.openmrs.Program;
import org.openmrs.api.ObsService;
import org.openmrs.api.ProgramWorkflowService;
import org.openmrs.api.context.Context;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Set;

public class PihCoreUtils {

    public static List<Obs> getObsWithinProgram(Patient patient, Set<Concept> concepts, String programUuid) {

        List<Obs> obsList = null;
        ProgramWorkflowService programWorkflowService = Context.getProgramWorkflowService();
        ObsService obsService = Context.getObsService();
        Date obsOnOrAfter = null;
        if (StringUtils.isNotBlank(programUuid)) {
            Program program = programWorkflowService.getProgramByUuid(programUuid);
            if (program == null) {
                throw new IllegalStateException("No program with uuid " + programUuid + " is found.");
            }
            for (PatientProgram pp : programWorkflowService.getPatientPrograms(patient, program, null, null, null, null, false)) {
                if (pp.getActive()) {
                    if (obsOnOrAfter == null || obsOnOrAfter.before(pp.getDateEnrolled())) {
                        obsOnOrAfter = pp.getDateEnrolled();
                    }
                }
            }
        }
        obsList = obsService.getObservations(
                Arrays.asList(patient),
                null,
                new ArrayList<>(concepts),
                null,
                null,
                null,
                Arrays.asList("obsDatetime"), //sort by field
                null,
                null,
                obsOnOrAfter, //fromDate
                null,
                false); //includeVoidedObs

        return obsList;
    }
}
