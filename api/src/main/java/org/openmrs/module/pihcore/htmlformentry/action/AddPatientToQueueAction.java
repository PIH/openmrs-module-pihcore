package org.openmrs.module.pihcore.htmlformentry.action;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.openmrs.Concept;
import org.openmrs.Encounter;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.module.htmlformentry.CustomFormSubmissionAction;
import org.openmrs.module.htmlformentry.FormEntryContext;
import org.openmrs.module.htmlformentry.FormEntrySession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.module.queue.api.QueueServicesWrapper;
import org.openmrs.module.queue.api.search.QueueEntrySearchCriteria;
import org.openmrs.module.queue.api.search.QueueSearchCriteria;
import org.openmrs.module.queue.model.Queue;
import org.openmrs.module.queue.model.QueueEntry;
import org.openmrs.module.queue.utils.QueueUtils;

/**
 * Custom action that adds a patient to a queue, if the encounter contains a top level obs with the
 * following structure
 * <obs conceptId="CIEL:1272"  answerConceptId="PIH:14976"  />
 * where answerConceptId points to the service concept associated with a queue.
 * It assumes that the service concept maps to one and only one queue.
 */
public class AddPatientToQueueAction implements CustomFormSubmissionAction {

    private static final String ADD_TO_QUEUE_CONCEPT_CODE_PIH = "1272";
    protected Log log = LogFactory.getLog(getClass());

    @Override
    public void applyAction(FormEntrySession formEntrySession) {

        if (formEntrySession.getContext().getMode().equals(FormEntryContext.Mode.ENTER)) {
            // we execute this post submission action only in the ENTER mode
            Concept addToQueueConcept = Context.getConceptService().getConceptByMapping(ADD_TO_QUEUE_CONCEPT_CODE_PIH, "PIH");

            if (addToQueueConcept == null) {
                throw new RuntimeException("AddPatientToQueueAction not executing as Concept PIH: " + ADD_TO_QUEUE_CONCEPT_CODE_PIH + " cannot be found");
            }
            Patient patient = formEntrySession.getPatient();
            Encounter encounter = formEntrySession.getEncounter();

            for (Obs candidate : encounter.getObsAtTopLevel(false)) {
                if (candidate.getConcept().equals(addToQueueConcept)) {
                    // The coded answer represents the Concept associated with the queue service
                    Concept serviceQueue = candidate.getValueCoded();
                    QueueSearchCriteria criteria = new QueueSearchCriteria();
                    criteria.setServices(Arrays.asList(serviceQueue));
                    QueueServicesWrapper queueServicesWrapper = Context.getRegisteredComponents(QueueServicesWrapper.class).get(0);
                    List<Queue> queues = queueServicesWrapper.getQueueService().getQueues(criteria);
                    Queue queue = null;
                    QueueEntry queueEntry = null;
                    if (queues.isEmpty()) {
                        throw new RuntimeException("AddPatientToQueueAction not executing as no queue with service concept " + serviceQueue.getName() + " could be found ");
                    }
                    if (queues.size() > 1) {
                        throw new RuntimeException("AddPatientToQueueAction not executing as there are more than one queue with service concept " + serviceQueue.getName());
                    }
                    queue = queues.get(0);

                    queueEntry = new QueueEntry();
                    queueEntry.setQueue(queue);
                    queueEntry.setPatient(patient);

                    List<Concept> allowedStatuses = queueServicesWrapper.getAllowedStatuses(queue);
                    if (!allowedStatuses.isEmpty()) {
                        queueEntry.setStatus(allowedStatuses.get(0));
                    }

                    List<Concept> allowedPriorities = queueServicesWrapper.getAllowedPriorities(queue);
                    if (!allowedPriorities.isEmpty()) {
                        queueEntry.setPriority(allowedPriorities.get(0));
                    }
                    queueEntry.setStartedAt(encounter.getEncounterDatetime());
                    queueEntry.setVisit(encounter.getVisit());

                    QueueEntrySearchCriteria searchCriteria = new QueueEntrySearchCriteria();
                    searchCriteria.setPatient(patient);
                    searchCriteria.setQueues(Collections.singletonList(queue));
                    searchCriteria.setIsEnded(null);
                    List<QueueEntry> queueEntries = queueServicesWrapper.getQueueEntryService().getQueueEntries(searchCriteria);
                    boolean isPatientAlreadyOnTheQueue = false;
                    for (QueueEntry entry : queueEntries) {
                        if (QueueUtils.datesOverlap(entry.getStartedAt(), entry.getEndedAt(), queueEntry.getStartedAt(), queueEntry.getEndedAt())) {
                            isPatientAlreadyOnTheQueue = true;
                            break;
                        }
                    }
                    if (!isPatientAlreadyOnTheQueue) {
                        try {
                            queueServicesWrapper.getQueueEntryService().saveQueueEntry(queueEntry);
                        } catch (Exception e) {
                            throw new RuntimeException("AddPatientToQueueAction failed to add patient to queue", e);
                        }
                    }
                }
            }
        }
    }
}
