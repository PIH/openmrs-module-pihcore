package org.openmrs.module.pihcore.task;

import org.joda.time.DateTime;
import org.openmrs.api.context.Context;
import org.openmrs.module.appointmentscheduling.PatientAppointment;
import org.openmrs.module.appointmentscheduling.api.AppointmentService;
import org.openmrs.module.emrapi.adt.AdtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Date;

/**
 * Custom task to update appointment statuses; looks at all the appointments that happened the previous day or earlier and
 * 1) sets any with a StatusType of SCHEDULED to MISSED, and 2) sets any with a StatusType of ACTIVE to COMPLETED *if* that appointment
 * has a visit with one or more consult encounters associated with it
 */
public class MarkAppointmentsAsMissedOrCompletedTask implements Runnable {

    private final Logger log = LoggerFactory.getLogger(getClass());

    @Override
    public void run() {
        log.info("Executing " + getClass());
        AppointmentService appointmentService = Context.getService(AppointmentService.class);

        AdtService adtService = Context.getService(AdtService.class);

        Date endOfYesterday = new DateTime().withTime(23, 59, 59, 999).minusDays(1).toDate();

        for (PatientAppointment appointment : appointmentService.getAppointmentsByConstraints(null, endOfYesterday, null, null, null, null,
                PatientAppointment.AppointmentStatus.getAppointmentsStatusByTypes(Arrays.asList(PatientAppointment.AppointmentStatusType.SCHEDULED)))) {
            appointment.setStatus(PatientAppointment.AppointmentStatus.MISSED);
            appointmentService.savePatientAppointment(appointment);
        }

        for (PatientAppointment appointment : appointmentService.getAppointmentsByConstraints(null, endOfYesterday, null, null, null, null,
                PatientAppointment.AppointmentStatus.getAppointmentsStatusByTypes(Arrays.asList(PatientAppointment.AppointmentStatusType.ACTIVE)))) {

            if (appointment.getVisit() != null && adtService.wrap(appointment.getVisit()).hasVisitNoteAtLocation(appointment.getTimeSlot().getAppointmentBlock().getLocation())) {
                appointment.setStatus(PatientAppointment.AppointmentStatus.COMPLETED);
                appointmentService.savePatientAppointment(appointment);
            }
        }

        log.info(getClass() + " Execution Completed");
    }
}
