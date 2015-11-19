package org.openmrs.module.pihcore.metadata.liberia;

import org.openmrs.module.metadatadeploy.descriptor.LocationDescriptor;
import org.openmrs.module.metadatadeploy.descriptor.LocationTagDescriptor;
import org.openmrs.module.pihcore.metadata.core.LocationTags;

import java.util.Arrays;
import java.util.List;

public class LiberiaLocations {

    public static LocationDescriptor PLEEBO = new LocationDescriptor() {
        public String uuid() { return "f6f87d10-f290-11e4-b939-0800200c9a66"; }
        public String name() { return "Pleebo"; }
        public String description() { return "Pleebo"; }
        public List<LocationTagDescriptor> tags() { return Arrays.asList(LocationTags.MEDICAL_RECORD_LOCATION, LocationTags.ARCHIVES_LOCATION, LocationTags.VISIT_LOCATION); }
    };

}
