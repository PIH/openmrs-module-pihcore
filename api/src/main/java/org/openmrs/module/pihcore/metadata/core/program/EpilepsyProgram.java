package org.openmrs.module.pihcore.metadata.core.program;

import org.openmrs.module.metadatadeploy.descriptor.ProgramDescriptor;

import static org.openmrs.module.pihcore.PihCoreConstants.EPILEPSY_PROGRAM_CONCEPT_UUID;
import static org.openmrs.module.pihcore.PihCoreConstants.EPILEPSY_PROGRAM_OUTCOMES_CONCEPT_UUID;


public class EpilepsyProgram {

    public static ProgramDescriptor EPILEPSY = new ProgramDescriptor() {
        public String name() { return "Epilepsy"; }
        public String description() { return "Epilepsy Program"; }
        public String conceptUuid() { return EPILEPSY_PROGRAM_CONCEPT_UUID; }
        @Override public String outcomesConceptUuid()  { return EPILEPSY_PROGRAM_OUTCOMES_CONCEPT_UUID; }
        public String uuid() { return "69e6a46d-674e-4281-99a0-4004f293ee57"; }
    };

}


