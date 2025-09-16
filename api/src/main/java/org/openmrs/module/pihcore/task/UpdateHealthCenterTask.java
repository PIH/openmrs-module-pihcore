package org.openmrs.module.pihcore.task;

import org.apache.commons.lang.time.StopWatch;
import org.openmrs.Patient;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;
import org.openmrs.module.pihcore.config.Config;
import org.openmrs.module.pihcore.config.ConfigDescriptor;
import org.openmrs.module.pihcore.service.PihCoreService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class UpdateHealthCenterTask implements Runnable {

    private final Logger log = LoggerFactory.getLogger(getClass());

    private static boolean isExecuting = false;

    @Override
    public void run() {

        log.info("Executing UpdateHealthCenterTask");

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
                PihCoreService pihCoreService = Context.getService(PihCoreService.class);
                PatientService patientService = Context.getService(PatientService.class);

                List<Patient> patients = patientService.getAllPatients();
                Set<Integer> patientIds = new HashSet<>();
                for(Patient patient : patients) {
                    patientIds.add(patient.getId());
                }

                int totalPatientCount = patientIds.size();
                log.info("Found {} patients", totalPatientCount);

                int i = 0;

                for (Integer patientId : patientIds) {
                    pihCoreService.updateHealthCenter(patientService.getPatient(patientId));
                    i++;
                    if (i % 10 == 0) {
                        log.info("Updated {} of {} patients, flushing session", i, totalPatientCount);
                        Context.flushSession();
                        Context.clearSession();
                    }
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
