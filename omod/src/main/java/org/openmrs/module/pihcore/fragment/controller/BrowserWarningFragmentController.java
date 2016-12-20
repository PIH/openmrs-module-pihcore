package org.openmrs.module.pihcore.fragment.controller;

import org.openmrs.module.pihcore.config.Config;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.openmrs.ui.framework.page.PageModel;

public class BrowserWarningFragmentController {

    public void controller(PageModel pageModel, @SpringBean Config config) {
        pageModel.put("browserWarning", config.getBrowserWarning());
    }

}
