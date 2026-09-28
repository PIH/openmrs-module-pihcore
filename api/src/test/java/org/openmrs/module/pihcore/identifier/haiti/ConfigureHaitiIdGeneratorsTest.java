package org.openmrs.module.pihcore.identifier.haiti;

import org.junit.jupiter.api.Test;
import org.openmrs.api.LocationService;
import org.openmrs.api.context.Context;
import org.openmrs.module.idgen.IdentifierPool;
import org.openmrs.module.idgen.IdentifierSource;
import org.openmrs.module.idgen.RemoteIdentifierSource;
import org.openmrs.module.idgen.SequentialIdentifierGenerator;
import org.openmrs.module.idgen.service.IdentifierSourceService;
import org.openmrs.module.initializer.Domain;
import org.openmrs.module.pihcore.PihCoreContextSensitiveTest;
import org.openmrs.module.pihcore.config.Config;
import org.openmrs.module.pihcore.config.ConfigDescriptor;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

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

    /**
     * A database created with the remote ZL identifier source (e.g. a seed image built without
     * haiti-local-idgen) that is later started with the local generator enabled should have its
     * existing Local Pool of ZL Identifiers repointed at the local generator, rather than
     * continuing to refill from the remote source.
     */
    @Test
    public void createPatientIdGeneratorShouldRepointExistingPoolWhenLocalGeneratorEnabled() {
        loadFromInitializer(Domain.PATIENT_IDENTIFIER_TYPES, "zlIdentifierTypes.csv");
        IdentifierSourceService identifierSourceService = Context.getService(IdentifierSourceService.class);

        ConfigDescriptor remoteDescriptor = new ConfigDescriptor();
        remoteDescriptor.setCountry(ConfigDescriptor.Country.HAITI);
        ConfigureHaitiIdGenerators remoteGenerators = new ConfigureHaitiIdGenerators(new Config(remoteDescriptor), identifierSourceService);
        ConfigureHaitiIdGenerators.createPatientIdGenerator(remoteGenerators);
        assertTrue(remoteGenerators.getLocalZlIdentifierPool().getSource() instanceof RemoteIdentifierSource);

        ConfigDescriptor localDescriptor = new ConfigDescriptor();
        localDescriptor.setCountry(ConfigDescriptor.Country.HAITI);
        localDescriptor.setLocalZlIdentifierGeneratorEnabled(true);
        localDescriptor.setLocalZlIdentifierGeneratorPrefix("Y");
        ConfigureHaitiIdGenerators localGenerators = new ConfigureHaitiIdGenerators(new Config(localDescriptor), identifierSourceService);
        ConfigureHaitiIdGenerators.createPatientIdGenerator(localGenerators);

        IdentifierPool pool = localGenerators.getLocalZlIdentifierPool();
        IdentifierSource source = pool.getSource();
        assertTrue(source instanceof SequentialIdentifierGenerator);
        assertEquals(localGenerators.getLocalZlIdentifierGenerator().getUuid(), source.getUuid());
    }
}
