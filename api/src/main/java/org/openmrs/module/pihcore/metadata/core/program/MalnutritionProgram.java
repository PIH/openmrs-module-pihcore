package org.openmrs.module.pihcore.metadata.core.program;

import org.openmrs.module.metadatadeploy.descriptor.ProgramDescriptor;

import static org.openmrs.module.pihcore.PihCoreConstants.MALNUTRITION_PROGRAM_CONCEPT_UUID;
import static org.openmrs.module.pihcore.PihCoreConstants.MALNUTRITION_PROGRAM_OUTCOMES_CONCEPT_UUID;


public class MalnutritionProgram {

    public static ProgramDescriptor MALNUTRITION = new ProgramDescriptor() {
        public String name() { return "Malnutrition"; }
        public String description() { return "Malnutrition Program"; }
        public String conceptUuid() { return MALNUTRITION_PROGRAM_CONCEPT_UUID; }
        @Override public String outcomesConceptUuid()  { return MALNUTRITION_PROGRAM_OUTCOMES_CONCEPT_UUID; }
        public String uuid() { return "61e38de2-44f2-470e-99da-3e97e93d388f"; }
    };

}


