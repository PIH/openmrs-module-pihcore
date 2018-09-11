package org.openmrs.module.pihcore.metadata.core.program;

import org.openmrs.module.metadatadeploy.descriptor.ProgramDescriptor;

import static org.openmrs.module.pihcore.PihCoreConstants.HYPERTENSION_PROGRAM_CONCEPT_UUID;
import static org.openmrs.module.pihcore.PihCoreConstants.HYPERTENSION_PROGRAM_OUTCOMES_CONCEPT_UUID;

public class HypertensionProgram {

    public static ProgramDescriptor HYPERTENSION = new ProgramDescriptor() {
        public String name() { return "Hypertension"; }
        public String description() { return "Hypertension Program"; }
        public String conceptUuid() { return HYPERTENSION_PROGRAM_CONCEPT_UUID; }   // this concept is installed via metadata package
        @Override public String outcomesConceptUuid()  { return HYPERTENSION_PROGRAM_OUTCOMES_CONCEPT_UUID; }   // this concept is installed via metadata package
        public String uuid() { return "6959057e-9a5c-40ba-a878-292ba4fc35bc"; }
    };

}


