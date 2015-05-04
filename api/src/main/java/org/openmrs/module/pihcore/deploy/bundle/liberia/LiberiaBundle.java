package org.openmrs.module.pihcore.deploy.bundle.liberia;

import org.openmrs.module.metadatadeploy.bundle.Requires;
import org.openmrs.module.pihcore.deploy.bundle.PihMetadataBundle;
import org.openmrs.module.pihcore.deploy.bundle.core.PihCoreMetadataBundle;
import org.springframework.stereotype.Component;

@Component
@Requires({ PihCoreMetadataBundle.class, LiberiaLocationsBundle.class } )
public class LiberiaBundle extends PihMetadataBundle {

    @Override
    public void install() throws Exception {

    }
}
