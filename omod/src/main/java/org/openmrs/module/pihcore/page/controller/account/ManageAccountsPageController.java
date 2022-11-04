package org.openmrs.module.pihcore.page.controller.account;

import org.openmrs.api.context.Context;
import org.openmrs.module.emrapi.account.AccountService;
import org.openmrs.module.pihcore.PihEmrConfigConstants;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.openmrs.ui.framework.page.PageModel;

public class ManageAccountsPageController {

    public String get(PageModel model,
                    @SpringBean("accountService") AccountService accountService) {
        if (!Context.hasPrivilege(PihEmrConfigConstants.PRIVILEGE_APP_EMR_SYSTEM_ADMINISTRATION)) {
             return "redirect:/index.htm";
        }
        model.addAttribute("accounts", accountService.getAllAccounts());
        return "account/manageAccounts";
    }

}
