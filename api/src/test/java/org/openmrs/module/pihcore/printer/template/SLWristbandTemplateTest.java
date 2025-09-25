package org.openmrs.module.pihcore.printer.template;

import org.joda.time.DateTime;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.openmrs.Concept;
import org.openmrs.Encounter;
import org.openmrs.Location;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifier;
import org.openmrs.PatientIdentifierType;
import org.openmrs.PersonAddress;
import org.openmrs.PersonAttribute;
import org.openmrs.PersonAttributeType;
import org.openmrs.PersonName;
import org.openmrs.Relationship;
import org.openmrs.RelationshipType;
import org.openmrs.api.ConceptService;
import org.openmrs.api.ObsService;
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
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;

import static org.hamcrest.CoreMatchers.any;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.hamcrest.MockitoHamcrest.argThat;
import static org.openmrs.module.pihcore.printer.template.SLWristbandTemplate.BIRTH_ORDER_CONCEPT_PIH_CODE;
import static org.openmrs.module.pihcore.printer.template.SLWristbandTemplate.BIRTH_WEIGHT_CONCEPT_PIH_CODE;
import static org.openmrs.module.pihcore.printer.template.SLWristbandTemplate.DATETIME_OF_DELIVERY_CONCEPT_PIH_CODE;
import static org.openmrs.module.pihcore.printer.template.SLWristbandTemplate.FEMALE_CONCEPT_PIH_CODE;
import static org.openmrs.module.pihcore.printer.template.SLWristbandTemplate.NEWBORN_DETAILS_CONCEPT_PIH_CODE;
import static org.openmrs.module.pihcore.printer.template.SLWristbandTemplate.PATIENT_EXISTS_IN_EMR_CONCEPT_PIH_CODE;
import static org.openmrs.module.pihcore.printer.template.SLWristbandTemplate.SEX_CONCEPT_PIH_CODE;
import static org.openmrs.module.pihcore.printer.template.SLWristbandTemplate.YES_CONCEPT_PIH_CODE;

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

    private ConceptService conceptService;

    private ObsService obsService;

    private PatientIdentifierType primaryIdentifierType = new PatientIdentifierType();

    private PatientIdentifierType nationalIdNumberIdentifierType = new PatientIdentifierType();

    private PersonAttributeType telephoneNumberAttributeType = new PersonAttributeType();

    private RelationshipType motherChildRelationshipType = new RelationshipType();

    private Concept newbornDetails = new Concept();

    private Concept patientExistsInEmrConcept = new Concept();

    private Concept yesConcept = new Concept();

    private Concept dateOfDeliveryConcept = new Concept();

    private Concept sexConcept = new Concept();

    private Concept femaleConcept = new Concept();

    private Concept birthWeightConcept = new Concept();

    private Concept birthOrderConcept = new Concept();

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
        conceptService = mock(ConceptService.class);
        obsService = mock(ObsService.class);
        visitLocation.setUuid(SierraLeoneConfigConstants.LOCATION_KGH_UUID);

        when(emrApiProperties.getPrimaryIdentifierType()).thenReturn(primaryIdentifierType);
        when(adtService.getLocationThatSupportsVisits(argThat(any(Location.class)))).thenReturn(visitLocation);
        when(messageSourceService.getMessage("coreapps.gender.M", null, locale)).thenReturn("Male");
        when(messageSourceService.getMessage("coreapps.gender.F", null, locale)).thenReturn("Female");
        when(messageSourceService.getMessage("pihcore.date_estimated", null, locale)).thenReturn("estimated");
        when(messageSourceService.getMessage("pihcore.units.years", null, locale)).thenReturn("year(s)");
        when(messageSourceService.getMessage("pihcore.born", null, locale)).thenReturn("Born");
        when(messageSourceService.getMessage("pihcore.mother", null, locale)).thenReturn("Mother");
        when(messageSourceService.getMessage("pihcore.twin", null, locale)).thenReturn("Twin");
        when(messageSourceService.getMessage("pihcore.triplet", null, locale)).thenReturn("Triplet");
        when(messageSourceService.getMessage("pihcore.of", null, locale)).thenReturn("of");
        when(personService.getPersonAttributeTypeByUuid(PihEmrConfigConstants.PERSONATTRIBUTETYPE_TELEPHONE_NUMBER_UUID)).thenReturn(telephoneNumberAttributeType);
        when(personService.getRelationshipTypeByUuid(PihEmrConfigConstants.RELATIONSHIPTYPE_MOTHERTOCHILD_UUID)).thenReturn(motherChildRelationshipType);
        when(patientService.getPatientIdentifierTypeByUuid(SierraLeoneConfigConstants.PATIENTIDENTIFIERTYPE_NATIONALID_UUID)).thenReturn(nationalIdNumberIdentifierType);
        when(conceptService.getConceptByMapping(NEWBORN_DETAILS_CONCEPT_PIH_CODE, "PIH")).thenReturn(newbornDetails);
        when(conceptService.getConceptByMapping(PATIENT_EXISTS_IN_EMR_CONCEPT_PIH_CODE, "PIH")).thenReturn(patientExistsInEmrConcept);
        when(conceptService.getConceptByMapping(YES_CONCEPT_PIH_CODE, "PIH")).thenReturn(yesConcept);
        when(conceptService.getConceptByMapping(DATETIME_OF_DELIVERY_CONCEPT_PIH_CODE, "PIH")).thenReturn(dateOfDeliveryConcept);
        when(conceptService.getConceptByMapping(SEX_CONCEPT_PIH_CODE, "PIH")).thenReturn(sexConcept);
        when(conceptService.getConceptByMapping(FEMALE_CONCEPT_PIH_CODE, "PIH")).thenReturn(femaleConcept);
        when(conceptService.getConceptByMapping(BIRTH_WEIGHT_CONCEPT_PIH_CODE, "PIH")).thenReturn(birthWeightConcept);
        when(conceptService.getConceptByMapping(BIRTH_ORDER_CONCEPT_PIH_CODE, "PIH")).thenReturn(birthOrderConcept);

        setupAddressHierarchyLevels();

        SLWristbandTemplate.setAdtService(adtService);
        SLWristbandTemplate.setEmrApiProperties(emrApiProperties);
        SLWristbandTemplate.setMessageSourceService(messageSourceService);
        SLWristbandTemplate.setAddressHierarchyService(addressHierarchyService);
        SLWristbandTemplate.setPersonService(personService);
        SLWristbandTemplate.setPatientService(patientService);
        SLWristbandTemplate.setConceptService(conceptService);
        SLWristbandTemplate.setObsService(obsService);

    }

    @Test
    public void testWristBandTemplate() throws Exception {

        Date today = new Date();

        Patient patient = new Patient();
        patient.setGender("F");
        patient.setBirthdate(new SimpleDateFormat("yyyy-MM-dd").parse("1940-07-07"));
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

        // for testing via: https://labelary.com/viewer.html
        System.out.println(output);

        assertThat(output, containsString("^XA^CI28^MTD^FWB^FO050,200^FB1650,1,0,L,0^AS^FDKoidu Government Hospital " + new SimpleDateFormat("dd-MMM-yyyy").format(today) + "^FS"));
        assertThat(output, containsString("^FO100,200^FB1650,1,0,L,0^AT^FDRingo Starr  KGH00234003^FS^FO150,200^FB1650,1,0,L,0^AS^FDFemale^FS"));
        assertThat(output, containsString("^FO150,200^FB1350,1,0,L,0^AS^FDestimated 85 year(s)^FS^FO190,200^FB1650,1,0,L,0^AS^FD1234567890^FS"));
        assertThat(output, containsString("^FO190,200^FB1350,1,0,L,0^AS^FDNIN: A123456^FS^FO230,200^FB1650,1,0,L,0^AS^FD15 BRIMA FONJAH ST^FS"));
        assertThat(output, containsString("^FO270,200^FB1650,1,0,L,0^AS^FDKoidu, Njaiafeh Section, Nimiyama, Kono^FS^FO100,1900^AT^BY4^BC,150,N^FDKGH00234003^FS^XZ"));
    }

    @Test
    public void testWristBandTemplateWithMinimalPatientInfo() {

        Date today = new Date();

        Patient patient = new Patient();

        PersonName name = new PersonName();
        name.setGivenName("Ringo");
        name.setFamilyName("Starr");
        patient.addName(name);

        String output = SLWristbandTemplate.generateWristband(patient, visitLocation, null);

        // for testing via: https://labelary.com/viewer.html
        System.out.println(output);

        assertThat(output, containsString("^XA^CI28^MTD^FWB^FO050,200^FB1650,1,0,L,0^AS^FDKoidu Government Hospital " + new SimpleDateFormat("dd-MMM-yyyy").format(today) + "^FS"));
        assertThat(output, containsString("^FO100,200^FB1650,1,0,L,0^AT^FDRingo Starr  ^FS^FO150,200^FB1650,1,0,L,0^AS^FD^FS^XZ"));
    }

    @Test
    public void testWristBandTemplateForChild() {

        Date today = new Date();

        Patient patient = new Patient();
        patient.setGender("F");
        patient.setBirthdate(today);

        PatientIdentifier primaryIdentifier = new PatientIdentifier();
        primaryIdentifier.setIdentifier("KGH00234003");
        primaryIdentifier.setIdentifierType(primaryIdentifierType);
        primaryIdentifier.setVoided(false);
        patient.addIdentifier(primaryIdentifier);

        PersonName name = new PersonName();
        name.setGivenName("Ringo");
        name.setFamilyName("Starr");
        patient.addName(name);

        Patient mother = new Patient();
        mother.setGender("F");
        mother.setBirthdate(new DateTime().minusYears(20).toDate());

        PersonName motherName = new PersonName();
        motherName.setGivenName("Mother");
        motherName.setFamilyName("Starr");
        mother.addName(motherName);

        PatientIdentifier motherPrimaryIdentifier = new PatientIdentifier();
        motherPrimaryIdentifier.setIdentifier("KGH00111003");
        motherPrimaryIdentifier.setIdentifierType(primaryIdentifierType);
        motherPrimaryIdentifier.setVoided(false);
        mother.addIdentifier(motherPrimaryIdentifier);

        Relationship motherChildRelationship = new Relationship();
        motherChildRelationship.setRelationshipType(motherChildRelationshipType);
        motherChildRelationship.setPersonA(mother);
        motherChildRelationship.setPersonB(patient);

        when(personService.getRelationships(null, patient, motherChildRelationshipType)).thenReturn(Collections.singletonList(motherChildRelationship));
        when(patientService.getPatientOrPromotePerson(mother.getId())).thenReturn(mother);

        Encounter deliveryEncounter = new Encounter();

        Obs nomMatchingDelivery = new Obs();
        nomMatchingDelivery.setConcept(newbornDetails);
        deliveryEncounter.addObs(nomMatchingDelivery);

        Obs anotherPatientExistsInEmrObs = new Obs();
        anotherPatientExistsInEmrObs.setConcept(patientExistsInEmrConcept);
        anotherPatientExistsInEmrObs.setComment("some-other-uuid");
        nomMatchingDelivery.addGroupMember(anotherPatientExistsInEmrObs);

        Obs matchingDelivery = new Obs();
        matchingDelivery.setConcept(newbornDetails);
        deliveryEncounter.addObs(matchingDelivery);

        Obs patientExistsInEmrObs = new Obs();
        patientExistsInEmrObs.setConcept(patientExistsInEmrConcept);
        patientExistsInEmrObs.setComment(patient.getUuid());
        matchingDelivery.addGroupMember(patientExistsInEmrObs);

        Obs birthWeightObs = new Obs();
        birthWeightObs.setConcept(birthWeightConcept);
        birthWeightObs.setValueNumeric(5.5);
        matchingDelivery.addGroupMember(birthWeightObs);

        Obs birthGenderObs = new Obs();
        birthGenderObs.setConcept(sexConcept);
        birthGenderObs.setValueCoded(femaleConcept);
        matchingDelivery.addGroupMember(birthGenderObs);

        Obs birthDatetimeObs = new Obs();
        birthDatetimeObs.setConcept(dateOfDeliveryConcept);
        birthDatetimeObs.setValueDatetime(today);
        matchingDelivery.addGroupMember(birthDatetimeObs);

        Obs birthOrderObs = new Obs();
        birthOrderObs.setConcept(birthOrderConcept);
        birthOrderObs.setValueNumeric(2.0);
        matchingDelivery.addGroupMember(birthOrderObs);

        when(obsService.getObservations(Collections.singletonList(mother), null, Collections.singletonList(patientExistsInEmrConcept),
                Collections.singletonList(yesConcept), null, null, null, null, null, null, null, false))
                .thenReturn(Arrays.asList(anotherPatientExistsInEmrObs, patientExistsInEmrObs));

        String output = SLWristbandTemplate.generateWristband(patient, visitLocation, null);

        // for testing via: https://labelary.com/viewer.html
        System.out.println(output);

        assertThat(output, containsString("^XA^CI28^MTD^FWB^FO050,200^FB1650,1,0,L,0^AS^FDKoidu Government Hospital " + new SimpleDateFormat("dd-MMM-yyyy").format(today) + "^FS"));
        assertThat(output, containsString("^FO100,200^FB1650,1,0,L,0^AT^FDRingo Starr  KGH00234003^FS"));
        assertThat(output, containsString("^FO150,200^FB1650,1,0,L,0^AS^FDBorn " + new SimpleDateFormat("dd-MMM-yyyy HH:mm").format(today) + "^FS"));
        assertThat(output, containsString("^FO190,200^FB1650,1,0,L,0^AS^FD5.5kg^FS^FO190,200^FB1550,1,0,L,0^AS^FDFI^FS"));
        assertThat(output, containsString("^FO190,200^FB1450,1,0,L,0^AS^FDTwin 2 of 2^FS"));
        assertThat(output, containsString("^FO230,200^FB1650,1,0,L,0^AS^FDMother: Mother Starr  KGH00111003^FS^FO100,1900^AT^BY4^BC,150,N^FDKGH00234003^FS^XZ"));
    }

    @Test
    public void testWristBandTemplateForChildWithMinimalPatientInfo() {

        Date today = new Date();

        Patient patient = new Patient();
        patient.setBirthdate(new DateTime().minusYears(1).toDate());

        PersonName name = new PersonName();
        name.setGivenName("Ringo");
        name.setFamilyName("Starr");
        patient.addName(name);

        Patient mother = new Patient();
        mother.setGender("F");
        mother.setBirthdate(new DateTime().minusYears(20).toDate());

        PersonName motherName = new PersonName();
        motherName.setGivenName("Mother");
        motherName.setFamilyName("Starr");
        mother.addName(motherName);

        Relationship motherChildRelationship = new Relationship();
        motherChildRelationship.setRelationshipType(motherChildRelationshipType);
        motherChildRelationship.setPersonA(mother);
        motherChildRelationship.setPersonB(patient);

        when(personService.getRelationships(null, patient, motherChildRelationshipType)).thenReturn(Collections.singletonList(motherChildRelationship));
        when(patientService.getPatientOrPromotePerson(mother.getId())).thenReturn(mother);

        String output = SLWristbandTemplate.generateWristband(patient, visitLocation, null);

        // for testing via: https://labelary.com/viewer.html
        System.out.println(output);

        assertThat(output, containsString("^XA^CI28^MTD^FWB^FO050,200^FB1650,1,0,L,0^AS^FDKoidu Government Hospital " + new SimpleDateFormat("dd-MMM-yyyy").format(today) + "^FS"));
        assertThat(output, containsString("^FO100,200^FB1650,1,0,L,0^AT^FDRingo Starr  ^FS"));
        assertThat(output, containsString("^FO230,200^FB1650,1,0,L,0^AS^FDMother: Mother Starr  ^FS^XZ"));
   }
}
