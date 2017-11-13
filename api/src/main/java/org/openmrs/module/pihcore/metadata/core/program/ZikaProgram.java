package org.openmrs.module.pihcore.metadata.core.program;

import org.openmrs.module.metadatadeploy.descriptor.ProgramDescriptor;

import static org.openmrs.module.pihcore.PihCoreConstants.ZIKA_PROGRAM_CONCEPT_UUID;
import static org.openmrs.module.pihcore.PihCoreConstants.ZIKA_PROGRAM_OUTCOMES_CONCEPT_UUID;

public class ZikaProgram {

    public static ProgramDescriptor ZIKA = new ProgramDescriptor() {
        public String name() { return "Zika"; }
        public String description() { return "Zika Program"; }
        public String conceptUuid() { return ZIKA_PROGRAM_CONCEPT_UUID; }   // this concept is installed via metadata package
        @Override public String outcomesConceptUuid()  { return ZIKA_PROGRAM_OUTCOMES_CONCEPT_UUID; }   // this concept is installed via metadata package
        public String uuid() { return "3bea593a-9afd-4642-96a6-210b60f5aff2"; }
    };
}
