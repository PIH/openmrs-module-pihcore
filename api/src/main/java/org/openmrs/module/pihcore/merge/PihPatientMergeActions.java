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

@Component("pihPatientMergeActions")
public class PihPatientMergeActions implements PatientMergeAction {

    @Autowired
    private PersonService personService;

    @Autowired
    private EncounterService encounterService;

    @Override
    public void beforeMergingPatients(Patient preferred, Patient nonPreferred) {
        // void telephone number and mother's name on non-preferred patient if present on preferred paitient
        voidNonPreferredAttribute(personService.getPersonAttributeTypeByUuid(PersonAttributeTypes.TELEPHONE_NUMBER.uuid()), preferred, nonPreferred);
        voidNonPreferredAttribute(personService.getPersonAttributeTypeByUuid(PersonAttributeTypes.MOTHERS_FIRST_NAME.uuid()), preferred, nonPreferred);
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

        EncounterType registration = encounterService.getEncounterTypeByUuid(EncounterTypes.PATIENT_REGISTRATION.uuid());

        // the getEncounters method returns encounters sorted by date
        List<Encounter> preferredRegistration = encounterService.getEncounters(preferred, null, null, null, null,
                Collections.singleton(registration), null, null, null, false);

        List<Encounter> nonPreferredRegistration = encounterService.getEncounters(nonPreferred, null, null, null, null,
                Collections.singleton(registration), null, null, null, false);

        Encounter mostRecentPreferredRegistration = (preferredRegistration != null && preferredRegistration.size() > 0)
                ? preferredRegistration.get(preferredRegistration.size() - 1) : null;

        Encounter mostRecentNonPreferredRegistration = (nonPreferredRegistration != null && nonPreferredRegistration.size() > 0)
                ? nonPreferredRegistration.get(nonPreferredRegistration.size() - 1) : null;

        if (mostRecentPreferredRegistration != null && mostRecentNonPreferredRegistration != null
                && mostRecentPreferredRegistration.getEncounterDatetime().before(mostRecentNonPreferredRegistration.getEncounterDatetime())) {
            // if the most recent registration encounter is on the non-preferred patient, void it
            encounterService.voidEncounter(mostRecentNonPreferredRegistration, "merging into patient " + preferred.getId() + ": voiding most recent registration encounter on non-preferred patient");
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
