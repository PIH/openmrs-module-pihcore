package org.openmrs.module.pihcore.fragment.controller.dashboardwidgets;

import org.joda.time.DateTime;
import org.openmrs.Concept;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.api.ConceptService;
import org.openmrs.api.ObsService;
import org.openmrs.module.appframework.domain.AppDescriptor;
import org.openmrs.module.emrapi.patient.PatientDomainWrapper;
import org.openmrs.ui.framework.SimpleObject;
import org.openmrs.ui.framework.UiUtils;
import org.openmrs.ui.framework.annotation.FragmentParam;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.openmrs.ui.framework.fragment.FragmentConfiguration;
import org.openmrs.ui.framework.fragment.FragmentModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class InfectiousDiseasesFragmentController {

    private final Logger log = LoggerFactory.getLogger(getClass());

    // CIEL:163722 — HIV rapid test result
    private static final String CIEL_CONCEPT_SOURCE = "CIEL";
    private static final String HIV_RAPID_TEST_CONCEPT_CODE = "163722";
    private static final String TEST_POSITIVE_CODE="703";
    
    // Syphilis rapid test
    private static final String SYPHILLIS_RAPID_TEST_CONCEPT_CODE = "165303";
    private static final String SYPHILLIS_TEST_POSITIVE_CODE="1228";
    
    // Hepatitis B test
    private static final String HEPATITIS_B_TEST_CONCEPT_CODE = "159430";

    public void controller(
            @SpringBean("conceptService") ConceptService conceptService,
            @SpringBean("obsService") ObsService obsService,
            @FragmentParam("app") AppDescriptor app,
            UiUtils ui,
            FragmentConfiguration config,
            FragmentModel model) {

        Patient patient = getPatientFromConfig(config);

        model.put("app", app);
        model.put("patient", patient);

        Map<String, SimpleObject> fields = new LinkedHashMap<>();

        // HIV rapid test
        addRapidTestField(fields, "hivRapidTest", "pihcore.hiv.state.title",
                HIV_RAPID_TEST_CONCEPT_CODE, CIEL_CONCEPT_SOURCE, TEST_POSITIVE_CODE,
                conceptService, obsService, patient, ui);

        // Syphilis rapid test
        addRapidTestField(fields, "syphillisRapidTest", "pihcore.syphillis.state.title",
                SYPHILLIS_RAPID_TEST_CONCEPT_CODE, CIEL_CONCEPT_SOURCE, SYPHILLIS_TEST_POSITIVE_CODE,
                conceptService, obsService, patient, ui);

        // Hepatitis B test
        addRapidTestField(fields, "hepatitisBTest", "pihcore.hepatitisB.state.title",
                HEPATITIS_B_TEST_CONCEPT_CODE, CIEL_CONCEPT_SOURCE, TEST_POSITIVE_CODE,
                conceptService, obsService, patient, ui);

        model.put("fields", fields);
    }

    /**
     * Extracts patient from the fragment configuration
     * @param config Fragment configuration
     * @return Patient object
     * @throws IllegalArgumentException if no patient is found
     */
    private Patient getPatientFromConfig(FragmentConfiguration config) {
        Object patientConfig = config.get("patient");
        Patient patient = null;
        if (patientConfig instanceof PatientDomainWrapper) {
            patient = ((PatientDomainWrapper) patientConfig).getPatient();
        }
        if (patient == null) {
            throw new IllegalArgumentException("No patient found. Please pass patient into the configuration");
        }
        return patient;
    }

    /**
     * Adds a rapid test field to the fields map
     * @param fields Map to add the field to
     * @param fieldKey Key for the field in the map
     * @param labelKey Message key for the label
     * @param conceptCode Concept code
     * @param conceptSource Concept source
     * @param conceptService Concept service
     * @param obsService Obs service
     * @param patient Patient
     * @param ui UI utilities
     */
    private void addRapidTestField(Map<String, SimpleObject> fields, String fieldKey, String labelKey,
                                   String conceptCode, String conceptSource, String conceptRedCode,
                                   ConceptService conceptService, ObsService obsService,
                                   Patient patient, UiUtils ui) {
        Concept concept = conceptService.getConceptByMapping(conceptCode, conceptSource);
        Concept positiveConcept = conceptService.getConceptByMapping(conceptRedCode, conceptSource);
        Obs mostRecentObs = getMostRecentObs(concept, conceptCode, obsService, patient);
        String cssClass = determineCssClass(mostRecentObs, positiveConcept);
        
        fields.put(fieldKey, createTestResultObject(labelKey, cssClass, mostRecentObs, ui));
    }

    /**
     * Retrieves the most recent observation for a given concept
     * @param concept The concept to search for
     * @param conceptCode The concept code (for logging)
     * @param obsService Obs service
     * @param patient Patient
     * @return Most recent Obs or null if not found
     */
    private Obs getMostRecentObs(Concept concept, String conceptCode, ObsService obsService, Patient patient) {
        if (concept == null) {
            log.warn("Could not find concept CIEL:{} — Test result will not be displayed", conceptCode);
            return null;
        }

        List<Obs> obsList = obsService.getObservationsByPersonAndConcept(patient, concept);
        Optional<Obs> latest = obsList.stream()
                .filter(o -> !o.getVoided())
                .max(Comparator.comparing(Obs::getObsDatetime));
        
        return latest.orElse(null);
    }

    /**
     * Determines the CSS class based on whether the observation's value matches the concept UUID
     * @param obs The observation
     * @param concept The concept to compare against
     * @return "red" if UUIDs match, empty string otherwise
     */
    private String determineCssClass(Obs obs, Concept concept) {
        if (obs != null && concept != null) {
            Concept valueCoded = obs.getValueCoded();
            if (valueCoded != null && valueCoded.getUuid().equals(concept.getUuid())) {
                return "red";
            }
        }
        return "";
    }

    /**
     * Creates a SimpleObject for a test result
     * @param labelKey Message key for the label
     * @param cssClass CSS class to apply
     * @param obs The observation
     * @param ui UI utilities
     * @return SimpleObject with test result data
     */
    private SimpleObject createTestResultObject(String labelKey, String cssClass, Obs obs, UiUtils ui) {
        return SimpleObject.create(
                "label", labelKey,
                "css", cssClass,
                "obs", obs != null ? ui.format(obs.getValueCoded()) : "",
                "date", obs != null ? ui.formatDateWithClientTimezone(new DateTime(obs.getObsDatetime()).toDate()) : null
        );
    }
}
