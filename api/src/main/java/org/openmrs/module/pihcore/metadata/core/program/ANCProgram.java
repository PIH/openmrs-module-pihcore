/*
 * A basic MCH program. Default workflows. Pregnancy-specific outcomes.
 */
package org.openmrs.module.pihcore.metadata.core.program;

import org.openmrs.module.metadatadeploy.descriptor.ProgramDescriptor;

import static org.openmrs.module.pihcore.PihCoreConstants.ANC_PROGRAM_CONCEPT_UUID;
import static org.openmrs.module.pihcore.PihCoreConstants.ANC_PROGRAM_OUTCOME_CONCEPT_UUID;

public class ANCProgram {

    public static ProgramDescriptor ANC = new ProgramDescriptor() {
        public String name() { return "ANC"; }
        public String description() { return "Antenatal Care Program"; }
        public String conceptUuid() { return ANC_PROGRAM_CONCEPT_UUID; }
        @Override public String outcomesConceptUuid()  { return ANC_PROGRAM_OUTCOME_CONCEPT_UUID; }
        public String uuid() { return "d830a5c1-30a2-4943-93a0-f918772496ec"; }
    };

}
