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
import org.openmrs.module.haiticore.metadata.HaitiAddressBundle;
import org.openmrs.module.haiticore.metadata.HaitiEncounterTypeBundle;
import org.openmrs.module.haiticore.metadata.HaitiPersonAttributeTypeBundle;
import org.openmrs.module.metadatadeploy.bundle.AbstractMetadataBundle;
import org.openmrs.module.metadatadeploy.bundle.Requires;
import org.openmrs.module.paperrecord.PaperRecordConstants;
import org.openmrs.module.pihcore.deploy.bundle.core.PihCoreMetadataBundle;
import org.openmrs.module.pihcore.metadata.haiti.HaitiPatientIdentifierTypes;
import org.openmrs.module.pihcore.metadata.haiti.mirebalais.MirebalaisLocations;
import org.openmrs.util.OpenmrsConstants;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.Map;

;

/**
 * Core metadata bundle
 */
@Component
@Requires( { PihCoreMetadataBundle.class,
        OrderEntryConcepts.class,
		// TestOrderConcepts.class,  I believe we are now installing these via MDS
        HaitiLocationsBundle.class,
		HaitiEncounterTypeBundle.class,
		HaitiPersonAttributeTypeBundle.class,
        HaitiPatientIdentifierTypeBundle.class,
		HaitiAddressBundle.class
} )
public class HaitiMetadataBundle extends AbstractMetadataBundle {

	protected Log log = LogFactory.getLog(getClass());

 	public static final class Packages {
		public static final String HUM_METADATA = "fa25ad0c-66cc-4715-8464-58570f7b5132";
	}

	public static final String DEFAULT_LOCALE = "fr";
    private static final String REGISTRATION_ENCOUNTER_NAME = "Enregistrement de patient";
    private static final String CHECK_IN_ENCOUNTER_NAME = "Inscription";
    private static final String PRIMARY_CARE_VISIT_ENCOUNTER_NAME = "Consultation soins de base";


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


		// EMR API
		// extra patient identifiers now set as part of Config
		// properties.put(EmrApiConstants.GP_EXTRA_PATIENT_IDENTIFIER_TYPES, HaitiPatientIdentifierTypes.DOSSIER_NUMBER.uuid() + "," + HaitiPatientIdentifierTypes.HIVEMR_V1.uuid());
		// primary identifier type now installed via metadata mappings
		// properties.put(EmrApiConstants.PRIMARY_IDENTIFIER_TYPE, HaitiPatientIdentifierTypes.ZL_EMR_ID.name());

        // Paper Record
        properties.put(PaperRecordConstants.GP_PAPER_RECORD_IDENTIFIER_TYPE, HaitiPatientIdentifierTypes.DOSSIER_NUMBER.uuid());
        properties.put(PaperRecordConstants.GP_EXTERNAL_DOSSIER_IDENTIFIER_TYPE, HaitiPatientIdentifierTypes.EXTERNAL_DOSSIER_NUMBER.uuid());

        // Core Apps
        properties.put(CoreAppsConstants.GP_DEFAULT_PATIENT_IDENTIFIER_LOCATION, MirebalaisLocations.MIREBALAIS_CDI_PARENT.uuid());

        setGlobalProperties(properties);

		uninstall(possible(GlobalProperty.class, EmrApiConstants.PRIMARY_IDENTIFIER_TYPE), "replaced by metadata mapping");

	}
}