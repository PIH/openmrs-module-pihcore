package org.openmrs.module.pihcore.page.controller.admin;

import org.openmrs.api.VisitService;
import org.openmrs.messagesource.MessageSourceService;
import org.openmrs.module.emr.EmrConstants;
import org.openmrs.module.emrapi.adt.AdtService;
import org.openmrs.module.emrapi.adt.InpatientAdmission;
import org.openmrs.module.emrapi.domainwrapper.DomainWrapperFactory;
import org.openmrs.module.pihcore.service.PihCoreService;
import org.openmrs.ui.framework.SimpleObject;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.openmrs.ui.framework.page.PageModel;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
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
                     HttpServletRequest request,
                     @RequestParam("staleInpatientAdmissionThresholdInDays") int staleInpatientAdmissionThresholdInDays,
                     @RequestParam("mostRecentEncounterThresholdInDays") int mostRecentEncounterThresholdInDays,
                     @RequestParam(value = "visitsToClose", required = false) List<Integer> visitsToClose,
                     @SpringBean("adtService") AdtService adtService,
                     @SpringBean("pihCoreService") PihCoreService pihCoreService,
                     @SpringBean("visitService") VisitService visitService,
                     @SpringBean("messageSourceService") MessageSourceService messageSourceService,
                     @SpringBean("domainWrapperFactory") DomainWrapperFactory domainWrapperFactory
    ) {

        if (visitsToClose != null && !visitsToClose.isEmpty()) {
            for (Integer visitId : visitsToClose) {
                adtService.closeAndSaveVisit(visitService.getVisit(visitId));
            }
            request.getSession().setAttribute(EmrConstants.SESSION_ATTRIBUTE_INFO_MESSAGE,
                    messageSourceService.getMessage("pihcore.admin.visitsClosed"));
            request.getSession().setAttribute(EmrConstants.SESSION_ATTRIBUTE_TOAST_MESSAGE, "true");
        }

        List<InpatientAdmission> staleInpatientAdmissions = pihCoreService.getStaleInpatientAdmissions(staleInpatientAdmissionThresholdInDays, mostRecentEncounterThresholdInDays);

        List<SimpleObject> results = new ArrayList<>();

        // we build this ourselves so we can wrap the patient in a domain wrapper
        for (InpatientAdmission admission : staleInpatientAdmissions) {
            SimpleObject admissionObject = new SimpleObject();
            admissionObject.put("patient", domainWrapperFactory.newPatientDomainWrapper(admission.getPatient()));
            admissionObject.put("visit", admission.getVisit());
            admissionObject.put("firstAdmissionOrTransferEncounter", admission.getFirstAdmissionOrTransferEncounter());
            admissionObject.put("latestEncounter", admission.getLatestEncounter());
            results.add(admissionObject);
        }

        model.addAttribute("staleInpatientAdmissions", results);
        model.put("staleInpatientAdmissionThresholdInDays", staleInpatientAdmissionThresholdInDays);
        model.put("mostRecentEncounterThresholdInDays", mostRecentEncounterThresholdInDays);

    }
}