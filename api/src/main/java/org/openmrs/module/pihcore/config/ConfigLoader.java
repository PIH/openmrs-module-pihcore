package org.openmrs.module.pihcore.config;

import org.apache.commons.io.IOUtils;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ObjectNode;
import org.openmrs.api.context.Context;
import org.openmrs.module.pihcore.PihCoreUtil;
import org.openmrs.util.OpenmrsClassLoader;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Iterator;

/**
 * Loads configuration that is appropriate for the environment based on runtime properties
 */
public class ConfigLoader {

    public static final String PIH_CONFIGURATION_RUNTIME_PROPERTY = "pih.config";

    // allows users to specify a directory other than the application data directory to look for config files
    // helpful for developers, where you may want to point this to the appropriate directory in your local checkout
    // of our puppet project, ie: /home/mgoodrich/openmrs/modules/mirebalais-puppet/mirebalais-modules/openmrs/files/config
    public static final String PIH_CONFIGURATION_DIR_RUNTIME_PROPERTY = "pih.config.dir";

    /**
     * @return the configuration based on runtime properties configuration, or based on default value if not found
     */
    public static String getRuntimeConfiguration(String defaultValue) {
        return PihCoreUtil.getSystemOrRuntimeProperty(PIH_CONFIGURATION_RUNTIME_PROPERTY, defaultValue);
    }

    public static String getPihConfigurationDirRuntimeProperty(String defaultValue) {
        return PihCoreUtil.getSystemOrRuntimeProperty(PIH_CONFIGURATION_DIR_RUNTIME_PROPERTY, defaultValue);
    }

    /**
     * Loads Configuration based on configuration in the runtime properties file
     */
    public static ConfigDescriptor loadFromRuntimeProperties() {
        return load(getRuntimeConfiguration("default,site-default"));
    }

    /**
     * Loads Configuration based on a comma-delimited series of configuration files that can override one another
     */
    public static ConfigDescriptor load(String configs) {

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(JsonParser.Feature.ALLOW_COMMENTS, true);

        try {

            JsonNode configNode = null;

            for (String config : configs.split(",")) {

                InputStream is = null;

                try {
                    String configFilename = "pih-config-" + config.trim() + ".json";

                    // first see if is in the .OpenMRS directory (or directory specified in pih.config.dir runtime property)
                    // (any file found will override any file of the same name on the classpath)
                    String dir = getPihConfigurationDirRuntimeProperty(PihCoreUtil.getDefaultPihConfigurationDir());
                    File configFile = new File(dir, configFilename);

                    if (configFile.exists()) {
                        try {
                            is = new FileInputStream(configFile);
                        }
                        catch (Exception e) {
                            throw new IllegalArgumentException("Error loading " + configFilename + " from application data directory", e);
                        }
                    }
                    else {
                        try {
                            is = OpenmrsClassLoader.getInstance().getResourceAsStream("config/" + configFilename);
                        }
                        catch (Exception e) {
                            throw new IllegalArgumentException("Error loading " + configFilename + " from classpath", e);
                        }
                    }

                    if (is == null && !config.equals("site-default")) {  // bit of a hack, we don't insist that a "pih-config-site-default.json" exists
                        throw new IllegalStateException("Unable to find config file for configuration " + config);
                    }

                    // TODO: remove this null test if we remove the "site-default" hack above
                    if (is != null) {
                        // Read the configuration file into a JsonNode
                        JsonNode rootNode = objectMapper.readTree(is);

                        // Merge this in if this is not the first configuration file loaded
                        if (configNode == null) {
                            configNode = rootNode;
                        }
                        else {
                            configNode = merge(configNode, rootNode);
                        }
                    }
                }
                finally {
                    IOUtils.closeQuietly(is);
                }

            }

            String json = objectMapper.writeValueAsString(configNode);
            return objectMapper.readValue(json, ConfigDescriptor.class);
        }
        catch (Exception e) {
            throw new RuntimeException("Error parsing json configuration", e);
        }
    }

    private static JsonNode merge(JsonNode mainNode, JsonNode updateNode) {
        Iterator<String> fieldNames = updateNode.getFieldNames();
        while (fieldNames.hasNext()) {
            String fieldName = fieldNames.next();
            JsonNode jsonNode = mainNode.get(fieldName);
            if (jsonNode != null && jsonNode.isObject()) {
                merge(jsonNode, updateNode.get(fieldName));
            }
            else {
                ((ObjectNode) mainNode).put(fieldName, updateNode.get(fieldName));
            }
        }
        return mainNode;
    }
}
