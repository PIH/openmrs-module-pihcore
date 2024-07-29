package org.openmrs.module.pihcore.htmlformentry.action;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Concept;
import org.openmrs.Encounter;
import org.openmrs.Location;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.PersonAddress;
import org.openmrs.PersonAttribute;
import org.openmrs.PersonAttributeType;
import org.openmrs.PersonName;
import org.openmrs.Relationship;
import org.openmrs.RelationshipType;
import org.openmrs.api.PersonService;
import org.openmrs.api.context.Context;
import org.openmrs.layout.address.AddressTemplate;
import org.openmrs.module.htmlformentry.CustomFormSubmissionAction;
import org.openmrs.module.htmlformentry.FormEntryContext;
import org.openmrs.module.htmlformentry.FormEntrySession;
import org.openmrs.module.pihcore.PihEmrConfigConstants;
import org.openmrs.module.registrationcore.RegistrationData;
import org.openmrs.module.registrationcore.api.RegistrationCoreService;

public class RegisterBabyAction implements CustomFormSubmissionAction {

    private static String NEWBORN_DETAILS_CONCEPT = "1585";
    private static String YES_CONCEPT = "1065";
    private static String NO_CONCEPT = "1066";
    private static String REGISTER_BABY_CONCEPT = "20150";
    private static String BABY_GENDER_CONCEPT_SOURCE = "CIEL";
    private static String BABY_GENDER_CONCEPT = "1587";
    private static String DATE_TIME_OF_BIRTH_CONCEPT_SOURCE = "PIH";
    private static String DATE_TIME_OF_BIRTH_CONCEPT = "15080";

    protected Log log = LogFactory.getLog(getClass());

    @Override
    public void applyAction(FormEntrySession formEntrySession) {

        if (formEntrySession.getContext().getMode().equals(FormEntryContext.Mode.ENTER)) {
            // we execute this post submission action only in the ENTER mode
            Concept newbornDetailsConcept = Context.getConceptService().getConceptByMapping(NEWBORN_DETAILS_CONCEPT, "CIEL");
            Concept registerBabyConcept = Context.getConceptService().getConceptByMapping(REGISTER_BABY_CONCEPT, "PIH");
            Concept yesConcept = Context.getConceptService().getConceptByMapping(YES_CONCEPT, "CIEL");
            Concept noConcept = Context.getConceptService().getConceptByMapping(NO_CONCEPT, "CIEL");

            Patient patient = formEntrySession.getPatient();
            Encounter encounter = formEntrySession.getEncounter();
            boolean updateEncounter = false;
            for (Obs candidate : encounter.getObsAtTopLevel(false)) {
                if (candidate.getConcept().equals(newbornDetailsConcept)) {
                    Set<Obs> groupMembers = candidate.getGroupMembers();
                    if ( groupMembers != null ) {
                        for (Obs groupMember : groupMembers) {
                            // search for Labour and Delivery obs that have the "Register patient in EMR" obs set to No (SL-617)
                            if (groupMember.getConcept().equals(registerBabyConcept) && groupMember.getValueCoded().equals(noConcept)) {
                                //find the gender and the birthdate time of the baby
                                String gender = getObsValue(groupMembers, BABY_GENDER_CONCEPT_SOURCE, BABY_GENDER_CONCEPT);
                                Date birthDatetime = getObsDateValue(groupMembers, DATE_TIME_OF_BIRTH_CONCEPT_SOURCE, DATE_TIME_OF_BIRTH_CONCEPT);
                                if (StringUtils.isNotBlank(gender) && (birthDatetime != null)) {
                                    Patient baby = registerBaby(patient, gender, birthDatetime, encounter.getLocation());
                                    if (baby != null && baby.getUuid() != null) {
                                        groupMember.setValueCoded(yesConcept);
                                        groupMember.setComment(baby.getUuid());
                                        updateEncounter = true;
                                    }
                                }
                            }
                        }
                    }
                }
            }
            if (updateEncounter) {
                Context.getEncounterService().saveEncounter(encounter);
            }

            //automatically redirect to the children page
            formEntrySession.setAfterSaveUrlTemplate("pihcore/children/children.page?patientId={{patient.id}}");
        }
    }

    Patient registerBaby(Patient mother, String gender, Date birthDatetime, Location location) {
        PersonService personService = Context.getPersonService();
        RelationshipType motherToChildRelationshipType = personService.getRelationshipTypeByUuid(PihEmrConfigConstants.RELATIONSHIPTYPE_MOTHERTOCHILD_UUID);
        RelationshipType relationship = personService.getRelationshipTypeByUuid(motherToChildRelationshipType.getUuid());
        List<Relationship> relationships = new ArrayList<Relationship>();
        if (relationship !=null) {
            relationships.add(new Relationship(mother, null, motherToChildRelationshipType));
        }
        Patient baby = new Patient();
        PersonName babyName = new PersonName();
        babyName.setFamilyName(mother.getFamilyName());
        babyName.setGivenName("Baby");
        baby.addName(babyName);
        String babyGender = gender;
        if (StringUtils.equalsIgnoreCase(gender, "Female")) {
            babyGender = "F";
        } else if (StringUtils.equalsIgnoreCase(gender,"Male")){
            babyGender = "M";
        }
        baby.setGender(babyGender);
        baby.setBirthdate(birthDatetime);
        PersonAddress motherAddress = mother.getPersonAddress();
        if ( motherAddress != null ) {
            PersonAddress babyAddress =  new PersonAddress();
            String xml = Context.getLocationService().getAddressTemplate();
            if ( StringUtils.isNotBlank(xml)) {
                Map<String, String> addressFields = new HashMap<>();
                try {
                    AddressTemplate addressTemplate = Context.getSerializationService().getDefaultSerializer().deserialize(xml, AddressTemplate.class);
                    addressFields = addressTemplate.getNameMappings();
                } catch (Exception e) {
                    log.error("Address Template.error:", e);
                }
                if (!addressFields.isEmpty()) {
                    for (String fieldName : addressFields.keySet()) {
                        Object value = null;
                        try {
                            String fieldValue = (String) PropertyUtils.getProperty(motherAddress, fieldName);
                            if (StringUtils.isNotBlank(fieldValue)) {
                                PropertyUtils.setProperty(babyAddress, fieldName, fieldValue);
                            }
                        } catch (Exception e) {
                            log.error("Address field error:", e);
                        }
                    }
                }
                baby.addAddress(babyAddress);
            }
        }
        PersonAttributeType motherFirstName = personService.getPersonAttributeTypeByUuid(PihEmrConfigConstants.PERSONATTRIBUTETYPE_MOTHERS_FIRST_NAME_UUID);
        if (motherFirstName != null) {
            baby.addAttribute(new PersonAttribute(motherFirstName, mother.getGivenName()));
        }
        PersonAttributeType phoneNumber = personService.getPersonAttributeTypeByUuid(PihEmrConfigConstants.PERSONATTRIBUTETYPE_TELEPHONE_NUMBER_UUID);
        if (phoneNumber != null) {
            PersonAttribute motherPhoneNumber = mother.getAttribute(phoneNumber);
            if (motherPhoneNumber != null) {
                baby.addAttribute(new PersonAttribute(phoneNumber, motherPhoneNumber.getValue()));
            }
        }
        RegistrationData registrationData = new RegistrationData();
        registrationData.setPatient(baby);
        registrationData.setIdentifierLocation(location);
        if (!relationships.isEmpty()) {
            registrationData.setRelationships(relationships);
        }
        try {
            baby = Context.getService(RegistrationCoreService.class).registerPatient(registrationData);
        } catch (Exception ex) {
            throw new RuntimeException("Falied to register baby", ex);
        }
        return baby;
    }
    String getObsValue(Set<Obs> groupMembers, String conceptSource, String conceptId) {
        String obsValue= null;
        Concept obsConcept = Context.getConceptService().getConceptByMapping(conceptId, conceptSource);
        if (obsConcept != null ) {
            for (Obs groupMember : groupMembers) {
                if (groupMember.getConcept().equals(obsConcept)) {
                    obsValue = groupMember.getValueAsString(Context.getLocale());
                    break;
                }
            }
        }
        return obsValue;
    }

    Date getObsDateValue(Set<Obs> groupMembers, String conceptSource, String conceptId) {
        Date obsValue= null;
        Concept obsConcept = Context.getConceptService().getConceptByMapping(conceptId, conceptSource);
        if (obsConcept != null ) {
            for (Obs groupMember : groupMembers) {
                if (groupMember.getConcept().equals(obsConcept)) {
                    obsValue = groupMember.getValueDate();
                    break;
                }
            }
        }
        return obsValue;
    }
}
