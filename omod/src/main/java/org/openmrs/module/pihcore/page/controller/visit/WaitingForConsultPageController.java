package org.openmrs.module.pihcore.page.controller.visit;

import org.joda.time.DateMidnight;
import org.openmrs.Concept;
import org.openmrs.Encounter;
import org.openmrs.EncounterType;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.api.ConceptService;
import org.openmrs.api.EncounterService;
import org.openmrs.api.ObsService;
import org.openmrs.api.PersonService;
import org.openmrs.module.emrapi.EmrApiConstants;
import org.openmrs.module.emrapi.domainwrapper.DomainWrapperFactory;
import org.openmrs.module.emrapi.patient.PatientDomainWrapper;
import org.openmrs.module.pihcore.metadata.core.EncounterTypes;
import org.openmrs.module.pihcore.metadata.core.PersonAttributeTypes;
import org.openmrs.module.pihcore.metadata.haiti.HaitiPatientIdentifierTypes;
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
        primaryCareEncounterTypes.add(encounterService.getEncounterTypeByUuid(EncounterTypes.PRIMARY_CARE_VISIT.uuid()));
        primaryCareEncounterTypes.add(encounterService.getEncounterTypeByUuid(EncounterTypes.PRIMARY_CARE_PEDS_INITIAL_CONSULT.uuid()));
        primaryCareEncounterTypes.add(encounterService.getEncounterTypeByUuid(EncounterTypes.PRIMARY_CARE_PEDS_FOLLOWUP_CONSULT.uuid()));
        primaryCareEncounterTypes.add(encounterService.getEncounterTypeByUuid(EncounterTypes.PRIMARY_CARE_ADULT_INITIAL_CONSULT.uuid()));
        primaryCareEncounterTypes.add(encounterService.getEncounterTypeByUuid(EncounterTypes.PRIMARY_CARE_ADULT_FOLLOWUP_CONSULT.uuid()));

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
            patientsWithDispositionToday.add((Patient) obs.getPerson());  // assumption: only patients have obs, not plain persio
        }

        // create a list of all patients that have a vitals encounter today, *ordered by time of first vitals encounter*
        LinkedHashSet<Patient> patientsWithVitalsToday = new LinkedHashSet<Patient>();
        for (Encounter encounter : encounterService.getEncounters(null, null, new DateMidnight().toDate(), null,
                null, Collections.singletonList(encounterService.getEncounterTypeByUuid(EncounterTypes.VITALS.uuid())),
                null, null, null, false)) {
            patientsWithVitalsToday.add(encounter.getPatient());
        }

        LinkedHashSet<Patient> patientListForToday = patientsWithVitalsToday;

        // assumption: you can't have a disposition without also having a consult
        if (filter.equals(Filter.WAITING_FOR_CONSULT)) {
            // all patients with vitals today but no consult
            patientListForToday.removeAll(patientsWithConsultToday);
        }
        else {
            // all patients with vitals and consult today but no dispostion (disposition is our trigger that a consult is finished)
            patientListForToday.retainAll(patientsWithConsultToday);
            patientListForToday.removeAll(patientsWithDispositionToday);
        }

        // fetch the set of all patients that have a primary care encounter yesterday or today
        Set<Patient> patientsWithConsultYesterdayOrToday = new HashSet<Patient>();
        for (Encounter encounter :  encounterService.getEncounters(null, null, new DateMidnight().minusDays(1).toDate(), null,
                null, primaryCareEncounterTypes, null, null, null, false)) {
            patientsWithConsultYesterdayOrToday.add(encounter.getPatient());
        }

        // fetch the set of all patients who have a disposition yesterday or today
        Set<Patient> patientsWithDispositionYesterdayOrToday = new HashSet<Patient>();
        for (Obs obs : obsService.getObservations(null, null, Collections.singletonList(dispoConceptSet),
                null, null, null, null, null, null, new DateMidnight().minusDays(1).toDate(), null,false)) {
            patientsWithDispositionYesterdayOrToday.add((Patient) obs.getPerson());  // assumption: only patients have obs, not plain persio
        }

        // create a list of all patients that have a vitals encounter today, *ordered by time of first vitals encounter*
        LinkedHashSet<Patient> patientsWithVitalsYesterday = new LinkedHashSet<Patient>();
        for (Encounter encounter : encounterService.getEncounters(null, null, new DateMidnight().minus(1).toDate(),
                new DateMidnight().toDate(), null, Collections.singletonList(encounterService.getEncounterTypeByUuid(EncounterTypes.VITALS.uuid())),
                null, null, null, false)) {
            patientsWithVitalsYesterday.add(encounter.getPatient());
        }

        LinkedHashSet<Patient> patientListForYesterday = patientsWithVitalsYesterday;

        // assumption: you can't have a disposition without also having a consult
        if (filter.equals(Filter.WAITING_FOR_CONSULT)) {
            // all patients with vitals yesterday but no consult yesterday or today
            patientListForYesterday.removeAll(patientsWithConsultYesterdayOrToday);
        }
        else {
            // all patients with vitals yesterday and consult yesterday or today, but no disposition
            patientListForYesterday.retainAll(patientsWithConsultYesterdayOrToday);
            patientListForYesterday.removeAll(patientsWithDispositionYesterdayOrToday);
        }


        // now create our final list
        LinkedHashSet<Patient> patientList = patientListForYesterday;
        patientList.addAll(patientListForToday);

        // now wrap them all in a patient domain wrapper
        List<PatientDomainWrapper> patientsListWrapped = new ArrayList<PatientDomainWrapper>();

        for (Patient patient : patientList) {
            patientsListWrapped.add(domainWrapperFactory.newPatientDomainWrapper(patient));
        }

        model.addAttribute("patientList", patientsListWrapped);
        model.addAttribute("filter", filter.toString().toLowerCase());
        model.addAttribute("dossierIdentifierName", HaitiPatientIdentifierTypes.DOSSIER_NUMBER.name());
        model.addAttribute("mothersFirstName", personService.getPersonAttributeTypeByUuid(PersonAttributeTypes.MOTHERS_FIRST_NAME.uuid()));

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
