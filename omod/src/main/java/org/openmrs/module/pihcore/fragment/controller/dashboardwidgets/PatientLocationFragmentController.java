package org.openmrs.module.pihcore.fragment.controller.dashboardwidgets;


import org.openmrs.Patient;
import org.openmrs.api.PatientService;
import org.openmrs.module.appframework.domain.AppDescriptor;
import org.openmrs.module.appui.UiSessionContext;
import org.openmrs.module.emrapi.adt.AdtService;
import org.openmrs.module.emrapi.patient.PatientDomainWrapper;
import org.openmrs.module.emrapi.visit.VisitDomainWrapper;
import org.openmrs.module.pihcore.PihCoreUtils;
import org.openmrs.ui.framework.SimpleObject;
import org.openmrs.ui.framework.UiUtils;
import org.openmrs.ui.framework.annotation.FragmentParam;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.openmrs.ui.framework.fragment.FragmentConfiguration;
import org.openmrs.ui.framework.fragment.FragmentModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PatientLocationFragmentController {
    private final Logger log = LoggerFactory.getLogger(getClass());

    public void controller(@SpringBean("patientService") PatientService patientService,
                           @SpringBean("adtService") AdtService adtService,
                           @FragmentParam("app") AppDescriptor app,
                           UiSessionContext uiSessionContext,
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
        VisitDomainWrapper activeVisit = adtService.getActiveVisit(patient, uiSessionContext.getSessionLocation());
        SimpleObject inpatientLocation = PihCoreUtils.getInpatientLocation(patient, activeVisit != null ?  activeVisit.getVisit() : null, adtService, ui);

        model.put("patient", patient);
        model.put("app", app);
        model.put("patientStatus", inpatientLocation.get("patientStatus"));
        model.put("inpatientLocation", inpatientLocation.get("currentInpatientLocation"));
        model.put("queueName", inpatientLocation.get("queueName"));
    }
}