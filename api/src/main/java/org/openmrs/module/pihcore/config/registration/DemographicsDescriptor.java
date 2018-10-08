package org.openmrs.module.pihcore.config.registration;

import org.codehaus.jackson.annotate.JsonProperty;

public class DemographicsDescriptor {

    @JsonProperty
    private DemographicsFieldDescriptor isImmigrant;

    @JsonProperty
    private DemographicsFieldDescriptor isIndigenous;

    @JsonProperty
    private DemographicsFieldDescriptor mothersName;

    public DemographicsFieldDescriptor getIsImmigrant() {
        return isImmigrant;
    }

    public void setIsImmigrant(DemographicsFieldDescriptor isImmigrant) {
        this.isImmigrant = isImmigrant;
    }

    public DemographicsFieldDescriptor getIsIndigenous() {
        return isIndigenous;
    }

    public void setIsIndigenous(DemographicsFieldDescriptor isIndigenous) {
        this.isIndigenous = isIndigenous;
    }

    public DemographicsFieldDescriptor getMothersName() {
        return mothersName;
    }

    public void setMothersName(DemographicsFieldDescriptor mothersName) {
        this.mothersName = mothersName;
    }

}
