package org.openmrs.module.pihcore.printer.impl;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.openmrs.Location;
import org.openmrs.LocationTag;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifierType;
import org.openmrs.api.LocationService;
import org.openmrs.api.PatientService;
import org.openmrs.contrib.testdata.TestDataManager;
import org.openmrs.contrib.testdata.builder.PatientBuilder;
import org.openmrs.module.emrapi.EmrApiActivator;
import org.openmrs.module.initializer.Domain;
import org.openmrs.module.metadatadeploy.MetadataUtils;
import org.openmrs.module.metadatadeploy.api.MetadataDeployService;
import org.openmrs.module.metadatamapping.api.MetadataMappingService;
import org.openmrs.module.paperrecord.PaperRecordConstants;
import org.openmrs.module.pihcore.PihCoreContextSensitiveTest;
import org.openmrs.module.pihcore.TestAddressBundle;
import org.openmrs.module.pihcore.ZlConfigConstants;
import org.openmrs.module.pihcore.config.Config;
import org.openmrs.module.pihcore.config.ConfigDescriptor;
import org.openmrs.module.pihcore.metadata.Metadata;
import org.openmrs.module.pihcore.setup.MetadataMappingsSetup;
import org.openmrs.module.pihcore.setup.PrinterSetup;
import org.openmrs.module.printer.Printer;
import org.openmrs.module.printer.PrinterModel;
import org.openmrs.module.printer.PrinterModuleActivator;
import org.openmrs.module.printer.PrinterService;
import org.openmrs.module.printer.PrinterType;
import org.springframework.beans.factory.annotation.Autowired;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Test the ZL EMR ID Card Printer functionality
 */
public class ZlEmrIdCardPrinterTest extends PihCoreContextSensitiveTest {

    @Autowired
    private TestDataManager testDataManager;

    @Autowired
    private PrinterService printerService;

    @Autowired
    private LocationService locationService;

    @Autowired
    PatientService patientService;

    @Autowired
    MetadataMappingService metadataMappingService;

    @Autowired
    MetadataDeployService metadataDeployService;

    @Autowired
    TestAddressBundle testAddressBundle;

    @Autowired
    ZlEmrIdCardPrinter zlEmrIdCardPrinter;

    @Before
    public void setup() throws Exception {

        EmrApiActivator emrApiActivator = new EmrApiActivator();
        emrApiActivator.started();

        PrinterModuleActivator printerModuleActivator = new PrinterModuleActivator();
        printerModuleActivator.started(); // Create Location Attribute Types Needed

        loadFromInitializer(Domain.LOCATIONS, "locations-base.csv");
        loadFromInitializer(Domain.LOCATIONS, "locations-site-mirebalais.csv");
        loadFromInitializer(Domain.PERSON_ATTRIBUTE_TYPES, "personAttributeTypes.csv");
        loadFromInitializer(Domain.PATIENT_IDENTIFIER_TYPES, "zlIdentifierTypes.csv");

        metadataDeployService.installBundle(testAddressBundle);

        PrinterSetup.registerPrintHandlers(printerService); // Register print handlers

        Config config = mock(Config.class);
        when(config.getCountry()).thenReturn(ConfigDescriptor.Country.HAITI);
        when(config.getSite()).thenReturn("MIREBALAIS");
        Location location = locationService.getLocation("Biwo Resepsyon");
        LocationTag tag = new LocationTag();
        tag.setName(PaperRecordConstants.LOCATION_TAG_MEDICAL_RECORD_LOCATION);
        locationService.saveLocationTag(tag);
        location.addTag(tag);
        MetadataMappingsSetup.setupPrimaryIdentifierTypeBasedOnCountry(metadataMappingService, patientService, config);
    }

    @Test
    public void testZlEmrIdCardPrinting() throws Exception {

        // Register printer model
        PrinterModel model = new PrinterModel();
        model.setName("Test Zebra P110i");
        model.setType(PrinterType.ID_CARD);
        model.setPrintHandler("p110iPrintHandler");
        printerService.savePrinterModel(model);

        // Register instance of this printer model
        Printer printer = new Printer();
        printer.setName("Test ZL EMR ID Card Printer");
        printer.setType(PrinterType.ID_CARD);
        printer.setModel(model);
        printer.setPhysicalLocation(null);
        printer.setIpAddress("127.0.0.1");
        printer.setPort("9105");
        printerService.savePrinter(printer);

        // Set location for this printer
        Location location = locationService.getLocation("Biwo Resepsyon");  // Clinic Reception
        printerService.setDefaultPrinter(location, PrinterType.ID_CARD, printer);

        // Create a patient for whom to print an id card
        PatientBuilder pb = testDataManager.patient().birthdate("1948-02-16").gender("M").name("Ringo", "Starr");
        pb.identifier(MetadataUtils.existing(PatientIdentifierType.class, ZlConfigConstants.PATIENTIDENTIFIERTYPE_ZLEMRID_UUID), "X2ECEX", location);
        pb.personAttribute(Metadata.getPhoneNumberAttributeType(), "555-1212");
        pb.address("should be line 2", "should be line 1", "should be line 4", "should be line 5a", "should not exist", "should be line 5b");
        Patient patient = pb.save();

        TestPrinter testPrinter = new TestPrinter("127.0.0.1", 9105, "Windows-1252");
        testPrinter.start();

        zlEmrIdCardPrinter.print(patient, location);

        // Pause up to 30 seconds for printing to happen
        for (int i=0; i<30 && testPrinter.getNumPrintJobs() == 0; i++) {
            Thread.sleep(1000);
        }

        Assert.assertEquals(1, testPrinter.getNumPrintJobs());
        TestPrinter.PrintJob job = testPrinter.getLatestPrintJob();
        Assert.assertTrue(job.containsData("B 75 550 0 0 0 3 100 0 X2ECEX"));

        testPrinter.stop();
    }
}
