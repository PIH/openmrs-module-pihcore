package org.openmrs.module.pihcore.apploader;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;
import org.openmrs.module.appframework.domain.AppDescriptor;
import org.openmrs.module.appframework.domain.Extension;
import org.openmrs.module.appui.AppUiExtensions;
import org.openmrs.module.pihcore.PihEmrConfigConstants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CustomAppLoaderUtil {

    private static final Log log = LogFactory.getLog(CustomAppLoaderUtil.class);

    private static final String BASE_PREFIX = "web/module/resources/";

    static public AppDescriptor app(String id, String label, String icon, String url, String privilege, ObjectNode config) {
        return app(id, label, icon, url, 0, privilege, config);
    }

    static public AppDescriptor app(String id, String label, String icon, String url, int order, String privilege, ObjectNode config) {

        AppDescriptor app = new AppDescriptor(id, id, label, url, icon, null, order, privilege, null);

        if (config != null) {
            app.setConfig(config);
        }

        return app;
    }

    static public AppDescriptor cloneApp(AppDescriptor app, String newId) {
        return app(newId, app.getLabel(), app.getIcon(), app.getUrl(), app.getRequiredPrivilege(), app.getConfig());
    }

    static public AppDescriptor findPatientTemplateApp(String id, String label, String icon, String privilege, String afterSelectedUrl, ArrayNode breadcrumbs, ArrayNode columnConfig) {

        AppDescriptor app = new AppDescriptor(id, id, label, "coreapps/findpatient/findPatient.page?app=" + id, icon, null, 0, privilege, null);

        app.setConfig(objectNode(
                "afterSelectedUrl", afterSelectedUrl,
                "label", label,
                "heading", label,
                "showLastViewedPatients", false,
                "breadcrumbs", breadcrumbs,
                "columnConfig", columnConfig));

        return app;
    }

    static public AppDescriptor addToHomePage(AppDescriptor app, String require) {
        appExtension(app, app.getId() + ".appLink",
                app.getLabel(),
                app.getIcon(),
                "link",
                "pihcore/router/appEntryRouter.page?app=" + app.getId(),  // NOTE THAT WE ARE NOW ROUTING ALL HOME PAGE APPS VIA THE APP ENTRY ROUTER!
                app.getRequiredPrivilege(),
                require,
                CustomAppLoaderConstants.HOME_PAGE_APPS_ORDER.indexOf(app.getId()),
                CustomAppLoaderConstants.ExtensionPoints.HOME_PAGE);
        return app;
    }

    static public AppDescriptor addToProgramSummaryListPage(AppDescriptor app, String require) {
        appExtension(app, app.getId() + ".appLink",
                app.getLabel(),
                app.getIcon(),
                "link",
                app.getUrl(),
                app.getRequiredPrivilege(),
                require,
                CustomAppLoaderConstants.PROGRAM_SUMMARY_LIST_APPS_ORDER.indexOf(app.getId()),
                CustomAppLoaderConstants.ExtensionPoints.PROGRAM_SUMMARY_LIST);
        return app;
    }

    // there's a problem with redirecting when the provider is the same as the app name (which happens in mirebalais with the "mirebalais" provider)
    // so we have this legacy method that avoids the router
    static public AppDescriptor addToHomePageWithoutUsingRouter(AppDescriptor app, String require) {
        appExtension(app, app.getId() + ".appLink",
                app.getLabel(),
                app.getIcon(),
                "link",
                app.getUrl(),
                app.getRequiredPrivilege(),
                require,
                CustomAppLoaderConstants.HOME_PAGE_APPS_ORDER.indexOf(app.getId()),
                CustomAppLoaderConstants.ExtensionPoints.HOME_PAGE);
        return app;
    }

    static public AppDescriptor addToHomePage(AppDescriptor app) {
        return addToHomePage(app, null);
    }

    static public AppDescriptor addToHomePageWithoutUsingRouter(AppDescriptor app) {
        return addToHomePageWithoutUsingRouter(app, null) ;
    }

    static public AppDescriptor addToOverallActions(AppDescriptor app, String label, String require) {
        appExtension(app, app.getId() + ".overallActions.appLink",
                label,
                app.getIcon(),
                "link",
                app.getUrl(),
                app.getRequiredPrivilege(),
                require,
                CustomAppLoaderConstants.OVERALL_ACTIONS_ORDER.indexOf(app.getId()),
                CustomAppLoaderConstants.ExtensionPoints.OVERALL_ACTIONS);
        return app;
    }

    static public AppDescriptor addToOverallActions(AppDescriptor app, String label) {
        return addToOverallActions(app,label, null);
    }

    static public AppDescriptor addToSystemAdministrationPage(AppDescriptor app, String require) {
        appExtension(app, app.getId() + ".systemAdministration.appLink",
                app.getLabel(),
                app.getIcon(),
                "link",
                app.getUrl(),
                app.getRequiredPrivilege(),
                require,
                CustomAppLoaderConstants.SYSTEM_ADMINISTRATION_APPS_ORDER.indexOf(app.getId()),
                CustomAppLoaderConstants.ExtensionPoints.SYSTEM_ADMINISTRATION_PAGE);
        return app;
    }

    static public AppDescriptor addToSystemAdministrationPage(AppDescriptor app) {
        return addToSystemAdministrationPage(app, null);
    }

    static public AppDescriptor addToDashboardColumn(AppDescriptor app, String provider, String fragment, String columnExtensionPoint, Integer order) {
        String appId = app.getId() + ".firstColumn";
        appExtension(app, appId,
                app.getLabel(),
                app.getIcon(),
                "link",
                app.getUrl(),
                app.getRequiredPrivilege(),
                null,
                order,
                columnExtensionPoint)
                .setExtensionParams(map("provider", provider,
                        "fragment", fragment));
        return app;

    }

    static public AppDescriptor addToClinicianDashboardFirstColumn(AppDescriptor app, String provider, String fragment) {
       return addToDashboardColumn(app, provider, fragment, CustomAppLoaderConstants.ExtensionPoints.CLINICIAN_DASHBOARD_FIRST_COLUMN, CustomAppLoaderConstants.CLINICIAN_DASHBOARD_FIRST_COLUMN_ORDER.indexOf(app.getId()));
    }

    static public AppDescriptor addToClinicianDashboardSecondColumn(AppDescriptor app, String provider, String fragment) {
        return addToDashboardColumn(app, provider, fragment, CustomAppLoaderConstants.ExtensionPoints.CLINICIAN_DASHBOARD_SECOND_COLUMN, CustomAppLoaderConstants.CLINICIAN_DASHBOARD_SECOND_COLUMN_ORDER.indexOf(app.getId()));
    }

    static public AppDescriptor addToProgramDashboardFirstColumn(String programUuid, AppDescriptor app, String provider, String fragment) {
        return addToDashboardColumn(app, provider, fragment, programUuid + ".firstColumnFragments", 0);
    }

    static public AppDescriptor addToProgramDashboardSecondColumn(String programUuid, AppDescriptor app, String provider, String fragment) {
        return addToDashboardColumn(app, provider, fragment, programUuid + ".secondColumnFragments", 0);
    }

    static public AppDescriptor addToHivDashboardFirstColumn(AppDescriptor app, String provider, String fragment, int order) {
        String programUuid = PihEmrConfigConstants.PROGRAM_HIV_UUID;
        return addToDashboardColumn(app, provider, fragment, programUuid + ".firstColumnFragments", order);
    }

    static public AppDescriptor addToHivDashboardSecondColumn(AppDescriptor app, String provider, String fragment, int order) {
        String programUuid = PihEmrConfigConstants.PROGRAM_HIV_UUID;
        return addToDashboardColumn(app, provider, fragment, programUuid + ".secondColumnFragments", order);
    }

    static public AppDescriptor addToHivSummaryDashboardFirstColumn(AppDescriptor app, String provider, String fragment) {
        return addToProgramSummaryDashboardFirstColumn(PihEmrConfigConstants.PROGRAM_HIV_UUID, app, provider, fragment);
    }

    static public AppDescriptor addToProgramSummaryDashboardFirstColumn(String programUuid, AppDescriptor app, String provider, String fragment) {
        return addToDashboardColumn(app, provider, fragment, "pih.app." + programUuid + ".programSummary.dashboard.firstColumnFragments", 1);  // TODO add order
    }

    static public AppDescriptor addToAsthmaDashboardFirstColumn(AppDescriptor app, String provider, String fragment) {
        return addToProgramDashboardFirstColumn(PihEmrConfigConstants.PROGRAM_ASTHMA_UUID, app, provider, fragment);
    }

    static public AppDescriptor addToAsthmaDashboardSecondColumn(AppDescriptor app, String provider, String fragment) {
        return addToProgramDashboardSecondColumn(PihEmrConfigConstants.PROGRAM_ASTHMA_UUID, app, provider, fragment);
    }

    static public AppDescriptor addToAsthmaSummaryDashboardFirstColumn(AppDescriptor app, String provider, String fragment) {
        return addToProgramSummaryDashboardFirstColumn(PihEmrConfigConstants.PROGRAM_ASTHMA_UUID, app, provider, fragment);
    }

    static public AppDescriptor addToDiabetesDashboardFirstColumn(AppDescriptor app, String provider, String fragment) {
        return addToProgramDashboardFirstColumn(PihEmrConfigConstants.PROGRAM_DIABETES_UUID, app, provider, fragment);
    }

    static public AppDescriptor addToDiabetesDashboardSecondColumn(AppDescriptor app, String provider, String fragment) {
        return addToProgramDashboardSecondColumn(PihEmrConfigConstants.PROGRAM_DIABETES_UUID, app, provider, fragment);
    }

    static public AppDescriptor addToDiabetesSummaryDashboardFirstColumn(AppDescriptor app, String provider, String fragment) {
        return addToProgramSummaryDashboardFirstColumn(PihEmrConfigConstants.PROGRAM_DIABETES_UUID, app, provider, fragment);
    }

    static public AppDescriptor addToEpilepsyDashboardFirstColumn(AppDescriptor app, String provider, String fragment) {
        return addToProgramDashboardFirstColumn(PihEmrConfigConstants.PROGRAM_EPILEPSY_UUID, app, provider, fragment);
    }

    static public AppDescriptor addToEpilepsyDashboardSecondColumn(AppDescriptor app, String provider, String fragment) {
        return addToProgramDashboardSecondColumn(PihEmrConfigConstants.PROGRAM_EPILEPSY_UUID, app, provider, fragment);
    }

    static public AppDescriptor addToEpilepsySummaryDashboardFirstColumn(AppDescriptor app, String provider, String fragment) {
        return addToProgramSummaryDashboardFirstColumn(PihEmrConfigConstants.PROGRAM_EPILEPSY_UUID, app, provider, fragment);
    }

    static public AppDescriptor addToHypertensionDashboardFirstColumn(AppDescriptor app, String provider, String fragment) {
        return addToProgramDashboardFirstColumn(PihEmrConfigConstants.PROGRAM_HYPERTENSION_UUID, app, provider, fragment);
    }

    static public AppDescriptor addToHypertensionDashboardSecondColumn(AppDescriptor app, String provider, String fragment) {
        return addToProgramDashboardSecondColumn(PihEmrConfigConstants.PROGRAM_HYPERTENSION_UUID, app, provider, fragment);
    }

    static public AppDescriptor addToHypertensionSummaryDashboardFirstColumn(AppDescriptor app, String provider, String fragment) {
        return addToProgramSummaryDashboardFirstColumn(PihEmrConfigConstants.PROGRAM_HYPERTENSION_UUID, app, provider, fragment);
    }

    static public AppDescriptor addToMentalHealthDashboardFirstColumn(AppDescriptor app, String provider, String fragment) {
        return addToProgramDashboardFirstColumn(PihEmrConfigConstants.PROGRAM_MENTALHEALTH_UUID, app, provider, fragment);
    }

    static public AppDescriptor addToMentalHealthDashboardSecondColumn(AppDescriptor app, String provider, String fragment) {
        return addToProgramDashboardSecondColumn(PihEmrConfigConstants.PROGRAM_MENTALHEALTH_UUID, app, provider, fragment);
    }

    static public AppDescriptor addToMentalHealthSummaryDashboardFirstColumn(AppDescriptor app, String provider, String fragment) {
        return addToProgramSummaryDashboardFirstColumn(PihEmrConfigConstants.PROGRAM_MENTALHEALTH_UUID, app, provider, fragment);
    }

    static public AppDescriptor addToMalnutritionDashboardFirstColumn(AppDescriptor app, String provider, String fragment) {
        return addToProgramDashboardFirstColumn(PihEmrConfigConstants.PROGRAM_MALNUTRITION_UUID, app, provider, fragment);
    }

    static public AppDescriptor addToMalnutritionDashboardSecondColumn(AppDescriptor app, String provider, String fragment) {
        return addToProgramDashboardSecondColumn(PihEmrConfigConstants.PROGRAM_MALNUTRITION_UUID, app, provider, fragment);
    }

    static public AppDescriptor addToMalnutritionSummaryDashboardFirstColumn(AppDescriptor app, String provider, String fragment) {
        return addToProgramSummaryDashboardFirstColumn(PihEmrConfigConstants.PROGRAM_MALNUTRITION_UUID, app, provider, fragment);
    }

    static public AppDescriptor addToMHDashboardFirstColumn(AppDescriptor app, String provider, String fragment) {
        return addToProgramDashboardFirstColumn(PihEmrConfigConstants.PROGRAM_MENTALHEALTH_UUID, app, provider, fragment);
    }

    static public AppDescriptor addToMHDashboardSecondColumn(AppDescriptor app, String provider, String fragment) {
        return addToProgramDashboardSecondColumn(PihEmrConfigConstants.PROGRAM_MENTALHEALTH_UUID, app, provider, fragment);
    }

    static public AppDescriptor addToMHSummaryDashboardFirstColumn(AppDescriptor app, String provider, String fragment) {
        return addToProgramSummaryDashboardFirstColumn(PihEmrConfigConstants.PROGRAM_MENTALHEALTH_UUID, app, provider, fragment);
    }

    static public AppDescriptor addToNCDDashboardFirstColumn(AppDescriptor app, String provider, String fragment) {
        return addToProgramDashboardFirstColumn(PihEmrConfigConstants.PROGRAM_NCD_UUID, app, provider, fragment);
    }

    static public AppDescriptor addToNCDDashboardSecondColumn(AppDescriptor app, String provider, String fragment) {
        return addToProgramDashboardSecondColumn(PihEmrConfigConstants.PROGRAM_NCD_UUID, app, provider, fragment);
    }

    static public AppDescriptor addToNCDSummaryDashboardFirstColumn(AppDescriptor app, String provider, String fragment) {
        return addToProgramSummaryDashboardFirstColumn(PihEmrConfigConstants.PROGRAM_NCD_UUID, app, provider, fragment);
    }

    static public AppDescriptor addToZikaDashboardFirstColumn(AppDescriptor app, String provider, String fragment) {
        return addToProgramDashboardFirstColumn(PihEmrConfigConstants.PROGRAM_ZIKA_UUID, app, provider, fragment);
    }

    static public AppDescriptor addToZikaDashboardSecondColumn(AppDescriptor app, String provider, String fragment) {
        return addToProgramDashboardSecondColumn(PihEmrConfigConstants.PROGRAM_ZIKA_UUID, app, provider, fragment);
    }

    static public AppDescriptor addToZikaSummaryDashboardFirstColumn(AppDescriptor app, String provider, String fragment) {
        return addToProgramSummaryDashboardFirstColumn(PihEmrConfigConstants.PROGRAM_ZIKA_UUID, app, provider, fragment);
    }

    static public AppDescriptor addToRegistrationSummaryContent(AppDescriptor app, String provider, String fragment, Map<String,Object> fragmentConfig) {
        appExtension(app, app.getId() + ".registrationSummaryContent",
                app.getLabel(),
                app.getIcon(),
                "link",
                app.getUrl(),
                app.getRequiredPrivilege(),
                null,
                CustomAppLoaderConstants.REGISTRATION_SUMMARY_FIRST_COLUMN_ORDER.indexOf(app.getId()),
                CustomAppLoaderConstants.ExtensionPoints.REGISTRATION_SUMMARY_CONTENT)
                .setExtensionParams(map("provider", provider,
                        "fragment", fragment, "fragmentConfig", fragmentConfig));
        return app;
    }

    static public AppDescriptor addToRegistrationSummaryContent(AppDescriptor app, String provider, String fragment) {
        return addToRegistrationSummaryContent(app, provider, fragment, null);
    }

    static public AppDescriptor addToRegistrationSummarySecondColumnContent(AppDescriptor app, String provider, String fragment, Map<String,Object> fragmentConfig) {
        appExtension(app, app.getId() + ".registrationSummaryContent",
                app.getLabel(),
                app.getIcon(),
                "link",
                app.getUrl(),
                app.getRequiredPrivilege(),
                null,
                CustomAppLoaderConstants.REGISTRATION_SUMMARY_SECOND_COLUMN_ORDER.indexOf(app.getId()),
                CustomAppLoaderConstants.ExtensionPoints.REGISTRATION_SUMMARY_SECOND_COLUMN_CONTENT)
                .setExtensionParams(map("provider", provider,
                        "fragment", fragment, "fragmentConfig", fragmentConfig));
        return app;
    }

    static public AppDescriptor addToRegistrationSummarySecondColumnContent(AppDescriptor app, String provider, String fragment) {
        return addToRegistrationSummarySecondColumnContent(app, provider, fragment, null);
    }

    static public Extension visitAction(String id, String label, String icon, String type, String urlOrScript, String privilege, String require) {
        return  extension(id, label, icon, type, urlOrScript, privilege, require,
                CustomAppLoaderConstants.ExtensionPoints.VISIT_ACTIONS, CustomAppLoaderConstants.VISIT_ACTIONS_ORDER.indexOf(id), null);
    }

    static public Extension hivVisitAction(String id, String label, String icon, String type, String urlOrScript, String privilege, String require) {
        return  extension(id, label, icon, type, urlOrScript, privilege, require,
                PihEmrConfigConstants.PROGRAM_HIV_UUID + ".visitActions", CustomAppLoaderConstants.HIV_VISIT_ACTIONS_ORDER.indexOf(id), null);
    }

    static public Extension oncologyVisitAction(String id, String label, String icon, String type, String urlOrScript, String privilege, String require) {
        return  extension(id, label, icon, type, urlOrScript, privilege, require,
                PihEmrConfigConstants.PROGRAM_ONCOLOGY_UUID + ".overallActions", CustomAppLoaderConstants.ONCOLOGY_VISIT_ACTIONS_ORDER.indexOf(id), null);
    }

    static public Extension cloneAsHivVisitAction(Extension ext) {
        return hivVisitAction(ext.getId() + ".hiv", ext.getLabel(), ext.getIcon(), ext.getType(), ext.getType().equals("link") ? ext.getUrl() : ext.getScript(),
                ext.getRequiredPrivilege(), ext.getRequire());
    }

    static public Extension cloneAsOncologyVisitAction(Extension ext) {
        return oncologyVisitAction(ext.getId() + ".oncology", ext.getLabel(), ext.getIcon(), ext.getType(), ext.getType().equals("link") ? ext.getUrl() : ext.getScript(),
                ext.getRequiredPrivilege(), ext.getRequire());
    }

    static public Extension overallAction(String id, String label, String icon, String type, String urlOrScript, String privilege, String require) {
        return  extension(id, label, icon, type, urlOrScript, privilege, require,
                CustomAppLoaderConstants.ExtensionPoints.OVERALL_ACTIONS, CustomAppLoaderConstants.OVERALL_ACTIONS_ORDER.indexOf(id), null);
    }

    static public Extension hivOverallAction(String id, String label, String icon, String type, String urlOrScript, String privilege, String require) {
        return  extension(id, label, icon, type, urlOrScript, privilege, require,
                PihEmrConfigConstants.PROGRAM_HIV_UUID + ".overallActions", 1, null);
    }

    static public Extension oncologyOverallAction(String id, String label, String icon, String type, String urlOrScript, String privilege, String require) {
        return  extension(id, label, icon, type, urlOrScript, privilege, require,
                PihEmrConfigConstants.PROGRAM_ONCOLOGY_UUID + ".overallActions", CustomAppLoaderConstants.ONCOLOGY_OVERALL_ACTIONS_ORDER.indexOf(id), null);
    }

    static public Extension cloneAsHivOverallAction(Extension ext) {
        return hivOverallAction(ext.getId() + ".hiv", ext.getLabel(), ext.getIcon(), ext.getType(), ext.getType().equals("link") ? ext.getUrl() : ext.getScript(),
                ext.getRequiredPrivilege(), ext.getRequire());
    }

    static public Extension cloneAsOncologyOverallAction(Extension ext) {
        return oncologyOverallAction(ext.getId() + ".oncology", ext.getLabel(), ext.getIcon(), ext.getType(), ext.getType().equals("link") ? ext.getUrl() : ext.getScript(),
                ext.getRequiredPrivilege(), ext.getRequire());
    }

    static public Extension overallRegistrationAction(String id, String label, String icon, String type, String urlOrScript, String privilege, String require) {
        return  extension(id, label, icon, type, urlOrScript, privilege, require,
                CustomAppLoaderConstants.ExtensionPoints.OVERALL_REGISTRATION_ACTIONS, CustomAppLoaderConstants.OVERALL_ACTIONS_ORDER.indexOf(id), null);
    }

    static public Extension awaitingAdmissionAction(String id, String label, String icon, String type, String urlOrScript, String privilege, String require) {
        return  extension(id, label, icon, type, urlOrScript, privilege, require,
                CustomAppLoaderConstants.ExtensionPoints.AWAITING_ADMISSION_ACTIONS, CustomAppLoaderConstants.AWAITING_ADMISSION_ACTIONS_ORDER.indexOf(id), null);
    }

    static public Extension dashboardTab(String id, String label, String privilege, String provider, String fragment) {
        return new Extension(id, null, CustomAppLoaderConstants.ExtensionPoints.DASHBOARD_TAB, "link", label, null, 0,
                privilege, map("provider", provider, "fragment", fragment));
    }

    static public Extension encounterTemplate(String id, String templateProvider, String templateFragment) {
        return new Extension(id, null, CustomAppLoaderConstants.ExtensionPoints.ENCOUNTER_TEMPLATE, "fragment", null, null, 0, null,
                map("templateId", id, "templateFragmentProviderName", templateProvider, "templateFragmentId", templateFragment));
    }

    static public Extension header(String id, String logo) {
        Map<String, Object> configs = map("logo-link-url", "/index.htm", "logo-icon-url", logo);
        return new Extension(id, null, AppUiExtensions.HEADER_CONFIG_EXTENSION, "config", null, null, 0, null, configs);
    }

    static public Extension fragmentExtension(String id, String provider, String fragment, String privilege, String extensionPoint, Map<String, Object> config) {
        return new Extension(id, null, extensionPoint, "include-fragment", null, null, 0,
                privilege, map("provider", provider, "fragment", fragment, "fragmentConfig", config));
    }

    static public Extension report(String id, String label, String provider, String fragment, String definitionUuid, String privilege, String extensionPoint, int order, String linkId) {
        return new Extension(id, null, extensionPoint, "link", label,provider + "/" + fragment + ".page?reportDefinition=" + definitionUuid,
                order, privilege, map("linkId", linkId));
    }

    static public Extension clinicianDashboardFirstColumn(String id, String label, String icon, String privilege, String require, String provider, String fragment, Map<String,Object> extensionParams) {
        return fragmentExtension(id, label, icon, privilege, require, CustomAppLoaderConstants.ExtensionPoints.CLINICIAN_DASHBOARD_FIRST_COLUMN, provider, fragment, CustomAppLoaderConstants.CLINICIAN_DASHBOARD_FIRST_COLUMN_ORDER, extensionParams);
    }

    static public Extension clinicianDashboardSecondColumn(String id, String label, String icon, String privilege, String require, String provider, String fragment, Map<String,Object> extensionParams) {
        return fragmentExtension(id, label, icon, privilege, require, CustomAppLoaderConstants.ExtensionPoints.CLINICIAN_DASHBOARD_SECOND_COLUMN, provider, fragment, CustomAppLoaderConstants.CLINICIAN_DASHBOARD_SECOND_COLUMN_ORDER, extensionParams);
    }

    static public Extension fragmentExtension(String id, String label, String icon, String privilege, String require, String extensionPointId, String provider, String fragment, List<String> orderFromList, Map<String,Object> extensionParams) {
        if (extensionParams == null) {
            extensionParams = new HashMap<String, Object>();
        }
        extensionParams.put("provider", provider);
        extensionParams.put("fragment", fragment);
        return extension(id, label, icon, "fragment", null, privilege, require, extensionPointId, orderFromList == null ? Integer.MIN_VALUE : orderFromList.indexOf(id), extensionParams);
    }

    static public Extension extension(String id, String label, String icon, String type, String urlOrScript, String privilege, String require, String extensionPoint, int order, Map<String,Object> extensionParams) {
        Extension extension = new Extension(id, null,extensionPoint, type, label, null, order, privilege, null);
        extension.setIcon(icon);

        if (StringUtils.isNotBlank(require)) {
            extension.setRequire(require);
        }

        if (type.equals("link")) {
            extension.setUrl(urlOrScript);
        }
        else if (type.equals("script")) {
            extension.setScript(urlOrScript);
        }
        else if (type.equals("fragment")) {
            // don't use urlOrScript
        }
        else {
            throw new IllegalStateException("Invalid type: " + type);
        }

        if (extensionParams != null) {
            extension.setExtensionParams(extensionParams);
        }

        return extension;
    }


    static public Extension appExtension(AppDescriptor app, String id, String label, String icon, String type, String url,
                                         String requiredPrivilege, String require, int order, String extensionPoint) {

        Extension extension = new Extension(id, app.getId(), extensionPoint, type, label, url, order, requiredPrivilege, null);
        extension.setIcon(icon);

        if (StringUtils.isNotBlank(require)) {
            extension.setRequire(require);
        }

        if (app.getExtensions() == null) {
            app.setExtensions(new ArrayList<Extension>());
        }

        app.getExtensions().add(extension);
        return extension;
    }

    static public String enterSimpleHtmlFormLink(String definitionUiResource) {
        return "htmlformentryui/htmlform/enterHtmlFormWithSimpleUi.page?patientId={{patient.uuid}}&visitId={{visit.id}}&definitionUiResource=" + definitionUiResource;
    }

    static public String editSimpleHtmlFormLink(String definitionUiResource) {
        return "htmlformentryui/htmlform/editHtmlFormWithSimpleUi.page?patientId={{patient.uuid}}&encounterId={{encounter.id}}&definitionUiResource=" + definitionUiResource;
    }

    static public String enterStandardHtmlFormLink(String definitionUiResource) {
        return "htmlformentryui/htmlform/enterHtmlFormWithStandardUi.page?patientId={{patient.uuid}}&visitId={{visit.id}}&definitionUiResource=" + definitionUiResource;
    }

    static public String andCreateVisit() {
        return "&createVisit=true";
    }

    static public void addFeatureToggleToApp(AppDescriptor app, String featureToggle) {
        app.setFeatureToggle(featureToggle);
    }

    static public void addFeatureToggleToExtension(Extension ext, String featureToggle) {
        ext.setFeatureToggle(featureToggle);
    }

    static public void registerTemplateForEncounterType(String encounterTypeUuid, Extension template, String icon) {
        registerTemplateForEncounterType(encounterTypeUuid, template, icon, null, false, null, null);
    }

    static public void registerTemplateForEncounterType(String encounterTypeUuid, Extension template, String icon,
                                                        Boolean displayWithHtmlForm, Boolean editable,
                                                        String editUrl,  // note that if editUrl is null/empty, the standard Html Form link is used by default--that's why we don't specify this is most cases
                                                        String primaryEncounterRoleUuid) {

        Map<String,Object> extensionParams = template.getExtensionParams();

        if (!extensionParams.containsKey("supportedEncounterTypes")) {
            extensionParams.put("supportedEncounterTypes", new HashMap<String,Object>());
        }

        Map<String,Object> encounterTypeParams = new HashMap<String, Object>();
        if (StringUtils.isNotBlank(icon)) {
            encounterTypeParams.put("icon", icon);
        }
        if (displayWithHtmlForm != null) {
            encounterTypeParams.put("displayWithHtmlForm", displayWithHtmlForm);
        }
        if (editable != null) {
            encounterTypeParams.put("editable", editable);
        }

        if (StringUtils.isNotBlank(editUrl)) {
            encounterTypeParams.put("editUrl", editUrl);
        }

        if (StringUtils.isNotBlank(primaryEncounterRoleUuid)) {
            encounterTypeParams.put("primaryEncounterRoleUuid", primaryEncounterRoleUuid);
        }

        ((Map<String,Object>) extensionParams.get("supportedEncounterTypes")).put(encounterTypeUuid, encounterTypeParams);
    }

    static public ObjectNode patientRegistrationConfig(String afterCreatedUrl, String patientDashboardLink, String registrationEncounterType, String registrationEncounterRole, ObjectNode ... sections) {
        return objectNode("afterCreatedUrl", afterCreatedUrl,
                "patientDashboardLink", patientDashboardLink,
                "allowRetrospectiveEntry", true,
                "allowUnknownPatients", true,
                "allowManualIdentifier", true,
                "registrationEncounter", objectNode("encounterType", registrationEncounterType,
                                                    "encounterRole", registrationEncounterRole),
                "sections", arrayNode(sections));
    }

    static public ObjectNode section(String sectionId, String sectionLabel, ObjectNode ... questions) {
        return objectNode("id", sectionId,
                "label", sectionLabel,
                "questions", arrayNode(questions));
    }

    static public ObjectNode question(String questionId, String questionLegend, ObjectNode ... fields) {
        return objectNode("id", questionId,
                "legend", questionLegend,
                "fields", arrayNode(fields));
    }

    static public ObjectNode question(String questionId, String questionLegend, String questionHeader, ObjectNode ... fields) {
        return objectNode("id", questionId,
                "legend", questionLegend,
                "header", questionHeader,
                "fields", arrayNode(fields));
    }

    static public ObjectNode field(String formFieldName, String label, String type, String uuid, String widgetProvider, String widgetFragment, String ... cssClasses) {

        return objectNode("formFieldName", formFieldName,
                        "label", label,
                        "type", type,
                        "uuid", uuid,
                        "cssClasses", arrayNode(cssClasses),
                        "widget", objectNode("providerName", widgetProvider,
                        "fragmentId", widgetFragment));


    }

    static public ObjectNode field(String formFieldName, String label, String type, String uuid, String widgetProvider, String widgetFragment, ObjectNode config, String ... cssClasses) {

        return objectNode("formFieldName", formFieldName,
                "label", label,
                "type", type,
                "uuid", uuid,
                "cssClasses", arrayNode(cssClasses),
                "widget", objectNode("providerName", widgetProvider,
                                    "fragmentId", widgetFragment,
                                    "config", config));
    }

    static public ObjectNode option(String label, String value) {
        return objectNode("label", label,
                            "value", value);
    }

    static public ArrayNode arrayNode(ObjectNode ... nodes) {
        ArrayNode arrayNode = new ObjectMapper().createArrayNode();
        for (int i = 0; i < nodes.length; i++) {
            arrayNode.add(nodes[i]);
        }
        return arrayNode;
    }

    static public ArrayNode arrayNode(String ... nodes) {
        ArrayNode arrayNode = new ObjectMapper().createArrayNode();
        for (int i = 0; i < nodes.length; i++) {
            arrayNode.add(nodes[i]);
        }
        return arrayNode;
    }

    static public ObjectNode objectNode(Object ... obj) {

        ObjectNode objectNode = new ObjectMapper().createObjectNode();

        for (int i = 0; i < obj.length; i=i+2) {
            String key = (String) obj[i];
            Object value = obj[i+1];

            if (value instanceof Boolean) {
                objectNode.put(key, (Boolean) value);
            }
            else if (value instanceof String) {
                objectNode.put(key, (String) value);
            }
            else if (value instanceof Integer) {
                objectNode.put(key, (Integer) value);
            }
            else if (value instanceof ArrayNode) {
                objectNode.put(key, (ArrayNode) value);
            }
            else if (value instanceof ObjectNode) {
                objectNode.put(key, (ObjectNode) value);
            }
        }

        return objectNode;
    }

    static public Map<String,Object> map(Object ... obj) {

        Map<String,Object> map = new HashMap<String, Object>();

        for (int i = 0; i < obj.length; i=i+2) {
            String key = (String) obj[i];
            Object value = obj[i+1];
            map.put(key, value);
        }

        return map;
    }

    static public Boolean containsExtension(List<Extension> extensions, String extensionId) {
        for (Extension e : extensions) {
            if (e.getId().equals(extensionId)) {
                return true;
            }
        }
        return false;
    }

}
