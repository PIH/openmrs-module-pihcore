package org.openmrs.module.pihcore.metadata.core.program;

import org.openmrs.module.metadatadeploy.descriptor.ProgramDescriptor;
import org.openmrs.module.metadatadeploy.descriptor.ProgramWorkflowDescriptor;
import org.openmrs.module.metadatadeploy.descriptor.ProgramWorkflowStateDescriptor;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static org.openmrs.module.pihcore.PihCoreConstants.*;

public class OncologyProgram {

    public static ProgramWorkflowStateDescriptor THERAPY_STATE = new ProgramWorkflowStateDescriptor() {
        public String conceptUuid() { return THERAPY_STATE_UUID; }
        public Boolean initial() { return true; }
        public Boolean terminal() { return false; }
        public String uuid() { return "45ffad1c-684e-11e8-adc0-fa7ae01bbebca"; }
    };

    public static ProgramWorkflowStateDescriptor SURVEILLANCE_STATE = new ProgramWorkflowStateDescriptor() {
        public String conceptUuid() { return SURVEILLANCE_STATE_UUID; }
        public Boolean initial() { return true; }
        public Boolean terminal() { return false; }
        public String uuid() { return "1573301c-6851-11e8-adc0-fa7ae01bbebc"; }
    };

    public static ProgramWorkflowStateDescriptor OTHER_STATE = new ProgramWorkflowStateDescriptor() {
        public String conceptUuid() { return OTHER_STATE_UUID; }
        public Boolean initial() { return true; }
        public Boolean terminal() { return false; }
        public String uuid() { return "157335ee-6851-11e8-adc0-fa7ae01bbebc"; }
    };

    public static ProgramWorkflowDescriptor ONCOLOGY_PROGRESS_STATUS = new ProgramWorkflowDescriptor() {
        public String conceptUuid() { return ONCOLOGY_PROGRESS_STATUS_UUID; }
        public String uuid() { return "157333e6-6851-11e8-adc0-fa7ae01bbebc"; }
        @Override public Set<ProgramWorkflowStateDescriptor> states() { return new HashSet<ProgramWorkflowStateDescriptor>(Arrays.asList(THERAPY_STATE,SURVEILLANCE_STATE,OTHER_STATE)); }
    };

    public static ProgramWorkflowStateDescriptor ONCOLOGY_PALLIATIVE_STATE = new ProgramWorkflowStateDescriptor() {
        public String conceptUuid() { return PALLIATIVE_STATE_UUID; }
        public Boolean initial() { return true; }
        public Boolean terminal() { return false; }
        public String uuid() { return "45ffabfa-684e-11e8-adc0-fa7ae01bbebc"; }
    };

    public static ProgramWorkflowStateDescriptor ONCOLOGY_CURE_STATE = new ProgramWorkflowStateDescriptor() {
        public String conceptUuid() { return CURATIVE_STATE_UUID; }
        public Boolean initial() { return true; }
        public Boolean terminal() { return false; }
        public String uuid() { return "45ffaace-684e-11e8-adc0-fa7ae01bbebc"; }
    };

    public static ProgramWorkflowDescriptor ONCOLOGY_TREATMENT_STATUS = new ProgramWorkflowDescriptor() {
        public String conceptUuid() { return ONCOLOGY_TREATMENT_STATUS_UUID; }
        public String uuid() { return "45ffa97a-684e-11e8-adc0-fa7ae01bbebc"; }
        @Override public Set<ProgramWorkflowStateDescriptor> states() { return new HashSet<ProgramWorkflowStateDescriptor>(Arrays.asList(ONCOLOGY_CURE_STATE,ONCOLOGY_PALLIATIVE_STATE)); }
    };

    public static ProgramDescriptor ONCOLOGY = new ProgramDescriptor() {
        public String conceptUuid() { return ONCOLOGY_PROGRAM_CONCEPT_UUID; }   // this concept is installed via metadata package
        public String name() { return "Oncology"; }
        public String description() { return "ONCOLOGY Program"; }
        @Override public String outcomesConceptUuid()  { return ONCOLOGY_PROGRAM_OUTCOME_CONCEPT_UUID; }   // this concept is installed via metadata package
        public String uuid() { return "5bdbf9f6-690c-11e8-adc0-fa7ae01bbebc"; }
        @Override public Set<ProgramWorkflowDescriptor> workflows() { return new HashSet<ProgramWorkflowDescriptor>(Arrays.asList(ONCOLOGY_TREATMENT_STATUS,ONCOLOGY_PROGRESS_STATUS)); }
    };

}
