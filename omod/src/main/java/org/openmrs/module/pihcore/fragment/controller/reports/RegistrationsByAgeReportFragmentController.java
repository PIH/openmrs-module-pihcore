package org.openmrs.module.pihcore.fragment.controller.reports;

import org.openmrs.module.pihcore.reporting.dataset.manager.RegistrationDataSetManager;
import org.openmrs.module.reporting.ReportingConstants;
import org.openmrs.module.reporting.dataset.DataSetRow;
import org.openmrs.module.reporting.dataset.SimpleDataSet;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.service.DataSetDefinitionService;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.ui.framework.SimpleObject;
import org.openmrs.ui.framework.UiUtils;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.openmrs.ui.framework.page.PageModel;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Quickly implemented report for Liberia that returns the number of registrations for particular age ranges,
 * and allows for drilling down into these patients to see the details
 */
public class RegistrationsByAgeReportFragmentController {

    protected Integer[] ageBoundaries = {5, 18};

    public SimpleObject evaluate(@SpringBean DataSetDefinitionService dataSetDefinitionService,
                           @SpringBean RegistrationDataSetManager registrationDataSetManager,
                           @RequestParam(value="startDate", required=false) Date startDate,
                           @RequestParam(value="endDate", required=false) Date endDate,
                           PageModel model,
                           UiUtils ui) throws Exception {

        Map<String, AgeResult> registrationsByAge = new LinkedHashMap<String, AgeResult>();

        EvaluationContext context = new EvaluationContext();
        context.addParameterValue(ReportingConstants.START_DATE_PARAMETER.getName(), startDate);
        context.addParameterValue(ReportingConstants.END_DATE_PARAMETER.getName(), endDate);

        DataSetDefinition dsd = registrationDataSetManager.constructDataSet();
        SimpleDataSet dataSet = (SimpleDataSet) dataSetDefinitionService.evaluate(dsd, context);

        List<String> columns = new ArrayList<String>();
        columns.add("EMR_ID");
        columns.add("GIVEN_NAME");
        columns.add("FAMILY_NAME");
        columns.add("AGE_AT_REGISTRATION");
        columns.add("BIRTHDATE");
        columns.add("GENDER");
        columns.add("REGISTRATION_DATE");

        List<String> ageCategories = new ArrayList<String>();
        ageCategories.add(getAgeCategory(0.0));
        for (Integer bound : ageBoundaries) {
            ageCategories.add(getAgeCategory(bound.doubleValue()));
        }
        String totalCategory = ui.message("pihcore.total");
        ageCategories.add(totalCategory);

        for (DataSetRow row : dataSet) {
            Double patientAge = (Double)row.getColumnValue("AGE_AT_REGISTRATION");
            String ageCategory = getAgeCategory(patientAge);
            Map<String, Object> m = new HashMap<String, Object>();
            for (String column : columns) {
                m.put(column, ui.format(row.getColumnValue(column)));
            }
            getAgeResult(registrationsByAge, ageCategory).addRow(m);
            getAgeResult(registrationsByAge, totalCategory).addRow(m);
        }

        List<String> columnLabels = new ArrayList<String>();
        for (String column : columns) {
            columnLabels.add(ui.message("pihcore." + column.toLowerCase()));
        }

        SimpleObject result = new SimpleObject();
        result.put("startDate", startDate);
        result.put("endDate", endDate);
        result.put("ageCategories", ageCategories);
        result.put("columns", columns);
        result.put("columnLabels", columnLabels);
        result.put("registrationsByAge", registrationsByAge);
        return result;
    }

    /**
     * @return the AgeResult that corresponds to the key in the map, initializing it if not yet present
     */
    protected AgeResult getAgeResult(Map<String, AgeResult> registrationsByAge, String ageCategory) {
        AgeResult ageResult = registrationsByAge.get(ageCategory);
        if (ageResult == null) {
            ageResult = new AgeResult();
            ageResult.setAgeCategory(ageCategory);
            registrationsByAge.put(ageCategory, ageResult);
        }
        return ageResult;
    }

    /**
     * @return the age category for the given age
     */
    protected String getAgeCategory(Double age) {
        int lowerBound = 0;
        for (Integer i=0; i<ageBoundaries.length; i++) {
            Integer bound = ageBoundaries[i];
            if (age < bound) {
                return lowerBound + "-" + (bound-1);
            }
            lowerBound = bound;
        }
        return ageBoundaries[ageBoundaries.length-1] + "+";
    }

    public static class AgeResult {

        private String ageCategory;
        private List<Map<String, Object>> rows;

        public String getAgeCategory() {
            return ageCategory;
        }

        public void setAgeCategory(String ageCategory) {
            this.ageCategory = ageCategory;
        }

        public List<Map<String, Object>> getRows() {
            if (rows == null) {
                rows = new ArrayList<Map<String, Object>>();
            }
            return rows;
        }

        public void setRows(List<Map<String, Object>> rows) {
            this.rows = rows;
        }

        public void addRow(Map<String, Object> row) {
            getRows().add(row);
        }
    }
}
