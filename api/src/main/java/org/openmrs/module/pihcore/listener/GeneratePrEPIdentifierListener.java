package org.openmrs.module.pihcore.listener;

import org.openmrs.Location;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifier;
import org.openmrs.PatientIdentifierType;
import org.openmrs.PatientProgram;
import org.openmrs.api.context.Context;
import org.openmrs.event.EntityEvent;
import org.openmrs.event.Event;
import org.openmrs.event.TransactionCommittedEvent;
import org.openmrs.module.idgen.service.IdentifierSourceService;
import org.openmrs.module.pihcore.ZlConfigConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

/**
 * Listener to generate PrEP identifier whenever a patient enrolls in the PrEP program
 * (and doesn't currently have an identifier)
 *
 * * Currently this is only enabled for our HAITI HIV server,
 */
@Component
public class GeneratePrEPIdentifierListener implements ApplicationListener<TransactionCommittedEvent> {

    private final Logger log = LoggerFactory.getLogger(getClass());

    private static boolean enabled = false;

    public static void setEnabled(boolean enabled) {
        GeneratePrEPIdentifierListener.enabled = enabled;
    }

    @Override
    public void onApplicationEvent(TransactionCommittedEvent transactionCommittedEvent) {
        if (!enabled) {
            return;
        }
        if (transactionCommittedEvent.getEvents() != null) {
            for (EntityEvent entityEvent : transactionCommittedEvent.getEvents()) {
                if (entityEvent.getAction().name().equals(Event.Action.CREATED.name()) && entityEvent.getEntity() instanceof PatientProgram) {
                    PatientProgram patientProgram = (PatientProgram) entityEvent.getEntity();
                    if (patientProgram.getProgram().getUuid().equals(ZlConfigConstants.PROGRAM_PREP_UUID)) {
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
                            } else {
                                log.error("Unable to generate PrEP identifier for patient " + patient.getId());
                            }
                        }
                    }
                }
            }
        }
    }

    private boolean prepIdentifierInUse(String identifier, PatientIdentifierType prepIdentifierType) {
        List<PatientIdentifier> identifiers = Context.getPatientService().getPatientIdentifiers(identifier,
                Collections.singletonList(prepIdentifierType), null, null, null);

        return identifiers != null && identifiers.size() > 0;
    }
}
