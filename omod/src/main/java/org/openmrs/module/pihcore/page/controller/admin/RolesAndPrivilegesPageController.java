package org.openmrs.module.pihcore.page.controller.admin;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.context.Context;
import org.openmrs.module.emrapi.account.AccountService;
import org.openmrs.module.pihcore.PihEmrConfigConstants;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.openmrs.ui.framework.page.PageModel;

public class RolesAndPrivilegesPageController {

    protected static final Log log = LogFactory.getLog(RolesAndPrivilegesPageController.class);

    public String get(
            PageModel pageModel,
            @SpringBean("accountService") AccountService accountService) {

        pageModel.addAttribute("capabilities", accountService.getAllCapabilities());
        if (!Context.hasPrivilege(PihEmrConfigConstants.PRIVILEGE_APP_EMR_SYSTEM_ADMINISTRATION)) {
            return "redirect:/index.htm";
        }
        return "admin/rolesAndPrivileges";
    }
}
