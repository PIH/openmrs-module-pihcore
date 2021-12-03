package org.openmrs.module.pihcore.task;

import org.openmrs.module.paperrecord.CloseStalePullRequestsTask;
import org.openmrs.scheduler.TaskDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Schedules and executes the paper record archives task to close stale pull requests
 */
public class PihCloseStalePullRequestsTask implements Runnable {

    private final Logger log = LoggerFactory.getLogger(getClass());

    @Override
    public void run() {
        log.info("Executing " + getClass());
        CloseStalePullRequestsTask task = new CloseStalePullRequestsTask();
        task.initialize(new TaskDefinition());
        task.execute();
    }
}
