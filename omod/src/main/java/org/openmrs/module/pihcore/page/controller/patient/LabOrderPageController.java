package org.openmrs.module.pihcore.page.controller.patient;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.CareSetting;
import org.openmrs.Concept;
import org.openmrs.ConceptAnswer;
import org.openmrs.EncounterRole;
import org.openmrs.EncounterType;
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

        // The orderentryowa hard-codes to the second returned care setting, which is Inpatient, so set this for now
        CareSetting careSetting = orderService.getCareSettingByName("Inpatient");

        // The type of encounter to create for new orders
        String encounterTypeProp = ConfigUtil.getGlobalProperty("orderentryowa.encounterType");
        EncounterType encounterType = null;
        if (StringUtils.isNotBlank(encounterTypeProp)) {
            encounterType = encounterService.getEncounterTypeByUuid(encounterTypeProp);
            if (encounterType == null) {
                encounterType = encounterService.getEncounterType(encounterTypeProp);
            }
        }
        if (encounterType == null) {
            log.warn("Invalid orderEntryowa encounterType configuration: " + encounterTypeProp);
        }

        // The provider role to associate with providers for new encounters
        String encounterRoleProp = ConfigUtil.getGlobalProperty("orderentryowa.encounterRole");
        EncounterRole encounterRole = null;
        if (StringUtils.isNotBlank(encounterRoleProp)) {
            encounterRole = encounterService.getEncounterRoleByUuid(encounterRoleProp);
            if (encounterRole == null) {
                encounterRole = encounterService.getEncounterRoleByName(encounterRoleProp);
            }
        }
        if (encounterRole == null) {
            log.warn("Invalid orderEntryowa encounterRole configuration: " + encounterTypeProp);
        }

        String autoExpireDaysProp = ConfigUtil.getGlobalProperty("orderentryowa.labOrderAutoExpireTimeInDays");
        Integer autoExpireDays = 30;
        if (StringUtils.isNotBlank(autoExpireDaysProp)) {
            try {
                autoExpireDays = Integer.parseInt(autoExpireDaysProp);
            }
            catch (NumberFormatException e) {
                log.warn("Invalid orderEntryowa autoExpireDays: " + autoExpireDaysProp);
            }
        }

        patientDomainWrapper.setPatient(patient);
        model.addAttribute("patient", patientDomainWrapper);
        model.addAttribute("labSet", labSet);
        model.addAttribute("orderReasonsMap", orderReasonsMap);
        model.addAttribute("careSetting", careSetting);
        model.addAttribute("encounterType", encounterType);
        model.addAttribute("encounterRole", encounterRole);
        model.addAttribute("autoExpireDays", autoExpireDays);
        model.addAttribute("returnUrl", returnUrl);
        model.addAttribute("pihui", new PihUiUtils());
    }
}
