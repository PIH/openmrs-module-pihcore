package org.openmrs.module.pihcore.deploy.bundle.core;

import org.openmrs.GlobalProperty;
import org.openmrs.module.attachments.AttachmentsConstants;
import org.openmrs.module.coreapps.CoreAppsConstants;
import org.openmrs.module.emr.EmrConstants;
import org.openmrs.module.emrapi.EmrApiConstants;
import org.openmrs.module.htmlformentry.HtmlFormEntryConstants;
import org.openmrs.module.metadatadeploy.bundle.AbstractMetadataBundle;
import org.openmrs.module.namephonetics.NamePhoneticsConstants;
import org.openmrs.module.pihcore.config.Config;
import org.openmrs.module.pihcore.deploy.bundle.core.concept.AllergyConcepts;
import org.openmrs.module.pihcore.deploy.bundle.core.concept.CommonConcepts;
import org.openmrs.module.pihcore.metadata.core.OrderTypes;
import org.openmrs.module.registrationcore.RegistrationCoreConstants;
import org.openmrs.module.reporting.ReportingConstants;
import org.openmrs.ui.framework.UiFrameworkConstants;
import org.openmrs.util.OpenmrsConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.Map;

@Component
public class GlobalPropertiesBundle extends AbstractMetadataBundle {

    @Autowired
    private Config config;

    private static final String DOUBLE_METAPHONE_ALTERNATE_NAME = "Double Metaphone Alternate";

    public static final class Concepts { // TODO: Confirm all below are in Hum_Metadata package

        public static final String LIBERIA_DIAGNOSIS_SET_OF_SETS = "ed97232b-1a09-4260-b06c-d193107c32a7";
        public static final String HAITI_DIAGNOSIS_SET_OF_SETS = "8fcd0b0c-f977-4a66-a1b5-ad7ce68e6770";
        public static final String MEXICO_DIAGNOSIS_SET_OF_SETS = "7fd1dd5a-ab2d-43cd-aedb-e0d6bb81f8bf";

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
        properties.put(OpenmrsConstants.GLOBAL_PROPERTY_PATIENT_NAME_REGEX, "");
        properties.put(OpenmrsConstants.GP_CASE_SENSITIVE_DATABASE_STRING_COMPARISON, "false");


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

        // REST
        // These do not use constants from the rest module due to the omod dependency when provided in maven.
        // These are used to increase the number of results that rest web services returns (for the appointment scheduling module)
        properties.put("webservices.rest.maxResultsAbsolute", "1000");
        properties.put("webservices.rest.maxResultsDefault", "500");

        // EMR-API
        properties.put(EmrApiConstants.GP_USE_LEGACY_DIAGNOSIS_SERVICE, "true");
        properties.put(EmrApiConstants.GP_VISIT_ASSIGNMENT_HANDLER_ADJUST_ENCOUNTER_TIME_OF_DAY_IF_NECESSARY, "true");

        // EMR
        properties.put(EmrConstants.PAYMENT_AMOUNT_CONCEPT, Concepts.PAYMENT_AMOUNT);
        properties.put(EmrConstants.PAYMENT_REASON_CONCEPT, Concepts.PAYMENT_REASON);
        properties.put(EmrConstants.PAYMENT_RECEIPT_NUMBER_CONCEPT, Concepts.PAYMENT_RECEIPT_NUMBER);
        properties.put(EmrConstants.PAYMENT_CONSTRUCT_CONCEPT, Concepts.PAYMENT_CONSTRUCT);

        // Core Apps (force the user to hit enter when searching--no "autocomplete")
        properties.put(CoreAppsConstants.GP_SEARCH_DELAY_SHORT, "99999999");
        properties.put(CoreAppsConstants.GP_SEARCH_DELAY_LONG, "99999999");

        // Registration Core
        properties.put(RegistrationCoreConstants.GP_PATIENT_NAME_SEARCH, "registrationcore.ExistingPatientNameSearch");
        properties.put(RegistrationCoreConstants.GP_FAST_SIMILAR_PATIENT_SEARCH_ALGORITHM, "pihcore.PihPatientSearchAlgorithm");
        properties.put(RegistrationCoreConstants.GP_PRECISE_SIMILAR_PATIENT_SEARCH_ALGORITHM, "registrationcore.BasicExactPatientSearchAlgorithm");

        // Name Phonetics
        properties.put(NamePhoneticsConstants.GIVEN_NAME_GLOBAL_PROPERTY, DOUBLE_METAPHONE_ALTERNATE_NAME);
        properties.put(NamePhoneticsConstants.MIDDLE_NAME_GLOBAL_PROPERTY, DOUBLE_METAPHONE_ALTERNATE_NAME);
        properties.put(NamePhoneticsConstants.FAMILY_NAME_GLOBAL_PROPERTY, DOUBLE_METAPHONE_ALTERNATE_NAME);
        properties.put(NamePhoneticsConstants.FAMILY_NAME2_GLOBAL_PROPERTY, DOUBLE_METAPHONE_ALTERNATE_NAME);

        // Allergies
        properties.put("allergy.concept.unknown", CommonConcepts.Concepts.UNKNOWN);
        properties.put("allergy.concept.otherNonCoded", CommonConcepts.Concepts.OTHER_NON_CODED);
        properties.put("allergy.concept.severity.mild", AllergyConcepts.Concepts.ALLERGY_SEVERITY_MILD);
        properties.put("allergy.concept.severity.moderate", AllergyConcepts.Concepts.ALLERGY_SEVERITY_MODERATE);
        properties.put("allergy.concept.severity.severe", AllergyConcepts.Concepts.ALLERGY_SEVERITY_SEVERE);
        properties.put("allergy.concept.allergen.food", AllergyConcepts.Concepts.ALLERGENS_FOOD_SET);
        properties.put("allergy.concept.allergen.drug", AllergyConcepts.Concepts.ALLERGENS_DRUG_SET);
        properties.put("allergy.concept.allergen.environment", AllergyConcepts.Concepts.ALLERGENS_ENVIRONMENT_SET);
        properties.put("allergy.concept.reactions", AllergyConcepts.Concepts.ALLERGY_REACTIONS_SET);

        // Attachments Module
        properties.put(AttachmentsConstants.GP_ALLOW_NO_CAPTION, "true");
        properties.put(AttachmentsConstants.GP_WEBCAM_ALLOWED, "false");
        properties.put(AttachmentsConstants.GP_MAX_STORAGE_FILE_SIZE, "5.0");
        properties.put(AttachmentsConstants.GP_MAX_UPLOAD_FILE_SIZE, "5.0");
        properties.put(AttachmentsConstants.GP_ASSOCIATE_WITH_VISIT, "false"); //Upload documents outside the visits
        properties.put(AttachmentsConstants.GP_ENCOUNTER_TYPE_UUID, "");

        // REST web services
        // this is because in the Lab Workflow OWA we want to fetch batches of 3000
        properties.put("webservices.rest.maxResultsAbsolute", "3000");

        // Order Entry OWA
        // TODO: can we get rid of "order.encounterType" and "order.encounterRole" GP?
        properties.put("orderentryowa.encounterType", "Test Order");
        properties.put("orderentryowa.encounterRole", "Ordering Provider");
        properties.put("orderentryowa.dateAndTimeFormat", "DD-MMM-YYYY HH:mm");
        properties.put("orderentryowa.labOrderAutoExpireTimeInDays", "30"); //30 days
        // each implementation will now specify their own orderables set
        //properties.put("orderentryowa.labOrderablesConceptSet","517d25f7-2e68-4da4-912b-76090fbfe0fd");

        // Lab Workflow OWA
        properties.put("labworkflowowa.labResultsEntryEncounterType","39C09928-0CAB-4DBA-8E48-39C631FA4286"); // Lab Specimen Collection Encounter (not Lab Results, potentially confusing)
        properties.put("labworkflowowa.labResultsEncounterTypes","39C09928-0CAB-4DBA-8E48-39C631FA4286,4d77916a-0620-11e5-a6c0-1697f925ec7b"); // Lab Specimen Collection Encounter and Lab Results (for display)
        properties.put("labworkflowowa.labResultsEncounterRole", EncounterRoleBundle.EncounterRoles.LAB_TECHNICIAN);
        properties.put("labworkflowowa.labResultsDateConcept","68d6bd27-37ff-4d7a-87a0-f5e0f9c8dcc0");   // PIH:Date of test results
        properties.put("labworkflowowa.testOrderNumberConcept","393dec41-2fb5-428f-acfa-36ea85da6666");   // PIH:Test order number
        properties.put("labworkflowowa.didNotPerformQuestion","7e0cf626-dbe8-42aa-9b25-483b51350bf8");   // CIEL:163725 (Test Status)
        properties.put("labworkflowowa.didNotPerformAnswer","3cd75550-26fe-102b-80cb-0017a47871b2");   // CIEL:1118 (Not done)
        properties.put("labworkflowowa.dateAndTimeFormat", "DD-MMM-YYYY HH:mm");
        properties.put("labworkflowowa.didNotPerformReason","5dc35a2a-228c-41d0-ae19-5b1e23618eda");  // CIEL:165182
        properties.put("labworkflowowa.locationOfLaboratory", "e9732df4-971d-4a9a-9129-e2e610552468");  //PIH: 1791
        properties.put("labworkflowowa.estimatedCollectionDateQuestion", "87f506e3-4433-40ec-b16c-b3c65e402989");  //  PIH:11781
        properties.put("labworkflowowa.estimatedCollectionDateAnswer", "3cd6f600-26fe-102b-80cb-0017a47871b2");  // CIEL:1065 ("yes")
        properties.put("labworkflowowa.testOrderType", OrderTypes.TEST_ORDER.uuid());
        properties.put("labworkflowowa.orderLabTestLink", "/coreapps/findpatient/findPatient.page?app=pih.app.labs.ordering");
        properties.put("labworkflowowa.enableLabelPrinting", "true");
        properties.put("labworkflowowa.labelPrintingEndpoint", "mirebalais/lablabelprinter");
        properties.put("labworkflowowa.ordersBatchSize", "3000");

        uninstall(possible(GlobalProperty.class, "labworkflowowa.specimenCollectionDateConcept"), "using encounter date instead");
        uninstall(possible(GlobalProperty.class, "labworkflowowa.labResultsEncounterType"), "using new lab result entry encounter type instead");

        setGlobalProperties(properties);

        // EMR API global properties are now set up via Metadata Mappings
        uninstall(possible(GlobalProperty.class, EmrApiConstants.GP_UNKNOWN_LOCATION), "replaced by metadata mapping");
        uninstall(possible(GlobalProperty.class, EmrApiConstants.GP_AT_FACILITY_VISIT_TYPE), "replaced by metadata mapping");
        uninstall(possible(GlobalProperty.class, EmrApiConstants.GP_CLINICIAN_ENCOUNTER_ROLE), "replaced by metadata mapping");
        uninstall(possible(GlobalProperty.class, EmrApiConstants.GP_ORDERING_PROVIDER_ENCOUNTER_ROLE), "replaced by metadata mapping");
        uninstall(possible(GlobalProperty.class, EmrApiConstants.GP_CHECK_IN_CLERK_ENCOUNTER_ROLE), "replaced by metadata mapping");
        uninstall(possible(GlobalProperty.class, EmrApiConstants.GP_CHECK_IN_ENCOUNTER_TYPE), "replaced by metadata mapping");
        uninstall(possible(GlobalProperty.class, EmrApiConstants.GP_EXIT_FROM_INPATIENT_ENCOUNTER_TYPE), "replaced by metadata mapping");
        uninstall(possible(GlobalProperty.class, EmrApiConstants.GP_ADMISSION_ENCOUNTER_TYPE), "replaced by metadata mapping");
        uninstall(possible(GlobalProperty.class, EmrApiConstants.GP_TRANSFER_WITHIN_HOSPITAL_ENCOUNTER_TYPE), "replaced by metadata mapping");
        uninstall(possible(GlobalProperty.class, EmrApiConstants.GP_VISIT_NOTE_ENCOUNTER_TYPE), "replaced by metadata mapping");
        uninstall(possible(GlobalProperty.class, EmrApiConstants.GP_ADMISSION_FORM), "replaced by metadata mapping");
        uninstall(possible(GlobalProperty.class, EmrApiConstants.GP_TRANSFER_WITHIN_HOSPITAL_FORM), "replaced by metadata mapping");
        uninstall(possible(GlobalProperty.class, EmrApiConstants.GP_EXIT_FROM_INPATIENT_FORM), "replaced by metadata mapping");
        uninstall(possible(GlobalProperty.class, EmrApiConstants.GP_EXTRA_PATIENT_IDENTIFIER_TYPES), "replaced by metadata mapping");

    }
}
