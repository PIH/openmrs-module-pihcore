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
import org.openmrs.api.PersonService;
import org.openmrs.api.UserService;
import org.openmrs.api.context.Context;
import org.openmrs.messagesource.MessageSourceService;
import org.openmrs.module.emr.EmrConstants;
import org.openmrs.module.emrapi.account.AccountService;
import org.openmrs.module.emrapi.account.AccountValidator;
import org.openmrs.module.pihcore.account.PihAccountDomainWrapper;
import org.openmrs.module.pihcore.service.PihCoreService;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.openmrs.ui.framework.page.PageModel;
import org.springframework.context.MessageSource;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Locale;

public class MyAccountPageController {

    protected final Log log = LogFactory.getLog(getClass());

    public void get(PageModel model,
                    @RequestParam(value = "mode", defaultValue = "") String mode,
                    @SpringBean("pihCoreService") PihCoreService pihCoreService,
                    @SpringBean("adminService") AdministrationService administrationService) {

        User user = Context.getAuthenticatedUser();
        PihAccountDomainWrapper account = pihCoreService.newPihAccountDomainWrapper(user.getPerson());
        model.addAttribute("account", account);
        model.addAttribute("mode", mode);
        model.addAttribute("allowedLocales", administrationService.getAllowedLocales());
    }

    public String post(@RequestParam(value = "mode", defaultValue = "") String mode,
                       @RequestParam(value = "username") String username,
                       @RequestParam(value = "givenName") String givenName,
                       @RequestParam(value = "familyName") String familyName,
                       @RequestParam(value = "gender") String gender,
                       @RequestParam(value = "email", required = false) String email,
                       @RequestParam(value = "phoneNumber", required = false) String phoneNumber,
                       @RequestParam(value = "defaultLocale", required = false) Locale defaultLocale,
                       @SpringBean("pihCoreService") PihCoreService pihCoreService,
                       @SpringBean("messageSource") MessageSource messageSource,
                       @SpringBean("messageSourceService") MessageSourceService messageSourceService,
                       @SpringBean("accountService") AccountService accountService,
                       @SpringBean("userService") UserService userService,
                       @SpringBean("personService") PersonService personService,
                       @SpringBean("adminService") AdministrationService administrationService,
                       @SpringBean("accountValidator") AccountValidator accountValidator, PageModel model,
                       HttpServletRequest request) {

        User user = Context.getAuthenticatedUser();
        Person person = personService.getPerson(user.getPerson().getPersonId());
        PihAccountDomainWrapper account = pihCoreService.newPihAccountDomainWrapper(person);

        Errors errors = new BeanPropertyBindingResult(account, "account");

        // If username has changed, make sure it isn't already in use
        if (!account.getUsername().equalsIgnoreCase(username)) {
            User existingUser = userService.getUserByUsername(username);
            if (existingUser != null) {
                errors.rejectValue("username", "emr.user.duplicateUsername");
            }
        }

        // If email has changed, make sure it isn't already in use or invalid
        if (StringUtils.isNotBlank(email) && !email.equalsIgnoreCase(account.getEmail())) {
            if (!EmailValidator.getInstance().isValid(email)) {
                errors.rejectValue("email", "error.email.invalid");
            }
            else {
                User existingUser = userService.getUserByUsernameOrEmail(email);
                if (existingUser != null && !existingUser.equals(account.getUser())) {
                    errors.rejectValue("email", "emr.account.error.emailAlreadyInUse");
                }
            }
        }

        account.setUsername(username);
        account.setGivenName(givenName);
        account.setFamilyName(familyName);
        account.setGender(gender);
        account.setEmail(email);
        account.setPhoneNumber(phoneNumber);
        account.setDefaultLocale(defaultLocale);

        accountValidator.validate(account, errors);

        if (!errors.hasErrors()) {
            try {
                accountService.saveAccount(account);
                Context.refreshAuthenticatedUser();
                if (defaultLocale != null) {
                    Context.setLocale(defaultLocale);
                }

                String msg = messageSourceService.getMessage("emr.account.saved");
                request.getSession().setAttribute(EmrConstants.SESSION_ATTRIBUTE_INFO_MESSAGE, msg);
                request.getSession().setAttribute(EmrConstants.SESSION_ATTRIBUTE_TOAST_MESSAGE, "true");
                return "redirect:/pihcore/account/myAccount.page";
            }
            catch (Exception e) {
                log.warn("Some error occurred while saving account details:", e);
                String msg = messageSourceService.getMessage("emr.account.error.save.fail", new Object[]{e.getMessage()}, Context.getLocale());
                request.getSession().setAttribute(EmrConstants.SESSION_ATTRIBUTE_ERROR_MESSAGE, msg);
            }
        }
        else {
            List<ObjectError> allErrors = errors.getAllErrors();
            String message = "";
            for (ObjectError error : allErrors) {
                Object[] arguments = error.getArguments();
                String errorMessage = messageSource.getMessage(error.getCode(), arguments, Context.getLocale());
                if (arguments != null) {
                    for (int i = 0; i < arguments.length; i++) {
                        String argument = (String) arguments[i];
                        errorMessage = errorMessage.replaceAll("\\{" + i + "\\}", argument);
                    }
                }
                message = message.concat(errorMessage.concat("<br>"));
            }
            request.getSession().setAttribute(EmrConstants.SESSION_ATTRIBUTE_ERROR_MESSAGE, message);
        }

        // reload page on error
        model.addAttribute("errors", errors);
        model.addAttribute("account", account);
        model.addAttribute("mode", mode);
        model.addAttribute("allowedLocales", administrationService.getAllowedLocales());
        return "account/myAccount";
    }
}
