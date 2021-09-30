package org.openmrs.module.pihcore.page.controller.router;

import org.openmrs.Patient;
import org.openmrs.PatientProgram;
import org.openmrs.api.ProgramWorkflowService;
import org.openmrs.api.context.Context;
import org.openmrs.module.pihcore.PihEmrConfigConstants;
import org.openmrs.ui.framework.page.Redirect;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;

/**
 * The purpose of this Controller is to route the user to a specific clinican-facing patient dashboard
 * based on the current program enrollment status of the patient.
 * If a patient is actively enrolled in only one program, this will route them to this program's dashboard.
 * If a patient is actively enrolled in no programs, or more than one program, this will route them to the default dashboard
 * This is currently limited to HIV program only
 */
public class ProgramDashboardPageController {

    public Redirect controller(@RequestParam(value = "patientId") Patient patient) {

        String providerName = "coreapps";
        String pageName = "clinicianfacing/patient";
        String queryString = "patientId=" + patient.getUuid();

        List<PatientProgram> activeEnrollments = new ArrayList<>();

        ProgramWorkflowService pws = Context.getProgramWorkflowService();
        for (PatientProgram pp : pws.getPatientPrograms(patient, null, null, null, null, null, false)) {
            if (pp.getActive()) {
                activeEnrollments.add(pp);
            }
        }
        if (activeEnrollments.size() == 1) {
            // TODO: Once we have legitimate dashboards for all programs, remove this constraint on HIV program
            String programUuid = activeEnrollments.get(0).getProgram().getUuid();
            if (programUuid.equals(PihEmrConfigConstants.PROGRAM_HIV_UUID)) {
                queryString += "&dashboard=" + programUuid;
            }
        }

        return new Redirect(providerName, pageName, queryString);
    }
}
