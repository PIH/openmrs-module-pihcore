package org.openmrs.module.pihcore.config.model;

import org.codehaus.jackson.annotate.JsonProperty;

/**
 * Object that encapsulates the options that can be configured on a per-installation basis
 */
public class ChecksumConfigFile extends ConfigFile {

    @JsonProperty
    private String path;

    @JsonProperty
    private String previousChecksum;

    @JsonProperty
    private String currentChecksum;

    public ChecksumConfigFile() {}

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getPreviousChecksum() {
        return previousChecksum;
    }

    public void setPreviousChecksum(String previousChecksum) {
        this.previousChecksum = previousChecksum;
    }

    public String getCurrentChecksum() {
        return currentChecksum;
    }

    public void setCurrentChecksum(String currentChecksum) {
        this.currentChecksum = currentChecksum;
    }
}
