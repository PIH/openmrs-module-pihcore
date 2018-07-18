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

    // uuids of concepts associated with programs
    public static final String ZIKA_PROGRAM_CONCEPT_UUID = "58c9ab03-9601-4d88-88b7-c4932004374e";
    public static final String ZIKA_PROGRAM_OUTCOMES_CONCEPT_UUID = "e39310c1-2936-4395-83a5-f4d49061a117";

    public static final String MENTAL_HEALTH_PROGRAM_CONCEPT_UUID = "d3368ded-6d96-4bfc-b590-cb663b7ec70b";
    public static final String MENTAL_HEALTH_PROGRAM_OUTCOMES_CONCEPT_UUID = "73eb05c2-e4be-4d82-bcad-ffec1be67d01";

    public static final String HIV_PROGRAM_CONCEPT_UUID = "3cdb4962-26fe-102b-80cb-0017a47871b2";
    public static final String HIV_PROGRAM_OUTCOMES_CONCEPT_UUID = "e65f5aaa-ba76-4a6c-ab38-07c9bf831892";
    public static final String HIV_PROGRAM_TREATMENT_STATUS_UUID = "37c7cf83-bce6-469c-acab-6a90e63264d2";
    public static final String HIV_PROGRAM_TRANSITION_UUID = "f3ebbcac-b614-4e65-becc-816079552cf5";
    public static final String HIV_PROGRAM_ON_ART_UUID = "3cdc0a8c-26fe-102b-80cb-0017a47871b2";

    public static final String NCD_PROGRAM_CONCEPT_UUID = "099d3ba9-b302-4565-a060-09915f3c85b5";
    public static final String NCD_PROGRAM_OUTCOMES_CONCEPT_UUID = "73eb05c2-e4be-4d82-bcad-ffec1be67d01";
    public static final String NCD_PROGRAM_STATUS_UUID = "83a258b8-3294-4746-acb6-f045cccd37e8";
    public static final String PROGRAM_STABLE_STATE_UUID = "df3e1542-035a-404f-84d6-8cb9b4266551";
    public static final String PROGRAM_UNSTABLE_STATE_UUID = "c799a966-e204-4d81-8bc6-31b3310ece5a";
    public static final String PROGRAM_COMPLICATED_STATE_UUID = "84d0a344-2725-437c-a9d7-e86be508c13c";

    // ToDo: Define the NCD workflow/state
    // public static final String NCD_PROGRAM_TREATMENT_STATUS_CONCEPT_UUID = "";

    public static final String ONCOLOGY_PROGRAM_CONCEPT_UUID = "fddbc14c-a11d-4526-8846-83d7b6425f19";
    public static final String ONCOLOGY_PROGRAM_OUTCOME_CONCEPT_UUID = "abc0e2ca-9175-411b-973c-7e6ee91aa2f3";
    public static final String ONCOLOGY_PROGRESS_STATUS_UUID = "6271c622-1909-41ca-a4ce-a6e31fae0d17";
    public static final String THERAPY_STATE_UUID = "324fdca6-6d0d-4898-89fd-2a3fa5a52bd5";
    public static final String SURVEILLANCE_STATE_UUID = "651f2289-9a4e-404b-8c93-894ab7349da5";
    public static final String FOLLOWUP_STATE_UUID = "3ce451d8-26fe-102b-80cb-0017a47871b2";
    public static final String PALLIATIVE_ONLY_STATE_UUID = "6bd18154-242a-44e9-8952-95eb837fe3fc";
    public static final String ONCOLOGY_TREATMENT_STATUS_UUID = "7883b7c3-02f7-485f-8b7c-555e08d13030";
    public static final String CURATIVE_STATE_UUID = "160849AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
    public static final String PALLIATIVE_STATE_UUID = "160847AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
    public static final String OTHER_STATE_UUID = "3cee7fb4-26fe-102b-80cb-0017a47871b2";

    public static final String GROUP1_UUID = "3cdf67d6-26fe-102b-80cb-0017a47871b2";
    public static final String GROUP2_UUID = "3cdf7f1e-26fe-102b-80cb-0017a47871b2";
    public static final String MOTHER_GROUP_UUID = "e219babf-faca-4d16-9207-c3e99d58a428";
    public static final String CHILD_GROUP_UUID = "68b4ecc7-f3c4-4f3f-a6e2-556cb33f463b";
    public static final String MCH_PROGRAM_CONCEPT_UUID = "159937AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
    public static final String MCH_PROGRAM_OUTCOME_CONCEPT_UUID = "73eb05c2-e4be-4d82-bcad-ffec1be67d01";
}