package org.openmrs.module.pihcore.fragment.controller.dashboardwidgets;

import org.apache.commons.lang.StringUtils;
import org.openmrs.Concept;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.api.ConceptService;
import org.openmrs.api.ObsService;
import org.openmrs.api.ProgramWorkflowService;
import org.openmrs.api.context.Context;
import org.openmrs.module.appframework.domain.AppDescriptor;
import org.openmrs.module.emrapi.patient.PatientDomainWrapper;
import org.openmrs.module.pihcore.PihCoreUtils;
import org.openmrs.ui.framework.SimpleObject;
import org.openmrs.ui.framework.annotation.FragmentParam;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.openmrs.ui.framework.fragment.FragmentConfiguration;
import org.openmrs.ui.framework.fragment.FragmentModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * This widget could be used to display discret obs values across many encounters within the same program enrollment.
 * The config parameters are an array of fields, where each field has a:
 *  - label : could indicate the name of the value to be displayed
 *  - concept: points to the Concept of the obs whose value is displayed
 *  - minValue: if specified and the obs.valueNumeric < minValue then the value will be highlighted in red
 *
 *      "fragmentConfig": {
 *             "fields": [
 *               {
 *                 "label" : "pihcore.apgar.tenMinutes",
 *                 "concept": "${concept.apgarAtTenMinutes.uuid}",
 *                 "minValue": "7"
 *               },
 *               {
 *                 "label" : "pihcore.apgar.fiveMinutes",
 *                 "concept": "${concept.apgarAtFiveMinutes.uuid}",
 *                 "minValue": "7"
 *               },
 *               {
 *                 "label" : "pihcore.apgar.oneMinute",
 *                 "concept": "${concept.apgarAtOneMinute.uuid}",
 *                 "minValue": "7"
 *               }
 *             ],
 *             "duringCurrentEnrollmentInProgram": "${program.infant.uuid}"
 *           }
 */
public class DiscreteValuesFragmentController {

    private final Logger log = LoggerFactory.getLogger(getClass());

    public void controller(
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

        List<SimpleObject> fieldsObject = (List<SimpleObject>) config.get("fields");
        if (fieldsObject == null) {
            throw new IllegalStateException("Missing configuration fields on widget");
        }
        Set<Concept> concepts = new HashSet<>();
        Map<String, SimpleObject> fields = new LinkedHashMap<>();
        for (int i = 0; i < fieldsObject.size(); i++ ) {
            try {
                Map<String, String> fieldNode = (LinkedHashMap) fieldsObject.get(i);
                String label = (String) fieldNode.get("label");
                String conceptUuid = (String) fieldNode.get("concept");
                String minValue = (String) fieldNode.get("minValue");
                float floatValue = 0;
                if (StringUtils.isNotBlank(minValue)) {
                    try {
                        floatValue = Float.parseFloat(minValue);
                    } catch (NumberFormatException e) {
                        throw new IllegalArgumentException("minValue config parameter has to be number", e);
                    }
                }
                if (StringUtils.isNotBlank(conceptUuid)) {
                    Concept concept = conceptService.getConceptByReference(conceptUuid);
                    if (concept == null) {
                        throw new IllegalArgumentException("No concept with this ref: " + conceptUuid + " found. Please pass correct concept ref into the configuration");
                    }
                    concepts.add(concept);
                    fields.put(conceptUuid, SimpleObject.create(
                            "label", label,
                            "minValue", floatValue
                    ));
                }
            } catch (Exception e) {
                throw new IllegalStateException("Invalid configuration for encounterType node", e);
            }
        }

        String programUuid = (String) config.get("duringCurrentEnrollmentInProgram");
        List<Obs> obsList = PihCoreUtils.getObsWithinProgram(patient, concepts, null, programUuid);

        for (Obs obs : obsList) {
            obs.getValueAsString(Context.getLocale());
            SimpleObject simpleObject = fields.get(obs.getConcept().getUuid());
            if (simpleObject != null ) {
                simpleObject.put("obs", obs);
            }
        }
        model.put("fields", fields);
    }
}
