package org.openmrs.module.pihcore.page.controller.visit;

import org.joda.time.DateMidnight;
import org.joda.time.DateTimeConstants;
import org.openmrs.Concept;
import org.openmrs.Encounter;
import org.openmrs.EncounterType;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifierType;
import org.openmrs.api.ConceptService;
import org.openmrs.api.EncounterService;
import org.openmrs.api.ObsService;
import org.openmrs.api.PersonService;
import org.openmrs.api.context.Context;
import org.openmrs.module.emrapi.EmrApiConstants;
import org.openmrs.module.emrapi.domainwrapper.DomainWrapperFactory;
import org.openmrs.module.emrapi.patient.PatientDomainWrapper;
import org.openmrs.module.haiticore.metadata.HaitiPersonAttributeTypes;
import org.openmrs.module.metadatadeploy.MetadataUtils;
import org.openmrs.module.pihcore.PihEmrConfigConstants;
import org.openmrs.module.pihcore.ZlConfigConstants;
import org.openmrs.module.pihcore.metadata.Metadata;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.openmrs.ui.framework.page.PageModel;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.Collections;
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
                      @SpringBean("personService") PersonService personService,
                      @SpringBean("domainWrapperFactory") DomainWrapperFactory domainWrapperFactory) {


        // TODO restrict by location at some point if necessary

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

        // first handle any patients with vitals taken today:

        // create a list of all patients that have a vitals encounter today, *ordered by time of first vitals encounter*
        LinkedHashSet<Patient> patientsWithVitalsToday = new LinkedHashSet<Patient>();
        for (Encounter encounter : encounterService.getEncounters(null, null, new DateMidnight().toDate(), null,
                null, Collections.singletonList(Metadata.lookupEncounterType(PihEmrConfigConstants.ENCOUNTERTYPE_VITALS_UUID)),
                null, null, null, false)) {
            patientsWithVitalsToday.add(encounter.getPatient());
        }

        // fetch the set of all patients that have a primary care encounter today
        Set<Patient> patientsWithConsultToday = new HashSet<Patient>();
        for (Encounter encounter :  encounterService.getEncounters(null, null, new DateMidnight().toDate(), null,
                null, primaryCareEncounterTypes, null, null, null, false)) {
            patientsWithConsultToday.add(encounter.getPatient());
        }

        // fetch the set of all patients who have a disposition today
        Set<Patient> patientsWithDispositionToday = new HashSet<Patient>();
        Concept dispoConceptSet = conceptService.getConceptByMapping(EmrApiConstants.CONCEPT_CODE_DISPOSITION_CONCEPT_SET, EmrApiConstants.EMR_CONCEPT_SOURCE_NAME);
        for (Obs obs : obsService.getObservations(null, null, Collections.singletonList(dispoConceptSet),
                null, null, null, null, null, null, new DateMidnight().toDate(), null,false)) {
            patientsWithDispositionToday.add(Context.getPatientService().getPatient(obs.getPersonId()));  // assumption: only patients have obs, not plain persio
        }

        LinkedHashSet<Patient> patientListForToday = patientsWithVitalsToday;

        // assumption: you can't have a disposition without also having a consult
        if (filter.equals(Filter.WAITING_FOR_CONSULT)) {
            // the "waiting for consult" list is all patients with vitals today but no consult
            patientListForToday.removeAll(patientsWithConsultToday);
        }
        else {
            // the "in-consultation" list is all patients with vitals AND consult today but no dispostion (disposition is our trigger that a consult is finished)
            patientListForToday.retainAll(patientsWithConsultToday);
            patientListForToday.removeAll(patientsWithDispositionToday);
        }

        // now handle any patients with vitals taken on the last business day

        boolean isMonday = new DateMidnight().getDayOfWeek() == DateTimeConstants.MONDAY;

        // create a list of all patients that have a vitals encounter on last business day, *ordered by time of first vitals encounter*
        LinkedHashSet<Patient> patientsWithVitalsOnPreviousBusinessDay = new LinkedHashSet<Patient>();
        for (Encounter encounter : encounterService.getEncounters(null, null,
                isMonday ? new DateMidnight().minusDays(3).toDate() : new DateMidnight().minusDays(1).toDate(),
                isMonday ? new DateMidnight().minusDays(2).toDate() : new DateMidnight().toDate(),
                null, Collections.singletonList(Metadata.lookupEncounterType(PihEmrConfigConstants.ENCOUNTERTYPE_VITALS_UUID)),
                null, null, null, false)) {
            patientsWithVitalsOnPreviousBusinessDay.add(encounter.getPatient());
        }

        Set<Patient> patientsWithConsultOnPreviousBusinessDayOrToday = new HashSet<Patient>();
        // fetch the set of all patients that have a primary care encounter last business day
        for (Encounter encounter :  encounterService.getEncounters(null, null,
                isMonday ? new DateMidnight().minusDays(3).toDate() : new DateMidnight().minusDays(1).toDate(),
                isMonday ? new DateMidnight().minusDays(2).toDate() : new DateMidnight().toDate(),
                null, primaryCareEncounterTypes, null, null, null, false)) {
            patientsWithConsultOnPreviousBusinessDayOrToday.add(encounter.getPatient());
        }
        // add all patiens with primary care encounter today
        patientsWithConsultOnPreviousBusinessDayOrToday.addAll(patientsWithConsultToday);

        Set<Patient> patientsWithDispositionOnPreviousDaysOrToday = new HashSet<Patient>();
        // fetch the set of all patients who have a disposition last business day
        for (Obs obs : obsService.getObservations(null, null, Collections.singletonList(dispoConceptSet),
                null, null, null, null, null, null,
                isMonday ? new DateMidnight().minusDays(3).toDate() : new DateMidnight().minusDays(1).toDate(),
                isMonday ? new DateMidnight().minusDays(2).toDate() : new DateMidnight().toDate(),
                false)) {
            patientsWithDispositionOnPreviousDaysOrToday.add(Context.getPatientService().getPatient(obs.getPersonId()));  // assumption: only patients have obs, not plain persio
        }
        // add all patiens with disposition today
        patientsWithConsultOnPreviousBusinessDayOrToday.addAll(patientsWithDispositionToday);


        LinkedHashSet<Patient> patientListForPreviousBusinessDay = patientsWithVitalsOnPreviousBusinessDay;

        // assumption: you can't have a disposition without also having a consult
        if (filter.equals(Filter.WAITING_FOR_CONSULT)) {
            // the "waiting for consult" list is all patients with vitals last business day but no consult then or today
            patientListForPreviousBusinessDay.removeAll(patientsWithConsultOnPreviousBusinessDayOrToday);
        }
        else {
            // the "in-consultation" list is all patients with vitals last business day and consult last business day or today, but no disposition
            patientListForPreviousBusinessDay.retainAll(patientsWithConsultOnPreviousBusinessDayOrToday);
            patientListForPreviousBusinessDay.removeAll(patientsWithDispositionOnPreviousDaysOrToday);
        }

        // now create our final list by combining the list for the previous business day with the list for the current day
        LinkedHashSet<Patient> patientList = patientListForPreviousBusinessDay;
        patientList.addAll(patientListForToday);

        // now wrap them all in a patient domain wrapper
        List<PatientDomainWrapper> patientsListWrapped = new ArrayList<PatientDomainWrapper>();

        for (Patient patient : patientList) {
            patientsListWrapped.add(domainWrapperFactory.newPatientDomainWrapper(patient));
        }

        PatientIdentifierType dossierNumberType = MetadataUtils.existing(PatientIdentifierType.class, ZlConfigConstants.PATIENTIDENTIFIERTYPE_DOSSIERNUMBER_UUID);

        model.addAttribute("patientList", patientsListWrapped);
        model.addAttribute("filter", filter.toString().toLowerCase());
        model.addAttribute("dossierIdentifierName", dossierNumberType.getName());
        model.addAttribute("mothersFirstName", personService.getPersonAttributeTypeByUuid(HaitiPersonAttributeTypes.MOTHERS_FIRST_NAME.uuid()));

        return null;
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
