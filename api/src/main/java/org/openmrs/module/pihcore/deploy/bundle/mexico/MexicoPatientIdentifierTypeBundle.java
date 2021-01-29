package org.openmrs.module.pihcore.deploy.bundle.mexico;

import org.openmrs.module.metadatadeploy.bundle.AbstractMetadataBundle;
import org.openmrs.module.pihcore.metadata.mexico.MexicoPatientIdentifierTypes;
import org.springframework.stereotype.Component;

@Component
public class MexicoPatientIdentifierTypeBundle extends AbstractMetadataBundle {

    @Override
    public void install() throws Exception {
        install(MexicoPatientIdentifierTypes.CHIAPAS_EMR_ID);
        install(MexicoPatientIdentifierTypes.CURP);
        install(MexicoPatientIdentifierTypes.MEXICO_DOSSIER_NUMBER);
    }
}
