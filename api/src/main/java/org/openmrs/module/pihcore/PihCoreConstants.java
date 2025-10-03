package org.openmrs.module.pihcore;

public class PihCoreConstants {

    public static final String TERMS_AND_CONDITIONS_ENABLED_PROPERTY = "terms_and_conditions_enabled";
    public static final String USER_PROPERTY_TERMS_AND_CONDITIONS_ACCEPTED_DATE = "termsAndConditionsAcceptedDate";
    public static final String SESSION_ATTRIBUTE_TERMS_ACCEPTED = "pihcore.termsAndConditionsAccepted";

    public static final String DBEVENT_ENABLED_PROPERTY = "dbevent_enabled";
    
    public static final String GP_COMPONENT_PREFIX = "pihcore.component.";
    public static final String GP_CONFIGURED_SITE = "pihcore.site";
    public static final String GP_NEXT_RADIOLOGY_ORDER_NUMBER_SEED = "order.nextRadiologyOrderNumberSeed";

    public static final String RADIOLOGY_ORDER_NUMBER_GENERATOR_BEAN_ID = "order.radiologyOrderNumberGenerator";

    public static final String HTMLFORMENTRY_CAUSE_OF_DEATH_LIST_TAG_NAME = "causeOfDeathList";
    public static final String HTMLFORMENTRY_PAST_MEDICAL_HISTORY_CHECKBOX_TAG_NAME = "pastMedicalHistoryCheckbox";
    public static final String HTMLFORMENTRY_FAMILY_HISTORY_RELATIVE_CHECKBOXES_TAG_NAME = "familyHistoryRelativeCheckboxes";

    public static final String TASK_CLOSE_STALE_VISITS_NAME = "EMR module - Close Stale Visits";

    public static final String TASK_CLOSE_STALE_VISITS_DESCRIPTION = "Closes any open visits that are no longer active";

    public static final long TASK_CLOSE_STALE_VISITS_REPEAT_INTERVAL = 5 * 60; // 5 minutes

    // encounter types
    public static final String DEATH_CERTIFICATE_ENCOUNTER_TYPE_UUID = "1545d7ff-60f1-485e-9c95-5740b8e6634b";

    public static final String LOCAL_ZL_IDENTIFIER_GENERATOR_UUID = "52250ca2-d25f-11e4-8dbf-54ee7513a7ff";
    public static final String LOCAL_ZL_IDENTIFIER_POOL_UUID = "aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa";

    public static final String UHM_DOSSIER_NUMBER_IDENTIFIER_SOURCE_UUID = "cccccccc-cccc-cccc-cccc-cccccccccccc";
    public static final String UHM_DOSSIER_NUMBER_PREFIX = "A";

    public static final String CDI_DOSSIER_NUMBER_IDENTIFIER_SOURCE_UUID = "fac39940-9c35-11e4-bd06-0800200c9a66";
    public static final String CDI_DOSSIER_NUMBER_PREFIX = "CDI";

    public static final String CANGE_DOSSIER_NUMBER_IDENTIFIER_SOURCE_UUID = "9dd9bdf3-4b57-47c3-b731-1000dbdef5d8";
    public static final String CANGE_DOSSIER_NUMBER_PREFIX = "CNG";

    public static final int LOCAL_ZL_IDENTIFIER_POOL_BATCH_SIZE = 1000;
    public static final int LOCAL_ZL_IDENTIFIER_POOL_MIN_POOL_SIZE = 2000;

    public static final String REMOTE_ZL_IDENTIFIER_SOURCE_URL = "http://localhost:8080/mirebalais/module/idgen/exportIdentifiers.form?source=3&comment=Testing+Mirebalais";
    public static final String REMOTE_ZL_IDENTIFIER_SOURCE_USERNAME = "testidgen";
    public static final String REMOTE_ZL_IDENTIFIER_SOURCE_PASSWORD = "Testing123";
    public static final String REMOTE_ZL_IDENTIFIER_SOURCE_UUID = "bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb";

    public static final String CURRENT_APP_SESSION_VARIABLE = "currentAppId";

    public static final String PATIENT_FAMILY_HISTORY_LIST_CONSTRUCT = "CIEL:160593";
    public static final String RELATIONSHIP_OF_RELATIVE_TO_PATIENT = "CIEL:1560";
    public static final String FAMILY_HISTORY_DIAGNOSIS = "CIEL:160592";
    public static final String FAMILY_HISTORY_COMMENT = "CIEL:160618";

    public static final String PAST_MEDICAL_HISTORY_CONSTRUCT = "CIEL:1633";
    public static final String PAST_MEDICAL_HISTORY_FINDING = "CIEL:1628";
    public static final String PAST_MEDICAL_HISTORY_FINDING_TEXT = "CIEL:160221";

    public static final String RELATIONSHIP_SIBLING = "8d91a01c-c2cc-11de-8d13-0010c6dffd0f";
    public static final String RELATIONSHIP_PARENT_CHILD = "8d91a210-c2cc-11de-8d13-0010c6dffd0f";
    public static final String RELATIONSHIP_AUNT_UNCLE = "8d91a3dc-c2cc-11de-8d13-0010c6dffd0f";
    public static final String RELATIONSHIP_NURSE_CHW = "9a4b3eea-8a9f-11e8-9a94-a6cf71072f73";
    public static final String RELATIONSHIP_CLINICIAN = "2fa6fcea-aa58-11e8-98d0-529269fb1459";

    public static final String COMMCARE_PROVIDER_UUID = "35711912-13A6-47F9-8D54-655FCAD75895";

    public static final String PATIENT_DIED_CONCEPT_UUID = "3cdd446a-26fe-102b-80cb-0017a47871b2";

    public static final String HAITI_HIV_EMR_LINK_URL_RUNTIME_PROPERTY = "haiti_hiv_emr_link_url";

    public static final String VACCINATION_GROUP_CONCEPT_UUID = "74260088-9c83-41d5-b92b-03a41654daaf";
    public static final String VACCINATION_TYPE_CONCEPT_UUID = "2dc6c690-a5fe-4cc4-97cc-32c70200a2eb";
    public static final String VACCINATION_NUM_CONCEPT_UUID = "ef6b45b4-525e-4d74-bf81-a65a41f3feb9";
    public static final String VACCINATION_DATE_CONCEPT_UUID = "1410AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";

    public static final String COVID_VACCINATION_CONCEPT_UUID = "c3cd46de-21fb-475b-b76a-1c638b250378";
    public static final String HEP_B_VACCINATION_CONCEPT_UUID = "3cd42a9c-26fe-102b-80cb-0017a47871b2";
    public static final String FLU_VACCINATION_CONCEPT_UUID = "78032AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
    public static final String MENINGO_VACCINATION_CONCEPT_UUID = "159900AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
    public static final String BCG_VACCINATION_CONCEPT_UUID = "3cd4e004-26fe-102b-80cb-0017a47871b2";
    public static final String POLIO_VACCINATION_CONCEPT_UUID = "3cd42c36-26fe-102b-80cb-0017a47871b2";
    public static final String PENTAVALENT_VACCINATION_CONCEPT_UUID = "1423AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
    public static final String ROTAVIRUS_VACCINATION_CONCEPT_UUID = "83531AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
    public static final String PNEUMOCOCCAL_VACCINATION_CONCEPT_UUID = "82213AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
    public static final String MEASLES_RUBELLA_VACCINATION_CONCEPT_UUID = "162586AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
    public static final String DIPTHERIA_TETANUS_VACCINATION_CONCEPT_UUID = "3ccc6b7c-26fe-102b-80cb-0017a47871b2";
}
