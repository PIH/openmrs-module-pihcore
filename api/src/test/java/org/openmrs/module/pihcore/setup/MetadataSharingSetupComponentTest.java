package org.openmrs.module.pihcore.setup;

import org.junit.Before;
import org.junit.Test;
import org.openmrs.api.context.Context;
import org.openmrs.module.metadatasharing.wrapper.PackageImporter;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.openmrs.util.OpenmrsConstants;

import java.io.File;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class MetadataSharingSetupComponentTest extends BaseModuleContextSensitiveTest  {

    public static final String appDataTestDir = "testAppDataDir";

    @Before
    public void setup() {
        String path = getClass().getClassLoader().getResource(appDataTestDir).getPath() + File.separator;
        System.setProperty("OPENMRS_APPLICATION_DATA_DIRECTORY", path);
        Properties prop = new Properties();
        prop.setProperty(OpenmrsConstants.APPLICATION_DATA_DIRECTORY_RUNTIME_PROPERTY, path);
        Context.setRuntimeProperties(prop);

        executeDataSet("metadataSharingSetupDataset.xml");
    }

    // TODO test null-safe, etc

    @Test
    public void shouldLoadPackageImporters() throws Exception {
        Collection<File> files = MetadataSharingSetup.loadMdsFiles();
        List<PackageImporter> importers = MetadataSharingSetup.loadPackageImporters(files);

        for (PackageImporter importer : importers) {
            if (importer.getPackage().getName().equals("PIH Allergies")) {
                assertThat(importer.getPackage().getGroupUuid(), is("436cc037-074f-4253-9aa7-900c32f51ee9"));
            }
            else if (importer.getPackage().getName().equals("HUM Emergency Triage")) {
                assertThat(importer.getPackage().getGroupUuid(), is("05fa9aa6-017d-410b-90f9-decf407fdf65"));
            }
            else {
                assertThat(importer.getPackage().getGroupUuid(), is("1c6c89d3-0088-4e00-80fe-28bd87faa0f0"));
            }
        }

        assertThat(importers.size(), is(3));
    }

    @Test
    public void shouldSortPackageImporters() throws Exception {
        Collection<File> files = MetadataSharingSetup.loadMdsFiles();
        List<PackageImporter> importers = MetadataSharingSetup.loadPackageImporters(files);
        importers = MetadataSharingSetup.sortPackageImporters(importers);

        Iterator<PackageImporter> i = importers.iterator();
        assertThat(i.next().getPackage().getName(), is("PIH Concept Sources"));
        assertThat(i.next().getPackage().getName(), is("HUM Emergency Triage"));
        assertThat(i.next().getPackage().getName(), is("PIH Allergies"));
    }

    @Test
    public void shouldRemoveAlreadyLoadedPackageImporters() throws Exception {
        Collection<File> files = MetadataSharingSetup.loadMdsFiles();
        List<PackageImporter> importers = MetadataSharingSetup.loadPackageImporters(files);
        importers = MetadataSharingSetup.removeAlreadyLoadedPackageImporters(importers);
        assertThat(importers.size(), is(2));

        importers = MetadataSharingSetup.sortPackageImporters(importers);
        Iterator<PackageImporter> i = importers.iterator();
        assertThat(i.next().getPackage().getName(), is("PIH Concept Sources"));
        assertThat(i.next().getPackage().getName(), is("HUM Emergency Triage"));
    }

}
