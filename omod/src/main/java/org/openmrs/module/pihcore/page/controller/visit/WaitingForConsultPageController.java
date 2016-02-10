package org.openmrs.module.pihcore.page.controller.visit;

import org.joda.time.DateMidnight;
import org.openmrs.Encounter;
import org.openmrs.Patient;
import org.openmrs.api.EncounterService;
import org.openmrs.api.PersonService;
import org.openmrs.module.emrapi.domainwrapper.DomainWrapperFactory;
import org.openmrs.module.emrapi.patient.PatientDomainWrapper;
import org.openmrs.module.pihcore.metadata.core.EncounterTypes;
import org.openmrs.module.pihcore.metadata.core.PersonAttributeTypes;
import org.openmrs.ui.framework.UiUtils;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.openmrs.ui.framework.page.PageModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class WaitingForConsultPageController {

    public String get(PageModel model, UiUtils ui,
                      @SpringBean("encounterService") EncounterService encounterService,
                      @SpringBean("personService") PersonService personService,
                      @SpringBean("domainWrapperFactory") DomainWrapperFactory domainWrapperFactory) {

        // TODO restrict by location at some point if necessary
        List<Patient> patientsWithVitalsTodayButNoConsultToday = new ArrayList<Patient>();
        List<Patient> patientsWithVitalsYesterdayButNoConsultTodayOrYesterday = new ArrayList<Patient>();

        List<Encounter> vitalsEncountersToday = encounterService.getEncounters(null, null, new DateMidnight().toDate(), null,
                null, Collections.singletonList(encounterService.getEncounterTypeByUuid(EncounterTypes.VITALS.uuid())),
                null, null, null, false);

        List<Encounter> primaryCareEncountersToday = encounterService.getEncounters(null, null, new DateMidnight().toDate(), null,
                null, Collections.singletonList(encounterService.getEncounterTypeByUuid(EncounterTypes.PRIMARY_CARE_VISIT.uuid())),
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
                null, null, Collections.singletonList(encounterService.getEncounterTypeByUuid(EncounterTypes.PRIMARY_CARE_VISIT.uuid())),
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

        List<PatientDomainWrapper> patientsWhoNeedConsultWrapped = new ArrayList<PatientDomainWrapper>();

        // now wrap them all in a patient domain wrap
        for (Patient patient : patientsWhoNeedConsult) {
            patientsWhoNeedConsultWrapped.add(domainWrapperFactory.newPatientDomainWrapper(patient));
        }

        // used to determine whether or not we display a link to the patient in the results list
        model.addAttribute("patientsWhoNeedConsult", patientsWhoNeedConsultWrapped);
        model.addAttribute("mothersFirstName", personService.getPersonAttributeTypeByUuid(PersonAttributeTypes.MOTHERS_FIRST_NAME.uuid()));

        return null;
    }
}
