package org.openmrs.module.pihcore.config.model;

import org.codehaus.jackson.annotate.JsonProperty;
import org.openmrs.module.appframework.domain.AppDescriptor;
import org.openmrs.module.appframework.domain.AppTemplate;
import org.openmrs.module.appframework.domain.Extension;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Object that encapsulates the options that can be configured on a per-installation basis
 */
public class AppAndExtensionConfig {

    @JsonProperty
    private Boolean loadAppsFromClasspath;

    @JsonProperty
    private Boolean appsEnabledByDefault;

    @JsonProperty
    private Boolean extensionsEnabledByDefault;

    @JsonProperty
    private List<String> enabledApps;

    @JsonProperty
    private List<String> enabledExtensions;

    @JsonProperty
    private Map<String, AppTemplate> appTemplates;

    @JsonProperty
    private Map<String, AppDescriptor> apps;

    @JsonProperty
    private Map<String, Extension> extensions;

    public AppAndExtensionConfig() {}

    public Boolean getLoadAppsFromClasspath() {
        return loadAppsFromClasspath;
    }

    public void setLoadAppsFromClasspath(Boolean loadAppsFromClasspath) {
        this.loadAppsFromClasspath = loadAppsFromClasspath;
    }

    public Boolean getAppsEnabledByDefault() {
        return appsEnabledByDefault;
    }

    public void setAppsEnabledByDefault(Boolean appsEnabledByDefault) {
        this.appsEnabledByDefault = appsEnabledByDefault;
    }

    public Boolean getExtensionsEnabledByDefault() {
        return extensionsEnabledByDefault;
    }

    public void setExtensionsEnabledByDefault(Boolean extensionsEnabledByDefault) {
        this.extensionsEnabledByDefault = extensionsEnabledByDefault;
    }

    public List<String> getEnabledApps() {
        if (enabledApps == null) {
            enabledApps = new ArrayList<>();
        }
        return enabledApps;
    }

    public void setEnabledApps(List<String> enabledApps) {
        this.enabledApps = enabledApps;
    }

    public List<String> getEnabledExtensions() {
        if (enabledExtensions == null) {
            enabledExtensions = new ArrayList<>();
        }
        return enabledExtensions;
    }

    public void setEnabledExtensions(List<String> enabledExtensions) {
        this.enabledExtensions = enabledExtensions;
    }

    public Map<String, AppTemplate> getAppTemplates() {
        if (appTemplates == null) {
            appTemplates = new LinkedHashMap<>();
        }
        return appTemplates;
    }

    public void setAppTemplates(Map<String, AppTemplate> appTemplates) {
        this.appTemplates = appTemplates;
    }

    public Map<String, AppDescriptor> getApps() {
        if (apps == null) {
            apps = new LinkedHashMap<>();
        }
        return apps;
    }

    public void setApps(Map<String, AppDescriptor> apps) {
        this.apps = apps;
    }

    public Map<String, Extension> getExtensions() {
        if (extensions == null) {
            extensions = new LinkedHashMap<>();
        }
        return extensions;
    }

    public void setExtensions(Map<String, Extension> extensions) {
        this.extensions = extensions;
    }
}
