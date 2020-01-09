package org.openmrs.module.pihcore.deploy.bundle.peru;

import org.openmrs.module.metadatadeploy.bundle.AbstractMetadataBundle;
import org.openmrs.module.pihcore.metadata.peru.PeruPatientIdentifierTypes;
import org.springframework.stereotype.Component;

@Component
public class PeruPatientIdentifierTypeBundle extends AbstractMetadataBundle {

    @Override
    public void install() throws Exception {
        install(PeruPatientIdentifierTypes.PERU_EMR_ID);
    }
}
