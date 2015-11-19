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

package org.openmrs.module.pihcore.metadata.haiti.mirebalais;

import org.openmrs.module.metadatadeploy.descriptor.LocationAttributeDescriptor;
import org.openmrs.module.metadatadeploy.descriptor.LocationDescriptor;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Constants for all defined locations
 */
public class MirebalaisLocations {

	//***** MIREBALAIS and CDI LOCATIONS *****

	public static LocationDescriptor MIREBALAIS_CDI_PARENT = new LocationDescriptor() {
		public String uuid() { return "a084f714-a536-473b-94e6-ec317b152b43"; }
		public String name() { return "Mirebalais"; }
		public String description() { return "Mirebalais"; }
		public List<LocationAttributeDescriptor> attributes() { return Collections.singletonList(MirebalaisLocationAttributes.MIREBALAIS_CDI_PARENT_NAME_TO_PRINT_ON_ID_CARD); }
	};

	//***** MIREBALAIS LOCATIONS *****

	public static LocationDescriptor MIREBALAIS_HOSPITAL = new LocationDescriptor() {
		public String uuid() { return "24bd1390-5959-11e4-8ed6-0800200c9a66"; }
		public String name() { return "Hôpital Universitaire de Mirebalais"; }
		public String description() { return "Mirebalais (MoH code 62101)"; }
		public LocationDescriptor parent() { return MirebalaisLocations.MIREBALAIS_CDI_PARENT; }
		public List<LocationAttributeDescriptor> attributes() { return Collections.singletonList(MirebalaisLocationAttributes.MIREBALAIS_HOSPITAL_NAME_TO_PRINT_ON_ID_CARD); }
	};

	public static LocationDescriptor ANTEPARTUM_WARD = new LocationDescriptor() {
		public String uuid() { return "272bd989-a8ee-4a16-b5aa-55bad4e84f5c"; }
		public String name() { return "Sal Avan Akouchman"; }
		public String description() { return "Antepartum ward at Mirebalais Hospital (Before giving birth)"; }
		public LocationDescriptor parent() { return MirebalaisLocations.MIREBALAIS_HOSPITAL; }
		public List<LocationAttributeDescriptor> attributes() { return Collections.singletonList(MirebalaisLocationAttributes.ANTEPARTUM_WARD_LOCATION_CODE); }
	};

	public static LocationDescriptor BLOOD_BANK = new LocationDescriptor() {
		public String uuid() { return "4ed8c0d3-8aed-4f80-96e8-55648abf51af"; }
		public String name() { return "Bank Pou San"; }
		public String description() { return "Blood Bank at Mirebalais Hospital"; }
		public LocationDescriptor parent() { return MirebalaisLocations.MIREBALAIS_HOSPITAL; }
		public List<LocationAttributeDescriptor> attributes() { return Collections.singletonList(MirebalaisLocationAttributes.BLOOD_BANK_LOCATION_CODE); }
	};

	public static LocationDescriptor CENTRAL_ARCHIVES = new LocationDescriptor() {
		public String uuid() { return "be50d584-26b2-4371-8768-2b9565742b3b"; }
		public String name() { return "Achiv Santral"; }
		public String description() { return "Central Archives room at Mirebalais Hospital"; }
		public LocationDescriptor parent() { return MirebalaisLocations.MIREBALAIS_HOSPITAL; }
		public List<LocationAttributeDescriptor> attributes() { return Collections.singletonList(MirebalaisLocationAttributes.CENTRAL_ARCHIVES_LOCATION_CODE); }
	};

	public static LocationDescriptor CHEMOTHERAPY = new LocationDescriptor() {
		public String uuid() { return "dc8413be-1075-48b5-9857-9bd4954686ed"; }
		public String name() { return "Chimyoterapi"; }
		public String description() { return "Chemotherapy at Mirebalais Hospital"; }
		public LocationDescriptor parent() { return MirebalaisLocations.MIREBALAIS_HOSPITAL; }
		public List<LocationAttributeDescriptor> attributes() { return Collections.singletonList(MirebalaisLocationAttributes.CHEMOTHERAPY_LOCATION_CODE); }
	};

	public static LocationDescriptor CLINIC_REGISTRATION = new LocationDescriptor() {
		public String uuid() { return "787a2422-a7a2-400e-bdbb-5c54b2691af5"; }
		public String name() { return "Biwo Resepsyon"; }
		public String description() { return "Clinic Registration at Mirebalais Hospital"; }
		public LocationDescriptor parent() { return MirebalaisLocations.MIREBALAIS_HOSPITAL; }
		public List<LocationAttributeDescriptor> attributes() { return Collections.singletonList(MirebalaisLocationAttributes.CLINIC_REGISTRATION_LOCATION_CODE); }
	};

	public static LocationDescriptor COMMUNITY_HEALTH = new LocationDescriptor() {
		public String uuid() { return "dce85b2e-6946-4798-9037-d00f123df7bd"; }
		public String name() { return "Sante Kominotè"; }
		public String description() { return "Community Health clinic at Mirebalais Hospital (Community Health clinic)."; }
		public LocationDescriptor parent() { return MirebalaisLocations.MIREBALAIS_HOSPITAL; }
		public List<LocationAttributeDescriptor> attributes() { return Collections.singletonList(MirebalaisLocationAttributes.COMMUNITY_HEALTH_LOCATION_CODE); }
	};

	public static LocationDescriptor DENTAL = new LocationDescriptor() {
		public String uuid() { return "4f2d9af1-7eec-4228-bbd6-7b0774c6c267"; }
		public String name() { return "Dantis"; }
		public String description() { return "Dental clinic at Mirebalais Hospital (Dental Clinic)."; }
		public LocationDescriptor parent() { return MirebalaisLocations.MIREBALAIS_HOSPITAL; }
		public List<LocationAttributeDescriptor> attributes() { return Collections.singletonList(MirebalaisLocationAttributes.DENTAL_LOCATION_CODE); }
	};

	public static LocationDescriptor EMERGENCY = new LocationDescriptor() {
		public String uuid() { return "f3a5586e-f06c-4dfb-96b0-6f3451a35e90"; }
		public String name() { return "Ijans"; }
		public String description() { return "Emergency room at Mirebalais Hospital (Ijans)"; }
		public LocationDescriptor parent() { return MirebalaisLocations.MIREBALAIS_HOSPITAL; }
		public List<LocationAttributeDescriptor> attributes() { return Collections.singletonList(MirebalaisLocationAttributes.EMERGENCY_LOCATION_CODE); }
	};

	public static LocationDescriptor EMERGENCY_DEPARTMENT_RECEPTION = new LocationDescriptor() {
		public String uuid() { return "afa09010-43b6-4f19-89e0-58d09941bcbd"; }
		public String name() { return "Ijans Resepsyon"; }
		public String description() { return "Emergency registration and check-in"; }
		public LocationDescriptor parent() { return MirebalaisLocations.MIREBALAIS_HOSPITAL; }
		public List<LocationAttributeDescriptor> attributes() { return Collections.singletonList(MirebalaisLocationAttributes.EMERGENCY_DEPARTMENT_RECEPTION_LOCATION_CODE); }
	};

	public static LocationDescriptor FAMILY_PLANNING = new LocationDescriptor() {
		public String uuid() { return "09af1ef5-d664-4c1f-a9e3-9c8d69bd77c4"; }
		public String name() { return "Planin Familyal"; }
		public String description() { return "Family Planning Clinic"; }
		public LocationDescriptor parent() { return MirebalaisLocations.MIREBALAIS_HOSPITAL; }
		// TODO: This is missing a location code.  Is this intentional?
	};

	public static LocationDescriptor ICU = new LocationDescriptor() {
		public String uuid() { return "b6fcd85f-5995-43c9-875f-3bb2299087ff"; }
		public String name() { return "Swen Entansif"; }
		public String description() { return "ICU at Mirebalais Hospital (Intensive Care Unit)"; }
		public LocationDescriptor parent() { return MirebalaisLocations.MIREBALAIS_HOSPITAL; }
		public List<LocationAttributeDescriptor> attributes() { return Collections.singletonList(MirebalaisLocationAttributes.ICU_LOCATION_CODE); }
	};

	public static LocationDescriptor ISOLATION = new LocationDescriptor() {
		public String uuid() { return "29437276-aeae-4ea8-8219-720886cdc87f"; }
		public String name() { return "Sal Izolman"; }
		public String description() { return "Isolation at Mirebalais Hospital"; }
		public LocationDescriptor parent() { return MirebalaisLocations.MIREBALAIS_HOSPITAL; }
		public List<LocationAttributeDescriptor> attributes() { return Collections.singletonList(MirebalaisLocationAttributes.ISOLATION_LOCATION_CODE); }
	};

	public static LocationDescriptor LABOR_AND_DELIVERY = new LocationDescriptor() {
		public String uuid() { return "dcfefcb7-163b-47e5-84ae-f715cf3e0e92"; }
		public String name() { return "Travay e Akouchman"; }
		public String description() { return "Labor and Delivery at Mirebalais Hospital (Births)"; }
		public LocationDescriptor parent() { return MirebalaisLocations.MIREBALAIS_HOSPITAL; }
		public List<LocationAttributeDescriptor> attributes() { return Collections.singletonList(MirebalaisLocationAttributes.LABOR_AND_DELIVERY_LOCATION_CODE); }
	};

	public static LocationDescriptor MAIN_LABORATORY = new LocationDescriptor() {
		public String uuid() { return "53cd4959-5f4d-4ddc-9971-7fc27e0b7946"; }
		public String name() { return "Laboratwa Prensipal"; }
		public String description() { return "Main laboratory at Mirebalais Hospital"; }
		public LocationDescriptor parent() { return MirebalaisLocations.MIREBALAIS_HOSPITAL; }
		public List<LocationAttributeDescriptor> attributes() { return Collections.singletonList(MirebalaisLocationAttributes.MAIN_LABORATORY_LOCATION_CODE); }
	};

	public static LocationDescriptor MENS_INTERNAL_MEDICINE = new LocationDescriptor() {
		public String uuid() { return "e5db0599-89e8-44fa-bfa2-07e47d63546f"; }
		public String name() { return "Sal Gason"; }
		public String description() { return "Men’s Internal Medicine at Mirebalais Hospital (Inpatient ward for men)"; }
		public LocationDescriptor parent() { return MirebalaisLocations.MIREBALAIS_HOSPITAL; }
		public List<LocationAttributeDescriptor> attributes() { return Collections.singletonList(MirebalaisLocationAttributes.MENS_INTERNAL_MEDICINE_LOCATION_CODE); }
	};

	public static LocationDescriptor MENS_INTERNAL_MEDICINE_A = new LocationDescriptor() {
		public String uuid() { return "3b6f010b-5d44-4ded-85b3-70ea7ba79fd5"; }
		public String name() { return "Sal Gason A"; }
		public LocationDescriptor parent() { return MirebalaisLocations.MENS_INTERNAL_MEDICINE; }
		public String description() { return "Men’s Internal Medicine A at Mirebalais Hospital (1st Inpatient ward for men)"; }
		public List<LocationAttributeDescriptor> attributes() { return Collections.singletonList(MirebalaisLocationAttributes.MENS_INTERNAL_MEDICINE_A_LOCATION_CODE); }
	};

	public static LocationDescriptor MENS_INTERNAL_MEDICINE_B = new LocationDescriptor() {
		public String uuid() { return "a296d225-0312-4ce5-836c-cb2d8e952f47"; }
		public String name() { return "Sal Gason B"; }
		public String description() { return "Men’s Internal Medicine B at Mirebalais Hospital (2nd Inpatient ward for men)"; }
		public LocationDescriptor parent() { return MirebalaisLocations.MENS_INTERNAL_MEDICINE; }
		public List<LocationAttributeDescriptor> attributes() { return Collections.singletonList(MirebalaisLocationAttributes.MENS_INTERNAL_MEDICINE_B_LOCATION_CODE); }
	};

	public static LocationDescriptor NICU = new LocationDescriptor() {
		public String uuid() { return "62a9500e-a1a5-4235-844f-3a8cc0765d53"; }
		public String name() { return "Swen Entansif Neonatal"; }
		public String description() { return "NICU at Mirebalais Hospital (Neonatal Intensive Care Unit)"; }
		public LocationDescriptor parent() { return MirebalaisLocations.MIREBALAIS_HOSPITAL; }
		public List<LocationAttributeDescriptor> attributes() { return Collections.singletonList(MirebalaisLocationAttributes.NICU_LOCATION_CODE); }
	};

	public static LocationDescriptor OPERATING_ROOMS = new LocationDescriptor() {
		public String uuid() { return "41736d90-12f9-4c16-b5a1-5af170b7bf2a"; }
		public String name() { return "Sal Operasyon"; }
		public String description() { return "Operating Rooms at Mirebalais Hospital (all six operating rooms)"; }
		public LocationDescriptor parent() { return MirebalaisLocations.MIREBALAIS_HOSPITAL; }
		public List<LocationAttributeDescriptor> attributes() { return Collections.singletonList(MirebalaisLocationAttributes.OPERATING_ROOMS_LOCATION_CODE); }
	};

	public static LocationDescriptor OUTPATIENT_CLINIC = new LocationDescriptor() {
		public String uuid() { return "199e7d87-92a0-4398-a0f8-11d012178164"; }
		public String name() { return "Klinik Ekstèn"; }
		public String description() { return "The outpatient clinic at Mirebalais Hospital (Klinik Extern)."; }
		public LocationDescriptor parent() { return MirebalaisLocations.MIREBALAIS_HOSPITAL; }
		public List<LocationAttributeDescriptor> attributes() { return Collections.singletonList(MirebalaisLocationAttributes.OUTPATIENT_CLINIC_LOCATION_CODE); }
	};

	public static LocationDescriptor OUTPATIENT_CLINIC_PHARMACY = new LocationDescriptor() {
		public String uuid() { return "79892ece-79f1-4674-abb5-a52c1898c762"; }
		public String name() { return "Klinik Ekstèn Famasi"; }
		public String description() { return "Outpatient Clinic Pharmacy at Mirebalais Hospital. For adults. Women with general medical conditions are also seen here."; }
		public LocationDescriptor parent() { return MirebalaisLocations.OUTPATIENT_CLINIC; }
		public List<LocationAttributeDescriptor> attributes() { return Collections.singletonList(MirebalaisLocationAttributes.OUTPATIENT_CLINIC_PHARMACY_LOCATION_CODE); }
	};

	public static LocationDescriptor PEDIATRICS = new LocationDescriptor() {
		public String uuid() { return "c9ab4c5c-0a8a-4375-b986-f23c163b2f69"; }
		public String name() { return "Sal Timoun"; }
		public String description() { return "Pediatrics at Mirebalais Hospital (Inpatient ward for children)"; }
		public LocationDescriptor parent() { return MirebalaisLocations.MIREBALAIS_HOSPITAL; }
		public List<LocationAttributeDescriptor> attributes() { return Collections.singletonList(MirebalaisLocationAttributes.PEDIATRICS_LOCATION_CODE); }
	};

	public static LocationDescriptor PEDIATRICS_A = new LocationDescriptor() {
		public String uuid() { return "e4ae2d28-d57c-42ba-b7aa-f6fcac421b2c"; }
		public String name() { return "Sal Timoun A"; }
		public String description() { return "Pediatrics A at Mirebalais Hospital (1st Inpatient ward for children)"; }
		public LocationDescriptor parent() { return MirebalaisLocations.PEDIATRICS; }
		public List<LocationAttributeDescriptor> attributes() { return Collections.singletonList(MirebalaisLocationAttributes.PEDIATRICS_A_LOCATION_CODE); }
	};

	public static LocationDescriptor PEDIATRICS_B = new LocationDescriptor() {
		public String uuid() { return "e752ec51-5633-489f-aef1-01f285c03f38"; }
		public String name() { return "Sal Timoun B"; }
		public String description() { return "Pediatrics B at Mirebalais Hospital (2nd Inpatient ward for children)"; }
		public LocationDescriptor parent() { return MirebalaisLocations.PEDIATRICS; }
		public List<LocationAttributeDescriptor> attributes() { return Collections.singletonList(MirebalaisLocationAttributes.PEDIATRICS_B_LOCATION_CODE); }
	};

	public static LocationDescriptor POST_OP_GYN = new LocationDescriptor() {
		public String uuid() { return "ec9d2302-b525-45ce-bebe-42ea1d38b251"; }
		public String name() { return "Sal Aprè Operasyon | Sante Fanm"; }
		public String description() { return "Post-op GYN at Mirebalais Hospital (Recovery after surgery related to gynecology)"; }
		public LocationDescriptor parent() { return MirebalaisLocations.MIREBALAIS_HOSPITAL; }
		public List<LocationAttributeDescriptor> attributes() { return Collections.singletonList(MirebalaisLocationAttributes.POST_OP_GYN_LOCATION_CODE); }
	};

	public static LocationDescriptor POSTPARTUM_WARD = new LocationDescriptor() {
		public String uuid() { return "950852f3-8a96-4d82-a5f8-a68a92043164"; }
		public String name() { return "Sal Aprè Akouchman"; }
		public String description() { return "Postpartum ward at Mirebalais Hospital (After giving birth)"; }
		public LocationDescriptor parent() { return MirebalaisLocations.MIREBALAIS_HOSPITAL; }
		public List<LocationAttributeDescriptor> attributes() { return Collections.singletonList(MirebalaisLocationAttributes.POSTPARTUM_WARD_LOCATION_CODE); }
	};

	public static LocationDescriptor PRE_OP_PACU = new LocationDescriptor() {
		public String uuid() { return "109a13a9-029d-498d-b66e-bab5eb396996"; }
		public String name() { return "Sal Avan Operasyon | PACU"; }
		public String description() { return "Pre-op/PACU at Mirebalais Hospital (Before surgery, and Post-Anesthesia Care Unit)"; }
		public LocationDescriptor parent() { return MirebalaisLocations.MIREBALAIS_HOSPITAL; }
		public List<LocationAttributeDescriptor> attributes() { return Collections.singletonList(MirebalaisLocationAttributes.PRE_OP_PACU_LOCATION_CODE); }
	};

	public static LocationDescriptor RADIOLOGY = new LocationDescriptor() {
		public String uuid() { return "cfe92278-0181-4315-ab7d-1731753120f0"; }
		public String name() { return "Radyografi"; }
		public String description() { return "Radiology at Mirebalais Hospital"; }
		public LocationDescriptor parent() { return MirebalaisLocations.MIREBALAIS_HOSPITAL; }
		public List<LocationAttributeDescriptor> attributes() { return Collections.singletonList(MirebalaisLocationAttributes.RADIOLOGY_LOCATION_CODE); }
	};

	public static LocationDescriptor REHABILITATION = new LocationDescriptor() {
		public String uuid() { return "5fd9a9b6-a9af-47be-b5d8-f2d4239dfdc1"; }
		public String name() { return "Sal Reyabilitasyon"; }
		public String description() { return "Rehabilitation Ward"; }
		public LocationDescriptor parent() { return MirebalaisLocations.MIREBALAIS_HOSPITAL; }
		public List<LocationAttributeDescriptor> attributes() { return Collections.singletonList(MirebalaisLocationAttributes.REHABILITATION_LOCATION_CODE); }
	};

	public static LocationDescriptor SURGICAL_WARD = new LocationDescriptor() {
		public String uuid() { return "7d6cc39d-a600-496f-a320-fd4985f07f0b"; }
		public String name() { return "Sal Aprè Operasyon"; }
		public String description() { return "Surgical Ward at Mirebalais Hospital (Surgery)"; }
		public LocationDescriptor parent() { return MirebalaisLocations.MIREBALAIS_HOSPITAL; }
		public List<LocationAttributeDescriptor> attributes() { return Collections.singletonList(MirebalaisLocationAttributes.SURGICAL_WARD_LOCATION_CODE); }
	};

	public static LocationDescriptor WOMENS_CLINIC = new LocationDescriptor() {
		public String uuid() { return "9b2066a2-7087-47f6-9b3a-b001037432a3"; }
		public String name() { return "Sante Fanm"; }
		public String description() { return "Women's Outpatient clinic at Mirebalais Hospital (Sante Fanm)."; }
		public LocationDescriptor parent() { return MirebalaisLocations.MIREBALAIS_HOSPITAL; }
		public List<LocationAttributeDescriptor> attributes() { return Collections.singletonList(MirebalaisLocationAttributes.WOMENS_CLINIC_LOCATION_CODE); }
	};

	public static LocationDescriptor WOMENS_AND_CHILDRENS_PHARMACY = new LocationDescriptor() {
		public String uuid() { return "de8892ff-e755-4ef0-ae0a-c27d2c1e6a74"; }
		public String name() { return "Famasi Famn ak Ti moun"; }
		public String description() { return "Women and Children's Pharmacy at Mirebalais Hospital."; }
		public LocationDescriptor parent() { return MirebalaisLocations.WOMENS_CLINIC; }
		public List<LocationAttributeDescriptor> attributes() { return Collections.singletonList(MirebalaisLocationAttributes.WOMENS_AND_CHILDRENS_PHARMACY_LOCATION_CODE); }
	};

	public static LocationDescriptor WOMENS_INTERNAL_MEDICINE = new LocationDescriptor() {
		public String uuid() { return "2c93919d-7fc6-406d-a057-c0b640104790"; }
		public String name() { return "Sal Fanm"; }
		public String description() { return "Women’s Internal Medicine at Mirebalais Hospital (Inpatient ward for women)"; }
		public LocationDescriptor parent() { return MirebalaisLocations.MIREBALAIS_HOSPITAL; }
		public List<LocationAttributeDescriptor> attributes() { return Collections.singletonList(MirebalaisLocationAttributes.WOMENS_INTERNAL_MEDICINE_LOCATION_CODE); }
	};

	public static LocationDescriptor WOMENS_INTERNAL_MEDICINE_A = new LocationDescriptor() {
		public String uuid() { return "8e4e930b-e801-44c5-8895-e7ef6feb6d73"; }
		public String name() { return "Sal Fanm A"; }
		public String description() { return "Women’s Internal Medicine A at Mirebalais Hospital (1st Inpatient ward for women)"; }
		public LocationDescriptor parent() { return MirebalaisLocations.WOMENS_INTERNAL_MEDICINE; }
		public List<LocationAttributeDescriptor> attributes() { return Collections.singletonList(MirebalaisLocationAttributes.WOMENS_INTERNAL_MEDICINE_A_LOCATION_CODE); }
	};

	public static LocationDescriptor WOMENS_INTERNAL_MEDICINE_B = new LocationDescriptor() {
		public String uuid() { return "195d0af1-3887-4a28-a542-b832099fe3ee"; }
		public String name() { return "Sal Fanm B"; }
		public String description() { return "Women’s Internal Medicine B at Mirebalais Hospital (2nd Inpatient ward for women)"; }
		public LocationDescriptor parent() { return MirebalaisLocations.WOMENS_INTERNAL_MEDICINE; }
		public List<LocationAttributeDescriptor> attributes() { return Collections.singletonList(MirebalaisLocationAttributes.WOMENS_INTERNAL_MEDICINE_B_LOCATION_CODE); }
	};

	public static LocationDescriptor WOMENS_OUTPATIENT_LABORATORY = new LocationDescriptor() {
		public String uuid() { return "0198d2c3-9c08-45b6-88df-cbb60446ef00"; }
		public String name() { return "Laboratwa Ekstèn pou Fanm"; }
		public String description() { return "Women’s Outpatient Laboratory at Mirebalais Hospital"; }
		public LocationDescriptor parent() { return MirebalaisLocations.MIREBALAIS_HOSPITAL; }
		public List<LocationAttributeDescriptor> attributes() { return Collections.singletonList(MirebalaisLocationAttributes.WOMENS_OUTPATIENT_LABORATORY_LOCATION_CODE); }
	};

	public static LocationDescriptor WOMENS_TRIAGE = new LocationDescriptor() {
		public String uuid() { return "d65eb8cf-d781-4ea8-9d9a-2b3e03c6074c"; }
		public String name() { return "Ijans | Sante Fanm"; }
		public String description() { return "Women’s Triage at Mirebalais Hospital"; }
		public LocationDescriptor parent() { return MirebalaisLocations.MIREBALAIS_HOSPITAL; }
		public List<LocationAttributeDescriptor> attributes() { return Collections.singletonList(MirebalaisLocationAttributes.WOMENS_TRIAGE_LOCATION_CODE); }
	};

	//***** CDI LOCATIONS *****

	public static LocationDescriptor CDI_KLINIK_EKSTEN_JENERAL = new LocationDescriptor() {
		public String uuid() { return "083e75b0-5959-11e4-8ed6-0800200c9a66"; }
		public String name() { return "CDI Klinik Ekstèn Jeneral"; }
		public String description() { return "CDI Klinik Ekstèn Jeneral"; }
		public LocationDescriptor parent() { return MirebalaisLocations.MIREBALAIS_CDI_PARENT; }
		public List<LocationAttributeDescriptor> attributes() { return Arrays.asList(MirebalaisLocationAttributes.CDI_KLINIK_EKSTEN_JENERAL_LOCATION_CODE, MirebalaisLocationAttributes.CDI_KLINIK_EKSTEN_JENERAL_NAME_TO_PRINT_ON_ID_CARD); }
	};

	public static LocationDescriptor CDI_KLINIK_EKSTEN_JENERAL_ACHIV_SANTRAL = new LocationDescriptor() {
		public String uuid() { return "11857d81-5959-11e4-8ed6-0800200c9a66"; }
		public String name() { return "CDI Klinik Ekstèn Jeneral Achiv Santral"; }
		public String description() { return "CDI Klinik Ekstèn Jeneral Achiv Santral"; }
		public LocationDescriptor parent() { return MirebalaisLocations.CDI_KLINIK_EKSTEN_JENERAL; }
		public List<LocationAttributeDescriptor> attributes() { return Collections.singletonList(MirebalaisLocationAttributes.CDI_KLINIK_EKSTEN_JENERAL_ACHIV_SANTRAL_LOCATION_CODE); }
	};

	public static LocationDescriptor CDI_KLINIK_EKSTEN_JENERAL_BIWO_RANDEVOU = new LocationDescriptor() {
		public String uuid() { return "11857d80-5959-11e4-8ed6-0800200c9a66"; }
		public String name() { return "CDI Klinik Ekstèn Jeneral Biwo Randevou"; }
		public String description() { return "CDI Klinik Ekstèn Jeneral Biwo Randevou"; }
		public LocationDescriptor parent() { return MirebalaisLocations.CDI_KLINIK_EKSTEN_JENERAL; }
		public List<LocationAttributeDescriptor> attributes() { return Collections.singletonList(MirebalaisLocationAttributes.CDI_KLINIK_EKSTEN_JENERAL_BIWO_RANDEVOU_LOCATION_CODE); }
	};

	public static LocationDescriptor CDI_KLINIK_EKSTEN_JENERAL_FAMASI = new LocationDescriptor() {
		public String uuid() { return "9e212720-eeab-43ef-a6c0-95c3881052bc"; }
		public String name() { return "CDI Klinik Ekstèn Jeneral Famasi"; }
		public String description() { return "CDI Klinik Ekstèn Jeneral Famasi"; }
		public LocationDescriptor parent() { return MirebalaisLocations.CDI_KLINIK_EKSTEN_JENERAL; }
		public List<LocationAttributeDescriptor> attributes() { return Collections.singletonList(MirebalaisLocationAttributes.CDI_KLINIK_EKSTEN_JENERAL_FAMASI_LOCATION_CODE); }
	};

	public static LocationDescriptor CDI_KLINIK_EKSTEN_JENERAL_LABORATWA = new LocationDescriptor() {
		public String uuid() { return "730ad64b-a2cd-40f7-91ac-ecc8fabf9f3e"; }
		public String name() { return "CDI Klinik Ekstèn Jeneral Laboratwa"; }
		public String description() { return "CDI Klinik Ekstèn Jeneral Laboratwa"; }
		public LocationDescriptor parent() { return MirebalaisLocations.CDI_KLINIK_EKSTEN_JENERAL; }
		public List<LocationAttributeDescriptor> attributes() { return Collections.singletonList(MirebalaisLocationAttributes.CDI_KLINIK_EKSTEN_JENERAL_LABORATWA_LOCATION_CODE); }
	};

	public static LocationDescriptor CDI_KLINIK_EKSTEN_JENERAL_RADYOGRAFI = new LocationDescriptor() {
		public String uuid() { return "3f0e7635-1fa3-4058-9eb7-162c0043e94f"; }
		public String name() { return "CDI Klinik Ekstèn Jeneral Radyografi"; }
		public String description() { return "CDI Klinik Ekstèn Jeneral Radyografi"; }
		public LocationDescriptor parent() { return MirebalaisLocations.CDI_KLINIK_EKSTEN_JENERAL; }
		public List<LocationAttributeDescriptor> attributes() { return Collections.singletonList(MirebalaisLocationAttributes.CDI_KLINIK_EKSTEN_JENERAL_RADYOGRAFI_LOCATION_CODE); }
	};

	public static LocationDescriptor CDI_KLINIK_EKSTEN_JENERAL_SAL_PWOSEDI = new LocationDescriptor() {
		public String uuid() { return "58f3e355-ff28-4151-bbf6-5be3cb06fba8"; }
		public String name() { return "CDI Klinik Ekstèn Jeneral Sal Pwosedi"; }
		public String description() { return "CDI Klinik Ekstèn Jeneral Sal Pwosedi"; }
		public LocationDescriptor parent() { return MirebalaisLocations.CDI_KLINIK_EKSTEN_JENERAL; }
		public List<LocationAttributeDescriptor> attributes() { return Collections.singletonList(MirebalaisLocationAttributes.CDI_KLINIK_EKSTEN_JENERAL_SAL_PWOSEDI_LOCATION_CODE); }
	};

	//***** RETIRED LOCATIONS *****

	public static LocationDescriptor RETIRED_CDI_KLINIK_EKSTEN_JENERAL_OLD = new LocationDescriptor() {
		public String uuid() { return "11857d82-5959-11e4-8ed6-0800200c9a66"; }
		public String name() { return "Retired CDI Klinik Ekstèn Jeneral"; }
		public String description() { return "Retired CDI Klinik Ekstèn Jeneral"; }
		public List<LocationAttributeDescriptor> attributes() { return null; }
	};

	public static LocationDescriptor RETIRED_ED_BOARDING = new LocationDescriptor() {
		public String uuid() { return "8355ad15-c3c9-4471-a1e7-dc18b7983087"; }
		public String name() { return "Retired ED Boarding"; }
		public String description() { return "Retired ED Boarding"; }
		public List<LocationAttributeDescriptor> attributes() { return null; }
	};

	public static LocationDescriptor RETIRED_ED_OBSERVATION = new LocationDescriptor() {
		public String uuid() { return "459c97ba-8d90-485d-8993-9540c55166f6"; }
		public String name() { return "Retired ED Observation"; }
		public String description() { return "Retired ED Observation"; }
		public List<LocationAttributeDescriptor> attributes() { return null; }
	};
}