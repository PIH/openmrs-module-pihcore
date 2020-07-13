package org.openmrs.module.pihcore.metadata.liberia;

import org.openmrs.module.metadatadeploy.descriptor.LocationDescriptor;
import org.openmrs.module.metadatadeploy.descriptor.LocationTagDescriptor;

import java.util.Collections;
import java.util.List;

public class LiberiaLocations {

    public static LocationDescriptor HEALTH_FACILITY = new LocationDescriptor() {
        @Override
        public String name() {
            return "Health Facility";
        }

        @Override
        public String description() {
            return "A place where health care is provided, including hospitals, clinics and health centers";
        }

        @Override
        public String uuid() {
            return "5af1ffcd-5178-11ea-a500-645d86728797";
        }
    };

    public static LocationDescriptor RECORDS_ROOM = new LocationDescriptor() {
        @Override
        public String name() {
            return "Records Room";
        }

        @Override
        public String description() {
            return "The records room at a health facility";
        }

        @Override
        public String uuid() {
            return "8a4e37e3-5178-11ea-a500-645d86728797";
        }

        public LocationDescriptor parent() {
            return LiberiaLocations.HEALTH_FACILITY;
        }
    };

    public static LocationDescriptor OUTPATIENT_CLINIC = new LocationDescriptor() {
        @Override
        public String name() {
            return "Outpatient Clinic";
        }

        @Override
        public String description() {
            return "The outpatient clinic";
        }

        @Override
        public String uuid() {
            return "267a0c52-5179-11ea-a500-645d86728797";
        }

        public LocationDescriptor parent() {
            return LiberiaLocations.HEALTH_FACILITY;
        }
    };

    public static LocationDescriptor MENTAL_HEALTH_CLINIC = new LocationDescriptor() {
        @Override
        public String name() {
            return "Mental Health Clinic";
        }

        @Override
        public String description() {
            return "Mental health clinic";
        }

        @Override
        public String uuid() {
            return "8b8dad4c-5179-11ea-a500-645d86728797";
        }

        public LocationDescriptor parent() {
            return LiberiaLocations.HEALTH_FACILITY;
        }
    };

    public static LocationDescriptor NCD_CLINIC = new LocationDescriptor() {
        @Override
        public String name() {
            return "NCD Clinic";
        }

        @Override
        public String description() {
            return "NCD clinic";
        }

        @Override
        public String uuid() {
            return "b7cfea60-5179-11ea-a500-645d86728797";
        }

        public LocationDescriptor parent() {
            return LiberiaLocations.HEALTH_FACILITY;
        }
    };

    //mch locations
    public static LocationDescriptor MCH_CLINIC = new LocationDescriptor() {
        @Override
        public String name() {
            return "MCH Clinic";
        }

        @Override
        public String description() {
            return "MCH clinic";
        }

        @Override
        public String uuid() {
            return "2e8c9500-bd41-11ea-aee0-3c6aa7c392cc";
        } // this uuid needs to be generated.

        public LocationDescriptor parent() {
            return LiberiaLocations.HEALTH_FACILITY;
        }
    };



    // legacy, replaced by new "Health Facility" locatin
    public static LocationDescriptor PLEEBO = new LocationDescriptor() {
        public String uuid() {
            return "f6f87d10-f290-11e4-b939-0800200c9a66";
        }

        public String name() {
            return "Pleebo";
        }

        public String description() {
            return "Pleebo";
        }

        public List<LocationTagDescriptor> tags() {
            return Collections.emptyList();
        }
    };

    public static LocationDescriptor HARPER = new LocationDescriptor() {
        public String uuid() {
            return "6fa7bab5-1ca5-4b77-8c31-80c9589af952";
        }

        public String name() {
            return "JJ Dossen";
        }

        public String description() {
            return "JJ Dossen";
        }

        public List<LocationTagDescriptor> tags() {
            return Collections.emptyList();
        }
    };

}
