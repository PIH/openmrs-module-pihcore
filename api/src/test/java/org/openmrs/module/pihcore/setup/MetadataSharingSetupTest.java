package org.openmrs.module.pihcore.setup;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openmrs.api.context.Context;
import org.openmrs.util.OpenmrsConstants;

import java.io.File;
import java.util.Collection;
import java.util.Properties;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class MetadataSharingSetupTest {

    public static final String appDataTestDir = "testAppDataDir";

    @BeforeEach
    public void setup() {
        String path = getClass().getClassLoader().getResource(appDataTestDir).getPath() + File.separator;

        System.setProperty("OPENMRS_APPLICATION_DATA_DIRECTORY", path);
        Properties prop = new Properties();
        prop.setProperty(OpenmrsConstants.APPLICATION_DATA_DIRECTORY_RUNTIME_PROPERTY, path);
        Context.setRuntimeProperties(prop);
    }

    @Test
    public void shouldLoadMdsFiles() {
        Collection<File> files = MetadataSharingSetup.loadMdsFiles();
        assertThat(files.size(), is(3));
    }

}
