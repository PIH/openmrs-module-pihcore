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

package org.openmrs.module.pihcore.page.controller;

import org.openmrs.module.appui.UiSessionContext;
import org.openmrs.module.authentication.AuthenticationConfig;
import org.openmrs.module.authentication.AuthenticationCredentials;
import org.openmrs.module.authentication.web.AuthenticationSession;
import org.openmrs.module.pihcore.PihBasicAuthenticationScheme;
import org.openmrs.module.pihcore.config.Config;
import org.openmrs.module.pihcore.metadata.Metadata;
import org.openmrs.ui.framework.UiUtils;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.openmrs.ui.framework.page.PageModel;
import org.openmrs.ui.framework.page.PageRequest;
import org.springframework.web.bind.annotation.CookieValue;

import static org.openmrs.module.authentication.AuthenticationConfig.SCHEME_ID;
import static org.openmrs.module.authentication.AuthenticationConfig.SCHEME_TYPE_TEMPLATE;
import static org.openmrs.module.emr.EmrConstants.COOKIE_NAME_LAST_SESSION_LOCATION;

public class LoginPageController {

	public String get(
			PageModel pageModel,
			@SpringBean Config config,
			@CookieValue(value = COOKIE_NAME_LAST_SESSION_LOCATION, required = false) String lastSessionLocationId,
			UiSessionContext context,
			UiUtils ui,
			PageRequest request) {

		if (context.isAuthenticated()) {
			return "redirect:" + ui.pageLink("pihcore", "home");
		}

        pageModel.put("welcomeMessage", config.getWelcomeMessage());
		pageModel.put("authenticationSession", new AuthenticationSession(request.getRequest(), request.getResponse()));
		pageModel.addAttribute("locations", Metadata.getLoginLocations());
		pageModel.addAttribute("lastSessionLocation", Metadata.getLoginLocation(lastSessionLocationId));

		return null;
	}

	/**
	 * NOTE: This post handler is never hit if the authentication module is configured.
	 * For backwards-compatibility, we mimic the behavior of the authentication module here, if it were to be
	 * configured without any 2FA enabled.
	 */
	public String post(
			UiUtils ui,
			PageRequest request) {

		AuthenticationSession session = new AuthenticationSession(request.getRequest(), request.getResponse());
		try {
			String schemeId = getClass().getName();
			AuthenticationConfig.setProperty(AuthenticationConfig.SCHEME, schemeId);
			AuthenticationConfig.setProperty(SCHEME_TYPE_TEMPLATE.replace(SCHEME_ID, schemeId), PihBasicAuthenticationScheme.class.getName());
			PihBasicAuthenticationScheme scheme = (PihBasicAuthenticationScheme) AuthenticationConfig.getAuthenticationScheme(schemeId);
			AuthenticationCredentials credentials = scheme.getCredentials(session);
			session.authenticate(scheme, credentials);
			session.regenerateHttpSession();  // Guard against session fixation attacks
			session.refreshDefaultLocale(); // Refresh context locale after authentication
			return "redirect:" + ui.pageLink("pihcore", "home");
		}
		catch (Exception e) {
			if (session.getErrorMessage() == null) {
				session.setErrorMessage("mirebalais.login.error.authentication");
			}
			return "redirect:" + ui.pageLink("pihcore", "login");
		}
		finally {
			session.removeAuthenticationContext();
		}
	}
}
