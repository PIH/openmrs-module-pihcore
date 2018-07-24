package org.openmrs.module.pihcore.config.registration;

import org.openmrs.module.addresshierarchy.AddressField;
import org.codehaus.jackson.annotate.JsonProperty;

/**
 * The configuration for an individual address field
 */
public class AddressFieldDescriptor {

    @JsonProperty
    private String addressField;
    // an entry from org.openmrs.module.addresshierarchy.AddressField

    @JsonProperty
    private boolean freeText;
    // whether this field should accept free text or else be looked for in the address entries

    public String getAddressField() {
        return addressField;
    }

    public void setAddressField(String addressField) {
        if (AddressField.getByName(addressField) == null) {
            throw new RuntimeException("addressField is not a value in addresshierarchy.AddressField");
        }
        this.addressField = addressField;
    }

    public boolean isFreeText() {
        return freeText;
    }

    public void setFreeText(boolean freeText) {
        this.freeText = freeText;
    }
}
