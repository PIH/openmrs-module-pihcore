package org.openmrs.module.pihcore.config;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ObjectNode;
import org.openmrs.api.context.Context;
import org.openmrs.module.pihcore.PihCoreUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Iterator;

/**
 * Loads configuration that is appropriate for the environment based on runtime properties
 */
public class ConfigLoader {

    public static final String PIH_CONFIGURATION_RUNTIME_PROPERTY = "pih.config";

    /**
     * @return the configuration based on runtime properties configuration, or based on default value if not found
     */
    public static String getRuntimeConfiguration(String defaultValue) {
        return PihCoreUtil.getSystemOrRuntimeProperty(PIH_CONFIGURATION_RUNTIME_PROPERTY, defaultValue);
    }

    /**
     * Loads Configuration based on configuration in the runtime properties file
     */
    public static ConfigDescriptor loadFromRuntimeProperties() {
        String configs = getRuntimeConfiguration("");
        return load(configs);
    }

    /**
     * Loads Configuration based on a comma-delimited series of configuration files that can override one another
     */
    public static ConfigDescriptor load(String configs) {

        if (StringUtils.isBlank(configs)) {
            throw new IllegalStateException("PIH CONFIGURATION ERROR: pih.config is not set");
        }

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(JsonParser.Feature.ALLOW_COMMENTS, true);

        try {

            JsonNode configNode = null;

            for (String config : configs.split(",")) {

                InputStream is = null;

                try {
                    String trimmedConfig = config.trim();
                    String configFilename = "pih-config-" + trimmedConfig + ".json";

                    String dir = PihCoreUtil.getDefaultPihConfigurationDir();
                    File configFile = new File(dir, configFilename);

                    if (!configFile.exists()) {
                        throw new IllegalStateException("PIH CONFIGURATION ERROR: Could not find a configuration file "
                                + "named '" + configFilename + "' for pih.config value '" + trimmedConfig + "'. Checked "
                                + configFile.getAbsolutePath() + ". Set the 'pih.config' runtime property to a valid, "
                                + "comma-delimited list of PIH configuration profile names for this distribution.");
                    }

                    try {
                        is = new FileInputStream(configFile);
                    }
                    catch (Exception e) {
                        throw new IllegalArgumentException("Error loading " + configFilename + " from " + dir, e);
                    }

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
                finally {
                    IOUtils.closeQuietly(is);
                }

            }

            String json = objectMapper.writeValueAsString(configNode);
            return objectMapper.readValue(json, ConfigDescriptor.class);
        }
        catch (IllegalStateException e) {
            // a missing config file is already a clear, actionable message -- don't bury it inside
            // the generic "Error parsing json configuration" message below
            throw e;
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
