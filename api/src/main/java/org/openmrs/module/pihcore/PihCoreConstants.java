package org.openmrs.module.pihcore;

public class PihCoreConstants {

    public static final String HTMLFORMENTRY_CAUSE_OF_DEATH_LIST_TAG_NAME = "causeOfDeathList";
    public static final String HTMLFORMENTRY_PAST_MEDICAL_HISTORY_CHECKBOX_TAG_NAME = "pastMedicalHistoryCheckbox";
    public static final String HTMLFORMENTRY_FAMILY_HISTORY_RELATIVE_CHECKBOXES_TAG_NAME = "familyHistoryRelativeCheckboxes";

    // encounter types
    public static final String DEATH_CERTIFICATE_ENCOUNTER_TYPE_UUID = "1545d7ff-60f1-485e-9c95-5740b8e6634b";

    public static final String LOCAL_ZL_IDENTIFIER_GENERATOR_UUID = "52250ca2-d25f-11e4-8dbf-54ee7513a7ff";
    public static final String LOCAL_ZL_IDENTIFIER_POOL_UUID = "aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa";

    public static final String UHM_DOSSIER_NUMBER_IDENTIFIER_SOURCE_UUID = "cccccccc-cccc-cccc-cccc-cccccccccccc";
    public static final String UHM_DOSSIER_NUMBER_PREFIX = "A";

    public static final String ZL_DOSSIER_NUMBER_IDENTIFIER_SOURCE_UUID = "9dd9bdf3-4b57-47c3-b731-1000dbdef5d8";

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

}
