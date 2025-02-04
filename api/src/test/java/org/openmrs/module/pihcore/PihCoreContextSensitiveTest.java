package org.openmrs.module.pihcore;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.dbunit.DatabaseUnitException;
import org.dbunit.DatabaseUnitRuntimeException;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.DefaultDataSet;
import org.dbunit.dataset.DefaultTable;
import org.dbunit.operation.DatabaseOperation;
import org.junit.jupiter.api.BeforeEach;
import org.openmrs.EncounterType;
import org.openmrs.api.context.Context;
import org.openmrs.messagesource.MessageSourceService;
import org.openmrs.module.Module;
import org.openmrs.module.ModuleFactory;
import org.openmrs.module.initializer.Domain;
import org.openmrs.module.initializer.InitializerConstants;
import org.openmrs.module.initializer.InitializerMessageSource;
import org.openmrs.module.initializer.api.ConfigDirUtil;
import org.openmrs.module.initializer.api.InitializerService;
import org.openmrs.module.pihcore.metadata.Metadata;
import org.openmrs.test.jupiter.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Properties;

import static org.openmrs.module.initializer.api.ConfigDirUtil.CHECKSUM_FILE_EXT;

public abstract class PihCoreContextSensitiveTest extends BaseModuleContextSensitiveTest {

    @Autowired
    MessageSourceService messageSourceService;

    @Autowired
    protected InitializerMessageSource initializerMessageSource;

    private boolean skipBaseSetup = false;

    private static boolean isBaseSetup;

    public PihCoreContextSensitiveTest() {
        super();
        {
            Module mod = new Module("");
            mod.setModuleId("metadatamapping");
            mod.setVersion("1.3.4");
            mod.setFile(new File(""));
            ModuleFactory.getStartedModulesMap().put(mod.getModuleId(), mod);
        }
    }

    @Override
    public void skipBaseSetup() throws Exception {
        super.skipBaseSetup();
        this.skipBaseSetup = true;
    }

    private String getSchemaPattern() {
        return this.useInMemoryDatabase() ? "PUBLIC" : "public";
    }

    public synchronized void deleteAllData() {
        try {
            Context.clearSession();

            Connection connection = getConnection();

            turnOffDBConstraints(connection);

            IDatabaseConnection dbUnitConn = setupDatabaseConnection(connection);

            String databaseName = System.getProperty("databaseName");

            // find all the tables for this connection
            ResultSet resultSet = connection.getMetaData().getTables(databaseName, getSchemaPattern(), "%", new String[] {"TABLE"});
            DefaultDataSet dataset = new DefaultDataSet();
            while (resultSet.next()) {
                String tableName = resultSet.getString(3);
                dataset.addTable(new DefaultTable(tableName));
            }

            // do the actual deleting/truncating
            DatabaseOperation.DELETE_ALL.execute(dbUnitConn, dataset);

            turnOnDBConstraints(connection);

            connection.commit();

            updateSearchIndex();

            isBaseSetup = false;
        }
        catch (SQLException | DatabaseUnitException e) {
            throw new DatabaseUnitRuntimeException(e);
        }
    }

    @BeforeEach
    @Override
    public void baseSetupWithStandardDataAndAuthentication() throws SQLException {
        // Open a session if needed
        if (!Context.isSessionOpen()) {
            Context.openSession();
        }
        if (!skipBaseSetup) {
            if (!isBaseSetup) {

                deleteAllData();

                if (useInMemoryDatabase()) {
                    initializeInMemoryDatabase();
                }
                else {
                    executeDataSet(INITIAL_XML_DATASET_PACKAGE_PATH);
                }

                executeDataSet(EXAMPLE_XML_DATASET_PACKAGE_PATH);

                //Commit so that it is not rolled back after a test.
                getConnection().commit();

                updateSearchIndex();

                isBaseSetup = true;
            }

            authenticate();
        } else {
            if (isBaseSetup) {
                deleteAllData();
            }
        }

        Context.clearSession();
        setupInitializerForTesting();
    }

    public void loadFromInitializer(Domain domain, String file) {
        InitializerService initializerService = Context.getService(InitializerService.class);
        List<File> configFiles = new ArrayList<>();
        try {
            configFiles.add(addResourceToConfigurationDirectory(domain.getName(), file));
            initializerService.loadUnsafe(true, true);
        }
        catch (Exception e) {
            throw new RuntimeException("An error occurred while setting up initializer configuration", e);
        }
        finally {
            cleanUpConfigurationDirectory(configFiles);
        }
    }

    public String getPihConfig() {
        return "mirebalais";
    }

    public void setupInitializerForTesting() {
        Properties prop = getRuntimeProperties();
        prop.setProperty("pih.config", getPihConfig());
        prop.setProperty(InitializerConstants.PROPS_SKIPCHECKSUMS, "true");
        prop.setProperty(InitializerConstants.PROPS_STARTUP_LOAD, InitializerConstants.PROPS_STARTUP_LOAD_FAIL_ON_ERROR);
        Context.setRuntimeProperties(prop);
        messageSourceService.setActiveMessageSource(initializerMessageSource);
        if (initializerMessageSource.getPresentations().isEmpty()) {
            initializerMessageSource.initialize();
        }
        if (!initializerMessageSource.getFallbackLanguages().containsKey("ht")) {
            initializerMessageSource.addFallbackLanguage("ht", "fr");
        }
        Locale.setDefault(Locale.ENGLISH);
    }

    public File addResourceToConfigurationDirectory(String domain, String resource) {
        InitializerService initializerService = Context.getService(InitializerService.class);
        File outputFile = Paths.get(initializerService.getConfigDirPath(), domain, resource).toFile();
        try {
            String inputResource = "testAppDataDir/configuration/" + domain + "/" + resource;
            InputStream in = getClass().getClassLoader().getResourceAsStream(inputResource);
            String contents = IOUtils.toString(in, "UTF-8");
            FileUtils.write(outputFile, contents, "UTF-8");
        }
        catch (Exception e) {
            throw new RuntimeException("An error occurred while copying initializer resource to configuration", e);
        }
        return outputFile;
    }

    public void cleanUpConfigurationDirectory(List<File> filesToDelete) {
        InitializerService initializerService = Context.getService(InitializerService.class);
        ConfigDirUtil.deleteFilesByExtension(initializerService.getChecksumsDirPath(), CHECKSUM_FILE_EXT);
        for (File f : filesToDelete) {
            FileUtils.deleteQuietly(f);
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
