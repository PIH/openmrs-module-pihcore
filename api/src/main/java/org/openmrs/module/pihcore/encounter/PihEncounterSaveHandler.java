package org.openmrs.module.pihcore.encounter;

import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Concept;
import org.openmrs.Encounter;
import org.openmrs.Obs;
import org.openmrs.User;
import org.openmrs.annotation.Handler;
import org.openmrs.api.context.Context;
import org.openmrs.api.handler.SaveHandler;
import org.openmrs.module.appointments.model.Appointment;
import org.openmrs.module.appointments.model.AppointmentStatus;
import org.openmrs.module.appointments.service.AppointmentsService;

/**
 * When an encounter is voided, voids any active appointment linked to the encounter via a
 * top-level "PIH:Appointment uuid" obs (as saved by the scheduleAppointment tag handler).
 *
 * This fires inside the saveEncounter transaction (after voidEncounter has already voided the
 * obs tree), so the appointment entity is mutated in-place rather than re-saved through
 * AppointmentsService.validateAndSave() — calling a @Transactional save here could force an
 * early Hibernate flush and trip over not-yet-populated entities in the same session.
 */
@Handler(supports = Encounter.class)
public class PihEncounterSaveHandler implements SaveHandler<Encounter> {

    private final Log log = LogFactory.getLog(getClass());

    @Override
    public void handle(Encounter encounter, User user, Date date, String reason) {
        voidAppointmentIfEncounterVoided(encounter, user, date);
    }

    private void voidAppointmentIfEncounterVoided(Encounter encounter, User user, Date date) {
        if (encounter.getEncounterId() == null || !encounter.getVoided()) {
            return;
        }

        Concept uuidConcept = Context.getConceptService().getConceptByMapping("Appointment uuid", "PIH");
        if (uuidConcept == null) {
            return;
        }

        AppointmentsService appointmentsService;
        try {
            appointmentsService = Context.getService(AppointmentsService.class);
        } catch (Exception e) {
            return;
        }

        for (Obs obs : encounter.getObsAtTopLevel(true)) {
            if (!uuidConcept.equals(obs.getConcept()) || obs.getValueText() == null) {
                continue;
            }
            Appointment appointment = appointmentsService.getAppointmentByUuid(obs.getValueText());
            if (appointment == null
                    || Boolean.TRUE.equals(appointment.getVoided())
                    || appointment.getStatus() == AppointmentStatus.Cancelled) {
                continue;
            }
            appointment.setVoided(true);
            appointment.setVoidedBy(user);
            appointment.setDateVoided(date);
            appointment.setVoidReason("Encounter voided");
            log.debug("Voided appointment " + appointment.getUuid()
                    + " due to voided encounter " + encounter.getUuid());
        }
    }
}
