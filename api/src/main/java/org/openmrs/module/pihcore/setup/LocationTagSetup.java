package org.openmrs.module.pihcore.setup;

import org.openmrs.Location;
import org.openmrs.LocationTag;
import org.openmrs.api.LocationService;
import org.openmrs.module.pihcore.config.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class LocationTagSetup {

    private static final Logger log = LoggerFactory.getLogger(LocationTagSetup.class);

    public static void setupLocationTags(LocationService locationService, Config config) {
        for (String locationTag : config.getLocationTags().keySet()) {
            setLocationTagsFor(locationService, locationTag, config.getLocationTags().get(locationTag));
        }
    }

    private static void setLocationTagsFor(LocationService service, String locationTag, List<String> locationsThatGetTag) {

        LocationTag tag = service.getLocationTagByName(locationTag);
        if (tag == null) {
            tag = service.getLocationTagByUuid(locationTag);
        }
        if (tag == null) {
            log.warn("Unable to find location tag: " + locationTag);
            return;
        }

        for (Location candidate : service.getAllLocations()) {
            boolean expected = false;
            if (locationsThatGetTag != null) {
                for (String location : locationsThatGetTag) {
                    if (location != null && location.equals(candidate.getUuid()) || location.equals(candidate.getName())) {
                        expected = true;
                    }
                }
            }
            boolean actual = candidate.hasTag(tag.getName());
            if (actual && !expected) {
                candidate.removeTag(tag);
                service.saveLocation(candidate);
            } else if (!actual && expected) {
                candidate.addTag(tag);
                service.saveLocation(candidate);
            }
        }
    }
}
