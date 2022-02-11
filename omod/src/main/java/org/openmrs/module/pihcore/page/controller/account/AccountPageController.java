/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
package org.openmrs.module.pihcore.page.controller.account;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.validator.routines.EmailValidator;
import org.openmrs.Person;
import org.openmrs.User;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.UserService;
import org.openmrs.api.context.Context;
import org.openmrs.messagesource.MessageSourceService;
import org.openmrs.module.emr.EmrConstants;
import org.openmrs.module.emrapi.EmrApiConstants;
import org.openmrs.module.emrapi.account.AccountService;
import org.openmrs.module.emrapi.account.AccountValidator;
import org.openmrs.module.pihcore.account.PihAccountDomainWrapper;
import org.openmrs.module.pihcore.service.PihCoreService;
import org.openmrs.module.providermanagement.api.ProviderManagementService;
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

public class AccountPageController {

    protected final Log log = LogFactory.getLog(getClass());


    public PihAccountDomainWrapper getAccount(@RequestParam(value = "personId", required = false) Person person,
                                              @SpringBean("pihCoreService") PihCoreService pihCoreService) {

        if (person == null) {
            person = new Person();
        }
        return pihCoreService.newPihAccountDomainWrapper(person);
    }

    public void get(PageModel model, @MethodParam("getAccount") PihAccountDomainWrapper account,
                    @SpringBean("accountService") AccountService accountService,
                    @SpringBean("adminService") AdministrationService administrationService,
                    @SpringBean("providerManagementService") ProviderManagementService providerManagementService) {

        model.addAttribute("account", account);
        model.addAttribute("capabilities", accountService.getAllCapabilities());
        model.addAttribute("privilegeLevels", accountService.getAllPrivilegeLevels());
        model.addAttribute("rolePrefix", EmrApiConstants.ROLE_PREFIX_CAPABILITY);
        model.addAttribute("allowedLocales", administrationService.getAllowedLocales());
        model.addAttribute("providerRoles", providerManagementService.getAllProviderRoles(false));
    }

    public String post(@MethodParam("getAccount") @BindParams PihAccountDomainWrapper account, BindingResult errors,
                       @RequestParam(value = "userEnabled", defaultValue = "false") boolean userEnabled,
                       @SpringBean("messageSource") MessageSource messageSource,
                       @SpringBean("messageSourceService") MessageSourceService messageSourceService,
                       @SpringBean("accountService") AccountService accountService,
                       @SpringBean("userService") UserService userService,
                       @SpringBean("adminService") AdministrationService administrationService,
                       @SpringBean("providerManagementService") ProviderManagementService providerManagementService,
                       @SpringBean("accountValidator") AccountValidator accountValidator, PageModel model,
                       HttpServletRequest request) {

        // manually bind userEnabled (since checkboxes don't submit anything if unchecked));
        account.setUserEnabled(userEnabled);

        accountValidator.validate(account, errors);

        String email = account.getEmail();
        if (StringUtils.isNotBlank(email)) {
            if (!EmailValidator.getInstance().isValid(email)) {
                errors.rejectValue("email", "error.email.invalid");
            }
            else {
                User existingUser = userService.getUserByUsernameOrEmail(email);
                if (existingUser != null && !existingUser.equals(account.getUser())) {
                    if (email.equalsIgnoreCase(existingUser.getEmail())) {
                        errors.rejectValue("email", "emr.account.error.emailAlreadyInUse");
                    }
                }
            }
        }

        if (!errors.hasErrors()) {
            try {
                accountService.saveAccount(account);
                request.getSession().setAttribute(EmrConstants.SESSION_ATTRIBUTE_INFO_MESSAGE,
                        messageSourceService.getMessage("emr.account.saved"));
                request.getSession().setAttribute(EmrConstants.SESSION_ATTRIBUTE_TOAST_MESSAGE, "true");

                return "redirect:/pihcore/account/manageAccounts.page";
            } catch (Exception e) {
                log.warn("Some error occurred while saving account details:", e);
                request.getSession().setAttribute(EmrConstants.SESSION_ATTRIBUTE_ERROR_MESSAGE,
                        messageSourceService.getMessage("emr.account.error.save.fail", new Object[]{e.getMessage()}, Context.getLocale()));
            }
        } else {
            sendErrorMessage(errors, messageSource, request);
        }

        // reload page on error
        // TODO: show password fields toggle should work better

        model.addAttribute("errors", errors);
        model.addAttribute("account", account);
        model.addAttribute("capabilities", accountService.getAllCapabilities());
        model.addAttribute("privilegeLevels", accountService.getAllPrivilegeLevels());
        model.addAttribute("rolePrefix", EmrApiConstants.ROLE_PREFIX_CAPABILITY);
        model.addAttribute("allowedLocales", administrationService.getAllowedLocales());
        model.addAttribute("providerRoles", providerManagementService.getAllProviderRoles(false));

        return "account/account";

    }


    private void sendErrorMessage(BindingResult errors, MessageSource messageSource, HttpServletRequest request) {
        List<ObjectError> allErrors = errors.getAllErrors();
        String message = getMessageErrors(messageSource, allErrors);
        request.getSession().setAttribute(EmrConstants.SESSION_ATTRIBUTE_ERROR_MESSAGE,
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

}
