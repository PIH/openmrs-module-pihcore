package org.openmrs.module.pihcore.status;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class StatusDataDefinition {

    @JsonIgnore
    private String path;

    private String id;
    private String labelCode;
    private String statusDataQuery;
    private String conditionExpression;
    private String valueExpression;
    private String formatExpression;

    public StatusDataDefinition() {}

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLabelCode() {
        return labelCode;
    }

    public void setLabelCode(String labelCode) {
        this.labelCode = labelCode;
    }

    public String getStatusDataQuery() {
        return statusDataQuery;
    }

    public void setStatusDataQuery(String statusDataQuery) {
        this.statusDataQuery = statusDataQuery;
    }

    public String getConditionExpression() {
        return conditionExpression;
    }

    public void setConditionExpression(String conditionExpression) {
        this.conditionExpression = conditionExpression;
    }

    public String getValueExpression() {
        return valueExpression;
    }

    public void setValueExpression(String valueExpression) {
        this.valueExpression = valueExpression;
    }

    public String getFormatExpression() {
        return formatExpression;
    }

    public void setFormatExpression(String formatExpression) {
        this.formatExpression = formatExpression;
    }
}
