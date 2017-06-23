package org.openmrs.module.pihcore.metadata.haiti.mirebalais;

import org.openmrs.module.metadatadeploy.descriptor.ProgramDescriptor;

import static org.openmrs.module.pihcore.PihCoreConstants.HIV_PROGRAM_CONCEPT_UUID;
import static org.openmrs.module.pihcore.PihCoreConstants.HIV_PROGRAM_OUTCOMES_CONCEPT_UUID;
import static org.openmrs.module.pihcore.PihCoreConstants.ZIKA_PROGRAM_CONCEPT_UUID;
import static org.openmrs.module.pihcore.PihCoreConstants.ZIKA_PROGRAM_OUTCOMES_CONCEPT_UUID;

public class PihHaitiPrograms {

    public static ProgramDescriptor ZIKA = new ProgramDescriptor() {
        public String conceptUuid() { return ZIKA_PROGRAM_CONCEPT_UUID; }   // this concept is installed via metadata package
        public String name() { return "Zika"; }
        public String description() { return "Zika Program"; }
        @Override public String outcomesConceptUuid()  { return ZIKA_PROGRAM_OUTCOMES_CONCEPT_UUID; }   // this concept is installed via metadata package
        public String uuid() { return "3bea593a-9afd-4642-96a6-210b60f5aff2"; }
    };

    public static ProgramDescriptor HIV = new ProgramDescriptor() {
        public String conceptUuid() { return HIV_PROGRAM_CONCEPT_UUID; }   // this concept is installed via metadata package
        public String name() { return "HIV"; }
        public String description() { return "HIV Program"; }
        @Override public String outcomesConceptUuid()  { return HIV_PROGRAM_OUTCOMES_CONCEPT_UUID; }   // this concept is installed via metadata package
        public String uuid() { return "b1cb1fc1-5190-4f7a-af08-48870975dafc"; }
    };

}
