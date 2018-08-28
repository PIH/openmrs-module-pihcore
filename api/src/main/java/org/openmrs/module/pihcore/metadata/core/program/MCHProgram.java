package org.openmrs.module.pihcore.metadata.core.program;

import org.openmrs.module.metadatadeploy.descriptor.ProgramDescriptor;
import org.openmrs.module.metadatadeploy.descriptor.ProgramWorkflowDescriptor;
import org.openmrs.module.metadatadeploy.descriptor.ProgramWorkflowStateDescriptor;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static org.openmrs.module.pihcore.PihCoreConstants.*;

public class MCHProgram {

    public static ProgramWorkflowStateDescriptor ANC_GROUP_CARE = new ProgramWorkflowStateDescriptor() {
        public String conceptUuid() { return GROUP_CARE_UUID; }
        public Boolean initial() { return true; }
        public Boolean terminal() { return false; }
        public String uuid() { return "41a2753c-8a14-11e8-9a94-a6cf71072f73"; }
    };

    public static ProgramWorkflowStateDescriptor PEDS_GROUP_CARE = new ProgramWorkflowStateDescriptor() {
        public String conceptUuid() { return PEDS_GROUP_CARE_UUID; }
        public Boolean initial() { return true; }
        public Boolean terminal() { return false; }
        public String uuid() { return "2fa7008c-aa58-11e8-98d0-529269fb1459"; }
    };

    public static ProgramWorkflowStateDescriptor INDIVIDUAL_CARE = new ProgramWorkflowStateDescriptor() {
        public String conceptUuid() { return INDIVIDUAL_CARE_UUID; }
        public Boolean initial() { return true; }
        public Boolean terminal() { return false; }
        public String uuid() { return "41a27d48-8a14-11e8-9a94-a6cf71072f73"; }
    };

    public static ProgramWorkflowDescriptor TREATMENT_CARE_WORKFLOW = new ProgramWorkflowDescriptor() {
        public String conceptUuid() { return TREATMENT_CARE_UUID; }
        public String uuid() { return "41a277d0-8a14-11e8-9a94-a6cf71072f73"; }
        @Override public Set<ProgramWorkflowStateDescriptor> states()
        { return new HashSet<ProgramWorkflowStateDescriptor>(Arrays.asList(ANC_GROUP_CARE, INDIVIDUAL_CARE, PEDS_GROUP_CARE)); }
    };

    public static ProgramDescriptor MCH = new ProgramDescriptor() {
        public String name() { return "MCH"; }
        public String description() { return "Maternal and Child Health Program"; }
        public String conceptUuid() { return MCH_PROGRAM_CONCEPT_UUID; }   // this concept is installed via metadata package
        @Override public String outcomesConceptUuid()  { return MCH_PROGRAM_OUTCOME_CONCEPT_UUID; }   // this concept is installed via metadata package
        public String uuid() { return "41a2715e-8a14-11e8-9a94-a6cf71072f73"; }
        @Override public Set<ProgramWorkflowDescriptor> workflows() { return new HashSet<ProgramWorkflowDescriptor>(Arrays.asList(TREATMENT_CARE_WORKFLOW)); }
    };

}
