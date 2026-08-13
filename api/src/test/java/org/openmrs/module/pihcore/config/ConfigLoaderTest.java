package org.openmrs.module.pihcore.config;

import org.junit.jupiter.api.Test;
import org.openmrs.module.pihcore.PihCoreContextSensitiveTest;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.StringContains.containsString;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ConfigLoaderTest extends PihCoreContextSensitiveTest {

    @Test
    public void loadShouldThrowExceptionWhenConfigFileIsMissing() {
        RuntimeException thrown = assertThrows(RuntimeException.class, () -> ConfigLoader.load("does-not-exist"));
        assertThat(thrown.getMessage(), containsString("Error loading PIH config"));
        assertThat(thrown.getCause().getMessage(), containsString("No pih config file found with name"));
    }

    @Test
    public void loadShouldTolerateWhitespaceAroundConfigNamesInCommaDelimitedList() {
        ConfigDescriptor descriptor = ConfigLoader.load(" default , override ");
        assertThat(descriptor.getWelcomeMessage(), is("Welcome to the PIH EMR"));
    }
}
