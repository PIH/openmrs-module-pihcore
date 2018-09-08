package org.openmrs.module.pihcore.deploy.bundle.core;

import org.openmrs.Privilege;
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

        log.info("Installing Privileges");

        install(Privileges.APP_APPOINTMENTSCHEDULINGUI_APPOINTMENT_TYPES);
        install(Privileges.APP_APPOINTMENTSCHEDULINGUI_HOME);
        install(Privileges.APP_APPOINTMENTSCHEDULINGUI_PROVIDER_SCHEDULES);
        install(Privileges.APP_APPOINTMENTSCHEDULINGUI_VIEW_APPOINTMENTS);
        install(Privileges.APP_COREAPPS_ACTIVE_VISITS);
        install(Privileges.APP_COREAPPS_AWAITING_ADMISSION);
        install(Privileges.APP_COREAPPS_DATA_MANAGEMENT);
        install(Privileges.APP_COREAPPS_FIND_PATIENT);
        install(Privileges.APP_COREAPPS_PATIENT_DASHBOARD);
        install(Privileges.APP_COREAPPS_SUMMARY_DASHBOARD);
        install(Privileges.APP_COREAPPS_PATIENT_VISITS);
        install(Privileges.APP_DISPENSING_APP_DISPENSE);
        install(Privileges.APP_EMR_ARCHIVES_ROOM);
        install(Privileges.APP_EMR_CHECK_IN);
        install(Privileges.APP_EMR_INPATIENTS);
        install(Privileges.APP_EMR_OUTPATIENT_VITALS);
        install(Privileges.APP_EMR_SYSTEM_ADMINISTRATION);
        install(Privileges.APP_LEGACY_ADMIN);
        install(Privileges.APP_REGISTRATION_REGISTER_PATIENT);
        install(Privileges.APP_REPORTINGUI_ADHOC_ANALYSIS);
        install(Privileges.APP_REPORTINGUI_REPORTS);
        install(Privileges.APP_WAITING_FOR_CONSULT);
        install(Privileges.APP_TODAYS_VISITS);
        install(Privileges.APP_ED_TRIAGE);
        install(Privileges.APP_ED_TRIAGE_QUEUE);
        install(Privileges.APP_ZL_MPI);
        install(Privileges.APP_ZL_REPORTS_DATA_EXPORTS);
        install(Privileges.APP_LAB_TRACKING_MONITOR_ORDERS);
        install(Privileges.APP_LABS);
        install(Privileges.APP_ATTACHMENTS_PAGE);
        install(Privileges.APP_CHW);
        install(Privileges.APP_ORDER_ENTRY_ORDER_DRUGS);
        install(Privileges.APP_COHORT_BUILDER);
        install(Privileges.TASK_ALLERGIES_MODIFY);
        install(Privileges.TASK_APPOINTMENTSCHEDULINGUI_BOOK_APPOINTMENTS);
        install(Privileges.TASK_APPOINTMENTSCHEDULINGUI_OVERBOOK_APPOINTMENTS);
        install(Privileges.TASK_APPOINTMENTSCHEDULINGUI_REQUEST_APPOINTMENTS);
        install(Privileges.TASK_APPOINTMENTSCHEDULINGUI_VIEW_CONFIDENTIAL);
        install(Privileges.TASK_ALLERGIES_MODIFY);
        install(Privileges.TASK_ARCHIVES_REQUEST_PAPER_RECORDS);
        install(Privileges.TASK_COREAPPS_CREATE_RETRO_VISIT);
        install(Privileges.TASK_COREAPPS_EDIT_RELATIONSHIPS);
        install(Privileges.TASK_COREAPPS_CREATE_VISIT);
        install(Privileges.TASK_COREAPPS_END_VISIT);
        install(Privileges.TASK_COREAPPS_DELETE_VISIT);
        install(Privileges.TASK_COREAPPS_MERGE_VISITS);
        install(Privileges.TASK_DISPENSING_DISPENSE);
        install(Privileges.TASK_DISPENSING_EDIT);
        install(Privileges.TASK_EMR_CHECK_IN);
        install(Privileges.TASK_EMR_DEATH_CERTIFICATE_FORM);
        install(Privileges.TASK_EMR_ENTER_ADMISSION_NOTE);
        install(Privileges.TASK_EMR_ENTER_CONSULT_NOTE);
        install(Privileges.TASK_EMR_ENTER_ED_NOTE);
        install(Privileges.TASK_EMR_ENTER_SURGICAL_NOTE);
        install(Privileges.TASK_EMR_ENTER_VITALS_NOTE);
        install(Privileges.TASK_EMR_ENTER_ONCOLOGY_CONSULT_NOTE);
        install(Privileges.TASK_EMR_ENTER_NCD_CONSULT_NOTE);
        install(Privileges.TASK_EMR_ENTER_MENTAL_HEALTH_NOTE);
        install(Privileges.TASK_EMR_ENTER_HIV_CONSULT_NOTE);
        install(Privileges.TASK_EMR_ENTER_VCT);
        install(Privileges.TASK_EMR_ENTER_SOCIO);
        install(Privileges.TASK_EMR_ENTER_MCH);
        install(Privileges.TASK_EMR_ENTER_LAB_RESULTS);
        install(Privileges.TASK_EMR_PATIENT_ENCOUNTER_DELETE);
        install(Privileges.TASK_EMR_PATIENT_ENCOUNTER_EDIT);
        install(Privileges.TASK_EMR_PRINT_LABELS);
        install(Privileges.TASK_EMR_PRINT_WRISTBAND);
        install(Privileges.TASK_EMR_RETRO_CLINICAL_NOTE);
        install(Privileges.TASK_EMR_RETRO_CLINICAL_NOTE_THIS_PROVIDER_ONLY);
        install(Privileges.TASK_RADIOLOGYAPP_ORDER_CT);
        install(Privileges.TASK_RADIOLOGYAPP_ORDER_US);
        install(Privileges.TASK_RADIOLOGYAPP_ORDER_XRAY);
        install(Privileges.TASK_RADIOLOGYAPP_RETRO_ORDER);
        install(Privileges.TASK_RADIOLOGYAPP_TAB);
        install(Privileges.TASK_ED_TRIAGE_ENTER_NOTE);
        install(Privileges.TASK_LAB_TRACKING_PLACE_ORDERS);
        install(Privileges.TASK_LAB_TRACKING_UPDATE);
        install(Privileges.TASK_ENROLL_IN_PROGRAM);
        install(Privileges.TASK_EDIT_PATIENT_PROGRAM);
        install(Privileges.TASK_DELETE_PATIENT_PROGRAM);
        install(Privileges.TASK_MANAGE_CONDITIONS_LIST);

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

        log.info("Installing roles");

        if (config.getCountry().equals(ConfigDescriptor.Country.HAITI)) {
            install(Roles.SYSTEM_ADMINISTRATOR);
            install(Roles.SCHEDULE_MANAGER);
            install(Roles.PHARMACIST);
            install(Roles.PHARMACY_AIDE);
            install(Roles.GENERAL_ADMINISTRATION);
            install(Roles.CLINICAL_MANAGER);
            install(Roles.MEQ);
            install(Roles.ARCHIVIST_CLERK);
            install(Roles.ARCHIVIST_MANAGER);
            install(Roles.ARCHIVIST_REGISTRATION);
            install(Roles.ARCHIVIST_CHECK_IN);
            install(Roles.AUXILIARY_NURSE);
            install(Roles.NURSE);
            install(Roles.ADVANCED_PRACTICE_NURSE);
            install(Roles.RADIOLOGY_TECHNICIAN);
            install(Roles.PHARMACIST);
            install(Roles.PHARMACY_AIDE);
            install(Roles.PHARMACY_MANAGER);
            install(Roles.MEDICAL_STUDENT);
            install(Roles.RESIDENT);
            install(Roles.PHYSICIAN);
            install(Roles.NCD_PHYSICIAN);
            install(Roles.SURGEON);
            install(Roles.ANAESTHETIST);
            install(Roles.PSYCHOLOGIST);
            install(Roles.PHYSICAL_THERAPIST);
            install(Roles.SOCIAL_WORKER);
            install(Roles.PRINT_LABELS);
            install(Roles.PROGRAM_MANAGER);
            install(Roles.PATHOLOGY_TECHNICIAN);
            install(Roles.LABS);
        }
        else if (config.getCountry().equals(ConfigDescriptor.Country.LIBERIA)
                || config.getCountry().equals(ConfigDescriptor.Country.SIERRA_LEONE)) {
            install(Roles.SYSTEM_ADMINISTRATOR);
            install(Roles.ARCHIVIST_CLERK);
            install(Roles.PHYSICIAN);
            install(Roles.MEQ);
        }
        else if (config.getCountry().equals(ConfigDescriptor.Country.MEXICO)) {
            install(Roles.SYSTEM_ADMINISTRATOR);
            install(Roles.ARCHIVIST_CLERK);
            install(Roles.PHYSICIAN);
            install(Roles.MEQ);
            install(Roles.PROGRAM_MANAGER);
        }

        // old privileges still in use at Mirebalais
        if (config.getSite().equals(ConfigDescriptor.Site.MIREBALAIS)) {
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

    }

}
