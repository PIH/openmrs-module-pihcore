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

package org.openmrs.module.pihcore.deploy.bundle.haiti;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.GlobalProperty;
import org.openmrs.module.coreapps.CoreAppsConstants;
import org.openmrs.module.emrapi.EmrApiConstants;
import org.openmrs.module.metadatadeploy.bundle.AbstractMetadataBundle;
import org.openmrs.module.metadatadeploy.bundle.Requires;
import org.openmrs.module.paperrecord.PaperRecordConstants;
import org.openmrs.module.pihcore.config.Config;
import org.openmrs.module.pihcore.config.ConfigDescriptor;
import org.openmrs.module.pihcore.deploy.bundle.core.MetadataMappingsBundle;
import org.openmrs.module.pihcore.metadata.haiti.HaitiMetadataMappings;
import org.openmrs.module.pihcore.metadata.haiti.HaitiPatientIdentifierTypes;
import org.openmrs.module.pihcore.metadata.haiti.mirebalais.MirebalaisLocations;
import org.openmrs.util.OpenmrsConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.Map;

;

/**
 * Core metadata bundle
 */
@Component
@Requires( { MetadataMappingsBundle.class, HaitiPatientIdentifierTypeBundle.class } )
public class HaitiPropertiesAndMappingsBundle extends AbstractMetadataBundle {

	protected Log log = LogFactory.getLog(getClass());

	public static final String DEFAULT_LOCALE = "fr";

	@Autowired
	private Config config;

	/**
	 * @see AbstractMetadataBundle#install()
	 */
	@Override
	public void install() {

        log.info("Install Metadata Sharing Packages");

        //installMetadataSharingPackage("HUM_Metadata-57.zip", Packages.HUM_METADATA);

		log.info("Setting Global Properties");

		Map<String, String> properties = new LinkedHashMap<String, String>();
		
		// OpenMRS Core
		properties.put(OpenmrsConstants.GLOBAL_PROPERTY_LOCALE_ALLOWED_LIST, "ht, fr, en");
		properties.put(OpenmrsConstants.GLOBAL_PROPERTY_DEFAULT_LOCALE, DEFAULT_LOCALE);

        // Paper Record
        properties.put(PaperRecordConstants.GP_PAPER_RECORD_IDENTIFIER_TYPE, HaitiPatientIdentifierTypes.DOSSIER_NUMBER.uuid());
        properties.put(PaperRecordConstants.GP_EXTERNAL_DOSSIER_IDENTIFIER_TYPE, HaitiPatientIdentifierTypes.EXTERNAL_DOSSIER_NUMBER.uuid());

        // Core Apps
        properties.put(CoreAppsConstants.GP_DEFAULT_PATIENT_IDENTIFIER_LOCATION, MirebalaisLocations.MIREBALAIS_CDI_PARENT.uuid());

        setGlobalProperties(properties);

		log.info("setting metadata mappings");

		// EMR API mappings
		install(HaitiMetadataMappings.PRIMARY_IDENTIFIER_TYPE);
		uninstall(possible(GlobalProperty.class, EmrApiConstants.PRIMARY_IDENTIFIER_TYPE), "replaced by metadata mapping");

		// extra identifier types different between mental health and all other Haiti sites
		if (!config.getSite().equals(ConfigDescriptor.Site.CROSS_SITE)) {
			install(HaitiMetadataMappings.EXTRA_IDENTIFIER_TYPES_SET);
			install(HaitiMetadataMappings.DOSSIER_NUMBER_METADATA_SET_MEMBER);
			install(HaitiMetadataMappings.HIVEMR_V1_METADATA_SET_MEMBER);
			install(HaitiMetadataMappings.EXTRA_IDENTIFIER_TYPES);
		}
		else {
			install(HaitiMetadataMappings.MENTAL_HEALTH_EXTRA_IDENTIFIER_TYPES_SET);
			install(HaitiMetadataMappings.USER_ENTERED_REF_NUMBER_METADATA_SET_MEMBER);
			install(HaitiMetadataMappings.MENTAL_HEALTH_EXTRA_IDENTIFIER_TYPES);
		}
		uninstall(possible(GlobalProperty.class, EmrApiConstants.GP_EXTRA_PATIENT_IDENTIFIER_TYPES), "replaced by metadata mapping");
	}
}