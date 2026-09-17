package org.openmrs.module.pihcore.page.controller.router;

import org.openmrs.module.appframework.service.AppFrameworkService;
import org.openmrs.module.pihcore.PihCoreConstants;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.openmrs.ui.framework.page.Redirect;

import javax.servlet.http.HttpServletRequest;

/**
 * Routes the user to the entry page of whichever app is currently active in their session
 * (i.e., whatever app they most recently entered via {@link AppEntryRouterPageController}).
 * <p>
 * Currently only used by the circular "Check-In" app ({@code mirebalais.liveCheckin}): when
 * archives/paper records are enabled, the check-in htmlform's {@code redirectOnSave} must first
 * send the user through the paper record request page before returning to the check-in app, so
 * that intermediate page is given this page as its own return destination, rather than the
 * check-in app's entry page directly.
 */
public class CurrentAppRouterPageController {

    public Redirect controller(HttpServletRequest request,
                               @SpringBean AppFrameworkService appFrameworkService) {

        String appId = getCurrentApp(request);
        return new Redirect(appFrameworkService.getApp(appId).getUrl());
    }

    private String getCurrentApp(HttpServletRequest request) {
        return (String) request.getSession().getAttribute(PihCoreConstants.CURRENT_APP_SESSION_VARIABLE);
    }

}
