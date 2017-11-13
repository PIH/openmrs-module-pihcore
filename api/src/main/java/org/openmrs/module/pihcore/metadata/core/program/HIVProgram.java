package org.openmrs.module.pihcore.metadata.core.program;

import org.openmrs.module.metadatadeploy.descriptor.ProgramDescriptor;
import org.openmrs.module.metadatadeploy.descriptor.ProgramWorkflowDescriptor;
import org.openmrs.module.metadatadeploy.descriptor.ProgramWorkflowStateDescriptor;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static org.openmrs.module.pihcore.PihCoreConstants.HIV_PROGRAM_CONCEPT_UUID;
import static org.openmrs.module.pihcore.PihCoreConstants.HIV_PROGRAM_ON_ART_UUID;
import static org.openmrs.module.pihcore.PihCoreConstants.HIV_PROGRAM_OUTCOMES_CONCEPT_UUID;
import static org.openmrs.module.pihcore.PihCoreConstants.HIV_PROGRAM_PRE_ART_UUID;
import static org.openmrs.module.pihcore.PihCoreConstants.HIV_PROGRAM_TREATMENT_STATUS_UUID;

public class HIVProgram {

    public static ProgramWorkflowStateDescriptor HIV_PRE_ART = new ProgramWorkflowStateDescriptor() {
        public String conceptUuid() { return HIV_PROGRAM_PRE_ART_UUID; }
        // TODO what exactly do initial and terminal mean again and what should these be?
        public Boolean initial() { return true; }
        public Boolean terminal() { return false; }
        public String uuid() { return "60030941-c0a3-4b84-92e8-2c201fb8102b"; }
    };

    public static ProgramWorkflowStateDescriptor HIV_ON_ART = new ProgramWorkflowStateDescriptor() {
        public String conceptUuid() { return HIV_PROGRAM_ON_ART_UUID; }
        // TODO what exactly  do initial and terminal mean again and what should these be?
        public Boolean initial() { return false; }
        public Boolean terminal() { return false; }
        public String uuid() { return "490a8b6f-4538-4c85-ace9-1535fc634ba7"; }
    };

    public static ProgramWorkflowDescriptor TREATMENT_STATUS = new ProgramWorkflowDescriptor() {
        public String conceptUuid() { return HIV_PROGRAM_TREATMENT_STATUS_UUID; }
        public String uuid() { return "f5022f7d-cdb2-491c-9f06-4f87c9877d17"; }
        @Override public Set<ProgramWorkflowStateDescriptor> states() { return new HashSet<ProgramWorkflowStateDescriptor>(Arrays.asList(HIV_PRE_ART, HIV_ON_ART)); }
    };

    public static ProgramDescriptor HIV = new ProgramDescriptor() {
        public String conceptUuid() { return HIV_PROGRAM_CONCEPT_UUID; }   // this concept is installed via metadata package
        public String name() { return "HIV"; }
        public String description() { return "HIV Program"; }
        @Override public String outcomesConceptUuid()  { return HIV_PROGRAM_OUTCOMES_CONCEPT_UUID; }   // this concept is installed via metadata package
        public String uuid() { return "b1cb1fc1-5190-4f7a-af08-48870975dafc"; }
        @Override public Set<ProgramWorkflowDescriptor> workflows() { return Collections.singleton(TREATMENT_STATUS); }
    };


}
