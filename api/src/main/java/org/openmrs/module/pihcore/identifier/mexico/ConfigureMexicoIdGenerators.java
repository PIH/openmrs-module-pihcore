package org.openmrs.module.pihcore.identifier.mexico;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Location;
import org.openmrs.PatientIdentifierType;
import org.openmrs.api.context.Context;
import org.openmrs.module.idgen.AutoGenerationOption;
import org.openmrs.module.idgen.IdentifierSource;
import org.openmrs.module.idgen.SequentialIdentifierGenerator;
import org.openmrs.module.idgen.service.IdentifierSourceService;
import org.openmrs.module.metadatadeploy.MetadataUtils;
import org.openmrs.module.pihcore.config.Config;
import org.openmrs.module.pihcore.metadata.mexico.MexicoLocations;
import org.openmrs.module.pihcore.metadata.mexico.MexicoPatientIdentifierTypes;

public class ConfigureMexicoIdGenerators {

    protected static Log log = LogFactory.getLog(ConfigureMexicoIdGenerators.class);

    private static final String CHIAPAS_PRIMARY_IDENTIFIER_SOURCE_UUID = "cf2b23e6-7a32-11e8-8624-54ee75ef41c2";
    private static final String CHIAPAS_DOSSIER_SOURCE_UUID = "28f83bda-d871-40e7-9f80-5dab0f219620";

    public static void configureGenerators(IdentifierSourceService identifierSourceService, Config config) {
        createPrimaryIdentifierGenerator(identifierSourceService, config);
        createDossierNumberGenerator(identifierSourceService, config);
    }

    private static void createPrimaryIdentifierGenerator(IdentifierSourceService identifierSourceService, Config config) {
        Location identifierLocation = MetadataUtils.existing(Location.class, MexicoLocations.CHIAPAS.uuid());

        SequentialIdentifierGenerator chiapasPrimaryIdentifierSource = (SequentialIdentifierGenerator)
                identifierSourceService.getIdentifierSourceByUuid(CHIAPAS_PRIMARY_IDENTIFIER_SOURCE_UUID);
        if (chiapasPrimaryIdentifierSource == null) {
            chiapasPrimaryIdentifierSource = new SequentialIdentifierGenerator();
        }

        chiapasPrimaryIdentifierSource.setName("Chiapas Primary Identifier Source");
        chiapasPrimaryIdentifierSource.setDescription("Primary Identifier Generator for Chiapas");
        chiapasPrimaryIdentifierSource.setIdentifierType(MetadataUtils.existing(PatientIdentifierType.class,
                MexicoPatientIdentifierTypes.CHIAPAS_EMR_ID.uuid()));
        String prefixForSite = config.getPrimaryIdentifierPrefix();
        chiapasPrimaryIdentifierSource.setPrefix(prefixForSite);
        chiapasPrimaryIdentifierSource.setMinLength(7 + chiapasPrimaryIdentifierSource.getPrefix().length());
        chiapasPrimaryIdentifierSource.setMaxLength(7 + chiapasPrimaryIdentifierSource.getPrefix().length());
        chiapasPrimaryIdentifierSource.setBaseCharacterSet("0123456789");
        chiapasPrimaryIdentifierSource.setFirstIdentifierBase("0000001");
        chiapasPrimaryIdentifierSource.setUuid(CHIAPAS_PRIMARY_IDENTIFIER_SOURCE_UUID);

        identifierSourceService.saveIdentifierSource(chiapasPrimaryIdentifierSource);

        AutoGenerationOption autoGenerationOption = identifierSourceService.getAutoGenerationOption(
                chiapasPrimaryIdentifierSource.getIdentifierType());

        if (autoGenerationOption == null) {
            autoGenerationOption = new AutoGenerationOption();
        }

        autoGenerationOption.setIdentifierType(MetadataUtils.existing(PatientIdentifierType.class,
                MexicoPatientIdentifierTypes.CHIAPAS_EMR_ID.uuid()));
        autoGenerationOption.setSource(chiapasPrimaryIdentifierSource);
        autoGenerationOption.setAutomaticGenerationEnabled(true);
        autoGenerationOption.setManualEntryEnabled(false);
        autoGenerationOption.setLocation(identifierLocation);

        identifierSourceService.saveAutoGenerationOption(autoGenerationOption);
    }

    private static void createDossierNumberGenerator(IdentifierSourceService identifierSourceService, Config config) {

        PatientIdentifierType dossierIdentifierType = MetadataUtils.existing(PatientIdentifierType.class, MexicoPatientIdentifierTypes.MEXICO_DOSSIER_NUMBER.uuid());
        String prefix = config.getDossierIdentifierPrefix();

        if (prefix == null) {
            log.warn("dossierIdentifierPrefix not configured; Dossier ID Generator will not be configured");
        } else {
            SequentialIdentifierGenerator sequentialIdentifierGenerator = (SequentialIdentifierGenerator) Context.getService(
                    IdentifierSourceService.class).getIdentifierSourceByUuid(
                    CHIAPAS_DOSSIER_SOURCE_UUID);

            if (sequentialIdentifierGenerator == null) {
                sequentialIdentifierGenerator = new SequentialIdentifierGenerator();
                sequentialIdentifierGenerator.setName("Sequential Generator for Dossier");
                sequentialIdentifierGenerator.setUuid(CHIAPAS_DOSSIER_SOURCE_UUID);
                sequentialIdentifierGenerator.setMaxLength(6 + prefix.length());
                sequentialIdentifierGenerator.setMinLength(6 + prefix.length());
                sequentialIdentifierGenerator.setPrefix(prefix);
                sequentialIdentifierGenerator.setBaseCharacterSet("0123456789");
                sequentialIdentifierGenerator.setFirstIdentifierBase("000001");
                sequentialIdentifierGenerator.setIdentifierType(dossierIdentifierType);
                identifierSourceService.saveIdentifierSource(sequentialIdentifierGenerator);
            }

            setAutoGenerationOptionsForDossierNumberGenerator(identifierSourceService, sequentialIdentifierGenerator);
        }
    }

    // @bistenes: Is any of this actually necessary?
    private static void setAutoGenerationOptionsForDossierNumberGenerator(
            IdentifierSourceService identifierSourceService,
            IdentifierSource identifierSource) {

        AutoGenerationOption autoGenerationOption = identifierSourceService.getAutoGenerationOption(identifierSource
                .getIdentifierType());

        if (autoGenerationOption == null) {
            autoGenerationOption = new AutoGenerationOption();
        }

        autoGenerationOption.setIdentifierType(identifierSource.getIdentifierType());
        autoGenerationOption.setSource(identifierSource);
        autoGenerationOption.setManualEntryEnabled(true);
        autoGenerationOption.setAutomaticGenerationEnabled(true);

        identifierSourceService.saveAutoGenerationOption(autoGenerationOption);
    }


}
