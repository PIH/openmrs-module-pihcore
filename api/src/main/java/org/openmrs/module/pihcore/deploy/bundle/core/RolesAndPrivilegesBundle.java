package org.openmrs.module.pihcore.deploy.bundle.core;

import org.openmrs.Privilege;
import org.openmrs.Role;
import org.openmrs.module.metadatadeploy.bundle.AbstractMetadataBundle;
import org.openmrs.module.pihcore.config.Config;
import org.openmrs.module.pihcore.config.ConfigDescriptor;
import org.openmrs.module.pihcore.metadata.core.Privileges;
import org.openmrs.module.pihcore.metadata.core.Roles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RolesAndPrivilegesBundle extends AbstractMetadataBundle {

    @Autowired
    private Config config;

    @Override
    public void install() throws Exception {
        
        log.info("Retiring old privileges");

        uninstall(possible(Privilege.class, Privileges.RETIRED_APP_APPOINTMENTSCHEDULING_OLDUI.uuid()), "no longer used");
        uninstall(possible(Privilege.class, Privileges.RETIRED_APP_APPOINTMENTSCHEDULINGUI_SCHEDULE_APPOINTMENT.uuid()), "no longer used");
        uninstall(possible(Privilege.class, Privileges.RETIRED_APP_EMR_ACTIVE_VISITS.uuid()), "no longer used");
        uninstall(possible(Privilege.class, Privileges.RETIRED_APP_ZL_REPORTS.uuid()), "no longer used");
        uninstall(possible(Privilege.class, Privileges.RETIRED_ENTER_CLINICAL_FORMS.uuid()), "no longer used");
        uninstall(possible(Privilege.class, Privileges.RETIRED_ORDER_CT.uuid()), "no longer used");
        uninstall(possible(Privilege.class, Privileges.RETIRED_ORDER_US.uuid()), "no longer used");
        uninstall(possible(Privilege.class, Privileges.RETIRED_ORDER_XRAY.uuid()), "no longer used");
        uninstall(possible(Privilege.class, Privileges.RETIRED_ORDER_XRAY_ANOTHER.uuid()), "no longer used");
        uninstall(possible(Privilege.class, Privileges.RETIRED_TASK_EMR_PRINT_ID_CARD_LABEL.uuid()), "no longer used");
        uninstall(possible(Privilege.class, Privileges.RETIRED_TASK_EMR_PRINT_PAPER_RECORD_LABELS.uuid()), "no longer used");
        uninstall(possible(Privilege.class, Privileges.RETIRED_APP_EMR_SYSTEM_ADMINISTRATION.uuid()), "replaced by new coreapps privilege");

        log.info("Installing roles");

        if (config.getCountry().equals(ConfigDescriptor.Country.HAITI)) {
            install(Roles.SCHEDULE_MANAGER);
            install(Roles.PHARMACIST);
            install(Roles.PHARMACY_AIDE);
            install(Roles.GENERAL_ADMINISTRATION);
            install(Roles.CLINICAL_MANAGER);
            install(Roles.ARCHIVIST_REGISTRATION);
            install(Roles.ARCHIVIST_CHECK_IN);
            install(Roles.AUXILIARY_NURSE);
            install(Roles.NURSE);
            install(Roles.ADVANCED_PRACTICE_NURSE);
            install(Roles.NURSE_MIDWIFE);
            install(Roles.RADIOLOGY_TECHNICIAN);
            install(Roles.PHARMACIST);
            install(Roles.PHARMACY_AIDE);
            install(Roles.PHARMACY_MANAGER);
            install(Roles.MEDICAL_STUDENT);
            install(Roles.RESIDENT);
            install(Roles.NCD_PHYSICIAN);
            install(Roles.SURGEON);
            install(Roles.ANAESTHETIST);
            install(Roles.PSYCHOLOGIST);
            install(Roles.PHYSICAL_THERAPIST);
            install(Roles.SOCIAL_WORKER);
            install(Roles.PRINT_LABELS);
            install(Roles.LAB_MANAGER);
            install(Roles.LAB_TECHNICIAN);
            install(Roles.VCT_COUNSELOR);
            install(Roles.PATIENT_DOCUMENTS);
        }
        else if (config.getCountry().equals(ConfigDescriptor.Country.LIBERIA)) {
            install(Roles.COMMUNITY_HEALTH_WORKER);
            install(Roles.SOCIAL_WORKER);
            install(Roles.TRIAGE_STAFF);
            install(Roles.NCD_PHYSICIAN);
            install(Roles.PSYCHOLOGIST);
            install(Roles.ARCHIVIST_REGISTRATION);
            install(Roles.LAB_MANAGER);
            install(Roles.LAB_TECHNICIAN);
            install(Roles.NURSE);
            install(Roles.OBSERVATION_STAFF);
            install(Roles.PHARMACIST);
            install(Roles.MEDICAL_STUDENT);
            install(Roles.RESIDENT);
            install(Roles.SURGEON);
        }
        else if (config.getCountry().equals(ConfigDescriptor.Country.SIERRA_LEONE)) {
            install(Roles.PHARMACY_MANAGER);
            install(Roles.LAB_MANAGER);
            install(Roles.LAB_TECHNICIAN);
            install(Roles.TRIAGE_STAFF);
            install(Roles.OBSERVATION_STAFF);
            install(Roles.SUPPORT_STAFF);
        }

        // old privileges still in use at Mirebalais
        if (config.getSite().equalsIgnoreCase("MIREBALAIS")) {
            install(Roles.LEGACY_CLINICAL);
            install(Roles.LEGACY_DATA_ARCHIVES);
            install(Roles.LEGACY_RADIOLOGY);
            install(Roles.LEGACY_REPORTS);
            install(Roles.LEGACY_ARCHIVIST_AIDE);
            install(Roles.LEGACY_SCHEDULE_ADMINISTRATOR);
            install(Roles.LEGACY_SCHEDULER);
            install(Roles.LEGACY_SCHEDULE_VIEWER);
            install(Roles.LEGACY_PATIENT_MEDICAL_INFORMATION);
            install(Roles.LEGACY_CHECK_IN);
        }

        // retired roles
        uninstall(possible(Role.class, Roles.LABS_RETIRED.uuid()), "replaced by Lab Manager and Lab Tech");
    }

}
