package org.openmrs.module.pihcore.config;

import org.codehaus.jackson.annotate.JsonProperty;

import java.util.Map;

/**
 * Object that encapsulates the patient registration options that can be configured on a per-installation basis
 */
public class RegistrationConfigDescriptor {

    @JsonProperty
    private boolean allowUnknownPatients = false;

    @JsonProperty
    private boolean allowManualEntryOfPrimaryIdentifier = false;

    @JsonProperty
    private String afterCreatedUrl = "registrationapp/registrationSummary.page?patientId={{patientId}}";

    @JsonProperty
    private Map<String, Object> similarPatientsSearch;

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

    public String getAfterCreatedUrl() {
        return afterCreatedUrl;
    }

    public void setAfterCreatedUrl(String afterCreatedUrl) {
        this.afterCreatedUrl = afterCreatedUrl;
    }

    public Map<String, Object> getSimilarPatientsSearch() {
        return similarPatientsSearch;
    }

    public void setSimilarPatientsSearch(Map<String, Object> similarPatientsSearch) {
        this.similarPatientsSearch = similarPatientsSearch;
    }
}
