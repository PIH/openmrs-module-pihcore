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

import org.openmrs.module.coreapps.CoreAppsConstants;
import org.openmrs.module.metadatadeploy.descriptor.PrivilegeDescriptor;

/**
 * Constants for all defined privileges
 */
public class Privileges {

    //
    // APP PRIVILEGES
    //

    public static PrivilegeDescriptor APP_APPOINTMENTSCHEDULINGUI_APPOINTMENT_TYPES = new PrivilegeDescriptor() {
        public String uuid() { return "80013028-96c8-4e96-b680-e19032f22571"; }
        public String privilege() { return "App: appointmentschedulingui.appointmentTypes"; }
        public String description() { return "Access to the Manage Service Types app"; }
    };

    public static PrivilegeDescriptor APP_APPOINTMENTSCHEDULINGUI_HOME = new PrivilegeDescriptor() {
        public String uuid() { return "607be939-6684-49b1-9bf0-38f5153a5346"; }
        public String privilege() { return "App: appointmentschedulingui.home"; }
        public String description() { return "Access to the Appointment Scheduling home page"; }
    };

    public static PrivilegeDescriptor APP_APPOINTMENTSCHEDULINGUI_PROVIDER_SCHEDULES = new PrivilegeDescriptor() {
        public String uuid() { return "18d90abd-1a59-49ea-b211-9726801cc434"; }
        public String privilege() { return "App: appointmentschedulingui.providerSchedules"; }
        public String description() { return "Access to the Manage Provider Schedules app"; }
    };

    public static PrivilegeDescriptor APP_APPOINTMENTSCHEDULINGUI_VIEW_APPOINTMENTS = new PrivilegeDescriptor() {
        public String uuid() { return "a8a5bcb9-73d5-44a1-b90f-b38feccdaf21"; }
        public String privilege() { return "App: appointmentschedulingui.viewAppointments"; }
        public String description() { return "Access to Manage Appointments and Daily Scheduled Appointments (but not the ability to book appointments from these pages)"; }
    };

    public static PrivilegeDescriptor APP_COREAPPS_ACTIVE_VISITS= new PrivilegeDescriptor() {
        public String uuid() { return "7ed67678-7b02-4fe6-8f53-f4a1a2ffa879"; }
        public String privilege() { return "App: coreapps.activeVisits"; }
        public String description() { return "Able to access the active visits app"; }
    };

    public static PrivilegeDescriptor APP_COREAPPS_AWAITING_ADMISSION= new PrivilegeDescriptor() {
        public String uuid() { return "7232d5a2-f890-4834-a0ea-e88eadc8aae2"; }
        public String privilege() { return "App: coreapps.awaitingAdmission"; }
        public String description() { return "Privilege to use the Awaiting Admission app"; }
    };

    public static PrivilegeDescriptor APP_COREAPPS_DATA_MANAGEMENT= new PrivilegeDescriptor() {
        public String uuid() { return "d82f58c3-f4f5-41de-8ff6-1b2d460d782d"; }
        public String privilege() { return "App: coreapps.dataManagement"; }
        public String description() { return "Able to access data management apps"; }
    };

    public static PrivilegeDescriptor APP_COREAPPS_FIND_PATIENT = new PrivilegeDescriptor() {
        public String uuid() { return "e4eb01c1-967a-4091-a2a0-ec933903fd54"; }
        public String privilege() { return "App: coreapps.findPatient"; }
        public String description() { return "Able to access the find patient app"; }
    };

    public static PrivilegeDescriptor APP_COREAPPS_PATIENT_DASHBOARD = new PrivilegeDescriptor() {
        public String uuid() { return "b37491af-10df-4465-863c-0a7f5d612c08"; }
        public String privilege() { return CoreAppsConstants.PRIVILEGE_PATIENT_DASHBOARD; }
        public String description() { return "Able to access the patient dashboard"; }
    };

    public static PrivilegeDescriptor APP_COREAPPS_PATIENT_VISITS = new PrivilegeDescriptor() {
        public String uuid() { return "908aa2ce-cdd6-4b77-9664-bbcdb011432a"; }
        public String privilege() { return CoreAppsConstants.PRIVILEGE_PATIENT_VISITS; }
        public String description() { return "Able to access the patient visits screen"; }
    };

    public static PrivilegeDescriptor APP_DISPENSING_APP_DISPENSE = new PrivilegeDescriptor() {
        public String uuid() { return "769a12c2-6ff0-42ba-888e-52e66ed16fb7"; }
        public String privilege() { return "App: dispensing.app.dispense"; }
        public String description() { return "Access to dispensing medication app"; }
    };

    public static PrivilegeDescriptor APP_EMR_ARCHIVES_ROOM = new PrivilegeDescriptor() {
        public String uuid() { return "24fb3877-c07e-436f-8005-ce7a81245fcd"; }
        public String privilege() { return "App: emr.archivesRoom"; }
        public String description() { return "Run the Archives Room app"; }
    };

    public static PrivilegeDescriptor APP_EMR_INPATIENTS = new PrivilegeDescriptor() {
        public String uuid() { return "60ce8766-57cc-4e4f-8356-6bbe3f56f980"; }
        public String privilege() { return "App: emr.inpatients"; }
        public String description() { return "Right to view the Impatient app"; }
    };

    public static PrivilegeDescriptor APP_EMR_SYSTEM_ADMINISTRATION = new PrivilegeDescriptor() {
        public String uuid() { return "6d01acf6-ba11-4634-866b-489eaa951681"; }
        public String privilege() { return "App: emr.systemAdministration"; }
        public String description() { return "Run the System Administration app"; }
    };

    public static PrivilegeDescriptor APP_LEGACY_ADMIN = new PrivilegeDescriptor() {
        public String uuid() { return "18c8d715-8845-44e3-ac62-4b613f00e082"; }
        public String privilege() { return "App: legacy.admin"; }
        public String description() { return "Run the (Legacy) OpenMRS Administration app"; }
    };

    public static PrivilegeDescriptor APP_EMR_CHECK_IN = new PrivilegeDescriptor() {
        public String uuid() { return "d36890f2-ef9f-4cd5-a48e-f5519e608b93"; }
        public String privilege() { return "App: mirebalais.checkin"; }
        public String description() { return "Access the ZL Mirebalais Check-In app"; }
    };

    public static PrivilegeDescriptor APP_ZL_MPI = new PrivilegeDescriptor() {
        public String uuid() { return "9500dbe5-1a70-44a5-881d-424afc103367"; }
        public String privilege() { return "App: mirebalais.mpi"; }
        public String description() { return "Run the Search Master Patient Index app"; }
    };

    public static PrivilegeDescriptor APP_EMR_OUTPATIENT_VITALS = new PrivilegeDescriptor() {
        public String uuid() { return "409c73d3-1184-4df1-b4b9-05f37b48f9ba"; }
        public String privilege() { return "App: mirebalais.outpatientVitals"; }
        public String description() { return "Run the capture Vitals app"; }
    };

    public static PrivilegeDescriptor APP_ZL_REPORTS_DATA_EXPORTS = new PrivilegeDescriptor() {
        public String uuid() { return "10122e43-196b-4563-b083-f0ec00f0429b"; }
        public String privilege() { return "App: mirebalaisreports.dataexports"; }
        public String description() { return "Ability to run data export reports"; }
    };

    public static PrivilegeDescriptor APP_REGISTRATION_REGISTER_PATIENT = new PrivilegeDescriptor() {
        public String uuid() { return "744d914c-8b1f-4f0e-abfb-b4ee17ad0865"; }
        public String privilege() { return "App: registrationapp.registerPatient"; }
        public String description() { return "Able to access the register patient app"; }
    };

    public static PrivilegeDescriptor APP_REPORTINGUI_ADHOC_ANALYSIS = new PrivilegeDescriptor() {
        public String uuid() { return "89cde56a-d8cc-4261-b491-38a1296edce0"; }
        public String privilege() { return "App: reportingui.adHocAnalysis"; }
        public String description() { return "Use the Ad Hoc Analysis tool"; }
    };

    public static PrivilegeDescriptor APP_REPORTINGUI_REPORTS = new PrivilegeDescriptor() {
        public String uuid() { return "a4bb96b4-b3cb-4e3c-9251-a7c0ca173035"; }
        public String privilege() { return "App: reportingui.reports"; }
        public String description() { return "Use the Reports app provided by the reportingui module"; }
    };

    public static PrivilegeDescriptor APP_WAITING_FOR_CONSULT = new PrivilegeDescriptor() {
        public String uuid() { return "a4bb96b4-b3cb-4e3c-9251-a7c0ca173035"; }
        public String privilege() { return "App: pihcore.waitingForConsult"; }
        public String description() { return "Use the Waiting For Consult app provided by PIH Core"; }
    };

    public static PrivilegeDescriptor APP_ED_TRIAGE = new PrivilegeDescriptor() {
        public String uuid() { return "1b699660-245a-11e6-bdf4-0800200c9a66"; }
        public String privilege() { return "App: edtriageapp.edtriage"; }
        public String description() { return "Use ED Triage App provided by the ED Triage moddule"; }
    };

    public static PrivilegeDescriptor APP_ED_TRIAGE_QUEUE = new PrivilegeDescriptor() {
        public String uuid() { return "F7BAE2F7-78AB-4D7A-8470-8068E4969F56"; }
        public String privilege() { return "App: edtriageapp.edtriage.queue"; }
        public String description() { return "Use ED Triage Queue provided by the ED Triage moddule"; }
    };

    //
    // TASK PRIVILEGES
    //

    public static PrivilegeDescriptor TASK_APPOINTMENTSCHEDULINGUI_BOOK_APPOINTMENTS = new PrivilegeDescriptor() {
        public String uuid() { return "93b2a795-650a-42a0-be23-7ce25dd06b9f"; }
        public String privilege() { return "Task: appointmentschedulingui.bookAppointments"; }
        public String description() { return "Ability book appointments, cancel appointments, and flag appointments as needs reschedule; Access to the Manage Rescheduled app"; }
    };

    public static PrivilegeDescriptor TASK_APPOINTMENTSCHEDULINGUI_OVERBOOK_APPOINTMENTS = new PrivilegeDescriptor() {
        public String uuid() { return "38ee3153-b20a-4684-b4c4-3f88578e2dd0"; }
        public String privilege() { return "Task: appointmentschedulingui.overbookAppointments"; }
        public String description() { return "Ability to overbook time slots"; }
    };

    public static PrivilegeDescriptor TASK_APPOINTMENTSCHEDULINGUI_REQUEST_APPOINTMENTS = new PrivilegeDescriptor() {
        public String uuid() { return "2709cc6e-a659-43bb-9706-c98502f88e9f"; }
        public String privilege() { return "Task: appointmentschedulingui.requestAppointments"; }
        public String description() { return "Ability to request an appointment for a patient"; }
    };

    public static PrivilegeDescriptor TASK_APPOINTMENTSCHEDULINGUI_VIEW_CONFIDENTIAL = new PrivilegeDescriptor() {
        public String uuid() { return "d42af282-f013-4cf2-b52f-9b02030c62a6"; }
        public String privilege() { return "Task: appointmentschedulingui.viewConfidential"; }
        public String description() { return "Ability to view appointments marked as confidential in the appointment scheduling ui"; }
    };

    public static PrivilegeDescriptor TASK_COREAPPS_CREATE_RETRO_VISIT = new PrivilegeDescriptor() {
        public String uuid() { return "2112a93c-f219-476e-8747-f23c38d9abaa"; }
        public String privilege() { return "Task: coreapps.createRetrospectiveVisit"; }
        public String description() { return "Ability to create a create retrospective visit (i.e., one that starts in the past)"; }
    };

    public static PrivilegeDescriptor TASK_COREAPPS_CREATE_VISIT = new PrivilegeDescriptor() {
        public String uuid() { return "ec6fcf8e-e4b7-4c69-8ca0-97cf2796315e"; }
        public String privilege() { return CoreAppsConstants.PRIVILEGE_START_VISIT; }
        public String description() { return "Rights to start a visit from the patient dashboard"; }
    };

    public static PrivilegeDescriptor TASK_COREAPPS_END_VISIT = new PrivilegeDescriptor() {
        public String uuid() { return "3082f9ac-e865-4234-81a9-b8a889225023"; }
        public String privilege() { return CoreAppsConstants.PRIVILEGE_END_VISIT; }
        public String description() { return "Ability to end visit"; }
    };

    public static PrivilegeDescriptor TASK_COREAPPS_MERGE_VISITS = new PrivilegeDescriptor() {
        public String uuid() { return "b79b8f83-75e0-4e74-a1c5-2a6e7eb67ab8"; }
        public String privilege() { return "Task: coreapps.mergeVisits"; }
        public String description() { return "Ability to merge visits"; }
    };

    public static PrivilegeDescriptor TASK_ARCHIVES_REQUEST_PAPER_RECORDS = new PrivilegeDescriptor() {
        public String uuid() { return "91d80e71-3ad7-4bc2-8bef-c6378e1b2b70"; }
        public String privilege() { return "Task: emr.requestPaperRecord"; }
        public String description() { return "Access the emr.requestPaperRecord task"; }
    };

    public static PrivilegeDescriptor TASK_EMR_ENTER_ADMISSION_NOTE = new PrivilegeDescriptor() {
        public String uuid() { return "3f407fdd-df00-42ec-9e1d-864ee896b2cb"; }
        public String privilege() { return "Task: emr.enterAdmissionNote"; }
        public String description() { return "Ability to enter an admission note"; }
    };

    public static PrivilegeDescriptor TASK_EMR_ENTER_CONSULT_NOTE = new PrivilegeDescriptor() {
        public String uuid() { return "02bd5352-aac7-4ec3-867b-79c66833d771"; }
        public String privilege() { return "Task: emr.enterConsultNote"; }
        public String description() { return "Ability to enter a consult note"; }
    };

    public static PrivilegeDescriptor TASK_EMR_ENTER_ED_NOTE = new PrivilegeDescriptor() {
        public String uuid() { return "62ef802c-0e6e-4aee-8340-bb25ed5da419"; }
        public String privilege() { return "Task: emr.enterEDNote"; }
        public String description() { return "Ability to enter an ED Note"; }
    };

    public static PrivilegeDescriptor TASK_EMR_ENTER_SURGICAL_NOTE = new PrivilegeDescriptor() {
        public String uuid() { return "1e4b2181-12fd-4dfc-a200-f17975116bf0"; }
        public String privilege() { return "Task: emr.enterSurgicalNote"; }
        public String description() { return "Ability to enter a surgical note"; }
    };

    public static PrivilegeDescriptor TASK_EMR_ENTER_VITALS_NOTE = new PrivilegeDescriptor() {
        public String uuid() { return "d7135338-93e9-46a9-9cde-07af84b2fe8c"; }
        public String privilege() { return "Task: emr.enterVitalsNote"; }
        public String description() { return "Ability to enter a vitals note"; }
    };

    public static PrivilegeDescriptor TASK_EMR_ENTER_ONCOLOGY_CONSULT_NOTE = new PrivilegeDescriptor() {
        public String uuid() { return "22d89390-105d-11e5-b939-0800200c9a66"; }
        public String privilege() { return "Task: emr.enterOncologyConsultNote"; }
        public String description() { return "Ability to enter an oncology consult note"; }
    };

    public static PrivilegeDescriptor TASK_EMR_ENTER_NCD_CONSULT_NOTE = new PrivilegeDescriptor() {
        public String uuid() { return "ba550ca0-6934-11e5-a837-0800200c9a66"; }
        public String privilege() { return "Task: emr.enterNCDConsultNote"; }
        public String description() { return "Ability to enter an NCD consult note"; }
    };

    public static PrivilegeDescriptor TASK_EMR_ENTER_MENTAL_HEALTH_NOTE = new PrivilegeDescriptor() {
        public String uuid() { return "5f520ae8-cf63-11e5-ab30-625662870761"; }
        public String privilege() { return "Task: emr.enterMentalHealthNote"; }
        public String description() { return "Ability to enter a mental health note"; }
    };

    public static PrivilegeDescriptor TASK_EMR_ENTER_LAB_RESULTS = new PrivilegeDescriptor() {
        public String uuid() { return "fbc80ff0-0805-11e6-a837-0800200c9a66"; }
        public String privilege() { return "Task: emr.enterLabResults"; }
        public String description() { return "Ability to enter a lab results form"; }
    };

    public static PrivilegeDescriptor TASK_EMR_PATIENT_ENCOUNTER_DELETE = new PrivilegeDescriptor() {
        public String uuid() { return "5367155a-e435-4010-877a-f3007059c352"; }
        public String privilege() { return "Task: emr.patient.encounter.delete"; }
        public String description() { return "Gives permission to a user to delete a patient encounter"; }
    };

    public static PrivilegeDescriptor TASK_EMR_PATIENT_ENCOUNTER_EDIT = new PrivilegeDescriptor() {
        public String uuid() { return "4841f2dd-31f0-46b9-9877-74dcaf2cf07b"; }
        public String privilege() { return "Task: emr.patient.encounter.edit"; }
        public String description() { return "Gives permission to a user to edit a patient encounter"; }
    };

    public static PrivilegeDescriptor TASK_COREAPPS_DELETE_VISIT = new PrivilegeDescriptor() {
        public String uuid() { return "6cdc5785-ee5c-48af-a2a2-7a574721fa97"; }
        public String privilege() { return "Task: emr.patient.visit.delete"; }
        public String description() { return "User has ability to delete a visit"; }
    };

    public static PrivilegeDescriptor TASK_EMR_PRINT_LABELS = new PrivilegeDescriptor() {
        public String uuid() { return "d8165924-87b3-441d-bce8-2230d7bb3307"; }
        public String privilege() { return "Task: emr.printLabels"; }
        public String description() { return "Access the emr.printPaperRecordLabel task"; }
    };

    public static PrivilegeDescriptor TASK_EMR_PRINT_WRISTBAND = new PrivilegeDescriptor() {
        public String uuid() { return "7f4087ec-1d57-42aa-8704-fa27b0867d6f"; }
        public String privilege() { return "Task: emr.printWristband"; }
        public String description() { return "Ability to print a wristband from the patient dashboard"; }
    };

    public static PrivilegeDescriptor TASK_EMR_RETRO_CLINICAL_NOTE = new PrivilegeDescriptor() {
        public String uuid() { return "d5e693ae-3f24-4fcb-abfd-eec345a76c17"; }
        public String privilege() { return "Task: emr.retroConsultNote"; }
        public String description() { return "Ability to retrospectively enter a consult note"; }
    };

    public static PrivilegeDescriptor TASK_EMR_RETRO_CLINICAL_NOTE_THIS_PROVIDER_ONLY = new PrivilegeDescriptor() {
        public String uuid() { return "ac6585f1-10e0-4a4e-84e3-fb8b098f0cbe"; }
        public String privilege() { return "Task: emr.retroConsultNoteThisProviderOnly"; }
        public String description() { return "Ability to retrospectively enter a consult note for provider user."; }
    };

    public static PrivilegeDescriptor TASK_EMR_CHECK_IN = new PrivilegeDescriptor() {
        public String uuid() { return "1a819ef6-4632-4d2e-8227-7bdf9e8e273a"; }
        public String privilege() { return "Task: mirebalais.checkinForm"; }
        public String description() { return "Ability to enter a Check In form from the patient dashboard."; }
    };

    public static PrivilegeDescriptor TASK_EMR_DEATH_CERTIFICATE_FORM = new PrivilegeDescriptor() {
        public String uuid() { return "c4121a43-895d-4c07-83f9-64e6907e3742"; }
        public String privilege() { return "Task: mirebalais.enterDeathCertificate"; }
        public String description() { return "Enter the Death Certificate form"; }
    };

    public static PrivilegeDescriptor TASK_DISPENSING_DISPENSE = new PrivilegeDescriptor() {
        public String uuid() { return "cc9bcfd5-2bc8-489f-85a7-fbd65e5a8293"; }
        public String privilege() { return "Task: mirebalais.dispensing"; }
        public String description() { return "Ability to dispense medicine."; }
    };

    public static PrivilegeDescriptor TASK_DISPENSING_EDIT = new PrivilegeDescriptor() {
        public String uuid() { return "d92637e3-8c03-4104-836a-f5bde8d8be47"; }
        public String privilege() { return "Task: mirebalaisDispensingEditProviderAndLocation"; }
        public String description() { return "Able to edit provide location and date of dispensing medication."; }
    };

    public static PrivilegeDescriptor TASK_ALLERGIES_MODIFY = new PrivilegeDescriptor() {
        public String uuid() { return "40fd2ed4-5244-478c-b5b3-9280b468b7bd"; }
        public String privilege() { return "Task: Modify Allergies"; }
        public String description() { return "Able to add, edit, delete allergies"; }
    };

    public static PrivilegeDescriptor TASK_RADIOLOGYAPP_ORDER_CT = new PrivilegeDescriptor() {
        public String uuid() { return "182974df-c322-4f13-a14e-a341a1fb3619"; }
        public String privilege() { return "Task: org.openmrs.module.radiologyapp.orderCT"; }
        public String description() { return "Rights to order a CT Scan via the radiology app"; }
    };

    public static PrivilegeDescriptor TASK_RADIOLOGYAPP_ORDER_US = new PrivilegeDescriptor() {
        public String uuid() { return "90ba15be-842d-4d11-969d-773958ec0ded"; }
        public String privilege() { return "Task: org.openmrs.module.radiologyapp.orderUS"; }
        public String description() { return "Privilege to order an ultrasound exam"; }
    };

    public static PrivilegeDescriptor TASK_RADIOLOGYAPP_ORDER_XRAY = new PrivilegeDescriptor() {
        public String uuid() { return "b5a20e50-6951-4eb8-92e3-d6011efc3b97"; }
        public String privilege() { return "Task: org.openmrs.module.radiologyapp.orderXray"; }
        public String description() { return "Rights to order an x-ray via the x-ray app"; }
    };

    public static PrivilegeDescriptor TASK_RADIOLOGYAPP_RETRO_ORDER = new PrivilegeDescriptor() {
        public String uuid() { return "d92e716b-9a41-41d8-97e9-5d8bde7c551b"; }
        public String privilege() { return "Task: org.openmrs.module.radiologyapp.retroOrder"; }
        public String description() { return "Ability to retrospectively order a radiology study"; }
    };

    public static PrivilegeDescriptor TASK_RADIOLOGYAPP_TAB = new PrivilegeDescriptor() {
        public String uuid() { return "15b126ce-9657-421f-a5be-19cea5573d3b"; }
        public String privilege() { return "Task: org.openmrs.module.radiologyapp.tab"; }
        public String description() { return "Ability to view the Radiology tab for patient"; }
    };

    public static PrivilegeDescriptor TASK_ED_TRIAGE_ENTER_NOTE = new PrivilegeDescriptor() {
        public String uuid() { return "8accc0c0-3faf-11e6-bdf4-0800200c9a66"; }
        public String privilege() { return "Task: edtriageapp.enterNote"; }
        public String description() { return "Ability to enter an ED Triage form fo a patient"; }
    };


    // RETIRED PRIVILEGES


    public static PrivilegeDescriptor RETIRED_APP_APPOINTMENTSCHEDULING_OLDUI = new PrivilegeDescriptor() {
        public String uuid() { return "bf179f22-d896-4692-8eac-f7b57a1aa6c7"; }
        public String privilege() { return "App: appointmentscheduling.oldUI"; }
        public String description() { return "Provides access to the old user interface for the appointment module"; }
    };

    public static PrivilegeDescriptor RETIRED_APP_APPOINTMENTSCHEDULINGUI_SCHEDULE_APPOINTMENT = new PrivilegeDescriptor() {
        public String uuid() { return "801f253b-eca3-4595-b3de-6d75d9b59666"; }
        public String privilege() { return "App: appointmentschedulingui.scheduleAppointment"; }
        public String description() { return "Access to the Schedule an Appointment app within the appointmentschedulingui module"; }
    };

    public static PrivilegeDescriptor RETIRED_APP_EMR_ACTIVE_VISITS = new PrivilegeDescriptor() {
        public String uuid() { return "ebf5f819-45ab-43c2-99ac-c5072a916c4a"; }
        public String privilege() { return "App: emr.activeVisits"; }
        public String description() { return "Run the Active Visits app"; }
    };

    public static PrivilegeDescriptor RETIRED_APP_ZL_REPORTS = new PrivilegeDescriptor() {
        public String uuid() { return "b910cc51-628e-4bda-9e41-b1bcdd78e06c"; }
        public String privilege() { return "App: mirebalaisreports.Reports"; }
        public String description() { return "Run the Reports app"; }
    };

    public static PrivilegeDescriptor RETIRED_TASK_EMR_PRINT_ID_CARD_LABEL = new PrivilegeDescriptor() {
        public String uuid() { return "f124da87-6b72-41d2-805a-d3007dace677"; }
        public String privilege() { return "Task: emr.printIdCardLabel"; }
        public String description() { return "Access the emr.printIdCardLabel task"; }
    };

    public static PrivilegeDescriptor RETIRED_TASK_EMR_PRINT_PAPER_RECORD_LABELS = new PrivilegeDescriptor() {
        public String uuid() { return "a43dd2e7-4f16-40fd-9aee-76563c9b44ac"; }
        public String privilege() { return "Task: emr.printPaperRecordLabel"; }
        public String description() { return "Access the emr.printPaperRecordLabel task"; }
    };

    public static PrivilegeDescriptor RETIRED_ENTER_CLINICAL_FORMS = new PrivilegeDescriptor() {
        public String uuid() { return "3f98d0c9-22ff-4d31-bad8-84a392420407"; }
        public String privilege() { return "Task: emr.enterClinicalForms"; }
        public String description() { return "Access the emr.consult task"; }
    };

    public static PrivilegeDescriptor RETIRED_ORDER_CT = new PrivilegeDescriptor() {
        public String uuid() { return "25686f11-96bf-4455-ab0d-8eab5f35ce12"; }
        public String privilege() { return "Task: emr.orderCTScan"; }
        public String description() { return "Access the emr.orderCTScan task"; }
    };

    public static PrivilegeDescriptor RETIRED_ORDER_US = new PrivilegeDescriptor() {
        public String uuid() { return "2c64d7129-3187-4428-94aa-b4f13997d821"; }
        public String privilege() { return "Task: emr.orderUltrasound"; }
        public String description() { return "Access the ask: emr.orderUltrasound task"; }
    };

    public static PrivilegeDescriptor RETIRED_ORDER_XRAY = new PrivilegeDescriptor() {
        public String uuid() { return "1dd0661b-fc19-4567-b691-1cc5a320afd3"; }
        public String privilege() { return "Task: emr.orderXray"; }
        public String description() { return "Access the emr.orderXray task"; }
    };

    public static PrivilegeDescriptor RETIRED_ORDER_XRAY_ANOTHER = new PrivilegeDescriptor() {
        public String uuid() { return "c8998d6b-f590-4786-ac43-cb367b3aca43"; }
        public String privilege() { return "Task: radiology.orderXray"; }
        public String description() { return "Access the radiology.orderXray task"; }
    };

}




