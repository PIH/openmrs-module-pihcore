/*
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */

package org.openmrs.module.pihcore.page.controller.reports;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Provider;
import org.openmrs.api.context.Context;
import org.openmrs.module.coreapps.CoreAppsProperties;
import org.openmrs.module.reporting.common.DateUtil;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.report.ReportData;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.openmrs.module.reporting.report.definition.service.ReportDefinitionService;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.openmrs.ui.framework.page.PageModel;
import org.openmrs.util.OpenmrsUtil;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 */
public class NonCodedDiagnosesPageController {
    private final Log log = LogFactory.getLog(getClass());

    public static final String NON_CODED_DIAGNOSES_REPORT_DEFINITION_UUID = "3737be52-2265-11e3-818c-c7ea4184d59e";

    public void get(@SpringBean ReportDefinitionService reportDefinitionService,
                    @RequestParam(required = false, value = "fromDate") Date fromDate,
                    @RequestParam(required = false, value = "toDate") Date toDate,
                    PageModel model) throws EvaluationException, IOException {

        if (fromDate == null) {
            fromDate = DateUtils.addDays(new Date(), -21);
        }
        if (toDate == null) {
            toDate = new Date();
        }
        fromDate = DateUtil.getStartOfDay(fromDate);
        toDate = DateUtil.getEndOfDay(toDate);

        ReportDefinition reportDefinition = reportDefinitionService.getDefinitionByUuid(NON_CODED_DIAGNOSES_REPORT_DEFINITION_UUID);

        model.addAttribute("nonCodedRows", null);
        model.addAttribute("providers", getAllProviders());
        model.addAttribute("reportDefinition", reportDefinition);
        model.addAttribute("fromDate", fromDate);
        model.addAttribute("toDate", toDate);
        model.addAttribute("nonCoded", "");
        model.addAttribute("providerId", null);
    }

    public void post(@SpringBean ReportDefinitionService reportDefinitionService,
                     @SpringBean CoreAppsProperties coreAppsProperties,
                     @RequestParam(required = false, value = "fromDate") Date fromDate,
                     @RequestParam(required = false, value = "toDate") Date toDate,
                     @RequestParam(required = false, value = "nonCoded") String nonCoded,
                     @RequestParam(required = false, value = "provider") Provider provider,
                     PageModel model) throws EvaluationException, IOException {


        if (fromDate == null) {
            fromDate = DateUtils.addDays(new Date(), -21);
        }
        if (toDate == null) {
            toDate = new Date();
        }
        fromDate = DateUtil.getStartOfDay(fromDate);
        toDate = DateUtil.getEndOfDay(toDate);

        Map<String, Object> params = new HashMap<String, Object>();
        params.put("fromDate", fromDate);
        params.put("toDate", toDate);
        if(StringUtils.isBlank(nonCoded)){
            nonCoded = "";
        }
        params.put("nonCoded", nonCoded);
        model.addAttribute("nonCoded", nonCoded);

        Integer providerId = null;
        if ( provider != null ){
            params.put("provider", provider);
            providerId =  provider.getId();
        }else {
            params.put("provider", null);
        }
        model.addAttribute("providerId", providerId);

        EvaluationContext context = new EvaluationContext();
        context.setParameterValues(params);

        ReportDefinition reportDefinition = reportDefinitionService.getDefinitionByUuid(NON_CODED_DIAGNOSES_REPORT_DEFINITION_UUID);
        ReportData reportData = reportDefinitionService.evaluate(reportDefinition, context);
        model.addAttribute("nonCodedRows", reportData.getDataSets().get("data"));
        model.addAttribute("reportDefinition", reportDefinition);
        model.addAttribute("fromDate", fromDate);
        model.addAttribute("toDate", DateUtil.getStartOfDay(toDate));
        model.addAttribute("providers", getAllProviders());
        model.addAttribute("dashboardUrl", coreAppsProperties.getDashboardUrl());
    }

    public List<Provider> getAllProviders(){
        List<Provider> providers = Context.getProviderService().getAllProviders(true);
        if (providers != null && providers.size() > 0){
            Collections.sort(providers, new Comparator<Provider>() {
                @Override
                public int compare(Provider p1, Provider p2) {
                    return OpenmrsUtil.compareWithNullAsGreatest(p1.getName(), p2.getName());
                }
            });
        }
        return providers;
    }
}
