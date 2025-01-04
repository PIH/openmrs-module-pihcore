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

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ImmunizationsPageController {

    public static final String[] IMMUNIZATION_TABLE_CONCEPTS = {
            PihCoreConstants.BCG_VACCINATION_CONCEPT_UUID,
            PihCoreConstants.COVID_VACCINATION_CONCEPT_UUID,
            PihCoreConstants.DIPTHERIA_TETANUS_VACCINATION_CONCEPT_UUID,
            PihCoreConstants.HEP_B_VACCINATION_CONCEPT_UUID,
            PihCoreConstants.FLU_VACCINATION_CONCEPT_UUID,
            PihCoreConstants.MENINGO_VACCINATION_CONCEPT_UUID,
            PihCoreConstants.PENTAVALENT_VACCINATION_CONCEPT_UUID,
            PihCoreConstants.PNEUMOCOCCAL_VACCINATION_CONCEPT_UUID,
            PihCoreConstants.POLIO_VACCINATION_CONCEPT_UUID,
            PihCoreConstants.ROTAVIRUS_VACCINATION_CONCEPT_UUID,
            PihCoreConstants.MEASLES_RUBELLA_VACCINATION_CONCEPT_UUID
    };

    public static final Map<String, Integer> SEQUENCE_NUMBER_VALUES = new LinkedHashMap<>();
    static {
        SEQUENCE_NUMBER_VALUES.put("pihcore.vaccination.sequence.doseZero", 0);
        SEQUENCE_NUMBER_VALUES.put("pihcore.vaccination.sequence.doseOne", 1);
        SEQUENCE_NUMBER_VALUES.put("pihcore.vaccination.sequence.doseTwo", 2);
        SEQUENCE_NUMBER_VALUES.put("pihcore.vaccination.sequence.doseThree", 3);
        SEQUENCE_NUMBER_VALUES.put("pihcore.vaccination.sequence.doseBoosterOne", 11);
        SEQUENCE_NUMBER_VALUES.put("pihcore.vaccination.sequence.doseBoosterTwo", 12);
    }

    public void get(PageModel model, UiUtils ui,
                      @InjectBeans PatientDomainWrapper patientDomainWrapper,
                      @RequestParam(value = "patientId") Patient patient,
                      @SpringBean("conceptService") ConceptService conceptService,
                      @SpringBean("pihCoreService") PihCoreService pihCoreService) {



        patientDomainWrapper.setPatient(patient);
        model.addAttribute("patient", patientDomainWrapper);

        List<Concept> immunizationConcepts = new ArrayList<>();
        for (String conceptUuid : IMMUNIZATION_TABLE_CONCEPTS) {
            immunizationConcepts.add(conceptService.getConceptByUuid(conceptUuid));
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
