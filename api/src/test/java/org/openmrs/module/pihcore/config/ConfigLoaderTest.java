package org.openmrs.module.pihcore.config;

import org.junit.jupiter.api.Test;
import org.openmrs.module.pihcore.PihCoreContextSensitiveTest;

import static org.hamcrest.MatcherAssert.assertThat;
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
}
