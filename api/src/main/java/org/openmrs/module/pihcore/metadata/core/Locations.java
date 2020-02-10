package org.openmrs.module.pihcore.metadata.core;

import org.openmrs.module.metadatadeploy.descriptor.LocationDescriptor;

public class Locations {


    public static LocationDescriptor UNKNOWN = new LocationDescriptor() {
        public String uuid() { return "8d6c993e-c2cc-11de-8d13-0010c6dffd0f"; }
        public String name() { return "Unknown Location"; }
        public String description() { return "Unknown Location"; }
    };

    public static LocationDescriptor HOME_VISIT = new LocationDescriptor() {
        public String uuid() { return "AF939782-898B-409A-99A4-D1653484EDBD"; }
        public String name() { return "Home Visit"; }
        public String description() { return "The patient is visited at home"; }
    };


}
