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
import org.openmrs.module.metadatadeploy.descriptor.LocationAttributeTypeDescriptor;
import org.openmrs.module.metadatadeploy.descriptor.LocationDescriptor;
import org.openmrs.module.pihcore.metadata.core.LocationAttributeTypes;

/**
 * Constants for all defined location attributes
 */
public class MirebalaisLocationAttributes {

	//***** LOCATION NAMES TO PRINT ON ID CARDS

	public static LocationAttributeDescriptor MIREBALAIS_CDI_PARENT_NAME_TO_PRINT_ON_ID_CARD = new LocationAttributeDescriptor() {
		public String uuid() { return "19cddbbe-fe82-448f-b2ab-95c4b73bc6f5"; }
		public LocationDescriptor location() { return MirebalaisLocations.MIREBALAIS_CDI_PARENT; }
		public LocationAttributeTypeDescriptor type() { return LocationAttributeTypes.NAME_TO_PRINT_ON_ID_CARD; }
		public String value() { return "Mirebalais"; }
	};

	public static LocationAttributeDescriptor MIREBALAIS_HOSPITAL_NAME_TO_PRINT_ON_ID_CARD = new LocationAttributeDescriptor() {
		public String uuid() { return "c98ecc70-bc72-11e4-bb52-0800200c9a66"; }
		public LocationDescriptor location() { return MirebalaisLocations.MIREBALAIS_HOSPITAL; }
		public LocationAttributeTypeDescriptor type() { return LocationAttributeTypes.NAME_TO_PRINT_ON_ID_CARD; }
		public String value() { return "Mirebalais"; }
	};

	public static LocationAttributeDescriptor CDI_KLINIK_EKSTEN_JENERAL_NAME_TO_PRINT_ON_ID_CARD = new LocationAttributeDescriptor() {
		public String uuid() { return "d3972870-bc72-11e4-bb52-0800200c9a66"; }
		public LocationDescriptor location() { return MirebalaisLocations.CDI_KLINIK_EKSTEN_JENERAL; }
		public LocationAttributeTypeDescriptor type() { return LocationAttributeTypes.NAME_TO_PRINT_ON_ID_CARD; }
		public String value() { return "Mirebalais"; }
	};

	//***** MIREBALAIS LOCATION CODES *****

	public static LocationAttributeDescriptor ANTEPARTUM_WARD_LOCATION_CODE = new LocationAttributeDescriptor() {
		public String uuid() { return "57f66e57-ddbf-41dd-b72b-734feb9cfc10"; }
		public LocationDescriptor location() { return MirebalaisLocations.ANTEPARTUM_WARD; }
		public LocationAttributeTypeDescriptor type() { return LocationAttributeTypes.LOCATION_CODE; }
		public String value() { return "M006"; }
	};

	public static LocationAttributeDescriptor BLOOD_BANK_LOCATION_CODE = new LocationAttributeDescriptor() {
		public String uuid() { return "90307894-ec4a-4dad-bb00-d44f633e6e18"; }
		public LocationDescriptor location() { return MirebalaisLocations.BLOOD_BANK; }
		public LocationAttributeTypeDescriptor type() { return LocationAttributeTypes.LOCATION_CODE; }
		public String value() { return "M033"; }
	};

	public static LocationAttributeDescriptor CENTRAL_ARCHIVES_LOCATION_CODE = new LocationAttributeDescriptor() {
		public String uuid() { return "2705ac66-949e-461a-8f73-ea087ea28bb0"; }
		public LocationDescriptor location() { return MirebalaisLocations.CENTRAL_ARCHIVES; }
		public LocationAttributeTypeDescriptor type() { return LocationAttributeTypes.LOCATION_CODE; }
		public String value() { return "M028"; }
	};

	public static LocationAttributeDescriptor CLINIC_REGISTRATION_LOCATION_CODE = new LocationAttributeDescriptor() {
		public String uuid() { return "58b76638-90b2-4e23-b0d9-b63f11c49e79"; }
		public LocationDescriptor location() { return MirebalaisLocations.CLINIC_REGISTRATION; }
		public LocationAttributeTypeDescriptor type() { return LocationAttributeTypes.LOCATION_CODE; }
		public String value() { return "M029"; }
	};

	public static LocationAttributeDescriptor CHEMOTHERAPY_LOCATION_CODE = new LocationAttributeDescriptor() {
		public String uuid() { return "7df84969-3685-4894-84aa-17b58ad93a94"; }
		public LocationDescriptor location() { return MirebalaisLocations.CHEMOTHERAPY; }
		public LocationAttributeTypeDescriptor type() { return LocationAttributeTypes.LOCATION_CODE; }
		public String value() { return "M038"; }
	};

	public static LocationAttributeDescriptor COMMUNITY_HEALTH_LOCATION_CODE = new LocationAttributeDescriptor() {
		public String uuid() { return "0b70293d-0995-404c-924c-e4f54d6860b5"; }
		public LocationDescriptor location() { return MirebalaisLocations.COMMUNITY_HEALTH; }
		public LocationAttributeTypeDescriptor type() { return LocationAttributeTypes.LOCATION_CODE; }
		public String value() { return "M011"; }
	};

	public static LocationAttributeDescriptor DENTAL_LOCATION_CODE = new LocationAttributeDescriptor() {
		public String uuid() { return "41fec28b-dd0c-4011-be67-9ebca234ebfd"; }
		public LocationDescriptor location() { return MirebalaisLocations.DENTAL; }
		public LocationAttributeTypeDescriptor type() { return LocationAttributeTypes.LOCATION_CODE; }
		public String value() { return "M019"; }
	};

	public static LocationAttributeDescriptor EMERGENCY_LOCATION_CODE = new LocationAttributeDescriptor() {
		public String uuid() { return "82c82a3d-5153-4eaf-9ad5-154eeffe0f1b"; }
		public LocationDescriptor location() { return MirebalaisLocations.EMERGENCY; }
		public LocationAttributeTypeDescriptor type() { return LocationAttributeTypes.LOCATION_CODE; }
		public String value() { return "M004"; }
	};

	public static LocationAttributeDescriptor EMERGENCY_DEPARTMENT_RECEPTION_LOCATION_CODE = new LocationAttributeDescriptor() {
		public String uuid() { return "709ee7f1-5d3d-493d-a931-6fca9a49b7ff"; }
		public LocationDescriptor location() { return MirebalaisLocations.EMERGENCY_DEPARTMENT_RECEPTION; }
		public LocationAttributeTypeDescriptor type() { return LocationAttributeTypes.LOCATION_CODE; }
		public String value() { return "M034"; }
	};

	public static LocationAttributeDescriptor ICU_LOCATION_CODE = new LocationAttributeDescriptor() {
		public String uuid() { return "8b13c4c2-8c44-4faa-b385-2734594a8d44"; }
		public LocationDescriptor location() { return MirebalaisLocations.ICU; }
		public LocationAttributeTypeDescriptor type() { return LocationAttributeTypes.LOCATION_CODE; }
		public String value() { return "M018"; }
	};

	public static LocationAttributeDescriptor ISOLATION_LOCATION_CODE = new LocationAttributeDescriptor() {
		public String uuid() { return "1f07a471-cd35-429d-a82b-7bff40650216"; }
		public LocationDescriptor location() { return MirebalaisLocations.ISOLATION; }
		public LocationAttributeTypeDescriptor type() { return LocationAttributeTypes.LOCATION_CODE; }
		public String value() { return "M017"; }
	};

	public static LocationAttributeDescriptor COVID19_ISOLATION_LOCATION_CODE = new LocationAttributeDescriptor() {
		public String uuid() { return "7ea7d069-1e3e-4f1d-b31e-306413325806"; }
		public LocationDescriptor location() { return MirebalaisLocations.COVID19_ISOLATION; }
		public LocationAttributeTypeDescriptor type() { return LocationAttributeTypes.LOCATION_CODE; }
		public String value() { return "M048"; }
	};

	public static LocationAttributeDescriptor COVID19_UMI_LOCATION_CODE = new LocationAttributeDescriptor() {
		public String uuid() { return "3d1316c8-71d9-4579-95c7-f58c711aa175"; }
		public LocationDescriptor location() { return MirebalaisLocations.COVID19_UMI; }
		public LocationAttributeTypeDescriptor type() { return LocationAttributeTypes.LOCATION_CODE; }
		public String value() { return "M049"; }
	};

	public static LocationAttributeDescriptor LABOR_AND_DELIVERY_LOCATION_CODE = new LocationAttributeDescriptor() {
		public String uuid() { return "54750c8b-d979-4d3b-bd1d-57830009e064"; }
		public LocationDescriptor location() { return MirebalaisLocations.LABOR_AND_DELIVERY; }
		public LocationAttributeTypeDescriptor type() { return LocationAttributeTypes.LOCATION_CODE; }
		public String value() { return "M023"; }
	};

	public static LocationAttributeDescriptor MAIN_LABORATORY_LOCATION_CODE = new LocationAttributeDescriptor() {
		public String uuid() { return "f3d2b863-4524-40a1-8908-6646255e2e32"; }
		public LocationDescriptor location() { return MirebalaisLocations.MAIN_LABORATORY; }
		public LocationAttributeTypeDescriptor type() { return LocationAttributeTypes.LOCATION_CODE; }
		public String value() { return "M007"; }
	};

	public static LocationAttributeDescriptor MENS_INTERNAL_MEDICINE_LOCATION_CODE = new LocationAttributeDescriptor() {
		public String uuid() { return "27f73024-9317-4b37-bc79-82ccad31fced"; }
		public LocationDescriptor location() { return MirebalaisLocations.MENS_INTERNAL_MEDICINE; }
		public LocationAttributeTypeDescriptor type() { return LocationAttributeTypes.LOCATION_CODE; }
		public String value() { return "M036"; }
	};

	public static LocationAttributeDescriptor MENS_INTERNAL_MEDICINE_A_LOCATION_CODE = new LocationAttributeDescriptor() {
		public String uuid() { return "3d2220a6-2ed2-4f9d-a856-67f79ae9b1c9"; }
		public LocationDescriptor location() { return MirebalaisLocations.MENS_INTERNAL_MEDICINE_A; }
		public LocationAttributeTypeDescriptor type() { return LocationAttributeTypes.LOCATION_CODE; }
		public String value() { return "M020"; }
	};

	public static LocationAttributeDescriptor MENS_INTERNAL_MEDICINE_B_LOCATION_CODE = new LocationAttributeDescriptor() {
		public String uuid() { return "ec2b429b-6c73-4863-bd89-a6501d58d540"; }
		public LocationDescriptor location() { return MirebalaisLocations.MENS_INTERNAL_MEDICINE_B; }
		public LocationAttributeTypeDescriptor type() { return LocationAttributeTypes.LOCATION_CODE; }
		public String value() { return "M021"; }
	};

	public static LocationAttributeDescriptor NICU_LOCATION_CODE = new LocationAttributeDescriptor() {
		public String uuid() { return "f8eba8dc-8939-4c5e-a39b-110d833b86e6"; }
		public LocationDescriptor location() { return MirebalaisLocations.NICU; }
		public LocationAttributeTypeDescriptor type() { return LocationAttributeTypes.LOCATION_CODE; }
		public String value() { return "M014"; }
	};

	public static LocationAttributeDescriptor OPERATING_ROOMS_LOCATION_CODE = new LocationAttributeDescriptor() {
		public String uuid() { return "d08b49d0-be14-49bb-9319-02f4125834c8"; }
		public LocationDescriptor location() { return MirebalaisLocations.OPERATING_ROOMS; }
		public LocationAttributeTypeDescriptor type() { return LocationAttributeTypes.LOCATION_CODE; }
		public String value() { return "M015"; }
	};

	public static LocationAttributeDescriptor OUTPATIENT_CLINIC_LOCATION_CODE = new LocationAttributeDescriptor() {
		public String uuid() { return "350badbe-82a1-42f6-81a3-e2f7aaec5d1e"; }
		public LocationDescriptor location() { return MirebalaisLocations.OUTPATIENT_CLINIC; }
		public LocationAttributeTypeDescriptor type() { return LocationAttributeTypes.LOCATION_CODE; }
		public String value() { return "M005"; }
	};

	public static LocationAttributeDescriptor OUTPATIENT_CLINIC_PHARMACY_LOCATION_CODE = new LocationAttributeDescriptor() {
		public String uuid() { return "96af5ad1-c2c5-4cf4-9333-26f0c1f2e75b"; }
		public LocationDescriptor location() { return MirebalaisLocations.OUTPATIENT_CLINIC_PHARMACY; }
		public LocationAttributeTypeDescriptor type() { return LocationAttributeTypes.LOCATION_CODE; }
		public String value() { return "M031"; }
	};

	public static LocationAttributeDescriptor PEDIATRICS_LOCATION_CODE = new LocationAttributeDescriptor() {
		public String uuid() { return "ebaa5b54-7405-4c99-a877-afdb8e199df4"; }
		public LocationDescriptor location() { return MirebalaisLocations.PEDIATRICS; }
		public LocationAttributeTypeDescriptor type() { return LocationAttributeTypes.LOCATION_CODE; }
		public String value() { return "M037"; }
	};

	public static LocationAttributeDescriptor PEDIATRICS_A_LOCATION_CODE = new LocationAttributeDescriptor() {
		public String uuid() { return "d5827677-9adf-48b2-b6fa-e769af037873"; }
		public LocationDescriptor location() { return MirebalaisLocations.PEDIATRICS_A; }
		public LocationAttributeTypeDescriptor type() { return LocationAttributeTypes.LOCATION_CODE; }
		public String value() { return "M012"; }
	};

	public static LocationAttributeDescriptor PEDIATRICS_B_LOCATION_CODE = new LocationAttributeDescriptor() {
		public String uuid() { return "24f2c381-2aa9-44cf-aeca-165d7d0196e8"; }
		public LocationDescriptor location() { return MirebalaisLocations.PEDIATRICS_B; }
		public LocationAttributeTypeDescriptor type() { return LocationAttributeTypes.LOCATION_CODE; }
		public String value() { return "M022"; }
	};

	public static LocationAttributeDescriptor PRE_OP_PACU_LOCATION_CODE = new LocationAttributeDescriptor() {
		public String uuid() { return "01b1db36-bf5b-450e-a53d-a5ac6c50b5b2"; }
		public LocationDescriptor location() { return MirebalaisLocations.PRE_OP_PACU; }
		public LocationAttributeTypeDescriptor type() { return LocationAttributeTypes.LOCATION_CODE; }
		public String value() { return "M013"; }
	};

	public static LocationAttributeDescriptor POST_OP_GYN_LOCATION_CODE = new LocationAttributeDescriptor() {
		public String uuid() { return "afa2909f-2b68-4923-9c29-07e8d37a99a1"; }
		public LocationDescriptor location() { return MirebalaisLocations.POST_OP_GYN; }
		public LocationAttributeTypeDescriptor type() { return LocationAttributeTypes.LOCATION_CODE; }
		public String value() { return "M009"; }
	};

	public static LocationAttributeDescriptor POSTPARTUM_WARD_LOCATION_CODE = new LocationAttributeDescriptor() {
		public String uuid() { return "a01858fe-2f18-418d-8b4b-5d3f926efc4f"; }
		public LocationDescriptor location() { return MirebalaisLocations.POSTPARTUM_WARD; }
		public LocationAttributeTypeDescriptor type() { return LocationAttributeTypes.LOCATION_CODE; }
		public String value() { return "M008"; }
	};

	public static LocationAttributeDescriptor RADIOLOGY_LOCATION_CODE = new LocationAttributeDescriptor() {
		public String uuid() { return "d8be457b-c418-4581-8673-50ca20d2ba42"; }
		public LocationDescriptor location() { return MirebalaisLocations.RADIOLOGY; }
		public LocationAttributeTypeDescriptor type() { return LocationAttributeTypes.LOCATION_CODE; }
		public String value() { return "M010"; }
	};

	public static LocationAttributeDescriptor REHABILITATION_LOCATION_CODE = new LocationAttributeDescriptor() {
		public String uuid() { return "0b2d83a4-7c45-417e-a08c-1e46dea54c73"; }
		public LocationDescriptor location() { return MirebalaisLocations.REHABILITATION; }
		public LocationAttributeTypeDescriptor type() { return LocationAttributeTypes.LOCATION_CODE; }
		public String value() { return "M039"; }
	};

	public static LocationAttributeDescriptor SURGICAL_WARD_LOCATION_CODE = new LocationAttributeDescriptor() {
		public String uuid() { return "162c86a7-5918-4311-ae98-25708541e97f"; }
		public LocationDescriptor location() { return MirebalaisLocations.SURGICAL_WARD; }
		public LocationAttributeTypeDescriptor type() { return LocationAttributeTypes.LOCATION_CODE; }
		public String value() { return "M025"; }
	};

	public static LocationAttributeDescriptor WOMENS_AND_CHILDRENS_PHARMACY_LOCATION_CODE = new LocationAttributeDescriptor() {
		public String uuid() { return "d3906d5e-6b4e-4c2b-b19a-26e247d89209"; }
		public LocationDescriptor location() { return MirebalaisLocations.WOMENS_AND_CHILDRENS_PHARMACY; }
		public LocationAttributeTypeDescriptor type() { return LocationAttributeTypes.LOCATION_CODE; }
		public String value() { return "M032"; }
	};

	public static LocationAttributeDescriptor WOMENS_CLINIC_LOCATION_CODE = new LocationAttributeDescriptor() {
		public String uuid() { return "703614f6-c941-4ec1-a639-65e308762034"; }
		public LocationDescriptor location() { return MirebalaisLocations.WOMENS_CLINIC; }
		public LocationAttributeTypeDescriptor type() { return LocationAttributeTypes.LOCATION_CODE; }
		public String value() { return "M003"; }
	};

	public static LocationAttributeDescriptor WOMENS_INTERNAL_MEDICINE_LOCATION_CODE = new LocationAttributeDescriptor() {
		public String uuid() { return "848120b5-a8ed-453d-b17c-3a1532f87e71"; }
		public LocationDescriptor location() { return MirebalaisLocations.WOMENS_INTERNAL_MEDICINE; }
		public LocationAttributeTypeDescriptor type() { return LocationAttributeTypes.LOCATION_CODE; }
		public String value() { return "M035"; }
	};

	public static LocationAttributeDescriptor WOMENS_INTERNAL_MEDICINE_A_LOCATION_CODE = new LocationAttributeDescriptor() {
		public String uuid() { return "4c7fe8b5-e965-4cd1-9021-dfa994c16838"; }
		public LocationDescriptor location() { return MirebalaisLocations.WOMENS_INTERNAL_MEDICINE_A; }
		public LocationAttributeTypeDescriptor type() { return LocationAttributeTypes.LOCATION_CODE; }
		public String value() { return "M026"; }
	};

	public static LocationAttributeDescriptor WOMENS_INTERNAL_MEDICINE_B_LOCATION_CODE = new LocationAttributeDescriptor() {
		public String uuid() { return "bdae8ec9-3ed0-4b93-a38b-0c61674aae8a"; }
		public LocationDescriptor location() { return MirebalaisLocations.WOMENS_INTERNAL_MEDICINE_B; }
		public LocationAttributeTypeDescriptor type() { return LocationAttributeTypes.LOCATION_CODE; }
		public String value() { return "M016"; }
	};

	public static LocationAttributeDescriptor WOMENS_OUTPATIENT_LABORATORY_LOCATION_CODE = new LocationAttributeDescriptor() {
		public String uuid() { return "5d82d626-27a4-42f8-898c-236426810e40"; }
		public LocationDescriptor location() { return MirebalaisLocations.WOMENS_OUTPATIENT_LABORATORY; }
		public LocationAttributeTypeDescriptor type() { return LocationAttributeTypes.LOCATION_CODE; }
		public String value() { return "M024"; }
	};

	public static LocationAttributeDescriptor WOMENS_TRIAGE_LOCATION_CODE = new LocationAttributeDescriptor() {
		public String uuid() { return "157c7f05-fa41-4ccb-a6cb-599d8c731aee"; }
		public LocationDescriptor location() { return MirebalaisLocations.WOMENS_TRIAGE; }
		public LocationAttributeTypeDescriptor type() { return LocationAttributeTypes.LOCATION_CODE; }
		public String value() { return "M027"; }
	};

	public static LocationAttributeDescriptor NCD_CLINIC_LOCATION_CODE = new LocationAttributeDescriptor() {
		public String uuid() { return "5acc239e-888f-4772-b027-fa9614d74ef5"; }
		public LocationDescriptor location() { return MirebalaisLocations.NCD_CLINIC; }
		public LocationAttributeTypeDescriptor type() { return LocationAttributeTypes.LOCATION_CODE; }
		public String value() { return "M047"; }
	};

	//***** CDI LOCATION CODES *****

	public static LocationAttributeDescriptor CDI_KLINIK_EKSTEN_JENERAL_LOCATION_CODE = new LocationAttributeDescriptor() {
		public String uuid() { return "69d62f1b-fc3d-43e5-a605-91597268b173"; }
		public LocationDescriptor location() { return MirebalaisLocations.CDI_KLINIK_EKSTEN_JENERAL; }
		public LocationAttributeTypeDescriptor type() { return LocationAttributeTypes.LOCATION_CODE; }
		public String value() { return "M040"; }
	};

	public static LocationAttributeDescriptor CDI_KLINIK_EKSTEN_JENERAL_ACHIV_SANTRAL_LOCATION_CODE = new LocationAttributeDescriptor() {
		public String uuid() { return "201db253-b474-4bce-b4f2-5b11acfe86fe"; }
		public LocationDescriptor location() { return MirebalaisLocations.CDI_KLINIK_EKSTEN_JENERAL_ACHIV_SANTRAL; }
		public LocationAttributeTypeDescriptor type() { return LocationAttributeTypes.LOCATION_CODE; }
		public String value() { return "M041"; }
	};

	public static LocationAttributeDescriptor CDI_KLINIK_EKSTEN_JENERAL_BIWO_RANDEVOU_LOCATION_CODE = new LocationAttributeDescriptor() {
		public String uuid() { return "d4228398-70d0-4262-896f-ef42ba86fb00"; }
		public LocationDescriptor location() { return MirebalaisLocations.CDI_KLINIK_EKSTEN_JENERAL_BIWO_RANDEVOU; }
		public LocationAttributeTypeDescriptor type() { return LocationAttributeTypes.LOCATION_CODE; }
		public String value() { return "M042"; }
	};

	public static LocationAttributeDescriptor CDI_KLINIK_EKSTEN_JENERAL_FAMASI_LOCATION_CODE = new LocationAttributeDescriptor() {
		public String uuid() { return "a9698409-c969-4ccc-bd50-a5cf5e694b5f"; }
		public LocationDescriptor location() { return MirebalaisLocations.CDI_KLINIK_EKSTEN_JENERAL_FAMASI; }
		public LocationAttributeTypeDescriptor type() { return LocationAttributeTypes.LOCATION_CODE; }
		public String value() { return "M043"; }
	};

	public static LocationAttributeDescriptor CDI_KLINIK_EKSTEN_JENERAL_LABORATWA_LOCATION_CODE = new LocationAttributeDescriptor() {
		public String uuid() { return "2dcc40bc-4700-4925-882c-710d74019f84"; }
		public LocationDescriptor location() { return MirebalaisLocations.CDI_KLINIK_EKSTEN_JENERAL_LABORATWA; }
		public LocationAttributeTypeDescriptor type() { return LocationAttributeTypes.LOCATION_CODE; }
		public String value() { return "M044"; }
	};

	public static LocationAttributeDescriptor CDI_KLINIK_EKSTEN_JENERAL_RADYOGRAFI_LOCATION_CODE = new LocationAttributeDescriptor() {
		public String uuid() { return "6446948f-4f1e-42f0-ba06-51b7e029eec3"; }
		public LocationDescriptor location() { return MirebalaisLocations.CDI_KLINIK_EKSTEN_JENERAL_RADYOGRAFI; }
		public LocationAttributeTypeDescriptor type() { return LocationAttributeTypes.LOCATION_CODE; }
		public String value() { return "M045"; }
	};

	public static LocationAttributeDescriptor CDI_KLINIK_EKSTEN_JENERAL_SAL_PWOSEDI_LOCATION_CODE = new LocationAttributeDescriptor() {
		public String uuid() { return "376cb73f-f8a5-4e5d-a62a-a19f7a8979c6"; }
		public LocationDescriptor location() { return MirebalaisLocations.CDI_KLINIK_EKSTEN_JENERAL_SAL_PWOSEDI; }
		public LocationAttributeTypeDescriptor type() { return LocationAttributeTypes.LOCATION_CODE; }
		public String value() { return "M046"; }
	};
}