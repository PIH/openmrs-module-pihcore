package org.openmrs.module.pihcore.config.registration;

import org.codehaus.jackson.annotate.JsonProperty;

public class RegistrationFieldDescriptor {

    @JsonProperty
    private Boolean required;

    @JsonProperty
    private String regex;

    public Boolean getRequired() {
        return required;
    }

    public void setRequired(Boolean required) {
        this.required = required;
    }

    public String getRegex() {
        return regex;
    }

    public void setRegex(String regex) {
        this.regex = regex;
    }
}
