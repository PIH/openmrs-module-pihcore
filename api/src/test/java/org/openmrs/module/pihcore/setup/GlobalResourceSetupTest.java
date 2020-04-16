package org.openmrs.module.pihcore.setup;

import org.junit.Before;
import org.junit.Test;
import org.openmrs.api.context.Context;
import org.openmrs.module.pihcore.PihCoreUtil;
import org.openmrs.util.OpenmrsConstants;

import java.io.File;
import java.util.List;
import java.util.Properties;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.Assert.assertThat;

public class GlobalResourceSetupTest  {

    public static final String appDataTestDir = "testAppDataDir";

    private String path;

    @Before
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
        List<String> cssFiles = GlobalResourceSetup.getGlobalResourcesFromFileSystemPath(PihCoreUtil.getGlobalStylesDirectory());
        assertThat(cssFiles, containsInAnyOrder( "configuration/pih/styles/global/global.css", "configuration/pih/styles/global/anotherGlobal.css"));
    }

    @Test
    public void shouldFindAllGlobalScriptsFiles() {
        List<String> cssFiles = GlobalResourceSetup.getGlobalResourcesFromFileSystemPath(PihCoreUtil.getGlobalScriptsDirectory());
        assertThat(cssFiles, containsInAnyOrder("configuration/pih/scripts/global/global.js", "configuration/pih/scripts/global/anotherGlobal.js"));
    }

}
