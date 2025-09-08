package org.openmrs.module.pihcore.fragment.controller.dashboardwidgets;

import org.openmrs.Patient;
import org.openmrs.api.PatientService;
import org.openmrs.module.appframework.domain.AppDescriptor;
import org.openmrs.module.emrapi.patient.PatientDomainWrapper;
import org.openmrs.ui.framework.annotation.FragmentParam;
import org.openmrs.ui.framework.annotation.InjectBeans;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.openmrs.ui.framework.fragment.FragmentConfiguration;
import org.openmrs.ui.framework.fragment.FragmentModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.openmrs.PatientIdentifier;
import org.openmrs.PatientIdentifierType;
import org.openmrs.module.pihcore.ZlConfigConstants;
import org.openmrs.api.context.Context;
import java.util.Date;

import java.util.List;

public class FingerprintFragmentController {

	private final Logger log = LoggerFactory.getLogger(getClass());

	public void controller(@SpringBean("patientService") PatientService patientService,
						   @InjectBeans PatientDomainWrapper patientWrapper,
						   @FragmentParam("app") AppDescriptor app,
						   FragmentConfiguration config,
						   FragmentModel model) {

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
			throw new IllegalArgumentException("No patient found in the status data fragment.  Please pass patient into the configuration");
		}
		
		 // Look up Biometrics Reference Code identifier type
        PatientIdentifierType fingerprintIdType = Context.getPatientService()
                .getPatientIdentifierTypeByUuid(ZlConfigConstants.PATIENTIDENTIFIERTYPE_BIOMETRICSREFERENCECODE_UUID);

        PatientIdentifier fingerprintIdentifier = patient.getPatientIdentifier(fingerprintIdType);

        if (fingerprintIdentifier != null) {
            Date collectedDate = fingerprintIdentifier.getDateCreated();
            model.put("fingerprintMessage", "zl.fingerprint.collected");
            model.put("fingerprintDateCollected", collectedDate);
        } else {
            model.put("fingerprintMessage", "zl.fingerprint.not.collected");
			model.put("fingerprintDateCollected", null);
        }

        model.put("app",  app);
	}
}
