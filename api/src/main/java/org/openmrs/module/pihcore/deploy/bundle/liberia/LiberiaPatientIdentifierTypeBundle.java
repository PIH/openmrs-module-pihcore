package org.openmrs.module.pihcore.deploy.bundle.liberia;

import org.openmrs.module.metadatadeploy.bundle.AbstractMetadataBundle;
import org.openmrs.module.pihcore.metadata.liberia.LiberiaPatientIdentifierTypes;
import org.springframework.stereotype.Component;

@Component
public class LiberiaPatientIdentifierTypeBundle extends AbstractMetadataBundle {

    @Override
    public void install() throws Exception {

        log.info("Installing PatientIdentifierTypes");

        install(LiberiaPatientIdentifierTypes.LIBERIA_EMR_ID);
        install(LiberiaPatientIdentifierTypes.SAMPLE_DOSSIER_NUMBER_ID);
        install(LiberiaPatientIdentifierTypes.BIOMETRIC_REF_NUMBER);
        install(LiberiaPatientIdentifierTypes.MENTAL_HEALTH_MEDICAL_RECORD_NUMBER);
    }
}
