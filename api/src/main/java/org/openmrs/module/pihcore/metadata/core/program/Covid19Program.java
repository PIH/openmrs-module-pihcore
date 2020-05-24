package org.openmrs.module.pihcore.metadata.core.program;

import org.openmrs.module.metadatadeploy.descriptor.ProgramDescriptor;

import static org.openmrs.module.pihcore.PihCoreConstants.COVID19_PROGRAM_CONCEPT_UUID;
import static org.openmrs.module.pihcore.PihCoreConstants.COVID19_PROGRAM_OUTCOMES_CONCEPT_UUID;

public class Covid19Program {

    public static ProgramDescriptor COVID19 = new ProgramDescriptor() {
        public String name() { return "COVID-19"; }
        public String description() { return "COVID-19 Program"; }
        public String conceptUuid() { return COVID19_PROGRAM_CONCEPT_UUID; }   // this concept is installed via metadata package
        @Override public String outcomesConceptUuid()  { return COVID19_PROGRAM_OUTCOMES_CONCEPT_UUID; }   // this concept is installed via metadata package
        public String uuid() { return "dc227c44-d68e-431b-a20d-a60bb3496543"; }
    };
}
