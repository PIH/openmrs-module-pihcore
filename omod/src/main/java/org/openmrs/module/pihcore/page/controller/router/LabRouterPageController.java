package org.openmrs.module.pihcore.page.controller.router;

import org.apache.commons.lang.StringUtils;
import org.openmrs.Patient;
import org.openmrs.ui.framework.page.Redirect;
import org.openmrs.util.ConfigUtil;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;

public class LabRouterPageController {

    public Redirect controller(HttpServletRequest request,
                               @RequestParam(value = "patient", required = false) Patient patient,
                               @RequestParam(value = "returnUrl", required = false) String returnUrl,
                               @RequestParam(value = "dashboard", required = false) String dashboard) {

        String redirectUrl;
        boolean usePihApps = Boolean.parseBoolean(ConfigUtil.getProperty("pihcore.usePihAppsLabs"));
        if (patient != null) {
            String patientUuid = patient.getUuid();
            if (usePihApps) {
                redirectUrl = "pihapps/labs/patientLabResults.page?patient=" + patientUuid;
            }
            else {
                if (StringUtils.isBlank(returnUrl)) {
                    returnUrl = "/coreapps/clinicianfacing/patient.page?patientId=" + patientUuid;
                    if (StringUtils.isNotBlank(dashboard)) {
                        returnUrl += "&dashboard=" + dashboard;
                    }
                    returnUrl += "#/LabResults";
                }
                redirectUrl = "owa/labworkflow/index.html?patient=" + patientUuid + "&returnUrl=" + returnUrl;
            }
        }
        else {
            if (usePihApps) {
                redirectUrl = "pihapps/labs/labOrderList.page";
            }
            else {
                redirectUrl = "owa/labworkflow/index.html";
            }
        }
        return new Redirect(redirectUrl);
    }
}
