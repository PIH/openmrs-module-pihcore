package org.openmrs.module.pihcore.config;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
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

    public String getWelcomeMessage() {
        return descriptor.getWelcomeMessage();
    }

    public String getDashboardUrl() {
        return descriptor.getDashboardUrl();
    }

    public String getProviderIdentifierPrefix() { return descriptor.getProviderIdentifierPrefix(); }

    public ConfigDescriptor.Site getSite() {
        return descriptor.getSite();
    }

    public ConfigDescriptor.Country getCountry() { return descriptor.getCountry(); }

    public String getGlobalProperty(String name) {
        return getGlobalProperties().get(name);
    }

    public List<String> getVisitTemplates() {
        return descriptor.getVisitTemplates();
    }

    public Map<String, String> getGlobalProperties() {
        return descriptor.getGlobalProperties() == null ? new HashMap<String, String>() : descriptor.getGlobalProperties();
    }

    public Boolean shouldScheduleBackupReports() {
        return descriptor.getScheduleBackupReports() != null ? descriptor.getScheduleBackupReports() : false;
    }

    public RegistrationConfigDescriptor getRegistrationConfig() {
        return descriptor.getRegistrationConfig();
    }
}
