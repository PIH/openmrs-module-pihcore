package org.openmrs.module.pihcore.apploader;

import org.openmrs.module.allergyui.AllergyUIConstants;
import org.openmrs.module.coreapps.CoreAppsConstants;
import org.openmrs.module.pihcore.PihEmrConfigConstants;
import org.openmrs.module.registrationapp.RegistrationAppConstants;

import java.util.Arrays;
import java.util.List;

public class CustomAppLoaderConstants {

    public static final class Apps {

        // TODO would be nice to rename this to "pih.checkin"--would need to change CheckInAppWorkflowController in pihcore
        // TODO for checkin would need to fix requestRecord.gsp:69
        public static final String CHECK_IN = "mirebalais.liveCheckin";
        public static final String UHM_VITALS = "pih.uhm.app.vitals";
        public static final String VITALS = "pih.app.vitals";
        public static final String AWAITING_ADMISSION = CoreAppsConstants.AWAITING_ADMISSION;
        public static final String ACTIVE_VISITS = "pih.app.activeVisits";
        public static final String ARCHIVES_ROOM = "paperrecord.app.archivesRoom";
        public static final String SYSTEM_ADMINISTRATION = "coreapps.app.systemAdministration";
        public static final String APPOINTMENT_SCHEDULING_HOME = "appointmentschedulingui.app";
        public static final String DISPENSING = "dispensing.app";  // This is the legacy, form-based dispensing app
        public static final String MEDICATION_DISPENSING = "medicationDispensing.app";  // This is the new, MFE-based dispensing app
        public static final String APPOINTMENTS = "appointments.app"; // This is the new, MFE-based appointments app
        public static final String DISPENSING_SUMMARY = "pih.app.dispensing.summary";
        public static final String NOTES_SUMMARY = "pih.app.notes.summary";
        public static final String HIV_DISPENSING_SUMMARY = "pih.app.hiv.dispensing.summary";
        public static final String HIV_DIAGNOSES_SUMMARY = "pih.app.hiv.diagnoses.summary";
        public static final String HIV_ADVERSE_EFFECT = "pih.app.hiv.adverse.effect";
        public static final String HIV_STATUS_SUMMARY = "pih.app.hiv.status";
		public static final String HIV_NOTES_SUMMARY = "pih.app.hiv.notes.summary";
        public static final String VITALS_SUMMARY = "pih.app.vitals.summary";
        public static final String SCHEDULE_APPOINTMENT = "appointmentschedulingui.schedulingAppointmentApp";
        public static final String MY_ACCOUNT = "emr.myAccount";
        public static final String REPORTS = "reportingui.reports";
        public static final String INPATIENTS = "mirebalaisreports.inpatients";
        public static final String ACTIVE_VISITS_LIST = "mirebalaisreports.activeVisitsList";
        public static final String PATIENT_REGISTRATION = "registrationapp.registerPatient";
        public static final String CLINICIAN_DASHBOARD = "pih.app.clinicianDashboard";
        public static final String VISITS_SUMMARY = "coreapps.clinicianfacing.visits";
        public static final String HOME_VISITS_SUMMARY = "coreapps.home.visits";
        public static final String WAITING_FOR_CONSULT = "pih.app.waitingForConsult";
        public static final String CHW_MGMT = "chw.app.mgmt";
        public static final String ED_TRIAGE = "edtriageapp.app.edTriage";
        public static final String ED_TRIAGE_QUEUE = "edtriageapp.app.triageQueue";
        public static final String EPILEPSY = "pihcore.ncd.epilepsy";
        public static final String EPILEPSY_INTAKE = "pihcore.ncd.epilepsyIntake";
        public static final String EPILEPSY_SUMMARY = "pih.app.epilepsy.summary";
        public static final String EPILEPSY_SEIZURES = "pih.app.epilepsy.seizures";
        public static final String TODAYS_VISITS = "pih.app.todaysVisits";
        public static final String PATHOLOGY_TRACKING = "labtracking.app.monitorOrders";
        public static final String LAB_TRACKING = "pih.app.labtracking";
        public static final String LABS = "pih.app.labs.label";
        public static final String ORDER_LABS = "pih.app.labs.ordering";
        public static final String PROGRAMS_LIST = "coreapps.app.programsList";
        public static final String RELATIONSHIPS_REGISTRATION_SUMMARY = "pih.app.relationships.registrationSummary";
        public static final String PROVIDER_RELATIONSHIPS_REGISTRATION_SUMMARY = "pih.app.relationships.providers.registrationSummary";
        public static final String RELATIONSHIPS_CLINICAL_SUMMARY = "pih.app.relationships.clinicalSummary";
        public static final String PROVIDER_RELATIONSHIPS_CLINICAL_SUMMARY = "pih.app.relationships.providers.clinicalSummary";
        public static final String PROGRAM_SUMMARY_LIST = "pih.app.programSummaryList";
        public static final String HIV_SUMMARY = "pih.app.hiv.summary";
        public static final String HIV_NEXT_DISPENSING = "pih.app.hiv.next.dispensing";
        public static final String HIV_ALERTS = "pih.app.hiv.alerts";
        public static final String HIV_TB_STATUS = "pih.app.hivTb.status";
        public static final String HIV_VIRAL_LOAD_HISTORY = "pih.app.hiv.viralLoadHistory";
        public static final String HIV_INTAKE_ENCOUNTERS = "pih.app.hiv.encounters.intake";
        public static final String HIV_FOLLOWUP_ENCOUNTERS = "pih.app.hiv.encounters.followup";
        public static final String HIV_DISPENSING_ENCOUNTERS = "pih.app.hiv.dispensing.followup";
        public static final String HIV_SOCIOECONOMIC_ENCOUNTERS = "pih.app.hiv.socioeconomic.followup";
        public static final String HIV_CD4_GRAPH = "pih.app.hiv.cd4Graph";
        public static final String HIV_VL_GRAPH = "pih.app.hiv.vlGraph";
        public static final String HIV_WEIGHT_GRAPH = "pih.app.hiv.weightGraph";
        public static final String HIV_OBS_CHART = "pih.app.hiv.obsChart";
        public static final String HIV_CONDITION_LIST = "pih.app.hiv.conditionList";
        public static final String HIV_VISIT_SUMMARY = "pih.app.hiv.visitSummary";
        public static final String HIV_LAST_VITALS = "pih.app.hiv.lastVitals";
        public static final String HIV_DISPENSING = "pih.app.hiv.dispensing";
        public static final String PATIENT_DOCUMENTS = "attachments.app.patientDocuments";
        public static final String CONDITION_LIST = "coreapps.conditionList";
        public static final String ACTIVE_DRUG_ORDERS = "coreapps.activeDrugOrders";
        public static final String BLOOD_PRESSURE_GRAPH = "pih.app.bloodPressure.graph";
        public static final String BLOOD_PRESSURE_OBS_TABLE = "pih.app.bloodPressure.obsTable";
        public static final String ASTHMA_SYMPTOMS_OBS_TABLE = "pih.app.asthma.symptomsObsTable";
        public static final String GLUCOSE_GRAPH = "pih.app.glucoseGraph";
        public static final String HBA1C_GRAPH = "pih.app.hba1cGraph";
        public static final String HEAD_CIRCUMFERENCE_GRAPH = "pih.app.headCircumferenceGraph";
        public static final String BMI_GRAPH = "pih.app.bmiGraph";
        public static final String ABDOMINAL_CIRCUMFERENCE_GRAPH = "pih.app.abdominalCircumferenceGraph";
        public static final String FOOT_EXAM_OBS_TABLE = "pih.app.diabetes.footExamObsTable";
        public static final String URINARY_ALBUMIN_OBS_TABLE = "pih.app.diabetes.urinaryAlbuminObsTable";
        public static final String ALC_TOBAC_USE_SUMMARY = "pih.app.alcTobacUse.summary";
        public static final String CHOLESTEROL_GRAPH = "pih.app.cholesterol.graph";
        public static final String PHQ9_GRAPH = "pih.app.phq9.graph";
        public static final String GAD7_GRAPH = "pih.app.gad7.graph";
        public static final String WHODAS_GRAPH = "pih.app.whodas.graph";
        public static final String ZLDSI_GRAPH = "pih.app.zldsi.graph";
        public static final String SEIZURE_FREQUENCY_GRAPH = "pih.app.seizure.frequency.graph.title";
        public static final String CGI_I_GRAPH = "pih.app.cgi_i.graph";
        public static final String J9_REFERRALS = "pih.app.j9Referrals";
        public static final String COVID_LAB_RESULTS = "pih.app.covidLabResults";
        public static final String ALL_LAB_RESULTS = "pih.app.allLabResults";
        public static final String ADD_LAB_RESULTS = "pih.app.addLabResults";

        public static final String MANAGE_ACCOUNTS = "emr.account.manageAccounts";
        public static final String PRINTER_ADMINISTRATION = "printer.printerAdministration";
        public static final String MERGE_PATIENTS = "coreapps.mergePatients";
        public static final String FEATURE_TOGGLES = "pih.featureToggles";
        public static final String PATIENT_EXPORT = "pih.exportPatients";
        public static final String PATIENT_IMPORT = "pih.importPatients";
        public static final String LEGACY_ADMINISTRATION = "pih.app.admin.legacyAdmin";
        public static final String SYSTEM_CONFIGURATION = "pih.app.admin.configuration";
        public static final String STATUS_DATA_VIEW = "pih.app.admin.statusData.view";
        public static final String STATUS_DATA_ADMIN = "pih.app.admin.statusData.admin";
        public static final String EMAIL_CONFIG_ADMIN = "pih.app.admin.email.admin";
        public static final String ACTIVE_USERS_ADMIN = "pih.app.admin.activeUsers.admin";

        public static final String ADDITIONAL_IDENTIFIERS = "registrationapp.additionalIdentifiers";
        public static final String MOST_RECENT_VITALS = "coreapps.mostRecentVitals";
        public static final String MOST_RECENT_REGISTRATION = "coreapps.mostRecentRegistration";
        public static final String MOST_RECENT_REGISTRATION_SUMMARY = "coreapps.mostRecentRegistrationSummary";
        public static final String MOST_RECENT_REGISTRATION_SOCIAL = "coreapps.mostRecentRegistrationSocial";
        public static final String MOST_RECENT_REGISTRATION_EBOLA_SCREENING = "mirebalais.mostRecentRegistrationEbolaScreening";
        public static final String MOST_RECENT_REGISTRATION_INSURANCE = "coreapps.mostRecentRegistrationInsurance";
        public static final String MOST_RECENT_REGISTRATION_CONTACT = "coreapps.mostRecentRegistrationContact";
        public static final String MOST_RECENT_REGISTRATION_LOCAL_ADDRESS = "coreapps.mostRecentRegistrationLocalAddress";
        public static final String MOST_RECENT_REGISTRATION_PATIENT_SUPPORT = "coreapps.mostRecentRegistrationPatientSupport";
        public static final String MOST_RECENT_CHECK_IN = "coreapps,mostRecentCheckIn";
        public static final String ALLERGY_SUMMARY = "allergyui.allergySummary";
        public static final String PATHOLOGY_SUMMARY = "labtrackingapp.labSummary";
        public static final String ID_CARD_PRINTING_STATUS = "mirebalais.idCardPrintingStatus";
        public static final String BIOMETRICS_SUMMARY = "registrationapp.biometricsSummary";

        public static final String RADIOLOGY_APP = "radiology.app";
        public static final String RADIOLOGY_ORDERS_APP = "radiology.orders.app";

        public static final String COHORT_BUILDER_APP = "cohortBuilder.app";

        public static final String PERU_LAB_ORDERS_ANALYSIS_REQUESTS = "peru.labOrdersAnalysisRequests.app";

        public static final String SPA_PREVIEW_HOME = "spa.preview.home";

        public static final String RECENT_LAB_RESULTS = "pih.app.lab.results";
    }

    public static final class Extensions {

        public static final String CHECK_IN_VISIT_ACTION = "pih.checkin.visitAction";
        public static final String CHECK_IN_MATERNAL_VISIT_ACTION = "pih.checkinMaternal.visitAction";
        public static final String CHECK_IN_REGISTRATION_ACTION = "pih.checkin.registrationAction";
        public static final String CHECK_IN_MATERNAL_REGISTRATION_ACTION = "pih.checkinMaternal.registrationAction";
        public static final String VITALS_CAPTURE_VISIT_ACTION = "pih.form.vitals";
        public static final String CONSULT_NOTE_VISIT_ACTION = "pih.form.consult";
        public static final String CONSULT_NOTE_INITIAL_VISIT_ACTION = "pih.form.consultInitial";
		public static final String NURSE_CONSULT_NOTE_VISIT_ACTION = "pih.form.nurseConsult";
        public static final String ADMISSION_NOTE_VISIT_ACTION = "pih.form.admission";
        public static final String DISPENSE_MEDICATION_VISIT_ACTION = "dispensing.form";
        public static final String ED_CONSULT_NOTE_VISIT_ACTION = "pih.form.edConsult";
        public static final String SURGICAL_NOTE_VISIT_ACTION = "pih.form.surgicalNote";
        public static final String ONCOLOGY_CONSULT_NOTE_VISIT_ACTION = "pih.form.oncologyNote";
        public static final String ONCOLOGY_INITIAL_VISIT_ACTION = "pih.form.oncologyIntake";
        public static final String ONCOLOGY_TREATMENT_PLAN_ACTION = "pih.form.oncologyTreatmentPlan";
        public static final String CHEMOTHERAPY_VISIT_ACTION = "pih.form.chemotherapy";
        public static final String LAB_RESULTS_OVERALL_ACTION = "pih.form.labResults";
        public static final String NCD_INITIAL_VISIT_ACTION = "pih.form.ncdAdultInitial";
        public static final String NCD_FOLLOWUP_VISIT_ACTION = "pih.form.ncdAdultFollowup";
        public static final String ECHO_VISIT_ACTION = "pih.form.echoConsult";
        public static final String VACCINATION_VISIT_ACTION = "pih.form.vaccination";
        public static final String COMMENT_VISIT_ACTION = "pih.form.comment";

        public static final String COVID19_INITIAL_VISIT_ACTION = "pih.form.covid19Admission";
        public static final String COVID19_FOLLOWUP_VISIT_ACTION = "pih.form.covid19Progress";
        public static final String COVID19_DISCHARGE_VISIT_ACTION = "pih.form.covid19Discharge";

        public static final String HIV_ZL_INITIAL_VISIT_ACTION = "pih.form.hivZLAdultInitial";
        public static final String HIV_ZL_FOLLOWUP_VISIT_ACTION = "pih.form.hivZLAdultFollowup";
        public static final String HIV_ZL_DISPENSING_VISIT_ACTION = "pih.form.hivZLDispensing";
        public static final String HIV_MEDICATION_LIST_OVERALL_ACTION = "pih.hiv.medicationList";

        public static final String HIV_PSYCHOSOCIAL_VISIT_ACTION = "pih.form.hivPsychosocial";
        public static final String PMTCT_INITIAL_VISIT_ACTION = "pih.form.pmtctInitial";
        public static final String PMTCT_FOLLOWUP_VISIT_ACTION = "pih.form.pmtctFollowup";
        public static final String EID_FOLLOWUP_VISIT_ACTION = "pih.form.eidFollowup";
        public static final String OVC_INITIAL_VISIT_ACTION = "pih.form.ovcInitial";
        public static final String OVC_FOLLOWUP_VISIT_ACTION = "pih.form.ovcFollowup";

        public static final String TB_INITIAL_VISIT_ACTION = "pih.form.tbInitial";

        public static final String HIV_ADULT_INITIAL_VISIT_ACTION = "pih.form.hivAdultInitial";
        public static final String HIV_ADULT_FOLLOWUP_VISIT_ACTION = "pih.form.hivAdultFollowup";
        public static final String HIV_PEDS_INITIAL_VISIT_ACTION = "pih.form.hivPedsInitial";
        public static final String HIV_PEDS_FOLLOWUP_VISIT_ACTION = "pih.form.hivPedsFollowup";
        public static final String HIV_ADHERENCE_VISIT_ACTION = "pih.form.hivAdherence";
        public static final String MCH_ANC_INTAKE_VISIT_ACTION = "pih.form.ancIntake";
        public static final String MCH_ANC_FOLLOWUP_VISIT_ACTION = "pih.form.ancFollowup";
        public static final String MCH_PEDS_ACTION = "pih.form.peds";
        public static final String MCH_DELIVERY_VISIT_ACTION = "pih.form.delivery";
        public static final String MCH_GAIN_DELIVERY_REGISTER_ACTION = "pih.form.gainDelivery";
        public static final String MCH_GAIN_SCBU_REGISTER_ACTION = "pih.form.gainNewbornSCBU";
        public static final String NEWBORN_ASSESSMENT_ACTION = "pih.form.newbornAssessment";
        public static final String NEWBORN_DAILY_PROGRESS_ACTION = "pih.form.newbornDailyProgress";
        public static final String NEWBORN_REFERRAL_ACTION = "pih.form.newbornReferral";
        public static final String NEWBORN_OBS_ACTION = "pih.form.newbornObs";
        public static final String MATERNAL_DISCHARGE_ACTION = "pih.form.maternalDischarge";
        public static final String LABOR_PROGRESS_ACTION = "pih.form.laborProgress";
        public static final String LABOR_DELIVERY_SUMMARY_ACTION = "pih.form.laborDeliverySummary";
        public static final String OB_GYN_VISIT_ACTION = "pih.form.obGyn";
        public static final String MENTAL_HEALTH_VISIT_ACTION = "pih.form.mentalHealth";
        public static final String MENTAL_HEALTH_INTAKE_VISIT_ACTION = "pih.form.mentalHealthIntake";
        public static final String MENTAL_HEALTH_FOLLOWUP_VISIT_ACTION = "pih.form.mentalHealthFollowup";
        public static final String EPILEPSY_VISIT_ACTION = "pih.form.epilepsy";
        public static final String EPILEPSY_INTAKE_VISIT_ACTION = "pih.form.epilepsyIntake";
        public static final String VCT_VISIT_ACTION = "pih.form.vct";
        public static final String SOCIO_ECONOMICS_VISIT_ACTION = "pih.form.socioEconomics";
        public static final String ORDER_XRAY_VISIT_ACTION = "radiologyapp.orderXray";
        public static final String ORDER_CT_VISIT_ACTION = "radiologyapp.orderCT";
        public static final String ORDER_ULTRASOUND_VISIT_ACTION = "radiologyapp.orderUS";
        public static final String REGISTRATION_SUMMARY_OVERALL_ACTION = "registrationapp.registrationSummary.link";
        public static final String PRIMARY_CARE_PEDS_INITIAL_VISIT_ACTION = "pih.primaryCare.pedsInitial";
        public static final String PRIMARY_CARE_PEDS_FOLLOWUP_VISIT_ACTION = "pih.primaryCare.pedsFollowup";
        public static final String PRIMARY_CARE_ADULT_INITIAL_VISIT_ACTION = "pih.primaryCare.adultInitial";
        public static final String PRIMARY_CARE_ADULT_FOLLOWUP_VISIT_ACTION = "pih.primaryCare.adultFollowup";
        public static final String PRESCRIPTION_VISIT_ACTION = "pih.form.prescription";
        public static final String ED_TRIAGE_VISIT_ACTION = "edtriageapp.edTriageNote";
        public static final String ORDER_LAB_VISIT_ACTION = "labtrackingapp.orderLab";
        public static final String CHEMO_ORDERING_VISIT_ACTION = "oncology.orderChemo";
        public static final String CHEMO_RECORDING_VISIT_ACTION = "oncology.recordChemo";
        public static final String MEXICO_CONSULT_ACTION = "pih.mexicoConsult";
        public static final String MEXICO_CLINICAL_HISTORY_ACTION = "pih.mexicoClinicalHistory";
        public static final String SIERRA_LEONE_OUTPATIENT_INITIAL_VISIT_ACTION = "pih.sierraLeone.outpatient.initial";
        public static final String SIERRA_LEONE_OUTPATIENT_FOLLOWUP_VISIT_ACTION = "pih.sierraLeone.outpatient.followup";

        public static final String REHAB_VISIT_ACTION = "pih.form.rehab";
        public static final String SECOND_LINE_HEADER = "patientHeader.secondLineFragments";
        public static final String ADMISSION_FORM_AWAITING_ADMISSION_ACTION = "pih.form.admit";
        public static final String DENY_ADMISSION_FORM_AWAITING_ADMISSION_ACTION = "pih.form.deny";
        public static final String HIV_PROGRAM_LOCATION_WARNING_HEADER_EXTENSION = "pih.header.hivProgramLocationWarning";
        public static final String ENCOUNTER_LIST_OVERALL_ACTION = "pihcore.encounterList";
        public static final String REQUEST_PAPER_RECORD_OVERALL_ACTION = "paperrecord.requestPaperRecord";
        public static final String REQUEST_APPOINTMENT_OVERALL_ACTION = "appointmentschedulingui.requestAppointment";
        public static final String SCHEDULE_APPOINTMENT_OVERALL_ACTION = "appointmentschedulingui.scheduleAppointment";
        public static final String PRINT_ID_CARD_OVERALL_ACTION = "paperrecord.printIdCardLabel";
        public static final String PRINT_PAPER_FORM_LABEL_OVERALL_ACTION = "paperrecord.printPaperFormLabel";
        public static final String PRINT_WRISTBAND_OVERALL_ACTION = "pih.wristband.print";
        public static final String CREATE_VISIT_OVERALL_ACTION = "coreapps.createVisit";
        public static final String CREATE_HIV_VISIT_OVERALL_ACTION = "coreapps.hiv.createVisit";
        public static final String CREATE_RETROSPECTIVE_VISIT_OVERALL_ACTION = "coreapps.createRetrospectiveVisit";
        public static final String MERGE_VISITS_OVERALL_ACTION = "coreapps.mergeVisits";
        public static final String DEATH_CERTIFICATE_OVERALL_ACTION = "pih.haiti.deathCertificate";
        public static final String CHART_SEARCH_OVERALL_ACTION = "chartsearch.overallAction";
        public static final String PATIENT_DOCUMENTS_OVERALL_ACTION = "attachments.patientDocuments.overallAction";
        public static final String ORDER_LABS_OVERALL_ACTION = "orderentryowa.orderLabs";
        public static final String VIEW_LABS_OVERALL_ACTION = "labworkflowowa.viewLabs";
        public static final String VIEW_GROWTH_CHART_ACTION = "growthchart.viewChart";
        public static final String MARK_PATIENT_DEAD_OVERALL_ACTION = "coreapps.markPatientDied";

        public static final String PAPER_RECORD_ACTIONS_INCLUDES = "paperrecord.patientDashboard.includes";
        public static final String PRINT_WRISTBAND_ACTION_INCLUDES = "pih.wristband.patientDashboard.includes";
        public static final String VISIT_ACTIONS_INCLUDES = "coreapps.patientDashboard.includes";

        public static final String RADIOLOGY_TAB = "radiologyapp.tab";
        public static final String APPOINTMENTS_TAB = "appointmentschedulingui.tab";

        public static final String EDIT_PATIENT_CONTACT_INFO = "registrationapp.editPatientContactInfo";
        public static final String EDIT_PATIENT_DEMOGRAPHICS = "registrationapp.editPatientDemographics";
        public static final String CLINICIAN_FACING_PATIENT_DASHBOARD = "coreapps.clinicianFacingPatientDashboardApp";
        public static final String REGISTER_NEW_PATIENT = "registrationapp.registerNewPatient";
        public static final String MERGE_INTO_ANOTHER_PATIENT = "registrationapp.mergePatient";
        public static final String PRINT_PAPER_FORM_LABEL = "registrationapp.printPaperFormLabel";
        public static final String PRINT_ID_CARD_REGISTRATION_ACTION = "mirebalais.printIdCard";
        public static final String VISITS_DASHBOARD = "coreapps.visitsDashboard";
        public static final String BIOMETRICS_FIND_PATIENT = "biometrics.findPatient";

        public static final String PIH_HEADER_EXTENSION = "pih.header";

        public static final String PIH_AUTH_ADMIN_EXTENSION = "pih.authenticationui.admin";
        public static final String PIH_AUTH_LOGIN_EXTENSION = "pih.authenticationui.login";

        public static final String DEATH_CERTIFICATE_HEADER_EXTENSION = "pih.header.deathCertificate";

        public static final String REPORTING_AD_HOC_ANALYSIS = "reportingui.dataExports.adHoc";

        public static final String ALLERGY_UI_VISIT_NOTE_NEXT_SUPPORT = "allergyui.allergires.visitNoteNextSupport";

        // Reports
        public static final String REGISTRATION_SUMMARY_BY_AGE_REPORT = "mirebalaisreports.overview.registrationsByAge";
        public static final String CHECK_IN_SUMMARY_BY_AGE_REPORT = "mirebalaisreports.overview.checkinsByAge";

        public static final String DAILY_INPATIENTS_OVERVIEW_REPORT = "mirebalaisreports.overview.inpatientDaily";


        public static final String NON_CODED_DIAGNOSES_DATA_QUALITY_REPORT = "mirebalaisreports.dataQuality.nonCodedDiagnoses";

        public static final String SPA_PREVIEW_PATIENT_CHART = "spa.preview.patientChart";

        public static final String HAITI_HIV_EMR_DASHBOARD_LINK = "patientHeader.linkToHivEmr";

    }

    public static final class ExtensionPoints {
        public static final String OVERALL_ACTIONS = "patientDashboard.overallActions";
        public static final String OVERALL_REGISTRATION_ACTIONS = "registrationSummary.overallActions";
        public static final String VISIT_ACTIONS = "patientDashboard.visitActions";
        public static final String AWAITING_ADMISSION_ACTIONS = "coreapps.app.awaitingAdmissionActions";
        public static final String ENCOUNTER_TEMPLATE = "org.openmrs.referenceapplication.encounterTemplate";
        public static final String HOME_PAGE = "org.openmrs.referenceapplication.homepageLink";
        public static final String PROGRAM_SUMMARY_LIST = Apps.PROGRAM_SUMMARY_LIST + ".apps";
        public static final String DEATH_INFO_HEADER = "patientHeader.deathInfo";
        public static final String SECOND_LINE_HEADER = "patientHeader.secondLineFragments";
        public static final String DASHBOARD_TAB = "patientDashboard.tabs";
        public static final String DASHBOARD_INCLUDE_FRAGMENTS = "patientDashboard.includeFragments";
        public static final String SYSTEM_ADMINISTRATION_PAGE = "systemAdministration.apps";
        public static final String REPORTING_DATA_EXPORT = "org.openmrs.module.reportingui.reports.dataexport";
        public static final String REPORTING_OVERVIEW_REPORTS = "org.openmrs.module.reportingui.reports.overview";
        public static final String REPORTING_DATA_QUALITY = "org.openmrs.module.reportingui.reports.dataquality";
        public static final String REPORTING_MONITORING = "org.openmrs.module.reportingui.reports.monitoring";
        public static final String PATIENT_HEADER_PATIENT_CONTACT_INFO = "patientHeader.editPatientContactInfo";
        public static final String PATIENT_HEADER_PATIENT_DEMOGRAPHICS = "patientHeader.editPatientDemographics";
        public static final String CLINICIAN_DASHBOARD_FIRST_COLUMN = "patientDashboard.firstColumnFragments";
        public static final String CLINICIAN_DASHBOARD_SECOND_COLUMN = "patientDashboard.secondColumnFragments";
        public static final String REGISTRATION_SUMMARY_CONTENT = "registrationSummary.contentFragments";
        public static final String REGISTRATION_SUMMARY_SECOND_COLUMN_CONTENT = "registrationSummary.secondColumnContentFragments";
        public static final String REGISTRATION_FIND_PATIENT_FRAGMENTS = RegistrationAppConstants.FIND_PATIENT_FRAGMENTS_EXTENSION_POINT;
        public static final String ALLERGIES_PAGE_INCLUDE_PAGE = AllergyUIConstants.ALLERGIES_PAGE_INCLUDE_FRAGMENT_EXTENSION_POINT;
        public static final String PATIENT_SEARCH = "coreapps.patientSearch.extension";
    }

    // TODO are these still used once we switch to the new visit dashboard?
    public static final class EncounterTemplates {
        public static final String DEFAULT = "defaultEncounterTemplate";
        public static final String CONSULT = "consultEncounterTemplate";
        public static final String NO_DETAILS = "noDetailsEncounterTemplate";
        public static final String ED_TRIAGE = "edtriageEncounterTemplate";
    }

    // order of lists define the order apps and extensions appear
    public static final List<String> HOME_PAGE_APPS_ORDER = Arrays.asList(
            Apps.ACTIVE_VISITS,
            Apps.PATIENT_REGISTRATION,
            Apps.CHECK_IN,
            Apps.AWAITING_ADMISSION,
            Apps.UHM_VITALS,
            Apps.VITALS,
            Apps.WAITING_FOR_CONSULT,
            Apps.TODAYS_VISITS,
            Apps.APPOINTMENT_SCHEDULING_HOME,
            Apps.ARCHIVES_ROOM,
            Apps.INPATIENTS,
            Apps.HIV_DISPENSING,
            Apps.LABS,
            Apps.PATHOLOGY_TRACKING,
            Apps.ADD_LAB_RESULTS,
            Apps.PERU_LAB_ORDERS_ANALYSIS_REQUESTS,
            Apps.PROGRAM_SUMMARY_LIST,
            Apps.REPORTS,
            Apps.DISPENSING,
            Apps.MEDICATION_DISPENSING,
            Apps.ED_TRIAGE,
            Apps.ED_TRIAGE_QUEUE,
            Apps.CHW_MGMT,
            Apps.COHORT_BUILDER_APP,
            Apps.J9_REFERRALS,
            Apps.MY_ACCOUNT,
            Apps.SYSTEM_ADMINISTRATION,
            Apps.SPA_PREVIEW_HOME);

    // The idiosyncratic ordering of these items is due to the fact that the ones used
    // in English and French -speaking places are alphebetized in English and the ones
    // used in Spanish-speaking places are alphebetized in Spanish.
    public static final List<String> PROGRAM_SUMMARY_LIST_APPS_ORDER = Arrays.asList(
            "pih.app." + PihEmrConfigConstants.PROGRAM_ASTHMA_UUID + ".programSummary.dashboard",
            "pih.app." + PihEmrConfigConstants.PROGRAM_MALNUTRITION_UUID + ".programSummary.dashboard",
            "pih.app." + PihEmrConfigConstants.PROGRAM_DIABETES_UUID + ".programSummary.dashboard",
            "pih.app." + PihEmrConfigConstants.PROGRAM_EPILEPSY_UUID + ".programSummary.dashboard",
            "pih.app." + PihEmrConfigConstants.PROGRAM_HIV_UUID + ".programSummary.dashboard",
            "pih.app." + PihEmrConfigConstants.PROGRAM_EID_UUID + ".programSummary.dashboard",
            "pih.app." + PihEmrConfigConstants.PROGRAM_HYPERTENSION_UUID + ".programSummary.dashboard",
            "pih.app." + PihEmrConfigConstants.PROGRAM_ANC_UUID + ".programSummary.dashboard",
            "pih.app." + PihEmrConfigConstants.PROGRAM_MCH_UUID + ".programSummary.dashboard",
            "pih.app." + PihEmrConfigConstants.PROGRAM_MENTALHEALTH_UUID + ".programSummary.dashboard",
            "pih.app." + PihEmrConfigConstants.PROGRAM_NCD_UUID + ".programSummary.dashboard",
            "pih.app." + PihEmrConfigConstants.PROGRAM_ONCOLOGY_UUID + ".programSummary.dashboard",
            "pih.app." + PihEmrConfigConstants.PROGRAM_COVID19_UUID + ".programSummary.dashboard",
            "pih.app." + PihEmrConfigConstants.PROGRAM_ZIKA_UUID + ".programSummary.dashboard"
    );

    public static final List<String> SYSTEM_ADMINISTRATION_APPS_ORDER = Arrays.asList(
            Apps.MANAGE_ACCOUNTS,
            Apps.PRINTER_ADMINISTRATION,
            Apps.MERGE_PATIENTS,
            Apps.FEATURE_TOGGLES);

    public static final List<String> OVERALL_ACTIONS_ORDER = Arrays.asList(
            Extensions.ENCOUNTER_LIST_OVERALL_ACTION,
            Extensions.CREATE_VISIT_OVERALL_ACTION,
            Extensions.CREATE_RETROSPECTIVE_VISIT_OVERALL_ACTION,
            Extensions.VIEW_GROWTH_CHART_ACTION,
            Extensions.HIV_MEDICATION_LIST_OVERALL_ACTION,
            Extensions.ORDER_LABS_OVERALL_ACTION,
            Extensions.VIEW_LABS_OVERALL_ACTION,
            Extensions.LAB_RESULTS_OVERALL_ACTION,
            Extensions.REQUEST_PAPER_RECORD_OVERALL_ACTION,
            Extensions.PRINT_PAPER_FORM_LABEL_OVERALL_ACTION,
            Extensions.PRINT_ID_CARD_OVERALL_ACTION,
            Extensions.PRINT_WRISTBAND_OVERALL_ACTION,
            Extensions.REQUEST_APPOINTMENT_OVERALL_ACTION,
            Extensions.SCHEDULE_APPOINTMENT_OVERALL_ACTION,
            Extensions.MERGE_VISITS_OVERALL_ACTION,
            Extensions.REGISTRATION_SUMMARY_OVERALL_ACTION,
            Extensions.DEATH_CERTIFICATE_OVERALL_ACTION,
            Extensions.PATIENT_DOCUMENTS_OVERALL_ACTION,
            Extensions.CHEMO_ORDERING_VISIT_ACTION,
            Extensions.EDIT_PATIENT_DEMOGRAPHICS,
            Extensions.EDIT_PATIENT_CONTACT_INFO,
            Extensions.CHART_SEARCH_OVERALL_ACTION,
            Extensions.MARK_PATIENT_DEAD_OVERALL_ACTION,
            Extensions.SPA_PREVIEW_PATIENT_CHART);  // TODO remember to permission chart search in Custom App Loader Factory

    public static final List<String> VISIT_ACTIONS_ORDER = Arrays.asList(
            Extensions.CHECK_IN_VISIT_ACTION,
            Extensions.CHECK_IN_MATERNAL_VISIT_ACTION,
            Extensions.VITALS_CAPTURE_VISIT_ACTION,
            Extensions.CONSULT_NOTE_VISIT_ACTION,
            Extensions.MEXICO_CONSULT_ACTION,
            Extensions.ADMISSION_NOTE_VISIT_ACTION, // ToDo:  Don't think this is in use from action list
            Extensions.PRIMARY_CARE_ADULT_INITIAL_VISIT_ACTION,
            Extensions.PRIMARY_CARE_ADULT_FOLLOWUP_VISIT_ACTION,
            Extensions.PRIMARY_CARE_PEDS_INITIAL_VISIT_ACTION,
            Extensions.PRIMARY_CARE_PEDS_FOLLOWUP_VISIT_ACTION,
			Extensions.NURSE_CONSULT_NOTE_VISIT_ACTION,
            Extensions.PRESCRIPTION_VISIT_ACTION,
            Extensions.VACCINATION_VISIT_ACTION,
            Extensions.ED_TRIAGE_VISIT_ACTION,
            Extensions.ED_CONSULT_NOTE_VISIT_ACTION,
            Extensions.DISPENSE_MEDICATION_VISIT_ACTION,
            Extensions.SURGICAL_NOTE_VISIT_ACTION,
            Extensions.COMMENT_VISIT_ACTION,
            Extensions.NCD_INITIAL_VISIT_ACTION,
            Extensions.NCD_FOLLOWUP_VISIT_ACTION,
            Extensions.ECHO_VISIT_ACTION,
            Extensions.MCH_ANC_INTAKE_VISIT_ACTION,
            Extensions.MCH_ANC_FOLLOWUP_VISIT_ACTION,
            Extensions.MCH_DELIVERY_VISIT_ACTION,
            Extensions.MCH_GAIN_DELIVERY_REGISTER_ACTION,
            Extensions.MCH_GAIN_SCBU_REGISTER_ACTION,
            Extensions.NEWBORN_ASSESSMENT_ACTION,
            Extensions.NEWBORN_DAILY_PROGRESS_ACTION,
            Extensions.MATERNAL_DISCHARGE_ACTION,
            Extensions.LABOR_PROGRESS_ACTION,
            Extensions.LABOR_DELIVERY_SUMMARY_ACTION,
            Extensions.NEWBORN_OBS_ACTION,
            Extensions.NEWBORN_REFERRAL_ACTION,
            Extensions.OB_GYN_VISIT_ACTION,
            Extensions.MENTAL_HEALTH_VISIT_ACTION,
            Extensions.MENTAL_HEALTH_INTAKE_VISIT_ACTION,
            Extensions.EPILEPSY_INTAKE_VISIT_ACTION,
            Extensions.EPILEPSY_VISIT_ACTION,
            Extensions.MENTAL_HEALTH_FOLLOWUP_VISIT_ACTION,
            Extensions.SOCIO_ECONOMICS_VISIT_ACTION,
            Extensions.VCT_VISIT_ACTION,
            Extensions.HIV_ZL_INITIAL_VISIT_ACTION,
            Extensions.HIV_ZL_FOLLOWUP_VISIT_ACTION,
            Extensions.HIV_ZL_DISPENSING_VISIT_ACTION,
            Extensions.HIV_PSYCHOSOCIAL_VISIT_ACTION,
            Extensions.HIV_ADHERENCE_VISIT_ACTION,
            Extensions.PMTCT_INITIAL_VISIT_ACTION,
            Extensions.PMTCT_FOLLOWUP_VISIT_ACTION,
            Extensions.EID_FOLLOWUP_VISIT_ACTION,
            Extensions.ONCOLOGY_INITIAL_VISIT_ACTION,
            Extensions.ONCOLOGY_CONSULT_NOTE_VISIT_ACTION,
            Extensions.ONCOLOGY_TREATMENT_PLAN_ACTION,
            Extensions.CHEMOTHERAPY_VISIT_ACTION,
            Extensions.CHEMO_RECORDING_VISIT_ACTION,
            Extensions.COVID19_INITIAL_VISIT_ACTION,
            Extensions.COVID19_FOLLOWUP_VISIT_ACTION,
            Extensions.COVID19_DISCHARGE_VISIT_ACTION,
            Extensions.ORDER_XRAY_VISIT_ACTION,
            Extensions.ORDER_CT_VISIT_ACTION,
            Extensions.ORDER_ULTRASOUND_VISIT_ACTION,
            Extensions.ORDER_LAB_VISIT_ACTION,
            Extensions.REHAB_VISIT_ACTION);

    public static final List<String> HIV_VISIT_ACTIONS_ORDER = Arrays.asList(
            Extensions.HIV_ZL_INITIAL_VISIT_ACTION + ".hiv",
            Extensions.HIV_ZL_FOLLOWUP_VISIT_ACTION + ".hiv",
            Extensions.HIV_ZL_DISPENSING_VISIT_ACTION + ".hiv",
            Extensions.VITALS_CAPTURE_VISIT_ACTION + ".hiv");

    public static final List<String> ONCOLOGY_VISIT_ACTIONS_ORDER = Arrays.asList(
            Extensions.CHEMO_RECORDING_VISIT_ACTION + "oncology"
    );

    public static final List<String> ONCOLOGY_OVERALL_ACTIONS_ORDER = Arrays.asList(
            Extensions.CHEMO_ORDERING_VISIT_ACTION + ".oncology"
    );

    public static final List<String> AWAITING_ADMISSION_ACTIONS_ORDER = Arrays.asList(
            Extensions.ADMISSION_FORM_AWAITING_ADMISSION_ACTION,
            Extensions.DENY_ADMISSION_FORM_AWAITING_ADMISSION_ACTION);

    public static final List<String> CLINICIAN_DASHBOARD_FIRST_COLUMN_ORDER = Arrays.asList(
            Apps.CONDITION_LIST,
            Apps.VISITS_SUMMARY,
            Apps.HOME_VISITS_SUMMARY,
            Apps.APPOINTMENT_SCHEDULING_HOME,
            Apps.PROVIDER_RELATIONSHIPS_CLINICAL_SUMMARY,
            Apps.RADIOLOGY_APP,
            Apps.RADIOLOGY_ORDERS_APP,
            Apps.BMI_GRAPH + ExtensionPoints.CLINICIAN_DASHBOARD_FIRST_COLUMN,
            Apps.VITALS_SUMMARY,
            Apps.DISPENSING_SUMMARY,
            Apps.NOTES_SUMMARY,
            Apps.COVID_LAB_RESULTS,
            Apps.RECENT_LAB_RESULTS,
            Apps.ALL_LAB_RESULTS
    );

    public static final List<String> CLINICIAN_DASHBOARD_SECOND_COLUMN_ORDER = Arrays.asList(
            Apps.PROGRAMS_LIST,
            Apps.MOST_RECENT_VITALS,
            Apps.PATIENT_DOCUMENTS,
            Apps.PATHOLOGY_SUMMARY,
            Apps.RELATIONSHIPS_CLINICAL_SUMMARY,
            Apps.ALLERGY_SUMMARY,
            Apps.MOST_RECENT_REGISTRATION,
            Apps.ACTIVE_DRUG_ORDERS);

    public static final List<String> REGISTRATION_SUMMARY_FIRST_COLUMN_ORDER = Arrays.asList(
            Apps.MOST_RECENT_REGISTRATION_SUMMARY,
            Apps.MOST_RECENT_REGISTRATION_INSURANCE,
            Apps.MOST_RECENT_REGISTRATION_SOCIAL,
            Apps.MOST_RECENT_REGISTRATION_PATIENT_SUPPORT
    );

    public static final List<String> REGISTRATION_SUMMARY_SECOND_COLUMN_ORDER = Arrays.asList(
            Apps.ADDITIONAL_IDENTIFIERS,
            Apps.MOST_RECENT_REGISTRATION_CONTACT,
            Apps.MOST_RECENT_REGISTRATION_LOCAL_ADDRESS,
            Apps.BIOMETRICS_SUMMARY,
            Apps.PROVIDER_RELATIONSHIPS_REGISTRATION_SUMMARY,
            Apps.RELATIONSHIPS_REGISTRATION_SUMMARY,
            Apps.MOST_RECENT_CHECK_IN,
            Apps.ID_CARD_PRINTING_STATUS

    );

    public static final String GLUCOSE_CONCEPT_UUID = "3cd4e194-26fe-102b-80cb-0017a47871b2";
    public static final String HBA1C_CONCEPT_UUID = "159644AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
    public static final String ABDOMINAL_CIRCUMFERENCE_CONCEPT_UUID = "163080AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
    public static final String FOOT_EXAM_CONCEPT_UUID = "18ea04b9-239e-43b8-9508-f57949d60361";
    public static final String URINARY_ALBUMIN_CONCEPT_UUID = "3cd49d88-26fe-102b-80cb-0017a47871b2";
    public static final String ALCOHOL_USE_CONCEPT_UUID = "3cdbde18-26fe-102b-80cb-0017a47871b2";
    public static final String TOBACCO_USE_CONCEPT_UUID = "3ce503e4-26fe-102b-80cb-0017a47871b2";
    public static final String TOTAL_CHOLESTEROL_CONCEPT_UUID = "3cd68c7e-26fe-102b-80cb-0017a47871b2";
    public static final String HDL_CONCEPT_UUID = "3cd68e18-26fe-102b-80cb-0017a47871b2";
    public static final String LDL_CONCEPT_UUID = "3cd68fa8-26fe-102b-80cb-0017a47871b2";
    public static final String SYSTOLIC_BP_CONCEPT_UUID = "3ce934fa-26fe-102b-80cb-0017a47871b2";
    public static final String DIASTOLIC_BP_CONCEPT_UUID = "3ce93694-26fe-102b-80cb-0017a47871b2";
    public static final String WEIGHT_CONCEPT_UUID = "3ce93b62-26fe-102b-80cb-0017a47871b2";
    public static final String HEIGHT_CONCEPT_UUID = "3ce93cf2-26fe-102b-80cb-0017a47871b2";
    public static final String HEAD_CIRC_CONCEPT_UUID = "3ceb96b4-26fe-102b-80cb-0017a47871b2";
    public static final String LABORATORY_RESULT_UUID = "4d77916a-0620-11e5-a6c0-1697f925ec7b";
    public static final String VIRAL_LOAD_UUID = "3cd4a882-26fe-102b-80cb-0017a47871b2";
    public static final String VIRAL_LOAD_QUALITATIVE_UUID = "1305AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
    public static final String VIRAL_LOAD_LLD_UUID = "53cb83ed-5d55-4b63-922f-d6b8fc67a5f8";
    public static final String ASTHMA_DAYTIME_SYMPTOMS_TWICE_WEEKLY = "cc4681ee-95df-4400-9900-23193cdc6592";
    public static final String ASTHMA_DAYTIME_SYMPTOMS_ONCE_WEEKLY = "3672ee1d-ba8e-4748-8ccc-98d70035857b";
    public static final String ASTHMA_MEDS_TWICE_WEEKLY = "f813d9fa-0842-4862-ae08-5ed30a068207";
    public static final String LIMITATION_OF_ACTIVITY = "abab707b-0ca5-43dd-9b6d-57cb2348e8f8";
    public static final String EPI_SEIZURES_BASELINE = "159517AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
    public static final String EPI_SEIZURES = "ba2e9e43-5a9d-423f-a33e-c34765785397";
    public static final String PHQ9 = "165137AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
    public static final String GAD7 = "8b8769a9-a8cc-4166-ba2a-2e61fb081be7";
    public static final String WHODAS = "163226AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
    public static final String ZLDSI = "163225AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
    public static final String SEIZURE_FREQUENCY = "ba2e9e43-5a9d-423f-a33e-c34765785397";
    public static final String CGI_I = "163223AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
    public static final String SARS_COV2_ANTIBODY_TEST = "165853AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
    public static final String SARS_COV2_ANTIGEN_TEST = "165852AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
    public static final String SARS_COV2_RT_PCR_TEST = "165840AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
    public static final String SARS_COV2_XPERT_TEST = "423edcfa-a5a6-4bc4-a43a-b19644252dc6";

    public static final String GLUCOSE_TEST = "3cd4e194-26fe-102b-80cb-0017a47871b2";
    public static final String BUN_TEST = "3cd4aa12-26fe-102b-80cb-0017a47871b2";
    public static final String NA_TEST = "3cd76b58-26fe-102b-80cb-0017a47871b2";
    public static final String K_TEST = "3cd76ce8-26fe-102b-80cb-0017a47871b2";
    public static final String CR_TEST = "3cd4374e-26fe-102b-80cb-0017a47871b2";
    public static final String HB_TEST = "3ccc7158-26fe-102b-80cb-0017a47871b2";

    public static final String MED_DISPENSED_NAME_UUID = "3cd9491e-26fe-102b-80cb-0017a47871b2";
    public static final String HEART_RATE_UUID = "3ce93824-26fe-102b-80cb-0017a47871b2";
    public static final String TEMPERATURE_UUID = "3ce939d2-26fe-102b-80cb-0017a47871b2";
    public static final String RESPIRATORY_RATE_UUID = "3ceb11f8-26fe-102b-80cb-0017a47871b2";
    public static final String ADVERSE_EFFECT_CONCEPT_UUID = "3cd96052-26fe-102b-80cb-0017a47871b2";
    public static final String ADVERSE_EFFECT_DATE_CONCEPT_UUID = "3cd964bc-26fe-102b-80cb-0017a47871b2";
    public static final String CLINICAL_COMMENTS_CONCEPT_UUID = "3cd9d956-26fe-102b-80cb-0017a47871b2";
    public static final String GP_LABWORKFLOW_OWA_LABRESULTSENCOUNTERTYPES = "labworkflowowa.labResultsEncounterTypes";
}
