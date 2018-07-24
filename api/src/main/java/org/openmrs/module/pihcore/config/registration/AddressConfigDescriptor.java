package org.openmrs.module.pihcore.config.registration;

import org.openmrs.module.addresshierarchy.AddressField;
import org.codehaus.jackson.annotate.JsonProperty;

import java.util.List;

/**
 * Contains configuration for addresses, used in mirebalais/.../PatientRegistrationApp
 */
public class AddressConfigDescriptor {

    @JsonProperty
    private String shortcutField;

    @JsonProperty
    private List<AddressFieldDescriptor> addressFields;

    public String getShortcutField() {
        return shortcutField;
    }

    public void setShortcutField(String shortcutField) {
        if (AddressField.getByName(shortcutField) == null) {
            throw new RuntimeException("shortcutField is not a value in addresshierarchy.AddressField");
        }
        this.shortcutField = shortcutField;
    }

    public List<AddressFieldDescriptor> getAddressFields() {
        return addressFields;
    }

    public void setAddressFields(List<AddressFieldDescriptor> addressFields) {
        this.addressFields = addressFields;
    }
}
