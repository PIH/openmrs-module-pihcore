package org.openmrs.module.pihcore.page.controller.reports;


import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Cohort;
import org.openmrs.Location;
import org.openmrs.api.context.Context;
import org.openmrs.module.appui.UiSessionContext;
import org.openmrs.module.coreapps.CoreAppsConstants;
import org.openmrs.module.coreapps.CoreAppsProperties;
import org.openmrs.module.pihcore.reporting.cohort.definition.ActiveVisitsWithEncountersCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.service.CohortDefinitionService;
import org.openmrs.module.reporting.common.TimeQualifier;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.openmrs.ui.framework.page.PageModel;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Collections;

@Controller
public class ActiveVisitsListPageController {

    private final Log log = LogFactory.getLog(getClass());

    public void get(PageModel model,
                    @SpringBean CoreAppsProperties coreAppsProperties,
                    @RequestParam(value = "location", required = false) Integer location,
                    @RequestParam(value = "timeQualifier", required = false) String timeQualifier,
                    UiSessionContext uiSessionContext
                    ) throws EvaluationException {


        EvaluationContext context = new EvaluationContext();
        ActiveVisitsWithEncountersCohortDefinition visitCohortDefinition = new ActiveVisitsWithEncountersCohortDefinition();
        visitCohortDefinition.setActive(true);
        if (location != null) {
            visitCohortDefinition.setLocationList(Collections.singletonList(new Location(location)));
            TimeQualifier qualifier = null;
            if (StringUtils.isNotBlank(timeQualifier)) {
                qualifier = TimeQualifier.valueOf(timeQualifier);
            }
            if (qualifier == null ) {
                qualifier = TimeQualifier.LAST;
            }
            visitCohortDefinition.setWhichEncounter(qualifier);
        }

        Cohort visitCohort = (Context.getService(CohortDefinitionService.class)).evaluate(visitCohortDefinition, context);

        model.addAttribute("locale", uiSessionContext.getLocale());
        model.addAttribute("lastLocation", location);
        model.addAttribute("activeVisitsCohort", visitCohort.getMemberIds());
        model.addAttribute("dashboardUrl", coreAppsProperties.getDashboardUrl());
        model.put("privilegePatientDashboard", CoreAppsConstants.PRIVILEGE_PATIENT_DASHBOARD);  // used to determine if we display links to patient dashboard)
    }
}
