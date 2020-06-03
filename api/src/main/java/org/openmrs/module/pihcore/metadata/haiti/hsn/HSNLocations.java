package org.openmrs.module.pihcore.metadata.haiti.hsn;

import org.openmrs.module.metadatadeploy.descriptor.LocationDescriptor;
import org.openmrs.module.pihcore.metadata.haiti.PihHaitiLocations;

public class HSNLocations {

    // we've expanded HSN to include sub-locations and are defining them here;
    // the parent HSN location is still installed in main PihHaitiLocations bundle
    // currently, these locations are *only* installed on the HSN server,
    // but when we switch to Iniz loading we may not preserve this
    // in preparation for that I'd appended the location names with "(HSN)" to make
    // them distinct from the equivalent Mirebalais locations

    public static LocationDescriptor COVID19_ISOLATION_HSN = new LocationDescriptor() {
        public String uuid() { return "6dcf6d61-b7b8-4e4e-bc2b-519b13f0a03a"; }
        public String name() { return "COVID-19 | Izolman | HSN"; }
        public String description() { return "COVID-19 Isolation"; }
        public LocationDescriptor parent() { return PihHaitiLocations.HSN_SAINT_MARC; }
    };

    public static LocationDescriptor COVID19_UMI_HSN = new LocationDescriptor() {
        public String uuid() { return "387956c5-c991-4a2d-a03e-c53c11200ef7"; }
        public String name() { return "COVID-19 | UMI | HSN"; }
        public String description() { return "Inite maladi enfeksyon (UMI) COVID-19"; }
        public LocationDescriptor parent() { return PihHaitiLocations.HSN_SAINT_MARC; }
    };

    public static LocationDescriptor SANTE_MANTAL_HSN = new LocationDescriptor() {
        public String uuid() { return "85c67863-1341-4bcc-a159-35cf202f3a25"; }
        public String name() { return "Sante Mantal | HSN"; }
        public String description() { return "Sante Mantal | HSN"; }  // TODO better description here?
        public LocationDescriptor parent() { return PihHaitiLocations.HSN_SAINT_MARC; }
    };
}
