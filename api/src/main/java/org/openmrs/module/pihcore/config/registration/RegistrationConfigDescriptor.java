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
    private List<PersonRelationshipConfigDescriptor> relationships;

    @JsonProperty
    private SocialConfigDescriptor social;

    @JsonProperty
    private ContactInfoConfigDescriptor contactInfo;

	@JsonProperty
	private ContactPersonConfigDescriptor contactPerson;

    @JsonProperty
    private Integer maxPatientMatchResults;

    // whether the registration form has the "Possible duplicates" question (see registrationapp), after the phone number; if so,
    // the similar patients banner is a note about how many there are, and the exact matches are not listed on the confirmation.
    // On by default, relies on the registrationcore similar patient search algorithm and registrationapp.strongMatchCutoff global
    // properties set in the pihemr config; to keep the old form, set this to false and
    // registrationcore.preciseSimilarPatientSearchAlgorithm to registrationcore.BasicExactPatientSearchAlgorithm
    @JsonProperty
    private boolean showDuplicatePatientSection = true;

    @JsonProperty
    private boolean showCountryInAddressHierarchy = false;

    public boolean isShowCountryInAddressHiercharchy() {
        return showCountryInAddressHierarchy;
    }
    public void setShowCountryInAddressHierarchy(boolean showCountryInAddressHierarchy) {
        this.showCountryInAddressHierarchy = showCountryInAddressHierarchy;
    }
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

    public boolean isShowDuplicatePatientSection() {
        return showDuplicatePatientSection;
    }

    public void setShowDuplicatePatientSection(boolean showDuplicatePatientSection) {
        this.showDuplicatePatientSection = showDuplicatePatientSection;
    }

    public DemographicsConfigDescriptor getDemographics() {
        return demographics;
    }

    public void setDemographics(DemographicsConfigDescriptor demographics) {
        this.demographics = demographics;
    }

    public List<PersonRelationshipConfigDescriptor> getRelationships() {
        return relationships;
    }

    public void setRelationships(List<PersonRelationshipConfigDescriptor> relationships) {
        this.relationships = relationships;
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
