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

        if (map != null && !map.isEmpty()) {
            String[] returnUrl = map.get("returnUrl");
            if (returnUrl != null && returnUrl.length > 0) {
                String url = returnUrl[0];
                if (StringUtils.isNotBlank(url)) {
                    String obsUuid = url.substring(url.indexOf("registerBabyObs=") + "registerBabyObs=".length());
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
