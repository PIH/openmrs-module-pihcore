package org.openmrs.module.pihcore.apploader;

import org.codehaus.jackson.node.ObjectNode;
import org.junit.Test;
import org.openmrs.module.appframework.domain.AppDescriptor;
import org.openmrs.module.appframework.domain.Extension;
import org.openmrs.module.appui.AppUiExtensions;

import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.openmrs.module.pihcore.apploader.CustomAppLoaderUtil.map;
import static org.openmrs.module.pihcore.apploader.CustomAppLoaderUtil.objectNode;

public class CustomAppLoaderTest  {

    @Test
    public void shouldCreateHeader() {
        Extension extension = CustomAppLoaderUtil.header("id", "logo");

        assertThat(extension.getId(), is("id"));
        assertThat(extension.getExtensionPointId(), is(AppUiExtensions.HEADER_CONFIG_EXTENSION));
        assertThat(extension.getType(), is("config"));
        assertThat((String) extension.getExtensionParams().get("logo-icon-url"), is("logo"));
    }

    @Test
    public void shouldCreateApp() {
        AppDescriptor app = CustomAppLoaderUtil.app("id", "label", "icon", "url", "privilege", objectNode("patientPageUrl", "patientPageUrl"));

        assertThat(app.getId(), is("id"));
        assertThat(app.getLabel(), is("label"));
        assertThat(app.getIcon(), is("icon"));
        assertThat(app.getUrl(), is("url"));
        assertThat(app.getRequiredPrivilege(), is("privilege"));
        assertThat(app.getConfig().get("patientPageUrl").getTextValue(), is("patientPageUrl"));

    }


    @Test
    public void shouldCreatePatientTemplateApp() {
        AppDescriptor app = CustomAppLoaderUtil.findPatientTemplateApp("id", "label", "icon", "privilege", "afterSelectedUrl",
                CustomAppLoaderUtil.arrayNode(CustomAppLoaderUtil.objectNode("label", "label1", "link", "link1"),
                        CustomAppLoaderUtil.objectNode("label", "label2", "link", "link2")),
                CustomAppLoaderUtil.arrayNode(CustomAppLoaderUtil.objectNode("type", "identifier", "label", "ID")));

        assertThat(app.getId(), is("id"));
        assertThat(app.getLabel(), is("label"));
        assertThat(app.getIcon(), is("icon"));
        assertThat(app.getUrl(), is("coreapps/findpatient/findPatient.page?app=id"));
        assertThat(app.getRequiredPrivilege(), is("privilege"));
        assertThat(app.getConfig().get("afterSelectedUrl").getTextValue(), is("afterSelectedUrl"));
        assertThat(app.getConfig().get("label").getTextValue(), is("label"));
        assertThat(app.getConfig().get("heading").getTextValue(), is("label"));
        assertThat(app.getConfig().get("showLastViewedPatients").getBooleanValue(), is(false));
        assertThat(app.getConfig().get("breadcrumbs").get(0).get("label").getTextValue(), is("label1"));
        assertThat(app.getConfig().get("breadcrumbs").get(0).get("link").getTextValue(), is("link1"));
        assertThat(app.getConfig().get("breadcrumbs").get(1).get("label").getTextValue(), is("label2"));
        assertThat(app.getConfig().get("breadcrumbs").get(1).get("link").getTextValue(), is("link2"));
        assertThat(app.getConfig().get("columnConfig").get(0).get("type").getTextValue(), is("identifier"));
        assertThat(app.getConfig().get("columnConfig").get(0).get("label").getTextValue(), is("ID"));
    }

    @Test
    public void shouldAddAppToHomePage() {
        AppDescriptor app = CustomAppLoaderUtil.app("id", "label", "icon", "url", "privilege", objectNode("patientPageUrl", "patientPageUrl"));

        CustomAppLoaderUtil.addToHomePage(app);
        assertThat(app.getExtensions().size(), is(1));
        assertThat(app.getExtensions().get(0).getId(), is("id.appLink"));
        assertThat(app.getExtensions().get(0).getType(), is("link"));
        assertThat(app.getExtensions().get(0).getLabel(), is("label"));
        assertThat(app.getExtensions().get(0).getUrl(), is("pihcore/router/appEntryRouter.page?app=id")); // we go through the app router now
        assertThat(app.getExtensions().get(0).getIcon(), is("icon"));
        assertThat(app.getExtensions().get(0).getRequiredPrivilege(), is("privilege"));
        assertThat(app.getExtensions().get(0).getExtensionPointId(), is(CustomAppLoaderConstants.ExtensionPoints.HOME_PAGE));
        assertThat(app.getConfig().get("patientPageUrl").getTextValue(), is("patientPageUrl"));
    }

    @Test
    public void shouldAddAppToSystemAdministrationPage() {
        AppDescriptor app = CustomAppLoaderUtil.app("id", "label", "icon", "url", "privilege", null);

        CustomAppLoaderUtil.addToSystemAdministrationPage(app);
        assertThat(app.getExtensions().size(), is(1));
        assertThat(app.getExtensions().get(0).getId(), is("id.systemAdministration.appLink"));
        assertThat(app.getExtensions().get(0).getType(), is("link"));
        assertThat(app.getExtensions().get(0).getLabel(), is("label"));
        assertThat(app.getExtensions().get(0).getUrl(), is("url"));
        assertThat(app.getExtensions().get(0).getIcon(), is("icon"));
        assertThat(app.getExtensions().get(0).getRequiredPrivilege(), is("privilege"));
        assertThat(app.getExtensions().get(0).getExtensionPointId(), is(CustomAppLoaderConstants.ExtensionPoints.SYSTEM_ADMINISTRATION_PAGE));
    }


    @Test
    public void shouldCreateVisitActionsExtension() {
        Extension extension = CustomAppLoaderUtil.visitAction(CustomAppLoaderConstants.Extensions.ORDER_XRAY_VISIT_ACTION, "label", "icon","link", "url", "privilege", "require");

        assertThat(extension.getId(), is(CustomAppLoaderConstants.Extensions.ORDER_XRAY_VISIT_ACTION));
        assertThat(extension.getLabel(), is("label"));
        assertThat(extension.getIcon(), is("icon"));
        assertThat(extension.getUrl(), is("url"));
        assertThat(extension.getScript(), nullValue());
        assertThat(extension.getRequiredPrivilege(), is("privilege"));
        assertThat(extension.getRequire(), is("require"));
        assertThat(extension.getType(), is("link"));
        assertThat(extension.getExtensionPointId(), is(CustomAppLoaderConstants.ExtensionPoints.VISIT_ACTIONS));
    }

    @Test
    public void shouldCreateOverallActionsExtension() {
        Extension extension = CustomAppLoaderUtil.overallAction(CustomAppLoaderConstants.Extensions.PRINT_PAPER_FORM_LABEL_OVERALL_ACTION, "label", "icon","script", "script", "privilege", "require");

        assertThat(extension.getId(), is(CustomAppLoaderConstants.Extensions.PRINT_PAPER_FORM_LABEL_OVERALL_ACTION));
        assertThat(extension.getLabel(), is("label"));
        assertThat(extension.getIcon(), is("icon"));
        assertThat(extension.getUrl(), is(nullValue()));
        assertThat(extension.getScript(), is("script"));
        assertThat(extension.getRequiredPrivilege(), is("privilege"));
        assertThat(extension.getRequire(), is("require"));
        assertThat(extension.getType(), is("script"));
        assertThat(extension.getExtensionPointId(), is(CustomAppLoaderConstants.ExtensionPoints.OVERALL_ACTIONS));
    }

    @Test
    public void shouldCreateAwaitingAdmissionActionsExtension() {
        Extension extension = CustomAppLoaderUtil.awaitingAdmissionAction(CustomAppLoaderConstants.Extensions.ADMISSION_FORM_AWAITING_ADMISSION_ACTION, "label", "icon", "link", "url", "privilege", "require");

        assertThat(extension.getId(), is(CustomAppLoaderConstants.Extensions.ADMISSION_FORM_AWAITING_ADMISSION_ACTION));
        assertThat(extension.getLabel(), is("label"));
        assertThat(extension.getIcon(), is("icon"));
        assertThat(extension.getUrl(), is("url"));
        assertThat(extension.getScript(), nullValue());
        assertThat(extension.getRequiredPrivilege(), is("privilege"));
        assertThat(extension.getRequire(), is("require"));
        assertThat(extension.getType(), is("link"));
        assertThat(extension.getExtensionPointId(), is(CustomAppLoaderConstants.ExtensionPoints.AWAITING_ADMISSION_ACTIONS));
    }


    @Test
    public void shouldCreateDashboardTab() {
        Extension extension = CustomAppLoaderUtil.dashboardTab("id", "label", "privilege", "provider", "fragment");

        assertThat(extension.getId(), is("id"));
        assertThat(extension.getExtensionPointId(), is("patientDashboard.tabs"));
        assertThat(extension.getType(), is("link"));
        assertThat(extension.getLabel(), is("label"));
        assertThat(extension.getRequiredPrivilege(), is("privilege"));
        assertThat((String) extension.getExtensionParams().get("provider"), is("provider"));
        assertThat((String) extension.getExtensionParams().get("fragment"), is("fragment"));
    }

    @Test
    public void shouldCreateFragmentExtension() {
        Extension extension = CustomAppLoaderUtil.fragmentExtension("id", "provider", "fragment", "privilege", "extensionPoint", map("config", "config"));

        assertThat(extension.getId(), is ("id"));
        assertThat((String) extension.getExtensionParams().get("provider"), is("provider"));
        assertThat((String) extension.getExtensionParams().get("fragment"), is("fragment"));
        assertThat((String) ((Map<String,Object>) extension.getExtensionParams().get("fragmentConfig")).get("config"), is("config"));
        assertThat(extension.getRequiredPrivilege(), is("privilege"));
        assertThat(extension.getExtensionPointId(), is("extensionPoint"));
    }

    @Test
    public void shouldCreateAppExtension() {
        AppDescriptor app = new AppDescriptor();
        CustomAppLoaderUtil.appExtension(app, "id", "label", "icon", "type",
                "url", "requiredPrivilege", "require", 1, "extensionPoint");

        assertThat(app.getExtensions().size(), is(1));
        assertThat(app.getExtensions().get(0).getId(), is("id"));
        assertThat(app.getExtensions().get(0).getExtensionPointId(), is("extensionPoint"));
        assertThat(app.getExtensions().get(0).getType(), is("type"));
        assertThat(app.getExtensions().get(0).getLabel(), is("label"));
        assertThat(app.getExtensions().get(0).getUrl(), is("url"));
        assertThat(app.getExtensions().get(0).getIcon(), is("icon"));
        assertThat(app.getExtensions().get(0).getOrder(), is(1));
        assertThat(app.getExtensions().get(0).getRequiredPrivilege(), is("requiredPrivilege"));
        assertThat(app.getExtensions().get(0).getRequire(), is("require"));
    }


    @Test
    public void shouldCreateEncounterTemplateExtension() {
        Extension  extension = CustomAppLoaderUtil.encounterTemplate("id", "provider", "fragment");

        assertThat(extension.getId(), is("id"));
        assertThat(extension.getExtensionPointId(), is("org.openmrs.referenceapplication.encounterTemplate"));
        assertThat(extension.getType(), is("fragment"));
        assertThat((String) extension.getExtensionParams().get("templateId"), is("id"));
        assertThat((String) extension.getExtensionParams().get("templateFragmentProviderName"), is("provider"));
        assertThat((String) extension.getExtensionParams().get("templateFragmentId"), is("fragment"));
        assertThat((String) extension.getExtensionParams().get("templateFragmentProviderName"), is("provider"));
    }

    @Test
    public void shouldConvertToObjectNode() {
        ObjectNode objectNode = CustomAppLoaderUtil.objectNode("int", 1, "string", "string", "boolean", true);
        assertThat(objectNode.get("int").getIntValue(), is(1));
        assertThat(objectNode.get("string").getTextValue(), is("string"));
        assertThat(objectNode.get("boolean").getBooleanValue(), is(true));
    }

    @Test
    public void shouldConvertToMap() {
        Map<String,Object> map = CustomAppLoaderUtil.map("int", 1, "string", "string", "boolean", true);
        assertThat((Integer) map.get("int"), is (1));
        assertThat((String) map.get("string"), is ("string"));
        assertThat((Boolean) map.get("boolean"), is (true));
    }

    @Test
    public void shouldCreateQuestion() {

        ObjectNode question = CustomAppLoaderUtil.question(
                "someQuestionId", "someQuestionLegend",
                CustomAppLoaderUtil.field("phoneNumber", "registrationapp.patient.phone.question", "personAttribute",
                        "14d4f066-15f5-102d-96e4-000c29c2a5d7", "uicommons", "field/text"),
                CustomAppLoaderUtil.field("anotherField", "anotherLabel", "anotherType",
                        "", "", "", "someClass","anotherClass"));

        assertThat(question.get("id").getTextValue(), is("someQuestionId"));
        assertThat(question.get("legend").getTextValue(), is("someQuestionLegend"));

        ObjectNode field1 = (ObjectNode) question.get("fields").get(0);
        ObjectNode field2 = (ObjectNode) question.get("fields").get(1);

        assertThat(field1.get("formFieldName").getTextValue(), is("phoneNumber"));
        assertThat(field1.get("label").getTextValue(), is("registrationapp.patient.phone.question"));
        assertThat(field1.get("type").getTextValue(), is("personAttribute"));
        assertThat(field1.get("uuid").getTextValue(), is("14d4f066-15f5-102d-96e4-000c29c2a5d7"));
        assertThat(field1.get("widget").get("providerName").getTextValue(), is("uicommons"));
        assertThat(field1.get("widget").get("fragmentId").getTextValue(), is("field/text"));

        assertThat(field2.get("formFieldName").getTextValue(), is("anotherField"));
        assertThat(field2.get("label").getTextValue(), is("anotherLabel"));
        assertThat(field2.get("type").getTextValue(), is("anotherType"));
        assertThat(field2.get("cssClasses").get(0).getTextValue(), is("someClass"));
        assertThat(field2.get("cssClasses").get(1).getTextValue(), is("anotherClass"));

    }

    @Test
    public void shouldCreateSection() {

        ObjectNode section = CustomAppLoaderUtil.section(
                "someSectionId", "someSectionLabel",
                CustomAppLoaderUtil.question(
                        "someQuestionId", "someQuestionLegend",
                        CustomAppLoaderUtil.field("someField", "someLabel", "someType", "", "", "")
                ),
                CustomAppLoaderUtil.question(
                        "anotherQuestionId", "anotherQuestionLegend",
                        CustomAppLoaderUtil.field("anotherField", "anotherLabel", "anotherType", "", "", "")
                ));

        assertThat(section.get("id").getTextValue(), is("someSectionId"));
        assertThat(section.get("label").getTextValue(), is("someSectionLabel"));

        ObjectNode question1 = (ObjectNode) section.get("questions").get(0);
        ObjectNode question2 = (ObjectNode) section.get("questions").get(1);

        assertThat(question1.get("id").getTextValue(), is("someQuestionId"));
        assertThat(question1.get("legend").getTextValue(), is("someQuestionLegend"));

        assertThat(question2.get("id").getTextValue(), is("anotherQuestionId"));
        assertThat(question2.get("legend").getTextValue(), is("anotherQuestionLegend"));

    }

    @Test
    public void shouldCreatePatientRegistrationConfig() {

        ObjectNode config = CustomAppLoaderUtil.patientRegistrationConfig("afterCreatedUrl",
                "",
                "123abc",
                "456def",
                CustomAppLoaderUtil.section(
                "someSectionId", "someSectionLabel",
                CustomAppLoaderUtil.question(
                        "someQuestionId", "someQuestionLegend",
                        CustomAppLoaderUtil.field("someField", "someLabel", "someType", "", "", "")
                )));

        assertThat(config.get("afterCreatedUrl").getTextValue(), is("afterCreatedUrl"));
        assertThat(config.get("registrationEncounter").get("encounterType").getTextValue(), is("123abc"));
        assertThat(config.get("registrationEncounter").get("encounterRole").getTextValue(), is("456def"));
        assertThat(config.get("allowRetrospectiveEntry").getBooleanValue(), is(true));
        assertThat(config.get("allowManualIdentifier").getBooleanValue(), is(true));
        assertThat(config.get("allowUnknownPatients").getBooleanValue(), is(true));

        ObjectNode section = (ObjectNode) config.get("sections").get(0);

        assertThat(section.get("id").getTextValue(), is("someSectionId"));
        assertThat(section.get("label").getTextValue(), is("someSectionLabel"));

        ObjectNode question1 = (ObjectNode) section.get("questions").get(0);

        assertThat(question1.get("id").getTextValue(), is("someQuestionId"));
        assertThat(question1.get("legend").getTextValue(), is("someQuestionLegend"));

    }

}
