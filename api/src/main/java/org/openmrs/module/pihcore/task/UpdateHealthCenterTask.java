package org.openmrs.module.pihcore.task;

import org.apache.commons.lang.time.StopWatch;
import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.module.pihcore.config.Config;
import org.openmrs.module.pihcore.config.ConfigDescriptor;
import org.openmrs.module.pihcore.service.PihCoreService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UpdateHealthCenterTask implements Runnable {

    private final Logger log = LoggerFactory.getLogger(getClass());

    private static boolean isExecuting = false;

    @Override
    public void run() {

        if (isExecuting) {
            log.info("Not executing " + getClass() + " because another instance is already executing");
            return;
        }

        isExecuting = true;

        try {
            Config config = Context.getRegisteredComponent("config", Config.class);
            if (config.isHaiti() && ConfigDescriptor.Specialty.HIV.equals(config.getSpecialty())) {
                log.info("Executing " + getClass());
                StopWatch stopWatch = new StopWatch();
                stopWatch.start();

                for (Patient paitent : Context.getPatientService().getAllPatients()) {
                    Context.getService(PihCoreService.class).updateHealthCenter(paitent);
                }

                stopWatch.stop();
                log.info("Execution of " + getClass() + " completed in {}", stopWatch);
            }
            else {
                log.info("UpdateHealthCenterTask not enabled, skipping");
            }
        } catch (Exception e) {
            log.error("Error executing " + getClass(), e);
        } finally {
            isExecuting = false;
        }

    }
}
