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

import org.openmrs.module.emrapi.EmrApiConstants;
import org.openmrs.module.metadatadeploy.descriptor.PersonAttributeTypeDescriptor;

/**
 * Constants for all defined person attribute types
 */
public class PersonAttributeTypes {

	public static PersonAttributeTypeDescriptor TELEPHONE_NUMBER = new PersonAttributeTypeDescriptor() {
		public String uuid() { return "14d4f066-15f5-102d-96e4-000c29c2a5d7"; }
		public String name() { return "Telephone Number"; }
		public String description() { return "The telephone number for the person"; }
		public double sortWeight() { return 7; }
	};

	public static PersonAttributeTypeDescriptor TEST_PATIENT = new PersonAttributeTypeDescriptor() {
		public String uuid() { return EmrApiConstants.TEST_PATIENT_ATTRIBUTE_UUID; }
		public String name() { return "Test Patient"; }
		public String description() { return "Flag to describe if the patient was created for demonstration or testing purposes"; }
		public Class<?> format() { return Boolean.class; }
		public double sortWeight() { return 8; }
	};

	public static PersonAttributeTypeDescriptor PROVIDER_IDENTIFIER = new PersonAttributeTypeDescriptor() {
		public String uuid() { return "6de6c415-97a2-4cca-817a-9501cd9ef382"; }
		public String name() { return "Provider Identifier"; }
		public String description() { return ""; }
		public boolean searchable() { return true; }
		public double sortWeight() { return 12; }
	};

	public static PersonAttributeTypeDescriptor UNKNOWN_PATIENT = new PersonAttributeTypeDescriptor() {
		public String uuid() { return "8b56eac7-5c76-4b9c-8c6f-1deab8d3fc47"; }
		public String name() { return "Unknown patient"; }
		public String description() { return "Used to flag patients that cannot be identified during the check-in process"; }
		public double sortWeight() { return 13; }
	};

	public static PersonAttributeTypeDescriptor MOTHERS_FIRST_NAME = new PersonAttributeTypeDescriptor() {
		public String uuid() { return "8d871d18-c2cc-11de-8d13-0010c6dffd0f"; }
		public String name() { return "First Name of Mother"; }
		public String description() { return "First name of the patient's mother, used for identification"; }
		public double sortWeight() { return 14; }
	};

    @Deprecated  // now using obs to store birthplace
	public static PersonAttributeTypeDescriptor BIRTHPLACE = new PersonAttributeTypeDescriptor() {
		public String uuid() { return "8d8718c2-c2cc-11de-8d13-0010c6dffd0f"; }
		public String name() { return "Place of birth"; }
		public String description() { return "Location of persons birth, used for identification"; }
		public double sortWeight() { return 15; }
	};
}