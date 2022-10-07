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

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.ThreadContext;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Enumeration;

/**
 * Basic servlet filter to log page requests, utilization, and performance
 */
public class RequestMonitoringFilter implements Filter {

	private static final Logger logger = LogManager.getLogger(RequestMonitoringFilter.class);

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		long startTime = System.currentTimeMillis();
		chain.doFilter(request, response);
		long endTime = System.currentTimeMillis();
		long totalTime = endTime - startTime;
		if (logger.isTraceEnabled()) {
			if (request instanceof HttpServletRequest) {
				try {
					HttpServletRequest httpRequest = (HttpServletRequest) request;
					HttpSession session = httpRequest.getSession();
					ThreadContext.put("sessionId", session.getId());
					ThreadContext.put("user", (String) session.getAttribute("username"));
					ThreadContext.put("loadTime", Long.toString(totalTime));
					ThreadContext.put("method", httpRequest.getMethod());
					ThreadContext.put("ipAddress", httpRequest.getRemoteAddr());
					ThreadContext.put("servletPath", httpRequest.getServletPath());
					ThreadContext.put("requestURI", httpRequest.getRequestURI());
					ThreadContext.put("queryParams", formatRequestParams(httpRequest));
					logger.trace(httpRequest.getRequestURI() + ": " + totalTime + "ms");
				} catch (Exception e) {
					// Do nothing, we don't want this filter to cause any adverse behavior
				} finally {
					ThreadContext.clearAll();
				}
			}
		}
	}

	private String formatRequestParams(HttpServletRequest request) {
		StringBuilder ret = new StringBuilder();
		if (request.getMethod().equalsIgnoreCase("GET")) {
			for (Enumeration<String> e = request.getParameterNames(); e.hasMoreElements(); ) {
				String paramName = e.nextElement();
				String[] paramValues = request.getParameterValues(paramName);
				if (paramValues != null) {
					for (String paramValue : paramValues) {
						if (StringUtils.isNotBlank(paramValue)) {
							if (ret.length() > 0) {
								ret.append("&");
							}
							if (paramName.toUpperCase().contains("PASSWORD")) {
								paramValue = "********";
							}
							ret.append(paramName).append("=").append(paramValue);
						}
					}
				}
			}
		}
		return ret.toString();
	}

	@Override
	public void destroy() {
	}
}
