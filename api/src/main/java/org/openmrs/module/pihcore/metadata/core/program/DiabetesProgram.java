package org.openmrs.module.pihcore.metadata.core.program;

import org.openmrs.module.metadatadeploy.descriptor.ProgramDescriptor;

import static org.openmrs.module.pihcore.PihCoreConstants.DIABETES_PROGRAM_CONCEPT_UUID;
import static org.openmrs.module.pihcore.PihCoreConstants.DIABETES_PROGRAM_OUTCOMES_CONCEPT_UUID;

public class DiabetesProgram {

    public static ProgramDescriptor DIABETES = new ProgramDescriptor() {
        public String name() { return "Diabetes"; }
        public String description() { return "Diabetes Program"; }
        public String conceptUuid() { return DIABETES_PROGRAM_CONCEPT_UUID; }   // this concept is installed via metadata package
        @Override public String outcomesConceptUuid()  { return DIABETES_PROGRAM_OUTCOMES_CONCEPT_UUID; }   // this concept is installed via metadata package
        public String uuid() { return "3f038507-f4bc-4877-ade0-96ce170fc8eb"; }
    };

}


