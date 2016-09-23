package org.openmrs.module.pihcore.page.controller.visit;

import org.joda.time.DateMidnight;
import org.openmrs.Encounter;
import org.openmrs.EncounterType;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.Person;
import org.openmrs.api.ConceptService;
import org.openmrs.api.EncounterService;
import org.openmrs.api.ObsService;
import org.openmrs.api.PersonService;
import org.openmrs.api.context.Context;
import org.openmrs.module.emrapi.domainwrapper.DomainWrapperFactory;
import org.openmrs.module.emrapi.patient.PatientDomainWrapper;
import org.openmrs.module.pihcore.metadata.core.EncounterTypes;
import org.openmrs.module.pihcore.metadata.core.PersonAttributeTypes;
import org.openmrs.ui.framework.UiUtils;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.openmrs.ui.framework.page.PageModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class WaitingForConsultPageController {

    public static String WAITING_FOR_CONSULT_STATUS_CONCEPT_UUID = "dee1b1ba-b82a-41b9-897b-28d7868c4bcd" ;

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
    }

    public String get(PageModel model, UiUtils ui,
                      @SpringBean("encounterService") EncounterService encounterService,
                      @SpringBean("obsService") ObsService obsService,
                      @SpringBean("conceptService") ConceptService conceptService,
                      @SpringBean("personService") PersonService personService,
                      @SpringBean("domainWrapperFactory") DomainWrapperFactory domainWrapperFactory) {



        // TODO restrict by location at some point if necessary
        List<Patient> patientsWithVitalsTodayButNoConsultToday = new ArrayList<Patient>();
        List<Patient> patientsWithVitalsYesterdayButNoConsultTodayOrYesterday = new ArrayList<Patient>();

        List<EncounterType> primaryCareEncounterTypes = new ArrayList<EncounterType>();
        primaryCareEncounterTypes.add(encounterService.getEncounterTypeByUuid(EncounterTypes.PRIMARY_CARE_VISIT.uuid()));
        primaryCareEncounterTypes.add(encounterService.getEncounterTypeByUuid(EncounterTypes.PRIMARY_CARE_PEDS_INITIAL_CONSULT.uuid()));
        primaryCareEncounterTypes.add(encounterService.getEncounterTypeByUuid(EncounterTypes.PRIMARY_CARE_PEDS_FOLLOWUP_CONSULT.uuid()));
        primaryCareEncounterTypes.add(encounterService.getEncounterTypeByUuid(EncounterTypes.PRIMARY_CARE_ADULT_INITIAL_CONSULT.uuid()));
        primaryCareEncounterTypes.add(encounterService.getEncounterTypeByUuid(EncounterTypes.PRIMARY_CARE_ADULT_FOLLOWUP_CONSULT.uuid()));

        List<Encounter> vitalsEncountersToday = encounterService.getEncounters(null, null, new DateMidnight().toDate(), null,
                null, Collections.singletonList(encounterService.getEncounterTypeByUuid(EncounterTypes.VITALS.uuid())),
                null, null, null, false);

        List<Encounter> primaryCareEncountersToday = encounterService.getEncounters(null, null, new DateMidnight().toDate(), null,
                null, primaryCareEncounterTypes,
                null, null, null, false);

        // make a list of all patients with vitals today
        for (Encounter encounter : vitalsEncountersToday) {
            // we check that the patient isn't already in the list instead of just inserting again because we want to keep the earliest date
            if (!patientsWithVitalsTodayButNoConsultToday.contains(encounter.getPatient())) {
                patientsWithVitalsTodayButNoConsultToday.add(encounter.getPatient());
            }
        }

        // remove any of those that already have a consult today
        for (Encounter encounter : primaryCareEncountersToday) {
            if (patientsWithVitalsTodayButNoConsultToday.contains(encounter.getPatient())) {
                patientsWithVitalsTodayButNoConsultToday.remove(encounter.getPatient());
            }
        }

        List<Encounter> vitalsEncountersYesterday = encounterService.getEncounters(null, null, new DateMidnight().minusDays(1).toDate(),
                new DateMidnight().toDate(), null, Collections.singletonList(encounterService.getEncounterTypeByUuid(EncounterTypes.VITALS.uuid())),
                null, null, null, false);

        List<Encounter> consultEncountersYesterdayAndToday = encounterService.getEncounters(null, null, new DateMidnight().minusDays(1).toDate(),
                null, null, primaryCareEncounterTypes,
                null, null, null, false);

        // make a list of all patients with vitals yesterday
        for (Encounter encounter : vitalsEncountersYesterday) {
            // we check that the patient isn't already in the list instead of just inserting again because we want to keep the earliest date
            if (!patientsWithVitalsYesterdayButNoConsultTodayOrYesterday.contains(encounter.getPatient())) {
                patientsWithVitalsYesterdayButNoConsultTodayOrYesterday.add(encounter.getPatient());
            }
        }

        // remove any of those that already have a consult today or yesteday
        for (Encounter encounter : consultEncountersYesterdayAndToday) {
            if (patientsWithVitalsYesterdayButNoConsultTodayOrYesterday.contains(encounter.getPatient())) {
                patientsWithVitalsYesterdayButNoConsultTodayOrYesterday.remove(encounter.getPatient());
            }
        }

        // now combine the lists
        List<Patient> patientsWhoNeedConsult = patientsWithVitalsYesterdayButNoConsultTodayOrYesterday;

        // patients from today get added to the end of the list (?)
        for (Patient patient : patientsWithVitalsTodayButNoConsultToday) {
            if (!patientsWhoNeedConsult.contains(patient)) {
                patientsWhoNeedConsult.add(patient);
            }
        }


        //List<PatientDomainWrapper> patientsWhoNeedConsultWrapped = new ArrayList<PatientDomainWrapper>();
        List<WaitingStatusWrapper> patientsWhoNeedConsultWrapped = new ArrayList<WaitingStatusWrapper>();

        // now wrap them all in a patient domain wrap
        for (Patient patient : patientsWhoNeedConsult) {
            WaitingStatusWrapper waitingStatusWrapper = new WaitingStatusWrapper();
            waitingStatusWrapper.setPatientDomainWrapper(domainWrapperFactory.newPatientDomainWrapper(patient));
            Obs status = getLatestWaitingStatus(patient, obsService, conceptService,  new DateMidnight().minusDays(1).toDate());
            if ( status != null ) {
                waitingStatusWrapper.setStatus(status);
            }
            patientsWhoNeedConsultWrapped.add(waitingStatusWrapper);
        }

        // used to determine whether or not we display a link to the patient in the results list
        model.addAttribute("patientsWhoNeedConsult", patientsWhoNeedConsultWrapped);
        model.addAttribute("mothersFirstName", personService.getPersonAttributeTypeByUuid(PersonAttributeTypes.MOTHERS_FIRST_NAME.uuid()));

        return null;
    }

    private Obs getLatestWaitingStatus(Patient patient, ObsService obsService, ConceptService conceptService, Date since) {
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
    }
}
