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

package org.openmrs.module.pihcore.fragment.controller.reports;

import org.openmrs.Cohort;
import org.openmrs.module.reporting.dataset.MapDataSet;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.indicator.dimension.CohortIndicatorAndDimensionResult;
import org.openmrs.module.reporting.report.ReportData;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.openmrs.module.reporting.report.definition.service.ReportDefinitionService;
import org.openmrs.ui.framework.SimpleObject;
import org.openmrs.ui.framework.UiUtils;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Date;
import java.util.Map;

/**
 *
 */
public class InpatientStatsDailyReportFragmentController {

    public static final String INPATIENT_STATS_DAILY_REPORT_DEFINITION_UUID = "f3bb8094-3738-11e3-b90a-a351ac6b1528";

    public SimpleObject evaluate(@RequestParam("day") Date day,
                                 UiUtils ui,
                                 @SpringBean ReportDefinitionService reportDefinitionService) throws EvaluationException {
        EvaluationContext context = new EvaluationContext();
        context.addParameterValue("day", day);

        ReportDefinition reportDefinition = reportDefinitionService.getDefinitionByUuid(INPATIENT_STATS_DAILY_REPORT_DEFINITION_UUID);
        ReportData data = reportDefinitionService.evaluate(reportDefinition, context);

        SimpleObject cohortResults = new SimpleObject();
        MapDataSet cohortDataSet = (MapDataSet) data.getDataSets().get("cohorts");
        for (Map.Entry<String, Object> entry : cohortDataSet.getData().getColumnValuesByKey().entrySet()) {
            cohortResults.put(entry.getKey(), simplify(entry.getValue()));
        }

        return SimpleObject.create("cohorts", cohortResults, "evaluationContext", SimpleObject.fromObject(context, ui, "evaluationDate", "parameterValues"));
    }

    private Object simplify(Object o) {
        if (o == null) {
            return null;
        }
        else if (o instanceof CohortIndicatorAndDimensionResult) {
            CohortIndicatorAndDimensionResult r = (CohortIndicatorAndDimensionResult) o;
            return simplify(r.getCohortIndicatorAndDimensionCohort());
        }
        else if (o instanceof Cohort) {
            return SimpleObject.create("size", ((Cohort) o).getSize(), "patientIds", ((Cohort) o).getMemberIds());
        } else {
            throw new IllegalArgumentException("Don't know how to simplify: " + o.getClass());
        }
    }

}
