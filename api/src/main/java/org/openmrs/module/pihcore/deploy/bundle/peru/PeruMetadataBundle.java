package org.openmrs.module.pihcore.deploy.bundle.peru;

import org.openmrs.module.metadatadeploy.bundle.AbstractMetadataBundle;
import org.openmrs.module.metadatadeploy.bundle.Requires;
import org.openmrs.module.pihcore.deploy.bundle.core.PihCoreMetadataBundle;
import org.springframework.stereotype.Component;

@Component
@Requires({ PihCoreMetadataBundle.class,
            PeruPatientIdentifierTypeBundle.class} )
public class PeruMetadataBundle extends AbstractMetadataBundle {

    @Override
    public void install() throws Exception {
    }

}
