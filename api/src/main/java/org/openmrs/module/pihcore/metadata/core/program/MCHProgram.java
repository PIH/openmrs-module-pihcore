package org.openmrs.module.pihcore.metadata.core.program;

import org.openmrs.module.metadatadeploy.descriptor.ProgramDescriptor;
import org.openmrs.module.metadatadeploy.descriptor.ProgramWorkflowDescriptor;
import org.openmrs.module.metadatadeploy.descriptor.ProgramWorkflowStateDescriptor;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static org.openmrs.module.pihcore.PihCoreConstants.*;

public class MCHProgram {

    public static ProgramWorkflowStateDescriptor GROUP1 = new ProgramWorkflowStateDescriptor() {
        public String conceptUuid() { return GROUP1_UUID; }
        public Boolean initial() { return true; }
        public Boolean terminal() { return false; }
        public String uuid() { return "41a2753c-8a14-11e8-9a94-a6cf71072f73"; }
    };

    public static ProgramWorkflowStateDescriptor GROUP2 = new ProgramWorkflowStateDescriptor() {
        public String conceptUuid() { return GROUP2_UUID; }
        public Boolean initial() { return true; }
        public Boolean terminal() { return false; }
        public String uuid() { return "41a27d48-8a14-11e8-9a94-a6cf71072f73"; }
    };

    public static ProgramWorkflowDescriptor MOTHER_GROUP = new ProgramWorkflowDescriptor() {
        public String conceptUuid() { return MOTHER_GROUP_UUID; }
        public String uuid() { return "41a277d0-8a14-11e8-9a94-a6cf71072f73"; }
        @Override public Set<ProgramWorkflowStateDescriptor> states()
            { return new HashSet<ProgramWorkflowStateDescriptor>(Arrays.asList(GROUP1,GROUP2)); }
    };

    public static ProgramWorkflowStateDescriptor GROUPC1 = new ProgramWorkflowStateDescriptor() {
        public String conceptUuid() { return GROUP1_UUID; }
        public Boolean initial() { return true; }
        public Boolean terminal() { return false; }
        public String uuid() { return "41a28324-8a14-11e8-9a94-a6cf71072f73"; }
    };

    public static ProgramWorkflowStateDescriptor GROUPC2 = new ProgramWorkflowStateDescriptor() {
        public String conceptUuid() { return GROUP2_UUID; }
        public Boolean initial() { return true; }
        public Boolean terminal() { return false; }
        public String uuid() { return "41a28112-8a14-11e8-9a94-a6cf71072f73"; }
    };

    public static ProgramWorkflowDescriptor CHILD_GROUP = new ProgramWorkflowDescriptor() {
        public String conceptUuid() { return CHILD_GROUP_UUID; }
        public String uuid() { return "41a27ab4-8a14-11e8-9a94-a6cf71072f73"; }
        @Override public Set<ProgramWorkflowStateDescriptor> states()
        { return new HashSet<ProgramWorkflowStateDescriptor>(Arrays.asList(GROUPC1,GROUPC2)); }
    };

    public static ProgramDescriptor MCH = new ProgramDescriptor() {
        public String conceptUuid() { return MCH_PROGRAM_CONCEPT_UUID; }   // this concept is installed via metadata package
        public String name() { return "MCH"; }
        public String description() { return "Maternal and Child Health Program"; }
        @Override public String outcomesConceptUuid()  { return MCH_PROGRAM_OUTCOME_CONCEPT_UUID; }   // this concept is installed via metadata package
        public String uuid() { return "41a2715e-8a14-11e8-9a94-a6cf71072f73"; }
        @Override public Set<ProgramWorkflowDescriptor> workflows() { return new HashSet<ProgramWorkflowDescriptor>(Arrays.asList(MOTHER_GROUP,CHILD_GROUP)); }
    };

}
