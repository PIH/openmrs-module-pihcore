package org.openmrs.module.pihcore.htmlformentry.action;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Concept;
import org.openmrs.Encounter;
import org.openmrs.EncounterProvider;
import org.openmrs.EncounterRole;
import org.openmrs.Location;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.PatientProgram;
import org.openmrs.PersonAddress;
import org.openmrs.PersonAttribute;
import org.openmrs.PersonAttributeType;
import org.openmrs.PersonName;
import org.openmrs.Program;
import org.openmrs.Provider;
import org.openmrs.Relationship;
import org.openmrs.RelationshipType;
import org.openmrs.User;
import org.openmrs.Visit;
import org.openmrs.api.ConceptService;
import org.openmrs.api.EncounterService;
import org.openmrs.api.FormService;
import org.openmrs.api.PersonService;
import org.openmrs.api.VisitService;
import org.openmrs.api.context.Context;
import org.openmrs.module.bedmanagement.BedDetails;
import org.openmrs.module.bedmanagement.service.BedManagementService;
import org.openmrs.module.emrapi.adt.AdtService;
import org.openmrs.module.emrapi.visit.VisitDomainWrapper;
import org.openmrs.module.htmlformentry.CustomFormSubmissionAction;
import org.openmrs.module.htmlformentry.FormEntryContext;
import org.openmrs.module.htmlformentry.FormEntrySession;
import org.openmrs.module.pihcore.PihEmrConfigConstants;
import org.openmrs.module.pihcore.SierraLeoneConfigConstants;
import org.openmrs.module.registrationcore.RegistrationData;
import org.openmrs.module.registrationcore.api.RegistrationCoreService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class RegisterBabyAction implements CustomFormSubmissionAction {

    private static String NEWBORN_DETAILS_CONCEPT = "1585";
    private static String YES_CONCEPT = "1065";
    private static String NO_CONCEPT = "1066";
    private static String REGISTER_BABY_CONCEPT = "20150";
    private static String BABY_GENDER_CONCEPT_SOURCE = "CIEL";
    private static String BABY_GENDER_CONCEPT = "1587";
    private static String DELIVERY_TYPE_CONCEPT = "11663";
    private static String DELIVERY_OUTCOME_CONCEPT = "159917";
    private static String BIRTH_WEIGHT_CONCEPT = "5916";
    private static String BIRTH_HEIGHT_CONCEPT = "163554";
    private static String HEAD_CIRCUMFERENCE_CONCEPT = "163555";
    private static String DATE_TIME_OF_BIRTH_CONCEPT_SOURCE = "PIH";
    private static String DATE_TIME_OF_BIRTH_CONCEPT = "5599";
    private static String APGAR_ONE_MINUTE_CONCEPT = "14419";
    private static String APGAR_FIVE_MINUTES_CONCEPT = "159604";
    private static String APGAR_TEN_MINUTES_CONCEPT = "14785";
    private static String VISIT_DIAGNOSIS_CONCEPT = "159947";
    private static String DIAGNOSIS_CONCEPT = "3064";
    private static String MULTIPLE_BABIES_CONCEPT = "115491";
    private static String BABY_NUMBER_CONCEPT = "163460";
    private static String NEWBORN_ASSESSMENT_FORM_UUID = "17e93a76-32d1-4435-a5e3-3068e305c2d7";

    protected Log log = LogFactory.getLog(getClass());

    @Override
    public void applyAction(FormEntrySession formEntrySession) {

        if (formEntrySession.getContext().getMode().equals(FormEntryContext.Mode.ENTER)) {
            // we execute this post submission action only in the ENTER mode
            Concept newbornDetailsConcept = Context.getConceptService().getConceptByMapping(NEWBORN_DETAILS_CONCEPT, "CIEL");
            Concept registerBabyConcept = Context.getConceptService().getConceptByMapping(REGISTER_BABY_CONCEPT, "PIH");
            Concept yesConcept = Context.getConceptService().getConceptByMapping(YES_CONCEPT, "CIEL");
            Concept noConcept = Context.getConceptService().getConceptByMapping(NO_CONCEPT, "CIEL");

            Patient mother = formEntrySession.getPatient();
            Encounter encounter = formEntrySession.getEncounter(); 
            HashMap<String, Obs> registeredBabies = new LinkedHashMap<>();
            for (Obs candidate : encounter.getObsAtTopLevel(false)) {
                if (candidate.getConcept().equals(newbornDetailsConcept)) {
                    Set<Obs> groupMembers = candidate.getGroupMembers();
                    if ( groupMembers != null ) {
                        for (Obs groupMember : groupMembers) {
                            // search for Labour and Delivery obs that have the "Register patient in EMR" obs set to No (SL-617)
                            if (groupMember.getConcept().equals(registerBabyConcept) && groupMember.getValueCoded().equals(noConcept)) {
                                //find the gender and the birthdate time of the baby
                                Concept gender = getCodedValue(groupMembers, BABY_GENDER_CONCEPT_SOURCE, BABY_GENDER_CONCEPT);
                                Date birthDatetime = getObsDateValue(groupMembers, DATE_TIME_OF_BIRTH_CONCEPT_SOURCE, DATE_TIME_OF_BIRTH_CONCEPT);
                                Double birthOrder = getObsNumericValue(groupMembers, "CIEL", BABY_NUMBER_CONCEPT);
                                int babyNumber = 1;
                                if (birthOrder != null && birthOrder.intValue() > 0) {
                                    babyNumber = birthOrder.intValue();
                                }
                                if ((gender != null) && (birthDatetime != null)) {
                                    Patient baby = registerBaby(mother, gender, birthDatetime, encounter.getLocation(), babyNumber);
                                    if (baby != null && baby.getUuid() != null) {
                                        registeredBabies.put(baby.getUuid(), groupMember);
                                        //create visit for the new registered baby
                                        Visit visit= createVisit(baby, birthDatetime, encounter);
                                        if (visit != null) {
                                            // create Admission encounter for the new registered baby
                                            Encounter babyEncounter = createAdmissionEncounter(visit, baby, birthDatetime, encounter);
                                            if(babyEncounter != null) {
                                                // assign baby to mother's bed, if exists
                                                BedManagementService bedManagementService = Context.getService(BedManagementService.class);
                                                BedDetails motherBed = bedManagementService.getBedAssignmentDetailsByPatient(mother);
                                                if(motherBed != null) {
                                                    bedManagementService.assignPatientToBed(baby, babyEncounter, "" + motherBed.getBedId());
                                                }
                                            }
                                            Encounter newbornAssessmentEncounter = createNewbornAssessmentEncounter(visit, baby, groupMembers, encounter, babyNumber);
                                            if ( newbornAssessmentEncounter != null ) {
                                                enrollInInfancyProgram(baby, birthDatetime, encounter);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            if (!registeredBabies.isEmpty()) {
                for (String babyUuid : registeredBabies.keySet()) {
                    Obs obs = registeredBabies.get(babyUuid);
                    obs.setValueCoded(yesConcept);
                    obs.setComment(babyUuid);
                }
                Context.getEncounterService().saveEncounter(encounter);
            }

            //automatically redirect to the children page
            formEntrySession.setAfterSaveUrlTemplate("pihcore/children/children.page?patientId={{patient.id}}");
        }
    }

    Patient registerBaby(Patient mother, Concept gender, Date birthDatetime, Location location, int babyNumber) {
        Concept maleConcept = Context.getConceptService().getConceptByMapping("MALE", "PIH");
        Concept femaleConcept = Context.getConceptService().getConceptByMapping("FEMALE", "PIH");
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
        // name babies after the first one as "Infant 2", "Infant 3", etc...
        if(babyNumber == 1) {
            babyName.setGivenName("Infant");
        } else {
            babyName.setGivenName("Infant " + babyNumber );
        }
        baby.addName(babyName);
        String babyGender = "M";
        if (gender.equals(femaleConcept)) {
            babyGender = "F";
        } else if (gender.equals(maleConcept)){
            babyGender = "M";
        }
        baby.setGender(babyGender);
        baby.setBirthdate(birthDatetime);
        PersonAddress motherAddress = mother.getPersonAddress();
        if ( motherAddress != null ) {
            PersonAddress babyAddress =  (PersonAddress) motherAddress.clone();
            babyAddress.setPersonAddressId(null);
            babyAddress.setVoided(false);
            babyAddress.setVoidedBy(null);
            babyAddress.setVoidReason(null);
            babyAddress.setPreferred(true);
            babyAddress.setUuid(UUID.randomUUID().toString());
            baby.addAddress(babyAddress);
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
            createRegisterEncounter(baby, birthDatetime, location);
        } catch (Exception ex) {
            throw new RuntimeException("Failed to register baby", ex);
        }
        return baby;
    }

    Encounter createRegisterEncounter(Patient patient, Date registrationDate, Location location) {

        EncounterService encounterService = Context.getEncounterService();
        Encounter registrationEncounter = new Encounter();
        registrationEncounter.setPatient(patient);
        registrationEncounter.setEncounterType(encounterService.getEncounterTypeByUuid(PihEmrConfigConstants.ENCOUNTERTYPE_PATIENT_REGISTRATION_UUID));
        registrationEncounter.setLocation(location);
        registrationEncounter.setEncounterDatetime(registrationDate);
        EncounterRole encounterRole = encounterService.getEncounterRoleByUuid(PihEmrConfigConstants.ENCOUNTERROLE_ADMINISTRATIVECLERK_UUID);
        if ( encounterRole != null ) {
            User user = Context.getAuthenticatedUser();
            Collection<Provider> providersByPerson = Context.getProviderService().getProvidersByPerson(user.getPerson());
            if (providersByPerson != null && !providersByPerson.isEmpty()) {
                registrationEncounter.addProvider(encounterRole, providersByPerson.iterator().next());
            }
        }
        encounterService.saveEncounter(registrationEncounter);
        return registrationEncounter;
    }

    Visit createVisit(Patient baby, Date startDatetime, Encounter motherEncounter) {
        Visit visit = null;
        Visit motherVisit =  motherEncounter.getVisit();
        if (motherVisit.getStopDatetime() == null ) {
            // we create a visit for the baby only if the mother has an active visit, (we are not creating retrospective visits, see SL-701)
            AdtService adtService = Context.getService(AdtService.class);
            VisitService visitService = Context.getVisitService();
            visit = new Visit();
            visit.setPatient(baby);
            visit.setLocation(adtService.getLocationThatSupportsVisits(motherEncounter.getLocation()));
            visit.setStartDatetime(startDatetime);
            visit.setVisitType(visitService.getVisitTypeByUuid(PihEmrConfigConstants.VISITTYPE_CLINIC_OR_HOSPITAL_VISIT_UUID));
            visitService.saveVisit(visit);
        }
        return visit;
    }

    Encounter createNewbornAssessmentEncounter(Visit visit, Patient patient, Set<Obs> newbornObs, Encounter motherEncounter, int babyNumber) {
        ConceptService conceptService = Context.getConceptService();
        Location motherEncounterLocation = motherEncounter.getLocation();
        Concept gender = getCodedValue(newbornObs, BABY_GENDER_CONCEPT_SOURCE, BABY_GENDER_CONCEPT);
        Concept deliveryType = getCodedValue(newbornObs, "PIH", DELIVERY_TYPE_CONCEPT);
        Concept outcome = getCodedValue(newbornObs, "CIEL", DELIVERY_OUTCOME_CONCEPT);
        Double weight = getObsNumericValue(newbornObs, "CIEL", BIRTH_WEIGHT_CONCEPT);
        Double height = getObsNumericValue(newbornObs, "CIEL", BIRTH_HEIGHT_CONCEPT);
        Double headCircumference = getObsNumericValue(newbornObs, "CIEL", HEAD_CIRCUMFERENCE_CONCEPT);
        Date birthDatetime = getObsDateValue(newbornObs, DATE_TIME_OF_BIRTH_CONCEPT_SOURCE, DATE_TIME_OF_BIRTH_CONCEPT);
        Double apgarScoreOneMinute = getObsNumericValue(newbornObs, "PIH", APGAR_ONE_MINUTE_CONCEPT);
        Double apgarScoreFiveMinutes = getObsNumericValue(newbornObs, "CIEL", APGAR_FIVE_MINUTES_CONCEPT);
        Double apgarScoreTenMinutes = getObsNumericValue(newbornObs, "PIH", APGAR_TEN_MINUTES_CONCEPT);

        Concept diagnosisConcept = getCodedValue(motherEncounter.getAllFlattenedObs(false), "PIH", DIAGNOSIS_CONCEPT);
        Concept multipleBabiesConcept = conceptService.getConceptByMapping(MULTIPLE_BABIES_CONCEPT, "CIEL");

        Encounter newbornAssessmentEncounter = new Encounter();
        EncounterService encounterService = Context.getEncounterService();
        newbornAssessmentEncounter.setPatient(patient);
        newbornAssessmentEncounter.setEncounterType(encounterService.getEncounterTypeByUuid(PihEmrConfigConstants.ENCOUNTERTYPE_NEWBORN_ASSESSMENT_UUID));
        newbornAssessmentEncounter.setEncounterDatetime(birthDatetime);
        newbornAssessmentEncounter.setForm(Context.getService(FormService.class).getFormByUuid(NEWBORN_ASSESSMENT_FORM_UUID));
        newbornAssessmentEncounter.setVisit(visit);
        newbornAssessmentEncounter.setLocation(motherEncounterLocation);
        Set<EncounterProvider> providers =  motherEncounter.getEncounterProviders();
        if (providers != null && !providers.isEmpty()) {
            for (EncounterProvider provider : providers) {
                newbornAssessmentEncounter.addProvider(provider.getEncounterRole(), provider.getProvider());
            }
        }
        if (diagnosisConcept != null && multipleBabiesConcept != null && diagnosisConcept.equals(multipleBabiesConcept)) {
            //multiple babies
            Obs visitDiagnosisObs = new Obs(patient, conceptService.getConceptByMapping(VISIT_DIAGNOSIS_CONCEPT, "CIEL"), birthDatetime, motherEncounterLocation);
            Obs diagnosisObs = new Obs(patient, conceptService.getConceptByMapping(DIAGNOSIS_CONCEPT, "PIH"), birthDatetime, motherEncounterLocation);
            diagnosisObs.setValueCoded(multipleBabiesConcept);
            visitDiagnosisObs.addGroupMember(diagnosisObs);
            newbornAssessmentEncounter.addObs(visitDiagnosisObs);
        }
        if (outcome != null) {
            Obs outcomeObservation = new Obs(patient, conceptService.getConceptByMapping(DELIVERY_OUTCOME_CONCEPT, "CIEL"), birthDatetime, motherEncounterLocation);
            outcomeObservation.setValueCoded(outcome);
            newbornAssessmentEncounter.addObs(outcomeObservation);
        }
        if (deliveryType != null) {
            Obs deliveryTypeObservation = new Obs(patient, conceptService.getConceptByMapping(DELIVERY_TYPE_CONCEPT, "PIH"), birthDatetime, motherEncounterLocation);
            deliveryTypeObservation.setValueCoded(deliveryType);
            newbornAssessmentEncounter.addObs(deliveryTypeObservation);
        }
        if (gender != null) {
            Obs genderObs = new Obs(patient, conceptService.getConceptByMapping(BABY_GENDER_CONCEPT, "CIEL"), birthDatetime, motherEncounterLocation);
            genderObs.setValueCoded(gender);
            newbornAssessmentEncounter.addObs(genderObs);
        }
        if (birthDatetime != null) {
            Obs birthTimeObs = new Obs(patient, conceptService.getConceptByMapping(DATE_TIME_OF_BIRTH_CONCEPT, DATE_TIME_OF_BIRTH_CONCEPT_SOURCE), birthDatetime, motherEncounterLocation);
            birthTimeObs.setValueDatetime(birthDatetime);
            newbornAssessmentEncounter.addObs(birthTimeObs);
        }
        if (weight != null) {
            Obs weightObs = new Obs(patient, conceptService.getConceptByMapping(BIRTH_WEIGHT_CONCEPT, "CIEL"), birthDatetime, motherEncounterLocation);
            weightObs.setValueNumeric(weight);
            newbornAssessmentEncounter.addObs(weightObs);
        }
        if (height != null) {
            Obs heightObs = new Obs(patient, conceptService.getConceptByMapping(BIRTH_HEIGHT_CONCEPT, "CIEL"), birthDatetime, motherEncounterLocation);
            heightObs.setValueNumeric(height);
            newbornAssessmentEncounter.addObs(heightObs);
        }
        if (headCircumference != null) {
            Obs headCircumferenceObs = new Obs(patient, conceptService.getConceptByMapping(HEAD_CIRCUMFERENCE_CONCEPT, "CIEL"), birthDatetime, motherEncounterLocation);
            headCircumferenceObs.setValueNumeric(headCircumference);
            newbornAssessmentEncounter.addObs(headCircumferenceObs);
        }
        if (apgarScoreOneMinute != null) {
            Obs apgarOneMinObs = new Obs(patient, conceptService.getConceptByMapping(APGAR_ONE_MINUTE_CONCEPT, "PIH"), birthDatetime, motherEncounterLocation);
            apgarOneMinObs.setValueNumeric(apgarScoreOneMinute);
            newbornAssessmentEncounter.addObs(apgarOneMinObs);
        }
        if (apgarScoreFiveMinutes != null) {
            Obs apgarFiveMinsObs = new Obs(patient, conceptService.getConceptByMapping(APGAR_FIVE_MINUTES_CONCEPT, "CIEL"), birthDatetime, motherEncounterLocation);
            apgarFiveMinsObs.setValueNumeric(apgarScoreFiveMinutes);
            newbornAssessmentEncounter.addObs(apgarFiveMinsObs);
        }
        if (apgarScoreTenMinutes != null) {
            Obs apgarTenMinsObs = new Obs(patient, conceptService.getConceptByMapping(APGAR_TEN_MINUTES_CONCEPT, "PIH"), birthDatetime, motherEncounterLocation);
            apgarTenMinsObs.setValueNumeric(apgarScoreTenMinutes);
            newbornAssessmentEncounter.addObs(apgarTenMinsObs);
        }
        if (babyNumber > 0) {
            Obs babyNumberObs = new Obs(patient, conceptService.getConceptByMapping(BABY_NUMBER_CONCEPT, "CIEL"), birthDatetime, motherEncounterLocation);
            babyNumberObs.setValueNumeric(Double.valueOf(babyNumber));
            newbornAssessmentEncounter.addObs(babyNumberObs);
        }
        return encounterService.saveEncounter(newbornAssessmentEncounter);
    }

    PatientProgram enrollInInfancyProgram(Patient patient, Date enrollmentDate, Encounter motherEncounter) {

        PatientProgram newPatientProgram = null;
        Program infantProgram = Context.getProgramWorkflowService().getProgramByUuid(SierraLeoneConfigConstants.PROGRAM_INFANT_UUID);
        if (infantProgram != null) {
            newPatientProgram = new PatientProgram();
            newPatientProgram.setProgram(infantProgram);
            newPatientProgram.setPatient(patient);
            newPatientProgram.setDateEnrolled(enrollmentDate);
            newPatientProgram.setLocation(Context.getService(AdtService.class).getLocationThatSupportsVisits(motherEncounter.getLocation()));
            newPatientProgram = Context.getProgramWorkflowService().savePatientProgram(newPatientProgram);
        }
        return newPatientProgram;
    }

    Encounter createAdmissionEncounter(Visit visit, Patient patient, Date birthDatetime, Encounter motherEncounter) {
        Encounter admissionEncounter = null;
        AdtService adtService = Context.getService(AdtService.class);
        EncounterService encounterService = Context.getEncounterService();
        VisitDomainWrapper wrappedVisit = adtService.wrap(motherEncounter.getVisit());
        Location motherLocation = null;
        try {
            // get the mother's current inpatient location
            motherLocation = wrappedVisit.getInpatientLocation(new Date());
        } catch (IllegalArgumentException e) {
            log.error(e);
        }

        if (motherLocation == null ) {
            log.warn("Mother of patient " + patient.getUuid() +" has no admission encounter, therefore we cannot create admission encounter for the baby");
        } else {
            admissionEncounter = new Encounter();
            admissionEncounter.setPatient(patient);
            admissionEncounter.setEncounterType(encounterService.getEncounterTypeByUuid(PihEmrConfigConstants.ENCOUNTERTYPE_ADMISSION_UUID));
            admissionEncounter.setVisit(visit);
            admissionEncounter.setEncounterDatetime(birthDatetime);
            admissionEncounter.setLocation(motherLocation);
            Set<EncounterProvider> providers =  motherEncounter.getEncounterProviders();
            if (providers != null && !providers.isEmpty()) {
                for (EncounterProvider provider : providers) {
                    admissionEncounter.addProvider(provider.getEncounterRole(), provider.getProvider());
                }
            }
            encounterService.saveEncounter(admissionEncounter);
        }
        return admissionEncounter;
    }

    Concept getCodedValue(Set<Obs> groupMembers, String conceptSource, String conceptId) {
        Concept codedValue= null;
        Concept obsConcept = Context.getConceptService().getConceptByMapping(conceptId, conceptSource);
        if (obsConcept != null ) {
            for (Obs groupMember : groupMembers) {
                if (groupMember.getConcept().equals(obsConcept)) {
                    codedValue = groupMember.getValueCoded();
                    break;
                }
            }
        }
        return codedValue;
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

    Double getObsNumericValue(Set<Obs> groupMembers, String conceptSource, String conceptId) {
        String obsValue= getObsValue(groupMembers, conceptSource, conceptId);
        if (obsValue != null) {
            return Double.valueOf(obsValue);
        }
        return null;
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
