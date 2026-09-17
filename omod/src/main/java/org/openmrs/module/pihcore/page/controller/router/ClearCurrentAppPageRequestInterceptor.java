package org.openmrs.module.pihcore.page.controller.router;

import org.openmrs.module.pihcore.PihCoreConstants;
import org.openmrs.ui.framework.interceptor.PageRequestInterceptor;
import org.openmrs.ui.framework.page.PageContext;
import org.openmrs.ui.framework.page.PageRequest;
import org.springframework.stereotype.Component;

/**
 * Clears {@link PihCoreConstants#CURRENT_APP_SESSION_VARIABLE} whenever the user lands back on
 * the EMR home page, so a stale "active app" doesn't linger in the session and incorrectly
 * influence routing decisions (see {@link CurrentAppRouterPageController}) the next time the user
 * does something unrelated to the app they had previously entered.
 */
@Component
public class ClearCurrentAppPageRequestInterceptor implements PageRequestInterceptor {

    @Override
    public void beforeHandleRequest(PageContext pageContext) {
        PageRequest request = pageContext.getRequest();
        if ("pihapps".equals(request.getProviderName()) && "home".equals(request.getPageName())) {
            request.getRequest().getSession().removeAttribute(PihCoreConstants.CURRENT_APP_SESSION_VARIABLE);
        }
    }

}
