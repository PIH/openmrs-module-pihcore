package org.openmrs.module.pihcore.htmlformentry.action;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.openmrs.Concept;
import org.openmrs.Location;
import org.openmrs.Obs;
import org.openmrs.Provider;
import org.openmrs.api.context.Context;
import org.openmrs.module.appointments.model.Appointment;
import org.openmrs.module.appointments.model.AppointmentKind;
import org.openmrs.module.appointments.model.AppointmentProvider;
import org.openmrs.module.appointments.model.AppointmentProviderResponse;
import org.openmrs.module.appointments.model.AppointmentServiceDefinition;
import org.openmrs.module.appointments.service.AppointmentServiceDefinitionService;
import org.openmrs.module.appointments.service.AppointmentsService;
import org.openmrs.module.htmlformentry.CustomFormSubmissionAction;
import org.openmrs.module.htmlformentry.FormEntryContext.Mode;
import org.openmrs.module.htmlformentry.FormEntrySession;
import org.openmrs.module.htmlformentry.HtmlFormEntryUtil;

/**
 * Creates a new appointment based on concepts in the set "PIH:Scheduled visit information".
 * By design, the obs are only used to create an appointment in ENTER mode, and editting
 * the obs does not modify the appointment.
 */
public class ScheduleAppointmentAction implements CustomFormSubmissionAction {

    static final String SCHEDULE_APPOINTMENT_CONCEPT = "PIH:Schedule appointment";
    static final String APPOINTMENT_GROUP_CONCEPT    = "PIH:Scheduled visit construct";
    static final String APPOINTMENT_DATETIME_CONCEPT = "PIH:Return visit datetime";
    static final String APPOINTMENT_LOCATION_CONCEPT = "PIH:Location";
    static final String APPOINTMENT_SERVICE_CONCEPT  = "PIH:Appointment service";
    static final String APPOINTMENT_TYPE_CONCEPT     = "PIH:Visit type";
    static final String APPOINTMENT_PROVIDER_CONCEPT = "PIH:Provider name non-coded";
    static final String APPOINTMENT_NOTES_CONCEPT    = "PIH:GENERAL FREE TEXT";

    @Override
    public void applyAction(FormEntrySession session) {
        if (session.getContext().getMode() != Mode.ENTER) {
            return;
        }

        if (!isScheduleAppointmentYes(session)) {
            return;
        }

        Obs appointmentGroup = findObsGroup(session);
        if (appointmentGroup == null) {
            return;
        }

        Date startDateTime = getDatetimeValue(appointmentGroup, APPOINTMENT_DATETIME_CONCEPT);
        String locationText = getTextValue(appointmentGroup, APPOINTMENT_LOCATION_CONCEPT);
        String serviceUuid  = getTextValue(appointmentGroup, APPOINTMENT_SERVICE_CONCEPT);
        Concept typeValue   = getCodedValue(appointmentGroup, APPOINTMENT_TYPE_CONCEPT);
        String providerText = getTextValue(appointmentGroup, APPOINTMENT_PROVIDER_CONCEPT);
        String notes        = getTextValue(appointmentGroup, APPOINTMENT_NOTES_CONCEPT);

        if (startDateTime == null || locationText == null || serviceUuid == null) {
            return;
        }

        Location location = HtmlFormEntryUtil.getLocation(locationText);
        AppointmentServiceDefinition service = getAppointmentService(serviceUuid);

        if (location == null || service == null) {
            return;
        }

        Appointment appointment = new Appointment();
        appointment.setPatient(session.getEncounter().getPatient());
        appointment.setLocation(location);
        appointment.setService(service);
        appointment.setStartDateTime(startDateTime);
        if (service.getDurationMins() != null) {
            appointment.setEndDateTime(new Date(startDateTime.getTime() + (long) service.getDurationMins() * 60 * 1000));
        }
        AppointmentKind kind = getAppointmentKind(typeValue);
        if (kind != null) {
            appointment.setAppointmentKind(kind);
        }
        if (notes != null) {
            appointment.setComments(notes);
        }
        if (providerText != null) {
            Provider provider = HtmlFormEntryUtil.getProvider(providerText);
            if (provider != null) {
                AppointmentProvider appointmentProvider = new AppointmentProvider();
                appointmentProvider.setProvider(provider);
                appointmentProvider.setResponse(AppointmentProviderResponse.ACCEPTED);
                appointmentProvider.setAppointment(appointment);
                Set<AppointmentProvider> providers = new HashSet<>();
                providers.add(appointmentProvider);
                appointment.setProviders(providers);
            }
        }

        Context.getService(AppointmentsService.class).validateAndSave(appointment);
    }

    private boolean isScheduleAppointmentYes(FormEntrySession session) {
        Concept yesConcept = HtmlFormEntryUtil.getConcept("PIH:YES");
        for (Obs obs : session.getEncounter().getObsAtTopLevel(false)) {
            if (obs.getConcept().equals(HtmlFormEntryUtil.getConcept(SCHEDULE_APPOINTMENT_CONCEPT))) {
                return yesConcept.equals(obs.getValueCoded());
            }
        }
        return false;
    }

    private Obs findObsGroup(FormEntrySession session) {
        for (Obs obs : session.getEncounter().getObsAtTopLevel(false)) {
            if (obs.getConcept().equals(HtmlFormEntryUtil.getConcept(APPOINTMENT_GROUP_CONCEPT))) {
                return obs;
            }
        }
        return null;
    }

    private Obs findMemberObs(Obs group, String conceptMapping) {
        for (Obs member : group.getGroupMembers(false)) {
            if (member.getConcept().equals(HtmlFormEntryUtil.getConcept(conceptMapping))) {
                return member;
            }
        }
        return null;
    }

    private Date getDatetimeValue(Obs group, String conceptMapping) {
        Obs obs = findMemberObs(group, conceptMapping);
        return obs != null ? obs.getValueDatetime() : null;
    }

    private String getTextValue(Obs group, String conceptMapping) {
        Obs obs = findMemberObs(group, conceptMapping);
        return obs != null ? obs.getValueText() : null;
    }

    private Concept getCodedValue(Obs group, String conceptMapping) {
        Obs obs = findMemberObs(group, conceptMapping);
        return obs != null ? obs.getValueCoded() : null;
    }

    private AppointmentKind getAppointmentKind(Concept concept) {
        if (concept == null) {
            return null;
        }
        if (concept.equals(HtmlFormEntryUtil.getConcept("PIH:SCHEDULED VISIT"))) {
            return AppointmentKind.Scheduled;
        }
        if (concept.equals(HtmlFormEntryUtil.getConcept("PIH:Walk-in"))) {
            return AppointmentKind.WalkIn;
        }
        if (concept.equals(HtmlFormEntryUtil.getConcept("CIEL:166997"))) {
            return AppointmentKind.Virtual;
        }
        return null;
    }

    private AppointmentServiceDefinition getAppointmentService(String uuid) {
        return Context.getService(AppointmentServiceDefinitionService.class)
                .getAllAppointmentServices(false)
                .stream()
                .filter(s -> s.getUuid().equals(uuid))
                .findFirst()
                .orElse(null);
    }
}
