package org.openmrs.module.pihcore.printer.template;

import org.joda.time.DateTime;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.openmrs.Location;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifier;
import org.openmrs.PatientIdentifierType;
import org.openmrs.PersonAddress;
import org.openmrs.PersonAttribute;
import org.openmrs.PersonAttributeType;
import org.openmrs.PersonName;
import org.openmrs.api.PatientService;
import org.openmrs.api.PersonService;
import org.openmrs.messagesource.MessageSourceService;
import org.openmrs.module.addresshierarchy.AddressField;
import org.openmrs.module.addresshierarchy.AddressHierarchyLevel;
import org.openmrs.module.addresshierarchy.service.AddressHierarchyService;
import org.openmrs.module.emrapi.EmrApiProperties;
import org.openmrs.module.emrapi.adt.AdtService;
import org.openmrs.module.pihcore.PihEmrConfigConstants;
import org.openmrs.module.pihcore.SierraLeoneConfigConstants;

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

public class SLWristbandTemplateTest {
    private static Locale locale = new Locale("en");

    private static DateFormat df  = new SimpleDateFormat("dd-MMM-yyyy");

    private SLWristbandTemplate SLWristbandTemplate = new SLWristbandTemplate();

    private EmrApiProperties emrApiProperties;

    private AdtService adtService;

    private MessageSourceService messageSourceService;

    private AddressHierarchyService addressHierarchyService;

    private PersonService personService;

    private PatientService patientService;

    private PatientIdentifierType primaryIdentifierType = new PatientIdentifierType();

    private PatientIdentifierType nationalIdNumberIdentifierType = new PatientIdentifierType();

    private PersonAttributeType telephoneNumberAttributeType = new PersonAttributeType();

    private Location visitLocation = new Location();

    private void setupAddressHierarchyLevels() {

        AddressHierarchyLevel country = new AddressHierarchyLevel();
        country.setAddressField(AddressField.COUNTRY);

        AddressHierarchyLevel district = new AddressHierarchyLevel();
        district.setAddressField(AddressField.COUNTY_DISTRICT);
        district.setParent(country);

        AddressHierarchyLevel chiefdom = new AddressHierarchyLevel();
        chiefdom.setAddressField(AddressField.STATE_PROVINCE);
        chiefdom.setParent(district);

        AddressHierarchyLevel section = new AddressHierarchyLevel();
        section.setAddressField(AddressField.ADDRESS_1);
        section.setParent(chiefdom);

        AddressHierarchyLevel village = new AddressHierarchyLevel();
        village.setAddressField(AddressField.CITY_VILLAGE);
        village.setParent(section);

        AddressHierarchyLevel address2 = new AddressHierarchyLevel();
        address2.setAddressField(AddressField.ADDRESS_2);
        address2.setParent(village);

        when(addressHierarchyService.getBottomAddressHierarchyLevel()).thenReturn(address2);
        when(addressHierarchyService.getAddressHierarchyLevelsCount()).thenReturn(6);
    }

    @BeforeEach
    public void setup() {

        emrApiProperties = mock(EmrApiProperties.class);
        adtService = mock(AdtService.class);
        messageSourceService = mock(MessageSourceService.class);
        addressHierarchyService = mock(AddressHierarchyService.class);
        personService = mock(PersonService.class);
        patientService = mock(PatientService.class);
        visitLocation.setUuid(SierraLeoneConfigConstants.LOCATION_KGH_UUID);

        when(emrApiProperties.getPrimaryIdentifierType()).thenReturn(primaryIdentifierType);
        when(adtService.getLocationThatSupportsVisits(argThat(any(Location.class)))).thenReturn(visitLocation);
        when(messageSourceService.getMessage("coreapps.gender.M", null, locale)).thenReturn("Male");
        when(messageSourceService.getMessage("coreapps.gender.F", null, locale)).thenReturn("Female");
        when(messageSourceService.getMessage("pihcore.date_estimated", null, locale)).thenReturn("estimated");
        when(messageSourceService.getMessage("pihcore.units.years", null, locale)).thenReturn("year(s)");
        when(personService.getPersonAttributeTypeByUuid(PihEmrConfigConstants.PERSONATTRIBUTETYPE_TELEPHONE_NUMBER_UUID)).thenReturn(telephoneNumberAttributeType);
        when(patientService.getPatientIdentifierTypeByUuid(SierraLeoneConfigConstants.PATIENTIDENTIFIERTYPE_NATIONALID_UUID)).thenReturn(nationalIdNumberIdentifierType);
        setupAddressHierarchyLevels();

        SLWristbandTemplate.setAdtService(adtService);
        SLWristbandTemplate.setEmrApiProperties(emrApiProperties);
        SLWristbandTemplate.setMessageSourceService(messageSourceService);
        SLWristbandTemplate.setAddressHierarchyService(addressHierarchyService);
        SLWristbandTemplate.setPersonService(personService);
        SLWristbandTemplate.setPatientService(patientService);

    }

    @Test
    public void testWristBandTemplate() {

        Date today = new Date();

        Patient patient = new Patient();
        patient.setGender("F");
        patient.setBirthdate(new DateTime(1940,7,7,5,5,5).toDate());
        patient.setBirthdateEstimated(true);

        PatientIdentifier primaryIdentifier = new PatientIdentifier();
        primaryIdentifier.setIdentifier("KGH00234003");
        primaryIdentifier.setIdentifierType(primaryIdentifierType);
        primaryIdentifier.setVoided(false);
        patient.addIdentifier(primaryIdentifier);

        PatientIdentifier nationalIdNumberIdentifier = new PatientIdentifier();
        nationalIdNumberIdentifier.setIdentifier("A123456");
        nationalIdNumberIdentifier.setIdentifierType(nationalIdNumberIdentifierType);
        nationalIdNumberIdentifier.setVoided(false);
        patient.addIdentifier(nationalIdNumberIdentifier);

        PersonAddress address = new PersonAddress();
        address.setAddress2("15 BRIMA FONJAH ST");
        address.setCityVillage("Koidu");
        address.setAddress1("Njaiafeh Section");
        address.setStateProvince("Nimiyama");
        address.setCountyDistrict("Kono");
        patient.addAddress(address);

        PersonName name = new PersonName();
        name.setGivenName("Ringo");
        name.setFamilyName("Starr");
        patient.addName(name);

        PersonAttribute telephoneNumber = new PersonAttribute();
        telephoneNumber.setAttributeType(telephoneNumberAttributeType);
        telephoneNumber.setValue("1234567890");
        patient.addAttribute(telephoneNumber);


        String output = SLWristbandTemplate.generateWristband(patient, visitLocation, null);

        System.out.println(output);

       /* assertThat(output, containsString("^XA^CI28^MTD^FWB"));
        assertThat(output, containsString("^FO050,200^FB2150,1,0,L,0^AS^FDKoidu Government Hospital " + df.format(today) + "^FS"));
        assertThat(output, containsString("^FO100,200^FB2150,1,0,L,0^AU^FDRingo Starr  KGH00234003^FS"));
        assertThat(output, containsString("^FO160,200^FB2150,1,0,L,0^AU^FD07-Jul-1940^FS"));
        assertThat(output, containsString("^FO160,200^FB1850,1,0,L,0^AT^FD" + patient.getAge() + " an(s)^FS"));
        assertThat(output, containsString("^FO160,200^FB1650,1,0,L,0^AU^FDMasculin  ^FS"));
        assertThat(output, containsString("^FO220,200^FB2150,1,0,L,0^AS^FD15 BRIMA FONJAH ST^FS"));
        assertThat(output, containsString("^FO270,200^FB2150,1,0,L,0^AS^FDKoidu, Njaiafeh Section, Nimiyama, Kono^FS"));
        assertThat(output, containsString("^FO100,2400^AT^BY4^BC,150,N^FDKGH00234003^XZ"));*/
    }

}
