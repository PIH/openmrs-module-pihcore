package org.openmrs.module.pihcore.setup;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openmrs.LocationTag;
import org.openmrs.api.LocationService;
import org.openmrs.module.pihcore.config.Config;
import org.openmrs.module.pihcore.PihCoreContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class LocationTagSetupTest extends PihCoreContextSensitiveTest {

    private static String ADMISSION_LOCATION_TAG_UUID = "2718d6bb-94f8-46ba-82e4-f35fcd25598e";
    private static String VISIT_LOCATION_TAG_UUID = "f4d0d8fd-adaf-4e60-b2ad-50c0b3153b37";

    @Autowired
    private LocationService locationService;

    @BeforeEach
    public void setup() {
        // create a couple location tags
        LocationTag admissionLocationTag = new LocationTag();
        admissionLocationTag.setUuid(ADMISSION_LOCATION_TAG_UUID);
        admissionLocationTag.setName("Admission Location");
        locationService.saveLocationTag(admissionLocationTag);

        LocationTag visitLocation = new LocationTag();
        visitLocation.setUuid(VISIT_LOCATION_TAG_UUID);
        visitLocation.setName("Visit Location");
        locationService.saveLocationTag(visitLocation);
    }

    @Test
    public void shouldSetLocationTagByName() {
        Map<String, List<String>> locationTags = new HashMap<>();

        List<String> visitLocations = new ArrayList<>();
        visitLocations.add("Xanadu");
        visitLocations.add("Never Never Land");

        List<String> admissionLocations = new ArrayList<>();
        admissionLocations.add("Xanadu");

        locationTags.put("Visit Location", visitLocations);
        locationTags.put("Admission Location", admissionLocations);

        Config config = mock(Config.class);

        when(config.getLocationTags()).thenReturn(locationTags);
        LocationTagSetup.setupLocationTags(locationService, config);

        assertTrue(locationService.getLocation("Xanadu").hasTag("Visit Location"));
        assertTrue(locationService.getLocation("Xanadu").hasTag("Admission Location"));
        assertTrue(locationService.getLocation("Never Never Land").hasTag("Visit Location"));
        assertFalse(locationService.getLocation("Never Never Land").hasTag("Admission Location"));
    }

    @Test
    public void shouldSetLocationTagByUUID() {
        Map<String, List<String>> locationTags = new HashMap<>();

        List<String> visitLocations = new ArrayList<>();
        visitLocations.add("9356400c-a5a2-4532-8f2b-2361b3446eb8"); // Xanadu
        visitLocations.add("167ce20c-4785-4285-9119-d197268f7f4a"); // Never Never Land

        List<String> admissionLocations = new ArrayList<>();
        admissionLocations.add("9356400c-a5a2-4532-8f2b-2361b3446eb8"); // Xanadu

        locationTags.put(VISIT_LOCATION_TAG_UUID, visitLocations);
        locationTags.put(ADMISSION_LOCATION_TAG_UUID, admissionLocations);

        Config config = mock(Config.class);

        when(config.getLocationTags()).thenReturn(locationTags);
        LocationTagSetup.setupLocationTags(locationService, config);

        assertTrue(locationService.getLocation("Xanadu").hasTag("Visit Location"));
        assertTrue(locationService.getLocation("Xanadu").hasTag("Admission Location"));
        assertTrue(locationService.getLocation("Never Never Land").hasTag("Visit Location"));
        assertFalse(locationService.getLocation("Never Never Land").hasTag("Admission Location"));
    }

}
