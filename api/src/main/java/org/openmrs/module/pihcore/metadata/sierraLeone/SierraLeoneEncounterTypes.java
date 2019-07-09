package org.openmrs.module.pihcore.metadata.sierraLeone;


import org.openmrs.module.metadatadeploy.descriptor.EncounterTypeDescriptor;

/**
 * Encounter types specific to Sierra Leone and not shared with other implementations
 */
public class SierraLeoneEncounterTypes {

    public static EncounterTypeDescriptor SIERRA_LEONE_OUTPATIENT_INITIAL = new EncounterTypeDescriptor() {
        public String uuid() { return "7d5853d4-67b7-4742-8492-fcf860690ed5"; }
        public String name() { return "Sierra Leone Outpatient Initial"; }
        public String description() { return "The outpatient intake form for Sierra Leone"; }
    };

    public static EncounterTypeDescriptor SIERRA_LEONE_OUTPATIENT_FOLLOWUP = new EncounterTypeDescriptor() {
        public String uuid() { return "d8a038b5-90d2-43dc-b94b-8338b76674f3"; }
        public String name() { return "Sierra Leone Outpatient Followup"; }
        public String description() { return "The outpatient visit form for Sierra Leone"; }
    };

}
