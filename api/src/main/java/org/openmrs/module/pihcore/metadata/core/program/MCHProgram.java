package org.openmrs.module.pihcore.metadata.core.program;

import org.openmrs.module.metadatadeploy.descriptor.ProgramDescriptor;
import org.openmrs.module.metadatadeploy.descriptor.ProgramWorkflowDescriptor;
import org.openmrs.module.metadatadeploy.descriptor.ProgramWorkflowStateDescriptor;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static org.openmrs.module.pihcore.PihCoreConstants.*;

public class MCHProgram {

    public static ProgramDescriptor MCH = new ProgramDescriptor() {
        public String name() { return "MCH"; }
        public String description() { return "Maternal and Child Health Program"; }
        public String conceptUuid() { return MCH_PROGRAM_CONCEPT_UUID; }   // this concept is installed via metadata package
        @Override public String outcomesConceptUuid()  { return MCH_PROGRAM_OUTCOME_CONCEPT_UUID; }   // this concept is installed via metadata package
        public String uuid() { return "41a2715e-8a14-11e8-9a94-a6cf71072f73"; }
        // @Override public Set<ProgramWorkflowDescriptor> workflows() { return new HashSet<ProgramWorkflowDescriptor>(Arrays.asList(MOTHER_GROUP,CHILD_GROUP)); }
    };

    // ToDo:  Delete ALL these after they are UNINSTALLED
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

    public static ProgramWorkflowStateDescriptor GROUP3 = new ProgramWorkflowStateDescriptor() {
        public String conceptUuid() { return GROUP3_UUID; }
        public Boolean initial() { return true; }
        public Boolean terminal() { return false; }
        public String uuid() { return "ffef535c-8eb5-11e8-9eb6-529269fb1459"; }
    };

    public static ProgramWorkflowStateDescriptor GROUP4 = new ProgramWorkflowStateDescriptor() {
        public String conceptUuid() { return GROUP4_UUID; }
        public Boolean initial() { return true; }
        public Boolean terminal() { return false; }
        public String uuid() { return "ffef574e-8eb5-11e8-9eb6-529269fb1459"; }
    };

    public static ProgramWorkflowStateDescriptor GROUP5 = new ProgramWorkflowStateDescriptor() {
        public String conceptUuid() { return GROUP5_UUID; }
        public Boolean initial() { return true; }
        public Boolean terminal() { return false; }
        public String uuid() { return "ffef59ba-8eb5-11e8-9eb6-529269fb1459"; }
    };

    public static ProgramWorkflowStateDescriptor GROUP6 = new ProgramWorkflowStateDescriptor() {
        public String conceptUuid() { return GROUP6_UUID; }
        public Boolean initial() { return true; }
        public Boolean terminal() { return false; }
        public String uuid() { return "ffef5bfe-8eb5-11e8-9eb6-529269fb1459"; }
    };

    public static ProgramWorkflowStateDescriptor GROUP7 = new ProgramWorkflowStateDescriptor() {
        public String conceptUuid() { return GROUP7_UUID; }
        public Boolean initial() { return true; }
        public Boolean terminal() { return false; }
        public String uuid() { return "ffef6144-8eb5-11e8-9eb6-529269fb1459"; }
    };

    public static ProgramWorkflowStateDescriptor GROUP8 = new ProgramWorkflowStateDescriptor() {
        public String conceptUuid() { return GROUP8_UUID; }
        public Boolean initial() { return true; }
        public Boolean terminal() { return false; }
        public String uuid() { return "ffef63b0-8eb5-11e8-9eb6-529269fb1459"; }
    };

    public static ProgramWorkflowStateDescriptor GROUP9 = new ProgramWorkflowStateDescriptor() {
        public String conceptUuid() { return GROUP9_UUID; }
        public Boolean initial() { return true; }
        public Boolean terminal() { return false; }
        public String uuid() { return "ffef664e-8eb5-11e8-9eb6-529269fb1459"; }
    };

    public static ProgramWorkflowStateDescriptor GROUP10 = new ProgramWorkflowStateDescriptor() {
        public String conceptUuid() { return GROUP10_UUID; }
        public Boolean initial() { return true; }
        public Boolean terminal() { return false; }
        public String uuid() { return "ffef68ba-8eb5-11e8-9eb6-529269fb1459"; }
    };

    public static ProgramWorkflowStateDescriptor GROUP11 = new ProgramWorkflowStateDescriptor() {
        public String conceptUuid() { return GROUP11_UUID; }
        public Boolean initial() { return true; }
        public Boolean terminal() { return false; }
        public String uuid() { return "ffef6afe-8eb5-11e8-9eb6-529269fb1459"; }
    };

    public static ProgramWorkflowStateDescriptor GROUP12 = new ProgramWorkflowStateDescriptor() {
        public String conceptUuid() { return GROUP12_UUID; }
        public Boolean initial() { return true; }
        public Boolean terminal() { return false; }
        public String uuid() { return "ffef6d42-8eb5-11e8-9eb6-529269fb1459"; }
    };

    public static ProgramWorkflowDescriptor MOTHER_GROUP = new ProgramWorkflowDescriptor() {
        public String conceptUuid() { return MOTHER_GROUP_UUID; }
        public String uuid() { return "41a277d0-8a14-11e8-9a94-a6cf71072f73"; }
        @Override public Set<ProgramWorkflowStateDescriptor> states()
            { return new HashSet<ProgramWorkflowStateDescriptor>(Arrays.asList(GROUP1,GROUP2,GROUP3,GROUP4,GROUP5,GROUP6,GROUP7,GROUP8,GROUP9,GROUP10,GROUP11,GROUP12)); }
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

    public static ProgramWorkflowStateDescriptor GROUPC3 = new ProgramWorkflowStateDescriptor() {
        public String conceptUuid() { return GROUP3_UUID; }
        public Boolean initial() { return true; }
        public Boolean terminal() { return false; }
        public String uuid() { return "ffef727e-8eb5-11e8-9eb6-529269fb1459"; }
    };

    public static ProgramWorkflowStateDescriptor GROUPC4 = new ProgramWorkflowStateDescriptor() {
        public String conceptUuid() { return GROUP4_UUID; }
        public Boolean initial() { return true; }
        public Boolean terminal() { return false; }
        public String uuid() { return "ffef777e-8eb5-11e8-9eb6-529269fb1459"; }
    };

    public static ProgramWorkflowStateDescriptor GROUPC5 = new ProgramWorkflowStateDescriptor() {
        public String conceptUuid() { return GROUP5_UUID; }
        public Boolean initial() { return true; }
        public Boolean terminal() { return false; }
        public String uuid() { return "ffef7a44-8eb5-11e8-9eb6-529269fb1459"; }
    };

    public static ProgramWorkflowStateDescriptor GROUPC6 = new ProgramWorkflowStateDescriptor() {
        public String conceptUuid() { return GROUP6_UUID; }
        public Boolean initial() { return true; }
        public Boolean terminal() { return false; }
        public String uuid() { return "ffef7ca6-8eb5-11e8-9eb6-529269fb1459"; }
    };

    public static ProgramWorkflowStateDescriptor GROUPC7 = new ProgramWorkflowStateDescriptor() {
        public String conceptUuid() { return GROUP7_UUID; }
        public Boolean initial() { return true; }
        public Boolean terminal() { return false; }
        public String uuid() { return "ffef7fe4-8eb5-11e8-9eb6-529269fb1459"; }
    };

    public static ProgramWorkflowStateDescriptor GROUPC8 = new ProgramWorkflowStateDescriptor() {
        public String conceptUuid() { return GROUP8_UUID; }
        public Boolean initial() { return true; }
        public Boolean terminal() { return false; }
        public String uuid() { return "ffef825a-8eb5-11e8-9eb6-529269fb1459"; }
    };

    public static ProgramWorkflowStateDescriptor GROUPC9 = new ProgramWorkflowStateDescriptor() {
        public String conceptUuid() { return GROUP9_UUID; }
        public Boolean initial() { return true; }
        public Boolean terminal() { return false; }
        public String uuid() { return "ffef84a8-8eb5-11e8-9eb6-529269fb1459"; }
    };

    public static ProgramWorkflowStateDescriptor GROUPC10 = new ProgramWorkflowStateDescriptor() {
        public String conceptUuid() { return GROUP10_UUID; }
        public Boolean initial() { return true; }
        public Boolean terminal() { return false; }
        public String uuid() { return "ffef89ee-8eb5-11e8-9eb6-529269fb1459"; }
    };

    public static ProgramWorkflowStateDescriptor GROUPC11 = new ProgramWorkflowStateDescriptor() {
        public String conceptUuid() { return GROUP11_UUID; }
        public Boolean initial() { return true; }
        public Boolean terminal() { return false; }
        public String uuid() { return "ffef8ca0-8eb5-11e8-9eb6-529269fb1459"; }
    };

    public static ProgramWorkflowStateDescriptor GROUPC12 = new ProgramWorkflowStateDescriptor() {
        public String conceptUuid() { return GROUP12_UUID; }
        public Boolean initial() { return true; }
        public Boolean terminal() { return false; }
        public String uuid() { return "ffef8f16-8eb5-11e8-9eb6-529269fb1459"; }
    };

    public static ProgramWorkflowDescriptor CHILD_GROUP = new ProgramWorkflowDescriptor() {
        public String conceptUuid() { return CHILD_GROUP_UUID; }
        public String uuid() { return "41a27ab4-8a14-11e8-9a94-a6cf71072f73"; }
        @Override public Set<ProgramWorkflowStateDescriptor> states()
        { return new HashSet<ProgramWorkflowStateDescriptor>(Arrays.asList(GROUPC1,GROUPC2,GROUPC3,GROUPC4,GROUPC5,GROUPC6,GROUPC7,GROUPC8,GROUPC9,GROUPC10,GROUPC11,GROUPC12)); }
    };

}
