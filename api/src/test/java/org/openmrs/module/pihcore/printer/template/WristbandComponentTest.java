package org.openmrs.module.pihcore.printer.template;

import org.joda.time.DateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openmrs.Location;
import org.openmrs.Patient;
import org.openmrs.api.PatientService;
import org.openmrs.contrib.testdata.TestDataManager;
import org.openmrs.messagesource.PresentationMessage;
import org.openmrs.module.emrapi.EmrApiActivator;
import org.openmrs.module.emrapi.EmrApiProperties;
import org.openmrs.module.metadatamapping.api.MetadataMappingService;
import org.openmrs.module.paperrecord.PaperRecordProperties;
import org.openmrs.module.pihcore.PihCoreContextSensitiveTest;
import org.openmrs.module.pihcore.config.Config;
import org.openmrs.module.pihcore.config.ConfigDescriptor;
import org.openmrs.module.pihcore.setup.MetadataMappingsSetup;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class WristbandComponentTest extends PihCoreContextSensitiveTest {

    private static Locale locale = new Locale("fr");

    private static DateFormat df  = new SimpleDateFormat("dd MMM yyyy", locale);

    @Autowired
    private TestDataManager testDataManager;

    @Autowired
    private WristbandTemplate wristbandTemplate;

    @Autowired
    private EmrApiProperties emrApiProperties;

    @Autowired
    private PaperRecordProperties paperRecordProperties;

    @Autowired
    private MetadataMappingService metadataMappingService;

    @Autowired
    private PatientService patientService;

    @BeforeEach
    public void setup() throws Exception {
        executeDataSet("wristbandTestDataset.xml");

        EmrApiActivator emrApiActivator = new EmrApiActivator();
        emrApiActivator.started();

        Config config = mock(Config.class);
        when(config.getCountry()).thenReturn(ConfigDescriptor.Country.HAITI);
        MetadataMappingsSetup.setupPrimaryIdentifierTypeBasedOnCountry(metadataMappingService, patientService, config);

        Locale fr = new Locale("fr");
        initializerMessageSource.addPresentation(new PresentationMessage("coreapps.ageYears", fr, "{0} an(s)", "Age"));
        initializerMessageSource.addPresentation(new PresentationMessage("coreapps.gender.M", fr, "Masculin", "Male"));
    }

    @Test
    public void testWristbandTemplate() {

        Date today = new Date();

        Location location = testDataManager.location()
                .name("Mirebalais")
                .tag(emrApiProperties.getSupportsVisitsLocationTag())
                .save();

        Patient patient = testDataManager.patient()
                .identifier(emrApiProperties.getPrimaryIdentifierType(), "X2ECEX", location)
                .identifier(paperRecordProperties.getPaperRecordIdentifierType(), "A000005", location)
                .birthdate(new DateTime(1940,7,7,5,5,5).toDate())
                .gender("M")
                .name("Ringo", "Starr")
                .save();

        String output = wristbandTemplate.generateWristband(patient, location);

        assertThat(output, containsString("^XA^CI28^MTD^FWB"));
        assertThat(output, containsString("^FO050,200^FB2150,1,0,L,0^AS^FDMirebalais " + df.format(today) + "^FS"));
        assertThat(output, containsString("^FO100,200^FB2150,1,0,L,0^AU^FDRingo Starr^FS"));
        assertThat(output, containsString("^FO160,200^FB2150,1,0,L,0^AU^FD07 juil. 1940^FS"));
        assertThat(output, containsString("^FO160,200^FB1850,1,0,L,0^AT^FD")); // broke this out to avoid testing for patient age, which obviously changes depending on date
        assertThat(output, containsString("an(s)^FS"));
        assertThat(output, containsString("^FO160,200^FB1650,1,0,L,0^AU^FDMasculin  A 000005^FS"));
        assertThat(output, containsString("^FO100,2400^AT^BY4^BC,150,N^FDX2ECEX^XZ"));
    }
}
