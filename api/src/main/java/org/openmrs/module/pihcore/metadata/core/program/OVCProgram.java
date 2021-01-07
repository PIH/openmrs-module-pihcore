package org.openmrs.module.pihcore.metadata.core.program;

import org.openmrs.module.metadatadeploy.descriptor.ProgramDescriptor;
import org.openmrs.module.metadatadeploy.descriptor.ProgramWorkflowDescriptor;
import org.openmrs.module.metadatadeploy.descriptor.ProgramWorkflowStateDescriptor;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static org.openmrs.module.pihcore.PihCoreConstants.OVC_PROGRAM_CONCEPT_UUID;
import static org.openmrs.module.pihcore.PihCoreConstants.OVC_PROGRAM_OUTCOMES_CONCEPT_UUID;

public class OVCProgram {

    public static ProgramWorkflowStateDescriptor ACTIVE = new ProgramWorkflowStateDescriptor() {
        public String conceptUuid() { return "161636AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"; }  // PIH:13221 Active status
        public Boolean initial() { return true; }
        public Boolean terminal() { return false; }
        public String uuid() { return "483b4e4a-39b7-4793-9f95-cc121fded9b7"; }
    };

    public static ProgramWorkflowStateDescriptor GRADUATED = new ProgramWorkflowStateDescriptor() {
        public String conceptUuid() { return "3cd93172-26fe-102b-80cb-0017a47871b2"; }  // PIH:1267 Completed
        public Boolean initial() { return false; }
        public Boolean terminal() { return true; }
        public String uuid() { return "430afe70-e2e1-4b44-87ed-e2153b42d5d3"; }
    };

    public static ProgramWorkflowStateDescriptor LTFU = new ProgramWorkflowStateDescriptor() {
        public String conceptUuid() { return "3ceb0ed8-26fe-102b-80cb-0017a47871b2"; }  // PIH:5240
        public Boolean initial() { return false; }
        public Boolean terminal() { return false; }
        public String uuid() { return "84fc28a6-0450-42fc-9820-e6c2a760a7b6"; }
    };

    public static ProgramWorkflowDescriptor OVC_WORKFLOW = new ProgramWorkflowDescriptor() {
        public String conceptUuid() { return "95d7c044-7b20-4b52-9444-3e13e0c35c7d"; }  // PIH:13223
        public String uuid() { return "ee228c4f-f898-494a-9740-213f757cb10c"; }
        @Override public Set<ProgramWorkflowStateDescriptor> states() {
            return new HashSet<ProgramWorkflowStateDescriptor>(Arrays.asList(ACTIVE, GRADUATED, LTFU));
        }
    };

    public static ProgramDescriptor OVC = new ProgramDescriptor() {
        public String conceptUuid() { return OVC_PROGRAM_CONCEPT_UUID; }
        public String name() { return "OVC"; }
        public String description() { return "USAID orphans and vulnerable children program"; }
        public String uuid() { return "e1b2f0b5-6d56-4500-8523-0ba71e75d897"; }

        @Override public Set<ProgramWorkflowDescriptor> workflows() {
            return new HashSet<>(Arrays.asList(OVC_WORKFLOW));
        }

        @Override public String outcomesConceptUuid()  { return OVC_PROGRAM_OUTCOMES_CONCEPT_UUID; }
    };

}
