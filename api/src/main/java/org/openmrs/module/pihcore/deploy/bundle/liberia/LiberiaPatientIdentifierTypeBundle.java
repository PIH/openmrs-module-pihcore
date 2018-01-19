package org.openmrs.module.pihcore.deploy.bundle.liberia;

import org.openmrs.module.metadatadeploy.bundle.AbstractMetadataBundle;
import org.openmrs.module.pihcore.metadata.liberia.LiberiaPatientIdentifierTypes;
import org.springframework.stereotype.Component;

@Component
public class LiberiaPatientIdentifierTypeBundle extends AbstractMetadataBundle {

    @Override
    public void install() throws Exception {

        log.info("Installing PatientIdentifierTypes");

        install(LiberiaPatientIdentifierTypes.PLEEBO_EMR_ID);
        install(LiberiaPatientIdentifierTypes.SAMPLE_DOSSIER_NUMBER_ID);

    }
}
