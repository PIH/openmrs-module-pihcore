package org.openmrs.module.pihcore.fragment.controller.dashboardwidgets;

import org.joda.time.DateTime;
import org.openmrs.Concept;
import org.openmrs.Encounter;
import org.openmrs.EncounterType;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.api.ConceptService;
import org.openmrs.api.EncounterService;
import org.openmrs.api.ObsService;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;
import org.openmrs.module.emrapi.patient.PatientDomainWrapper;
import org.openmrs.module.pihcore.PihEmrConfigConstants;
import org.openmrs.parameter.EncounterSearchCriteriaBuilder;
import org.openmrs.ui.framework.UiUtils;
import org.openmrs.ui.framework.annotation.InjectBeans;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.openmrs.ui.framework.fragment.FragmentConfiguration;
import org.openmrs.ui.framework.fragment.FragmentModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class HivAlertsFragmentController {

	private final Logger log = LoggerFactory.getLogger(getClass());

	public void controller(@SpringBean("obsService") ObsService obsService,
							@SpringBean("conceptService") ConceptService conceptService,
							@SpringBean("encounterService") EncounterService encounterService,
							@SpringBean("patientService") PatientService patientService,
						   @InjectBeans PatientDomainWrapper patientWrapper,
							UiUtils ui,
							FragmentConfiguration config,
							FragmentModel model) {

		// pretty much stole all this from the coreapps dashboard widgets controller... could this be simpler or extracted out somewhere?
		Object patient = null;
		patient = config.get("patient");
		if (patient == null ) {
			patient = config.get("patientId");
		}

		if (patient != null) {
			if (patient instanceof Patient) {
				patientWrapper.setPatient((Patient) patient);
			} else if (patient instanceof PatientDomainWrapper) {
				patientWrapper = (PatientDomainWrapper) patient;
			} else if (patient instanceof Integer) {
				// assume we have patientId
				patientWrapper.setPatient(Context.getPatientService().getPatient((Integer) patient));
			} else {
				throw new IllegalArgumentException("Patient must be of type Patient or PatientDomainWrapper");
			}
		}

		generateLateForMedPickupAlert(model, patientWrapper, encounterService, conceptService, obsService);
		generateLateForAppointmentAlert(model, patientWrapper, encounterService, conceptService,obsService);

	}

	private void generateLateForMedPickupAlert(FragmentModel model, PatientDomainWrapper patientWrapper, EncounterService encounterService, ConceptService conceptService, ObsService obsService) {
		Date returnVisitDateForDispensing = null;
		EncounterType drugDispensing = encounterService.getEncounterTypeByUuid(PihEmrConfigConstants.ENCOUNTERTYPE_HIV_DISPENSING_UUID);
		Concept returnVisitDateConcept = conceptService.getConceptByMapping("RETURN VISIT DATE", "PIH");

		EncounterSearchCriteriaBuilder encounterSearchCriteriaBuilder = new EncounterSearchCriteriaBuilder();
		encounterSearchCriteriaBuilder.setEncounterTypes(Collections.singletonList(drugDispensing));
		encounterSearchCriteriaBuilder.setPatient(patientWrapper.getPatient());
		List<Encounter> dispensingEncounters = encounterService.getEncounters(encounterSearchCriteriaBuilder.createEncounterSearchCriteria());

		Encounter mostRecentDispensingEncounter = null;
		if (dispensingEncounters != null && dispensingEncounters.size() > 0) {
			mostRecentDispensingEncounter = dispensingEncounters.get(dispensingEncounters.size()-1);  // sorted by date, so most recent should be at end of list
		}

		if (mostRecentDispensingEncounter != null) {
			List<Obs> returnVisitDates = obsService.getObservations(Collections.singletonList(patientWrapper.getPatient()), Collections.singletonList(mostRecentDispensingEncounter), Collections.singletonList(returnVisitDateConcept), null, null, null, null, null, null, null, null, false);
			if (returnVisitDates != null && returnVisitDates.size() > 0) {
				if (returnVisitDates.size() > 1) {
					log.warn("Multiple return visit dates found, choosing one arbitrarily");
				}
				returnVisitDateForDispensing = returnVisitDates.get(0).getValueDate();
			}
		}

		if (returnVisitDateForDispensing != null && returnVisitDateForDispensing.before(new Date())) {
			model.put("lateReturnVisitDateForDispensing", returnVisitDateForDispensing);
		}
		else {
			model.put("lateReturnVisitDateForDispensing", null);
		}

		// if more than 28 days late, highlight
		if (returnVisitDateForDispensing != null && returnVisitDateForDispensing.before(new DateTime().minusDays(28).toDate())) {
			model.put("lateReturnVisitDateForDispensingHighlight", returnVisitDateForDispensing);
		}
		else {
			model.put("lateReturnVisitDateForDispensingHighlight", null);
		}
	}

	private void generateLateForAppointmentAlert(FragmentModel model, PatientDomainWrapper patientWrapper, EncounterService encounterService, ConceptService conceptService, ObsService obsService) {

		Date returnVisitDate = null;
		EncounterType hivInitial = encounterService.getEncounterTypeByUuid(PihEmrConfigConstants.ENCOUNTERTYPE_HIV_INTAKE_UUID);
		EncounterType hivFollowup = encounterService.getEncounterTypeByUuid(PihEmrConfigConstants.ENCOUNTERTYPE_HIV_FOLLOWUP_UUID);
		EncounterType pmtctIntake = encounterService.getEncounterTypeByUuid(PihEmrConfigConstants.ENCOUNTERTYPE_PMTCT_INTAKE_UUID);
		EncounterType pmtctFollowup = encounterService.getEncounterTypeByUuid(PihEmrConfigConstants.ENCOUNTERTYPE_PMTCT_FOLLOWUP_UUID);
		EncounterType eidFollowup = encounterService.getEncounterTypeByUuid(PihEmrConfigConstants.ENCOUNTERTYPE_EID_FOLLOWUP_UUID);
		Concept returnVisitDateConcept = conceptService.getConceptByMapping("RETURN VISIT DATE", "PIH");

		EncounterSearchCriteriaBuilder encounterSearchCriteriaBuilder = new EncounterSearchCriteriaBuilder();
		encounterSearchCriteriaBuilder.setEncounterTypes(Arrays.asList(hivInitial, hivFollowup, pmtctIntake, pmtctFollowup, eidFollowup));
		encounterSearchCriteriaBuilder.setPatient(patientWrapper.getPatient());
		List<Encounter> dispensingEncounters = encounterService.getEncounters(encounterSearchCriteriaBuilder.createEncounterSearchCriteria());

		Encounter mostRecentEncounter = null;
		if (dispensingEncounters != null && dispensingEncounters.size() > 0) {
			mostRecentEncounter = dispensingEncounters.get(dispensingEncounters.size()-1);  // sorted by date, so most recent should be at end of list
		}

		if (mostRecentEncounter != null) {
			List<Obs> returnVisitDates = obsService.getObservations(Collections.singletonList(patientWrapper.getPatient()), Collections.singletonList(mostRecentEncounter), Collections.singletonList(returnVisitDateConcept), null, null, null, null, null, null, null, null, false);
			if (returnVisitDates != null && returnVisitDates.size() > 0) {
				if (returnVisitDates.size() > 1) {
					log.warn("Multiple return visit dates found, choosing one arbitrarily");
				}
				returnVisitDate = returnVisitDates.get(0).getValueDate();
			}
		}

		if (returnVisitDate != null && returnVisitDate.before(new Date())) {
			model.put("lateReturnVisitDateForAppointment", returnVisitDate);
		}
		else {
			model.put("lateReturnVisitDateForAppointment", null);
		}
	}

}
