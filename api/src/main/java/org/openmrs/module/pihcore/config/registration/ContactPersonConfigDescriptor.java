package org.openmrs.module.pihcore.config.registration;

import org.codehaus.jackson.annotate.JsonProperty;

public class ContactPersonConfigDescriptor {

	@JsonProperty
	private Boolean required;

	@JsonProperty
	private RegistrationFieldDescriptor phoneNumber;

	public Boolean getRequired() {
		return required;
	}

	public void setRequired(Boolean required) {
		this.required = required;
	}

	public RegistrationFieldDescriptor getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(RegistrationFieldDescriptor phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

}

