package org.openmrs.module.pihcore.task;

import org.openmrs.api.context.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.concurrent.ScheduledExecutorFactoryBean;
import org.springframework.stereotype.Component;

import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Executor that is responsible for scheduling and running the pihcore scheduled tasks
 */
@Component("pihCoreScheduledTaskExecutor")
public class PihCoreScheduledTaskExecutor extends ScheduledExecutorFactoryBean {

    private static final Logger log = LoggerFactory.getLogger(PihCoreScheduledTaskExecutor.class);

    private static final long oneSecond = 1000;
    private static final long oneMinute = oneSecond * 60;
    private static final long fiveMinutes = oneMinute * 5;
    private static final long tenMinutes = oneMinute * 10;
    private static final long oneHour = oneMinute * 60;
    private static final long fourHours = oneHour * 4;
    private static final long twelveHours = oneHour * 12;
    private static final long twentyFourHours = oneHour * 24;

    public static void setup() {
        ScheduledThreadPoolExecutor executor = Context.getRegisteredComponent("pihCoreScheduledTaskExecutor",  ScheduledThreadPoolExecutor.class);
        task(executor, fiveMinutes, fourHours, MarkAppointmentSchedulingAppointmentsAsMissedOrCompletedTask.class);
        task(executor, fiveMinutes, oneHour, PihCloseStalePullRequestsTask.class);
        task(executor, fiveMinutes, oneHour, PihCloseStaleCreateRequestsTask.class);
        task(executor, fiveMinutes, oneHour, PihCloseStaleVisitsTask.class);
        task(executor, fiveMinutes, oneHour, PihRemovePatientsFromMCOEQueue.class);
        task(executor, fiveMinutes, twelveHours, ClosePregnancyProgramTask.class);
        task(executor, fiveMinutes, twelveHours, CloseInfantProgramTask.class);
        task(executor, tenMinutes, oneHour, MarkBahmniAppointmentsAsCompleted.class);  // generally we want this to run after the close stale visits task
        task(executor, fiveMinutes, twentyFourHours, UpdateHealthCenterTask.class);
        task(executor, oneSecond, oneMinute, HeartbeatTask.class);
    }

    private static void  task(ScheduledThreadPoolExecutor executor, long delay, long period, Class<? extends Runnable> runnable) {
        log.info("Scheduling task " + runnable.getSimpleName() + " with delay " + delay + " and period " + period);
        executor.scheduleAtFixedRate(new PihCoreTimerTask(runnable), delay, period, TimeUnit.MILLISECONDS);
    }

}
