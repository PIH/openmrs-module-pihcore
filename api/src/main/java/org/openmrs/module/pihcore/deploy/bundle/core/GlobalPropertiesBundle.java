package org.openmrs.module.pihcore.deploy.bundle.core;

import org.openmrs.module.coreapps.CoreAppsConstants;
import org.openmrs.module.emr.EmrConstants;
import org.openmrs.module.emrapi.EmrApiConstants;
import org.openmrs.module.htmlformentry.HtmlFormEntryConstants;
import org.openmrs.module.pihcore.deploy.bundle.PihMetadataBundle;
import org.openmrs.module.pihcore.deploy.bundle.core.concept.AllergyConcepts;
import org.openmrs.module.pihcore.deploy.bundle.core.concept.CommonConcepts;
import org.openmrs.module.pihcore.metadata.core.EncounterTypes;
import org.openmrs.module.pihcore.metadata.core.Locations;
import org.openmrs.module.registrationcore.RegistrationCoreConstants;
import org.openmrs.module.reporting.ReportingConstants;
import org.openmrs.ui.framework.UiFrameworkConstants;
import org.openmrs.util.OpenmrsConstants;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.Map;

@Component
public class GlobalPropertiesBundle extends PihMetadataBundle {

    public static final class Concepts { // TODO: Confirm all below are in Hum_Metadata package
        public static final String DIAGNOSIS_SET_OF_SETS = "8fcd0b0c-f977-4a66-a1b5-ad7ce68e6770";
        public static final String PAYMENT_AMOUNT = "5d1bc5de-6a35-4195-8631-7322941fe528";
        public static final String PAYMENT_REASON = "36ba7721-fae0-4da4-aef2-7e476cc04bdf";
        public static final String PAYMENT_RECEIPT_NUMBER = "20438dc7-c5b4-4d9c-8480-e888f4795123";
        public static final String PAYMENT_CONSTRUCT = "7a6330f1-9503-465c-8d63-82e1ad914b47";

    }

    public static final class Forms {
        public static final String ADMISSION = "43acf930-eb1b-11e2-91e2-0800200c9a66";  // TODO: Install in bundle
        public static final String TRANSFER_WITHIN_HOSPITAL = "d068bc80-fb95-11e2-b778-0800200c9a66";  // TODO: Install in bundle
        public static final String EXIT_FROM_INPATIENT = "e0a26c20-fba6-11e2-b778-0800200c9a66";  // TODO: Install in bundle
    }

    public static final String DEFAULT_DATE_FORMAT = "dd MMM yyyy";
    public static final String DEFAULT_TIME_FORMAT = "h:mm aa";
    public static final String DEFAULT_DATETIME_FORMAT = DEFAULT_DATE_FORMAT + " " + DEFAULT_TIME_FORMAT;

    @Override
    public void install() throws Exception {

        Map<String, String> properties = new LinkedHashMap<String, String>();

        // OpenMRS Core
        properties.put(OpenmrsConstants.GP_PASSWORD_MINIMUM_LENGTH, "8");
        properties.put(OpenmrsConstants.GP_PASSWORD_REQUIRES_DIGIT, "false");
        properties.put(OpenmrsConstants.GP_PASSWORD_REQUIRES_NON_DIGIT, "false");
        properties.put(OpenmrsConstants.GP_PASSWORD_REQUIRES_UPPER_AND_LOWER_CASE, "false");
        //properties.put(OpenmrsConstants.GP_CASE_SENSITIVE_DATABASE_STRING_COMPARISON, "false");


        // Html Form Entry
        properties.put(HtmlFormEntryConstants.GP_DATE_FORMAT, DEFAULT_DATE_FORMAT);
        properties.put(HtmlFormEntryConstants.GP_TIME_FORMAT, DEFAULT_TIME_FORMAT);
        properties.put(HtmlFormEntryConstants.GP_SHOW_DATE_FORMAT, "false");
        properties.put(HtmlFormEntryConstants.GP_UNKNOWN_CONCEPT, CommonConcepts.Concepts.UNKNOWN);

        // Reporting
        properties.put(ReportingConstants.GLOBAL_PROPERTY_TEST_PATIENTS_COHORT_DEFINITION, "");

        // UI Framework
        properties.put(UiFrameworkConstants.GP_FORMATTER_DATE_FORMAT, DEFAULT_DATE_FORMAT);
        properties.put(UiFrameworkConstants.GP_FORMATTER_DATETIME_FORMAT, DEFAULT_DATETIME_FORMAT);

        // EMR API
        properties.put(EmrApiConstants.GP_CLINICIAN_ENCOUNTER_ROLE, EncounterRoleBundle.EncounterRoles.CONSULTING_CLINICIAN);
        properties.put(EmrApiConstants.GP_ORDERING_PROVIDER_ENCOUNTER_ROLE, EncounterRoleBundle.EncounterRoles.ORDERING_PROVIDER);
        properties.put(EmrApiConstants.GP_CHECK_IN_CLERK_ENCOUNTER_ROLE, EncounterRoleBundle.EncounterRoles.ADMINISTRATIVE_CLERK);
        properties.put(EmrApiConstants.GP_AT_FACILITY_VISIT_TYPE, VisitTypeBundle.VisitTypes.CLINIC_OR_HOSPITAL_VISIT);
        properties.put(EmrApiConstants.GP_VISIT_NOTE_ENCOUNTER_TYPE, EncounterTypes.CONSULTATION.uuid());
        properties.put(EmrApiConstants.GP_CHECK_IN_ENCOUNTER_TYPE, EncounterTypes.CHECK_IN.uuid());
        properties.put(EmrApiConstants.GP_ADMISSION_ENCOUNTER_TYPE, EncounterTypes.ADMISSION.uuid());
        properties.put(EmrApiConstants.GP_EXIT_FROM_INPATIENT_ENCOUNTER_TYPE, EncounterTypes.EXIT_FROM_CARE.uuid());
        properties.put(EmrApiConstants.GP_TRANSFER_WITHIN_HOSPITAL_ENCOUNTER_TYPE, EncounterTypes.TRANSFER.uuid());
        properties.put(EmrApiConstants.GP_DIAGNOSIS_SET_OF_SETS, Concepts.DIAGNOSIS_SET_OF_SETS);
        properties.put(EmrApiConstants.GP_UNKNOWN_LOCATION, Locations.UNKNOWN.uuid());
        properties.put(EmrApiConstants.GP_ADMISSION_FORM, Forms.ADMISSION);
        properties.put(EmrApiConstants.GP_TRANSFER_WITHIN_HOSPITAL_FORM, Forms.TRANSFER_WITHIN_HOSPITAL);
        properties.put(EmrApiConstants.GP_EXIT_FROM_INPATIENT_FORM, Forms.EXIT_FROM_INPATIENT);


        // REST
        // These do not use constants from the rest module due to the omod dependency when provided in maven.
        // These are used to increase the number of results that rest web services returns (for the appointment scheduling module)
        properties.put("webservices.rest.maxResultsAbsolute", "1000");
        properties.put("webservices.rest.maxResultsDefault", "500");

        // EMR
        properties.put(EmrConstants.PAYMENT_AMOUNT_CONCEPT, Concepts.PAYMENT_AMOUNT);
        properties.put(EmrConstants.PAYMENT_REASON_CONCEPT, Concepts.PAYMENT_REASON);
        properties.put(EmrConstants.PAYMENT_RECEIPT_NUMBER_CONCEPT, Concepts.PAYMENT_RECEIPT_NUMBER);
        properties.put(EmrConstants.PAYMENT_CONSTRUCT_CONCEPT, Concepts.PAYMENT_CONSTRUCT);

        // Core Apps
        properties.put(CoreAppsConstants.GP_SEARCH_DELAY_SHORT, "500");

        // Registration Core
        properties.put(RegistrationCoreConstants.GP_PATIENT_NAME_SEARCH, "registrationcore.NamePhoneticsPatientNameSearch");
        properties.put(RegistrationCoreConstants.GP_FAST_SIMILAR_PATIENT_SEARCH_ALGORITHM, "registrationcore.NamePhoneticsPatientSearchAlgorithm");
        properties.put(RegistrationCoreConstants.GP_PRECISE_SIMILAR_PATIENT_SEARCH_ALGORITHM, "registrationcore.BasicExactPatientSearchAlgorithm");

        // Allergies
        properties.put("allergy.concept.unknown", CommonConcepts.Concepts.UNKNOWN);
        properties.put("allergy.concept.otherNonCoded", CommonConcepts.Concepts.OTHER_NON_CODED);
       // properties.put("allergy.concept.severity.mild", CommonConcepts.Concepts.OTHER_NON_CODED);
       // properties.put("allergy.concept.severity.moderate", CommonConcepts.Concepts.OTHER_NON_CODED);
       // properties.put("allergy.concept.severity.severe", CommonConcepts.Concepts.OTHER_NON_CODED);
        properties.put("allergy.concept.allergen.food", AllergyConcepts.Concepts.ALLERGENS_FOOD_SET);
        properties.put("allergy.concept.allergen.drug", AllergyConcepts.Concepts.ALLERGENS_DRUG_SET);
        properties.put("allergy.concept.allergen.environment", AllergyConcepts.Concepts.ALLERGENS_ENVIRONMENT_SET);
        properties.put("allergy.concept.reactions", AllergyConcepts.Concepts.ALLERGY_REACTIONS_SET);

        setGlobalProperties(properties);
    }
}
