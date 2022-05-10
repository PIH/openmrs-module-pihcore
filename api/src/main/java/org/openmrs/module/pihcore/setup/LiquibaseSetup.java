package org.openmrs.module.pihcore.setup;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.context.Context;
import org.openmrs.liquibase.ChangeSetExecutorCallback;
import org.openmrs.module.pihcore.PihCoreUtil;
import org.openmrs.util.DatabaseUpdater;

public class LiquibaseSetup {

    protected static Log log = LogFactory.getLog(LiquibaseSetup.class);

    public static void setup() throws Exception {
        try {
            updateLiquibaseChangeLogPath();
            DatabaseUpdater.executeChangelog(PihCoreUtil.getLiquibaseChangeLog(), (ChangeSetExecutorCallback) null);
        } catch (Exception e) {
            log.error("Unable run liquibase change sets provided by PIH EMR config", e);
            throw e;
        }
    }

    /**
     * See UHM-6473
     * If the PIH liquibase has not run yet on OpenMRS 2.5.x, then first update all of the filenames to be
     * relative to the application data directory, in order to match the new liquibase file names.
     */
    private static void updateLiquibaseChangeLogPath() {
        log.warn("Checking if liquibase filenames need to be updated");
        AdministrationService as = Context.getAdministrationService();
        StringBuilder sql = new StringBuilder();
        sql.append("select count(*) from liquibasechangelog where filename = 'configuration/pih/liquibase/liquibase.xml'");
        int numRows = Integer.parseInt(as.executeSQL(sql.toString(), true).get(0).get(0).toString());
        if (numRows == 0) {
            log.warn("Liquibase filename update is needed, executing");
            sql = new StringBuilder();
            sql.append("update liquibasechangelog ");
            sql.append("set filename = concat('configuration', substring_index(filename, 'configuration', -1)) ");
            sql.append("where filename like '%/configuration/pih/liquibase/%' ");
            as.executeSQL(sql.toString(), false);
        }
        else {
            log.warn("Liquibase filename update is not required");
        }
    }

}
