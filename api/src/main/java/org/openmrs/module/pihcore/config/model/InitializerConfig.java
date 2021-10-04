package org.openmrs.module.pihcore.config.model;

import org.codehaus.jackson.annotate.JsonProperty;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Object that encapsulates the options that can be configured on a per-installation basis
 */
public class InitializerConfig {

    @JsonProperty
    private String configDir;

    @JsonProperty
    private Map<String, ConfigDomain> domains;

    public InitializerConfig() {}

    public String getConfigDir() {
        return configDir;
    }

    public void setConfigDir(String configDir) {
        this.configDir = configDir;
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
}
