package org.openmrs.module.pihcore.page.controller.admin;

import org.openmrs.api.PatientService;
import org.openmrs.module.pihcore.status.StatusDataEvaluator;
import org.openmrs.ui.framework.UiUtils;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.openmrs.ui.framework.page.PageModel;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;

/**
 * Administrative tool to facilitate authoring and testing status data
 */
public class StatusAdminPageController {


    public void get(PageModel model, UiUtils ui,
                      @RequestParam(required = false, value = "patientId") String patientId,
                      @SpringBean("statusDataEvaluator") StatusDataEvaluator statusDataEvaluator,
                      @SpringBean("patientService") PatientService patientService) throws IOException {

        model.addAttribute("patientId", patientId);
    }
}
