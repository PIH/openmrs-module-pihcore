package org.openmrs.module.pihcore;

import org.apache.commons.lang.StringUtils;
import org.openmrs.Concept;
import org.openmrs.Location;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.PatientProgram;
import org.openmrs.Program;
import org.openmrs.Visit;
import org.openmrs.api.ObsService;
import org.openmrs.api.ProgramWorkflowService;
import org.openmrs.api.context.Context;
import org.openmrs.module.emrapi.adt.AdtService;
import org.openmrs.module.emrapi.adt.InpatientAdmission;
import org.openmrs.module.emrapi.adt.InpatientAdmissionSearchCriteria;
import org.openmrs.module.emrapi.adt.InpatientRequest;
import org.openmrs.module.emrapi.adt.InpatientRequestSearchCriteria;
import org.openmrs.module.emrapi.disposition.DispositionType;
import org.openmrs.module.queue.api.QueueServicesWrapper;
import org.openmrs.module.queue.api.search.QueueEntrySearchCriteria;
import org.openmrs.module.queue.model.QueueEntry;
import org.openmrs.ui.framework.SimpleObject;
import org.openmrs.ui.framework.UiUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Set;

public class PihCoreUtils {

    public static List<Obs> getObsWithinProgram(Patient patient, Set<Concept> concepts, String programUuid) {

        List<Obs> obsList = null;
        ProgramWorkflowService programWorkflowService = Context.getProgramWorkflowService();
        ObsService obsService = Context.getObsService();
        Date obsOnOrAfter = null;
        if (StringUtils.isNotBlank(programUuid)) {
            Program program = programWorkflowService.getProgramByUuid(programUuid);
            if (program == null) {
                throw new IllegalStateException("No program with uuid " + programUuid + " is found.");
            }
            for (PatientProgram pp : programWorkflowService.getPatientPrograms(patient, program, null, null, null, null, false)) {
                if (pp.getActive()) {
                    if (obsOnOrAfter == null || obsOnOrAfter.before(pp.getDateEnrolled())) {
                        obsOnOrAfter = pp.getDateEnrolled();
                    }
                }
            }
        }
        obsList = obsService.getObservations(
                Arrays.asList(patient),
                null,
                new ArrayList<>(concepts),
                null,
                null,
                null,
                Arrays.asList("obsDatetime"), //sort by field
                null,
                null,
                obsOnOrAfter, //fromDate
                null,
                false); //includeVoidedObs

        return obsList;
    }

    public static SimpleObject getInpatientLocation(Patient patient, Visit activeVisit, AdtService adtService, UiUtils ui) {
        Location currentInpatientLocation = null;
        String patientStatus = "";
        String queueName = "";
        if (activeVisit != null ) {
            // #1 Check if patient was admitted
            InpatientAdmissionSearchCriteria criteria = new InpatientAdmissionSearchCriteria();
            criteria.setPatientIds(Arrays.asList(patient.getPatientId()));
            criteria.setIncludeDischarged(false);
            List<InpatientAdmission> inpatientAdmissions = adtService.getInpatientAdmissions(criteria);
            InpatientRequest currentInpatientRequest = null;
            if (inpatientAdmissions != null && !inpatientAdmissions.isEmpty()) {
                for (InpatientAdmission inpatientAdmission : inpatientAdmissions) {
                    // it should never be more than one inpatientAdmission per patient
                    currentInpatientLocation = inpatientAdmission.getCurrentInpatientLocation();
                    currentInpatientRequest = inpatientAdmission.getCurrentInpatientRequest();
                    if (currentInpatientRequest != null) {
                        String dispositionLocation = currentInpatientRequest.getDispositionLocation() !=null ?  currentInpatientRequest.getDispositionLocation().getName() : currentInpatientRequest.getDispositionEncounter().getLocation().getName();
                        if (currentInpatientRequest.getDispositionType() == DispositionType.ADMIT) {
                            patientStatus = ui.message("pihcore.waiting.tobe.admitted") + " " + dispositionLocation;
                        } else if (currentInpatientRequest.getDispositionType() == DispositionType.TRANSFER) {
                            patientStatus = ui.message("pihcore.waiting.for.transfer") + " " + dispositionLocation;
                        } else if (currentInpatientRequest.getDispositionType() == DispositionType.DISCHARGE) {
                            patientStatus = ui.message("pihcore.waiting.for.discharge") + " " + dispositionLocation;
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
                        currentInpatientLocation = inpatientRequest.getDispositionLocation() !=null ? inpatientRequest.getDispositionLocation() : inpatientRequest.getDispositionEncounter().getLocation();
                        if (inpatientRequest.getDispositionType() == DispositionType.ADMIT) {
                            patientStatus = ui.message("pihcore.waiting.tobe.admitted") + " " + currentInpatientLocation;
                        } else if (inpatientRequest.getDispositionType() == DispositionType.TRANSFER) {
                            patientStatus = ui.message("pihcore.waiting.for.transfer") + " " + currentInpatientLocation;
                        } else if (inpatientRequest.getDispositionType() == DispositionType.DISCHARGE) {
                            patientStatus = ui.message("pihcore.waiting.for.discharge") + " " + currentInpatientLocation;
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
                qeSearchCriteria.setVisit(activeVisit);
                qeSearchCriteria.setIsEnded(null);
                QueueServicesWrapper queueServicesWrapper = Context.getRegisteredComponents(QueueServicesWrapper.class).get(0);
                List<QueueEntry> queueEntries = queueServicesWrapper.getQueueEntryService().getQueueEntries(qeSearchCriteria);
                for (QueueEntry queueEntry : queueEntries) {
                    queueName = queueEntry.getQueue().getName();
                    patientStatus = queueEntry.getStatus().getName().getName();
                }
            }
        }
        return SimpleObject.create(
                "currentInpatientLocation", currentInpatientLocation,
                "queueName", queueName,
                "patientStatus", patientStatus
        );
    }
}
