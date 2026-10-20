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

 Attributes:
 Customizable via pih-config. A set of key-value pairs matching address attribute names to weights
 Special functionality for attribute named "Telephone Number": all non-numeric characters are stripped out before the comparison is made

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

        for (Patient match : patients) {
            List<String> matchedFields = new ArrayList<String>();

            double score = 0;

            // per-field points, only used to report the per-field results; they don't affect the overall score
            double genderScore = 0;
            double birthdateScore = 0;
            double nameScore = 0;
            double addressScore = 0;
            double phoneScore = 0;
            double mothersFirstNameScore = 0;
            FieldMatchStatus birthdateStatus = FieldMatchStatus.MISSING;

            // these are matched by the fact that we made it through the name phonetics match
            matchedFields.add("names.givenName");
            matchedFields.add("names.familyName");

            // one point if gender matches
            if (patient.getGender() != null && match.getGender() != null) {
                if (patient.getGender().equals(match.getGender())) {
                    score += 1;
                    genderScore = 1;
                    matchedFields.add("gender");
                }
                else {
                    score += -8;
                    genderScore = -8;
                }
            }

            // exact birthdate match = 10 pts; otherwise, if years between < 5, assign points based on 5-years between
            if (patient.getBirthdate() != null && match.getBirthdate() != null) {
                if (!match.getBirthdateEstimated() && new DateTime(patient.getBirthdate()).withTimeAtStartOfDay().equals(new DateTime(match.getBirthdate()).withTimeAtStartOfDay())) {
                    score += 10;
                    birthdateScore = 10;
                    birthdateStatus = FieldMatchStatus.MATCH;
                    matchedFields.add("birthdate");
                }
                else {
                    int yearsBetween = Math.abs(Years.yearsBetween(new DateTime(patient.getBirthdate()), new DateTime(match.getBirthdate())).getYears());
                    if (yearsBetween <= 5) {
                        score += (6 - yearsBetween);
                        birthdateScore = 6 - yearsBetween;
                        birthdateStatus = FieldMatchStatus.SIMILAR;
                        matchedFields.add("birthdate");
                    }
                    else {
                        score += -8;
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
                    if (yearsBetween < 5) {
                        score += (6 - yearsBetween);
                        birthdateScore = 6 - yearsBetween;
                        birthdateStatus = FieldMatchStatus.SIMILAR;
                        matchedFields.add("birthdate");
                    }
                    else {
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
            score += nameScore;

            // check for address matches
            if (config.getRegistrationConfig() != null && config.getRegistrationConfig().getSimilarPatientsSearch() != null
                    && config.getRegistrationConfig().getSimilarPatientsSearch().containsKey("addressFields")) {
                Map<String, String> addressFields = (Map<String,String>) config.getRegistrationConfig().getSimilarPatientsSearch().get("addressFields");
                PersonAddress patientAddress = patient.getPersonAddress();
                PersonAddress matchAddress = match.getPersonAddress();
                if (addressFields != null && patientAddress != null && matchAddress != null) {
                    for (String addressField : addressFields.keySet()) {
                        try {
                            String patientField = (String) PropertyUtils.getProperty(patientAddress, addressField);
                            String matchField = (String) PropertyUtils.getProperty(matchAddress, addressField);
                            if (StringUtils.isNotBlank(patientField) && StringUtils.isNotBlank(matchField) &&
                                    stripAccentMarks(patientField).equalsIgnoreCase(stripAccentMarks(matchField))) {
                                score += new Double(addressFields.get(addressField));
                                addressScore += new Double(addressFields.get(addressField));
                                matchedFields.add("addresses." + addressField);
                            }
                        }
                        catch (Exception e) {
                            log.error("Unable to access " + addressField + " on patient during similar patient search");
                        }
                    }
                }
            }

            // check person attribute matches
            if (config.getRegistrationConfig() != null && config.getRegistrationConfig().getSimilarPatientsSearch() != null
                    && config.getRegistrationConfig().getSimilarPatientsSearch().containsKey("personAttributeTypes")) {
                Map<String, String> personAttributeTypes = (Map<String,String>) config.getRegistrationConfig().getSimilarPatientsSearch().get("personAttributeTypes");
                for (String personAttributeType : personAttributeTypes.keySet()) {
                    String patientAttribute = patient.getAttribute(personAttributeType) != null ? patient.getAttribute(personAttributeType).getValue() : null;
                    String matchAttribute = match.getAttribute(personAttributeType) != null ? match.getAttribute(personAttributeType).getValue() : null;

                    if (StringUtils.isNotBlank(patientAttribute) && StringUtils.isNotBlank(matchAttribute)) {

                        // special case telephone number: strip all non-numerics
                        if (personAttributeType.equalsIgnoreCase(Metadata.getPhoneNumberAttributeType().getName())) {
                            patientAttribute = patientAttribute.replaceAll("[^0-9]", "");
                            matchAttribute = matchAttribute.replaceAll("[^0-9]", "");
                        }

                        // special case First Name of Mother: convert to name phonetics
                        if (personAttributeType.equalsIgnoreCase(Metadata.getMothersFirstNameAttributeType().getName())) {
                            patientAttribute = NamePhoneticsUtil.encodeString(patientAttribute, adminService.getGlobalProperty("namephonetics.givenNameStringEncoder"));
                            matchAttribute = NamePhoneticsUtil.encodeString(matchAttribute, adminService.getGlobalProperty("namephonetics.givenNameStringEncoder"));
                        }

                        if (stripAccentMarks(patientAttribute).equalsIgnoreCase(stripAccentMarks(matchAttribute))) {
                            score += new Double(personAttributeTypes.get(personAttributeType));
                            if (personAttributeType.equalsIgnoreCase(phoneAttributeName)) {
                                phoneScore = new Double(personAttributeTypes.get(personAttributeType));
                            }
                            else if (personAttributeType.equalsIgnoreCase(mothersFirstNameAttributeName)) {
                                mothersFirstNameScore = new Double(personAttributeTypes.get(personAttributeType));
                            }
                            matchedFields.add("attributes." + personAttributeType);
                        }
                    }
                }
            }

            List<FieldMatchResult> fieldResults = new ArrayList<FieldMatchResult>();
            fieldResults.add(new FieldMatchResult(FieldMatchResult.NAME,
                    (familyNameMatch && givenNameMatch) ? FieldMatchStatus.MATCH : FieldMatchStatus.SIMILAR, nameScore));
            fieldResults.add(new FieldMatchResult(FieldMatchResult.GENDER, genderStatus(patient, match), genderScore));
            fieldResults.add(new FieldMatchResult(FieldMatchResult.BIRTHDATE, birthdateStatus, birthdateScore));
            fieldResults.add(new FieldMatchResult(FieldMatchResult.MOTHERS_FIRST_NAME,
                    mothersFirstNameStatus(patient, match, mothersFirstNameAttributeName), mothersFirstNameScore));
            fieldResults.add(new FieldMatchResult(FieldMatchResult.ADDRESS, addressStatus(patient, match), addressScore));
            fieldResults.add(new FieldMatchResult(FieldMatchResult.PHONE,
                    phoneStatus(patient, match, phoneAttributeName), phoneScore));

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
        return patient.getGender().equals(match.getGender()) ? FieldMatchStatus.MATCH : FieldMatchStatus.DIFFERENT;
    }

    // compares all configured address fields that are filled in on both sides
    private FieldMatchStatus addressStatus(Patient patient, Patient match) {
        if (config.getRegistrationConfig() == null || config.getRegistrationConfig().getSimilarPatientsSearch() == null
                || !config.getRegistrationConfig().getSimilarPatientsSearch().containsKey("addressFields")) {
            return FieldMatchStatus.MISSING;
        }
        Map<String, String> addressFields = (Map<String,String>) config.getRegistrationConfig().getSimilarPatientsSearch().get("addressFields");
        PersonAddress patientAddress = patient.getPersonAddress();
        PersonAddress matchAddress = match.getPersonAddress();
        if (addressFields == null || patientAddress == null || matchAddress == null) {
            return FieldMatchStatus.MISSING;
        }

        int compared = 0;
        int matched = 0;
        for (String addressField : addressFields.keySet()) {
            try {
                String patientField = (String) PropertyUtils.getProperty(patientAddress, addressField);
                String matchField = (String) PropertyUtils.getProperty(matchAddress, addressField);
                if (StringUtils.isNotBlank(patientField) && StringUtils.isNotBlank(matchField)) {
                    compared++;
                    if (stripAccentMarks(patientField).equalsIgnoreCase(stripAccentMarks(matchField))) {
                        matched++;
                    }
                }
            }
            catch (Exception e) {
                log.error("Unable to access " + addressField + " on patient during similar patient search");
            }
        }

        if (compared == 0) {
            return FieldMatchStatus.MISSING;
        }
        if (matched == compared) {
            return FieldMatchStatus.MATCH;
        }
        return matched > 0 ? FieldMatchStatus.SIMILAR : FieldMatchStatus.DIFFERENT;
    }

    // as in the score, only the digits are compared
    private FieldMatchStatus phoneStatus(Patient patient, Patient match, String phoneAttributeName) {
        String patientPhone = digitsOnly(attributeValue(patient, phoneAttributeName));
        String matchPhone = digitsOnly(attributeValue(match, phoneAttributeName));
        if (StringUtils.isBlank(patientPhone) || StringUtils.isBlank(matchPhone)) {
            return FieldMatchStatus.MISSING;
        }
        return patientPhone.equals(matchPhone) ? FieldMatchStatus.MATCH : FieldMatchStatus.DIFFERENT;
    }

    // as in the score, names that only sound alike (name phonetics) count as a match; here they are reported as similar
    private FieldMatchStatus mothersFirstNameStatus(Patient patient, Patient match, String mothersFirstNameAttributeName) {
        String patientName = attributeValue(patient, mothersFirstNameAttributeName);
        String matchName = attributeValue(match, mothersFirstNameAttributeName);
        if (StringUtils.isBlank(patientName) || StringUtils.isBlank(matchName)) {
            return FieldMatchStatus.MISSING;
        }
        if (nameExactMatch(patientName, matchName)) {
            return FieldMatchStatus.MATCH;
        }
        String encoder = adminService.getGlobalProperty("namephonetics.givenNameStringEncoder");
        String patientEncoded = NamePhoneticsUtil.encodeString(patientName, encoder);
        String matchEncoded = NamePhoneticsUtil.encodeString(matchName, encoder);
        if (patientEncoded != null && matchEncoded != null
                && stripAccentMarks(patientEncoded).equalsIgnoreCase(stripAccentMarks(matchEncoded))) {
            return FieldMatchStatus.SIMILAR;
        }
        return FieldMatchStatus.DIFFERENT;
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
