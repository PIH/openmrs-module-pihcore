/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
package org.openmrs.module.pihcore;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.context.Context;
import org.openmrs.api.context.Daemon;
import org.openmrs.module.BaseModuleActivator;
import org.openmrs.module.DaemonToken;
import org.openmrs.module.DaemonTokenAware;
import org.openmrs.module.Module;
import org.openmrs.module.ModuleFactory;
import org.openmrs.module.pihcore.setup.ConfigurationSetup;
import org.openmrs.module.pihcore.setup.MergeActionsSetup;
import org.openmrs.module.pihcore.task.PihCoreTimerTask;

import java.util.concurrent.TimeUnit;

public class PihCoreActivator extends BaseModuleActivator implements DaemonTokenAware {

	protected Log log = LogFactory.getLog(getClass());

    private DaemonToken daemonToken;

    @Override
	public void started() {
        log.info("PIH Core Module Started");
        configureSystem();
        PihCoreTimerTask.setEnabled(true);
    }

    public void configureSystem() {
        try {
            log.info("Initiating pihcore configuration");

            final ConfigurationSetup configurationSetup = Context.getRegisteredComponents(ConfigurationSetup.class).get(0);
            configurationSetup.setupBase();
            configurationSetup.configureDependencies();

            // Startup DB event consumers in a separate thread
            Daemon.runInDaemonThread(() -> {
                try {
                    TimeUnit.MINUTES.sleep(1);
                }
                catch (Exception e) {}
                configurationSetup.setupDbEventConsumers();
            }, daemonToken);

            log.info("Distribution startup complete.");
        }
        catch (Exception e) {
            Module mod = ModuleFactory.getModuleById("pihcore");
            ModuleFactory.stopModule(mod, true, true);
            throw new RuntimeException("An error occurred while starting the pihcore module", e);
        }
    }

    @Override
    public void stopped() {
        log.info("PIH Core Module Stopped");
        MergeActionsSetup.deregisterMergeActions();
    }

    @Override
    public void setDaemonToken(DaemonToken daemonToken) {
        this.daemonToken = daemonToken;
        PihCoreTimerTask.setDaemonToken(daemonToken);
    }
}
