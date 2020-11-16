package org.openmrs.module.pihcore.metadata.core.program;

import org.openmrs.module.metadatadeploy.descriptor.ProgramDescriptor;

import static org.openmrs.module.pihcore.PihCoreConstants.OVC_PROGRAM_CONCEPT_UUID;
import static org.openmrs.module.pihcore.PihCoreConstants.OVC_PROGRAM_OUTCOMES_CONCEPT_UUID;

public class OVCProgram {

    public static ProgramDescriptor OVC = new ProgramDescriptor() {
        public String conceptUuid() { return OVC_PROGRAM_CONCEPT_UUID; }
        public String name() { return "OVC"; }
        public String description() { return "USAID orphans and vulnerable children program"; }
        public String uuid() { return "e1b2f0b5-6d56-4500-8523-0ba71e75d897"; }

        @Override public String outcomesConceptUuid()  { return OVC_PROGRAM_OUTCOMES_CONCEPT_UUID; }
    };

}
