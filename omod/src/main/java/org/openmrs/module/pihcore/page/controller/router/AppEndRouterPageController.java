package org.openmrs.module.pihcore.page.controller.router;

import org.apache.commons.lang.StringUtils;
import org.openmrs.Patient;
import org.openmrs.module.appframework.service.AppFrameworkService;
import org.openmrs.module.pihcore.PihCoreConstants;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.openmrs.ui.framework.page.Redirect;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;

public class AppEndRouterPageController {

    public Redirect controller(HttpServletRequest request,
                               @SpringBean AppFrameworkService appFrameworkService,
                               @RequestParam(value = "patientId", required = false) Patient patient) {

        String appId = getCurrentApp(request);

        // TODO right now we only handle check-in app, and assume it is cyclical
        if (StringUtils.isNotBlank(appId) && appId.equals("mirebalais.liveCheckin")) {
            // TODO should we include patient here
            return new Redirect(appFrameworkService.getApp(appId).getUrl());
        }

        // TODO HACK--since we are only using this for the check-in use case righ now, by default we redirect back to registration summary page
        return new Redirect("registrationapp", "registrationSummary", "patientId=" + patient.getId() + "&appId=registrationapp.registerPatient");
    }

    private String getCurrentApp(HttpServletRequest request) {
        return (String) request.getSession().getAttribute(PihCoreConstants.CURRENT_APP_SESSION_VARIABLE);
    }


}
