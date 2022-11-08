package org.openmrs.module.pihcore.setup;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.module.authentication.AuthenticationConfig;

import java.util.ArrayList;
import java.util.List;
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
    public static final String TWO_FACTOR = "2fa";

    /**
     * Setup Authentication Configuration and Schemes
     */
    public static void setup() {

        // Needed to ensure the authentication module can load custom PIH Authentication Scheme
        AuthenticationConfig.registerClassLoader(AuthenticationSetup.class.getClassLoader());

        // Setup default white-list if not configured already
        if (StringUtils.isBlank(AuthenticationConfig.getProperty(WHITE_LIST))) {
            List<String> whitelist = new ArrayList<>();
            whitelist.add("/login.htm");
            whitelist.add("/pihcore/login.page");
            whitelist.add("/pihcore/loginSecret.page");
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
            AuthenticationConfig.setProperty(WHITE_LIST, String.join(",", whitelist));
        }

        // Basic Authentication Scheme.  This provides basic auth + session location selection
        {
            String className = "org.openmrs.module.pihcore.PihBasicAuthenticationScheme";
            Properties config = new Properties();
            config.put("loginPage", "/login.htm");
            config.put("usernameParam", "username");
            config.put("passwordParam", "password");
            addScheme(BASIC, className, config);
        }

        // Secret Question Authentication Scheme.  This is an available 2nd factor
        {
            String className = "org.openmrs.module.authentication.web.SecretQuestionAuthenticationScheme";
            Properties config = new Properties();
            config.put("loginPage", "/pihcore/loginSecret.page");
            config.put("configurationPage", "/pihcore/account/changeSecurityQuestion.page");
            addScheme(SECRET, className, config);
        }

        // Two-Factor Authentication Scheme.
        {
            String className = "org.openmrs.module.authentication.web.TwoFactorAuthenticationScheme";
            Properties config = new Properties();
            config.put("primaryOptions", BASIC);
            config.put("secondaryOptions", SECRET);
            addScheme(TWO_FACTOR, className, config);
        }

        // If no authentication scheme is explicitly configured, default to basic
        if (StringUtils.isBlank(AuthenticationConfig.getProperty(SCHEME))) {
            AuthenticationConfig.setProperty(SCHEME, BASIC);
        }

        log.info("Authentication Schemes Configured");
        Properties config = AuthenticationConfig.getConfig();
        Set<String> sortedKeys = new TreeSet<>(config.stringPropertyNames());
        for (String key : sortedKeys) {
            log.info(key + " = " + config.getProperty(key));
        }
    }

    /**
     * Add configuration for a scheme with the given schemeId, if a scheme with this schemeId is not already configured
     */
    protected static void addScheme(String schemeId, String className, Properties config) {
        String schemeTypeProperty = SCHEME_TYPE_TEMPLATE.replace(SCHEME_ID, schemeId);
        if (StringUtils.isBlank(AuthenticationConfig.getProperty(schemeTypeProperty))) {
            AuthenticationConfig.setProperty(schemeTypeProperty, className);
            if (config != null) {
                for (String propertyName : config.stringPropertyNames()) {
                    String key = SCHEME_CONFIG_PREFIX_TEMPLATE.replace(SCHEME_ID, schemeId) + propertyName;
                    String value = config.getProperty(propertyName);
                    AuthenticationConfig.setProperty(key, value);
                }
            }
        }
    }
}
