package org.openmrs.module.pihcore.page.controller.checkin;

import org.openmrs.Encounter;
import org.openmrs.Patient;
import org.openmrs.module.appui.UiSessionContext;
import org.openmrs.module.emrapi.EmrApiProperties;
import org.openmrs.module.emrapi.adt.AdtService;
import org.openmrs.module.emrapi.patient.PatientDomainWrapper;
import org.openmrs.module.emrapi.visit.VisitDomainWrapper;
import org.openmrs.ui.framework.SimpleObject;
import org.openmrs.ui.framework.UiUtils;
import org.openmrs.ui.framework.annotation.InjectBeans;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.openmrs.ui.framework.page.PageModel;
import org.openmrs.ui.framework.page.Redirect;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * This is the "entry" page for our cyclical check-in app; current workflow is as follows:
 *
 * 1) If patient does not have an existing visit, redirect directly to registration summary page
 * 2) If the patient DOES have an active visit, load checkin.gsp, which provides user with three
 *      options:
 *          a) Close current visit and continue
 *          b) Continue
 *          c) Go Back
 *    Options B & C simply link to the registration summary back or the find patient page, respectively
 *    Option A reloads this "entry" page with a request param "closeVisit" set to true.  This page will close
 *    the active vist, and then redirect to the registration summary page
 */
public class CheckinPageController {

    public Object controller(@RequestParam("patientId") Patient patient,
                               @RequestParam(value = "closeVisit", required = false) Boolean closeVisit,
                               UiUtils ui,
                               UiSessionContext uiSessionContext,
                               PageModel model,
                               EmrApiProperties emrApiProperties,
                               @SpringBean AdtService adtService,
                               @InjectBeans PatientDomainWrapper patientDomainWrapper) {

        patientDomainWrapper.setPatient(patient);
        VisitDomainWrapper activeVisit = patientDomainWrapper.getActiveVisit(uiSessionContext.getSessionLocation());

        if (closeVisit != null && closeVisit && activeVisit != null) {
            adtService.closeAndSaveVisit(activeVisit.getVisit());
            activeVisit = null;
        }

        // if there is no active visit, redirect directly to the form
        if (activeVisit == null) {
            return new Redirect("/registrationapp/registrationSummary.page?patientId=" + patient.getId() +
                    "&breadcrumbOverrideProvider=coreapps&breadcrumbOverridePage=findpatient%2FfindPatient&breadcrumbOverrideApp=mirebalais.liveCheckin&breadcrumbOverrideLabel=mirebalais.app.patientRegistration.checkin.label");
        }
        // prompt the user to see if they want close the existing visit
        else {

            List<Encounter> existingEncounters = new ArrayList<Encounter>();
            if (activeVisit != null) {
                for (Encounter encounter : activeVisit.getVisit().getEncounters()) {
                    if (!encounter.isVoided()
                            && emrApiProperties.getCheckInEncounterType().equals(encounter.getEncounterType())) {
                        existingEncounters.add(encounter);
                    }
                }
            }

            SimpleObject appHomepageBreadcrumb = SimpleObject.create("label", ui.escapeJs(ui.message("mirebalais.checkin.title"))) ;
            SimpleObject patientPageBreadcrumb = SimpleObject.create("label", ui.escapeJs(patient.getFamilyName()) + ", " + ui.escapeJs(patient.getGivenName()), "link", ui.thisUrlWithContextPath());
            model.addAttribute("breadcrumbOverride", ui.toJson(Arrays.asList(appHomepageBreadcrumb, patientPageBreadcrumb)));
            model.addAttribute("activeVisit", activeVisit);
            model.addAttribute("existingEncounters", existingEncounters);
            model.addAttribute("patient", patientDomainWrapper);
            model.addAttribute("appName", "mirebalais.liveCheckin");

            return null;
        }

    }

}
