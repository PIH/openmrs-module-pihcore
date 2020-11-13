package org.openmrs.module.pihcore.metadata.core.program;

import org.openmrs.module.metadatadeploy.descriptor.ProgramDescriptor;
import org.openmrs.module.metadatadeploy.descriptor.ProgramWorkflowDescriptor;
import org.openmrs.module.metadatadeploy.descriptor.ProgramWorkflowStateDescriptor;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static org.openmrs.module.pihcore.PihCoreConstants.*;

public class OVCProgram {

    public static ProgramDescriptor OVC = new ProgramDescriptor() {
        public String conceptUuid() { return OVC_PROGRAM_CONCEPT_UUID; }
        public String name() { return "OVC"; }
        public String description() { return "USAID orphans and vulnerable children program"; }
        public String uuid() { return "b1cb1fc1-5190-4f7a-af08-48870975dafc"; }

        @Override public String outcomesConceptUuid()  { return OVC_PROGRAM_OUTCOMES_CONCEPT_UUID; }
    };

}
