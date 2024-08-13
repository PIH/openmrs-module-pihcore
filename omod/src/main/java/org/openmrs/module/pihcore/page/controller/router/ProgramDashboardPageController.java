package org.openmrs.module.pihcore.page.controller.router;

import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.openmrs.Patient;
import org.openmrs.PatientProgram;
import org.openmrs.PatientState;
import org.openmrs.ProgramWorkflow;
import org.openmrs.api.ProgramWorkflowService;
import org.openmrs.api.context.Context;
import org.openmrs.module.pihcore.PihEmrConfigConstants;
import org.openmrs.ui.framework.page.Redirect;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;

/**
 * The purpose of this Controller is to route the user to a specific clinician-facing patient dashboard.
 * There are a few use cases of this:
 * 1. If the patient is only enrolled in 1 program, and this is the HIV program, then route them there by default rather than the default.  This is used by Haiti HIV.
 * 2. If the pregnancy program dashboard is requested, and the user has an active type of treatment workflow state, then direct to the state-based dashboard.  This is used by MCOE.
 */
public class ProgramDashboardPageController {

    public Redirect controller(@RequestParam(value = "patientId") Patient patient,
                               @RequestParam(value = "dashboard", required = false) String dashboard,
                               @RequestParam(value = "noRedirect", required = false) Boolean noRedirect) {

        String providerName = "coreapps";
        String pageName = "clinicianfacing/patient";
        String queryString = "patientId=" + patient.getUuid();

        if (BooleanUtils.isTrue(noRedirect)) {
            return new Redirect(providerName, pageName, queryString);
        }

        List<PatientProgram> activeEnrollments = new ArrayList<>();

        ProgramWorkflowService pws = Context.getProgramWorkflowService();
        for (PatientProgram pp : pws.getPatientPrograms(patient, null, null, null, null, null, false)) {
            if (pp.getActive()) {
                activeEnrollments.add(pp);
            }
        }

        // If the patient has only an active HIV enrollment
        // Then redirect the user to the HIV patient dashboard by default, if they are not already on it
        if (activeEnrollments.size() == 1) {
            String programUuid = activeEnrollments.get(0).getProgram().getUuid();
            if (programUuid.equals(PihEmrConfigConstants.PROGRAM_HIV_UUID)) {
                queryString += "&dashboard=" + programUuid;
                return new Redirect(providerName, pageName, queryString);
            }
        }

        // If the pregnancy program dashboard is requested, and the patient has an active state in the "type of treatment" workflow
        // Then redirect to the dashboard for their current state, rather than for the program, if they are not already on it.
        if (PihEmrConfigConstants.PROGRAM_PREGNANCY_UUID.equalsIgnoreCase(dashboard)) {
            ProgramWorkflow typeOfTx = pws.getWorkflowByUuid(PihEmrConfigConstants.PROGRAMWORKFLOW_PREGNANCYPROGRAMTYPEOFTREATMENT_UUID);
            for (PatientProgram pp : activeEnrollments) {
                if (pp.getProgram().getUuid().equals(PihEmrConfigConstants.PROGRAM_PREGNANCY_UUID)) {
                    PatientState state = pp.getCurrentState(typeOfTx);
                    if (state != null) {
                        queryString += "&dashboard=" + state.getUuid();
                        return new Redirect(providerName, pageName, queryString);
                    }
                }
            }
        }


        queryString += (StringUtils.isNotBlank(dashboard) ? "&dashboard=" + dashboard : "");
        return new Redirect(providerName, pageName, queryString);
    }
}
