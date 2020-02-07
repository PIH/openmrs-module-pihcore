package org.openmrs.module.pihcore;

public class PihCoreUtil {

    public static String getFormDirectory() {
        return "configuration/pih/htmlforms/";
    }

    public static String getFormResource(String formName) {
        return "file:" + getFormDirectory() + formName;
    }

}
