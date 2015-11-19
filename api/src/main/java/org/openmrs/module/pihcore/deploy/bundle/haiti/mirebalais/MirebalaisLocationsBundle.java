package org.openmrs.module.pihcore.deploy.bundle.haiti.mirebalais;

import org.openmrs.Location;
import org.openmrs.LocationAttribute;
import org.openmrs.module.metadatadeploy.bundle.LocationsMetadataBundle;
import org.openmrs.module.metadatadeploy.bundle.Requires;
import org.openmrs.module.pihcore.deploy.bundle.core.LocationAttributeTypeBundle;
import org.openmrs.module.pihcore.deploy.bundle.core.LocationTagBundle;
import org.openmrs.module.pihcore.metadata.haiti.mirebalais.MirebalaisLocations;
import org.springframework.stereotype.Component;

;

@Component
@Requires({ LocationTagBundle.class, LocationAttributeTypeBundle.class} )
public class MirebalaisLocationsBundle extends LocationsMetadataBundle {

    @Override
    public void install() throws Exception {

        // Top level Locations at Mirebalais
        installLocation(MirebalaisLocations.MIREBALAIS_CDI_PARENT);
        installLocation(MirebalaisLocations.MIREBALAIS_HOSPITAL);

        // Locations within Mirebalais Hospital
        installLocation(MirebalaisLocations.ANTEPARTUM_WARD);
        installLocation(MirebalaisLocations.BLOOD_BANK);
        installLocation(MirebalaisLocations.CENTRAL_ARCHIVES);
        installLocation(MirebalaisLocations.CHEMOTHERAPY);
        installLocation(MirebalaisLocations.CLINIC_REGISTRATION);
        installLocation(MirebalaisLocations.COMMUNITY_HEALTH);
        installLocation(MirebalaisLocations.DENTAL);
        installLocation(MirebalaisLocations.EMERGENCY);
        installLocation(MirebalaisLocations.EMERGENCY_DEPARTMENT_RECEPTION);
        installLocation(MirebalaisLocations.FAMILY_PLANNING);
        installLocation(MirebalaisLocations.ICU);
        installLocation(MirebalaisLocations.ISOLATION);
        installLocation(MirebalaisLocations.LABOR_AND_DELIVERY);
        installLocation(MirebalaisLocations.MAIN_LABORATORY);
        installLocation(MirebalaisLocations.MENS_INTERNAL_MEDICINE);
        installLocation(MirebalaisLocations.MENS_INTERNAL_MEDICINE_A);
        installLocation(MirebalaisLocations.MENS_INTERNAL_MEDICINE_B);
        installLocation(MirebalaisLocations.NICU);
        installLocation(MirebalaisLocations.OPERATING_ROOMS);
        installLocation(MirebalaisLocations.OUTPATIENT_CLINIC);
        installLocation(MirebalaisLocations.OUTPATIENT_CLINIC_PHARMACY);
        installLocation(MirebalaisLocations.PEDIATRICS);
        installLocation(MirebalaisLocations.PEDIATRICS_A);
        installLocation(MirebalaisLocations.PEDIATRICS_B);
        installLocation(MirebalaisLocations.PRE_OP_PACU);
        installLocation(MirebalaisLocations.POST_OP_GYN);
        installLocation(MirebalaisLocations.POSTPARTUM_WARD);
        installLocation(MirebalaisLocations.RADIOLOGY);
        installLocation(MirebalaisLocations.REHABILITATION);
        installLocation(MirebalaisLocations.SURGICAL_WARD);
        installLocation(MirebalaisLocations.WOMENS_CLINIC);
        installLocation(MirebalaisLocations.WOMENS_AND_CHILDRENS_PHARMACY); // out of order because of above location
        installLocation(MirebalaisLocations.WOMENS_INTERNAL_MEDICINE);
        installLocation(MirebalaisLocations.WOMENS_INTERNAL_MEDICINE_A);
        installLocation(MirebalaisLocations.WOMENS_INTERNAL_MEDICINE_B);
        installLocation(MirebalaisLocations.WOMENS_OUTPATIENT_LABORATORY);
        installLocation(MirebalaisLocations.WOMENS_TRIAGE);

        // Locations at CDI
        installLocation(MirebalaisLocations.CDI_KLINIK_EKSTEN_JENERAL);
        installLocation(MirebalaisLocations.CDI_KLINIK_EKSTEN_JENERAL_ACHIV_SANTRAL);
        installLocation(MirebalaisLocations.CDI_KLINIK_EKSTEN_JENERAL_BIWO_RANDEVOU);
        installLocation(MirebalaisLocations.CDI_KLINIK_EKSTEN_JENERAL_FAMASI);
        installLocation(MirebalaisLocations.CDI_KLINIK_EKSTEN_JENERAL_LABORATWA);
        installLocation(MirebalaisLocations.CDI_KLINIK_EKSTEN_JENERAL_RADYOGRAFI);
        installLocation(MirebalaisLocations.CDI_KLINIK_EKSTEN_JENERAL_SAL_PWOSEDI);


        log.info("Retiring old Mirebalais Locations");

        uninstall(possible(Location.class, MirebalaisLocations.RETIRED_CDI_KLINIK_EKSTEN_JENERAL_OLD.uuid()), "no longer used");  // a CDI location that was only temporarily added
        uninstall(possible(Location.class, MirebalaisLocations.RETIRED_ED_OBSERVATION.uuid()), "no longer used");
        uninstall(possible(Location.class, MirebalaisLocations.RETIRED_ED_BOARDING.uuid()), "no longer used");

        log.info("Voiding old location attributes");

        uninstall(possible(LocationAttribute.class, "d24c621b-fa4f-4d99-a15c-933186d9e8cd"), "belongs to retired location");
        uninstall(possible(LocationAttribute.class, "f170427f-8cc7-421e-bd52-350c3580ea90"), "belongs to retired location");
    }

    //***** BUNDLE INSTALLATION METHODS FOR DESCRIPTORS

}
