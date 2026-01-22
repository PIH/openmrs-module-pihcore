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
package org.openmrs.module.pihcore.fragment.controller.home;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ArrayNode;
import org.openmrs.api.context.Context;
import org.openmrs.module.coreapps.CoreAppsConstants;
import org.openmrs.module.pihcore.PihEmrConfigConstants;
import org.openmrs.module.pihcore.apploader.CustomAppLoaderConstants;
import org.openmrs.module.pihcore.config.Config;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.openmrs.ui.framework.fragment.FragmentModel;

/**
 * Patient Search Fragment for the home page
 */
public class PatientSearchFragmentController {

	private static final Log log = LogFactory.getLog(PatientSearchFragmentController.class);
	
	public void controller(FragmentModel model, @SpringBean("config") Config config) {

		String findPatientColumnConfigJson = null;
		try {
			ArrayNode findPatientColumnConfig = config.getFindPatientColumnConfig();
			if (findPatientColumnConfig != null) {
				findPatientColumnConfigJson = new ObjectMapper().writeValueAsString(findPatientColumnConfig);
			}
		}
		catch (Exception e) {
			log.warn("Error parsing find patient column configuration", e);
		}
		model.addAttribute("findPatientColumnConfig", findPatientColumnConfigJson);

        model.addAttribute("privilegeSearchForPatients", PihEmrConfigConstants.PRIVILEGE_APP_COREAPPS_FIND_PATIENT);

        if (Context.hasPrivilege(CoreAppsConstants.PRIVILEGE_PATIENT_DASHBOARD)) {
            if (StringUtils.isNotBlank(config.getDashboardUrl())) {
                model.addAttribute("dashboardUrl", config.getDashboardUrl());
            } else {
                model.addAttribute("dashboardUrl", "/coreapps/patientdashboard/patientDashboard.page?patientId={{patientId}}");
            }
        }
        // if the user doesn't have permission to view patient dashboard, we redirect to registration dashboard
        else {
            model.addAttribute("dashboardUrl", "/registrationapp/registrationSummary.page?patientId={{patientId}}&appId=" + CustomAppLoaderConstants.Apps.PATIENT_REGISTRATION);
        }
	}
}
