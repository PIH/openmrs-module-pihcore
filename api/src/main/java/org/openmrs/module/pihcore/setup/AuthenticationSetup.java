package org.openmrs.module.pihcore.setup;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.module.authentication.AuthenticationConfig;
import org.openmrs.module.authenticationui.AuthenticationUiModuleConfig;
import org.openmrs.module.pihcore.PihEmrConfigConstants;
import org.openmrs.module.pihcore.config.Config;
import org.openmrs.module.pihcore.config.model.AuthenticationConfigDescriptor;

import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;

import static org.openmrs.module.authentication.AuthenticationConfig.SCHEME;
import static org.openmrs.module.authentication.AuthenticationConfig.SCHEME_CONFIG_PREFIX_TEMPLATE;
import static org.openmrs.module.authentication.AuthenticationConfig.SCHEME_ID;
import static org.openmrs.module.authentication.AuthenticationConfig.SCHEME_TYPE_TEMPLATE;
import static org.openmrs.module.authentication.AuthenticationConfig.WHITE_LIST;

public class AuthenticationSetup {

    protected static Log log = LogFactory.getLog(AuthenticationSetup.class);

    public static final String BASIC = "basic";
    public static final String SECRET = "secret";
    public static final String TOTP = "totp";
    public static final String TWO_FACTOR = "2fa";

    /**
     * Setup Authentication Configuration and Schemes
     */
    public static void setup(Config config) {

        // Needed to ensure the authentication module can load custom PIH Authentication Scheme
        AuthenticationConfig.registerClassLoader(AuthenticationSetup.class.getClassLoader());

        AuthenticationConfigDescriptor cd = config.getAuthenticationConfig();

        // If no authentication scheme is explicitly configured, default to basic
        String scheme = StringUtils.isBlank(cd.getScheme()) ? BASIC : cd.getScheme();
        AuthenticationConfig.setProperty(SCHEME, scheme);

        // We set up white list as everything needed for the basic login page and any additional scheme login page
        // Add in any additional white list pages that are included in the config

        Set<String> whitelist = new TreeSet<>(cd.getWhitelist());
        whitelist.add("/login.htm");
        whitelist.add("/authenticationui/login/login.page");
        whitelist.add("/appui/session/getLoginLocations.action");
        whitelist.add("/csrfguard");
        whitelist.add("*.js");
        whitelist.add("*.css");
        whitelist.add("*.gif");
        whitelist.add("*.jpg");
        whitelist.add("*.png");
        whitelist.add("*.ico");
        whitelist.add("*.ttf");
        whitelist.add("*.woff");

        // Set up all the supported authentication schemes with default values.
        // Allow overriding with values from the config

        // Basic Authentication Scheme.  This provides basic auth + session location selection
        {
            String className = "org.openmrs.module.authentication.web.BasicWithLocationAuthenticationScheme";
            Properties p = new Properties();
            p.put("loginPage", "/authenticationui/login/login.page");
            p.put("usernameParam", "username");
            p.put("passwordParam", "password");
            p.put("locationParamName", "sessionLocation");
            p.put("locationRequired", "true");
            p.put("onlyLocationsWithTag", "Login Location");
            p.put("locationSessionAttributeName", "emrContext.sessionLocationId");
            p.put("lastLocationCookieName", "emr.lastSessionLocation");
            addScheme(BASIC, className, p, whitelist);
        }

        // Secret Question Authentication Scheme.  This is an available 2nd factor
        {
            String className = "org.openmrs.module.authentication.web.SecretQuestionAuthenticationScheme";
            Properties p = new Properties();
            p.put("loginPage", "/authenticationui/login/loginSecret.page");
            p.put("configurationPage", "/authenticationui/account/changeSecurityQuestion.page?schemeId={schemeId}&userId={userId}");
            addScheme(SECRET, className, p, whitelist);
        }

        // Totp Authentication Scheme.  This is an available 2nd factor
        {
            String className = "org.openmrs.module.authentication.web.TotpAuthenticationScheme";
            Properties p = new Properties();
            p.put("qrCodeIssuer", "PIHEMR");
            p.put("loginPage", "/authenticationui/login/loginTotp.page");
            p.put("configurationPage", "/authenticationui/account/configureTotp.page?schemeId={schemeId}&userId={userId}");
            addScheme(TOTP, className, p, whitelist);
        }

        // Two-Factor Authentication Scheme.
        {
            String className = "org.openmrs.module.authentication.web.TwoFactorAuthenticationScheme";
            Properties p = new Properties();
            p.put("primaryOptions", BASIC);
            p.put("secondaryOptions", SECRET + "," + TOTP);
            addScheme(TWO_FACTOR, className, p, whitelist);
        }

        // Configuration overrides
        for (String schemeId : cd.getSchemes().keySet()) {
            AuthenticationConfigDescriptor.SchemeDescriptor sd = cd.getSchemes().get(schemeId);
            if (StringUtils.isNotBlank(sd.getType())) {
                AuthenticationConfig.setProperty(SCHEME_TYPE_TEMPLATE.replace(SCHEME_ID, schemeId), sd.getType());
            }
            for (String property : sd.getConfig().keySet()) {
                String propertyName = SCHEME_CONFIG_PREFIX_TEMPLATE.replace(SCHEME_ID, schemeId) + property;
                String propertyVal = sd.getConfig().get(property);
                AuthenticationConfig.setProperty(propertyName, propertyVal);
                if (property.equalsIgnoreCase("loginPage")) {
                    whitelist.add(propertyVal);
                }
            }
        }

        AuthenticationConfig.setProperty(WHITE_LIST, String.join(",", whitelist));

        log.info("Authentication Schemes Configured");
        Properties p = AuthenticationConfig.getConfig();
        Set<String> sortedKeys = new TreeSet<>(p.stringPropertyNames());
        for (String key : sortedKeys) {
            log.info(key + " = " + p.getProperty(key));
        }

        AuthenticationUiModuleConfig.setHeaderLogoUrlProvider("file");
        AuthenticationUiModuleConfig.setHeaderLogoUrlResource("configuration/pih/logo/logo.png");
        AuthenticationUiModuleConfig.setHomePageProvider("pihcore");
        AuthenticationUiModuleConfig.setHomePageResource("home");
        AuthenticationUiModuleConfig.setLoginWelcomeMessage(config.getWelcomeMessage());
        AuthenticationUiModuleConfig.setLoginWarningIfNotChrome(config.getBrowserWarning());
        AuthenticationUiModuleConfig.setAllowPasswordReset(true);
        AuthenticationUiModuleConfig.setAccountAdminPrivilege("App: coreapps.systemAdministration");
        AuthenticationUiModuleConfig.setPhoneNumberPersonAttributeType(PihEmrConfigConstants.PERSONATTRIBUTETYPE_TELEPHONE_NUMBER_UUID);
        AuthenticationUiModuleConfig.setDefaultLocationUserProperty(null);
    }

    /**
     * Add configuration for a scheme with the given schemeId, if a scheme with this schemeId is not already configured
     */
    protected static void addScheme(String schemeId, String className, Properties config, Set<String> whitelist) {
        String schemeTypeProperty = SCHEME_TYPE_TEMPLATE.replace(SCHEME_ID, schemeId);
        if (StringUtils.isBlank(AuthenticationConfig.getProperty(schemeTypeProperty))) {
            AuthenticationConfig.setProperty(schemeTypeProperty, className);
            if (config != null) {
                for (String propertyName : config.stringPropertyNames()) {
                    String key = SCHEME_CONFIG_PREFIX_TEMPLATE.replace(SCHEME_ID, schemeId) + propertyName;
                    String value = config.getProperty(propertyName);
                    AuthenticationConfig.setProperty(key, value);
                    if (propertyName.equalsIgnoreCase("loginPage")) {
                        whitelist.add(value);
                    }
                }
            }
        }
    }
}
