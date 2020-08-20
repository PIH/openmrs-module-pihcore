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

    public static LocationDescriptor PLAN_ALTA = new LocationDescriptor() {
        public String uuid() { return "06109e0d-4a83-4f02-be62-8175bf04f8f0"; }
        public String name() { return "Plan Alta"; }
        public String description() { return "Plan de la Libertad, Alta"; }
        public LocationDescriptor parent() { return MexicoLocations.CHIAPAS; }
    };

    public static LocationDescriptor PLAN_BAJA = new LocationDescriptor() {
        public String uuid() { return "e1ae1851-1611-42c1-8b99-f4b719faedc1"; }
        public String name() { return "Plan Baja"; }
        public String description() { return "Plan de la Libertad, Baja"; }
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

    // Served from cloud instance //

    public static LocationDescriptor HOSPITAL = new LocationDescriptor() {
        public String uuid() { return "8bb35919-daff-412f-868c-9db7a0c2bac4"; }
        public String name() { return "Hospital"; }
        public String description() { return "Hospital"; }
        public LocationDescriptor parent() { return MexicoLocations.CHIAPAS; }
    };

    public static LocationDescriptor OFFICE = new LocationDescriptor() {
        public String uuid() { return "17b5962a-8145-4738-be8d-537dc5c103e7"; }
        public String name() { return "CES Oficina"; }
        public String description() { return "CES Oficina"; }
        public LocationDescriptor parent() { return MexicoLocations.CHIAPAS; }
    };

    public static LocationDescriptor CASA_MATERNA = new LocationDescriptor() {
        public String uuid() { return "1d7567a9-64d0-4b79-82a5-59d9e930e426"; }
        public String name() { return "Casa Materna"; }
        public String description() { return "Birthing center operated by CES in Jaltenango"; }
        public LocationDescriptor parent() { return MexicoLocations.CHIAPAS; }
    };

    public static LocationDescriptor CER = new LocationDescriptor() {
        public String uuid() { return "431f3329-3a87-48fa-a60a-255cbc328b0c"; }
        public String name() { return "CER"; }
        public String description() { return "Clínica de Enfermedades Respiratorias, the CES COVID-19 clinic"; }
        public LocationDescriptor parent() { return MexicoLocations.CHIAPAS; }
    };

    // UNINSTALLED
    public static LocationDescriptor SURGERY = new LocationDescriptor() {
        public String uuid() { return "78a18ed4-c798-4f47-a0f1-7e483c89da37"; }
        public String name() { return "Surgery"; }
        public String description() { return "Surgery"; }
        public LocationDescriptor parent() { return MexicoLocations.CHIAPAS; }
    };

    public static LocationDescriptor JALTENANGO = new LocationDescriptor() {
        public String uuid() { return "8be98d12-848d-11e8-9d02-74e5f9828fde"; }
        public String name() { return "Jaltenango"; }
        public String description() { return "Jaltenango, the CES main office location"; }
        public LocationDescriptor parent() { return MexicoLocations.CHIAPAS; }
    };

    public static LocationDescriptor PLAN_DE_LA_LIBERTAD = new LocationDescriptor() {
        public String uuid() { return "aa4bb709-8535-11e8-9d02-74e5f9828fde"; }
        public String name() { return "Plan de la Libertad"; }
        public String description() { return "Plan de la Libertad"; }
        public LocationDescriptor parent() { return MexicoLocations.CHIAPAS; }
    };

}

