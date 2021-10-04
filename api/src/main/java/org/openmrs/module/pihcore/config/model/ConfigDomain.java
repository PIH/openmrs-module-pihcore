package org.openmrs.module.pihcore.config.model;

import org.codehaus.jackson.annotate.JsonProperty;

import java.util.ArrayList;
import java.util.List;

/**
 * A domain of configuration - i.e. drugs or encountertypes
 */
public class ConfigDomain {

    @JsonProperty
    private String domain;

    @JsonProperty
    private List<ConfigFile> configFiles;

    public ConfigDomain() {}


    public ConfigDomain(String domain) {
        this.domain = domain;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public List<ConfigFile> getConfigFiles() {
        if (configFiles == null) {
            configFiles = new ArrayList<>();
        }
        return configFiles;
    }

    public void setConfigFiles(List<ConfigFile> configFiles) {
        this.configFiles = configFiles;
    }
}
