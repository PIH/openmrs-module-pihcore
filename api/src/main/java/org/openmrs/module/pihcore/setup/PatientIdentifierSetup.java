package org.openmrs.module.pihcore.setup;

import org.openmrs.api.LocationService;
import org.openmrs.module.idgen.service.IdentifierSourceService;
import org.openmrs.module.pihcore.config.Config;
import org.openmrs.module.pihcore.config.ConfigDescriptor;
import org.openmrs.module.pihcore.identifier.haiti.ConfigureHaitiIdGenerators;
import org.openmrs.module.pihcore.identifier.liberia.ConfigureLiberiaIdGenerators;
import org.openmrs.module.pihcore.identifier.mexico.ConfigureMexicoIdGenerators;
import org.openmrs.module.pihcore.identifier.peru.ConfigurePeruIdGenerators;
import org.openmrs.module.pihcore.identifier.sierraLeone.ConfigureSierraLeoneIdGenerators;

public class PatientIdentifierSetup {

    public static final String ARCHIVES = "archives";

    public static void setupIdentifierGeneratorsIfNecessary(IdentifierSourceService identifierSourceService,
                                                      LocationService locationService,
                                                      Config config) {

        if (config.getCountry().equals(ConfigDescriptor.Country.HAITI)) {
            ConfigureHaitiIdGenerators configureHaitiIdGenerators = new ConfigureHaitiIdGenerators(config, identifierSourceService);
            ConfigureHaitiIdGenerators.createPatientIdGenerator(configureHaitiIdGenerators);
            ConfigureHaitiIdGenerators.createDossierNumberGenerator(locationService, configureHaitiIdGenerators, config);
            if (ConfigDescriptor.Specialty.HIV.equals(config.getSpecialty())) {
                ConfigureHaitiIdGenerators.createSourceAndAutoGenerationOptionForHivEmrV1(identifierSourceService);
            }
        }
        else if (config.getCountry().equals(ConfigDescriptor.Country.LIBERIA)) {
            ConfigureLiberiaIdGenerators.configureGenerators(identifierSourceService, config);
        }
        else if (config.getCountry().equals(ConfigDescriptor.Country.SIERRA_LEONE)) {
            ConfigureSierraLeoneIdGenerators.configurePrimaryIdentifierSource(identifierSourceService, config);
        }
        else if (config.getCountry().equals(ConfigDescriptor.Country.MEXICO)) {
            ConfigureMexicoIdGenerators.configureGenerators(identifierSourceService, config);
        }
        else if (config.getCountry().equals(ConfigDescriptor.Country.PERU)) {
            ConfigurePeruIdGenerators.configureGenerators(identifierSourceService, config);
        }
    }
}
