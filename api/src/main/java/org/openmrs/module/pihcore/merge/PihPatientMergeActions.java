package org.openmrs.module.pihcore.merge;

import org.openmrs.Encounter;
import org.openmrs.EncounterType;
import org.openmrs.Patient;
import org.openmrs.PersonAttribute;
import org.openmrs.PersonAttributeType;
import org.openmrs.api.EncounterService;
import org.openmrs.api.PersonService;
import org.openmrs.api.context.Context;
import org.openmrs.module.emrapi.merge.PatientMergeAction;
import org.openmrs.module.pihcore.metadata.core.EncounterTypes;
import org.openmrs.module.pihcore.metadata.core.PersonAttributeTypes;
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
 */
@Component("pihPatientMergeActions")
public class PihPatientMergeActions implements PatientMergeAction {

    @Autowired
    private PersonService personService;

    @Autowired
    private EncounterService encounterService;

    @Override
    public void beforeMergingPatients(Patient preferred, Patient nonPreferred) {
        // void telephone number and mother's name on non-preferred patient if present on preferred patient
        voidNonPreferredAttribute(personService.getPersonAttributeTypeByUuid(PersonAttributeTypes.TELEPHONE_NUMBER.uuid()), preferred, nonPreferred);
        voidNonPreferredAttribute(personService.getPersonAttributeTypeByUuid(PersonAttributeTypes.MOTHERS_FIRST_NAME.uuid()), preferred, nonPreferred);

        // make sure the most recent registration encounter belongs to the preferred patient.
        voidMostRecentRegistrationIfNonPreferred(preferred, nonPreferred);
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
            // dont need to save, because it will be committed as part of the merge?
        }
    }

    private void voidMostRecentRegistrationIfNonPreferred(Patient preferred, Patient nonPreferred) {

        EncounterType registrationEncounterType = encounterService.getEncounterTypeByUuid(EncounterTypes.PATIENT_REGISTRATION.uuid());

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


    // for injecting mocks during tests
    public void setPersonService(PersonService personService) {
        this.personService = personService;
    }

    public void setEncounterService(EncounterService encounterService) {
        this.encounterService = encounterService;
    }
}
