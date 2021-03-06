package org.openmrs.module.pihcore.page.controller.visit;

import org.openmrs.Encounter;
import org.openmrs.Patient;
import org.openmrs.Visit;
import org.openmrs.VisitType;
import org.openmrs.api.VisitService;
import org.openmrs.module.appui.UiSessionContext;
import org.openmrs.module.emrapi.patient.PatientDomainWrapper;
import org.openmrs.module.pihcore.config.Config;
import org.openmrs.ui.framework.MissingRequiredParameterException;
import org.openmrs.ui.framework.annotation.InjectBeans;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.openmrs.ui.framework.page.PageModel;
import org.springframework.web.bind.annotation.RequestParam;

public class VisitPageController {

    public void get(@SpringBean("visitService") VisitService visitService,
                    @SpringBean("config") Config config,
                    @InjectBeans PatientDomainWrapper patientDomainWrapper,
                    @RequestParam(required = false, value = "patient") Patient patient,
                    @RequestParam(required = false, value = "visit") Visit visit,
                    @RequestParam(required = false, value = "visitType") VisitType visitType,
                    @RequestParam(value="suppressActions", required = false) Boolean suppressActions,
                    @RequestParam(required = false, value = "encounter") Encounter enc,
                    @RequestParam(required = false, value = "encounterId") Encounter encounterById,  // passed by the htmformentryui module after form submission creates new encounter (really should be "encounter" for consistency)
                    @RequestParam(required = false, value = "goToNextSection") String goToNextSection,
                    UiSessionContext uiSessionContext,
                    PageModel model) {

        // there are two params that could pass in an encounter, test which one, if any, was used to pass in a value
        Encounter encounter = enc != null ? enc : (encounterById != null ? encounterById : null);

        // fwiw, you are allowed to have a patient without a visit, but only if viewing the "visitList" view
        if (patient == null) {
            if (visit != null) {
                patient = visit.getPatient();
            }
            else if (encounter != null) {
                patient = encounter.getPatient();
            }
            else {
                throw new MissingRequiredParameterException("patient or visit or encounter is required");
            }
        }

        // see if we can get a visit from the encounter
        if (visit == null && encounter != null) {
            visit = encounter.getVisit();
        }

        patientDomainWrapper.setPatient(patient);
        model.addAttribute("patient", patientDomainWrapper);
        model.addAttribute("visit", visit);
        model.addAttribute("visitType", visitType);
        model.addAttribute("suppressActions", (suppressActions != null) ? suppressActions : false);
        model.addAttribute("encounter", encounter);
        model.addAttribute("locale", uiSessionContext.getLocale());
        model.addAttribute("country", config.getCountry().toString().toLowerCase());
        model.addAttribute("site", config.getSite().toString().toLowerCase());
        model.addAttribute("goToNextSection", goToNextSection);
    }

}
