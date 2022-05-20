package org.openmrs.module.pihcore.page.controller.reports;

import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.openmrs.module.reporting.report.definition.service.ReportDefinitionService;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.openmrs.ui.framework.page.PageModel;
import org.springframework.web.bind.annotation.RequestParam;

public class PowerbiPageController {

    public void get(PageModel model,
                    @SpringBean ReportDefinitionService reportDefinitionService,
                    @RequestParam("reportDefinition") String reportDefinitionUuid) throws Exception {

        ReportDefinition reportDefinition = reportDefinitionService.getDefinitionByUuid(reportDefinitionUuid);
        if (reportDefinition == null) {
            throw new IllegalArgumentException("No reportDefinition with the given uuid");
        }

        model.addAttribute("reportDefinition", reportDefinition);

        Parameter idParam = reportDefinition.getParameter("pbiReportId");
        if (idParam == null) {
            throw new IllegalArgumentException("The given reportDefinition does not contain a 'pbiReportId' parameter");
        }
        model.addAttribute("pbiReportId", idParam.getDefaultValue());
    }

}
