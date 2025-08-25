package org.openmrs.module.pihcore.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openmrs.Location;
import org.openmrs.Patient;
import org.openmrs.PersonAttribute;
import org.openmrs.PersonAttributeType;
import org.openmrs.api.context.Context;
import org.openmrs.module.pihcore.PihCoreContextSensitiveTest;
import org.openmrs.module.pihcore.PihEmrConfigConstants;
import org.openmrs.module.pihcore.config.Config;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class PihCoreServiceImplTest extends PihCoreContextSensitiveTest {

    @BeforeEach
    public void setup() {
        Config config = mock(Config.class);
        when(config.isHaiti()).thenReturn(true);
        Context.getService(PihCoreService.class).setConfig(config);
        executeDataSet("pihCoreServiceTestDataset.xml");
    }


    @Test
    public void shouldUpdatePatientHealthCenterIfPatientEnrolledInHIVProgram() {
        Patient patient = Context.getPatientService().getPatient(2);  // patient from standard test dataset
        Context.getService(PihCoreService.class).updateHealthCenter(patient);
        PersonAttributeType healthCenterAttributeType = Context.getPersonService().getPersonAttributeTypeByUuid(PihEmrConfigConstants.PERSONATTRIBUTETYPE_HEALTH_CENTER_UUID);

        // this is a location set up in the standard test database
        Location xanadu = Context.getLocationService().getLocation(2);
        PersonAttribute healthCenter = patient.getAttribute(healthCenterAttributeType);
        assertNotNull(healthCenter);
        assertThat(healthCenter.getHydratedObject(), is(xanadu));
    }

    @Test
    public void shouldNotUpdatePatientHealthCenterIfPatientEnrolledInHIVProgram() {
        Patient patient = Context.getPatientService().getPatient(6);  // another patient from standard test dataset
        Context.getService(PihCoreService.class).updateHealthCenter(patient);
        PersonAttributeType healthCenterAttributeType = Context.getPersonService().getPersonAttributeTypeByUuid(PihEmrConfigConstants.PERSONATTRIBUTETYPE_HEALTH_CENTER_UUID);
        PersonAttribute healthCenter = patient.getAttribute(healthCenterAttributeType);
        assertNull(healthCenter);
    }

}
