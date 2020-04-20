package org.openmrs.module.pihcore.deploy.bundle.liberia;

import org.openmrs.Location;
import org.openmrs.module.metadatadeploy.bundle.AbstractMetadataBundle;
import org.openmrs.module.metadatadeploy.bundle.Requires;
import org.openmrs.module.pihcore.deploy.bundle.core.PihCoreMetadataBundle;
import org.openmrs.module.pihcore.metadata.liberia.LiberiaLocations;
import org.springframework.stereotype.Component;

@Component
@Requires({PihCoreMetadataBundle.class})
public class LiberiaLocationsBundle extends AbstractMetadataBundle {

    @Override
    public void install() throws Exception {
        install(LiberiaLocations.HEALTH_FACILITY);
        install(LiberiaLocations.RECORDS_ROOM);
        install(LiberiaLocations.OUTPATIENT_CLINIC);
        install(LiberiaLocations.MENTAL_HEALTH_CLINIC);
        install(LiberiaLocations.NCD_CLINIC);

        uninstall(possible(Location.class, LiberiaLocations.HARPER.uuid()), "replaced by Health Facility location");
        uninstall(possible(Location.class, LiberiaLocations.PLEEBO.uuid()), "replaced by Health Facility location");
    }
}
