package org.openmrs.module.pihcore;

import org.openmrs.Concept;
import org.openmrs.ConceptName;
import org.openmrs.api.ConceptNameType;
import org.openmrs.api.context.Context;

import java.util.Locale;

public class PihUiUtils {

    /**
     * @return the best short name for a concept
     * Taken from orderentryowa - helpers.getConceptShortName
     */
    public String getBestShortName(Concept c) {
        ConceptName preferredShortLocale = null;
        ConceptName shortLocale = null;
        ConceptName preferredLocale = null;
        ConceptName preferredShortEnglish = null;
        ConceptName shortEnglish = null;
        if (c == null || c.getNames() == null || c.getNames().isEmpty()) {
            return "";
        }
        // Get the locale for the current locale, language only
        Locale locale = Context.getLocale();
        String language = locale.getLanguage();
        for (ConceptName cn : c.getNames()) {
            boolean isShort = cn.getConceptNameType() == ConceptNameType.SHORT;
            boolean isPreferred = cn.isPreferred();
            boolean isLocale = cn.getLocale().equals(locale) || cn.getLocale().getLanguage().equals(language);
            boolean isEnglish = cn.getLocale().getLanguage().equals("en");
            if (isPreferred && isShort && isLocale) {
                preferredShortLocale = cn;
            }
            else if (isShort && isLocale) {
                shortLocale = cn;
            }
            else if (isPreferred && isLocale) {
                preferredLocale = cn;
            }
            else if (isPreferred && isShort && isEnglish) {
                preferredShortEnglish = cn;
            }
            else if (isShort && isEnglish) {
                shortEnglish = cn;
            }
        }
        if (preferredShortLocale != null) {
            return preferredShortLocale.getName();
        }
        if (shortLocale != null) {
            return shortLocale.getName();
        }
        if (preferredLocale != null) {
            return preferredLocale.getName();
        }
        if (preferredShortEnglish != null) {
            return preferredShortEnglish.getName();
        }
        if (shortEnglish != null) {
            return shortEnglish.getName();
        }
        return c.getDisplayString();
    }
}
