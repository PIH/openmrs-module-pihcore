package org.openmrs.module.pihcore.setup;

import org.openmrs.Location;
import org.openmrs.LocationTag;
import org.openmrs.api.LocationService;
import org.openmrs.module.metadatadeploy.descriptor.LocationDescriptor;
import org.openmrs.module.metadatadeploy.descriptor.LocationTagDescriptor;
import org.openmrs.module.pihcore.config.Config;
import org.openmrs.module.pihcore.config.ConfigDescriptor;
import org.openmrs.module.pihcore.metadata.core.LocationTags;
import org.openmrs.module.pihcore.metadata.haiti.PihHaitiLocations;
import org.openmrs.module.pihcore.metadata.haiti.mirebalais.MirebalaisLocations;
import org.openmrs.module.pihcore.metadata.liberia.LiberiaLocations;
import org.openmrs.module.pihcore.metadata.sierraLeone.SierraLeoneLocations;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class LocationTagSetup {

    // TODO make this less if-then based
    // TODO actually, when we get rid of these, we can probably set most of the tags in the bundles?
    public static void setupLocationTags(LocationService locationService, Config config) {

        if (config.getCountry().equals(ConfigDescriptor.Country.LIBERIA)) {
            if (config.getSite().equals(ConfigDescriptor.Site.PLEEBO)) {
                setupLocationTagsForPleebo(locationService);
            }
            else if (config.getSite().equals(ConfigDescriptor.Site.MENTAL_HEALTH)) {
                // TODO set up actual locations & tags for Liberia mental health
                setupLocationTagsForPleebo(locationService);
            }
        }
        else if (config.getCountry().equals(ConfigDescriptor.Country.SIERRA_LEONE)) {
            setupLocationTagsForSierraLeone(locationService);
        }
        else if (config.getCountry().equals(ConfigDescriptor.Country.HAITI)) {
            if (config.getSite().equals(ConfigDescriptor.Site.LACOLLINE)) {
                setupLocationTagsForLacolline(locationService);
            } else if (config.getSite().equals(ConfigDescriptor.Site.THOMONDE)) {
                setupLocationTagsForThomonde(locationService);
            } else if (config.getSite().equals(ConfigDescriptor.Site.ZLTRAINING)) {
                setupLocationTagsForThomonde(locationService);
            } else if (config.getSite().equals(ConfigDescriptor.Site.MIREBALAIS)) {
                setupLocationTagsForMirebalais(locationService);
            } else if (config.getSite().equals(ConfigDescriptor.Site.MENTAL_HEALTH)) {
                setupLocationTagsForHaitiMentalHealth(locationService);
            }
        }
    }

    private static void setupLocationTagsForSierraLeone(LocationService locationService) {
        setLocationTagsFor(locationService, LocationTags.LOGIN_LOCATION, Arrays.asList(SierraLeoneLocations.WELLBODY_HEALTH_CENTER));
        setLocationTagsFor(locationService, LocationTags.CHECKIN_LOCATION, Arrays.asList(SierraLeoneLocations.WELLBODY_HEALTH_CENTER));
        setLocationTagsFor(locationService, LocationTags.REGISTRATION_LOCATION, Arrays.asList(SierraLeoneLocations.WELLBODY_HEALTH_CENTER));
    }

    private static void setupLocationTagsForPleebo(LocationService locationService) {
        setLocationTagsFor(locationService, LocationTags.LOGIN_LOCATION, Arrays.asList(LiberiaLocations.PLEEBO));
        setLocationTagsFor(locationService, LocationTags.CHECKIN_LOCATION, Arrays.asList(LiberiaLocations.PLEEBO));
        setLocationTagsFor(locationService, LocationTags.REGISTRATION_LOCATION, Arrays.asList(LiberiaLocations.PLEEBO));
        setLocationTagsFor(locationService, LocationTags.CONSULT_NOTE_LOCATION, Arrays.asList(LiberiaLocations.PLEEBO));
    }

    private static void setupLocationTagsForLacolline(LocationService locationService) {
        setStandardHaitiSingleSiteLocationTags(locationService, PihHaitiLocations.LACOLLINE);
    }

    private static void setupLocationTagsForThomonde(LocationService locationService) {
        setStandardHaitiSingleSiteLocationTags(locationService, PihHaitiLocations.THOMONDE);
    }

    private static void setupLocationTagsForZLTraining(LocationService locationService) {
        setStandardHaitiSingleSiteLocationTags(locationService, PihHaitiLocations.THOMONDE);
    }

    private static void setStandardHaitiSingleSiteLocationTags(LocationService locationService, LocationDescriptor location) {
        setLocationTagsFor(locationService, LocationTags.LOGIN_LOCATION, Arrays.asList(location));
        setLocationTagsFor(locationService, LocationTags.CONSULT_NOTE_LOCATION, Arrays.asList(location));
        setLocationTagsFor(locationService, LocationTags.VITALS_LOCATION, Arrays.asList(location));
        setLocationTagsFor(locationService, LocationTags.CHECKIN_LOCATION, Arrays.asList(location));
        setLocationTagsFor(locationService, LocationTags.REGISTRATION_LOCATION, Arrays.asList(location));
        setLocationTagsFor(locationService, LocationTags.MEDICAL_RECORD_LOCATION, Arrays.asList(location));
        setLocationTagsFor(locationService, LocationTags.ED_TRIAGE_LOCATION, Arrays.asList(location));
        setLocationTagsFor(locationService, LocationTags.ADMISSION_LOCATION, null);
        setLocationTagsFor(locationService, LocationTags.TRANSFER_LOCAITON, null);
        setLocationTagsFor(locationService, LocationTags.ED_NOTE_LOCATION, null);
        setLocationTagsFor(locationService, LocationTags.SURGERY_NOTE_LOCATION, null);
        setLocationTagsFor(locationService, LocationTags.APPOINTMENT_LOCATION, null);
        setLocationTagsFor(locationService, LocationTags.DISPENSING_LOCATION, null);
        setLocationTagsFor(locationService, LocationTags.INPATIENTS_APP_LOCATION, null);
        setLocationTagsFor(locationService, LocationTags.ORDER_RADIOLOGY_STUDY_LOCATION, null);
    }

    private static void setupLocationTagsForHaitiMentalHealth(LocationService locationService) {

        List<LocationDescriptor> allZlFacilities = Arrays.asList(PihHaitiLocations.BELLADERE, PihHaitiLocations.BOUCAN_CARRE,
                PihHaitiLocations.CANGE, PihHaitiLocations.CERCA_LA_SOURCE, PihHaitiLocations.HINCHE, PihHaitiLocations.HSN_SAINT_MARC,
                PihHaitiLocations.LACOLLINE, PihHaitiLocations.PETITE_RIVIERE, PihHaitiLocations.SSPE_SAINT_MARC, PihHaitiLocations.THOMONDE,
                PihHaitiLocations.VERRETTES);

        setLocationTagsFor(locationService, LocationTags.LOGIN_LOCATION, allZlFacilities);
        setLocationTagsFor(locationService, LocationTags.CONSULT_NOTE_LOCATION, allZlFacilities);
        setLocationTagsFor(locationService, LocationTags.VITALS_LOCATION,  allZlFacilities);
        setLocationTagsFor(locationService, LocationTags.CHECKIN_LOCATION, allZlFacilities);
        setLocationTagsFor(locationService, LocationTags.REGISTRATION_LOCATION, allZlFacilities);
        setLocationTagsFor(locationService, LocationTags.MEDICAL_RECORD_LOCATION, allZlFacilities);
        setLocationTagsFor(locationService, LocationTags.ADMISSION_LOCATION, null);
        setLocationTagsFor(locationService, LocationTags.TRANSFER_LOCAITON, null);
        setLocationTagsFor(locationService, LocationTags.ED_NOTE_LOCATION, null);
        setLocationTagsFor(locationService, LocationTags.SURGERY_NOTE_LOCATION, null);
        setLocationTagsFor(locationService, LocationTags.APPOINTMENT_LOCATION, null);
        setLocationTagsFor(locationService, LocationTags.DISPENSING_LOCATION, null);
        setLocationTagsFor(locationService, LocationTags.INPATIENTS_APP_LOCATION, null);
        setLocationTagsFor(locationService, LocationTags.ORDER_RADIOLOGY_STUDY_LOCATION, null);
    }


    private static void setupLocationTagsForMirebalais(LocationService locationService) {

        setLocationTagsFor(locationService, LocationTags.VISIT_LOCATION, Arrays.asList(
            MirebalaisLocations.MIREBALAIS_CDI_PARENT
        ));

        setLocationTagsFor(locationService, LocationTags.MEDICAL_RECORD_LOCATION, Arrays.asList(
            MirebalaisLocations.MIREBALAIS_HOSPITAL,
            MirebalaisLocations.CDI_KLINIK_EKSTEN_JENERAL
        ));

        setLocationTagsFor(locationService, LocationTags.IDENTIFIER_ASSIGNMENT_LOCATION, Arrays.asList(
            MirebalaisLocations.MIREBALAIS_HOSPITAL,
            MirebalaisLocations.CDI_KLINIK_EKSTEN_JENERAL
        ));

        setLocationTagsFor(locationService, LocationTags.ARCHIVES_LOCATION, Arrays.asList(
            MirebalaisLocations.CENTRAL_ARCHIVES,
            MirebalaisLocations.CDI_KLINIK_EKSTEN_JENERAL_ACHIV_SANTRAL
        ));

        setLocationTagsFor(locationService, LocationTags.LOGIN_LOCATION, Arrays.asList(
            MirebalaisLocations.CLINIC_REGISTRATION,
            MirebalaisLocations.EMERGENCY_DEPARTMENT_RECEPTION,
            MirebalaisLocations.CENTRAL_ARCHIVES,
            MirebalaisLocations.OUTPATIENT_CLINIC,
            MirebalaisLocations.EMERGENCY,
            MirebalaisLocations.COMMUNITY_HEALTH,
            MirebalaisLocations.DENTAL,
            MirebalaisLocations.WOMENS_CLINIC,
            MirebalaisLocations.WOMENS_TRIAGE,
            MirebalaisLocations.LABOR_AND_DELIVERY,
            MirebalaisLocations.ANTEPARTUM_WARD,
            MirebalaisLocations.POSTPARTUM_WARD,
            MirebalaisLocations.POST_OP_GYN,
            MirebalaisLocations.SURGICAL_WARD,
            MirebalaisLocations.OPERATING_ROOMS,
            MirebalaisLocations.PRE_OP_PACU,
            MirebalaisLocations.MAIN_LABORATORY,
            MirebalaisLocations.WOMENS_OUTPATIENT_LABORATORY,
            MirebalaisLocations.RADIOLOGY,
            MirebalaisLocations.MENS_INTERNAL_MEDICINE,
            MirebalaisLocations.WOMENS_INTERNAL_MEDICINE,
            MirebalaisLocations.PEDIATRICS,
            MirebalaisLocations.ICU,
            MirebalaisLocations.NICU,
            MirebalaisLocations.ISOLATION,
            MirebalaisLocations.CHEMOTHERAPY,
            MirebalaisLocations.OUTPATIENT_CLINIC_PHARMACY,
            MirebalaisLocations.WOMENS_AND_CHILDRENS_PHARMACY,
            MirebalaisLocations.REHABILITATION,
            MirebalaisLocations.FAMILY_PLANNING,
            MirebalaisLocations.BLOOD_BANK,
            MirebalaisLocations.CDI_KLINIK_EKSTEN_JENERAL,
            MirebalaisLocations.CDI_KLINIK_EKSTEN_JENERAL_ACHIV_SANTRAL,
            MirebalaisLocations.CDI_KLINIK_EKSTEN_JENERAL_BIWO_RANDEVOU,
            MirebalaisLocations.CDI_KLINIK_EKSTEN_JENERAL_FAMASI,
            MirebalaisLocations.CDI_KLINIK_EKSTEN_JENERAL_LABORATWA,
            MirebalaisLocations.CDI_KLINIK_EKSTEN_JENERAL_RADYOGRAFI,
            MirebalaisLocations.CDI_KLINIK_EKSTEN_JENERAL_SAL_PWOSEDI
        ));


        setLocationTagsFor(locationService, LocationTags.ADMISSION_LOCATION, Arrays.asList(
            MirebalaisLocations.SURGICAL_WARD,
            MirebalaisLocations.ANTEPARTUM_WARD,
            MirebalaisLocations.LABOR_AND_DELIVERY,
            MirebalaisLocations.POSTPARTUM_WARD,
            MirebalaisLocations.PEDIATRICS,
            MirebalaisLocations.NICU,
            MirebalaisLocations.MENS_INTERNAL_MEDICINE,
            MirebalaisLocations.WOMENS_INTERNAL_MEDICINE,
            MirebalaisLocations.ISOLATION,
            MirebalaisLocations.REHABILITATION,
            MirebalaisLocations.POST_OP_GYN,
            MirebalaisLocations.ICU
        ));

        setLocationTagsFor(locationService, LocationTags.TRANSFER_LOCAITON, Arrays.asList(
            MirebalaisLocations.SURGICAL_WARD,
            MirebalaisLocations.ANTEPARTUM_WARD,
            MirebalaisLocations.LABOR_AND_DELIVERY,
            MirebalaisLocations.POSTPARTUM_WARD,
            MirebalaisLocations.EMERGENCY,
            MirebalaisLocations.COMMUNITY_HEALTH,
            MirebalaisLocations.OUTPATIENT_CLINIC,
            MirebalaisLocations.WOMENS_CLINIC,
            MirebalaisLocations.WOMENS_TRIAGE,
            MirebalaisLocations.PEDIATRICS,
            MirebalaisLocations.NICU,
            MirebalaisLocations.DENTAL,
            MirebalaisLocations.MENS_INTERNAL_MEDICINE,
            MirebalaisLocations.WOMENS_INTERNAL_MEDICINE,
            MirebalaisLocations.ISOLATION,
            MirebalaisLocations.REHABILITATION,
            MirebalaisLocations.POST_OP_GYN,
            MirebalaisLocations.ICU,
            MirebalaisLocations.CDI_KLINIK_EKSTEN_JENERAL
        ));

        setLocationTagsFor(locationService, LocationTags.CONSULT_NOTE_LOCATION, Arrays.asList(
            MirebalaisLocations.POSTPARTUM_WARD,
            MirebalaisLocations.WOMENS_CLINIC,
            MirebalaisLocations.PRE_OP_PACU,
            MirebalaisLocations.OUTPATIENT_CLINIC,
            MirebalaisLocations.ISOLATION,
            MirebalaisLocations.DENTAL,
            MirebalaisLocations.MENS_INTERNAL_MEDICINE,
            MirebalaisLocations.MENS_INTERNAL_MEDICINE_A,
            MirebalaisLocations.MENS_INTERNAL_MEDICINE_B,
            MirebalaisLocations.WOMENS_INTERNAL_MEDICINE,
            MirebalaisLocations.WOMENS_INTERNAL_MEDICINE_A,
            MirebalaisLocations.WOMENS_INTERNAL_MEDICINE_B,
            MirebalaisLocations.PEDIATRICS,
            MirebalaisLocations.PEDIATRICS_A,
            MirebalaisLocations.PEDIATRICS_B,
            MirebalaisLocations.SURGICAL_WARD,
            MirebalaisLocations.POST_OP_GYN,
            MirebalaisLocations.COMMUNITY_HEALTH,
            MirebalaisLocations.NICU,
            MirebalaisLocations.WOMENS_TRIAGE,
            MirebalaisLocations.ICU,
            MirebalaisLocations.LABOR_AND_DELIVERY,
            MirebalaisLocations.CHEMOTHERAPY,
            MirebalaisLocations.REHABILITATION,
            MirebalaisLocations.FAMILY_PLANNING,
            MirebalaisLocations.ANTEPARTUM_WARD,
            MirebalaisLocations.CDI_KLINIK_EKSTEN_JENERAL,
            MirebalaisLocations.CDI_KLINIK_EKSTEN_JENERAL_SAL_PWOSEDI
        ));

        setLocationTagsFor(locationService, LocationTags.ED_NOTE_LOCATION, Arrays.asList(
            MirebalaisLocations.EMERGENCY
        ));

        setLocationTagsFor(locationService, LocationTags.SURGERY_NOTE_LOCATION, Arrays.asList(
            MirebalaisLocations.SURGICAL_WARD,
            MirebalaisLocations.OPERATING_ROOMS,
            MirebalaisLocations.POSTPARTUM_WARD,
            MirebalaisLocations.POST_OP_GYN
        ));

        setLocationTagsFor(locationService, LocationTags.ONCOLOGY_CONSULT_LOCATION, Arrays.asList(
            MirebalaisLocations.OUTPATIENT_CLINIC
        ));

        setLocationTagsFor(locationService, LocationTags.CHEMOTHERAPY_LOCATION, Arrays.asList(
                MirebalaisLocations.CHEMOTHERAPY
        ));

        setLocationTagsFor(locationService, LocationTags.NCD_CONSULT_LOCATION, Arrays.asList(
                MirebalaisLocations.OUTPATIENT_CLINIC,
                MirebalaisLocations.WOMENS_CLINIC
        ));

        setLocationTagsFor(locationService, LocationTags.LAB_RESULTS_LOCATION, Arrays.asList(
                MirebalaisLocations.OUTPATIENT_CLINIC,
                MirebalaisLocations.CDI_KLINIK_EKSTEN_JENERAL
        ));

        // TODO: update the following tags to remove unnecessary/unsupported CDI locations (has this been done already?)

        setLocationTagsFor(locationService, LocationTags.ADMISSION_NOTE_LOCATION, Arrays.asList(
            MirebalaisLocations.ANTEPARTUM_WARD,
            MirebalaisLocations.POSTPARTUM_WARD,
            MirebalaisLocations.PRE_OP_PACU,
            MirebalaisLocations.MENS_INTERNAL_MEDICINE,
            MirebalaisLocations.MENS_INTERNAL_MEDICINE_A,
            MirebalaisLocations.MENS_INTERNAL_MEDICINE_B,
            MirebalaisLocations.WOMENS_INTERNAL_MEDICINE,
            MirebalaisLocations.WOMENS_INTERNAL_MEDICINE_A,
            MirebalaisLocations.WOMENS_INTERNAL_MEDICINE_B,
            MirebalaisLocations.PEDIATRICS,
            MirebalaisLocations.PEDIATRICS_A,
            MirebalaisLocations.PEDIATRICS_B,
            MirebalaisLocations.SURGICAL_WARD,
            MirebalaisLocations.POST_OP_GYN,
            MirebalaisLocations.NICU,
            MirebalaisLocations.OPERATING_ROOMS,
            MirebalaisLocations.WOMENS_TRIAGE,
            MirebalaisLocations.ICU,
            MirebalaisLocations.LABOR_AND_DELIVERY,
            MirebalaisLocations.ISOLATION,
            MirebalaisLocations.REHABILITATION,
            MirebalaisLocations.CDI_KLINIK_EKSTEN_JENERAL,
            MirebalaisLocations.CDI_KLINIK_EKSTEN_JENERAL_ACHIV_SANTRAL,
            MirebalaisLocations.CDI_KLINIK_EKSTEN_JENERAL_BIWO_RANDEVOU,
            MirebalaisLocations.CDI_KLINIK_EKSTEN_JENERAL_FAMASI,
            MirebalaisLocations.CDI_KLINIK_EKSTEN_JENERAL_LABORATWA,
            MirebalaisLocations.CDI_KLINIK_EKSTEN_JENERAL_RADYOGRAFI,
            MirebalaisLocations.CDI_KLINIK_EKSTEN_JENERAL_SAL_PWOSEDI
        ));


        setLocationTagsFor(locationService, LocationTags.DISPENSING_LOCATION, Arrays.asList(
            MirebalaisLocations.WOMENS_AND_CHILDRENS_PHARMACY,
            MirebalaisLocations.OUTPATIENT_CLINIC_PHARMACY,
            MirebalaisLocations.CDI_KLINIK_EKSTEN_JENERAL_FAMASI
        ));

        setLocationTagsFor(locationService, LocationTags.APPOINTMENT_LOCATION, Arrays.asList(
            MirebalaisLocations.CHEMOTHERAPY,
            MirebalaisLocations.OUTPATIENT_CLINIC,
            MirebalaisLocations.WOMENS_CLINIC,
            MirebalaisLocations.MAIN_LABORATORY,
            MirebalaisLocations.WOMENS_OUTPATIENT_LABORATORY,
            MirebalaisLocations.COMMUNITY_HEALTH,
            MirebalaisLocations.DENTAL,
            MirebalaisLocations.FAMILY_PLANNING,
            MirebalaisLocations.WOMENS_AND_CHILDRENS_PHARMACY,
            MirebalaisLocations.RADIOLOGY,
            MirebalaisLocations.OUTPATIENT_CLINIC_PHARMACY,
            MirebalaisLocations.CDI_KLINIK_EKSTEN_JENERAL,
            MirebalaisLocations.CDI_KLINIK_EKSTEN_JENERAL_ACHIV_SANTRAL,
            MirebalaisLocations.CDI_KLINIK_EKSTEN_JENERAL_BIWO_RANDEVOU,
            MirebalaisLocations.CDI_KLINIK_EKSTEN_JENERAL_FAMASI,
            MirebalaisLocations.CDI_KLINIK_EKSTEN_JENERAL_LABORATWA,
            MirebalaisLocations.CDI_KLINIK_EKSTEN_JENERAL_RADYOGRAFI,
            MirebalaisLocations.CDI_KLINIK_EKSTEN_JENERAL_SAL_PWOSEDI
        ));

        setLocationTagsFor(locationService, LocationTags.VITALS_LOCATION, Arrays.asList(
            MirebalaisLocations.ANTEPARTUM_WARD,
            MirebalaisLocations.POSTPARTUM_WARD,
            MirebalaisLocations.WOMENS_CLINIC,
            MirebalaisLocations.EMERGENCY,
            MirebalaisLocations.PRE_OP_PACU,
            MirebalaisLocations.OUTPATIENT_CLINIC,
            MirebalaisLocations.DENTAL,
            MirebalaisLocations.MENS_INTERNAL_MEDICINE,
            MirebalaisLocations.MENS_INTERNAL_MEDICINE_A,
            MirebalaisLocations.MENS_INTERNAL_MEDICINE_B,
            MirebalaisLocations.WOMENS_INTERNAL_MEDICINE,
            MirebalaisLocations.WOMENS_INTERNAL_MEDICINE_A,
            MirebalaisLocations.WOMENS_INTERNAL_MEDICINE_B,
            MirebalaisLocations.PEDIATRICS,
            MirebalaisLocations.PEDIATRICS_A,
            MirebalaisLocations.PEDIATRICS_B,
            MirebalaisLocations.SURGICAL_WARD,
            MirebalaisLocations.POST_OP_GYN,
            MirebalaisLocations.COMMUNITY_HEALTH,
            MirebalaisLocations.NICU,
            MirebalaisLocations.OPERATING_ROOMS,
            MirebalaisLocations.WOMENS_TRIAGE,
            MirebalaisLocations.ICU,
            MirebalaisLocations.LABOR_AND_DELIVERY,
            MirebalaisLocations.CHEMOTHERAPY,
            MirebalaisLocations.REHABILITATION,
            MirebalaisLocations.CDI_KLINIK_EKSTEN_JENERAL,
            MirebalaisLocations.CDI_KLINIK_EKSTEN_JENERAL_ACHIV_SANTRAL,
            MirebalaisLocations.CDI_KLINIK_EKSTEN_JENERAL_BIWO_RANDEVOU,
            MirebalaisLocations.CDI_KLINIK_EKSTEN_JENERAL_FAMASI,
            MirebalaisLocations.CDI_KLINIK_EKSTEN_JENERAL_LABORATWA,
            MirebalaisLocations.CDI_KLINIK_EKSTEN_JENERAL_RADYOGRAFI,
            MirebalaisLocations.CDI_KLINIK_EKSTEN_JENERAL_SAL_PWOSEDI
        ));

        setLocationTagsFor(locationService, LocationTags.INPATIENTS_APP_LOCATION, Arrays.asList(
            MirebalaisLocations.ANTEPARTUM_WARD,
            MirebalaisLocations.MAIN_LABORATORY,
            MirebalaisLocations.POSTPARTUM_WARD,
            MirebalaisLocations.WOMENS_CLINIC,
            MirebalaisLocations.RADIOLOGY,
            MirebalaisLocations.EMERGENCY,
            MirebalaisLocations.PRE_OP_PACU,
            MirebalaisLocations.OUTPATIENT_CLINIC,
            MirebalaisLocations.ISOLATION,
            MirebalaisLocations.DENTAL,
            MirebalaisLocations.MENS_INTERNAL_MEDICINE,
            MirebalaisLocations.MENS_INTERNAL_MEDICINE_A,
            MirebalaisLocations.MENS_INTERNAL_MEDICINE_B,
            MirebalaisLocations.WOMENS_INTERNAL_MEDICINE,
            MirebalaisLocations.WOMENS_INTERNAL_MEDICINE_A,
            MirebalaisLocations.WOMENS_INTERNAL_MEDICINE_B,
            MirebalaisLocations.PEDIATRICS,
            MirebalaisLocations.PEDIATRICS_A,
            MirebalaisLocations.PEDIATRICS_B,
            MirebalaisLocations.SURGICAL_WARD,
            MirebalaisLocations.POST_OP_GYN,
            MirebalaisLocations.COMMUNITY_HEALTH,
            MirebalaisLocations.NICU,
            MirebalaisLocations.CLINIC_REGISTRATION,
            MirebalaisLocations.OPERATING_ROOMS,
            MirebalaisLocations.WOMENS_TRIAGE,
            MirebalaisLocations.ICU,
            MirebalaisLocations.LABOR_AND_DELIVERY,
            MirebalaisLocations.CENTRAL_ARCHIVES,
            MirebalaisLocations.WOMENS_OUTPATIENT_LABORATORY,
            MirebalaisLocations.EMERGENCY_DEPARTMENT_RECEPTION,
            MirebalaisLocations.CHEMOTHERAPY,
            MirebalaisLocations.BLOOD_BANK,
            MirebalaisLocations.REHABILITATION,
            MirebalaisLocations.FAMILY_PLANNING,
            MirebalaisLocations.CDI_KLINIK_EKSTEN_JENERAL,
            MirebalaisLocations.CDI_KLINIK_EKSTEN_JENERAL_ACHIV_SANTRAL,
            MirebalaisLocations.CDI_KLINIK_EKSTEN_JENERAL_BIWO_RANDEVOU,
            MirebalaisLocations.CDI_KLINIK_EKSTEN_JENERAL_FAMASI,
            MirebalaisLocations.CDI_KLINIK_EKSTEN_JENERAL_LABORATWA,
            MirebalaisLocations.CDI_KLINIK_EKSTEN_JENERAL_RADYOGRAFI,
            MirebalaisLocations.CDI_KLINIK_EKSTEN_JENERAL_SAL_PWOSEDI
        ));

        setLocationTagsFor(locationService, LocationTags.CHECKIN_LOCATION, Arrays.asList(
            MirebalaisLocations.WOMENS_CLINIC,
            MirebalaisLocations.EMERGENCY,
            MirebalaisLocations.PRE_OP_PACU,
            MirebalaisLocations.OUTPATIENT_CLINIC,
            MirebalaisLocations.DENTAL,
            MirebalaisLocations.SURGICAL_WARD,
            MirebalaisLocations.COMMUNITY_HEALTH,
            MirebalaisLocations.WOMENS_TRIAGE,
            MirebalaisLocations.CHEMOTHERAPY,
            MirebalaisLocations.POSTPARTUM_WARD,
            MirebalaisLocations.FAMILY_PLANNING,
            MirebalaisLocations.CDI_KLINIK_EKSTEN_JENERAL,
            MirebalaisLocations.CDI_KLINIK_EKSTEN_JENERAL_ACHIV_SANTRAL,
            MirebalaisLocations.CDI_KLINIK_EKSTEN_JENERAL_BIWO_RANDEVOU,
            MirebalaisLocations.CDI_KLINIK_EKSTEN_JENERAL_FAMASI,
            MirebalaisLocations.CDI_KLINIK_EKSTEN_JENERAL_LABORATWA,
            MirebalaisLocations.CDI_KLINIK_EKSTEN_JENERAL_RADYOGRAFI,
            MirebalaisLocations.CDI_KLINIK_EKSTEN_JENERAL_SAL_PWOSEDI
        ));

        setLocationTagsFor(locationService, LocationTags.REGISTRATION_LOCATION, Arrays.asList(
            MirebalaisLocations.COMMUNITY_HEALTH,
            MirebalaisLocations.CLINIC_REGISTRATION,
            MirebalaisLocations.CENTRAL_ARCHIVES,
            MirebalaisLocations.EMERGENCY_DEPARTMENT_RECEPTION,
            MirebalaisLocations.POSTPARTUM_WARD,
            MirebalaisLocations.CDI_KLINIK_EKSTEN_JENERAL,
            MirebalaisLocations.CDI_KLINIK_EKSTEN_JENERAL_ACHIV_SANTRAL,
            MirebalaisLocations.CDI_KLINIK_EKSTEN_JENERAL_BIWO_RANDEVOU,
            MirebalaisLocations.CDI_KLINIK_EKSTEN_JENERAL_FAMASI,
            MirebalaisLocations.CDI_KLINIK_EKSTEN_JENERAL_LABORATWA,
            MirebalaisLocations.CDI_KLINIK_EKSTEN_JENERAL_RADYOGRAFI,
            MirebalaisLocations.CDI_KLINIK_EKSTEN_JENERAL_SAL_PWOSEDI
        ));

        setLocationTagsFor(locationService, LocationTags.ED_REGISTRATION_LOCATION, Arrays.asList(
            MirebalaisLocations.EMERGENCY,
            MirebalaisLocations.PRE_OP_PACU,
            MirebalaisLocations.OUTPATIENT_CLINIC,
            MirebalaisLocations.DENTAL,
            MirebalaisLocations.SURGICAL_WARD,
            MirebalaisLocations.POST_OP_GYN,
            MirebalaisLocations.COMMUNITY_HEALTH,
            MirebalaisLocations.CDI_KLINIK_EKSTEN_JENERAL,
            MirebalaisLocations.CDI_KLINIK_EKSTEN_JENERAL_ACHIV_SANTRAL,
            MirebalaisLocations.CDI_KLINIK_EKSTEN_JENERAL_BIWO_RANDEVOU,
            MirebalaisLocations.CDI_KLINIK_EKSTEN_JENERAL_FAMASI,
            MirebalaisLocations.CDI_KLINIK_EKSTEN_JENERAL_LABORATWA,
            MirebalaisLocations.CDI_KLINIK_EKSTEN_JENERAL_RADYOGRAFI,
            MirebalaisLocations.CDI_KLINIK_EKSTEN_JENERAL_SAL_PWOSEDI
        ));

        setLocationTagsFor(locationService, LocationTags.ORDER_RADIOLOGY_STUDY_LOCATION, Arrays.asList(
            MirebalaisLocations.ANTEPARTUM_WARD,
            MirebalaisLocations.POSTPARTUM_WARD,
            MirebalaisLocations.WOMENS_CLINIC,
            MirebalaisLocations.PRE_OP_PACU,
            MirebalaisLocations.OUTPATIENT_CLINIC,
            MirebalaisLocations.RADIOLOGY,
            MirebalaisLocations.EMERGENCY,
            MirebalaisLocations.ISOLATION,
            MirebalaisLocations.DENTAL,
            MirebalaisLocations.MENS_INTERNAL_MEDICINE,
            MirebalaisLocations.MENS_INTERNAL_MEDICINE_A,
            MirebalaisLocations.MENS_INTERNAL_MEDICINE_B,
            MirebalaisLocations.WOMENS_INTERNAL_MEDICINE,
            MirebalaisLocations.WOMENS_INTERNAL_MEDICINE_A,
            MirebalaisLocations.WOMENS_INTERNAL_MEDICINE_B,
            MirebalaisLocations.PEDIATRICS,
            MirebalaisLocations.PEDIATRICS_A,
            MirebalaisLocations.PEDIATRICS_B,
            MirebalaisLocations.SURGICAL_WARD,
            MirebalaisLocations.POST_OP_GYN,
            MirebalaisLocations.COMMUNITY_HEALTH,
            MirebalaisLocations.NICU,
            MirebalaisLocations.OPERATING_ROOMS,
            MirebalaisLocations.WOMENS_TRIAGE,
            MirebalaisLocations.ICU,
            MirebalaisLocations.LABOR_AND_DELIVERY,
            MirebalaisLocations.CHEMOTHERAPY,
            MirebalaisLocations.REHABILITATION,
            MirebalaisLocations.FAMILY_PLANNING,
            MirebalaisLocations.CDI_KLINIK_EKSTEN_JENERAL,
            MirebalaisLocations.CDI_KLINIK_EKSTEN_JENERAL_ACHIV_SANTRAL,
            MirebalaisLocations.CDI_KLINIK_EKSTEN_JENERAL_BIWO_RANDEVOU,
            MirebalaisLocations.CDI_KLINIK_EKSTEN_JENERAL_FAMASI,
            MirebalaisLocations.CDI_KLINIK_EKSTEN_JENERAL_LABORATWA,
            MirebalaisLocations.CDI_KLINIK_EKSTEN_JENERAL_RADYOGRAFI,
            MirebalaisLocations.CDI_KLINIK_EKSTEN_JENERAL_SAL_PWOSEDI
        ));

        setLocationTagsFor(locationService, LocationTags.ED_TRIAGE_LOCATION, Arrays.asList(
                MirebalaisLocations.EMERGENCY
        ));
    }

    private static void setLocationTagsFor(LocationService service, LocationTagDescriptor locationTag, Collection<LocationDescriptor> locationsThatGetTag) {

        LocationTag tag = service.getLocationTagByUuid(locationTag.uuid());

        for (Location candidate : service.getAllLocations()) {
            boolean expected = false;
            if (locationsThatGetTag != null) {
                for (LocationDescriptor d : locationsThatGetTag) {
                    if (d != null && d.uuid().equals(candidate.getUuid())) {
                        expected = true;
                    }
                }
            }
            boolean actual = candidate.hasTag(tag.getName());
            if (actual && !expected) {
                candidate.removeTag(tag);
                service.saveLocation(candidate);
            } else if (!actual && expected) {
                candidate.addTag(tag);
                service.saveLocation(candidate);
            }
        }
    }

}
