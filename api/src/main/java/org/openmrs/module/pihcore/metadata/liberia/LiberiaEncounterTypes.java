package org.openmrs.module.pihcore.metadata.liberia;

import org.openmrs.module.metadatadeploy.descriptor.EncounterTypeDescriptor;

public class LiberiaEncounterTypes {

    public static EncounterTypeDescriptor ANC  = new EncounterTypeDescriptor() {
        public String uuid() { return "c4ed1a17-cb3d-11ea-b84d-3c6aa7c392cc"; }
        public String name() { return "ANC Form"; }
        public String description() { return "General form for capturing all ANC-related data for Liberia"; }
    };
}
