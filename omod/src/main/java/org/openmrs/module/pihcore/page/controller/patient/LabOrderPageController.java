package org.openmrs.module.pihcore.page.controller.patient;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Concept;
import org.openmrs.ConceptAnswer;
import org.openmrs.Patient;
import org.openmrs.api.ConceptService;
import org.openmrs.api.EncounterService;
import org.openmrs.api.OrderService;
import org.openmrs.module.emrapi.patient.PatientDomainWrapper;
import org.openmrs.module.pihcore.PihUiUtils;
import org.openmrs.ui.framework.UiUtils;
import org.openmrs.ui.framework.annotation.InjectBeans;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.openmrs.ui.framework.page.PageModel;
import org.openmrs.util.ConfigUtil;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LabOrderPageController {

    private static final Log log = LogFactory.getLog(LabOrderPageController.class);

    public void get(PageModel model, UiUtils ui,
                      @InjectBeans PatientDomainWrapper patientDomainWrapper,
                      @RequestParam(value = "patient") Patient patient,
                      @RequestParam(value = "returnUrl", required = false) String returnUrl,
                      @SpringBean("orderService") OrderService orderService,
                      @SpringBean("encounterService") EncounterService encounterService,
                      @SpringBean("conceptService") ConceptService conceptService) throws IOException {

        String labSetProp = ConfigUtil.getGlobalProperty("orderentryowa.labOrderablesConceptSet");
        Concept labSet = conceptService.getConceptByReference(labSetProp);

        String orderReasonProp = ConfigUtil.getGlobalProperty("orderentryowa.orderReasonsMap");
        Map<String, List<Concept>> orderReasonsMap = new HashMap<>();
        if (StringUtils.isNotBlank(orderReasonProp)) {
            for (String conceptUuidToReasonSet : orderReasonProp.split(",")) {
                String[] split = conceptUuidToReasonSet.split("=");
                if (split.length != 2) {
                    log.warn("Invalid orderReasonMap entry: " + conceptUuidToReasonSet);
                }
                else {
                    Concept reasonConcept = conceptService.getConceptByReference(split[1]);
                    if (reasonConcept == null) {
                        log.warn("Invalid orderReasonMap entry.  Unable to find concept " + split[1]);
                    }
                    else {
                        List<Concept> orderReasons = new ArrayList<>();
                        if (reasonConcept.getSetMembers() != null && !reasonConcept.getSetMembers().isEmpty()) {
                            orderReasons.addAll(reasonConcept.getSetMembers());
                        }
                        else if (reasonConcept.getAnswers() != null && !reasonConcept.getAnswers().isEmpty()) {
                            for (ConceptAnswer ca : reasonConcept.getAnswers()) {
                                orderReasons.add(ca.getAnswerConcept());
                            }
                        }
                        else {
                            log.warn("Invalid orderReasonMap entry: " + split[1] + " has no set members or answers");
                        }
                        orderReasonsMap.put(split[0], orderReasons);
                    }
                }
            }
        }

        patientDomainWrapper.setPatient(patient);
        model.addAttribute("patient", patientDomainWrapper);
        model.addAttribute("labSet", labSet);
        model.addAttribute("orderReasonsMap", orderReasonsMap);
        model.addAttribute("returnUrl", returnUrl);
        model.addAttribute("pihui", new PihUiUtils());
    }
}
