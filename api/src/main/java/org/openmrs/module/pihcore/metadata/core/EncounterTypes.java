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

    public static EncounterTypeDescriptor NCD_INITIAL_CONSULT = new EncounterTypeDescriptor() {
        public String uuid() { return "ae06d311-1866-455b-8a64-126a9bd74171"; }
        public String name() { return "NCD Initial Consult"; }
        public String description() { return "Non-communicable disease initial consult"; }
    };

	public static EncounterTypeDescriptor NCD_FOLLOWUP_CONSULT = new EncounterTypeDescriptor() {
		public String uuid() { return "5cbfd6a2-92d9-4ad0-b526-9d29bfe1d10c"; }
		public String name() { return "NCD Followup Consult"; }
		public String description() { return "Non-communicable disease followup consult"; }
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

	public static EncounterTypeDescriptor PATHOLOGY_SPECIMEN_COLLECTION = new EncounterTypeDescriptor() {
		public String uuid() { return "10db3139-07c0-4766-b4e5-a41b01363145"; }
		public String name() { return "Pathology Specimen Collection"; }
		public String description() { return "Pathology Specimen Collection - the collection of a pathology specimen for a test (blood draw, biopsy, etc)"; }
	};

	public static EncounterTypeDescriptor LAB_SPECIMEN_COLLECTION = new EncounterTypeDescriptor() {
		public String uuid() { return "39C09928-0CAB-4DBA-8E48-39C631FA4286"; }
		public String name() { return "Lab Specimen Collection"; }
		public String description() { return "Lab Specimen Collection - the collection of a lab specimen for a test (blood draw, biopsy, etc)"; }
	};

	public static EncounterTypeDescriptor HIV_INTAKE = new EncounterTypeDescriptor() {
		public String uuid() { return "c31d306a-40c4-11e7-a919-92ebcb67fe33"; }
		public String name() { return "HIV Intake"; }
		public String description() { return "HIV Intake"; }
	};

	public static EncounterTypeDescriptor HIV_FOLLOWUP = new EncounterTypeDescriptor() {
		public String uuid() { return "c31d3312-40c4-11e7-a919-92ebcb67fe33"; }
		public String name() { return "HIV Followup"; }
		public String description() { return "HIV Followup"; }
	};

	public static EncounterTypeDescriptor VCT  = new EncounterTypeDescriptor() {
		public String uuid() { return "616b66fe-f189-11e7-8c3f-9a214cf093ae"; }
		public String name() { return "Voluntary counselling and testing"; }
		public String description() { return "Voluntary counselling and testing (VCT) for HIV"; }
	};

	public static EncounterTypeDescriptor HIV_DISPENSING  = new EncounterTypeDescriptor() {
		public String uuid() { return "cc1720c9-3e4c-4fa8-a7ec-40eeaad1958c"; }
		public String name() { return "HIV drug dispensing"; }
		public String description() { return "Dispensing for HIV medications"; }
	};

	public static EncounterTypeDescriptor DRUG_ORDER_DOCUMENTATION  = new EncounterTypeDescriptor() {
		public String uuid() { return "0b242b71-5b60-11eb-8f5a-0242ac110002"; }
		public String name() { return "Drug Order Documentation"; }
		public String description() { return "Documentation of drug orders for a patient, often dated retrospectively"; }
	};

	public static EncounterTypeDescriptor SOCIO_ECONOMICS  = new EncounterTypeDescriptor() {
		public String uuid() { return "de844e58-11e1-11e8-b642-0ed5f89f718b"; }
		public String name() { return "Socio-economics"; }
		public String description() { return "Education and housing information on the patient"; }
	};

	public static EncounterTypeDescriptor ANC_INTAKE  = new EncounterTypeDescriptor() {
		public String uuid() { return "00e5e810-90ec-11e8-9eb6-529269fb1459"; }
		public String name() { return "ANC Intake"; }
		public String description() { return "Initial prenatal (aka ANC) visit for pregnant mother"; }
	};

	public static EncounterTypeDescriptor ANC_FOLLOWUP  = new EncounterTypeDescriptor() {
		public String uuid() { return "00e5e946-90ec-11e8-9eb6-529269fb1459"; }
		public String name() { return "ANC Followup"; }
		public String description() { return "Followup prenatal (aka ANC) visits for pregnant mother"; }
	};

	public static EncounterTypeDescriptor MCH_DELIVERY  = new EncounterTypeDescriptor() {
		public String uuid() { return "00e5ebb2-90ec-11e8-9eb6-529269fb1459"; }
		public String name() { return "MCH Delivery"; }
		public String description() { return "Mother's visit for delivery of baby"; }
	};

	public static EncounterTypeDescriptor OB_GYN  = new EncounterTypeDescriptor() {
		public String uuid() { return "d83e98fd-dc7b-420f-aa3f-36f648b4483d"; }
		public String name() { return "OB/GYN"; }
		public String description() { return "Obstetrics and gynecology encounter for woman"; }
	};

	public static EncounterTypeDescriptor VACCINATION  = new EncounterTypeDescriptor() {
		public String uuid() { return "1e2a509c-7c9f-11e9-8f9e-2a86e4085a59"; }
		public String name() { return "Vaccination"; }
		public String description() { return "Vaccination form only (not within another encounter)"; }
	};

	public static EncounterTypeDescriptor ECHOCARDIOGRAM  = new EncounterTypeDescriptor() {
		public String uuid() { return "fdee591e-78ba-11e9-8f9e-2a86e4085a59"; }
		public String name() { return "Echocardiogram"; }
		public String description() { return "Echocardiogram consultation"; }
	};

	public static EncounterTypeDescriptor PRENATAL_HOME_ASSESSMENT = new EncounterTypeDescriptor() {
		public String uuid() { return "91DDF969-A2D4-4603-B979-F2D6F777F4AF"; }
		public String name() { return "Prenatal Home Assessment"; }
		public String description() { return "Prenatal home assessment"; }
	};

	public static EncounterTypeDescriptor PEDIATRIC_HOME_ASSESSMENT = new EncounterTypeDescriptor() {
		public String uuid() { return "0CF4717A-479F-4349-AE6F-8602E2AA41D3"; }
		public String name() { return "Pediatric Home Assessment"; }
		public String description() { return "Pediatric home assessment"; }
	};

	public static EncounterTypeDescriptor MATERNAL_POST_PARTUM_HOME_ASSESSMENT = new EncounterTypeDescriptor() {
		public String uuid() { return "0E7160DF-2DD1-4728-B951-641BBE4136B8"; }
		public String name() { return "Maternal Post-partum Home Assessment"; }
		public String description() { return "Maternal post-partum home assessment"; }
	};

	public static EncounterTypeDescriptor MATERNAL_FOLLOWUP_HOME_ASSESSMENT  = new EncounterTypeDescriptor() {
		public String uuid() { return "690670E2-A0CC-452B-854D-B95E2EAB75C9"; }
		public String name() { return "Maternal Follow-up Home Assessment"; }
		public String description() { return "Maternal follow-up home assessment"; }
	};

	public static EncounterTypeDescriptor COVID19_INTAKE  = new EncounterTypeDescriptor() {
		public String uuid() { return "8d50b938-dcf9-4b8e-9938-e625bd2f0a81"; }
		public String name() { return "COVID-19 Admission"; }
		public String description() { return "COVID-19 admission note at health facility"; }
	};

	public static EncounterTypeDescriptor COVID19_FOLLOWUP  = new EncounterTypeDescriptor() {
		public String uuid() { return "ca65f5d3-6312-4143-ae4e-0237427f339e"; }
		public String name() { return "COVID-19 Progress"; }
		public String description() { return "COVID-19 follow-up/daily progress at health facility"; }
	};

	public static EncounterTypeDescriptor COVID19_DISCHARGE  = new EncounterTypeDescriptor() {
		public String uuid() { return "5e82bea0-fd7b-47f9-858a-91be87521073"; }
		public String name() { return "COVID-19 Discharge"; }
		public String description() { return "COVID-19 Discharge note at health facility"; }
	};

	public static EncounterTypeDescriptor OVC_INTAKE  = new EncounterTypeDescriptor() {
		public String uuid() { return "651d4359-4463-4e52-8fde-e62876f90792"; }
		public String name() { return "OVC Intake"; }
		public String description() { return "USAID Orphans and Vulnerable Children (OVC) program intake"; }
	};

	public static EncounterTypeDescriptor OVC_FOLLOWUP  = new EncounterTypeDescriptor() {
		public String uuid() { return "f8d426fd-132a-4032-93da-1213c30e2b74"; }
		public String name() { return "OVC Follow-up"; }
		public String description() { return "USAID Orphans and Vulnerable Children (OVC) program follow-up"; }
	};

	public static EncounterTypeDescriptor TB_INTAKE  = new EncounterTypeDescriptor() {
		public String uuid() { return "aa42cc6c-b9ee-4850-926c-dda4bb14d890"; }
		public String name() { return "Tuberculosis Intake"; }
		public String description() { return "Tuberculosis (TB) intake form"; }
	};

	public static EncounterTypeDescriptor COMMENT = new EncounterTypeDescriptor() {
		public String uuid() { return "c30d6e06-0f00-460a-8f81-3c39a1853b56"; }
		public String name() { return "Comment"; }
		public String description() { return "Free-text clinical impression comments"; }
	};

	public static EncounterTypeDescriptor HIV_INFANT_DOCUMENTATION = new EncounterTypeDescriptor() {
		public String uuid() { return "00DA14B9-7066-45A7-8FEC-0CAD60D1EBD1"; }
		public String name() { return "HIV Infant Documentation"; }
		public String description() { return "HIV Infant Documentation encounter type"; }
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
	public static EncounterTypeDescriptor MEXICO_CLINIC_VISIT =  new EncounterTypeDescriptor() {
		public String uuid() { return "b29cec8c-b21c-4c95-bfed-916a51db2a26"; }
		public String name() { return "Clinic Visit"; }
		public String description() { return "A visit to one of our primary care clinics"; }
	};

	@Deprecated
	public static EncounterTypeDescriptor CONSULTATION_PLAN  = new EncounterTypeDescriptor() {
		public String uuid() { return "e0aaa214-1d4b-442a-b527-144adf025299"; }
		public String name() { return "Conduite a tenir"; }
		public String description() { return "Orders placed during a consultation"; }
	};

	@Deprecated
	public static EncounterTypeDescriptor ZL_PEDS_HIV_INTAKE  = new EncounterTypeDescriptor() {
		public String uuid() { return "c31d3416-40c4-11e7-a919-92ebcb67fe33"; }
		public String name() { return "ZL VIH Données de Base Pédiatriques"; }
		public String description() { return "VIH Données de Base Pédiatriques (HIV intake child)"; }
	};

	@Deprecated
	public static EncounterTypeDescriptor ZL_PEDS_HIV_FOLLOWUP  = new EncounterTypeDescriptor() {
		public String uuid() { return "c31d34f2-40c4-11e7-a919-92ebcb67fe33"; }
		public String name() { return "ZL VIH Rendez-vous Pédiatriques"; }
		public String description() { return "Fiche de suivi des visites cliniques - Enfants VIH+ (HIV followup child)"; }
	};

	@Deprecated
	public static EncounterTypeDescriptor ADULT_HIV_INTAKE  = new EncounterTypeDescriptor() {
		public String uuid() { return "17536ba6-dd7c-4f58-8014-08c7cb798ac7"; }
		public String name() { return "Saisie Première pour le VIH"; }
		public String description() { return "iSantePlus Saisie Première visite Adulte VIH"; }
	};

	@Deprecated
	public static EncounterTypeDescriptor ADULT_HIV_FOLLOWUP  = new EncounterTypeDescriptor() {
		public String uuid() { return "204ad066-c5c2-4229-9a62-644bc5617ca2"; }
		public String name() { return "Suivi Visite pour le VIH"; }
		public String description() { return "iSantePlus Saisie visite suivi Adulte VIH"; }
	};

	@Deprecated
	public static EncounterTypeDescriptor PEDS_HIV_INTAKE  = new EncounterTypeDescriptor() {
		public String uuid() { return "349ae0b4-65c1-4122-aa06-480f186c8350"; }
		public String name() { return "Saisie Première pour le VIH (pédiatrique)"; }
		public String description() { return "iSantePlus Saisie Première visite Pédiatrique VIH"; }
	};

	@Deprecated
	public static EncounterTypeDescriptor PEDS_HIV_FOLLOWUP  = new EncounterTypeDescriptor() {
		public String uuid() { return "33491314-c352-42d0-bd5d-a9d0bffc9bf1"; }
		public String name() { return "Suivi visite pour le VIH (pédiatrique)"; }
		public String description() { return "iSantePlus Saisie visite Suivi pédiatrique VIH"; }
	};

	@Deprecated
	public static EncounterTypeDescriptor ART_ADHERENCE  = new EncounterTypeDescriptor() {
		public String uuid() { return "c45d7299-ad08-4cb5-8e5d-e0ce40532939"; }
		public String name() { return "ART Adhérence"; }
		public String description() { return "iSantePlus Conseils ART d'Adhérence"; }
	};

}
