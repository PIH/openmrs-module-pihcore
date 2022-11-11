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

package org.openmrs.module.pihcore.page.controller.account;

import org.openmrs.User;
import org.openmrs.api.UserService;
import org.openmrs.module.appui.UiSessionContext;
import org.openmrs.module.authentication.UserLogin;
import org.openmrs.module.authentication.web.AuthenticationSession;
import org.openmrs.module.pihcore.config.Config;
import org.openmrs.ui.framework.UiUtils;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.openmrs.ui.framework.page.PageModel;
import org.openmrs.ui.framework.page.PageRequest;

public class LoginTotpPageController {

	public String get(
			PageModel pageModel,
			@SpringBean("userService") UserService userService,
			@SpringBean Config config,
			UiSessionContext context,
			UiUtils ui,
			PageRequest request) {

		if (context.isAuthenticated()) {
			return "redirect:" + ui.pageLink("pihcore", "home");
		}

		pageModel.put("welcomeMessage", config.getWelcomeMessage());

		AuthenticationSession session = new AuthenticationSession(request.getRequest(), request.getResponse());
		UserLogin authenticationContext = session.getUserLogin();
		User candidateUser = authenticationContext.getUser();

		if (candidateUser == null) {
			if (session.getErrorMessage() == null) {
				session.setErrorMessage("authentication.error.candidateUserRequired");
			}
			return "redirect:" + ui.pageLink("pihcore", "login");
		}

		pageModel.put("authenticationSession", session);
		return null;
	}
}
