package org.openmrs.module.pihcore.setup;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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
     */
    private static void updateLiquibaseChangeLogPath() {
        StringBuilder sql = new StringBuilder();
        sql.append("update liquibasechangelog ");
        sql.append("set filename = concat('configuration', substring_index(filename, 'configuration', -1)) ");
        sql.append("where filename like '%/configuration/pih/liquibase/%' ");
        Context.getAdministrationService().executeSQL(sql.toString(), false);
    }

}
