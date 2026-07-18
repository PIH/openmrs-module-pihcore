package org.openmrs.module.pihcore.encounter;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockedStatic;
import org.openmrs.Concept;
import org.openmrs.Encounter;
import org.openmrs.Obs;
import org.openmrs.User;
import org.openmrs.api.ConceptService;
import org.openmrs.api.context.Context;
import org.openmrs.module.appointments.model.Appointment;
import org.openmrs.module.appointments.model.AppointmentStatus;
import org.openmrs.module.appointments.service.AppointmentsService;

import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class PihEncounterSaveHandlerTest {

    private PihEncounterSaveHandler handler;

    private ConceptService mockConceptService;
    private AppointmentsService mockAppointmentsService;

    private Concept uuidConcept;

    private User user;
    private Date date;

    private MockedStatic<Context> mockedContext;

    @Before
    public void setUp() {
        handler = new PihEncounterSaveHandler();

        mockConceptService = mock(ConceptService.class);
        mockAppointmentsService = mock(AppointmentsService.class);

        uuidConcept = new Concept();

        mockedContext = mockStatic(Context.class);
        mockedContext.when(Context::getConceptService).thenReturn(mockConceptService);
        mockedContext.when(() -> Context.getService(AppointmentsService.class)).thenReturn(mockAppointmentsService);
        when(mockConceptService.getConceptByMapping("Appointment uuid", "PIH")).thenReturn(uuidConcept);

        user = new User();
        date = new Date();
    }

    @After
    public void tearDown() {
        mockedContext.close();
    }

    @Test
    public void handle_encounterNotVoided_doesNotTouchAppointments() {
        Encounter encounter = new Encounter();
        encounter.setId(1);
        encounter.setVoided(false);

        handler.handle(encounter, user, date, "reason");

        verify(mockConceptService, never()).getConceptByMapping(anyString(), anyString());
    }

    @Test
    public void handle_newEncounterNoId_doesNotTouchAppointments() {
        Encounter encounter = new Encounter();
        encounter.setVoided(true);
        // encounterId is null — handler should be a no-op for unsaved encounters

        handler.handle(encounter, user, date, "reason");

        verify(mockConceptService, never()).getConceptByMapping(anyString(), anyString());
    }

    @Test
    public void handle_voidedEncounterWithNoAppointmentObs_doesNothing() {
        Encounter encounter = new Encounter();
        encounter.setId(1);
        encounter.setVoided(true);
        // encounter has no obs at all

        handler.handle(encounter, user, date, "reason");

        verify(mockAppointmentsService, never()).getAppointmentByUuid(anyString());
    }

    @Test
    public void handle_voidedEncounterWithActiveAppointment_voidsAppointment() {
        String apptUuid = "appt-uuid-123";
        Encounter encounter = buildVoidedEncounterWithUuidObs(apptUuid);

        Appointment appointment = new Appointment();
        appointment.setUuid(apptUuid);
        appointment.setVoided(false);
        appointment.setStatus(AppointmentStatus.Scheduled);
        when(mockAppointmentsService.getAppointmentByUuid(apptUuid)).thenReturn(appointment);

        handler.handle(encounter, user, date, "reason");

        assertTrue(appointment.getVoided());
        assertEquals(user, appointment.getVoidedBy());
        assertEquals(date, appointment.getDateVoided());
        assertEquals("Encounter voided", appointment.getVoidReason());
    }

    @Test
    public void handle_voidedEncounterWithAlreadyVoidedAppointment_skips() {
        String apptUuid = "appt-uuid-456";
        Encounter encounter = buildVoidedEncounterWithUuidObs(apptUuid);

        Appointment appointment = new Appointment();
        appointment.setUuid(apptUuid);
        appointment.setVoided(true);
        when(mockAppointmentsService.getAppointmentByUuid(apptUuid)).thenReturn(appointment);

        handler.handle(encounter, user, date, "reason");

        // voidedBy was never set by the handler
        assertNull(appointment.getVoidedBy());
    }

    @Test
    public void handle_voidedEncounterWithCancelledAppointment_skips() {
        String apptUuid = "appt-uuid-789";
        Encounter encounter = buildVoidedEncounterWithUuidObs(apptUuid);

        Appointment appointment = new Appointment();
        appointment.setUuid(apptUuid);
        appointment.setVoided(false);
        appointment.setStatus(AppointmentStatus.Cancelled);
        when(mockAppointmentsService.getAppointmentByUuid(apptUuid)).thenReturn(appointment);

        handler.handle(encounter, user, date, "reason");

        assertFalse(appointment.getVoided());
    }

    @Test
    public void handle_voidedEncounterWithMissingAppointment_skips() {
        String apptUuid = "gone-uuid";
        Encounter encounter = buildVoidedEncounterWithUuidObs(apptUuid);

        when(mockAppointmentsService.getAppointmentByUuid(apptUuid)).thenReturn(null);

        // should not throw
        handler.handle(encounter, user, date, "reason");
    }

    // --- helpers ---

    /**
     * Builds a voided encounter with a top-level "PIH:Appointment uuid" obs holding the given UUID.
     */
    private Encounter buildVoidedEncounterWithUuidObs(String appointmentUuid) {
        Encounter encounter = new Encounter();
        encounter.setId(1);
        encounter.setVoided(true);

        Obs uuidObs = new Obs();
        uuidObs.setConcept(uuidConcept);
        uuidObs.setValueText(appointmentUuid);
        encounter.addObs(uuidObs);

        return encounter;
    }
}
