package org.openmrs.module.pihcore.config;

import org.springframework.stereotype.Component;

@Component
public class Config {

    private ConfigDescriptor descriptor;

    public Config() {
        descriptor = ConfigLoader.load("pihcore");
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

    public ConfigDescriptor.Site getSite() {
        return descriptor.getSite();
    }

    public Boolean shouldScheduleBackupReports() {
        return descriptor.getScheduleBackupReports() != null ? descriptor.getScheduleBackupReports() : false;
    }
}


