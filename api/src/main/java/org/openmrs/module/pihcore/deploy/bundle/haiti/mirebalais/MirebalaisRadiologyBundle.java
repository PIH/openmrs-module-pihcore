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

package org.openmrs.module.pihcore.deploy.bundle.haiti.mirebalais;

import org.openmrs.module.metadatadeploy.bundle.AbstractMetadataBundle;
import org.openmrs.module.pacsintegration.PacsIntegrationConstants;
import org.openmrs.module.pihcore.deploy.bundle.core.concept.CoreConceptMetadataBundle;
import org.openmrs.module.pihcore.metadata.core.EncounterTypes;
import org.openmrs.module.pihcore.metadata.core.LocationAttributeTypes;
import org.openmrs.module.pihcore.metadata.core.OrderTypes;
import org.openmrs.module.pihcore.metadata.haiti.PihHaitiPatientIdentifierTypes;
import org.openmrs.module.radiologyapp.RadiologyConstants;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.Map;

import static org.openmrs.module.metadatadeploy.bundle.CoreConstructors.encounterRole;

/**
 * Radiology metadata bundle
 */
@Component
public class MirebalaisRadiologyBundle extends AbstractMetadataBundle {

	public static final class EncounterRoles {
		public static final String RADIOLOGY_TECHNICIAN = "8f4d96e2-c97c-4285-9319-e56b9ba6029c";
		public static final String PRINCIPAL_RESULTS_INTERPRETER = "08f73be2-9452-44b5-801b-bdf7418c2f71";
	}

	public static final class Concepts {
		public static final String XRAY_ORDERABLES = "35c24af8-6d60-4189-95c6-7e91e421d11f";
		public static final String CT_SCAN_ORDERABLES = "381d653b-a6b7-438a-b9f0-5034b5272def";
		public static final String ULTRASOUND_ORDERABLES = "a400b7e5-6b2f-404f-84d0-6eb2ca611a7d";
		public static final String CONTRAST_ORDERABLES = "60e6cd5a-3070-4b41-a36d-4730d4e85733";
		public static final String CREATININE_LEVEL = "668cd2a5-60dd-4dc4-889b-e09f072c6a1a";
	}

	public static final class Packages {
		public static final String RADIOLOGY_ORDERABLES = "c827fa67-24ca-40a0-95e4-4c07fecf0576";
	}

	/**
	 * @see AbstractMetadataBundle#install()
	 */
	@Override
	public void install() {

        // note that the radiology order type itself is installed as part of the OrderTypes bundle
		install(EncounterTypes.RADIOLOGY_ORDER);
		install(EncounterTypes.RADIOLOGY_REPORT);
		install(EncounterTypes.RADIOLOGY_STUDY);
		install(encounterRole("Radiology Technician", "Radiology Technician - person who performs radiology studies", EncounterRoles.RADIOLOGY_TECHNICIAN));
		install(encounterRole("Principal Results Interpreter", "Principal Results Interpreter - the provider responsible for interpreting the results of a radiology study", EncounterRoles.PRINCIPAL_RESULTS_INTERPRETER));
/*
		boolean conceptsInstalled = installMetadataSharingPackage("HUM_Radiology_Orderables-12.zip", Packages.RADIOLOGY_ORDERABLES);
		if (conceptsInstalled) {
			verifyConceptPresent(RadiologyConstants.CONCEPT_CODE_RADIOLOGY_PROCEDURE, EmrApiConstants.EMR_CONCEPT_SOURCE_NAME);
			verifyConceptPresent(RadiologyConstants.CONCEPT_CODE_RADIOLOGY_REPORT_TYPE, EmrApiConstants.EMR_CONCEPT_SOURCE_NAME);
			verifyConceptPresent(RadiologyConstants.CONCEPT_CODE_RADIOLOGY_REPORT_BODY, EmrApiConstants.EMR_CONCEPT_SOURCE_NAME);
			verifyConceptPresent(RadiologyConstants.CONCEPT_CODE_RADIOLOGY_ORDER_NUMBER, EmrApiConstants.EMR_CONCEPT_SOURCE_NAME);
			verifyConceptPresent(RadiologyConstants.CONCEPT_CODE_RADIOLOGY_IMAGES_AVAILABLE, EmrApiConstants.EMR_CONCEPT_SOURCE_NAME);
			verifyConceptPresent(RadiologyConstants.CONCEPT_CODE_RADIOLOGY_REPORT_CORRECTION, EmrApiConstants.EMR_CONCEPT_SOURCE_NAME);
			verifyConceptPresent(RadiologyConstants.CONCEPT_CODE_RADIOLOGY_REPORT_FINAL, EmrApiConstants.EMR_CONCEPT_SOURCE_NAME);
			verifyConceptPresent(RadiologyConstants.CONCEPT_CODE_RADIOLOGY_REPORT_PRELIM, EmrApiConstants.EMR_CONCEPT_SOURCE_NAME);
			verifyConceptPresent(RadiologyConstants.CONCEPT_CODE_RADIOLOGY_STUDY_SET, EmrApiConstants.EMR_CONCEPT_SOURCE_NAME);
			verifyConceptPresent(RadiologyConstants.CONCEPT_CODE_RADIOLOGY_REPORT_SET, EmrApiConstants.EMR_CONCEPT_SOURCE_NAME);
		}*/

		Map<String, String> properties = new LinkedHashMap<String, String>();
		
		// Radiology
		properties.put(RadiologyConstants.GP_RADIOLOGY_ORDER_ENCOUNTER_TYPE, EncounterTypes.RADIOLOGY_ORDER.uuid());
		properties.put(RadiologyConstants.GP_RADIOLOGY_STUDY_ENCOUNTER_TYPE, EncounterTypes.RADIOLOGY_STUDY.uuid());
		properties.put(RadiologyConstants.GP_RADIOLOGY_REPORT_ENCOUNTER_TYPE, EncounterTypes.RADIOLOGY_REPORT.uuid());
		properties.put(RadiologyConstants.GP_RADIOLOGY_TEST_ORDER_TYPE, OrderTypes.RADIOLOGY_TEST_ORDER.uuid());
		properties.put(RadiologyConstants.GP_XRAY_ORDERABLES_CONCEPT, Concepts.XRAY_ORDERABLES);
		properties.put(RadiologyConstants.GP_CT_SCAN_ORDERABLES_CONCEPT, Concepts.CT_SCAN_ORDERABLES);
		properties.put(RadiologyConstants.GP_ULTRASOUND_ORDERABLES_CONCEPT, Concepts.ULTRASOUND_ORDERABLES);
		properties.put(RadiologyConstants.GP_CONTRAST_ORDERABLES_CONCEPT, Concepts.CONTRAST_ORDERABLES);
		properties.put(RadiologyConstants.GP_CREATININE_LEVEL_CONCEPT, Concepts.CREATININE_LEVEL);
		properties.put(RadiologyConstants.GP_RADIOLOGY_TECHNICIAN_ENCOUNTER_ROLE, EncounterRoles.RADIOLOGY_TECHNICIAN);
		properties.put(RadiologyConstants.GP_PRINCIPAL_RESULTS_INTERPRETER_ENCOUNTER_ROLE, EncounterRoles.PRINCIPAL_RESULTS_INTERPRETER);
		properties.put(RadiologyConstants.GP_LEAD_RADIOLOGY_TECH_NAME, "Edner");
		properties.put(RadiologyConstants.GP_LEAD_RADIOLOGY_TECH_CONTACT_INFO, "4868-9765");
		
		// PACS Integration
		properties.put(PacsIntegrationConstants.GP_PATIENT_IDENTIFIER_TYPE_UUID, PihHaitiPatientIdentifierTypes.ZL_EMR_ID.uuid());
		properties.put(PacsIntegrationConstants.GP_DEFAULT_LOCALE, "en");
		properties.put(PacsIntegrationConstants.GP_SENDING_FACILITY, "Mirebalais");
		properties.put(PacsIntegrationConstants.GP_PROCEDURE_CODE_CONCEPT_SOURCE_UUID, CoreConceptMetadataBundle.ConceptSources.LOINC);
		properties.put(PacsIntegrationConstants.GP_LOCATION_CODE_ATTRIBUTE_TYPE_UUID, LocationAttributeTypes.LOCATION_CODE.uuid());
		properties.put(PacsIntegrationConstants.GP_HL7_LISTENER_PORT, "6663");
		
		
		setGlobalProperties(properties);
	}

}
