package org.openmrs.module.pihcore.deploy.bundle.liberia;

import org.openmrs.module.metadatadeploy.bundle.AbstractMetadataBundle;
import org.openmrs.module.metadatadeploy.bundle.Requires;
import org.openmrs.module.pihcore.deploy.bundle.core.PihCoreMetadataBundle;
import org.springframework.stereotype.Component;

@Component
@Requires({ PihCoreMetadataBundle.class,
            LiberiaLocationsBundle.class,
            LiberiaPatientIdentifierTypeBundle.class} )
public class LiberiaMetadataBundle extends AbstractMetadataBundle {

    @Override
    public void install() throws Exception {
    }
}
