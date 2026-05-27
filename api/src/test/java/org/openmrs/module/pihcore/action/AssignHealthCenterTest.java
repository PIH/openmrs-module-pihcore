package org.openmrs.module.pihcore.action;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openmrs.Location;
import org.openmrs.Patient;
import org.openmrs.PersonAttribute;
import org.openmrs.PersonAttributeType;
import org.openmrs.api.PatientService;
import org.openmrs.api.PersonService;
import org.openmrs.module.emrapi.adt.AdtService;
import org.openmrs.module.pihcore.PihEmrConfigConstants;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class AssignHealthCenterTest {

    private PersonService personService;
    private PatientService patientService;
    private AdtService adtService;

    private Location sessionLocation;
    private Location visitLocation;
    private PersonAttributeType healthCenterAttrType;
    private Patient patient;
    private AssignHealthCenter action;

    @BeforeEach
    public void setup() {
        personService = mock(PersonService.class);
        patientService = mock(PatientService.class);
        adtService = mock(AdtService.class);

        sessionLocation = new Location();
        sessionLocation.setId(10);

        visitLocation = new Location();
        visitLocation.setId(5);
        visitLocation.setName("Mirebalais");

        healthCenterAttrType = new PersonAttributeType();
        healthCenterAttrType.setUuid(PihEmrConfigConstants.PERSONATTRIBUTETYPE_HEALTH_CENTER_UUID);

        patient = new Patient();

        // anonymous subclass overrides the two protected static-Context wrappers
        action = new AssignHealthCenter() {
            @Override
            protected Location getSessionLocation() { return sessionLocation; }
            @Override
            protected AdtService getAdtService() { return adtService; }
        };

        ReflectionTestUtils.setField(action, "personService", personService);
        ReflectionTestUtils.setField(action, "patientService", patientService);

        // default happy-path stubs
        when(adtService.getLocationThatSupportsVisits(sessionLocation)).thenReturn(visitLocation);
        when(personService.getPersonAttributeTypeByUuid(PihEmrConfigConstants.PERSONATTRIBUTETYPE_HEALTH_CENTER_UUID))
                .thenReturn(healthCenterAttrType);
    }

    @Test
    public void shouldSetHealthCenterAttribute() {
        action.afterPatientCreated(patient, Collections.emptyMap());

        verify(patientService).savePatient(patient);

        PersonAttribute attr = patient.getActiveAttributes().stream()
                .filter(a -> a.getAttributeType().equals(healthCenterAttrType))
                .findFirst().orElse(null);
        assertNotNull(attr);
        assertEquals(String.valueOf(visitLocation.getId()), attr.getValue());
    }

    @Test
    public void shouldDoNothingWhenSessionLocationIsNull() {
        sessionLocation = null;

        action.afterPatientCreated(patient, Collections.emptyMap());

        verify(patientService, never()).savePatient(patient);
    }

    @Test
    public void shouldDoNothingWhenNoVisitLocationFound() {
        when(adtService.getLocationThatSupportsVisits(sessionLocation)).thenReturn(null);

        action.afterPatientCreated(patient, Collections.emptyMap());

        verify(patientService, never()).savePatient(patient);
    }

    @Test
    public void shouldDoNothingWhenHealthCenterAttributeTypeNotFound() {
        when(personService.getPersonAttributeTypeByUuid(PihEmrConfigConstants.PERSONATTRIBUTETYPE_HEALTH_CENTER_UUID))
                .thenReturn(null);

        action.afterPatientCreated(patient, Collections.emptyMap());

        verify(patientService, never()).savePatient(patient);
    }
}
