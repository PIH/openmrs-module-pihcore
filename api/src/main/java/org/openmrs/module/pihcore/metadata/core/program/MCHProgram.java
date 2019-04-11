/*
 * A basic MCH program. Default workflows. Pregnancy-specific outcomes.
 */
package org.openmrs.module.pihcore.metadata.core.program;

import org.openmrs.module.metadatadeploy.descriptor.ProgramDescriptor;

import static org.openmrs.module.pihcore.PihCoreConstants.*;

public class MCHProgram {

    public static ProgramDescriptor MCH = new ProgramDescriptor() {
        public String name() { return "MCH"; }
        public String description() { return "Maternal and Child Health Program"; }
        public String conceptUuid() { return MCH_PROGRAM_CONCEPT_UUID; }
        @Override public String outcomesConceptUuid()  { return MCH_PROGRAM_OUTCOME_CONCEPT_UUID; }
        public String uuid() { return "41a2715e-8a14-11e8-9a94-a6cf71072f73"; }
    };

}
