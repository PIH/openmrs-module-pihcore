package org.openmrs.module.pihcore.fragment.controller.dashboardwidgets;


import org.apache.commons.lang.StringUtils;
import org.openmrs.Location;
import org.openmrs.Patient;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;
import org.openmrs.module.appframework.domain.AppDescriptor;
import org.openmrs.module.appui.UiSessionContext;
import org.openmrs.module.emrapi.adt.AdtService;
import org.openmrs.module.emrapi.adt.InpatientAdmission;
import org.openmrs.module.emrapi.adt.InpatientAdmissionSearchCriteria;
import org.openmrs.module.emrapi.adt.InpatientRequest;
import org.openmrs.module.emrapi.adt.InpatientRequestSearchCriteria;
import org.openmrs.module.emrapi.disposition.DispositionType;
import org.openmrs.module.emrapi.patient.PatientDomainWrapper;
import org.openmrs.module.emrapi.visit.VisitDomainWrapper;
import org.openmrs.module.queue.api.QueueServicesWrapper;
import org.openmrs.module.queue.api.search.QueueEntrySearchCriteria;
import org.openmrs.module.queue.model.QueueEntry;
import org.openmrs.ui.framework.UiUtils;
import org.openmrs.ui.framework.annotation.FragmentParam;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.openmrs.ui.framework.fragment.FragmentConfiguration;
import org.openmrs.ui.framework.fragment.FragmentModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;

public class PatientLocationFragmentController {
    private final Logger log = LoggerFactory.getLogger(getClass());

    public void controller(@SpringBean("patientService") PatientService patientService,
                           @SpringBean("adtService") AdtService adtService,
                           @FragmentParam("app") AppDescriptor app,
                           UiSessionContext uiSessionContext,
                           UiUtils ui,
                           FragmentConfiguration config,
                           FragmentModel model) throws Exception {

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
        VisitDomainWrapper activeVisit = adtService.getActiveVisit(patient, uiSessionContext.getSessionLocation());
        String patientStatus = "";
        String queueName = "";
        Location currentInpatientLocation = null;
        if (activeVisit != null ) {
            // #1 Check if patient was admitted
            InpatientAdmissionSearchCriteria criteria = new InpatientAdmissionSearchCriteria();
            criteria.setPatientIds(Arrays.asList(patient.getPatientId()));
            criteria.setIncludeDischarged(false);
            List<InpatientAdmission> inpatientAdmissions = adtService.getInpatientAdmissions(criteria);
            InpatientRequest currentInpatientRequest = null;
            if (inpatientAdmissions != null && !inpatientAdmissions.isEmpty()) {
                for (InpatientAdmission inpatientAdmission : inpatientAdmissions) {
                    currentInpatientLocation = inpatientAdmission.getCurrentInpatientLocation();
                    currentInpatientRequest = inpatientAdmission.getCurrentInpatientRequest();
                    if (currentInpatientRequest != null) {
                        String dispositionLocation = currentInpatientRequest.getDispositionLocation().getName();
                        if (currentInpatientRequest.getDispositionType() == DispositionType.ADMIT) {
                            patientStatus = ui.message("pihcore.waiting.tobe.admitted") + " " + dispositionLocation;
                        } else if (currentInpatientRequest.getDispositionType() == DispositionType.TRANSFER) {
                            patientStatus = ui.message("pihcore.waiting.for.transfer") + " " + dispositionLocation;
                        } else {
                            patientStatus = currentInpatientRequest.getDisposition().getName().getName();
                        }
                    } else {
                        patientStatus = ui.message("pihcore.admitted");
                    }
                }
            } else {
                // #2 Check for disposition requests for this patient
                InpatientRequestSearchCriteria requestSearchCriteria = new InpatientRequestSearchCriteria();
                requestSearchCriteria.setPatientIds(Arrays.asList(patient.getPatientId()));
                requestSearchCriteria.setVisitIds(Arrays.asList(activeVisit.getVisitId()));
                List<InpatientRequest> inpatientRequests = adtService.getInpatientRequests(requestSearchCriteria);
                if (inpatientRequests != null && !inpatientRequests.isEmpty()) {
                    for (InpatientRequest inpatientRequest : inpatientRequests) {
                        currentInpatientLocation = inpatientRequest.getDispositionLocation();
                        if (inpatientRequest.getDispositionType() == DispositionType.ADMIT) {
                            patientStatus = ui.message("pihcore.waiting.tobe.admitted") + " " + currentInpatientLocation;
                        } else if (inpatientRequest.getDispositionType() == DispositionType.TRANSFER) {
                            patientStatus = ui.message("pihcore.waiting.for.transfer") + " " + currentInpatientLocation;
                        } else {
                            patientStatus = inpatientRequest.getDisposition().getName().getName();
                        }
                    }
                }
            }

            if (StringUtils.isBlank(patientStatus)) {
                // #3 Check to see if the patient is waiting on any queues
                QueueEntrySearchCriteria qeSearchCriteria = new QueueEntrySearchCriteria();
                qeSearchCriteria.setPatient(patient);
                qeSearchCriteria.setVisit(activeVisit.getVisit());
                qeSearchCriteria.setIsEnded(null);
                QueueServicesWrapper queueServicesWrapper = Context.getRegisteredComponents(QueueServicesWrapper.class).get(0);
                List<QueueEntry> queueEntries = queueServicesWrapper.getQueueEntryService().getQueueEntries(qeSearchCriteria);
                for (QueueEntry queueEntry : queueEntries) {
                    queueName = queueEntry.getQueue().getName();
                    patientStatus = queueEntry.getStatus().getName().getName();
                }
            }
        }
        model.put("patient", patient);
        model.put("app", app);
        model.put("patientStatus", patientStatus);
        model.put("inpatientLocation", currentInpatientLocation);
        model.put("queueName", queueName);
    }
}