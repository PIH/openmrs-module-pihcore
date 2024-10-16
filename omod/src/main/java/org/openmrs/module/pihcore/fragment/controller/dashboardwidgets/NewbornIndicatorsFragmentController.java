package org.openmrs.module.pihcore.fragment.controller.dashboardwidgets;

import org.openmrs.Concept;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.Relationship;
import org.openmrs.RelationshipType;
import org.openmrs.api.ConceptService;
import org.openmrs.api.PatientService;
import org.openmrs.api.PersonService;
import org.openmrs.api.ProgramWorkflowService;
import org.openmrs.api.context.Context;
import org.openmrs.module.appframework.domain.AppDescriptor;
import org.openmrs.module.appui.UiSessionContext;
import org.openmrs.module.emrapi.EmrApiProperties;
import org.openmrs.module.emrapi.adt.AdtService;
import org.openmrs.module.emrapi.patient.PatientDomainWrapper;
import org.openmrs.module.emrapi.visit.VisitDomainWrapper;
import org.openmrs.module.pihcore.PihCoreUtils;
import org.openmrs.module.pihcore.PihEmrConfigConstants;
import org.openmrs.ui.framework.SimpleObject;
import org.openmrs.ui.framework.UiUtils;
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

public class NewbornIndicatorsFragmentController {
    private final Logger log = LoggerFactory.getLogger(getClass());

    public void controller(
            @SpringBean("conceptService") ConceptService conceptService,
            @SpringBean("personService") PersonService personService,
            @SpringBean("patientService") PatientService patientService,
            @SpringBean("adtService") AdtService adtService,
            @SpringBean EmrApiProperties emrApiProperties,
            @SpringBean("programWorkflowService") ProgramWorkflowService programWorkflowService,
            @FragmentParam("app") AppDescriptor app,
            UiSessionContext uiSessionContext,
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

        String programUuid = (String) config.get("duringCurrentEnrollmentInProgram");
        Map<String, SimpleObject> fields = new LinkedHashMap<>();
        fields.put(PihEmrConfigConstants.CONCEPT_DELIVERYDATEANDTIME_UUID, SimpleObject.create(
                "label", "pihcore.mch.deliveryDateTime",
                "css", "red"
        ));
        fields.put(PihEmrConfigConstants.CONCEPT_TYPEOFDELIVERY_UUID, SimpleObject.create(
                "label", "pihcore.mch.deliveryType"
        ));
        Obs multipleBirth = null;
        Concept multipleBirthConcept = conceptService.getConceptByUuid(PihEmrConfigConstants.CONCEPT_MULTIPLEBIRTH_UUID);
        if (multipleBirthConcept != null ) {
            Set<Concept> answers = new HashSet<>();
            answers.add(multipleBirthConcept);
            List<Obs> multipleBirthObsList = PihCoreUtils.getObsWithinProgram(patient, null, answers, programUuid);
            if (multipleBirthObsList != null && multipleBirthObsList.size() > 0) {
                multipleBirth = multipleBirthObsList.get(0);
            }
        }
        fields.put("multipleBirth", SimpleObject.create(
        "label", "pihcore.multiple.birth",
                "obs", multipleBirth != null ? ui.message("general.true") : ui.message("general.false")
        ));
        fields.put(PihEmrConfigConstants.CONCEPT_GESTATIONALAGE_UUID, SimpleObject.create(
                "label", "pihcore.newborn.gestational.age",
                "css", "red"
        ));
        fields.put(PihEmrConfigConstants.CONCEPT_BIRTHWEIGHT_UUID, SimpleObject.create(
                "label", "pihcore.birthweight"
        ));
        fields.put(PihEmrConfigConstants.CONCEPT_BABYNUMBER_UUID, SimpleObject.create(
                "label", "pihcore.baby.number"
        ));
        Set<Concept> concepts = new HashSet<>();
        for (String conceptRef : fields.keySet()) {
            if ( conceptRef == "multipleBirth") {
                // this is a special case that we need to skip over
                continue;
            }
            Concept concept = conceptService.getConceptByReference(conceptRef);
            if (concept == null) {
                throw new IllegalArgumentException("No concept with this ref: " + conceptRef + " found. Please pass correct concept ref into the configuration");
            }
            concepts.add(concept);
        }
        List<Obs> obsList = PihCoreUtils.getObsWithinProgram(patient, concepts, null, programUuid);

        for (Obs obs : obsList) {
            obs.getValueAsString(Context.getLocale());
            SimpleObject simpleObject = fields.get(obs.getConcept().getUuid());
            if (simpleObject != null ) {
                simpleObject.put("obs", obs);
            }
        }

        RelationshipType motherToChildRelationshipType = personService.getRelationshipTypeByUuid(PihEmrConfigConstants.RELATIONSHIPTYPE_MOTHERTOCHILD_UUID);
        List<Relationship> relationships = personService.getRelationships(null, patient.getPerson(), motherToChildRelationshipType);
        if (relationships != null && relationships.size() == 1) {
            // it should be only one entry on this type of relationship
            Patient mother =  patientService.getPatientByUuid(relationships.get(0).getPersonA().getUuid());
            if (mother != null ){
                VisitDomainWrapper activeVisit = adtService.getActiveVisit(patient, uiSessionContext.getSessionLocation());
                SimpleObject inpatientLocation = PihCoreUtils.getInpatientLocation(patient, activeVisit != null ?  activeVisit.getVisit() : null, adtService, ui);

                fields.put("mother", SimpleObject.create(
                        "label", "pihcore.mother",
                        "name", mother.getGivenName() + " " + mother.getFamilyName(),
                        "primaryIdentifier", mother.getPatientIdentifier(emrApiProperties.getPrimaryIdentifierType()),
                        "inpatientLocation", inpatientLocation.get("currentInpatientLocation"),
                        "queueName", inpatientLocation.get("queueName"),
                        "patientStatus", inpatientLocation.get("patientStatus"),
                        "url", "coreapps/clinicianfacing/patient.page?patientId=" + mother.getUuid()
                ));
            }
        }
        model.put("fields", fields);

    }
}
