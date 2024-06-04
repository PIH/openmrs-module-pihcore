package org.openmrs.module.pihcore.task;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.concurrent.ScheduledExecutorFactoryBean;
import org.springframework.scheduling.concurrent.ScheduledExecutorTask;
import org.springframework.stereotype.Component;

/**
 * Executor that is responsible for scheduling and running the pihcore scheduled tasks
 */
@Component
public class PihCoreScheduledTaskExecutor extends ScheduledExecutorFactoryBean {

    private final Logger log = LoggerFactory.getLogger(getClass());

    private final long oneSecond = 1000;
    private final long oneMinute = oneSecond * 60;
    private final long fiveMinutes = oneMinute * 5;

    private final long tenMinutes = oneMinute * 10;
    private final long oneHour = oneMinute * 60;
    private final long fourHours = oneHour * 4;

    public PihCoreScheduledTaskExecutor() {
        setScheduledExecutorTasks(
                task(fiveMinutes, fourHours, MarkAppointmentSchedulingAppointmentsAsMissedOrCompletedTask.class),
                task(fiveMinutes, oneHour, PihCloseStalePullRequestsTask.class),
                task(fiveMinutes, oneHour, PihCloseStaleCreateRequestsTask.class),
                task(fiveMinutes, oneHour, PihCloseStaleVisitsTask.class),
                task(tenMinutes, oneHour, MarkBahmniAppointmentsAsCompleted.class)  // generally we want this to run afte the close stale visits task
        );
    }

    private ScheduledExecutorTask task(long delay, long period, Class<? extends Runnable> runnable) {
        log.info("Scheduling task " + runnable.getSimpleName() + " with delay " + delay + " and period " + period);
        ScheduledExecutorTask task = new ScheduledExecutorTask();
        task.setDelay(delay);
        task.setPeriod(period);
        task.setRunnable(new PihCoreTimerTask(runnable));
        return task;
    }
}
