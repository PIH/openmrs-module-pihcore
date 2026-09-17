package org.openmrs.module.pihcore.page.controller.router;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openmrs.module.pihcore.PihCoreConstants;
import org.openmrs.ui.framework.page.PageContext;
import org.openmrs.ui.framework.page.PageRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ClearCurrentAppPageRequestInterceptorTest {

    private HttpServletRequest httpServletRequest;

    private HttpSession session;

    private ClearCurrentAppPageRequestInterceptor interceptor;

    @BeforeEach
    public void setup() {
        httpServletRequest = mock(HttpServletRequest.class);
        session = mock(HttpSession.class);
        when(httpServletRequest.getSession()).thenReturn(session);
        interceptor = new ClearCurrentAppPageRequestInterceptor();
    }

    @Test
    public void shouldClearCurrentAppSessionAttributeWhenOnHomePage() {
        PageRequest pageRequest = new PageRequest("pihapps", "home", httpServletRequest,
                mock(HttpServletResponse.class), null);

        interceptor.beforeHandleRequest(new PageContext(pageRequest));

        verify(session).removeAttribute(PihCoreConstants.CURRENT_APP_SESSION_VARIABLE);
    }

    @Test
    public void shouldNotClearCurrentAppSessionAttributeForOtherPages() {
        PageRequest pageRequest = new PageRequest("pihcore", "checkin/checkin", httpServletRequest,
                mock(HttpServletResponse.class), null);

        interceptor.beforeHandleRequest(new PageContext(pageRequest));

        verify(session, never()).removeAttribute(PihCoreConstants.CURRENT_APP_SESSION_VARIABLE);
    }

}
