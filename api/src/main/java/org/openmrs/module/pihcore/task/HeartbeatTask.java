package org.openmrs.module.pihcore.task;

import lombok.Setter;
import org.apache.logging.log4j.ThreadContext;
import org.openmrs.module.authentication.UserLoginTracker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HeartbeatTask implements Runnable {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Setter
    private static Long serverStartTime = null;

    @Override
    public void run() {
        if (serverStartTime != null) {
            try {
                ThreadContext.clearAll();
                int numActiveUsers = UserLoginTracker.getActiveLogins().size();
                ThreadContext.put("numActiveUsers", Integer.toString(numActiveUsers));
                long uptime = System.currentTimeMillis() - serverStartTime;
                ThreadContext.put("uptimeMillis", Long.toString(uptime));
                logger.debug(ThreadContext.getContext().toString());
            } catch (Exception e) {
                // Do nothing, we don't want this filter to cause any adverse behavior
            } finally {
                ThreadContext.clearAll();
            }
        }
    }
}
