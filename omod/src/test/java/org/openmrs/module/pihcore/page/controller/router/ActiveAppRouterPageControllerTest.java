package org.openmrs.module.pihcore.page.controller.router;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openmrs.module.appframework.domain.AppDescriptor;
import org.openmrs.module.appframework.service.AppFrameworkService;
import org.openmrs.module.pihcore.PihCoreConstants;
import org.openmrs.ui.framework.page.Redirect;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ActiveAppRouterPageControllerTest {

    private AppFrameworkService appFrameworkService;

    private HttpServletRequest request;

    private HttpSession session;

    @BeforeEach
    public void setup() {
        appFrameworkService = mock(AppFrameworkService.class);
        request = mock(HttpServletRequest.class);
        session = mock(HttpSession.class);
        when(request.getSession()).thenReturn(session);
    }

    @Test
    public void shouldRedirectToStartOfApp() {
        AppDescriptor app = new AppDescriptor();
        app.setId("mirebalais.liveCheckin");
        app.setUrl("coreapps/findpatient/findPatient.page?app=mirebalais.liveCheckin");
        when(appFrameworkService.getApp("mirebalais.liveCheckin")).thenReturn(app);
        when(session.getAttribute(PihCoreConstants.CURRENT_APP_SESSION_VARIABLE)).thenReturn("mirebalais.liveCheckin");
        Redirect redirect = new ActiveAppRouterPageController().controller(request, appFrameworkService);
        assertThat(redirect.getUrl(), is("coreapps/findpatient/findPatient.page?app=mirebalais.liveCheckin"));
    }

}
