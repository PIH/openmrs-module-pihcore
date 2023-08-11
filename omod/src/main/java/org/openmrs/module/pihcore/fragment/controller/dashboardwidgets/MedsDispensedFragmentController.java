package org.openmrs.module.pihcore.fragment.controller.dashboardwidgets;

import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.JsonNode;
import org.openmrs.Concept;
import org.openmrs.ConceptName;
import org.openmrs.Encounter;
import org.openmrs.EncounterType;
import org.openmrs.MedicationDispense;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.api.ConceptService;
import org.openmrs.api.EncounterService;
import org.openmrs.api.MedicationDispenseService;
import org.openmrs.api.ObsService;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;
import org.openmrs.module.appframework.domain.AppDescriptor;
import org.openmrs.module.emrapi.patient.PatientDomainWrapper;
import org.openmrs.module.pihcore.apploader.CustomAppLoaderConstants;
import org.openmrs.parameter.MedicationDispenseCriteria;
import org.openmrs.ui.framework.UiUtils;
import org.openmrs.ui.framework.annotation.FragmentParam;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.openmrs.ui.framework.fragment.FragmentConfiguration;
import org.openmrs.ui.framework.fragment.FragmentModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * The purpose of this is to provide a unified view of recent medications dispensed on the patient dashboard,
 * whether those were collected using the Medications Dispensed Obs Group or via the more recently added Domain object
 */
public class MedsDispensedFragmentController {

    private final Logger log = LoggerFactory.getLogger(getClass());

    public void controller(@SpringBean("patientService") PatientService patientService,
                           @SpringBean("encounterService") EncounterService encounterService,
                           @SpringBean("obsService") ObsService obsService,
                           @SpringBean("medicationDispenseService") MedicationDispenseService medicationDispenseService,
                           @SpringBean("conceptService") ConceptService conceptService,
                           @FragmentParam("app") AppDescriptor app,
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
        model.put("patient", patient);
        model.put("app", app);

        // The existing behavior seems to be
        // Get the last 5 encounters of the given type (PihEmrConfigConstants.ENCOUNTERTYPE_MEDICATION_DISPENSED_UUID)
        // Render one row for each observation in these encounters with concept = CustomAppLoaderConstants.MED_DISPENSED_NAME_UUID
        // If any of the encounters has no observations with this concept, render an empty row
        // Where the observation has a valueDrug, display the associated Concept (using short name if available)

        Map<String, List<Concept>> medsDispensed = new HashMap<>();
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        Locale locale = Context.getLocale();

        String encounterTypesStr = getConfigValue(app, "encounterTypes");
        List<EncounterType> encounterTypes = new ArrayList<>();
        if (StringUtils.isNotBlank(encounterTypesStr)) {
            for (String type : encounterTypesStr.split(",")) {
                encounterTypes.add(encounterService.getEncounterTypeByUuid(type.trim()));
            }
        }

        // Get all Med Dispensed Name observations from the given encounters
        Concept medDispensedName = conceptService.getConceptByUuid(CustomAppLoaderConstants.MED_DISPENSED_NAME_UUID);
        List<Obs> obsList = obsService.getObservationsByPersonAndConcept(patient, medDispensedName);
        for (Obs o : obsList) {
            Encounter e = o.getEncounter();
            if (encounterTypes.isEmpty() || (e != null && encounterTypes.contains(e.getEncounterType()))) {
                Concept concept = (o.getValueDrug() != null ? o.getValueDrug().getConcept() : o.getValueCoded());
                Date dispenseDate = e.getEncounterDatetime();
                List<Concept> medsOnDate = medsDispensed.computeIfAbsent(df.format(dispenseDate), k -> new ArrayList<>());
                medsOnDate.add(concept);
            }
        }

        // Get all Med Dispensed records from the Domain
        MedicationDispenseCriteria criteria = new MedicationDispenseCriteria();
        criteria.setPatient(patient);
        criteria.setIncludeVoided(false);
        List<MedicationDispense> dispenses = medicationDispenseService.getMedicationDispenseByCriteria(criteria);
        for (MedicationDispense d : dispenses) {
            Concept medConcept = (d.getDrug() != null ? d.getDrug().getConcept() : d.getConcept());
            Date dispenseDate = d.getDateHandedOver();
            if (dispenseDate == null) {
                dispenseDate = d.getEncounter() != null ? d.getEncounter().getEncounterDatetime() : d.getDateCreated();
            }
            List<Concept> medsOnDate = medsDispensed.computeIfAbsent(df.format(dispenseDate), k -> new ArrayList<>());
            medsOnDate.add(medConcept);
        }

        // Now that we have all, return all from the most recent dates based on the maxDatesToShow parameter, defaulting to 5

        String maxDatesStr = getConfigValue(app, "maxDatesToShow");
        int maxDates = StringUtils.isNotBlank(maxDatesStr) ? Integer.parseInt(maxDatesStr) : 5;

        List<String> allDates = new ArrayList<>(medsDispensed.keySet());
        Collections.sort(allDates);
        Collections.reverse(allDates);

        if (maxDates < allDates.size()) {
            allDates = allDates.subList(0, maxDates);
        }

        // Iterate through the dates and construct the values to render
        List<Map<String, Object>> medsToDisplay = new ArrayList<>();
        for (String dateYmd : allDates) {

            for (Concept concept : medsDispensed.get(dateYmd)) {
                Map<String, Object> row = new HashMap<>();
                row.put("dispenseDate", df.parse(dateYmd));
                row.put("dispenseConcept", concept);
                ConceptName name = concept.getShortNameInLocale(locale);
                if (name == null) {
                    name = concept.getName();
                }
                row.put("dispenseConceptName", name.getName());
                medsToDisplay.add(row);
            }
        }

        model.put("medsDispensedHeaderName", medDispensedName.getShortestName(Context.getLocale(), false));
        model.put("detailsUrl", getConfigValue(app, "detailsUrl"));
        model.put("medsToDisplay", medsToDisplay);
    }

    private String getConfigValue(AppDescriptor app, String configValue) {
        JsonNode node = app.getConfig().get(configValue);
        if (node == null) {
            return "";
        }
        return node.asText();
    }

}
