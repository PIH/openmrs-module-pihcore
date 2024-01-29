package org.openmrs.module.pihcore.htmlformentry.action;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import org.openmrs.Concept;
import org.openmrs.Encounter;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.module.htmlformentry.FormEntrySession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.module.queue.api.QueueServicesWrapper;
import org.openmrs.module.queue.api.search.QueueSearchCriteria;
import org.openmrs.module.queue.model.Queue;
import org.openmrs.module.queue.model.QueueEntry;

public class AddPatientToQueue implements org.openmrs.module.htmlformentry.CustomFormSubmissionAction {

    private static final String ADD_TO_QUEUE_CONCEPT_CODE_PIH = "1272";
    protected Log log = LogFactory.getLog(getClass());

    @Override
    public void applyAction(FormEntrySession formEntrySession) {

        Concept addToQueueConcept = Context.getConceptService().getConceptByMapping(ADD_TO_QUEUE_CONCEPT_CODE_PIH, "PIH");

        if (addToQueueConcept == null) {
            log.error("Concept PIH: " + ADD_TO_QUEUE_CONCEPT_CODE_PIH + " cannot be found");
            return;
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
                Queue queue = null ;
                QueueEntry queueEntry = null;
                if ( !queues.isEmpty() ) {
                    queue = queues.get(0);
                }
                if (queue != null ) {
                    List<Concept> allowedStatuses = queueServicesWrapper.getAllowedStatuses(queue);
                    if (allowedStatuses.isEmpty()) {
                        log.error("Queue " + queue.getName() + " has not valid allowed statuses");
                        return;
                    }
                    List<Concept> allowedPriorities = queueServicesWrapper.getAllowedPriorities(queue);
                    if (allowedPriorities.isEmpty()) {
                        log.error("Queue " + queue.getName() + " has not valid allowed priorities");
                        return;
                    }
                    queueEntry = new QueueEntry();
                    queueEntry.setQueue(queue);
                    queueEntry.setPatient(patient);
                    // The first entry in the allowedPriorities should always be Normal
                    queueEntry.setPriority(allowedPriorities.get(0));
                    // The first entry in the allowedStatuses should always be Waiting
                    queueEntry.setStatus(allowedStatuses.get(0));
                    queueEntry.setStartedAt(new Date());
                    queueServicesWrapper.getQueueEntryService().saveQueueEntry(queueEntry);
                }
            }
        }

    }
}
