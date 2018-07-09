package org.openmrs.module.pihcore.metadata.mexico;

import org.openmrs.module.metadatadeploy.descriptor.LocationDescriptor;
import org.openmrs.module.metadatadeploy.descriptor.LocationTagDescriptor;
import org.openmrs.module.pihcore.metadata.core.LocationTags;

import java.util.Arrays;
import java.util.Collections;

public class MexicoLocations {

    public static LocationDescriptor JALTENANGO = new LocationDescriptor() {
        public String uuid() { return "8be98d12-848d-11e8-9d02-74e5f9828fde"; }
        public String name() { return "Jaltenango"; }
        public String description() { return "Jaltenango"; }
        public List<LocationTagDescriptor> tags() { return Collections.emptyList(); }
    };

    public static LocationDescriptor CAPITAN = new LocationDescriptor() {
        public String uuid() { return "6ed84a4b-848d-11e8-9d02-74e5f9828fde"; }
        public String name() { return "Capitan"; }
        public String description() { return "Capitan"; }
        public List<LocationTagDescriptor> tags() { return Arrays.asList(LocationTags.VISIT_LOCATION); }
    };

    public static LocationDescriptor HONDURAS = new LocationDescriptor() {
        public String uuid() { return "5a49103e-a263-4a83-a29a-490788226787"; }
        public String name() { return "Honduras"; }
        public String description() { return "Honduras"; }
        public List<LocationTagDescriptor> tags() { return Arrays.asList(LocationTags.VISIT_LOCATION); }
    };

    public static LocationDescriptor LAGUNA_DEL_COFRE = new LocationDescriptor() {
        public String uuid() { return "63133477-8535-11e8-9d02-74e5f9828fde"; }
        public String name() { return "Laguna del Cofre"; }
        public String description() { return "Laguna del Cofre"; }
        public List<LocationTagDescriptor> tags() { return Arrays.asList(LocationTags.VISIT_LOCATION); }
    };

    public static LocationDescriptor LETRERO = new LocationDescriptor() {
        public String uuid() { return "928cb518-8535-11e8-9d02-74e5f9828fde"; }
        public String name() { return "Letrero"; }
        public String description() { return "Letrero"; }
        public List<LocationTagDescriptor> tags() { return Arrays.asList(LocationTags.VISIT_LOCATION); }
    };

    public static LocationDescriptor MATAZANO = new LocationDescriptor() {
        public String uuid() { return "98c940fd-8535-11e8-9d02-74e5f9828fde"; }
        public String name() { return "Matazano"; }
        public String description() { return "Matazano"; }
        public List<LocationTagDescriptor> tags() { return Arrays.asList(LocationTags.VISIT_LOCATION); }
    };

    public static LocationDescriptor MONTERREY = new LocationDescriptor() {
        public String uuid() { return "a27578cc-8535-11e8-9d02-74e5f9828fde"; }
        public String name() { return "Monterrey"; }
        public String description() { return "Monterrey"; }
        public List<LocationTagDescriptor> tags() { return Arrays.asList(LocationTags.VISIT_LOCATION); }
    };

    public static LocationDescriptor PLAN_DE_LA_LIBERTAD = new LocationDescriptor() {
        public String uuid() { return "aa4bb709-8535-11e8-9d02-74e5f9828fde"; }
        public String name() { return "Plan de la Libertad"; }
        public String description() { return "Plan de la Libertad"; }
        public List<LocationTagDescriptor> tags() { return Arrays.asList(LocationTags.VISIT_LOCATION); }
    };

    public static LocationDescriptor REFORMA = new LocationDescriptor() {
        public String uuid() { return "b4c7e4f9-8535-11e8-9d02-74e5f9828fde"; }
        public String name() { return "Reforma"; }
        public String description() { return "Reforma"; }
        public List<LocationTagDescriptor> tags() { return Arrays.asList(LocationTags.VISIT_LOCATION); }
    };

    public static LocationDescriptor SALVADOR = new LocationDescriptor() {
        public String uuid() { return "bb54f7df-8535-11e8-9d02-74e5f9828fde"; }
        public String name() { return "Salvador"; }
        public String description() { return "Salvador"; }
        public List<LocationTagDescriptor> tags() { return Arrays.asList(LocationTags.VISIT_LOCATION); }
    };

    public static LocationDescriptor SOLEDAD = new LocationDescriptor() {
        public String uuid() { return "c31869d6-8535-11e8-9d02-74e5f9828fde"; }
        public String name() { return "Soledad"; }
        public String description() { return "Soledad"; }
        public List<LocationTagDescriptor> tags() { return Arrays.asList(LocationTags.VISIT_LOCATION); }
    };
}

