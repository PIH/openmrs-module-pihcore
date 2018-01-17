package org.openmrs.module.pihcore.setup;

import org.apache.commons.lang.time.DateUtils;
import org.openmrs.api.context.Context;
import org.openmrs.module.emr.EmrConstants;
import org.openmrs.module.emrapi.utils.GeneralUtils;
import org.openmrs.module.pihcore.task.PihCloseStaleVisitsTask;
import org.openmrs.scheduler.SchedulerException;
import org.openmrs.scheduler.SchedulerService;
import org.openmrs.scheduler.TaskDefinition;

import java.util.Date;

public class CloseStaleVisitsSetup {

    public static void setupCloseStaleVisitsTask() {

        SchedulerService schedulerService = Context.getSchedulerService();
        TaskDefinition task = schedulerService.getTaskByName(EmrConstants.TASK_CLOSE_STALE_VISITS_NAME);
        if (task == null) {
            task = new TaskDefinition();
            task.setName(EmrConstants.TASK_CLOSE_STALE_VISITS_NAME);
            task.setDescription(EmrConstants.TASK_CLOSE_STALE_VISITS_DESCRIPTION);
            task.setTaskClass(PihCloseStaleVisitsTask.class.getName());
            task.setStartTime(DateUtils.addMinutes(new Date(), 5));
            task.setRepeatInterval(EmrConstants.TASK_CLOSE_STALE_VISITS_REPEAT_INTERVAL);
            task.setStartOnStartup(true);
            try {
                schedulerService.scheduleTask(task);
            } catch (SchedulerException e) {
                throw new RuntimeException("Failed to schedule close stale visits task", e);
            }
        } else {
            // if you modify any of the properties above, you also need to set them here, in order to update existing servers
            boolean changed = GeneralUtils.setPropertyIfDifferent(task, "taskClass", PihCloseStaleVisitsTask.class.getName());
            if (changed) {
                schedulerService.saveTaskDefinition(task);
            }

            if (!task.getStarted()) {
                task.setStarted(true);
                try {
                    schedulerService.scheduleTask(task);
                } catch (SchedulerException e) {
                    throw new RuntimeException("Failed to schedule close stale visits task", e);
                }
            }
        }

    }
}
