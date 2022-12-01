package org.openmrs.module.pihcore.apploader;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.module.ModuleFactory;
import org.openmrs.module.appframework.domain.AppDescriptor;
import org.openmrs.module.appframework.domain.AppTemplate;
import org.openmrs.module.appframework.domain.Extension;
import org.openmrs.module.appframework.factory.AppFrameworkFactory;
import org.openmrs.module.appframework.feature.FeatureToggleProperties;
import org.openmrs.module.coreapps.CoreAppsConstants;
import org.openmrs.module.pihcore.CesConfigConstants;
import org.openmrs.module.pihcore.LiberiaConfigConstants;
import org.openmrs.module.pihcore.PihCoreConstants;
import org.openmrs.module.pihcore.PihCoreUtil;
import org.openmrs.module.pihcore.PihEmrConfigConstants;
import org.openmrs.module.pihcore.SierraLeoneConfigConstants;
import org.openmrs.module.pihcore.ZlConfigConstants;
import org.openmrs.module.pihcore.apploader.apps.GraphFactory;
import org.openmrs.module.pihcore.apploader.apps.patientregistration.PatientRegistrationApp;
import org.openmrs.module.pihcore.config.Components;
import org.openmrs.module.pihcore.config.Config;
import org.openmrs.module.pihcore.config.ConfigDescriptor;
import org.openmrs.module.reporting.config.ReportDescriptor;
import org.openmrs.module.reporting.config.ReportLoader;
import org.openmrs.ui.framework.WebConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.openmrs.module.pihcore.apploader.CustomAppLoaderUtil.addFeatureToggleToExtension;
import static org.openmrs.module.pihcore.apploader.CustomAppLoaderUtil.addToAsthmaDashboardFirstColumn;
import static org.openmrs.module.pihcore.apploader.CustomAppLoaderUtil.addToClinicianDashboardFirstColumn;
import static org.openmrs.module.pihcore.apploader.CustomAppLoaderUtil.addToClinicianDashboardSecondColumn;
import static org.openmrs.module.pihcore.apploader.CustomAppLoaderUtil.addToDiabetesDashboardFirstColumn;
import static org.openmrs.module.pihcore.apploader.CustomAppLoaderUtil.addToDiabetesDashboardSecondColumn;
import static org.openmrs.module.pihcore.apploader.CustomAppLoaderUtil.addToEpilepsyDashboardSecondColumn;
import static org.openmrs.module.pihcore.apploader.CustomAppLoaderUtil.addToHivDashboardFirstColumn;
import static org.openmrs.module.pihcore.apploader.CustomAppLoaderUtil.addToHivDashboardSecondColumn;
import static org.openmrs.module.pihcore.apploader.CustomAppLoaderUtil.addToHomePage;
import static org.openmrs.module.pihcore.apploader.CustomAppLoaderUtil.addToHomePageWithoutUsingRouter;
import static org.openmrs.module.pihcore.apploader.CustomAppLoaderUtil.addToHypertensionDashboardFirstColumn;
import static org.openmrs.module.pihcore.apploader.CustomAppLoaderUtil.addToHypertensionDashboardSecondColumn;
import static org.openmrs.module.pihcore.apploader.CustomAppLoaderUtil.addToMalnutritionDashboardSecondColumn;
import static org.openmrs.module.pihcore.apploader.CustomAppLoaderUtil.addToMentalHealthDashboardSecondColumn;
import static org.openmrs.module.pihcore.apploader.CustomAppLoaderUtil.addToProgramDashboardFirstColumn;
import static org.openmrs.module.pihcore.apploader.CustomAppLoaderUtil.addToProgramSummaryDashboardFirstColumn;
import static org.openmrs.module.pihcore.apploader.CustomAppLoaderUtil.addToProgramSummaryListPage;
import static org.openmrs.module.pihcore.apploader.CustomAppLoaderUtil.addToRegistrationSummaryContent;
import static org.openmrs.module.pihcore.apploader.CustomAppLoaderUtil.addToRegistrationSummarySecondColumnContent;
import static org.openmrs.module.pihcore.apploader.CustomAppLoaderUtil.addToSystemAdministrationPage;
import static org.openmrs.module.pihcore.apploader.CustomAppLoaderUtil.andCreateVisit;
import static org.openmrs.module.pihcore.apploader.CustomAppLoaderUtil.app;
import static org.openmrs.module.pihcore.apploader.CustomAppLoaderUtil.arrayNode;
import static org.openmrs.module.pihcore.apploader.CustomAppLoaderUtil.awaitingAdmissionAction;
import static org.openmrs.module.pihcore.apploader.CustomAppLoaderUtil.cloneAsHivOverallAction;
import static org.openmrs.module.pihcore.apploader.CustomAppLoaderUtil.cloneAsHivVisitAction;
import static org.openmrs.module.pihcore.apploader.CustomAppLoaderUtil.cloneAsOncologyOverallAction;
import static org.openmrs.module.pihcore.apploader.CustomAppLoaderUtil.cloneAsOncologyVisitAction;
import static org.openmrs.module.pihcore.apploader.CustomAppLoaderUtil.containsExtension;
import static org.openmrs.module.pihcore.apploader.CustomAppLoaderUtil.dashboardTab;
import static org.openmrs.module.pihcore.apploader.CustomAppLoaderUtil.editSimpleHtmlFormLink;
import static org.openmrs.module.pihcore.apploader.CustomAppLoaderUtil.encounterTemplate;
import static org.openmrs.module.pihcore.apploader.CustomAppLoaderUtil.enterSimpleHtmlFormLink;
import static org.openmrs.module.pihcore.apploader.CustomAppLoaderUtil.enterStandardHtmlFormLink;
import static org.openmrs.module.pihcore.apploader.CustomAppLoaderUtil.extension;
import static org.openmrs.module.pihcore.apploader.CustomAppLoaderUtil.findPatientTemplateApp;
import static org.openmrs.module.pihcore.apploader.CustomAppLoaderUtil.fragmentExtension;
import static org.openmrs.module.pihcore.apploader.CustomAppLoaderUtil.header;
import static org.openmrs.module.pihcore.apploader.CustomAppLoaderUtil.map;
import static org.openmrs.module.pihcore.apploader.CustomAppLoaderUtil.objectNode;
import static org.openmrs.module.pihcore.apploader.CustomAppLoaderUtil.overallAction;
import static org.openmrs.module.pihcore.apploader.CustomAppLoaderUtil.overallRegistrationAction;
import static org.openmrs.module.pihcore.apploader.CustomAppLoaderUtil.registerTemplateForEncounterType;
import static org.openmrs.module.pihcore.apploader.CustomAppLoaderUtil.report;
import static org.openmrs.module.pihcore.apploader.CustomAppLoaderUtil.visitAction;
import static org.openmrs.module.pihcore.apploader.RequireUtil.and;
import static org.openmrs.module.pihcore.apploader.RequireUtil.not;
import static org.openmrs.module.pihcore.apploader.RequireUtil.or;
import static org.openmrs.module.pihcore.apploader.RequireUtil.patientAgeInMonthsLessThanAtVisitStart;
import static org.openmrs.module.pihcore.apploader.RequireUtil.patientAgeLessThanOrEqualToAtVisitStart;
import static org.openmrs.module.pihcore.apploader.RequireUtil.patientAgeUnknown;
import static org.openmrs.module.pihcore.apploader.RequireUtil.patientDoesNotActiveVisit;
import static org.openmrs.module.pihcore.apploader.RequireUtil.patientHasActiveVisit;
import static org.openmrs.module.pihcore.apploader.RequireUtil.patientHasPreviousEncounter;
import static org.openmrs.module.pihcore.apploader.RequireUtil.patientIsAdult;
import static org.openmrs.module.pihcore.apploader.RequireUtil.patientIsChild;
import static org.openmrs.module.pihcore.apploader.RequireUtil.patientIsFemale;
import static org.openmrs.module.pihcore.apploader.RequireUtil.patientNotDead;
import static org.openmrs.module.pihcore.apploader.RequireUtil.patientVisitWithinPastThirtyDays;
import static org.openmrs.module.pihcore.apploader.RequireUtil.sessionLocationHasTag;
import static org.openmrs.module.pihcore.apploader.RequireUtil.userHasPrivilege;
import static org.openmrs.module.pihcore.apploader.RequireUtil.visitDoesNotHaveEncounterOfType;
import static org.openmrs.module.pihcore.apploader.RequireUtil.visitHasEncounterOfType;


@Component("customAppLoaderFactory")
public class CustomAppLoaderFactory implements AppFrameworkFactory {

    private final Log log = LogFactory.getLog(getClass());

    private Config config;

    private FeatureToggleProperties featureToggles;

    private PatientRegistrationApp patientRegistrationApp;

    private GraphFactory graphs;

    private List<AppDescriptor> apps = new ArrayList<AppDescriptor>();

    private List<Extension> extensions = new ArrayList<Extension>();

    private Boolean readyForRefresh = false;

    private String patientEncountersPageUrl = "";
    private String patientVisitsPageUrl = "";

    private String patientVisitsPageWithSpecificVisitUrl = "";

    public enum ReportCategory { OVERVIEW, DAILY, DATA_EXPORT, DATA_QUALITY, MONITORING };

    @Autowired
    public CustomAppLoaderFactory(Config config,
                                  FeatureToggleProperties featureToggles,
                                  PatientRegistrationApp patientRegistrationApp,
                                  GraphFactory graphs) {
        this.config = config;
        this.featureToggles = featureToggles;
        this.patientRegistrationApp = patientRegistrationApp;
        this.graphs = graphs;
    }

    public void reloadAllAppsAndExtensions() {
        this.setReadyForRefresh(true);
        apps = new ArrayList<AppDescriptor>();
        extensions = new ArrayList<Extension>();
        ModuleFactory.getStartedModuleById("appframework").getModuleActivator().contextRefreshed();
    }

    @Override
    public List<AppDescriptor> getAppDescriptors() throws IOException {
        if (readyForRefresh) {
            loadAppsAndExtensions();
        }
        return apps;
    }

    @Override
    public List<Extension> getExtensions() throws IOException {
        if (readyForRefresh) {
            loadAppsAndExtensions();
        }
        return extensions;
    }

    @Override
    public List<AppTemplate> getAppTemplates() throws IOException {
        return null;
    }


    private String addParametersToUrl(String url, Map<String, String> parameters){
        String urlParams = null;
        if ( StringUtils.isNotBlank(url) && parameters != null && parameters.size() > 0) {
            int separatorIndex = url.indexOf("?");
            StringBuilder sb = new StringBuilder()
                    .append(url.substring(0, separatorIndex))
                    .append("?");
            for (String param : parameters.keySet()) {
                String value = parameters.get(param);
                sb.append(param).append("=").append(value).append("&");
            }
            sb.append(url.substring(separatorIndex + 1));
            urlParams = sb.toString();
        }

        return urlParams;
    }

    private void loadAppsAndExtensions() throws UnsupportedEncodingException {

        configureHeader(config);
        setupDefaultEncounterTemplates();

        //  whether we are using the new visit note
        if (config.isComponentEnabled(Components.VISIT_NOTE)) {
            patientVisitsPageUrl = "/pihcore/visit/visit.page?patient={{patient.uuid}}#/visitList";
            patientVisitsPageWithSpecificVisitUrl = "/pihcore/visit/visit.page?patient={{patient.uuid}}&visit={{visit.uuid}}#/overview";

            extensions.add(overallAction(CustomAppLoaderConstants.Extensions.ENCOUNTER_LIST_OVERALL_ACTION,
                    "pihcore.encounterList",
                    "fas fa-fw fa-file",
                    "link",
                    "pihcore/patient/encounterList.page?patientId={{patient.uuid}}",
                    null,
                    null));

        } else {
            patientVisitsPageUrl = "/coreapps/patientdashboard/patientDashboard.page?patientId={{patient.patientId}}";
            patientVisitsPageWithSpecificVisitUrl = patientVisitsPageUrl + "&visitId={{visit.visitId}}";
        }
        patientEncountersPageUrl="/pihcore/visit/visit.page?patient={{patient.uuid}}#/encounterList";

        if (config.isComponentEnabled(Components.VISIT_MANAGEMENT)) {
            enableVisitManagement();
        }

        if (config.isComponentEnabled(Components.ACTIVE_VISITS)) {
            enableActiveVisits();
        }

        if (config.isComponentEnabled(Components.CHECK_IN)) {
            enableCheckIn(config);
        }

        if (config.isComponentEnabled(Components.UHM_VITALS) ||
                config.isComponentEnabled(Components.VITALS)) {
            enableVitals();
        }

        if (config.isComponentEnabled(Components.CONSULT)) {
            enableConsult();
        }

        if (config.isComponentEnabled(Components.CONSULT_INITIAL)) {
            enableConsultInitial();
        }

		if (config.isComponentEnabled(Components.NURSE_CONSULT)) {
			enableNurseConsult();
		}

        if (config.isComponentEnabled(Components.ED_CONSULT)) {
            enableEDConsult();
        }

        if (config.isComponentEnabled(Components.ADT)) {
            enableADT();
        }

        if (config.isComponentEnabled(Components.DEATH_CERTIFICATE)) {
            enableDeathCertificate();
        }

        if (config.isComponentEnabled(Components.RADIOLOGY)) {
            enableRadiology();
        }

        if (config.isComponentEnabled(Components.DISPENSING)) {
            enableDispensing();
        }

        if (config.isComponentEnabled(Components.MEDICATION_DISPENSING)) {
            enableMedicationDispensing();
        }

        if (config.isComponentEnabled(Components.SURGERY)) {
            enableSurgery();
        }

        if (config.isComponentEnabled(Components.LAB_RESULTS)) {
            enableLabResults();
        }

        if (config.anyComponentEnabled(Arrays.asList(
                Components.OVERVIEW_REPORTS, Components.MONITORING_REPORTS, Components.DATA_EXPORTS)
        )) {
            enableReportsAndExports();
        }

        if (config.isComponentEnabled(Components.ARCHIVES)) {
            enableArchives();
        }

        if (config.isComponentEnabled(Components.WRISTBANDS)) {
            enableWristbands();
        }

        if (config.isComponentEnabled(Components.APPOINTMENT_SCHEDULING)) {
            enableAppointmentScheduling();
        }

        if (config.isComponentEnabled(Components.SYSTEM_ADMINISTRATION)) {
            enableSystemAdministration();
        }

        if (config.isComponentEnabled(Components.MANAGE_PRINTERS)) {
            enableManagePrinters();
        }

        if (config.isComponentEnabled(Components.MY_ACCOUNT)) {
            enableMyAccount();
        }

        if (config.isComponentEnabled(Components.PATIENT_REGISTRATION)) {
            enablePatientRegistration();
        }

        if (config.isComponentEnabled(Components.LEGACY_MPI)) {
            enableLegacyMPI();
        }

        if (config.isComponentEnabled(Components.LACOLLINE_PATIENT_REGISTRATION_ENCOUNTER_TYPES)) {
            registerLacollinePatientRegistrationEncounterTypes();
        }

        if (config.isComponentEnabled(Components.CLINICIAN_DASHBOARD)) {
            enableClinicianDashboard();
        }

        if (config.isComponentEnabled(Components.ALLERGIES)) {
            enableAllergies();
        }

        // will need to add chart search module back to distro if we want to use this again
        if (config.isComponentEnabled(Components.CHART_SEARCH)) {
            enableChartSearch();
        }

        if (config.isComponentEnabled(Components.WAITING_FOR_CONSULT)) {
            enableWaitingForConsult();
        }

        if (config.isComponentEnabled(Components.PRIMARY_CARE)) {
            enablePrimaryCare();
        }

        if (config.isComponentEnabled(Components.ED_TRIAGE)) {
            enableEDTriage();
        }

        if (config.isComponentEnabled(Components.ED_TRIAGE_QUEUE)) {
            enableEDTriageQueue();
        }

        if (config.isComponentEnabled(Components.CHW_APP)) {
            enableCHWApp();
        }

        if (config.isComponentEnabled(Components.BIOMETRICS_FINGERPRINTS)) {
            enableBiometrics(config);
        }

        if (config.isComponentEnabled(Components.TODAYS_VISITS)) {
            enableTodaysVisits();
        }

        if (config.isComponentEnabled(Components.PATHOLOGY_TRACKING)) {
            enablePathologyTracking();
        }

        if (config.isComponentEnabled(Components.LABS)) {
            enableLabs();
        }

        if (config.isComponentEnabled(Components.GROWTH_CHART)) {
            enableGrowthChart();
        }

        if (config.isComponentEnabled(Components.RELATIONSHIPS)) {
            enableRelationships();
        }

        if (config.isComponentEnabled(Components.PROVIDER_RELATIONSHIPS)) {
            enableProviderRelationships();
        }

        if (config.isComponentEnabled(Components.EXPORT_PATIENTS)) {
            enableExportPatients();
        }

        if (config.isComponentEnabled(Components.IMPORT_PATIENTS)) {
            enableImportPatients();
        }

        if (config.isComponentEnabled(Components.PATIENT_DOCUMENTS)) {
            enablePatientDocuments();
        }

        if (config.isComponentEnabled(Components.CONDITION_LIST)) {
            enableConditionList();
        }

        if (config.isComponentEnabled(Components.VCT)) {
            enableVCT();
        }

        if (config.isComponentEnabled(Components.SOCIO_ECONOMICS)) {
            enableSocioEconomics();
        }

//        if (config.isComponentEnabled(Components.ORDER_ENTRY)) {
//            enableOrderEntry();
//        }

        if (config.isComponentEnabled(Components.COHORT_BUILDER)) {
            enableCohortBuilder();
        }

        if (config.isComponentEnabled(Components.CHEMOTHERAPY)) {
            enableChemotherapy();
        }

        if (config.isComponentEnabled(Components.MCH_FORMS)) {
            enableMCHForms();
        }

        if (config.isComponentEnabled(Components.J9)) {
            enableJ9();
        }

        if (config.isComponentEnabled(Components.MCH_GAIN_MATERNAL)) {
            enableMCHGainMaternal();
        }

        if (config.isComponentEnabled(Components.MCH_GAIN_NEWBORN)) {
            enableMCHGainNewborn();
        }

        if (config.isComponentEnabled(Components.COVID19)) {
            enableCovid19();
        }

        if (config.isComponentEnabled(Components.COVID19_INTAKE_FORM)) {
            enableCovid19IntakeForm();
        }

        if (config.isComponentEnabled(Components.TUBERCULOSIS)) {
            enableTuberculosis();
        }

        if (config.isComponentEnabled(Components.MARK_PATIENT_DEAD)) {
            enableMarkPatientDead();
        }

        if (config.isComponentEnabled(Components.PROGRAMS)) {
            enablePrograms(config);
        }

        if (config.isComponentEnabled(Components.PERU_LAB_ORDERS_ANALYSIS_REQUESTS)) {
            enablePeruLabOrdersAnalysisRequest();
        }

        if (config.isComponentEnabled(Components.COMMENT_FORM)) {
            enableCommentForm();
        }

        if (config.isComponentEnabled(Components.SPA_PREVIEW)) {
            enableSpaPreview();
        }

        if (config.isComponentEnabled(Components.REHAB)) {
            enableRehab();
        }
        if (config.isComponentEnabled(Components.PRESCRIPTION)){
            enablePrescription();
        }

        if (StringUtils.isNotBlank(PihCoreUtil.getSystemOrRuntimeProperty(PihCoreConstants.HAITI_HIV_EMR_LINK_URL_RUNTIME_PROPERTY))) {
            enableHaitiHIVLink();
        }

        configureAdditionalExtensions(config);

        readyForRefresh = false;
    }

    private void configureHeader(Config config) {
        extensions.add(header(CustomAppLoaderConstants.Extensions.PIH_HEADER_EXTENSION, "/ms/uiframework/resource/file/configuration/pih/logo/logo.png"));
    }

    // TODO will these be needed/used after we switch to the visit note view?
    private void setupDefaultEncounterTemplates() {

        extensions.add(encounterTemplate(CustomAppLoaderConstants.EncounterTemplates.DEFAULT,
                "coreapps",
                "patientdashboard/encountertemplate/defaultEncounterTemplate"));

        extensions.add(encounterTemplate(CustomAppLoaderConstants.EncounterTemplates.NO_DETAILS,
                "coreapps",
                "patientdashboard/encountertemplate/noDetailsEncounterTemplate"));

        extensions.add(encounterTemplate(CustomAppLoaderConstants.EncounterTemplates.ED_TRIAGE,
                "edtriageapp",
                "edtriageEncounterTemplate"));

    }

    // TODO does this need to be modified for the new visit note at all?
    private void enableVisitManagement() {

        if (!config.getCountry().equals(ConfigDescriptor.Country.PERU)) {
            extensions.add(overallAction(CustomAppLoaderConstants.Extensions.MERGE_VISITS_OVERALL_ACTION,
                    "coreapps.task.mergeVisits.label",
                    "fas fa-fw fa-link",
                    "link",
                    "coreapps/mergeVisits.page?patientId={{patient.uuid}}",
                    "Task: coreapps.mergeVisits",
                    null));
        }

        extensions.add(overallAction(CustomAppLoaderConstants.Extensions.CREATE_VISIT_OVERALL_ACTION,
                "coreapps.task.startVisit.label",
                "fas fa-fw icon-check-in",
                "script",
                "visit.showQuickVisitCreationDialog({{patient.patientId}})",
                "Task: coreapps.createVisit",
                and(patientDoesNotActiveVisit(), patientNotDead())));

        extensions.add(overallAction(CustomAppLoaderConstants.Extensions.CREATE_RETROSPECTIVE_VISIT_OVERALL_ACTION,
                "coreapps.task.createRetrospectiveVisit.label",
                "fas fa-fw fa-plus",
                "script",
                "visit.showRetrospectiveVisitCreationDialog()",
                "Task: coreapps.createRetrospectiveVisit",
                null));

        // this provides the javascript & dialogs the backs the overall action buttons (to start/end visits, etc)
        extensions.add(fragmentExtension(CustomAppLoaderConstants.Extensions.VISIT_ACTIONS_INCLUDES,
                "coreapps",
                "patientdashboard/visitIncludes",
                null,
                CustomAppLoaderConstants.ExtensionPoints.DASHBOARD_INCLUDE_FRAGMENTS,
                map("patientVisitsPage", patientVisitsPageWithSpecificVisitUrl,
                        "visitType", PihEmrConfigConstants.VISITTYPE_CLINIC_OR_HOSPITAL_VISIT_UUID)));

    }

    private void enableActiveVisits() {

        apps.add(addToHomePage(app(CustomAppLoaderConstants.Apps.ACTIVE_VISITS_LIST,
                "coreapps.activeVisits.app.label",
                "fas fa-fw icon-check-in",
                "pihcore/reports/activeVisitsList.page?app=" + CustomAppLoaderConstants.Apps.ACTIVE_VISITS,
                "App: coreapps.activeVisits",
                objectNode("patientPageUrl", patientVisitsPageWithSpecificVisitUrl))));

    }

    private void enableCheckIn(Config config) {

        // circular app that redirects to registraton page, see comments in CheckInPageController
        if (config.isComponentEnabled(Components.CHECK_IN_HOMEPAGE_APP)) {
            apps.add(addToHomePage(findPatientTemplateApp(CustomAppLoaderConstants.Apps.CHECK_IN,
                    "mirebalais.app.patientRegistration.checkin.label",
                    "fas fa-fw fa-paste",
                    "App: mirebalais.checkin",
                    "/pihcore/checkin/checkin.page?patientId={{patientId}}",
                    //     "/registrationapp/registrationSummary.page?patientId={{patientId}}&breadcrumbOverrideProvider=coreapps&breadcrumbOverridePage=findpatient%2FfindPatient&breadcrumbOverrideApp=" + Apps.CHECK_IN + "&breadcrumbOverrideLabel=mirebalais.app.patientRegistration.checkin.label",
                    null, config.getFindPatientColumnConfig()),
                    sessionLocationHasTag("Check-In Location")));
        }

		// check-in form that appears on visit and clinicial dashboard after a visit has been started as a "Visit Action"
        extensions.add(visitAction(CustomAppLoaderConstants.Extensions.CHECK_IN_VISIT_ACTION,
                "mirebalais.task.checkin.label",
                "fas fa-fw icon-check-in",
                "link",
                enterSimpleHtmlFormLink(PihCoreUtil.getFormResource("checkin.xml")),
                "Task: mirebalais.checkinForm",
                sessionLocationHasTag("Check-In Location")));

		// check-in form that appears on the Registration Page as a "Registration Action" and starts a visit
        extensions.add(overallRegistrationAction(CustomAppLoaderConstants.Extensions.CHECK_IN_REGISTRATION_ACTION,
                "mirebalais.task.checkin.label",
                "fas fa-fw icon-check-in",
                "link",
                enterSimpleHtmlFormLink(PihCoreUtil.getFormResource("liveCheckin.xml")) + andCreateVisit(),
                "Task: mirebalais.checkinForm",
                sessionLocationHasTag("Check-In Location")));

        // TODO will this be needed after we stop using the old patient visits page view, or is is replaced by encounterTypeConfig?
        registerTemplateForEncounterType(PihEmrConfigConstants.ENCOUNTERTYPE_CHECK_IN_UUID,
                findExtensionById(CustomAppLoaderConstants.EncounterTemplates.DEFAULT), "fas fa-fw icon-check-in", true, true,
                editSimpleHtmlFormLink(PihCoreUtil.getFormResource("checkin.xml")), null);
    }

    private void enableVitals() {

        if (config.isComponentEnabled(Components.VITALS_HOMEPAGE_APP)) {
            if (config.isComponentEnabled(Components.UHM_VITALS)) {
                // custom vitals app used in Mirebalais
                apps.add(addToHomePage(findPatientTemplateApp(CustomAppLoaderConstants.Apps.UHM_VITALS,
                                "mirebalais.outpatientVitals.title",
                                "fas fa-fw fa-heartbeat",
                                "App: mirebalais.outpatientVitals",
                                "/pihcore/outpatientvitals/patient.page?patientId={{patientId}}",
                                null, config.getFindPatientColumnConfig()),
                        sessionLocationHasTag("Vitals Location")));
            } else {
                apps.add(addToHomePage(app(CustomAppLoaderConstants.Apps.VITALS,
                        "pihcore.vitalsList.title",
                        "fas fa-fw fa-heartbeat",
                        "/pihcore/vitals/vitalsList.page",
                        "App: mirebalais.outpatientVitals",  // TODO rename this permission to not be mirebalais-specific?
                        null)));

            }
        }

        if(config.getCountry().equals(ConfigDescriptor.Country.PERU)){
            extensions.add(visitAction(CustomAppLoaderConstants.Extensions.VITALS_CAPTURE_VISIT_ACTION,
                    "Triaje",
                    "fas fa-fw fa-heartbeat",
                    "link",
                    enterSimpleHtmlFormLink(PihCoreUtil.getFormResource("vitals.xml")),
                    null,
                    and(sessionLocationHasTag("Vitals Location"),
                            or(and(userHasPrivilege(  PihEmrConfigConstants.PRIVILEGE_TASK_EMR_ENTER_VITALS_NOTE), patientHasActiveVisit()),
                                    userHasPrivilege(  PihEmrConfigConstants.PRIVILEGE_TASK_EMR_RETRO_CLINICAL_NOTE),
                                    and(userHasPrivilege(  PihEmrConfigConstants.PRIVILEGE_TASK_EMR_RETRO_CLINICAL_NOTE_THIS_PROVIDER_ONLY), patientVisitWithinPastThirtyDays(config))))));

            AppDescriptor mostRecentVitals = app(CustomAppLoaderConstants.Apps.MOST_RECENT_VITALS,
                    "Triaje",
                    "fas fa-fw fa-heartbeat",
                    null,
                    "App: mirebalais.outpatientVitals",
                    objectNode("encounterDateLabel", "mirebalais.mostRecentVitals.encounterDateLabel",
                            "encounterTypeUuid", PihEmrConfigConstants.ENCOUNTERTYPE_VITALS_UUID,
                            "editable", Boolean.TRUE,
                            "edit-provider", "htmlformentryui",
                            "edit-fragment", "htmlform/editHtmlFormWithSimpleUi",
                            "definitionUiResource", PihCoreUtil.getFormResource("vitals.xml"),
                            "returnUrl", "/" + WebConstants.CONTEXT_PATH + "/" + config.getDashboardUrl()));  // we don't have a good pattern when one needs to include the CONTEXT_PATH
            apps.add(addToClinicianDashboardSecondColumn(mostRecentVitals, "coreapps", "encounter/mostRecentEncounter"));
        }else {
            extensions.add(visitAction(CustomAppLoaderConstants.Extensions.VITALS_CAPTURE_VISIT_ACTION,
                    "mirebalais.task.vitals.label",
                    "fas fa-fw fa-heartbeat",
                    "link",
                    enterSimpleHtmlFormLink(PihCoreUtil.getFormResource("vitals.xml")),
                    null,
                    and(sessionLocationHasTag("Vitals Location"),
                            or(and(userHasPrivilege(PihEmrConfigConstants.PRIVILEGE_TASK_EMR_ENTER_VITALS_NOTE), patientHasActiveVisit()),
                                    userHasPrivilege(PihEmrConfigConstants.PRIVILEGE_TASK_EMR_RETRO_CLINICAL_NOTE),
                                    and(userHasPrivilege(PihEmrConfigConstants.PRIVILEGE_TASK_EMR_RETRO_CLINICAL_NOTE_THIS_PROVIDER_ONLY), patientVisitWithinPastThirtyDays(config))))));

            AppDescriptor mostRecentVitals = app(CustomAppLoaderConstants.Apps.MOST_RECENT_VITALS,
                    "mirebalais.mostRecentVitals.label",
                    "fas fa-fw fa-heartbeat",
                    null,
                    "App: mirebalais.outpatientVitals",
                    objectNode("encounterDateLabel", "mirebalais.mostRecentVitals.encounterDateLabel",
                            "encounterTypeUuid", PihEmrConfigConstants.ENCOUNTERTYPE_VITALS_UUID,
                            "editable", Boolean.TRUE,
                            "edit-provider", "htmlformentryui",
                            "edit-fragment", "htmlform/editHtmlFormWithSimpleUi",
                            "definitionUiResource", PihCoreUtil.getFormResource("vitals.xml"),
							"returnUrl", "/" + WebConstants.CONTEXT_PATH + "/" + config.getDashboardUrl()));  // we don't have a good pattern when one needs to include the CONTEXT_PATH

            apps.add(addToClinicianDashboardSecondColumn(mostRecentVitals, "coreapps", "encounter/mostRecentEncounter"));
        }

        if (config.getCountry().equals(ConfigDescriptor.Country.SIERRA_LEONE) ) {
            apps.add(addToClinicianDashboardFirstColumn(app(CustomAppLoaderConstants.Apps.VITALS_SUMMARY,
                    "mirebalais.vitalsTrend.label",
                    "fas fa-fw fa-heartbeat",
                    null,
                    null,
                    objectNode(
                            "widget", "obsacrossencounters",
                            "icon", "fas fa-fw fa-heartbeat",
                            "label", "mirebalais.vitalsTrend.label",
                            "encounterType", PihEmrConfigConstants.ENCOUNTERTYPE_VITALS_UUID,
                            "detailsUrl", patientVisitsPageUrl,
                            "headers", "zl.date,mirebalais.vitals.short.heartRate.title,mirebalais.vitals.short.temperature.title,mirebalais.vitals.systolic.bp.short.title,mirebalais.vitals.diastolic.bp.short.title,mirebalais.vitals.respiratoryRate.short.title",
                            "concepts", CustomAppLoaderConstants.HEART_RATE_UUID + "," +
                                    CustomAppLoaderConstants.TEMPERATURE_UUID + "," +
                                    CustomAppLoaderConstants.SYSTOLIC_BP_CONCEPT_UUID + "," +
                                    CustomAppLoaderConstants.DIASTOLIC_BP_CONCEPT_UUID  + "," +
                                    CustomAppLoaderConstants.RESPIRATORY_RATE_UUID,
                            "maxRecords", "5"
                    )),
                    "coreapps", "dashboardwidgets/dashboardWidget"));
        }

        // TODO will this be needed after we stop using the old patient visits page view, or is is replaced by encounterTypeConfig?
        registerTemplateForEncounterType(PihEmrConfigConstants.ENCOUNTERTYPE_VITALS_UUID,
                findExtensionById(CustomAppLoaderConstants.EncounterTemplates.DEFAULT), "fas fa-fw fa-heartbeat", null, true,
                editSimpleHtmlFormLink(PihCoreUtil.getFormResource("vitals.xml")), null);

    }

    private void enableConsult() {
        if (config.getCountry().equals(ConfigDescriptor.Country.PERU) ){
            extensions.add(visitAction(CustomAppLoaderConstants.Extensions.CONSULT_NOTE_VISIT_ACTION,
                    "Consulta Ambulatoria",
                    "fas fa-fw fa-stethoscope",
                    "link",
                    enterStandardHtmlFormLink(PihCoreUtil.getFormResource("outpatientConsult.xml")),
                    null,
                    and(sessionLocationHasTag("Consult Note Location"),
                            or(and(userHasPrivilege(  PihEmrConfigConstants.PRIVILEGE_TASK_EMR_ENTER_CONSULT_NOTE), patientHasActiveVisit()),
                                    userHasPrivilege(  PihEmrConfigConstants.PRIVILEGE_TASK_EMR_RETRO_CLINICAL_NOTE),
                                    and(userHasPrivilege(  PihEmrConfigConstants.PRIVILEGE_TASK_EMR_RETRO_CLINICAL_NOTE_THIS_PROVIDER_ONLY), patientVisitWithinPastThirtyDays(config))))));

            // TODO will this be needed after we stop using the old patient visits page view, or is is replaced by encounterTypeConfig?
            extensions.add(encounterTemplate(CustomAppLoaderConstants.EncounterTemplates.CONSULT, "pihcore", "patientdashboard/encountertemplate/consultEncounterTemplate"));

            // TODO will this be needed after we stop using the old patient visits page view, or is is replaced by encounterTypeConfig?
            registerTemplateForEncounterType(PihEmrConfigConstants.ENCOUNTERTYPE_CONSULTATION_UUID,
                    findExtensionById(CustomAppLoaderConstants.EncounterTemplates.CONSULT), "fas fa-fw fa-stethoscope", null, true, null, null);
        }else {

            extensions.add(visitAction(CustomAppLoaderConstants.Extensions.CONSULT_NOTE_VISIT_ACTION,
                    "coreapps.clinic.consult.title",
                    "fas fa-fw fa-stethoscope",
                    "link",
                    enterStandardHtmlFormLink(PihCoreUtil.getFormResource("outpatientConsult.xml")),
                    null,
                    and(sessionLocationHasTag("Consult Note Location"),
                            or(and(userHasPrivilege(PihEmrConfigConstants.PRIVILEGE_TASK_EMR_ENTER_CONSULT_NOTE), patientHasActiveVisit()),
                                    userHasPrivilege(PihEmrConfigConstants.PRIVILEGE_TASK_EMR_RETRO_CLINICAL_NOTE),
                                    and(userHasPrivilege(PihEmrConfigConstants.PRIVILEGE_TASK_EMR_RETRO_CLINICAL_NOTE_THIS_PROVIDER_ONLY), patientVisitWithinPastThirtyDays(config))))));

            // TODO will this be needed after we stop using the old patient visits page view, or is is replaced by encounterTypeConfig?
            extensions.add(encounterTemplate(CustomAppLoaderConstants.EncounterTemplates.CONSULT, "pihcore", "patientdashboard/encountertemplate/consultEncounterTemplate"));

            // TODO will this be needed after we stop using the old patient visits page view, or is is replaced by encounterTypeConfig?
            registerTemplateForEncounterType(PihEmrConfigConstants.ENCOUNTERTYPE_CONSULTATION_UUID,
                    findExtensionById(CustomAppLoaderConstants.EncounterTemplates.CONSULT), "fas fa-fw fa-stethoscope", null, true, null, null);
            }
        }

    private void enableConsultInitial() {
        extensions.add(visitAction(CustomAppLoaderConstants.Extensions.CONSULT_NOTE_INITIAL_VISIT_ACTION,
                "ui.i18n.EncounterType.name." + PihEmrConfigConstants.ENCOUNTERTYPE_CONSULTATION_INITIAL_UUID,
                "fas fa-fw fa-stethoscope",
                "link",
                enterStandardHtmlFormLink(PihCoreUtil.getFormResource("outpatientConsultInitial.xml")),
                null,
                and(sessionLocationHasTag("Consult Note Location"),
                        not(patientHasPreviousEncounter(PihEmrConfigConstants.ENCOUNTERTYPE_CONSULTATION_INITIAL_UUID)))));
    }

	private void enableNurseConsult() {
		extensions.add(visitAction(CustomAppLoaderConstants.Extensions.NURSE_CONSULT_NOTE_VISIT_ACTION,
				"ui.i18n.EncounterType.name." + PihEmrConfigConstants.ENCOUNTERTYPE_NURSE_CONSULT_UUID,
				"fas fa-fw fa-stethoscope",
				"link",
				enterStandardHtmlFormLink(PihCoreUtil.getFormResource("nurseConsult.xml")),
				null,
				sessionLocationHasTag("Consult Note Location")));
	}
    private void enablePrescription(){
            extensions.add(visitAction(CustomAppLoaderConstants.Extensions.PRESCRIPTION_VISIT_ACTION,
                    "ui.i18n.EncounterType.name."+ PihEmrConfigConstants.ENCOUNTERTYPE_PRESCRIPTION_UUID,
                    "fas fa-fw fa-stethoscope",
                    "link",
                    enterStandardHtmlFormLink(PihCoreUtil.getFormResource("prescription.xml")),
                    null,
                    sessionLocationHasTag("Consult Note Location")));

    }


    private void enableEDConsult() {

        extensions.add(visitAction(CustomAppLoaderConstants.Extensions.ED_CONSULT_NOTE_VISIT_ACTION,
                "coreapps.ed.consult.title",
                "fas fa-fw fa-stethoscope",
                "link",
                enterStandardHtmlFormLink(PihCoreUtil.getFormResource("edNote.xml")),
                null,
                and(sessionLocationHasTag("ED Note Location"),
                        or(and(userHasPrivilege(  PihEmrConfigConstants.PRIVILEGE_TASK_EMR_ENTER_ED_NOTE), patientHasActiveVisit()),
                                userHasPrivilege(  PihEmrConfigConstants.PRIVILEGE_TASK_EMR_RETRO_CLINICAL_NOTE),
                                and(userHasPrivilege(  PihEmrConfigConstants.PRIVILEGE_TASK_EMR_RETRO_CLINICAL_NOTE_THIS_PROVIDER_ONLY), patientVisitWithinPastThirtyDays(config))))));
    }

    private void enableADT() {

        apps.add(addToHomePage(app(CustomAppLoaderConstants.Apps.AWAITING_ADMISSION,
                "coreapps.app.awaitingAdmission.label",
                "fas fa-fw fa-list-ul",
                "coreapps/adt/awaitingAdmission.page?app=" + CustomAppLoaderConstants.Apps.AWAITING_ADMISSION,
                "App: coreapps.awaitingAdmission",
                objectNode("patientPageUrl", config.getDashboardUrl()))));

        apps.add(addToHomePage(app(CustomAppLoaderConstants.Apps.INPATIENTS,
                "mirebalaisreports.app.inpatients.label",
                "fas fa-fw fa-hospital",
                "pihcore/reports/inpatientList.page",
                "App: emr.inpatients",
                null),
                sessionLocationHasTag("Inpatients App Location")));

        extensions.add(awaitingAdmissionAction(CustomAppLoaderConstants.Extensions.ADMISSION_FORM_AWAITING_ADMISSION_ACTION,
                "mirebalais.task.admit.label",
                "fas fa-fw fa-hospital-symbol",
                "link",
                enterStandardHtmlFormLink(PihCoreUtil.getFormResource("admissionNote.xml") + "&returnProvider=coreapps&returnPage=adt/awaitingAdmission&returnLabel=coreapps.app.awaitingAdmission.label"),
                "Task: emr.enterAdmissionNote",
                null));

        extensions.add(awaitingAdmissionAction(CustomAppLoaderConstants.Extensions.DENY_ADMISSION_FORM_AWAITING_ADMISSION_ACTION,
                "uicommons.cancel",
                "fas fa-fw fa-user-minus",
                "link",
                enterStandardHtmlFormLink(PihCoreUtil.getFormResource("cancelAdmission.xml") + "&returnProvider=coreapps&returnPage=adt/awaitingAdmission"),
                "Task: emr.enterAdmissionNote",
                null));

        extensions.add(visitAction(CustomAppLoaderConstants.Extensions.ADMISSION_NOTE_VISIT_ACTION,
                "mirebalais.task.admit.label",
                "fas fa-fw fa-hospital-symbol",
                "link",
                enterStandardHtmlFormLink(PihCoreUtil.getFormResource("admissionNote.xml")),
                null,
                and(sessionLocationHasTag("Admission Note Location"),
                        or(and(userHasPrivilege(  PihEmrConfigConstants.PRIVILEGE_TASK_EMR_ENTER_ADMISSION_NOTE), patientHasActiveVisit()),
                                userHasPrivilege(  PihEmrConfigConstants.PRIVILEGE_TASK_EMR_RETRO_CLINICAL_NOTE),
                                and(userHasPrivilege(  PihEmrConfigConstants.PRIVILEGE_TASK_EMR_RETRO_CLINICAL_NOTE_THIS_PROVIDER_ONLY), patientVisitWithinPastThirtyDays(config))))));

        // TODO will these be needed after we stop using the old patient visits page view?
        registerTemplateForEncounterType(PihEmrConfigConstants.ENCOUNTERTYPE_ADMISSION_UUID,
                findExtensionById(CustomAppLoaderConstants.EncounterTemplates.DEFAULT), "fas fa-fw fa-sign-in-alt", null, true, null, null);

        registerTemplateForEncounterType(PihEmrConfigConstants.ENCOUNTERTYPE_CANCEL_ADMISSION_UUID,
                findExtensionById(CustomAppLoaderConstants.EncounterTemplates.DEFAULT), "fas fa-fw fa-ban", true, true, null, null);

        registerTemplateForEncounterType(PihEmrConfigConstants.ENCOUNTERTYPE_TRANSFER_UUID,
                findExtensionById(CustomAppLoaderConstants.EncounterTemplates.NO_DETAILS), "fas fa-fw fa-share", null, true, null, null);

        registerTemplateForEncounterType(PihEmrConfigConstants.ENCOUNTERTYPE_EXIT_FROM_CARE_UUID,
                findExtensionById(CustomAppLoaderConstants.EncounterTemplates.NO_DETAILS), "fas fa-fw fa-sign-out-alt", null, true, null, null);
    }

    private void enableDeathCertificate() {

        extensions.add(overallAction(CustomAppLoaderConstants.Extensions.DEATH_CERTIFICATE_OVERALL_ACTION,
                "mirebalais.deathCertificate.death_certificate",
                "fas fa-fw fa-times-circle",
                "link",
                enterSimpleHtmlFormLink(PihCoreUtil.getFormResource("deathCertificate.xml")),
                "Task: mirebalais.enterDeathCertificate",
                "!patient.person.dead"
        ));

        extensions.add(fragmentExtension(CustomAppLoaderConstants.Extensions.DEATH_CERTIFICATE_HEADER_EXTENSION,
                "pihcore",
                "deathcertificate/headerLink",
                "Task: mirebalais.enterDeathCertificate",
                CustomAppLoaderConstants.ExtensionPoints.DEATH_INFO_HEADER,
                null));
    }

    private void enableRadiology() {

        extensions.add(dashboardTab(CustomAppLoaderConstants.Extensions.RADIOLOGY_TAB,
                "radiologyapp.radiology.label",
                "Task: org.openmrs.module.radiologyapp.tab",
                "radiologyapp",
                "radiologyTab"));

        extensions.add(visitAction(CustomAppLoaderConstants.Extensions.ORDER_XRAY_VISIT_ACTION,
                "radiologyapp.task.order.CR.label",
                "fas fa-fw fa-x-ray",
                "link",
                "radiologyapp/orderRadiology.page?patientId={{patient.uuid}}&visitId={{visit.id}}&modality=CR",
                null,
                and(sessionLocationHasTag("Order Radiology Study Location"),
                        or(and(userHasPrivilege(  PihEmrConfigConstants.PRIVILEGE_TASK_RADIOLOGYAPP_ORDER_XRAY), patientHasActiveVisit()),
                                userHasPrivilege(  PihEmrConfigConstants.PRIVILEGE_TASK_RADIOLOGYAPP_RETRO_ORDER)))));

        extensions.add(visitAction(CustomAppLoaderConstants.Extensions.ORDER_CT_VISIT_ACTION,
                "radiologyapp.task.order.CT.label",
                "fas fa-fw fa-x-ray",
                "link",
                "radiologyapp/orderRadiology.page?patientId={{patient.uuid}}&visitId={{visit.id}}&modality=Ct",
                null,
                and(sessionLocationHasTag("Order Radiology Study Location"),
                        or(and(userHasPrivilege(  PihEmrConfigConstants.PRIVILEGE_TASK_RADIOLOGYAPP_ORDER_CT), patientHasActiveVisit()),
                                userHasPrivilege(  PihEmrConfigConstants.PRIVILEGE_TASK_RADIOLOGYAPP_RETRO_ORDER)))));

        extensions.add(visitAction(CustomAppLoaderConstants.Extensions.ORDER_ULTRASOUND_VISIT_ACTION,
                "radiologyapp.task.order.US.label",
                "fas fa-fw fa-x-ray",
                "link",
                "radiologyapp/orderRadiology.page?patientId={{patient.uuid}}&visitId={{visit.id}}&modality=US",
                null,
                and(sessionLocationHasTag("Order Radiology Study Location"),
                        or(and(userHasPrivilege(  PihEmrConfigConstants.PRIVILEGE_TASK_RADIOLOGYAPP_ORDER_US), patientHasActiveVisit()),
                                userHasPrivilege(  PihEmrConfigConstants.PRIVILEGE_TASK_RADIOLOGYAPP_RETRO_ORDER)))));

        if (config.isComponentEnabled(Components.CLINICIAN_DASHBOARD)) {
            apps.add(addToClinicianDashboardFirstColumn(app(CustomAppLoaderConstants.Apps.RADIOLOGY_ORDERS_APP,
                    "radiologyapp.app.orders",
                    "fas fa-fw fa-camera",
                    "null",
                    "Task: org.openmrs.module.radiologyapp.tab",
                    null),
                    "radiologyapp", "radiologyOrderSection"));

            apps.add(addToClinicianDashboardFirstColumn(app(CustomAppLoaderConstants.Apps.RADIOLOGY_APP,
                    "coreapps.clinicianfacing.radiology",
                    "fas fa-fw fa-camera",
                    "null",
                    "Task: org.openmrs.module.radiologyapp.tab",
                    null),
                    "radiologyapp", "radiologySection"));
        }

        // TODO will this be needed after we stop using the old patient visits page view, or is is replaced by encounterTypeConfig?
        registerTemplateForEncounterType(PihEmrConfigConstants.ENCOUNTERTYPE_RADIOLOGY_ORDER_UUID,
                findExtensionById(CustomAppLoaderConstants.EncounterTemplates.DEFAULT), "fas fa-fw fa-x-ray");

        registerTemplateForEncounterType(PihEmrConfigConstants.ENCOUNTERTYPE_RADIOLOGY_STUDY_UUID,
                findExtensionById(CustomAppLoaderConstants.EncounterTemplates.DEFAULT), "fas fa-fw fa-x-ray");

        registerTemplateForEncounterType(PihEmrConfigConstants.ENCOUNTERTYPE_RADIOLOGY_REPORT_UUID,
                findExtensionById(CustomAppLoaderConstants.EncounterTemplates.DEFAULT), "fas fa-fw fa-x-ray");
    }

    /**
     * This enables the legacy, form-based medication dispensing functionality
     */
    private void enableDispensing() {

        // TODO change this to use the coreapps find patient app?
        apps.add(addToHomePage(app(CustomAppLoaderConstants.Apps.DISPENSING,
                "dispensing.app.label",
                "fas fa-fw fa-pills",
                "dispensing/findPatient.page",
                "App: dispensing.app.dispense",
                objectNode("definitionUiResource", PihCoreUtil.getFormResource("dispensing.xml"))),
                sessionLocationHasTag("Dispensing Location")));

        extensions.add(visitAction(CustomAppLoaderConstants.Extensions.DISPENSE_MEDICATION_VISIT_ACTION,
                "dispensing.app.label",
                "fas fa-fw fa-pills",
                "link",
                enterStandardHtmlFormLink(PihCoreUtil.getFormResource("dispensing.xml")),
                "Task: mirebalais.dispensing",
                sessionLocationHasTag("Dispensing Location")));

        // ToDo:  Add this back when the widget is changes to show all obs groups (not just one) per encounter

        apps.add(addToClinicianDashboardFirstColumn(app(CustomAppLoaderConstants.Apps.DISPENSING_SUMMARY,
                "mirebalais.dispensing.title",
                "fas fa-fw fa-pills",
                "dispensing/patient.page?patientId={{patient.uuid}}",
                null,
                objectNode(
                        "widget", "obsacrossencounters",
                        "icon", "fas fa-fw fa-pills",
                        "label", "mirebalais.dispensing.title",
                        "encounterType", PihEmrConfigConstants.ENCOUNTERTYPE_MEDICATION_DISPENSED_UUID,
                        "detailsUrl", "dispensing/dispensingSummary.page?patientId={{patient.uuid}}",
                        "concepts", CustomAppLoaderConstants.MED_DISPENSED_NAME_UUID,
                        "useConceptNameForDrugValues", true,
                        "maxRecords", "5"
                )),
                "coreapps", "dashboardwidgets/dashboardWidget"));


        // TODO will this be needed after we stop using the old patient visits page view, or is is replaced by encounterTypeConfig?
        registerTemplateForEncounterType(PihEmrConfigConstants.ENCOUNTERTYPE_MEDICATION_DISPENSED_UUID,
                findExtensionById(CustomAppLoaderConstants.EncounterTemplates.DEFAULT), "fas fa-fw fa-pills", true, true, null, "bad21515-fd04-4ff6-bfcd-78456d12f168");

    }

    /**
     * This enables the new, micro-frontend medication dispensing functionality
     */
    private void enableMedicationDispensing() {
        apps.add(addToHomePage(app(CustomAppLoaderConstants.Apps.MEDICATION_DISPENSING,
                        "pih.app.medicationDispensing.title",
                        "fas fa-fw fa-pills",
                        "spa/dispensing",
                        "App: dispensing.app.dispense",
                        null),
                sessionLocationHasTag("Dispensing Location")));
    }

    private void enableSurgery() {

        extensions.add(visitAction(CustomAppLoaderConstants.Extensions.SURGICAL_NOTE_VISIT_ACTION,
                "mirebalais.task.surgicalOperativeNote.label",
                "fas fa-fw fa-paste",
                "link",
                enterStandardHtmlFormLink(PihCoreUtil.getFormResource("surgicalPostOpNote.xml")),
                  PihEmrConfigConstants.PRIVILEGE_TASK_EMR_ENTER_SURGICAL_NOTE,
                sessionLocationHasTag("Surgery Note Location")));

        // TODO will this be needed after we stop using the old patient visits page view, or is is replaced by encounterTypeConfig?
        registerTemplateForEncounterType(PihEmrConfigConstants.ENCOUNTERTYPE_POST_OPERATIVE_NOTE_UUID,
                findExtensionById(CustomAppLoaderConstants.EncounterTemplates.DEFAULT), "fas fa-fw fa-paste", true, true, null, "9b135b19-7ebe-4a51-aea2-69a53f9383af");
    }

    private void enableReportsAndExports() {

        apps.add(addToHomePage(app(CustomAppLoaderConstants.Apps.REPORTS,
                "reportingui.reportsapp.home.title",
                "fas fa-fw fa-chart-bar",
                "reportingui/reportsapp/home.page",
                "App: reportingui.reports",
                null)));

        // This is how things have been setup.  Consider refactoring this to make more sense.
        Set<ReportCategory> enabledCategories = new HashSet<ReportCategory>();
        if (config.isComponentEnabled(Components.OVERVIEW_REPORTS)) {
            enabledCategories.add(ReportCategory.OVERVIEW);
            enabledCategories.add(ReportCategory.DAILY);
            enabledCategories.add(ReportCategory.DATA_QUALITY);
        }
        if (config.isComponentEnabled(Components.MONITORING_REPORTS)) {
            enabledCategories.add(ReportCategory.MONITORING);
        }
        if (config.isComponentEnabled(Components.DATA_EXPORTS)) {
            enabledCategories.add(ReportCategory.DATA_EXPORT);
        }

        // Next, iterate across all of the config-defined reports and add them in if enabled
        // reports defined through Reporting Config
        for (ReportDescriptor reportDescriptor : ReportLoader.loadReportDescriptors()) {
            if (reportDescriptor.getConfig() != null) {
                ReportCategory category = null;
                Object rptCategory = reportDescriptor.getConfig().get("category");
                if (rptCategory != null) {
                    if ("dataExport".equalsIgnoreCase(rptCategory.toString())) {
                        rptCategory = "DATA_EXPORT";
                    }
                    category = ReportCategory.valueOf(rptCategory.toString());
                }

                Map<String, Object> reportConfig = reportDescriptor.getConfig();

                List<String> components = reportConfig.containsKey("components") ? (List<String>) reportConfig.get("components") : null;
                Integer order = reportConfig.containsKey("order") ? Integer.valueOf(reportConfig.get("order").toString()) : 9999;
                List<String> countries = reportConfig.containsKey("countries") ? (List<String>) reportConfig.get("countries") : null;
                List<String> sites = reportConfig.containsKey("sites") ? (List<String>) reportConfig.get("sites") : null;
                String pageProvider = reportConfig.containsKey("pageProvider") ? (String) reportConfig.get("pageProvider") : null;
                String pagePath = reportConfig.containsKey("pagePath") ? (String) reportConfig.get("pagePath") : null;
                String privilege = reportConfig.containsKey("privilege") ? (String) reportConfig.get("privilege") : null;

                boolean matchesComponent = (components == null || config.anyComponentEnabled(components));
                boolean matchesCountry = (countries == null || countries.contains(config.getCountry().name()));
                boolean matchesSite = (sites == null || sites.contains(config.getSite()));

                if (matchesComponent && matchesCountry && matchesSite) {
                    if (category == ReportCategory.OVERVIEW && enabledCategories.contains(ReportCategory.OVERVIEW)) {
                        extensions.add(report(
                                "mirebalaisreports.overview." + reportDescriptor.getKey(),
                                reportDescriptor.getName(),
                                (pageProvider == null ? "reportingui" : pageProvider),
                                (pagePath == null ? "runReport" : pagePath),
                                reportDescriptor.getUuid(),
                                (privilege == null ? "App: reportingui.reports" : privilege),
                                CustomAppLoaderConstants.ExtensionPoints.REPORTING_OVERVIEW_REPORTS,
                                order,
                                "mirebalaisreports-" + reportDescriptor.getKey() + "-link")
                        );
                    }
                    else if (category == ReportCategory.DAILY && enabledCategories.contains(ReportCategory.DAILY)) {
                        extensions.add(report(
                                "mirebalaisreports.dailyReports." + reportDescriptor.getKey(),
                                reportDescriptor.getName(),
                                (pageProvider == null ? "pihcore" : pageProvider),
                                (pagePath == null ? "reports/dailyReport" : pagePath),
                                reportDescriptor.getUuid(),
                                (privilege == null ? "App: reportingui.reports" : privilege),
                                CustomAppLoaderConstants.ExtensionPoints.REPORTING_OVERVIEW_REPORTS,
                                order,
                                "mirebalaisreports-" + reportDescriptor.getKey() + "-link")
                        );
                    }
                    else if (category == ReportCategory.DATA_QUALITY && enabledCategories.contains(ReportCategory.DATA_QUALITY)) {
                        extensions.add(report(
                                "mirebalaisreports.dataQualityReports." + reportDescriptor.getKey(),
                                reportDescriptor.getName(),
                                (pageProvider == null ? "reportingui" : pageProvider),
                                (pagePath == null ? "runReport" : pagePath),
                                reportDescriptor.getUuid(),
                                (privilege == null ? "App: reportingui.reports" : privilege),
                                CustomAppLoaderConstants.ExtensionPoints.REPORTING_DATA_QUALITY,
                                order,
                                "mirebalaisreports-" + reportDescriptor.getKey() + "-link")
                        );
                    }
                    else if (category == ReportCategory.MONITORING && enabledCategories.contains(ReportCategory.MONITORING)) {
                        extensions.add(report(
                                "mirebalaisreports.monitoring." + reportDescriptor.getKey(),
                                reportDescriptor.getName(),
                                (pageProvider == null ? "reportingui" : pageProvider),
                                (pagePath == null ? "runReport" : pagePath),
                                reportDescriptor.getUuid(),
                                (privilege == null ? "App: reportingui.reports" : privilege),
                                CustomAppLoaderConstants.ExtensionPoints.REPORTING_MONITORING,
                                order,
                                "mirebalaisreports-" + reportDescriptor.getKey() + "-link")
                        );
                    }
                    else if (category == ReportCategory.DATA_EXPORT && enabledCategories.contains(ReportCategory.DATA_EXPORT)) {
                        extensions.add(report(
                                "mirebalaisreports.dataExports." + reportDescriptor.getKey(),
                                reportDescriptor.getName(),
                                (pageProvider == null ? "reportingui" : pageProvider),
                                (pagePath == null ? "runReport" : pagePath),
                                reportDescriptor.getUuid(),
                                (privilege == null ? "App: mirebalaisreports.dataexports" : privilege),
                                CustomAppLoaderConstants.ExtensionPoints.REPORTING_DATA_EXPORT,
                                order,
                                "mirebalaisreports-" + reportDescriptor.getKey() + "-link")
                        );
                    }
                }
            }
        }

        // TODO: Get rid of these hacked-in reports in favor of proper configuration
        if (enabledCategories.contains(ReportCategory.OVERVIEW)) {
            if (config.getCountry() == ConfigDescriptor.Country.LIBERIA || config.getCountry() == ConfigDescriptor.Country.SIERRA_LEONE) {
                extensions.add(extension(CustomAppLoaderConstants.Extensions.REGISTRATION_SUMMARY_BY_AGE_REPORT,
                        "mirebalaisreports.registrationoverview.title",
                        null,
                        "link",
                        "pihcore/reports/registrationsByAge.page",
                        "App: reportingui.reports",
                        null,
                        CustomAppLoaderConstants.ExtensionPoints.REPORTING_OVERVIEW_REPORTS,
                        1,
                        map("linkId", "mirebalaisreports-registrationoverview-link")));

                extensions.add(extension(CustomAppLoaderConstants.Extensions.CHECK_IN_SUMMARY_BY_AGE_REPORT,
                        "mirebalaisreports.checkinoverview.title",
                        null,
                        "link",
                        "pihcore/reports/checkInsByAge.page",
                        "App: reportingui.reports",
                        null,
                        CustomAppLoaderConstants.ExtensionPoints.REPORTING_OVERVIEW_REPORTS,
                        1,
                        map("linkId", "mirebalaisreports-checkinoverview-link")));
            }
        }

        if (enabledCategories.contains(ReportCategory.DATA_EXPORT)) {

            extensions.add(extension(CustomAppLoaderConstants.Extensions.REPORTING_AD_HOC_ANALYSIS,
                    "reportingui.adHocAnalysis.label",
                    null,
                    "link",
                    "reportingui/adHocManage.page",
                    "App: reportingui.adHocAnalysis",
                    null,
                    CustomAppLoaderConstants.ExtensionPoints.REPORTING_DATA_EXPORT,
                    9999,
                    null));

            addFeatureToggleToExtension(findExtensionById(CustomAppLoaderConstants.Extensions.REPORTING_AD_HOC_ANALYSIS), "reporting_adHocAnalysis");
        }
    }

    private void enableArchives() {

        apps.add(addToHomePage(app(CustomAppLoaderConstants.Apps.ARCHIVES_ROOM,
                "paperrecord.app.archivesRoom.label",
                "fas fa-fw fa-folder-open",
                "paperrecord/archivesRoom.page",
                "App: emr.archivesRoom",
                null)));
                // ToDo:  Only for archives location
                // sessionLocationHasTag(LocationTags.ARCHIVES_LOCATION)));

        extensions.add(overallAction(CustomAppLoaderConstants.Extensions.REQUEST_PAPER_RECORD_OVERALL_ACTION,
                "paperrecord.task.requestPaperRecord.label",
                "fas fa-fw fa-folder-open",
                "script",
                "showRequestChartDialog()",
                "Task: emr.requestPaperRecord",
                null));

        extensions.add(overallAction(CustomAppLoaderConstants.Extensions.PRINT_ID_CARD_OVERALL_ACTION,
                "paperrecord.task.printIdCardLabel.label",
                "fas fa-fw fa-print",
                "script",
                "printIdCardLabel()",
                "Task: emr.printLabels",
                null));

        extensions.add(overallAction(CustomAppLoaderConstants.Extensions.PRINT_PAPER_FORM_LABEL_OVERALL_ACTION,
                "paperrecord.task.printPaperFormLabel.label",
                "fas fa-fw fa-print",
                "script",
                "printPaperFormLabel()",
                "Task: emr.printLabels",
                null));

        addPaperRecordActionsIncludesIfNeeded();
    }

    public void enableWristbands() {

        extensions.add(overallAction(CustomAppLoaderConstants.Extensions.PRINT_WRISTBAND_OVERALL_ACTION,
                "mirebalais.printWristband",
                "fas fa-fw fa-print",
                "script",
                "printWristband()",
                "Task: emr.printWristband",
                null));

        // this provides the javascript the backs the print wrist action button
        extensions.add(fragmentExtension(CustomAppLoaderConstants.Extensions.PRINT_WRISTBAND_ACTION_INCLUDES,
                "pihcore",
                "wristband/printWristband",
                null,
                CustomAppLoaderConstants.ExtensionPoints.DASHBOARD_INCLUDE_FRAGMENTS,
                null));

    }

    private void enableAppointmentScheduling() {

        AppDescriptor apppointmentScheduling = app(CustomAppLoaderConstants.Apps.APPOINTMENT_SCHEDULING_HOME,
                "appointmentschedulingui.scheduleAppointment.new.title",
                "fas fa-fw fa-calendar-alt",
                "appointmentschedulingui/home.page",
                "App: appointmentschedulingui.home",
                null);

        apps.add(addToHomePage((apppointmentScheduling),
                sessionLocationHasTag("Appointment Location")));

        apps.add(findPatientTemplateApp(CustomAppLoaderConstants.Apps.SCHEDULE_APPOINTMENT,
                "appointmentschedulingui.scheduleAppointment.buttonTitle",
                "fas fa-fw fa-calendar-alt",
                "Task: appointmentschedulingui.bookAppointments",
                "/appointmentschedulingui/manageAppointments.page?patientId={{patientId}}&breadcrumbOverride={{breadcrumbOverride}}",
                arrayNode(objectNode("icon", "fas fa-fw fa-home", "link", "/index.htm"),
                        objectNode("label", "appointmentschedulingui.home.title", "link", "/appointmentschedulingui/home.page"),
                        objectNode("label", "appointmentschedulingui.scheduleAppointment.buttonTitle")),
                config.getFindPatientColumnConfig()));

        extensions.add(overallAction(CustomAppLoaderConstants.Extensions.SCHEDULE_APPOINTMENT_OVERALL_ACTION,
                "appointmentschedulingui.scheduleAppointment.new.title",
                "fas fa-fw fa-calendar-alt",
                "link",
                "appointmentschedulingui/manageAppointments.page?patientId={{patient.uuid}}",
                "Task: appointmentschedulingui.bookAppointments",
                null));

        extensions.add(overallAction(CustomAppLoaderConstants.Extensions.REQUEST_APPOINTMENT_OVERALL_ACTION,
                "appointmentschedulingui.requestAppointment.label",
                "fas fa-fw fa-calendar-alt",
                "link",
                "appointmentschedulingui/requestAppointment.page?patientId={{patient.uuid}}",
                "Task: appointmentschedulingui.requestAppointments",
                null));

        extensions.add(dashboardTab(CustomAppLoaderConstants.Extensions.APPOINTMENTS_TAB,
                "appointmentschedulingui.appointmentsTab.label",
                "App: appointmentschedulingui.viewAppointments",
                "appointmentschedulingui",
                "appointmentsTab"));

        if (config.isComponentEnabled(Components.CLINICIAN_DASHBOARD)) {
            addToClinicianDashboardFirstColumn(apppointmentScheduling,
                    "appointmentschedulingui", "miniPatientAppointments");
        }

    }

    private void enableSystemAdministration() {

        if (findAppById(CustomAppLoaderConstants.Apps.SYSTEM_ADMINISTRATION) == null) {
            apps.add(addToHomePage(app(CustomAppLoaderConstants.Apps.SYSTEM_ADMINISTRATION,
                    "coreapps.app.system.administration.label",
                    "fas fa-fw fa-cogs",
                    "coreapps/systemadministration/systemAdministration.page",
                    "App: coreapps.systemAdministration",
                    null)));
        }

        apps.add(addToSystemAdministrationPage(app(CustomAppLoaderConstants.Apps.MANAGE_ACCOUNTS,
                "coreapps.task.accountManagement.label",
                "fas fa-fw fa-book",
                "pihcore/account/manageAccounts.page",
                1,
                "App: coreapps.systemAdministration",
                null)));

        apps.add(addToSystemAdministrationPage(app(CustomAppLoaderConstants.Apps.MERGE_PATIENTS,
                "coreapps.mergePatientsLong",
                "fas fa-users",
                "coreapps/datamanagement/mergePatients.page?app=coreapps.mergePatients",
                2,
                "App: coreapps.systemAdministration",
                objectNode("breadcrumbs", arrayNode(objectNode("icon", "fas fa-fw fa-home", "link", "/index.htm"),
                        objectNode("label", "coreapps.app.systemAdministration.label", "link", "/coreapps/systemadministration/systemAdministration.page"),
                        objectNode("label", "coreapps.mergePatientsLong")),
                        "dashboardUrl", (config.getAfterMergeUrl() != null) ? (config.getAfterMergeUrl()) : (config.getDashboardUrl())))));

        apps.add(addToSystemAdministrationPage(app(CustomAppLoaderConstants.Apps.FEATURE_TOGGLES,
                "pih.app.featureToggles.label",
                "fas fa-fw fa-toggle-on",
                "pihcore/toggles.page",
                3,
                "App: coreapps.systemAdministration",
                null)));

        apps.add(addToSystemAdministrationPage(app(CustomAppLoaderConstants.Apps.LEGACY_ADMINISTRATION,
                "emr.advancedFeatures",
                "fas fa-fw fa-toolbox",
                "admin/index.htm",
                4,
                "App: coreapps.systemAdministration",
                null)));

        apps.add(addToSystemAdministrationPage(app(CustomAppLoaderConstants.Apps.SYSTEM_CONFIGURATION,
                "pih.app.admin.configuration",
                "fas fa-fw fa-search",
                "pihcore/admin/configuration.page",
                5,
                "App: coreapps.systemAdministration",
                null)));

        apps.add(addToSystemAdministrationPage(app(CustomAppLoaderConstants.Apps.STATUS_DATA_VIEW,
                "pih.app.admin.statusData.view",
                "fas fa-fw icon-flag",
                "pihcore/admin/statusData.page",
                6,
                "App: coreapps.systemAdministration",
                null)));

        apps.add(addToSystemAdministrationPage(app(CustomAppLoaderConstants.Apps.STATUS_DATA_ADMIN,
                "pih.app.admin.statusData.admin",
                "fas fa-fw fa-calculator",
                "pihcore/admin/statusAdmin.page",
                7,
                "App: coreapps.systemAdministration",
                null)));

        apps.add(addToSystemAdministrationPage(app(CustomAppLoaderConstants.Apps.EMAIL_CONFIG_ADMIN,
                "pih.app.admin.email.config",
                "fas fa-fw fa-envelope",
                "pihcore/admin/emailTest.page",
                8,
                "App: coreapps.systemAdministration",
                null)));

        apps.add(addToSystemAdministrationPage(app(CustomAppLoaderConstants.Apps.ACTIVE_USERS_ADMIN,
                "authenticationui.activeUsers.title",
                "fas fa-fw fa-users",
                "authenticationui/admin/activeUsers.page",
                9,
                "App: coreapps.systemAdministration",
                null)));
    }

    private void enableManagePrinters() {

        if (findAppById(CustomAppLoaderConstants.Apps.SYSTEM_ADMINISTRATION) == null) {
            apps.add(addToHomePage(app(CustomAppLoaderConstants.Apps.SYSTEM_ADMINISTRATION,
                    "coreapps.app.system.administration.label",
                    "fas fa-fw fa-cogs",
                    "coreapps/systemadministration/systemAdministration.page",
                    "App: coreapps.systemAdministration",
                    null)));
        }

        apps.add(addToSystemAdministrationPage(app(CustomAppLoaderConstants.Apps.PRINTER_ADMINISTRATION,
                "printer.administration",
                "fas fa-fw fa-print",
                "printer/printerAdministration.page",
                "App: coreapps.systemAdministration",
                null)));

    }

    private void enableMyAccount() {

        apps.add(addToHomePage(app(CustomAppLoaderConstants.Apps.MY_ACCOUNT,
                "authenticationui.myAccount.title",
                "fas fa-fw fa-cog",
                "authenticationui/account/account.page",
                null, null)));

    }

    private void enablePatientRegistration() {

        apps.add(addToHomePage(patientRegistrationApp.getAppDescriptor(config),
                sessionLocationHasTag("Registration Location")));

        // Show additional identifiers (from form section "patient-identification-section")
        //   - in Mexico
        //   - in Haiti if configured for HIV
        if (config.getCountry().equals(ConfigDescriptor.Country.MEXICO) || (
                        config.getCountry().equals(ConfigDescriptor.Country.HAITI) &&
                        ConfigDescriptor.Specialty.HIV.equals(config.getSpecialty()))) {  // reversed to make this null safe
            apps.add(addToRegistrationSummarySecondColumnContent(app(CustomAppLoaderConstants.Apps.ADDITIONAL_IDENTIFIERS,
                    "zl.registration.patient.additionalIdentifiers",
                    "fas fa-fw fa-user",
                    null,
                    "App: registrationapp.registerPatient",
                    null),
                    "registrationapp",
                    "summary/section",
                    map("sectionId", "patient-identification-section")));
        }

        apps.add(addToRegistrationSummaryContent(app(CustomAppLoaderConstants.Apps.MOST_RECENT_REGISTRATION_SUMMARY,
                "mirebalais.mostRecentRegistration.label",
                "fas fa-fw fa-user",
                null,
                "App: registrationapp.registerPatient",
                objectNode("encounterDateLabel", "mirebalais.mostRecentRegistration.encounterDateLabel",
                        "encounterTypeUuid", PihEmrConfigConstants.ENCOUNTERTYPE_PATIENT_REGISTRATION_UUID,
                        "definitionUiResource", PihCoreUtil.getFormResource("patientRegistration-rs.xml"),
                        "editable", true,
                        "creatable", true)),
                "coreapps",
                "encounter/mostRecentEncounter"));

        if (config.isComponentEnabled(Components.PROVIDER_RELATIONSHIPS)) {
            apps.add(addToRegistrationSummarySecondColumnContent(app(CustomAppLoaderConstants.Apps.PROVIDER_RELATIONSHIPS_REGISTRATION_SUMMARY,
                    "pihcore.providerRelationshipsDashboardWidget.label",
                    "fas fa-fw fa-users",
                    null,
                    null,
                    objectNode(
                            "widget", "relationships",
                            "baseAppPath", "/registrationapp",
                            "editable", "true",
                            "editPrivilege", CoreAppsConstants.PRIVILEGE_EDIT_RELATIONSHIPS,
                            "dashboardPage", "/registrationapp/registrationSummary.page?patientId={{patientUuid}}&appId=registrationapp.registerPatient",
                            "providerPage", "/coreapps/providermanagement/editProvider.page?personUuid={{personUuid}}",
                            "includeRelationshipTypes", PihEmrConfigConstants.RELATIONSHIPTYPE_CHWTOPATIENT_UUID,
                            "icon", "fas fa-fw fa-users",
                            "label", "pihcore.providerRelationshipsDashboardWidget.label"
                    )),
                    "coreapps", "dashboardwidgets/dashboardWidget"));
        }

        if (config.isComponentEnabled(Components.RELATIONSHIPS)) {
            apps.add(addToRegistrationSummarySecondColumnContent(app(CustomAppLoaderConstants.Apps.RELATIONSHIPS_REGISTRATION_SUMMARY,
                    "pihcore.relationshipsDashboardWidget.label",
                    "fas fa-fw fa-users",
                    null,
                    null, // TODO restrict by privilege or location)
                    objectNode(
                            "widget", "relationships",
                            "baseAppPath", "/registrationapp",
                            "editable", "true",
                            "editPrivilege", CoreAppsConstants.PRIVILEGE_EDIT_RELATIONSHIPS,
                            "dashboardPage", "/registrationapp/registrationSummary.page?patientId={{patientUuid}}&appId=registrationapp.registerPatient",
                            "providerPage", "/coreapps/providermanagement/editProvider.page?personUuid={{personUuid}}",
                            "includeRelationshipTypes", PihEmrConfigConstants.RELATIONSHIPTYPE_SPOUSEPARTNER_UUID
                                    + "," + PihCoreConstants.RELATIONSHIP_SIBLING
                                    + "," + PihCoreConstants.RELATIONSHIP_PARENT_CHILD,
                            "icon", "fas fa-fw fa-users",
                            "label", "pihcore.relationshipsDashboardWidget.label"
                    )),
                    "coreapps", "dashboardwidgets/dashboardWidget"));
        }


        if (config.getCountry().equals(ConfigDescriptor.Country.MEXICO) ||
                (config.getCountry().equals(ConfigDescriptor.Country.HAITI) &&
                        !ConfigDescriptor.Specialty.MENTAL_HEALTH.equals(config.getSpecialty()))) {  // reversed to make this null safe
            apps.add(addToRegistrationSummaryContent(app(CustomAppLoaderConstants.Apps.MOST_RECENT_REGISTRATION_INSURANCE,
                    "zl.registration.patient.insurance.insuranceName.label",
                    "fas fa-fw fa-address-card",
                    null,
                    "App: registrationapp.registerPatient",
                    objectNode("encounterDateLabel", "mirebalais.mostRecentRegistration.encounterDateLabel",
                            "encounterTypeUuid", PihEmrConfigConstants.ENCOUNTERTYPE_PATIENT_REGISTRATION_UUID,
                            "definitionUiResource", PihCoreUtil.getFormResource("patientRegistration-insurance.xml"),
                            "editable", true)),
                    "coreapps",
                    "encounter/mostRecentEncounter"));
        }
        apps.add(addToRegistrationSummaryContent(app(CustomAppLoaderConstants.Apps.MOST_RECENT_REGISTRATION_SOCIAL,
                "zl.registration.patient.social.label",
                "fas fa-fw fa-user",
                null,
                "App: registrationapp.registerPatient",
                objectNode("encounterDateLabel", "mirebalais.mostRecentRegistration.encounterDateLabel",
                        "encounterTypeUuid", PihEmrConfigConstants.ENCOUNTERTYPE_PATIENT_REGISTRATION_UUID,
                        "definitionUiResource", PihCoreUtil.getFormResource("patientRegistration-social.xml"),
                        "editable", true)),
                "coreapps",
                "encounter/mostRecentEncounter"));


        if (!config.getCountry().equals(ConfigDescriptor.Country.LIBERIA)) {
            apps.add(addToRegistrationSummarySecondColumnContent(app(CustomAppLoaderConstants.Apps.MOST_RECENT_REGISTRATION_CONTACT,
                    "zl.registration.patient.contactPerson.label",
                    "fas fa-fw fa-phone",
                    null,
                    "App: registrationapp.registerPatient",
                    objectNode("encounterDateLabel", "mirebalais.mostRecentRegistration.encounterDateLabel",
                            "encounterTypeUuid", PihEmrConfigConstants.ENCOUNTERTYPE_PATIENT_REGISTRATION_UUID,
                            "definitionUiResource", PihCoreUtil.getFormResource("patientRegistration-contact.xml"),
                            "editable", true)),
                    "coreapps",
                    "encounter/mostRecentEncounter"));
        }

        if (config.isComponentEnabled(Components.CHECK_IN)) {
            apps.add(addToRegistrationSummarySecondColumnContent(app(CustomAppLoaderConstants.Apps.MOST_RECENT_CHECK_IN,
                    "pihcore.mostRecentCheckin.label",
                    "fas fa-fw fa-check",
                    null,
                    "App: registrationapp.registerPatient",
                    objectNode("encounterDateLabel", "pihcore.mostRecentCheckin.encounterDateLabel",
                            "encounterTypeUuid", PihEmrConfigConstants.ENCOUNTERTYPE_CHECK_IN_UUID,
                            "definitionUiResource", PihCoreUtil.getFormResource("checkin.xml"),
                            "editable", true,
                            "edit-provider", "htmlformentryui",
                            "edit-fragment", "htmlform/editHtmlFormWithSimpleUi")),
                    "coreapps",
                    "encounter/mostRecentEncounter"));
        }

        if (config.isComponentEnabled(Components.ID_CARD_PRINTING)) {
            apps.add(addToRegistrationSummarySecondColumnContent(app(CustomAppLoaderConstants.Apps.ID_CARD_PRINTING_STATUS,
                    "zl.registration.patient.idcard.status",
                    "fas fa-fw fa-barcode",
                    null,
                    "App: registrationapp.registerPatient",
                    null),
                    "pihcore",
                    "patientRegistration/idCardStatus"));
        }

        extensions.add(overallRegistrationAction(CustomAppLoaderConstants.Extensions.REGISTER_NEW_PATIENT,
                "registrationapp.home",
                "fas fa-fw fa-user",
                "link",
                "registrationapp/findPatient.page?appId=" + CustomAppLoaderConstants.Apps.PATIENT_REGISTRATION,
                "App: registrationapp.registerPatient",
                sessionLocationHasTag("Registration Location")));

        extensions.add(overallRegistrationAction(CustomAppLoaderConstants.Extensions.MERGE_INTO_ANOTHER_PATIENT,
                "coreapps.mergePatientsShort",
                "fas fa-fw fa-users",
                "link",
                "coreapps/datamanagement/mergePatients.page?app=coreapps.mergePatients&patient1={{patient.patientId}}",
                "App: registrationapp.registerPatient",
                null));

        if (config.isComponentEnabled(Components.CLINICIAN_DASHBOARD)) {
            extensions.add(overallRegistrationAction(CustomAppLoaderConstants.Extensions.CLINICIAN_FACING_PATIENT_DASHBOARD,
                    "registrationapp.clinicalDashboard",
                    "fas fa-fw fa-stethoscope",
                    "link",
                    config.getDashboardUrl() + "&appId=" + CustomAppLoaderConstants.Apps.PATIENT_REGISTRATION,  // TODO what was/is the app id supposed to do here?
                    "App: coreapps.patientDashboard",
                    null));

            extensions.add(overallAction(CustomAppLoaderConstants.Extensions.REGISTRATION_SUMMARY_OVERALL_ACTION,
                    "registrationapp.patient.registrationSummary",
                    "fas fa-fw fa-user",
                    "link",
                    "registrationapp/registrationSummary.page?patientId={{patient.patientId}}&appId=" + CustomAppLoaderConstants.Apps.PATIENT_REGISTRATION,
                    "App: registrationapp.registerPatient",
                    null));
        }

        if (config.isComponentEnabled(Components.VISIT_MANAGEMENT)) {
            extensions.add(overallRegistrationAction(CustomAppLoaderConstants.Extensions.VISITS_DASHBOARD,
                    "pihcore.visitDashboard",
                    "fas fa-fw fa-user",
                    "link",
                    patientVisitsPageUrl,
                    "App: coreapps.patientDashboard",
                    null));
        }

        if (config.isComponentEnabled(Components.ARCHIVES)) {
            extensions.add(overallRegistrationAction(CustomAppLoaderConstants.Extensions.PRINT_PAPER_FORM_LABEL,
                    "paperrecord.task.printPaperFormLabel.label",
                    "fas fa-fw fa-print",
                    "script",
                    "printPaperFormLabel()",
                    "Task: emr.printLabels",
                    null));
        }

        if (config.isComponentEnabled(Components.ID_CARD_PRINTING)) {
            extensions.add(overallRegistrationAction(CustomAppLoaderConstants.Extensions.PRINT_ID_CARD_REGISTRATION_ACTION,
                    "zl.registration.patient.idcard.label",
                    "fas fa-fw fa-barcode",
                    "link",
                    "pihcore/patientRegistration/printIdCard.page?patientId={{patient.patientId}}",
                    "App: registrationapp.registerPatient",
                    null));
        }

        addPaperRecordActionsIncludesIfNeeded();

    }

    // legacy MPI used in Mirebalais to connect to Lacolline
    private void enableLegacyMPI() {
        apps.add(addToHomePageWithoutUsingRouter(app(CustomAppLoaderConstants.Apps.LEGACY_MPI,
                "mirebalais.mpi.title",
                "fas fa-fw fa-search-plus",
                "pihcore/mpi/findPatient.page",
                "App: mirebalais.mpi",
                null)));
    }

    private void enableClinicianDashboard() {

        apps.add(app(CustomAppLoaderConstants.Apps.CLINICIAN_DASHBOARD,
                "mirebalais.app.clinicianDashboard.label",
                "fas fa-fw fa-medkit",
                "coreapps/clinicianfacing/patient.page?app=" + CustomAppLoaderConstants.Apps.CLINICIAN_DASHBOARD,
                CoreAppsConstants.PRIVILEGE_PATIENT_DASHBOARD,
                objectNode(
                        "visitUrl", patientVisitsPageWithSpecificVisitUrl,
                        "visitsUrl", patientVisitsPageUrl
                )));
        AppDescriptor visitSummary = app(CustomAppLoaderConstants.Apps.VISITS_SUMMARY,
                "coreapps.clinicianfacing.visits",
                "fas fa-fw fa-calendar-alt",
                null,
                null,
                objectNode("visitType", PihEmrConfigConstants.VISITTYPE_CLINIC_OR_HOSPITAL_VISIT_UUID));

        apps.add(addToClinicianDashboardFirstColumn(visitSummary, "coreapps", "clinicianfacing/visitsSection"));

        if (config.isComponentEnabled(Components.HOME_VISITS_ON_CLINICIAN_DASHBOARD)) {
            HashMap<String, String> visitParams = new HashMap<String, String>();
            visitParams.put("suppressActions", "true");
            visitParams.put("visitType", PihEmrConfigConstants.VISITTYPE_HOME_VISIT_UUID);

            AppDescriptor homeVisitsSummary = app(CustomAppLoaderConstants.Apps.HOME_VISITS_SUMMARY,
                    "mirebalais.home.visits",
                    "fas fa-fw fa-calendar-alt",
                    null,
                    null,
                    objectNode(
                            "visitType", PihEmrConfigConstants.VISITTYPE_HOME_VISIT_UUID,
                            "visitsUrl", addParametersToUrl(patientVisitsPageUrl, visitParams),
                            "visitUrl",  addParametersToUrl(patientVisitsPageWithSpecificVisitUrl, visitParams),
                            "showVisitTypeOnPatientHeaderSection", true,
                            "label", "mirebalais.home.visits"));

            apps.add(addToClinicianDashboardFirstColumn(homeVisitsSummary, "coreapps", "clinicianfacing/visitsSection"));
        }

        if (config.isComponentEnabled(Components.BMI_ON_CLINICIAN_DASHBOARD)) {
            apps.add(addToClinicianDashboardFirstColumn(
                    graphs.getBmiGraph(CustomAppLoaderConstants.ExtensionPoints.CLINICIAN_DASHBOARD_FIRST_COLUMN),
                    "coreapps",
                    "dashboardwidgets/dashboardWidget"));
        }

        // link for new pihcore visit view
        //"visitUrl", "pihcore/visit/visit.page?visit={{visit.uuid}}"

     /*   if (config.isComponentEnabled(CustomAppLoaderConstants.Components.PRESCRIPTIONS)) {
            // TODO we should actually define an app here, not use the existing app
            addToClinicianDashboardSecondColumn(app, "coreapps", "patientdashboard/activeDrugOrders");
        }
*/
    }

    private void enableAllergies() {
        apps.add(addToClinicianDashboardSecondColumn(app(CustomAppLoaderConstants.Apps.ALLERGY_SUMMARY,
                "allergyui.allergies",
                "fas fa-fw fa-allergies",
                null,
                null,
                null),
                "allergyui", "allergies"));
    }

    private void enableOncology() {

        configureBasicProgramDashboard(PihEmrConfigConstants.PROGRAM_ONCOLOGY_UUID);

        extensions.add(visitAction(CustomAppLoaderConstants.Extensions.ONCOLOGY_CONSULT_NOTE_VISIT_ACTION,
                "pih.task.oncologyConsultNote.label",
                "fas fa-fw fa-hand-holding-heart",
                "link",
                enterStandardHtmlFormLink(PihCoreUtil.getFormResource("oncologyConsult.xml")),
                  PihEmrConfigConstants.PRIVILEGE_TASK_EMR_ENTER_ONCOLOGY_CONSULT_NOTE,
                and(sessionLocationHasTag("Oncology Consult Location"),
                        visitDoesNotHaveEncounterOfType(PihEmrConfigConstants.ENCOUNTERTYPE_ONCOLOGY_TREATMENT_PLAN_UUID),
                        visitDoesNotHaveEncounterOfType(PihEmrConfigConstants.ENCOUNTERTYPE_ONCOLOGY_CONSULT_UUID),
                        or(and(userHasPrivilege(  PihEmrConfigConstants.PRIVILEGE_TASK_EMR_ENTER_ONCOLOGY_CONSULT_NOTE), patientHasActiveVisit()),
                                userHasPrivilege(  PihEmrConfigConstants.PRIVILEGE_TASK_EMR_RETRO_CLINICAL_NOTE),
                                and(userHasPrivilege(  PihEmrConfigConstants.PRIVILEGE_TASK_EMR_RETRO_CLINICAL_NOTE_THIS_PROVIDER_ONLY), patientVisitWithinPastThirtyDays(config))))));

        // will we need this template after we stop using old patient visits view?
        registerTemplateForEncounterType(PihEmrConfigConstants.ENCOUNTERTYPE_ONCOLOGY_CONSULT_UUID,
                findExtensionById(CustomAppLoaderConstants.EncounterTemplates.DEFAULT), "fas fa-fw fa-paste", true, true,
                null, PihEmrConfigConstants.ENCOUNTERROLE_CONSULTINGCLINICIAN_UUID);

        extensions.add(visitAction(CustomAppLoaderConstants.Extensions.ONCOLOGY_TREATMENT_PLAN_ACTION,
                "pih.task.oncologyTreatmentPlan.label",
                "fas fa-fw fa-hand-holding-heart",
                "link",
                enterStandardHtmlFormLink(PihCoreUtil.getFormResource("oncologyIntake.xml")),
                  PihEmrConfigConstants.PRIVILEGE_TASK_EMR_ENTER_ONCOLOGY_CONSULT_NOTE,
                and(sessionLocationHasTag("Oncology Consult Location"),
                        visitDoesNotHaveEncounterOfType(PihEmrConfigConstants.ENCOUNTERTYPE_ONCOLOGY_TREATMENT_PLAN_UUID),
                        visitDoesNotHaveEncounterOfType(PihEmrConfigConstants.ENCOUNTERTYPE_ONCOLOGY_CONSULT_UUID),
                        or(and(userHasPrivilege(  PihEmrConfigConstants.PRIVILEGE_TASK_EMR_ENTER_ONCOLOGY_CONSULT_NOTE), patientHasActiveVisit()),
                                userHasPrivilege(  PihEmrConfigConstants.PRIVILEGE_TASK_EMR_RETRO_CLINICAL_NOTE),
                                and(userHasPrivilege(  PihEmrConfigConstants.PRIVILEGE_TASK_EMR_RETRO_CLINICAL_NOTE_THIS_PROVIDER_ONLY), patientVisitWithinPastThirtyDays(config))))));

        // will we need this template after we stop using old patient visits view?
        registerTemplateForEncounterType(PihEmrConfigConstants.ENCOUNTERTYPE_ONCOLOGY_TREATMENT_PLAN_UUID,
                findExtensionById(CustomAppLoaderConstants.EncounterTemplates.DEFAULT), "fas fa-fw fa-paste", true, true,
                null, PihEmrConfigConstants.ENCOUNTERROLE_CONSULTINGCLINICIAN_UUID);

        extensions.add(visitAction(CustomAppLoaderConstants.Extensions.CHEMOTHERAPY_VISIT_ACTION,
                "pih.task.chemotherapySession.label",
                "fas fa-fw fa-retweet",
                "link",
                enterStandardHtmlFormLink(PihCoreUtil.getFormResource("chemotherapyTreatment.xml")),
                  PihEmrConfigConstants.PRIVILEGE_TASK_EMR_ENTER_ONCOLOGY_CONSULT_NOTE,
                and(sessionLocationHasTag("Chemotherapy Location"),
                        or(and(userHasPrivilege(  PihEmrConfigConstants.PRIVILEGE_TASK_EMR_ENTER_ONCOLOGY_CONSULT_NOTE), patientHasActiveVisit()),
                                userHasPrivilege(  PihEmrConfigConstants.PRIVILEGE_TASK_EMR_RETRO_CLINICAL_NOTE),
                                and(userHasPrivilege(  PihEmrConfigConstants.PRIVILEGE_TASK_EMR_RETRO_CLINICAL_NOTE_THIS_PROVIDER_ONLY), patientVisitWithinPastThirtyDays(config))))));
    }

    public void enableChemotherapy() {

        Extension chemoOrdering = overallAction(CustomAppLoaderConstants.Extensions.CHEMO_ORDERING_VISIT_ACTION,
                "pih.task.orderChemo",
                "fas fa-fw fa-pills",
                "link",
                "owa/openmrs-owa-oncology/index.html?patientId={{patient.uuid}}/#physicianDashboard",
                  PihEmrConfigConstants.PRIVILEGE_TASK_EMR_ENTER_CONSULT_NOTE,
                and(sessionLocationHasTag("Chemotherapy Location"),
                        or(and(userHasPrivilege(  PihEmrConfigConstants.PRIVILEGE_TASK_EMR_ENTER_CONSULT_NOTE), patientHasActiveVisit()),
                                userHasPrivilege(  PihEmrConfigConstants.PRIVILEGE_TASK_EMR_RETRO_CLINICAL_NOTE),
                                and(userHasPrivilege(  PihEmrConfigConstants.PRIVILEGE_TASK_EMR_RETRO_CLINICAL_NOTE_THIS_PROVIDER_ONLY), patientVisitWithinPastThirtyDays(config)))));

        extensions.add(chemoOrdering);

        Extension chemoRecording = visitAction(CustomAppLoaderConstants.Extensions.CHEMO_RECORDING_VISIT_ACTION,
                "pih.task.recordChemo",
                "fas fa-fw fa-pills",
                "link",
                "owa/openmrs-owa-oncology/index.html?patientId={{patient.uuid}}&visitId={{visit.uuid}}/#nurseDashboard",
                  PihEmrConfigConstants.PRIVILEGE_TASK_EMR_ENTER_CONSULT_NOTE,
                and(sessionLocationHasTag("Chemotherapy Location"),
                        or(and(userHasPrivilege(  PihEmrConfigConstants.PRIVILEGE_TASK_EMR_ENTER_CONSULT_NOTE), patientHasActiveVisit()),
                                userHasPrivilege(  PihEmrConfigConstants.PRIVILEGE_TASK_EMR_RETRO_CLINICAL_NOTE),
                                and(userHasPrivilege(  PihEmrConfigConstants.PRIVILEGE_TASK_EMR_RETRO_CLINICAL_NOTE_THIS_PROVIDER_ONLY), patientVisitWithinPastThirtyDays(config)))));

        extensions.add(chemoRecording);

        extensions.add(cloneAsOncologyOverallAction(chemoOrdering));
        extensions.add(cloneAsOncologyVisitAction(chemoRecording));

        registerTemplateForEncounterType(PihEmrConfigConstants.ENCOUNTERTYPE_CHEMOTHERAPY_SESSION_UUID,
                findExtensionById(CustomAppLoaderConstants.EncounterTemplates.DEFAULT), "fas fa-fw fa-retweet", true, true,
                null, PihEmrConfigConstants.ENCOUNTERROLE_CONSULTINGCLINICIAN_UUID);
    }

    private void enableLabResults() {

        extensions.add(overallAction(CustomAppLoaderConstants.Extensions.LAB_RESULTS_OVERALL_ACTION,
                "pih.task.addPastLabResults.label",
                "fas fa-fw fa-vial",
                "link",
                enterSimpleHtmlFormLink(PihCoreUtil.getFormResource("labResults.xml")),
                  PihEmrConfigConstants.PRIVILEGE_TASK_EMR_ENTER_LAB_RESULTS,
                sessionLocationHasTag("Lab Results Location")));

        // circular app for lab results
        apps.add(addToHomePage(findPatientTemplateApp(CustomAppLoaderConstants.Apps.ADD_LAB_RESULTS,
                "pih.app.pastLabResults",
                "fas fa-fw fa-vial",
                  PihEmrConfigConstants.PRIVILEGE_TASK_EMR_ENTER_LAB_RESULTS,
                "/htmlformentryui/htmlform/enterHtmlFormWithSimpleUi.page?patientId={{patientId}}&definitionUiResource=" + PihCoreUtil.getFormResource("labResults.xml") + "&returnUrl=/" + WebConstants.CONTEXT_PATH + "/coreapps/findpatient/findPatient.page?app=" + CustomAppLoaderConstants.Apps.ADD_LAB_RESULTS,
                null, config.getFindPatientColumnConfig()),
                sessionLocationHasTag("Lab Results Location")));


        // will we need this template after we stop using old patient visits view?
        registerTemplateForEncounterType(PihEmrConfigConstants.ENCOUNTERTYPE_LAB_RESULTS_UUID,
                findExtensionById(CustomAppLoaderConstants.EncounterTemplates.DEFAULT), "fas fa-fw fa-vial", true, true,
                editSimpleHtmlFormLink(PihCoreUtil.getFormResource("labResults.xml")), PihEmrConfigConstants.ENCOUNTERROLE_CONSULTINGCLINICIAN_UUID);

    }

    private void enableNCDs() {

        configureBasicProgramDashboard(PihEmrConfigConstants.PROGRAM_NCD_UUID);

        String definitionUiResource = PihCoreUtil.getFormResource("ncd-adult-initial.xml");
        if (!config.getCountry().equals(ConfigDescriptor.Country.LIBERIA)) {
            definitionUiResource = definitionUiResource + "&returnUrl=/" + WebConstants.CONTEXT_PATH + "/" + patientVisitsPageWithSpecificVisitUrl;
        }

        extensions.add(visitAction(CustomAppLoaderConstants.Extensions.NCD_INITIAL_VISIT_ACTION,
                "ui.i18n.EncounterType.name." + PihEmrConfigConstants.ENCOUNTERTYPE_NCD_INITIAL_CONSULT_UUID,
                "fas fa-fw fa-heart",
                "link",
                enterStandardHtmlFormLink(definitionUiResource),  // always redirect to visit page after clicking this link
                  PihEmrConfigConstants.PRIVILEGE_TASK_EMR_ENTER_NCD_CONSULT_NOTE,
                and(sessionLocationHasTag("NCD Consult Location"),
                    visitDoesNotHaveEncounterOfType(PihEmrConfigConstants.ENCOUNTERTYPE_NCD_INITIAL_CONSULT_UUID),
                    visitDoesNotHaveEncounterOfType(PihEmrConfigConstants.ENCOUNTERTYPE_NCD_FOLLOWUP_CONSULT_UUID),
                    not(patientHasPreviousEncounter(PihEmrConfigConstants.ENCOUNTERTYPE_NCD_INITIAL_CONSULT_UUID)),
                        or(and(userHasPrivilege(  PihEmrConfigConstants.PRIVILEGE_TASK_EMR_ENTER_NCD_CONSULT_NOTE), patientHasActiveVisit()),
                                userHasPrivilege(  PihEmrConfigConstants.PRIVILEGE_TASK_EMR_RETRO_CLINICAL_NOTE),
                                and(userHasPrivilege(  PihEmrConfigConstants.PRIVILEGE_TASK_EMR_RETRO_CLINICAL_NOTE_THIS_PROVIDER_ONLY), patientVisitWithinPastThirtyDays(config))))));

        definitionUiResource = PihCoreUtil.getFormResource("ncd-adult-followup.xml");
        if (!config.getCountry().equals(ConfigDescriptor.Country.LIBERIA)) {
            definitionUiResource = definitionUiResource + "&returnUrl=/" + WebConstants.CONTEXT_PATH + "/" + patientVisitsPageWithSpecificVisitUrl;
        }

        extensions.add(visitAction(CustomAppLoaderConstants.Extensions.NCD_FOLLOWUP_VISIT_ACTION,
                "ui.i18n.EncounterType.name." + PihEmrConfigConstants.ENCOUNTERTYPE_NCD_FOLLOWUP_CONSULT_UUID,
                "fas fa-fw fa-heart",
                "link",
                enterStandardHtmlFormLink(definitionUiResource),  // always redirect to visit page after clicking this link
                  PihEmrConfigConstants.PRIVILEGE_TASK_EMR_ENTER_NCD_CONSULT_NOTE,
                and(sessionLocationHasTag("NCD Consult Location"),
                    visitDoesNotHaveEncounterOfType(PihEmrConfigConstants.ENCOUNTERTYPE_NCD_INITIAL_CONSULT_UUID),
                    visitDoesNotHaveEncounterOfType(PihEmrConfigConstants.ENCOUNTERTYPE_NCD_FOLLOWUP_CONSULT_UUID),
                        or(and(userHasPrivilege(  PihEmrConfigConstants.PRIVILEGE_TASK_EMR_ENTER_NCD_CONSULT_NOTE), patientHasActiveVisit()),
                                userHasPrivilege(  PihEmrConfigConstants.PRIVILEGE_TASK_EMR_RETRO_CLINICAL_NOTE),
                                and(userHasPrivilege(  PihEmrConfigConstants.PRIVILEGE_TASK_EMR_RETRO_CLINICAL_NOTE_THIS_PROVIDER_ONLY), patientVisitWithinPastThirtyDays(config))))));
    }

    private void enableEcho() {

        String definitionUiResource = PihCoreUtil.getFormResource("echocardiogram.xml");

        extensions.add(visitAction(CustomAppLoaderConstants.Extensions.ECHO_VISIT_ACTION,
                "ui.i18n.EncounterType.name." + PihEmrConfigConstants.ENCOUNTERTYPE_ECHOCARDIOGRAM_UUID,
                "fas fa-fw fa-chart-line",
                "link",
                enterStandardHtmlFormLink(definitionUiResource),  // always redirect to visit page after clicking this link
                  PihEmrConfigConstants.PRIVILEGE_TASK_EMR_ENTER_NCD_CONSULT_NOTE,
                and(sessionLocationHasTag("Echocardiogram Location"),
                        or(and(userHasPrivilege(  PihEmrConfigConstants.PRIVILEGE_TASK_EMR_ENTER_NCD_CONSULT_NOTE), patientHasActiveVisit()),
                                userHasPrivilege(  PihEmrConfigConstants.PRIVILEGE_TASK_EMR_RETRO_CLINICAL_NOTE),
                                and(userHasPrivilege(  PihEmrConfigConstants.PRIVILEGE_TASK_EMR_RETRO_CLINICAL_NOTE_THIS_PROVIDER_ONLY), patientVisitWithinPastThirtyDays(config))))));

    }

    private void enableMCHForms() {

        if (config.getCountry().equals(ConfigDescriptor.Country.LIBERIA)) {

            extensions.add(visitAction(CustomAppLoaderConstants.Extensions.MCH_ANC_INTAKE_VISIT_ACTION,
                    "ui.i18n.EncounterType.name." + PihEmrConfigConstants.ENCOUNTERTYPE_ANC_INTAKE_UUID,
                    "fas fa-fw fa-gift",
                    "link",
                    enterStandardHtmlFormLink(PihCoreUtil.getFormResource("anc-initial.xml")),
                      PihEmrConfigConstants.PRIVILEGE_TASK_EMR_ENTER_MCH,
                    and(sessionLocationHasTag("Maternal and Child Location"),
                            visitDoesNotHaveEncounterOfType(PihEmrConfigConstants.ENCOUNTERTYPE_ANC_INTAKE_UUID),
                            visitDoesNotHaveEncounterOfType(PihEmrConfigConstants.ENCOUNTERTYPE_ANC_FOLLOWUP_UUID),
                            and(patientIsFemale()))));

            extensions.add(visitAction(CustomAppLoaderConstants.Extensions.MCH_ANC_FOLLOWUP_VISIT_ACTION,
                    "ui.i18n.EncounterType.name." + PihEmrConfigConstants.ENCOUNTERTYPE_ANC_FOLLOWUP_UUID,
                    "fas fa-fw fa-gift",
                    "link",
                    enterStandardHtmlFormLink(PihCoreUtil.getFormResource("anc-followup.xml")),
                      PihEmrConfigConstants.PRIVILEGE_TASK_EMR_ENTER_MCH,
                    and(sessionLocationHasTag("Maternal and Child Location"),
                            visitDoesNotHaveEncounterOfType(PihEmrConfigConstants.ENCOUNTERTYPE_ANC_INTAKE_UUID),
                            visitDoesNotHaveEncounterOfType(PihEmrConfigConstants.ENCOUNTERTYPE_ANC_FOLLOWUP_UUID),
                            and(patientIsFemale()))));

            extensions.add(visitAction(CustomAppLoaderConstants.Extensions.MCH_DELIVERY_VISIT_ACTION,            //TODO: working on this
                    "ui.i18n.EncounterType.name." + PihEmrConfigConstants.ENCOUNTERTYPE_MCH_DELIVERY_UUID,
                    "fas fa-fw fa-gift",
                    "link",
                    enterStandardHtmlFormLink(PihCoreUtil.getFormResource("anc-delivery.xml")),
                      PihEmrConfigConstants.PRIVILEGE_TASK_EMR_ENTER_MCH,
                    and(sessionLocationHasTag("Maternal and Child Location"),
                            visitDoesNotHaveEncounterOfType(PihEmrConfigConstants.ENCOUNTERTYPE_MCH_DELIVERY_UUID),
                            and(patientIsFemale()))));

            extensions.add(visitAction(CustomAppLoaderConstants.Extensions.MCH_PEDS_ACTION,
                    "ui.i18n.EncounterType.name." + LiberiaConfigConstants.ENCOUNTERTYPE_LIBERIAPEDSFORM_UUID,
                    "fas fa-fw fa-gift",
                    "link",
                    enterStandardHtmlFormLink(PihCoreUtil.getFormResource("peds.xml")),
                      PihEmrConfigConstants.PRIVILEGE_TASK_EMR_ENTER_MCH,
                    and(sessionLocationHasTag("Maternal and Child Location"),
                            and(patientIsFemale()))));
        } else if (config.getCountry() == ConfigDescriptor.Country.HAITI) {

            extensions.add(visitAction(CustomAppLoaderConstants.Extensions.MCH_DELIVERY_VISIT_ACTION,
                    "ui.i18n.EncounterType.name." + PihEmrConfigConstants.ENCOUNTERTYPE_MCH_DELIVERY_UUID,
                    "fas fa-fw fa-baby",
                    "link",
                    enterStandardHtmlFormLink(PihCoreUtil.getFormResource("delivery.xml") + "&returnUrl=/" + WebConstants.CONTEXT_PATH + "/" + patientVisitsPageWithSpecificVisitUrl),  // always redirect to visit page after clicking this link
                      PihEmrConfigConstants.PRIVILEGE_TASK_EMR_ENTER_MCH,
                    and(sessionLocationHasTag("Maternal and Child Location"),
                            visitDoesNotHaveEncounterOfType(PihEmrConfigConstants.ENCOUNTERTYPE_MCH_DELIVERY_UUID),
                            and(patientIsFemale()))));
        } else {

            extensions.add(visitAction(CustomAppLoaderConstants.Extensions.MCH_ANC_INTAKE_VISIT_ACTION,
                    "ui.i18n.EncounterType.name." + PihEmrConfigConstants.ENCOUNTERTYPE_ANC_INTAKE_UUID,
                    "fas fa-fw fa-gift",
                    "link",
                    enterStandardHtmlFormLink(PihCoreUtil.getFormResource("ancIntake.xml") + "&returnUrl=/" + WebConstants.CONTEXT_PATH + "/" + patientVisitsPageWithSpecificVisitUrl),  // always redirect to visit page after clicking this link
                      PihEmrConfigConstants.PRIVILEGE_TASK_EMR_ENTER_MCH,
                    and(sessionLocationHasTag("Maternal and Child Location"),
                            visitDoesNotHaveEncounterOfType(PihEmrConfigConstants.ENCOUNTERTYPE_ANC_INTAKE_UUID),
                            visitDoesNotHaveEncounterOfType(PihEmrConfigConstants.ENCOUNTERTYPE_ANC_FOLLOWUP_UUID),
                            and(patientIsFemale()))));

            extensions.add(visitAction(CustomAppLoaderConstants.Extensions.MCH_ANC_FOLLOWUP_VISIT_ACTION,
                    "ui.i18n.EncounterType.name." + PihEmrConfigConstants.ENCOUNTERTYPE_ANC_FOLLOWUP_UUID,
                    "fas fa-fw fa-gift",
                    "link",
                    enterStandardHtmlFormLink(PihCoreUtil.getFormResource("ancFollowup.xml") + "&returnUrl=/" + WebConstants.CONTEXT_PATH + "/" + patientVisitsPageWithSpecificVisitUrl),  // always redirect to visit page after clicking this link
                      PihEmrConfigConstants.PRIVILEGE_TASK_EMR_ENTER_MCH,
                    and(sessionLocationHasTag("Maternal and Child Location"),
                            visitDoesNotHaveEncounterOfType(PihEmrConfigConstants.ENCOUNTERTYPE_ANC_INTAKE_UUID),
                            visitDoesNotHaveEncounterOfType(PihEmrConfigConstants.ENCOUNTERTYPE_ANC_FOLLOWUP_UUID),
                            and(patientIsFemale()))));

            extensions.add(visitAction(CustomAppLoaderConstants.Extensions.MCH_DELIVERY_VISIT_ACTION,
                    "ui.i18n.EncounterType.name." + PihEmrConfigConstants.ENCOUNTERTYPE_MCH_DELIVERY_UUID,
                    "fas fa-fw fa-baby",
                    "link",
                    enterStandardHtmlFormLink(PihCoreUtil.getFormResource("delivery.xml") + "&returnUrl=/" + WebConstants.CONTEXT_PATH + "/" + patientVisitsPageWithSpecificVisitUrl),  // always redirect to visit page after clicking this link
                      PihEmrConfigConstants.PRIVILEGE_TASK_EMR_ENTER_MCH,
                    and(sessionLocationHasTag("Maternal and Child Location"),
                            visitDoesNotHaveEncounterOfType(PihEmrConfigConstants.ENCOUNTERTYPE_MCH_DELIVERY_UUID),
                            and(patientIsFemale()))));

        }

        if (config.isComponentEnabled(Components.OBGYN)) {
            extensions.add(visitAction(CustomAppLoaderConstants.Extensions.OB_GYN_VISIT_ACTION,
                    "ui.i18n.EncounterType.name." + PihEmrConfigConstants.ENCOUNTERTYPE_OB_GYN_UUID,
                    "fas fa-fw fa-female",
                    "link",
                    enterStandardHtmlFormLink(PihCoreUtil.getFormResource("obGyn.xml") + "&returnUrl=/" + WebConstants.CONTEXT_PATH + "/" + patientVisitsPageWithSpecificVisitUrl),  // always redirect to visit page after clicking this link
                      PihEmrConfigConstants.PRIVILEGE_TASK_EMR_ENTER_MCH,
                    and(sessionLocationHasTag("Maternal and Child Location"),
                            and(patientIsFemale()))));

        }

    }

    private void enableMCHGainMaternal() {
        if (config.getCountry().equals(ConfigDescriptor.Country.SIERRA_LEONE)) {

            extensions.add(visitAction(CustomAppLoaderConstants.Extensions.MCH_GAIN_DELIVERY_REGISTER_ACTION,
                    "pih.task.maternalDeliveryRegister",
                    "fas fa-fw fa-baby",
                    "link",
                    enterStandardHtmlFormLink(PihCoreUtil.getFormResource("gainMaternalRegister.xml")),
                    PihEmrConfigConstants.PRIVILEGE_TASK_EMR_ENTER_MCH,
                    and(sessionLocationHasTag("Maternal and Child Location"),
                            and(patientIsFemale()))));
        }
    }

    private void enableMCHGainNewborn() {
        if (config.getCountry().equals(ConfigDescriptor.Country.SIERRA_LEONE)) {

            extensions.add(visitAction(CustomAppLoaderConstants.Extensions.MCH_GAIN_SCBU_REGISTER_ACTION,
                    "pih.task.sbcuRegister",
                    "fas fa-fw fa-baby",
                    "link",
                    enterStandardHtmlFormLink(PihCoreUtil.getFormResource("gainNewbornRegister.xml")),
                    PihEmrConfigConstants.PRIVILEGE_TASK_EMR_ENTER_MCH,
                    and(sessionLocationHasTag("Maternal and Child Location"),
                            and(patientIsFemale()))));
        }
    }

    private void enableANCProgram() {
        configureBasicProgramDashboard(PihEmrConfigConstants.PROGRAM_ANC_UUID);
    }

    private void enableMCHProgram() {
        configureBasicProgramDashboard(PihEmrConfigConstants.PROGRAM_MCH_UUID);
    }

    private void enableTBProgram(){
        configureBasicProgramDashboard(PihEmrConfigConstants.PROGRAM_TB_UUID);
    }

    private void enableVaccinationOnly() {
        extensions.add(visitAction(CustomAppLoaderConstants.Extensions.VACCINATION_VISIT_ACTION,
                "ui.i18n.EncounterType.name." + PihEmrConfigConstants.ENCOUNTERTYPE_VACCINATION_UUID,
                "fas fa-fw fa-umbrella",
                "link",
                enterStandardHtmlFormLink(PihCoreUtil.getFormResource("vaccination-only.xml") + "&returnUrl=/" + WebConstants.CONTEXT_PATH + "/" + patientVisitsPageWithSpecificVisitUrl),  // always redirect to visit page after clicking this link
                  PihEmrConfigConstants.PRIVILEGE_TASK_EMR_ENTER_VACCINATION,
                and(sessionLocationHasTag("Vaccination Location"),
                        visitDoesNotHaveEncounterOfType(PihEmrConfigConstants.ENCOUNTERTYPE_VACCINATION_UUID))));
    }

    private void enableMentalHealthForm() {

        String definitionUiResource = PihCoreUtil.getFormResource("mentalHealth.xml");
        if (!config.getCountry().equals(ConfigDescriptor.Country.LIBERIA)) {
            definitionUiResource = definitionUiResource + "&returnUrl=/" + WebConstants.CONTEXT_PATH + "/" + patientVisitsPageWithSpecificVisitUrl;
        }

        extensions.add(visitAction(CustomAppLoaderConstants.Extensions.MENTAL_HEALTH_VISIT_ACTION,
                "pih.task.mentalHealth.label",
                "fas fa-fw fa-user",
                "link",
                enterStandardHtmlFormLink(definitionUiResource),
                  PihEmrConfigConstants.PRIVILEGE_TASK_EMR_ENTER_MENTAL_HEALTH_NOTE,
                and(sessionLocationHasTag("Mental Health Location"),
                        visitDoesNotHaveEncounterOfType(PihEmrConfigConstants.ENCOUNTERTYPE_MENTAL_HEALTH_ASSESSMENT_UUID))));

        // will we need this template after we stop using old patient visits view?
        registerTemplateForEncounterType(PihEmrConfigConstants.ENCOUNTERTYPE_MENTAL_HEALTH_ASSESSMENT_UUID,
                findExtensionById(CustomAppLoaderConstants.EncounterTemplates.DEFAULT), "fas fa-fw fa-user", true, true,
                null, PihEmrConfigConstants.ENCOUNTERROLE_CONSULTINGCLINICIAN_UUID);
    }

    private void enableVCT() {

        extensions.add(visitAction(CustomAppLoaderConstants.Extensions.VCT_VISIT_ACTION,
                "pih.task.vct.label",
                "fas fa-fw fa-ribbon",
                "link",
                enterStandardHtmlFormLink(PihCoreUtil.getFormResource("hiv/vct.xml")),
                  PihEmrConfigConstants.PRIVILEGE_TASK_EMR_ENTER_VCT,
                and(sessionLocationHasTag("Consult Note Location"),
                        visitDoesNotHaveEncounterOfType(PihEmrConfigConstants.ENCOUNTERTYPE_VCT_UUID))));
    }

    private void enableSocioEconomics() {
        extensions.add(visitAction(CustomAppLoaderConstants.Extensions.SOCIO_ECONOMICS_VISIT_ACTION,
                "pih.task.socioEcon.label",
                "fas fa-fw fa-home",
                "link",
                enterStandardHtmlFormLink(PihCoreUtil.getFormResource("socio-econ.xml")),
                  PihEmrConfigConstants.PRIVILEGE_TASK_EMR_ENTER_SOCIO,
                and(sessionLocationHasTag("Consult Note Location"),
                        visitDoesNotHaveEncounterOfType(PihEmrConfigConstants.ENCOUNTERTYPE_SOCIO_ECONOMICS_UUID))));
    }

    private void enableChartSearch() {
        extensions.add(overallAction(CustomAppLoaderConstants.Extensions.CHART_SEARCH_OVERALL_ACTION,
                "pihcore.chartSearch.label",
                "fas fa-fw fa-search",
                "link",
                "chartsearch/chartsearch.page?patientId={{patient.patientId}}",
                  PihEmrConfigConstants.PRIVILEGE_TASK_EMR_ENTER_CONSULT_NOTE, // TODO correct permission!
                null));
    }

    private void enableWaitingForConsult() {

        apps.add(addToHomePage(app(CustomAppLoaderConstants.Apps.WAITING_FOR_CONSULT,
                "pihcore.waitingForConsult.title",
                "fas fa-fw fa-stethoscope",
                "pihcore/visit/waitingForConsult.page",
                  PihEmrConfigConstants.PRIVILEGE_APP_WAITING_FOR_CONSULT,
                null)));
    }

    private void enableTodaysVisits() {

        apps.add(addToHomePage(app(CustomAppLoaderConstants.Apps.TODAYS_VISITS,
                "pihcore.todaysVisits.title",
                "fas fa-fw icon-check-in",
                "pihcore/visit/todaysVisits.page",
                  PihEmrConfigConstants.PRIVILEGE_APP_TODAYS_VISITS,
                null)));

    }

    private void enableCHWApp() {
        if (findAppById(CustomAppLoaderConstants.Apps.CHW_MGMT) == null) {
            apps.add(addToHomePage(app(CustomAppLoaderConstants.Apps.CHW_MGMT,
                    "chwapp.label",
                    "fas fa-users",
                    "/coreapps/providermanagement/providerList.page",
                      PihEmrConfigConstants.PRIVILEGE_APP_CHW,
                    null),
                    sessionLocationHasTag("Provider Management Location")));
        }
    }

    private void enableEDTriage() {
        apps.add(addToHomePage(findPatientTemplateApp(CustomAppLoaderConstants.Apps.ED_TRIAGE,
                "edtriageapp.label",
                "fas fa-fw fa-ambulance",
                  PihEmrConfigConstants.PRIVILEGE_APP_ED_TRIAGE,
                "/edtriageapp/edtriageEditPatient.page?patientId={{patientId}}&appId=" + CustomAppLoaderConstants.Apps.ED_TRIAGE
                        + "&dashboardUrl=" + config.getDashboardUrl(),
                null, config.getFindPatientColumnConfig()),
                sessionLocationHasTag("ED Triage Location")));

        extensions.add(visitAction(CustomAppLoaderConstants.Extensions.ED_TRIAGE_VISIT_ACTION,
                "ui.i18n.EncounterType.name." + PihEmrConfigConstants.ENCOUNTERTYPE_EMERGENCY_TRIAGE_UUID,
                "fas fa-fw fa-ambulance",
                "link",
                "/edtriageapp/edtriageEditPatient.page?patientId={{patient.uuid}}&appId=" + CustomAppLoaderConstants.Apps.ED_TRIAGE,
                null,
                and(sessionLocationHasTag("ED Triage Location"), patientHasActiveVisit())));

        // TODO will this be needed after we stop using the old patient visits page view, or is is replaced by encounterTypeConfig?
        registerTemplateForEncounterType(PihEmrConfigConstants.ENCOUNTERTYPE_EMERGENCY_TRIAGE_UUID,
                findExtensionById(CustomAppLoaderConstants.EncounterTemplates.ED_TRIAGE), "fas fa-fw fa-ambulance", false, true,
                "edtriageapp/edtriageEditPatient.page?patientId={{patient.uuid}}&encounterId={{encounter.uuid}}&appId=edtriageapp.app.triageQueue&returnUrl={{returnUrl}}&breadcrumbOverride={{breadcrumbOverride}}&editable=true",
                null);
    }

    private void enableEDTriageQueue() {
        apps.add(addToHomePage(app(CustomAppLoaderConstants.Apps.ED_TRIAGE_QUEUE,
                "edtriageapp.queue.label",
                "fas fa-fw fa-list-ol",
                "/edtriageapp/edtriageViewQueue.page?appId=" + CustomAppLoaderConstants.Apps.ED_TRIAGE_QUEUE,
                  PihEmrConfigConstants.PRIVILEGE_APP_ED_TRIAGE_QUEUE,
                objectNode("dashboardUrl", config.getDashboardUrl())),
                sessionLocationHasTag("ED Triage Location")));
    }

    private void enablePrimaryCare() {

        if (config.getCountry() == ConfigDescriptor.Country.HAITI) {

            extensions.add(visitAction(CustomAppLoaderConstants.Extensions.PRIMARY_CARE_PEDS_INITIAL_VISIT_ACTION,
                    "ui.i18n.EncounterType.name." + PihEmrConfigConstants.ENCOUNTERTYPE_PRIMARY_CARE_PEDS_INITIAL_CONSULT_UUID,
                    "fas fa-fw fa-stethoscope",
                    "link",
                    enterStandardHtmlFormLink(PihCoreUtil.getFormResource("primary-care-peds-initial.xml") + "&returnUrl=/" + WebConstants.CONTEXT_PATH + "/" + patientVisitsPageWithSpecificVisitUrl),  // always redirect to visit page after clicking this link
                    null,
                    and(sessionLocationHasTag("Primary Care Consult Location"),
                            visitDoesNotHaveEncounterOfType(PihEmrConfigConstants.ENCOUNTERTYPE_PRIMARY_CARE_PEDS_INITIAL_CONSULT_UUID),
                            visitDoesNotHaveEncounterOfType(PihEmrConfigConstants.ENCOUNTERTYPE_PRIMARY_CARE_PEDS_FOLLOWUP_CONSULT_UUID),
                            not(patientHasPreviousEncounter(PihEmrConfigConstants.ENCOUNTERTYPE_PRIMARY_CARE_PEDS_INITIAL_CONSULT_UUID)),
                            or(patientIsChild(), patientAgeUnknown(), patientDoesNotActiveVisit()),
                            or(and(userHasPrivilege(  PihEmrConfigConstants.PRIVILEGE_TASK_EMR_ENTER_PRIMARY_CARE_CONSULT_NOTE), patientHasActiveVisit()),
                                    userHasPrivilege(  PihEmrConfigConstants.PRIVILEGE_TASK_EMR_RETRO_CLINICAL_NOTE),
                                    and(userHasPrivilege(  PihEmrConfigConstants.PRIVILEGE_TASK_EMR_RETRO_CLINICAL_NOTE_THIS_PROVIDER_ONLY), patientVisitWithinPastThirtyDays(config))))));

            extensions.add(visitAction(CustomAppLoaderConstants.Extensions.PRIMARY_CARE_PEDS_FOLLOWUP_VISIT_ACTION,
                    "ui.i18n.EncounterType.name." + PihEmrConfigConstants.ENCOUNTERTYPE_PRIMARY_CARE_PEDS_FOLLOWUP_CONSULT_UUID,
                    "fas fa-fw fa-stethoscope",
                    "link",
                    enterStandardHtmlFormLink(PihCoreUtil.getFormResource("primary-care-peds-followup.xml") + "&returnUrl=/" + WebConstants.CONTEXT_PATH + "/" + patientVisitsPageWithSpecificVisitUrl),  // always redirect to visit page after clicking this link
                    null,
                    and(sessionLocationHasTag("Primary Care Consult Location"),
                            visitDoesNotHaveEncounterOfType(PihEmrConfigConstants.ENCOUNTERTYPE_PRIMARY_CARE_PEDS_INITIAL_CONSULT_UUID),
                            visitDoesNotHaveEncounterOfType(PihEmrConfigConstants.ENCOUNTERTYPE_PRIMARY_CARE_PEDS_FOLLOWUP_CONSULT_UUID),
                            or(patientIsChild(), patientAgeUnknown(), patientDoesNotActiveVisit()),
                            or(and(userHasPrivilege(  PihEmrConfigConstants.PRIVILEGE_TASK_EMR_ENTER_PRIMARY_CARE_CONSULT_NOTE), patientHasActiveVisit()),
                                    userHasPrivilege(  PihEmrConfigConstants.PRIVILEGE_TASK_EMR_RETRO_CLINICAL_NOTE),
                                    and(userHasPrivilege(  PihEmrConfigConstants.PRIVILEGE_TASK_EMR_RETRO_CLINICAL_NOTE_THIS_PROVIDER_ONLY), patientVisitWithinPastThirtyDays(config))))));

            extensions.add(visitAction(CustomAppLoaderConstants.Extensions.PRIMARY_CARE_ADULT_INITIAL_VISIT_ACTION,
                    "ui.i18n.EncounterType.name." + PihEmrConfigConstants.ENCOUNTERTYPE_PRIMARY_CARE_ADULT_INITIAL_CONSULT_UUID,
                    "fas fa-fw fa-stethoscope",
                    "link",
                    enterStandardHtmlFormLink(PihCoreUtil.getFormResource("primary-care-adult-initial.xml") + "&returnUrl=/" + WebConstants.CONTEXT_PATH + "/" + patientVisitsPageWithSpecificVisitUrl),  // always redirect to visit page after clicking this link
                    null,
                    and(sessionLocationHasTag("Primary Care Consult Location"),
                            visitDoesNotHaveEncounterOfType(PihEmrConfigConstants.ENCOUNTERTYPE_PRIMARY_CARE_ADULT_INITIAL_CONSULT_UUID),
                            visitDoesNotHaveEncounterOfType(PihEmrConfigConstants.ENCOUNTERTYPE_PRIMARY_CARE_ADULT_FOLLOWUP_CONSULT_UUID),
                            not(patientHasPreviousEncounter(PihEmrConfigConstants.ENCOUNTERTYPE_PRIMARY_CARE_ADULT_INITIAL_CONSULT_UUID)),
                            or(patientIsAdult(), patientAgeUnknown(), patientDoesNotActiveVisit()),
                            or(and(userHasPrivilege(  PihEmrConfigConstants.PRIVILEGE_TASK_EMR_ENTER_PRIMARY_CARE_CONSULT_NOTE), patientHasActiveVisit()),
                                    userHasPrivilege(  PihEmrConfigConstants.PRIVILEGE_TASK_EMR_RETRO_CLINICAL_NOTE),
                                    and(userHasPrivilege(  PihEmrConfigConstants.PRIVILEGE_TASK_EMR_RETRO_CLINICAL_NOTE_THIS_PROVIDER_ONLY), patientVisitWithinPastThirtyDays(config))))));

            extensions.add(visitAction(CustomAppLoaderConstants.Extensions.PRIMARY_CARE_ADULT_FOLLOWUP_VISIT_ACTION,
                    "ui.i18n.EncounterType.name." + PihEmrConfigConstants.ENCOUNTERTYPE_PRIMARY_CARE_ADULT_FOLLOWUP_CONSULT_UUID,
                    "fas fa-fw fa-stethoscope",
                    "link",
                    enterStandardHtmlFormLink(PihCoreUtil.getFormResource("primary-care-adult-followup.xml") + "&returnUrl=/" + WebConstants.CONTEXT_PATH + "/" + patientVisitsPageWithSpecificVisitUrl),  // always redirect to visit page after clicking this link
                    null,
                    and(sessionLocationHasTag("Primary Care Consult Location"),
                            visitDoesNotHaveEncounterOfType(PihEmrConfigConstants.ENCOUNTERTYPE_PRIMARY_CARE_ADULT_INITIAL_CONSULT_UUID),
                            visitDoesNotHaveEncounterOfType(PihEmrConfigConstants.ENCOUNTERTYPE_PRIMARY_CARE_ADULT_FOLLOWUP_CONSULT_UUID),
                            or(patientIsAdult(), patientAgeUnknown(), patientDoesNotActiveVisit()),
                            or(and(userHasPrivilege(  PihEmrConfigConstants.PRIVILEGE_TASK_EMR_ENTER_PRIMARY_CARE_CONSULT_NOTE), patientHasActiveVisit()),
                                    userHasPrivilege(  PihEmrConfigConstants.PRIVILEGE_TASK_EMR_RETRO_CLINICAL_NOTE),
                                    and(userHasPrivilege(  PihEmrConfigConstants.PRIVILEGE_TASK_EMR_RETRO_CLINICAL_NOTE_THIS_PROVIDER_ONLY), patientVisitWithinPastThirtyDays(config))))));

        } else if (config.getCountry() == ConfigDescriptor.Country.MEXICO) {

            extensions.add(visitAction(CustomAppLoaderConstants.Extensions.MEXICO_CONSULT_ACTION,
                    "ui.i18n.EncounterType.name." + CesConfigConstants.ENCOUNTERTYPE_MEXICOCONSULT_UUID,
                    "fas fa-fw fa-stethoscope",
                    "link",
                    enterStandardHtmlFormLink(PihCoreUtil.getFormResource("consult.xml")),
                    null,
                    sessionLocationHasTag("Consult Note Location")));

            extensions.add(visitAction(CustomAppLoaderConstants.Extensions.MEXICO_CLINICAL_HISTORY_ACTION,
                    "ui.i18n.EncounterType.name.0d16a7c9-07fb-43f6-8984-dd7787f26a5a",  // Clinical History
                    "fas fa-fw fa-clipboard-list",
                    "link",
                    enterStandardHtmlFormLink(PihCoreUtil.getFormResource("clinicalHistory.xml")),
                    null,
                    sessionLocationHasTag("Consult Note Location")));

        } else if (config.getCountry() == ConfigDescriptor.Country.SIERRA_LEONE) {

            extensions.add(visitAction(CustomAppLoaderConstants.Extensions.SIERRA_LEONE_OUTPATIENT_INITIAL_VISIT_ACTION,
                    "ui.i18n.EncounterType.name." + SierraLeoneConfigConstants.ENCOUNTERTYPE_SIERRALEONEOUTPATIENTINITIAL_UUID,
                    "fas fa-fw fa-stethoscope",
                    "link",
                    enterStandardHtmlFormLink(PihCoreUtil.getFormResource("outpatient-initial.xml")
                            + "&returnUrl=/" + WebConstants.CONTEXT_PATH + "/" + patientVisitsPageWithSpecificVisitUrl),
                    null,
                    and(sessionLocationHasTag("Consult Note Location"),
                        visitDoesNotHaveEncounterOfType(SierraLeoneConfigConstants.ENCOUNTERTYPE_SIERRALEONEOUTPATIENTINITIAL_UUID),
                        visitDoesNotHaveEncounterOfType(SierraLeoneConfigConstants.ENCOUNTERTYPE_SIERRALEONEOUTPATIENTFOLLOWUP_UUID))));

            extensions.add(visitAction(CustomAppLoaderConstants.Extensions.SIERRA_LEONE_OUTPATIENT_FOLLOWUP_VISIT_ACTION,
                    "ui.i18n.EncounterType.name." + SierraLeoneConfigConstants.ENCOUNTERTYPE_SIERRALEONEOUTPATIENTFOLLOWUP_UUID,
                    "fas fa-fw fa-stethoscope",
                    "link",
                    enterStandardHtmlFormLink(PihCoreUtil.getFormResource("outpatient-followup.xml")
                            + "&returnUrl=/" + WebConstants.CONTEXT_PATH + "/" + patientVisitsPageWithSpecificVisitUrl),
                    null,
                    and(sessionLocationHasTag("Consult Note Location"),
                        visitDoesNotHaveEncounterOfType(SierraLeoneConfigConstants.ENCOUNTERTYPE_SIERRALEONEOUTPATIENTINITIAL_UUID),
                        visitDoesNotHaveEncounterOfType(SierraLeoneConfigConstants.ENCOUNTERTYPE_SIERRALEONEOUTPATIENTFOLLOWUP_UUID))));
        }

    }

    private void enableHIV() {
        enableHIVProgram();
        enableHIVForms();
        enableHIVActions();
    }

    private void enableHIVProgram() {

        String programUuid = PihEmrConfigConstants.PROGRAM_HIV_UUID;

        int firstColumnIndex = 0;
        int secondColumnIndex = 0;

        // FIRST COLUMN

        // HIV Status
        apps.add(addToHivDashboardFirstColumn(app(CustomAppLoaderConstants.Apps.HIV_ALERTS,
                        "pih.app.hiv.status.title",
                        "fas fa-fw fa-exclamation-circle",
                        null,
                        null,
                        objectNode(
                                "configFile", "hiv/hivStatuses.yml"
                        )),
                "pihcore", "dashboardwidgets/statusData",
                firstColumnIndex++
        ));

        // TB Status
        apps.add(addToHivDashboardFirstColumn(app(CustomAppLoaderConstants.Apps.HIV_TB_STATUS,
                        "pih.app.hivTb.status.title",
                        "fas fa-fw fa-exclamation-circle",
                        null,
                        null,
                        objectNode(
                                "configFile", "hivTb/statuses.yml"
                        )),
                "pihcore", "dashboardwidgets/statusData",
                firstColumnIndex++
        ));

        // HIV Intake
        apps.add(addToHivDashboardFirstColumn(app(CustomAppLoaderConstants.Apps.HIV_INTAKE_ENCOUNTERS,
                        "pih.app.hiv.intake.title",
                        "icon-calendar",
                        patientVisitsPageUrl,
                        null,
                        objectNode(
                                "encounterTypes", arrayNode(
                                        objectNode(
                                                "encounterType", PihEmrConfigConstants.ENCOUNTERTYPE_HIV_INTAKE_UUID,
                                                "url", patientVisitsPageWithSpecificVisitUrl
                                        )
                                )
                        )),
                "pihcore", "dashboardwidgets/encounters",
                firstColumnIndex++
        ));

        // HIV Followup
        apps.add(addToHivDashboardFirstColumn(app(CustomAppLoaderConstants.Apps.HIV_FOLLOWUP_ENCOUNTERS,
                        "pih.app.hiv.followup.title",
                        "icon-calendar",
                        patientVisitsPageUrl,
                        null,
                        objectNode(
                                "encounterTypes", arrayNode(
                                        objectNode(
                                                "encounterType", PihEmrConfigConstants.ENCOUNTERTYPE_HIV_FOLLOWUP_UUID,
                                                "url", patientVisitsPageWithSpecificVisitUrl
                                        )
                                ),
                                "maxToDisplay", "3"
                        )),
                "pihcore", "dashboardwidgets/encounters",
                firstColumnIndex++
        ));

        // HIV Dispensing
        apps.add(addToHivDashboardFirstColumn(app(CustomAppLoaderConstants.Apps.HIV_DISPENSING_ENCOUNTERS,
                        "pih.app.hiv.dispensing.title",
                        "icon-calendar",
                        patientVisitsPageUrl,
                        null,
                        objectNode(
                                "encounterTypes", arrayNode(
                                        objectNode(
                                                "encounterType", PihEmrConfigConstants.ENCOUNTERTYPE_HIV_DISPENSING_UUID,
                                                "url", patientVisitsPageWithSpecificVisitUrl
                                        )
                                ),
                                "maxToDisplay", "3"
                        )),
                "pihcore", "dashboardwidgets/encounters",
                firstColumnIndex++
        ));

        // HIV Socioeconomics
        apps.add(addToHivDashboardFirstColumn(app(CustomAppLoaderConstants.Apps.HIV_SOCIOECONOMIC_ENCOUNTERS,
                        "pih.app.hiv.socioeconomics.title",
                        "icon-calendar",
                        patientVisitsPageUrl,
                        null,
                        objectNode(
                                "encounterTypes", arrayNode(
                                        objectNode(
                                                "encounterType", PihEmrConfigConstants.ENCOUNTERTYPE_SOCIO_ECONOMICS_UUID,
                                                "url", patientVisitsPageWithSpecificVisitUrl
                                        )
                                )
                        )),
                "pihcore", "dashboardwidgets/encounters",
                firstColumnIndex++
        ));

        // Weight Graph
        apps.add(addToHivDashboardFirstColumn(app(CustomAppLoaderConstants.Apps.HIV_WEIGHT_GRAPH,
                        "pih.app.hivWeightGraph.title",
                        "fas fa-fw fa-chart-bar",
                        null,
                        null,
                        objectNode(
                                "widget", "obsgraph",
                                "icon", "fas fa-fw fa-chart-bar",
                                "label", "pih.app.hivWeightGraph.title",
                                "conceptId", CustomAppLoaderConstants.WEIGHT_CONCEPT_UUID,
                                "maxResults", "1000"
                        )),
                "coreapps", "dashboardwidgets/dashboardWidget",
                        firstColumnIndex++
        ));

        // SECOND COLUMN

        // Current Enrollment
        apps.add(addToHivDashboardSecondColumn(app(
                "pih.app." + programUuid + ".patientProgramSummary",
                "coreapps.currentEnrollmentDashboardWidget.label",
                "fas fa-fw fa-stethoscope",
                null,
                PihEmrConfigConstants.PRIVILEGE_APP_COREAPPS_PATIENT_DASHBOARD,
                objectNode(
                        "widget", "programstatus",
                        "icon", "fas fa-fw fa-stethoscope",
                        "label", "coreapps.currentEnrollmentDashboardWidget.label",
                        "program", programUuid,
                        "locationTag", "Program Location",
                        "markPatientDeadOutcome", config.isComponentEnabled(Components.MARK_PATIENT_DEAD) ? PihCoreConstants.PATIENT_DIED_CONCEPT_UUID : null,
                        "dashboard", programUuid   // provides contextual context so this widget knows which dashboard it's being rendered on
                )),
                "coreapps", "dashboardwidgets/dashboardWidget",
                secondColumnIndex++
        ));

        // Previous Enrollment TODO DO WE WANT TO KEEP THIS, IT ISN'T IN THE DESIGNS
        apps.add(addToHivDashboardSecondColumn(app(
                "pih.app." + programUuid + ".patientProgramHistory",
                "coreapps.programHistoryDashboardWidget.label",
                "fas fa-fw fa-stethoscope",
                null,
                PihEmrConfigConstants.PRIVILEGE_APP_COREAPPS_PATIENT_DASHBOARD,
                objectNode(
                        "icon", "fas fa-fw fa-stethoscope",
                        "label", "coreapps.programHistoryDashboardWidget.label",
                        "program", programUuid,
                        "includeActive", false,
                        "locationTag", "Program Location",
                        "markPatientDeadOutcome", config.isComponentEnabled(Components.MARK_PATIENT_DEAD) ? PihCoreConstants.PATIENT_DIED_CONCEPT_UUID : null,
                        "dashboard", programUuid   // provides contextual context so this widget knows which dashboard it's being rendered on
                )),
                "coreapps", "program/programHistory",
                secondColumnIndex++
        ));

        // TODO: Add ACTIVE MEDICATIONS HERE

        // Viral Load History
        apps.add(addToHivDashboardSecondColumn(app(CustomAppLoaderConstants.Apps.HIV_VIRAL_LOAD_HISTORY,
                        "pih.app.hiv.viralLoadHistory.title",
                        "fas fa-fw fa-history",
                        null,
                        null,
                        objectNode(
                                "widget", "obsacrossencounters",
                                "icon", "fas fa-fw fa-history",
                                "label", "pih.app.hiv.viralLoadHistory.title",
                                "encounterTypes", CustomAppLoaderConstants.LABORATORY_RESULT_UUID,
                                "concepts",
                                CustomAppLoaderConstants.VIRAL_LOAD_QUALITATIVE_UUID + "," +
                                        CustomAppLoaderConstants.VIRAL_LOAD_UUID + "," +
                                        CustomAppLoaderConstants.VIRAL_LOAD_LLD_UUID,
                                "headers", "zl.date,pihcore.status,pih.app.hiv.viralLoadHistory.load,pih.app.hiv.viralLoadHistory.lld",
                                "maxRecords", "5"
                        )),
                "coreapps", "dashboardwidgets/dashboardWidget",
                secondColumnIndex++
        ));

        // Viral Load
        apps.add(addToHivDashboardSecondColumn(app(CustomAppLoaderConstants.Apps.HIV_VL_GRAPH,
                "pih.app.hivvlGraph.title",
                "fas fa-fw fa-chart-bar",
                null,
                null,
                objectNode(
                        "widget", "obsgraph",
                        "icon", "fas fa-fw fa-chart-bar",
                        "label", "pih.app.hivvlGraph.title",
                        "conceptId", CustomAppLoaderConstants.VIRAL_LOAD_UUID,
                        "type", "logarithmic",
                        "maxResults", "1000"
                )),
                "coreapps", "dashboardwidgets/dashboardWidget",
                secondColumnIndex++
        ));

        // Adverse Reactions
        apps.add(addToHivDashboardSecondColumn(app(CustomAppLoaderConstants.Apps.HIV_ADVERSE_EFFECT,
                "pihcore.adverse.reactions",
                "fas fa-fw fa-allergies",
                patientVisitsPageUrl,
                null,
                objectNode(
                        "widget", "obsacrossencounters",
                        "icon", "fas fa-fw fa-allergies",
                        "label", "pihcore.adverse.reactions",
                        "detailsUrl", patientVisitsPageUrl,
                        "encounterTypes", PihEmrConfigConstants.ENCOUNTERTYPE_HIV_INTAKE_UUID + "," + PihEmrConfigConstants.ENCOUNTERTYPE_HIV_FOLLOWUP_UUID,
                        "concepts",
                        CustomAppLoaderConstants.ADVERSE_EFFECT_CONCEPT_UUID + "," +
                                CustomAppLoaderConstants.ADVERSE_EFFECT_DATE_CONCEPT_UUID,
                        "headers", "zl.date,pihcore.reaction,pihcore.on.date"
                )),
                "coreapps", "dashboardwidgets/dashboardWidget",
                secondColumnIndex++
        ));

        // TODO:  ADD PATIENT PROGRAMS WIDGET HERE

        configureBasicProgramSummaryApps(programUuid);
        configureBasicProgramExtensions(programUuid);
    }

    private void enableHIVForms() {
        enableHIVIntakeForm();

        Extension hivFollowup = visitAction(CustomAppLoaderConstants.Extensions.HIV_ZL_FOLLOWUP_VISIT_ACTION,
                "pih.task.hivFollowup.label",
                "fas fa-fw fa-ribbon",
                "link",
                enterStandardHtmlFormLink(PihCoreUtil.getFormResource("hiv/hiv-followup.xml") + "&returnUrl=/" + WebConstants.CONTEXT_PATH + "/" + patientVisitsPageWithSpecificVisitUrl),
                  PihEmrConfigConstants.PRIVILEGE_TASK_EMR_ENTER_HIV_CONSULT_NOTE,
                and(sessionLocationHasTag("HIV Consult Location"),
                        visitDoesNotHaveEncounterOfType(PihEmrConfigConstants.ENCOUNTERTYPE_HIV_INTAKE_UUID),
                        visitDoesNotHaveEncounterOfType(PihEmrConfigConstants.ENCOUNTERTYPE_HIV_FOLLOWUP_UUID),
                        or(and(userHasPrivilege(  PihEmrConfigConstants.PRIVILEGE_TASK_EMR_ENTER_HIV_CONSULT_NOTE), patientHasActiveVisit()),
                                userHasPrivilege(  PihEmrConfigConstants.PRIVILEGE_TASK_EMR_RETRO_CLINICAL_NOTE),
                                and(userHasPrivilege(  PihEmrConfigConstants.PRIVILEGE_TASK_EMR_RETRO_CLINICAL_NOTE_THIS_PROVIDER_ONLY), patientVisitWithinPastThirtyDays(config)))));

        extensions.add(hivFollowup);
        extensions.add(cloneAsHivVisitAction(hivFollowup));

        Extension hivDispensing = visitAction(CustomAppLoaderConstants.Extensions.HIV_ZL_DISPENSING_VISIT_ACTION,
                "pihcore.hivDispensing.short",
                "fas fa-fw fa-ribbon",
                "link",
                enterStandardHtmlFormLink(PihCoreUtil.getFormResource("hiv/hiv-dispensing.xml") + "&returnUrl=/" + WebConstants.CONTEXT_PATH + "/" + patientVisitsPageWithSpecificVisitUrl),
                  PihEmrConfigConstants.PRIVILEGE_TASK_EMR_ENTER_HIV_CONSULT_NOTE,
                and(sessionLocationHasTag("HIV Consult Location"),
                        or(and(userHasPrivilege(  PihEmrConfigConstants.PRIVILEGE_TASK_EMR_ENTER_HIV_CONSULT_NOTE), patientHasActiveVisit()),
                                userHasPrivilege(  PihEmrConfigConstants.PRIVILEGE_TASK_EMR_RETRO_CLINICAL_NOTE),
                                and(userHasPrivilege(  PihEmrConfigConstants.PRIVILEGE_TASK_EMR_RETRO_CLINICAL_NOTE_THIS_PROVIDER_ONLY), patientVisitWithinPastThirtyDays(config)))));

        extensions.add(hivDispensing);
        extensions.add(cloneAsHivVisitAction(hivDispensing));
        // circular app for dispensiing
        apps.add(addToHomePage(findPatientTemplateApp(CustomAppLoaderConstants.Apps.HIV_DISPENSING,
                "pihcore.hivDispensing.short",
                "fas fa-fw fa-ribbon",
                        PihEmrConfigConstants.PRIVILEGE_APP_DISPENSING_APP_DISPENSE,
                "/htmlformentryui/htmlform/enterHtmlFormWithStandardUi.page?patientId={{patientId}}&definitionUiResource=" + PihCoreUtil.getFormResource("hiv/hiv-dispensing.xml") + "&returnUrl=/" + WebConstants.CONTEXT_PATH + "/coreapps/findpatient/findPatient.page?app=" + CustomAppLoaderConstants.Apps.HIV_DISPENSING + "&returnLabel=pihcore.hivDispensing.short",
                null, config.getFindPatientColumnConfig()),
                sessionLocationHasTag("HIV Consult Location")));

        extensions.add(cloneAsHivVisitAction(findExtensionById(CustomAppLoaderConstants.Extensions.VITALS_CAPTURE_VISIT_ACTION)));
    }

    private void enableHIVIntakeForm() {
        Extension hivInitial = visitAction(CustomAppLoaderConstants.Extensions.HIV_ZL_INITIAL_VISIT_ACTION,
                "pih.task.hivIntake.label",
                "fas fa-fw fa-ribbon",
                "link",
                enterStandardHtmlFormLink(PihCoreUtil.getFormResource("hiv/hiv-intake.xml") + "&returnUrl=/" + WebConstants.CONTEXT_PATH + "/" + patientVisitsPageWithSpecificVisitUrl),
                  PihEmrConfigConstants.PRIVILEGE_TASK_EMR_ENTER_HIV_CONSULT_NOTE,
                and(sessionLocationHasTag("HIV Consult Location"),
                        visitDoesNotHaveEncounterOfType(PihEmrConfigConstants.ENCOUNTERTYPE_HIV_INTAKE_UUID),
                        visitDoesNotHaveEncounterOfType(PihEmrConfigConstants.ENCOUNTERTYPE_HIV_FOLLOWUP_UUID),
                        not(patientHasPreviousEncounter(PihEmrConfigConstants.ENCOUNTERTYPE_HIV_INTAKE_UUID)),
                        or(and(userHasPrivilege(  PihEmrConfigConstants.PRIVILEGE_TASK_EMR_ENTER_HIV_CONSULT_NOTE), patientHasActiveVisit()),
                                userHasPrivilege(  PihEmrConfigConstants.PRIVILEGE_TASK_EMR_RETRO_CLINICAL_NOTE),
                                and(userHasPrivilege(  PihEmrConfigConstants.PRIVILEGE_TASK_EMR_RETRO_CLINICAL_NOTE_THIS_PROVIDER_ONLY), patientVisitWithinPastThirtyDays(config)))));

        extensions.add(hivInitial);
        extensions.add(cloneAsHivVisitAction(hivInitial));
    }

    private void enableHIVActions() {
        extensions.add(overallAction(CustomAppLoaderConstants.Extensions.HIV_MEDICATION_LIST_OVERALL_ACTION,
                "pihcore.hivMedicationList.overallAction.label",
                "fas fa-fw fa-capsules",
                "link",
                "pihcore/meds/drugOrders.page?patient={{patient.uuid}}",
                  PihEmrConfigConstants.PRIVILEGE_APP_COREAPPS_PATIENT_DASHBOARD,
                null));
        extensions.add(cloneAsHivOverallAction(findExtensionById(CustomAppLoaderConstants.Extensions.HIV_MEDICATION_LIST_OVERALL_ACTION)));
    }


    private void enablePMTCTForms() {
        extensions.add(visitAction(CustomAppLoaderConstants.Extensions.PMTCT_INITIAL_VISIT_ACTION,
                "pih.task.pmtctIntake.label",
                "fas fa-fw fa-ribbon",
                "link",
                enterStandardHtmlFormLink(PihCoreUtil.getFormResource("hiv/pmtct-intake.xml") + "&returnUrl=/" + WebConstants.CONTEXT_PATH + "/" + patientVisitsPageWithSpecificVisitUrl),
                  PihEmrConfigConstants.PRIVILEGE_TASK_EMR_ENTER_HIV_CONSULT_NOTE,
                and(sessionLocationHasTag("HIV Consult Location"),
                        visitDoesNotHaveEncounterOfType(PihEmrConfigConstants.ENCOUNTERTYPE_PMTCT_INTAKE_UUID),
                        visitDoesNotHaveEncounterOfType(PihEmrConfigConstants.ENCOUNTERTYPE_PMTCT_FOLLOWUP_UUID),
                        and(patientIsFemale()),
                        or(and(userHasPrivilege(  PihEmrConfigConstants.PRIVILEGE_TASK_EMR_ENTER_HIV_CONSULT_NOTE), patientHasActiveVisit()),
                                userHasPrivilege(  PihEmrConfigConstants.PRIVILEGE_TASK_EMR_RETRO_CLINICAL_NOTE),
                                and(userHasPrivilege(  PihEmrConfigConstants.PRIVILEGE_TASK_EMR_RETRO_CLINICAL_NOTE_THIS_PROVIDER_ONLY), patientVisitWithinPastThirtyDays(config))))));

        extensions.add(visitAction(CustomAppLoaderConstants.Extensions.PMTCT_FOLLOWUP_VISIT_ACTION,
                "pih.task.pmtctFollowup.label",
                "fas fa-fw fa-ribbon",
                "link",
                enterStandardHtmlFormLink(PihCoreUtil.getFormResource("hiv/pmtct-followup.xml") + "&returnUrl=/" + WebConstants.CONTEXT_PATH + "/" + patientVisitsPageWithSpecificVisitUrl),
                  PihEmrConfigConstants.PRIVILEGE_TASK_EMR_ENTER_HIV_CONSULT_NOTE,
                and(sessionLocationHasTag("HIV Consult Location"),
                        visitDoesNotHaveEncounterOfType(PihEmrConfigConstants.ENCOUNTERTYPE_PMTCT_INTAKE_UUID),
                        visitDoesNotHaveEncounterOfType(PihEmrConfigConstants.ENCOUNTERTYPE_PMTCT_FOLLOWUP_UUID),
                        and(patientIsFemale()),
                        or(and(userHasPrivilege(  PihEmrConfigConstants.PRIVILEGE_TASK_EMR_ENTER_HIV_CONSULT_NOTE), patientHasActiveVisit()),
                                userHasPrivilege(  PihEmrConfigConstants.PRIVILEGE_TASK_EMR_RETRO_CLINICAL_NOTE),
                                and(userHasPrivilege(  PihEmrConfigConstants.PRIVILEGE_TASK_EMR_RETRO_CLINICAL_NOTE_THIS_PROVIDER_ONLY), patientVisitWithinPastThirtyDays(config))))));
    }

    private void enableEIDForm() {
        // ToDo:  Limit age to infant (18 months and younger)
        extensions.add(visitAction(CustomAppLoaderConstants.Extensions.EID_FOLLOWUP_VISIT_ACTION,
                "pih.task.eidFollowup.label",
                "fas fa-fw fa-baby",
                "link",
                enterStandardHtmlFormLink(PihCoreUtil.getFormResource("hiv/eid-followup.xml") + "&returnUrl=/" + WebConstants.CONTEXT_PATH + "/" + patientVisitsPageWithSpecificVisitUrl),
                  PihEmrConfigConstants.PRIVILEGE_TASK_EMR_ENTER_HIV_CONSULT_NOTE,
                and(sessionLocationHasTag("HIV Consult Location"),
                        visitDoesNotHaveEncounterOfType(PihEmrConfigConstants.ENCOUNTERTYPE_EID_FOLLOWUP_UUID),
                        and(patientAgeInMonthsLessThanAtVisitStart(24)),
                        or(and(userHasPrivilege(  PihEmrConfigConstants.PRIVILEGE_TASK_EMR_ENTER_HIV_CONSULT_NOTE), patientHasActiveVisit()),
                                userHasPrivilege(  PihEmrConfigConstants.PRIVILEGE_TASK_EMR_RETRO_CLINICAL_NOTE),
                                and(userHasPrivilege(  PihEmrConfigConstants.PRIVILEGE_TASK_EMR_RETRO_CLINICAL_NOTE_THIS_PROVIDER_ONLY), patientVisitWithinPastThirtyDays(config))))));
    }

    private void enableCovid19() {

        // ToDo: Fix privileges and locations for these forms.
        enableCovid19IntakeForm();

        extensions.add(visitAction(CustomAppLoaderConstants.Extensions.COVID19_FOLLOWUP_VISIT_ACTION,
                "ui.i18n.EncounterType.name." + PihEmrConfigConstants.ENCOUNTERTYPE_COVID19_FOLLOWUP_UUID,
                "fab fa-fw fa-first-order-alt",
                "link",
                enterStandardHtmlFormLink(PihCoreUtil.getFormResource("covid19Followup.xml")),
                  PihEmrConfigConstants.PRIVILEGE_TASK_EMR_ENTER_COVID,
                and(sessionLocationHasTag("COVID-19 Location"),
                    visitHasEncounterOfType(PihEmrConfigConstants.ENCOUNTERTYPE_COVID19_INTAKE_UUID))));

        extensions.add(visitAction(CustomAppLoaderConstants.Extensions.COVID19_DISCHARGE_VISIT_ACTION,
                "ui.i18n.EncounterType.name." + PihEmrConfigConstants.ENCOUNTERTYPE_COVID19_DISCHARGE_UUID,
                "fab fa-fw fa-first-order-alt",
                "link",
                enterStandardHtmlFormLink(PihCoreUtil.getFormResource("covid19Discharge.xml")),
                  PihEmrConfigConstants.PRIVILEGE_TASK_EMR_ENTER_COVID,
                and(sessionLocationHasTag("COVID-19 Location"),
                        visitDoesNotHaveEncounterOfType(PihEmrConfigConstants.ENCOUNTERTYPE_COVID19_DISCHARGE_UUID),
                        visitHasEncounterOfType(PihEmrConfigConstants.ENCOUNTERTYPE_COVID19_INTAKE_UUID))));
    }

    private void enableCovid19IntakeForm() {
        extensions.add(visitAction(CustomAppLoaderConstants.Extensions.COVID19_INITIAL_VISIT_ACTION,
                "ui.i18n.EncounterType.name." + PihEmrConfigConstants.ENCOUNTERTYPE_COVID19_INTAKE_UUID,
                "fab fa-fw fa-first-order-alt",
                "link",
                enterStandardHtmlFormLink(PihCoreUtil.getFormResource("covid19Intake.xml")),
                  PihEmrConfigConstants.PRIVILEGE_TASK_EMR_ENTER_COVID,
                and(sessionLocationHasTag("COVID-19 Location"),
                        visitDoesNotHaveEncounterOfType(PihEmrConfigConstants.ENCOUNTERTYPE_COVID19_INTAKE_UUID))));
    }

    private void enableTuberculosis() {
        extensions.add(visitAction(CustomAppLoaderConstants.Extensions.TB_INITIAL_VISIT_ACTION,
                "ui.i18n.EncounterType.name." + PihEmrConfigConstants.ENCOUNTERTYPE_TB_INTAKE_UUID,
                "fas fa-fw fa-wind",
                "link",
                enterStandardHtmlFormLink(PihCoreUtil.getFormResource("tbIntake.xml")),
                  PihEmrConfigConstants.PRIVILEGE_TASK_EMR_ENTER_CONSULT_NOTE,
                sessionLocationHasTag("Consult Note Location")));
    }

    private void enableOvc() {
        configureBasicProgramDashboard(PihEmrConfigConstants.PROGRAM_OVC_UUID);

        extensions.add(visitAction(CustomAppLoaderConstants.Extensions.OVC_INITIAL_VISIT_ACTION,
                "ui.i18n.EncounterType.name." + PihEmrConfigConstants.ENCOUNTERTYPE_OVC_INTAKE_UUID,
                "fas fa-fw fa-child",
                "link",
                enterStandardHtmlFormLink(PihCoreUtil.getFormResource("ovcIntake.xml")),
                null,
                and(or(patientAgeUnknown(), patientAgeLessThanOrEqualToAtVisitStart(21)),
                        visitDoesNotHaveEncounterOfType(PihEmrConfigConstants.ENCOUNTERTYPE_OVC_INTAKE_UUID),
                        visitDoesNotHaveEncounterOfType(PihEmrConfigConstants.ENCOUNTERTYPE_OVC_FOLLOWUP_UUID))));

        extensions.add(visitAction(CustomAppLoaderConstants.Extensions.OVC_FOLLOWUP_VISIT_ACTION,
                "ui.i18n.EncounterType.name." + PihEmrConfigConstants.ENCOUNTERTYPE_OVC_FOLLOWUP_UUID,
                "fas fa-fw fa-child",
                "link",
                enterStandardHtmlFormLink(PihCoreUtil.getFormResource("ovcFollowup.xml")),
                null,
                and(or(patientAgeUnknown(), patientAgeLessThanOrEqualToAtVisitStart(21)),
                        visitDoesNotHaveEncounterOfType(PihEmrConfigConstants.ENCOUNTERTYPE_OVC_INTAKE_UUID),
                        visitDoesNotHaveEncounterOfType(PihEmrConfigConstants.ENCOUNTERTYPE_OVC_FOLLOWUP_UUID))));
    }

    private void enableMarkPatientDead() {

        extensions.add(overallAction(CustomAppLoaderConstants.Extensions.MARK_PATIENT_DEAD_OVERALL_ACTION,
                "coreapps.markPatientDead.label",
                "icon-plus-sign-alt",
                "link",
                "coreapps/markPatientDead.page?patientId={{patient.uuid}}&defaultDead=true",
                  PihEmrConfigConstants.PRIVILEGE_TASK_MARK_PATIENT_DEAD,
               null));
    }

    // not currently used
    private void enableRehab() {
        extensions.add(visitAction(CustomAppLoaderConstants.Extensions.REHAB_VISIT_ACTION,
                "pihcore.ncd.rehab",
                "fas fa-fw fa-user-injured",
                "link",
                enterStandardHtmlFormLink(PihCoreUtil.getFormResource("retired/physicalRehab.xml")),
                  PihEmrConfigConstants.PRIVILEGE_TASK_EMR_ENTER_CONSULT_NOTE,
                and(sessionLocationHasTag("Consult Note Location"),
                        visitDoesNotHaveEncounterOfType(PihEmrConfigConstants.ENCOUNTERTYPE_REHAB_EVAL_UUID))));
    }

    private void enableHIViSantePlus() {
        // iSantePlus forms were added but  should not appear
        extensions.add(visitAction(CustomAppLoaderConstants.Extensions.HIV_ADULT_INITIAL_VISIT_ACTION,
                "pih.task.hivIntakeISantePlus.label",
                "fas fa-fw fa-ribbon",
                "link",
                enterStandardHtmlFormLink(PihCoreUtil.getFormResource("hiv/iSantePlus/SaisiePremiereVisiteAdult.xml")),
                  PihEmrConfigConstants.PRIVILEGE_TASK_EMR_ENTER_HIV_CONSULT_NOTE,
                and(patientIsAdult())));

        extensions.add(visitAction(CustomAppLoaderConstants.Extensions.HIV_PEDS_INITIAL_VISIT_ACTION,
                "pih.task.hivIntakeISantePlus.label",
                "fas fa-fw fa-ribbon",
                "link",
                enterStandardHtmlFormLink(PihCoreUtil.getFormResource("hiv/iSantePlus/SaisiePremiereVisitePediatrique.xml")),
                null,
                and(patientIsChild())));

        extensions.add(visitAction(CustomAppLoaderConstants.Extensions.HIV_ADULT_FOLLOWUP_VISIT_ACTION,
                "pih.task.hivFollowupISantePlus.label",
                "fas fa-fw fa-ribbon",
                "link",
                enterStandardHtmlFormLink(PihCoreUtil.getFormResource("hiv/iSantePlus/VisiteDeSuivi.xml")),
                  PihEmrConfigConstants.PRIVILEGE_TASK_EMR_ENTER_HIV_CONSULT_NOTE,
                and(patientIsAdult())));

        extensions.add(visitAction(CustomAppLoaderConstants.Extensions.HIV_PEDS_FOLLOWUP_VISIT_ACTION,
                "pih.task.hivFollowupISantePlus.label",
                "fas fa-fw fa-ribbon",
                "link",
                enterStandardHtmlFormLink(PihCoreUtil.getFormResource("hiv/iSantePlus/VisiteDeSuiviPediatrique.xml")),
                  PihEmrConfigConstants.PRIVILEGE_TASK_EMR_ENTER_HIV_CONSULT_NOTE,
                and(patientIsChild())));

        extensions.add(visitAction(CustomAppLoaderConstants.Extensions.HIV_ADHERENCE_VISIT_ACTION,
                "pih.task.hivAdherence.label",
                "fas fa-fw fa-ribbon",
                "link",
                enterStandardHtmlFormLink(PihCoreUtil.getFormResource("hiv/iSantePlus/Adherence.xml")),
                  PihEmrConfigConstants.PRIVILEGE_TASK_EMR_ENTER_HIV_CONSULT_NOTE,
                null));
    }

    private void enableAsthmaProgram() {
        configureBasicProgramDashboard(PihEmrConfigConstants.PROGRAM_ASTHMA_UUID);

        apps.add(addToAsthmaDashboardFirstColumn(app(CustomAppLoaderConstants.Apps.ASTHMA_SYMPTOMS_OBS_TABLE,
                "pih.app.asthma.symptomsObsTable.title",
                "fas fa-fw fa-chart-bar",
                null,
                null,
                objectNode(
                        "widget", "obsacrossencounters",
                        "icon", "fas fa-fw fa-list-alt",
                        "label", "pih.app.asthma.symptomsObsTable.title",
                        "concepts", CustomAppLoaderConstants.ASTHMA_DAYTIME_SYMPTOMS_TWICE_WEEKLY + ','
                                + CustomAppLoaderConstants.ASTHMA_DAYTIME_SYMPTOMS_ONCE_WEEKLY + ','
                                + CustomAppLoaderConstants.ASTHMA_MEDS_TWICE_WEEKLY + ','
                                + CustomAppLoaderConstants.LIMITATION_OF_ACTIVITY,
                        "maxRecords", "40"  // MEX-127
                )),
                "coreapps", "dashboardwidgets/dashboardWidget"));
    }

    private void enableDiabetesProgram() {

        configureBasicProgramDashboard(PihEmrConfigConstants.PROGRAM_DIABETES_UUID);

        apps.add(addToDiabetesDashboardFirstColumn(app(CustomAppLoaderConstants.Apps.ABDOMINAL_CIRCUMFERENCE_GRAPH,
                "pih.app.abdominalCircumference.graph.title",
                "fas fa-fw fa-chart-bar",
                null,
                null,
                objectNode(
                        "widget", "obsgraph",
                        "label", "pih.app.abdominalCircumference.graph.title",
                        "icon", "fas fa-fw fa-chart-bar",
                        "conceptId", CustomAppLoaderConstants.ABDOMINAL_CIRCUMFERENCE_CONCEPT_UUID,
                        "maxRecords", "4"
                )),
                "coreapps", "dashboardwidgets/dashboardWidget"));

        apps.add(addToDiabetesDashboardFirstColumn(app(CustomAppLoaderConstants.Apps.FOOT_EXAM_OBS_TABLE,
                "pih.app.footExamObsTable.title",
                "fas fa-fw fa-chart-bar",
                null,
                null,
                objectNode(
                        "widget", "obsacrossencounters",
                        "icon", "fas fa-fw fa-list-alt",
                        "label", "pih.app.footExamObsTable.title",
                        "concepts", CustomAppLoaderConstants.FOOT_EXAM_CONCEPT_UUID,
                        "maxRecords", "100"  // MEX-127 - should be ten or so rows
                )),
                "coreapps", "dashboardwidgets/dashboardWidget"));

        apps.add(addToDiabetesDashboardFirstColumn(app(CustomAppLoaderConstants.Apps.URINARY_ALBUMIN_OBS_TABLE,
                "pih.app.urinaryAlbuminObsTable.title",
                "fas fa-fw fa-chart-bar",
                null,
                null,
                objectNode(
                        "widget", "obsacrossencounters",
                        "icon", "fas fa-fw fa-list-alt",
                        "label", "pih.app.urinaryAlbuminObsTable.title",
                        "concepts", CustomAppLoaderConstants.URINARY_ALBUMIN_CONCEPT_UUID,
                        "maxRecords", "10"  // MEX-127 - should be 3 rows
                )),
                "coreapps", "dashboardwidgets/dashboardWidget"));

        apps.add(addToDiabetesDashboardFirstColumn(app(CustomAppLoaderConstants.Apps.ALC_TOBAC_USE_SUMMARY,
                "pih.app.patientSummary.title",
                "fas fa-fw fa-user-md",
                null,
                null,
                objectNode(
                        "widget", "latestobsforconceptlist",
                        "icon", "fas fa-fw fa-user-md",
                        "label", "pih.app.patientSummary.title",
                        "concepts", CustomAppLoaderConstants.ALCOHOL_USE_CONCEPT_UUID + ','
                                + CustomAppLoaderConstants.TOBACCO_USE_CONCEPT_UUID
                )),
                "coreapps", "dashboardwidgets/dashboardWidget"));

        apps.add(addToDiabetesDashboardSecondColumn(app(CustomAppLoaderConstants.Apps.GLUCOSE_GRAPH,
                "pih.app.glucose.graph.title",
                "fas fa-fw fa-chart-bar",
                null,
                null,
                objectNode(
                        "widget", "obsgraph",
                        "label", "pih.app.glucose.graph.title",
                        "icon", "fas fa-fw fa-chart-bar",
                        "conceptId", CustomAppLoaderConstants.GLUCOSE_CONCEPT_UUID,
                        "maxRecords", "12"
                )),
                "coreapps", "dashboardwidgets/dashboardWidget"));

        apps.add(addToDiabetesDashboardSecondColumn(app(CustomAppLoaderConstants.Apps.HBA1C_GRAPH,
                "pih.app.hba1c.graph.title",
                "fas fa-fw fa-chart-bar",
                null,
                null,
                objectNode(
                        "widget", "obsgraph",
                        "label", "pih.app.hba1c.graph.title",
                        "icon", "fas fa-fw fa-chart-bar",
                        "conceptId", CustomAppLoaderConstants.HBA1C_CONCEPT_UUID,
                        "maxRecords", "4"
                )),
                "coreapps", "dashboardwidgets/dashboardWidget"));

        apps.add(addToDiabetesDashboardSecondColumn(
                graphs.getCholesterolGraph(".diabetes"),
                "coreapps",
                "dashboardwidgets/dashboardWidget"));
    }

    private void enableEpilepsyProgram() {

        configureBasicProgramDashboard(PihEmrConfigConstants.PROGRAM_EPILEPSY_UUID);

        apps.add(addToEpilepsyDashboardSecondColumn(app(CustomAppLoaderConstants.Apps.EPILEPSY_SUMMARY,
                "pih.app.patientSummary.title",
                "fas fa-fw fa-user-md",
                null,
                null,
                objectNode(
                        "widget", "latestobsforconceptlist",
                        "icon", "fas fa-fw fa-user-md",
                        "label", "pih.app.patientSummary.title",
                        "concepts", CustomAppLoaderConstants.EPI_SEIZURES_BASELINE
                )),
                "coreapps", "dashboardwidgets/dashboardWidget"));

        apps.add(addToEpilepsyDashboardSecondColumn(app(CustomAppLoaderConstants.Apps.EPILEPSY_SEIZURES,
                "pih.app.epilepsy.seizureGraph",
                "fas fa-fw fa-chart-bar",
                null,
                null,
                objectNode(
                        "widget", "obsgraph",
                        "label", "pih.app.epilepsy.seizureGraph",
                        "icon", "fas fa-fw fa-chart-bar",
                        "conceptId", CustomAppLoaderConstants.EPI_SEIZURES,
                        "maxResults", "30"  // MEX-127
                )),
                "coreapps", "dashboardwidgets/dashboardWidget"));

    }

    private void enableHypertensionProgram() {

        configureBasicProgramDashboard(PihEmrConfigConstants.PROGRAM_HYPERTENSION_UUID);

        apps.add(addToHypertensionDashboardFirstColumn(
                graphs.getBloodPressureGraph(".htn"),
                "coreapps",
                "dashboardwidgets/dashboardWidget"));

        apps.add(addToHypertensionDashboardFirstColumn(app(CustomAppLoaderConstants.Apps.BLOOD_PRESSURE_OBS_TABLE,
                "pih.app.bloodPressure.obsTable.title",
                "fas fa-fw fa-chart-bar",
                null,
                null,
                objectNode(
                        "widget", "obsacrossencounters",
                        "icon", "fas fa-fw fa-list-alt",
                        "label", "pih.app.bloodPressure.obsTable.title",
                        "concepts", CustomAppLoaderConstants.SYSTOLIC_BP_CONCEPT_UUID + ","
                                + CustomAppLoaderConstants.DIASTOLIC_BP_CONCEPT_UUID,
                        "maxRecords", "100"
                )),
                "coreapps", "dashboardwidgets/dashboardWidget"));

        apps.add(addToHypertensionDashboardSecondColumn(
                graphs.getBmiGraph(".htn"),
                "coreapps",
                "dashboardwidgets/dashboardWidget"));

        apps.add(addToHypertensionDashboardSecondColumn(
                graphs.getCholesterolGraph(".htn"),
                "coreapps",
                "dashboardwidgets/dashboardWidget"));
    }


    private void enableMentalHealthProgram() {
        configureBasicProgramDashboard(PihEmrConfigConstants.PROGRAM_MENTALHEALTH_UUID);

        if (config.getCountry().equals(ConfigDescriptor.Country.MEXICO) || config.getCountry().equals(ConfigDescriptor.Country.LIBERIA)) {
            apps.add(addToMentalHealthDashboardSecondColumn(
                    graphs.getPHQ9Graph(CustomAppLoaderConstants.ExtensionPoints.CLINICIAN_DASHBOARD_SECOND_COLUMN),
                    "coreapps",
                    "dashboardwidgets/dashboardWidget"));
        }

        if (config.getCountry().equals(ConfigDescriptor.Country.HAITI)) {
                apps.add(addToMentalHealthDashboardSecondColumn(
                        graphs.getWHODASGraph(CustomAppLoaderConstants.ExtensionPoints.CLINICIAN_DASHBOARD_SECOND_COLUMN),
                        "coreapps",
                        "dashboardwidgets/dashboardWidget"));
                }

        if (config.getCountry().equals(ConfigDescriptor.Country.HAITI)) {
            apps.add(addToMentalHealthDashboardSecondColumn(
                    graphs.getZLDSIGraph(CustomAppLoaderConstants.ExtensionPoints.CLINICIAN_DASHBOARD_SECOND_COLUMN),
                    "coreapps",
                    "dashboardwidgets/dashboardWidget"));
        }

        if (config.getCountry().equals(ConfigDescriptor.Country.HAITI) || config.getCountry().equals(ConfigDescriptor.Country.LIBERIA)) {
            apps.add(addToMentalHealthDashboardSecondColumn(
                    graphs.getSeizureFrequencyGraph(CustomAppLoaderConstants.ExtensionPoints.CLINICIAN_DASHBOARD_SECOND_COLUMN),
                    "coreapps",
                    "dashboardwidgets/dashboardWidget"));
        }

        if (config.getCountry().equals(ConfigDescriptor.Country.MEXICO)) {
            apps.add(addToMentalHealthDashboardSecondColumn(
                    graphs.getGAD7Graph(CustomAppLoaderConstants.ExtensionPoints.CLINICIAN_DASHBOARD_SECOND_COLUMN),
                    "coreapps",
                    "dashboardwidgets/dashboardWidget"));
        }
    }

    private void enableMalnutritionProgram() {
        configureBasicProgramDashboard(PihEmrConfigConstants.PROGRAM_MALNUTRITION_UUID);

        apps.add(addToMalnutritionDashboardSecondColumn(
                graphs.getBmiGraph(".malnutrition"),
                "coreapps",
                "dashboardwidgets/dashboardWidget"));

        apps.add(addToMalnutritionDashboardSecondColumn(app(CustomAppLoaderConstants.Apps.HEAD_CIRCUMFERENCE_GRAPH,
                "pih.app.headCircumferenceGraph.title",
                "fas fa-fw fa-chart-bar",
                null,
                null,
                objectNode(
                        "widget", "obsgraph",
                        "icon", "fas fa-fw fa-chart-bar",
                        "conceptId", CustomAppLoaderConstants.HEAD_CIRC_CONCEPT_UUID,
                        "maxResults", "12"
                )),
                "coreapps", "dashboardwidgets/dashboardWidget"));

    }

    private void enableBiometrics(Config config) {

        extensions.add(fragmentExtension(CustomAppLoaderConstants.Extensions.BIOMETRICS_FIND_PATIENT,
                "registrationapp",
                "biometrics/fingerprintSearch",
                null,   // shouldn't need a privilege, since this is injected into the patient search
                CustomAppLoaderConstants.ExtensionPoints.PATIENT_SEARCH,
                map("scanUrl", config.getBiometricsConfig().getScanUrl(),
                        "devicesUrl", config.getBiometricsConfig().getDevicesUrl())));


        apps.add(addToRegistrationSummarySecondColumnContent(app(CustomAppLoaderConstants.Apps.BIOMETRICS_SUMMARY,
                "registrationapp.biometrics.summary",
                "fas fa-fw fa-fingerprint",
                null,
                "App: registrationapp.registerPatient",
                objectNode(
                        "registrationAppId", CustomAppLoaderConstants.Apps.PATIENT_REGISTRATION,
                        "icon", "fas fa-fw fa-fingerprint")),
                "registrationapp",
                "summary/biometricsSummary"));
    }

    private void enablePathologyTracking() {

        apps.add(addToHomePage(app(CustomAppLoaderConstants.Apps.PATHOLOGY_TRACKING,
                "labtrackingapp.app.label",
                "fas fa-fw fa-microscope",
                "/labtrackingapp/labtrackingViewQueue.page?appId=" + CustomAppLoaderConstants.Apps.PATHOLOGY_TRACKING,
                  PihEmrConfigConstants.PRIVILEGE_APP_LAB_TRACKING_MONITOR_ORDERS,
                null),
                sessionLocationHasTag("Order Pathology Location")));

        extensions.add(visitAction(CustomAppLoaderConstants.Extensions.ORDER_LAB_VISIT_ACTION,
                "labtrackingapp.orderPathology.label",
                "fas fa-fw fa-microscope",
                "link",
                "/labtrackingapp/labtrackingAddOrder.page?patientId={{patient.uuid}}&visitId={{visit.id}}",
                  PihEmrConfigConstants.PRIVILEGE_TASK_LAB_TRACKING_PLACE_ORDERS,
                sessionLocationHasTag("Order Pathology Location")));

        apps.add(addToClinicianDashboardSecondColumn(app(CustomAppLoaderConstants.Apps.PATHOLOGY_SUMMARY,
                "labtrackingapp.pathology",
                "fas fa-fw fa-microscope",
                null,
                  PihEmrConfigConstants.PRIVILEGE_TASK_LAB_TRACKING_PLACE_ORDERS,
                null),
                "labtrackingapp", "labtrackingPatientDashboard"));
    }

    private void enableLabs() throws UnsupportedEncodingException {
        /* this really represents the Labs component, that has a sub-menu linking to multiple apps*/
        apps.add(addToHomePage(app(CustomAppLoaderConstants.Apps.LABS,
                "pih.app.labs.label",
                "fas fa-vials",
                "owa/labworkflow/index.html",
                  PihEmrConfigConstants.PRIVILEGE_APP_LABS,
                null),
                sessionLocationHasTag("Labs Component Location")));

        // note that this is only currently accessed via the Lab Workflow "Add Order" button, and the returnUrl and afterAddOrderUrl are both hardcoded below for this
        apps.add(findPatientTemplateApp(CustomAppLoaderConstants.Apps.ORDER_LABS,
                "pih.app.labs.ordering",
                "icon",
                  PihEmrConfigConstants.PRIVILEGE_TASK_ORDER_LABS,
                "/owa/orderentry/index.html?patient={{patientId}}&page=laborders&breadcrumbOverride={{breadcrumbOverride}}&returnUrl="
                        + URLEncoder.encode("/" + WebConstants.CONTEXT_PATH + "/owa/labworkflow/index.html","UTF-8")
						+ "&afterAddOrderUrl="
                        + URLEncoder.encode("/" + WebConstants.CONTEXT_PATH + "/owa/labworkflow/index.html", "UTF-8"),
                arrayNode(objectNode("icon", "fas fa-fw fa-home", "link", "/index.htm"),
                        objectNode("label", "pih.app.labs.label", "link", "/owa/labworkflow/index.html"),
                        objectNode("label", "coreapps.findPatient.app.label")),
                config.getFindPatientColumnConfig()
                ));

        extensions.add(overallAction(CustomAppLoaderConstants.Extensions.ORDER_LABS_OVERALL_ACTION,
                "pihcore.orderLabs.overallAction.label",
                "fas fa-fw fa-vials",
                "link",
                "owa/orderentry/index.html?patient={{patient.uuid}}&page=laborders",
                  PihEmrConfigConstants.PRIVILEGE_TASK_ORDER_LABS,
                null));

        extensions.add(overallAction(CustomAppLoaderConstants.Extensions.VIEW_LABS_OVERALL_ACTION,
                "pihcore.viewLabs.overallAction.label",
                "fas fa-fw fa-stream",
                "link",
                "owa/labworkflow/index.html?patient={{patient.uuid}}#/LabResults",
                  PihEmrConfigConstants.PRIVILEGE_TASK_VIEW_LABS,
                null));

        apps.add(addToClinicianDashboardFirstColumn(app(CustomAppLoaderConstants.Apps.COVID_LAB_RESULTS,
                "pihcore.labResults.covid",
                "fab fa-fw fa-first-order-alt",
                null,
                null,
                objectNode(
                        "widget", "latestObsForConceptList",
                        "icon", "fab fa-fw fa-first-order-alt",
                        "label", "pihcore.labResults.covid",
                        "concepts", CustomAppLoaderConstants.SARS_COV2_ANTIBODY_TEST + "," + CustomAppLoaderConstants.SARS_COV2_ANTIGEN_TEST + "," + CustomAppLoaderConstants.SARS_COV2_RT_PCR_TEST + "," + CustomAppLoaderConstants.SARS_COV2_XPERT_TEST,
                        "conceptNameType", "shortName",
                        "maxRecords", "4"
                )),
                "coreapps", "dashboardwidgets/dashboardWidget"));

    }

    private void enableGrowthChart() {

        extensions.add(overallAction(CustomAppLoaderConstants.Extensions.VIEW_GROWTH_CHART_ACTION,
                "pihcore.viewGrowthChart.overallAction.label",
                "fas fa-fw fa-chart-line",
                "link",
                "growthchart/growthCharts.page?patientId={{patient.uuid}}",
                  PihEmrConfigConstants.PRIVILEGE_TASK_VIEW_GROWTH_CHARTS,
                null));
    }

    private void enableCohortBuilder() {

        apps.add(addToHomePage(app(CustomAppLoaderConstants.Apps.COHORT_BUILDER_APP,
                "pih.app.cohortBuilder.label",
                "fas fa-fw icon-check-in",
                "owa/cohortbuilder/index.html#/",
                   PihEmrConfigConstants.PRIVILEGE_APP_COHORT_BUILDER,null)));

    }

    private void enableHaitiHIVLink() {
        extensions.add(extension(
                CustomAppLoaderConstants.Extensions.HAITI_HIV_EMR_DASHBOARD_LINK,
                "pihcore.hiv.viewPatientInHivEmr",
                "icon-external-link",
                "link",
                PihCoreUtil.getSystemOrRuntimeProperty(PihCoreConstants.HAITI_HIV_EMR_LINK_URL_RUNTIME_PROPERTY) + "?patientId={{identifier}}&identifierType=" + ZlConfigConstants.PATIENTIDENTIFIERTYPE_HIVEMRV1_UUID + "&dashboard="+ PihEmrConfigConstants.PROGRAM_HIV_UUID,
                null,
                null,
                "patientHeader.extraIdentifierLinks",
                1,
                map("identifierType", ZlConfigConstants.PATIENTIDENTIFIERTYPE_HIVEMRV1_UUID)
        ));
    }

    private void enablePrograms(Config config) {

        List<String> supportedPrograms = new ArrayList<String>();

        if (config.isComponentEnabled(Components.ANC_PROGRAM)) {
            supportedPrograms.add(PihEmrConfigConstants.PROGRAM_ANC_UUID);
            enableANCProgram();
        }

        if (config.isComponentEnabled(Components.ASTHMA_PROGRAM)) {
            supportedPrograms.add(PihEmrConfigConstants.PROGRAM_ASTHMA_UUID);
            enableAsthmaProgram();
        }

        if (config.isComponentEnabled(Components.DIABETES_PROGRAM)) {
            supportedPrograms.add(PihEmrConfigConstants.PROGRAM_DIABETES_UUID);
            enableDiabetesProgram();
        }

        if (config.isComponentEnabled(Components.EPILEPSY_PROGRAM)) {
            supportedPrograms.add(PihEmrConfigConstants.PROGRAM_EPILEPSY_UUID);
            enableEpilepsyProgram();
        }

        if (config.isComponentEnabled(Components.HIV)) {
            supportedPrograms.add(PihEmrConfigConstants.PROGRAM_HIV_UUID);
            enableHIV();
        }

        if (config.isComponentEnabled(Components.HIV_PROGRAM)) {
            supportedPrograms.add(PihEmrConfigConstants.PROGRAM_HIV_UUID);
            enableHIVProgram();
        }

        if (config.isComponentEnabled(Components.HIV_FORMS)) {
            enableHIVForms();
        }

        if (config.isComponentEnabled(Components.HIV_INTAKE_FORM)) {
            enableHIVIntakeForm();
        }

        if (config.isComponentEnabled(Components.PMTCT)) {
            enablePMTCTForms();
        }

        if (config.isComponentEnabled(Components.EXP_INFANT)) {
            supportedPrograms.add(PihEmrConfigConstants.PROGRAM_EID_UUID);
            configureBasicProgramDashboard(PihEmrConfigConstants.PROGRAM_EID_UUID);
            enableEIDForm();
        }

        if (config.isComponentEnabled(Components.HYPERTENSION_PROGRAM)) {
            supportedPrograms.add(PihEmrConfigConstants.PROGRAM_HYPERTENSION_UUID);
            enableHypertensionProgram();
        }

        if (config.isComponentEnabled(Components.MALNUTRITION_PROGRAM)) {
            supportedPrograms.add(PihEmrConfigConstants.PROGRAM_MALNUTRITION_UUID);
            enableMalnutritionProgram();
        }

        if (config.isComponentEnabled(Components.MENTAL_HEALTH)) {
            enableMentalHealthForm();
            supportedPrograms.add(PihEmrConfigConstants.PROGRAM_MENTALHEALTH_UUID);
            enableMentalHealthProgram();
        }

        if (config.isComponentEnabled(Components.MENTAL_HEALTH_FORM)) {
            enableMentalHealthForm();
        }

        if (config.isComponentEnabled(Components.MENTAL_HEALTH_PROGRAM)) {
            supportedPrograms.add(PihEmrConfigConstants.PROGRAM_MENTALHEALTH_UUID);
            enableMentalHealthProgram();
        }

        if (config.isComponentEnabled(Components.NCD)) {
            supportedPrograms.add(PihEmrConfigConstants.PROGRAM_NCD_UUID);
            enableNCDs();

            if (config.isComponentEnabled(Components.ECHO)) {
                enableEcho();
            }
        }

        if (config.isComponentEnabled(Components.OVC)) {
            supportedPrograms.add(PihEmrConfigConstants.PROGRAM_OVC_UUID);
            enableOvc();
        }

        if (config.isComponentEnabled(Components.VACCINATION_FORM)) {
            enableVaccinationOnly();
        }

        if (config.isComponentEnabled(Components.ONCOLOGY)) {
            supportedPrograms.add(PihEmrConfigConstants.PROGRAM_ONCOLOGY_UUID);
            enableOncology();
        }

        if (config.isComponentEnabled(Components.MCH)) {
            enableMCHForms();
            supportedPrograms.add(PihEmrConfigConstants.PROGRAM_MCH_UUID);
            enableMCHProgram();
        }

        if(config.isComponentEnabled(Components.TUBERCULOSIS)){
            supportedPrograms.add(PihEmrConfigConstants.PROGRAM_TB_UUID);
            enableTBProgram();
        }

        if (config.isComponentEnabled(Components.MCH_PROGRAM)) {
            supportedPrograms.add(PihEmrConfigConstants.PROGRAM_MCH_UUID);
            enableMCHProgram();
        }

        if (config.isComponentEnabled(Components.ZIKA)) {
            supportedPrograms.add(PihEmrConfigConstants.PROGRAM_ZIKA_UUID);
            configureBasicProgramDashboard(PihEmrConfigConstants.PROGRAM_ZIKA_UUID);
        }

        if (config.isComponentEnabled(Components.COVID19)) {
            supportedPrograms.add(PihEmrConfigConstants.PROGRAM_COVID19_UUID);
            configureBasicProgramDashboard(PihEmrConfigConstants.PROGRAM_COVID19_UUID);
        }

        // TODO better/more granular privileges?
        if (supportedPrograms.size() > 0) {

            apps.add(addToHomePage(app(CustomAppLoaderConstants.Apps.PROGRAM_SUMMARY_LIST,
                    "pih.app.programSummaryList.title",
                    "fas fa-fw fa-chart-pie",
                    "/coreapps/applist/appList.page?app=" + CustomAppLoaderConstants.Apps.PROGRAM_SUMMARY_LIST,
                      PihEmrConfigConstants.PRIVILEGE_APP_COREAPPS_SUMMARY_DASHBOARD,
                    null),
                    null));

            apps.add(addToClinicianDashboardSecondColumn(app(CustomAppLoaderConstants.Apps.PROGRAMS_LIST,
                    "coreapps.programsListDashboardWidget.label",
                    "fas fa-fw fa-stethoscope",  // TODO figure out right icon
                    null,
                      PihEmrConfigConstants.PRIVILEGE_APP_COREAPPS_PATIENT_DASHBOARD,
                    objectNode(
                            "widget", "programs",
                            "icon", "fas fa-fw fa-stethoscope",
                            "label", "coreapps.programsDashboardWidget.label",
                            "supportedPrograms", StringUtils.join(supportedPrograms, ','),
                            "enableProgramDashboards", "true"
                    )),
                    "coreapps", "dashboardwidgets/dashboardWidget"));
        }
    }

    private void configureBasicProgramDashboard(String programUuid) {
        apps.add(addToProgramDashboardFirstColumn(programUuid,
                app("pih.app." + programUuid + ".patientProgramSummary",
                        "coreapps.currentEnrollmentDashboardWidget.label",
                        "fas fa-fw fa-stethoscope",  // TODO figure out right icon
                        null,
                        PihEmrConfigConstants.PRIVILEGE_APP_COREAPPS_PATIENT_DASHBOARD,
                        objectNode(
                                "widget", "programstatus",
                                "icon", "fas fa-fw fa-stethoscope",
                                "label", "coreapps.currentEnrollmentDashboardWidget.label",
                                "program", programUuid,
                                "locationTag", "Program Location",
                                "markPatientDeadOutcome", config.isComponentEnabled(Components.MARK_PATIENT_DEAD) ? PihCoreConstants.PATIENT_DIED_CONCEPT_UUID : null,
                                "dashboard", programUuid   // provides contextual context so this widget knows which dashboard it's being rendered on
                        )),
                "coreapps", "dashboardwidgets/dashboardWidget"));

        apps.add(addToProgramDashboardFirstColumn(programUuid,
                app("pih.app." + programUuid + ".patientProgramHistory",
                        "coreapps.programHistoryDashboardWidget.label",
                        "fas fa-fw fa-stethoscope",  // TODO figure out right icon
                        null,
                        PihEmrConfigConstants.PRIVILEGE_APP_COREAPPS_PATIENT_DASHBOARD,
                        objectNode(
                                "icon", "fas fa-fw fa-stethoscope",
                                "label", "coreapps.programHistoryDashboardWidget.label",
                                "program", programUuid,
                                "includeActive", false,
                                "locationTag", "Program Location",
                                "markPatientDeadOutcome", config.isComponentEnabled(Components.MARK_PATIENT_DEAD) ? PihCoreConstants.PATIENT_DIED_CONCEPT_UUID : null,
                                "dashboard", programUuid   // provides contextual context so this widget knows which dashboard it's being rendered on
                        )),
                "coreapps", "program/programHistory"));

        configureBasicProgramSummaryApps(programUuid);
        configureBasicProgramExtensions(programUuid);
    }

    private void configureBasicProgramSummaryApps(String programUuid) {
        // TODO correct the privilege
        apps.add(addToProgramSummaryListPage(app("pih.app." + programUuid + ".programSummary.dashboard",
                        "pih.app." + programUuid + ".programSummary.dashboard",
                        "fas fa-fw fa-list-alt",
                        "/coreapps/summarydashboard/summaryDashboard.page?app=" + "pih.app." + programUuid + ".programSummary.dashboard",
                        PihEmrConfigConstants.PRIVILEGE_APP_COREAPPS_SUMMARY_DASHBOARD,
                        objectNode(
                                "program", programUuid
                        )),
                null));

        apps.add(addToProgramSummaryDashboardFirstColumn(programUuid,
                app("pih.app." + programUuid + " .programStatistics",
                        "pih.app." + programUuid + ".programStatistics.title",
                        "fas fa-fw fa-bars",  // TODO figure out right icon
                        null,
                        null, // TODO restrict by privilege or location)
                        objectNode(
                                "widget", "programstatistics",
                                "icon", "fas fa-fw fa-bars",
                                "label", "pih.app." + programUuid + ".programStatistics.title",
                                "dateFormat", "dd MMM yyyy",
                                "program", programUuid
                        )),
                "coreapps", "dashboardwidgets/dashboardWidget"));
    }

    private void configureBasicProgramExtensions(String programUuid) {
        // All program dashboards should provide a general action link back to general patient dashboard
        String programKey = programUuid.replace("-", "");
        extensions.add(extension(
                "pih.extension." + programKey + " .clinicianDashboardLink",
                "registrationapp.clinicalDashboard",
                "fas fa-fw fa-stethoscope",
                "link",
                 config.getDashboardUrl() + "&currentDashboard=" + programUuid,
                "App: coreapps.patientDashboard",
                null,
                programUuid + ".overallActions",
                1,
                null
        ));

        // All clinician-facing dashboard general actions should be configured by default on program dashboards
        List<Extension> programExtensions = new ArrayList<Extension>();
        for (Extension e : extensions) {
            if (CustomAppLoaderConstants.ExtensionPoints.OVERALL_ACTIONS.equals(e.getExtensionPointId())) {
                programExtensions.add(extension(
                        e.getId() + "." + programUuid, e.getLabel(), e.getIcon(), e.getType(),
                        (e.getUrl() == null ? e.getScript() : e.getUrl()), e.getRequiredPrivilege(), e.getRequire(),
                        programUuid + ".overallActions", e.getOrder(), e.getExtensionParams()
                ));
            }
        }
        extensions.addAll(programExtensions);

        // This provides the javascript & dialogs the backs the overall action buttons (to start/end visits, etc)
        extensions.add(fragmentExtension(
                "pih.extension.patientDashboard." + programKey + ".visitIncludes",
                "coreapps",
                "patientdashboard/visitIncludes",
                null,
                programUuid + ".includeFragments",
                map("patientVisitsPage", patientVisitsPageWithSpecificVisitUrl,
                        "visitType", PihEmrConfigConstants.VISITTYPE_CLINIC_OR_HOSPITAL_VISIT_UUID)
        ));
    }

    private void enableExportPatients() {
        apps.add(addToSystemAdministrationPage(app(CustomAppLoaderConstants.Apps.PATIENT_EXPORT,
                "pihcore.patient.export",
                "fas fa-fw fa-external-link-alt",
                "pihcore/export/exportPatients.page",
                "App: coreapps.systemAdministration",
                null)));
    }

    private void enableImportPatients() {
        apps.add(addToSystemAdministrationPage(app(CustomAppLoaderConstants.Apps.PATIENT_IMPORT,
                "pihcore.patient.import",
                "fas fa-fw fa-sign-in-alt",
                "pihcore/export/importPatients.page",
                "App: coreapps.systemAdministration",
                null)));
    }

    private void enableProviderRelationships() {

        apps.add(addToClinicianDashboardFirstColumn(app(CustomAppLoaderConstants.Apps.PROVIDER_RELATIONSHIPS_CLINICAL_SUMMARY,
                "pihcore.providerRelationshipsDashboardWidget.label",
                "fas fa-fw fa-users",
                null,
                null,
                objectNode(
                    "widget", "relationships",
                    "editPrivilege", CoreAppsConstants.PRIVILEGE_EDIT_RELATIONSHIPS,
                    "dashboardPage", config.getDashboardUrl(),
                    "providerPage", "/coreapps/providermanagement/editProvider.page?personUuid={{personUuid}}",
                    "includeRelationshipTypes", PihEmrConfigConstants.RELATIONSHIPTYPE_CHWTOPATIENT_UUID,
                    "icon", "fas fa-fw fa-users",
                    "label", "pihcore.providerRelationshipsDashboardWidget.label"
                )),
                "coreapps", "dashboardwidgets/dashboardWidget"));
    }

    private void enableRelationships() {

        apps.add(addToClinicianDashboardSecondColumn(app(CustomAppLoaderConstants.Apps.RELATIONSHIPS_CLINICAL_SUMMARY,
                "pihcore.relationshipsDashboardWidget.label",
                "fas fa-fw fa-users",
                null,
                null, // TODO restrict by privilege or location)
                objectNode(
                        "widget", "relationships",
                        "editPrivilege", CoreAppsConstants.PRIVILEGE_EDIT_RELATIONSHIPS,
                        "dashboardPage", config.getDashboardUrl(),
                        "providerPage", "/coreapps/providermanagement/editProvider.page?personUuid={{personUuid}}",
                        "includeRelationshipTypes", PihEmrConfigConstants.RELATIONSHIPTYPE_SPOUSEPARTNER_UUID
                                + "," + PihCoreConstants.RELATIONSHIP_SIBLING
                                + "," + PihCoreConstants.RELATIONSHIP_PARENT_CHILD,
                        "icon", "fas fa-fw fa-users",
                        "label", "pihcore.relationshipsDashboardWidget.label"
                )),
                "coreapps", "dashboardwidgets/dashboardWidget"));
    }

    // TODO we probably will break this down in a different way instead of "order entry"... like perhaps "drugOrders" and "labOrders"... but for demoing starting like thist
    // TODO this widget was also moved from Order Enry UI to Core Apps, we need to test everything is still working before reenabling
    private void enableOrderEntry() {
        apps.add(addToClinicianDashboardSecondColumn(app(CustomAppLoaderConstants.Apps.ACTIVE_DRUG_ORDERS,
                "coreapps.patientdashboard.activeDrugOrders",
                null,
                null,
                null, // TODO restrict by privilege?
               null),
                "coreapps", "patientdashboard/activeDrugOrders"));

    }

    private void enablePatientDocuments() {
        apps.add(addToClinicianDashboardSecondColumn(app(CustomAppLoaderConstants.Apps.PATIENT_DOCUMENTS,
                "pihcore.patientDocuments.label",
                "fas fa-fw fa-paperclip",
                null,
                  PihEmrConfigConstants.PRIVILEGE_APP_ATTACHMENTS_PAGE,
                null),
                "attachments", "dashboardWidget"));

        extensions.add(overallAction(CustomAppLoaderConstants.Extensions.PATIENT_DOCUMENTS_OVERALL_ACTION,
                "pihcore.patientDocuments.overallAction.label",
                "fas fa-fw fa-paperclip",
                "link",
                "attachments/attachments.page?patient={{patient.uuid}}&patientId={{patient.patientId}}",
                  PihEmrConfigConstants.PRIVILEGE_APP_ATTACHMENTS_PAGE,
                null));
    }


    private void enableConditionList() {

        AppDescriptor conditionList = app(CustomAppLoaderConstants.Apps.CONDITION_LIST,
                null, // TODO: add our own label?
                null,  // TODO: add our own icon?
                null,
                  PihEmrConfigConstants.PRIVILEGE_TASK_MANAGE_CONDITIONS_LIST,
                null);

        apps.add(addToClinicianDashboardFirstColumn(conditionList, "coreapps", "conditionlist/conditions"));
    }

    private void enableJ9() {
        apps.add(addToHomePage(app(CustomAppLoaderConstants.Apps.J9_REFERRALS,
                "pih.app.j9Referrals.title",
                "fa fa-fw fa-baby",
                "spa/referrals-queue",
                  PihEmrConfigConstants.PRIVILEGE_TASK_EMR_ENTER_MCH,
                null),
                sessionLocationHasTag("Maternal and Child Location")));
    }

    private void enablePeruLabOrdersAnalysisRequest() {
        apps.add(addToHomePage(app(CustomAppLoaderConstants.Apps.PERU_LAB_ORDERS_ANALYSIS_REQUESTS,
                "Analysis Requests",  // TODO: feel free to localize...
                "fas fa-fw fa-vial",  // all font awesome 5 icons shold be available: https://fontawesome.com/icons?d=gallery&p=1
                "pihcore/peru/analysisRequests.page",  // link to the new page we created in PIH Core
                null,  // TODO: do we want to limit this is users with a certain privilege?
                null),
                sessionLocationHasTag("Consult Note Location")));   //TODO: could change this if need be?  Right now only "COR" is tagged as a consult note location
    }

    private void enableCommentForm() {
        extensions.add(visitAction(CustomAppLoaderConstants.Extensions.COMMENT_VISIT_ACTION,
                "pihcore.comment",
                "fas fa-fw fa-pencil-alt",
                "link",
                enterStandardHtmlFormLink(PihCoreUtil.getFormResource("comment.xml")),
                null,
                null));

        HashMap<String, String> encounterParams = new HashMap<String, String>();
        encounterParams.put("encounterType", PihEmrConfigConstants.ENCOUNTERTYPE_COMMENT_UUID);

		 AppDescriptor notesSummary = app(CustomAppLoaderConstants.Apps.NOTES_SUMMARY,
                "pih.app.notes.title",
                "fas fa-comments",
                addParametersToUrl(patientEncountersPageUrl, encounterParams),
                null,
                objectNode(
                        "widget", "obsacrossencounters",
                        "icon", "fas fa-comments",
                        "label", "pih.app.notes.title",
                        "detailsUrl", addParametersToUrl(patientEncountersPageUrl, encounterParams),
                        "encounterTypes", PihEmrConfigConstants.ENCOUNTERTYPE_COMMENT_UUID,
                        "concepts", CustomAppLoaderConstants.CLINICAL_COMMENTS_CONCEPT_UUID ,
                        "sortOrder", "desc",
                        "headers", "zl.date,pih.app.notes.title"
                ));
		apps.add(addToClinicianDashboardFirstColumn(notesSummary, "coreapps", "dashboardwidgets/dashboardWidget"));
    }

    private void enableSpaPreview() {
        apps.add(addToHomePage(app(CustomAppLoaderConstants.Apps.SPA_PREVIEW_HOME,
                "pihcore.spa.home",
                "fas fa-fw fa-exclamation-triangle",
                "spa/home",
                  PihEmrConfigConstants.PRIVILEGE_APP_EMR_SYSTEM_ADMINISTRATION,
                null),
                null));
        extensions.add(overallAction(CustomAppLoaderConstants.Extensions.SPA_PREVIEW_PATIENT_CHART,
                "pihcore.spa.patientChart",
                "fas fa-fw fa-exclamation-triangle",
                "link",
                "spa/patient/{{patient.uuid}}/chart/summary",
                  PihEmrConfigConstants.PRIVILEGE_APP_EMR_SYSTEM_ADMINISTRATION,
                null));
    }

    private void registerLacollinePatientRegistrationEncounterTypes() {
        // TODO: I *believe* these are used in Lacolline, but not 100% sure
        registerTemplateForEncounterType(PihEmrConfigConstants.ENCOUNTERTYPE_PAYMENT_UUID,
                findExtensionById(CustomAppLoaderConstants.EncounterTemplates.DEFAULT), "fas fa-fw fa-money-bill-alt");
        registerTemplateForEncounterType(PihEmrConfigConstants.ENCOUNTERTYPE_PRIMARY_CARE_VISIT_UUID,
                findExtensionById(CustomAppLoaderConstants.EncounterTemplates.DEFAULT), "fas fa-fw fa-calendar");

    }

    private void addPaperRecordActionsIncludesIfNeeded() {

        // this provides the javascript the backs the three overall action buttons
        // we need to make sure we don't add it twice
        if (! containsExtension(extensions, CustomAppLoaderConstants.Extensions.PAPER_RECORD_ACTIONS_INCLUDES)) {
            extensions.add(fragmentExtension(CustomAppLoaderConstants.Extensions.PAPER_RECORD_ACTIONS_INCLUDES,
                    "paperrecord",
                    "patientdashboard/overallActionsIncludes",
                    null,
                    CustomAppLoaderConstants.ExtensionPoints.DASHBOARD_INCLUDE_FRAGMENTS,
                    null));
        }
    }

    private void configureAdditionalExtensions(Config config) {
        Collections.sort(config.getExtensions());
        for (Extension extension : config.getExtensions()) {
            extensions.add(extension);
        }
    }

    public AppDescriptor findAppById(String id) {
        for (AppDescriptor app : apps) {
            if (app.getId().equals(id)) {
                return app;
            }
        }
        log.warn("App Not Found: " + id);
        return null;
    }

    public Extension findExtensionById(String id) {
        for (Extension extension : extensions) {
            if (extension.getId().equals(id)) {
                return extension;
            }
        }
        log.warn("Extension Not Found: " + id);
        return null;
    }

    public void setReadyForRefresh(Boolean readyForRefresh) {
        this.readyForRefresh = readyForRefresh;
    }

    // used for mocking
    public void setApps(List<AppDescriptor> apps) {
        this.apps = apps;
    }

    public void setExtensions(List<Extension> extensions) {
        this.extensions = extensions;
    }

    public void setConfig(Config config) {
        this.config = config;
    }
}
