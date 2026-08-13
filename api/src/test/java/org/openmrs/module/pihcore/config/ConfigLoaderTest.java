package org.openmrs.module.pihcore.config;

import org.junit.jupiter.api.Test;
import org.openmrs.module.pihcore.PihCoreContextSensitiveTest;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.StringContains.containsString;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ConfigLoaderTest extends PihCoreContextSensitiveTest {

    @Test
    public void loadShouldThrowClearUnwrappedErrorWhenConfigFileIsMissing() {
        IllegalStateException thrown = assertThrows(IllegalStateException.class,
                () -> ConfigLoader.load("does-not-exist"));

        assertThat(thrown.getMessage(), containsString("does-not-exist"));
        assertThat(thrown.getMessage(), containsString("pih.config"));
    }

    @Test
    public void loadShouldTolerateWhitespaceAroundConfigNamesInCommaDelimitedList() {
        // " default" -- note the leading space -- should still resolve pih-config-default.json
        ConfigDescriptor descriptor = ConfigLoader.load(" default");
        assertThat(descriptor.getWelcomeMessage(), is("Welcome to the PIH EMR"));
    }

    @Test
    public void loadShouldThrowWhenSiteDefaultFileDoesNotExist() {
        // "site-default" is no longer special-cased as optional -- a missing
        // pih-config-site-default.json must fail loudly like any other missing config name
        IllegalStateException thrown = assertThrows(IllegalStateException.class,
                () -> ConfigLoader.load("default,site-default"));

        assertThat(thrown.getMessage(), containsString("site-default"));
    }
}
