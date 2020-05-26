package org.openmrs.module.pihcore.deploy.bundle.haiti.mirebalais;

import org.openmrs.Location;
import org.openmrs.LocationAttribute;
import org.openmrs.module.metadatadeploy.bundle.AbstractMetadataBundle;
import org.openmrs.module.metadatadeploy.bundle.Requires;
import org.openmrs.module.pihcore.deploy.bundle.core.LocationAttributeTypeBundle;
import org.openmrs.module.pihcore.deploy.bundle.core.LocationTagBundle;
import org.openmrs.module.pihcore.metadata.haiti.mirebalais.MirebalaisLocations;
import org.springframework.stereotype.Component;

;

@Component
@Requires({ LocationTagBundle.class, LocationAttributeTypeBundle.class} )
public class MirebalaisLocationsBundle extends AbstractMetadataBundle {

    @Override
    public void install() throws Exception {

        // Top level Locations at Mirebalais
        install(MirebalaisLocations.MIREBALAIS_CDI_PARENT);
        install(MirebalaisLocations.MIREBALAIS_HOSPITAL);

        // Locations within Mirebalais Hospital
        install(MirebalaisLocations.ANTEPARTUM_WARD);
        install(MirebalaisLocations.BLOOD_BANK);
        install(MirebalaisLocations.CENTRAL_ARCHIVES);
        install(MirebalaisLocations.CHEMOTHERAPY);
        install(MirebalaisLocations.CLINIC_REGISTRATION);
        install(MirebalaisLocations.COMMUNITY_HEALTH);
        install(MirebalaisLocations.DENTAL);
        install(MirebalaisLocations.EMERGENCY);
        install(MirebalaisLocations.EMERGENCY_DEPARTMENT_RECEPTION);
        install(MirebalaisLocations.FAMILY_PLANNING);
        install(MirebalaisLocations.ICU);
        install(MirebalaisLocations.ISOLATION);
        install(MirebalaisLocations.COVID19_ISOLATION);
        install(MirebalaisLocations.COVID19_UMI);
        install(MirebalaisLocations.LABOR_AND_DELIVERY);
        install(MirebalaisLocations.MAIN_LABORATORY);
        install(MirebalaisLocations.MENS_INTERNAL_MEDICINE);
        install(MirebalaisLocations.MENS_INTERNAL_MEDICINE_A);
        install(MirebalaisLocations.MENS_INTERNAL_MEDICINE_B);
        install(MirebalaisLocations.NICU);
        install(MirebalaisLocations.OPERATING_ROOMS);
        install(MirebalaisLocations.OUTPATIENT_CLINIC);
        install(MirebalaisLocations.OUTPATIENT_CLINIC_PHARMACY);
        install(MirebalaisLocations.PEDIATRICS);
        install(MirebalaisLocations.PEDIATRICS_A);
        install(MirebalaisLocations.PEDIATRICS_B);
        install(MirebalaisLocations.PRE_OP_PACU);
        install(MirebalaisLocations.POST_OP_GYN);
        install(MirebalaisLocations.POSTPARTUM_WARD);
        install(MirebalaisLocations.RADIOLOGY);
        install(MirebalaisLocations.REHABILITATION);
        install(MirebalaisLocations.SURGICAL_WARD);
        install(MirebalaisLocations.WOMENS_CLINIC);
        install(MirebalaisLocations.WOMENS_AND_CHILDRENS_PHARMACY); // out of order because of above location
        install(MirebalaisLocations.WOMENS_INTERNAL_MEDICINE);
        install(MirebalaisLocations.WOMENS_INTERNAL_MEDICINE_A);
        install(MirebalaisLocations.WOMENS_INTERNAL_MEDICINE_B);
        install(MirebalaisLocations.WOMENS_OUTPATIENT_LABORATORY);
        install(MirebalaisLocations.WOMENS_TRIAGE);
        install(MirebalaisLocations.NCD_CLINIC);

        // Locations at CDI
        install(MirebalaisLocations.CDI_KLINIK_EKSTEN_JENERAL);
        install(MirebalaisLocations.CDI_KLINIK_EKSTEN_JENERAL_ACHIV_SANTRAL);
        install(MirebalaisLocations.CDI_KLINIK_EKSTEN_JENERAL_BIWO_RANDEVOU);
        install(MirebalaisLocations.CDI_KLINIK_EKSTEN_JENERAL_FAMASI);
        install(MirebalaisLocations.CDI_KLINIK_EKSTEN_JENERAL_LABORATWA);
        install(MirebalaisLocations.CDI_KLINIK_EKSTEN_JENERAL_RADYOGRAFI);
        install(MirebalaisLocations.CDI_KLINIK_EKSTEN_JENERAL_SAL_PWOSEDI);


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
