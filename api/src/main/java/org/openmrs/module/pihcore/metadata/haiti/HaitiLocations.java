package org.openmrs.module.pihcore.metadata.haiti;

import org.openmrs.module.metadatadeploy.descriptor.LocationDescriptor;
import org.openmrs.module.metadatadeploy.descriptor.LocationTagDescriptor;
import org.openmrs.module.pihcore.metadata.core.LocationTags;

import java.util.Arrays;
import java.util.List;

public class HaitiLocations {

    public static LocationDescriptor LACOLLINE = new LocationDescriptor() {
        public String uuid() { return "23e7bb0d-51f9-4d5f-b34b-2fbbfeea1960"; }
        public String name() { return "Lacolline"; }
        public String description() { return "Lacolline"; }
        public List<LocationTagDescriptor> tags() { return Arrays.asList(LocationTags.VISIT_LOCATION); }
    };


    public static LocationDescriptor CANGE = new LocationDescriptor() {
        public String uuid() { return "328f68e4-0370-102d-b0e3-001ec94a0cc"; }
        public String name() { return "Cange"; }
        public String description() { return "Cange"; }
        public List<LocationTagDescriptor> tags() { return Arrays.asList(LocationTags.VISIT_LOCATION); }
    };


    public static LocationDescriptor HINCHE = new LocationDescriptor() {
        public String uuid() { return "328f6a60-0370-102d-b0e3-001ec94a0cc1"; }
        public String name() { return "Hinche"; }
        public String description() { return "Hinche"; }
        public List<LocationTagDescriptor> tags() { return Arrays.asList(LocationTags.VISIT_LOCATION); }
    };

   /* public static LocationDescriptor BOUCAN_CARRE = new LocationDescriptor() {
        public String uuid() { return "9bc65df6-99a2-4d43-b56b-f42bde3f5d6d"; }
        public String name() { return "Boucan-Carré"; }
        public String description() { return "Boucan-Carré"; }
        public List<LocationTagDescriptor> tags() { return Arrays.asList(LocationTags.VISIT_LOCATION); }
    };

    public static LocationDescriptor BELLADERES = new LocationDescriptor() {
        public String uuid() { return "d6dafa1a-9a2f-4f61-a33e-410acb64b0e9"; }
        public String name() { return "Belladères"; }
        public String description() { return "Belladères"; }
        public List<LocationTagDescriptor> tags() { return Arrays.asList(LocationTags.VISIT_LOCATION); }
    };
*/


}
