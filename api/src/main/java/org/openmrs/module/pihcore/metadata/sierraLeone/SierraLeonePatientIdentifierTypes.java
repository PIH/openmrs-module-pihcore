package org.openmrs.module.pihcore.metadata.sierraLeone;

import org.openmrs.module.pihcore.descriptor.PatientIdentifierTypeDescriptor;
import org.openmrs.patient.IdentifierValidator;

public class SierraLeonePatientIdentifierTypes {

    public static PatientIdentifierTypeDescriptor WELLBODY_EMR_ID = new PatientIdentifierTypeDescriptor() {
        public String uuid() { return "1a2acce0-7426-11e5-a837-0800200c9a66"; }
        public String name() { return "Wellbody EMR ID"; }
        public String description() { return "A unique identifier issued to all patients in the Wellbody EMR." ; }
        public Class<? extends IdentifierValidator> validator() { return null; }
    };
}
