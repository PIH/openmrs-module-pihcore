package org.openmrs.module.pihcore.page.controller.router;

import org.junit.Before;
import org.junit.Test;
import org.openmrs.module.appframework.domain.AppDescriptor;
import org.openmrs.module.pihcore.PihCoreConstants;
import org.openmrs.ui.framework.page.Redirect;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class AppEntryRouterPageControllerTest {

    private HttpServletRequest request;

    private HttpSession session;

    @Before
    public void setup() {
        request = mock(HttpServletRequest.class);
        session = mock(HttpSession.class);
        when(request.getSession()).thenReturn(session);
    }

    @Test
    public void shouldRedirectToFindPatientPageAndSetAppSessionVariable() {
        AppDescriptor app = new AppDescriptor();
        app.setId("mirebalais.liveCheckin");
        app.setUrl("coreapps/findpatient/findPatient.page?app=mirebalais.liveCheckin");
        Redirect redirect = new AppEntryRouterPageController().controller(request, null, app);
        verify(session).setAttribute(PihCoreConstants.CURRENT_APP_SESSION_VARIABLE, "mirebalais.liveCheckin");
        assertThat(redirect.getUrl(), is("coreapps/findpatient/findPatient.page?app=mirebalais.liveCheckin"));
    }

}
