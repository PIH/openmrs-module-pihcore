package org.openmrs.module.pihcore.page.controller.router;

import org.openmrs.Patient;
import org.openmrs.module.appframework.domain.AppDescriptor;
import org.openmrs.module.pihcore.PihCoreConstants;
import org.openmrs.ui.framework.page.Redirect;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;

/**
 * **Note that routing currently does not support multiple browser tabs, because they rely on the session**
 */
public class AppEntryRouterPageController {

    public Redirect controller(HttpServletRequest request,
                               @RequestParam(value = "patientId", required = false) Patient patient,
                               @RequestParam(value = "app") AppDescriptor app) {

        setCurrentApp(request, app);
        return new Redirect(app.getUrl());
    }

    private void setCurrentApp(HttpServletRequest request, AppDescriptor app) {
        request.getSession().setAttribute(PihCoreConstants.CURRENT_APP_SESSION_VARIABLE, app.getId());
    }

}
