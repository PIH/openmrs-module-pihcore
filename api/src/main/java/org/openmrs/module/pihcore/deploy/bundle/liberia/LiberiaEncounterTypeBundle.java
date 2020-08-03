package org.openmrs.module.pihcore.deploy.bundle.liberia;

import org.openmrs.EncounterType;
import org.openmrs.module.metadatadeploy.bundle.AbstractMetadataBundle;
import org.openmrs.module.pihcore.metadata.liberia.LiberiaEncounterTypes;
import org.springframework.stereotype.Component;

@Component
public class LiberiaEncounterTypeBundle extends AbstractMetadataBundle {

    @Override
    public void install() throws Exception {
        install(LiberiaEncounterTypes.ANC);
        install(LiberiaEncounterTypes.PEDS);

        uninstall(possible(EncounterType.class, LiberiaEncounterTypes.ANC.uuid()), "using the initial and followup forms instead");
    }
}
