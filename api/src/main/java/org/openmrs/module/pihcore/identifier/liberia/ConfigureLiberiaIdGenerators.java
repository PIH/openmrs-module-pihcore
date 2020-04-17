package org.openmrs.module.pihcore.identifier.liberia;

import org.openmrs.Location;
import org.openmrs.PatientIdentifierType;
import org.openmrs.module.idgen.AutoGenerationOption;
import org.openmrs.module.idgen.SequentialIdentifierGenerator;
import org.openmrs.module.idgen.service.IdentifierSourceService;
import org.openmrs.module.metadatadeploy.MetadataUtils;
import org.openmrs.module.pihcore.config.Config;
import org.openmrs.module.pihcore.config.ConfigDescriptor;
import org.openmrs.module.pihcore.metadata.liberia.LiberiaLocations;
import org.openmrs.module.pihcore.metadata.liberia.LiberiaPatientIdentifierTypes;

public class ConfigureLiberiaIdGenerators {


    public static final String LIBERIA_PRIMARY_IDENTIFIER_SOURCE_UUID = "06817a00-f405-11e4-b939-0800200c9a66";

    public static void configureGenerators(IdentifierSourceService identifierSourceService, Config config) {

        Location identifierLocation = getIdentifierLocation(config);

        SequentialIdentifierGenerator liberiaPrimaryIdentifierSource = (SequentialIdentifierGenerator) identifierSourceService.getIdentifierSourceByUuid(LIBERIA_PRIMARY_IDENTIFIER_SOURCE_UUID);
        if (liberiaPrimaryIdentifierSource == null) {
            liberiaPrimaryIdentifierSource = new SequentialIdentifierGenerator();
        }

        liberiaPrimaryIdentifierSource.setName("Liberia Primary Identifier Source");
        liberiaPrimaryIdentifierSource.setDescription("Primary Identifier Generator for Liberia");
        liberiaPrimaryIdentifierSource.setIdentifierType(MetadataUtils.existing(PatientIdentifierType.class, LiberiaPatientIdentifierTypes.LIBERIA_EMR_ID.uuid()));
        liberiaPrimaryIdentifierSource.setPrefix(config.getPrimaryIdentifierPrefix());
        liberiaPrimaryIdentifierSource.setMinLength(7 + liberiaPrimaryIdentifierSource.getPrefix().length());
        liberiaPrimaryIdentifierSource.setMaxLength(8 + liberiaPrimaryIdentifierSource.getPrefix().length());
        liberiaPrimaryIdentifierSource.setBaseCharacterSet("0123456789");
        if (config.getSite().equals(ConfigDescriptor.Site.HARPER)) {
            liberiaPrimaryIdentifierSource.setFirstIdentifierBase("0100000");
        } else {
            liberiaPrimaryIdentifierSource.setFirstIdentifierBase("0000001");
        }
        liberiaPrimaryIdentifierSource.setUuid(LIBERIA_PRIMARY_IDENTIFIER_SOURCE_UUID);

        identifierSourceService.saveIdentifierSource(liberiaPrimaryIdentifierSource);

        AutoGenerationOption autoGenerationOption = identifierSourceService.getAutoGenerationOption(liberiaPrimaryIdentifierSource
                .getIdentifierType());

        if (autoGenerationOption == null) {
            autoGenerationOption = new AutoGenerationOption();
        }

        autoGenerationOption.setIdentifierType(MetadataUtils.existing(PatientIdentifierType.class, LiberiaPatientIdentifierTypes.LIBERIA_EMR_ID.uuid()));
        autoGenerationOption.setSource(liberiaPrimaryIdentifierSource);
        autoGenerationOption.setAutomaticGenerationEnabled(true);
        autoGenerationOption.setManualEntryEnabled(false);
        autoGenerationOption.setLocation(identifierLocation);

        identifierSourceService.saveAutoGenerationOption(autoGenerationOption);
    }

    private static Location getIdentifierLocation(Config config) {
        return MetadataUtils.existing(Location.class, LiberiaLocations.HEALTH_FACILITY.uuid());
    }

}
