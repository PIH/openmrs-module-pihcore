package org.openmrs.module.pihcore.deploy.bundle.haiti;

import org.openmrs.module.metadatadeploy.bundle.AbstractMetadataBundle;
import org.openmrs.module.pihcore.metadata.haiti.PihHaitiPatientIdentifierTypes;
import org.springframework.stereotype.Component;

@Component
public class PihHaitiPatientIdentifierTypeBundle extends AbstractMetadataBundle {

    @Override
    public void install() throws Exception {

        log.info("Installing PatientIdentifierTypes");

        install(PihHaitiPatientIdentifierTypes.ZL_EMR_ID);
        install(PihHaitiPatientIdentifierTypes.DOSSIER_NUMBER);
        install(PihHaitiPatientIdentifierTypes.EXTERNAL_DOSSIER_NUMBER);
        install(PihHaitiPatientIdentifierTypes.HIVEMR_V1);
        install(PihHaitiPatientIdentifierTypes.USER_ENTERED_REF_NUMBER);
        install(PihHaitiPatientIdentifierTypes.DENTAL_DOSSIER_NUMBER);
    }

}
