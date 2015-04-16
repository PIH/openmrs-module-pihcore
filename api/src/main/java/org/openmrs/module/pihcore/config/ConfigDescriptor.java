package org.openmrs.module.pihcore.config;

import org.codehaus.jackson.annotate.JsonProperty;

import java.util.List;
import java.util.Map;

/**
 * Object that encapsulates the options that can be configured on a per-installation basis
 */
public class ConfigDescriptor {

    public enum Site {
        MIREBALAIS, LACOLLINE
    }

    @JsonProperty
    String welcomeMessage;

    @JsonProperty
    Site site;

    @JsonProperty
    Map<String, String> globalProperties;

    @JsonProperty
    List<String> components;

    @JsonProperty
    Boolean scheduleBackupReports; // whether or not to schedule a set of reports to be exported to disk regularly as a backup in case of downtime (see scheduleBackupReports method in Mirebalais Module Activator), generally this should only be turned on on production

    public String getWelcomeMessage() {
        return welcomeMessage;
    }

    public void setWelcomeMessage(String welcomeMessage) {
        this.welcomeMessage = welcomeMessage;
    }

    public Site getSite() {
        return site;
    }

    public void setSite(Site site) {
        this.site = site;
    }

    public Map<String, String> getGlobalProperties() {
        return globalProperties;
    }

    public void setGlobalProperties(Map<String, String> globalProperties) {
        this.globalProperties = globalProperties;
    }

    public List<String> getComponents() {
        return components;
    }

    public void setComponents(List<String> components) {
        this.components = components;
    }

    public Boolean getScheduleBackupReports() {
        return scheduleBackupReports;
    }

    public void setScheduleBackupReports(Boolean scheduleBackupReports) {
        this.scheduleBackupReports = scheduleBackupReports;
    }
}
