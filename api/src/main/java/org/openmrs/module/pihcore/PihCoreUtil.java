package org.openmrs.module.pihcore;

import org.openmrs.util.OpenmrsUtil;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class PihCoreUtil {

    public static final String getDefaultPihConfigurationDir() {
        return OpenmrsUtil.getApplicationDataDirectory() + "/configuration/pih";
    }

    public static final String getDefaultPihConfigurationChecksumDir() {
        return OpenmrsUtil.getApplicationDataDirectory() + "/configuration_checksums/pih";
    }

    public static String getFormDirectory() {
        return getDefaultPihConfigurationDir() + "/htmlforms/";
    }

    public static String getConceptsMetadataSharingDirectory() {
        return getDefaultPihConfigurationDir() + "/concepts/";
    }

    public static  String getLiquibaseDir() {
        return getDefaultPihConfigurationDir() + "/liquibase/";
    }

    public static String getLiquibaseChangeLog() {
        return getLiquibaseDir() + "liquibase.xml";
    }

    public static String getDrugListDirectory() {
        return getDefaultPihConfigurationDir() + "/drugs/";
    }

    public static String getDrugListChecksumDirectory() {
        return PihCoreUtil.getDefaultPihConfigurationChecksumDir() + "/drugs/";
    }

    public static String getFormResource(String formName) {
        try {
            return URLEncoder.encode("file:" + getFormDirectory() + formName, StandardCharsets.UTF_8.toString()).toString();
        } catch (UnsupportedEncodingException e) {
            return "file:" + getFormDirectory() + formName;
        }
    }

}
