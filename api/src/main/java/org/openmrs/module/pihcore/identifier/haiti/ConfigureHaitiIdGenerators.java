package org.openmrs.module.pihcore.identifier.haiti;

import org.openmrs.Location;
import org.openmrs.PatientIdentifierType;
import org.openmrs.api.LocationService;
import org.openmrs.api.context.Context;
import org.openmrs.module.idgen.AutoGenerationOption;
import org.openmrs.module.idgen.IdentifierPool;
import org.openmrs.module.idgen.IdentifierSource;
import org.openmrs.module.idgen.RemoteIdentifierSource;
import org.openmrs.module.idgen.SequentialIdentifierGenerator;
import org.openmrs.module.idgen.service.IdentifierSourceService;
import org.openmrs.module.metadatadeploy.MetadataUtils;
import org.openmrs.module.pihcore.PihCoreConstants;
import org.openmrs.module.pihcore.config.Config;
import org.openmrs.module.pihcore.metadata.haiti.PihHaitiPatientIdentifierTypes;
import org.openmrs.module.pihcore.metadata.haiti.mirebalais.MirebalaisLocations;

public class ConfigureHaitiIdGenerators {

    public static final String LOCAL_ZL_IDENTIFIER_GENERATOR_ENABLED = "local_zl_identifier_generator_enabled";
    public static final String LOCAL_ZL_IDENTIFIER_GENERATOR_PREFIX = "local_zl_identifier_generator_prefix";

    public static final String REMOTE_ZL_IDENTIFIER_SOURCE_URL_PROPERTY = "remote_zlidentifier_url";
    public static final String REMOTE_ZL_IDENTIFIER_SOURCE_USERNAME_PROPERTY = "remote_zlidentifier_username";
    public static final String REMOTE_ZL_IDENTIFIER_SOURCE_PASSWORD_PROPERTY = "remote_zlidentifier_password";
    private static final String ZL_DOSSIER_NUMBER_IDENTIFIER_SOURCE_UUID = "9dd9bdf3-4b57-47c3-b731-1000dbdef5d8";
    private static final String HIVEMR_V1_IDENTIFIER_SOURCE_UUID = "f36ec8b2-70ca-11eb-8aa6-0242ac110002";
    private static final String HIVEMR_V1_AUTOGENERATION_OPTION_UUID = "570fe8a7-70cd-11eb-8aa6-0242ac110002";

    private final IdentifierSourceService identifierSourceService;

	public ConfigureHaitiIdGenerators(
            IdentifierSourceService identifierSourceService) {

		this.identifierSourceService = identifierSourceService;

		if (identifierSourceService == null) {
			throw new IllegalStateException("All the dependencies are mandatory");
		}

	}

    // TODO refactor all this to make more sense
    public static void createPatientIdGenerator(ConfigureHaitiIdGenerators configureHaitiIdGenerators) {
        PatientIdentifierType zlIdentifierType = getZlIdentifierType();
        IdentifierSource zlIdentifierGenerator = configureHaitiIdGenerators.zlIdentifierGenerator(zlIdentifierType);
        IdentifierPool localZlIdentifierPool = configureHaitiIdGenerators.localZlIdentifierSource(zlIdentifierGenerator);
        configureHaitiIdGenerators.setAutoGenerationOptionsForZlIdentifier(localZlIdentifierPool);
    }

    public static void createDossierNumberGenerator(LocationService locationService, ConfigureHaitiIdGenerators configureHaitiIdGenerators, Config config) {

        PatientIdentifierType dossierIdentifierType = getDossierIdentifierType();

        // special, legacy case for Mirebalais
        if (config.getSite().equalsIgnoreCase("MIREBALAIS")) {

            SequentialIdentifierGenerator sequentialIdentifierGeneratorForUHM = configureHaitiIdGenerators
                    .sequentialIdentifierGeneratorForDossier(dossierIdentifierType,
                            PihCoreConstants.UHM_DOSSIER_NUMBER_PREFIX,
                            PihCoreConstants.UHM_DOSSIER_NUMBER_IDENTIFIER_SOURCE_UUID);

            configureHaitiIdGenerators.setAutoGenerationOptionsForDossierNumberGenerator(sequentialIdentifierGeneratorForUHM,
                    locationService.getLocationByUuid(MirebalaisLocations.MIREBALAIS_HOSPITAL.uuid()));

            SequentialIdentifierGenerator sequentialIdentifierGeneratorForCDI = configureHaitiIdGenerators
                    .sequentialIdentifierGeneratorForDossier(dossierIdentifierType,
                            PihCoreConstants.CDI_DOSSIER_NUMBER_PREFIX,
                            PihCoreConstants.CDI_DOSSIER_NUMBER_IDENTIFIER_SOURCE_UUID);

            configureHaitiIdGenerators.setAutoGenerationOptionsForDossierNumberGenerator(sequentialIdentifierGeneratorForCDI,
                    locationService.getLocationByUuid(MirebalaisLocations.CDI_KLINIK_EKSTEN_JENERAL.uuid()));

        }
        else if (config.getDossierIdentifierPrefix() != null) {

            SequentialIdentifierGenerator sequentialIdentifierGenerator = configureHaitiIdGenerators
                    .sequentialIdentifierGeneratorForDossier(dossierIdentifierType,
                            config.getDossierIdentifierPrefix().toString(),
                            ZL_DOSSIER_NUMBER_IDENTIFIER_SOURCE_UUID);

            configureHaitiIdGenerators.setAutoGenerationOptionsForDossierNumberGenerator(sequentialIdentifierGenerator, null);

        }
    }

    /**
     * This is a bit of a hack to ensure that the HIV EMR v1 ID is displayed, but is not editable from the patient header
     * Non-editable or not is determined based on the presence of an autogeneration option with manual entry disabled
     */
    public static void createSourceAndAutoGenerationOptionForHivEmrV1(IdentifierSourceService iss) {
        RemoteIdentifierSource source = (RemoteIdentifierSource) iss
                .getIdentifierSourceByUuid(HIVEMR_V1_IDENTIFIER_SOURCE_UUID);
        if (source == null) {
            source = new RemoteIdentifierSource();
            source.setUrl(HIVEMR_V1_IDENTIFIER_SOURCE_UUID);
            source.setName("Dummy Source for HIVEMR-V1");
            source.setUrl("NO URL");
            source.setIdentifierType(getHivEmrV1IdentifierType());
            iss.saveIdentifierSource(source);
        }
        AutoGenerationOption autoGen = iss.getAutoGenerationOptionByUuid(HIVEMR_V1_AUTOGENERATION_OPTION_UUID);
        if (autoGen == null) {
            autoGen = new AutoGenerationOption();
            autoGen.setUuid(HIVEMR_V1_AUTOGENERATION_OPTION_UUID);
            autoGen.setSource(source);
            autoGen.setIdentifierType(source.getIdentifierType());
            autoGen.setAutomaticGenerationEnabled(false);
            autoGen.setManualEntryEnabled(false);
            iss.saveAutoGenerationOption(autoGen);
        }
    }

    public void setAutoGenerationOptionsForDossierNumberGenerator(IdentifierSource identifierSource, Location location) {

        AutoGenerationOption autoGenerationOption = identifierSourceService.getAutoGenerationOption(identifierSource
                .getIdentifierType(), location);

        if (autoGenerationOption == null) {
            autoGenerationOption = new AutoGenerationOption();
        }

        autoGenerationOption.setIdentifierType(identifierSource.getIdentifierType());
        autoGenerationOption.setSource(identifierSource);
        autoGenerationOption.setLocation(location);
        autoGenerationOption.setManualEntryEnabled(true);
        autoGenerationOption.setAutomaticGenerationEnabled(true);

        identifierSourceService.saveAutoGenerationOption(autoGenerationOption);

    }

    public void setAutoGenerationOptionsForZlIdentifier(IdentifierSource identifierSource) {

        AutoGenerationOption autoGenerationOption = identifierSourceService.getAutoGenerationOption(identifierSource
                .getIdentifierType());

        if (autoGenerationOption == null) {
            autoGenerationOption = new AutoGenerationOption();
        }

        autoGenerationOption.setIdentifierType(identifierSource.getIdentifierType());
        autoGenerationOption.setSource(identifierSource);
        autoGenerationOption.setManualEntryEnabled(false);
        autoGenerationOption.setAutomaticGenerationEnabled(true);

        identifierSourceService.saveAutoGenerationOption(autoGenerationOption);
    }

	public IdentifierPool localZlIdentifierSource(IdentifierSource zlIdentifierGenerator) {
		IdentifierPool localZlIdentifierPool;
		try {
			localZlIdentifierPool = getLocalZlIdentifierPool();
		}
		catch (IllegalStateException ex) {
            localZlIdentifierPool = new IdentifierPool();
            localZlIdentifierPool.setName("Local Pool of ZL Identifiers");
            localZlIdentifierPool.setUuid(PihCoreConstants.LOCAL_ZL_IDENTIFIER_POOL_UUID);
            localZlIdentifierPool.setSource(zlIdentifierGenerator);
            localZlIdentifierPool.setIdentifierType(zlIdentifierGenerator.getIdentifierType());
            localZlIdentifierPool.setMinPoolSize(PihCoreConstants.LOCAL_ZL_IDENTIFIER_POOL_MIN_POOL_SIZE);
            localZlIdentifierPool.setBatchSize(PihCoreConstants.LOCAL_ZL_IDENTIFIER_POOL_BATCH_SIZE);
            localZlIdentifierPool.setSequential(false);
			identifierSourceService.saveIdentifierSource(localZlIdentifierPool);
		}
		return localZlIdentifierPool;
	}

    public SequentialIdentifierGenerator localZlIdentifierGenerator(PatientIdentifierType zlPatientIdentifierType) {
        SequentialIdentifierGenerator localZlIdentifierGenerator;
        try {
            localZlIdentifierGenerator = getLocalZlIdentifierGenerator();
        }
        catch (IllegalStateException ex) {
            localZlIdentifierGenerator = new SequentialIdentifierGenerator();
            localZlIdentifierGenerator.setName("Local ZL Identifier Generator");
            localZlIdentifierGenerator.setUuid(PihCoreConstants.LOCAL_ZL_IDENTIFIER_GENERATOR_UUID);
            localZlIdentifierGenerator.setIdentifierType(zlPatientIdentifierType);
            localZlIdentifierGenerator.setBaseCharacterSet("ACDEFGHJKLMNPRTUVWXY1234567890");
            localZlIdentifierGenerator.setMaxLength(6);
            localZlIdentifierGenerator.setMinLength(6);
        }
        String prefix = getLocalZlIdentifierGeneratorPrefix();
        int firstIdentifierBase = (int)Math.pow(10, (4-prefix.length()));
        localZlIdentifierGenerator.setPrefix(prefix);
        localZlIdentifierGenerator.setFirstIdentifierBase(Integer.toString(firstIdentifierBase));

        identifierSourceService.saveIdentifierSource(localZlIdentifierGenerator);
        return localZlIdentifierGenerator;
    }

	public IdentifierSource zlIdentifierGenerator(PatientIdentifierType zlPatientIdentifierType) {
        if (getLocalZlIdentifierGeneratorEnabled()) {
            return localZlIdentifierGenerator(zlPatientIdentifierType);
        }

        RemoteIdentifierSource remoteZlIdentifierSource;
		try {
			remoteZlIdentifierSource = getRemoteZlIdentifierSource();
		}
		catch (IllegalStateException ex) {
            remoteZlIdentifierSource = new RemoteIdentifierSource();
            remoteZlIdentifierSource.setName("Remote Source for ZL Identifiers");
            remoteZlIdentifierSource.setUuid(PihCoreConstants.REMOTE_ZL_IDENTIFIER_SOURCE_UUID);
            remoteZlIdentifierSource.setIdentifierType(zlPatientIdentifierType);
		}
        remoteZlIdentifierSource.setUrl(getRemoteZlIdentifierSourceUrl());
        remoteZlIdentifierSource.setUser(getRemoteZlIdentifierSourceUsername());
        remoteZlIdentifierSource.setPassword(getRemoteZlIdentifierSourcePassword());

		identifierSourceService.saveIdentifierSource(remoteZlIdentifierSource);
		return remoteZlIdentifierSource;
	}

	public SequentialIdentifierGenerator sequentialIdentifierGeneratorForDossier(PatientIdentifierType patientIdentifierType, String prefix, String identifierSourceUuid) {
		SequentialIdentifierGenerator dossierSequenceGenerator;
		try {
			dossierSequenceGenerator = getDossierSequenceGenerator(identifierSourceUuid);
		}
		catch (IllegalStateException e) {
            dossierSequenceGenerator = new SequentialIdentifierGenerator();
            dossierSequenceGenerator.setName("Sequential Generator for Dossier");
            dossierSequenceGenerator.setUuid(identifierSourceUuid);
            dossierSequenceGenerator.setMaxLength(6 + prefix.length());
            dossierSequenceGenerator.setMinLength(6 + prefix.length());
            dossierSequenceGenerator.setPrefix(prefix);
            dossierSequenceGenerator.setBaseCharacterSet("0123456789");
            dossierSequenceGenerator.setFirstIdentifierBase("000001");
            dossierSequenceGenerator.setIdentifierType(patientIdentifierType);
			identifierSourceService.saveIdentifierSource(dossierSequenceGenerator);
		}

		return dossierSequenceGenerator;
	}

    /**
     * @return the prefix which should be used by the local zl identifier generator, if it is enabled.
     * The system will always create zl identifiers of length 6, one of which is a check-digit.  So
     * if this prefix is empty, the first identifier base will be 10000, if the prefix is a single character,
     * the first identifier base will be 1000, if the prefix is two characters, the first identifier base will be 100, etc
     */
    public String getLocalZlIdentifierGeneratorPrefix() {
        String property = Context.getRuntimeProperties().getProperty(LOCAL_ZL_IDENTIFIER_GENERATOR_PREFIX);
        return property != null ? property : "";
    }

    /**
     * @return whether or not the system should create a local identifier generator for the ZL EMR ID
     * This is needed primarily in development environments so that the remote source can connect to it, rather than to a server in the cloud
     */
    public boolean getLocalZlIdentifierGeneratorEnabled() {
        String property = Context.getRuntimeProperties().getProperty(LOCAL_ZL_IDENTIFIER_GENERATOR_ENABLED);
        return Boolean.parseBoolean(property);
    }

    /**
     * @return the url of the remote zl identifier source.
     * If this is left empty, and the local zl identifier generator is enabled, it will default to connecting to localhost with that identifier source
     */
    public String getRemoteZlIdentifierSourceUrl() {
        String property = Context.getRuntimeProperties().getProperty(REMOTE_ZL_IDENTIFIER_SOURCE_URL_PROPERTY);
        return property != null ? property : PihCoreConstants.REMOTE_ZL_IDENTIFIER_SOURCE_URL;
    }

    /**
     * @return the username with which to connect to the remote server
     */
    public String getRemoteZlIdentifierSourceUsername() {
        String property = Context.getRuntimeProperties().getProperty(REMOTE_ZL_IDENTIFIER_SOURCE_USERNAME_PROPERTY);
        return property != null ? property : PihCoreConstants.REMOTE_ZL_IDENTIFIER_SOURCE_USERNAME;
    }

    /**
     * @return the password with which to connect to the remote server
     */
    public String getRemoteZlIdentifierSourcePassword() {
        String property = Context.getRuntimeProperties().getProperty(REMOTE_ZL_IDENTIFIER_SOURCE_PASSWORD_PROPERTY);
        return property != null ? property : PihCoreConstants.REMOTE_ZL_IDENTIFIER_SOURCE_PASSWORD;
    }

    private <T extends IdentifierSource> T getIdentifierSource(String uuid, Class<T> sourceType) {
        IdentifierSourceService iss = Context.getService(IdentifierSourceService.class);
        IdentifierSource source = iss.getIdentifierSourceByUuid(uuid);
        if (source == null) {
            throw new IllegalStateException(sourceType.getSimpleName() + " has not been configured");
        }
        return (T) source;
    }

    public IdentifierPool getLocalZlIdentifierPool() {
        return getIdentifierSource(PihCoreConstants.LOCAL_ZL_IDENTIFIER_POOL_UUID, IdentifierPool.class);
    }

    public SequentialIdentifierGenerator getLocalZlIdentifierGenerator() {
        return getIdentifierSource(PihCoreConstants.LOCAL_ZL_IDENTIFIER_GENERATOR_UUID, SequentialIdentifierGenerator.class);
    }

    public RemoteIdentifierSource getRemoteZlIdentifierSource() {
        return getIdentifierSource(PihCoreConstants.REMOTE_ZL_IDENTIFIER_SOURCE_UUID, RemoteIdentifierSource.class);
    }

    public SequentialIdentifierGenerator getDossierSequenceGenerator(String identifierSourceUuid) {

        SequentialIdentifierGenerator sequentialIdentifierGenerator = (SequentialIdentifierGenerator) Context.getService(
                IdentifierSourceService.class).getIdentifierSourceByUuid(
                identifierSourceUuid);

        if (sequentialIdentifierGenerator == null) {
            throw new IllegalStateException("Sequential Identifier Generator for Dossier has not been configured");
        }

        return sequentialIdentifierGenerator;
    }

    public static PatientIdentifierType getZlIdentifierType() {
        return MetadataUtils.existing(PatientIdentifierType.class, PihHaitiPatientIdentifierTypes.ZL_EMR_ID.uuid());
    }

    public static PatientIdentifierType getDossierIdentifierType() {
        return MetadataUtils.existing(PatientIdentifierType.class, PihHaitiPatientIdentifierTypes.DOSSIER_NUMBER.uuid());
    }

    public static PatientIdentifierType getHivEmrV1IdentifierType() {
        return MetadataUtils.existing(PatientIdentifierType.class, PihHaitiPatientIdentifierTypes.HIVEMR_V1.uuid());
    }
}
