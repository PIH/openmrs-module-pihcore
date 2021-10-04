package org.openmrs.module.pihcore.config.model;

import org.codehaus.jackson.annotate.JsonProperty;

/**
 * Object that encapsulates the options that can be configured on a per-installation basis
 */
public class ConfigFile {

    @JsonProperty
    private String path;

    @JsonProperty
    private Boolean enabled;

    public ConfigFile() {}

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }
}
