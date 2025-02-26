package org.openmrs.module.pihcore.page.controller.router;

import org.openmrs.Patient;
import org.openmrs.PatientProgram;
import org.openmrs.api.ProgramWorkflowService;
import org.openmrs.api.context.Context;
import org.openmrs.module.appui.UiSessionContext;
import org.openmrs.module.pihcore.PihEmrConfigConstants;
import org.openmrs.module.pihcore.config.Config;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.openmrs.ui.framework.page.Redirect;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;

/*
 * The purpose of this Controller is to route the user to a specific clinician-facing patient dashboard
 * based on the current program enrollment status of the patient.
 *
 * Note that this controller is wired in via the config as the "dashboardUrl", and is currently only wired in on the Haiti and Sierra Leone servers
 */
public class ProgramDashboardPageController {

    public Redirect controller(@RequestParam(value = "patientId") Patient patient,
                               @RequestParam(value = "currentDashboard", required = false) String currentDashboard,
                               @SpringBean Config config,
                               UiSessionContext sessionContext) {

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

        // logic for Sierra Leone
        if (config.isSierraLeone()) {
            // if the patient is enrolled in the Pregnancy Program, and the session location is tagged as an Pregnancy Program Dashboard location, redirect to the Pregnancy Dashboard
            if (isPatientActivelyEnrolledInProgram(activeEnrollments, PihEmrConfigConstants.PROGRAM_PREGNANCY_UUID) &&
                    sessionContext.getSessionLocation().hasTag("Pregnancy Program Dashboard Location") &&
                    !PihEmrConfigConstants.PROGRAM_PREGNANCY_UUID.equals(currentDashboard)) {  // make sure clicking on the clinical dashboard link while on the Pregnancy dashboard redirects to the basic dashboard
                queryString += "&dashboard=" + PihEmrConfigConstants.PROGRAM_PREGNANCY_UUID;
            }

            // if the patient is enrolled in the Infant Program, and the session location is tagged as an Infant Program Dashboard location, redirect to the Infant Dashboard
            else if (isPatientActivelyEnrolledInProgram(activeEnrollments, PihEmrConfigConstants.PROGRAM_INFANT_UUID) &&
                    sessionContext.getSessionLocation().hasTag("Infant Program Dashboard Location") &&
                    !PihEmrConfigConstants.PROGRAM_INFANT_UUID.equals(currentDashboard)) {  // make sure clicking on the clinical dashboard link while on the Infant dashboard redirects to the basic dashboard
                queryString += "&dashboard=" + PihEmrConfigConstants.PROGRAM_INFANT_UUID;
            }
        }

        // logic for Haiti
        if (config.isHaiti()) {
            // if the patient is enrolled in the MCH Program, and the session location is tagged as an MCH Program Dashboard location, redirect to the MCH Dashboard
            if (isPatientActivelyEnrolledInProgram(activeEnrollments, PihEmrConfigConstants.PROGRAM_MCH_UUID) &&
                    sessionContext.getSessionLocation().hasTag("MCH Program Dashboard Location") &&
                    !PihEmrConfigConstants.PROGRAM_MCH_UUID.equals(currentDashboard)) {  // make sure clicking on the clinical dashboard link while on the Infant dashboard redirects to the basic dashboard
                queryString += "&dashboard=" + PihEmrConfigConstants.PROGRAM_MCH_UUID;
            }

            // if there is only one active enrollment, and it is for the HIV program, redirect to the HIV dashboard
            else if (activeEnrollments.size() == 1 && activeEnrollments.get(0).getProgram().getUuid().equals(PihEmrConfigConstants.PROGRAM_HIV_UUID) &&
                    !PihEmrConfigConstants.PROGRAM_HIV_UUID.equals(currentDashboard)) {  // make sure clicking on the clinical dashboard link while on the HIV dashboard redirects to the basic dashboard
                queryString += "&dashboard=" + PihEmrConfigConstants.PROGRAM_HIV_UUID;
            }
        }

        return new Redirect(providerName, pageName, queryString);
    }

    private Boolean isPatientActivelyEnrolledInProgram(List<PatientProgram> activePatientProgramEnrollments, String programUuid) {
        for (PatientProgram pp : activePatientProgramEnrollments) {
            if (pp.getProgram().getUuid().equals(programUuid)) {
                return true;
            }
        }
        return false;
    }

}
