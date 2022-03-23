package org.openmrs.module.pihcore.page.controller.patient;

import org.openmrs.Encounter;
import org.openmrs.Patient;
import org.openmrs.api.EncounterService;
import org.openmrs.module.emrapi.patient.PatientDomainWrapper;
import org.openmrs.parameter.EncounterSearchCriteriaBuilder;
import org.openmrs.ui.framework.UiUtils;
import org.openmrs.ui.framework.annotation.InjectBeans;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.openmrs.ui.framework.page.PageModel;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;

public class EncounterListPageController {

    public void get(PageModel model, UiUtils ui,
                      @InjectBeans PatientDomainWrapper patientDomainWrapper,
                      @RequestParam(value = "patientId") Patient patient,
                      @SpringBean("encounterService") EncounterService encounterService) throws IOException {

        // Get all of the encounters for the patient
        EncounterSearchCriteriaBuilder b = new EncounterSearchCriteriaBuilder();
        b.setPatient(patient).setIncludeVoided(false);
        List<Encounter> encounters = encounterService.getEncounters(b.createEncounterSearchCriteria());
        encounters.sort(Comparator.comparing(Encounter::getEncounterDatetime).reversed());

        patientDomainWrapper.setPatient(patient);
        model.addAttribute("patient", patientDomainWrapper);
        model.addAttribute("encounters", encounters);
    }
}
