package org.openmrs.module.pihcore.status;

import java.util.HashMap;
import java.util.Map;

public class StatusData {

    private String definitionId;
    private String label;
    private Map<String, Object> queryData;
    private String displayValue;
    private String displayFormat;

    public StatusData(String definitionId) {
        this.definitionId = definitionId;
    }

    public String getDefinitionId() {
        return definitionId;
    }

    public void setDefinitionId(String definitionId) {
        this.definitionId = definitionId;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public Map<String, Object> getQueryData() {
        if (queryData == null) {
            queryData = new HashMap<>();
        }
        return queryData;
    }

    public void setQueryData(Map<String, Object> queryData) {
        this.queryData = queryData;
    }

    public String getDisplayValue() {
        return displayValue;
    }

    public void setDisplayValue(String displayValue) {
        this.displayValue = displayValue;
    }

    public String getDisplayFormat() {
        return displayFormat;
    }

    public void setDisplayFormat(String displayFormat) {
        this.displayFormat = displayFormat;
    }
}
