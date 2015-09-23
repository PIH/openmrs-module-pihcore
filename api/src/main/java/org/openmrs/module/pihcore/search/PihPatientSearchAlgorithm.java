package org.openmrs.module.pihcore.search;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.Years;
import org.openmrs.Patient;
import org.openmrs.PersonName;
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

    @Override
    public List<PatientAndMatchQuality> findSimilarPatients(Patient patient, Map<String, Object> otherDataPoints, Double cutoff, Integer maxResults) {

        // only do search if we have family name, given name, gender, and birthdate--return empty list otherwise
        if (patient.getFamilyName() == null || patient.getGivenName() == null || patient.getBirthdate() == null || patient.getGender() == null) {
            return new ArrayList<PatientAndMatchQuality>();
        }

        List<Patient> patients = Context.getService(NamePhoneticsService.class).findPatient(patient.getGivenName(), null, patient.getFamilyName(), null);

        List<PatientAndMatchQuality> matches = new ArrayList<PatientAndMatchQuality>();

        for (Patient match : patients) {
            List<String> matchedFields = new ArrayList<String>();

            double score = 1;    // start with a score of one because already assuming a name phonetics match of given name and family name

            matchedFields.add("names.givenName");
            matchedFields.add("names.familyName");

            // only half a point for gender match
            if (patient.getGender() != null && match.getGender() != null) {
                if (patient.getGender().equals(match.getGender())) {
                    score += 0.5;
                }
            }

            if (patient.getBirthdate() != null && match.getBirthdate() != null) {
                // exact birthdate match is worth 5 points
                if (new DateTime(patient.getBirthdate()).withTimeAtStartOfDay().equals(new DateTime(match.getBirthdate()).withTimeAtStartOfDay())) {
                    score += 5;
                }

                // otherwise check the years
                int yearsBetween = Math.abs(Years.yearsBetween(new DateTime(patient.getBirthdate()), new DateTime(match.getBirthdate())).getYears());
                if (yearsBetween < 5) {
                    score += (6 - yearsBetween) / 3;
                }
            }

            // bump up the count if there is an exact name match
            double familyNameScore = 0;
            double givenNameScore = 0;
            double middleNameScore = 0;
            for (PersonName patientName : patient.getNames()) {
                for (PersonName matchName : match.getNames()) {;
                    if (familyNameScore == 0) {  // to avoid given multiple points if a patient has two names
                        familyNameScore = nameExactMatch(patientName.getFamilyName(), matchName.getFamilyName());
                    }

                    if (givenNameScore == 0) {   // to avoid given multiple points if a patient has two names
                        givenNameScore = nameExactMatch(patientName.getGivenName(), matchName.getGivenName());
                        //matchedFields.add("names.givenName");
                    }


                    if (middleNameScore == 0) {   // to avoid given multiple points if a patient has two names
                        givenNameScore = nameExactMatch(patientName.getMiddleName(), matchName.getMiddleName());
                        matchedFields.add("names.middleName");
                    }
                }
            }
            score = score + familyNameScore + givenNameScore + middleNameScore;

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
    /**
     * Returns a score higher than 0 if value starts with the matches string.
     *
     * @param value
     * @param matches
     * @return 0 if not matched and 1 if matched
     */
    public double nameExactMatch(String value, String matches) {
        if (!StringUtils.isBlank(value) && !StringUtils.isBlank(matches)) {
            if (value.equals(matches)) {
                return 1;
            }
        }
        return 0;
    }


}
