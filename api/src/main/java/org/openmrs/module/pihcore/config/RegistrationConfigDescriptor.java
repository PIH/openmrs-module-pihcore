package org.openmrs.module.pihcore.config;

import org.codehaus.jackson.annotate.JsonProperty;

/**
 * Object that encapsulates the patient registration options that can be configured on a per-installation basis
 */
public class RegistrationConfigDescriptor {

    @JsonProperty
    private boolean allowUnknownPatients = false;

    @JsonProperty
    private boolean allowManualEntryOfPrimaryIdentifier = false;

    public boolean isAllowUnknownPatients() {
        return allowUnknownPatients;
    }

    public void setAllowUnknownPatients(boolean allowUnknownPatients) {
        this.allowUnknownPatients = allowUnknownPatients;
    }

    public boolean isAllowManualEntryOfPrimaryIdentifier() {
        return allowManualEntryOfPrimaryIdentifier;
    }

    public void setAllowManualEntryOfPrimaryIdentifier(boolean allowManualEntryOfPrimaryIdentifier) {
        this.allowManualEntryOfPrimaryIdentifier = allowManualEntryOfPrimaryIdentifier;
    }
}
