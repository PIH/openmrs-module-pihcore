package org.openmrs.module.pihcore.search;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.joda.time.DateTime;
import org.joda.time.Years;
import org.openmrs.Patient;
import org.openmrs.PersonAddress;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;
import org.openmrs.module.addresshierarchy.AddressHierarchyLevel;
import org.openmrs.module.addresshierarchy.service.AddressHierarchyService;
import org.openmrs.module.namephonetics.NamePhonetic;
import org.openmrs.module.namephonetics.NamePhoneticsUtil;
import org.openmrs.module.pihcore.config.Config;
import org.openmrs.module.pihcore.metadata.Metadata;
import org.openmrs.module.registrationcore.api.search.FieldMatchResult;
import org.openmrs.module.registrationcore.api.search.FieldMatchStatus;
import org.openmrs.module.registrationcore.api.search.PatientAndMatchQuality;
import org.openmrs.module.registrationcore.api.search.SimilarPatientSearchAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;


/**
 The way the search works is that fetches "base cohort" via an HQL query and then "scores" each person in the result set.
 Persons are ranked via that score and only patients over a certain threshold are returned.  (The Registration App has "2.0"
 hardcoded as the threshold, so I used that as a basis for determining how to score elements).

 Base Cohort:

 An AND query against the name phonetics table checking given name and family name, so, more or less:
 WHERE encodedPotentialMatchGivenName LIKE encodedPatientGivenName% AND encodedPotentialMatchFamilyName LIKE encodedMatchFamilyName%

 Then, that cohort is scores as follows:

 Gender:
 Match; 1 pt
 Otherwise: -8 pts

 Birth Date:
 *Exact* match (same day, month and year): 10 pt (what I call one of the "jackpot" cases)
 Similar age match: if age difference in years <=5, then (6 - difference in age) pts
 Otherwise: -8 pts

 Exact name matches (name field matching *without* using name phonetics):
 If givenName, familyName and middleName (nickname) are all the same: 4 pts
 If givenName and familyName are the same: 2 pts
 Otherwise, if any *one* of the names match: 0.5 pts

 Address:
 Customizable via pih-config. A set of key-value pairs matching address field names to weights
 The fields are compared from the top of the address hierarchy down (see the address hierarchy levels), and once a field
 differs the fields below it no longer count: the same village name in a different district is not the same village.
 Fields above the configured ones (eg country) are only used this way, they don't score; levels below the lowest configured
 one (eg a house number) are not compared at all. Configured fields that are not in the hierarchy (eg postalCode) never
 rule out other fields. If there are no address hierarchy levels, every configured field counts on its own.
 A configured field that is blank on either side earns nothing, so only an address where all the configured fields are filled
 in and match gets the full score (and the MATCH status).

 Attributes:
 Customizable via pih-config. A set of key-value pairs matching address attribute names to weights
 Special functionality for attribute named "Telephone Number": all non-numeric characters are stripped out before the comparison is made (a value without any digits counts as blank)
 Special functionality for attribute named "First Name of Mother": names that only sound alike (name phonetics) match too
 Only the attributes that are configured are scored, and reported in the field results.

 Example config snippet for Haiti:

 "registrationConfig": {
    "allowUnknownPatients": true,
    "allowManualEntryOfPrimaryIdentifier": true,
    "afterCreatedUrl": "pihcore/patientRegistration/afterRegistration.page?patientId={{patientId}}&encounterId={{encounterId}}",
    "similarPatientsSearch": {
        "addressFields": {
            "cityVillage": "1",
            "address3": "1",
            "address1": "1"
        },
    "personAttributeTypes" : {
        "First Name of Mother": "3",
        "Telephone Number": "10"
        }
    }
 }
**/

@Service("pihcore.PihPatientSearchAlgorithm")
public class PihPatientSearchAlgorithm  implements SimilarPatientSearchAlgorithm {

    protected static final Log log = LogFactory.getLog(PihPatientSearchAlgorithm.class);

    @Autowired
    private SessionFactory sessionFactory;

    @Autowired
    private PatientService patientService;

    @Autowired
    private Config config;

    @Qualifier("adminService")
    @Autowired
    private AdministrationService adminService;

    // TODO what about highlighting/showing the reasons for the match on the screen?
    // TODO what about exact patient search algo? do we want to still use that, or just use this with a higher cutoff?
    // TODO phonetics on attributes?
    // TODO what about max results on initial query? needed?
    // TODO more tests? make sure matchedfields are properly included?
    // TODO address hierarchy field changes don't trigger a re-search

    @Override
    public List<PatientAndMatchQuality> findSimilarPatients(Patient patient, Map<String, Object> otherDataPoints, Double cutoff, Integer maxResults) {

        // only do search if we have family name, given name, gender, and birthdate--return empty list otherwise
        if (!hasName(patient) || !hasBirthdate(patient, otherDataPoints) || patient.getGender() == null) {
            return new ArrayList<PatientAndMatchQuality>();
        }

        // our initial search to find a "base cohort"; hits will only occur if there is a phonetic match on both given name and family name
        List<Patient> patients = getPatientsByPhonetics(patient.getGivenName(), patient.getFamilyName());

        List<PatientAndMatchQuality> matches = new ArrayList<PatientAndMatchQuality>();

        // either type may not exist at a given site; the corresponding field is then reported as missing
        String phoneAttributeName = Metadata.getPhoneNumberAttributeType() != null ? Metadata.getPhoneNumberAttributeType().getName() : null;
        String mothersFirstNameAttributeName = Metadata.getMothersFirstNameAttributeType() != null ? Metadata.getMothersFirstNameAttributeType().getName() : null;

        // the address hierarchy is only needed to compare addresses, which only happens if there are address fields configured
        Map<String, String> addressFields = getConfiguredAddressFields();
        List<String> addressHierarchyFields = (addressFields != null && !addressFields.isEmpty())
                ? getAddressHierarchyFields() : new ArrayList<String>();

        Map<String, String> personAttributeTypes = getConfiguredPersonAttributeTypes();
        String nameEncoder = adminService.getGlobalProperty("namephonetics.givenNameStringEncoder");

        for (Patient match : patients) {
            List<String> matchedFields = new ArrayList<String>();

            // per-field points; the overall score is their sum
            double genderScore = 0;
            double birthdateScore = 0;
            double nameScore = 0;
            double addressScore = 0;
            double phoneScore = 0;
            double mothersFirstNameScore = 0;
            // only for attributes that are configured; the others are left out of the results
            FieldMatchStatus phoneStatus = null;
            FieldMatchStatus mothersFirstNameStatus = null;
            double otherAttributesScore = 0;  // configured attributes without a field result of their own, eg birthplace
            FieldMatchStatus birthdateStatus = FieldMatchStatus.MISSING;

            // these are matched by the fact that we made it through the name phonetics match
            matchedFields.add("names.givenName");
            matchedFields.add("names.familyName");

            // one point if gender matches
            if (patient.getGender() != null && match.getGender() != null) {
                if (genderEquals(patient, match)) {
                    genderScore = 1;
                    matchedFields.add("gender");
                }
                else {
                    genderScore = -8;
                }
            }

            // exact birthdate match = 10 pts; otherwise, if years between < 5, assign points based on 5-years between
            if (patient.getBirthdate() != null && match.getBirthdate() != null) {
                if (!match.getBirthdateEstimated() && new DateTime(patient.getBirthdate()).withTimeAtStartOfDay().equals(new DateTime(match.getBirthdate()).withTimeAtStartOfDay())) {
                    birthdateScore = 10;
                    birthdateStatus = FieldMatchStatus.MATCH;
                    matchedFields.add("birthdate");
                }
                else {
                    int yearsBetween = Math.abs(Years.yearsBetween(new DateTime(patient.getBirthdate()), new DateTime(match.getBirthdate())).getYears());
                    if (yearsBetween <= 5) {
                        birthdateScore = 6 - yearsBetween;
                        birthdateStatus = FieldMatchStatus.SIMILAR;
                        matchedFields.add("birthdate");
                    }
                    else {
                        birthdateScore = -8;
                        birthdateStatus = FieldMatchStatus.DIFFERENT;
                    }
                }
            }
            // try estimated birthdate
            else {

                Integer patientEstimatedAge = null;
                if (otherDataPoints != null && otherDataPoints.containsKey("birthdateYears") && otherDataPoints.get("birthdateYears") != null) {
                    patientEstimatedAge = (Integer) otherDataPoints.get("birthdateYears");
                }
                else if (otherDataPoints != null && otherDataPoints.containsKey("birthdateMonths") && otherDataPoints.get("birthdateMonths") != null) {
                    patientEstimatedAge = 0;
                }

                if (patientEstimatedAge != null && match.getAge() != null) {
                    int yearsBetween = Math.abs(patientEstimatedAge - match.getAge());
                    // same scale as for an exact birthdate
                    if (yearsBetween <= 5) {
                        birthdateScore = 6 - yearsBetween;
                        birthdateStatus = FieldMatchStatus.SIMILAR;
                        matchedFields.add("birthdate");
                    }
                    else {
                        birthdateScore = -8;
                        birthdateStatus = FieldMatchStatus.DIFFERENT;
                    }
                }
            }

            // check for *exact* name matches
            boolean familyNameMatch = false;
            boolean givenNameMatch = false;
            boolean middleNameMatch = false;

            familyNameMatch = nameExactMatch(patient.getFamilyName(), match.getFamilyName());
            givenNameMatch = nameExactMatch(patient.getGivenName(), match.getGivenName());
            middleNameMatch = nameExactMatch(patient.getMiddleName(), match.getMiddleName());

            if (familyNameMatch && givenNameMatch && middleNameMatch) {
                nameScore = 4;
            }
            else if (familyNameMatch && givenNameMatch) {
                nameScore = 2;
            }
            else if (familyNameMatch || givenNameMatch || middleNameMatch) {
                nameScore = 0.5;
            }

            // check for address matches
            AddressComparison addressComparison = compareAddresses(patient, match, addressFields, addressHierarchyFields);
            addressScore = addressComparison.score;
            matchedFields.addAll(addressComparison.matchedFields);

            // check person attribute matches
            if (personAttributeTypes != null) {
                for (String personAttributeType : personAttributeTypes.keySet()) {
                    boolean isPhone = personAttributeType.equalsIgnoreCase(phoneAttributeName);
                    boolean isMothersFirstName = personAttributeType.equalsIgnoreCase(mothersFirstNameAttributeName);

                    String patientAttribute = attributeValue(patient, personAttributeType);
                    String matchAttribute = attributeValue(match, personAttributeType);

                    // special case telephone number: strip all non-numerics (so a value without any digits is as good as blank)
                    if (isPhone) {
                        patientAttribute = digitsOnly(patientAttribute);
                        matchAttribute = digitsOnly(matchAttribute);
                    }

                    FieldMatchStatus status = FieldMatchStatus.MISSING;
                    double attributeScore = 0;

                    if (StringUtils.isNotBlank(patientAttribute) && StringUtils.isNotBlank(matchAttribute)) {
                        status = FieldMatchStatus.DIFFERENT;

                        // special case First Name of Mother: names that only sound alike are similar, and score the same as an exact match
                        boolean same = stripAccentMarks(patientAttribute).equalsIgnoreCase(stripAccentMarks(matchAttribute));
                        if (same) {
                            status = FieldMatchStatus.MATCH;
                        }
                        else if (isMothersFirstName) {
                            same = soundAlike(patientAttribute, matchAttribute, nameEncoder);
                            if (same) {
                                status = FieldMatchStatus.SIMILAR;
                            }
                        }

                        if (same) {
                            attributeScore = new Double(personAttributeTypes.get(personAttributeType));
                            matchedFields.add("attributes." + personAttributeType);
                        }
                    }

                    if (isPhone) {
                        phoneScore = attributeScore;
                        phoneStatus = status;
                    }
                    else if (isMothersFirstName) {
                        mothersFirstNameScore = attributeScore;
                        mothersFirstNameStatus = status;
                    }
                    else {
                        otherAttributesScore += attributeScore;
                    }
                }
            }

            double score = genderScore + birthdateScore + nameScore + addressScore + phoneScore + mothersFirstNameScore + otherAttributesScore;

            List<FieldMatchResult> fieldResults = new ArrayList<FieldMatchResult>();
            fieldResults.add(new FieldMatchResult(FieldMatchResult.NAME,
                    (familyNameMatch && givenNameMatch) ? FieldMatchStatus.MATCH : FieldMatchStatus.SIMILAR, nameScore));
            fieldResults.add(new FieldMatchResult(FieldMatchResult.GENDER, genderStatus(patient, match), genderScore));
            fieldResults.add(new FieldMatchResult(FieldMatchResult.BIRTHDATE, birthdateStatus, birthdateScore));
            if (mothersFirstNameStatus != null) {
                fieldResults.add(new FieldMatchResult(FieldMatchResult.MOTHERS_FIRST_NAME, mothersFirstNameStatus, mothersFirstNameScore));
            }
            if (addressComparison.status != null) {
                fieldResults.add(new FieldMatchResult(FieldMatchResult.ADDRESS, addressComparison.status, addressScore));
            }
            if (phoneStatus != null) {
                fieldResults.add(new FieldMatchResult(FieldMatchResult.PHONE, phoneStatus, phoneScore));
            }

            // only take matches that make the cut
            if (cutoff == null) {
                matches.add(new PatientAndMatchQuality(match, score, matchedFields, fieldResults));
            } else {
                if (score >= cutoff) {
                    matches.add(new PatientAndMatchQuality(match, score, matchedFields, fieldResults));
                }
            }
        }

        Collections.sort(matches);

        if (maxResults != null && matches.size() > maxResults) {
            return matches.subList(0, maxResults);
        } else {
            return matches;
        }

    }

    /***
     * @param value
     * @param matches
     * @return
     */
    private boolean nameExactMatch(String value, String matches) {
        if (!StringUtils.isBlank(value) && !StringUtils.isBlank(matches)) {
            if (stripAccentMarks(value).equalsIgnoreCase(stripAccentMarks(matches))) {
                return true;
            }
        }
        return false;
    }

    private FieldMatchStatus genderStatus(Patient patient, Patient match) {
        if (patient.getGender() == null || match.getGender() == null) {
            return FieldMatchStatus.MISSING;
        }
        return genderEquals(patient, match) ? FieldMatchStatus.MATCH : FieldMatchStatus.DIFFERENT;
    }

    private boolean genderEquals(Patient patient, Patient match) {
        return patient.getGender().trim().equalsIgnoreCase(match.getGender().trim());
    }

    private static class AddressComparison {
        FieldMatchStatus status;  // null if the addresses can't be compared, in which case the field is left out of the results
        double score = 0;
        List<String> matchedFields = new ArrayList<String>();
    }

    // the address properties of the address hierarchy levels, from the top (eg country) down; empty if no levels are defined
    private List<String> getAddressHierarchyFields() {
        List<String> fields = new ArrayList<String>();
        // not getAddressHierarchyLevels(), which makes no promise about the order
        List<AddressHierarchyLevel> levels = Context.getService(AddressHierarchyService.class).getOrderedAddressHierarchyLevels();
        if (levels != null) {
            for (AddressHierarchyLevel level : levels) {
                if (level.getAddressField() != null) {
                    fields.add(level.getAddressField().getName());
                }
            }
        }
        return fields;
    }

    // the person attributes to compare and score, from "personAttributeTypes" in the similarPatientsSearch section of pih-config,
    // mapped to their weights (eg Telephone Number -> "10"); null if none are configured
    private Map<String, String> getConfiguredPersonAttributeTypes() {
        if (config.getRegistrationConfig() == null || config.getRegistrationConfig().getSimilarPatientsSearch() == null
                || !config.getRegistrationConfig().getSimilarPatientsSearch().containsKey("personAttributeTypes")) {
            return null;
        }
        return (Map<String,String>) config.getRegistrationConfig().getSimilarPatientsSearch().get("personAttributeTypes");
    }

    // the address fields to compare and score, from "addressFields" in the similarPatientsSearch section of pih-config, mapped to
    // their weights (eg cityVillage -> "1"); null if none are configured
    private Map<String, String> getConfiguredAddressFields() {
        if (config.getRegistrationConfig() == null || config.getRegistrationConfig().getSimilarPatientsSearch() == null
                || !config.getRegistrationConfig().getSimilarPatientsSearch().containsKey("addressFields")) {
            return null;
        }
        return (Map<String,String>) config.getRegistrationConfig().getSimilarPatientsSearch().get("addressFields");
    }

    /**
     * Compares the addresses field by field, from the top of the address hierarchy down. A configured field matches if it
     * is filled in on both sides and the values are the same; a field that is blank on either side does not match (but it
     * is not a difference either). Once a field in the hierarchy differs, the fields below it say nothing about whether it
     * is the same place (the same village name in another district is not the same village), so they stop counting. This
     * also means that fields above the configured ones (eg country) can rule an address out without scoring anything.
     * Levels of the hierarchy below the lowest configured field are not compared at all (eg a different house number
     * when only the village is configured), so they can't rule anything out.
     * Configured fields that are not in the hierarchy (eg postalCode) are compared after the hierarchy, and a difference
     * in them does not rule out any other field.
     * Without an address hierarchy, every configured field counts on its own.
     *
     * result.score is the sum of the weights of the matching fields, so only a MATCH gets the full score.
     *
     * MATCH: all the configured fields match
     * SIMILAR: some of the configured fields match
     * DIFFERENT: none match, and a field that is filled in on both sides differs
     * MISSING: the address is not recorded on one or both sides (or there is no value in any of the fields that are compared)
     * null: both are recorded, but there is nothing to compare (no address fields are configured, or none of them is filled
     * in on both sides); MISSING would wrongly say the address is not recorded, so the field is left out of the results
     *
     * @param addressFields the address fields to score, mapped to their weights, as returned by getConfiguredAddressFields()
     * @param hierarchyFields the address properties of the address hierarchy levels, from the top (eg country) down, as
     *                        returned by getAddressHierarchyFields(); the order is what decides which fields can rule out
     *                        which; empty if no levels are defined
     */
    private AddressComparison compareAddresses(Patient patient, Patient match, Map<String, String> addressFields, List<String> hierarchyFields) {
        AddressComparison result = new AddressComparison();

        if (addressFields == null || addressFields.isEmpty()) {
            // nothing to score, but still report an address that isn't there
            if (isBlank(patient.getPersonAddress(), hierarchyFields) || isBlank(match.getPersonAddress(), hierarchyFields)) {
                result.status = FieldMatchStatus.MISSING;
            }
            return result;
        }

        // the hierarchy down to the lowest configured level, then the configured fields that are not in it
        int lowestConfiguredLevel = -1;
        for (int i = 0; i < hierarchyFields.size(); i++) {
            if (addressFields.containsKey(hierarchyFields.get(i))) {
                lowestConfiguredLevel = i;
            }
        }
        List<String> fieldsToCompare = new ArrayList<String>(hierarchyFields.subList(0, lowestConfiguredLevel + 1));
        for (String addressField : addressFields.keySet()) {
            if (!fieldsToCompare.contains(addressField)) {
                fieldsToCompare.add(addressField);
            }
        }

        // a PersonAddress without any value in these fields (which is what a form that was left blank produces) is not an address
        PersonAddress patientAddress = patient.getPersonAddress();
        PersonAddress matchAddress = match.getPersonAddress();
        if (isBlank(patientAddress, fieldsToCompare) || isBlank(matchAddress, fieldsToCompare)) {
            result.status = FieldMatchStatus.MISSING;
            return result;
        }

        // set when a field in the address hierarchy differs, in which case we stop comparing the rest of the hierarchy: the
        // fields below it no longer count as matches (eg a different department rules out a village with the same name)
        boolean higherLevelDiffers = false;
        int compared = 0;
        int matched = 0;
        for (String addressField : fieldsToCompare) {
            boolean inHierarchy = hierarchyFields.contains(addressField);
            if (higherLevelDiffers && inHierarchy) {
                continue;
            }
            try {
                String patientField = (String) PropertyUtils.getProperty(patientAddress, addressField);
                String matchField = (String) PropertyUtils.getProperty(matchAddress, addressField);
                if (StringUtils.isNotBlank(patientField) && StringUtils.isNotBlank(matchField)) {
                    boolean configured = addressFields.containsKey(addressField);
                    boolean equal = stripAccentMarks(patientField).equalsIgnoreCase(stripAccentMarks(matchField));
                    if (configured) {
                        compared++;
                    }
                    if (equal) {
                        if (configured) {
                            matched++;
                            result.score += new Double(addressFields.get(addressField));
                            result.matchedFields.add("addresses." + addressField);
                        }
                    }
                    else if (inHierarchy) {
                        higherLevelDiffers = true;
                    }
                }
            }
            catch (Exception e) {
                log.error("Unable to access " + addressField + " on patient during similar patient search");
            }
        }

        if (matched == addressFields.size()) {
            result.status = FieldMatchStatus.MATCH;
        }
        else if (matched > 0) {
            result.status = FieldMatchStatus.SIMILAR;
        }
        else if (compared > 0 || higherLevelDiffers) {
            result.status = FieldMatchStatus.DIFFERENT;
        }
        // otherwise nothing could be compared, and the status stays null
        return result;
    }

    // true if there is no address, or none of the given fields has a value in it
    private boolean isBlank(PersonAddress address, List<String> addressFields) {
        if (address == null) {
            return true;
        }
        for (String addressField : addressFields) {
            try {
                if (StringUtils.isNotBlank((String) PropertyUtils.getProperty(address, addressField))) {
                    return false;
                }
            }
            catch (Exception e) {
                log.error("Unable to access " + addressField + " on patient during similar patient search");
            }
        }
        return true;
    }

    // names that sound alike, according to name phonetics
    private boolean soundAlike(String name, String otherName, String encoder) {
        try {
            String encoded = NamePhoneticsUtil.encodeString(name, encoder);
            String otherEncoded = NamePhoneticsUtil.encodeString(otherName, encoder);
            return encoded != null && otherEncoded != null && stripAccentMarks(encoded).equalsIgnoreCase(stripAccentMarks(otherEncoded));
        }
        catch (RuntimeException e) {
            // the encoder can't handle some characters; this is only a refinement of the comparison, so don't fail the search
            log.warn("Unable to encode name during similar patient search", e);
            return false;
        }
    }

    private String attributeValue(Patient patient, String attributeTypeName) {
        if (attributeTypeName == null) {
            return null;
        }
        return patient.getAttribute(attributeTypeName) != null ? patient.getAttribute(attributeTypeName).getValue() : null;
    }

    private String digitsOnly(String value) {
        return value != null ? value.replaceAll("[^0-9]", "") : null;
    }

    private boolean hasName(Patient patient) {
        return (patient.getGivenName() != null && patient.getFamilyName() != null);
    }

    private boolean hasBirthdate(Patient patient, Map<String, Object> otherDataPoints) {
        return ((otherDataPoints != null && otherDataPoints.containsKey("birthdateYears") && otherDataPoints.get("birthdateYears") != null) ||
                (otherDataPoints != null && otherDataPoints.containsKey("birthdateMonths") && otherDataPoints.get("birthdateMonths") != null) ||
                patient.getBirthdate() != null);
    }


    // does a AND match on firstname and lastname against the name phonetics table; returns partial matches as long as start is match (ie, 'match%')
    private List<Patient> getPatientsByPhonetics(String firstName, String lastName) {

        List<Integer> personIds;
        List<Patient> patients = new ArrayList<Patient>();

        if (StringUtils.isBlank(firstName) || (StringUtils.isBlank(lastName))){
            return new ArrayList<Patient>();
        }

        StringBuilder sql = new StringBuilder();
        sql.append("select distinct np1.personName.person.personId ");
        sql.append("from NamePhonetic np1 ");
        sql.append("where np1.renderedString like '")
                .append(NamePhoneticsUtil.encodeString(firstName, adminService.getGlobalProperty("namephonetics.givenNameStringEncoder")))
                .append("%' ");
        sql.append("and np1.nameField=" + NamePhonetic.NameField.GIVEN_NAME.getValue() + " ");
        sql.append("and np1.personName.personNameId in ");
        sql.append("(select np2.personName.personNameId from NamePhonetic np2 ");
        sql.append("where np2.renderedString like '")
                .append(NamePhoneticsUtil.encodeString(lastName, adminService.getGlobalProperty("namephonetics.familyNameStringEncoder")))
                .append("%' ");
        sql.append("and np2.nameField=" + NamePhonetic.NameField.FAMILY_NAME.getValue() + ")");
        try{
            Query query = sessionFactory.getCurrentSession().createQuery(sql.toString());
            //query.setCacheMode(CacheMode.IGNORE);  // was there a reason we were ignoring the cache? seems like we'd want to cache for performance reasons?
            personIds = query.list();
            if (personIds != null && personIds.size() > 0) {
                query = sessionFactory.getCurrentSession().createQuery("from Patient as p where p.personId in (:personIds) and voided='false'");
                query.setParameterList("personIds", personIds);
                patients = query.list();
            }
        }
        catch(Exception e){
            log.error("error retrieving name phonetics", e);
        }

        return patients;
    }


    private String stripAccentMarks(String string) {

        // This will separate all of the accent marks from the characters.
        string = Normalizer.normalize(string, Normalizer.Form.NFD);

        // this removes all non-alphanumerics
        string = string.replaceAll("\\p{M}", "");

        return string;
    }

}
