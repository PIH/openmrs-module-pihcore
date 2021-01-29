package org.openmrs.module.pihcore.metadata.mexico;

import org.openmrs.PatientIdentifierType;
import org.openmrs.module.metadatadeploy.descriptor.PatientIdentifierTypeDescriptor;
import org.openmrs.patient.IdentifierValidator;

public class MexicoPatientIdentifierTypes {

    public static PatientIdentifierTypeDescriptor CHIAPAS_EMR_ID = new PatientIdentifierTypeDescriptor() {
        public String uuid() { return "506add39-794f-11e8-9bcd-74e5f916c5ec"; }
        public String name() { return "Chiapas EMR ID"; }
        public String description() { return "A unique identifier issued to all patients in the Chiapas EMR." ; }
        public Class<? extends IdentifierValidator> validator() { return null; }
    };

    public static PatientIdentifierTypeDescriptor CURP = new PatientIdentifierTypeDescriptor() {
        public String uuid() { return "c1fe3790-915a-4f03-861f-5e477f36cec0"; }
        public String name() { return "Mexican National ID"; }
        public String description() { return "A unique identifier given to every Mexican citizen by the government of Mexico." ; }
        public Class<? extends IdentifierValidator> validator() { return null; }
    };

    public static PatientIdentifierTypeDescriptor MEXICO_DOSSIER_NUMBER = new PatientIdentifierTypeDescriptor() {
        public String uuid() { return "9124b296-d14d-4222-862d-44867e728812"; }
        public String name() { return "Expediente"; }
        public String description() { return "Dossier number for identifying patient documents"; }
        public String format() { return "\\w{1,4}\\d{6}"; }
        public String formatDescription() { return "A000001"; }
        public PatientIdentifierType.LocationBehavior locationBehavior() { return PatientIdentifierType.LocationBehavior.REQUIRED; }
    };
}
