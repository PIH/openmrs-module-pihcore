package org.openmrs.module.pihcore.page.controller;

import org.openmrs.Patient;
import org.openmrs.PatientIdentifier;
import org.openmrs.PatientIdentifierType;
import org.openmrs.api.PatientService;
import org.openmrs.ui.framework.UiUtils;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.openmrs.ui.framework.converter.util.ConversionUtil;
import org.openmrs.ui.framework.page.PageModel;
import org.openmrs.ui.framework.page.Redirect;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PatientPageController {

    public Object controller(PageModel model, UiUtils ui,
                      @RequestParam(required = true, value = "patientId") String patientId,
                      @RequestParam(required = false, value = "identifierType") String identifierType,
                      @RequestParam(required = false, value = "dashboard") String dashboard,
                      @SpringBean("patientService") PatientService patientService) throws IOException {

        Patient p;
        if (identifierType == null) {
            if (ConversionUtil.onlyDigits(patientId)) {
                p = patientService.getPatient(Integer.valueOf(patientId));
            }
            else {
                p = patientService.getPatientByUuid(patientId);
            }
            if (p == null) {
                throw new IllegalArgumentException("Unable to find patient with patientId: " + patientId);
            }
        }
        else {
            PatientIdentifierType piType = patientService.getPatientIdentifierTypeByUuid(identifierType);
            List<PatientIdentifier> identifiers = patientService.getPatientIdentifiers(patientId, Arrays.asList(piType), null, null, null);
            Set<Patient> patientsFound = new HashSet<>();
            for (PatientIdentifier pi : identifiers) {
                patientsFound.add(pi.getPatient());
            }
            if (patientsFound.size() == 1) {
                p = patientsFound.iterator().next();
            }
            else {
                throw new IllegalArgumentException("Found " + patientsFound.size() + " with " + identifierType + " = " + patientId);
            }
        }

        if ("registration".equalsIgnoreCase(dashboard)) {
            return new Redirect("registrationapp", "registrationSummary", "patientId=" + p.getUuid());
        }
        else {
            return new Redirect("coreapps", "clinicianfacing/patient", "patientId=" + p.getUuid() + "&dashboard=" + dashboard);
        }
    }
}
