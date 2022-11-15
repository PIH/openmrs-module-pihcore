package org.openmrs.module.pihcore.page.controller.admin;

import org.openmrs.api.context.Context;
import org.openmrs.module.authentication.UserLoginTracker;
import org.openmrs.module.pihcore.PihEmrConfigConstants;
import org.openmrs.ui.framework.page.PageModel;

import java.io.IOException;

/**
 * Administrative page to view currently logged-in users
 */
public class ActiveUsersPageController {

    public String get(PageModel model) throws IOException {
        if (!Context.hasPrivilege(PihEmrConfigConstants.PRIVILEGE_APP_EMR_SYSTEM_ADMINISTRATION)) {
            return "redirect:/index.htm";
        }
        model.addAttribute("activeUsers", UserLoginTracker.getActiveLogins().values());
        return "admin/activeUsers";
    }
}
