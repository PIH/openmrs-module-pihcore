package org.openmrs.module.pihcore.config.registration;

import org.codehaus.jackson.annotate.JsonProperty;

public class SocialConfigDescriptor {

    @JsonProperty
    private RegistrationFieldDescriptor activeCasefinding;

    @JsonProperty
    private RegistrationFieldDescriptor canRead;

    @JsonProperty
    private RegistrationFieldDescriptor isDisabled;

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

    public RegistrationFieldDescriptor getCanRead() {
        return canRead;
    }

    public void setCanRead(RegistrationFieldDescriptor canRead) {
        this.canRead = canRead;
    }

    public RegistrationFieldDescriptor getIsDisabled() {
        return isDisabled;
    }

    public void setIsDisabled(RegistrationFieldDescriptor isDisabled) {
        this.isDisabled = isDisabled;
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
