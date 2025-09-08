package org.openmrs.module.pihcore.listener;

import org.openmrs.OpenmrsObject;
import org.openmrs.Patient;
import org.openmrs.PatientProgram;
import org.openmrs.annotation.Handler;
import org.openmrs.api.context.Daemon;
import org.openmrs.event.Event;
import org.openmrs.event.SubscribableEventListener;
import org.openmrs.module.DaemonToken;
import org.springframework.stereotype.Component;

import javax.jms.Message;
import java.util.Arrays;
import java.util.List;


/**
 * This listens for the creation or update of any Patient or Patient State records,
 * and then (via the UpdateHealthCenterListenerTask) calls our "updateHealthCenter" server to
 * update the Health Center attribute for the specific patient
 *
 * Currently this is only enabled for our HAITI HIV server, but could likely be expanded to other servers as we merge Haiti servers
 */
@Component
@Handler
public class UpdateHealthCenterListener implements SubscribableEventListener {

    private static DaemonToken daemonToken;
    public static void setDaemonToken(DaemonToken daemonToken) {
        UpdateHealthCenterListener.daemonToken = daemonToken;
    }

    private static boolean enabled = false;
    public static void setEnabled(boolean enabled) {
        UpdateHealthCenterListener.enabled = enabled;
    }
    public static boolean isEnabled() {
        return enabled;
    }


    @Override
    public List<Class<? extends OpenmrsObject>> subscribeToObjects() {
        Object classes = Arrays.asList(Patient.class, PatientProgram.class);
        return (List<Class<? extends OpenmrsObject>>) classes;
    }

    @Override
    public List<String> subscribeToActions() {
        return Arrays.asList(Event.Action.CREATED.name(), Event.Action.UPDATED.name());
    }

    @Override
    public void onMessage(Message message) {
        if (enabled) {
            UpdateHealthCenterListenerTask task = new UpdateHealthCenterListenerTask(message);
            Daemon.runInDaemonThreadWithoutResult(task, daemonToken);
        }
    }
}

