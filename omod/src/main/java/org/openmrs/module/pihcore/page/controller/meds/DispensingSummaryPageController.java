package org.openmrs.module.pihcore.page.controller.meds;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.MedicationDispense;
import org.openmrs.Patient;
import org.openmrs.api.MedicationDispenseService;
import org.openmrs.module.appui.UiSessionContext;
import org.openmrs.module.dispensing.DispensedMedication;
import org.openmrs.module.dispensing.api.DispensingService;
import org.openmrs.module.emrapi.patient.PatientDomainWrapper;
import org.openmrs.parameter.MedicationDispenseCriteria;
import org.openmrs.ui.framework.UiUtils;
import org.openmrs.ui.framework.annotation.InjectBeans;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.openmrs.ui.framework.page.PageModel;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DispensingSummaryPageController {
    private static final Log log = LogFactory.getLog(DispensingSummaryPageController.class);

    public void controller(@RequestParam("patientId") Patient patient,
                           UiUtils ui,
                           UiSessionContext emrContext,
                           PageModel model,
                           @SpringBean("medicationDispenseService") MedicationDispenseService medicationDispenseService,
                           @SpringBean("dispensingService") DispensingService dispensingService,
                           @InjectBeans PatientDomainWrapper patientDomainWrapper) {

        patientDomainWrapper.setPatient(patient);

        List<Map<String, Object>> dispenses = new ArrayList<>();

        // Get all legacy Med Dispensed records from the Dispensing module
        List<DispensedMedication> dispensedMedications = dispensingService.getDispensedMedication(patient, null, null, null, null, null);
        if (dispensedMedications != null) {
            for (DispensedMedication m : dispensedMedications) {
                Map<String, Object> dispense = new HashMap<>();
                dispense.put("dispenseDate", m.getDispensedDateTime());
                dispense.put("drug", m.getDrug());
                StringBuilder dose = new StringBuilder();
                if (m.getMedicationDose() != null && m.getMedicationDose().getDose() != null) {
                    dose.append(ui.format(m.getMedicationDose().getDose()));
                    if (m.getMedicationDose().getUnits() != null) {
                        dose.append(" ").append(m.getMedicationDose().getUnits());
                    }
                }
                dispense.put("dose", dose.toString());
                dispense.put("frequency", (m.getMedicationFrequency() == null ? "" : m.getMedicationFrequency().getFrequency()));
                StringBuilder duration = new StringBuilder();
                if (m.getMedicationDuration() != null && m.getMedicationDuration().getDuration() != null) {
                    duration.append(m.getMedicationDuration().getDuration());
                    if (m.getMedicationDuration().getTimeUnits() != null) {
                        duration.append(" ").append(m.getMedicationDuration().getTimeUnits());
                    }
                }
                dispense.put("duration", duration.toString());
                dispense.put("quantity", m.getQuantityDispensed());
                StringBuilder origin = new StringBuilder();
                if (StringUtils.isNotBlank(m.getTimingOfHospitalPrescription())) {
                    origin.append(m.getTimingOfHospitalPrescription());
                }
                if (m.getDischargeLocation() != null) {
                    if (origin.length() > 0) {
                        origin.append(" - ");
                    }
                    origin.append(ui.format(m.getDischargeLocation()));
                }
                dispense.put("origin", origin.toString());
                dispense.put("instructions", m.getAdministrationInstructions());

                dispenses.add(dispense);
            }
        }

        // Get all Med Dispensed records from the Domain
        MedicationDispenseCriteria criteria = new MedicationDispenseCriteria();
        criteria.setPatient(patient);
        criteria.setIncludeVoided(false);
        List<MedicationDispense> medicationDispenses = medicationDispenseService.getMedicationDispenseByCriteria(criteria);
        for (MedicationDispense m : medicationDispenses) {
            if (m.getDateHandedOver() != null) {
                Map<String, Object> dispense = new HashMap<>();
                dispense.put("dispenseDate", m.getDateHandedOver());
                dispense.put("drug", m.getDrug());
                StringBuilder dose = new StringBuilder();
                if (m.getDose() != null) {
                    dose.append(m.getDose());
                    if (m.getDoseUnits() != null) {
                        dose.append(" ").append(ui.format(m.getDoseUnits()));
                    }
                }
                dispense.put("dose", dose.toString());
                dispense.put("frequency", ui.format(m.getFrequency()));
                StringBuilder duration = new StringBuilder();
                dispense.put("duration", "");  // TODO: Do we need to populate this?
                StringBuilder quantity = new StringBuilder();
                if (m.getQuantity() != null) {
                    quantity.append(m.getQuantity());
                    if (m.getQuantityUnits() != null) {
                        quantity.append(" ").append(ui.format(m.getQuantityUnits()));
                    }
                }
                dispense.put("quantity", quantity);
                dispense.put("origin", "");  // TODO: Do we need to populate this?
                dispense.put("instructions", m.getDosingInstructions());
                dispenses.add(dispense);
            }
        }

        // Now, sort these descending by dispense date
        dispenses.sort((m1, m2) -> {
            Date d1 = (Date) m1.get("dispenseDate");
            Date d2 = (Date) m2.get("dispenseDate");
            return d2.compareTo(d1);
        });

        model.addAttribute("dispenses", dispenses);
        model.addAttribute("patient", patientDomainWrapper);

    }
}
