package org.openmrs.module.pihcore.status;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.openmrs.module.initializer.InitializerConstants;
import org.openmrs.util.OpenmrsUtil;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class StatusDataLoader {

    public static final Yaml getYaml() {
        DumperOptions options = new DumperOptions();
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        options.setDefaultScalarStyle(DumperOptions.ScalarStyle.DOUBLE_QUOTED);
        return new Yaml(options);
    }

    public static File getStatusDataDirectory() {
        File configDir = OpenmrsUtil.getDirectoryInApplicationDataDirectory(InitializerConstants.DIR_NAME_CONFIG);
        File pihConfigDir = new File(configDir, "pih");
        File statusDataDir = new File(pihConfigDir, "statusData");
        return statusDataDir;
    }

    public static List<String> getStatusDataConfigurationPaths() {
        List<String> ret = new ArrayList<>();
        File dir = getStatusDataDirectory();
        Collection<File> files = FileUtils.listFiles(dir, FileFilterUtils.suffixFileFilter("yml"), TrueFileFilter.INSTANCE);
        for (File file : files) {
            ret.add(dir.toPath().relativize(file.toPath()).toString());
        }
        return ret;
    }

    public static StatusDataDefinition getStatusDataDefinition(String path, String id) {
        for (StatusDataDefinition definition : getStatusDataDefinitions(path)) {
            if (definition.getId().equalsIgnoreCase(id)) {
                return definition;
            }
        }
        return null;
    }

    public static List<StatusDataDefinition> getStatusDataDefinitions(String path) {
        List<StatusDataDefinition> ret = new ArrayList<>();
        File definitionFile = new File(getStatusDataDirectory(), path);
        if (!definitionFile.exists()) {
            throw new IllegalArgumentException("Status Data file <" + path + "> does not exist.");
        }
        try (InputStream inputStream = FileUtils.openInputStream(definitionFile)) {
            List<Map<String, Object>> loadedMaps = getYaml().load(inputStream);
            for (Map<String, Object> m : loadedMaps) {
                StatusDataDefinition definition = convertToObject(m, StatusDataDefinition.class);
                definition.setPath(path);
                ret.add(definition);
            }
        }
        catch (Exception e) {
            throw new RuntimeException("Unable to load status data definitions from file " + definitionFile.getAbsolutePath(), e);
        }

        return ret;
    }

    private static <T> T convertToObject(Map<String, Object> inputMap, Class<T> type) {
        try {
            T ret = type.newInstance();
            for (String propertyName : inputMap.keySet()) {
                Object propertyValue = inputMap.get(propertyName);
                PropertyUtils.setProperty(ret, propertyName, propertyValue);
            }
            return ret;
        }
        catch (Exception e) {
            throw new IllegalArgumentException("Unable to convert " + inputMap + " to " + type);
        }
    }
}
