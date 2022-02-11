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
package org.openmrs.module.pihcore;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.openmrs.User;
import org.openmrs.api.context.Context;
import org.openmrs.ui.framework.WebConstants;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static org.openmrs.module.pihcore.PihCoreConstants.SESSION_ATTRIBUTE_TERMS_ACCEPTED;
import static org.openmrs.module.pihcore.PihCoreConstants.TERMS_AND_CONDITIONS_ENABLED_PROPERTY;

/**
 * Basic servlet filter to log page requests, utilization, and performance
 */
public class TermsAndConditionsFilter implements Filter {

	private static Logger log = Logger.getLogger(TermsAndConditionsFilter.class);

	public static final String TERMS_URI = "/" + WebConstants.CONTEXT_PATH + "/pihcore/account/termsAndConditions.page";

	public static final List<String> EXCLUSION_EXTENSIONS = Arrays.asList(
		"js", "css", "gif", "jpg", "jpeg", "png", ".ttf", ".woff", ".action"
	);

	private boolean enabled = false;

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		try {
			enabled = "true".equalsIgnoreCase(PihCoreUtil.getSystemOrRuntimeProperty(TERMS_AND_CONDITIONS_ENABLED_PROPERTY, "false"));
		}
		catch (Exception e) {
			log.warn("Unable to initialize TermsAndConditionsFilter.  It will be disabled.", e);
		}
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		if (enabled && request instanceof HttpServletRequest && response instanceof HttpServletResponse) {
			HttpServletRequest httpRequest = (HttpServletRequest) request;
			try {
				// If we have not already indicated on the session that the Terms were accepted, run the check
				if (!"true".equals(httpRequest.getSession().getAttribute(SESSION_ATTRIBUTE_TERMS_ACCEPTED))) {
					User currentUser = Context.getAuthenticatedUser();
					// Only check once a user has logged in successfully, so we can check and update their account
					if (currentUser != null) {
						String propertyVal = currentUser.getUserProperty(PihCoreConstants.USER_PROPERTY_TERMS_AND_CONDITIONS_ACCEPTED_DATE);
						// If the user had previously accepted the terms, update the session with this information
						if (StringUtils.isNotBlank(propertyVal)) {
							httpRequest.getSession().setAttribute(SESSION_ATTRIBUTE_TERMS_ACCEPTED, "true");
						}
						else {
							// Only redirect for page requests, not for included resources
							if (!isExcluded(httpRequest.getRequestURI())) {
								log.warn("Redirecting " + currentUser + " from " + httpRequest.getRequestURI() + " to terms and conditions page");
								HttpServletResponse httpResponse = (HttpServletResponse) response;
								httpResponse.sendRedirect(TERMS_URI);
								return;
							}
						}
					}
				}
			}
			catch (Exception e) {
				log.trace("An error occurred in the terms and conditions filter", e);
			}
		}
		chain.doFilter(request, response);
	}

	public boolean isExcluded(String uri) {
		if (uri.equals(TERMS_URI)) {
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
	}
}
