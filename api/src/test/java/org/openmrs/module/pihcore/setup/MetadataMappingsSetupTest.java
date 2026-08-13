package org.openmrs.module.pihcore.setup;

import org.junit.jupiter.api.Test;
import org.openmrs.api.context.Context;
import org.openmrs.module.metadatamapping.api.MetadataMappingService;
import org.openmrs.module.pihcore.PihCoreContextSensitiveTest;
import org.openmrs.module.pihcore.config.Config;
import org.openmrs.module.pihcore.config.ConfigDescriptor;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

public class MetadataMappingsSetupTest extends PihCoreContextSensitiveTest {

    @Override
    public String getPihConfig() {
        return "default";
    }

    /**
     * A generic, country-only Sierra Leone config (no site) should not throw when setting up the
     * primary identifier type -- previously this unconditionally threw an IllegalStateException
     * because neither isWellbody() nor isKgh() matched a null site.
     *
     * This same null-site path now also logs a WARN (via MetadataMappingsSetup.log) explaining that
     * primary identifier type mapping is being skipped. Asserting on log output content isn't
     * exercised here (no existing precedent in this test suite for capturing commons-logging output,
     * and standing up an appender for one assertion isn't worth the added fragility); this test
     * still confirms the warning path completes without throwing.
     */
    @Test
    public void setupPrimaryIdentifierTypeBasedOnCountryShouldNotThrowForSierraLeoneWithNullSite() {
        ConfigDescriptor descriptor = new ConfigDescriptor();
        descriptor.setCountry(ConfigDescriptor.Country.SIERRA_LEONE);
        // deliberately not calling descriptor.setSite(...) -- site is null
        Config config = new Config(descriptor);

        MetadataMappingService mms = Context.getService(MetadataMappingService.class);

        assertDoesNotThrow(() ->
                MetadataMappingsSetup.setupPrimaryIdentifierTypeBasedOnCountry(mms, Context.getPatientService(), config));
    }
}
