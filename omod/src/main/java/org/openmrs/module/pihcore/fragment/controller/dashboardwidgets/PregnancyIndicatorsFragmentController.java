package org.openmrs.module.pihcore.fragment.controller.dashboardwidgets;

import org.openmrs.Concept;
import org.openmrs.Encounter;
import org.openmrs.EncounterType;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.api.ConceptService;
import org.openmrs.api.EncounterService;
import org.openmrs.module.appframework.domain.AppDescriptor;
import org.openmrs.module.emrapi.EmrApiProperties;
import org.openmrs.module.emrapi.patient.PatientDomainWrapper;
import org.openmrs.module.pihcore.PihCoreUtils;
import org.openmrs.module.pihcore.PihEmrConfigConstants;
import org.openmrs.module.pihcore.SierraLeoneConfigConstants;
import org.openmrs.parameter.EncounterSearchCriteriaBuilder;
import org.openmrs.ui.framework.SimpleObject;
import org.openmrs.ui.framework.UiUtils;
import org.openmrs.ui.framework.annotation.FragmentParam;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.openmrs.ui.framework.fragment.FragmentConfiguration;
import org.openmrs.ui.framework.fragment.FragmentModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class PregnancyIndicatorsFragmentController {

    private final Logger log = LoggerFactory.getLogger(getClass());

    public void controller(
            @SpringBean("conceptService") ConceptService conceptService,
            @SpringBean("encounterService") EncounterService encounterService,
            @SpringBean EmrApiProperties emrApiProperties,
            @FragmentParam("app") AppDescriptor app,
            UiUtils ui,
            FragmentConfiguration config,
            FragmentModel model) {

        Object patientConfig = config.get("patient");
        Patient patient = null;
        if (patientConfig != null && (patientConfig instanceof PatientDomainWrapper)) {
            patient = ((PatientDomainWrapper) patientConfig).getPatient();
        }
        if (patient == null) {
            throw new IllegalArgumentException("No patient found. Please pass patient into the configuration");
        }

        model.put("app", app);
        model.put("patient", patient);
        String programUuid = PihCoreUtils.getConfigValue(app, "duringCurrentEnrollmentInProgram");

        Map<String, SimpleObject> fields = new LinkedHashMap<>();

        fields.put("status", SimpleObject.create(
                "label", "pihcore.status",
                "css", "red",
                "obs", ui.message("pihcore.pregnant")
        ));
        Concept question = conceptService.getConceptByUuid(SierraLeoneConfigConstants.CONCEPT_ESTIMATEDDUEDATE_UUID);
        List<Obs> obsWithinProgram = null;
        Date dueDate= null;
        if ( question != null ) {
            obsWithinProgram = PihCoreUtils.getObsWithinProgram(patient, new HashSet<>(Arrays.asList(question)), null, programUuid);
            if ( obsWithinProgram == null || obsWithinProgram.size() < 1) {
                // if there are no ESTIMATED_DUE_DATE obs then look for
                // GESTATIONAL_AGE_IN_WEEKS obs
                question = conceptService.getConceptByUuid(SierraLeoneConfigConstants.CONCEPT_GESTATIONALAGEINWEEKS_UUID);
                if ( question != null ) {
                    obsWithinProgram = PihCoreUtils.getObsWithinProgram(patient, new HashSet<>(Arrays.asList(question)), null, programUuid);
                    if (obsWithinProgram != null && obsWithinProgram.size() > 0 ) {
                        dueDate = calculateDueDate(obsWithinProgram.get(0));
                    }
                }
            } else {
                dueDate = obsWithinProgram.get(0).getValueDatetime();
            }
        }

        fields.put("dueDate", SimpleObject.create(
                "label", "pihcore.delivery.dueDate",
                "css", "red",
                "obs", (dueDate !=null ) ? dueDate : ""
        ));

        question = conceptService.getConceptByUuid(SierraLeoneConfigConstants.CONCEPT_PREGNANCYRISKFACTORS_UUID);
        Concept answer = conceptService.getConceptByUuid(PihEmrConfigConstants.CONCEPT_MULTIPLEBIRTH_UUID);
        if ( question != null && answer != null) {
            obsWithinProgram = PihCoreUtils.getObsWithinProgram(patient, new HashSet<>(Arrays.asList(question)), new HashSet<>(Arrays.asList(answer)), programUuid);
        }
        fields.put("multipleGestation", SimpleObject.create(
                "label", "pihcore.multiple.birth",
                "obs", (obsWithinProgram!=null && obsWithinProgram.size() > 0 ) ? ui.message("general.yes") : ui.message("general.no")
        ));

        question = conceptService.getConceptByUuid(PihEmrConfigConstants.CONCEPT_DELIVERYDATEANDTIME_UUID);
        Date latestDeliveryDate = null;
        Obs latestDeliveryObs = null;
        if ( question != null ) {
            obsWithinProgram = PihCoreUtils.getObsWithinProgram(patient, new HashSet<>(Arrays.asList(question)), null, null);
            for (Obs obs : obsWithinProgram) {
                Date obsValueDate = obs.getValueDate();
                if ( (obsValueDate!= null)  &&
                        (latestDeliveryDate == null || latestDeliveryDate.before(obs.getValueDate()))) {
                    latestDeliveryDate = obs.getValueDate();
                    latestDeliveryObs = obs;
                }
            }
        }
        fields.put("latestDelivery", SimpleObject.create(
                "label", "pihcore.latest.delivery",
                "obs", ( latestDeliveryDate !=null ) ? latestDeliveryDate : ""
        ));

        Set<Obs> methodsOfDelivery = new HashSet<>();
        if ( latestDeliveryObs != null ) {
            Encounter encounter = latestDeliveryObs.getEncounter();
            // search for all the Methods of Delivery associated with the encounter (form) that contains the most recent date/delivery time
            for (Obs obs : encounter.getAllObs(false) ) {
                if (obs.getConcept().getUuid().equals(PihEmrConfigConstants.CONCEPT_TYPEOFDELIVERY_UUID)) {
                    // check if this method of delivery was already added to the list
                    boolean found = false;
                    for (Obs method: methodsOfDelivery) {
                        if ( method.getValueCoded().getUuid().equalsIgnoreCase(obs.getValueCoded().getUuid()) ) {
                            found = true;
                        }
                    }
                    if (!found) {
                        methodsOfDelivery.add(obs);
                    }
                }
            }
        }

        fields.put("deliveryType", SimpleObject.create(
                "label", "pihcore.latest.delivery.type",
                "obs", (methodsOfDelivery != null && methodsOfDelivery.size() > 0 ) ? methodsOfDelivery : ""
        ));

        EncounterSearchCriteriaBuilder searchCriteriaBuilder = new EncounterSearchCriteriaBuilder();
        searchCriteriaBuilder.setPatient(patient);
        searchCriteriaBuilder.setEncounterTypes(new HashSet<>(Arrays.asList(emrApiProperties.getAdmissionEncounterType())));
        List<Encounter> encounters = encounterService.getEncounters(searchCriteriaBuilder.createEncounterSearchCriteria());
        Date lastAdmissionDatetime = null;
        for (Encounter encounter : encounters) {
            Date encounterDatetime = encounter.getEncounterDatetime();
            if ( lastAdmissionDatetime == null || lastAdmissionDatetime.before(encounterDatetime)) {
                // find the most recent Admission datetime
                lastAdmissionDatetime = encounterDatetime;
            }
        }
        fields.put("latestAdmission", SimpleObject.create(
                "label", "pihcore.latest.admission",
                "obs",  lastAdmissionDatetime != null ? lastAdmissionDatetime : ""
        ));

        Set<EncounterType> ancEncTypes = new HashSet<>();
        ancEncTypes.add(encounterService.getEncounterTypeByUuid(PihEmrConfigConstants.ENCOUNTERTYPE_ANC_INTAKE_UUID));
        ancEncTypes.add(encounterService.getEncounterTypeByUuid(PihEmrConfigConstants.ENCOUNTERTYPE_ANC_FOLLOWUP_UUID));

        searchCriteriaBuilder = new EncounterSearchCriteriaBuilder();
        searchCriteriaBuilder.setPatient(patient);
        searchCriteriaBuilder.setEncounterTypes(ancEncTypes);
        if ( programUuid != null ) {
            searchCriteriaBuilder.setFromDate(PihCoreUtils.getActiveProgramEnrollmentDate( patient, programUuid ));
        }
        encounters = encounterService.getEncounters(searchCriteriaBuilder.createEncounterSearchCriteria());
        int numberOfVisits = encounters != null ? encounters.size() : 0;
        for (Encounter encounter : encounters) {
            if (encounter.getEncounterType().getUuid().equals(PihEmrConfigConstants.ENCOUNTERTYPE_ANC_INTAKE_UUID)) {
                for (Obs candidate : encounter.getObsAtTopLevel(false)) {
                    if (candidate.getConcept().getUuid().equalsIgnoreCase(SierraLeoneConfigConstants.CONCEPT_NUMBEROFANCVISITS_UUID)) {
                        Double valueNumeric = candidate.getValueNumeric();
                        if (!valueNumeric.isNaN()) {
                            // the question on the form is "What number ANC Visit is this?", therefore it should include the ANC Intake visit
                            numberOfVisits = numberOfVisits + valueNumeric.intValue() - 1;
                            break;
                        }
                    }
                }
            }
        }

        fields.put("ancVisits", SimpleObject.create(
                "label", "pihcore.anc.visits",
                "obs",  numberOfVisits
        ));

        model.put("fields", fields);
    }

    /**
     * Calculate dueDate based on the recorded Gestational Age
     * @param obs that represents the gestational age
     * @return
     */
    private Date calculateDueDate(Obs obs) {
        Date dueDate = null;
        if (obs != null ) {
            Double gestationalAge = obs.getValueNumeric();
            if ( gestationalAge != null ){
                int gaWeeks = gestationalAge.intValue();
                if (gaWeeks > 0 && gaWeeks <= 40) {
                    Date obsDatetime = obs.getObsDatetime();
                    Calendar calendar = Calendar.getInstance();
                    // we set the calendar to the date when the gestational age was recorded
                    calendar.setTime(obsDatetime);
                    // we add the remaining of weeks to due date
                    calendar.add(Calendar.WEEK_OF_YEAR, 40 - gaWeeks);
                    dueDate = calendar.getTime();
                }
            }
        }
        return dueDate;
    }
}
