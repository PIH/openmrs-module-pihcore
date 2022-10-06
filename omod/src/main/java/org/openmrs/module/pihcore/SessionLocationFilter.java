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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Location;
import org.openmrs.api.context.Context;
import org.openmrs.module.pihcore.metadata.Metadata;
import org.openmrs.ui.framework.WebConstants;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * This filter ensures that a user is always logged in with a specific session location
 */
public class SessionLocationFilter implements Filter {

	private final Log log = LogFactory.getLog(getClass());

	public static final String LOGIN_LOCATION_URI = "/" + WebConstants.CONTEXT_PATH + "/pihcore/loginLocation.page";

	public static final List<String> EXCLUSION_EXTENSIONS = Arrays.asList(
			"js", "css", "gif", "jpg", "jpeg", "png", ".ttf", ".woff", ".action"
	);

	private final static String SESSION_LOCATION_KEY = "emrContext.sessionLocationId";

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		log.warn("SessionLocationFilter initializing");
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		if (request instanceof HttpServletRequest && response instanceof HttpServletResponse) {
			HttpServletRequest httpRequest = (HttpServletRequest) request;
			HttpServletResponse httpResponse = (HttpServletResponse) response;
			HttpSession session = httpRequest.getSession();
			if (Context.isSessionOpen() && Context.isAuthenticated()) {
				if (session.getAttribute(SESSION_LOCATION_KEY) == null) {
					if (!isExcluded(httpRequest.getRequestURI())) {
						List<Location> loginLocations = Metadata.getLoginLocations();
						if (loginLocations.size() == 1) {
							Location location = loginLocations.get(0);
							log.info("SessionLocationFilter defaulting to " + location.getName());
							session.setAttribute(SESSION_LOCATION_KEY, location.getLocationId());
							Context.getUserContext().setLocation(location);
						}
						else {
							log.info("SessionLocationFilter redirecting to login location page");
							httpResponse.sendRedirect(LOGIN_LOCATION_URI);
							return;
						}
					}
				}
			}
		}
		chain.doFilter(request, response);
	}

	public boolean isExcluded(String uri) {
		if (uri.equals(LOGIN_LOCATION_URI)) {
			return true;
		}
		for (String extension : EXCLUSION_EXTENSIONS) {
			if (uri.endsWith(extension)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public void destroy() {
		log.warn("SessionLocationFilter destroying");
	}
}
