package org.openmrs.module.pihcore.search;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.Years;
import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.module.namephonetics.NamePhoneticsService;
import org.openmrs.module.registrationcore.api.search.PatientAndMatchQuality;
import org.openmrs.module.registrationcore.api.search.SimilarPatientSearchAlgorithm;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service("pihcore.PihPatientSearchAlgorithm")
public class PihPatientSearchAlgorithm  implements SimilarPatientSearchAlgorithm {

    // TODO add some tests of this
    // TODO what about mother's name?
    // TODO phone number?
    // TODO add address back in

    @Override
    public List<PatientAndMatchQuality> findSimilarPatients(Patient patient, Map<String, Object> otherDataPoints, Double cutoff, Integer maxResults) {

        // only do search if we have family name, given name, gender, and birthdate--return empty list otherwise
        if (!hasName(patient) || !hasBirthdate(patient, otherDataPoints) || patient.getGender() == null) {
            return new ArrayList<PatientAndMatchQuality>();
        }

        // our initial search to find a "base cohort"; hits will only occur if there is a exact phonetic match on both given name and family name
        List<Patient> patients = Context.getService(NamePhoneticsService.class).findPatient(patient.getGivenName(), null, patient.getFamilyName(), null);

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
    public boolean nameExactMatch(String value, String matches) {
        if (!StringUtils.isBlank(value) && !StringUtils.isBlank(matches)) {
            if (value.equalsIgnoreCase(matches)) {
                return true;
            }
        }
        return false;
    }

    public boolean hasName(Patient patient) {
        return (patient.getGivenName() != null && patient.getFamilyName() != null);
    }

    public boolean hasBirthdate(Patient patient, Map<String, Object> otherDataPoints) {
        return ((otherDataPoints.containsKey("birthdateYears") && otherDataPoints.get("birthdateYears") != null) ||
                (otherDataPoints.containsKey("birthdateMonths") && otherDataPoints.get("birthdateMonths") != null) ||
                patient.getBirthdate() != null);
    }

}
