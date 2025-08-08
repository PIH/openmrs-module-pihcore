package org.openmrs.module.pihcore.web;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.report.Report;
import org.openmrs.module.reporting.report.ReportRequest;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.openmrs.module.reporting.report.definition.service.ReportDefinitionService;
import org.openmrs.module.reporting.report.renderer.CsvReportRenderer;
import org.openmrs.module.reporting.report.renderer.ExcelTemplateRenderer;
import org.openmrs.module.reporting.report.renderer.RenderingMode;
import org.openmrs.module.reporting.report.renderer.ReportRenderer;
import org.openmrs.module.reporting.report.renderer.XlsReportRenderer;
import org.openmrs.module.reporting.report.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Provides the ability to run and download a report on demand via a url
 */
@Controller
public class DownloadReportController {

    @Autowired
    ReportService reportService;

    @Autowired
    ReportDefinitionService reportDefinitionService;

    @RequestMapping(value = "/module/pihcore/downloadReport.form", method = RequestMethod.GET)
    public void downloadReport(
            HttpServletRequest request,
            HttpServletResponse response,
            @RequestParam("reportUuid") String reportUuid) throws Exception {

        ReportDefinition reportDefinition = reportDefinitionService.getDefinitionByUuid(reportUuid);
        if (reportDefinition == null) {
            throw new IllegalArgumentException("Report definition with uuid " + reportUuid + " not found");
        }

        // For now, assume all reports have either a single Excel or CSV renderer, and use that
        RenderingMode excelMode = null;
        RenderingMode csvMode = null;
        for (RenderingMode renderingMode : reportService.getRenderingModes(reportDefinition)) {
            ReportRenderer renderer = renderingMode.getRenderer();
            if (renderer instanceof ExcelTemplateRenderer || renderer instanceof XlsReportRenderer) {
                excelMode = renderingMode;
            }
            else if (renderer instanceof CsvReportRenderer) {
                csvMode = renderingMode;
            }
        }
        RenderingMode renderingMode = excelMode != null ? excelMode : csvMode;
        if (renderingMode == null) {
            throw new IllegalArgumentException("Report definition must have either an Excel, Xls, or CSV renderer");
        }

        Map<String, Object> params = new HashMap<>();
        for (Parameter parameter : reportDefinition.getParameters()) {
            Class<?> type = parameter.getType();
            String name = parameter.getName();;
            String paramValueStr = request.getParameter(name);
            if (StringUtils.isNotBlank(paramValueStr)) {
                if (Date.class.isAssignableFrom(type)) {
                    params.put(name, new SimpleDateFormat(("yyyy-MM-dd")).parse(paramValueStr));
                }
                else if (Integer.class.isAssignableFrom(type)) {
                    params.put(name, Integer.parseInt(paramValueStr));
                }
                else {
                    // TODO: Add more conditions here as needs arise
                    params.put(name, paramValueStr);
                }
            }
        }

        ReportRequest reportRequest = new ReportRequest();
        reportRequest.setReportDefinition(new Mapped<>(reportDefinition, params));
        reportRequest.setRenderingMode(renderingMode);
        Report report = reportService.runReport(reportRequest);
        String filename = renderingMode.getRenderer().getFilename(reportRequest);

        response.setContentType(report.getOutputContentType());
        response.setHeader("Content-Disposition", "attachment; filename=" + filename);
        response.setHeader("Pragma", "no-cache");
        IOUtils.write(report.getRenderedOutput(), response.getOutputStream());
    }
}
