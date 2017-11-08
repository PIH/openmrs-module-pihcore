package org.openmrs.module.pihcore.metadata.haiti;

import org.openmrs.module.metadatadeploy.descriptor.LocationDescriptor;
import org.openmrs.module.metadatadeploy.descriptor.LocationTagDescriptor;
import org.openmrs.module.pihcore.metadata.core.LocationTags;

import java.util.Arrays;
import java.util.List;

public class PihHaitiLocations {

    public static LocationDescriptor BELLADERE = new LocationDescriptor() {
        public String uuid() { return "d6dafa1a-9a2f-4f61-a33e-410acb64b0e9"; }
        public String name() { return "Hôpital Notre-Dame de la Nativité de Belladère"; }
        public String description() { return "Hôpital Notre-Dame de la Nativité de Belladère"; }
        public List<LocationTagDescriptor> tags() { return Arrays.asList(LocationTags.VISIT_LOCATION); }
    };

    public static LocationDescriptor BOUCAN_CARRE = new LocationDescriptor() {
        public String uuid() { return "9bc65df6-99a2-4d43-b56b-f42bde3f5d6d"; }
        public String name() { return "Centre de Santé Saint-Michel de Boucan-Carré"; }
        public String description() { return "Centre de Santé Saint-Michel de Boucan-Carré"; }
        public List<LocationTagDescriptor> tags() { return Arrays.asList(LocationTags.VISIT_LOCATION); }
    };

    public static LocationDescriptor CANGE = new LocationDescriptor() {
        public String uuid() { return "328f68e4-0370-102d-b0e3-001ec94a0cc"; }
        public String name() { return "Hôpital Bon Sauveur de Cange"; }
        public String description() { return "Hôpital Bon Sauveur de Cange"; }
        public List<LocationTagDescriptor> tags() { return Arrays.asList(LocationTags.VISIT_LOCATION); }
    };

    public static LocationDescriptor CERCA_LA_SOURCE = new LocationDescriptor() {
        public String uuid() { return "c741e25d-eb07-4efc-89e4-38ac73948ae1"; }
        public String name() { return "Centre de Santé de Cerca la Source"; }
        public String description() { return "Centre de Santé de Cerca la Source"; }
        public List<LocationTagDescriptor> tags() { return Arrays.asList(LocationTags.VISIT_LOCATION); }
    };

    public static LocationDescriptor HINCHE = new LocationDescriptor() {
        public String uuid() { return "328f6a60-0370-102d-b0e3-001ec94a0cc1"; }
        public String name() { return "Hôpital Sainte-Thérèse de Hinche"; }
        public String description() { return "Hôpital Sainte-Thérèse de Hinche"; }
        public List<LocationTagDescriptor> tags() { return Arrays.asList(LocationTags.VISIT_LOCATION); }
    };

    public static LocationDescriptor HSN_SAINT_MARC = new LocationDescriptor() {
        public String uuid() { return "97c241a0-eaec-11e5-a837-0800200c9a66"; }
        public String name() { return "Hôpital Saint-Nicolas de Saint-Marc"; }
        public String description() { return "Hôpital Saint-Nicolas de Saint-Marc"; }
        public List<LocationTagDescriptor> tags() { return Arrays.asList(LocationTags.VISIT_LOCATION); }
    };

    public static LocationDescriptor LACOLLINE = new LocationDescriptor() {
        public String uuid() { return "23e7bb0d-51f9-4d5f-b34b-2fbbfeea1960"; }
        public String name() { return "Hôpital la Colline de Lascahobas"; }
        public String description() { return "Hôpital la Colline de Lascahobas"; }
        public List<LocationTagDescriptor> tags() { return Arrays.asList(LocationTags.VISIT_LOCATION); }
    };

    public static LocationDescriptor PETITE_RIVIERE = new LocationDescriptor() {
        public String uuid() { return "a7af173a-f623-445a-bf01-e6a64c0b2c98"; }
        public String name() { return "Centre Médical Charles Colimon de Petite Riviere de l'Artibonite"; }
        public String description() { return "Centre Médical Charles Colimon de Petite Riviere de l'Artibonite"; }
        public List<LocationTagDescriptor> tags() { return Arrays.asList(LocationTags.VISIT_LOCATION); }
    };

    public static LocationDescriptor SSPE_SAINT_MARC = new LocationDescriptor() {
        public String uuid() { return "209c84cf-4cd7-4908-a946-030499c1ff75"; }
        public String name() { return "SSPE de Saint-Marc"; }
        public String description() { return "SSPE de Saint-Marc"; }
        public List<LocationTagDescriptor> tags() { return Arrays.asList(LocationTags.VISIT_LOCATION); }
    };

    public static LocationDescriptor THOMONDE = new LocationDescriptor() {
        public String uuid() { return "376b3e7e-f7c0-4268-a98d-c2bddfee8bcf"; }
        public String name() { return "Centre de Santé de Thomonde"; }
        public String description() { return "Centre de Santé de Thomonde"; }
        public List<LocationTagDescriptor> tags() { return Arrays.asList(LocationTags.VISIT_LOCATION); }
    };

    public static LocationDescriptor VERRETTES = new LocationDescriptor() {
        public String uuid() { return "6c1c2579-7d16-4d39-bca2-e77141ff435f"; }
        public String name() { return "Hôpital Dumarsais Estimé de Verrettes"; }
        public String description() { return "Hôpital Dumarsais Estimé de Verrettes"; }
        public List<LocationTagDescriptor> tags() { return Arrays.asList(LocationTags.VISIT_LOCATION); }
    };


    // we are uninstalling this until we actually start to use it
    public static LocationDescriptor LASCAHOBAS = new LocationDescriptor() {
        public String uuid() { return "59326152-F3AD-42A1-AAEF-4114E6D88B81"; }
        public String name() { return "Lascahobas"; }
        public String description() { return "Lascahobas"; }
        public List<LocationTagDescriptor> tags() { return Arrays.asList(LocationTags.VISIT_LOCATION); }
    };

    // we are uninstalling this until we actually start to use it
    public static LocationDescriptor POZ = new LocationDescriptor() {
        public String uuid() { return "c488ed05-f259-4f7b-a9d4-8f56736da691"; }
        public String name() { return "POZ"; }
        public String description() { return "Promotion Objectif Zerosida"; }
        public List<LocationTagDescriptor> tags() { return Arrays.asList(LocationTags.VISIT_LOCATION); }
    };

}
