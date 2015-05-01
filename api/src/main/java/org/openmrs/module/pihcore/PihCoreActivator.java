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


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.context.Context;
import org.openmrs.module.BaseModuleActivator;
import org.openmrs.module.metadatadeploy.api.MetadataDeployService;
import org.openmrs.module.pihcore.deploy.bundle.VersionedPihMetadataBundle;

import java.util.List;

public class PihCoreActivator extends BaseModuleActivator {

	public static final String RUNTIME_PROPERTY_INSTALL_AT_STARTUP = "pihcore.installMetadataAtStartup";

	protected Log log = LogFactory.getLog(getClass());

    // TODO: do we *want* to install the bundles defined in this module here?

	/**
	 * Typically a different module will install the metadata deploy packages at startup. (e.g. mirebalaismetadata)
	 * However for the specific case of concepts.pih-emr.org, we don't have a specific distro module, so we want this
	 * module to set up concepts at startup.
	 *
	 * To enable this, set a runtime property of:
	 *     pihcore.installMetadataAtStartup = true
	 */
	@Override
	public void started() {
		String property = Context.getRuntimeProperties().getProperty(RUNTIME_PROPERTY_INSTALL_AT_STARTUP, "false");
		if (Boolean.valueOf(property)) {
			// Limit to VersionedPihMetadataBundle, in case there are other bundles in non-PIH modules
			List<VersionedPihMetadataBundle> bundles = Context.getRegisteredComponents(VersionedPihMetadataBundle.class);

			log.info("Deploying " + bundles.size() + " bundles of class VersionedPihMetadataBundle...");
			MetadataDeployService deployService = Context.getService(MetadataDeployService.class);
			deployService.installBundles((List) bundles);
			log.info("Successfully deployed bundles:");
			for (VersionedPihMetadataBundle bundle : bundles) {
				String template = bundle.isInstalledNewVersion() ?
						"Installed version %d of %s" :
						"Was already at version %d of %s";
				log.info(String.format(template, bundle.getVersion(), bundle.getClass().getSimpleName()));
			}
		}
		else {
			log.info("Not installing metadata deploy bundles. This will be done by a distribution module that depends on this one.");
		}
	}
}
