package org.openmrs.module.pihcore.page.controller.visit;

import org.joda.time.DateMidnight;
import org.joda.time.DateTimeConstants;
import org.openmrs.Concept;
import org.openmrs.Encounter;
import org.openmrs.EncounterType;
import org.openmrs.Location;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifierType;
import org.openmrs.Visit;
import org.openmrs.api.ConceptService;
import org.openmrs.api.EncounterService;
import org.openmrs.api.ObsService;
import org.openmrs.api.PatientService;
import org.openmrs.api.VisitService;
import org.openmrs.module.appui.UiSessionContext;
import org.openmrs.module.emrapi.EmrApiConstants;
import org.openmrs.module.emrapi.adt.AdtService;
import org.openmrs.module.emrapi.domainwrapper.DomainWrapperFactory;
import org.openmrs.module.emrapi.patient.PatientDomainWrapper;
import org.openmrs.module.pihcore.PihEmrConfigConstants;
import org.openmrs.module.pihcore.ZlConfigConstants;
import org.openmrs.module.pihcore.metadata.Metadata;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.openmrs.ui.framework.page.PageModel;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class WaitingForConsultPageController {

    enum Filter { WAITING_FOR_CONSULT, IN_CONSULTATION }

    public String get(PageModel model,
                      @RequestParam(required = false, value = "filter") String filterString,
                      @SpringBean("encounterService") EncounterService encounterService,
                      @SpringBean("obsService") ObsService obsService,
                      @SpringBean("conceptService") ConceptService conceptService,
                      @SpringBean("patientService") PatientService patientService,
                      @SpringBean("visitService") VisitService visitService,
                      @SpringBean("adtService") AdtService adtService,
                      @SpringBean("domainWrapperFactory") DomainWrapperFactory domainWrapperFactory,
                      UiSessionContext uiSessionContext) {

        Filter filter;

        if (filterString != null && filterString.toLowerCase().equals("in_consultation")) {
            filter = Filter.IN_CONSULTATION;
        }
        else {
            filter = Filter.WAITING_FOR_CONSULT;
        }

        List<EncounterType> primaryCareEncounterTypes = new ArrayList<EncounterType>();
        primaryCareEncounterTypes.add(Metadata.lookupEncounterType(PihEmrConfigConstants.ENCOUNTERTYPE_PRIMARY_CARE_VISIT_UUID));
        primaryCareEncounterTypes.add(Metadata.lookupEncounterType(PihEmrConfigConstants.ENCOUNTERTYPE_PRIMARY_CARE_PEDS_INITIAL_CONSULT_UUID));
        primaryCareEncounterTypes.add(Metadata.lookupEncounterType(PihEmrConfigConstants.ENCOUNTERTYPE_PRIMARY_CARE_PEDS_FOLLOWUP_CONSULT_UUID));
        primaryCareEncounterTypes.add(Metadata.lookupEncounterType(PihEmrConfigConstants.ENCOUNTERTYPE_PRIMARY_CARE_ADULT_INITIAL_CONSULT_UUID));
        primaryCareEncounterTypes.add(Metadata.lookupEncounterType(PihEmrConfigConstants.ENCOUNTERTYPE_PRIMARY_CARE_ADULT_FOLLOWUP_CONSULT_UUID));

        List<EncounterType> vitalsEncounterTypes = Collections.singletonList(Metadata.lookupEncounterType(PihEmrConfigConstants.ENCOUNTERTYPE_VITALS_UUID));
        Concept dispoConceptSet = conceptService.getConceptByMapping(EmrApiConstants.CONCEPT_CODE_DISPOSITION_CONCEPT_SET, EmrApiConstants.EMR_CONCEPT_SOURCE_NAME);

        Date startOfToday = new DateMidnight().toDate();
        boolean isMonday = new DateMidnight().getDayOfWeek() == DateTimeConstants.MONDAY;
        Date startOfPreviousBusinessDay = isMonday ? new DateMidnight().minusDays(3).toDate() : new DateMidnight().minusDays(1).toDate();
        Date endOfPreviousBusinessDay = isMonday ? new DateMidnight().minusDays(2).toDate() : startOfToday;

        // restrict the queue to active visits at the visit location (e.g. Cange or Mirebalais) that contains the session location
        Set<Visit> activeVisits = getActiveVisitsAtVisitLocation(uiSessionContext.getSessionLocation(), adtService, visitService);

        // first handle any patients with vitals taken today:

        // create a list of all patients that have a vitals encounter today, *ordered by time of first vitals encounter*
        LinkedHashSet<Patient> patientListForToday = getPatientsWithEncounters(encounterService, vitalsEncounterTypes, startOfToday, null, activeVisits);

        // fetch the set of all patients that have a primary care encounter today
        Set<Patient> patientsWithConsultToday = getPatientsWithEncounters(encounterService, primaryCareEncounterTypes, startOfToday, null, activeVisits);

        // now handle any patients with vitals taken on the last business day whose visit is still active

        // create a list of all patients that have a vitals encounter on last business day, *ordered by time of first vitals encounter*
        LinkedHashSet<Patient> patientListForPreviousBusinessDay = getPatientsWithEncounters(encounterService, vitalsEncounterTypes,
                startOfPreviousBusinessDay, endOfPreviousBusinessDay, activeVisits);

        // fetch the set of all patients that have a primary care encounter last business day or today
        Set<Patient> patientsWithConsultOnPreviousBusinessDayOrToday = getPatientsWithEncounters(encounterService, primaryCareEncounterTypes,
                startOfPreviousBusinessDay, endOfPreviousBusinessDay, activeVisits);
        patientsWithConsultOnPreviousBusinessDayOrToday.addAll(patientsWithConsultToday);

        // assumption: you can't have a disposition without also having a consult
        if (filter.equals(Filter.WAITING_FOR_CONSULT)) {
            // the "waiting for consult" list is all patients with vitals today but no consult
            patientListForToday.removeAll(patientsWithConsultToday);
            // plus all patients with vitals last business day but no consult then or today
            patientListForPreviousBusinessDay.removeAll(patientsWithConsultOnPreviousBusinessDayOrToday);
        }
        else {
            // fetch the set of all patients who have a disposition today, and last business day or today
            Set<Patient> patientsWithDispositionToday = getPatientsWithDisposition(obsService, dispoConceptSet, startOfToday, null, activeVisits);
            Set<Patient> patientsWithDispositionOnPreviousBusinessDayOrToday = getPatientsWithDisposition(obsService, dispoConceptSet,
                    startOfPreviousBusinessDay, endOfPreviousBusinessDay, activeVisits);
            patientsWithDispositionOnPreviousBusinessDayOrToday.addAll(patientsWithDispositionToday);

            // the "in-consultation" list is all patients with vitals AND consult today but no dispostion (disposition is our trigger that a consult is finished)
            patientListForToday.retainAll(patientsWithConsultToday);
            patientListForToday.removeAll(patientsWithDispositionToday);
            // plus all patients with vitals last business day and consult last business day or today, but no disposition
            patientListForPreviousBusinessDay.retainAll(patientsWithConsultOnPreviousBusinessDayOrToday);
            patientListForPreviousBusinessDay.removeAll(patientsWithDispositionOnPreviousBusinessDayOrToday);
        }

        // now create our final list by combining the list for the previous business day with the list for the current day
        LinkedHashSet<Patient> patientList = patientListForPreviousBusinessDay;
        patientList.addAll(patientListForToday);

        // now wrap them all in a patient domain wrapper
        List<PatientDomainWrapper> patientsListWrapped = new ArrayList<PatientDomainWrapper>();

        for (Patient patient : patientList) {
            patientsListWrapped.add(domainWrapperFactory.newPatientDomainWrapper(patient));
        }

        PatientIdentifierType dossierNumberType = patientService.getPatientIdentifierTypeByUuid(ZlConfigConstants.PATIENTIDENTIFIERTYPE_DOSSIERNUMBER_UUID);

        model.addAttribute("patientList", patientsListWrapped);
        model.addAttribute("filter", filter.toString().toLowerCase());
        model.addAttribute("dossierIdentifierName", dossierNumberType == null ? null : dossierNumberType.getName());
        model.addAttribute("mothersFirstName", Metadata.getMothersFirstNameAttributeType());

        return null;
    }

    private Set<Visit> getActiveVisitsAtVisitLocation(Location sessionLocation, AdtService adtService, VisitService visitService) {
        Location visitLocation;
        try {
            visitLocation = sessionLocation == null ? null : adtService.getLocationThatSupportsVisits(sessionLocation);
        }
        catch (IllegalArgumentException e) {
            // thrown when neither the session location nor any of its ancestors is tagged as a Visit Location
            visitLocation = null;
        }
        if (visitLocation == null) {
            return Collections.emptySet();
        }
        return new HashSet<Visit>(visitService.getVisits(null, null, Collections.singletonList(visitLocation),
                null, null, null, null, null, null, false, false));
    }

    private LinkedHashSet<Patient> getPatientsWithEncounters(EncounterService encounterService, List<EncounterType> encounterTypes,
                                                             Date fromDate, Date toDate, Set<Visit> activeVisits) {
        // ordered by time of first encounter
        LinkedHashSet<Patient> patients = new LinkedHashSet<Patient>();
        for (Encounter encounter : encounterService.getEncounters(null, null, fromDate, toDate,
                null, encounterTypes, null, null, null, false)) {
            if (isInActiveVisit(encounter, activeVisits)) {
                patients.add(encounter.getPatient());
            }
        }
        return patients;
    }

    private Set<Patient> getPatientsWithDisposition(ObsService obsService, Concept dispoConceptSet,
                                                    Date fromDate, Date toDate, Set<Visit> activeVisits) {
        Set<Patient> patients = new HashSet<Patient>();
        for (Obs obs : obsService.getObservations(null, null, Collections.singletonList(dispoConceptSet),
                null, null, null, null, null, null, fromDate, toDate, false)) {
            if (isInActiveVisit(obs.getEncounter(), activeVisits)) {
                patients.add(obs.getEncounter().getPatient());
            }
        }
        return patients;
    }

    private boolean isInActiveVisit(Encounter encounter, Set<Visit> activeVisits) {
        return encounter != null && encounter.getVisit() != null && activeVisits.contains(encounter.getVisit());
    }


    // TODO support for old "Waiting Status" concept which we will (hopefully) be able to delete eventually
/*    private Obs getLatestWaitingStatus(Patient patient, ObsService obsService, ConceptService conceptService, Date since) {
        Obs status = null;
        List<Obs> wfcObs = obsService.getObservations(
                Collections.singletonList((Person) patient), null,
                Collections.singletonList(conceptService.getConceptByUuid(WAITING_FOR_CONSULT_STATUS_CONCEPT_UUID)),
                null, null, null, null, new Integer(1), null,
                since, null, false);

        if ((wfcObs != null) && (wfcObs.size() > 0)) {
            //the patient was removed or is in consultation
            return wfcObs.get(0);
        }
        return status;
    }

    private boolean hasQueueStatusChanged(Encounter encounter, ObsService obsService, ConceptService conceptService) {
        if (encounter != null) {
            return (getLatestWaitingStatus(encounter.getPatient(), obsService, conceptService, encounter.getEncounterDatetime()) != null ) ? true : false ;
        }
        return false;
    }*/

/*
    class WaitingStatusWrapper {
        Obs status = null;
        PatientDomainWrapper patientDomainWrapper = null;

        public Obs getStatus() {
            return status;
        }

        public void setStatus(Obs status) {
            this.status = status;
        }

        public PatientDomainWrapper getPatientDomainWrapper() {
            return patientDomainWrapper;
        }

        public void setPatientDomainWrapper(PatientDomainWrapper patientDomainWrapper) {
            this.patientDomainWrapper = patientDomainWrapper;
        }

        public WaitingStatusWrapper() {}
    }*/

}
