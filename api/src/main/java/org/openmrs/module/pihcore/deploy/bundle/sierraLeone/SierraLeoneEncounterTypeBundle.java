package org.openmrs.module.pihcore.deploy.bundle.sierraLeone;

import org.openmrs.module.metadatadeploy.bundle.AbstractMetadataBundle;
import org.openmrs.module.pihcore.metadata.sierraLeone.SierraLeoneEncounterTypes;
import org.springframework.stereotype.Component;

@Component
public class SierraLeoneEncounterTypeBundle extends AbstractMetadataBundle {

    @Override
    public void install() throws Exception {
        install(SierraLeoneEncounterTypes.SIERRA_LEONE_OUTPATIENT_INITIAL);
        install(SierraLeoneEncounterTypes.SIERRA_LEONE_OUTPATIENT_FOLLOWUP);
    }

}
