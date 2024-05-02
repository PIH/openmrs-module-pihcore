package org.openmrs.module.pihcore.merge;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openmrs.Encounter;
import org.openmrs.Patient;
import org.openmrs.PersonAttributeType;
import org.openmrs.User;
import org.openmrs.Visit;
import org.openmrs.api.ConceptService;
import org.openmrs.api.EncounterService;
import org.openmrs.api.PersonService;
import org.openmrs.api.context.Context;
import org.openmrs.contrib.testdata.TestDataManager;
import org.openmrs.module.initializer.Domain;
import org.openmrs.module.pihcore.PihCoreContextSensitiveTest;
import org.openmrs.module.queue.api.QueueEntryService;
import org.openmrs.module.queue.api.QueueService;
import org.openmrs.module.queue.model.QueueEntry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.Date;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PihPatientMergeActionsTest extends PihCoreContextSensitiveTest {

    @Autowired PihPatientMergeActions pihPatientMergeActions;
    @Autowired PersonService personService;
    @Autowired EncounterService encounterService;
    @Autowired ConceptService conceptService;
    @Autowired @Qualifier("queue.QueueService") QueueService queueService;
    @Autowired @Qualifier("queue.QueueEntryService") QueueEntryService queueEntryService;
    @Autowired TestDataManager tdm;

    PersonAttributeType phoneNumber;

    PersonAttributeType mothersName;

    User user;

    Patient preferred;

    Patient nonPreferred;

    @BeforeEach
    public void setup() {
        loadFromInitializer(Domain.LOCATIONS, "locations-base.csv");
        loadFromInitializer(Domain.LOCATIONS, "locations-site-mirebalais.csv");
        loadFromInitializer(Domain.PERSON_ATTRIBUTE_TYPES, "personAttributeTypes.csv");
        loadFromInitializer(Domain.PATIENT_IDENTIFIER_TYPES, "zlIdentifierTypes.csv");
        loadFromInitializer(Domain.ENCOUNTER_TYPES, "encounterTypes.csv");
        executeDataSet("queueTestDataset.xml");

        phoneNumber = personService.getPersonAttributeTypeByName("Telephone Number");
        mothersName = personService.getPersonAttributeTypeByName("First Name of Mother");
        user = Context.getUserService().getUser(1);
    }

    @Test
    public void shouldRemoveNonPreferredPhoneNumberAndMothersNameIfPresentOnPreferredPatient() {

        preferred = tdm.randomPatient()
                .personAttribute(phoneNumber, "preferred")
                .personAttribute(mothersName, "preferred")
                .save();

        nonPreferred = tdm.randomPatient()
                .personAttribute(phoneNumber, "non-preferred")
                .personAttribute(mothersName, "non-preferred")
                .save();

        //sanity check
        assertThat(preferred.getActiveAttributes().size(), is(2));
        assertThat(nonPreferred.getActiveAttributes().size(), is(2));

        pihPatientMergeActions.beforeMergingPatients(preferred, nonPreferred);

        assertThat(preferred.getActiveAttributes().size(), is(2));
        assertThat(nonPreferred.getActiveAttributes().size(), is(0));
    }

    @Test
    public void shouldNotRemoveNonPreferredPhoneNumberAndMothersNameIfNotPresentOnPreferredPatient() {

        preferred = tdm.randomPatient()
                .save();

        nonPreferred = tdm.randomPatient()
                .personAttribute(phoneNumber, "non-preferred")
                .personAttribute(mothersName, "non-preferred")
                .save();

        //sanity check
        assertThat(preferred.getActiveAttributes().size(), is(0));
        assertThat(nonPreferred.getActiveAttributes().size(), is(2));

        pihPatientMergeActions.beforeMergingPatients(preferred, nonPreferred);

        assertThat(preferred.getActiveAttributes().size(), is(0));
        assertThat(nonPreferred.getActiveAttributes().size(), is(2));
    }

    @Test
    public void shouldRemoveNonPreferredPhoneNumberButNotMothersName() {

        preferred = tdm.randomPatient()
                .personAttribute(phoneNumber, "preferred")
                .save();

        nonPreferred = tdm.randomPatient()
                .personAttribute(phoneNumber, "non-preferred")
                .personAttribute(mothersName, "non-preferred")
                .save();

        //sanity check
        assertThat(preferred.getActiveAttributes().size(), is(1));
        assertThat(nonPreferred.getActiveAttributes().size(), is(2));

        pihPatientMergeActions.beforeMergingPatients(preferred, nonPreferred);

        assertThat(preferred.getActiveAttributes().size(), is(1));
        assertThat(nonPreferred.getActiveAttributes().size(), is(1));
        assertThat(nonPreferred.getActiveAttributes().get(0).getValue(), is("non-preferred"));
    }

    @Test
    public void shouldNotFailIfNonPreferredPatientDoesNotHaveAttributes() {

        preferred = tdm.randomPatient()
                .personAttribute(phoneNumber, "preferred")
                .personAttribute(mothersName, "preferred")
                .save();

        nonPreferred = tdm.randomPatient()
                .save();

        pihPatientMergeActions.beforeMergingPatients(preferred, nonPreferred);

        assertThat(preferred.getAttributes().size(), is(2));
        assertThat(nonPreferred.getAttributes().size(), is(0));
    }

    @Test
    public void shouldVoidMostRecentRegistrationEncountersOnNonPreferredPatientIfMoreRecentThanPreferred() {

        preferred = tdm.randomPatient().save();
        nonPreferred = tdm.randomPatient().save();

        Encounter preferredEncounter1 = tdm.randomEncounter().patient(preferred).encounterDatetime("2012-09-10").encounterType(getRegistrationEncounterType()).save();
        Encounter preferredEncounter2 = tdm.randomEncounter().patient(preferred).encounterDatetime("2012-10-10").encounterType(getRegistrationEncounterType()).save();

        Encounter nonPreferredEncounter1 = tdm.randomEncounter().patient(nonPreferred).encounterDatetime("2012-11-10").encounterType(getRegistrationEncounterType()).save();
        Encounter nonPreferredEncounter2 = tdm.randomEncounter().patient(nonPreferred).encounterDatetime("2012-12-10").encounterType(getRegistrationEncounterType()).save();

        pihPatientMergeActions.beforeMergingPatients(preferred, nonPreferred);

        assertFalse(encounterService.getEncounter(preferredEncounter1.getEncounterId()).getVoided());
        assertFalse(encounterService.getEncounter(preferredEncounter2.getEncounterId()).getVoided());
        assertTrue(encounterService.getEncounter(nonPreferredEncounter1.getEncounterId()).getVoided());
        assertTrue(encounterService.getEncounter(nonPreferredEncounter2.getEncounterId()).getVoided());
    }

    @Test
    public void shouldNotVoidMostRecentRegistrationEncounterOnNonPreferredPatientIfNotMoreRecentThanPreferred() {

        preferred = tdm.randomPatient().save();
        nonPreferred = tdm.randomPatient().save();

        Encounter preferredEncounter1 = tdm.randomEncounter().patient(preferred).encounterDatetime("2012-10-10").encounterType(getRegistrationEncounterType()).save();
        Encounter preferredEncounter2 = tdm.randomEncounter().patient(preferred).encounterDatetime("2012-11-10").encounterType(getRegistrationEncounterType()).save();

        Encounter nonPreferredEncounter1 = tdm.randomEncounter().patient(nonPreferred).encounterDatetime("2012-1-10").encounterType(getRegistrationEncounterType()).save();
        Encounter nonPreferredEncounter2 = tdm.randomEncounter().patient(nonPreferred).encounterDatetime("2012-2-10").encounterType(getRegistrationEncounterType()).save();

        pihPatientMergeActions.beforeMergingPatients(preferred, nonPreferred);

        assertFalse(encounterService.getEncounter(preferredEncounter1.getEncounterId()).getVoided());
        assertFalse(encounterService.getEncounter(preferredEncounter2.getEncounterId()).getVoided());
        assertFalse(encounterService.getEncounter(nonPreferredEncounter1.getEncounterId()).getVoided());
        assertFalse(encounterService.getEncounter(nonPreferredEncounter2.getEncounterId()).getVoided());
    }

    @Test
    public void shouldMoveQueueEntryIfNotAssociatedWithAVisit() {

        Date today = new Date();
        preferred = tdm.randomPatient().save();
        nonPreferred = tdm.randomPatient().save();

        QueueEntry qe1 = new QueueEntry();
        qe1.setQueue(queueService.getQueueById(1).get());
        qe1.setPatient(nonPreferred);
        qe1.setPriority(conceptService.getConcept(1001));
        qe1.setStatus(conceptService.getConcept(3001));
        qe1.setStartedAt(today);
        queueEntryService.saveQueueEntry(qe1);

        pihPatientMergeActions.beforeMergingPatients(preferred, nonPreferred);

        qe1 = queueEntryService.getQueueEntryById(qe1.getQueueEntryId()).get();
        assertThat(qe1.getPatient(), equalTo(preferred));
    }

    @Test
    public void shouldNotMoveQueueEntryIfAssociatedWithAVisit() {

        Date today = new Date();
        preferred = tdm.randomPatient().save();
        nonPreferred = tdm.randomPatient().save();
        Visit visit = tdm.visit().visitType(1).started(today).patient(nonPreferred).save();

        QueueEntry qe1 = new QueueEntry();
        qe1.setQueue(queueService.getQueueById(1).get());
        qe1.setPatient(nonPreferred);
        qe1.setVisit(visit);
        qe1.setPriority(conceptService.getConcept(1001));
        qe1.setStatus(conceptService.getConcept(3001));
        qe1.setStartedAt(today);
        queueEntryService.saveQueueEntry(qe1);

        pihPatientMergeActions.beforeMergingPatients(preferred, nonPreferred);

        qe1 = queueEntryService.getQueueEntryById(qe1.getQueueEntryId()).get();
        assertThat(qe1.getPatient(), equalTo(nonPreferred));
    }

    @Test
    public void shouldNotFailIfNoEncounters() {
        preferred = tdm.randomPatient().save();
        nonPreferred = tdm.randomPatient().save();
        pihPatientMergeActions.beforeMergingPatients(preferred, nonPreferred);
        // just make sure no NPE
    }

}
