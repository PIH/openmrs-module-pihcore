package org.openmrs.module.pihcore.deploy.bundle.liberia;

import org.openmrs.module.metadatadeploy.bundle.Requires;
import org.openmrs.module.pihcore.deploy.bundle.PihLocationsMetadataBundle;
import org.openmrs.module.pihcore.deploy.bundle.core.PihCoreMetadataBundle;
import org.openmrs.module.pihcore.metadata.liberia.LiberiaLocations;
import org.springframework.stereotype.Component;

@Component
@Requires({ PihCoreMetadataBundle.class } )
public class LiberiaLocationsBundle extends PihLocationsMetadataBundle {

    @Override
    public void install() throws Exception {
        installLocation(LiberiaLocations.PLEEBO);
    }
}
