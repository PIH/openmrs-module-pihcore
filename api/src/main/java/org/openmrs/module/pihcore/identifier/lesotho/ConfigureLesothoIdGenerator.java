package org.openmrs.module.pihcore.identifier.lesotho;

import org.openmrs.api.context.Context;
import org.openmrs.module.idgen.AutoGenerationOption;
import org.openmrs.module.idgen.SequentialIdentifierGenerator;
import org.openmrs.module.idgen.service.IdentifierSourceService;
import org.openmrs.module.pihcore.LesothoConfigConstants;

public class ConfigureLesothoIdGenerator {

    // TODO: all just for demo purposes, change this as we get requirements
    public static final String LESOTHO_SAMPLE_IDENTIFIER_SOURCE_UUID = "c8f25496-da86-45c3-a621-e9694d89f0b2";

    public static void configureGenerators(IdentifierSourceService identifierSourceService) {
        SequentialIdentifierGenerator lesothoPrimaryIdentifierSource = (SequentialIdentifierGenerator) identifierSourceService.getIdentifierSourceByUuid(LESOTHO_SAMPLE_IDENTIFIER_SOURCE_UUID);
        if (lesothoPrimaryIdentifierSource == null) {
            lesothoPrimaryIdentifierSource = new SequentialIdentifierGenerator();
        }

        lesothoPrimaryIdentifierSource.setName("Lesotho Primary Identifier Source");
        lesothoPrimaryIdentifierSource.setDescription("Primary Identifier Generator for Lesotho");
        lesothoPrimaryIdentifierSource.setIdentifierType(Context.getPatientService().getPatientIdentifierTypeByUuid(LesothoConfigConstants.PATIENTIDENTIFIERTYPE_SAMPLEIDENTIFIER_UUID));
        lesothoPrimaryIdentifierSource.setPrefix("X");  // TODO: figure out prefix
        lesothoPrimaryIdentifierSource.setMinLength(8);
        lesothoPrimaryIdentifierSource.setMaxLength(9);
        lesothoPrimaryIdentifierSource.setBaseCharacterSet("0123456789");
        lesothoPrimaryIdentifierSource.setFirstIdentifierBase("0000001");
        lesothoPrimaryIdentifierSource.setUuid(LESOTHO_SAMPLE_IDENTIFIER_SOURCE_UUID);

        identifierSourceService.saveIdentifierSource(lesothoPrimaryIdentifierSource);

        AutoGenerationOption autoGenerationOption = identifierSourceService.getAutoGenerationOption(lesothoPrimaryIdentifierSource
                .getIdentifierType());

        if (autoGenerationOption == null) {
            autoGenerationOption = new AutoGenerationOption();
        }

        autoGenerationOption.setIdentifierType(Context.getPatientService().getPatientIdentifierTypeByUuid(LesothoConfigConstants.PATIENTIDENTIFIERTYPE_SAMPLEIDENTIFIER_UUID));
        autoGenerationOption.setSource(lesothoPrimaryIdentifierSource);
        autoGenerationOption.setAutomaticGenerationEnabled(true);
        autoGenerationOption.setManualEntryEnabled(false);

        identifierSourceService.saveAutoGenerationOption(autoGenerationOption);

    }


}
