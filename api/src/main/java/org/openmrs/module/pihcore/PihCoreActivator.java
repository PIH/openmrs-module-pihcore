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
import org.openmrs.api.LocationService;
import org.openmrs.api.context.Context;
import org.openmrs.module.BaseModuleActivator;
import org.openmrs.module.Module;
import org.openmrs.module.ModuleFactory;
import org.openmrs.module.idgen.service.IdentifierSourceService;
import org.openmrs.module.metadatadeploy.api.MetadataDeployService;
import org.openmrs.module.pihcore.config.Config;
import org.openmrs.module.pihcore.config.ConfigDescriptor;
import org.openmrs.module.pihcore.deploy.bundle.haiti.HaitiMetadataBundle;
import org.openmrs.module.pihcore.deploy.bundle.haiti.mirebalais.MirebalaisBundle;
import org.openmrs.module.pihcore.deploy.bundle.liberia.LiberiaBundle;
import org.openmrs.module.pihcore.setup.HtmlFormSetup;
import org.openmrs.module.pihcore.setup.LocationTagSetup;
import org.openmrs.module.pihcore.setup.PatientIdentifierSetup;

public class PihCoreActivator extends BaseModuleActivator {

	protected Log log = LogFactory.getLog(getClass());

    private Config config;

	@Override
	public void started() {

        try {
            LocationService locationService = Context.getLocationService();
            IdentifierSourceService identifierSourceService = Context.getService(IdentifierSourceService.class);

            if (config == null) {  // hack to allow injecting a mock config for testing
                config = Context.getRegisteredComponents(Config.class).get(0); // currently only one of these
            }
            installMetadataBundles(config);
            LocationTagSetup.setupLocationTags(locationService, config);
            HtmlFormSetup.setupHtmlFormEntryTagHandlers();
            HtmlFormSetup.setupHtmlForms(config);

            PatientIdentifierSetup.setupIdentifierGeneratorsIfNecessary(identifierSourceService, locationService, config);
        }
        catch (Exception e) {
            Module mod = ModuleFactory.getModuleById("pihcore");
            ModuleFactory.stopModule(mod);
            throw new RuntimeException("failed to setup the required modules", e);
        }

    }

    private void installMetadataBundles(Config config) {

        MetadataDeployService deployService = Context.getService(MetadataDeployService.class);

        // make this more dynamic, less dependent on if-thens

        if (config.getSite().equals(ConfigDescriptor.Site.MIREBALAIS)) {
            deployService.installBundle(Context.getRegisteredComponents(MirebalaisBundle.class).get(0));
        }
        else if (config.getSite().equals(ConfigDescriptor.Site.LACOLLINE)) {
            // TODO: will need a "Lacolline" location bundle with just Lacolline?
            deployService.installBundle(Context.getRegisteredComponents(HaitiMetadataBundle.class).get(0));
        }
        else if (config.getCountry().equals(ConfigDescriptor.Country.LIBERIA)) {
            deployService.installBundle(Context.getRegisteredComponents(LiberiaBundle.class).get(0));
        }

    }

    // hack to allow injecting a mock config for testing
    public void setConfig(Config config) {
        this.config = config;
    }
}

/* Config config = Context.getRegisteredComponents(Config.class).get(0); // currently only one of these
        installMetadataBundles(config);

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
		}*/