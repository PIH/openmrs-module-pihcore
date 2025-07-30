package org.openmrs.module.pihcore.task;

import org.apache.commons.lang.time.StopWatch;
import org.openmrs.Concept;
import org.openmrs.api.context.Context;
import org.openmrs.module.queue.api.QueueServicesWrapper;
import org.openmrs.module.queue.api.search.QueueEntrySearchCriteria;
import org.openmrs.module.queue.api.search.QueueSearchCriteria;
import org.openmrs.module.queue.model.Queue;
import org.openmrs.module.queue.model.QueueEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class PihRemovePatientsFromMCOEQueue implements Runnable {

    private static final Long QUEUE_TIMEOUT_MINUTES = 720L;
    private static final String MCOE_TRIAGE_QUEUE_CONCEPT_CODE_PIH = "14976";

    private final Logger log = LoggerFactory.getLogger(getClass());

    private static boolean isExecuting = false;

    @Override
    public void run() {

        if (isExecuting) {
            log.info("Not executing " + getClass() + " because another instance is already executing");
            return;
        }

        isExecuting = true;

        try {
            log.info("Executing " + getClass());
            StopWatch stopWatch = new StopWatch();
            stopWatch.start();

            Queue queue = getQueue();
            if (queue == null) return;

            int removedPatientsCount = removeTimedOutPatients(queue);

            stopWatch.stop();
            log.info("Execution completed in {} - {} patients removed", stopWatch, removedPatientsCount);
        } catch (Exception e) {
            log.error("Error executing " + getClass(), e);
        } finally {
            isExecuting = false;
        }
    }

    private Queue getQueue() throws Exception {
        Concept MCOE_TRIAGE_QUEUE_CONCEPT = Context.getConceptService().getConceptByMapping(MCOE_TRIAGE_QUEUE_CONCEPT_CODE_PIH, "PIH");
        QueueSearchCriteria queueSearchCriteria = new QueueSearchCriteria();
        queueSearchCriteria.setServices(Collections.singletonList(MCOE_TRIAGE_QUEUE_CONCEPT));

        QueueServicesWrapper queueServices = Context.getRegisteredComponents(QueueServicesWrapper.class).get(0);
        List<Queue> queues = queueServices.getQueueService().getQueues(queueSearchCriteria);

        if (queues.isEmpty()) {
            throw new Exception("No queue found with service concept " + MCOE_TRIAGE_QUEUE_CONCEPT_CODE_PIH);
        }
        if (queues.size() > 1) {
            throw new Exception("Multiple queues found with service concept " + MCOE_TRIAGE_QUEUE_CONCEPT_CODE_PIH);
        }

        return queues.get(0);
    }

    private int removeTimedOutPatients(Queue queue) {
        int numPatientsRemoved = 0;

        QueueServicesWrapper queueServices = Context.getRegisteredComponents(QueueServicesWrapper.class).get(0);
        QueueEntrySearchCriteria criteria = new QueueEntrySearchCriteria();
        criteria.setQueues(Collections.singletonList(queue));
        criteria.setIsEnded(false);

        List<QueueEntry> queueEntries = queueServices.getQueueEntryService().getQueueEntries(criteria);

        if (queueEntries == null || queueEntries.isEmpty()) {
            return numPatientsRemoved;
        }

        Date currentTime = new Date();

        for (QueueEntry entry : queueEntries) {
            Date startedAt = entry.getStartedAt();
            long activeMinutes = Duration.between(startedAt.toInstant(), currentTime.toInstant()).toMinutes();

            if (activeMinutes > QUEUE_TIMEOUT_MINUTES) {
                log.warn("Removing patient {} from queue after {} minutes", entry.getPatient().getUuid(), activeMinutes);
                entry.setEndedAt(currentTime);
                queueServices.getQueueEntryService().saveQueueEntry(entry);

                numPatientsRemoved++;
            }
        }

        return numPatientsRemoved;
    }
}
