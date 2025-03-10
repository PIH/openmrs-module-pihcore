package org.openmrs.module.pihcore.fragment.controller.dashboardwidgets;

import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.JsonNode;
import org.openmrs.Concept;
import org.openmrs.Encounter;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.PatientProgram;
import org.openmrs.Program;
import org.openmrs.api.ConceptService;
import org.openmrs.api.ObsService;
import org.openmrs.api.PatientService;
import org.openmrs.api.ProgramWorkflowService;
import org.openmrs.module.appframework.domain.AppDescriptor;
import org.openmrs.module.emrapi.patient.PatientDomainWrapper;
import org.openmrs.ui.framework.annotation.FragmentParam;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.openmrs.ui.framework.fragment.FragmentConfiguration;
import org.openmrs.ui.framework.fragment.FragmentModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ObsByEncounterFragmentController {
    private final Logger log = LoggerFactory.getLogger(getClass());

    public void controller(@SpringBean("patientService") PatientService patientService,
                           @SpringBean("conceptService") ConceptService conceptService,
                           @SpringBean("obsService") ObsService obsService,
                           @SpringBean("programWorkflowService") ProgramWorkflowService programWorkflowService,
                           @FragmentParam("app") AppDescriptor app,
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

        JsonNode node = app.getConfig().get("concepts");
        if (node == null) {
            throw new IllegalStateException("Missing configuration concepts on widget");
        }
        Set<Concept> concepts = new HashSet<>();
        Iterator<JsonNode> i = node.getElements();
        while (i.hasNext()) {
            String conceptUuid = i.next().getTextValue();
            if (StringUtils.isNotBlank(conceptUuid)) {
                Concept concept = conceptService.getConceptByUuid(conceptUuid);
                if (concept == null ) {
                    throw new IllegalArgumentException("No concept with this UUID: " + conceptUuid + " found. Please pass correct concept UUID into the configuration");
                }
                concepts.add(concept);
            }
        }
        node = app.getConfig().get("headers");
        if (node == null) {
            throw new IllegalStateException("Missing configuration headers on widget");
        }
        List<String> headers = new ArrayList<>();
        i = node.getElements();
        while (i.hasNext()) {
            String header = i.next().getTextValue();
            if (StringUtils.isNotBlank(header)) {
                headers.add(header);
            } else {
                throw new IllegalStateException("Missing configuration, headers entries cannot be empty.");
            }
        }
        model.put("headers", headers);

        Date obsOnOrAfter = null;
        String visitUrl = getConfigValue(app, "visitUrl");
        String programUuid = getConfigValue(app, "duringCurrentEnrollmentInProgram");
        if (StringUtils.isNotBlank(programUuid)) {
            Program program = programWorkflowService.getProgramByUuid(programUuid);
            if (program == null) {
                throw new IllegalStateException("No program with uuid " + programUuid + " is found.  Please check app configuration of " + app.getId());
            }
            for (PatientProgram pp : programWorkflowService.getPatientPrograms(patient, program, null, null, null, null, false)) {
                if (pp.getActive()) {
                    if (obsOnOrAfter == null || obsOnOrAfter.before(pp.getDateEnrolled())) {
                        obsOnOrAfter = pp.getDateEnrolled();
                    }
                }
            }
        }
        List<Obs> obsList = obsService.getObservations(
                Arrays.asList(patient),
                null,
                new ArrayList<>(concepts),
                null,
                null,
                null,
                Arrays.asList("obsDatetime"), //sort by field
                null,
                null,
                obsOnOrAfter, //fromDate
                null,
                false); //includeVoidedObs

        Map<Encounter, List<Obs>> encounterObs = new HashMap<>();
        for (Obs obs : obsList) {
            //group obs by encounter
            List<Obs> existingList = encounterObs.get(obs.getEncounter());
            if (existingList == null ) {
                existingList = new ArrayList<>();
            }
            existingList.add(obs);
            encounterObs.put(obs.getEncounter(), existingList);
        }
        List<Encounter> encounters = new ArrayList<>(encounterObs.keySet());
        // order the encounters by encounterDatetime descending
        Collections.sort(encounters, (e1, e2) -> e2.getEncounterDatetime().compareTo(e1.getEncounterDatetime()));
        Map<Encounter, List<Obs>> sortedEncObs = new LinkedHashMap<>();
        for (Encounter encounter : encounters) {
            sortedEncObs.put(encounter, encounterObs.get(encounter));
        }

        Boolean showTime = app.getConfig().get("showTime") != null ? app.getConfig().get("showTime").getBooleanValue() : false;
        model.put("minValue", app.getConfig().get("minValue") != null ? app.getConfig().get("minValue").getTextValue() : "");
        model.put("maxValue", app.getConfig().get("maxValue") != null ? app.getConfig().get("maxValue").getTextValue() : "");
        model.put("showTime", showTime);
        model.put("visitUrl", visitUrl);
        model.put("encounterObs", sortedEncObs);
    }

    private String getConfigValue(AppDescriptor app, String configValue) {
        JsonNode node = app.getConfig().get(configValue);
        if (node == null) {
            return "";
        }
        return node.asText();
    }
}
