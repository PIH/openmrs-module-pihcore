/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 * <p>
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 * <p>
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
package org.openmrs.module.pihcore;

import org.apache.commons.lang.StringUtils;
import org.openmrs.Location;
import org.openmrs.api.context.Context;
import org.openmrs.api.context.ContextAuthenticationException;
import org.openmrs.module.authentication.AuthenticationUtil;
import org.openmrs.module.authentication.web.AuthenticationSession;
import org.openmrs.module.authentication.web.BasicWebAuthenticationScheme;
import org.openmrs.module.pihcore.metadata.Metadata;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Properties;

import static org.openmrs.module.appui.UiSessionContext.LOCATION_SESSION_ATTRIBUTE;
import static org.openmrs.module.emr.EmrConstants.COOKIE_NAME_LAST_SESSION_LOCATION;

/**
 * This expands on the BasicWebAuthenticationScheme to also handle collecting, validating, and setting sessionLocation
 */
public class PihBasicAuthenticationScheme extends BasicWebAuthenticationScheme {

	public static final String SESSION_LOCATION = "sessionLocation";
	public static final String LOCATION_REQUIRED = "locationRequired";

	private boolean locationRequired = true;

	@Override
	public void configure(String schemeId, Properties config) {
		super.configure(schemeId, config);
		locationRequired = AuthenticationUtil.getBoolean(config.getProperty(LOCATION_REQUIRED), true);
	}

	@Override
	public void beforeAuthentication(AuthenticationSession session) {
		super.beforeAuthentication(session);
		Location loginLocation = getLoginLocation(session.getHttpRequest());
		if (loginLocation == null && locationRequired) {
			// TODO: For now, do not require session location for authentication via header (REST/FHIR/etc)
			if (session.getRequestHeader(AUTHORIZATION_HEADER) == null) {
				throw new ContextAuthenticationException("mirebalais.login.error.locationRequired");
			}
		}
	}

	@Override
	public void afterAuthenticationSuccess(AuthenticationSession session) {
		super.afterAuthenticationSuccess(session);
		Location loginLocation = getLoginLocation(session.getHttpRequest());
		if (loginLocation != null) {
			Context.getUserContext().setLocation(loginLocation);
			session.setHttpSessionAttribute(LOCATION_SESSION_ATTRIBUTE, loginLocation.getLocationId());
			session.setCookieValue(COOKIE_NAME_LAST_SESSION_LOCATION, loginLocation.getLocationId().toString());
		}
	}

	/**
	 * @return the Login Location for the given request.  If not present in the request, but only one login location
	 * is present in the system, then return this Location
	 */
	protected Location getLoginLocation(HttpServletRequest request) {
		Location loginLocation = null;
		String locationIdStr = request.getParameter(SESSION_LOCATION);
		if (StringUtils.isNotBlank(locationIdStr)) {
			loginLocation = Metadata.getLoginLocation(locationIdStr);
			if (loginLocation == null) {
				throw new IllegalArgumentException("mirebalais.login.error.invalidLocation");
			}
		}
		if (loginLocation == null) {
			List<Location> loginLocations = Metadata.getLoginLocations();
			if (loginLocations.size() == 1) {
				loginLocation = loginLocations.get(0);
			}
		}
		return loginLocation;
	}
}
