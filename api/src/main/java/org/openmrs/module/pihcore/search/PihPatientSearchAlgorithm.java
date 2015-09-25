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
import org.openmrs.Person;
import org.openmrs.PersonAddress;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.PatientService;
import org.openmrs.module.namephonetics.NamePhonetic;
import org.openmrs.module.namephonetics.NamePhoneticsUtil;
import org.openmrs.module.pihcore.config.Config;
import org.openmrs.module.registrationcore.api.search.PatientAndMatchQuality;
import org.openmrs.module.registrationcore.api.search.SimilarPatientSearchAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

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
    // TODO create config for Pleebo? is this a current bug in Pleebo?
    // TODO phonetics on attributes?
    // TODO what about max results on initial query? needed?
    // TODO more tests? make sure matchedfields are properly included?
    // TODO max results (10 results each time, but changes, and you don't know?)
    // TODO address hierarchy field changes don't trigger a re-search
    // TODO clear off accent marks when doing exact name search?

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
                }
                matchedFields.add("gender");
            }

            // exact birthdate match = 10 pts; otherwise, if years between < 5, assign points based on 5-years between
            if (patient.getBirthdate() != null && match.getBirthdate() != null) {
                if (!match.getBirthdateEstimated() && new DateTime(patient.getBirthdate()).withTimeAtStartOfDay().equals(new DateTime(match.getBirthdate()).withTimeAtStartOfDay())) {
                    score += 10;
                    matchedFields.add("birthdate");
                }

                int yearsBetween = Math.abs(Years.yearsBetween(new DateTime(patient.getBirthdate()), new DateTime(match.getBirthdate())).getYears());
                if (yearsBetween <= 5) {
                    score += (6 - yearsBetween);
                    matchedFields.add("birthdate");
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

                if (patientEstimatedAge != null) {
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
                score += 5;
            }
            else if (familyNameMatch && givenNameMatch) {
                score += 2;
            }
            else if (familyNameMatch || givenNameMatch || middleNameMatch) {
                score += 0.5;
            }

            // check for address matches
            if (config.getRegistrationConfig() != null && config.getRegistrationConfig().getSimilarPatientsSearch().containsKey("addressFields")) {
                Map<String, String> addressFields = (Map<String,String>) config.getRegistrationConfig().getSimilarPatientsSearch().get("addressFields");
                PersonAddress patientAddress = patient.getPersonAddress();
                PersonAddress matchAddress = match.getPersonAddress();
                if (addressFields != null && patientAddress != null && matchAddress != null) {
                    for (String addressField : addressFields.keySet()) {
                        try {
                            String patientField = (String) PropertyUtils.getProperty(patientAddress, addressField);
                            String matchField = (String) PropertyUtils.getProperty(matchAddress, addressField);
                            if (StringUtils.isNotBlank(patientField) && StringUtils.isNotBlank(matchField) &&
                                    patientField.equalsIgnoreCase(matchField)) {
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
            if (config.getRegistrationConfig() != null && config.getRegistrationConfig().getSimilarPatientsSearch().containsKey("personAttributeTypes")) {
                Map<String, String> personAttributeTypes = (Map<String,String>) config.getRegistrationConfig().getSimilarPatientsSearch().get("personAttributeTypes");
                for (String personAttributeType : personAttributeTypes.keySet()) {
                    String patientAttribute = patient.getAttribute(personAttributeType) != null ? patient.getAttribute(personAttributeType).getValue() : null;
                    String matchAttribute = match.getAttribute(personAttributeType) != null ? match.getAttribute(personAttributeType).getValue() : null;

                    if (StringUtils.isNotBlank(patientAttribute) && StringUtils.isNotBlank(matchAttribute)) {

                        // special case telephone number: strip all non-numerics
                        if (personAttributeType.equalsIgnoreCase("Telephone Number")) {
                            patientAttribute = patientAttribute.replaceAll("[^0-9]", "");
                            matchAttribute = matchAttribute.replaceAll("[^0-9]", "");
                        }

                        if (patientAttribute.equalsIgnoreCase(matchAttribute)) {
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
            if (value.equalsIgnoreCase(matches)) {
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


    // does a AND match on firstname and lastname against the name phonetics table; returns partial matches as long as start is match (ie, 'match%')
    private List<Patient> getPatientsByPhonetics(String firstName, String lastName) {

        List<Integer> queryResults = new ArrayList<Integer>();

        if (StringUtils.isBlank(firstName) || (StringUtils.isBlank(lastName))){
            return new ArrayList<Patient>();
        }

        StringBuilder sql = new StringBuilder();
        sql.append("select distinct np1.personName.person.personId ");   // we fetch ids here and then convert to patients in idToPatients so that we can exclude non-patients from the result list and avoid a cast exception
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
            queryResults = query.list();

        }catch(Exception e){
            log.error("error retrieving name phonetics", e);
        }

        return idsToPatients(queryResults);
    }

    private List<Patient> idsToPatients(List<Integer> queryResults) {
        List<Patient> patients = new ArrayList<Patient>();
        for (Integer i : queryResults) {
            Patient patient = patientService.getPatient(i);
            if (patient !=  null) {
                patients.add(patient);
            }
        }
        return patients;
    }


}
