package org.openmrs.module.pihcore.config;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ObjectNode;
import org.openmrs.api.context.Context;
import org.openmrs.util.OpenmrsUtil;

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
     * Loads Configuration based on configuration in the runtime properties file
     */
    public static ConfigDescriptor loadFromRuntimeProperties() {
        String configs = Context.getRuntimeProperties().getProperty(PIH_CONFIGURATION_RUNTIME_PROPERTY);
        return load(configs);
    }

    /**
     * Loads Configuration based on a comma-delimited series of configuration files that can override one another
     */
    public static ConfigDescriptor load(String configs) {

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(JsonParser.Feature.ALLOW_COMMENTS, true);

        if (StringUtils.isBlank(configs)) {
            configs = "mirebalais";  // we default to mirebalais for now
        }

        try {

            JsonNode configNode = null;

            for (String config : configs.split(",")) {

                InputStream is = null;

                try {
                    String configFilename = "pih-config-" + config.trim() + ".json";

                    // first see if is in the .OpenMRS directory (which will override any file of the same name on the classpath)
                    File configFile = new File(OpenmrsUtil.getApplicationDataDirectory() + File.separatorChar + config);

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
                            is = ConfigLoader.class.getClassLoader().getResourceAsStream("config/" + configFilename);
                        }
                        catch (Exception e) {
                            throw new IllegalArgumentException("Error loading " + configFilename + " from classpath", e);
                        }
                    }

                    if (is == null) {
                        throw new IllegalStateException("Unable to find config file for configuration " + config);
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
