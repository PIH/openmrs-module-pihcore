package org.openmrs.module.pihcore.deploy.bundle.haiti;

import org.openmrs.module.metadatadeploy.bundle.AbstractMetadataBundle1_11;
import org.openmrs.module.pihcore.metadata.haiti.PihHaitiPatientIdentifierTypes;
import org.springframework.stereotype.Component;

@Component
public class PihHaitiPatientIdentifierTypeBundle extends AbstractMetadataBundle1_11 {

    @Override
    public void install() throws Exception {

        log.info("Installing PatientIdentifierTypes");

        install(PihHaitiPatientIdentifierTypes.ZL_EMR_ID);
        install(PihHaitiPatientIdentifierTypes.DOSSIER_NUMBER);
        install(PihHaitiPatientIdentifierTypes.EXTERNAL_DOSSIER_NUMBER);
        install(PihHaitiPatientIdentifierTypes.HIVEMR_V1);
        install(PihHaitiPatientIdentifierTypes.USER_ENTERED_REF_NUMBER);
        install(PihHaitiPatientIdentifierTypes.DENTAL_DOSSIER_NUMBER);
        install(PihHaitiPatientIdentifierTypes.HIV_DOSSIER_NUMBER);
        install(PihHaitiPatientIdentifierTypes.NATIONAL_IDENTIFIER_ID);
        install(PihHaitiPatientIdentifierTypes.NATIONAL_FISCAL_ID);

    }

}
