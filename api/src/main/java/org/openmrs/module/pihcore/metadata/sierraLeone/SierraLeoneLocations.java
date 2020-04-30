package org.openmrs.module.pihcore.metadata.sierraLeone;

import org.openmrs.module.metadatadeploy.descriptor.LocationDescriptor;

public class SierraLeoneLocations {

    public static LocationDescriptor WELLBODY = new LocationDescriptor() {
        public String uuid() { return "0561303b-9868-4a1d-933d-fe52ce1b8c9f"; }
        public String name() { return "Wellbody"; }
        public String description() { return "Wellbody"; }
    };

    public static LocationDescriptor WELLBODY_CLINIC = new LocationDescriptor() {
        public String uuid() { return "b6733150-7426-11e5-a837-0800200c9a66"; }
        public String name() { return "Wellbody Clinic"; }
        public String description() { return "Wellbody Clinic"; }
        public LocationDescriptor parent() {
            return SierraLeoneLocations.WELLBODY;
        }
    };

    public static LocationDescriptor WELLBODY_MCH = new LocationDescriptor() {
        public String uuid() { return "dac348b7-2ece-47ad-b80a-140fc6788706"; }
        public String name() { return "Wellbody MCH"; }
        public String description() { return "Wellbody MCH"; }
        public LocationDescriptor parent() {
            return SierraLeoneLocations.WELLBODY;
        }
    };
}
