package org.openmrs.module.pihcore.fragment.controller.dashboardwidgets;

import org.openmrs.Concept;
import org.openmrs.Patient;
import org.openmrs.api.ConceptService;
import org.openmrs.api.PatientService;
import org.openmrs.module.appframework.domain.AppDescriptor;
import org.openmrs.module.emrapi.patient.PatientDomainWrapper;
import org.openmrs.module.pihcore.model.Vaccination;
import org.openmrs.module.pihcore.service.PihCoreService;
import org.openmrs.ui.framework.UiUtils;
import org.openmrs.ui.framework.annotation.FragmentParam;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.openmrs.ui.framework.fragment.FragmentConfiguration;
import org.openmrs.ui.framework.fragment.FragmentModel;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Displays information about vaccinations on the patient dashboard
 */
public class VaccinationsFragmentController {

    public void controller(@SpringBean("patientService") PatientService patientService,
                           @SpringBean("pihCoreService") PihCoreService pihCoreService,
                           @SpringBean("conceptService") ConceptService conceptService,
                           @FragmentParam("app") AppDescriptor app,
                           UiUtils ui,
                           FragmentConfiguration config,
                           FragmentModel model) throws Exception {

        Object patientConfig = config.get("patient");
        if (patientConfig == null ) {
            patientConfig = config.get("patientId");
        }
        Patient patient = null;
        if (patientConfig != null) {
            if (patientConfig instanceof Patient) {
                patient = (Patient) patientConfig;
            }
            else if (patientConfig instanceof PatientDomainWrapper) {
                patient = ((PatientDomainWrapper) patientConfig).getPatient();
            }
            else if (patientConfig instanceof Integer) {
                patient = patientService.getPatient((Integer)patientConfig);
            }
            else if (patientConfig instanceof String) {
                patient = patientService.getPatientByUuid((String)patientConfig);
            }
        }
        if (patient == null) {
            throw new IllegalArgumentException("No patient found. Please pass patient into the configuration");
        }
        model.put("patient", patient);
        model.put("app", app);

        Map<Concept, Vaccination> vaccinationsByConcept = new LinkedHashMap<>();
        try {
            List<String> vaccinationConceptUuids = (List<String>)config.get("vaccinations");
            for (String conceptUuid : vaccinationConceptUuids) {
                vaccinationsByConcept.put(conceptService.getConceptByUuid(conceptUuid), new Vaccination());
            }
        }
        catch (Exception ignored) {}

        // Get all vaccinations given and retain the most recent by type in the configured list
        for (Vaccination vaccination : pihCoreService.getVaccinations(patient)) {
            Concept vaccinationTypeValue = vaccination.getVaccinationObs().getValueCoded();
            Vaccination existing = vaccinationsByConcept.get(vaccinationTypeValue);
            if (existing != null) {
                Date existingDate = existing.getVaccinationObs() == null ? null : existing.getEffectiveDate();
                if (existingDate == null || existingDate.before(vaccination.getEffectiveDate())) {
                    vaccinationsByConcept.put(vaccinationTypeValue, vaccination);
                }
            }
        }

        model.put("vaccinationsByConcept", vaccinationsByConcept);
    }
}
