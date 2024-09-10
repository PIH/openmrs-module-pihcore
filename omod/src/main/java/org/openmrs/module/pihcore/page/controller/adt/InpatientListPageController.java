package org.openmrs.module.pihcore.page.controller.adt;

import org.apache.commons.lang.time.StopWatch;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Encounter;
import org.openmrs.Location;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifier;
import org.openmrs.PatientIdentifierType;
import org.openmrs.api.PatientService;
import org.openmrs.module.coreapps.CoreAppsProperties;
import org.openmrs.module.emrapi.EmrApiProperties;
import org.openmrs.module.emrapi.adt.AdtService;
import org.openmrs.module.emrapi.adt.InpatientAdmission;
import org.openmrs.module.emrapi.adt.InpatientAdmissionSearchCriteria;
import org.openmrs.module.pihcore.PihEmrConfigConstants;
import org.openmrs.module.pihcore.ZlConfigConstants;
import org.openmrs.module.reporting.common.ObjectUtil;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.openmrs.ui.framework.page.PageModel;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class InpatientListPageController {
    private final Log log = LogFactory.getLog(getClass());

    public void get(PageModel model,
                    @RequestParam(value = "visitLocation", required = false) Location visitLocation,
                    @RequestParam(value = "currentInpatientLocation", required = false) List<Location> currentInpatientLocations,
                    @RequestParam(value = "includeDischarged", required = false, defaultValue = "false") boolean includeDischarged,
                    @SpringBean("coreAppsProperties") CoreAppsProperties coreAppsProperties,
                    @SpringBean("patientService") PatientService patientService,
                    @SpringBean("adtService") AdtService adtService,
                    @SpringBean("emrApiProperties") EmrApiProperties emrApiProperties) throws EvaluationException {

        InpatientAdmissionSearchCriteria criteria = new InpatientAdmissionSearchCriteria();
        criteria.setVisitLocation(visitLocation);
        if (currentInpatientLocations != null && !currentInpatientLocations.isEmpty()) {
            criteria.setCurrentInpatientLocations(currentInpatientLocations);
        }
        criteria.setIncludeDischarged(includeDischarged);

        PatientIdentifierType dossierNumber = patientService.getPatientIdentifierTypeByUuid(ZlConfigConstants.PATIENTIDENTIFIERTYPE_DOSSIERNUMBER_UUID);
        model.addAttribute("dossierNumberDefinitionAvailable", dossierNumber != null);

        log.warn("Searching for inpatient admissions");
        StopWatch sw = new StopWatch();
        sw.start();
        List<InpatientAdmission> admissions = adtService.getInpatientAdmissions(criteria);
        sw.stop();
        log.warn("Found " + admissions.size() + " inpatient admissions in " + sw);

        sw.reset();
        sw.start();
        List<Map<String, Object>> rows = new ArrayList<>();
        for (InpatientAdmission admission : admissions) {
            Patient patient = admission.getPatient();
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("patientId", patient.getId());
            row.put("familyName", patient.getPersonName().getFamilyName());
            row.put("givenName", patient.getPersonName().getGivenName());
            row.put("primaryIdentifier", getIdentifier(patient, emrApiProperties.getPrimaryIdentifierType()));
            if (dossierNumber != null) {
                row.put("dossierNumber", getIdentifier(patient, dossierNumber));
            }
            Encounter firstEncounter = admission.getFirstAdmissionOrTransferEncounter();
            Encounter latestEncounter = admission.getLatestAdmissionOrTransferEncounter();
            row.put("firstAdmittedLocation", ObjectUtil.format(firstEncounter.getLocation()));
            row.put("admissionDateTime", firstEncounter.getEncounterDatetime());
            row.put("inpatientLocation", ObjectUtil.format(latestEncounter.getLocation()));
            row.put("inpatientDateTime", latestEncounter.getEncounterDatetime());
            rows.add(row);
        }

        model.addAttribute("inpatientsList", rows);
        model.addAttribute("dashboardUrl", coreAppsProperties.getDashboardUrl());
        model.put("privilegePatientDashboard", PihEmrConfigConstants.PRIVILEGE_APP_COREAPPS_PATIENT_DASHBOARD);  // used to determine if we display links to patient dashboard)
    }

    protected String getIdentifier(Patient patient, PatientIdentifierType type) {
        if (type != null) {
            PatientIdentifier pi = patient.getPatientIdentifier(type);
            if (pi != null) {
                return pi.getIdentifier();
            }
        }
        return "";
    }
}
