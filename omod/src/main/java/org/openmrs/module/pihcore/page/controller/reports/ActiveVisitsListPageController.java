package org.openmrs.module.pihcore.page.controller.reports;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.context.Context;
import org.openmrs.api.db.hibernate.DbSessionFactory;
import org.openmrs.module.appui.UiSessionContext;
import org.openmrs.module.coreapps.CoreAppsConstants;
import org.openmrs.module.coreapps.CoreAppsProperties;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.evaluation.querybuilder.SqlQueryBuilder;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.openmrs.ui.framework.page.PageModel;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;

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

        SqlQueryBuilder q = new SqlQueryBuilder();
        q.append("select e.patient_id, e.encounter_datetime");
        q.append(" from encounter e");
        q.append(" inner join (");
        q.append(" select patient_id, max(encounter_datetime) lastSeen ");
        q.append(" from encounter");
        q.append(" where voided = 0 and visit_id in ( select visit_id from visit where date_stopped is null and voided = 0)");
        q.append(" group by patient_id");
        q.append(" ) b on e.patient_id = b.patient_id and  e.encounter_datetime = b.lastSeen");
        q.append(" where e.voided = 0 and e.visit_id in");
        q.append(" ( select visit_id from visit where date_stopped is null and voided = 0)");
        if (location != null) {
            q.append(" and e.location_id = :location").addParameter("location", location);
        }
        q.append(" group by e.patient_id");
        q.append(" order by e.encounter_datetime desc");

        DbSessionFactory dbSessionFactory = Context.getRegisteredComponents(DbSessionFactory.class).get(0);
        List<Object[]> resultSet = q.evaluateToList(dbSessionFactory, context);
        List<Integer> activeVisitsCohort = new ArrayList<Integer>();
        if (resultSet != null && resultSet.size() > 0) {
            for (Object[] activeVisit : resultSet) {
                activeVisitsCohort.add((Integer) activeVisit[0]);
            }
        }

        model.addAttribute("locale", uiSessionContext.getLocale());
        model.addAttribute("lastLocation", location);
        model.addAttribute("activeVisitsCohort", activeVisitsCohort);
        model.addAttribute("dashboardUrl", coreAppsProperties.getDashboardUrl());
        model.put("privilegePatientDashboard", CoreAppsConstants.PRIVILEGE_PATIENT_DASHBOARD);  // used to determine if we display links to patient dashboard)
    }
}
