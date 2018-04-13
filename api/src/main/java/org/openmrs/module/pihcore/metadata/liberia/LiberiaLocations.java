package org.openmrs.module.pihcore.metadata.liberia;

import org.openmrs.module.metadatadeploy.descriptor.LocationDescriptor;
import org.openmrs.module.metadatadeploy.descriptor.LocationTagDescriptor;

import java.util.Collections;
import java.util.List;

public class LiberiaLocations {

    public static LocationDescriptor PLEEBO = new LocationDescriptor() {
        public String uuid() { return "f6f87d10-f290-11e4-b939-0800200c9a66"; }
        public String name() { return "Pleebo"; }
        public String description() { return "Pleebo"; }
        public List<LocationTagDescriptor> tags() { return Collections.emptyList(); }
    };

    public static LocationDescriptor HARPER = new LocationDescriptor() {
        public String uuid() { return "6fa7bab5-1ca5-4b77-8c31-80c9589af952"; }
        public String name() { return "JJ Dossen"; }
        public String description() { return "JJ Dossen"; }
        public List<LocationTagDescriptor> tags() { return Collections.emptyList(); }
    };

}
