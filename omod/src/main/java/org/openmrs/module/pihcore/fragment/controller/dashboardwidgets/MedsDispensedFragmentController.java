package org.openmrs.module.pihcore.fragment.controller.dashboardwidgets;

import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.JsonNode;
import org.openmrs.Concept;
import org.openmrs.ConceptName;
import org.openmrs.MedicationDispense;
import org.openmrs.Patient;
import org.openmrs.api.ConceptService;
import org.openmrs.api.MedicationDispenseService;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;
import org.openmrs.module.appframework.domain.AppDescriptor;
import org.openmrs.module.dispensing.DispensedMedication;
import org.openmrs.module.dispensing.api.DispensingService;
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
                           @SpringBean("dispensingService") DispensingService dispensingService,
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

        // We organize all drug concepts dispensed by dispense date (time excluded)
        Map<String, List<Concept>> medsDispensed = new HashMap<>();
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        Locale locale = Context.getLocale();

        // Get all Med Dispensed based on obs (using Dispensing module)
        List<DispensedMedication> dispensedMedications = dispensingService.getDispensedMedication(patient, null, null, null, null, null);
        if (dispensedMedications != null && !dispensedMedications.isEmpty()) {
            for (DispensedMedication dispensedMedication : dispensedMedications) {
                Date dispenseDate = dispensedMedication.getDispensedDateTime();
                if (dispenseDate != null && dispensedMedication.getDrug() != null) {
                    Concept concept = dispensedMedication.getDrug().getConcept();
                    List<Concept> medsOnDate = medsDispensed.computeIfAbsent(df.format(dispenseDate), k -> new ArrayList<>());
                    medsOnDate.add(concept);
                }
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
            if (dispenseDate != null) {
                List<Concept> medsOnDate = medsDispensed.computeIfAbsent(df.format(dispenseDate), k -> new ArrayList<>());
                medsOnDate.add(medConcept);
            }
        }

        // Now that we have all, sort them in reverse chronological order
        List<String> datesToInclude = new ArrayList<>(medsDispensed.keySet());
        Collections.sort(datesToInclude);
        Collections.reverse(datesToInclude);

        // If configured to do so, limit the returned data to the most recent maxDatesToShow dates from configuration
        String maxDatesStr = getConfigValue(app, "maxDatesToShow");
        Integer maxDates = StringUtils.isNotBlank(maxDatesStr) ? Integer.parseInt(maxDatesStr) : null;
        if (maxDates != null && maxDates < datesToInclude.size()) {
            datesToInclude = datesToInclude.subList(0, maxDates);
        }

        // Iterate through the dates and construct the values to render
        List<Map<String, Object>> medsToDisplay = new ArrayList<>();
        for (String dateYmd : datesToInclude) {

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

        Concept medDispensedName = conceptService.getConceptByUuid(CustomAppLoaderConstants.MED_DISPENSED_NAME_UUID);
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
