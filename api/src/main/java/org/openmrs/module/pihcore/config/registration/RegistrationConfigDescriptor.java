package org.openmrs.module.pihcore.config.registration;

import org.codehaus.jackson.annotate.JsonProperty;

import java.util.List;
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
    private List<String> afterCreatedActions;

    @JsonProperty
    private List<String> matchingPatientsPropertiesToDisplay;

    @JsonProperty
    private List<String> identifierTypesToDisplay;

    @JsonProperty
    private Map<String, Object> similarPatientsSearch;

    @JsonProperty
    private DemographicsConfigDescriptor demographics;

    @JsonProperty
    private SocialConfigDescriptor social;

    @JsonProperty
    private ContactInfoConfigDescriptor contactInfo;

	@JsonProperty
	private ContactPersonConfigDescriptor contactPerson;

    @JsonProperty
    private Integer maxPatientMatchResults;

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

    public List<String> getAfterCreatedActions() {
        return afterCreatedActions;
    }

    public void setAfterCreatedActions(List<String> afterCreatedActions) {
        this.afterCreatedActions = afterCreatedActions;
    }

    public Map<String, Object> getSimilarPatientsSearch() {
        return similarPatientsSearch;
    }

    public void setSimilarPatientsSearch(Map<String, Object> similarPatientsSearch) {
        this.similarPatientsSearch = similarPatientsSearch;
    }

    public List<String> getMatchingPatientsPropertiesToDisplay() {
        return matchingPatientsPropertiesToDisplay;
    }

    public void setMatchingPatientsPropertiesToDisplay(List<String> matchingPatientsPropertiesToDisplay) {
        this.matchingPatientsPropertiesToDisplay = matchingPatientsPropertiesToDisplay;
    }

    public List<String> getIdentifierTypesToDisplay() {
        return identifierTypesToDisplay;
    }

    public void setIdentifierTypesToDisplay(List<String> identifierTypesToDisplay) {
        this.identifierTypesToDisplay = identifierTypesToDisplay;
    }

    public Integer getMaxPatientMatchResults() {
        return maxPatientMatchResults;
    }

    public void setMaxPatientMatchResults(Integer maxPatientMatchResults) {
        this.maxPatientMatchResults = maxPatientMatchResults;
    }

    public DemographicsConfigDescriptor getDemographics() {
        return demographics;
    }

    public void setDemographics(DemographicsConfigDescriptor demographics) {
        this.demographics = demographics;
    }

    public SocialConfigDescriptor getSocial() {
        return social;
    }

    public void setSocial(SocialConfigDescriptor social) {
        this.social = social;
    }

    public ContactInfoConfigDescriptor getContactInfo() {
        return contactInfo;
    }

    public void setContactInfo(ContactInfoConfigDescriptor contactInfo) {
        this.contactInfo = contactInfo;
    }

	public ContactPersonConfigDescriptor getContactPerson() {
		return contactPerson;
	}

	public void setContactPersonConfig(ContactPersonConfigDescriptor contactPersonConfigDescriptor) {
		this.contactPerson = contactPersonConfigDescriptor;
	}
}
