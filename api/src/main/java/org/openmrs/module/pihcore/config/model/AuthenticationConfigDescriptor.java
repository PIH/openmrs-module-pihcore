package org.openmrs.module.pihcore.config.model;

import org.codehaus.jackson.annotate.JsonProperty;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AuthenticationConfigDescriptor {

    @JsonProperty
    private String scheme;

    @JsonProperty
    private Map<String, SchemeDescriptor> schemes;

    @JsonProperty
    private List<String> whitelist;

    public String getScheme() {
        return scheme;
    }

    public void setScheme(String scheme) {
        this.scheme = scheme;
    }

    public Map<String, SchemeDescriptor> getSchemes() {
        if (schemes == null) {
            schemes = new HashMap<>();
        }
        return schemes;
    }

    public void setSchemes(Map<String, SchemeDescriptor> schemes) {
        this.schemes = schemes;
    }

    public List<String> getWhitelist() {
        if (whitelist == null) {
            whitelist = new ArrayList<>();
        }
        return whitelist;
    }

    public void setWhitelist(List<String> whitelist) {
        this.whitelist = whitelist;
    }

    public static class SchemeDescriptor {

        @JsonProperty
        private String type;

        @JsonProperty
        private Map<String, String> config;

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public Map<String, String> getConfig() {
            if (config == null) {
                config = new HashMap<>();
            }
            return config;
        }

        public void setConfig(Map<String, String> config) {
            this.config = config;
        }
    }
}
