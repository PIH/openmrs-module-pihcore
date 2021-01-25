package org.openmrs.module.pihcore.metadata.core.program;

import org.openmrs.module.metadatadeploy.descriptor.ProgramDescriptor;

import static org.openmrs.module.pihcore.PihCoreConstants.*;

public class TBProgram {

    public static ProgramDescriptor TB = new ProgramDescriptor() {
        public String name() { return "TB"; }
        public String description() { return "Tuberculosis Program"; }
        public String conceptUuid() { return TB_PROGRAM_CONCEPT_UUID; }
        @Override public String outcomesConceptUuid()  { return TB_PROGRAM_OUTCOME_CONCEPT_UUID; }
        public String uuid() { return "5682f919-a43b-4d4a-bcc3-ef0426162e23"; }
    };

}