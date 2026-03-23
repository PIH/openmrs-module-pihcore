package org.openmrs.module.pihcore.page.controller.reports;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Location;
import org.openmrs.api.context.Context;
import org.openmrs.api.db.hibernate.DbSessionFactory;
import org.openmrs.module.appui.UiSessionContext;
import org.openmrs.module.coreapps.CoreAppsConstants;
import org.openmrs.module.coreapps.CoreAppsProperties;
import org.openmrs.module.emrapi.adt.AdtService;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.evaluation.querybuilder.SqlQueryBuilder;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.openmrs.ui.framework.page.PageModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;

@Controller
public class ActiveVisitsListPageController {

    private final Log log = LogFactory.getLog(getClass());

    public void get(PageModel model,
                    @SpringBean AdtService adtService,
                    @SpringBean CoreAppsProperties coreAppsProperties,
                    @RequestParam(value = "locationId", required = false) Integer locationId,
                    UiSessionContext uiSessionContext
                    ) throws EvaluationException {

        EvaluationContext context = new EvaluationContext();

        // use the closest visit location associated with the session location if the location param is not specified
        if (locationId == null) {
            Location location = adtService.getLocationThatSupportsVisits(uiSessionContext.getSessionLocation());
            if (location != null) {
                locationId = location.getId();
            }
        }

        SqlQueryBuilder q = new SqlQueryBuilder();
        q.append("select distinct patient_id from visit");
        q.append(" where date_stopped is null and voided = 0");
        if (locationId != null) {
            q.append(" and location_id = :location").addParameter("location", locationId);
        }

        DbSessionFactory dbSessionFactory = Context.getRegisteredComponents(DbSessionFactory.class).get(0);
        List<Object[]> resultSet = q.evaluateToList(dbSessionFactory, context);
        List<Integer> activeVisitsCohort = new ArrayList<Integer>();
        if (resultSet != null && resultSet.size() > 0) {
            for (Object[] activeVisit : resultSet) {
                activeVisitsCohort.add((Integer) activeVisit[0]);
            }
        }

        model.addAttribute("locale", uiSessionContext.getLocale());
        model.addAttribute("lastLocation", locationId);
        model.addAttribute("activeVisitsCohort", activeVisitsCohort);
        model.addAttribute("dashboardUrl", coreAppsProperties.getDashboardUrl());
        model.put("privilegePatientDashboard", CoreAppsConstants.PRIVILEGE_PATIENT_DASHBOARD);  // used to determine if we display links to patient dashboard)
    }
}
