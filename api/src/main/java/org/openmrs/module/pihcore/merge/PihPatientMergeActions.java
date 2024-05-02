package org.openmrs.module.pihcore.merge;

import org.openmrs.Encounter;
import org.openmrs.EncounterType;
import org.openmrs.Patient;
import org.openmrs.PersonAttribute;
import org.openmrs.PersonAttributeType;
import org.openmrs.api.EncounterService;
import org.openmrs.api.context.Context;
import org.openmrs.module.emrapi.merge.PatientMergeAction;
import org.openmrs.module.pihcore.PihEmrConfigConstants;
import org.openmrs.module.pihcore.metadata.Metadata;
import org.openmrs.module.queue.api.QueueEntryService;
import org.openmrs.module.queue.api.search.QueueEntrySearchCriteria;
import org.openmrs.module.queue.model.QueueEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Custom actions to perform when merging two patients. Currently used to resolve registration data points as follows:
 *
 * 1) If both patients have a telephone number person attribute or a mother's name attribute, void the attribute associated with
 * the non-preferred patient
 *
 * 2) Make sure that the most recent registration encounter is from the preferred patient (void any registration encounters associated with
 * the non-preferred patient that occurred after the most recent associated with the preferred patient).  This is because
 * our registration summary simply displays the data from the most recent registration encounter.
 *
 * 3) Make sure that if the patient has any queue entries not associated with a visit, that these are moved.
 * Queue entries associated with a visit are handled by the PihVisitMergeActions class
 */
@Component("pihPatientMergeActions")
public class PihPatientMergeActions implements PatientMergeAction {

    private static final Logger log = LoggerFactory.getLogger(PihVisitMergeActions.class);

    @Autowired
    private EncounterService encounterService;

    @Autowired
    private QueueEntryService queueEntryService;

    @Override
    public void beforeMergingPatients(Patient preferred, Patient nonPreferred) {
        // void attributes on non-preferred patient if present on preferred patient
        voidNonPreferredAttribute(Metadata.getPhoneNumberAttributeType(), preferred, nonPreferred);
        voidNonPreferredAttribute(Metadata.getMothersFirstNameAttributeType(), preferred, nonPreferred);

        // make sure the most recent registration encounter belongs to the preferred patient.
        voidMostRecentRegistrationIfNonPreferred(preferred, nonPreferred);

        moveQueueEntriesNotAssociatedWithVisits(preferred, nonPreferred);
    }

    @Override
    public void afterMergingPatients(Patient patient, Patient patient1) {
        // do nothing
    }

    private void voidNonPreferredAttribute(PersonAttributeType type, Patient preferred, Patient nonPreferred) {
        if (preferred.getAttribute(type) != null) {
            PersonAttribute attr = nonPreferred.getAttribute(type);
            if (attr != null) {
                attr.setVoided(true);
                attr.setDateVoided(new Date());
                attr.setVoidReason("Merging into patient " + preferred.getId());
                attr.setVoidedBy(Context.getAuthenticatedUser());
            }
            // no need to save, because it will be committed as part of the merge
        }
    }

    private void voidMostRecentRegistrationIfNonPreferred(Patient preferred, Patient nonPreferred) {

        EncounterType registrationEncounterType = encounterService.getEncounterTypeByUuid(PihEmrConfigConstants.ENCOUNTERTYPE_PATIENT_REGISTRATION_UUID);

        // the getEncounters method returns encounters sorted by date
        List<Encounter> preferredRegistrations = encounterService.getEncounters(preferred, null, null, null, null,
                Collections.singleton(registrationEncounterType), null, null, null, false);

        List<Encounter> nonPreferredRegistrations = encounterService.getEncounters(nonPreferred, null, null, null, null,
                Collections.singleton(registrationEncounterType), null, null, null, false);

        Encounter mostRecentPreferredRegistration = (preferredRegistrations != null && preferredRegistrations.size() > 0)
                ? preferredRegistrations.get(preferredRegistrations.size() - 1) : null;

        if (nonPreferredRegistrations != null && mostRecentPreferredRegistration != null) {
            for (Encounter nonPreferredRegistration : nonPreferredRegistrations) {
                if (nonPreferredRegistration.getEncounterDatetime().after(mostRecentPreferredRegistration.getEncounterDatetime())) {
                    encounterService.voidEncounter(nonPreferredRegistration, "merging into patient " + preferred.getId() + ": voiding most recent registration encounter on non-preferred patient");
                }
            }
        }
    }

    /**
     * Move queue entries that are not associated with visits (we likely do not have any of these, but this is a sanity check).
     * Those that are associated with visits are handled within the PihVisitMergeActions class
     * TODO: This likely belongs in emrapi or queue modules, we should consider moving it down the road
     */
    private void moveQueueEntriesNotAssociatedWithVisits(Patient preferred, Patient nonPreferred) {
        QueueEntrySearchCriteria c = new QueueEntrySearchCriteria();
        c.setPatient(nonPreferred);
        c.setHasVisit(false);
        List<QueueEntry> queueEntries = queueEntryService.getQueueEntries(c);
        for (QueueEntry queueEntry: queueEntries) {
            log.warn("Moving queue entry " + queueEntry.getId() + " from patient " + nonPreferred.getId() + " to " + preferred.getId());
            queueEntry.setPatient(preferred);
            queueEntryService.saveQueueEntry(queueEntry);
        }
    }

    public void setEncounterService(EncounterService encounterService) {
        this.encounterService = encounterService;
    }
}
