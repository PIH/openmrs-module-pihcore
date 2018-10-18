package org.openmrs.module.pihcore.config.registration;

import org.codehaus.jackson.annotate.JsonProperty;

public class DemographicsConfigDescriptor {

    @JsonProperty
    private RegistrationFieldDescriptor mothersName;

    public RegistrationFieldDescriptor getMothersName() {
        return mothersName;
    }

    public void setMothersName(RegistrationFieldDescriptor mothersName) {
        this.mothersName = mothersName;
    }

}
