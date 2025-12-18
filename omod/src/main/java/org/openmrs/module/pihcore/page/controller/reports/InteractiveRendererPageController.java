package org.openmrs.module.pihcore.page.controller.reports;

import org.openmrs.module.coreapps.CoreAppsProperties;
import org.openmrs.module.pihcore.PihEmrConfigConstants;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.openmrs.module.reporting.report.definition.service.ReportDefinitionService;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.openmrs.ui.framework.page.PageModel;
import org.openmrs.util.ConfigUtil;
import org.springframework.web.bind.annotation.RequestParam;

public class InteractiveRendererPageController {

    public void get(@SpringBean ReportDefinitionService reportDefinitionService,
                    @SpringBean CoreAppsProperties coreAppsProperties,
                    @RequestParam("reportDefinition") String reportDefinitionUuid,
                    PageModel model) throws Exception {

        ReportDefinition reportDefinition = reportDefinitionService.getDefinitionByUuid(reportDefinitionUuid);
        if (reportDefinition == null) {
            throw new IllegalArgumentException("No reportDefinition with the given uuid");
        }
        model.addAttribute("reportDefinition", reportDefinition);
        model.addAttribute("dashboardUrlWithoutQueryParams", coreAppsProperties.getDashboardUrlWithoutQueryParams());
        model.addAttribute("privilegePatientDashboard", PihEmrConfigConstants.PRIVILEGE_APP_COREAPPS_PATIENT_DASHBOARD);
        model.addAttribute("dateFormat", ConfigUtil.getProperty("uiframework.formatter.JSdateFormat", "DD-MMM-YYYY"));
        model.addAttribute("dateTimeFormat", ConfigUtil.getProperty("uiframework.formatter.JSdateAndTimeFormat", "DD-MMM-YYYY HH:mm"));
    }

}
