package org.openmrs.module.pihcore.identifier.sierraLeone;

import org.openmrs.Location;
import org.openmrs.PatientIdentifierType;
import org.openmrs.module.idgen.AutoGenerationOption;
import org.openmrs.module.idgen.SequentialIdentifierGenerator;
import org.openmrs.module.idgen.service.IdentifierSourceService;
import org.openmrs.module.metadatadeploy.MetadataUtils;
import org.openmrs.module.pihcore.metadata.sierraLeone.SierraLeoneLocations;
import org.openmrs.module.pihcore.metadata.sierraLeone.SierraLeonePatientIdentifierTypes;

public class ConfigureSierraLeoneIdGenerators {

    public static final String WELLBODY_PRIMARY_IDENTIFIER_SOURCE_UUID = "a1516200-7427-11e5-a837-0800200c9a66";

    public static void configureGenerators(IdentifierSourceService identifierSourceService) {

        Location identifierLocation = MetadataUtils.existing(Location.class, SierraLeoneLocations.WELLBODY_HEALTH_CENTER.uuid());

        SequentialIdentifierGenerator wellbodyPrimaryIdentifierSource = (SequentialIdentifierGenerator) identifierSourceService.getIdentifierSourceByUuid(WELLBODY_PRIMARY_IDENTIFIER_SOURCE_UUID);
        if (wellbodyPrimaryIdentifierSource == null) {
            wellbodyPrimaryIdentifierSource = new SequentialIdentifierGenerator();
        }

        wellbodyPrimaryIdentifierSource.setName("Wellbody Primary Identifier Source");
        wellbodyPrimaryIdentifierSource.setDescription("Primary Identifier Generator for Wellbody");
        wellbodyPrimaryIdentifierSource.setIdentifierType(MetadataUtils.existing(PatientIdentifierType.class, SierraLeonePatientIdentifierTypes.WELLBODY_EMR_ID.uuid()));
        wellbodyPrimaryIdentifierSource.setPrefix("WBA");
        wellbodyPrimaryIdentifierSource.setMinLength(9);
        wellbodyPrimaryIdentifierSource.setMaxLength(9);
        wellbodyPrimaryIdentifierSource.setBaseCharacterSet("1234567890");
        wellbodyPrimaryIdentifierSource.setFirstIdentifierBase("100001");
        wellbodyPrimaryIdentifierSource.setUuid(WELLBODY_PRIMARY_IDENTIFIER_SOURCE_UUID);

        identifierSourceService.saveIdentifierSource(wellbodyPrimaryIdentifierSource);

        AutoGenerationOption autoGenerationOption = identifierSourceService.getAutoGenerationOption(wellbodyPrimaryIdentifierSource
                .getIdentifierType(), identifierLocation);

        if (autoGenerationOption == null) {
            autoGenerationOption = new AutoGenerationOption();
        }

        autoGenerationOption.setIdentifierType(MetadataUtils.existing(PatientIdentifierType.class, SierraLeonePatientIdentifierTypes.WELLBODY_EMR_ID.uuid()));
        autoGenerationOption.setSource(wellbodyPrimaryIdentifierSource);
        autoGenerationOption.setAutomaticGenerationEnabled(true);
        autoGenerationOption.setManualEntryEnabled(false);
        autoGenerationOption.setLocation(identifierLocation);

        identifierSourceService.saveAutoGenerationOption(autoGenerationOption);
    }

}
