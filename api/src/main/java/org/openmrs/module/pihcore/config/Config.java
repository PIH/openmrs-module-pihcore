package org.openmrs.module.pihcore.config;

import org.openmrs.module.pihcore.config.registration.AddressConfigDescriptor;
import org.openmrs.module.pihcore.config.registration.BiometricsConfigDescriptor;
import org.openmrs.module.pihcore.config.registration.RegistrationConfigDescriptor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Component
@Qualifier("config")
public class Config {

    private ConfigDescriptor descriptor;


    public Config() {
        descriptor = ConfigLoader.loadFromRuntimeProperties();
    }

    public void reload(ConfigDescriptor descriptor) {
        this.descriptor = descriptor;
    }

    public Config(ConfigDescriptor descriptor) {
        this.descriptor = descriptor;
    }

    public Boolean isComponentEnabled(String component) {
        return descriptor.getComponents().contains(component);
    }

    public Boolean anyComponentEnabled(List<String> components) {
        for (String component : components) {
            if (isComponentEnabled(component)) {
                return true;
            }
        }
        return false;
    }

    public String getWelcomeMessage() {
        return descriptor.getWelcomeMessage();
    }

    public String getBrowserWarning() { return descriptor.getBrowserWarning(); }

    public String getDashboardUrl() {
        return descriptor.getDashboardUrl();
    }

    public String getAfterMergeUrl() {
        return descriptor.getAfterMergeUrl();
    }

    public String getVisitPageUrl() {
        return descriptor.getVisitsPageUrl();
    }

    public String getVisitsPageWithSpecificUrl() {
        return descriptor.getVisitsPageWithSpecificUrl();
    }

    public String getPrimaryIdentifierPrefix() { return descriptor.getPrimaryIdentifierPrefix(); }

    public String getProviderIdentifierPrefix() { return descriptor.getProviderIdentifierPrefix(); }

    public String getDossierIdentifierPrefix() {
        return descriptor.getDossierIdentifierPrefix();
    }

    public List<String> getExtraIdentifierTypes() { return descriptor.getExtraIdentifierTypes(); }

    public String getSite() {
        return descriptor.getSite();
    }

    public ConfigDescriptor.Specialty getSpecialty() {
        return descriptor.getSpecialty();
    }

    public ConfigDescriptor.Country getCountry() { return descriptor.getCountry(); }

    public String getGlobalProperty(String name) {
        return getGlobalProperties().get(name);
    }

    public String getDispositionConfig() { return descriptor.getDispositionConfig(); }

    public Map<String, String> getGlobalProperties() {
        return descriptor.getGlobalProperties() == null ? new HashMap<String, String>() : descriptor.getGlobalProperties();
    }

    public Map<String, List<String>> getLocationTags() {
        return descriptor.getLocationTags() == null ? new HashMap<String, List<String>>() : descriptor.getLocationTags();
    }

    public Boolean shouldScheduleBackupReports() {
        return descriptor.getScheduleBackupReports() != null ? descriptor.getScheduleBackupReports() : false;
    }

    public Boolean shouldScheduleMonthlyDataExports() {
        return descriptor.getScheduleMonthlyDataExports() != null ? descriptor.getScheduleMonthlyDataExports() : false;
    }

    public String getIdCardLabel() {
        return descriptor.getIdCardLabel();
    }

    public Locale getIdCardLocale() {
        if (descriptor.getIdCardLocale() == null) {
            return new Locale("ht");  // Haiti/Creole by default
        }
        else {
            return descriptor.getIdCardLocale();
        }
    }

    public AddressConfigDescriptor getAddressConfig() {
        return descriptor.getAddressConfig();
    }

    public RegistrationConfigDescriptor getRegistrationConfig() {
        return descriptor.getRegistrationConfig();
    }

    public BiometricsConfigDescriptor getBiometricsConfig() {
        return descriptor.getBiometricsConfig();
    }

    public Boolean shouldRebuildSearchIndex() {
        return descriptor.getRebuildSearchIndexOnStartup() != null ? descriptor.getRebuildSearchIndexOnStartup() : false;
    }
}
