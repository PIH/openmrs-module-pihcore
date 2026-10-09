package org.openmrs.module.pihcore.biometrics;

import org.apache.commons.lang3.StringUtils;
import org.openmrs.util.ConfigUtil;

/**
 * Biometrics settings, read via ConfigUtil so each can be overridden at runtime by a system property,
 * runtime property, or global property, in that order of precedence
 */
public class BiometricsConfig {

    public static final String BIOMETRIC_ENGINE = "pihcore.biometrics.biometricEngine";
    public static final String TEMPLATE_FORMAT = "pihcore.biometrics.templateFormat";
    public static final String SUBJECT_URL = "pihcore.biometrics.subjectUrl";
    public static final String MATCH_URL = "pihcore.biometrics.matchUrl";
    public static final String SCAN_URL = "pihcore.biometrics.scanUrl";
    public static final String DEVICES_URL = "pihcore.biometrics.devicesUrl";

    // Lowercase forms, checked first: Docker instances set runtime properties as OMRS_EXTRA_* variables, whose names
    // OpenMRS lowercases
    public static final String SUBJECT_URL_LOWERCASE = "pihcore.biometrics.subjecturl";
    public static final String MATCH_URL_LOWERCASE = "pihcore.biometrics.matchurl";

    public String getBiometricEngine() {
        return ConfigUtil.getProperty(BIOMETRIC_ENGINE, "restBiometricEngine");
    }

    public String getTemplateFormat() {
        return ConfigUtil.getProperty(TEMPLATE_FORMAT, "PROPRIETARY");
    }

    /**
     * Called by the OpenMRS server to reach the fingerprint server
     */
    public String getSubjectUrl() {
        return getProperty(SUBJECT_URL_LOWERCASE, SUBJECT_URL, "http://localhost:9000/subject");
    }

    /**
     * Called by the OpenMRS server to reach the fingerprint server
     */
    public String getMatchUrl() {
        return getProperty(MATCH_URL_LOWERCASE, MATCH_URL, "http://localhost:9000/match");
    }

    /**
     * Called by the browser to reach the fingerprint client on the user's workstation
     */
    public String getScanUrl() {
        return ConfigUtil.getProperty(SCAN_URL, "http://localhost:9000/fingerprint/scan");
    }

    /**
     * Called by the browser to reach the fingerprint client on the user's workstation
     */
    public String getDevicesUrl() {
        return ConfigUtil.getProperty(DEVICES_URL, "http://localhost:9000/fingerprint/devices");
    }

    private String getProperty(String name, String fallbackName, String defaultValue) {
        String value = ConfigUtil.getProperty(name);
        return StringUtils.isNotBlank(value) ? value : ConfigUtil.getProperty(fallbackName, defaultValue);
    }
}
