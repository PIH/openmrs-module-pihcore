package org.openmrs.module.pihcore.page.controller.visit;

import org.openmrs.Patient;
import org.openmrs.Visit;
import org.openmrs.api.VisitService;
import org.openmrs.module.appui.UiSessionContext;
import org.openmrs.module.emrapi.patient.PatientDomainWrapper;
import org.openmrs.ui.framework.MissingRequiredParameterException;
import org.openmrs.ui.framework.annotation.InjectBeans;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.openmrs.ui.framework.page.PageModel;
import org.springframework.web.bind.annotation.RequestParam;

public class VisitPageController {

    public void get(@SpringBean("visitService") VisitService visitService,
                    @InjectBeans PatientDomainWrapper patientDomainWrapper,
                    @RequestParam(required = false, value = "patient") Patient patient,
                    @RequestParam(required = false, value = "visit") Visit visit,
                    UiSessionContext uiSessionContext,
                    PageModel model) {

        // fwiw, you are allowed to have a patient without a visit, but only if viewing the "visitList" view
        if (patient == null) {
            if (visit != null) {
                patient = visit.getPatient();
            }
            else {
                throw new MissingRequiredParameterException("patient or visit is required");
            }
        }

        patientDomainWrapper.setPatient(patient);
        model.addAttribute("patient", patientDomainWrapper);
        model.addAttribute("visit", visit);
        model.addAttribute("locale", uiSessionContext.getLocale());
    }

}
