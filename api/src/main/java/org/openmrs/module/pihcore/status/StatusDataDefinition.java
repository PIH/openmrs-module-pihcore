package org.openmrs.module.pihcore.status;

public class StatusDataDefinition {

    private String id;
    private String labelCode;
    private String statusDataQuery;
    private String valueExpression;
    private String formatExpression;

    public StatusDataDefinition() {}

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
