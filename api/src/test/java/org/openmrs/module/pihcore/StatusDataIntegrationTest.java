package org.openmrs.module.pihcore;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.api.context.Daemon;
import org.openmrs.api.db.hibernate.DbSessionFactory;
import org.openmrs.module.DaemonToken;
import org.openmrs.module.ModuleFactory;
import org.openmrs.module.pihcore.status.StatusData;
import org.openmrs.module.pihcore.status.StatusDataDefinition;
import org.openmrs.module.pihcore.status.StatusDataEvaluator;
import org.openmrs.test.SkipBaseSetup;
import org.openmrs.test.jupiter.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.FileInputStream;
import java.lang.reflect.Field;
import java.util.Map;
import java.util.Properties;

/**
 * This is an integration test that connects to an existing database for testing
 *
 * To execute this against an appropriate database, ensure you have a properties file with the following settings:
 * -  connection.url, connection.username, connection.password properties that point to a valid DB connection
 * -  junit.username and junit.password properties that point to a valid OpenMRS account
 *
 * Configure a system property named "integration_test_properties_file" with the location of this properties file.
 * If you are running this test from the command line, that can be done with a -D argument like this:
 *
 * mvn test -Dtest=StatusDataIntegrationTest -Dintegration_test_properties_file=/tmp/humci.properties
 */
@SkipBaseSetup
@Disabled
public class StatusDataIntegrationTest extends BaseModuleContextSensitiveTest {

    static Properties props = null;
    static {
        String propFile = System.getProperty("integration_test_properties_file");
        if (propFile != null) {
            props = new Properties();
            try {
                props.load(new FileInputStream(propFile));
            } catch (Exception e) {
                System.out.println("Error loading properties from " + propFile + ": " + e.getMessage());
            }
        }

        if (props != null && !props.isEmpty()) {
            System.setProperty("databaseUrl", props.getProperty("connection.url"));
            System.setProperty("databaseUsername", props.getProperty("connection.username"));
            System.setProperty("databasePassword", props.getProperty("connection.password"));
            System.setProperty("databaseDriver", props.getProperty("connection.driver_class"));
            System.setProperty("databaseDialect", "org.hibernate.dialect.MySQLDialect");
            System.setProperty("useInMemoryDatabase", "false");
        }
    }

    @Autowired
    DbSessionFactory sessionFactory;

    @Autowired
    StatusDataEvaluator evaluator;

    DaemonToken daemonToken;

    @Override
    public Properties getRuntimeProperties() {
        Properties p = new Properties(props);
        p.putAll(super.getRuntimeProperties());
        return p;
    }

    @BeforeEach
    public void setupDaemonToken() {
        Map<String, DaemonToken> daemonTokens;
        try {
            Field field = ModuleFactory.class.getDeclaredField("daemonTokens");
            field.setAccessible(true);
            daemonTokens = (Map<String, DaemonToken>) field.get(null);
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
        daemonToken = new DaemonToken("pihcore");
        daemonTokens.put(daemonToken.getId(), daemonToken);
    }

    @Test
    public void performTest() throws Exception {

        // Only run this test if it is being run alone.  This ensures this test will not run in normal build.
        if (props == null) {
            return;
        }

        if (!Context.isSessionOpen()) {
            Context.openSession();
        }
        authenticate();

        StatusDataDefinition def5Secs = new StatusDataDefinition();
        def5Secs.setLabelCode("numConcepts");
        def5Secs.setStatusDataQuery("select sleep(5), count(*) as num from concept;");
        def5Secs.setValueExpression("$num");

        StatusDataDefinition def10Secs = new StatusDataDefinition();
        def10Secs.setLabelCode("numConcepts");
        def10Secs.setStatusDataQuery("select sleep(10), count(*) as num from concept;");
        def10Secs.setValueExpression("$num");

        Thread thread5Secs = runInThread(def5Secs);
        Thread thread10Secs = runInThread(def10Secs);
        thread5Secs.join();
        thread10Secs.join();
    }

    protected Thread runInThread(final StatusDataDefinition definition) {
        return Daemon.runInDaemonThread(() -> {
            StatusData statusData = evaluator.evaluate(new Patient(1), definition);
            System.out.println(statusData.getLabel() + ": " + statusData.getDisplayValue());
        }, daemonToken);
    }
}
