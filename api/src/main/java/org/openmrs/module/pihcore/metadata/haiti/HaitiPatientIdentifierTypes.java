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

package org.openmrs.module.pihcore.metadata.haiti;

import org.openmrs.PatientIdentifierType;
import org.openmrs.module.idgen.validator.LuhnMod30IdentifierValidator;
import org.openmrs.module.metadatadeploy.descriptor.PatientIdentifierTypeDescriptor;
import org.openmrs.patient.IdentifierValidator;

/**
 * Constants for all defined patient identifier types
 */
public class HaitiPatientIdentifierTypes {

	public static PatientIdentifierTypeDescriptor ZL_EMR_ID = new PatientIdentifierTypeDescriptor() {
		public String uuid() { return "a541af1e-105c-40bf-b345-ba1fd6a59b85"; }
		public String name() { return "ZL EMR ID"; }
		public String description() { return "A unique identifier issued to all patients by the ZL EMR.  Blocks of this identifier are issued to each site to prevent duplication, so the identifier is unique across all sites.  The identifier uses six digits and is alphanumeric base 30, omitting the letters B, I, O, Q, S and Z as these can be confused with 8, 1, 0, 0, 5 and 2"; }
		public Class<? extends IdentifierValidator> validator() { return LuhnMod30IdentifierValidator.class; }
	};

	public static PatientIdentifierTypeDescriptor DOSSIER_NUMBER = new PatientIdentifierTypeDescriptor() {
		public String uuid() { return "e66645eb-03a8-4991-b4ce-e87318e37566"; }
		public String name() { return "Nimewo Dosye"; }
		public String description() { return "Patient Dossier number"; }
		public String format() { return "\\w{1,3}\\d{6}"; }
		public String formatDescription() { return "A000001"; }
		public PatientIdentifierType.LocationBehavior locationBehavior() { return PatientIdentifierType.LocationBehavior.REQUIRED; }
	};

	public static PatientIdentifierTypeDescriptor EXTERNAL_DOSSIER_NUMBER = new PatientIdentifierTypeDescriptor() {
		public String uuid() { return "9dbea4d4-35a9-4793-959e-952f2a9f5347"; }
		public String name() { return "External Nimewo Dosye"; }
		public String description() { return "External Dossier number"; }
	};

	public static PatientIdentifierTypeDescriptor HIVEMR_V1 = new PatientIdentifierTypeDescriptor() {
		public String uuid() { return "139766e8-15f5-102d-96e4-000c29c2a5d7"; }
		public String name() { return "HIVEMR-V1"; }
		public String description() { return "Internal EMR ID for this Patient in the Haiti EMR V1 system"; }
	};

	public static PatientIdentifierTypeDescriptor USER_ENTERED_REF_NUMBER = new PatientIdentifierTypeDescriptor() {
		public String uuid() { return "d9009bd0-eac9-11e5-a837-0800200c9a66"; }
		public String name() { return "User-Entered Reference Number"; }
		public String description() { return "Any user-entered reference number not assigned by the system; for instance, allows the user to store a dossier number not assigned by the current system"; }
	};

	public static PatientIdentifierTypeDescriptor DENTAL_DOSSIER_NUMBER = new PatientIdentifierTypeDescriptor() {
		public String uuid() { return "b5d0a5bd-adf3-4fe0-a231-5a488f6d2c61"; }
		public String name() { return "Dental Dossier Number"; }
		public String description() { return "Dental clinic dossier number"; }
	};
}