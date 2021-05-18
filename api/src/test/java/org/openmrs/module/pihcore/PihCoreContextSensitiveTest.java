package org.openmrs.module.pihcore;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.openmrs.EncounterType;
import org.openmrs.api.context.Context;
import org.openmrs.module.initializer.Domain;
import org.openmrs.module.initializer.InitializerConstants;
import org.openmrs.module.initializer.api.ConfigDirUtil;
import org.openmrs.module.initializer.api.InitializerService;
import org.openmrs.module.pihcore.metadata.Metadata;
import org.openmrs.test.BaseModuleContextSensitiveTest;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Paths;
import java.util.Properties;

import static org.openmrs.module.initializer.api.ConfigDirUtil.CHECKSUM_FILE_EXT;

public abstract class PihCoreContextSensitiveTest extends BaseModuleContextSensitiveTest {

    public void loadFromInitializer(Domain domain, String file) {
        InitializerService initializerService = Context.getService(InitializerService.class);
        File outputFile = Paths.get(initializerService.getConfigDirPath(), domain.getName(), file).toFile();
        try {
            Properties prop = new Properties();
            prop.setProperty(InitializerConstants.PROPS_SKIPCHECKSUMS, "true");
            prop.setProperty(InitializerConstants.PROPS_STARTUP_LOAD, InitializerConstants.PROPS_STARTUP_LOAD_FAIL_ON_ERROR);
            Context.setRuntimeProperties(prop);

            String inputResource = "testAppDataDir/configuration/" + domain.getName() + "/" + file;
            InputStream in = getClass().getClassLoader().getResourceAsStream(inputResource);
            String contents = IOUtils.toString(in, "UTF-8");
            FileUtils.write(outputFile, contents, "UTF-8");
            initializerService.loadUnsafe(true, true);
        }
        catch (Exception e) {
            throw new RuntimeException("An error occurred while setting up initializer configuration", e);
        }
        finally {
            ConfigDirUtil.deleteFilesByExtension(initializerService.getChecksumsDirPath(), CHECKSUM_FILE_EXT);
            FileUtils.deleteQuietly(outputFile);
        }
    }

    public EncounterType getRegistrationEncounterType() {
        return Metadata.lookupEncounterType(PihEmrConfigConstants.ENCOUNTERTYPE_PATIENT_REGISTRATION_UUID);
    }

    public EncounterType getCheckInEncounterType() {
        return Metadata.lookupEncounterType(PihEmrConfigConstants.ENCOUNTERTYPE_CHECK_IN_UUID);
    }

    public EncounterType getAdmissionEncounterType() {
        return Metadata.lookupEncounterType(PihEmrConfigConstants.ENCOUNTERTYPE_ADMISSION_UUID);
    }

    public EncounterType getTransferEncounterType() {
        return Metadata.lookupEncounterType(PihEmrConfigConstants.ENCOUNTERTYPE_TRANSFER_UUID);
    }

    public EncounterType getExitEncounterType() {
        return Metadata.lookupEncounterType(PihEmrConfigConstants.ENCOUNTERTYPE_EXIT_FROM_CARE_UUID);
    }

    public EncounterType getConsultationEncounterType() {
        return Metadata.lookupEncounterType(PihEmrConfigConstants.ENCOUNTERTYPE_CONSULTATION_UUID);
    }

    public EncounterType getPostOpNoteEncounterType() {
        return Metadata.lookupEncounterType(PihEmrConfigConstants.ENCOUNTERTYPE_POST_OPERATIVE_NOTE_UUID);
    }

    public EncounterType getVitalsEncounterType() {
        return Metadata.lookupEncounterType(PihEmrConfigConstants.ENCOUNTERTYPE_VITALS_UUID);
    }
}
