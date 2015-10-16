package org.openmrs.module.pihcore.deploy.bundle.sierraLeone;

import org.openmrs.module.metadatadeploy.bundle.Requires;
import org.openmrs.module.pihcore.deploy.bundle.PihLocationsMetadataBundle;
import org.openmrs.module.pihcore.deploy.bundle.core.PihCoreMetadataBundle;
import org.openmrs.module.pihcore.metadata.sierraLeone.SierraLeoneLocations;
import org.springframework.stereotype.Component;

@Component
@Requires({ PihCoreMetadataBundle.class } )
public class SierraLeoneLocationsBundle extends PihLocationsMetadataBundle {

    @Override
    public void install() throws Exception {
        installLocation(SierraLeoneLocations.WELLBODY_HEALTH_CENTER);
    }
}

