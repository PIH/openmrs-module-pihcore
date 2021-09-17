package org.openmrs.module.pihcore.setup;

import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.module.pihcore.PihCoreUtil;
import org.openmrs.util.DatabaseUpdater;

import java.io.File;

public class LiquibaseSetup {

    protected static Log log = LogFactory.getLog(LiquibaseSetup.class);

    public static void setup() {
        try {
			for (File file : FileUtils.listFiles(new File(PihCoreUtil.getLiquibaseDir()), new String[]{"xml"}, true)) {
				DatabaseUpdater.executeChangelog(file.getAbsolutePath(), null);
			}
        } catch (Exception e) {
            log.error("Unable run liquibase change sets", e);
        }
    }

}
