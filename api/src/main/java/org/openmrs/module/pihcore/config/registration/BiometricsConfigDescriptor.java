package org.openmrs.module.pihcore.config.registration;

import org.codehaus.jackson.annotate.JsonProperty;

public class BiometricsConfigDescriptor {

    @JsonProperty
    private String biometricEngine = "restBiometricEngine";

    @JsonProperty
    private String templateFormat = "PROPRIETARY";

    @JsonProperty
    private String subjectUrl = "http://localhost:9000/subject";

    @JsonProperty
    private String matchUrl = "http://localhost:9000/match";

    @JsonProperty
    private String scanUrl = "http://localhost:9000/fingerprint/scan";

    @JsonProperty
    private String devicesUrl = "http://localhost:9000/fingerprint/devices";

    public String getBiometricEngine() {
        return biometricEngine;
    }

    public void setBiometricEngine(String biometricEngine) {
        this.biometricEngine = biometricEngine;
    }

    public String getTemplateFormat() {
        return templateFormat;
    }

    public void setTemplateFormat(String templateFormat) {
        this.templateFormat = templateFormat;
    }

    public String getSubjectUrl() {
        return subjectUrl;
    }

    public void setSubjectUrl(String subjectUrl) {
        this.subjectUrl = subjectUrl;
    }

    public String getMatchUrl() {
        return matchUrl;
    }

    public void setMatchUrl(String matchUrl) {
        this.matchUrl = matchUrl;
    }

    public String getScanUrl() {
        return scanUrl;
    }

    public void setScanUrl(String scanUrl) {
        this.scanUrl = scanUrl;
    }

    public String getDevicesUrl() {
        return devicesUrl;
    }

    public void setDevicesUrl(String devicesUrl) {
        this.devicesUrl = devicesUrl;
    }
}
