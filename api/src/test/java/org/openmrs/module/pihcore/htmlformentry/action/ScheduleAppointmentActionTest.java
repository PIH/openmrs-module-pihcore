package org.openmrs.module.pihcore.htmlformentry.action;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.openmrs.Concept;
import org.openmrs.Encounter;
import org.openmrs.Location;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.Provider;
import org.openmrs.api.context.Context;
import org.openmrs.module.appointments.model.Appointment;
import org.openmrs.module.appointments.model.AppointmentServiceDefinition;
import org.openmrs.module.appointments.service.AppointmentServiceDefinitionService;
import org.openmrs.module.appointments.service.AppointmentsService;
import org.openmrs.module.htmlformentry.FormEntryContext;
import org.openmrs.module.htmlformentry.FormEntrySession;
import org.openmrs.module.htmlformentry.HtmlFormEntryUtil;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

@RunWith(PowerMockRunner.class)
@PrepareForTest({Context.class, HtmlFormEntryUtil.class})
@PowerMockIgnore({"javax.management.*", "org.apache.*", "org.slf4j.*"})
public class ScheduleAppointmentActionTest {

    private FormEntrySession mockSession;
    private FormEntryContext mockContext;
    private ScheduleAppointmentAction action;

    private AppointmentsService mockAppointmentsService;
    private AppointmentServiceDefinitionService mockAppointmentServiceDefService;

    private Concept yesConcept;
    private Concept scheduleConcept;
    private Concept groupConcept;
    private Concept datetimeConcept;
    private Concept locationConcept;
    private Concept serviceConcept;
    private Concept typeConcept;
    private Concept providerConcept;
    private Concept notesConcept;

    private Patient patient;
    private Encounter encounter;

    @Before
    public void setUp() {
        mockSession = mock(FormEntrySession.class);
        mockContext = mock(FormEntryContext.class);
        when(mockSession.getContext()).thenReturn(mockContext);
        when(mockContext.getMode()).thenReturn(FormEntryContext.Mode.ENTER);

        patient = new Patient();
        encounter = new Encounter();
        encounter.setPatient(patient);
        when(mockSession.getEncounter()).thenReturn(encounter);

        mockStatic(Context.class);
        mockStatic(HtmlFormEntryUtil.class);

        mockAppointmentsService = mock(AppointmentsService.class);
        mockAppointmentServiceDefService = mock(AppointmentServiceDefinitionService.class);
        when(Context.getService(AppointmentsService.class)).thenReturn(mockAppointmentsService);
        when(Context.getService(AppointmentServiceDefinitionService.class)).thenReturn(mockAppointmentServiceDefService);

        yesConcept = new Concept();
        scheduleConcept = new Concept();
        groupConcept = new Concept();
        datetimeConcept = new Concept();
        locationConcept = new Concept();
        serviceConcept = new Concept();
        typeConcept = new Concept();
        providerConcept = new Concept();
        notesConcept = new Concept();

        when(HtmlFormEntryUtil.getConcept("PIH:YES")).thenReturn(yesConcept);
        when(HtmlFormEntryUtil.getConcept(ScheduleAppointmentAction.SCHEDULE_APPOINTMENT_CONCEPT)).thenReturn(scheduleConcept);
        when(HtmlFormEntryUtil.getConcept(ScheduleAppointmentAction.APPOINTMENT_GROUP_CONCEPT)).thenReturn(groupConcept);
        when(HtmlFormEntryUtil.getConcept(ScheduleAppointmentAction.APPOINTMENT_DATETIME_CONCEPT)).thenReturn(datetimeConcept);
        when(HtmlFormEntryUtil.getConcept(ScheduleAppointmentAction.APPOINTMENT_LOCATION_CONCEPT)).thenReturn(locationConcept);
        when(HtmlFormEntryUtil.getConcept(ScheduleAppointmentAction.APPOINTMENT_SERVICE_CONCEPT)).thenReturn(serviceConcept);
        when(HtmlFormEntryUtil.getConcept(ScheduleAppointmentAction.APPOINTMENT_TYPE_CONCEPT)).thenReturn(typeConcept);
        when(HtmlFormEntryUtil.getConcept(ScheduleAppointmentAction.APPOINTMENT_PROVIDER_CONCEPT)).thenReturn(providerConcept);
        when(HtmlFormEntryUtil.getConcept(ScheduleAppointmentAction.APPOINTMENT_NOTES_CONCEPT)).thenReturn(notesConcept);

        action = new ScheduleAppointmentAction();
    }

    @Test
    public void applyAction_shouldCreateAppointmentWhenAllRequiredFieldsPresent() {
        Date startDate = new Date();
        Location location = new Location();
        AppointmentServiceDefinition serviceDef = new AppointmentServiceDefinition();
        Provider provider = new Provider();

        setupEncounterObs(startDate, "location-ref", "service-uuid", "provider-ref");
        when(HtmlFormEntryUtil.getLocation("location-ref")).thenReturn(location);
        when(mockAppointmentServiceDefService.getAppointmentServiceByUuid("service-uuid")).thenReturn(serviceDef);
        when(HtmlFormEntryUtil.getProvider("provider-ref")).thenReturn(provider);

        action.applyAction(mockSession);

        ArgumentCaptor<Appointment> captor = ArgumentCaptor.forClass(Appointment.class);
        verify(mockAppointmentsService).validateAndSave(captor.capture());
        Appointment saved = captor.getValue();
        assertEquals(patient, saved.getPatient());
        assertEquals(location, saved.getLocation());
        assertEquals(serviceDef, saved.getService());
        assertEquals(startDate, saved.getStartDateTime());
        assertNotNull(saved.getProviders());
        assertEquals(1, saved.getProviders().size());
        assertEquals(provider, saved.getProviders().iterator().next().getProvider());
    }

    @Test
    public void applyAction_shouldDoNothingInEditMode() {
        when(mockContext.getMode()).thenReturn(FormEntryContext.Mode.EDIT);
        action.applyAction(mockSession);
        verify(mockAppointmentsService, never()).validateAndSave(any(Appointment.class));
    }

    @Test
    public void applyAction_shouldDoNothingWhenScheduleAppointmentIsNo() {
        Obs scheduleObs = new Obs();
        scheduleObs.setConcept(scheduleConcept);
        scheduleObs.setValueCoded(new Concept());
        encounter.addObs(scheduleObs);

        action.applyAction(mockSession);

        verify(mockAppointmentsService, never()).validateAndSave(any(Appointment.class));
    }

    @Test(expected = IllegalStateException.class)
    public void applyAction_shouldThrowWhenRequiredObsMissing() {
        Obs scheduleObs = new Obs();
        scheduleObs.setConcept(scheduleConcept);
        scheduleObs.setValueCoded(yesConcept);
        encounter.addObs(scheduleObs);

        Obs group = new Obs();
        group.setConcept(groupConcept);
        encounter.addObs(group);

        action.applyAction(mockSession);
    }

    @Test(expected = IllegalStateException.class)
    public void applyAction_shouldThrowWhenLocationNotFound() {
        setupEncounterObs(new Date(), "unknown-location", "service-uuid", "provider-ref");
        when(HtmlFormEntryUtil.getLocation("unknown-location")).thenReturn(null);
        when(mockAppointmentServiceDefService.getAppointmentServiceByUuid("service-uuid")).thenReturn(new AppointmentServiceDefinition());
        when(HtmlFormEntryUtil.getProvider("provider-ref")).thenReturn(new Provider());

        action.applyAction(mockSession);
    }

    @Test(expected = IllegalStateException.class)
    public void applyAction_shouldThrowWhenServiceNotFound() {
        setupEncounterObs(new Date(), "location-ref", "bad-uuid", "provider-ref");
        when(HtmlFormEntryUtil.getLocation("location-ref")).thenReturn(new Location());
        when(mockAppointmentServiceDefService.getAppointmentServiceByUuid("bad-uuid")).thenReturn(null);

        action.applyAction(mockSession);
    }

    @Test(expected = IllegalStateException.class)
    public void applyAction_shouldThrowWhenProviderNotFound() {
        setupEncounterObs(new Date(), "location-ref", "service-uuid", "unknown-provider");
        when(HtmlFormEntryUtil.getLocation("location-ref")).thenReturn(new Location());
        when(mockAppointmentServiceDefService.getAppointmentServiceByUuid("service-uuid")).thenReturn(new AppointmentServiceDefinition());
        when(HtmlFormEntryUtil.getProvider("unknown-provider")).thenReturn(null);

        action.applyAction(mockSession);
    }

    private void setupEncounterObs(Date startDate, String locationRef, String serviceRef, String providerRef) {
        Obs scheduleObs = new Obs();
        scheduleObs.setConcept(scheduleConcept);
        scheduleObs.setValueCoded(yesConcept);

        Obs group = new Obs();
        group.setConcept(groupConcept);

        Obs dateObs = new Obs();
        dateObs.setConcept(datetimeConcept);
        dateObs.setValueDatetime(startDate);
        group.addGroupMember(dateObs);

        Obs locationObs = new Obs();
        locationObs.setConcept(locationConcept);
        locationObs.setValueText(locationRef);
        group.addGroupMember(locationObs);

        Obs serviceObs = new Obs();
        serviceObs.setConcept(serviceConcept);
        serviceObs.setValueText(serviceRef);
        group.addGroupMember(serviceObs);

        Obs providerObs = new Obs();
        providerObs.setConcept(providerConcept);
        providerObs.setValueText(providerRef);
        group.addGroupMember(providerObs);

        encounter.addObs(scheduleObs);
        encounter.addObs(group);
    }
}
