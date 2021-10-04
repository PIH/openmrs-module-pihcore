package org.openmrs.module.pihcore.config.model;

import org.codehaus.jackson.annotate.JsonProperty;
import org.openmrs.module.pihcore.config.ConfigDescriptor;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Object that encapsulates the options that can be configured on a per-installation basis
 */
public class PihConfig {

    @JsonProperty
    private String configDir;

    @JsonProperty
    private String configProperty;

    @JsonProperty
    private ConfigDescriptor configDescriptor;

    @JsonProperty
    private InitializerConfig initializerConfig;

    @JsonProperty
    private Map<String, ConfigDomain> domains;

    @JsonProperty
    private AppAndExtensionConfig appAndExtensionConfig;

    public PihConfig() {}

    public String getConfigDir() {
        return configDir;
    }

    public void setConfigDir(String configDir) {
        this.configDir = configDir;
    }

    public String getConfigProperty() {
        return configProperty;
    }

    public void setConfigProperty(String configProperty) {
        this.configProperty = configProperty;
    }

    public ConfigDescriptor getConfigDescriptor() {
        return configDescriptor;
    }

    public void setConfigDescriptor(ConfigDescriptor configDescriptor) {
        this.configDescriptor = configDescriptor;
    }

    public InitializerConfig getInitializerConfig() {
        return initializerConfig;
    }

    public void setInitializerConfig(InitializerConfig initializerConfig) {
        this.initializerConfig = initializerConfig;
    }

    public Map<String, ConfigDomain> getDomains() {
        if (domains == null) {
            domains = new LinkedHashMap<>();
        }
        return domains;
    }

    public void setDomains(Map<String, ConfigDomain> domains) {
        this.domains = domains;
    }

    public AppAndExtensionConfig getAppAndExtensionConfig() {
        return appAndExtensionConfig;
    }

    public void setAppAndExtensionConfig(AppAndExtensionConfig appAndExtensionConfig) {
        this.appAndExtensionConfig = appAndExtensionConfig;
    }
}
