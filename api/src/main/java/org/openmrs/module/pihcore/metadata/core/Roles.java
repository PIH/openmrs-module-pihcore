/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
package org.openmrs.module.pihcore.metadata.core;

import org.openmrs.module.metadatadeploy.descriptor.PrivilegeDescriptor;
import org.openmrs.module.metadatadeploy.descriptor.RoleDescriptor;

import java.util.Arrays;
import java.util.List;

/**
 * Constants for all defined roles
 */
public class Roles {

    public static RoleDescriptor SYSTEM_ADMINISTRATOR = new RoleDescriptor() {
        public String uuid() { return "ba7772b3-89c1-4179-9b4d-e0eb96164da6"; }
        public String role() { return "Application Role: sysAdmin"; }
        public String description() { return "Application Role: sysAdmin"; }
        public List<PrivilegeDescriptor> privileges() {
            return Arrays.asList(
                Privileges.APP_APPOINTMENTSCHEDULINGUI_APPOINTMENT_TYPES,
                Privileges.APP_APPOINTMENTSCHEDULINGUI_HOME,
                Privileges.APP_APPOINTMENTSCHEDULINGUI_PROVIDER_SCHEDULES,
                Privileges.APP_APPOINTMENTSCHEDULINGUI_VIEW_APPOINTMENTS,
                Privileges.APP_COREAPPS_ACTIVE_VISITS,
                Privileges.APP_COREAPPS_AWAITING_ADMISSION,
                Privileges.APP_COREAPPS_DATA_MANAGEMENT,
                Privileges.APP_COREAPPS_FIND_PATIENT,
                Privileges.APP_COREAPPS_PATIENT_DASHBOARD,
                Privileges.APP_COREAPPS_PATIENT_VISITS,
                Privileges.APP_DISPENSING_APP_DISPENSE,
                Privileges.APP_EMR_ARCHIVES_ROOM,
                Privileges.APP_EMR_CHECK_IN,
                Privileges.APP_EMR_INPATIENTS,
                Privileges.APP_EMR_OUTPATIENT_VITALS,
                Privileges.APP_EMR_SYSTEM_ADMINISTRATION,
                Privileges.APP_LEGACY_ADMIN,
                Privileges.APP_REGISTRATION_REGISTER_PATIENT,
                Privileges.APP_REPORTINGUI_ADHOC_ANALYSIS,
                Privileges.APP_REPORTINGUI_REPORTS,
                Privileges.APP_ZL_MPI,
                Privileges.APP_ZL_REPORTS_DATA_EXPORTS,
                Privileges.APP_ED_TRIAGE,
                Privileges.APP_ED_TRIAGE_QUEUE,
                Privileges.TASK_ALLERGIES_MODIFY,
                Privileges.TASK_APPOINTMENTSCHEDULINGUI_BOOK_APPOINTMENTS,
                Privileges.TASK_APPOINTMENTSCHEDULINGUI_OVERBOOK_APPOINTMENTS,
                Privileges.TASK_APPOINTMENTSCHEDULINGUI_REQUEST_APPOINTMENTS,
                Privileges.TASK_APPOINTMENTSCHEDULINGUI_VIEW_CONFIDENTIAL,
                Privileges.TASK_ARCHIVES_REQUEST_PAPER_RECORDS,
                Privileges.TASK_COREAPPS_CREATE_RETRO_VISIT,
                Privileges.TASK_COREAPPS_CREATE_VISIT,
                Privileges.TASK_COREAPPS_END_VISIT,
                Privileges.TASK_COREAPPS_DELETE_VISIT,
                Privileges.TASK_COREAPPS_MERGE_VISITS,
                Privileges.TASK_DISPENSING_DISPENSE,
                Privileges.TASK_DISPENSING_EDIT,
                Privileges.TASK_EMR_CHECK_IN,
                Privileges.TASK_EMR_DEATH_CERTIFICATE_FORM,
                Privileges.TASK_EMR_ENTER_ADMISSION_NOTE,
                Privileges.TASK_EMR_ENTER_CONSULT_NOTE,
                Privileges.TASK_EMR_ENTER_ONCOLOGY_CONSULT_NOTE,
                Privileges.TASK_EMR_ENTER_NCD_CONSULT_NOTE,
                Privileges.TASK_EMR_ENTER_MENTAL_HEALTH_NOTE,
                Privileges.TASK_EMR_ENTER_LAB_RESULTS,
                Privileges.TASK_EMR_ENTER_ED_NOTE,
                Privileges.TASK_EMR_ENTER_SURGICAL_NOTE,
                Privileges.TASK_EMR_ENTER_VITALS_NOTE,
                Privileges.TASK_EMR_PATIENT_ENCOUNTER_DELETE,
                Privileges.TASK_EMR_PATIENT_ENCOUNTER_EDIT,
                Privileges.TASK_EMR_PRINT_LABELS,
                Privileges.TASK_EMR_PRINT_WRISTBAND,
                Privileges.TASK_EMR_RETRO_CLINICAL_NOTE,
                Privileges.TASK_EMR_RETRO_CLINICAL_NOTE_THIS_PROVIDER_ONLY,
                Privileges.TASK_RADIOLOGYAPP_ORDER_CT,
                Privileges.TASK_RADIOLOGYAPP_ORDER_US,
                Privileges.TASK_RADIOLOGYAPP_ORDER_XRAY,
                Privileges.TASK_RADIOLOGYAPP_RETRO_ORDER,
                Privileges.TASK_RADIOLOGYAPP_TAB,
                Privileges.TASK_ED_TRIAGE_ENTER_NOTE
            );}
    };


    public static RoleDescriptor GENERAL_ADMINISTRATION = new RoleDescriptor() {
        public String uuid() { return "09c552a0-e789-11e4-b571-0800200c9a66"; }
        public String role() { return "Application Role: generalAdministration"; }
        public String description() { return "Application Role: generalAdministration"; }
        public List<PrivilegeDescriptor> privileges() {
            return Arrays.asList(
                    Privileges.APP_COREAPPS_FIND_PATIENT,
                    Privileges.APP_COREAPPS_ACTIVE_VISITS,
                    Privileges.APP_EMR_INPATIENTS,
                    Privileges.APP_COREAPPS_PATIENT_VISITS,
                    Privileges.APP_COREAPPS_PATIENT_DASHBOARD,
                    Privileges.TASK_RADIOLOGYAPP_TAB
            );}
    };

    public static RoleDescriptor CLINICAL_MANAGER = new RoleDescriptor() {
        public String uuid() { return "f0929650-e786-11e4-b571-0800200c9a66"; }
        public String role() { return "Application Role: clinicalManager"; }
        public String description() { return "Application Role: clinicalManager"; }
        public List<PrivilegeDescriptor> privileges() {
            return Arrays.asList(
                    Privileges.APP_COREAPPS_FIND_PATIENT,
                    Privileges.APP_COREAPPS_ACTIVE_VISITS,
                    Privileges.APP_EMR_INPATIENTS,
                    Privileges.APP_COREAPPS_PATIENT_VISITS,
                    Privileges.APP_COREAPPS_PATIENT_DASHBOARD,
                    Privileges.APP_COREAPPS_AWAITING_ADMISSION,
                    Privileges.TASK_RADIOLOGYAPP_TAB,
                    Privileges.APP_REGISTRATION_REGISTER_PATIENT,
                    Privileges.APP_EMR_CHECK_IN,
                    Privileges.APP_ZL_MPI,
                    Privileges.APP_REPORTINGUI_REPORTS,
                    Privileges.APP_ZL_REPORTS_DATA_EXPORTS,
                    Privileges.APP_APPOINTMENTSCHEDULINGUI_HOME,
                    Privileges.APP_APPOINTMENTSCHEDULINGUI_VIEW_APPOINTMENTS
            );}
    };

    public static RoleDescriptor MEQ = new RoleDescriptor() {
        public String uuid() { return "eb0d9830-e789-11e4-b571-0800200c9a66"; }
        public String role() { return "Application Role: MEQ"; }
        public String description() { return "Application Role: MEQ"; }
        public List<PrivilegeDescriptor> privileges() {
            return Arrays.asList(
                    Privileges.APP_COREAPPS_FIND_PATIENT,
                    Privileges.APP_COREAPPS_ACTIVE_VISITS,
                    Privileges.APP_EMR_INPATIENTS,
                    Privileges.APP_COREAPPS_PATIENT_VISITS,
                    Privileges.APP_COREAPPS_PATIENT_DASHBOARD,
                    Privileges.TASK_RADIOLOGYAPP_TAB,
                    Privileges.APP_REPORTINGUI_REPORTS,
                    Privileges.APP_ZL_REPORTS_DATA_EXPORTS
            );}
    };

    public static RoleDescriptor ARCHIVIST_CLERK = new RoleDescriptor() {
        public String uuid() { return "38048180-e78a-11e4-b571-0800200c9a66"; }
        public String role() { return "Application Role: archivistClerk"; }
        public String description() { return "Application Role: archivistClerk"; }
        public List<PrivilegeDescriptor> privileges() {
            return Arrays.asList(
                    Privileges.APP_COREAPPS_FIND_PATIENT,
                    Privileges.APP_EMR_ARCHIVES_ROOM,
                    Privileges.APP_REGISTRATION_REGISTER_PATIENT,
                    Privileges.APP_EMR_CHECK_IN,
                    Privileges.TASK_EMR_CHECK_IN,
                    Privileges.TASK_EMR_PRINT_WRISTBAND,
                    Privileges.TASK_ARCHIVES_REQUEST_PAPER_RECORDS,
                    Privileges.APP_ZL_MPI
            );}
    };

    public static RoleDescriptor ARCHIVIST_MANAGER = new RoleDescriptor() {
        public String uuid() { return "a5559530-e78a-11e4-b571-0800200c9a66"; }
        public String role() { return "Application Role: archivistManager"; }
        public String description() { return "Application Role: archivistManager"; }
        public List<PrivilegeDescriptor> privileges() {
            return Arrays.asList(
                    Privileges.APP_COREAPPS_FIND_PATIENT,
                    Privileges.APP_COREAPPS_ACTIVE_VISITS,
                    Privileges.APP_EMR_INPATIENTS,
                    Privileges.APP_COREAPPS_PATIENT_VISITS,
                    Privileges.APP_COREAPPS_PATIENT_DASHBOARD,
                    Privileges.APP_EMR_ARCHIVES_ROOM,
                    Privileges.APP_REGISTRATION_REGISTER_PATIENT,
                    Privileges.APP_EMR_CHECK_IN,
                    Privileges.TASK_EMR_CHECK_IN,
                    Privileges.TASK_EMR_PRINT_LABELS,
                    Privileges.TASK_EMR_PRINT_WRISTBAND,
                    Privileges.TASK_ARCHIVES_REQUEST_PAPER_RECORDS,
                    Privileges.APP_ZL_MPI
            );}
    };

    public static RoleDescriptor AUXILIARY_NURSE = new RoleDescriptor() {
        public String uuid() { return "eb5f9350-e78a-11e4-b571-0800200c9a66"; }
        public String role() { return "Application Role: auxiliaryNurse"; }
        public String description() { return "Application Role: auxiliaryNurse"; }
        public List<PrivilegeDescriptor> privileges() {
            return Arrays.asList(
                Privileges.APP_EMR_OUTPATIENT_VITALS
            );}
    };

    public static RoleDescriptor NURSE = new RoleDescriptor() {
        public String uuid() { return "a77483f0-e792-11e4-b571-0800200c9a66"; }
        public String role() { return "Application Role: nurse"; }
        public String description() { return "Application Role: nurse"; }

        public List<PrivilegeDescriptor> privileges() {
            return Arrays.asList(
                    Privileges.APP_COREAPPS_FIND_PATIENT,
                    Privileges.APP_COREAPPS_ACTIVE_VISITS,
                    Privileges.APP_EMR_INPATIENTS,
                    Privileges.APP_COREAPPS_PATIENT_VISITS,
                    Privileges.APP_COREAPPS_PATIENT_DASHBOARD,
                    Privileges.TASK_COREAPPS_CREATE_VISIT,
                    Privileges.APP_EMR_OUTPATIENT_VITALS,
                    Privileges.APP_COREAPPS_AWAITING_ADMISSION,
                    Privileges.APP_ED_TRIAGE,
                    Privileges.APP_ED_TRIAGE_QUEUE,
                    Privileges.TASK_ARCHIVES_REQUEST_PAPER_RECORDS,
                    Privileges.TASK_COREAPPS_CREATE_RETRO_VISIT,
                    Privileges.TASK_RADIOLOGYAPP_TAB,
                    Privileges.TASK_ED_TRIAGE_ENTER_NOTE
            );}
    };

    public static RoleDescriptor ADVANCED_PRACTICE_NURSE = new RoleDescriptor() {
        public String uuid() { return "3f0812e0-e793-11e4-b571-0800200c9a66"; }
        public String role() { return "Application Role: advancedPracticeNurse"; }
        public String description() { return "Application Role: advancedPracticeNurse"; }
        public List<PrivilegeDescriptor> privileges() {
            return Arrays.asList(
                    Privileges.APP_COREAPPS_FIND_PATIENT,
                    Privileges.APP_COREAPPS_ACTIVE_VISITS,
                    Privileges.APP_EMR_INPATIENTS,
                    Privileges.APP_COREAPPS_PATIENT_VISITS,
                    Privileges.APP_COREAPPS_PATIENT_DASHBOARD,
                    Privileges.TASK_COREAPPS_CREATE_VISIT,
                    Privileges.APP_EMR_OUTPATIENT_VITALS,
                    Privileges.APP_COREAPPS_AWAITING_ADMISSION,
                    Privileges.TASK_ARCHIVES_REQUEST_PAPER_RECORDS,
                    Privileges.TASK_COREAPPS_CREATE_RETRO_VISIT,
                    Privileges.TASK_RADIOLOGYAPP_TAB
            );}
    };

    public static RoleDescriptor RADIOLOGY_TECHNICIAN = new RoleDescriptor() {
        public String uuid() { return "6af99db0-e793-11e4-b571-0800200c9a66"; }
        public String role() { return "Application Role: radiologyTechnician"; }
        public String description() { return "Application Role: radiologyTechnician"; }
        public List<PrivilegeDescriptor> privileges() {
            return Arrays.asList(
                    Privileges.APP_COREAPPS_FIND_PATIENT,
                    Privileges.APP_COREAPPS_PATIENT_VISITS,
                    Privileges.TASK_RADIOLOGYAPP_TAB
            );}
    };

    public static RoleDescriptor PHARMACIST = new RoleDescriptor() {
        public String uuid() { return "5da08e21-efd6-497c-9ef5-d50d7e33a63a"; }
        public String role() { return "Application Role: pharmacist"; }
        public String description() { return "Application Role: pharmacist"; }
        public List<PrivilegeDescriptor> privileges() {
            return Arrays.asList(
                    Privileges.APP_DISPENSING_APP_DISPENSE
            );}
    };

    public static RoleDescriptor PHARMACY_AIDE = new RoleDescriptor() {
        public String uuid() { return "36fd044e-f76a-4a07-84b0-1af9d7ce0c59"; }
        public String role() { return "Application Role: pharmacyAide"; }
        public String description() { return "Application Role: pharmacyAide"; }
        public List<PrivilegeDescriptor> privileges() {
            return Arrays.asList(
                    Privileges.APP_DISPENSING_APP_DISPENSE
            );}
    };

    public static RoleDescriptor PHARMACY_MANAGER = new RoleDescriptor() {
        public String uuid() { return "b80f6ec0-e795-11e4-b571-0800200c9a66"; }
        public String role() { return "Application Role: pharmacyManager"; }
        public String description() { return "Application Role: pharmacyManager"; }
        public List<PrivilegeDescriptor> privileges() {
            return Arrays.asList(
                    Privileges.APP_COREAPPS_FIND_PATIENT,
                    Privileges.APP_COREAPPS_ACTIVE_VISITS,
                    Privileges.APP_EMR_INPATIENTS,
                    Privileges.APP_COREAPPS_PATIENT_VISITS,
                    Privileges.APP_COREAPPS_PATIENT_DASHBOARD,
                    Privileges.TASK_COREAPPS_CREATE_RETRO_VISIT,
                    Privileges.APP_DISPENSING_APP_DISPENSE,
                    Privileges.TASK_DISPENSING_DISPENSE,
                    Privileges.TASK_DISPENSING_EDIT,
                    Privileges.TASK_EMR_CHECK_IN
            );}
    };

    public static RoleDescriptor MEDICAL_STUDENT = new RoleDescriptor() {
        public String uuid() { return "257ceea0-e797-11e4-b571-0800200c9a66"; }
        public String role() { return "Application Role: medicalStudent"; }
        public String description() { return "Application Role: medicalStudent"; }
        public List<PrivilegeDescriptor> privileges() {
            return Arrays.asList(
                    Privileges.APP_COREAPPS_FIND_PATIENT,
                    Privileges.APP_COREAPPS_ACTIVE_VISITS,
                    Privileges.APP_EMR_INPATIENTS,
                    Privileges.APP_COREAPPS_PATIENT_VISITS,
                    Privileges.APP_COREAPPS_PATIENT_DASHBOARD,
                    Privileges.APP_EMR_OUTPATIENT_VITALS,
                    Privileges.APP_COREAPPS_AWAITING_ADMISSION,
                    Privileges.TASK_EMR_ENTER_ADMISSION_NOTE,
                    Privileges.TASK_EMR_ENTER_CONSULT_NOTE,
                    Privileges.TASK_EMR_ENTER_ONCOLOGY_CONSULT_NOTE,
                    Privileges.TASK_EMR_ENTER_NCD_CONSULT_NOTE,
                    //Privileges.TASK_EMR_ENTER_MENTAL_HEALTH_NOTE,
                    Privileges.TASK_EMR_ENTER_LAB_RESULTS,
                    Privileges.TASK_EMR_ENTER_ED_NOTE,
                    Privileges.TASK_EMR_ENTER_SURGICAL_NOTE,
                    Privileges.TASK_EMR_ENTER_VITALS_NOTE,
                    Privileges.TASK_EMR_DEATH_CERTIFICATE_FORM,
                    Privileges.TASK_EMR_PATIENT_ENCOUNTER_EDIT,
                    Privileges.TASK_EMR_PATIENT_ENCOUNTER_DELETE,
                    Privileges.TASK_EMR_RETRO_CLINICAL_NOTE,
                    Privileges.TASK_EMR_RETRO_CLINICAL_NOTE_THIS_PROVIDER_ONLY,  // TODO this privilege is kind of meaningless if they have the basic retro clinical note privilege?
                    Privileges.TASK_ARCHIVES_REQUEST_PAPER_RECORDS,
                    Privileges.TASK_COREAPPS_CREATE_RETRO_VISIT,
                    Privileges.TASK_RADIOLOGYAPP_ORDER_CT,
                    Privileges.TASK_RADIOLOGYAPP_ORDER_US,
                    Privileges.TASK_RADIOLOGYAPP_ORDER_XRAY,
                    Privileges.TASK_RADIOLOGYAPP_TAB,
                    Privileges.TASK_ALLERGIES_MODIFY
            );}
    };

    public static RoleDescriptor RESIDENT = new RoleDescriptor() {
        public String uuid() { return "4a8ea310-e796-11e4-b571-0800200c9a66"; }
        public String role() { return "Application Role: resident"; }
        public String description() { return "Application Role: resident"; }
        public List<PrivilegeDescriptor> privileges() {
            return Arrays.asList(
                    Privileges.APP_COREAPPS_FIND_PATIENT,
                    Privileges.APP_COREAPPS_ACTIVE_VISITS,
                    Privileges.APP_EMR_INPATIENTS,
                    Privileges.APP_COREAPPS_PATIENT_VISITS,
                    Privileges.APP_COREAPPS_PATIENT_DASHBOARD,
                    Privileges.TASK_COREAPPS_CREATE_VISIT,
                    Privileges.APP_EMR_OUTPATIENT_VITALS,
                    Privileges.APP_COREAPPS_AWAITING_ADMISSION,
                    Privileges.TASK_EMR_ENTER_ADMISSION_NOTE,
                    Privileges.TASK_EMR_ENTER_CONSULT_NOTE,
                    Privileges.TASK_EMR_ENTER_ONCOLOGY_CONSULT_NOTE,
                    Privileges.TASK_EMR_ENTER_NCD_CONSULT_NOTE,
                    Privileges.TASK_EMR_ENTER_LAB_RESULTS,
                    Privileges.TASK_EMR_ENTER_ED_NOTE,
                    Privileges.TASK_EMR_ENTER_SURGICAL_NOTE,
                    Privileges.TASK_EMR_ENTER_VITALS_NOTE,
                    Privileges.TASK_EMR_DEATH_CERTIFICATE_FORM,
                    Privileges.TASK_EMR_PATIENT_ENCOUNTER_EDIT,
                    Privileges.TASK_EMR_PATIENT_ENCOUNTER_DELETE,
                    Privileges.TASK_EMR_RETRO_CLINICAL_NOTE,
                    Privileges.TASK_EMR_RETRO_CLINICAL_NOTE_THIS_PROVIDER_ONLY,  // TODO this privilege is kind of meaningless if they have the basic retro clinical note privilege?
                    Privileges.TASK_ARCHIVES_REQUEST_PAPER_RECORDS,
                    Privileges.TASK_COREAPPS_CREATE_RETRO_VISIT,
                    Privileges.TASK_RADIOLOGYAPP_ORDER_CT,
                    Privileges.TASK_RADIOLOGYAPP_ORDER_US,
                    Privileges.TASK_RADIOLOGYAPP_ORDER_XRAY,
                    Privileges.TASK_RADIOLOGYAPP_TAB,
                    Privileges.TASK_ALLERGIES_MODIFY
            );}
    };

    public static RoleDescriptor PHYSICIAN = new RoleDescriptor() {
        public String uuid() { return "55f0a630-e797-11e4-b571-0800200c9a66"; }
        public String role() { return "Application Role: physician"; }
        public String description() { return "Application Role: physician"; }
        public List<PrivilegeDescriptor> privileges() {
            return Arrays.asList(
                    Privileges.APP_COREAPPS_FIND_PATIENT,
                    Privileges.APP_COREAPPS_ACTIVE_VISITS,
                    Privileges.APP_EMR_INPATIENTS,
                    Privileges.APP_COREAPPS_PATIENT_VISITS,
                    Privileges.APP_COREAPPS_PATIENT_DASHBOARD,
                    Privileges.TASK_COREAPPS_CREATE_VISIT,
                    Privileges.APP_EMR_OUTPATIENT_VITALS,
                    Privileges.APP_COREAPPS_AWAITING_ADMISSION,
                    Privileges.APP_ED_TRIAGE,
                    Privileges.APP_ED_TRIAGE_QUEUE,
                    Privileges.TASK_EMR_ENTER_ADMISSION_NOTE,
                    Privileges.TASK_EMR_ENTER_CONSULT_NOTE,
                    Privileges.TASK_EMR_ENTER_ONCOLOGY_CONSULT_NOTE,
                    Privileges.TASK_EMR_ENTER_NCD_CONSULT_NOTE,
                    // Privileges.TASK_EMR_ENTER_MENTAL_HEALTH_NOTE,
                    Privileges.TASK_EMR_ENTER_LAB_RESULTS,
                    Privileges.TASK_EMR_ENTER_ED_NOTE,
                    Privileges.TASK_EMR_ENTER_SURGICAL_NOTE,
                    Privileges.TASK_EMR_ENTER_VITALS_NOTE,
                    Privileges.TASK_EMR_DEATH_CERTIFICATE_FORM,
                    Privileges.TASK_EMR_PATIENT_ENCOUNTER_EDIT,
                    Privileges.TASK_EMR_PATIENT_ENCOUNTER_DELETE,
                    Privileges.TASK_EMR_PRINT_LABELS,
                    Privileges.TASK_EMR_RETRO_CLINICAL_NOTE,
                    Privileges.TASK_EMR_RETRO_CLINICAL_NOTE_THIS_PROVIDER_ONLY,  // TODO this privilege is kind of meaningless if they have the basic retro clinical note privilege?
                    Privileges.TASK_ARCHIVES_REQUEST_PAPER_RECORDS,
                    Privileges.TASK_COREAPPS_CREATE_RETRO_VISIT,
                    Privileges.TASK_RADIOLOGYAPP_ORDER_CT,
                    Privileges.TASK_RADIOLOGYAPP_ORDER_US,
                    Privileges.TASK_RADIOLOGYAPP_ORDER_XRAY,
                    Privileges.TASK_RADIOLOGYAPP_TAB,
                    Privileges.TASK_ALLERGIES_MODIFY,
                    Privileges.TASK_ED_TRIAGE_ENTER_NOTE
            );}
    };

    public static RoleDescriptor SURGEON = new RoleDescriptor() {
        public String uuid() { return "78dc9f00-e797-11e4-b571-0800200c9a66"; }
        public String role() { return "Application Role: surgeon"; }
        public String description() { return "Application Role: surgeon"; }
        public List<PrivilegeDescriptor> privileges() {
            return Arrays.asList(
                    Privileges.APP_COREAPPS_FIND_PATIENT,
                    Privileges.APP_COREAPPS_ACTIVE_VISITS,
                    Privileges.APP_EMR_INPATIENTS,
                    Privileges.APP_COREAPPS_PATIENT_VISITS,
                    Privileges.APP_COREAPPS_PATIENT_DASHBOARD,
                    Privileges.TASK_COREAPPS_CREATE_VISIT,
                    Privileges.APP_EMR_OUTPATIENT_VITALS,
                    Privileges.APP_COREAPPS_AWAITING_ADMISSION,
                    Privileges.TASK_EMR_ENTER_ADMISSION_NOTE,
                    Privileges.TASK_EMR_ENTER_CONSULT_NOTE,
                    Privileges.TASK_EMR_ENTER_ONCOLOGY_CONSULT_NOTE,
                    Privileges.TASK_EMR_ENTER_NCD_CONSULT_NOTE,
                    Privileges.TASK_EMR_ENTER_LAB_RESULTS,
                    Privileges.TASK_EMR_ENTER_ED_NOTE,
                    Privileges.TASK_EMR_ENTER_SURGICAL_NOTE,
                    Privileges.TASK_EMR_ENTER_VITALS_NOTE,
                    Privileges.TASK_EMR_DEATH_CERTIFICATE_FORM,
                    Privileges.TASK_EMR_PATIENT_ENCOUNTER_EDIT,
                    Privileges.TASK_EMR_PATIENT_ENCOUNTER_DELETE,
                    Privileges.TASK_EMR_RETRO_CLINICAL_NOTE,
                    Privileges.TASK_EMR_RETRO_CLINICAL_NOTE_THIS_PROVIDER_ONLY,  // TODO this privilege is kind of meaningless if they have the basic retro clinical note privilege?
                    Privileges.TASK_ARCHIVES_REQUEST_PAPER_RECORDS,
                    Privileges.TASK_COREAPPS_CREATE_RETRO_VISIT,
                    Privileges.TASK_RADIOLOGYAPP_ORDER_CT,
                    Privileges.TASK_RADIOLOGYAPP_ORDER_US,
                    Privileges.TASK_RADIOLOGYAPP_ORDER_XRAY,
                    Privileges.TASK_RADIOLOGYAPP_TAB,
                    Privileges.TASK_ALLERGIES_MODIFY
            );}
    };

    public static RoleDescriptor ANAESTHETIST = new RoleDescriptor() {
        public String uuid() { return "aa0c7aa0-e797-11e4-b571-0800200c9a66"; }
        public String role() { return "Application Role: anaesthetist"; }
        public String description() { return "Application Role: anaesthetist"; }
        public List<PrivilegeDescriptor> privileges() {
            return Arrays.asList(
                    Privileges.APP_COREAPPS_FIND_PATIENT,
                    Privileges.APP_COREAPPS_ACTIVE_VISITS,
                    Privileges.APP_EMR_INPATIENTS,
                    Privileges.APP_COREAPPS_PATIENT_VISITS,
                    Privileges.APP_COREAPPS_PATIENT_DASHBOARD,
                    Privileges.APP_EMR_OUTPATIENT_VITALS,
                    Privileges.APP_COREAPPS_AWAITING_ADMISSION,
                    Privileges.TASK_EMR_ENTER_ADMISSION_NOTE,
                    Privileges.TASK_EMR_ENTER_CONSULT_NOTE,
                    Privileges.TASK_EMR_ENTER_ONCOLOGY_CONSULT_NOTE,
                    Privileges.TASK_EMR_ENTER_NCD_CONSULT_NOTE,
                    Privileges.TASK_EMR_ENTER_LAB_RESULTS,
                    Privileges.TASK_EMR_ENTER_ED_NOTE,
                    Privileges.TASK_EMR_ENTER_SURGICAL_NOTE,
                    Privileges.TASK_EMR_ENTER_VITALS_NOTE,
                    Privileges.TASK_EMR_DEATH_CERTIFICATE_FORM,
                    Privileges.TASK_EMR_PATIENT_ENCOUNTER_EDIT,
                    Privileges.TASK_EMR_PATIENT_ENCOUNTER_DELETE,
                    Privileges.TASK_EMR_RETRO_CLINICAL_NOTE,
                    Privileges.TASK_EMR_RETRO_CLINICAL_NOTE_THIS_PROVIDER_ONLY,  // TODO this privilege is kind of meaningless if they have the basic retro clinical note privilege?
                    Privileges.TASK_ARCHIVES_REQUEST_PAPER_RECORDS,
                    Privileges.TASK_COREAPPS_CREATE_RETRO_VISIT,
                    Privileges.TASK_RADIOLOGYAPP_ORDER_CT,
                    Privileges.TASK_RADIOLOGYAPP_ORDER_US,
                    Privileges.TASK_RADIOLOGYAPP_ORDER_XRAY,
                    Privileges.TASK_RADIOLOGYAPP_TAB,
                    Privileges.TASK_ALLERGIES_MODIFY
            );}
    };

    public static RoleDescriptor PSYCHOLOGIST = new RoleDescriptor() {
        public String uuid() { return "e28fcb70-e797-11e4-b571-0800200c9a66"; }
        public String role() { return "Application Role: psychologist"; }
        public String description() { return "Application Role: psychologist"; }
        public List<PrivilegeDescriptor> privileges() {
            return Arrays.asList(
                    Privileges.APP_COREAPPS_FIND_PATIENT,
                    Privileges.APP_COREAPPS_ACTIVE_VISITS,
                    Privileges.APP_EMR_INPATIENTS,
                    Privileges.APP_COREAPPS_PATIENT_VISITS,
                    Privileges.APP_COREAPPS_PATIENT_DASHBOARD,
                    Privileges.TASK_COREAPPS_CREATE_VISIT,
                    Privileges.APP_EMR_OUTPATIENT_VITALS,
                    Privileges.APP_COREAPPS_AWAITING_ADMISSION,
                    Privileges.TASK_EMR_ENTER_ADMISSION_NOTE,
                    Privileges.TASK_EMR_ENTER_CONSULT_NOTE,
                    Privileges.TASK_EMR_ENTER_NCD_CONSULT_NOTE,
                    Privileges.TASK_EMR_ENTER_MENTAL_HEALTH_NOTE,
                    Privileges.TASK_EMR_ENTER_ED_NOTE,
                    Privileges.TASK_EMR_ENTER_SURGICAL_NOTE,
                    Privileges.TASK_EMR_ENTER_VITALS_NOTE,
                    Privileges.TASK_EMR_DEATH_CERTIFICATE_FORM,
                    Privileges.TASK_EMR_PATIENT_ENCOUNTER_EDIT,
                    Privileges.TASK_EMR_PATIENT_ENCOUNTER_DELETE,
                    Privileges.TASK_EMR_RETRO_CLINICAL_NOTE,
                    Privileges.TASK_EMR_RETRO_CLINICAL_NOTE_THIS_PROVIDER_ONLY,  // TODO this privilege is kind of meaningless if they have the basic retro clinical note privilege?
                    Privileges.TASK_ARCHIVES_REQUEST_PAPER_RECORDS,
                    Privileges.TASK_COREAPPS_CREATE_RETRO_VISIT,
                    Privileges.TASK_RADIOLOGYAPP_TAB,
                    Privileges.TASK_ALLERGIES_MODIFY
            );}
    };

    public static RoleDescriptor PHYSICAL_THERAPIST = new RoleDescriptor() {
        public String uuid() { return "7bea85d0-e798-11e4-b571-0800200c9a66"; }
        public String role() { return "Application Role: physicalTherapist"; }
        public String description() { return "Application Role: physicalTherapist"; }
        public List<PrivilegeDescriptor> privileges() {
            return Arrays.asList(
                    Privileges.APP_COREAPPS_FIND_PATIENT,
                    Privileges.APP_COREAPPS_ACTIVE_VISITS,
                    Privileges.APP_EMR_INPATIENTS,
                    Privileges.APP_COREAPPS_PATIENT_VISITS,
                    Privileges.APP_COREAPPS_PATIENT_DASHBOARD,
                    Privileges.APP_EMR_OUTPATIENT_VITALS,
                    Privileges.APP_COREAPPS_AWAITING_ADMISSION,
                    Privileges.TASK_EMR_ENTER_ADMISSION_NOTE,
                    Privileges.TASK_EMR_ENTER_CONSULT_NOTE,
                    Privileges.TASK_EMR_ENTER_NCD_CONSULT_NOTE,
                    Privileges.TASK_EMR_ENTER_ED_NOTE,
                    Privileges.TASK_EMR_ENTER_SURGICAL_NOTE,
                    Privileges.TASK_EMR_ENTER_VITALS_NOTE,
                    Privileges.TASK_EMR_DEATH_CERTIFICATE_FORM,
                    Privileges.TASK_EMR_PATIENT_ENCOUNTER_EDIT,
                    Privileges.TASK_EMR_PATIENT_ENCOUNTER_DELETE,
                    Privileges.TASK_EMR_RETRO_CLINICAL_NOTE,
                    Privileges.TASK_EMR_RETRO_CLINICAL_NOTE_THIS_PROVIDER_ONLY,  // TODO this privilege is kind of meaningless if they have the basic retro clinical note privilege?
                    Privileges.TASK_ARCHIVES_REQUEST_PAPER_RECORDS,
                    Privileges.TASK_COREAPPS_CREATE_RETRO_VISIT,
                    Privileges.TASK_RADIOLOGYAPP_TAB,
                    Privileges.TASK_ALLERGIES_MODIFY
            );}
    };

    public static RoleDescriptor SOCIAL_WORKER = new RoleDescriptor() {
        public String uuid() { return "fd7f2150-e798-11e4-b571-0800200c9a66"; }
        public String role() { return "Application Role: socialWorker"; }
        public String description() { return "Application Role: socialWorker"; }
        public List<PrivilegeDescriptor> privileges() {
            return Arrays.asList(
                    Privileges.APP_COREAPPS_FIND_PATIENT,
                    Privileges.APP_COREAPPS_ACTIVE_VISITS,
                    Privileges.APP_EMR_INPATIENTS,
                    Privileges.APP_COREAPPS_PATIENT_VISITS,
                    Privileges.APP_COREAPPS_PATIENT_DASHBOARD,
                    Privileges.APP_EMR_OUTPATIENT_VITALS,
                    Privileges.APP_COREAPPS_AWAITING_ADMISSION
            );}
    };

    public static RoleDescriptor SCHEDULE_MANAGER = new RoleDescriptor() {
        public String uuid() { return "af138b30-b078-4b28-8ded-d0dfbcb8c783"; }
        public String role() { return "Application Role: scheduleManager"; }
        public String description() { return "Gives user access to all apps and tasks within the Appointment Scheduling UI module"; }
        public List<PrivilegeDescriptor> privileges() {
            return Arrays.asList(
                    Privileges.APP_COREAPPS_FIND_PATIENT,
                    Privileges.APP_COREAPPS_ACTIVE_VISITS,
                    Privileges.APP_EMR_INPATIENTS,
                    Privileges.APP_COREAPPS_PATIENT_VISITS,
                    Privileges.APP_COREAPPS_PATIENT_DASHBOARD,
                    Privileges.APP_COREAPPS_AWAITING_ADMISSION,
                    Privileges.TASK_RADIOLOGYAPP_TAB,
                    Privileges.APP_REGISTRATION_REGISTER_PATIENT,
                    Privileges.TASK_EMR_CHECK_IN,
                    Privileges.APP_ZL_MPI,
                    Privileges.APP_REPORTINGUI_REPORTS,
                    Privileges.APP_ZL_REPORTS_DATA_EXPORTS,
                    Privileges.APP_APPOINTMENTSCHEDULINGUI_HOME,
                    Privileges.APP_APPOINTMENTSCHEDULINGUI_APPOINTMENT_TYPES,
                    Privileges.APP_APPOINTMENTSCHEDULINGUI_PROVIDER_SCHEDULES,
                    Privileges.TASK_APPOINTMENTSCHEDULINGUI_BOOK_APPOINTMENTS,
                    Privileges.TASK_APPOINTMENTSCHEDULINGUI_OVERBOOK_APPOINTMENTS,
                    Privileges.APP_APPOINTMENTSCHEDULINGUI_VIEW_APPOINTMENTS
            );}
    };

    public static RoleDescriptor LEGACY_ARCHIVIST_AIDE = new RoleDescriptor() {
        public String uuid() { return "2170e9bf-6d30-4ad9-9319-a454bf32dbf9"; }
        public String role() { return "Application Role: archivistAide"; }
        public String description() { return "Gives access to only the archives app"; }
        public List<PrivilegeDescriptor> privileges() {
            return Arrays.asList(
                    Privileges.APP_EMR_ARCHIVES_ROOM
            );}
    };

    public static RoleDescriptor LEGACY_CHECK_IN = new RoleDescriptor() {
        public String uuid() { return "d711c948-2d9b-4700-9c80-c32ec6a8ddeb"; }
        public String role() { return "Application Role: checkIn"; }
        public String description() { return "Gives user access to the Start a clinic visit app"; }
        public List<PrivilegeDescriptor> privileges() {
            return Arrays.asList(
                    Privileges.APP_EMR_CHECK_IN
            );}
    };

    public static RoleDescriptor LEGACY_CLINICAL = new RoleDescriptor() {
        public String uuid() { return "05a3c5fc-3c7f-4c93-87d2-c6013e71a102"; }
        public String role() { return "Application Role: clinical"; }
        public String description() { return "Application Role: clinical"; }
        public List<PrivilegeDescriptor> privileges() {
            return Arrays.asList(
                    Privileges.APP_COREAPPS_AWAITING_ADMISSION,
                    Privileges.APP_EMR_INPATIENTS,
                    Privileges.APP_EMR_OUTPATIENT_VITALS,
                    Privileges.APP_COREAPPS_ACTIVE_VISITS,
                    Privileges.APP_REPORTINGUI_REPORTS,
                    Privileges.APP_ED_TRIAGE,
                    Privileges.APP_ED_TRIAGE_QUEUE,
                    Privileges.TASK_APPOINTMENTSCHEDULINGUI_REQUEST_APPOINTMENTS,
                    Privileges.TASK_APPOINTMENTSCHEDULINGUI_VIEW_CONFIDENTIAL,
                    Privileges.TASK_COREAPPS_CREATE_VISIT,
                    Privileges.TASK_EMR_ENTER_ADMISSION_NOTE,
                    Privileges.TASK_EMR_ENTER_CONSULT_NOTE,
                    Privileges.TASK_EMR_ENTER_ONCOLOGY_CONSULT_NOTE,
                    Privileges.TASK_EMR_ENTER_NCD_CONSULT_NOTE,
                    //Privileges.TASK_EMR_ENTER_MENTAL_HEALTH_NOTE,
                    Privileges.TASK_EMR_ENTER_LAB_RESULTS,
                    Privileges.TASK_EMR_ENTER_ED_NOTE,
                    Privileges.TASK_EMR_ENTER_SURGICAL_NOTE,
                    Privileges.TASK_EMR_ENTER_VITALS_NOTE,
                    Privileges.TASK_EMR_PRINT_LABELS,
                    Privileges.TASK_EMR_PRINT_WRISTBAND,
                    Privileges.TASK_EMR_RETRO_CLINICAL_NOTE_THIS_PROVIDER_ONLY,
                    Privileges.TASK_EMR_DEATH_CERTIFICATE_FORM,
                    Privileges.TASK_RADIOLOGYAPP_ORDER_CT,
                    Privileges.TASK_RADIOLOGYAPP_ORDER_US,
                    Privileges.TASK_RADIOLOGYAPP_ORDER_XRAY,
                    Privileges.TASK_RADIOLOGYAPP_TAB,
                    Privileges.TASK_ALLERGIES_MODIFY,
                    Privileges.TASK_ED_TRIAGE_ENTER_NOTE
            );}
    };

    public static RoleDescriptor LEGACY_DATA_ARCHIVES = new RoleDescriptor() {
        public String uuid() { return "0b30c2e0-6276-4bf5-a8ac-e38da035888a"; }
        public String role() { return "Application Role: dataArchives"; }
        public String description() { return "Application Role: dataArchives"; }
        public List<PrivilegeDescriptor> privileges() {
            return Arrays.asList(
                    Privileges.APP_COREAPPS_FIND_PATIENT,
                    Privileges.APP_COREAPPS_AWAITING_ADMISSION,
                    Privileges.APP_EMR_ARCHIVES_ROOM,
                    Privileges.APP_EMR_INPATIENTS,
                    Privileges.APP_EMR_CHECK_IN,
                    Privileges.APP_ZL_MPI,
                    Privileges.APP_COREAPPS_ACTIVE_VISITS,
                    Privileges.APP_REGISTRATION_REGISTER_PATIENT,
                    Privileges.APP_REPORTINGUI_REPORTS,
                    Privileges.TASK_ARCHIVES_REQUEST_PAPER_RECORDS,
                    Privileges.TASK_COREAPPS_CREATE_VISIT,
                    Privileges.TASK_COREAPPS_CREATE_RETRO_VISIT,
                    Privileges.TASK_COREAPPS_MERGE_VISITS,
                    Privileges.TASK_EMR_CHECK_IN,
                    Privileges.TASK_EMR_ENTER_ADMISSION_NOTE,
                    Privileges.TASK_EMR_ENTER_CONSULT_NOTE,
                    Privileges.TASK_EMR_ENTER_ONCOLOGY_CONSULT_NOTE,
                    Privileges.TASK_EMR_ENTER_NCD_CONSULT_NOTE,
                    //Privileges.TASK_EMR_ENTER_MENTAL_HEALTH_NOTE,
                    Privileges.TASK_EMR_ENTER_LAB_RESULTS,
                    Privileges.TASK_EMR_ENTER_ED_NOTE,
                    Privileges.TASK_EMR_ENTER_SURGICAL_NOTE,
                    Privileges.TASK_EMR_ENTER_VITALS_NOTE,
                    Privileges.TASK_EMR_PRINT_WRISTBAND,
                    Privileges.TASK_EMR_RETRO_CLINICAL_NOTE,
                    Privileges.TASK_RADIOLOGYAPP_ORDER_CT,
                    Privileges.TASK_RADIOLOGYAPP_ORDER_US,
                    Privileges.TASK_RADIOLOGYAPP_ORDER_XRAY,
                    Privileges.TASK_RADIOLOGYAPP_RETRO_ORDER
            );}
    };

    public static RoleDescriptor LEGACY_PATIENT_MEDICAL_INFORMATION = new RoleDescriptor() {
        public String uuid() { return "5d08f1df-f161-46f0-9246-90b2f24aa862"; }
        public String role() { return "Application Role: patientMedicalInformation"; }
        public String description() { return "Gives access to patient-level medical information, including access to patient dashboard"; }
        public List<PrivilegeDescriptor> privileges() {
            return Arrays.asList(
                    Privileges.APP_COREAPPS_FIND_PATIENT,
                    Privileges.APP_COREAPPS_PATIENT_DASHBOARD,
                    Privileges.APP_COREAPPS_PATIENT_VISITS
            );}
    };



    public static RoleDescriptor LEGACY_RADIOLOGY = new RoleDescriptor() {
        public String uuid() { return "3672ebe7-ed59-461f-a662-5ae3cd2e46ac"; }
        public String role() { return "Application Role: radiology"; }
        public String description() { return "Application Role: radiology"; }
        public List<PrivilegeDescriptor> privileges() {
            return Arrays.asList(
                    Privileges.APP_EMR_INPATIENTS,
                    Privileges.APP_COREAPPS_ACTIVE_VISITS,
                    Privileges.APP_REPORTINGUI_REPORTS,
                    Privileges.TASK_COREAPPS_CREATE_VISIT,
                    Privileges.TASK_EMR_PRINT_WRISTBAND,
                    Privileges.TASK_ARCHIVES_REQUEST_PAPER_RECORDS,
                    Privileges.TASK_RADIOLOGYAPP_ORDER_CT,
                    Privileges.TASK_RADIOLOGYAPP_ORDER_US,
                    Privileges.TASK_RADIOLOGYAPP_ORDER_XRAY,
                    Privileges.TASK_RADIOLOGYAPP_TAB
            );}
    };

    public static RoleDescriptor LEGACY_REPORTS = new RoleDescriptor() {
        public String uuid() { return "343872bd-a927-4f9d-bf2c-75f895c44f6c"; }
        public String role() { return "Application Role: reports"; }
        public String description() { return "Rights  to view and run reports and data exports"; }
        public List<PrivilegeDescriptor> privileges() {
            return Arrays.asList(
                    Privileges.APP_REPORTINGUI_ADHOC_ANALYSIS,
                    Privileges.APP_REPORTINGUI_REPORTS,
                    Privileges.APP_ZL_REPORTS_DATA_EXPORTS
            );}
    };

    public static RoleDescriptor LEGACY_SCHEDULE_ADMINISTRATOR = new RoleDescriptor() {
        public String uuid() { return "f6fcff07-8da1-4c7f-8c62-07352101d0a8"; }
        public String role() { return "Application Role: scheduleAdministrator"; }
        public String description() { return "Gives user the ability to manage provider schedules and service types"; }
        public List<PrivilegeDescriptor> privileges() {
            return Arrays.asList(
                    Privileges.APP_APPOINTMENTSCHEDULINGUI_APPOINTMENT_TYPES,
                    Privileges.APP_APPOINTMENTSCHEDULINGUI_PROVIDER_SCHEDULES,
                    Privileges.APP_APPOINTMENTSCHEDULINGUI_HOME
            );}
    };


    public static RoleDescriptor LEGACY_SCHEDULER = new RoleDescriptor() {
        public String uuid() { return "eeece3a4-94f8-4693-bc29-d862bbf6da0b"; }
        public String role() { return "Application Role: scheduler"; }
        public String description() { return "Gives user the ability to view and schedule appointments within the appointmentschedulingui module (but not to administer provider schedules or to overbook appointments)"; }
        public List<PrivilegeDescriptor> privileges() {
            return Arrays.asList(
                    Privileges.APP_APPOINTMENTSCHEDULINGUI_HOME,
                    Privileges.APP_APPOINTMENTSCHEDULINGUI_VIEW_APPOINTMENTS,
                    Privileges.TASK_APPOINTMENTSCHEDULINGUI_BOOK_APPOINTMENTS,
                    Privileges.TASK_APPOINTMENTSCHEDULINGUI_VIEW_CONFIDENTIAL
            );}
    };

    public static RoleDescriptor LEGACY_SCHEDULE_VIEWER = new RoleDescriptor() {
        public String uuid() { return "7b82b7ff-6da7-4be2-adb3-44e000f95895"; }
        public String role() { return "Application Role: scheduleViewer"; }
        public String description() { return "Gives user the ability to view appointment schedules (but not to modify them)"; }
        public List<PrivilegeDescriptor> privileges() {
            return Arrays.asList(
                    Privileges.APP_APPOINTMENTSCHEDULINGUI_HOME,
                    Privileges.APP_APPOINTMENTSCHEDULINGUI_VIEW_APPOINTMENTS
            );}
    };

}
