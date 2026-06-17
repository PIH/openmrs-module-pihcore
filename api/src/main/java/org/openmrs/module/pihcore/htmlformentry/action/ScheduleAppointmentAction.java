package org.openmrs.module.pihcore.htmlformentry.action;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
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
 * Only runs in ENTER mode — editing the form does not update or void the appointment.
 * This is intentional: appointments may be modified independently after creation,
 * so re-syncing from obs on edit could overwrite legitimate changes.
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

        // SCHEDULE_APPOINTMENT_CONCEPT is a top-level yes/no obs; the appointment detail
        // construct is only present when the answer is Yes
        if (!isScheduleAppointmentYes(session)) {
            return;
        }

        Obs appointmentGroup = findObsGroup(session);
        if (appointmentGroup == null) {
            return;
        }

        Date startDateTime = getDatetimeValue(appointmentGroup, APPOINTMENT_DATETIME_CONCEPT);
        String locationRef = getTextValue(appointmentGroup, APPOINTMENT_LOCATION_CONCEPT);
        String serviceRef  = getTextValue(appointmentGroup, APPOINTMENT_SERVICE_CONCEPT);
        Concept typeValue  = getCodedValue(appointmentGroup, APPOINTMENT_TYPE_CONCEPT);
        String providerRef = getTextValue(appointmentGroup, APPOINTMENT_PROVIDER_CONCEPT);
        String notes       = getTextValue(appointmentGroup, APPOINTMENT_NOTES_CONCEPT);

        if (startDateTime == null || locationRef == null || serviceRef == null || StringUtils.isBlank(providerRef)) {
            throw new IllegalStateException("ScheduleAppointmentAction requires startDateTime, location, service, and provider obs");
        }

        Location location = HtmlFormEntryUtil.getLocation(locationRef);
        if (location == null) {
            throw new IllegalStateException("ScheduleAppointmentAction: no location found for ref: " + locationRef);
        }

        AppointmentServiceDefinition service = Context.getService(AppointmentServiceDefinitionService.class).getAppointmentServiceByUuid(serviceRef);
        if (service == null) {
            throw new IllegalStateException("ScheduleAppointmentAction: no appointment service found for ref: " + serviceRef);
        }

        Appointment appointment = new Appointment();
        appointment.setPatient(session.getEncounter().getPatient());
        appointment.setLocation(location);
        appointment.setService(service);
        appointment.setStartDateTime(startDateTime);
        if (service.getDurationMins() != null) {
            appointment.setEndDateTime(new Date(startDateTime.getTime() + (long) service.getDurationMins() * 60 * 1000));
        }
        appointment.setAppointmentKind(getAppointmentKind(typeValue));
        appointment.setComments(notes);

        Provider provider = HtmlFormEntryUtil.getProvider(providerRef);
        if (provider == null) {
            throw new IllegalStateException("ScheduleAppointmentAction: no provider found for ref: " + providerRef);
        }
        AppointmentProvider appointmentProvider = new AppointmentProvider();
        appointmentProvider.setProvider(provider);
        appointmentProvider.setResponse(AppointmentProviderResponse.ACCEPTED);
        appointmentProvider.setAppointment(appointment);
        Set<AppointmentProvider> providers = new HashSet<>();
        providers.add(appointmentProvider);
        appointment.setProviders(providers);

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
        if (group.getGroupMembers(false) == null) {
            return null;
        }
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

}
