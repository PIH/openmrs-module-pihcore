package org.openmrs.module.pihcore.fragment.controller.dashboardwidgets;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.codehaus.jackson.JsonNode;
import org.joda.time.DateTime;
import org.openmrs.Patient;
import org.openmrs.api.PatientService;
import org.openmrs.module.appframework.domain.AppDescriptor;
import org.openmrs.module.appointments.model.Appointment;
import org.openmrs.module.appointments.model.AppointmentSearchRequest;
import org.openmrs.module.appointments.model.AppointmentStatus;
import org.openmrs.module.appointments.service.AppointmentsService;
import org.openmrs.module.emrapi.patient.PatientDomainWrapper;
import org.openmrs.ui.framework.annotation.FragmentParam;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.openmrs.ui.framework.fragment.FragmentConfiguration;
import org.openmrs.ui.framework.fragment.FragmentModel;

public class AppointmentsFragmentController {
    public void controller(@SpringBean("appointmentsService") AppointmentsService appointmentsService,
                           @SpringBean("patientService") PatientService patientService,
                           @FragmentParam("app") AppDescriptor app,
                           FragmentConfiguration config,
                           FragmentModel model) {

        Object patientConfig = config.get("patient");
        if (patientConfig == null ) {
            patientConfig = config.get("patientId");
        }
        Patient patient = null;
        if (patientConfig != null) {
            if (patientConfig instanceof Patient) {
                patient = (Patient) patientConfig;
            }
            else if (patientConfig instanceof PatientDomainWrapper) {
                patient = ((PatientDomainWrapper) patientConfig).getPatient();
            }
            else if (patientConfig instanceof Integer) {
                patient = patientService.getPatient((Integer)patientConfig);
            }
            else if (patientConfig instanceof String) {
                patient = patientService.getPatientByUuid((String)patientConfig);
            }
        }
        if (patient == null) {
            throw new IllegalArgumentException("No patient found. Please pass patient into the configuration");
        }

        AppointmentSearchRequest appointmentSearchRequest = new AppointmentSearchRequest();
        appointmentSearchRequest.setPatientUuid(patient.getUuid());
        appointmentSearchRequest.setStartDate(new Date(0)); // ugly hack since the appointments search method requires a start date, this is "the epoch", ie January 1, 1970
        List<Appointment> appointments = appointmentsService.search(appointmentSearchRequest);

        List<Appointment> filteredAppointments = appointments != null ? appointments.stream()
                .filter(a -> a.getStatus() != null && (a.getStatus() == AppointmentStatus.Scheduled || a.getStatus() == AppointmentStatus.CheckedIn))
                .sorted(Comparator.comparing(Appointment::getStartDateTime))
                .collect(Collectors.toList()) : Collections.emptyList();

        model.put("appointments", filteredAppointments);
        model.put("app", app);
        model.put("patientUuid", patient.getUuid());
    }

    private String getConfigValue(AppDescriptor app, String configValue) {
        JsonNode node = app.getConfig().get(configValue);
        if (node == null) {
            return "";
        }
        return node.asText();
    }
}
