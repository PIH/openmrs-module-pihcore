package org.openmrs.module.pihcore.status;

import org.apache.commons.lang.StringUtils;
import org.apache.velocity.VelocityContext;
import org.openmrs.Patient;
import org.openmrs.messagesource.MessageSourceService;
import org.openmrs.module.reporting.dataset.DataSet;
import org.openmrs.module.reporting.dataset.DataSetRow;
import org.openmrs.module.reporting.dataset.definition.SqlFileDataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.service.DataSetDefinitionService;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.openmrs.api.context.Context;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@Component
public class StatusDataEvaluator {

    @Autowired
    DataSetDefinitionService dataSetDefinitionService;

    @Autowired
    MessageSourceService messageSourceService;

    public StatusData evaluate(Patient patient, StatusDataDefinition definition) {
        StatusData data = new StatusData(definition.getId());
        data.setLabel(messageSourceService.getMessage(definition.getLabelCode()));

        SqlFileDataSetDefinition dsd = new SqlFileDataSetDefinition();
        dsd.addParameter(new Parameter("patientId", "Patient ID", Integer.class));
        dsd.addParameter(new Parameter("locale", "Locale", String.class));
        String sql = definition.getStatusDataQuery();
        if (sql.toLowerCase().endsWith(".sql")) {
            File statusDataDir = StatusDataLoader.getStatusDataDirectory();
            File statusDefFile = new File(statusDataDir, definition.getPath());
            File sqlFile = new File(statusDefFile.getParentFile(), definition.getStatusDataQuery());
            dsd.setSqlFile(sqlFile.getAbsolutePath());
        }
        else {
            dsd.setSql(sql);
        }

        EvaluationContext context = new EvaluationContext();
        context.addParameterValue("patientId", patient.getPatientId());
        context.addParameterValue("locale", Context.getLocale().toString());
        List<Map<String, Object>> queryData = new ArrayList<>();
        try {
            DataSet dataSet = dataSetDefinitionService.evaluate(dsd, context);
            for (Iterator<DataSetRow> i = dataSet.iterator(); i.hasNext();) {
                queryData.add(i.next().getColumnValuesByKey());
            }
        }
        catch (EvaluationException e) {
            throw new RuntimeException(e);
        }
        data.getQueryData().put("data", queryData);
        if (queryData.size() == 1) {
            Map<String, Object> vals = queryData.get(0);
            for (String key : vals.keySet()) {
                if (data.getQueryData().containsKey(key)) {
                    throw new IllegalArgumentException("Key <" + key + "> is already defined, please adjust your query column names");
                }
                data.getQueryData().put(key, vals.get(key));
            }
        }

        VelocityContext velocityContext = StatusDataFunctions.getVelocityContext(data.getQueryData());
        boolean enabled = true;
        if (StringUtils.isNotEmpty(definition.getConditionExpression())) {
            String conditionValue = StatusDataFunctions.evaluateExpression(velocityContext, definition.getConditionExpression());
            if (conditionValue != null && (conditionValue.equalsIgnoreCase("false") || conditionValue.equalsIgnoreCase("0"))) {
                enabled = false;
            }
        }
        data.setEnabled(enabled);

        String displayValue = StatusDataFunctions.evaluateExpression(velocityContext, definition.getValueExpression());
        data.setDisplayValue(displayValue);
        String displayFormat = "";
        if (StringUtils.isNotBlank(definition.getFormatExpression())) {
            displayFormat = StatusDataFunctions.evaluateExpression(velocityContext, definition.getFormatExpression());
        }
        data.setDisplayFormat(displayFormat);

        return data;
    }

    public List<StatusData> evaluate(Patient patient, String pathToFile) {
        List<StatusData> ret = new ArrayList<>();
        List<StatusDataDefinition> definitions = StatusDataLoader.getStatusDataDefinitions(pathToFile);
        for (StatusDataDefinition definition : definitions) {
            ret.add(evaluate(patient, definition));
        }
        return ret;
    }

    public StatusData evaluate(Patient patient, String pathToFile, String definitionId) {
        List<StatusDataDefinition> definitions = StatusDataLoader.getStatusDataDefinitions(pathToFile);
        for (StatusDataDefinition definition : definitions) {
            if (definition.getId().equalsIgnoreCase(definitionId)) {
                return evaluate(patient, definition);
            }
        }
        throw new IllegalArgumentException("No definition with id " + definitionId + " found in " + pathToFile);
    }
}
