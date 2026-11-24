package org.openmrs.module.pihcore.apploader;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openmrs.module.appframework.domain.Extension;
import org.openmrs.module.pihcore.PihCoreContextSensitiveTest;
import org.openmrs.module.pihcore.PihEmrConfigConstants;
import org.openmrs.module.pihcore.config.Components;
import org.openmrs.module.pihcore.config.Config;
import org.openmrs.module.pihcore.config.ConfigDescriptor;
import org.openmrs.test.SkipBaseSetup;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.openmrs.module.pihcore.apploader.CustomAppLoaderUtil.registerTemplateForEncounterType;

@SkipBaseSetup
public class CustomAppLoaderComponentTest extends PihCoreContextSensitiveTest {

    @Autowired
    private CustomAppLoaderFactory factory;

    @BeforeEach
    public void setup() throws Exception {
        setAutoIncrementOnTablesWithNativeIfNotAssignedIdentityGenerator();
        executeDataSet("org/openmrs/module/pihcore/coreMetadata.xml");
        authenticate();
    }

    @Test
    public void shouldSetUpAppsAndExtensions() throws Exception {
        factory.setConfig(new Config());
        factory.getExtensions();
        factory.getAppDescriptors();
    }

    @Test
    public void shouldRegisterTemplateForEncounterType() {

        List<Extension> extensions = new ArrayList<Extension>();
        Extension template = CustomAppLoaderUtil.encounterTemplate("id", "provider", "fragment");
        extensions.add(template);

        factory.setExtensions(extensions);

        String patRegUuid = PihEmrConfigConstants.ENCOUNTERTYPE_PATIENT_REGISTRATION_UUID;

        registerTemplateForEncounterType(patRegUuid, factory.findExtensionById("id"), "icon",
                true, false, "someLink", "primaryEncounterRoleUuid");

        assertTrue(template.getExtensionParams().containsKey("supportedEncounterTypes"));
        assertTrue(((Map<String, Object>) template.getExtensionParams().get("supportedEncounterTypes")).containsKey(patRegUuid));

        Map<String, Object> params = (Map<String, Object>) ((Map<String, Object>) template.getExtensionParams().get("supportedEncounterTypes")).get(patRegUuid);
        assertThat((String) params.get("icon"), is("icon"));
        assertThat((String) params.get("primaryEncounterRoleUuid"), is("primaryEncounterRoleUuid"));
        assertThat((Boolean) params.get("displayWithHtmlForm"), is(true));
        assertThat((Boolean) params.get("editable"), is(false));
        assertThat((String) params.get("editUrl"), is("someLink"));

    }

    private void loadWithComponents(String... components) throws Exception {
        ConfigDescriptor descriptor = new ConfigDescriptor();
        descriptor.setComponents(Arrays.asList(components));
        factory.setConfig(new Config(descriptor));
        factory.setApps(new ArrayList<>());
        factory.setExtensions(new ArrayList<>());
        factory.setReadyForRefresh(true);
        factory.getAppDescriptors();
    }

    @Test
    public void shouldAddVitalsListAppForVitalsHomepageListApp() throws Exception {
        loadWithComponents(Components.VITALS, Components.VITALS_HOMEPAGE_LIST_APP);
        assertNotNull(factory.findAppById(CustomAppLoaderConstants.Apps.VITALS));
        assertNull(factory.findAppById(CustomAppLoaderConstants.Apps.VITALS_SEARCH));
    }

    @Test
    public void shouldAddVitalsListAppForDeprecatedVitalsHomepageApp() throws Exception {
        loadWithComponents(Components.VITALS, Components.VITALS_HOMEPAGE_APP);
        assertNotNull(factory.findAppById(CustomAppLoaderConstants.Apps.VITALS));
        assertNull(factory.findAppById(CustomAppLoaderConstants.Apps.VITALS_SEARCH));
    }

    @Test
    public void shouldAddVitalsSearchAppForVitalsHomepageSearchApp() throws Exception {
        loadWithComponents(Components.VITALS, Components.VITALS_HOMEPAGE_SEARCH_APP);
        assertNotNull(factory.findAppById(CustomAppLoaderConstants.Apps.VITALS_SEARCH));
        assertNull(factory.findAppById(CustomAppLoaderConstants.Apps.VITALS));
    }

    @Test
    public void shouldNotAddVitalsHomepageAppsWithoutVitals() throws Exception {
        loadWithComponents(Components.VITALS_HOMEPAGE_LIST_APP, Components.VITALS_HOMEPAGE_SEARCH_APP);
        assertNull(factory.findAppById(CustomAppLoaderConstants.Apps.VITALS));
        assertNull(factory.findAppById(CustomAppLoaderConstants.Apps.VITALS_SEARCH));
    }

    @Test
    public void shouldNotAddVitalsHomepageAppsWithVitalsOnly() throws Exception {
        loadWithComponents(Components.VITALS);
        assertNull(factory.findAppById(CustomAppLoaderConstants.Apps.VITALS));
        assertNull(factory.findAppById(CustomAppLoaderConstants.Apps.VITALS_SEARCH));
    }

}
