package org.openmrs.module.pihcore;

import org.openmrs.test.SkipBaseSetup;
import org.openmrs.test.jupiter.BaseModuleContextSensitiveTest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

@SkipBaseSetup
public abstract class PihCoreSdkTest extends BaseModuleContextSensitiveTest {

    Properties p = null;
    private static final Logger log = LoggerFactory.getLogger(PihCoreSdkTest.class);
    String serverId = null;

    public PihCoreSdkTest() {
        super();
        p = getRuntimeProperties();
    }

    @Override
    public Properties getRuntimeProperties() {
        Properties props = new Properties();
        serverId = System.getProperty("serverId");
        if (serverId != null) {
            log.debug("Executing test using sdk: " + serverId);
            String path = System.getProperty("user.home") + File.separator + "openmrs" + File.separator + serverId + File.separator + "openmrs-runtime.properties";
            try (FileInputStream fis = new FileInputStream(path)) {
                props.load(fis);
            } catch (Exception e) {
                throw new RuntimeException("Error loading properties from " + path, e);
            }
            if (!props.isEmpty()) {
                System.setProperty("databaseUrl", props.getProperty("connection.url"));
                System.setProperty("databaseUsername", props.getProperty("connection.username"));
                System.setProperty("databasePassword", props.getProperty("connection.password"));
                System.setProperty("databaseDriver", props.getProperty("connection.driver_class"));
                System.setProperty("databaseDialect", "org.hibernate.dialect.MySQLDialect");
                System.setProperty("useInMemoryDatabase", "false");
            }
        }
        else {
            log.info("No sdk specified in system properties: " + serverId);
        }
        props = super.getRuntimeProperties();
        props.setProperty("junit.username", "admin");
        props.setProperty("junit.password", "Admin123");
        return props;
    }
}
