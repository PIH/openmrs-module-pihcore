package org.openmrs.module.pihcore.fragment.controller.dashboardwidgets;

import org.openmrs.Concept;
import org.openmrs.Patient;
import org.openmrs.api.ConceptService;
import org.openmrs.api.PatientService;
import org.openmrs.module.appframework.domain.AppDescriptor;
import org.openmrs.module.emrapi.patient.PatientDomainWrapper;
import org.openmrs.module.pihcore.model.Immunization;
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
 * Displays information about immunizations on the patient dashboard
 */
public class ImmunizationsFragmentController {

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

        Map<Concept, Immunization> immunizationsByConcept = new LinkedHashMap<>();
        try {
            List<String> immunizationConceptUuids = (List<String>)config.get("immunizations");
            for (String conceptUuid : immunizationConceptUuids) {
                immunizationsByConcept.put(conceptService.getConceptByUuid(conceptUuid), new Immunization());
            }
        }
        catch (Exception ignored) {}

        // Get all immunizations given and retain the most recent by type in the configured list
        for (Immunization immunization : pihCoreService.getImmunizations(patient)) {
            Concept immunizationTypeValue = immunization.getImmunizationObs().getValueCoded();
            Immunization existing = immunizationsByConcept.get(immunizationTypeValue);
            if (existing != null) {
                Date existingDate = existing.getImmunizationObs() == null ? null : existing.getEffectiveDate();
                if (existingDate == null || existingDate.before(immunization.getEffectiveDate())) {
                    immunizationsByConcept.put(immunizationTypeValue, immunization);
                }
            }
        }

        model.put("immunizationsByConcept", immunizationsByConcept);
    }
}
