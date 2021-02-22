package org.openmrs.module.pihcore.metadata.sierraLeone;

import org.openmrs.module.metadatadeploy.descriptor.PatientIdentifierTypeDescriptor;
import org.openmrs.patient.IdentifierValidator;

public class SierraLeonePatientIdentifierTypes {

    public static PatientIdentifierTypeDescriptor WELLBODY_EMR_ID = new PatientIdentifierTypeDescriptor() {
        public String uuid() { return "1a2acce0-7426-11e5-a837-0800200c9a66"; }
        public String name() { return "Wellbody EMR ID"; }
        public String description() { return "A unique identifier issued to all patients in the Wellbody EMR." ; }
        public Class<? extends IdentifierValidator> validator() { return null; }
    };

    public static PatientIdentifierTypeDescriptor KGH_EMR_ID = new PatientIdentifierTypeDescriptor() {
        public String uuid() { return "c09a1d24-7162-11eb-8aa6-0242ac110002"; }
        public String name() { return "KGH EMR ID"; }
        public String description() { return "A unique identifier issued to all patients in the KGH EMR." ; }
        public Class<? extends IdentifierValidator> validator() { return null; }
    };
}
