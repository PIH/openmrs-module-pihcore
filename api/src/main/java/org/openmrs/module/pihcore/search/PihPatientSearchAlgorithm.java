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
import org.openmrs.module.registrationcore.api.search.PatientAndMatchQuality;
import org.openmrs.module.registrationcore.api.search.SimilarPatientSearchAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 The way the search works is that fetches "base cohort" via an HQL query and then "scores" each person in the result set.
 Persons are ranked via that score and only patients over a certain threshold are returned.  (The Registration App has "2.0"
 hardcoded as the threshold, so I used that as a basis for determining how to score elements).

 Base Cohort (union of the following):

 1. An AND query against the name phonetics table checking given name and family name, so, more or less:
 WHERE encodedPotentialMatchGivenName LIKE encodedPatientGivenName% AND encodedPotentialMatchFamilyName LIKE encodedMatchFamilyName%

 2. Typo tolerance: phonetic encoders (ex: Double Metaphone) truncate their output, so a single-letter typo can change the
 code and drop the right patient from (1). So we also include patients that match phonetically on the given name and whose
 family name is within a small edit distance of the family name entered (and vice versa: phonetic family name match with
 a given name within a small edit distance).

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

        for (Patient match : patients) {
            List<String> matchedFields = new ArrayList<String>();

            double score = 0;

            // these are matched by the fact that we made it through the name phonetics match
            matchedFields.add("names.givenName");
            matchedFields.add("names.familyName");

            // one point if gender matches
            if (patient.getGender() != null && match.getGender() != null) {
                if (patient.getGender().equals(match.getGender())) {
                    score += 1;
                    matchedFields.add("gender");
                }
                else {
                    score += -8;
                }
            }

            // exact birthdate match = 10 pts; otherwise, if years between < 5, assign points based on 5-years between
            if (patient.getBirthdate() != null && match.getBirthdate() != null) {
                if (!match.getBirthdateEstimated() && new DateTime(patient.getBirthdate()).withTimeAtStartOfDay().equals(new DateTime(match.getBirthdate()).withTimeAtStartOfDay())) {
                    score += 10;
                    matchedFields.add("birthdate");
                }
                else {
                    int yearsBetween = Math.abs(Years.yearsBetween(new DateTime(patient.getBirthdate()), new DateTime(match.getBirthdate())).getYears());
                    if (yearsBetween <= 5) {
                        score += (6 - yearsBetween);
                        matchedFields.add("birthdate");
                    }
                    else {
                        score += -8;
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
                        matchedFields.add("birthdate");
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
                score += 4;
            }
            else if (familyNameMatch && givenNameMatch) {
                score += 2;
            }
            else if (familyNameMatch || givenNameMatch || middleNameMatch) {
                score += 0.5;
            }

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
                            matchedFields.add("attributes." + personAttributeType);
                        }
                    }
                }
            }

            // only take matches that make the cut
            if (cutoff == null) {
                matches.add(new PatientAndMatchQuality(match, score, matchedFields));
            } else {
                if (score >= cutoff) {
                    matches.add(new PatientAndMatchQuality(match, score, matchedFields));
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

    private boolean hasName(Patient patient) {
        return (patient.getGivenName() != null && patient.getFamilyName() != null);
    }

    private boolean hasBirthdate(Patient patient, Map<String, Object> otherDataPoints) {
        return ((otherDataPoints != null && otherDataPoints.containsKey("birthdateYears") && otherDataPoints.get("birthdateYears") != null) ||
                (otherDataPoints != null && otherDataPoints.containsKey("birthdateMonths") && otherDataPoints.get("birthdateMonths") != null) ||
                patient.getBirthdate() != null);
    }


    // builds the "base cohort": the phonetic AND match on given and family name, plus the patients whose
    // given (or family) name matches phonetically and whose other name is "close enough" (see isCloseMatch)
    private List<Patient> getPatientsByPhonetics(String firstName, String lastName) {

        if (StringUtils.isBlank(firstName) || (StringUtils.isBlank(lastName))){
            return new ArrayList<Patient>();
        }

        List<Patient> patients = new ArrayList<Patient>();

        try {
            Set<Integer> personIds = new LinkedHashSet<Integer>(getPersonIdsByPhoneticsOnBothNames(firstName, lastName));
            personIds.addAll(getPersonIdsByPhoneticsOnOneNameAndCloseOtherName(firstName, NamePhonetic.NameField.GIVEN_NAME,
                    lastName, NamePhonetic.NameField.FAMILY_NAME));
            personIds.addAll(getPersonIdsByPhoneticsOnOneNameAndCloseOtherName(lastName, NamePhonetic.NameField.FAMILY_NAME,
                    firstName, NamePhonetic.NameField.GIVEN_NAME));

            if (personIds.size() > 0) {
                Query query = sessionFactory.getCurrentSession().createQuery("from Patient as p where p.personId in (:personIds) and voided='false'");
                query.setParameterList("personIds", personIds);
                patients = query.list();
            }
        }
        catch(Exception e){
            log.error("error retrieving name phonetics", e);
        }

        return patients;
    }

    // does a AND match on firstname and lastname against the name phonetics table; returns partial matches as long as start is match (ie, 'match%')
    private List<Integer> getPersonIdsByPhoneticsOnBothNames(String firstName, String lastName) {
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
        return sessionFactory.getCurrentSession().createQuery(sql.toString()).list();
    }

    // finds names that match phonetically on one field (ex: given name), and then keeps those whose *other* name field
    // (ex: family name) is a close (edit distance) match; the edit distance is done in memory against the (relatively
    // small) set of names that already matched phonetically on the first field
    private List<Integer> getPersonIdsByPhoneticsOnOneNameAndCloseOtherName(String phoneticValue, NamePhonetic.NameField phoneticField,
                                                                           String closeValue, NamePhonetic.NameField closeField) {

        String encoderGp = phoneticField == NamePhonetic.NameField.GIVEN_NAME ? "namephonetics.givenNameStringEncoder" : "namephonetics.familyNameStringEncoder";
        String encoded = NamePhoneticsUtil.encodeString(phoneticValue, adminService.getGlobalProperty(encoderGp));

        Query query = sessionFactory.getCurrentSession().createQuery(
                "select pn.person.personId, pn.givenName, pn.familyName from PersonName pn " +
                "where pn.voided = false and pn.personNameId in " +
                "(select np.personName.personNameId from NamePhonetic np " +
                "where np.renderedString like :encoded and np.nameField = :field)");
        query.setString("encoded", encoded + "%");
        query.setInteger("field", phoneticField.getValue());

        List<Integer> personIds = new ArrayList<Integer>();
        for (Object row : query.list()) {
            Object[] columns = (Object[]) row;
            String candidate = closeField == NamePhonetic.NameField.GIVEN_NAME ? (String) columns[1] : (String) columns[2];
            if (isCloseMatch(closeValue, candidate)) {
                personIds.add((Integer) columns[0]);
            }
        }
        return personIds;
    }

    /**
     * Two names are a "close match" if they are within a small edit distance (ignoring case and accent marks), to allow
     * for typos. Very short names are not fuzzy matched, because a single edit changes too much of the name.
     */
    protected boolean isCloseMatch(String value, String candidate) {
        if (StringUtils.isBlank(value) || StringUtils.isBlank(candidate)) {
            return false;
        }
        String a = stripAccentMarks(value.trim()).toLowerCase();
        String b = stripAccentMarks(candidate.trim()).toLowerCase();
        int length = Math.min(a.length(), b.length());
        int maxDistance = length <= 3 ? 0 : (length <= 5 ? 1 : 2);
        return StringUtils.getLevenshteinDistance(a, b) <= maxDistance;
    }


    private String stripAccentMarks(String string) {

        // This will separate all of the accent marks from the characters.
        string = Normalizer.normalize(string, Normalizer.Form.NFD);

        // this removes all non-alphanumerics
        string = string.replaceAll("\\p{M}", "");

        return string;
    }

}
