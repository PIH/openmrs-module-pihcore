package org.openmrs.module.pihcore.metadata.liberia;

import org.openmrs.module.metadatadeploy.descriptor.PatientIdentifierTypeDescriptor;
import org.openmrs.patient.IdentifierValidator;

public class LiberiaPatientIdentifierTypes {

    public static PatientIdentifierTypeDescriptor LIBERIA_EMR_ID = new PatientIdentifierTypeDescriptor() {
        public String uuid() {
            return "0bc545e0-f401-11e4-b939-0800200c9a66";
        }

        public String name() {
            return "Liberia EMR ID";
        }

        public String description() {
            return "A unique identifier issued to all patients in the Liberia EMR.";
        }

        public Class<? extends IdentifierValidator> validator() {
            return null;
        }
    };

    public static PatientIdentifierTypeDescriptor SAMPLE_DOSSIER_NUMBER_ID = new PatientIdentifierTypeDescriptor() {
        public String uuid() {
            return "c31b0dd2-b484-41f0-8fce-bbe72e07843c";
        }

        public String name() {
            return "Sample Dossier Number";
        }

        public String description() {
            return "A sample dossier number used for demo purposes";
        }

        public Class<? extends IdentifierValidator> validator() {
            return null;
        }
    };

    // pretty much copied from Haiti Core
    public static PatientIdentifierTypeDescriptor BIOMETRIC_REF_NUMBER = new PatientIdentifierTypeDescriptor() {
        public String uuid() {
            return "e26ca279-8f57-44a5-9ed8-8cc16e90e559";
        }

        public String name() {
            return "Biometrics Reference Code";
        }

        public String description() {
            return "Code referencing a patient's record in an external biometrics system";
        }
    };

    public static PatientIdentifierTypeDescriptor MENTAL_HEALTH_MEDICAL_RECORD_NUMBER = new PatientIdentifierTypeDescriptor() {
        @Override
        public String name() {
            return "Mental Health Medical Record Number";
        }

        @Override
        public String description() {
            return "A unique identifier issued to all patients in the mental health program";
        }

        @Override
        public String uuid() {
            return "23507d1e-4f22-11ea-9717-645d86728797";
        }
    };

}
