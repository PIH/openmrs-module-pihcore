package org.openmrs.module.pihcore.identifier.haiti;

import org.junit.jupiter.api.Test;
import org.openmrs.api.LocationService;
import org.openmrs.api.context.Context;
import org.openmrs.module.idgen.AutoGenerationOption;
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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
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

    /**
     * The local generator with no prefix is used as the production ZL EMR ID generator (TASKS-564), replacing the
     * one hosted on Zanmi, so it must produce the same identifiers as Zanmi for the same sequence value.
     * Expected values computed independently from Zanmi's definition: base ACDEFGHJKLMNPRTUVWXY1234567890,
     * no prefix, first identifier base 10000, length 6, LuhnMod30 check digit
     */
    @Test
    public void localGeneratorWithNoPrefixShouldGenerateSameIdentifiersAsZanmi() {
        loadFromInitializer(Domain.PATIENT_IDENTIFIER_TYPES, "zlIdentifierTypes.csv");
        IdentifierSourceService identifierSourceService = Context.getService(IdentifierSourceService.class);

        ConfigDescriptor descriptor = new ConfigDescriptor();
        descriptor.setCountry(ConfigDescriptor.Country.HAITI);
        descriptor.setLocalZlIdentifierGeneratorEnabled(true);
        ConfigureHaitiIdGenerators generators = new ConfigureHaitiIdGenerators(new Config(descriptor), identifierSourceService);
        ConfigureHaitiIdGenerators.createPatientIdGenerator(generators);

        SequentialIdentifierGenerator generator = generators.getLocalZlIdentifierGenerator();
        assertEquals("ACDEFGHJKLMNPRTUVWXY1234567890", generator.getBaseCharacterSet());
        assertEquals("10000", generator.getFirstIdentifierBase());

        // the idgen processor generates using next_sequence_value as the seed
        assertEquals("10000X", generator.getIdentifierForSeed(17009999L)); // first identifier base 10000
        assertEquals("33WNJ0", generator.getIdentifierForSeed(18429637L)); // Zanmi next_sequence_value as of 2026-10-16
        assertEquals("000000", generator.getIdentifierForSeed(24299999L)); // last available identifier
    }

    /**
     * A local generator with no prefix issues production ZL EMR IDs, so the pool must not draw from it until its
     * sequence has been set to continue from the existing generator, or it would reissue identifiers from the start
     */
    @Test
    public void createPatientIdGeneratorShouldNotUseLocalGeneratorWithNoPrefixUntilSequenceValueIsSet() {
        loadFromInitializer(Domain.PATIENT_IDENTIFIER_TYPES, "zlIdentifierTypes.csv");
        IdentifierSourceService identifierSourceService = Context.getService(IdentifierSourceService.class);

        ConfigDescriptor descriptor = new ConfigDescriptor();
        descriptor.setCountry(ConfigDescriptor.Country.HAITI);
        descriptor.setLocalZlIdentifierGeneratorEnabled(true);
        ConfigureHaitiIdGenerators generators = new ConfigureHaitiIdGenerators(new Config(descriptor), identifierSourceService);

        ConfigureHaitiIdGenerators.createPatientIdGenerator(generators);
        SequentialIdentifierGenerator generator = generators.getLocalZlIdentifierGenerator();
        assertEquals(-1L, identifierSourceService.getSequenceValue(generator));
        assertTrue(generators.getLocalZlIdentifierPool().getSource() instanceof RemoteIdentifierSource);

        // set once at the switchover, with an update to idgen_seq_id_gen
        identifierSourceService.saveSequenceValue(generator, 18429637L);
        ConfigureHaitiIdGenerators.createPatientIdGenerator(generators);
        assertEquals(generator.getUuid(), generators.getLocalZlIdentifierPool().getSource().getUuid());
        assertEquals(18429637L, identifierSourceService.getSequenceValue(generator));
    }

    /**
     * Where generators must be seeded, a local generator with a prefix (eg. on a test server configured like
     * production) is also not used until its sequence value is set
     */
    @Test
    public void createPatientIdGeneratorShouldNotUseLocalGeneratorWithPrefixUntilSequenceValueIsSetIfSeedingRequired() {
        loadFromInitializer(Domain.PATIENT_IDENTIFIER_TYPES, "zlIdentifierTypes.csv");
        IdentifierSourceService identifierSourceService = Context.getService(IdentifierSourceService.class);

        ConfigDescriptor descriptor = new ConfigDescriptor();
        descriptor.setCountry(ConfigDescriptor.Country.HAITI);
        descriptor.setLocalZlIdentifierGeneratorEnabled(true);
        descriptor.setLocalZlIdentifierGeneratorPrefix("Y");
        descriptor.setRequireSeededIdentifierGenerators(true);
        ConfigureHaitiIdGenerators generators = new ConfigureHaitiIdGenerators(new Config(descriptor), identifierSourceService);

        ConfigureHaitiIdGenerators.createPatientIdGenerator(generators);
        SequentialIdentifierGenerator generator = generators.getLocalZlIdentifierGenerator();
        assertTrue(generators.getLocalZlIdentifierPool().getSource() instanceof RemoteIdentifierSource);

        identifierSourceService.saveSequenceValue(generator, 1000L);
        ConfigureHaitiIdGenerators.createPatientIdGenerator(generators);
        assertEquals(generator.getUuid(), generators.getLocalZlIdentifierPool().getSource().getUuid());
    }

    /**
     * Where generators must be seeded, a dossier number generator created in advance of migrating a site has
     * automatic generation disabled until its sequence value is set to continue from the site's existing generator
     */
    @Test
    public void enableDossierNumberGeneratorsOnceSeededShouldOnlyEnableAutomaticGenerationOnceSequenceValueIsSet() {
        loadFromInitializer(Domain.PATIENT_IDENTIFIER_TYPES, "zlIdentifierTypes.csv");
        IdentifierSourceService identifierSourceService = Context.getService(IdentifierSourceService.class);

        ConfigDescriptor descriptor = new ConfigDescriptor();
        descriptor.setCountry(ConfigDescriptor.Country.HAITI);
        descriptor.setRequireSeededIdentifierGenerators(true);
        ConfigureHaitiIdGenerators generators = new ConfigureHaitiIdGenerators(new Config(descriptor), identifierSourceService);

        AutoGenerationOption option = createDossierNumberGeneratorAndOption(generators, identifierSourceService);
        SequentialIdentifierGenerator generator = (SequentialIdentifierGenerator) option.getSource();

        generators.enableDossierNumberGeneratorsOnceSeeded();
        assertFalse(identifierSourceService.getAutoGenerationOption(option.getId()).isAutomaticGenerationEnabled());
        assertNull(identifierSourceService.generateIdentifier(option.getIdentifierType(), option.getLocation(), "test"));

        // set once the site has been migrated, with an update to idgen_seq_id_gen
        identifierSourceService.saveSequenceValue(generator, 1234L);
        generators.enableDossierNumberGeneratorsOnceSeeded();
        assertTrue(identifierSourceService.getAutoGenerationOption(option.getId()).isAutomaticGenerationEnabled());
        assertEquals("CNG001234", generator.getIdentifierForSeed(identifierSourceService.getSequenceValue(generator)));
    }

    @Test
    public void enableDossierNumberGeneratorsOnceSeededShouldNotChangeAutomaticGenerationIfSeedingNotRequired() {
        loadFromInitializer(Domain.PATIENT_IDENTIFIER_TYPES, "zlIdentifierTypes.csv");
        IdentifierSourceService identifierSourceService = Context.getService(IdentifierSourceService.class);

        ConfigDescriptor descriptor = new ConfigDescriptor();
        descriptor.setCountry(ConfigDescriptor.Country.HAITI);
        ConfigureHaitiIdGenerators generators = new ConfigureHaitiIdGenerators(new Config(descriptor), identifierSourceService);

        AutoGenerationOption option = createDossierNumberGeneratorAndOption(generators, identifierSourceService);

        generators.enableDossierNumberGeneratorsOnceSeeded();
        assertTrue(identifierSourceService.getAutoGenerationOption(option.getId()).isAutomaticGenerationEnabled());
    }

    private AutoGenerationOption createDossierNumberGeneratorAndOption(ConfigureHaitiIdGenerators generators, IdentifierSourceService identifierSourceService) {
        SequentialIdentifierGenerator generator = generators.sequentialIdentifierGeneratorForDossier(
                ConfigureHaitiIdGenerators.getDossierIdentifierType(), "CNG", "9dd9bdf3-4b57-47c3-b731-1000dbdef5d8");
        AutoGenerationOption option = new AutoGenerationOption();
        option.setIdentifierType(generator.getIdentifierType());
        option.setSource(generator);
        option.setLocation(Context.getLocationService().getLocation(1));
        option.setManualEntryEnabled(true);
        option.setAutomaticGenerationEnabled(true);
        return identifierSourceService.saveAutoGenerationOption(option);
    }
}
