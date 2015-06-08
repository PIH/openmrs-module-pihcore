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
import org.openmrs.module.coreapps.CoreAppsConstants;
import org.openmrs.module.emrapi.EmrApiConstants;
import org.openmrs.module.metadatadeploy.bundle.AbstractMetadataBundle;
import org.openmrs.module.metadatadeploy.bundle.Requires;
import org.openmrs.module.namephonetics.NamePhoneticsConstants;
import org.openmrs.module.paperrecord.PaperRecordConstants;
import org.openmrs.module.patientregistration.PatientRegistrationGlobalProperties;
import org.openmrs.module.patientregistration.search.DefaultPatientRegistrationSearch;
import org.openmrs.module.pihcore.deploy.bundle.PihMetadataBundle;
import org.openmrs.module.pihcore.deploy.bundle.core.PihCoreMetadataBundle;
import org.openmrs.module.pihcore.metadata.core.LocationTags;
import org.openmrs.module.pihcore.metadata.core.PersonAttributeTypes;
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
		TestOrderConcepts.class,
        HaitiLocationsBundle.class,
        HaitiPatientIdentifierTypeBundle.class,
        HaitiAddressBundle.class
} )
public class HaitiMetadataBundle extends PihMetadataBundle {

	protected Log log = LogFactory.getLog(getClass());

 	public static final class Packages {
		public static final String HUM_METADATA = "fa25ad0c-66cc-4715-8464-58570f7b5132";
	}

	public static final String DEFAULT_LOCALE = "fr";
    private static final String REGISTRATION_ENCOUNTER_NAME = "Enregistrement de patient";
    private static final String CHECK_IN_ENCOUNTER_NAME = "Inscription";
    private static final String PRIMARY_CARE_VISIT_ENCOUNTER_NAME = "Consultation soins de base";
    private static final String DOUBLE_METAPHONE_ALTERNATE_NAME = "Double Metaphone Alternate";


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
		properties.put(EmrApiConstants.GP_EXTRA_PATIENT_IDENTIFIER_TYPES, HaitiPatientIdentifierTypes.DOSSIER_NUMBER.uuid() + "," + HaitiPatientIdentifierTypes.HIVEMR_V1.uuid());
		properties.put(EmrApiConstants.PRIMARY_IDENTIFIER_TYPE, HaitiPatientIdentifierTypes.ZL_EMR_ID.name());

        // Paper Record
        properties.put(PaperRecordConstants.GP_PAPER_RECORD_IDENTIFIER_TYPE, HaitiPatientIdentifierTypes.DOSSIER_NUMBER.uuid());
        properties.put(PaperRecordConstants.GP_EXTERNAL_DOSSIER_IDENTIFIER_TYPE, HaitiPatientIdentifierTypes.EXTERNAL_DOSSIER_NUMBER.uuid());

        // Core Apps
        properties.put(CoreAppsConstants.GP_DEFAULT_PATIENT_IDENTIFIER_LOCATION, MirebalaisLocations.MIREBALAIS_CDI_PARENT.uuid());

        // Name Phonetics
        properties.put(NamePhoneticsConstants.GIVEN_NAME_GLOBAL_PROPERTY, DOUBLE_METAPHONE_ALTERNATE_NAME);
        properties.put(NamePhoneticsConstants.MIDDLE_NAME_GLOBAL_PROPERTY, DOUBLE_METAPHONE_ALTERNATE_NAME);
        properties.put(NamePhoneticsConstants.FAMILY_NAME_GLOBAL_PROPERTY, DOUBLE_METAPHONE_ALTERNATE_NAME);
        properties.put(NamePhoneticsConstants.FAMILY_NAME2_GLOBAL_PROPERTY, DOUBLE_METAPHONE_ALTERNATE_NAME);

        // Patient Registration
		properties.put(PatientRegistrationGlobalProperties.SUPPORTED_TASKS, "patientRegistration|primaryCareReception|edCheckIn|patientLookup");
		properties.put(PatientRegistrationGlobalProperties.SEARCH_CLASS, DefaultPatientRegistrationSearch.class.getName());
		properties.put(PatientRegistrationGlobalProperties.LABEL_PRINT_COUNT, "1");
		properties.put(PatientRegistrationGlobalProperties.PROVIDER_ROLES, "");  // note that this global property is only used for the primary care visit component of the Patient Registration module, which we aren't using in Mirebalais
		properties.put(PatientRegistrationGlobalProperties.ID_CARD_LABEL_TEXT, "Zanmi Lasante Patient ID Card");
		properties.put(PatientRegistrationGlobalProperties.BIRTH_YEAR_INTERVAL, "1");
		properties.put(PatientRegistrationGlobalProperties.MEDICAL_RECORD_LOCATION_TAG, LocationTags.MEDICAL_RECORD_LOCATION.uuid());
		properties.put(PatientRegistrationGlobalProperties.PRIMARY_IDENTIFIER_TYPE, HaitiPatientIdentifierTypes.ZL_EMR_ID.name());
		properties.put(PatientRegistrationGlobalProperties.NUMERO_DOSSIER, HaitiPatientIdentifierTypes.DOSSIER_NUMBER.uuid());
		properties.put(PatientRegistrationGlobalProperties.EXTERNAL_NUMERO_DOSSIER, HaitiPatientIdentifierTypes.EXTERNAL_DOSSIER_NUMBER.uuid());
		properties.put(PatientRegistrationGlobalProperties.PROVIDER_IDENTIFIER_PERSON_ATTRIBUTE_TYPE, PersonAttributeTypes.PROVIDER_IDENTIFIER.name());
		properties.put(PatientRegistrationGlobalProperties.ID_CARD_PERSON_ATTRIBUTE_TYPE, PersonAttributeTypes.TELEPHONE_NUMBER.name());
		properties.put(PatientRegistrationGlobalProperties.PATIENT_VIEWING_ATTRIBUTE_TYPES, PersonAttributeTypes.TELEPHONE_NUMBER.name());
		properties.put(PatientRegistrationGlobalProperties.PATIENT_REGISTRATION_ENCOUNTER_TYPE, REGISTRATION_ENCOUNTER_NAME);
		properties.put(PatientRegistrationGlobalProperties.PRIMARY_CARE_RECEPTION_ENCOUNTER_TYPE, CHECK_IN_ENCOUNTER_NAME);
		properties.put(PatientRegistrationGlobalProperties.PRIMARY_CARE_VISIT_ENCOUNTER_TYPE, PRIMARY_CARE_VISIT_ENCOUNTER_NAME);

		// TODO: For all of these, determine best way to set metadata against these
		properties.put(PatientRegistrationGlobalProperties.URGENT_DIAGNOSIS_CONCEPT, "PIH: Haiti nationally urgent diseases");
		properties.put(PatientRegistrationGlobalProperties.NOTIFY_DIAGNOSIS_CONCEPT, "PIH: Haiti nationally notifiable diseases");
		properties.put(PatientRegistrationGlobalProperties.NON_CODED_DIAGNOSIS_CONCEPT, "PIH: ZL Primary care diagnosis non-coded");
		properties.put(PatientRegistrationGlobalProperties.NEONATAL_DISEASES_CONCEPT, "PIH: Haiti neonatal diseases");
		properties.put(PatientRegistrationGlobalProperties.CODED_DIAGNOSIS_CONCEPT, "PIH: HUM Outpatient diagnosis");
		properties.put(PatientRegistrationGlobalProperties.AGE_RESTRICTED_CONCEPT, "PIH: Haiti age restricted diseases");
		properties.put(PatientRegistrationGlobalProperties.RECEIPT_NUMBER_CONCEPT, "PIH: Receipt number|en:Receipt Number|ht:Nimewo Resi a");
		properties.put(PatientRegistrationGlobalProperties.PAYMENT_AMOUNT_CONCEPT, "PIH: Payment amount|en:Payment amount|ht:Kantite lajan");
		properties.put(PatientRegistrationGlobalProperties.VISIT_REASON_CONCEPT, "PIH: Type of HUM visit|en:Visit reason|ht:Tip de Vizit");
		properties.put(PatientRegistrationGlobalProperties.ICD10_CONCEPT_SOURCE, "ICD-10");

        setGlobalProperties(properties);
	}
}