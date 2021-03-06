package org.openmrs.module.pihcore;

public class PihCoreConstants {

    public static final String GP_RUN_CONCEPT_SETUP_TASK_IN_SEPARATE_THREAD = "pihcore.runMetadataSetupTaskInSeparateThread";

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

    public static final int LOCAL_ZL_IDENTIFIER_POOL_BATCH_SIZE = 1000;
    public static final int LOCAL_ZL_IDENTIFIER_POOL_MIN_POOL_SIZE = 2000;

    public static final String REMOTE_ZL_IDENTIFIER_SOURCE_URL = "http://localhost:8080/mirebalais/module/idgen/exportIdentifiers.form?source=3&comment=Testing+Mirebalais";
    public static final String REMOTE_ZL_IDENTIFIER_SOURCE_USERNAME = "testidgen";
    public static final String REMOTE_ZL_IDENTIFIER_SOURCE_PASSWORD = "Testing123";


    public static final String CDI_DOSSIER_NUMBER_IDENTIFIER_SOURCE_UUID = "fac39940-9c35-11e4-bd06-0800200c9a66";
    public static final String CDI_DOSSIER_NUMBER_PREFIX = "CDI";
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

}
