package org.openmrs.module.pihcore.page.controller.visit;

import org.apache.commons.lang.StringUtils;
import org.openmrs.Encounter;
import org.openmrs.EncounterType;
import org.openmrs.Patient;
import org.openmrs.Visit;
import org.openmrs.VisitType;
import org.openmrs.api.VisitService;
import org.openmrs.api.context.Context;
import org.openmrs.module.appui.UiSessionContext;
import org.openmrs.module.coreapps.CoreAppsProperties;
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
                    @RequestParam(required = false, value = "encounterType") String encounterTypeUuid,
                    @RequestParam(required = false, value = "encounterId") Encounter encounterById,  // passed by the htmformentryui module after form submission creates new encounter (really should be "encounter" for consistency)
                    @RequestParam(required = false, value = "currentSection") String currentSection,
                    @RequestParam(required = false, value = "goToNext") String goToNext,
                    @RequestParam(required = false, value = "nextSection") String nextSection,
                    @RequestParam(required = false, value = "initialRouterState") String initialRouterState,    // hacky variable which page state to redirect to
					@SpringBean("coreAppsProperties") CoreAppsProperties coreAppsProperties,
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

        EncounterType encounterType = null;
        if (StringUtils.isNotBlank(encounterTypeUuid) ) {
            encounterType = Context.getEncounterService().getEncounterTypeByUuid(encounterTypeUuid);
        }

        patientDomainWrapper.setPatient(patient);
        model.addAttribute("patient", patientDomainWrapper);
        model.addAttribute("visit", visit);
        model.addAttribute("visitType", visitType);
        model.addAttribute("encounterType", encounterType);
        model.addAttribute("suppressActions", (suppressActions != null) ? suppressActions : false);
        model.addAttribute("encounter", encounter);
        model.addAttribute("locale", uiSessionContext.getLocale().getLanguage());
        model.addAttribute("country", config.getCountry().toString().toLowerCase());
        model.addAttribute("site", config.getSite().toString().toLowerCase());
        model.addAttribute("goToNext", goToNext);
        model.addAttribute("nextSection", nextSection);
        model.addAttribute("currentSection", currentSection);
        model.addAttribute("initialRouterState", initialRouterState);
		model.addAttribute("dashboardUrl", coreAppsProperties.getDashboardUrl());
    }

}
