package org.openmrs.module.pihcore.printer.template;

import lombok.Setter;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Concept;
import org.openmrs.Encounter;
import org.openmrs.Location;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifier;
import org.openmrs.PatientIdentifierType;
import org.openmrs.PersonAddress;
import org.openmrs.PersonAttributeType;
import org.openmrs.Relationship;
import org.openmrs.RelationshipType;
import org.openmrs.api.ConceptService;
import org.openmrs.api.ObsService;
import org.openmrs.api.PatientService;
import org.openmrs.api.PersonService;
import org.openmrs.api.context.Context;
import org.openmrs.messagesource.MessageSourceService;
import org.openmrs.module.addresshierarchy.AddressHierarchyLevel;
import org.openmrs.module.addresshierarchy.service.AddressHierarchyService;
import org.openmrs.module.addresshierarchy.util.AddressHierarchyUtil;
import org.openmrs.module.emrapi.EmrApiProperties;
import org.openmrs.module.emrapi.adt.AdtService;
import org.openmrs.module.pihcore.PihEmrConfigConstants;
import org.openmrs.module.pihcore.SierraLeoneConfigConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.text.SimpleDateFormat;
import java.util.stream.Collectors;

/**
 * Template for the wristbands we print at MCOE.
 * Created the code in ZPL II language to send to the wristband printer
 */
@Component("slWristbandTemplate")
public class SLWristbandTemplate {

    private final Log log = LogFactory.getLog(getClass());
    public static Boolean LOWEST_LEVEL_ON_SEPARATE_LINE = true;

    public static Boolean SKIP_HIGHEST_LEVEL = true;

    public static final String  NEWBORN_DETAILS_CONCEPT_PIH_CODE = "13555";

    public static final String PATIENT_EXISTS_IN_EMR_CONCEPT_PIH_CODE = "20150";

    public static final String YES_CONCEPT_PIH_CODE = "YES";

    public static final String DATETIME_OF_DELIVERY_CONCEPT_PIH_CODE = "5599";

    public static final String SEX_CONCEPT_PIH_CODE = "13055";

    public static final String FEMALE_CONCEPT_PIH_CODE = "1535";

    public static final String BIRTH_WEIGHT_CONCEPT_PIH_CODE = "11067";

    public static final String BIRTH_ORDER_CONCEPT_PIH_CODE = "13126";

    @Autowired
    @Setter
    private AdtService adtService;

    @Autowired
    @Setter
    private MessageSourceService messageSourceService;

    @Autowired
    @Setter
    private EmrApiProperties emrApiProperties;

    @Autowired
    @Setter
    private PersonService personService;

    @Autowired
    @Setter
    private PatientService patientService;

    @Autowired
    @Setter
    private ObsService obsService;

    @Autowired
    @Setter
    private ConceptService conceptService;

    // TODO figure out why this isn't getting autowired properly (at least for tests)
    //@Autowired
    private AddressHierarchyService addressHierarchyService;

    public void setAddressHierarchyService(AddressHierarchyService addressHierarchyService) {
        this.addressHierarchyService = addressHierarchyService;
    }


    public String generateWristband(Patient patient, Location location, Locale locale) {
        StringBuffer data = new StringBuffer();

        if (addressHierarchyService == null) {
            addressHierarchyService = Context.getService(AddressHierarchyService.class);
        }
        Locale printerLocale = (locale != null) ? locale : new Locale("en"); // default to English

        data.append("^XA");
        data.append("^CI28");   // specify Unicode encoding
        data.append("^MTD");    // set direct transfer type
        data.append("^FWB");    // set orientation

        // visit location & current date
        Location visitLocation = adtService.getLocationThatSupportsVisits((org.openmrs.Location) location);
        String visitLocationDisplay = "";
        if (visitLocation != null) {
            visitLocationDisplay = visitLocation.getUuid().equals(SierraLeoneConfigConstants.LOCATION_KGH_UUID) ? "Koidu Government Hospital" : visitLocation.getName();
        }

        data.append("^FO050,200^FB1650,1,0,L,0^AS^FD" + visitLocationDisplay + " "
                + new SimpleDateFormat("dd-MMM-yyyy", printerLocale).format(new Date()) + "^FS");

        // name and primary identifier
        data.append("^FO100,200^FB1650,1,0,L,0^AT^FD" + generateNameAndPrimaryIdentifier(patient) + "^FS");

        // branch to generate the adult and child-specific components
        if (patient.getAge() == null || patient.getAge() >= 5) {
            data.append(generateAdultComponent(patient, printerLocale));
        } else {
            RelationshipType motherToChildRelationshipType = personService.getRelationshipTypeByUuid(PihEmrConfigConstants.RELATIONSHIPTYPE_MOTHERTOCHILD_UUID);
            List<Relationship> motherToChildRelationship = personService.getRelationships(null, patient, motherToChildRelationshipType);
            List<Patient> mothers = motherToChildRelationship.stream().filter(r -> r.getPersonA().getIsPatient())
                    .map(r -> patientService.getPatientOrPromotePerson(r.getPersonA().getId())).collect(Collectors.toList());
            if (!mothers.isEmpty()) {
                if (mothers.size() > 1) {
                    log.warn("Patient " + patient.getPatientId() + " has more than one mother");
                }
                data.append(generateChildComponent(patient, mothers.get(0), printerLocale));
            } else {
                data.append(generateAdultComponent(patient, printerLocale));
            }
        }

        // barcode with primary identifier
        PatientIdentifier primaryIdentifier = patient.getPatientIdentifier(emrApiProperties.getPrimaryIdentifierType());
        if (primaryIdentifier != null) {
            data.append("^FO100,1900^AT^BY4^BC,150,N^FD" + primaryIdentifier.getIdentifier() + "^FS");
        }

        data.append("^XZ");
        return data.toString();
    }

    private String generateAdultComponent(Patient patient, Locale printerLocale) {

        StringBuffer data = new StringBuffer();
        // gender
        data.append("^FO150,200^FB1650,1,0,L,0^AS^FD" + (patient.getGender() != null
                ? messageSourceService.getMessage("coreapps.gender." + patient.getGender(), null, printerLocale) : "") + "^FS");

        // birthdate
        if (patient.getBirthdate() != null) {
            if (!patient.getBirthdateEstimated()) {
                data.append("^FO150,200^FB1350,1,0,L,0^AS^FD" + new SimpleDateFormat("dd-MMM-yyyy", printerLocale).format(patient.getBirthdate()) +  "^FS");
            }
            else {
                data.append("^FO150,200^FB1350,1,0,L,0^AS^FD" + messageSourceService.getMessage("pihcore.date_estimated", null, printerLocale) + " "
                        + patient.getAge() + " " + messageSourceService.getMessage("pihcore.units.years", null, printerLocale) +  "^FS");
            }
        }

        // telephone number
        PersonAttributeType telephoneNumberAttributeType = personService.getPersonAttributeTypeByUuid(PihEmrConfigConstants.PERSONATTRIBUTETYPE_TELEPHONE_NUMBER_UUID);
        if (telephoneNumberAttributeType != null) {
            String telephoneNumber = patient.getAttribute(telephoneNumberAttributeType) != null ? patient.getAttribute(telephoneNumberAttributeType).getValue() : null;
            if (StringUtils.isNotBlank(telephoneNumber)) {
                data.append("^FO190,200^FB1650,1,0,L,0^AS^FD" + telephoneNumber + "^FS");
            }
        }

        // national identification number
        PatientIdentifierType nationalIdNumberType = patientService.getPatientIdentifierTypeByUuid(SierraLeoneConfigConstants.PATIENTIDENTIFIERTYPE_NATIONALID_UUID);
        if (nationalIdNumberType != null) {
            PatientIdentifier nationalIdNumber = patient.getPatientIdentifier(nationalIdNumberType);
            if (nationalIdNumber != null) {
                data.append("^FO190,200^FB1350,1,0,L,0^AS^FDNIN: " + nationalIdNumber.getIdentifier() + "^FS");
            }
        }

        // address (based on address hierarchy)
        PersonAddress address = patient.getPersonAddress();
        AddressHierarchyLevel level = addressHierarchyService.getBottomAddressHierarchyLevel();
        int numberOfLevels = addressHierarchyService.getAddressHierarchyLevelsCount();

        if (address != null && numberOfLevels > 0) {

            int levelCount = 1;

            if (LOWEST_LEVEL_ON_SEPARATE_LINE) {
                String lowestLevelStr = AddressHierarchyUtil.getAddressFieldValue(address, level.getAddressField());
                if (StringUtils.isNotBlank(address.getAddress2())) {
                    data.append("^FO230,200^FB1650,1,0,L,0^AS^FD" + lowestLevelStr + "^FS");
                }
                levelCount++;
            }

            StringBuffer addressStr = new StringBuffer();

            while (levelCount < numberOfLevels || (!SKIP_HIGHEST_LEVEL && levelCount <= numberOfLevels)
                    && level.getParent() != null) {  // level.getParent() should never equal null as long as levelCount <= numberOfLevels, but just to be safe we will check

                level = level.getParent();

                String levelStr = AddressHierarchyUtil.getAddressFieldValue(address, level.getAddressField());

                if (StringUtils.isNotBlank(levelStr)) {
                    addressStr.append(levelStr + ", ");
                }

                levelCount++;
            }

            if (StringUtils.isNotBlank(addressStr.toString())) {
                // trim off trailing comma and space
                addressStr.delete(addressStr.length() - 2, addressStr.length());
                data.append("^FO270,200^FB1650,1,0,L,0^AS^FD" + addressStr.toString() + "^FS");
            }
        }

        return data.toString();
    }

    private String generateChildComponent(Patient patient, Patient mother, Locale printerLocale) {
        StringBuffer data = new StringBuffer();

        // see if we can find the labour and delivery form with this child
        Concept patientExistsInEmrConcept  = conceptService.getConceptByMapping(PATIENT_EXISTS_IN_EMR_CONCEPT_PIH_CODE, "PIH");
        Concept yesConcept = conceptService.getConceptByMapping(YES_CONCEPT_PIH_CODE, "PIH");

        // get all obs linking the mother to a baby via the Labor and Delivery form
        List<Obs> linkedBabyRecords = obsService.getObservations(Collections.singletonList(mother), null, Collections.singletonList(patientExistsInEmrConcept),
                Collections.singletonList(yesConcept), null, null, null, null, null, null, null, false)
                .stream().filter(o -> o.getComment() != null && o.getComment().equals(patient.getUuid())).collect(Collectors.toList());


        if (!linkedBabyRecords.isEmpty()) {
            if (linkedBabyRecords.size() > 1) {
                log.warn("Mother " + mother.getPatientId() + " has more than one baby with the same uuid " + patient.getUuid());
            }

            Obs linkedBabyRecord = linkedBabyRecords.get(0).getObsGroup();
            Encounter deliveryEncounter = linkedBabyRecord.getEncounter();

            // determine how many babies were part of this delivery (so we can note twin/triplet/etc)
            Concept newbornDetailsConcept = conceptService.getConceptByMapping(NEWBORN_DETAILS_CONCEPT_PIH_CODE, "PIH");
            long numberOfBabies = deliveryEncounter != null ? deliveryEncounter.getAllFlattenedObs(false).stream().filter(o -> o.getConcept().equals(newbornDetailsConcept) && !o.getVoided()).count() : 1;

            if (linkedBabyRecord != null) {
                Concept birthWeightConcept = conceptService.getConceptByMapping(BIRTH_WEIGHT_CONCEPT_PIH_CODE, "PIH");
                Concept birthSexConcept = conceptService.getConceptByMapping(SEX_CONCEPT_PIH_CODE, "PIH");
                Concept dateTimeOfDeliveryConcept = conceptService.getConceptByMapping(DATETIME_OF_DELIVERY_CONCEPT_PIH_CODE, "PIH");
                Concept femaleConcept = conceptService.getConceptByMapping(FEMALE_CONCEPT_PIH_CODE, "PIH");
                Concept birthOrderConcept = conceptService.getConceptByMapping(BIRTH_ORDER_CONCEPT_PIH_CODE, "PIH");

                Obs birthWeight = linkedBabyRecord.getGroupMembers().stream().filter(o -> o.getConcept().equals(birthWeightConcept) && !o.getVoided()).findFirst().orElse(null);
                Obs birthSex = linkedBabyRecord.getGroupMembers().stream().filter(o -> o.getConcept().equals(birthSexConcept) && !o.getVoided()).findFirst().orElse(null);
                Obs dateTimeOfDelivery = linkedBabyRecord.getGroupMembers().stream().filter(o -> o.getConcept().equals(dateTimeOfDeliveryConcept) && !o.getVoided()).findFirst().orElse(null);
                Obs birthOrder = linkedBabyRecord.getGroupMembers().stream().filter(o -> o.getConcept().equals(birthOrderConcept) && !o.getVoided()).findFirst().orElse(null);

                if (dateTimeOfDelivery != null && dateTimeOfDelivery.getValueDatetime() != null) {
                    data.append("^FO150,200^FB1650,1,0,L,0^AS^FD" + messageSourceService.getMessage("pihcore.born", null, printerLocale) + " "
                            + new SimpleDateFormat("dd-MMM-yyyy HH:mm", printerLocale).format(dateTimeOfDelivery.getValueDatetime()) + "^FS");
                }

                if (birthWeight != null && birthWeight.getValueNumeric() != null) {
                    data.append("^FO190,200^FB1650,1,0,L,0^AS^FD" + birthWeight.getValueNumeric() + "kg^FS");
                }

                if (birthSex != null && birthSex.getValueCoded() != null) {
                    data.append("^FO190,200^FB1550,1,0,L,0^AS^FD" + (birthSex.getValueCoded().equals(femaleConcept) ? "FI" : "MI") + "^FS");
                }

                if (birthOrder != null && birthOrder.getValueNumeric() != null && numberOfBabies > 1) {
                    String numberOfBabiesStr = numberOfBabies == 2 ? messageSourceService.getMessage("pihcore.twin", null, printerLocale)
                            : (numberOfBabies == 3 ? messageSourceService.getMessage("pihcore.triplet", null, printerLocale) : "");

                    data.append("^FO190,200^FB1450,1,0,L,0^AS^FD" + numberOfBabiesStr + " " + birthOrder.getValueNumeric().intValue() + " " +
                            messageSourceService.getMessage("pihcore.of", null, printerLocale) + " " + numberOfBabies + "^FS");
                }
            }
        }

        // add mother information
        data.append("^FO230,200^FB1650,1,0,L,0^AS^FD" + messageSourceService.getMessage("pihcore.mother", null, printerLocale) + ": " + generateNameAndPrimaryIdentifier(mother) + "^FS");

        return data.toString();
    }

    private String generateNameAndPrimaryIdentifier(Patient patient) {
        PatientIdentifier primaryIdentifier = patient.getPatientIdentifier(emrApiProperties.getPrimaryIdentifierType());
        // patient name: for now, only printing given and family names
        String patientName = null;

        if(patient.getPersonName() != null ){
            patientName = (patient.getPersonName().getGivenName() != null ? patient.getPersonName().getGivenName() : "") + " "
                    + (patient.getPersonName().getFamilyName() != null ? patient.getPersonName().getFamilyName() : "");
        }

        return  patientName + "  " + (primaryIdentifier != null ? primaryIdentifier.getIdentifier() : "");
    }
}
