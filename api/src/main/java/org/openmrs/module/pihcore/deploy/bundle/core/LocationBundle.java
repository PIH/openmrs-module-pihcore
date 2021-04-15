package org.openmrs.module.pihcore.deploy.bundle.core;

import org.openmrs.module.metadatadeploy.bundle.AbstractMetadataBundle;
import org.openmrs.module.metadatadeploy.bundle.Requires;
import org.openmrs.module.pihcore.metadata.core.Locations;
import org.springframework.stereotype.Component;

@Component
public class LocationBundle extends AbstractMetadataBundle {

    @Override
    public void install() throws Exception {
        install(Locations.UNKNOWN);
    }

}
