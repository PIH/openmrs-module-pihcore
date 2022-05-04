package org.openmrs.module.pihcore.setup;

import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openmrs.Concept;
import org.openmrs.ConceptMap;
import org.openmrs.ConceptName;
import org.openmrs.ConceptReferenceTerm;
import org.openmrs.ConceptSource;
import org.openmrs.Drug;
import org.openmrs.api.ConceptService;
import org.openmrs.api.context.Context;
import org.openmrs.module.pihcore.PihCoreContextSensitiveTest;
import org.openmrs.util.OpenmrsConstants;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Properties;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.in;
import static org.hamcrest.MatcherAssert.assertThat;

public class DrugListSetupTest extends PihCoreContextSensitiveTest {

    public static final String appDataTestDir = "testAppDataDir";

    @BeforeEach
    public void setup() {
        // configure app data dir path
        String path = getClass().getClassLoader().getResource(appDataTestDir).getPath() + File.separator;
        System.setProperty("OPENMRS_APPLICATION_DATA_DIRECTORY", path);
        Properties prop = new Properties();
        prop.setProperty(OpenmrsConstants.APPLICATION_DATA_DIRECTORY_RUNTIME_PROPERTY, path);
        Context.setRuntimeProperties(prop);

        // delete checksums
        String checksumsPath = path + "configuration_checksums/";
        File checksumsDir = new File(checksumsPath);
        if (checksumsDir.exists()) {
            try {
                FileUtils.forceDelete(checksumsDir);
            } catch (IOException e) {
                System.err.println("Failed to delete checksums dir " + checksumsDir.getAbsolutePath());
            }
        }

    }

    @Test
    public void shouldInstallNewDrugList() throws Exception {
        ConceptService cs = Context.getConceptService();
        List<Drug> initialInstalledDrugs = Context.getConceptService().getAllDrugs();
        Concept testDrugConcept = new Concept();
        testDrugConcept.addName(new ConceptName("test drug concept", Locale.ENGLISH));
        testDrugConcept.setDatatype(cs.getConceptDatatype(4));  // N/A
        testDrugConcept.setConceptClass(cs.getConceptClass(3));  // Drug
        cs.saveConcept(testDrugConcept);
        ConceptReferenceTerm crt = new ConceptReferenceTerm();
        ConceptSource source = cs.getConceptSource(2);  // SNOMED CT
        crt.setConceptSource(source);
        crt.setCode("9876");
        cs.saveConceptReferenceTerm(crt);
        ConceptMap map = new ConceptMap();
        map.setConcept(testDrugConcept);
        map.setConceptReferenceTerm(crt);
        testDrugConcept.addConceptMapping(map);
        cs.saveConcept(testDrugConcept);
        Context.flushSession();  // prevents "Unexpected call to partialFlushEnd; expecting partialFlushStart"
        DrugListSetup.installDrugList();
        List<Drug> installedDrugs = Context.getConceptService().getAllDrugs();
        List<String> installedDrugNames = new ArrayList<String>();
        for (Drug drug : installedDrugs) {
            installedDrugNames.add(drug.getName());
        }
        assertThat("Test Drug", is(in(installedDrugNames)));
        assertThat(installedDrugs.size(), is(initialInstalledDrugs.size() + 1));
    }

    @Test
    public void shouldNotInstallSameDrugListTwice() throws Exception {
        DrugListSetup.installDrugList();
        List<Drug> firstInstalledDrugs = Context.getConceptService().getAllDrugs();
        DrugListSetup.installDrugList();
        List<Drug> secondInstalledDrugs = Context.getConceptService().getAllDrugs();
        assertThat(firstInstalledDrugs.size(), is(secondInstalledDrugs.size()));
    }
}
