package org.openmrs.module.pihcore;

import org.junit.jupiter.api.Test;
import org.openmrs.api.context.Context;
import org.openmrs.module.pihcore.service.PihCoreService;
import org.openmrs.test.jupiter.BaseModuleContextSensitiveTest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;

import static org.testcontainers.shaded.org.hamcrest.MatcherAssert.assertThat;
import static org.testcontainers.shaded.org.hamcrest.Matchers.notNullValue;

public class ContextLoadingSmokeTest extends BaseModuleContextSensitiveTest {

    private static final Logger log = LoggerFactory.getLogger(PihCoreSdkTest.class);

    @Override
    public void authenticate() {
        log.warn("Authenticating...");
        super.authenticate();
    }

    @Override
    public void initializeInMemoryDatabase() throws SQLException {
        log.warn("Initializing In Memory Database...");
        super.initializeInMemoryDatabase();
    }

    @Override
    public void executeDataSet(String datasetFilename) {
        log.warn("Executing DataSet " + datasetFilename + "...");
        super.executeDataSet(datasetFilename);
    }

    @Override
    public void executeLargeDataSet(String datasetFilename) throws Exception {
        log.warn("Executing Large DataSet " + datasetFilename + "...");
        super.executeLargeDataSet(datasetFilename);
    }

    @Override
    public void executeXmlDataSet(String datasetFilename) throws Exception {
        log.warn("Executing Xml DataSet " + datasetFilename + "...");
        super.executeXmlDataSet(datasetFilename);
    }

    @Override
    public synchronized void deleteAllData() {
        log.warn("Deleting all data...");
        super.deleteAllData();
    }

    @Override
    public void clearHibernateCache() {
        log.warn("Clearing Hibernate Cache...");
        super.clearHibernateCache();
    }

    @Override
    public void baseSetupWithStandardDataAndAuthentication() throws SQLException {
        log.warn("Executing baseSetupWithStandardDataAndAuthentication...");
        super.baseSetupWithStandardDataAndAuthentication();
    }

    @Override
    public void updateSearchIndex() {
        log.warn("Updating search index...");
        super.updateSearchIndex();
    }

    @Override
    public void clearSessionAfterEachTest() {
        log.warn("Clearing Session...");
        super.clearSessionAfterEachTest();
    }

    @Test
    public void executeTest() {
        PihCoreService pihCoreService = Context.getService(PihCoreService.class);
        assertThat(pihCoreService, notNullValue());
    }

}
