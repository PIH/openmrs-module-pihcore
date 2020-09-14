package org.openmrs.module.pihcore.metadata.core.program;

import org.openmrs.module.metadatadeploy.descriptor.ProgramDescriptor;
import org.openmrs.module.metadatadeploy.descriptor.ProgramWorkflowDescriptor;
import org.openmrs.module.metadatadeploy.descriptor.ProgramWorkflowStateDescriptor;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static org.openmrs.module.pihcore.PihCoreConstants.*;

public class HIVProgram {

    public static ProgramDescriptor HIV = new ProgramDescriptor() {
        public String conceptUuid() { return HIV_PROGRAM_CONCEPT_UUID; }   // this concept is installed via metadata package
        public String name() { return "HIV"; }
        public String description() { return "HIV Program"; }
        @Override public String outcomesConceptUuid()  { return HIV_PROGRAM_OUTCOMES_CONCEPT_UUID; }   // this concept is installed via metadata package
        public String uuid() { return "b1cb1fc1-5190-4f7a-af08-48870975dafc"; }
    };

}
