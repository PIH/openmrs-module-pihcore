package org.openmrs.module.pihcore.fragment.controller.hiv;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Patient;
import org.openmrs.PatientProgram;
import org.openmrs.Program;
import org.openmrs.api.ProgramWorkflowService;
import org.openmrs.module.appui.UiSessionContext;
import org.openmrs.module.emrapi.patient.PatientDomainWrapper;
import org.openmrs.module.pihcore.PihEmrConfigConstants;
import org.openmrs.ui.framework.UiUtils;
import org.openmrs.ui.framework.annotation.FragmentParam;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.openmrs.ui.framework.fragment.FragmentModel;

import java.util.List;

public class ProgramLocationWarningFragmentController {

    protected final Log log = LogFactory.getLog(getClass());

    public void controller(FragmentModel model,
                           @FragmentParam(required = false, value="patient") Object patientObject,
                           @SpringBean("programWorkflowService") ProgramWorkflowService programWorkflowService,
                           UiSessionContext uiSessionContext,
                           UiUtils ui) {

        model.addAttribute("sessionLocation", uiSessionContext.getSessionLocation());
        if (patientObject != null ) {
            Patient patient = ((PatientDomainWrapper) patientObject).getPatient();
            Program hivProgram = programWorkflowService.getProgramByUuid(PihEmrConfigConstants.PROGRAM_HIV_UUID);
            if (patient !=null && hivProgram !=null) {
                List<PatientProgram> patientPrograms = programWorkflowService.getPatientPrograms(patient, hivProgram, null, null, null, null, false);
                if (patientPrograms != null && patientPrograms.size() > 0 ) {
                    model.addAttribute("hivProgramLocation", patientPrograms.get(0).getLocation());
                }
            }
        }
    }
}


