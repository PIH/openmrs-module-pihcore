package org.openmrs.module.pihcore.config.registration;

import org.codehaus.jackson.annotate.JsonProperty;

public class PersonRelationshipConfigDescriptor {

    @JsonProperty
    private String type;

    @JsonProperty
    private String relationshipType;

    @JsonProperty
    private Boolean multipleValues = false;

    @JsonProperty
    private String label;

    @JsonProperty
    private String gender;

    @JsonProperty
    private Boolean required = false;

    public String getRelationshipType() {
        return relationshipType;
    }

    public void setRelationshipType(String relationshipType) {
        this.relationshipType = relationshipType;
    }

    public Boolean getMultipleValues() {
        return multipleValues;
    }

    public void setMultipleValues(Boolean multipleValues) {
        this.multipleValues = multipleValues;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Boolean getRequired() {
        return required;
    }

    public void setRequired(Boolean required) {
        this.required = required;
    }
}
