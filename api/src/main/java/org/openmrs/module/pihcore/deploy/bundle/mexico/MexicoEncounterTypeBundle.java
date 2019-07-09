package org.openmrs.module.pihcore.deploy.bundle.mexico;

import org.openmrs.module.metadatadeploy.bundle.AbstractMetadataBundle;
import org.openmrs.module.pihcore.metadata.mexico.MexicoEncounterTypes;
import org.springframework.stereotype.Component;

@Component
public class MexicoEncounterTypeBundle extends AbstractMetadataBundle {


    @Override
    public void install() throws Exception {
        install(MexicoEncounterTypes.MEXICO_CONSULT);
    }

}
