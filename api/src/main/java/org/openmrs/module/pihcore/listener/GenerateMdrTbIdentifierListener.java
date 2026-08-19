package org.openmrs.module.pihcore.listener;

import org.openmrs.Patient;
import org.openmrs.PatientIdentifier;
import org.openmrs.PatientIdentifierType;
import org.openmrs.PatientProgram;
import org.openmrs.api.context.Context;
import org.openmrs.event.EntityEvent;
import org.openmrs.event.Event;
import org.openmrs.event.TransactionBeforeCompletionEvent;
import org.openmrs.module.idgen.service.IdentifierSourceService;
import org.openmrs.module.pihcore.LesothoConfigConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

/**
 * Listener to generate a MDR-TB identifier whenever a patient enrolls in the MDR-TB program
 * (and doesn't currently have an identifier)
 *
 * Currently this is only enabled for Lesotho
 */
@Component
public class GenerateMdrTbIdentifierListener implements ApplicationListener<TransactionBeforeCompletionEvent> {

    private final Logger log = LoggerFactory.getLogger(getClass());

    // manual entry of MDR-TB IDs is allowed, so a generated identifier may already be in use; give up after this many
    // attempts rather than walking through a long run of manually-entered identifiers
    private static final int MAX_GENERATION_ATTEMPTS = 10;

    private static boolean enabled = false;

    public static void setEnabled(boolean enabled) {
        GenerateMdrTbIdentifierListener.enabled = enabled;
    }

    @Override
    public void onApplicationEvent(TransactionBeforeCompletionEvent transactionBeforeCompletionEvent) {
        if (!enabled) {
            return;
        }
        if (transactionBeforeCompletionEvent.getEvents() != null) {
            for (EntityEvent entityEvent : transactionBeforeCompletionEvent.getEvents()) {
                if (entityEvent.getAction().name().equals(Event.Action.CREATED.name()) && entityEvent.getEntity() instanceof PatientProgram) {
                    PatientProgram patientProgram = (PatientProgram) entityEvent.getEntity();
                    if (patientProgram.getProgram().getUuid().equals(LesothoConfigConstants.PROGRAM_MDRTB_UUID)) {
                        Patient patient = patientProgram.getPatient();
                        try {
                            generateMdrTbIdentifierIfNecessary(patient);
                        }
                        catch (Exception e) {
                            // we are within the transaction that is enrolling the patient in the program, so let a failure
                            // to generate an identifier be logged rather than rolling back the enrollment itself
                            log.error("Unable to generate MDR-TB identifier for patient " + patient.getId(), e);
                        }
                    }
                }
            }
        }
    }

    private void generateMdrTbIdentifierIfNecessary(Patient patient) {
        PatientIdentifierType mdrTbIdentifierType = Context.getPatientService().getPatientIdentifierTypeByUuid(LesothoConfigConstants.PATIENTIDENTIFIERTYPE_MDRTB_ID_UUID);
        if (patient.getPatientIdentifier(mdrTbIdentifierType) != null) {
            return;
        }

        IdentifierSourceService identifierSourceService = Context.getService(IdentifierSourceService.class);
        String mdrTbIdentifier = null;

        for (int attempt = 0; attempt < MAX_GENERATION_ATTEMPTS; attempt++) {
            // the MDR-TB ID has a location behavior of NOT_USED, so we don't associate the identifier with a location;
            // this assumes a single auto generation option is configured for the identifier type
            String candidate = identifierSourceService.generateIdentifier(mdrTbIdentifierType, null, "generating MDR-TB identifier");
            if (candidate == null) {
                log.error("No auto generation option enabled for the MDR-TB identifier type");
                break;
            }
            // double check to make sure this identifier is not in use--since manual entry is allowed, it could be
            if (!mdrTbIdentifierInUse(candidate, mdrTbIdentifierType)) {
                mdrTbIdentifier = candidate;
                break;
            }
            log.warn("Attempted to generate duplicate MDR-TB identifier " + candidate);
        }

        if (mdrTbIdentifier != null) {
            PatientIdentifier identifier = new PatientIdentifier(mdrTbIdentifier, mdrTbIdentifierType, null);
            patient.addIdentifier(identifier);
            Context.getPatientService().savePatientIdentifier(identifier);
        } else {
            log.error("Unable to generate MDR-TB identifier for patient " + patient.getId());
        }
    }

    private boolean mdrTbIdentifierInUse(String identifier, PatientIdentifierType mdrTbIdentifierType) {
        List<PatientIdentifier> identifiers = Context.getPatientService().getPatientIdentifiers(identifier,
                Collections.singletonList(mdrTbIdentifierType), null, null, null);

        return identifiers != null && identifiers.size() > 0;
    }
}
