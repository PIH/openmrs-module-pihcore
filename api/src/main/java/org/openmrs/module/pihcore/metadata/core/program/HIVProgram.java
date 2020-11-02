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

    public static ProgramWorkflowStateDescriptor ON_ART = new ProgramWorkflowStateDescriptor() {
        public String conceptUuid() { return "3cdc0a8c-26fe-102b-80cb-0017a47871b2"; }  // PIH:1577
        public Boolean initial() { return true; }
        public Boolean terminal() { return false; }
        public String uuid() { return "872c529c-a5a4-47c7-9584-bd15fa5bb0a9"; }
    };

    public static ProgramWorkflowStateDescriptor LTFU = new ProgramWorkflowStateDescriptor() {
        public String conceptUuid() { return "3ceb0ed8-26fe-102b-80cb-0017a47871b2"; }  // PIH:5240
        public Boolean initial() { return true; }
        public Boolean terminal() { return false; }
        public String uuid() { return "d7727695-44bf-4d58-a5c6-1424a3959581"; }
    };

    public static ProgramWorkflowDescriptor HIV_TREATMENT_STATUS = new ProgramWorkflowDescriptor() {
        public String conceptUuid() { return "37c7cf83-bce6-469c-acab-6a90e63264d2"; }  // PIH:11449
        public String uuid() { return "aba55bfe-9490-4362-9841-0c476e379889"; }
        @Override public Set<ProgramWorkflowStateDescriptor> states() {
            return new HashSet<ProgramWorkflowStateDescriptor>(Arrays.asList(ON_ART, LTFU));
        }
    };

    public static ProgramWorkflowStateDescriptor TREATMENT_STOPPED = new ProgramWorkflowStateDescriptor() {
        public String conceptUuid() { return "3cdc0d7a-26fe-102b-80cb-0017a47871b2"; }  // PIH:1579
        public Boolean initial() { return true; }
        public Boolean terminal() { return false; }
        public String uuid() { return "267cb08a-a8b3-439d-a3e0-17b2341c85b6"; }
    };

    public static ProgramWorkflowStateDescriptor TREATMENT_REFUSED = new ProgramWorkflowStateDescriptor() {
        public String conceptUuid() { return "3cdc0604-26fe-102b-80cb-0017a47871b2"; }  // PIH:1574
        public Boolean initial() { return true; }
        public Boolean terminal() { return false; }
        public String uuid() { return "b547c95e-aba9-4a44-9b04-6275c0b3ebab"; }
    };

    public static ProgramWorkflowStateDescriptor PATIENT_MOVED = new ProgramWorkflowStateDescriptor() {
        public String conceptUuid() { return "3562ccaf-6926-40fe-9b95-6c38bf45b1f6"; }  // PIH:11976
        public Boolean initial() { return true; }
        public Boolean terminal() { return false; }
        public String uuid() { return "ff007e66-599b-4c21-be41-4fd93fc6af2f"; }
    };

    public static ProgramWorkflowDescriptor LTFU_STATUS_WORKFLOW = new ProgramWorkflowDescriptor() {
        public String conceptUuid() { return "92964825-6952-407e-8331-8bbd0df7ef5c"; }  // PIH:13159
        public String uuid() { return "56b3e516-b57e-4ab2-bbc2-82a8b3242d67"; }
        @Override public Set<ProgramWorkflowStateDescriptor> states() {
            return new HashSet<ProgramWorkflowStateDescriptor>(Arrays.asList(TREATMENT_STOPPED, TREATMENT_REFUSED, PATIENT_MOVED));
        }
    };

    public static ProgramWorkflowStateDescriptor RETURNED_TO_CARE = new ProgramWorkflowStateDescriptor() {
        public String conceptUuid() { return "1f098974-c3d4-450b-b1ee-09676b0bf48c"; }  // PIH:13161
        public Boolean initial() { return true; }
        public Boolean terminal() { return false; }
        public String uuid() { return "69d1c18b-46c4-4ad6-9ca6-9b14cb320d96"; }
    };

    public static ProgramWorkflowDescriptor REACTIVATION_STATUS_WORKFLOW = new ProgramWorkflowDescriptor() {
        public String conceptUuid() { return "e7503dd3-ea1e-4709-81a1-990015eb71e5"; }  // PIH:13160
        public String uuid() { return "038f8382-aeca-459a-a8b9-859043b64bf8"; }
        @Override public Set<ProgramWorkflowStateDescriptor> states() {
            return new HashSet<ProgramWorkflowStateDescriptor>(Arrays.asList(RETURNED_TO_CARE));
        }
    };

    public static ProgramDescriptor HIV = new ProgramDescriptor() {
        public String conceptUuid() { return HIV_PROGRAM_CONCEPT_UUID; }   // this concept is installed via metadata package
        public String name() { return "HIV"; }
        public String description() { return "HIV Program"; }
        public String uuid() { return "b1cb1fc1-5190-4f7a-af08-48870975dafc"; }

        @Override public String outcomesConceptUuid()  { return HIV_PROGRAM_OUTCOMES_CONCEPT_UUID; }   // this concept is installed via metadata package

        @Override public Set<ProgramWorkflowDescriptor> workflows() {
            return new HashSet<>(Arrays.asList(HIV_TREATMENT_STATUS, LTFU_STATUS_WORKFLOW, REACTIVATION_STATUS_WORKFLOW));
        }
    };

}
