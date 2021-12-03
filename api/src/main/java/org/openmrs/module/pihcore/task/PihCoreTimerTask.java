package org.openmrs.module.pihcore.task;

import org.openmrs.api.context.Daemon;
import org.openmrs.module.DaemonToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.TimerTask;

/**
 * Abstract class for a timer task that utilises a daemon thread
 */
public class PihCoreTimerTask extends TimerTask {

    private final Logger log = LoggerFactory.getLogger(getClass());

    private static DaemonToken daemonToken;
    public static void setDaemonToken(DaemonToken daemonToken) {
        PihCoreTimerTask.daemonToken = daemonToken;
    }

    private static boolean enabled = false;
    public static void setEnabled(boolean enabled) {
        PihCoreTimerTask.enabled = enabled;
    }
    public static boolean isEnabled() {
        return enabled;
    }

    private Class<? extends Runnable> taskClass;

    public PihCoreTimerTask(Class<? extends Runnable> taskClass) {
        this.taskClass = taskClass;
    }

    /**
     * @see TimerTask#run()
     */
    @Override
    public final void run() {
        if (daemonToken != null && enabled) {
            try {
                log.debug("Running task: " + taskClass.getSimpleName());
                Runnable taskInstance = taskClass.newInstance();
                Daemon.runInDaemonThread(taskInstance, daemonToken);
            }
            catch (Exception e) {
                log.error("An error occurred while running scheduled task " + taskClass.getSimpleName(), e);
            }
        }
        else {
            log.debug("Not running scheduled task. DaemonToken = " + daemonToken + "; enabled = " + enabled);
        }
    }
}
