package org.openmrs.module.pihcore.identifier.sierraLeone;

import org.junit.jupiter.api.Test;
import org.openmrs.api.context.Context;
import org.openmrs.module.idgen.service.IdentifierSourceService;
import org.openmrs.module.pihcore.PihCoreContextSensitiveTest;
import org.openmrs.module.pihcore.config.Config;
import org.openmrs.module.pihcore.config.ConfigDescriptor;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

public class ConfigureSierraLeoneIdGeneratorsTest extends PihCoreContextSensitiveTest {

    @Override
    public String getPihConfig() {
        return "default";
    }

    /**
     * A generic, country-only Sierra Leone config (no site) should not throw when configuring the
     * primary identifier source -- previously this unconditionally threw an IllegalStateException
     * because neither isWellbody() nor isKgh() matched a null site.
     */
    @Test
    public void configurePrimaryIdentifierSourceShouldNotThrowForNullSite() {
        ConfigDescriptor descriptor = new ConfigDescriptor();
        descriptor.setCountry(ConfigDescriptor.Country.SIERRA_LEONE);
        // deliberately not calling descriptor.setSite(...) -- site is null
        Config config = new Config(descriptor);

        IdentifierSourceService iss = Context.getService(IdentifierSourceService.class);

        assertDoesNotThrow(() -> ConfigureSierraLeoneIdGenerators.configurePrimaryIdentifierSource(iss, config));
    }
}
