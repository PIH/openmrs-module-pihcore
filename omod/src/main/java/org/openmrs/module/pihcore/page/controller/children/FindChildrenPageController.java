package org.openmrs.module.pihcore.page.controller.children;

import org.apache.commons.lang3.StringUtils;
import org.openmrs.Patient;
import org.openmrs.PersonAttribute;
import org.openmrs.PersonAttributeType;
import org.openmrs.RelationshipType;
import org.openmrs.api.PersonService;
import org.openmrs.module.appui.UiSessionContext;
import org.openmrs.module.coreapps.CoreAppsProperties;
import org.openmrs.module.pihcore.PihEmrConfigConstants;
import org.openmrs.module.pihcore.metadata.Metadata;
import org.openmrs.module.reporting.common.ObjectUtil;
import org.openmrs.ui.framework.SimpleObject;
import org.openmrs.ui.framework.UiUtils;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.openmrs.ui.framework.page.PageModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestParam;

public class FindChildrenPageController {

    private final Logger log = LoggerFactory.getLogger(getClass());
    public void controller(PageModel model, UiUtils ui, UiSessionContext uiSessionContext,
                           @RequestParam("patientId") Patient patient,
                           @RequestParam(value="returnUrl", required=false) String returnUrl,
                           @SpringBean("personService") PersonService personService,
                           @SpringBean("coreAppsProperties") CoreAppsProperties coreAppsProperties
    ) {

        if (StringUtils.isBlank(returnUrl)) {
            returnUrl = ui.pageLink("pihcore", "children/children", ObjectUtil.toMap("patientId", patient.getUuid()));
        }
        RelationshipType motherToChildRelationshipType = personService.getRelationshipTypeByUuid(PihEmrConfigConstants.RELATIONSHIPTYPE_MOTHERTOCHILD_UUID);

        model.addAttribute("patient", patient);
        model.addAttribute("motherToChildRelationshipType", motherToChildRelationshipType);
        String phoneNumber = null;
        PersonAttributeType phoneNumberAttributeType = personService.getPersonAttributeTypeByUuid(Metadata.getPhoneNumberAttributeType().getUuid());
        if (phoneNumberAttributeType != null ) {
            PersonAttribute attribute = patient.getAttribute(phoneNumberAttributeType);
            if ( attribute != null) {
                phoneNumber = attribute.getValue();
            }
        }
        String patientInfo = patient.getPatientIdentifier().getIdentifier() + " - "
                + patient.getFamilyName() + ", "
                + patient.getGivenName() + ", "
                + patient.getGender() + ", "
                + patient.getBirthdate().toString();
        // pass the mother's info to the baby registration form as a list of property-value in the following format:
        // SECTION.QUESTION.FIELD , this matches the existing format of the registration form
        SimpleObject initialValues = SimpleObject.create(
                "demographics.mothersFirstNameLabel.mothersFirstName", patient.getGivenName(),
                "contactInfo.personAddressQuestion.country", patient.getPersonAddress().getCountry(),
                "contactInfo.phoneNumberLabel.phoneNumber", phoneNumber,
                "registerRelationships.relationships_mother.relationship_type", motherToChildRelationshipType.getUuid() + "-A",
                "registerRelationships.relationships_mother.other_person_uuid", patient.getUuid(),
                "registerRelationships.relationships_mother.other_person_name", patientInfo,
                "registerRelationships.relationships_mother.mother-field", patientInfo
                );
        model.addAttribute("initialRegistrationValues", ui.toJson(initialValues));
        model.addAttribute("returnUrl", returnUrl);
    }
}
