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

import org.openmrs.module.metadatadeploy.descriptor.PersonAttributeTypeDescriptor;

/**
 * Constants for all defined person attribute types
 */
public class PersonAttributeTypes {

	public static PersonAttributeTypeDescriptor PROVIDER_IDENTIFIER = new PersonAttributeTypeDescriptor() {
		public String uuid() { return "6de6c415-97a2-4cca-817a-9501cd9ef382"; }
		public String name() { return "Provider Identifier"; }
		public String description() { return ""; }
		public boolean searchable() { return true; }
		public double sortWeight() { return 12; }
	};

	// telephone number, test patient, unknown patient, and mother's first name have been moved to Haiti Core module

	@Deprecated  // now using patient identifier to store biometrics
	public static PersonAttributeTypeDescriptor BIOMETRIC_REFERENCE_NUMBER = new PersonAttributeTypeDescriptor() {
		public String uuid() { return "602a3c35-abfd-4a06-93d8-c9b52fe2b860"; }
		public String name() { return "Biometric Reference Number"; }
		public String description() { return "Reference to the user in an external biometric matching system"; }
		public double sortWeight() { return 99; }
	};

    @Deprecated  // now using obs to store birthplace
	public static PersonAttributeTypeDescriptor BIRTHPLACE = new PersonAttributeTypeDescriptor() {
		public String uuid() { return "8d8718c2-c2cc-11de-8d13-0010c6dffd0f"; }
		public String name() { return "Place of birth"; }
		public String description() { return "Location of persons birth, used for identification"; }
		public double sortWeight() { return 15; }
	};
}