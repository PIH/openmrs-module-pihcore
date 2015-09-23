package org.openmrs.module.pihcore.search;

import org.openmrs.Patient;
import org.openmrs.module.registrationcore.api.search.PatientAndMatchQuality;
import org.openmrs.module.registrationcore.api.search.SimilarPatientSearchAlgorithm;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Search algorithm that always returns an empty list
 */
@Service("pihcore.EmptyPatientSearchAlgorithm")
public class EmptyPatientSearchAlgorithm  implements SimilarPatientSearchAlgorithm {

    @Override
    public List<PatientAndMatchQuality> findSimilarPatients(Patient patient, Map<String, Object> map, Double aDouble, Integer integer) {
        return new ArrayList<PatientAndMatchQuality>();
    }
}
