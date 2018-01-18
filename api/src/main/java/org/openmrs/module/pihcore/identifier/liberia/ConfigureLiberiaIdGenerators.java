package org.openmrs.module.pihcore.identifier.liberia;

import org.openmrs.Location;
import org.openmrs.PatientIdentifierType;
import org.openmrs.module.idgen.AutoGenerationOption;
import org.openmrs.module.idgen.SequentialIdentifierGenerator;
import org.openmrs.module.idgen.service.IdentifierSourceService;
import org.openmrs.module.metadatadeploy.MetadataUtils;
import org.openmrs.module.pihcore.config.Config;
import org.openmrs.module.pihcore.metadata.liberia.LiberiaLocations;
import org.openmrs.module.pihcore.metadata.liberia.LiberiaPatientIdentifierTypes;

public class ConfigureLiberiaIdGenerators {


    public static final String PLEEBO_PRIMARY_IDENTIFIER_SOURCE_UUID = "06817a00-f405-11e4-b939-0800200c9a66";

    public static void configureGenerators(IdentifierSourceService identifierSourceService, Config config) {

        Location identifierLocation = MetadataUtils.existing(Location.class, LiberiaLocations.PLEEBO.uuid());

        SequentialIdentifierGenerator pleeboPrimaryIdentifierSource = (SequentialIdentifierGenerator) identifierSourceService.getIdentifierSourceByUuid(PLEEBO_PRIMARY_IDENTIFIER_SOURCE_UUID);
        if (pleeboPrimaryIdentifierSource == null) {
            pleeboPrimaryIdentifierSource = new SequentialIdentifierGenerator();
        }

        pleeboPrimaryIdentifierSource.setName("Pleebo Primary Identifier Source");
        pleeboPrimaryIdentifierSource.setDescription("Primary Identifier Generator for Pleebo");
        pleeboPrimaryIdentifierSource.setIdentifierType(MetadataUtils.existing(PatientIdentifierType.class, LiberiaPatientIdentifierTypes.PLEEBO_EMR_ID.uuid()));
        pleeboPrimaryIdentifierSource.setPrefix(config.getPrimaryIdentifierPrefix());
        pleeboPrimaryIdentifierSource.setMinLength(7 + pleeboPrimaryIdentifierSource.getPrefix().length());
        pleeboPrimaryIdentifierSource.setMaxLength(8 + pleeboPrimaryIdentifierSource.getPrefix().length());
        pleeboPrimaryIdentifierSource.setBaseCharacterSet("0123456789");
        pleeboPrimaryIdentifierSource.setFirstIdentifierBase("0000001");
        pleeboPrimaryIdentifierSource.setUuid(PLEEBO_PRIMARY_IDENTIFIER_SOURCE_UUID);

        identifierSourceService.saveIdentifierSource(pleeboPrimaryIdentifierSource);

        AutoGenerationOption autoGenerationOption = identifierSourceService.getAutoGenerationOption(pleeboPrimaryIdentifierSource
                .getIdentifierType(), identifierLocation);

        if (autoGenerationOption == null) {
            autoGenerationOption = new AutoGenerationOption();
        }

        autoGenerationOption.setIdentifierType(MetadataUtils.existing(PatientIdentifierType.class, LiberiaPatientIdentifierTypes.PLEEBO_EMR_ID.uuid()));
        autoGenerationOption.setSource(pleeboPrimaryIdentifierSource);
        autoGenerationOption.setAutomaticGenerationEnabled(true);
        autoGenerationOption.setManualEntryEnabled(false);
        autoGenerationOption.setLocation(identifierLocation);

        identifierSourceService.saveAutoGenerationOption(autoGenerationOption);
    }

}
