package org.openmrs.module.pihcore.deploy.bundle.sierraLeone;

import org.openmrs.module.metadatadeploy.bundle.LocationsMetadataBundle;
import org.openmrs.module.metadatadeploy.bundle.Requires;
import org.openmrs.module.pihcore.deploy.bundle.core.PihCoreMetadataBundle;
import org.openmrs.module.pihcore.metadata.sierraLeone.SierraLeoneLocations;
import org.springframework.stereotype.Component;

@Component
@Requires({ PihCoreMetadataBundle.class } )
public class SierraLeoneLocationsBundle extends LocationsMetadataBundle {

    @Override
    public void install() throws Exception {
        installLocation(SierraLeoneLocations.WELLBODY_HEALTH_CENTER);
    }
}

