package org.openmrs.module.pihcore.page.controller.account;

import org.apache.commons.lang.StringUtils;
import org.openmrs.User;
import org.openmrs.api.UserService;
import org.openmrs.api.context.AuthenticationScheme;
import org.openmrs.api.context.Context;
import org.openmrs.messagesource.MessageSourceService;
import org.openmrs.module.authentication.AuthenticationConfig;
import org.openmrs.module.authentication.web.TwoFactorAuthenticationScheme;
import org.openmrs.module.authentication.web.WebAuthenticationScheme;
import org.openmrs.module.emr.EmrConstants;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.openmrs.ui.framework.page.PageModel;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

public class TwoFactorSetupPageController {

    public String get(PageModel model) {
        User user = Context.getAuthenticatedUser();

        AuthenticationScheme authenticationScheme = AuthenticationConfig.getAuthenticationScheme();
        boolean twoFactorAvailable = (authenticationScheme instanceof TwoFactorAuthenticationScheme);
        model.addAttribute("twoFactorAvailable", twoFactorAvailable);

        String existingOption = user.getUserProperty(TwoFactorAuthenticationScheme.USER_PROPERTY_SECONDARY_TYPE);
        model.addAttribute("existingOption", existingOption);

        if (twoFactorAvailable) {
            TwoFactorAuthenticationScheme scheme = (TwoFactorAuthenticationScheme) authenticationScheme;
            List<SecondaryOption> secondaryOptions = new ArrayList<>();
            for (String schemeId : scheme.getSecondaryOptions()) {
                SecondaryOption option = new SecondaryOption(schemeId);
                if (option.getScheme() instanceof WebAuthenticationScheme) {
                    WebAuthenticationScheme webAuthenticationScheme = (WebAuthenticationScheme) option.getScheme();
                    option.setConfigurationRequired(webAuthenticationScheme.isUserConfigurationRequired(user));
                    option.setConfigurationPage(webAuthenticationScheme.getUserConfigurationPage());
                }
                option.setCurrentlySelected(schemeId.equalsIgnoreCase(existingOption));
                secondaryOptions.add(option);
            }
            model.addAttribute("secondaryOptions", secondaryOptions);
        }

        return "account/twoFactorSetup";
    }

    public String post(@RequestParam(value = "schemeId", required = false) String schemeId,
                       @SpringBean("userService") UserService userService,
                       @SpringBean("messageSourceService") MessageSourceService messageSourceService,
                       HttpServletRequest request) {

        try {
            // If this scheme requires configuration, redirect to the configuration page
            User user = Context.getAuthenticatedUser();
            if (StringUtils.isNotBlank(schemeId)) {
                AuthenticationScheme scheme = AuthenticationConfig.getAuthenticationScheme(schemeId);
                if (scheme instanceof WebAuthenticationScheme) {
                    WebAuthenticationScheme webScheme = (WebAuthenticationScheme) scheme;
                    if (StringUtils.isNotBlank(webScheme.getUserConfigurationPage())) {
                        String url = webScheme.getUserConfigurationPage().replace("{schemeId}", schemeId);
                        return "redirect:" + url;
                    }
                }
            }
            // Otherwise, assume no configuration is needed and set this as the two factor method
            if (StringUtils.isBlank(schemeId)) {
                user.removeUserProperty(TwoFactorAuthenticationScheme.USER_PROPERTY_SECONDARY_TYPE);
            }
            else {
                user.setUserProperty(TwoFactorAuthenticationScheme.USER_PROPERTY_SECONDARY_TYPE, schemeId);
            }
            userService.saveUser(user);
            String msg = messageSourceService.getMessage("authentication.2fa.success", null, Context.getLocale());
            request.getSession().setAttribute(EmrConstants.SESSION_ATTRIBUTE_INFO_MESSAGE, msg);
            request.getSession().setAttribute(EmrConstants.SESSION_ATTRIBUTE_TOAST_MESSAGE, "true");
            return "redirect:pihcore/account/myAccount.page";
        }
        catch (Exception e) {
            String msg = messageSourceService.getMessage("authentication.2fa.error", new Object[]{e.getMessage()}, Context.getLocale());
            request.getSession().setAttribute(EmrConstants.SESSION_ATTRIBUTE_ERROR_MESSAGE, msg);
            return "redirect:pihcore/account/twoFactorSetup.page";
        }
    }

    public static class SecondaryOption {

        private final String schemeId;
        private final AuthenticationScheme scheme;
        private boolean configurationRequired = false;
        private String configurationPage;
        private boolean currentlySelected = false;

        public SecondaryOption(String schemeId) {
            this.schemeId = schemeId;
            this.scheme = AuthenticationConfig.getAuthenticationScheme(schemeId);
        }

        public String getSchemeId() {
            return schemeId;
        }

        public AuthenticationScheme getScheme() {
            return scheme;
        }

        public boolean isConfigurationRequired() {
            return configurationRequired;
        }

        public void setConfigurationRequired(boolean configurationRequired) {
            this.configurationRequired = configurationRequired;
        }

        public String getConfigurationPage() {
            return configurationPage;
        }

        public void setConfigurationPage(String configurationPage) {
            this.configurationPage = configurationPage;
        }

        public boolean isCurrentlySelected() {
            return currentlySelected;
        }

        public void setCurrentlySelected(boolean currentlySelected) {
            this.currentlySelected = currentlySelected;
        }
    }
}
