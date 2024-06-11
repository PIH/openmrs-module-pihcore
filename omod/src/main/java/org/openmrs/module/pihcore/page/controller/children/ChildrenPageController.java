package org.openmrs.module.pihcore.page.controller.children;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.commons.lang3.StringUtils;
import org.openmrs.Concept;
import org.openmrs.Encounter;
import org.openmrs.EncounterType;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.Person;
import org.openmrs.Relationship;
import org.openmrs.RelationshipType;
import org.openmrs.api.ConceptService;
import org.openmrs.api.EncounterService;
import org.openmrs.api.PersonService;
import org.openmrs.module.appui.UiSessionContext;
import org.openmrs.module.coreapps.CoreAppsProperties;
import org.openmrs.module.pihcore.PihEmrConfigConstants;
import org.openmrs.module.pihcore.SierraLeoneConfigConstants;
import org.openmrs.module.reporting.common.ObjectUtil;
import org.openmrs.parameter.EncounterSearchCriteriaBuilder;
import org.openmrs.ui.framework.UiUtils;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.openmrs.ui.framework.page.PageModel;
import org.springframework.web.bind.annotation.RequestParam;

public class ChildrenPageController {

    private static String NEWBORN_DETAILS_CONCEPT = "1585";
    private static String NO_CONCEPT = "1066";
    private static String REGISTER_BABY_CONCEPT = "20150";
    public void controller(PageModel model, UiUtils ui, UiSessionContext uiSessionContext,
                           @RequestParam("patientId") Patient patient,
                           @RequestParam(value="returnUrl", required=false) String returnUrl,
                           @SpringBean("personService") PersonService personService,
                           @SpringBean("encounterService") EncounterService encounterService,
                           @SpringBean("conceptService") ConceptService conceptService,
                           @SpringBean("coreAppsProperties") CoreAppsProperties coreAppsProperties
                           ) {


        if (StringUtils.isBlank(returnUrl)) {
            returnUrl = ui.pageLink("coreapps", "clinicianfacing/patient", ObjectUtil.toMap("patientId", patient.getUuid()));
        }
        RelationshipType motherToChildRelationshipType = personService.getRelationshipTypeByUuid(PihEmrConfigConstants.RELATIONSHIPTYPE_MOTHERTOCHILD_UUID);
        List<Relationship> relationships = personService.getRelationships(patient.getPerson(), null, motherToChildRelationshipType);
        Map<String, Person> children= new HashMap<String, Person>();
        for (Relationship relationship : relationships) {
            children.put(relationship.getUuid(), relationship.getPersonB());
        }
        model.addAttribute("patient", patient);
        model.addAttribute("children", children);
        model.addAttribute("dashboardUrl", coreAppsProperties.getDashboardUrl());
        model.addAttribute("returnUrl", returnUrl);

        Set<EncounterType> encTypes = new HashSet<>();
        encTypes.add(encounterService.getEncounterTypeByUuid(SierraLeoneConfigConstants.ENCOUNTERTYPE_SIERRALEONELABORDELIVERYSUMMARY_UUID));
        EncounterSearchCriteriaBuilder searchCriteriaBuilder = new EncounterSearchCriteriaBuilder();
        searchCriteriaBuilder.setPatient(patient);
        searchCriteriaBuilder.setEncounterTypes(encTypes);
        List<Encounter> encounters = encounterService.getEncounters(searchCriteriaBuilder.createEncounterSearchCriteria());
        Concept newbornDetailsConcept = conceptService.getConceptByMapping(NEWBORN_DETAILS_CONCEPT, "CIEL");
        Concept registerBabyConcept = conceptService.getConceptByMapping(REGISTER_BABY_CONCEPT, "PIH");
        Concept noConcept = conceptService.getConceptByMapping(NO_CONCEPT, "CIEL");
        List<Encounter> deliveryEncounters= new ArrayList<>();
        for (Encounter encounter : encounters) {
            for (Obs candidate : encounter.getObsAtTopLevel(false)){
                if (candidate.getConcept().equals(newbornDetailsConcept)) {
                    Set<Obs> groupMembers = candidate.getGroupMembers();
                    if ( groupMembers != null ) {
                        for (Obs groupMember : groupMembers) {
                            if(groupMember.getConcept().equals(registerBabyConcept) && groupMember.getValueCoded().equals(noConcept)) {
                                // include only the Labour and Delivery encounters that have the "Register patient in EMR" obs set to No (SL-617)
                                deliveryEncounters.add(encounter);
                            }
                        }
                    }
                }
            }
        }
        model.addAttribute("deliveryEncounters", deliveryEncounters);
    }
}
