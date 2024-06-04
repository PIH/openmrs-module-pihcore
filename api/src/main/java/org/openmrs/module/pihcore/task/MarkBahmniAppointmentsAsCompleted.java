package org.openmrs.module.pihcore.task;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.joda.time.DateTime;
import org.openmrs.Visit;
import org.openmrs.api.context.Context;
import org.openmrs.module.appointments.model.Appointment;
import org.openmrs.module.appointments.model.AppointmentSearchRequest;
import org.openmrs.module.appointments.model.AppointmentStatus;
import org.openmrs.module.appointments.service.AppointmentsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This closes any "CheckedIn" appointments that are associated with 1 or more visits, when all those associated visits are completed
 */
public class MarkBahmniAppointmentsAsCompleted implements Runnable {

    private final Logger log = LoggerFactory.getLogger(getClass());
    @Override
    public void run() {
        // fetch all appointments in the "checked in" state
        AppointmentSearchRequest request = new AppointmentSearchRequest();
        request.setStatus(AppointmentStatus.CheckedIn);
        request.setStartDate(new DateTime().minusYears(1000).toDate()); // hack, see: https://bahmni.atlassian.net/browse/BAH-3867; this will start to fail in a thousand years
        List<Appointment> appointments = Context.getService(AppointmentsService.class).search(request);

        // test each appointment to see if it should be marked as complete
        for (Appointment appointment : appointments) {
           if (appointment.getFulfillingEncounters() != null) {
               // get all visits associated with encounters associated with the appointment
               Set<Visit> visits = appointment.getFulfillingEncounters().stream()
                    .filter(encounter -> encounter.getVisit() != null)
                    .map(encounter -> encounter.getVisit()).collect(Collectors.toSet());
               // if there are one or more visits (really should never be more than one), and they are all closed, then mark appointment as complete
               if (visits != null && visits.size() > 0 && visits.stream().allMatch(visit -> {
                    return visit.getStopDatetime() != null;
               })) {
                   appointment.setStatus(AppointmentStatus.Completed);
                   Context.getService(AppointmentsService.class).validateAndSave(appointment);
               }
           }
        }
    }
}
