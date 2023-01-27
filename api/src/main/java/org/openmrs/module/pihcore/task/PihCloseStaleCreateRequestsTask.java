package org.openmrs.module.pihcore.task;

import org.openmrs.module.paperrecord.CloseStaleCreateRequestsTask;
import org.openmrs.scheduler.TaskDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Schedules and executes the paper record archives task to close stale create requests
 */
public class PihCloseStaleCreateRequestsTask implements Runnable {

    private final Logger log = LoggerFactory.getLogger(getClass());

    @Override
    public void run() {
        log.info("Executing " + getClass());
        CloseStaleCreateRequestsTask task = new CloseStaleCreateRequestsTask();
        task.initialize(new TaskDefinition());
        task.execute();
        log.info(getClass() + " Execution Completed");
    }
}
