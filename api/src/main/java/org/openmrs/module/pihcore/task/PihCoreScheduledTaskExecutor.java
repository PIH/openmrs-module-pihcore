package org.openmrs.module.pihcore.task;

import org.springframework.scheduling.concurrent.ScheduledExecutorFactoryBean;
import org.springframework.stereotype.Component;

/**
 * Executor that is responsible for scheduling and running the pihcore scheduled tasks
 */
@Component("pihCoreScheduledTaskExecutor")
public class PihCoreScheduledTaskExecutor extends ScheduledExecutorFactoryBean {

}
