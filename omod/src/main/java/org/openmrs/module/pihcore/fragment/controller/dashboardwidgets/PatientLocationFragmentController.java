package org.openmrs.module.pihcore.fragment.controller.dashboardwidgets;


import org.openmrs.Patient;
import org.openmrs.Visit;
import org.openmrs.VisitAttribute;
import org.openmrs.VisitAttributeType;
import org.openmrs.api.PatientService;
import org.openmrs.api.VisitService;
import org.openmrs.api.context.Context;
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
    private static String BORN_DURING_VISIT_ATTRIBUTE_TYPE = "86f716fc-5e26-4eb1-9484-46370cff28f0";
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
        model.put("admissionStatus", getAdmissionStatus(activeVisit, ui));
    }

    /**
     * Determines the admission status (inborn vs outborn) based on the "born during visit" attribute
     *
     * @param activeVisit the active visit wrapper, may be null
     * @param ui the UI utils for message localization
     * @return localized admission status message
     */
    private String getAdmissionStatus(VisitDomainWrapper activeVisit, UiUtils ui) {
        String admissionStatus = ui.message("pihcore.outborn");
        if (activeVisit != null && activeVisit.getVisit() != null) {
            Visit visit = activeVisit.getVisit();
            VisitAttributeType visitAttributeType = Context.getVisitService().getVisitAttributeTypeByUuid(BORN_DURING_VISIT_ATTRIBUTE_TYPE);
            if (visitAttributeType != null) {
                for (VisitAttribute attribute : visit.getActiveAttributes()) {
                    if (attribute.getAttributeType().equals(visitAttributeType)) {
                        Object value = attribute.getValue();
                        if (value != null && value instanceof Boolean && (Boolean)value == true) {
                            admissionStatus = ui.message("pihcore.inborn");
                        }
                        break;
                    }
                }
            }
        }
        return admissionStatus;
    }
}