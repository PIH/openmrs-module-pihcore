package org.openmrs.module.pihcore.page.controller.children;

import org.apache.commons.lang3.StringUtils;
import org.openmrs.Patient;
import org.openmrs.module.appui.UiSessionContext;
import org.openmrs.module.reporting.common.ObjectUtil;
import org.openmrs.ui.framework.UiUtils;
import org.openmrs.ui.framework.page.PageModel;
import org.springframework.web.bind.annotation.RequestParam;

public class ChildrenPageController {

    public void controller(PageModel model, UiUtils ui, UiSessionContext uiSessionContext,
                           @RequestParam("patientId") Patient patient,
                           @RequestParam(value="returnUrl", required=false) String returnUrl
                           ) {

        if (StringUtils.isBlank(returnUrl)) {
            returnUrl = ui.pageLink("coreapps", "clinicianfacing/patient", ObjectUtil.toMap("patientId", patient.getUuid()));
        }
        model.addAttribute("patient", patient);
        model.addAttribute("returnUrl", returnUrl);
    }
}
