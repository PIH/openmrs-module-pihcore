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
    public void shouldDefaultToLocalhost() {
        BiometricsConfig config = new BiometricsConfig();
        assertThat(config.getSubjectUrl(), is("http://localhost:9000/subject"));
        assertThat(config.getMatchUrl(), is("http://localhost:9000/match"));
    }

    @Test
    public void shouldReadLowercaseNamesAsSetOnDockerInstances() {
        setRuntimeProperty("pihcore.biometrics.subjecturl", "http://biometrics:9000/subject");
        setRuntimeProperty("pihcore.biometrics.matchurl", "http://biometrics:9000/match");
        BiometricsConfig config = new BiometricsConfig();
        assertThat(config.getSubjectUrl(), is("http://biometrics:9000/subject"));
        assertThat(config.getMatchUrl(), is("http://biometrics:9000/match"));
    }

    @Test
    public void shouldStillReadCamelCaseNames() {
        setRuntimeProperty("pihcore.biometrics.subjectUrl", "http://192.168.1.111:9000/subject");
        setRuntimeProperty("pihcore.biometrics.matchUrl", "http://192.168.1.111:9000/match");
        BiometricsConfig config = new BiometricsConfig();
        assertThat(config.getSubjectUrl(), is("http://192.168.1.111:9000/subject"));
        assertThat(config.getMatchUrl(), is("http://192.168.1.111:9000/match"));
    }

    @Test
    public void shouldPreferLowercaseNamesOverCamelCase() {
        setRuntimeProperty("pihcore.biometrics.subjectUrl", "http://old:9000/subject");
        setRuntimeProperty("pihcore.biometrics.subjecturl", "http://new:9000/subject");
        assertThat(new BiometricsConfig().getSubjectUrl(), is("http://new:9000/subject"));
    }

    @Test
    public void shouldFallBackToCamelCaseWhenLowercaseIsBlank() {
        setRuntimeProperty("pihcore.biometrics.matchurl", " ");
        setRuntimeProperty("pihcore.biometrics.matchUrl", "http://old:9000/match");
        assertThat(new BiometricsConfig().getMatchUrl(), is("http://old:9000/match"));
    }
}
