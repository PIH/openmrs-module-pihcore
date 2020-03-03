package org.openmrs.module.pihcore;

import org.openmrs.util.OpenmrsUtil;

public class PihCoreUtil {

    public static final String getDefaultPihConfigurationDir() {
        return OpenmrsUtil.getApplicationDataDirectory() + "/configuration/pih";
    }

    public static String getFormDirectory() {
        return getDefaultPihConfigurationDir() + "/htmlforms/";
    }

    public static String getConceptsMetadataSharingDirectory() {
        return getDefaultPihConfigurationDir() + "/concepts/";
    }

    public static String getFormResource(String formName) {
        return "file:" + getFormDirectory() + formName;
    }

}
