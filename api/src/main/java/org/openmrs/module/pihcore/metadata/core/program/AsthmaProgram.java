package org.openmrs.module.pihcore.metadata.core.program;

import org.openmrs.module.metadatadeploy.descriptor.ProgramDescriptor;

import static org.openmrs.module.pihcore.PihCoreConstants.*;

public class AsthmaProgram {

    public static ProgramDescriptor ASTHMA = new ProgramDescriptor() {
        public String name() { return "Asthma"; }
        public String description() { return "Asthma Program"; }
        public String conceptUuid() { return ASTHMA_PROGRAM_CONCEPT_UUID; }
        @Override public String outcomesConceptUuid()  { return ASTHMA_PROGRAM_OUTCOMES_CONCEPT_UUID; }
        public String uuid() { return "2639449c-8764-4003-be5f-dba522b4b680"; }
    };

}


