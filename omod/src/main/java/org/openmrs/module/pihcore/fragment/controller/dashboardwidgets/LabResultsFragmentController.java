package org.openmrs.module.pihcore.fragment.controller.dashboardwidgets;

import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.JsonNode;
import org.openmrs.Concept;
import org.openmrs.ConceptNumeric;
import org.openmrs.Encounter;
import org.openmrs.EncounterType;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.api.ConceptService;
import org.openmrs.api.EncounterService;
import org.openmrs.api.PatientService;
import org.openmrs.module.appframework.domain.AppDescriptor;
import org.openmrs.module.emrapi.patient.PatientDomainWrapper;
import org.openmrs.parameter.EncounterSearchCriteriaBuilder;
import org.openmrs.ui.framework.UiUtils;
import org.openmrs.ui.framework.annotation.FragmentParam;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.openmrs.ui.framework.fragment.FragmentConfiguration;
import org.openmrs.ui.framework.fragment.FragmentModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class LabResultsFragmentController {

    private final Logger log = LoggerFactory.getLogger(getClass());

    public void controller(@SpringBean("patientService") PatientService patientService,
                           @SpringBean("encounterService") EncounterService encounterService,
                           @SpringBean("conceptService") ConceptService conceptService,
                           @FragmentParam("app") AppDescriptor app,
                           UiUtils ui,
                           FragmentConfiguration config,
                           FragmentModel model) {

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

        String encTypes = getConfigValue(app, "encounterTypes");
        Set<EncounterType> encounterTypes = new HashSet<>();
        if (StringUtils.isNotBlank(encTypes)) {
            List<String> encTypesList = Arrays.asList(encTypes.split(",", -1));
            for (String type : encTypesList) {
                EncounterType encounterTypeByUuid = encounterService.getEncounterTypeByUuid(type);
                encounterTypes.add(encounterTypeByUuid);
            }
        }

        EncounterSearchCriteriaBuilder builder = new EncounterSearchCriteriaBuilder();
        builder.setPatient(patient);
        builder.setEncounterTypes(encounterTypes);
        List<Encounter> encounters = encounterService.getEncounters(builder.createEncounterSearchCriteria());
        // sort by encounter datetime descending
        encounters.sort((e1, e2) -> e2.getEncounterDatetime().compareTo(e1.getEncounterDatetime()));

        String maxConfig = getConfigValue(app, "maxToDisplay");
        Integer maxToDisplay = null;
        if (StringUtils.isNotBlank(maxConfig)) {
            maxToDisplay = Integer.parseInt(maxConfig);
            if (maxToDisplay < encounters.size()) {
                encounters = encounters.subList(0, maxToDisplay);
            }
        }

        String obsTypes = getConfigValue(app, "obsTypes");
        List<String> obsTypesList = null;
        if (StringUtils.isNotBlank(obsTypes)){
            obsTypesList = Arrays.asList(obsTypes.split(",", -1));
        }
        String labCategoriesSet = getConfigValue(app, "labCategoriesSet");
        Set<String> categoriesList = getLabCategoriesList(labCategoriesSet, conceptService);
        Map<String, String> conceptUnits = new HashMap<String, String>();

        List<Obs> labResults = new ArrayList<>();
        for (Encounter encounter : encounters) {
            Set<Obs> obs = encounter.getObs();
            if (obs !=null && obs.size() > 0) {
                List<Obs> obsList = new ArrayList<>(obs);
                // sort by obs datetime descending
                obsList.sort((ob1, ob2) -> ob2.getObsDatetime().compareTo(ob1.getObsDatetime()));
                for (Obs ob : obsList) {
                    if (labResults.size() > maxToDisplay) {
                        break;
                    }
                    String className = ob.getConcept().getConceptClass().getName();
                    if (obsTypesList != null && obsTypesList.size() > 0 ) {
                        for (String obsType : obsTypesList) {
                            // if the obs is of the type Test or LabTest and it is also in the labCategoriesSet
                            if (className.equalsIgnoreCase(obsType) && categoriesList.contains(ob.getConcept().getUuid())) {
                                labResults.add(ob);
                                if (ob.getConcept().getDatatype().isNumeric()) {
                                    ConceptNumeric conceptNumeric = conceptService.getConceptNumericByUuid(ob.getConcept().getUuid());
                                    if (conceptNumeric != null && conceptNumeric.getUnits() != null) {
                                        conceptUnits.put(conceptNumeric.getUuid(), conceptNumeric.getUnits());
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        String detailsUrl = getConfigValue(app, "detailsUrl");
        model.put("detailsUrl", detailsUrl);
        model.put("labResults", labResults);
        model.put("conceptUnits", conceptUnits);
    }

    private Set<String> getLabCategoriesList(String labCategoriesSet, ConceptService conceptService) {
        Set<String> labCategories = new HashSet<>();
        if (StringUtils.isNotBlank(labCategoriesSet)) {
            Concept conceptSet = conceptService.getConceptByUuid(labCategoriesSet);
            if (conceptSet != null && conceptSet.getSetMembers().size() > 0) {
                for (Concept member : conceptSet.getSetMembers() ) {
                    labCategories.addAll(getLabCategoriesList(member.getUuid(), conceptService));
                }
            }
            labCategories.add(conceptSet.getUuid());
        }
        return labCategories;
    }

    private String getConfigValue(AppDescriptor app, String configValue) {
        JsonNode node = app.getConfig().get(configValue);
        if (node == null) {
            return "";
        }
        return node.asText();
    }

}
