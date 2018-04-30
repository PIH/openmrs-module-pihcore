package org.openmrs.module.pihcore.metadata.core.program;

import org.openmrs.module.metadatadeploy.descriptor.ProgramDescriptor;

import static org.openmrs.module.pihcore.PihCoreConstants.MENTAL_HEALTH_PROGRAM_CONCEPT_UUID;
import static org.openmrs.module.pihcore.PihCoreConstants.MENTAL_HEALTH_PROGRAM_OUTCOMES_CONCEPT_UUID;

public class MentalHealthProgram {

    public static ProgramDescriptor MENTAL_HEALTH = new ProgramDescriptor() {
        public String name() { return "Mental Health"; }
        public String description() { return "Mental Health Program"; }
        public String conceptUuid() { return MENTAL_HEALTH_PROGRAM_CONCEPT_UUID; }   // this concept is installed via metadata package
        @Override public String outcomesConceptUuid()  { return MENTAL_HEALTH_PROGRAM_OUTCOMES_CONCEPT_UUID; }   // this concept is installed via metadata package
        public String uuid() { return "0e69c3ab-1ccb-430b-b0db-b9760319230f"; }
    };

}


