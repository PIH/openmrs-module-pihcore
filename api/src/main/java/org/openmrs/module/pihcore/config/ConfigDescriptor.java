package org.openmrs.module.pihcore.config;

import org.apache.commons.lang.BooleanUtils;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.node.ArrayNode;
import org.openmrs.module.appframework.domain.Extension;
import org.openmrs.module.pihcore.config.model.AuthenticationConfigDescriptor;
import org.openmrs.module.pihcore.config.registration.AddressConfigDescriptor;
import org.openmrs.module.pihcore.config.registration.BiometricsConfigDescriptor;
import org.openmrs.module.pihcore.config.registration.RegistrationConfigDescriptor;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Object that encapsulates the options that can be configured on a per-installation basis
 */
public class ConfigDescriptor {

    public enum Country {
        HAITI, LIBERIA, SIERRA_LEONE, MEXICO, PERU, OTHER
    }

    public enum Specialty {
        HIV, MENTAL_HEALTH
    }

    @JsonProperty
    private String welcomeMessage;

    @JsonProperty
    private String browserWarning;

    @JsonProperty
    private String dashboardUrl;

    @JsonProperty
    private String afterMergeUrl;

    @JsonProperty
    private String visitsPageUrl;

    @JsonProperty
    private String visitsPageWithSpecificUrl;

    @JsonProperty
    private Country country;

    @JsonProperty
    private Specialty specialty;

    @JsonProperty
    private String site;

    @JsonProperty
    private Map<String, String> globalProperties;

    @JsonProperty
    private Map<String, List<String>> locationTags;

    @JsonProperty
    private List<String> components;

    @JsonProperty
    private String primaryIdentifierPrefix;

    @JsonProperty
    private String providerIdentifierPrefix;

    @JsonProperty
    private String dossierIdentifierPrefix;

    @JsonProperty
    private List<String> extraIdentifierTypes;

    @JsonProperty
    private String dispositionConfig; // override the default disposition config file to use

    @JsonProperty
    private Boolean scheduleBackupReports; // whether or not to schedule a set of reports to be exported to disk regularly as a backup in case of downtime (see scheduleBackupReports method in Mirebalais Module Activator), generally this should only be turned on on production

    @JsonProperty
    private Boolean scheduleMonthlyDataExports; // whether or not to schedule the full data export to run monthly on the 5th of each month

    @JsonProperty
    private String idCardLabel;  // label printed on the standard PIH ID card

    @JsonProperty
    private Locale idCardLocale;  // language/locale of labels on on PIH ID card

    @JsonProperty
    private AddressConfigDescriptor addressConfig;  // configuration of the address hierarchy

    @JsonProperty
    private RegistrationConfigDescriptor registrationConfig;

    @JsonProperty
    private BiometricsConfigDescriptor biometricsConfig;

    @JsonProperty
    private AuthenticationConfigDescriptor authenticationConfig;

    /**
     * A value less than zero indicates that the search index should always be rebuilt on server startup.
     * This is here to support systems that are based on DB backups and always need an initial search index created.
     * A value greater than zero is checked against a global property which stores the most recent value that triggered
     * a search index rebuild, and if it is greater than the stored value, this indicates that the search index
     * should be rebuilt at startup.
     * This is here to support the ability to trigger a search index rebuild when required by the code, but not to
     * always have a search index rebuild if not needed, which would add several minutes to a system startup.
     */
    @JsonProperty
    private Integer rebuildSearchIndexConfig;

    @JsonProperty
    private ArrayNode findPatientColumnConfig;

    @JsonProperty
    private Boolean localZlIdentifierGeneratorEnabled;

    @JsonProperty
    private String localZlIdentifierGeneratorPrefix;

    @JsonProperty
    private List<Extension> extensions;

    public String getWelcomeMessage() {
        return welcomeMessage;
    }

    public void setWelcomeMessage(String welcomeMessage) {
        this.welcomeMessage = welcomeMessage;
    }

    public String getBrowserWarning() {
        return browserWarning;
    }

    public void setBrowserWarning(String browserWarning) {
        this.browserWarning = browserWarning;
    }

    public Country getCountry() {
        return country;
    }

    public void setCountry(Country country) {
        this.country = country;
    }

    public Specialty getSpecialty() {
        return specialty;
    }

    public void setSpecialty(Specialty specialty) {
        this.specialty = specialty;
    }

    public String getSite() {
        return site;
    }

    public void setSite(String site) {
        this.site = site;
    }

    public String getDashboardUrl() { return dashboardUrl; }

    public void setDashboardUrl(String dashboardUrl) { this.dashboardUrl = dashboardUrl; }

    public String getAfterMergeUrl() { return afterMergeUrl; }

    public void setAfterMergeUrl(String afterMergeUrl) { this.afterMergeUrl = afterMergeUrl; }

    public String getVisitsPageUrl() { return visitsPageUrl; }

    public void setVisitsPageUrl(String visitsPageUrl) { this.visitsPageUrl = visitsPageUrl; }

    public String getVisitsPageWithSpecificUrl() {
        return visitsPageWithSpecificUrl;
    }

    public void setVisitsPageWithSpecificUrl(String visitsPageWithSpecificUrl) {
        this.visitsPageWithSpecificUrl = visitsPageWithSpecificUrl;
    }

    public Map<String, String> getGlobalProperties() {
        return globalProperties;
    }

    public void setGlobalProperties(Map<String, String> globalProperties) {
        this.globalProperties = globalProperties;
    }

    public Map<String, List<String>> getLocationTags() {
        return locationTags;
    }

    public void setLocationTags(Map<String, List<String>> locationTags) {
        this.locationTags = locationTags;
    }

    public List<String> getComponents() {
        if (components == null) {
            components = new ArrayList<String>();
        }
        return components;
    }

    public void setComponents(List<String> components) {
        this.components = components;
    }

    public String getPrimaryIdentifierPrefix() {
        return primaryIdentifierPrefix;
    }

    public void setPrimaryIdentifierPrefix(String primaryIdentifierPrefix) {
        this.primaryIdentifierPrefix = primaryIdentifierPrefix;
    }

    public String getProviderIdentifierPrefix() {
        return providerIdentifierPrefix;
    }

    public void setProviderIdentifierPrefix(String providerIdentifierPrefix) {
        this.providerIdentifierPrefix = providerIdentifierPrefix;
    }

    public String getDossierIdentifierPrefix() {
        return dossierIdentifierPrefix;
    }

    public void setDossierIdentifierPrefix(String dossierIdentifierPrefix) {
        this.dossierIdentifierPrefix = dossierIdentifierPrefix;
    }

    public List<String> getExtraIdentifierTypes() {
        return extraIdentifierTypes;
    }

    public void setExtraIdentifierTypes(List<String> extraIdentifierTypes) {
        this.extraIdentifierTypes = extraIdentifierTypes;
    }

    public String getDispositionConfig() {
        return dispositionConfig;
    }

    public void setDispositionConfig(String dispositionConfig) {
        this.dispositionConfig = dispositionConfig;
    }

    public Boolean getScheduleBackupReports() {
        return scheduleBackupReports;
    }

    public void setScheduleBackupReports(Boolean scheduleBackupReports) {
        this.scheduleBackupReports = scheduleBackupReports;
    }

    public Boolean getScheduleMonthlyDataExports() {
        return scheduleMonthlyDataExports;
    }

    public void setScheduleMonthlyDataExports(Boolean scheduleMonthlyDataExports) {
        this.scheduleMonthlyDataExports = scheduleMonthlyDataExports;
    }

    public String getIdCardLabel() {
        return idCardLabel;
    }

    public void setIdCardLabel(String idCardLabel) {
        this.idCardLabel = idCardLabel;
    }

    public Locale getIdCardLocale() {
        return idCardLocale;
    }

    public void setIdCardLocale(Locale idCardLocale) {
        this.idCardLocale = idCardLocale;
    }

    public AddressConfigDescriptor getAddressConfig() {
        return addressConfig;
    }

    public void setAddressConfig(AddressConfigDescriptor addressConfig) {
        this.addressConfig = addressConfig;
    }

    public RegistrationConfigDescriptor getRegistrationConfig() {
        if (registrationConfig == null) {
            registrationConfig = new RegistrationConfigDescriptor();
        }
        return registrationConfig;
    }

    public void setRegistrationConfig(RegistrationConfigDescriptor registrationConfig) {
        this.registrationConfig = registrationConfig;
    }

    public BiometricsConfigDescriptor getBiometricsConfig() {
        if (biometricsConfig == null) {
            biometricsConfig = new BiometricsConfigDescriptor();
        }
        return biometricsConfig;
    }

    public void setBiometrics(BiometricsConfigDescriptor biometricsConfig) {
        this.biometricsConfig = biometricsConfig;
    }

    public AuthenticationConfigDescriptor getAuthenticationConfig() {
        if (authenticationConfig == null) {
            authenticationConfig = new AuthenticationConfigDescriptor();
        }
        return authenticationConfig;
    }

    public void setAuthenticationConfig(AuthenticationConfigDescriptor authenticationConfig) {
        this.authenticationConfig = authenticationConfig;
    }

    public Integer getRebuildSearchIndexConfig() {
        return rebuildSearchIndexConfig;
    }

    public void setRebuildSearchIndexConfig(Integer rebuildSearchIndexConfig) {
        this.rebuildSearchIndexConfig = rebuildSearchIndexConfig;
    }

    public ArrayNode getFindPatientColumnConfig() {
        return findPatientColumnConfig;
    }

    public void setFindPatientColumnConfig(ArrayNode findPatientColumnConfig) {
        this.findPatientColumnConfig = findPatientColumnConfig;
    }

    public Boolean getLocalZlIdentifierGeneratorEnabled() {
        return BooleanUtils.isTrue(localZlIdentifierGeneratorEnabled);
    }

    public void setLocalZlIdentifierGeneratorEnabled(Boolean localZlIdentifierGeneratorEnabled) {
        this.localZlIdentifierGeneratorEnabled = localZlIdentifierGeneratorEnabled;
    }

    public String getLocalZlIdentifierGeneratorPrefix() {
        return localZlIdentifierGeneratorPrefix == null ? "" : localZlIdentifierGeneratorPrefix;
    }

    public void setLocalZlIdentifierGeneratorPrefix(String localZlIdentifierGeneratorPrefix) {
        this.localZlIdentifierGeneratorPrefix = localZlIdentifierGeneratorPrefix;
    }

    public List<Extension> getExtensions() {
        if (extensions == null) {
            extensions = new ArrayList<>();
        }
        return extensions;
    }

    public void setExtensions(List<Extension> extensions) {
        this.extensions = extensions;
    }
}
