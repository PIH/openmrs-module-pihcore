package org.openmrs.module.pihcore.setup;

import org.apache.commons.lang.time.DateUtils;
import org.openmrs.api.context.Context;
import org.openmrs.module.emrapi.utils.GeneralUtils;
import org.openmrs.module.pihcore.task.RetireProvidersWithRetiredUserAccounts;
import org.openmrs.scheduler.SchedulerException;
import org.openmrs.scheduler.SchedulerService;
import org.openmrs.scheduler.TaskDefinition;

import java.util.Date;

public class RetireProvidersSetup {

    public static final String TASK_RETIRE_PROVIDERS = "PIH Core - Retire Old Provider Accounts";

    // once a day (60 * 60 * 24 seconds)
    public static final long TASK_RETIRE_PROVIDERS_REPEAT_INTERVAL =  60 * 60 * 24;

    public static void setupRetireProvidersTask() {

        SchedulerService schedulerService = Context.getSchedulerService();
        TaskDefinition task = schedulerService.getTaskByName(TASK_RETIRE_PROVIDERS);
        if (task == null) {
            task = new TaskDefinition();
            task.setName(TASK_RETIRE_PROVIDERS);
            task.setDescription(TASK_RETIRE_PROVIDERS);
            task.setTaskClass(RetireProvidersWithRetiredUserAccounts.class.getName());
            task.setStartTime(DateUtils.addMinutes(new Date(), 5));
            task.setRepeatInterval(TASK_RETIRE_PROVIDERS_REPEAT_INTERVAL);
            task.setStartOnStartup(true);
            try {
                schedulerService.scheduleTask(task);
            } catch (SchedulerException e) {
                throw new RuntimeException("Failed to schedule retire old providers task", e);
            }
        } else {
            // if you modify any of the properties above, you also need to set them here, in order to update existing servers
            boolean changed = GeneralUtils.setPropertyIfDifferent(task, "taskClass", RetireProvidersWithRetiredUserAccounts.class.getName());
            if (changed) {
                schedulerService.saveTask(task);
            }

            if (!task.getStarted()) {
                task.setStarted(true);
                try {
                    schedulerService.scheduleTask(task);
                } catch (SchedulerException e) {
                    throw new RuntimeException("Failed to schedule retire old providers task", e);
                }
            }
        }

    }

}
