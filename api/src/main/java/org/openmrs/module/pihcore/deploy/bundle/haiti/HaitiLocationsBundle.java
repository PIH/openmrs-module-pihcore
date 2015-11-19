package org.openmrs.module.pihcore.deploy.bundle.haiti;

import org.openmrs.module.metadatadeploy.bundle.LocationsMetadataBundle;
import org.openmrs.module.metadatadeploy.bundle.Requires;
import org.openmrs.module.pihcore.deploy.bundle.core.LocationAttributeTypeBundle;
import org.openmrs.module.pihcore.deploy.bundle.core.LocationTagBundle;
import org.openmrs.module.pihcore.metadata.haiti.HaitiLocations;
import org.springframework.stereotype.Component;

@Component
@Requires({ LocationTagBundle.class, LocationAttributeTypeBundle.class} )
public class HaitiLocationsBundle extends LocationsMetadataBundle {

    @Override
    public void install() throws Exception {
        installLocation(HaitiLocations.LACOLLINE);
    }
}
