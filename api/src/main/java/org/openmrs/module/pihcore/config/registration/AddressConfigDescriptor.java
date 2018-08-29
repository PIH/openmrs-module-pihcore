package org.openmrs.module.pihcore.config.registration;

import org.openmrs.module.addresshierarchy.AddressField;
import org.codehaus.jackson.annotate.JsonProperty;

import java.util.List;

/**
 * Contains configuration for addresses, used at runtime (in mirebalais/.../PatientRegistrationApp)
 */
public class AddressConfigDescriptor {

    @JsonProperty
    private List<String> manualFields;

    @JsonProperty
    private String shortcutField;

    public List<String> getManualFields() {
        return manualFields;
    }

    public void setManualFields(List<String> manualFields) {
        for (String fieldName : manualFields) {
            if (AddressField.getByName(fieldName) == null) {
                throw new RuntimeException("manual field '" + fieldName + "' is not a value in addresshierarchy.AddressField");
            }
        }
        this.manualFields = manualFields;
    }

    public String getShortcutField() {
        return shortcutField;
    }

    public void setShortcutField(String shortcutField) {
        if (AddressField.getByName(shortcutField) == null) {
            throw new RuntimeException("shortcutField '" + shortcutField + "' is not a value in addresshierarchy.AddressField");
        }
        this.shortcutField = shortcutField;
    }
}
