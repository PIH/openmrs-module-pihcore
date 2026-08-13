package org.openmrs.module.pihcore.config;

import org.junit.jupiter.api.Test;
import org.openmrs.api.context.Context;
import org.openmrs.module.pihcore.PihCoreContextSensitiveTest;

import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;
import static org.hamcrest.core.StringContains.containsString;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ConfigTest extends PihCoreContextSensitiveTest{

    private Config config;

    @Override
    public String getPihConfig() {
        return "default";
    }

    @Test
    public void testComponentConfiguration() {
        config = new Config();
        assertThat(config.isComponentEnabled("clinicianDashboard"), is(true));
        assertThat(config.isComponentEnabled("patientRegistration"), is(true));
        assertThat(config.isComponentEnabled("missingComponent"), is(false));
        assertThat(config.getWelcomeMessage(), is("Welcome to the PIH EMR"));
        assertThat(config.getSite(), is("OTHER"));
        assertNull(config.getSpecialty());
        assertFalse(config.shouldScheduleBackupReports());

        List<String> components = Arrays.asList("missingComponent");
        assertThat(config.anyComponentEnabled(components), is (false));
        components = Arrays.asList("clinicianDashboard", "patientRegistration", "missingComponent");
        assertThat(config.anyComponentEnabled(components), is (true));

    }

    @Test
    public void testReloadingConfiguration() {
        config = new Config();
        assertThat(config.getWelcomeMessage(), is("Welcome to the PIH EMR"));
        assertThat(config.getSite(), is("OTHER"));
        config.reload(ConfigLoader.load("custom"));
        assertThat(config.getWelcomeMessage(), is("Hello custom!"));
        assertThat(config.getSite(), is("LACOLLINE"));
    }

    @Test
    public void testCustomizingFilenameViaRuntimeProperties() {
        config = new Config(ConfigLoader.load("custom"));
        assertThat(config.isComponentEnabled("someComponent"), is(true));
        assertThat(config.isComponentEnabled("anotherComponent"), is(false));
        assertThat(config.isComponentEnabled("customComponent"), is(true));
        assertThat(config.getWelcomeMessage(), is("Hello custom!"));
        assertThat(config.getSite(), is("LACOLLINE"));
        assertFalse(config.shouldScheduleBackupReports());
    }

    @Test
    public void noArgConstructorShouldNotSwallowFailureWhenRuntimePropertyIsInvalid() {
        String original = System.getProperty(ConfigLoader.PIH_CONFIGURATION_RUNTIME_PROPERTY);
        try {
            System.setProperty(ConfigLoader.PIH_CONFIGURATION_RUNTIME_PROPERTY, "does-not-exist");

            // the exception thrown by ConfigLoader.loadFromRuntimeProperties() must propagate out of the
            // Config() constructor, not be swallowed after logging the startup failure banner
            assertThrows(RuntimeException.class, () -> new Config());
        }
        finally {
            restorePihConfigSystemProperty(original);
        }
    }

    @Test
    public void loadFromRuntimePropertiesShouldThrowClearErrorWhenPihConfigIsNotSet() {
        // getSystemOrRuntimeProperty() checks the system property first, then falls back to the runtime
        // property that @BeforeEach's setupInitializerForTesting() already populated for this test class
        // (via getPihConfig()) -- both have to be cleared to genuinely simulate "pih.config was never set"
        String originalSystemProperty = System.getProperty(ConfigLoader.PIH_CONFIGURATION_RUNTIME_PROPERTY);
        Properties prop = getRuntimeProperties();
        String originalRuntimeProperty = prop.getProperty(ConfigLoader.PIH_CONFIGURATION_RUNTIME_PROPERTY);
        try {
            System.clearProperty(ConfigLoader.PIH_CONFIGURATION_RUNTIME_PROPERTY);
            prop.remove(ConfigLoader.PIH_CONFIGURATION_RUNTIME_PROPERTY);
            Context.setRuntimeProperties(prop);

            IllegalStateException thrown = assertThrows(IllegalStateException.class,
                    ConfigLoader::loadFromRuntimeProperties);
            assertThat(thrown.getMessage(), containsString("is not defined"));
            assertThat(thrown.getMessage(), not(containsString("pih-config-")));
        }
        finally {
            restorePihConfigSystemProperty(originalSystemProperty);
            if (originalRuntimeProperty == null) {
                prop.remove(ConfigLoader.PIH_CONFIGURATION_RUNTIME_PROPERTY);
            }
            else {
                prop.setProperty(ConfigLoader.PIH_CONFIGURATION_RUNTIME_PROPERTY, originalRuntimeProperty);
            }
            Context.setRuntimeProperties(prop);
        }
    }

    private void restorePihConfigSystemProperty(String original) {
        if (original == null) {
            System.clearProperty(ConfigLoader.PIH_CONFIGURATION_RUNTIME_PROPERTY);
        }
        else {
            System.setProperty(ConfigLoader.PIH_CONFIGURATION_RUNTIME_PROPERTY, original);
        }
    }

    @Test
    public void testCascadingConfigs() {
        config = new Config(ConfigLoader.load("custom,override"));
        assertThat(config.isComponentEnabled("override"), is(true));
        assertThat(config.isComponentEnabled("someComponent"), is(false));
        assertThat(config.isComponentEnabled("customComponent"), is(false));
        assertThat(config.getWelcomeMessage(), is("Hello custom!"));
        assertThat(config.getSite(), is("LACOLLINE"));
        assertTrue(config.shouldScheduleBackupReports());
        assertThat(config.getGlobalProperty("test"), is("override"));
        assertThat(config.getGlobalProperty("anotherTest"), is("anotherTest"));
        assertThat(config.getLocationTags().get("Admission Location").size(), is(2));
        assertThat(config.getLocationTags().get("Admission Location").get(0), is("Women's Ward"));
        assertThat(config.getLocationTags().get("Visit Location").size(), is(1));
    }

}
