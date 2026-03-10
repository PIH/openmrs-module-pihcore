package org.openmrs.module.pihcore.listener;

import org.openmrs.OpenmrsObject;
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
 * Listener to generate PrEP identifier whenever a patient enrolls in the PrEP program
 * (and doesn't currently have an identifier)
 *
 * * Currently this is only enabled for our HAITI HIV server,
 */
@Component
@Handler
public class GeneratePrEPIdentifierListener implements SubscribableEventListener {

    private static DaemonToken daemonToken;
    public static void setDaemonToken(DaemonToken daemonToken) {
        GeneratePrEPIdentifierListener.daemonToken = daemonToken;
    }

    private static boolean enabled = false;
    public static void setEnabled(boolean enabled) {
        GeneratePrEPIdentifierListener.enabled = enabled;
    }
    public static boolean isEnabled() {
        return enabled;
    }

    @Override
    public List<Class<? extends OpenmrsObject>> subscribeToObjects() {
        Object classes = Arrays.asList(PatientProgram.class);
        return (List<Class<? extends OpenmrsObject>>) classes;
    }

    @Override
    public List<String> subscribeToActions() {
        return Arrays.asList(Event.Action.CREATED.name());
    }

    @Override
    public void onMessage(Message message) {
        if (enabled) {
            GeneratePrEPIdentifierListenerTask task = new GeneratePrEPIdentifierListenerTask(message);
            Daemon.runInDaemonThreadWithoutResult(task, daemonToken);
        }
    }
}
