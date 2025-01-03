package org.openmrs.module.pihcore.fragment.controller.dashboardwidgets;

import lombok.Data;
import org.apache.commons.lang.BooleanUtils;
import org.openmrs.Concept;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.api.ConceptService;
import org.openmrs.api.ObsService;
import org.openmrs.api.PatientService;
import org.openmrs.module.appframework.domain.AppDescriptor;
import org.openmrs.module.emrapi.patient.PatientDomainWrapper;
import org.openmrs.module.pihcore.PihCoreConstants;
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
                           @SpringBean("obsService") ObsService obsService,
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
        Concept immunizationConcept = conceptService.getConceptByUuid(PihCoreConstants.IMMUNIZATION_TYPE_CONCEPT_UUID);
        List<Obs> immunizationList = obsService.getObservationsByPersonAndConcept(patient, immunizationConcept);
        for (Obs obs : immunizationList) {
            Concept immunizationTypeValue = obs.getValueCoded();
            Immunization existing = immunizationsByConcept.get(immunizationTypeValue);
            // Only include obs that match one of the configured concepts
            if (existing != null) {
                Immunization candidate = new Immunization();
                candidate.setImmunizationObs(obs);
                if (obs.getObsGroup() != null) {
                    candidate.setGroupObs(obs.getObsGroup());
                    for (Obs sibling : obs.getObsGroup().getGroupMembers()) {
                        if (BooleanUtils.isNotTrue(sibling.getVoided())) {
                            String siblingConcept = sibling.getConcept().getUuid();
                            if (siblingConcept.equalsIgnoreCase(PihCoreConstants.IMMUNIZATION_NUM_CONCEPT_UUID)) {
                                candidate.setSequenceNumberObs(sibling);
                            } else if (siblingConcept.equalsIgnoreCase(PihCoreConstants.IMMUNIZATION_DATE_CONCEPT_UUID)) {
                                candidate.setDateObs(sibling);
                            }
                        }
                    }
                }
                Date existingDate = existing.getImmunizationObs() == null ? null : existing.getEffectiveDate();
                if (existingDate == null || existingDate.before(candidate.getEffectiveDate())) {
                    immunizationsByConcept.put(immunizationTypeValue, candidate);
                }
            }
        }

        model.put("immunizationsByConcept", immunizationsByConcept);
    }

    @Data
    public static class Immunization {
        private Obs groupObs;
        private Obs immunizationObs;
        private Obs sequenceNumberObs;
        private Obs dateObs;

        public Date getEffectiveDate() {
            return dateObs != null ? dateObs.getValueDatetime() : immunizationObs.getObsDatetime();
        }
    }
}
