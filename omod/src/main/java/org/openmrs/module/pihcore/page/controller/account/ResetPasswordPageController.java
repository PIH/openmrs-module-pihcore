package org.openmrs.module.pihcore.page.controller.account;


import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.User;
import org.openmrs.api.InvalidActivationKeyException;
import org.openmrs.api.UserService;
import org.openmrs.api.ValidationException;
import org.openmrs.api.context.Context;
import org.openmrs.module.emr.EmrConstants;
import org.openmrs.ui.framework.UiUtils;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.openmrs.ui.framework.page.PageModel;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;

import static org.openmrs.util.PrivilegeConstants.GET_USERS;

public class ResetPasswordPageController {

    protected final Log log = LogFactory.getLog(getClass());

    public String get(@RequestParam(value = "activationKey", required = false) String activationKey,
                      @SpringBean("userService") UserService userService,
                      HttpServletRequest request,
                      PageModel model, UiUtils ui) {
        try {
            Context.addProxyPrivilege(GET_USERS);
            User user = userService.getUserByActivationKey(activationKey);
            if (user == null) {
                request.getSession().setAttribute(EmrConstants.SESSION_ATTRIBUTE_ERROR_MESSAGE,
                        ui.message("activation.key.not.correct", null, Context.getLocale()));
                return "redirect:index.htm";
            }
            Context.setLocale(userService.getDefaultLocaleForUser(user));
            model.addAttribute("activationKey", activationKey);
            return null;
        }
        finally {
            Context.removeProxyPrivilege(GET_USERS);
        }
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
            Context.addProxyPrivilege(GET_USERS);
            User user = userService.getUserByActivationKey(activationKey);
            if (user == null) {
                throw new InvalidActivationKeyException("activation.key.not.correct");
            }
            Context.setLocale(userService.getDefaultLocaleForUser(user));
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
        finally {
            Context.removeProxyPrivilege(GET_USERS);
        }

        return "redirect:index.htm";
    }
}
