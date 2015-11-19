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

import org.openmrs.customdatatype.datatype.FreeTextDatatype;
import org.openmrs.module.metadatadeploy.descriptor.LocationAttributeTypeDescriptor;
import org.openmrs.module.printer.PrinterDatatype;

/**
 * Constants for all defined location types
 */
public class LocationAttributeTypes {

	public static LocationAttributeTypeDescriptor LOCATION_CODE = new LocationAttributeTypeDescriptor() {
		public String uuid() { return "64f01c78-191d-4947-a201-7e0a7f0caf21"; }
		public String name() { return "Location Code"; }
		public String description() { return "Unique identifier assigned to the location.  (Currently used as a key when exchanging information with McKesson PACS via the PACS Integration module.)"; }
		public Class<?> datatype() { return FreeTextDatatype.class; }
	};

	public static LocationAttributeTypeDescriptor DEFAULT_LABEL_PRINTER = new LocationAttributeTypeDescriptor() {
		public String uuid() { return "bd6c1c10-38d3-11e2-81c1-0800200c9a66"; }
		public String name() { return "Default Label Printer"; }
		public String description() { return "The default label printer for this location"; }
		public Class<?> datatype() { return PrinterDatatype.class; }
		public String datatypeConfig() { return "LABEL"; }
	};

	public static LocationAttributeTypeDescriptor DEFAULT_ID_CARD_PRINTER = new LocationAttributeTypeDescriptor() {
		public String uuid() { return "b48ef9a0-38d3-11e2-81c1-0800200c9a66"; }
		public String name() { return "Default ID card Printer"; }
		public String description() { return "The default id card printer for this location"; }
		public Class<?> datatype() { return PrinterDatatype.class; }
		public String datatypeConfig() { return "ID_CARD"; }
	};

	public static LocationAttributeTypeDescriptor DEFAULT_WRISTBAND_PRINTER = new LocationAttributeTypeDescriptor() {
		public String uuid() { return "7f73ad30-0b89-11e4-9191-0800200c9a66"; }
		public String name() { return "Default Wristband Printer"; }
		public String description() { return "The default wristband printer for this location"; }
		public Class<?> datatype() { return PrinterDatatype.class; }
		public String datatypeConfig() { return "WRISTBAND"; }
	};

	public static LocationAttributeTypeDescriptor NAME_TO_PRINT_ON_ID_CARD = new LocationAttributeTypeDescriptor() {
		public String uuid() { return "a5fb5770-409a-11e2-a25f-0800200c9a66"; }
		public String name() { return "Name to print on ID card"; }
		public String description() { return "The name to use when printing a location on an id card"; }
		public Class<?> datatype() { return FreeTextDatatype.class; }
	};
}