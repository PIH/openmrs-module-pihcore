package org.openmrs.module.pihcore.config.registration;

import org.codehaus.jackson.annotate.JsonProperty;

public class ContactInfoConfigDescriptor {

    @JsonProperty
    private RegistrationFieldDescriptor phoneNumber;

    public RegistrationFieldDescriptor getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(RegistrationFieldDescriptor phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
