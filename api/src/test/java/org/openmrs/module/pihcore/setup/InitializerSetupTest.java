package org.openmrs.module.pihcore.setup;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openmrs.api.LocationService;
import org.openmrs.module.pihcore.PihCoreContextSensitiveTest;
import org.openmrs.module.pihcore.config.Config;
import org.openmrs.test.SkipBaseSetup;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * This tests logic for installing metadata with InitailizerSetup class
 */
@SkipBaseSetup
public class InitializerSetupTest extends PihCoreContextSensitiveTest {

    @Autowired
    LocationService locationService;

    Config config;
    List<File> configFiles;

    @BeforeEach
    public void setUp() throws Exception {
        initializeInMemoryDatabase();
        executeDataSet("requiredDataTestDataset.xml");
        authenticate();
        configFiles = new ArrayList<>();
        configFiles.add(addResourceToConfigurationDirectory("locations", "locations-base.csv"));
        configFiles.add(addResourceToConfigurationDirectory("locations", "locations-site-hsn.csv"));
        configFiles.add(addResourceToConfigurationDirectory("locations", "locations-site-mirebalais.csv"));
        config = mock(Config.class);
    }

    @AfterEach
    public void tearDown() {
        cleanUpConfigurationDirectory(configFiles);
    }

    @Test
    public void testLocationsSetupforMirebalais() {
        assertThat(locationService.getAllLocations().size(), is(2));
        when(config.getSite()).thenReturn("MIREBALAIS");
        InitializerSetup.install(config);
        assertThat(locationService.getAllLocations().size(), is(70)); // One is an edit from requiredDataTestDataset
    }

    @Test
    public void testLocationsSetupforHsn() {
        assertThat(locationService.getAllLocations().size(), is(2));
        when(config.getSite()).thenReturn("HSN");
        InitializerSetup.install(config);
        assertThat(locationService.getAllLocations().size(), is(26));
    }

    @Test
    public void testLocationsSetupforCrossSite() {
        assertThat(locationService.getAllLocations().size(), is(2));
        when(config.getSite()).thenReturn("CROSS_SITE");
        InitializerSetup.install(config);
        assertThat(locationService.getAllLocations().size(), is(23));
    }
}
