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
import org.openmrs.module.metadatadeploy.descriptor.LocationTagDescriptor;
import org.openmrs.module.paperrecord.PaperRecordConstants;
import org.openmrs.module.registrationcore.RegistrationCoreConstants;

/**
 * Constants for all defined location tags
 */
public class LocationTags {

	public static LocationTagDescriptor MEDICAL_RECORD_LOCATION = new LocationTagDescriptor() {
		public String uuid() { return "71c99f93-bc0c-4a44-b573-a7ac096ff636"; }
		public String name() { return PaperRecordConstants.LOCATION_TAG_MEDICAL_RECORD_LOCATION; }
		public String description() { return "Notes that this location is a valid identifier location for an identifier that references a paper medical record"; }
	};

	public static LocationTagDescriptor ARCHIVES_LOCATION = new LocationTagDescriptor() {
		public String uuid() { return "fa2c2030-65fb-11e4-9803-0800200c9a66"; }
		public String name() { return PaperRecordConstants.LOCATION_TAG_ARCHIVES_LOCATION; }
		public String description() { return "A location that serves as an archives for storing medical records"; }
	};

    public static LocationTagDescriptor IDENTIFIER_ASSIGNMENT_LOCATION = new LocationTagDescriptor() {
        public String uuid() { return "4f8bb500-58bc-11e5-a837-0800200c9a66"; }
        public String name() { return RegistrationCoreConstants.LOCATION_TAG_IDENTIFIER_ASSIGNMENT_LOCATION; }
        public String description() { return "A location that that can be associated with a patient identifier"; }
    };

	public static LocationTagDescriptor LOGIN_LOCATION = new LocationTagDescriptor() {
		public String uuid() { return "b8bbf83e-645f-451f-8efe-a0db56f09676"; }
		public String name() { return EmrApiConstants.LOCATION_TAG_SUPPORTS_LOGIN; }
		public String description() { return "When a user logs in and chooses a session location, they may only choose one with this tag"; }
	};

	public static LocationTagDescriptor VISIT_LOCATION = new LocationTagDescriptor() {
		public String uuid() { return "0967d73d-f3c9-492b-abed-356478610a94"; }
		public String name() { return EmrApiConstants.LOCATION_TAG_SUPPORTS_VISITS; }
		public String description() { return "Visits are only allowed to happen at locations tagged with this location tag or at locations that descend from a location tagged with this tag."; }
	};

	public static LocationTagDescriptor ADMISSION_LOCATION = new LocationTagDescriptor() {
		public String uuid() { return "f5b9737b-14d5-402b-8475-dd558808e172"; }
		public String name() { return EmrApiConstants.LOCATION_TAG_SUPPORTS_ADMISSION; }
		public String description() { return "Patients may only be admitted to inpatient care in a location with this tag"; }
	};

	public static LocationTagDescriptor TRANSFER_LOCAITON = new LocationTagDescriptor() {
		public String uuid() { return "9783aba6-df7b-4969-be6e-1e03e7a08965"; }
		public String name() { return EmrApiConstants.LOCATION_TAG_SUPPORTS_TRANSFER; }
		public String description() { return "Patients may only be transfer to inpatient care in a location with this tag"; }
	};

	public static LocationTagDescriptor CONSULT_NOTE_LOCATION = new LocationTagDescriptor() {
		public String uuid() { return "dea8febf-0bbe-4111-8152-a9cf7df622b6"; }
		public String name() { return "Consult Note Location"; }
		public String description() { return "A location where a consult note can be written"; }
	};

	public static LocationTagDescriptor SURGERY_NOTE_LOCATION = new LocationTagDescriptor() {
		public String uuid() { return "8ce9abcb-b0d9-4897-bc65-d8d2d3362b67"; }
		public String name() { return "Surgery Note Location"; }
		public String description() { return "A location where a surgery note can be written"; }
	};

	public static LocationTagDescriptor ED_NOTE_LOCATION = new LocationTagDescriptor() {
		public String uuid() { return "fe742fba-840a-4155-bfc4-a5b35ac1bb03"; }
		public String name() { return "ED Note Location"; }
		public String description() { return "A location where an ED note can be written"; }
	};

    public static LocationTagDescriptor ADMISSION_NOTE_LOCATION = new LocationTagDescriptor() {
        public String uuid() { return "a0cc8ad0-e53e-11e4-b571-0800200c9a66"; }
        public String name() { return "Admission Note Location"; }
        public String description() { return "A location where an Admission note can be written"; }
    };

	public static LocationTagDescriptor DISPENSING_LOCATION = new LocationTagDescriptor() {
		public String uuid() { return "c42b7bc3-b34d-4b8f-9796-09208f9dfd72"; }
		public String name() { return EmrApiConstants.LOCATION_TAG_SUPPORTS_DISPENSING; }
		public String description() { return "A location where a pharmacist or pharmacist aide can dispensed medication."; }
	};

	public static LocationTagDescriptor APPOINTMENT_LOCATION = new LocationTagDescriptor() {
		public String uuid() { return "2b5c7110-d571-4f5f-b84e-500070b40ef8"; }
		public String name() { return "Appointment Location"; }
		public String description() { return "Signifies a Location where appointments can be scheduled"; }
	};

    public static LocationTagDescriptor VITALS_LOCATION = new LocationTagDescriptor() {
        public String uuid() { return "d9865139-dfb4-11e4-bccc-56847afe9799"; }
        public String name() { return "Vitals Location"; }
        public String description() { return "Signifies a Location where the vitals app and note should be available"; }
    };

    public static LocationTagDescriptor INPATIENTS_APP_LOCATION = new LocationTagDescriptor() {
        public String uuid() { return "5e824f3a-dfb8-11e4-bccc-56847afe9799"; }
        public String name() { return "Inpatients App Location"; }
        public String description() { return "Signifies a Location where the inpatients app should be available"; }
    };

    public static LocationTagDescriptor CHECKIN_LOCATION = new LocationTagDescriptor() {
        public String uuid() { return "24ffeea6-dfbb-11e4-bccc-56847afe9799"; }
        public String name() { return "Check-In Location"; }
        public String description() { return "Signifies a Location where the check-in app and form should be available"; }
    };

    public static LocationTagDescriptor REGISTRATION_LOCATION = new LocationTagDescriptor() {
        public String uuid() { return "8fa112e0-e506-11e4-b571-0800200c9a66"; }
        public String name() { return "Registration Location"; }
        public String description() { return "Signifies a Location where the registration app should be available"; }
    };

    public static LocationTagDescriptor ED_REGISTRATION_LOCATION = new LocationTagDescriptor() {
        public String uuid() { return "a6f68610-e53c-11e4-b571-0800200c9a66"; }
        public String name() { return "Registration and Check-In Location"; }
        public String description() { return "Signifies a Location where the combined registration/check-in app should be available"; }
    };

    public static LocationTagDescriptor ORDER_RADIOLOGY_STUDY_LOCATION = new LocationTagDescriptor() {
        public String uuid() { return "0ba7b0d0-e1ef-11e4-b571-0800200c9a66"; }
        public String name() { return "Order Radiology Study Location"; }
        public String description() { return "Signifies a Location where a radiology study can be ordered"; }
    };

    public static LocationTagDescriptor ONCOLOGY_CONSULT_LOCATION = new LocationTagDescriptor() {
        public String uuid() { return "993af5d0-57ea-11e5-a837-0800200c9a66"; }
        public String name() { return "Oncology Consult Location"; }
        public String description() { return "Signifies a Location where oncology consult and intake forms can be filled out"; }
    };

    public static LocationTagDescriptor CHEMOTHERAPY_LOCATION = new LocationTagDescriptor() {
        public String uuid() { return "a2487710-57ea-11e5-a837-0800200c9a66"; }
        public String name() { return "Chemotherapy Location"; }
        public String description() { return "Signifies a Location where chemotherapy is performed, and therefore a chemotherapy treament note can be filled out"; }
    };

	public static LocationTagDescriptor NCD_CONSULT_LOCATION = new LocationTagDescriptor() {
		public String uuid() { return "d3bb01c0-00f3-11e6-a837-0800200c9a66"; }
		public String name() { return "NCD Consult Location"; }
		public String description() { return "Signifies a Location where NCD consult form can be filled out"; }
	};

	public static LocationTagDescriptor HIV_CONSULT_LOCATION = new LocationTagDescriptor() {
		public String uuid() { return "d31ced52-9103-4b01-81ae-69f1bb216a6a"; }
		public String name() { return "HIV Consult Location"; }
		public String description() { return "Signifies a Location where HIV consult form can be filled out"; }
	};

	public static LocationTagDescriptor PRIMARY_CARE_CONSULT_LOCATION = new LocationTagDescriptor() {
		public String uuid() { return "fe895df1-f228-45db-9f14-d27b72f91319"; }
		public String name() { return "Primary Care Consult Location"; }
		public String description() { return "Signifies a Location where primary care consult form can be filled out"; }
	};


	public static LocationTagDescriptor LAB_RESULTS_LOCATION = new LocationTagDescriptor() {
		public String uuid() { return "5a1a98d0-0805-11e6-a837-0800200c9a66"; }
		public String name() { return "Lab Results Location"; }
		public String description() { return "Signifies a Location where a Lab Results form can be filled out"; }
	};

	public static LocationTagDescriptor ED_TRIAGE_LOCATION = new LocationTagDescriptor() {
		@Override
		public String uuid() { return "641ed690-3fb2-11e6-bdf4-0800200c9a66"; }
		public String name() { return "ED Triage Location"; }
		public String description() { return "Signifies a Location where an ED Triage form can be filled out"; }
	};

	public static LocationTagDescriptor MCH_LOCATION = new LocationTagDescriptor() {
		@Override
		public String uuid() { return "87037934-91a6-11e8-9eb6-529269fb1459"; }
		public String name() { return "Maternal and Child Location"; }
		public String description() { return "Signifies Locations where Maternal and Child health is provided"; }
	};

	public static LocationTagDescriptor ORDER_PATHOLOGY_LOCATION = new LocationTagDescriptor() {
		public String uuid() { return "728f5f07-f748-4efa-9abe-b6df64e84c92"; }
		public String name() { return "Order Pathology Location"; }
		public String description() { return ""; }
		public boolean retired() { return true; }
	};

	public static LocationTagDescriptor RETIRED_OUTPATIENT_TRANSFER_LOCATION = new LocationTagDescriptor() {
		public String uuid() { return "84864264-5fda-4626-b22f-4b690b7279f3"; }
		public String name() { return "Outpatient Transfer Location"; }
		public String description() { return ""; }
		public boolean retired() { return true; }
	};

	public static LocationTagDescriptor RETIRED_INPATIENT_TRANSFER_LOCATION = new LocationTagDescriptor() {
		public String uuid() { return "160a6e45-9274-4dd8-abf7-e46af8cbb41a"; }
		public String name() { return "Inpatient Transfer Location"; }
		public String description() { return ""; }
		public boolean retired() { return true; }
	};

}
