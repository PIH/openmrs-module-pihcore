package org.openmrs.module.pihcore.action;


import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.openmrs.Concept;
import org.openmrs.Encounter;
import org.openmrs.Location;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.Visit;
import org.openmrs.module.queue.api.QueueEntryService;
import org.openmrs.module.queue.api.dao.QueueDao;
import org.openmrs.module.queue.api.dao.QueueEntryDao;
import org.openmrs.module.queue.api.impl.QueueEntryServiceImpl;
import org.openmrs.module.queue.api.impl.QueueServiceImpl;
import org.openmrs.module.queue.api.search.QueueEntrySearchCriteria;
import org.openmrs.module.queue.api.sort.BasicPrioritySortWeightGenerator;
import org.openmrs.module.queue.model.Queue;
import org.openmrs.api.ConceptService;
import org.openmrs.api.context.Context;
import org.openmrs.module.htmlformentry.FormEntryContext;
import org.openmrs.module.htmlformentry.FormEntrySession;
import org.openmrs.module.htmlformentry.FormSubmissionActions;
import org.openmrs.module.pihcore.htmlformentry.action.AddPatientToQueueAction;
import org.openmrs.module.queue.api.QueueService;
import org.openmrs.module.queue.api.QueueServicesWrapper;
import org.openmrs.module.queue.api.search.QueueSearchCriteria;
import org.openmrs.module.queue.model.QueueEntry;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

@RunWith(PowerMockRunner.class)
@PrepareForTest(Context.class)
@PowerMockIgnore({"javax.management.*", "org.apache.*", "org.slf4j.*"})
public class AddPatientToQueueActionTest {

    private static final String ADD_TO_QUEUE_CONCEPT_CODE_PIH = "1272";
    private FormEntrySession mockFormEntrySession;
    private FormEntryContext mockFormEntryContext;
    private FormSubmissionActions mockFormSubmissionActions;
    private AddPatientToQueueAction addPatientToQueueAction;

    private ConceptService conceptService;
    private Concept addToQueueConcept;
    private Concept mcoeTriageService;
    private QueueServicesWrapper wrapper;
    private QueueService queueServiceImpl;
    private QueueEntryService queueEntryServiceImpl;
    private QueueDao mockDao;
    private QueueEntryDao mockQueueEntryDao;

    @Captor
    ArgumentCaptor<QueueEntry> queueEntryArgumentCaptor;

    @Before
    public void setUp()  {

        mockFormEntrySession = mock(FormEntrySession.class);
        mockFormEntryContext = mock(FormEntryContext.class);
        mockFormSubmissionActions = mock(FormSubmissionActions.class);

        addPatientToQueueAction = new AddPatientToQueueAction();
        when(mockFormEntrySession.getSubmissionActions()).thenReturn(mockFormSubmissionActions);
        when(mockFormEntrySession.getContext()).thenReturn(mockFormEntryContext);
        when(mockFormEntryContext.getMode()).thenReturn(FormEntryContext.Mode.ENTER);
        conceptService = mock(ConceptService.class);
        addToQueueConcept = new Concept();
        mcoeTriageService = new Concept();
        mockStatic(Context.class);
        when(Context.getConceptService()).thenReturn(conceptService);
        when(conceptService.getConceptByMapping(ADD_TO_QUEUE_CONCEPT_CODE_PIH, "PIH")).thenReturn(addToQueueConcept);
        wrapper = mock(QueueServicesWrapper.class);

        when(Context.getRegisteredComponents(QueueServicesWrapper.class)).thenReturn(Arrays.asList(wrapper));
        mockDao = mock(QueueDao.class);
        queueServiceImpl = new QueueServiceImpl();
        ((QueueServiceImpl) queueServiceImpl).setDao(mockDao);
        when(wrapper.getQueueService()).thenReturn(queueServiceImpl);

        mockQueueEntryDao = mock(QueueEntryDao.class);
        queueEntryServiceImpl = new QueueEntryServiceImpl();
        queueEntryServiceImpl.setSortWeightGenerator(new BasicPrioritySortWeightGenerator(wrapper));
        ((QueueEntryServiceImpl) queueEntryServiceImpl).setDao(mockQueueEntryDao);
        when(wrapper.getQueueEntryService()).thenReturn(queueEntryServiceImpl);
    }

    @Test
    public void applyAction_shouldAddPatientToQueue() {
        Patient patient = new Patient();
        Encounter encounter = new Encounter();
        encounter.setPatient(patient);
        encounter.setEncounterDatetime(new Date());
        Location location = new Location();
        encounter.setLocation(location);
        Obs referToMCOE = new Obs(patient, addToQueueConcept, encounter.getEncounterDatetime(), location);
        referToMCOE.setValueCoded(mcoeTriageService);
        encounter.addObs(referToMCOE);
        Visit visit = new Visit();
        visit.addEncounter(encounter);
        visit.setPatient(patient);
        Queue queue = new Queue();
        queue.setService(mcoeTriageService);
        QueueSearchCriteria searchCriteria = new QueueSearchCriteria();
        searchCriteria.setServices(Arrays.asList(mcoeTriageService));
        QueueEntry queueEntry = new QueueEntry();
        queueEntry.setPatient(patient);
        queueEntry.setQueue(queue);
        queueEntry.setStartedAt(encounter.getEncounterDatetime());

        when(mockFormEntrySession.getPatient()).thenReturn(patient);
        when(mockFormEntrySession.getEncounter()).thenReturn(encounter);
        when(mockFormEntryContext.getVisit()).thenReturn(visit);
        when(mockDao.getQueues(searchCriteria)).thenReturn(Arrays.asList(queue));
        when(mockQueueEntryDao.createOrUpdate(queueEntry)).thenReturn(queueEntry);

        addPatientToQueueAction.applyAction(mockFormEntrySession);
        // verify that a new QueueEntry is added to the queue
        verify(mockQueueEntryDao).createOrUpdate(any());
        verify(mockQueueEntryDao).createOrUpdate(queueEntryArgumentCaptor.capture());
        QueueEntry capturedValue = queueEntryArgumentCaptor.getValue();
        assertThat(capturedValue.getQueue(), is(queue));
        assertThat(capturedValue.getPatient(), is(patient));
        assertThat(capturedValue.getStartedAt(), is(encounter.getEncounterDatetime()));
    }

    @Test
    public void applyAction_shouldNotAddPatientToQueueIfPatientIsAlreadyOnTheQueue() {
        Patient patient = new Patient();
        Encounter encounter = new Encounter();
        encounter.setPatient(patient);
        encounter.setEncounterDatetime(new Date());
        Location location = new Location();
        encounter.setLocation(location);
        Obs referToMCOE = new Obs(patient, addToQueueConcept, encounter.getEncounterDatetime(), location);
        referToMCOE.setValueCoded(mcoeTriageService);
        encounter.addObs(referToMCOE);
        Visit visit = new Visit();
        visit.addEncounter(encounter);
        visit.setPatient(patient);
        Queue queue = new Queue();
        queue.setService(mcoeTriageService);
        QueueSearchCriteria searchCriteria = new QueueSearchCriteria();
        searchCriteria.setServices(Arrays.asList(mcoeTriageService));
        QueueEntry queueEntry = new QueueEntry();
        queueEntry.setPatient(patient);
        queueEntry.setQueue(queue);
        queueEntry.setStartedAt(encounter.getEncounterDatetime());

        when(mockFormEntrySession.getPatient()).thenReturn(patient);
        when(mockFormEntrySession.getEncounter()).thenReturn(encounter);
        when(mockFormEntryContext.getVisit()).thenReturn(visit);
        when(mockDao.getQueues(searchCriteria)).thenReturn(Arrays.asList(queue));
        when(mockQueueEntryDao.createOrUpdate(queueEntry)).thenReturn(queueEntry);

        QueueEntrySearchCriteria qeCriteria = new QueueEntrySearchCriteria();
        qeCriteria.setPatient(patient);
        qeCriteria.setQueues(Collections.singletonList(queue));
        qeCriteria.setIsEnded(null);
        when(mockQueueEntryDao.getQueueEntries(qeCriteria)).thenReturn(Arrays.asList(queueEntry));

        addPatientToQueueAction.applyAction(mockFormEntrySession);
        // a new QueueEntry is never added/saved to the db
        verify(mockQueueEntryDao, never()).createOrUpdate(any());

    }
}
