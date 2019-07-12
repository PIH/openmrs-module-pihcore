package org.openmrs.module.pihcore.metadata.mexico;

import org.openmrs.module.metadatadeploy.descriptor.EncounterTypeDescriptor;

/**
 * Encounter types specific to Mexico and not shared with other implementations
 */
public class MexicoEncounterTypes {

    public static EncounterTypeDescriptor MEXICO_CONSULT =  new EncounterTypeDescriptor() {
        public String uuid() { return "aa61d509-6e76-4036-a65d-7813c0c3b752"; }
        public String name() { return "Consult"; }
        public String description() { return "A doctor consult at one of our primary care clinics"; }
    };

}
