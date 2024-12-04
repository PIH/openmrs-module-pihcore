package org.openmrs.module.pihcore.page.controller.admin;

import org.openmrs.api.VisitService;
import org.openmrs.module.emrapi.adt.AdtService;
import org.openmrs.module.emrapi.adt.InpatientAdmission;
import org.openmrs.module.pihcore.service.PihCoreService;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.openmrs.ui.framework.page.PageModel;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

public class StaleInpatientAdmissionsPageController {

    static final int DEFAULT_STALE_INPATIENT_ADMISSIONS_THRESHOLD_IN_DAYS = 28;
    static final int DEFAULT_MOST_RECENT_ENCOUNTER_THRESHOLD_IN_DAYS = 5;

    public void get(PageModel model) {
        model.put("staleInpatientAdmissionThresholdInDays", DEFAULT_STALE_INPATIENT_ADMISSIONS_THRESHOLD_IN_DAYS);
        model.put("mostRecentEncounterThresholdInDays", DEFAULT_MOST_RECENT_ENCOUNTER_THRESHOLD_IN_DAYS);
        model.put("staleInpatientAdmissions", null);
    }


    public void post(PageModel model,
                     @RequestParam("staleInpatientAdmissionThresholdInDays") int staleInpatientAdmissionThresholdInDays,
                     @RequestParam("mostRecentEncounterThresholdInDays") int mostRecentEncounterThresholdInDays,
                     @RequestParam(value = "visitsToClose", required = false) List<Integer> visitsToClose,
                     @SpringBean("adtService") AdtService adtService,
                     @SpringBean("pihCoreService") PihCoreService pihCoreService,
                     @SpringBean("visitService") VisitService visitService) {

        if (visitsToClose != null) {
            for (Integer visitId : visitsToClose) {
                adtService.closeAndSaveVisit(visitService.getVisit(visitId));
            }
        }

        List<InpatientAdmission> staleInpatientAdmissions = pihCoreService.getStaleInpatientAdmissions(staleInpatientAdmissionThresholdInDays, mostRecentEncounterThresholdInDays);
        model.put("staleInpatientAdmissionThresholdInDays", staleInpatientAdmissionThresholdInDays);
        model.put("mostRecentEncounterThresholdInDays", mostRecentEncounterThresholdInDays);
        model.addAttribute("staleInpatientAdmissions", staleInpatientAdmissions);
    }
}