package org.openmrs.module.pihcore.apploader;

import org.junit.Before;
import org.junit.Test;
import org.openmrs.module.appframework.domain.Extension;
import org.openmrs.module.pihcore.PihCoreContextSensitiveTest;
import org.openmrs.module.pihcore.PihEmrConfigConstants;
import org.openmrs.module.pihcore.config.Config;
import org.openmrs.test.SkipBaseSetup;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.openmrs.module.pihcore.apploader.CustomAppLoaderUtil.registerTemplateForEncounterType;

@SkipBaseSetup
public class CustomAppLoaderComponentTest extends PihCoreContextSensitiveTest {

    @Autowired
    private CustomAppLoaderFactory factory;

    @Override
    public Properties getRuntimeProperties() {
        Properties p = super.getRuntimeProperties();
        p.setProperty("pih.config", "default");
        return p;
    }

    @Before
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

}
