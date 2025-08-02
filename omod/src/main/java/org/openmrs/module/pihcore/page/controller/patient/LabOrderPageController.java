package org.openmrs.module.pihcore.page.controller.patient;

import org.openmrs.Concept;
import org.openmrs.Patient;
import org.openmrs.api.ConceptService;
import org.openmrs.module.emrapi.patient.PatientDomainWrapper;
import org.openmrs.module.pihcore.PihUiUtils;
import org.openmrs.ui.framework.UiUtils;
import org.openmrs.ui.framework.annotation.InjectBeans;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.openmrs.ui.framework.page.PageModel;
import org.openmrs.util.ConfigUtil;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;

public class LabOrderPageController {

    public void get(PageModel model, UiUtils ui,
                      @InjectBeans PatientDomainWrapper patientDomainWrapper,
                      @RequestParam(value = "patient") Patient patient,
                      @SpringBean("conceptService") ConceptService conceptService) throws IOException {

        String labSetProp = ConfigUtil.getGlobalProperty("orderentryowa.labOrderablesConceptSet");
        Concept labSet = conceptService.getConceptByReference(labSetProp);

        patientDomainWrapper.setPatient(patient);
        model.addAttribute("patient", patientDomainWrapper);
        model.addAttribute("labSet", labSet);
        model.addAttribute("pihui", new PihUiUtils());
    }

    public void post() {



        String encounterTypeProp = ConfigUtil.getGlobalProperty("orderentryowa.encounterType");
        String encounterRoleProp = ConfigUtil.getGlobalProperty("orderentryowa.encounterRole");
        String dateAndTimeFormatProp = ConfigUtil.getGlobalProperty("orderentryowa.dateAndTimeFormat");
        String autoExpireDaysProp = ConfigUtil.getGlobalProperty("orderentryowa.labOrderAutoExpireTimeInDays");


    }
}
