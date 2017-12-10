package org.openmrs.module.pihcore.metadata.core.program;

import org.openmrs.module.metadatadeploy.descriptor.ProgramDescriptor;
import org.openmrs.module.metadatadeploy.descriptor.ProgramWorkflowDescriptor;
import org.openmrs.module.metadatadeploy.descriptor.ProgramWorkflowStateDescriptor;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static org.openmrs.module.pihcore.PihCoreConstants.NCD_PROGRAM_CONCEPT_UUID;
import static org.openmrs.module.pihcore.PihCoreConstants.NCD_PROGRAM_OUTCOMES_CONCEPT_UUID;
import static org.openmrs.module.pihcore.PihCoreConstants.NCD_PROGRAM_STATUS_UUID;
import static org.openmrs.module.pihcore.PihCoreConstants.PROGRAM_COMPLICATED_STATE_UUID;
import static org.openmrs.module.pihcore.PihCoreConstants.PROGRAM_STABLE_STATE_UUID;
import static org.openmrs.module.pihcore.PihCoreConstants.PROGRAM_UNSTABLE_STATE_UUID;

public class NCDProgram {

    public static ProgramWorkflowStateDescriptor NCD_STABLE_STATE = new ProgramWorkflowStateDescriptor() {
        public String conceptUuid() { return PROGRAM_STABLE_STATE_UUID; }
        public Boolean initial() { return true; }
        public Boolean terminal() { return false; }
        public String uuid() { return "51579e44-bf3a-11e7-abc4-cec278b6b50a"; }
    };

    public static ProgramWorkflowStateDescriptor NCD_UNSTABLE_STATE = new ProgramWorkflowStateDescriptor() {
        public String conceptUuid() { return PROGRAM_UNSTABLE_STATE_UUID; }
        public Boolean initial() { return true; }
        public Boolean terminal() { return false; }
        public String uuid() { return "51579fde-bf3a-11e7-abc4-cec278b6b50a"; }
    };

    public static ProgramWorkflowStateDescriptor NCD_COMPLICATED_STATE = new ProgramWorkflowStateDescriptor() {
        public String conceptUuid() { return PROGRAM_COMPLICATED_STATE_UUID; }
        public Boolean initial() { return true; }
        public Boolean terminal() { return false; }
        public String uuid() { return "5157a15a-bf3a-11e7-abc4-cec278b6b50a"; }
    };

    public static ProgramWorkflowDescriptor NCD_CLINICAL_STATUS = new ProgramWorkflowDescriptor() {
        public String conceptUuid() { return NCD_PROGRAM_STATUS_UUID; }
        public String uuid() { return "51579bce-bf3a-11e7-abc4-cec278b6b50a"; }
        @Override public Set<ProgramWorkflowStateDescriptor> states() { return new HashSet<ProgramWorkflowStateDescriptor>(Arrays.asList(NCD_STABLE_STATE,NCD_UNSTABLE_STATE,NCD_COMPLICATED_STATE)); }
    };

    public static ProgramDescriptor NCD = new ProgramDescriptor() {
        public String conceptUuid() { return NCD_PROGRAM_CONCEPT_UUID; }   // this concept is installed via metadata package
        public String name() { return "NCD"; }
        public String description() { return "NCD Program"; }
        @Override public String outcomesConceptUuid()  { return NCD_PROGRAM_OUTCOMES_CONCEPT_UUID; }   // this concept is installed via metadata package
        public String uuid() { return "515796ec-bf3a-11e7-abc4-cec278b6b50a"; }
        @Override public Set<ProgramWorkflowDescriptor> workflows() { return Collections.singleton(NCD_CLINICAL_STATUS); }
    };

}
