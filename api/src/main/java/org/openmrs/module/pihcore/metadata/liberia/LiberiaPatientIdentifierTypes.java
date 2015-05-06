package org.openmrs.module.pihcore.metadata.liberia;

import org.openmrs.module.pihcore.descriptor.PatientIdentifierTypeDescriptor;
import org.openmrs.patient.IdentifierValidator;

public class LiberiaPatientIdentifierTypes {

    public static PatientIdentifierTypeDescriptor PLEEBO_EMR_ID = new PatientIdentifierTypeDescriptor() {
        public String uuid() { return "0bc545e0-f401-11e4-b939-0800200c9a66"; }
        public String name() { return "Pleebo EMR ID"; }
        public String description() { return "A unique identifier issued to all patients in the Pleebo EMR." ; }
        public Class<? extends IdentifierValidator> validator() { return null; }
    };

}
