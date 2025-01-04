package org.openmrs.module.pihcore.page.controller.patient;

import org.openmrs.Concept;
import org.openmrs.Patient;
import org.openmrs.api.ConceptService;
import org.openmrs.module.emrapi.patient.PatientDomainWrapper;
import org.openmrs.module.pihcore.PihCoreConstants;
import org.openmrs.module.pihcore.model.Vaccination;
import org.openmrs.module.pihcore.service.PihCoreService;
import org.openmrs.ui.framework.UiUtils;
import org.openmrs.ui.framework.annotation.InjectBeans;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.openmrs.ui.framework.page.PageModel;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class VaccinationsPageController {

    public static final Map<String, Integer[]> VACCINATION_TABLE_CONCEPTS = new LinkedHashMap<>();

    static {
        VACCINATION_TABLE_CONCEPTS.put(PihCoreConstants.BCG_VACCINATION_CONCEPT_UUID, new Integer[]{ 1 });
        VACCINATION_TABLE_CONCEPTS.put(PihCoreConstants.COVID_VACCINATION_CONCEPT_UUID, new Integer[]{ 1, 2, 3 });
        VACCINATION_TABLE_CONCEPTS.put(PihCoreConstants.DIPTHERIA_TETANUS_VACCINATION_CONCEPT_UUID, new Integer[]{ 0, 1, 2, 3, 11, 12 });
        VACCINATION_TABLE_CONCEPTS.put(PihCoreConstants.HEP_B_VACCINATION_CONCEPT_UUID, new Integer[]{ 0, 1, 2, 3 });
        VACCINATION_TABLE_CONCEPTS.put(PihCoreConstants.FLU_VACCINATION_CONCEPT_UUID, new Integer[]{ 1, 2, 3, 11, 12 });
        VACCINATION_TABLE_CONCEPTS.put(PihCoreConstants.MENINGO_VACCINATION_CONCEPT_UUID, new Integer[]{ 1, 2 });
        VACCINATION_TABLE_CONCEPTS.put(PihCoreConstants.PENTAVALENT_VACCINATION_CONCEPT_UUID, new Integer[]{ 1, 2, 3 });
        VACCINATION_TABLE_CONCEPTS.put(PihCoreConstants.PNEUMOCOCCAL_VACCINATION_CONCEPT_UUID, new Integer[]{ 1, 2, 3 });
        VACCINATION_TABLE_CONCEPTS.put(PihCoreConstants.POLIO_VACCINATION_CONCEPT_UUID, new Integer[]{ 0, 1, 2, 3, 11, 12 });
        VACCINATION_TABLE_CONCEPTS.put(PihCoreConstants.ROTAVIRUS_VACCINATION_CONCEPT_UUID, new Integer[]{ 1, 2 });
        VACCINATION_TABLE_CONCEPTS.put(PihCoreConstants.MEASLES_RUBELLA_VACCINATION_CONCEPT_UUID, new Integer[]{ 1 });
    }

    public static final Map<Integer, String> SEQUENCE_NUMBER_VALUES = new LinkedHashMap<>();
    static {
        SEQUENCE_NUMBER_VALUES.put(0, "pihcore.vaccination.sequence.doseZero");
        SEQUENCE_NUMBER_VALUES.put(1, "pihcore.vaccination.sequence.doseOne");
        SEQUENCE_NUMBER_VALUES.put(2, "pihcore.vaccination.sequence.doseTwo");
        SEQUENCE_NUMBER_VALUES.put(3, "pihcore.vaccination.sequence.doseThree");
        SEQUENCE_NUMBER_VALUES.put(11, "pihcore.vaccination.sequence.doseBoosterOne");
        SEQUENCE_NUMBER_VALUES.put(12, "pihcore.vaccination.sequence.doseBoosterTwo");
    }

    public void get(PageModel model, UiUtils ui,
                      @InjectBeans PatientDomainWrapper patientDomainWrapper,
                      @RequestParam(value = "patientId") Patient patient,
                      @SpringBean("conceptService") ConceptService conceptService,
                      @SpringBean("pihCoreService") PihCoreService pihCoreService) {

        patientDomainWrapper.setPatient(patient);
        model.addAttribute("patient", patientDomainWrapper);

        Map<Concept, Integer[]> vaccinationConcepts = new LinkedHashMap<>();
        for (String conceptUuid : VACCINATION_TABLE_CONCEPTS.keySet()) {
            vaccinationConcepts.put(conceptService.getConceptByUuid(conceptUuid), VACCINATION_TABLE_CONCEPTS.get(conceptUuid));
        }
        model.addAttribute("vaccinationConcepts", vaccinationConcepts);
        model.addAttribute("vaccinationSequenceNumbers", SEQUENCE_NUMBER_VALUES);

        List<Vaccination> vaccinations = pihCoreService.getVaccinations(patient);
        Map<String, Vaccination> vaccinationMap = new LinkedHashMap<>();
        for (Vaccination vaccination : vaccinations) {
            String key = vaccination.getVaccinationObs().getValueCoded().getUuid();
            if (vaccination.getSequenceNumberObs() != null) {
                key += "|" + vaccination.getSequenceNumberObs().getValueNumeric().intValue();
            }
            if (vaccinationMap.containsKey(key)) {
                key = vaccination.getVaccinationObs().getUuid();
            }
            vaccinationMap.put(key, vaccination);
        }
        model.addAttribute("vaccinations", vaccinationMap);
    }
}
