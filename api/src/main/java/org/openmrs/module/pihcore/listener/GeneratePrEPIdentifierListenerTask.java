package org.openmrs.module.pihcore.listener;

import org.openmrs.Location;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifier;
import org.openmrs.PatientIdentifierType;
import org.openmrs.PatientProgram;
import org.openmrs.api.context.Context;
import org.openmrs.module.idgen.service.IdentifierSourceService;
import org.openmrs.module.pihcore.ZlConfigConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.MapMessage;
import javax.jms.Message;
import java.util.Collections;
import java.util.List;

public class GeneratePrEPIdentifierListenerTask implements Runnable {

    private final Logger log = LoggerFactory.getLogger(getClass());

    private Message message;

    public GeneratePrEPIdentifierListenerTask(Message message) {
        this.message = message;
    }

    @Override
    public void run() {
        MapMessage mapMessage = (MapMessage) message;
        try {
            String className = mapMessage.getString("classname");
            String uuid = mapMessage.getString("uuid");

            if ("org.openmrs.PatientProgram".equals(className)) {
                PatientProgram patientProgram = Context.getProgramWorkflowService().getPatientProgramByUuid(uuid);
                if (patientProgram != null && patientProgram.getProgram().getUuid().equals(ZlConfigConstants.PROGRAM_PREP_UUID)) {
                    Patient patient = patientProgram.getPatient();
                    PatientIdentifierType prepIdentifierType = Context.getPatientService().getPatientIdentifierTypeByUuid(ZlConfigConstants.PATIENTIDENTIFIERTYPE_PREPCODE_UUID);
                    if (patient.getPatientIdentifier(prepIdentifierType) == null) {
                        Location location = patientProgram.getLocation();
                        String prepIdentifier = Context.getService(IdentifierSourceService.class).generateIdentifier(
                                prepIdentifierType, location, "generating PrEP identifier");

                        // double check to make sure this identifier is not in use--since manual entry is allowed, it could be
                        while (prepIdentifierInUse(prepIdentifier, prepIdentifierType)) {
                            log.warn("Attempted to generate duplicate PrEP identifier " + prepIdentifier);
                            prepIdentifier = Context.getService(IdentifierSourceService.class).generateIdentifier(
                                    prepIdentifierType, location, "generating PrEP identifier");
                        }

                        if (prepIdentifier != null) {
                            PatientIdentifier identifier = new PatientIdentifier(prepIdentifier, prepIdentifierType, location);
                            patient.addIdentifier(identifier);
                            Context.getPatientService().savePatientIdentifier(identifier);
                            log.info("Generated PrEP identifier " + prepIdentifier + " for patient " + patient.getId());
                        } else {
                            log.error("Unable to generate PrEP identifier for patient " + patient.getId());
                        }
                    }
                } else {
                    log.error("Unable to find patient program " + uuid);
                }
            }
        } catch (Exception e) {
            log.error("Error processing message", e);
        }
    }

    private boolean prepIdentifierInUse(String identifier, PatientIdentifierType prepIdentifierType) {
        List<PatientIdentifier> identifiers = Context.getPatientService().getPatientIdentifiers(identifier,
                Collections.singletonList(prepIdentifierType), null, null, null);

        return identifiers != null && identifiers.size() > 0;
    }
}
