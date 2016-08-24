package org.openmrs.module.pihcore.deploy.bundle.haiti;

import org.openmrs.module.metadatadeploy.bundle.AbstractMetadataBundle;
import org.openmrs.module.pihcore.metadata.haiti.HaitiPatientIdentifierTypes;
import org.springframework.stereotype.Component;

@Component
public class HaitiPatientIdentifierTypeBundle extends AbstractMetadataBundle {

    @Override
    public void install() throws Exception {

        log.info("Installing PatientIdentifierTypes");

        install(HaitiPatientIdentifierTypes.ZL_EMR_ID);
        install(HaitiPatientIdentifierTypes.DOSSIER_NUMBER);
        install(HaitiPatientIdentifierTypes.EXTERNAL_DOSSIER_NUMBER);
        install(HaitiPatientIdentifierTypes.HIVEMR_V1);
        install(HaitiPatientIdentifierTypes.USER_ENTERED_REF_NUMBER);
        install(HaitiPatientIdentifierTypes.BIOMETRIC_REF_NUMBER);
    }

}
