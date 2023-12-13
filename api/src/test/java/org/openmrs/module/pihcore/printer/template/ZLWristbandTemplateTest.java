package org.openmrs.module.pihcore.printer.template;

import org.joda.time.DateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.openmrs.Location;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifier;
import org.openmrs.PatientIdentifierType;
import org.openmrs.PersonAddress;
import org.openmrs.PersonName;
import org.openmrs.messagesource.MessageSourceService;
import org.openmrs.module.addresshierarchy.AddressField;
import org.openmrs.module.addresshierarchy.AddressHierarchyLevel;
import org.openmrs.module.addresshierarchy.service.AddressHierarchyService;
import org.openmrs.module.emrapi.EmrApiProperties;
import org.openmrs.module.emrapi.adt.AdtService;
import org.openmrs.module.paperrecord.PaperRecordProperties;
import org.openmrs.module.printer.Printer;
import org.openmrs.module.printer.PrinterServiceImpl;
import org.openmrs.module.printer.UnableToPrintViaSocketException;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;

import static org.hamcrest.CoreMatchers.any;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.hamcrest.MockitoHamcrest.argThat;

public class ZLWristbandTemplateTest {

    private static Locale locale = new Locale("fr");

    private static DateFormat df  = new SimpleDateFormat("dd MMM yyyy", locale);

    private ZLWristbandTemplate ZLWristbandTemplate = new ZLWristbandTemplate();

    private EmrApiProperties emrApiProperties;

    private PaperRecordProperties paperRecordProperties;

    private AdtService adtService;

    private MessageSourceService messageSourceService;

    private AddressHierarchyService addressHierarchyService;

    private PatientIdentifierType primaryIdentifierType = new PatientIdentifierType();

    private PatientIdentifierType paperRecordIdentifierType = new PatientIdentifierType();

    private Location visitLocation = new Location();

    @BeforeEach
    public void setup() {

        emrApiProperties = mock(EmrApiProperties.class);
        paperRecordProperties = mock(PaperRecordProperties.class);
        adtService = mock(AdtService.class);
        messageSourceService = mock(MessageSourceService.class);
        addressHierarchyService = mock(AddressHierarchyService.class);

        when(emrApiProperties.getPrimaryIdentifierType()).thenReturn(primaryIdentifierType);
        when(paperRecordProperties.getPaperRecordIdentifierType()).thenReturn(paperRecordIdentifierType);
        when(adtService.getLocationThatSupportsVisits(argThat(any(Location.class)))).thenReturn(visitLocation);
        when(messageSourceService.getMessage("coreapps.gender.M", null, locale)).thenReturn("Masculin");
        when(messageSourceService.getMessage("coreapps.gender.F", null, locale)).thenReturn("Féminin");

        setupAddressHierarchyLevels();

        ZLWristbandTemplate.setAdtService(adtService);
        ZLWristbandTemplate.setEmrApiProperties(emrApiProperties);
        ZLWristbandTemplate.setMessageSourceService(messageSourceService);
        ZLWristbandTemplate.setPaperRecordProperties(paperRecordProperties);
        ZLWristbandTemplate.setAddressHierarchyService(addressHierarchyService);

    }

    private void setupAddressHierarchyLevels() {

        AddressHierarchyLevel country = new AddressHierarchyLevel();
        country.setAddressField(AddressField.COUNTRY);

        AddressHierarchyLevel state = new AddressHierarchyLevel();
        state.setAddressField(AddressField.STATE_PROVINCE);
        state.setParent(country);

        AddressHierarchyLevel city = new AddressHierarchyLevel();
        city.setAddressField(AddressField.CITY_VILLAGE);
        city.setParent(state);

        AddressHierarchyLevel address3 = new AddressHierarchyLevel();
        address3.setAddressField(AddressField.ADDRESS_3);
        address3.setParent(city);

        AddressHierarchyLevel address1 = new AddressHierarchyLevel();
        address1.setAddressField(AddressField.ADDRESS_1);
        address1.setParent(address3);

        AddressHierarchyLevel address2 = new AddressHierarchyLevel();
        address2.setAddressField(AddressField.ADDRESS_2);
        address2.setParent(address1);

        when(addressHierarchyService.getBottomAddressHierarchyLevel()).thenReturn(address2);
        when(addressHierarchyService.getAddressHierarchyLevelsCount()).thenReturn(6);
    }

    @Test
    public void testWristBandTemplate() {

        Date today = new Date();

        visitLocation.setName("Mirebalais");

        Patient patient = new Patient();
        patient.setGender("M");
        patient.setBirthdate(new DateTime(1940,7,7,5,5,5).toDate());

        PatientIdentifier primaryIdentifier = new PatientIdentifier();
        primaryIdentifier.setIdentifier("ZL1234");
        primaryIdentifier.setIdentifierType(primaryIdentifierType);
        primaryIdentifier.setVoided(false);
        patient.addIdentifier(primaryIdentifier);

        PatientIdentifier paperRecordIdentifier = new PatientIdentifier();
        paperRecordIdentifier.setIdentifier("A000005");
        paperRecordIdentifier.setIdentifierType(paperRecordIdentifierType);
        paperRecordIdentifier.setVoided(false);
        paperRecordIdentifier.setLocation(visitLocation);
        patient.addIdentifier(paperRecordIdentifier);

        PersonAddress address = new PersonAddress();
        address.setAddress2("Avant Eglise Chretienne des perlerlerin de la siant tete de moliere");
        address.setAddress1("Saut D'Eau");
        address.setAddress3("1ere Riviere Canot");
        address.setCityVillage("Saut d'Eau");
        address.setStateProvince("Centre");
        patient.addAddress(address);

        PersonName name = new PersonName();
        name.setGivenName("Ringo");
        name.setFamilyName("Starr");
        patient.addName(name);

        when(messageSourceService.getMessage("coreapps.ageYears", Collections.singletonList(patient.getAge()).toArray(), locale)).thenReturn(patient.getAge() + " an(s)");

        String output = ZLWristbandTemplate.generateWristband(patient, visitLocation);

        assertThat(output, containsString("^XA^CI28^MTD^FWB"));
        assertThat(output, containsString("^FO050,200^FB2150,1,0,L,0^AS^FDMirebalais " + df.format(today) + "^FS"));
        assertThat(output, containsString("^FO100,200^FB2150,1,0,L,0^AU^FDRingo Starr^FS"));
        assertThat(output, containsString("^FO160,200^FB2150,1,0,L,0^AU^FD07 juil. 1940^FS"));
        assertThat(output, containsString("^FO160,200^FB1850,1,0,L,0^AT^FD" + patient.getAge() + " an(s)^FS"));
        assertThat(output, containsString("^FO160,200^FB1650,1,0,L,0^AU^FDMasculin  A 000005^FS"));
        assertThat(output, containsString("^FO220,200^FB2150,1,0,L,0^AS^FDAvant Eglise Chretienne des perlerlerin de la siant tete de moliere^FS"));
        assertThat(output, containsString("^FO270,200^FB2150,1,0,L,0^AS^FDSaut D'Eau, 1ere Riviere Canot, Saut d'Eau, Centre^FS"));
        assertThat(output, containsString("^FO100,2400^AT^BY4^BC,150,N^FDZL1234^XZ"));
    }

    @Test
    public void testEstimatedBirthDate() {

        visitLocation.setName("Mirebalais");

        Patient patient = new Patient();
        patient.setGender("M");
        patient.setBirthdate(new DateTime(1940,7,7,5,5,5).toDate());
        patient.setBirthdateEstimated(true);

        PatientIdentifier primaryIdentifier = new PatientIdentifier();
        primaryIdentifier.setIdentifier("ZL1234");
        primaryIdentifier.setIdentifierType(primaryIdentifierType);
        primaryIdentifier.setVoided(false);
        patient.addIdentifier(primaryIdentifier);

        PatientIdentifier paperRecordIdentifier = new PatientIdentifier();
        paperRecordIdentifier.setIdentifier("A00005");
        paperRecordIdentifier.setIdentifierType(paperRecordIdentifierType);
        paperRecordIdentifier.setVoided(false);
        patient.addIdentifier(paperRecordIdentifier);

        PersonName name = new PersonName();
        name.setGivenName("Ringo");
        name.setFamilyName("Starr");
        patient.addName(name);

        String output = ZLWristbandTemplate.generateWristband(patient, new Location());

        assertThat(output, containsString("^FO160,200^FB2150,1,0,L,0^AU^FD1940^FS"));

    }

    @Test
    @Disabled
    public void testWristbandHack() throws UnableToPrintViaSocketException {
        int i = 0;

        while(i < 1) {
            StringBuffer data = new StringBuffer();

            data.append("^XA");
            data.append("^CI28");   // specify Unicode encoding
            data.append("^MTD");    // set direct transfer type
            data.append("^FWB");    // set orientation

            // demographics
            data.append("^FO050,200^FB2150,1,0,L,0^AS^FDMirebalais  19-May-2014^FS");
            data.append("^FO100,200^FB2150,1,0,L,0^AU^FDRingo Starr^FS");
            data.append("^FO160,200^FB2150,1,0,L,0^AU^FD07 Jul 1940^FS");
            data.append("^FO160,200^FB1850,1,0,L,0^AT^FD40 years^FS");
            data.append("^FO160,200^FB1650,1,0,L,0^AU^FDMale  A 000005^FS");
            data.append("^FO220,200^FB2150,1,0,L,0^AS^FDAvant Eglise Chretienne des perlerlerin de la siant tete de moliere^FS");
            data.append("^FO270,200^FB2150,1,0,L,0^AS^FDSaut D'Eau, 1ere Riviere Canot, Saut d'Eau, Centre^FS");

            // barcode
            data.append("^FO100,2400^AT^BY4^BC,150,N^FDABC^XZ");

            Printer printer = new Printer();
            printer.setIpAddress("10.3.18.113");
            printer.setPort("9100");
            printer.setId(1);

            new PrinterServiceImpl().printViaSocket(data.toString(), printer, "UTF-8");

            i++;
        }
    }

}
