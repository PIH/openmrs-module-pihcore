package org.openmrs.module.pihcore.metadata.mexico;

import org.openmrs.module.metadatadeploy.descriptor.PatientIdentifierTypeDescriptor;
import org.openmrs.patient.IdentifierValidator;

public class MexicoPatientIdentifierTypes {

    public static PatientIdentifierTypeDescriptor CHIAPAS_EMR_ID = new PatientIdentifierTypeDescriptor() {
        public String uuid() { return "506add39-794f-11e8-9bcd-74e5f916c5ec"; }
        public String name() { return "Chiapas EMR ID"; }
        public String description() { return "A unique identifier issued to all patients in the Chiapas EMR." ; }
        public Class<? extends IdentifierValidator> validator() { return null; }
    };

}
