package org.openmrs.module.pihcore.action;

import java.util.Map;
import org.apache.commons.lang.StringUtils;
import org.openmrs.Concept;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.api.ConceptService;
import org.openmrs.api.ObsService;
import org.openmrs.module.registrationapp.action.AfterPatientCreatedAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * This action that is triggered after a patient registration is used to update an obs in the Labour and Delivery form.
 * The Labour and Delivery obs that gets updated here indicates whether the baby delivery was registered as a new patient in the EMR.
 *  SL-618
 */
@Component("registerBaby")
public class RegisterBaby implements AfterPatientCreatedAction {
    private final Logger log = LoggerFactory.getLogger(getClass());

    private static String YES_CONCEPT = "1065";
    private static String NO_CONCEPT = "1066";

    @Autowired
    private ObsService obsService;

    @Autowired
    private ConceptService conceptService;

    @Override
    public void afterPatientCreated(Patient patient, Map<String, String[]> map) {
        Concept noConcept = conceptService.getConceptByMapping(NO_CONCEPT, "CIEL");
        Concept yesConcept = conceptService.getConceptByMapping(YES_CONCEPT, "CIEL");
        String parameterName = "registerBabyObs=";

        if (map != null && !map.isEmpty()) {
            // the Obs UUID that we need to update has been added to the returnURL
            String[] returnUrl = map.get("returnUrl");
            if (returnUrl != null && returnUrl.length > 0) {
                String url = returnUrl[0];
                if (StringUtils.isNotBlank(url)) {
                    // just grab the UUID string which is 36 characters long
                    int parameterIndex = url.indexOf(parameterName);
                    if (parameterIndex >= 0 ) {
                        if (url.length() >= parameterIndex + parameterName.length() + 36) {
                            // if the url string length is at least as long as the parameter we are looking for
                            // e.g. pihcore/children/children.page?patientId=27bb698c-a374-47d4-8585-6d4b633dd1d7&registerBabyObs=3f495910-3a28-4aa3-864f-11f7b8fbacdb
                            // where 36 is the length of an UUID generated string
                            String obsUuid = url.substring(parameterIndex + parameterName.length(), parameterIndex + parameterName.length() + 36);
                            // extract just the UUID, in the future there might be additional parameters tacked on to the end of the returnURL
                            if (StringUtils.isNotBlank(obsUuid)) {
                                Obs registerBabyObs = obsService.getObsByUuid(obsUuid);
                                if (registerBabyObs != null) {
                                    if (registerBabyObs.getValueCoded().equals(noConcept) ) {
                                        registerBabyObs.setValueCoded(yesConcept);
                                        registerBabyObs.setComment(patient.getUuid());
                                        obsService.saveObs(registerBabyObs, "baby is registered");
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
