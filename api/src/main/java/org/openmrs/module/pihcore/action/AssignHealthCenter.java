package org.openmrs.module.pihcore.action;

import org.openmrs.Location;
import org.openmrs.Patient;
import org.openmrs.PersonAttribute;
import org.openmrs.PersonAttributeType;
import org.openmrs.api.PatientService;
import org.openmrs.api.PersonService;
import org.openmrs.api.context.Context;
import org.openmrs.module.emrapi.adt.AdtService;
import org.openmrs.module.pihcore.PihEmrConfigConstants;
import org.openmrs.module.registrationapp.action.AfterPatientCreatedAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * AfterPatientCreatedAction that sets the "Health Center" person attribute to the Visit Location
 * associated with the current user's session location.
 *
 * NOTE: This bean is currently only wired in for the Haiti CENTRAL server. Country/site
 * checking is handled by selective bean wiring rather than a runtime guard.
 *
 * If any required information is unavailable (no session location, no Visit Location ancestor,
 * or the attribute type is not found) the action silently does nothing.
 */
@Component("assignHealthCenter")
public class AssignHealthCenter implements AfterPatientCreatedAction {

    private final Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
    private PersonService personService;

    @Autowired
    private PatientService patientService;

    @Override
    public void afterPatientCreated(Patient patient, Map<String, String[]> map) {

        Location sessionLocation = getSessionLocation();
        if (sessionLocation == null) {
            log.warn("No session location found; skipping health center assignment for patient {}", patient);
            return;
        }

        Location visitLocation = getAdtService().getLocationThatSupportsVisits(sessionLocation);
        if (visitLocation == null) {
            log.warn("No Visit Location found for session location {}; skipping health center assignment for patient {}",
                    sessionLocation, patient);
            return;
        }

        PersonAttributeType healthCenterAttrType = personService.getPersonAttributeTypeByUuid(
                PihEmrConfigConstants.PERSONATTRIBUTETYPE_HEALTH_CENTER_UUID);
        if (healthCenterAttrType == null) {
            log.warn("Health Center person attribute type not found; skipping health center assignment for patient {}", patient);
            return;
        }

        patient.addAttribute(new PersonAttribute(healthCenterAttrType, visitLocation.getId().toString()));
        patientService.savePatient(patient);
    }

    /**
     * Returns the current user's session location.
     * Protected to allow overriding in tests without requiring PowerMock.
     */
    protected Location getSessionLocation() {
        return Context.getUserContext().getLocation();
    }

    /**
     * Returns the AdtService.
     * Protected to allow overriding in tests without requiring PowerMock.
     */
    protected AdtService getAdtService() {
        return Context.getService(AdtService.class);
    }
}
