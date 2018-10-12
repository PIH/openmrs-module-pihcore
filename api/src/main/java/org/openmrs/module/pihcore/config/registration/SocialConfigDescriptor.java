package org.openmrs.module.pihcore.config.registration;

import org.codehaus.jackson.annotate.JsonProperty;

public class SocialConfigDescriptor {

    @JsonProperty
    private RegistrationFieldDescriptor activeCasefinding;

    @JsonProperty
    private RegistrationFieldDescriptor isImmigrant;

    @JsonProperty
    private RegistrationFieldDescriptor isIndigenous;

    public RegistrationFieldDescriptor getActiveCasefinding() {
        return activeCasefinding;
    }

    public void setActiveCasefinding(RegistrationFieldDescriptor activeCasefinding) {
        this.activeCasefinding = activeCasefinding;
    }

    public RegistrationFieldDescriptor getIsImmigrant() {
        return isImmigrant;
    }

    public void setIsImmigrant(RegistrationFieldDescriptor isImmigrant) {
        this.isImmigrant = isImmigrant;
    }

    public RegistrationFieldDescriptor getIsIndigenous() {
        return isIndigenous;
    }

    public void setIsIndigenous(RegistrationFieldDescriptor isIndigenous) {
        this.isIndigenous = isIndigenous;
    }

}
