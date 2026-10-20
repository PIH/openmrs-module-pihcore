package org.openmrs.module.pihcore.search;

import org.junit.jupiter.api.BeforeEach;
import org.joda.time.DateTime;
import org.junit.jupiter.api.Test;
import org.openmrs.Patient;
import org.openmrs.PersonAddress;
import org.openmrs.PersonAttribute;
import org.openmrs.PersonAttributeType;
import org.openmrs.PersonName;
import org.openmrs.api.PatientService;
import org.openmrs.api.PersonService;
import org.openmrs.api.context.Context;
import org.openmrs.module.pihcore.PihCoreContextSensitiveTest;
import org.openmrs.module.pihcore.metadata.Metadata;
import org.openmrs.module.registrationcore.api.search.FieldMatchResult;
import org.openmrs.module.registrationcore.api.search.FieldMatchStatus;
import org.openmrs.module.registrationcore.api.search.PatientAndMatchQuality;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.Map;
import java.util.HashMap;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.jupiter.api.Assertions.assertTrue;


public class PihPatientSearchAlgorithmTest extends PihCoreContextSensitiveTest {

    @Autowired
    private PihPatientSearchAlgorithm searchAlgorithm;

    @Autowired
    private PersonService personService;

    @Autowired
    private PatientService patientService;

    @Override
    public Properties getRuntimeProperties() {
        Properties p = super.getRuntimeProperties();
        p.setProperty("pih.config", "default");
        return p;
    }

    @BeforeEach
    public void setup() throws Exception {
        executeDataSet("searchTestDataset.xml");
    }

    @Test
    public void shouldReturnEmptyListIfNoNameBirthdateOrGender() {

        Patient patient = new Patient();
        List<PatientAndMatchQuality> results = searchAlgorithm.findSimilarPatients(patient, null, 2.0, 10);
        assertThat(results.size(), is(0));

    }

    @Test
    public void shouldFindNameMatchWithoutBirthdate() {

        Patient patient = new Patient();

        PersonName name = new PersonName();
        patient.addName(name);
        name.setGivenName("Ion");
        name.setFamilyName("Popa");

        patient.setBirthdate(new Date());
        patient.setGender("M");
        Map<String, Object> otherDataPoints = new HashMap<>();
        otherDataPoints.put("birthdateYears", new Integer(52));

        List<PatientAndMatchQuality> results = searchAlgorithm.findSimilarPatients(patient, otherDataPoints, null, 10);

        //one of the two patient matches has no birthdate
        assertThat(results.size(), is(1));
        assertThat(results.get(0).getPatient().getPersonName().getGivenName(), is("Ion"));
        assertThat(results.get(0).getPatient().getPersonName().getFamilyName(), is("Popa"));
        assertThat(results.get(0).getPatient().getBirthdate(), nullValue());
    }
    @Test
    public void shouldFindNamePhoneticsMatch() {

        Patient patient = new Patient();

        PersonName name = new PersonName();
        patient.addName(name);
        name.setGivenName("Jarusz");
        name.setFamilyName("Rapondee");

        patient.setBirthdate(new Date());
        patient.setGender("M");

        List<PatientAndMatchQuality> results = searchAlgorithm.findSimilarPatients(patient, null, null, 10);

        assertThat(results.size(), is(1));
        assertThat(results.get(0).getPatient().getPersonName().getGivenName(), is("Jarus"));
        assertThat(results.get(0).getPatient().getPersonName().getFamilyName(), is("Rapondi"));

    }

    @Test
    public void shouldExcludeVoidedPatients() {

        Patient patientToVoid = patientService.getPatient(13);
        patientService.voidPatient(patientToVoid, "test");
        Context.flushSession();

        Patient patient = new Patient();

        PersonName name = new PersonName();
        patient.addName(name);
        name.setGivenName("Jarusz");
        name.setFamilyName("Rapondee");

        patient.setBirthdate(new Date());
        patient.setGender("M");

        List<PatientAndMatchQuality> results = searchAlgorithm.findSimilarPatients(patient, null, null, 10);

        assertThat(results.size(), is(0));


    }

    @Test
    public void exactAddressFieldMatchShouldIncreaseScore() {

        // first get the base score for this patient
        Patient patient = new Patient();

        PersonName name = new PersonName();
        patient.addName(name);
        name.setGivenName("Jarusz");
        name.setFamilyName("Rapondee");

        patient.setBirthdate(new Date());
        patient.setGender("M");

        List<PatientAndMatchQuality> results = searchAlgorithm.findSimilarPatients(patient, null, null, 10);
        double scoreWithoutAddress = results.get(0).getScore();

        // now add in address and recalculate
        PersonAddress address = new PersonAddress();
        address.setCityVillage("shiseso");
        patient.addAddress(address);

        results = searchAlgorithm.findSimilarPatients(patient, null, null, 10);
        double scoreWithAddress = results.get(0).getScore();

        // in our test config we've weighed cityVillage at 1 point
        assertThat(scoreWithAddress - scoreWithoutAddress, is(1.0));
        assertTrue(results.get(0).getMatchedFields().contains("addresses.cityVillage"));

    }

    @Test
    public void exactPersonAttributeFieldMatchShouldIncreaseScore() {

        // first get the base score for this patient
        Patient patient = new Patient();

        PersonName name = new PersonName();
        patient.addName(name);
        name.setGivenName("Jarusz");
        name.setFamilyName("Rapondee");

        patient.setBirthdate(new Date());
        patient.setGender("M");

        List<PatientAndMatchQuality> results = searchAlgorithm.findSimilarPatients(patient, null, null, 10);
        double scoreWithoutAddress = results.get(0).getScore();

        // now add in address and recalculate
        PersonAttribute attribute = new PersonAttribute();
        attribute.setAttributeType(personService.getPersonAttributeTypeByName("Birthplace"));
        attribute.setValue("boston");
        patient.addAttribute(attribute);

        results = searchAlgorithm.findSimilarPatients(patient, null, null, 10);
        double scoreWithAddress = results.get(0).getScore();

        // in our test config we've weighed birthplace at 1 point
        assertThat(scoreWithAddress - scoreWithoutAddress, is(1.0));
        assertTrue(results.get(0).getMatchedFields().contains("attributes.Birthplace"));

    }

    @Test
    public void shouldMatchPhoneNumberOnNumericsOnly() {

        // first get the base score for this patient
        Patient patient = new Patient();

        PersonName name = new PersonName();
        patient.addName(name);
        name.setGivenName("Jarusz");
        name.setFamilyName("Rapondee");

        patient.setBirthdate(new Date());
        patient.setGender("M");

        List<PatientAndMatchQuality> results = searchAlgorithm.findSimilarPatients(patient, null, null, 10);
        double scoreWithoutAddress = results.get(0).getScore();

        // now add in address and recalculate
        PersonAttribute attribute = new PersonAttribute();
        attribute.setAttributeType(Metadata.getPhoneNumberAttributeType());
        attribute.setValue("6178675309");
        patient.addAttribute(attribute);

        results = searchAlgorithm.findSimilarPatients(patient, null, null, 10);
        double scoreWithAddress = results.get(0).getScore();

        // in our test config we've weighed telephone at 3 points
        assertThat(scoreWithAddress - scoreWithoutAddress, is(3.0));
        assertTrue(results.get(0).getMatchedFields().contains("attributes.Telephone Number"));

    }

    @Test
    public void shouldMatchMothersNameViaPhonetics() {

        // first get the base score for this patient
        Patient patient = new Patient();

        PersonName name = new PersonName();
        patient.addName(name);
        name.setGivenName("Jarusz");
        name.setFamilyName("Rapondee");

        patient.setBirthdate(new Date());
        patient.setGender("M");

        List<PatientAndMatchQuality> results = searchAlgorithm.findSimilarPatients(patient, null, null, 10);
        double scoreWithoutAddress = results.get(0).getScore();

        // now add in address and recalculate
        PersonAttribute attribute = new PersonAttribute();
        attribute.setAttributeType(Metadata.getMothersFirstNameAttributeType());
        attribute.setValue("Mary");
        patient.addAttribute(attribute);

        results = searchAlgorithm.findSimilarPatients(patient, null, null, 10);
        double scoreWithAddress = results.get(0).getScore();

        // in our test config we've weighed mother's first name at 5 points
        assertThat(scoreWithAddress - scoreWithoutAddress, is(5.0));
        assertTrue(results.get(0).getMatchedFields().contains("attributes.First Name of Mother"));

    }

    @Test
    public void shouldDisregardAccentMarksWhenMakingMatch() {

        Patient patient = new Patient();

        PersonName name = new PersonName();
        patient.addName(name);
        // this is an exact name match against the database
        name.setGivenName("Jarus");
        name.setFamilyName("Rapondi");

        patient.setBirthdate(new Date());
        patient.setGender("M");

        List<PatientAndMatchQuality> results = searchAlgorithm.findSimilarPatients(patient, null, null, 10);
        double score = results.get(0).getScore();

        // now we add some accent marks
        name.setGivenName("Járùs");
        name.setFamilyName("Rápóndi");

        // if we do a search again, the score should be the same, assuming the accent marks were ignored
        results = searchAlgorithm.findSimilarPatients(patient, null, null, 10);
        assertThat(results.get(0).getScore(), is(score));


    }

    @Test
    public void shouldReportFieldResultsForEveryWellKnownField() {

        PatientAndMatchQuality result = searchByName("Jarus", "Rapondi").get(0);

        for (String field : new String[] { FieldMatchResult.NAME, FieldMatchResult.GENDER, FieldMatchResult.BIRTHDATE,
                FieldMatchResult.MOTHERS_FIRST_NAME, FieldMatchResult.ADDRESS, FieldMatchResult.PHONE }) {
            assertThat(field, result.getFieldResult(field) != null, is(true));
        }
        assertThat(result.getFieldResults().size(), is(6));
    }

    @Test
    public void shouldReportExactNameAsMatchAndPhoneticOnlyNameAsSimilar() {
        assertThat(fieldStatus(searchByName("Jarus", "Rapondi").get(0), FieldMatchResult.NAME), is(FieldMatchStatus.MATCH));
        assertThat(fieldStatus(searchByName("Járùs", "Rápóndi").get(0), FieldMatchResult.NAME), is(FieldMatchStatus.MATCH));
        assertThat(fieldStatus(searchByName("Jarusz", "Rapondee").get(0), FieldMatchResult.NAME), is(FieldMatchStatus.SIMILAR));
    }

    @Test
    public void shouldReportGenderMatchAndDifference() {
        Patient patient = patientNamed("Jarus", "Rapondi");
        assertThat(fieldStatus(search(patient), FieldMatchResult.GENDER), is(FieldMatchStatus.MATCH));

        patient.setGender("F");
        PatientAndMatchQuality result = search(patient);
        assertThat(fieldStatus(result, FieldMatchResult.GENDER), is(FieldMatchStatus.DIFFERENT));
        assertThat(result.getFieldResult(FieldMatchResult.GENDER).getScore(), is(-8.0));
    }

    @Test
    public void shouldReportBirthdateStatuses() {
        // the existing patient's birthdate (1974-11-02) is estimated, so even the same day is only "similar"
        Patient patient = patientNamed("Jarus", "Rapondi");
        patient.setBirthdate(new DateTime(1974, 11, 2, 0, 0).toDate());
        assertThat(fieldStatus(search(patient), FieldMatchResult.BIRTHDATE), is(FieldMatchStatus.SIMILAR));

        patient.setBirthdate(new DateTime(1990, 1, 1, 0, 0).toDate());
        assertThat(fieldStatus(search(patient), FieldMatchResult.BIRTHDATE), is(FieldMatchStatus.DIFFERENT));
    }

    @Test
    public void shouldReportPhoneMatchDifferentAndMissing() {
        Patient patient = patientNamed("Jarus", "Rapondi");
        PatientAndMatchQuality result = search(patient);
        assertThat(fieldStatus(result, FieldMatchResult.PHONE), is(FieldMatchStatus.MISSING));
        assertThat(result.getFieldResult(FieldMatchResult.PHONE).getScore(), is(0.0));

        patient.addAttribute(attribute(Metadata.getPhoneNumberAttributeType(), "617-867-5309"));
        result = search(patient);
        assertThat(fieldStatus(result, FieldMatchResult.PHONE), is(FieldMatchStatus.MATCH));
        assertThat(result.getFieldResult(FieldMatchResult.PHONE).getScore(), is(3.0));

        patient.getAttribute(Metadata.getPhoneNumberAttributeType()).setValue("617-555-0000");
        result = search(patient);
        assertThat(fieldStatus(result, FieldMatchResult.PHONE), is(FieldMatchStatus.DIFFERENT));
        assertThat(result.getFieldResult(FieldMatchResult.PHONE).getScore(), is(0.0));
    }

    @Test
    public void shouldReportMothersFirstNameStatuses() {
        Patient patient = patientNamed("Jarus", "Rapondi");
        assertThat(fieldStatus(search(patient), FieldMatchResult.MOTHERS_FIRST_NAME), is(FieldMatchStatus.MISSING));

        // the existing patient's mother is "Marie"
        patient.addAttribute(attribute(Metadata.getMothersFirstNameAttributeType(), "Marie"));
        assertThat(fieldStatus(search(patient), FieldMatchResult.MOTHERS_FIRST_NAME), is(FieldMatchStatus.MATCH));

        patient.getAttribute(Metadata.getMothersFirstNameAttributeType()).setValue("Mary");
        assertThat(fieldStatus(search(patient), FieldMatchResult.MOTHERS_FIRST_NAME), is(FieldMatchStatus.SIMILAR));

        patient.getAttribute(Metadata.getMothersFirstNameAttributeType()).setValue("Zebedee");
        assertThat(fieldStatus(search(patient), FieldMatchResult.MOTHERS_FIRST_NAME), is(FieldMatchStatus.DIFFERENT));
    }

    @Test
    public void shouldReportAddressStatuses() {
        Patient patient = patientNamed("Jarus", "Rapondi");
        assertThat(fieldStatus(search(patient), FieldMatchResult.ADDRESS), is(FieldMatchStatus.MISSING));

        PersonAddress address = new PersonAddress();
        address.setCityVillage("Shiseso");
        patient.addAddress(address);
        PatientAndMatchQuality result = search(patient);
        assertThat(fieldStatus(result, FieldMatchResult.ADDRESS), is(FieldMatchStatus.MATCH));
        assertThat(result.getFieldResult(FieldMatchResult.ADDRESS).getScore(), is(1.0));

        address.setCityVillage("Elsewhere");
        assertThat(fieldStatus(search(patient), FieldMatchResult.ADDRESS), is(FieldMatchStatus.DIFFERENT));
    }

    private List<PatientAndMatchQuality> searchByName(String givenName, String familyName) {
        return searchAlgorithm.findSimilarPatients(patientNamed(givenName, familyName), null, null, 10);
    }

    private PatientAndMatchQuality search(Patient patient) {
        List<PatientAndMatchQuality> results = searchAlgorithm.findSimilarPatients(patient, null, null, 10);
        assertThat(results.size(), is(1));
        return results.get(0);
    }

    private Patient patientNamed(String givenName, String familyName) {
        Patient patient = new Patient();
        PersonName name = new PersonName();
        name.setGivenName(givenName);
        name.setFamilyName(familyName);
        patient.addName(name);
        patient.setBirthdate(new Date());
        patient.setGender("M");
        return patient;
    }

    private PersonAttribute attribute(PersonAttributeType type, String value) {
        PersonAttribute attribute = new PersonAttribute();
        attribute.setAttributeType(type);
        attribute.setValue(value);
        return attribute;
    }

    private FieldMatchStatus fieldStatus(PatientAndMatchQuality result, String field) {
        return result.getFieldResult(field).getStatus();
    }

}
