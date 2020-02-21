package org.openmrs.module.pihcore.config;

import org.junit.Test;
import org.openmrs.test.BaseModuleContextSensitiveTest;

import java.util.Properties;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class ConfigTest extends BaseModuleContextSensitiveTest{

    private Config config;

    @Override
    public Properties getRuntimeProperties() {
        Properties p = super.getRuntimeProperties();
        p.setProperty("pih.config", "default");
        return p;
    }

    @Test
    public void testComponentConfiguration() {
        config = new Config();
        assertThat(config.isComponentEnabled("clinicianDashboard"), is(true));
        assertThat(config.isComponentEnabled("patientRegistration"), is(true));
        assertThat(config.isComponentEnabled("missingComponent"), is(false));
        assertThat(config.getWelcomeMessage(), is("Welcome to the PIH EMR"));
        assertThat(config.getSite(), is(ConfigDescriptor.Site.OTHER));
        assertNull(config.getSpecialty());
        assertFalse(config.shouldScheduleBackupReports());
    }

    @Test
    public void testReloadingConfiguration() {
        config = new Config();
        assertThat(config.getWelcomeMessage(), is("Welcome to the PIH EMR"));
        assertThat(config.getSite(), is(ConfigDescriptor.Site.OTHER));
        config.reload(ConfigLoader.load("custom"));
        assertThat(config.getWelcomeMessage(), is("Hello custom!"));
        assertThat(config.getSite(), is(ConfigDescriptor.Site.LACOLLINE));
    }

    @Test
    public void testCustomizingFilenameViaRuntimeProperties() {
        runtimeProperties.setProperty(ConfigLoader.PIH_CONFIGURATION_RUNTIME_PROPERTY, "custom");
        config = new Config(ConfigLoader.loadFromRuntimeProperties());
        assertThat(config.isComponentEnabled("someComponent"), is(true));
        assertThat(config.isComponentEnabled("anotherComponent"), is(false));
        assertThat(config.isComponentEnabled("customComponent"), is(true));
        assertThat(config.getWelcomeMessage(), is("Hello custom!"));
        assertThat(config.getSite(), is(ConfigDescriptor.Site.LACOLLINE));
        assertFalse(config.shouldScheduleBackupReports());
        runtimeProperties.remove(ConfigLoader.PIH_CONFIGURATION_RUNTIME_PROPERTY);
    }

    @Test
    public void testCascadingConfigs() {
        runtimeProperties.setProperty(ConfigLoader.PIH_CONFIGURATION_RUNTIME_PROPERTY, "custom,override");
        config = new Config(ConfigLoader.loadFromRuntimeProperties());
        assertThat(config.isComponentEnabled("override"), is(true));
        assertThat(config.isComponentEnabled("someComponent"), is(false));
        assertThat(config.isComponentEnabled("customComponent"), is(false));
        assertThat(config.getWelcomeMessage(), is("Hello custom!"));
        assertThat(config.getSite(), is(ConfigDescriptor.Site.LACOLLINE));
        assertTrue(config.shouldScheduleBackupReports());
        assertThat(config.getGlobalProperty("test"), is("override"));
        assertThat(config.getGlobalProperty("anotherTest"), is("anotherTest"));
        runtimeProperties.remove(ConfigLoader.PIH_CONFIGURATION_RUNTIME_PROPERTY);
    }

}
