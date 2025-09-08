package org.openmrs.module.pihcore.listener;

import org.openmrs.Patient;
import org.openmrs.PatientProgram;
import org.openmrs.api.context.Context;

import org.openmrs.module.pihcore.service.PihCoreService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.MapMessage;
import javax.jms.Message;

public class UpdateHealthCenterListenerTask implements Runnable {

    private final Logger log = LoggerFactory.getLogger(getClass());

    private Message message;

    public UpdateHealthCenterListenerTask(Message message) {
        this.message = message;
    }

    @Override
    public void run() {

        try {
            MapMessage mapMessage = (MapMessage) message;
            String className = mapMessage.getString("classname");
            String uuid = mapMessage.getString("uuid");

            Patient patient = null;

            if ("org.openmrs.PatientProgram".equals(className)) {
                PatientProgram patientProgram = Context.getProgramWorkflowService().getPatientProgramByUuid(uuid);
                if (patientProgram != null) {
                    patient = patientProgram.getPatient();
                }
                else {
                    log.error("Unable to find patient program " + uuid);
                }
            }
            else if ("org.openmrs.Patient".equals(className)) {
                patient = Context.getPatientService().getPatientByUuid(uuid);
            }
            else {
                log.error("Unable to handle class of type:" + className);
            }
            if (patient != null) {
                Context.getService(PihCoreService.class).updateHealthCenter(patient);
            }
            else {
                log.error("Unable to find patient");
            }

        }
        catch (Exception e) {
            log.error("Error processing message", e);
        }

    }
}
