package org.openmrs.module.pihcore.setup;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openmrs.api.context.Context;
import org.openmrs.ui.framework.resource.ConfigurationResourceProvider;
import org.openmrs.util.OpenmrsConstants;

import java.io.File;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

public class GlobalResourceSetupTest  {

    public static final String appDataTestDir = "testAppDataDir";

    private String path;

    @BeforeEach
    public void setup() {
        // configure app data dir path
        path = getClass().getClassLoader().getResource(appDataTestDir).getPath() + File.separator;
        System.setProperty("OPENMRS_APPLICATION_DATA_DIRECTORY", path);
        Properties prop = new Properties();
        prop.setProperty(OpenmrsConstants.APPLICATION_DATA_DIRECTORY_RUNTIME_PROPERTY, path);
        Context.setRuntimeProperties(prop);
    }

    @Test
    public void shouldFindAllGlobalStylesFiles() {
        ConfigurationResourceProvider provider = new ConfigurationResourceProvider();
        assertNotNull(provider.getResource("configuration/pih/styles/global/global.css"));
        assertNotNull(provider.getResource("configuration/pih/styles/global/anotherGlobal.css"));
        assertNull(provider.getResource("configuration/pih/styles/global/missingGlobal.css"));
    }

    @Test
    public void shouldFindAllGlobalScriptsFiles() {
        ConfigurationResourceProvider provider = new ConfigurationResourceProvider();
        assertNotNull(provider.getResource("configuration/pih/scripts/global/global.js"));
        assertNotNull(provider.getResource("configuration/pih/scripts/global/anotherGlobal.js"));
        assertNull(provider.getResource("configuration/pih/scripts/global/missingGlobal.js"));
    }

}
