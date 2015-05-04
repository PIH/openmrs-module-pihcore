package org.openmrs.module.pihcore.metadata.core;

import org.openmrs.module.pihcore.descriptor.LocationDescriptor;

public class Locations {


    public static LocationDescriptor UNKNOWN = new LocationDescriptor() {
        public String uuid() { return "8d6c993e-c2cc-11de-8d13-0010c6dffd0f"; }
        public String name() { return "Unknown Location"; }
        public String description() { return "Unknown Location"; }
    };


}
