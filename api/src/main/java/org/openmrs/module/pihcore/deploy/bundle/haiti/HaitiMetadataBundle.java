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
import org.openmrs.module.haiticore.metadata.bundles.HaitiAddressBundle;
import org.openmrs.module.haiticore.metadata.bundles.HaitiEncounterTypeBundle;
import org.openmrs.module.haiticore.metadata.bundles.HaitiPatientIdentifierTypeBundle;
import org.openmrs.module.haiticore.metadata.bundles.HaitiPersonAttributeTypeBundle;
import org.openmrs.module.metadatadeploy.bundle.AbstractMetadataBundle;
import org.openmrs.module.metadatadeploy.bundle.Requires;
import org.openmrs.module.paperrecord.PaperRecordConstants;
import org.openmrs.module.pihcore.deploy.bundle.core.GlobalPropertiesBundle;
import org.openmrs.module.pihcore.deploy.bundle.core.PihCoreMetadataBundle;
import org.openmrs.module.pihcore.metadata.haiti.PihHaitiPatientIdentifierTypes;
import org.openmrs.module.pihcore.metadata.haiti.mirebalais.MirebalaisLocations;
import org.openmrs.util.LocaleUtility;
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
        // OrderEntryConcepts.class,  install this after the MDS packages
		// TestOrderConcepts.class,  I believe we are now installing these via MDS
 		HaitiPatientIdentifierTypeBundle.class,
 		HaitiEncounterTypeBundle.class,
		HaitiPersonAttributeTypeBundle.class,
        PihHaitiPatientIdentifierTypeBundle.class,
		PihHaitiLocationsBundle.class,
		HaitiAddressBundle.class
} )
public class HaitiMetadataBundle extends AbstractMetadataBundle {

	protected Log log = LogFactory.getLog(getClass());

 	public static final class Packages {
		public static final String HUM_METADATA = "fa25ad0c-66cc-4715-8464-58570f7b5132";
	}

	public static final String DEFAULT_LOCALE = "fr";
 	public static final String ALLOWED_LOCALES = "ht, fr, en";
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
		// (we have to do this rigamarole because of new validations in 2.x that confirms that the allowed list contains the default locale, making it a two-step process to change)
		// (this is also a direct copy of code in LiberiaMetadataBundle, we should abstract this out)
		if (ALLOWED_LOCALES.contains(LocaleUtility.getDefaultLocale().toString())) {
			properties.put(OpenmrsConstants.GLOBAL_PROPERTY_LOCALE_ALLOWED_LIST, ALLOWED_LOCALES);
		}
		else {
			properties.put(OpenmrsConstants.GLOBAL_PROPERTY_LOCALE_ALLOWED_LIST, ALLOWED_LOCALES +", " + LocaleUtility.getDefaultLocale().toString());
		}
		properties.put(OpenmrsConstants.GLOBAL_PROPERTY_DEFAULT_LOCALE, DEFAULT_LOCALE);
		setGlobalProperties(properties);

		properties.put(OpenmrsConstants.GLOBAL_PROPERTY_LOCALE_ALLOWED_LIST, ALLOWED_LOCALES);

        // EMR API: most global properties have been moved to metadata mappings, but evidently not this one
		properties.put(EmrApiConstants.GP_DIAGNOSIS_SET_OF_SETS, GlobalPropertiesBundle.Concepts.HAITI_DIAGNOSIS_SET_OF_SETS);

		// extra patient identifiers now set as part of Config
		// properties.put(EmrApiConstants.GP_EXTRA_PATIENT_IDENTIFIER_TYPES, PihHaitiPatientIdentifierTypes.DOSSIER_NUMBER.uuid() + "," + PihHaitiPatientIdentifierTypes.HIVEMR_V1.uuid());
		// primary identifier type now installed via metadata mappings
		// properties.put(EmrApiConstants.PRIMARY_IDENTIFIER_TYPE, PihHaitiPatientIdentifierTypes.ZL_EMR_ID.name());

        // Paper Record
        properties.put(PaperRecordConstants.GP_PAPER_RECORD_IDENTIFIER_TYPE, PihHaitiPatientIdentifierTypes.DOSSIER_NUMBER.uuid());
        properties.put(PaperRecordConstants.GP_EXTERNAL_DOSSIER_IDENTIFIER_TYPE, PihHaitiPatientIdentifierTypes.EXTERNAL_DOSSIER_NUMBER.uuid());

        // Core Apps
        properties.put(CoreAppsConstants.GP_DEFAULT_PATIENT_IDENTIFIER_LOCATION, MirebalaisLocations.MIREBALAIS_CDI_PARENT.uuid());

        // Order Entry OWA
		properties.put("orderentryowa.labOrderablesConceptSet","517d25f7-2e68-4da4-912b-76090fbfe0fd");


        setGlobalProperties(properties);

		uninstall(possible(GlobalProperty.class, EmrApiConstants.PRIMARY_IDENTIFIER_TYPE), "replaced by metadata mapping");

	}
}
