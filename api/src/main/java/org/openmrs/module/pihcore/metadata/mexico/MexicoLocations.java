package org.openmrs.module.pihcore.metadata.mexico;

import org.openmrs.module.metadatadeploy.descriptor.LocationDescriptor;
import org.openmrs.module.metadatadeploy.descriptor.LocationTagDescriptor;

import java.util.Collections;
import java.util.List;

public class MexicoLocations {

    public static LocationDescriptor CHIAPAS = new LocationDescriptor() {
        public String uuid() { return "5f638ab8-87f7-48bc-8111-178000380dc9"; }
        public String name() { return "Chiapas"; }
        public String description() { return "The state of Chiapas, where CES operates"; }
    };

    public static LocationDescriptor CAPITAN = new LocationDescriptor() {
        public String uuid() { return "6ed84a4b-848d-11e8-9d02-74e5f9828fde"; }
        public String name() { return "Capitan"; }
        public String description() { return "Capitan"; }
        public LocationDescriptor parent() { return MexicoLocations.CHIAPAS; }
    };

    public static LocationDescriptor HONDURAS = new LocationDescriptor() {
        public String uuid() { return "5a49103e-a263-4a83-a29a-490788226787"; }
        public String name() { return "Honduras"; }
        public String description() { return "Honduras"; }
        public LocationDescriptor parent() { return MexicoLocations.CHIAPAS; }
    };

    public static LocationDescriptor LAGUNA_DEL_COFRE = new LocationDescriptor() {
        public String uuid() { return "63133477-8535-11e8-9d02-74e5f9828fde"; }
        public String name() { return "Laguna del Cofre"; }
        public String description() { return "Laguna del Cofre"; }
        public LocationDescriptor parent() { return MexicoLocations.CHIAPAS; }
    };

    public static LocationDescriptor LETRERO = new LocationDescriptor() {
        public String uuid() { return "928cb518-8535-11e8-9d02-74e5f9828fde"; }
        public String name() { return "Letrero"; }
        public String description() { return "Letrero"; }
        public LocationDescriptor parent() { return MexicoLocations.CHIAPAS; }
    };

    public static LocationDescriptor MATAZANO = new LocationDescriptor() {
        public String uuid() { return "98c940fd-8535-11e8-9d02-74e5f9828fde"; }
        public String name() { return "Matazano"; }
        public String description() { return "Matazano"; }
        public LocationDescriptor parent() { return MexicoLocations.CHIAPAS; }
    };

    public static LocationDescriptor MONTERREY = new LocationDescriptor() {
        public String uuid() { return "a27578cc-8535-11e8-9d02-74e5f9828fde"; }
        public String name() { return "Monterrey"; }
        public String description() { return "Monterrey"; }
        public LocationDescriptor parent() { return MexicoLocations.CHIAPAS; }
    };

    public static LocationDescriptor PLAN_DE_LA_LIBERTAD = new LocationDescriptor() {
        public String uuid() { return "aa4bb709-8535-11e8-9d02-74e5f9828fde"; }
        public String name() { return "Plan de la Libertad"; }
        public String description() { return "Plan de la Libertad"; }
        public LocationDescriptor parent() { return MexicoLocations.CHIAPAS; }
    };

    public static LocationDescriptor REFORMA = new LocationDescriptor() {
        public String uuid() { return "b4c7e4f9-8535-11e8-9d02-74e5f9828fde"; }
        public String name() { return "Reforma"; }
        public String description() { return "Reforma"; }
        public LocationDescriptor parent() { return MexicoLocations.CHIAPAS; }
    };

    public static LocationDescriptor SALVADOR = new LocationDescriptor() {
        public String uuid() { return "bb54f7df-8535-11e8-9d02-74e5f9828fde"; }
        public String name() { return "Salvador"; }
        public String description() { return "Salvador"; }
        public LocationDescriptor parent() { return MexicoLocations.CHIAPAS; }
    };

    public static LocationDescriptor SOLEDAD = new LocationDescriptor() {
        public String uuid() { return "c31869d6-8535-11e8-9d02-74e5f9828fde"; }
        public String name() { return "Soledad"; }
        public String description() { return "Soledad"; }
        public LocationDescriptor parent() { return MexicoLocations.CHIAPAS; }
    };

    // UNINSTALLED
    public static LocationDescriptor JALTENANGO = new LocationDescriptor() {
        public String uuid() { return "8be98d12-848d-11e8-9d02-74e5f9828fde"; }
        public String name() { return "Jaltenango"; }
        public String description() { return "Jaltenango, the CES main office location"; }
        public LocationDescriptor parent() { return MexicoLocations.CHIAPAS; }
    };

}

