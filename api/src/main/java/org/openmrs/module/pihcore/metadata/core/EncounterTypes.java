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

package org.openmrs.module.pihcore.metadata.core;


import org.openmrs.module.metadatadeploy.descriptor.EncounterTypeDescriptor;

/**
 * Constants for all defined encounter types
 */
public class EncounterTypes {

	// note that the Haiti Core module also installs this encounter type as well--we should try to keep these consistent!
	public static EncounterTypeDescriptor PATIENT_REGISTRATION  = new EncounterTypeDescriptor() {
		public String uuid() { return "873f968a-73a8-4f9c-ac78-9f4778b751b6"; }
		public String name() { return "Enregistrement de patient"; }
		public String description() { return "Patient registration -- normally a new patient"; }
	};

	public static EncounterTypeDescriptor CHECK_IN  = new EncounterTypeDescriptor() {
		public String uuid() { return "55a0d3ea-a4d7-4e88-8f01-5aceb2d3c61b"; }
		public String name() { return "Inscription"; }
		public String description() { return "Check-in encounter, formerly known as Primary care reception"; }
	};

	public static EncounterTypeDescriptor PAYMENT  = new EncounterTypeDescriptor() {
		public String uuid() { return "f1c286d0-b83f-4cd4-8348-7ea3c28ead13"; }
		public String name() { return "Rencontre de paiement"; }
		public String description() { return "Encounter used to capture patient payments"; }
	};

	public static EncounterTypeDescriptor VITALS  = new EncounterTypeDescriptor() {
		public String uuid() { return "4fb47712-34a6-40d2-8ed3-e153abbd25b7"; }
		public String name() { return "Signes vitaux"; }
		public String description() { return "Encounter where vital signs were captured, and triage may have been done, possibly for triage purposes, but a complete exam was not done."; }
	};

	// TODO can this be retired if we replace with speific consults?
	public static EncounterTypeDescriptor PRIMARY_CARE_VISIT  = new EncounterTypeDescriptor() {
		public String uuid() { return "1373cf95-06e8-468b-a3da-360ac1cf026d"; }
		public String name() { return "Consultation soins de base"; }
		public String description() { return "Primary care visit (In Kreyol, it&apos;s &apos;vizit swen primè&apos;)"; }
	};

	public static EncounterTypeDescriptor PRIMARY_CARE_PEDS_INITIAL_CONSULT = new EncounterTypeDescriptor() {
		public String uuid() { return "5b812660-0262-11e6-a837-0800200c9a66"; }
		public String name() { return "Primary Care Pediatric Initial Consult"; }
		public String description() { return "Primary Care Pediatric Initial Consult"; }
	};

	public static EncounterTypeDescriptor PRIMARY_CARE_PEDS_FOLLOWUP_CONSULT = new EncounterTypeDescriptor() {
		public String uuid() { return "229e5160-031b-11e6-a837-0800200c9a66"; }
		public String name() { return "Primary Care Pediatric Followup Consult"; }
		public String description() { return "Primary Care Pediatric Followup Consult"; }
	};

	public static EncounterTypeDescriptor PRIMARY_CARE_ADULT_INITIAL_CONSULT = new EncounterTypeDescriptor() {
		public String uuid() { return "27d3a180-031b-11e6-a837-0800200c9a66"; }
		public String name() { return "Primary Care Adult Initial Consult"; }
		public String description() { return "Primary Care Adult Initial Consult"; }
	};

	public static EncounterTypeDescriptor PRIMARY_CARE_ADULT_FOLLOWUP_CONSULT = new EncounterTypeDescriptor() {
		public String uuid() { return "27d3a181-031b-11e6-a837-0800200c9a66"; }
		public String name() { return "Primary Care Adult Followup Consult"; }
		public String description() { return "Primary Care Adult Followup Consult"; }
	};

	public static EncounterTypeDescriptor CONSULTATION  = new EncounterTypeDescriptor() {
		public String uuid() { return "92fd09b4-5335-4f7e-9f63-b2a663fd09a6"; }
		public String name() { return "Consultation"; }
		public String description() { return "Encounter where a full or abbreviated examination is done, leading to a presumptive or confirmed diagnosis"; }
	};

    public static EncounterTypeDescriptor ONCOLOGY_CONSULT  = new EncounterTypeDescriptor() {
        public String uuid() { return "035fb8da-226a-420b-8d8b-3904f3bedb25"; }
        public String name() { return "Oncology Consultation"; }
        public String description() { return "Consultation for Oncology"; }
    };

    public static EncounterTypeDescriptor ONCOLOGY_INITIAL_VISIT  = new EncounterTypeDescriptor() {
        public String uuid() { return "f9cfdf8b-d086-4658-9b9d-45a62896da03"; }
        public String name() { return "Oncology Initial Visit"; }
        public String description() { return "Intake for oncology patient"; }
    };

    public static EncounterTypeDescriptor CHEMOTHERAPY_SESSION  = new EncounterTypeDescriptor() {
        public String uuid() { return "828964fa-17eb-446e-aba4-e940b0f4be5b"; }
        public String name() { return "Chemotheraphy treatment session"; }
        public String description() { return "Chemotheraphy treatment session for HUM and other places."; }
    };

	public static EncounterTypeDescriptor MEDICATION_DISPENSED  = new EncounterTypeDescriptor() {
		public String uuid() { return "8ff50dea-18a1-4609-b4c9-3f8f2d611b84"; }
		public String name() { return "Médicaments administrés"; }
		public String description() { return "When someone gets medicine from the pharmacy"; }
	};

	public static EncounterTypeDescriptor POST_OPERATIVE_NOTE  = new EncounterTypeDescriptor() {
		public String uuid() { return "c4941dee-7a9b-4c1c-aa6f-8193e9e5e4e5"; }
		public String name() { return "Note de chirurgie"; }
		public String description() { return "The surgeons&apos; notes after performing surgery"; }
	};

	public static EncounterTypeDescriptor TRANSFER  = new EncounterTypeDescriptor() {
		public String uuid() { return "436cfe33-6b81-40ef-a455-f134a9f7e580"; }
		public String name() { return "Transfert"; }
		public String description() { return "Indicates that a patient is being transferred into a different department within the hospital. (Transfers out of the hospital should not use this encounter type.)"; }
	};

	public static EncounterTypeDescriptor ADMISSION  = new EncounterTypeDescriptor() {
		public String uuid() { return "260566e1-c909-4d61-a96f-c1019291a09d"; }
		public String name() { return "Admission aux soins hospitaliers"; }
		public String description() { return "Indicates that the patient has been admitted for inpatient care, and is not expected to leave the hospital unless discharged."; }
	};

	public static EncounterTypeDescriptor CANCEL_ADMISSION  = new EncounterTypeDescriptor() {
		public String uuid() { return "edbb857b-e736-4296-9438-462b31f97ef9"; }
		public String name() { return "Annuler l'admission"; }
		public String description() { return "An encounter that notes that a request to admit a patient (via giving them a dispositon of &quot;admit&quot; on another form) is being overridden"; }
	};

	public static EncounterTypeDescriptor EXIT_FROM_CARE  = new EncounterTypeDescriptor() {
		public String uuid() { return "b6631959-2105-49dd-b154-e1249e0fbcd7"; }
		public String name() { return "Sortie de soins hospitaliers"; }
		public String description() { return "Indicates that a patient&apos;s inpatient care at the hospital is ending, and they are expected to leave soon"; }
	};


    public static EncounterTypeDescriptor LAB_RESULTS = new EncounterTypeDescriptor() {
        public String uuid() { return "4d77916a-0620-11e5-a6c0-1697f925ec7b"; }
        public String name() { return "Laboratory Results"; }
        public String description() { return "Laboratory Results "; }
    };

	public static EncounterTypeDescriptor RADIOLOGY_ORDER  = new EncounterTypeDescriptor() {
		public String uuid() { return "1b3d1e13-f0b1-4b83-86ea-b1b1e2fb4efa"; }
		public String name() { return "Commande de radio"; }
		public String description() { return "Radiology Order  - the ordering of a radiology exam"; }
	};

	public static EncounterTypeDescriptor RADIOLOGY_STUDY  = new EncounterTypeDescriptor() {
		public String uuid() { return "5b1b4a4e-0084-4137-87db-dba76c784439"; }
		public String name() { return "Examen de radiologie"; }
		public String description() { return "Radiology Study - represents performance of a radiology study on a patient by a radiology technician"; }
	};

	public static EncounterTypeDescriptor RADIOLOGY_REPORT  = new EncounterTypeDescriptor() {
		public String uuid() { return "d5ca53a7-d3b5-44ac-9aa2-1491d2a4b4e9"; }
		public String name() { return "Rapport de radiologie"; }
		public String description() { return "Radiology Report - represents a report on a radiology study performed by a radiologist"; }
	};

	public static EncounterTypeDescriptor DEATH_CERTIFICATE = new EncounterTypeDescriptor() {
		public String uuid() { return "1545d7ff-60f1-485e-9c95-5740b8e6634b"; }
		public String name() { return "Death Certificate"; }
		public String description() { return "The official record of a patient's death. A patient may be dead without having one of these encounters, but no living patient should have an encounter with this type"; }
	};

    public static EncounterTypeDescriptor NCD_ADULT_INITIAL_CONSULT = new EncounterTypeDescriptor() {
        public String uuid() { return "ae06d311-1866-455b-8a64-126a9bd74171"; }
        public String name() { return "NCD Adult Initial Consult"; }
        public String description() { return "Non-communicable disease initial consult for adults"; }
    };

	public static EncounterTypeDescriptor NCD_ADULT_FOLLOWUP_CONSULT = new EncounterTypeDescriptor() {
		public String uuid() { return "5cbfd6a2-92d9-4ad0-b526-9d29bfe1d10c"; }
		public String name() { return "NCD Adult Followup Consult"; }
		public String description() { return "Non-communicable disease followup consult for adults"; }
	};

	public static EncounterTypeDescriptor MENTAL_HEALTH_ASSESSMENT = new EncounterTypeDescriptor() {
		public String uuid() { return "a8584ab8-cc2a-11e5-9956-625662870761"; }
		public String name() { return "Mental Health Consult"; }
		public String description() { return "Mental health visit and assessment"; }
	};

	public static EncounterTypeDescriptor EMERGENCY_TRIAGE = new EncounterTypeDescriptor() {
		public String uuid() { return "74cef0a6-2801-11e6-b67b-9e71128cae77"; }
		public String name() { return "Emergency Triage"; }
		public String description() { return "Emergency Department patient triage"; }
	};

	public static EncounterTypeDescriptor TEST_ORDER  = new EncounterTypeDescriptor() {
		public String uuid() { return "b3a0e3ad-b80c-4f3f-9626-ace1ced7e2dd"; }
		public String name() { return "Test Order"; }
		public String description() { return "Test Order - the order of test (labs, biopsy, etc)"; }
	};

	public static EncounterTypeDescriptor SPECIMEN_COLLECTION  = new EncounterTypeDescriptor() {
		public String uuid() { return "10db3139-07c0-4766-b4e5-a41b01363145"; }
		public String name() { return "Specimen Collection"; }
		public String description() { return "Specimen Collection - the collection of specimen for a test (blood draw, biopsy, etc)"; }
	};

	public static EncounterTypeDescriptor ZL_ADULT_HIV_INTAKE  = new EncounterTypeDescriptor() {
		public String uuid() { return "c31d306a-40c4-11e7-a919-92ebcb67fe33"; }
		public String name() { return "ZL VIH Données de Base Adultes"; }
		public String description() { return "ZL VIH Données de Base Adultes (HIV Intake adult)"; }
	};

	public static EncounterTypeDescriptor ZL_ADULT_HIV_FOLLOWUP  = new EncounterTypeDescriptor() {
		public String uuid() { return "c31d3312-40c4-11e7-a919-92ebcb67fe33"; }
		public String name() { return "ZL VIH Rendez-vous Adultes"; }
		public String description() { return "ZL VIH Rendez-vous Adultes (HIV Followup adult"; }
	};

	public static EncounterTypeDescriptor ZL_PEDS_HIV_INTAKE  = new EncounterTypeDescriptor() {
		public String uuid() { return "c31d3416-40c4-11e7-a919-92ebcb67fe33"; }
		public String name() { return "ZL VIH Données de Base Pédiatriques"; }
		public String description() { return "VIH Données de Base Pédiatriques (HIV intake child)"; }
	};

	public static EncounterTypeDescriptor ZL_PEDS_HIV_FOLLOWUP  = new EncounterTypeDescriptor() {
		public String uuid() { return "c31d34f2-40c4-11e7-a919-92ebcb67fe33"; }
		public String name() { return "ZL VIH Rendez-vous Pédiatriques"; }
		public String description() { return "Fiche de suivi des visites cliniques - Enfants VIH+ (HIV followup child)"; }
	};

	// the following have been deprecated and should eventually be able to be removed--they were encounter types we were developing for the new visit note
	// but have decided to use a single encounter for the visit (with the primary care visit encounter type)

	@Deprecated
    public static EncounterTypeDescriptor PRIMARY_CARE_DISPOSITION = new EncounterTypeDescriptor() {
        public String uuid() { return "5C16E1D6-8E73-47E4-A861-D6AAC03E2224"; }
        public String name() { return "Primary Care Disposition"; }
        public String description() { return "Indicates the disposition of the primary care visit"; }
    };

	@Deprecated
    public static EncounterTypeDescriptor PRIMARY_CARE_PEDS_FEEDING = new EncounterTypeDescriptor() {
        public String uuid() { return "92DBE011-67CA-4C0C-80DB-D38989E554C9"; }
        public String name() { return "Primary Care Pediatric Feeding"; }
        public String description() { return "Indicates the current feeding"; }
    };

	@Deprecated
    public static EncounterTypeDescriptor PRIMARY_CARE_PEDS_SUPPLEMENTS = new EncounterTypeDescriptor() {
        public String uuid() { return "D25FFD97-417F-46CC-85EE-3E7DA68B0D07"; }
        public String name() { return "Primary Care Pediatric Supplements"; }
        public String description() { return "Indicates the supplements taken by a pediatric patient"; }
    };

	@Deprecated
	public static EncounterTypeDescriptor PRIMARY_CARE_HISTORY  = new EncounterTypeDescriptor() {
		public String uuid() { return "ffa148de-2c88-4828-833e-f3788991543d"; }
		public String name() { return "Antecedents"; }
		public String description() { return "Past medical history, for general primary care. Typically only captured at a patient's first visit"; }
	};

	@Deprecated
	public static EncounterTypeDescriptor PRIMARY_CARE_EXAM  = new EncounterTypeDescriptor() {
		public String uuid() { return "0a9facff-fdc4-4aa9-aae0-8d7feaf5b3ef"; }
		public String name() { return "Examen"; }
		public String description() { return "Physical exam, typically captured at every clinical visit"; }
	};

	@Deprecated
	public static EncounterTypeDescriptor PRIMARY_CARE_DIAGNOSIS  = new EncounterTypeDescriptor() {
		public String uuid() { return "09febbd8-03f1-11e5-8418-1697f925ec7b"; }
		public String name() { return "Diagnostic"; }
		public String description() { return "Diagnosis, typically captured at every clinical visit"; }
	};

	@Deprecated
	public static EncounterTypeDescriptor CONSULTATION_PLAN  = new EncounterTypeDescriptor() {
		public String uuid() { return "e0aaa214-1d4b-442a-b527-144adf025299"; }
		public String name() { return "Conduite a tenir"; }
		public String description() { return "Orders placed during a consultation"; }
	};

	// iSantePlus / MSPP encounter types
	public static EncounterTypeDescriptor ADULT_HIV_INTAKE  = new EncounterTypeDescriptor() {
		public String uuid() { return "17536ba6-dd7c-4f58-8014-08c7cb798ac7"; }
		public String name() { return "Saisie Première pour le VIH"; }
		public String description() { return "iSantePlus Saisie Première visite Adulte VIH"; }
	};

	public static EncounterTypeDescriptor ADULT_HIV_FOLLOWUP  = new EncounterTypeDescriptor() {
		public String uuid() { return "204ad066-c5c2-4229-9a62-644bc5617ca2"; }
		public String name() { return "Suivi Visite pour le VIH"; }
		public String description() { return "iSantePlus Saisie visite suivi Adulte VIH"; }
	};

	public static EncounterTypeDescriptor PEDS_HIV_INTAKE  = new EncounterTypeDescriptor() {
		public String uuid() { return "349ae0b4-65c1-4122-aa06-480f186c8350"; }
		public String name() { return "Saisie Première pour le VIH (pédiatrique)"; }
		public String description() { return "iSantePlus Saisie Première visite Pédiatrique VIH"; }
	};

	public static EncounterTypeDescriptor PEDS_HIV_FOLLOWUP  = new EncounterTypeDescriptor() {
		public String uuid() { return "33491314-c352-42d0-bd5d-a9d0bffc9bf1"; }
		public String name() { return "Suivi visite pour le VIH (pédiatrique)"; }
		public String description() { return "iSantePlus Saisie visite Suivi pédiatrique VIH"; }
	};

	public static EncounterTypeDescriptor ART_ADHERENCE  = new EncounterTypeDescriptor() {
		public String uuid() { return "c45d7299-ad08-4cb5-8e5d-e0ce40532939"; }
		public String name() { return "ART Adhérence"; }
		public String description() { return "iSantePlus Conseils ART d'Adhérence"; }
	};

}