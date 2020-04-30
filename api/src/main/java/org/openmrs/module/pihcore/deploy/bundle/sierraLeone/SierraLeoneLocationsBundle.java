package org.openmrs.module.pihcore.deploy.bundle.sierraLeone;

import org.openmrs.Location;
import org.openmrs.api.LocationService;
import org.openmrs.api.context.Context;
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
        install(SierraLeoneLocations.WELLBODY);
        install(SierraLeoneLocations.WELLBODY_CLINIC);
        install(SierraLeoneLocations.WELLBODY_MCH);

        // the Wellbody Clinic isn't updated properly, and I can't for the life of me figure out why (some kind of lazy loading with location ID?), so
        // doing it manually here, since we will be switching to Iniz for Locations in the near future
        LocationService locationService = Context.getLocationService();
        Location location = locationService.getLocationByUuid(SierraLeoneLocations.WELLBODY_CLINIC.uuid());
        location.setName(SierraLeoneLocations.WELLBODY_CLINIC.name());
        location.setDescription(SierraLeoneLocations.WELLBODY_CLINIC.description());
        location.setParentLocation(locationService.getLocationByUuid(SierraLeoneLocations.WELLBODY.uuid()));
        locationService.saveLocation(location);
    }
}

