package org.openmrs.module.pihcore.page.controller.reports;

import org.openmrs.module.pihcore.PihEmrConfigConstants;
import org.openmrs.ui.framework.page.PageModel;

/**
 * Quickly implemented report for Liberia that returns the number of registrations for particular age ranges,
 * and allows for drilling down into these patients to see the details
 */
public class RegistrationsByAgePageController {

    public void get(PageModel model) throws Exception {
        model.addAttribute("privilegePatientDashboard", PihEmrConfigConstants.PRIVILEGE_APP_COREAPPS_PATIENT_DASHBOARD);
    }
}
