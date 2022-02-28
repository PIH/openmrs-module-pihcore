package org.openmrs.module.pihcore.page.controller.account;


import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.UserService;
import org.openmrs.api.ValidationException;
import org.openmrs.api.context.Context;
import org.openmrs.module.emr.EmrConstants;
import org.openmrs.ui.framework.UiUtils;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.openmrs.ui.framework.page.PageModel;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;

public class ResetPasswordPageController {

    protected final Log log = LogFactory.getLog(getClass());

    public void get(@RequestParam(value = "activationKey", required = false) String activationKey, PageModel model) {
        model.addAttribute("activationKey", activationKey);
    }

    public String post(@RequestParam(value = "activationKey") String activationKey,
                       @RequestParam(value = "newPassword") String newPassword,
                       @RequestParam(value = "confirmPassword") String confirmPassword,
                       @SpringBean("userService") UserService userService,
                       HttpServletRequest request,
                       PageModel model, UiUtils ui) {

        model.addAttribute("activationKey", activationKey);
        model.addAttribute("newPassword", newPassword);
        model.addAttribute("confirmPassword", confirmPassword);

        try {
            if (StringUtils.isBlank(newPassword) || StringUtils.isBlank(confirmPassword)) {
                throw new ValidationException(ui.message("emr.account.changePassword.newAndConfirmPassword.required"));
            }
            else if (!newPassword.equals(confirmPassword)) {
                throw new ValidationException(ui.message("emr.account.changePassword.newAndConfirmPassword.DoesNotMatch"));
            }
            userService.changePasswordUsingActivationKey(activationKey, newPassword);
            request.getSession().setAttribute(EmrConstants.SESSION_ATTRIBUTE_INFO_MESSAGE, ui.message("emr.account.changePassword.success"));
            request.getSession().setAttribute(EmrConstants.SESSION_ATTRIBUTE_TOAST_MESSAGE, "true");
        }
        catch (Exception e) {
            request.getSession().setAttribute(
                    EmrConstants.SESSION_ATTRIBUTE_ERROR_MESSAGE,
                    ui.message("emr.account.changePassword.fail", new Object[]{e.getMessage()}, Context.getLocale())
            );
            log.warn("An error occurred while trying to reset password", e);
        }

        return "redirect:index.htm";
    }
}
