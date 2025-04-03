package org.openmrs.module.pihcore.fragment.controller.dashboardwidgets;

import org.codehaus.jackson.JsonNode;
import org.openmrs.Concept;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.api.ConceptService;
import org.openmrs.api.ObsService;
import org.openmrs.api.PatientService;
import org.openmrs.module.appframework.domain.AppDescriptor;
import org.openmrs.module.emrapi.diagnosis.ObsGroupDiagnosisService;
import org.openmrs.module.emrapi.patient.PatientDomainWrapper;
import org.openmrs.ui.framework.UiUtils;
import org.openmrs.ui.framework.annotation.FragmentParam;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.openmrs.ui.framework.fragment.FragmentConfiguration;
import org.openmrs.ui.framework.fragment.FragmentModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The purpose of this is to render one row per distinct coded value for the given patient and the given configured
 * `concept`, where the most recent observation per coded value is used.
 * If a particular `codedValueSet` is configured, it limits the coded values rendered to just those within that set
 */
public class MostRecentObsWithCodedValueFragmentController {

    private final Logger log = LoggerFactory.getLogger(getClass());

    public void controller(@SpringBean("patientService") PatientService patientService,
                           @SpringBean("obsService") ObsService obsService,
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

        Map<Concept, Obs> latestObsWithCodedValue = new HashMap<>();

        // First get all observations for the configured concept and organize the most recent by coded value
        String conceptLookup = getConfigValue(app, "concept");
        Concept concept = conceptService.getConceptByReference(conceptLookup);
        String codedValueSetLookup = getConfigValue(app, "codedValueSet");
        Concept codedValueSet = conceptService.getConceptByReference(codedValueSetLookup);
        if (concept != null) {
            List<Obs> obsList = obsService.getObservationsByPersonAndConcept(patient, concept);
            for (Obs o : obsList) {
                Concept valueCoded = o.getValueCoded();
                if (codedValueSet == null || codedValueSet.getSetMembers().contains(valueCoded)) {
                    Obs latestValue = latestObsWithCodedValue.get(valueCoded);
                    if (latestValue == null || latestValue.getObsDatetime().before(o.getObsDatetime())) {
                        latestObsWithCodedValue.put(valueCoded, o);
                    }
                }
            }
        }

        // Return the distinct obs values, sorted by date descending
        List<Obs> obsList = new ArrayList<>(latestObsWithCodedValue.values());
        obsList.sort(Comparator.comparing(Obs::getObsDatetime).reversed());

        model.put("obsList", obsList);
        model.put("codedValueHeader", getConfigValue(app, "codedValueHeader"));
        model.put("detailsUrl", getConfigValue(app, "detailsUrl"));
    }

    private String getConfigValue(AppDescriptor app, String configValue) {
        JsonNode node = app.getConfig().get(configValue);
        if (node == null) {
            return "";
        }
        return node.asText();
    }

}
