/*
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */

package org.openmrs.module.pihcore.fragment.controller.account;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.User;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.UserService;
import org.openmrs.api.context.Context;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.openmrs.util.OpenmrsConstants;
import org.openmrs.util.PrivilegeConstants;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;

public class ResetPasswordFragmentController {

    protected final Log log = LogFactory.getLog(getClass());

    public void reset(@RequestParam(value = "username") String username,
                      @SpringBean("userService") UserService userService,
                      @SpringBean("adminService") AdministrationService administrationService,
                      HttpServletRequest request) {
        try {
            Context.addProxyPrivilege(PrivilegeConstants.GET_USERS);
            User user = userService.getUserByUsernameOrEmail(username);

            ensureHostUrl(administrationService, request);

            userService.setUserActivationKey(user);
        }
        catch (Exception e) {
            log.warn("Unable to send user password reset email to " + username, e);
        }
        finally {
            Context.removeProxyPrivilege(PrivilegeConstants.GET_USERS);
        }
    }

    /**
     * I don't know why this is implemented this way in OpenMRS core, but this is needed to get things to work
     */
    private synchronized void ensureHostUrl(AdministrationService administrationService, HttpServletRequest request) {
        try {
            Context.addProxyPrivilege(PrivilegeConstants.MANAGE_GLOBAL_PROPERTIES);
            String requestUrl = request.getRequestURL().toString();
            String hostUrl = requestUrl.replace("resetPassword/reset.action", "resetPassword.page");
            hostUrl += "?activationKey={activationKey}";
            administrationService.setGlobalProperty(OpenmrsConstants.GP_HOST_URL, hostUrl);
        }
        finally {
            Context.removeProxyPrivilege(PrivilegeConstants.MANAGE_GLOBAL_PROPERTIES);
        }
    }
}
