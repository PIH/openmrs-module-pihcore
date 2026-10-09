package org.openmrs.module.pihcore.biometrics;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openmrs.api.context.Context;
import org.openmrs.module.pihcore.PihCoreContextSensitiveTest;

import java.util.Properties;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

public class BiometricsConfigTest extends PihCoreContextSensitiveTest {

    private Properties originalRuntimeProperties;

    @BeforeEach
    public void saveRuntimeProperties() {
        originalRuntimeProperties = Context.getRuntimeProperties();
    }

    @AfterEach
    public void restoreRuntimeProperties() {
        Context.setRuntimeProperties(originalRuntimeProperties);
    }

    private void setRuntimeProperty(String key, String value) {
        Properties properties = Context.getRuntimeProperties();
        properties.setProperty(key, value);
        Context.setRuntimeProperties(properties);
    }

    @Test
    public void shouldUseDefaultsWhenNothingIsSet() {
        BiometricsConfig config = new BiometricsConfig();
        assertThat(config.getBiometricEngine(), is("restBiometricEngine"));
        assertThat(config.getTemplateFormat(), is("PROPRIETARY"));
        assertThat(config.getSubjectUrl(), is("http://localhost:9000/subject"));
        assertThat(config.getMatchUrl(), is("http://localhost:9000/match"));
        assertThat(config.getScanUrl(), is("http://localhost:9000/fingerprint/scan"));
        assertThat(config.getDevicesUrl(), is("http://localhost:9000/fingerprint/devices"));
    }

    @Test
    public void shouldReadExactNames() {
        setRuntimeProperty("pihcore.biometrics.biometricEngine", "otherEngine");
        setRuntimeProperty("pihcore.biometrics.templateFormat", "ISO");
        setRuntimeProperty("pihcore.biometrics.subjectUrl", "http://192.168.1.111:9000/subject");
        setRuntimeProperty("pihcore.biometrics.matchUrl", "http://192.168.1.111:9000/match");
        setRuntimeProperty("pihcore.biometrics.scanUrl", "http://localhost:9001/fingerprint/scan");
        setRuntimeProperty("pihcore.biometrics.devicesUrl", "http://localhost:9001/fingerprint/devices");
        BiometricsConfig config = new BiometricsConfig();
        assertThat(config.getBiometricEngine(), is("otherEngine"));
        assertThat(config.getTemplateFormat(), is("ISO"));
        assertThat(config.getSubjectUrl(), is("http://192.168.1.111:9000/subject"));
        assertThat(config.getMatchUrl(), is("http://192.168.1.111:9000/match"));
        assertThat(config.getScanUrl(), is("http://localhost:9001/fingerprint/scan"));
        assertThat(config.getDevicesUrl(), is("http://localhost:9001/fingerprint/devices"));
    }

    @Test
    public void shouldReadLowercaseNamesAsSetOnDockerInstances() {
        setRuntimeProperty("pihcore.biometrics.biometricengine", "otherEngine");
        setRuntimeProperty("pihcore.biometrics.templateformat", "ISO");
        setRuntimeProperty("pihcore.biometrics.subjecturl", "http://biometrics:9000/subject");
        setRuntimeProperty("pihcore.biometrics.matchurl", "http://biometrics:9000/match");
        setRuntimeProperty("pihcore.biometrics.scanurl", "http://localhost:9001/fingerprint/scan");
        setRuntimeProperty("pihcore.biometrics.devicesurl", "http://localhost:9001/fingerprint/devices");
        BiometricsConfig config = new BiometricsConfig();
        assertThat(config.getBiometricEngine(), is("otherEngine"));
        assertThat(config.getTemplateFormat(), is("ISO"));
        assertThat(config.getSubjectUrl(), is("http://biometrics:9000/subject"));
        assertThat(config.getMatchUrl(), is("http://biometrics:9000/match"));
        assertThat(config.getScanUrl(), is("http://localhost:9001/fingerprint/scan"));
        assertThat(config.getDevicesUrl(), is("http://localhost:9001/fingerprint/devices"));
    }

    @Test
    public void shouldPreferExactNamesOverLowercase() {
        setRuntimeProperty("pihcore.biometrics.subjectUrl", "http://exact:9000/subject");
        setRuntimeProperty("pihcore.biometrics.subjecturl", "http://lowercase:9000/subject");
        assertThat(new BiometricsConfig().getSubjectUrl(), is("http://exact:9000/subject"));
    }

    @Test
    public void shouldFallBackToLowercaseWhenExactIsBlank() {
        setRuntimeProperty("pihcore.biometrics.matchUrl", " ");
        setRuntimeProperty("pihcore.biometrics.matchurl", "http://lowercase:9000/match");
        assertThat(new BiometricsConfig().getMatchUrl(), is("http://lowercase:9000/match"));
    }
}
