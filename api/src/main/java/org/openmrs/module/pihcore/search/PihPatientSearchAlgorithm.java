package org.openmrs.module.pihcore.search;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.joda.time.DateTime;
import org.joda.time.Years;
import org.openmrs.Patient;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.PatientService;
import org.openmrs.module.namephonetics.NamePhoneticsUtil;
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

    @Qualifier("adminService")
    @Autowired
    private AdministrationService adminService;

    // TODO add some tests of this
    // TODO what about mother's name?
    // TODO phone number?
    // TODO add address back in
    // TODO check special characters
    // TODO what about max results?
    // TODO switch to direct DB query of name phonetics table?

    @Override
    public List<PatientAndMatchQuality> findSimilarPatients(Patient patient, Map<String, Object> otherDataPoints, Double cutoff, Integer maxResults) {

        // only do search if we have family name, given name, gender, and birthdate--return empty list otherwise
        if (!hasName(patient) || !hasBirthdate(patient, otherDataPoints) || patient.getGender() == null) {
            return new ArrayList<PatientAndMatchQuality>();
        }

        // our initial search to find a "base cohort"; hits will only occur if there is a exact phonetic match on both given name and family name
        List<Patient> patients = getPatientsByPhonetics(patient.getGivenName(), patient.getFamilyName());

        List<PatientAndMatchQuality> matches = new ArrayList<PatientAndMatchQuality>();

        for (Patient match : patients) {
            List<String> matchedFields = new ArrayList<String>();

            double score = 0;

            matchedFields.add("names.givenName");
            matchedFields.add("names.familyName");

            // one point if gender matches
            if (patient.getGender() != null && match.getGender() != null) {
                if (patient.getGender().equals(match.getGender())) {
                    score += 1;
                }
            }

            // exact birthdate match = 10 pts; otherwise, if years between < 5, assign points based on 5-years between
            if (patient.getBirthdate() != null && match.getBirthdate() != null) {
                if (!match.getBirthdateEstimated() && new DateTime(patient.getBirthdate()).withTimeAtStartOfDay().equals(new DateTime(match.getBirthdate()).withTimeAtStartOfDay())) {
                    score += 10;
                }

                int yearsBetween = Math.abs(Years.yearsBetween(new DateTime(patient.getBirthdate()), new DateTime(match.getBirthdate())).getYears());
                if (yearsBetween < 5) {
                    score += (6 - yearsBetween);
                }
            }
            // try estimated birthdate
            // TODO this could be better
            else {

                Integer patientEstimatedAge = null;
                if (otherDataPoints.containsKey("birthdateYears") && otherDataPoints.get("birthdateYears") != null) {
                    patientEstimatedAge = (Integer) otherDataPoints.get("birthdateYears");
                }
                else if (otherDataPoints.containsKey("birthdateMonths") && otherDataPoints.get("birthdateMonths") != null) {
                    patientEstimatedAge = 0;
                }

                if (patientEstimatedAge != null) {
                    int yearsBetween = Math.abs(patientEstimatedAge - match.getAge());
                    if (yearsBetween < 5) {
                        score += (6 - yearsBetween);
                    }
                }
            }

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

// TODO add back in address matching, but skip country, of course; allow us to customize by implementation? what are they currently doing in Pleebo?
/*

            for (PersonAddress patientAddress : patient.getAddresses()) {
                for (PersonAddress matchAddress : match.getAddresses()) {
                    if (!StringUtils.isBlank(matchAddress.getCountry())
                            && matchAddress.getCountry().equals(patientAddress.getCountry())) {
                        matchedFields.add("addresses.country");
                        score += 1;
                    }

                    if (!StringUtils.isBlank(matchAddress.getCityVillage())
                            && matchAddress.getCityVillage().equals(patientAddress.getCityVillage())) {
                        matchedFields.add("addresses.cityVillage");
                        score += 1;
                    }
                }
            }
*/

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
        return ((otherDataPoints.containsKey("birthdateYears") && otherDataPoints.get("birthdateYears") != null) ||
                (otherDataPoints.containsKey("birthdateMonths") && otherDataPoints.get("birthdateMonths") != null) ||
                patient.getBirthdate() != null);
    }


    // TODO this should be converted to HQL and/or cleaned up in some other way?
    private List<Patient> getPatientsByPhonetics(String firstName, String lastName) {

        List<Integer> queryResults = null;

        if (StringUtils.isBlank(firstName) || (StringUtils.isBlank(lastName))){
            return new ArrayList<Patient>();
        }

        StringBuilder sql = new StringBuilder();
        sql.append("select distinct np1.personName.person.personId ");
        sql.append("from NamePhonetic np1 ");
        sql.append("where np1.renderedString like '")
                .append(NamePhoneticsUtil.encodeString(firstName, adminService.getGlobalProperty("namephonetics.givenNameStringEncoder")))
                .append("%' ");
        sql.append("and np1.nameField=1 ");
        sql.append("and np1.personName.personNameId in ");
        sql.append("(select np2.personName.personNameId from NamePhonetic np2 ");
        sql.append("where np2.renderedString like '")
                .append(NamePhoneticsUtil.encodeString(lastName, adminService.getGlobalProperty("namephonetics.familyNameStringEncoder")))
                .append("%' ");
        sql.append("and np2.nameField=3)");
        try{
            Query query = sessionFactory.getCurrentSession().createQuery(sql.toString());
            //query.setCacheMode(CacheMode.IGNORE);
            queryResults = query.list();

        }catch(Exception e){
            log.error("error retrieving name phonetics", e);
        }

        if (queryResults != null && queryResults.size() > 0) {
            return idsToPatients(queryResults);
        }
        else {
            return new ArrayList<Patient>();
        }

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
