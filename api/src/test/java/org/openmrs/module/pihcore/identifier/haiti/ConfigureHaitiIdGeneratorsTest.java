package org.openmrs.module.pihcore.identifier.haiti;

import org.junit.jupiter.api.Test;
import org.openmrs.api.LocationService;
import org.openmrs.api.context.Context;
import org.openmrs.module.idgen.service.IdentifierSourceService;
import org.openmrs.module.pihcore.PihCoreContextSensitiveTest;
import org.openmrs.module.pihcore.config.Config;
import org.openmrs.module.pihcore.config.ConfigDescriptor;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

public class ConfigureHaitiIdGeneratorsTest extends PihCoreContextSensitiveTest {

    @Override
    public String getPihConfig() {
        return "default";
    }

    /**
     * A generic, country-only Haiti config (no site, no dossierIdentifierPrefix) should not throw
     * when creating the dossier number generator -- previously this threw a NullPointerException
     * on config.getSite().equalsIgnoreCase(...) (both the "MIREBALAIS" and "CENTRAL" comparisons).
     * With no site and no dossierIdentifierPrefix, none of the three branches should apply, and the
     * method should simply do nothing.
     */
    @Test
    public void createDossierNumberGeneratorShouldNotThrowForNullSite() {
        ConfigDescriptor descriptor = new ConfigDescriptor();
        descriptor.setCountry(ConfigDescriptor.Country.HAITI);
        // deliberately not calling descriptor.setSite(...) -- site is null
        // deliberately not calling descriptor.setDossierIdentifierPrefix(...) -- also null
        Config config = new Config(descriptor);

        IdentifierSourceService identifierSourceService = Context.getService(IdentifierSourceService.class);
        LocationService locationService = Context.getLocationService();
        ConfigureHaitiIdGenerators configureHaitiIdGenerators = new ConfigureHaitiIdGenerators(config, identifierSourceService);

        assertDoesNotThrow(() ->
                ConfigureHaitiIdGenerators.createDossierNumberGenerator(locationService, configureHaitiIdGenerators, config));
    }
}
