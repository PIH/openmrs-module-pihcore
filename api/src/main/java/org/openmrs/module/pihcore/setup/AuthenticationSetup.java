package org.openmrs.module.pihcore.setup;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.module.authentication.AuthenticationConfig;
import org.openmrs.module.pihcore.config.Config;
import org.openmrs.module.pihcore.config.model.AuthenticationConfigDescriptor;

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

    /**
     * Setup Authentication Configuration and Schemes
     */
    public static void setup(Config config) {

        // Needed to ensure the authentication module can load custom PIH Authentication Scheme
        AuthenticationConfig.registerClassLoader(AuthenticationSetup.class.getClassLoader());

        AuthenticationConfigDescriptor cd = config.getAuthenticationConfig();

        // If no authentication scheme is explicitly configured, default to basic
        AuthenticationConfig.setProperty(SCHEME, StringUtils.isBlank(cd.getScheme()) ? BASIC : cd.getScheme());

        List<String> whiteList = cd.getWhitelist();
        for (String schemeId : cd.getSchemes().keySet()) {
            AuthenticationConfigDescriptor.SchemeDescriptor sd = cd.getSchemes().get(schemeId);
            AuthenticationConfig.setProperty(SCHEME_TYPE_TEMPLATE.replace(SCHEME_ID, schemeId), sd.getType());
            for (String property : sd.getConfig().keySet()) {
                String propertyName = SCHEME_CONFIG_PREFIX_TEMPLATE.replace(SCHEME_ID, schemeId) + property;
                String propertyVal = sd.getConfig().get(property);
                AuthenticationConfig.setProperty(propertyName, propertyVal);
                if (property.equalsIgnoreCase("loginPage")) {
                    whiteList.add(propertyVal);
                }
            }
        }
        AuthenticationConfig.setProperty(WHITE_LIST, String.join(",", cd.getWhitelist()));

        log.info("Authentication Schemes Configured");
        Properties p = AuthenticationConfig.getConfig();
        Set<String> sortedKeys = new TreeSet<>(p.stringPropertyNames());
        for (String key : sortedKeys) {
            log.info(key + " = " + p.getProperty(key));
        }
    }
}
