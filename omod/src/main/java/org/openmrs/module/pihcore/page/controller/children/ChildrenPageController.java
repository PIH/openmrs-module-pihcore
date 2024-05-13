package org.openmrs.module.pihcore.page.controller.children;

import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.openmrs.Patient;
import org.openmrs.Person;
import org.openmrs.Relationship;
import org.openmrs.RelationshipType;
import org.openmrs.api.PersonService;
import org.openmrs.module.appui.UiSessionContext;
import org.openmrs.module.coreapps.CoreAppsProperties;
import org.openmrs.module.pihcore.PihEmrConfigConstants;
import org.openmrs.module.reporting.common.ObjectUtil;
import org.openmrs.ui.framework.UiUtils;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.openmrs.ui.framework.page.PageModel;
import org.springframework.web.bind.annotation.RequestParam;

public class ChildrenPageController {

    public void controller(PageModel model, UiUtils ui, UiSessionContext uiSessionContext,
                           @RequestParam("patientId") Patient patient,
                           @RequestParam(value="returnUrl", required=false) String returnUrl,
                           @SpringBean("personService") PersonService personService,
                           @SpringBean("coreAppsProperties") CoreAppsProperties coreAppsProperties
                           ) {


        if (StringUtils.isBlank(returnUrl)) {
            returnUrl = ui.pageLink("coreapps", "clinicianfacing/patient", ObjectUtil.toMap("patientId", patient.getUuid()));
        }
        RelationshipType motherToChildRelationshipType = personService.getRelationshipTypeByUuid(PihEmrConfigConstants.RELATIONSHIPTYPE_MOTHERTOCHILD_UUID);
        List<Relationship> relationships = personService.getRelationships(patient.getPerson(), null, motherToChildRelationshipType);
        List<Person> children = new ArrayList<>();
        for (Relationship relationship : relationships) {
            children.add(relationship.getPersonB());
        }
        model.addAttribute("patient", patient);
        model.addAttribute("children", children);
        model.addAttribute("dashboardUrl", coreAppsProperties.getDashboardUrl());
        model.addAttribute("returnUrl", returnUrl);
    }
}
