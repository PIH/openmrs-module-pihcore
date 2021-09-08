package org.openmrs.module.pihcore.setup;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.api.LocationService;
import org.openmrs.module.pihcore.PihCoreContextSensitiveTest;
import org.openmrs.module.pihcore.config.Config;
import org.openmrs.test.SkipBaseSetup;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
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

    @Before
    public void setUp() throws Exception {
        initializeInMemoryDatabase();
        executeDataSet("requiredDataTestDataset.xml");
        authenticate();
        configFiles = new ArrayList<>();
        configFiles.add(addResourceToConfigurationDirectory("locations", "locations.csv"));
        configFiles.add(addResourceToConfigurationDirectory("locations", "locations-hsn.csv"));
        configFiles.add(addResourceToConfigurationDirectory("locations", "locations-mirebalais.csv"));
        setupInitializerForTesting();
        config = mock(Config.class);
    }

    @After
    public void tearDown() {
        cleanUpConfigurationDirectory(configFiles);
    }

    @Test
    public void testLocationsSetupforMirebalais() {
        assertThat(locationService.getAllLocations().size(), is(2));
        when(config.getSite()).thenReturn("MIREBALAIS");
        InitializerSetup.loadPreConceptDomains(config);
        assertThat(locationService.getAllLocations().size(), is(70)); // One is an edit from requiredDataTestDataset
    }

    @Test
    public void testLocationsSetupforHsn() {
        assertThat(locationService.getAllLocations().size(), is(2));
        when(config.getSite()).thenReturn("HSN");
        InitializerSetup.loadPreConceptDomains(config);
        assertThat(locationService.getAllLocations().size(), is(26));
    }

    @Test
    public void testLocationsSetupforCrossSite() {
        assertThat(locationService.getAllLocations().size(), is(2));
        when(config.getSite()).thenReturn("CROSS_SITE");
        InitializerSetup.loadPreConceptDomains(config);
        assertThat(locationService.getAllLocations().size(), is(23));
    }
}
