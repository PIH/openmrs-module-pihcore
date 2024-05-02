package org.openmrs.module.pihcore.merge;

import org.apache.commons.lang.time.DateUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openmrs.Patient;
import org.openmrs.Visit;
import org.openmrs.api.ConceptService;
import org.openmrs.api.EncounterService;
import org.openmrs.api.PersonService;
import org.openmrs.contrib.testdata.TestDataManager;
import org.openmrs.module.emrapi.adt.AdtService;
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

public class PihVisitMergeActionsTest extends PihCoreContextSensitiveTest {

    @Autowired PihVisitMergeActions pihVisitMergeActions;
    @Autowired AdtService adtService;
    @Autowired PersonService personService;
    @Autowired EncounterService encounterService;
    @Autowired ConceptService conceptService;
    @Autowired @Qualifier("queue.QueueService") QueueService queueService;
    @Autowired @Qualifier("queue.QueueEntryService") QueueEntryService queueEntryService;
    @Autowired TestDataManager tdm;

    @BeforeEach
    public void setup() {
        loadFromInitializer(Domain.LOCATIONS, "locations-base.csv");
        executeDataSet("queueTestDataset.xml");
        adtService.addVisitMergeAction(pihVisitMergeActions);
    }

    @AfterEach
    public void teardown() {
        adtService.removeVisitMergeAction(pihVisitMergeActions);
    }

    @Test
    public void shouldMoveQueueEntryWithinVisit() {

        Date today = new Date();
        Patient p1 = tdm.randomPatient().save();
        Patient p2 = tdm.randomPatient().save();
        Visit v1 = tdm.visit().visitType(1).started(DateUtils.addDays(today, -10)).stopped(DateUtils.addDays(today, -9)).patient(p1).save();
        Visit v2 = tdm.visit().visitType(1).started(DateUtils.addDays(today, -2)).stopped(DateUtils.addDays(today, -1)).patient(p2).save();

        QueueEntry qe = new QueueEntry();
        qe.setQueue(queueService.getQueueById(1).get());
        qe.setPatient(p2);
        qe.setVisit(v2);
        qe.setPriority(conceptService.getConcept(1001));
        qe.setStatus(conceptService.getConcept(3001));
        qe.setStartedAt(v2.getStartDatetime());
        qe.setEndedAt(v2.getStopDatetime());
        queueEntryService.saveQueueEntry(qe);

        adtService.mergeVisits(v1, v2);

        assertThat(qe.getPatient(), equalTo(p1));
        assertThat(qe.getVisit(), equalTo(v1));
    }
}
