package org.openmrs.module.pihcore.config.registration;

import org.codehaus.jackson.annotate.JsonProperty;

public class DemographicsDescriptor {

    @JsonProperty
    private DemographicsFieldDescriptor mothersName;

    public DemographicsFieldDescriptor getMothersName() {
        return mothersName;
    }

    public void setMothersName(DemographicsFieldDescriptor mothersName) {
        this.mothersName = mothersName;
    }
}
