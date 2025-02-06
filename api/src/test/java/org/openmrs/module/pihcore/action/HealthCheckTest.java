package org.openmrs.module.pihcore.action;

import org.hibernate.cfg.Environment;
import org.junit.jupiter.api.Test;
import org.openmrs.test.jupiter.BaseModuleContextSensitiveTest;

import java.util.Properties;

public class HealthCheckTest extends BaseModuleContextSensitiveTest {

    @Override
    public Properties getRuntimeProperties() {
        runtimeProperties = super.getRuntimeProperties();
        String url = "jdbc:h2:mem:openmrs;DB_CLOSE_DELAY=-1;LOCK_TIMEOUT=10000;IGNORECASE=TRUE;DATABASE_TO_LOWER=TRUE;CASE_INSENSITIVE_IDENTIFIERS=TRUE;TRACE_LEVEL_FILE=3;TRACE_LEVEL_SYSTEM_OUT=3";
        runtimeProperties.setProperty(Environment.URL, url);
        runtimeProperties.setProperty("connection.url", url);
        return runtimeProperties;
    }

    @Test
    public void reopenVisitAction_shouldReopenVisitIfNoSubsequentVisitAtSameLocation() {
        System.out.println("EXECUTING TEST");
    }
}
