package org.openmrs.module.pihcore.setup;

import org.openmrs.PatientIdentifierType;
import org.openmrs.api.LocationService;
import org.openmrs.module.idgen.IdentifierPool;
import org.openmrs.module.idgen.RemoteIdentifierSource;
import org.openmrs.module.idgen.SequentialIdentifierGenerator;
import org.openmrs.module.idgen.service.IdentifierSourceService;
import org.openmrs.module.metadatadeploy.MetadataUtils;
import org.openmrs.module.pihcore.PihCoreConstants;
import org.openmrs.module.pihcore.config.Config;
import org.openmrs.module.pihcore.config.ConfigDescriptor;
import org.openmrs.module.pihcore.identifier.ConfigureIdGenerators;
import org.openmrs.module.pihcore.metadata.core.PatientIdentifierTypes;
import org.openmrs.module.pihcore.metadata.haiti.mirebalais.MirebalaisLocations;

public class PatientIdentifierSetup {

    public static final String ARCHIVES = "archives";

    public static void setupIdentifierGeneratorsIfNecessary(IdentifierSourceService identifierSourceService,
                                                      LocationService locationService,
                                                      Config config
                                                      ) {

        ConfigureIdGenerators configureIdGenerators = new ConfigureIdGenerators(identifierSourceService, locationService);

        if (config.getCountry().equals(ConfigDescriptor.Country.HAITI)) {
            createPatientIdGenerator(configureIdGenerators);
        }

        if (config.isComponentEnabled(ARCHIVES)) {
            createDossierNumberGenerator(locationService, configureIdGenerators, config);
        }

    }

    private static void createPatientIdGenerator( ConfigureIdGenerators configureIdGenerators) {
        PatientIdentifierType zlIdentifierType = getZlIdentifierType();
        RemoteIdentifierSource remoteZlIdentifierSource = configureIdGenerators.remoteZlIdentifierSource(zlIdentifierType);
        IdentifierPool localZlIdentifierPool = configureIdGenerators.localZlIdentifierSource(remoteZlIdentifierSource);
        configureIdGenerators.setAutoGenerationOptionsForZlIdentifier(localZlIdentifierPool);
    }

    private static void createDossierNumberGenerator(LocationService locationService, ConfigureIdGenerators configureIdGenerators, Config config) {

        // TODO configure dossier generators for sites besides Mirebalais, if any of them start using the archives app
        if (config.getSite().equals(ConfigDescriptor.Site.MIREBALAIS)) {
            PatientIdentifierType dossierIdentifierType = getDossierIdentifierType();

            SequentialIdentifierGenerator sequentialIdentifierGeneratorForUHM = configureIdGenerators
                    .sequentialIdentifierGeneratorForDossier(dossierIdentifierType,
                            PihCoreConstants.UHM_DOSSIER_NUMBER_PREFIX,
                            PihCoreConstants.UHM_DOSSIER_NUMBER_IDENTIFIER_SOURCE_UUID);

            configureIdGenerators.setAutoGenerationOptionsForDossierNumberGenerator(sequentialIdentifierGeneratorForUHM,
                    locationService.getLocationByUuid(MirebalaisLocations.MIREBALAIS_HOSPITAL.uuid()));

            SequentialIdentifierGenerator sequentialIdentifierGeneratorForCDI = configureIdGenerators
                    .sequentialIdentifierGeneratorForDossier(dossierIdentifierType,
                            PihCoreConstants.CDI_DOSSIER_NUMBER_PREFIX,
                            PihCoreConstants.CDI_DOSSIER_NUMBER_IDENTIFIER_SOURCE_UUID);

            configureIdGenerators.setAutoGenerationOptionsForDossierNumberGenerator(sequentialIdentifierGeneratorForCDI,
                    locationService.getLocationByUuid(MirebalaisLocations.CDI_KLINIK_EKSTEN_JENERAL.uuid()));

        }

    }

    public static PatientIdentifierType getZlIdentifierType() {
        return MetadataUtils.existing(PatientIdentifierType.class, PatientIdentifierTypes.ZL_EMR_ID.uuid());
    }

    public static PatientIdentifierType getDossierIdentifierType() {
        return MetadataUtils.existing(PatientIdentifierType.class, PatientIdentifierTypes.DOSSIER_NUMBER.uuid());
    }
}
