package org.openmrs.module.pihcore.status;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.apache.commons.io.FileUtils;
import org.openmrs.module.initializer.InitializerConstants;
import org.openmrs.util.OpenmrsUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class StatusDataLoader {

    protected static ObjectMapper objectMapper = new ObjectMapper(new YAMLFactory());

    public static File getStatusDataDirectory() {
        File configDir = OpenmrsUtil.getDirectoryInApplicationDataDirectory(InitializerConstants.DIR_NAME_CONFIG);
        File pihConfigDir = new File(configDir, "pih");
        File statusDataDir = new File(pihConfigDir, "statusData");
        return statusDataDir;
    }

    public static List<StatusDataDefinition> getStatusDataDefinitions(String path) {
        List<StatusDataDefinition> ret = new ArrayList<>();
        File definitionFile = new File(getStatusDataDirectory(), path);
        if (!definitionFile.exists()) {
            throw new IllegalArgumentException("Status Data file <" + path + "> does not exist.");
        }
        try {
            ArrayNode definitionsNode = (ArrayNode) objectMapper.readTree(definitionFile);
            for (JsonNode definitionNode : definitionsNode) {
                StatusDataDefinition definition = objectMapper.treeToValue(definitionNode, StatusDataDefinition.class);
                if (definition.getStatusDataQuery().toLowerCase().endsWith(".sql")) {
                    File sqlFile = new File(definitionFile.getParentFile(), definition.getStatusDataQuery());
                    if (!sqlFile.exists()) {
                        throw new IllegalArgumentException("The SQL file " + sqlFile.getAbsolutePath() + " defined in " + definitionFile.getAbsolutePath() + " does not exist");
                    }
                    String sqlFromFile = FileUtils.readFileToString(sqlFile, "UTF-8");
                    definition.setStatusDataQuery(sqlFromFile.trim());
                }
                ret.add(definition);
            }
        }
        catch (Exception e) {
            throw new RuntimeException("Unable to load status data definitions from file " + definitionFile.getAbsolutePath(), e);
        }
        return ret;
    }
}
