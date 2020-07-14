package org.openmrs.module.pihcore.setup;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.module.pihcore.PihCoreUtil;
import org.openmrs.util.DatabaseUpdater;

public class LiquibaseSetup {

    protected static Log log = LogFactory.getLog(LiquibaseSetup.class);

    public static void setup() {
        try {
            DatabaseUpdater.executeChangelog(PihCoreUtil.getLiquibaseChangeLog(), null);
        } catch (Exception e) {
            log.error("Unable run liquibase change sets", e);
        }
    }

}
