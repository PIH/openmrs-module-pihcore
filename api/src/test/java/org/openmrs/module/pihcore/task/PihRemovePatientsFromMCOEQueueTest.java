package org.openmrs.module.pihcore.task;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openmrs.module.pihcore.PihCoreContextSensitiveTest;
import org.openmrs.module.pihcore.SierraLeoneConfigConstants;
import org.openmrs.module.queue.api.QueueEntryService;
import org.openmrs.module.queue.api.QueueService;
import org.openmrs.module.queue.api.search.QueueEntrySearchCriteria;
import org.openmrs.module.queue.model.Queue;
import org.openmrs.module.queue.model.QueueEntry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;

public class PihRemovePatientsFromMCOEQueueTest extends PihCoreContextSensitiveTest {

    @Autowired
    @Qualifier("queue.QueueEntryService")
    protected QueueEntryService queueEntryService;

    @Autowired
    @Qualifier("queue.QueueService")
    protected QueueService queueService;

    PihRemovePatientsFromMCOEQueue task;

    @BeforeEach
    public void setUp() throws Exception {
        executeDataSet("mcoeTriageQueueTestDataset.xml");
        task = new PihRemovePatientsFromMCOEQueue();
    }
    @Test
    public void testGetMCOEQueue() {
        Optional<Queue> mcoeQueue = queueService.getQueueByUuid(SierraLeoneConfigConstants.QUEUE_TRIAGE_UUID);
        assertThat(mcoeQueue.isPresent(), org.hamcrest.Matchers.is(true));
        assertThat(mcoeQueue.get().getName(), org.hamcrest.Matchers.is("MCOE Triage"));
    }

    @Test
    public void shouldRemoveQueueEntriesOlderThanTwelfeHours() {
        Optional<Queue> mcoeQueue = queueService.getQueueByUuid(SierraLeoneConfigConstants.QUEUE_TRIAGE_UUID);
        assertThat(mcoeQueue.isPresent(), org.hamcrest.Matchers.is(true));
        assertThat(mcoeQueue.get().getName(), org.hamcrest.Matchers.is("MCOE Triage"));

        QueueEntrySearchCriteria criteria = new QueueEntrySearchCriteria();
        criteria.setQueues(Collections.singletonList(mcoeQueue.get()));
        criteria.setIsEnded(false);
        LocalDateTime now = LocalDateTime.now();
        ZonedDateTime atZone = now.atZone(ZoneId.systemDefault());
        Date removalTimeLimit = Date.from(atZone.minusHours(PihRemovePatientsFromMCOEQueue.QUEUE_TIMEOUT_HOURS).toInstant());
        criteria.setStartedOnOrBefore(removalTimeLimit);

        List<QueueEntry> queueEntries = queueEntryService.getQueueEntries(criteria);
        assertThat(queueEntries.size(), org.hamcrest.Matchers.is(1));

        task.run();
        queueEntries = queueEntryService.getQueueEntries(criteria);
        assertThat(queueEntries.size(), org.hamcrest.Matchers.is(0));

    }
}
