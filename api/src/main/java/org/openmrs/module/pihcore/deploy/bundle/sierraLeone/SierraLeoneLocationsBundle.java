package org.openmrs.module.pihcore.deploy.bundle.sierraLeone;

import org.openmrs.module.metadatadeploy.bundle.AbstractMetadataBundle;
import org.openmrs.module.metadatadeploy.bundle.Requires;
import org.openmrs.module.pihcore.deploy.bundle.core.PihCoreMetadataBundle;
import org.openmrs.module.pihcore.metadata.sierraLeone.SierraLeoneLocations;
import org.springframework.stereotype.Component;

@Component
@Requires({ PihCoreMetadataBundle.class } )
public class SierraLeoneLocationsBundle extends AbstractMetadataBundle {

    @Override
    public void install() throws Exception {
        install(SierraLeoneLocations.WELLBODY_HEALTH_CENTER);
    }
}

