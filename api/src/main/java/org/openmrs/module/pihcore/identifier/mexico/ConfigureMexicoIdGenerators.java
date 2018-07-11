package org.openmrs.module.pihcore.identifier.mexico;

import org.openmrs.Location;
import org.openmrs.PatientIdentifierType;
import org.openmrs.module.idgen.AutoGenerationOption;
import org.openmrs.module.idgen.SequentialIdentifierGenerator;
import org.openmrs.module.idgen.service.IdentifierSourceService;
import org.openmrs.module.metadatadeploy.MetadataUtils;
import org.openmrs.module.pihcore.metadata.mexico.MexicoLocations;
import org.openmrs.module.pihcore.metadata.mexico.MexicoPatientIdentifierTypes;
import org.openmrs.module.pihcore.metadata.sierraLeone.SierraLeoneLocations;
import org.openmrs.module.pihcore.metadata.sierraLeone.SierraLeonePatientIdentifierTypes;

public class ConfigureMexicoIdGenerators {

    public static final String CHIAPAS_PRIMARY_IDENTIFIER_SOURCE_UUID = "cf2b23e6-7a32-11e8-8624-54ee75ef41c2";

    public static void configureGenerators(IdentifierSourceService identifierSourceService) {

        Location identifierLocation = MetadataUtils.existing(Location.class, MexicoLocations.JALTENANGO.uuid());

        SequentialIdentifierGenerator chiapasPrimaryIdentifierSource = (SequentialIdentifierGenerator)
                identifierSourceService.getIdentifierSourceByUuid(CHIAPAS_PRIMARY_IDENTIFIER_SOURCE_UUID);
        if (chiapasPrimaryIdentifierSource == null) {
            chiapasPrimaryIdentifierSource = new SequentialIdentifierGenerator();
        }

        chiapasPrimaryIdentifierSource.setName("Chiapas Primary Identifier Source");
        chiapasPrimaryIdentifierSource.setDescription("Primary Identifier Generator for Chiapas");
        chiapasPrimaryIdentifierSource.setIdentifierType(MetadataUtils.existing(PatientIdentifierType.class,
                MexicoPatientIdentifierTypes.CHIAPAS_EMR_ID.uuid()));
        chiapasPrimaryIdentifierSource.setPrefix("W");
        chiapasPrimaryIdentifierSource.setMinLength(7 + chiapasPrimaryIdentifierSource.getPrefix().length());
        chiapasPrimaryIdentifierSource.setMaxLength(8 + chiapasPrimaryIdentifierSource.getPrefix().length());
        chiapasPrimaryIdentifierSource.setBaseCharacterSet("0123456789");
        chiapasPrimaryIdentifierSource.setFirstIdentifierBase("0000001");
        chiapasPrimaryIdentifierSource.setUuid(CHIAPAS_PRIMARY_IDENTIFIER_SOURCE_UUID);

        identifierSourceService.saveIdentifierSource(chiapasPrimaryIdentifierSource);

        AutoGenerationOption autoGenerationOption = identifierSourceService.getAutoGenerationOption(
                chiapasPrimaryIdentifierSource.getIdentifierType(), identifierLocation);

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

}
