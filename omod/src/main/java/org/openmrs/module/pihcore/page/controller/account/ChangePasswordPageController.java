package org.openmrs.module.pihcore.page.controller.account;


import org.apache.commons.lang.StringUtils;
import org.openmrs.api.UserService;
import org.openmrs.api.context.Context;
import org.openmrs.messagesource.MessageSourceService;
import org.openmrs.module.uicommons.UiCommonsConstants;
import org.openmrs.ui.framework.annotation.BindParams;
import org.openmrs.ui.framework.annotation.MethodParam;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.openmrs.ui.framework.page.PageModel;
import org.springframework.context.MessageSource;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public class ChangePasswordPageController {

    public ChangePassword getChangePassword(@RequestParam(value = "oldPassword", required = false) String oldPassword,
                                            @RequestParam(value = "newPassword", required = false) String newPassword,
                                            @RequestParam(value = "confirmPassword", required = false) String confirmPassword) {
        return new ChangePassword(oldPassword, newPassword, confirmPassword);
    }


    public String post(@MethodParam("getChangePassword") @BindParams ChangePassword changePassword,
                       BindingResult errors,
                       @SpringBean("userService") UserService userService,
                       @SpringBean("messageSourceService") MessageSourceService messageSourceService,
                       @SpringBean("messageSource") MessageSource messageSource,
                       HttpServletRequest request,
                       PageModel model) {

        validatePasswords(changePassword, errors, messageSourceService);

        if (errors.hasErrors()) {
            sendErrorMessage(errors, messageSource, request);
            model.addAttribute("errors", errors);
            return "account/changePassword";
        } else {
            return changePasswords(changePassword, userService, messageSourceService, request);
        }

    }

    private String changePasswords(ChangePassword changePassword, UserService userService, MessageSourceService messageSourceService, HttpServletRequest request) {
        try {
            userService.changePassword(changePassword.getOldPassword(), changePassword.getNewPassword());
            request.getSession().setAttribute(UiCommonsConstants.SESSION_ATTRIBUTE_INFO_MESSAGE,
                    messageSourceService.getMessage("emr.account.changePassword.success", null, Context.getLocale()));
            request.getSession().setAttribute(UiCommonsConstants.SESSION_ATTRIBUTE_TOAST_MESSAGE, "true");
        } catch (Exception e) {
            request.getSession().setAttribute(UiCommonsConstants.SESSION_ATTRIBUTE_ERROR_MESSAGE,
                    messageSourceService.getMessage("emr.account.changePassword.fail", new Object[]{e.getMessage()}, Context.getLocale()));
            return "account/changePassword";
        }

        return "redirect:pihcore/account/myAccount.page";
    }

    private void validatePasswords(ChangePassword changePassword, BindingResult errors, MessageSourceService messageSourceService) {
        if (StringUtils.isBlank(changePassword.getOldPassword())) {
            errors.rejectValue("oldPassword", "emr.account.changePassword.oldPassword.required",
                    new Object[]{messageSourceService.getMessage("emr.account.changePassword.oldPassword.required")}, null);
        }
        if (StringUtils.isBlank(changePassword.getNewPassword()) || StringUtils.isBlank(changePassword.getConfirmPassword())) {
            errors.rejectValue("newPassword", "emr.account.changePassword.newAndConfirmPassword.required",
                    new Object[]{messageSourceService.getMessage("emr.account.changePassword.newAndConfirmPassword.required")}, null);
        } else if (!changePassword.getNewPassword().equals(changePassword.getConfirmPassword())) {
            errors.rejectValue("", "emr.account.changePassword.newAndConfirmPassword.DoesNotMatch",
                    new Object[]{messageSourceService.getMessage("emr.account.changePassword.newAndConfirmPassword.DoesNotMatch")}, null);
        }
    }

    public String get() {
        return "account/changePassword";
    }

    private void sendErrorMessage(BindingResult errors, MessageSource messageSource, HttpServletRequest request) {
        List<ObjectError> allErrors = errors.getAllErrors();
        String message = getMessageErrors(messageSource, allErrors);
        request.getSession().setAttribute(UiCommonsConstants.SESSION_ATTRIBUTE_ERROR_MESSAGE,
                message);
    }

    private String getMessageErrors(MessageSource messageSource, List<ObjectError> allErrors) {
        String message = "";
        for (ObjectError error : allErrors) {
            Object[] arguments = error.getArguments();
            String errorMessage = messageSource.getMessage(error.getCode(), arguments, Context.getLocale());
            message = message.concat(replaceArguments(errorMessage, arguments).concat("<br>"));
        }
        return message;
    }

    private String replaceArguments(String message, Object[] arguments) {
        if (arguments != null) {
            for (int i = 0; i < arguments.length; i++) {
                String argument = (String) arguments[i];
                message = message.replaceAll("\\{" + i + "\\}", argument);
            }
        }
        return message;
    }

    private class ChangePassword {
        private final String oldPassword;
        private final String newPassword;
        private final String confirmPassword;

        public ChangePassword(String oldPassword, String newPassword, String confirmPassword) {
            this.oldPassword = oldPassword;
            this.newPassword = newPassword;
            this.confirmPassword = confirmPassword;
        }

        public String getConfirmPassword() {
            return confirmPassword;
        }

        public String getNewPassword() {
            return newPassword;
        }

        public String getOldPassword() {
            return oldPassword;
        }
    }
}
