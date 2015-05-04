package org.openmrs.module.pihcore.deploy.bundle.core;

import org.openmrs.module.pihcore.deploy.bundle.PihMetadataBundle;
import org.springframework.stereotype.Component;

import static org.openmrs.module.metadatadeploy.bundle.CoreConstructors.encounterType;

@Component
public class EncounterTypeBundle extends PihMetadataBundle {

    public static final class EncounterTypes {
        public static final String PATIENT_REGISTRATION = "873f968a-73a8-4f9c-ac78-9f4778b751b6";
        public static final String CHECK_IN = "55a0d3ea-a4d7-4e88-8f01-5aceb2d3c61b";
        public static final String PAYMENT = "f1c286d0-b83f-4cd4-8348-7ea3c28ead13";
        public static final String VITALS = "4fb47712-34a6-40d2-8ed3-e153abbd25b7";
        public static final String PRIMARY_CARE_VISIT = "1373cf95-06e8-468b-a3da-360ac1cf026d";
        public static final String CONSULTATION = "92fd09b4-5335-4f7e-9f63-b2a663fd09a6";
        public static final String MEDICATION_DISPENSED = "8ff50dea-18a1-4609-b4c9-3f8f2d611b84";
        public static final String POST_OPERATIVE_NOTE = "c4941dee-7a9b-4c1c-aa6f-8193e9e5e4e5";
        public static final String TRANSFER = "436cfe33-6b81-40ef-a455-f134a9f7e580";
        public static final String ADMISSION = "260566e1-c909-4d61-a96f-c1019291a09d";
        public static final String CANCEL_ADMISSION = "edbb857b-e736-4296-9438-462b31f97ef9";
        public static final String EXIT_FROM_CARE = "b6631959-2105-49dd-b154-e1249e0fbcd7";
        public static final String PRIMARY_CARE_HISTORY = "ffa148de-2c88-4828-833e-f3788991543d";
        public static final String PRIMARY_CARE_EXAM = "0a9facff-fdc4-4aa9-aae0-8d7feaf5b3ef";
        public static final String CONSULTATION_PLAN = "e0aaa214-1d4b-442a-b527-144adf025299";
    }

    private static final String REGISTRATION_ENCOUNTER_NAME = "Enregistrement de patient";
    private static final String CHECK_IN_ENCOUNTER_NAME = "Inscription";
    private static final String PRIMARY_CARE_VISIT_ENCOUNTER_NAME = "Consultation soins de base";

    @Override
    public void install() throws Exception {
        install(encounterType(REGISTRATION_ENCOUNTER_NAME, "Patient registration -- normally a new patient", EncounterTypes.PATIENT_REGISTRATION));
        install(encounterType(CHECK_IN_ENCOUNTER_NAME, "Check-in encounter, formerly known as Primary care reception", EncounterTypes.CHECK_IN));
        install(encounterType(PRIMARY_CARE_VISIT_ENCOUNTER_NAME, "Primary care visit (In Kreyol, it&apos;s &apos;vizit swen primè&apos;)", EncounterTypes.PRIMARY_CARE_VISIT));
        install(encounterType("Rencontre de paiement", "Encounter used to capture patient payments", EncounterTypes.PAYMENT));
        install(encounterType("Signes vitaux", "Encounter where vital signs were captured, and triage may have been done, possibly for triage purposes, but a complete exam was not done.", EncounterTypes.VITALS));
        install(encounterType("Consultation", "Encounter where a full or abbreviated examination is done, leading to a presumptive or confirmed diagnosis", EncounterTypes.CONSULTATION));
        install(encounterType("Médicaments administrés", "When someone gets medicine from the pharmacy", EncounterTypes.MEDICATION_DISPENSED));
        install(encounterType("Note de chirurgie", "The surgeons&apos; notes after performing surgery", EncounterTypes.POST_OPERATIVE_NOTE));
        install(encounterType("Transfert", "Indicates that a patient is being transferred into a different department within the hospital. (Transfers out of the hospital should not use this encounter type.)", EncounterTypes.TRANSFER));
        install(encounterType("Admission aux soins hospitaliers", "Indicates that the patient has been admitted for inpatient care, and is not expected to leave the hospital unless discharged.", EncounterTypes.ADMISSION));
        install(encounterType("Annuler l'admission", "An encounter that notes that a request to admit a patient (via giving them a dispositon of &quot;admit&quot; on another form) is being overridden", EncounterTypes.CANCEL_ADMISSION));
        install(encounterType("Sortie de soins hospitaliers", "Indicates that a patient&apos;s inpatient care at the hospital is ending, and they are expected to leave soon", EncounterTypes.EXIT_FROM_CARE));
        install(encounterType("Antecedents", "Past medical history, for general primary care. Typically only captured at a patient's first visit", EncounterTypes.PRIMARY_CARE_HISTORY));
        install(encounterType("Examen", "Physical exam and diagnosis, typically captured at every clinical visit", EncounterTypes.PRIMARY_CARE_EXAM));
        install(encounterType("Conduite a tenir", "Orders placed during a consultation", EncounterTypes.CONSULTATION_PLAN));
    }

}
