package org.openmrs.module.pihcore.task;

import org.apache.commons.lang.time.DateUtils;
import org.apache.commons.lang.time.StopWatch;
import org.openmrs.api.context.Context;
import org.openmrs.module.pihcore.SierraLeoneConfigConstants;
import org.openmrs.module.queue.api.QueueServicesWrapper;
import org.openmrs.module.queue.api.search.QueueEntrySearchCriteria;
import org.openmrs.module.queue.model.Queue;
import org.openmrs.module.queue.model.QueueEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;

public class PihRemovePatientsFromMCOEQueue implements Runnable {

    public static final Long QUEUE_TIMEOUT_HOURS = 12L; // remove all queue entries from the MCOE Triage older than 12 hours
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
            if (queue == null) {
                return;
            }

            int removedPatientsCount = removeTimedOutPatients(queue);

            stopWatch.stop();
            log.info("Execution completed in {} - {} patients removed", stopWatch, removedPatientsCount);
        } catch (Exception e) {
            log.error("Error executing " + getClass(), e);
        } finally {
            isExecuting = false;
        }
    }

    private Queue getQueue() {

        QueueServicesWrapper queueServices = Context.getRegisteredComponents(QueueServicesWrapper.class).get(0);
        Optional<Queue> mcoeQueue = queueServices.getQueueService().getQueueByUuid(SierraLeoneConfigConstants.QUEUE_TRIAGE_UUID);
        if (mcoeQueue.isPresent()) {
            return mcoeQueue.get();
        } else {
            log.debug("No queue found with uuid " + SierraLeoneConfigConstants.QUEUE_TRIAGE_UUID);
            return null;
        }
    }

    private int removeTimedOutPatients(Queue queue) {
        int numPatientsRemoved = 0;

        QueueServicesWrapper queueServices = Context.getRegisteredComponents(QueueServicesWrapper.class).get(0);
        QueueEntrySearchCriteria criteria = new QueueEntrySearchCriteria();
        criteria.setQueues(Collections.singletonList(queue));
        criteria.setIsEnded(false);

        Date currentTime = new Date();
        Date removalTimeLimit = DateUtils.addHours(currentTime, (int) (-1*QUEUE_TIMEOUT_HOURS));

        criteria.setStartedOnOrBefore(removalTimeLimit);

        List<QueueEntry> queueEntries = queueServices.getQueueEntryService().getQueueEntries(criteria);

        if (queueEntries == null || queueEntries.isEmpty()) {
            return numPatientsRemoved;
        }

        for (QueueEntry entry : queueEntries) {
            Date startedAt = entry.getStartedAt();
            long activeMinutes = Duration.between(startedAt.toInstant(), currentTime.toInstant()).toMinutes();
            entry.setEndedAt(currentTime);
            try {
                queueServices.getQueueEntryService().saveQueueEntry(entry);
            } catch (Exception e) {
                log.error("Failed to remove patient: " + entry.getPatient().getUuid() + " from queue.", e);
                continue;
            }
            log.warn("Removing patient {} from queue after {} minutes", entry.getPatient().getUuid(), activeMinutes);
            numPatientsRemoved++;
        }

        return numPatientsRemoved;
    }
}
