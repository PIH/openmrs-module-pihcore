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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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
        dsd.setSql(definition.getStatusDataQuery());
        EvaluationContext context = new EvaluationContext();
        context.addParameterValue("patientId", patient.getPatientId());

        List<DataSetRow> rows = new ArrayList<>();
        try {
            DataSet dataSet = dataSetDefinitionService.evaluate(dsd, context);
            for (Iterator<DataSetRow> i = dataSet.iterator(); i.hasNext();) {
                rows.add(i.next());
            }
        }
        catch (EvaluationException e) {
            throw new RuntimeException("An error occurred calculating " + definition.getId() + " for patient: " + patient.getPatientId(), e);
        }

        VelocityContext velocityContext = StatusDataFunctions.getVelocityContext(patient);
        velocityContext.put("dataSize", rows.size());
        if (rows.size() == 1) {
            velocityContext.put("data", rows.get(0).getColumnValuesByKey());
        }
        else if (rows.size() > 1) {
            velocityContext.put("data", rows);
        }

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
