package org.openmrs.module.pihcore.page.controller.admin;

import org.openmrs.api.VisitService;
import org.openmrs.module.emrapi.adt.AdtService;
import org.openmrs.module.emrapi.adt.InpatientRequest;
import org.openmrs.module.pihcore.service.PihCoreService;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.openmrs.ui.framework.page.PageModel;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

public class StaleAdmissionRequestsPageController {

    static final int DEFAULT_STALE_ADMISSION_REQUESTS_THRESHOLD_IN_DAYS = 7;
    static final int DEFAULT_MOST_RECENT_ENCOUNTER_THRESHOLD_IN_DAYS = 2;

    public void get(PageModel model) {
        model.put("staleAdmissionRequestsThresholdInDays", DEFAULT_STALE_ADMISSION_REQUESTS_THRESHOLD_IN_DAYS);
        model.put("mostRecentEncounterThresholdInDays", DEFAULT_MOST_RECENT_ENCOUNTER_THRESHOLD_IN_DAYS);
        model.put("staleAdmissionRequests", null);

    }

    public void post(PageModel model,
                     @RequestParam("staleAdmissionRequestsThresholdInDays") int staleAdmissionRequestsThresholdInDays,
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

        List<InpatientRequest> staleAdmissionRequests = pihCoreService.getStaleAdmissionRequests(staleAdmissionRequestsThresholdInDays, mostRecentEncounterThresholdInDays);
        model.put("staleAdmissionRequestsThresholdInDays", staleAdmissionRequestsThresholdInDays);
        model.put("mostRecentEncounterThresholdInDays", mostRecentEncounterThresholdInDays);
        model.addAttribute("staleAdmissionRequests", staleAdmissionRequests);

    }
}
