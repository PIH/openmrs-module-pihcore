/**
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
package org.openmrs.module.pihcore.page.controller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.helpers.ISO8601DateFormat;
import org.openmrs.User;
import org.openmrs.api.UserService;
import org.openmrs.api.context.Context;
import org.openmrs.module.pihcore.PihCoreConstants;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.openmrs.ui.framework.page.PageModel;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Date;

public class TermsAndConditionsPageController {

    protected final Log log = LogFactory.getLog(getClass());

    public void get(PageModel model) {
    }

    public String post(@RequestParam(value = "termsAndConditionsAccepted", defaultValue = "false") boolean termsAndConditionsAccepted,
                       @SpringBean("userService") UserService userService) {

        if (termsAndConditionsAccepted) {
            User currentUser = userService.getUser(Context.getAuthenticatedUser().getUserId());
            String acceptanceDate = new ISO8601DateFormat().format(new Date());
            currentUser.setUserProperty(PihCoreConstants.USER_PROPERTY_TERMS_AND_CONDITIONS_ACCEPTED_DATE, acceptanceDate);
            userService.saveUser(currentUser);
            Context.refreshAuthenticatedUser();
        }
        return "redirect:/pihcore/home.page";
    }
}
