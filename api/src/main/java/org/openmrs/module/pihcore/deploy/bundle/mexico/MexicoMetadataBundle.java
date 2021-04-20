package org.openmrs.module.pihcore.deploy.bundle.mexico;

import org.openmrs.module.metadatadeploy.bundle.AbstractMetadataBundle;
import org.openmrs.module.metadatadeploy.bundle.Requires;
import org.openmrs.module.pihcore.deploy.bundle.core.PihCoreMetadataBundle;
import org.springframework.stereotype.Component;

@Component
@Requires({ PihCoreMetadataBundle.class,
            MexicoEncounterTypeBundle.class,
            MexicoPatientIdentifierTypeBundle.class} )
public class MexicoMetadataBundle extends AbstractMetadataBundle {

    @Override
    public void install() throws Exception {
    }

}
