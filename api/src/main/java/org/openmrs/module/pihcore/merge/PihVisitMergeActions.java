package org.openmrs.module.pihcore.merge;

import org.openmrs.Visit;
import org.openmrs.module.emrapi.merge.VisitMergeAction;
import org.openmrs.module.queue.api.QueueEntryService;
import org.openmrs.module.queue.api.search.QueueEntrySearchCriteria;
import org.openmrs.module.queue.model.QueueEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Custom actions when merging visits
 * TODO: This likely belongs in emrapi or queue modules, we should consider moving it down the road
 */
@Component("pihVisitMergeActions")
public class PihVisitMergeActions implements VisitMergeAction {

    private static final Logger log = LoggerFactory.getLogger(PihVisitMergeActions.class);

    private QueueEntryService queueEntryService;

    public PihVisitMergeActions(@Autowired QueueEntryService queueEntryService) {
        this.queueEntryService = queueEntryService;
    }

    @Override
    public void beforeSavingVisits(Visit preferred, Visit nonPreferred) {
        moveQueueEntries(preferred, nonPreferred);
    }

    // Move queue entries from nonPreferred to preferred visit, and ensure patient is set from preferred as well
    private void moveQueueEntries(Visit preferred, Visit nonPreferred) {
        List<QueueEntry> nonPreferredQueueEntries = getQueueEntries(nonPreferred);
        for (QueueEntry queueEntry : nonPreferredQueueEntries) {
            log.warn("Moving queueEntry " + queueEntry.getId() + " from visit " + nonPreferred.getVisitId() + " to visit " + preferred.getId());
            queueEntry.setPatient(preferred.getPatient());
            queueEntry.setVisit(preferred);
            queueEntryService.saveQueueEntry(queueEntry);
        }
    }

    // Get the queue entries associated with the given visit
    private List<QueueEntry> getQueueEntries(Visit visit) {
        QueueEntrySearchCriteria c = new QueueEntrySearchCriteria();
        c.setVisit(visit);
        return queueEntryService.getQueueEntries(c);
    }

    @Override
    public void afterSavingVisits(Visit preferred, Visit nonPreferred) {
        // Do nothing
    }
}
