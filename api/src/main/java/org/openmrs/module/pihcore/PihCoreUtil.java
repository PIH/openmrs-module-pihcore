package org.openmrs.module.pihcore;

import org.openmrs.Visit;
import org.openmrs.api.context.Context;
import org.openmrs.util.OpenmrsUtil;

import java.util.Collections;
import java.util.List;

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
        return "file:" + getFormDirectory() + formName;
    }

    /**
     * Utility  method that reopens a visit (sets its end date to null) if it is the most recent visit for a patient
     * (Used by the ReopenVisitAction and ReopenVisitDispositionAction)
     * TODO: is there a better place for this to live?
     *
     * @param visit
     */
    public static void reopenVisit(Visit visit) {
        // get all visits for the patient with a start date time after the current visit at the same location
        List<Visit> moreRecentVisits = Context.getVisitService().getVisits(null, Collections.singletonList(visit.getPatient()), Collections.singletonList(visit.getLocation()), null, visit.getStartDatetime(), null, null, null, null, true, false);

        // exclude the visit we are working with itself
        if (moreRecentVisits != null) {   // I suspect this isn't needed, and getVisits will at minimum return an empty list, but just to be safe
            moreRecentVisits.remove(visit);
        }

        // if none, and the visit is closed, reopen
        if ((moreRecentVisits == null || moreRecentVisits.size() == 0) && visit.getStopDatetime() != null) {
            visit.setStopDatetime(null);
            Context.getVisitService().saveVisit(visit);
        }
    }

}
