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

import org.apache.commons.lang.StringUtils;
import org.openmrs.Location;
import org.openmrs.api.LocationService;
import org.openmrs.api.context.Context;
import org.openmrs.module.appui.UiSessionContext;
import org.openmrs.module.emrapi.EmrApiConstants;
import org.openmrs.module.emrapi.EmrApiProperties;
import org.openmrs.module.pihcore.config.Config;
import org.openmrs.ui.framework.UiUtils;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.openmrs.ui.framework.page.PageModel;
import org.openmrs.ui.framework.page.PageRequest;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;
import java.util.List;

import static org.openmrs.module.uicommons.UiCommonsConstants.SESSION_ATTRIBUTE_ERROR_MESSAGE;
import static org.openmrs.util.PrivilegeConstants.GET_LOCATIONS;

/**
 * TODO: grant GET_LOCATIONS privilege to Anonymous instead of using a proxy privilege
 */
public class LoginLocationPageController {

    public static final String COOKIE_NAME_LAST_SESSION_LOCATION = "emr.lastSessionLocation";

	public String get(
			PageModel pageModel,
			@SpringBean EmrApiProperties emrApiProperties,
			@CookieValue(value = COOKIE_NAME_LAST_SESSION_LOCATION, required = false) String lastSessionLocationId,
			@SpringBean("locationService") LocationService locationService,
			@SpringBean Config config) {

		pageModel.put("welcomeMessage", config.getWelcomeMessage());

		// Since the user isn't authenticated, we need to use proxy privileges to get locations via the API
		try {
			Context.addProxyPrivilege(GET_LOCATIONS);

			List<Location> locations = locationService.getLocationsByTag(emrApiProperties.getSupportsLoginLocationTag());
			if (locations.size() == 0) {
				locations = locationService.getAllLocations(false);
			}
			pageModel.addAttribute("locations", locations);

			Location lastSessionLocation = null;
			if (StringUtils.isNotBlank(lastSessionLocationId)) {
				try {
					lastSessionLocation = locationService.getLocation(Integer.valueOf(lastSessionLocationId));
					// double-check that the last session location is still a valid login location (and null if not)
					if (!locations.contains(lastSessionLocation)) {
						lastSessionLocation = null;
					}
				}
				catch (Exception ex) {
				}
			}
            pageModel.addAttribute("lastSessionLocation", lastSessionLocation);
		}
		finally {
			Context.removeProxyPrivilege(GET_LOCATIONS);
		}
        return null;
	}

	public String post(
	        @RequestParam(value = "sessionLocation", required = false) Integer sessionLocationId,
			@SpringBean("locationService") LocationService locationService,
			UiUtils ui,
	        UiSessionContext context,
			PageRequest request) {

		HttpSession httpSession = request.getRequest().getSession();

		// Session Location
		Location sessionLocation = null;
		try {
			Context.addProxyPrivilege(GET_LOCATIONS);

			if (sessionLocationId != null) {
				sessionLocation = locationService.getLocation(sessionLocationId);
				if (!sessionLocation.hasTag(EmrApiConstants.LOCATION_TAG_SUPPORTS_LOGIN)) {
					// the UI shouldn't allow this, but protect against it just in case
					httpSession.setAttribute(SESSION_ATTRIBUTE_ERROR_MESSAGE, ui.message("mirebalais.login.error.invalidLocation"));
					return "redirect:" + ui.pageLink("pihcore", "loginLocation");
				}
			}
			if (sessionLocation == null) {
				httpSession.setAttribute(SESSION_ATTRIBUTE_ERROR_MESSAGE, ui.message("mirebalais.login.error.locationRequired"));
				return "redirect:" + ui.pageLink("pihcore", "loginLocation");
			}

			// Set a cookie, so next time someone logs in on this machine, we can default to that same location
			request.setCookieValue(COOKIE_NAME_LAST_SESSION_LOCATION, sessionLocationId.toString());

			// Set the location on the context
			context.setSessionLocation(sessionLocation);
		}
		finally {
			Context.removeProxyPrivilege(GET_LOCATIONS);
		}

		return "redirect:" + ui.pageLink("pihcore", "home");
	}
}
