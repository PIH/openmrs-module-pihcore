package org.openmrs.module.pihcore.setup;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.module.pihcore.PihCoreUtil;
import org.openmrs.util.DatabaseUpdater;

public class ReportSetup {

    protected static Log log = LogFactory.getLog(ReportSetup.class);

    // TODO: there is an existing ReportSetup class in Mirebalais; we should be migrating that functionality here as we refactor
    public static void setup() {
        loadFunctionsAndProcedures();
    }

    private static void loadFunctionsAndProcedures() {
        try {
            DatabaseUpdater.executeChangelog(PihCoreUtil.getSQLProceduresChangelog(), null);
        } catch (Exception e) {
            log.error("Unable to install SQL functions and procedures", e);
        }
    }
}
