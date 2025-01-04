package org.openmrs.module.pihcore.page.controller.patient;

import org.openmrs.Concept;
import org.openmrs.Patient;
import org.openmrs.api.ConceptService;
import org.openmrs.module.emrapi.patient.PatientDomainWrapper;
import org.openmrs.module.pihcore.PihCoreConstants;
import org.openmrs.module.pihcore.model.Immunization;
import org.openmrs.module.pihcore.service.PihCoreService;
import org.openmrs.ui.framework.UiUtils;
import org.openmrs.ui.framework.annotation.InjectBeans;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.openmrs.ui.framework.page.PageModel;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ImmunizationsPageController {

    public static final Map<String, Integer[]> IMMUNIZATION_TABLE_CONCEPTS = new LinkedHashMap<>();

    static {
        IMMUNIZATION_TABLE_CONCEPTS.put(PihCoreConstants.BCG_VACCINATION_CONCEPT_UUID, new Integer[]{ 1 });
        IMMUNIZATION_TABLE_CONCEPTS.put(PihCoreConstants.COVID_VACCINATION_CONCEPT_UUID, new Integer[]{ 1, 2, 3 });
        IMMUNIZATION_TABLE_CONCEPTS.put(PihCoreConstants.DIPTHERIA_TETANUS_VACCINATION_CONCEPT_UUID, new Integer[]{ 0, 1, 2, 3, 11, 12 });
        IMMUNIZATION_TABLE_CONCEPTS.put(PihCoreConstants.HEP_B_VACCINATION_CONCEPT_UUID, new Integer[]{ 0, 1, 2, 3 });
        IMMUNIZATION_TABLE_CONCEPTS.put(PihCoreConstants.FLU_VACCINATION_CONCEPT_UUID, new Integer[]{ 1, 2, 3, 11, 12 });
        IMMUNIZATION_TABLE_CONCEPTS.put(PihCoreConstants.MENINGO_VACCINATION_CONCEPT_UUID, new Integer[]{ 1, 2 });
        IMMUNIZATION_TABLE_CONCEPTS.put(PihCoreConstants.PENTAVALENT_VACCINATION_CONCEPT_UUID, new Integer[]{ 1, 2, 3 });
        IMMUNIZATION_TABLE_CONCEPTS.put(PihCoreConstants.PNEUMOCOCCAL_VACCINATION_CONCEPT_UUID, new Integer[]{ 1, 2, 3 });
        IMMUNIZATION_TABLE_CONCEPTS.put(PihCoreConstants.POLIO_VACCINATION_CONCEPT_UUID, new Integer[]{ 0, 1, 2, 3, 11, 12 });
        IMMUNIZATION_TABLE_CONCEPTS.put(PihCoreConstants.ROTAVIRUS_VACCINATION_CONCEPT_UUID, new Integer[]{ 1, 2 });
        IMMUNIZATION_TABLE_CONCEPTS.put(PihCoreConstants.MEASLES_RUBELLA_VACCINATION_CONCEPT_UUID, new Integer[]{ 1 });
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

        Map<Concept, Integer[]> immunizationConcepts = new LinkedHashMap<>();
        for (String conceptUuid : IMMUNIZATION_TABLE_CONCEPTS.keySet()) {
            immunizationConcepts.put(conceptService.getConceptByUuid(conceptUuid), IMMUNIZATION_TABLE_CONCEPTS.get(conceptUuid));
        }
        model.addAttribute("immunizationConcepts", immunizationConcepts);
        model.addAttribute("immunizationSequenceNumbers", SEQUENCE_NUMBER_VALUES);

        List<Immunization> immunizations = pihCoreService.getImmunizations(patient);
        Map<String, Immunization> immunizationMap = new LinkedHashMap<>();
        for (Immunization immunization : immunizations) {
            String key = immunization.getImmunizationObs().getValueCoded().getUuid();
            if (immunization.getSequenceNumberObs() != null) {
                key += "|" + immunization.getSequenceNumberObs().getValueNumeric().intValue();
            }
            if (immunizationMap.containsKey(key)) {
                key = immunization.getImmunizationObs().getUuid();
            }
            immunizationMap.put(key, immunization);
        }
        model.addAttribute("immunizations", immunizationMap);
    }
}
