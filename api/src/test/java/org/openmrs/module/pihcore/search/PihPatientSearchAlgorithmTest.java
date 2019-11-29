package org.openmrs.module.pihcore.search;

import org.junit.Before;
import org.junit.Test;
import org.openmrs.Patient;
import org.openmrs.PersonAddress;
import org.openmrs.PersonAttribute;
import org.openmrs.PersonName;
import org.openmrs.api.PatientService;
import org.openmrs.api.PersonService;
import org.openmrs.api.context.Context;
import org.openmrs.module.haiticore.metadata.HaitiPersonAttributeTypes;
import org.openmrs.module.registrationcore.api.search.PatientAndMatchQuality;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.List;
import java.util.Properties;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;


public class PihPatientSearchAlgorithmTest extends BaseModuleContextSensitiveTest {

    @Autowired
    private PihPatientSearchAlgorithm searchAlgorithm;

    @Autowired
    private PersonService personService;

    @Autowired
    private PatientService patientService;

    @Override
    public Properties getRuntimeProperties() {
        Properties p = super.getRuntimeProperties();
        p.setProperty("pih.config", "pihcore");
        return p;
    }

    @Before
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
        attribute.setAttributeType(personService.getPersonAttributeTypeByName(HaitiPersonAttributeTypes.TELEPHONE_NUMBER.name()));
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
        attribute.setAttributeType(personService.getPersonAttributeTypeByName(HaitiPersonAttributeTypes.MOTHERS_FIRST_NAME.name()));
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

}
