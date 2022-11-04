/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 * <p>
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 * <p>
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
package org.openmrs.module.pihcore.page.controller.account;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Person;
import org.openmrs.User;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.PersonService;
import org.openmrs.api.context.Context;
import org.openmrs.messagesource.MessageSourceService;
import org.openmrs.module.emr.EmrConstants;
import org.openmrs.module.emrapi.account.AccountService;
import org.openmrs.module.pihcore.account.PihAccountDomainWrapper;
import org.openmrs.module.pihcore.account.PihAccountValidator;
import org.openmrs.module.pihcore.service.PihCoreService;
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

public class MyAccountPageController {

    protected final Log log = LogFactory.getLog(getClass());

    public PihAccountDomainWrapper getAccount(
            @SpringBean("personService") PersonService personService,
            @SpringBean("pihCoreService") PihCoreService pihCoreService) {

        // This is needed to avoid Hibernate Initialization and Loading issues
        User user = Context.getAuthenticatedUser();
        Person person = personService.getPerson(user.getPerson().getPersonId());
        return pihCoreService.newPihAccountDomainWrapper(person);
    }

    public void get(PageModel model,
                    @MethodParam("getAccount") PihAccountDomainWrapper account,
                    @RequestParam(value = "edit", required = false) Boolean edit,
                    @SpringBean("adminService") AdministrationService administrationService) {

        model.addAttribute("account", account);
        model.addAttribute("editMode", edit == Boolean.TRUE);
        model.addAttribute("allowedLocales", administrationService.getAllowedLocales());
    }

    public String post(@MethodParam("getAccount") @BindParams PihAccountDomainWrapper account, BindingResult errors,
                       @SpringBean("messageSource") MessageSource messageSource,
                       @SpringBean("messageSourceService") MessageSourceService messageSourceService,
                       @SpringBean("accountService") AccountService accountService,
                       @SpringBean("adminService") AdministrationService administrationService,
                       @SpringBean("pihAccountValidator") PihAccountValidator pihAccountValidator,
                       PageModel model,
                       HttpServletRequest request) {

        pihAccountValidator.validate(account, errors);

        if (!errors.hasErrors()) {
            try {
                accountService.saveAccount(account);

                Context.refreshAuthenticatedUser();
                if (account.getDefaultLocale() != null) {
                    Context.setLocale(account.getDefaultLocale());
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
                if (error.getCode() != null) {
                    String errorMessage = messageSource.getMessage(error.getCode(), arguments, Context.getLocale());
                    if (arguments != null) {
                        for (int i = 0; i < arguments.length; i++) {
                            String argument = (String) arguments[i];
                            errorMessage = errorMessage.replaceAll("\\{" + i + "}", argument);
                        }
                    }
                    message = message.concat(errorMessage.concat("<br>"));
                }
            }
            request.getSession().setAttribute(EmrConstants.SESSION_ATTRIBUTE_ERROR_MESSAGE, message);
        }

        // reload page on error
        model.addAttribute("errors", errors);
        model.addAttribute("account", account);
        model.addAttribute("editMode", true);
        model.addAttribute("allowedLocales", administrationService.getAllowedLocales());
        return "account/myAccount";
    }
}
