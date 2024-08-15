package org.openmrs.module.pihcore.apploader;

import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.openmrs.module.appframework.domain.AppDescriptor;
import org.openmrs.module.appframework.domain.AppTemplate;
import org.openmrs.module.appframework.domain.Extension;
import org.openmrs.module.appframework.factory.AppFrameworkFactory;
import org.openmrs.util.OpenmrsUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Component
public class ConfigAppLoaderFactory implements AppFrameworkFactory {

    protected Logger logger = LoggerFactory.getLogger(getClass());

    private final ObjectMapper objectMapper = new ObjectMapper();

    public ConfigAppLoaderFactory() {
        // Tell the parser to all // and /* style comments.
        objectMapper.configure(JsonParser.Feature.ALLOW_COMMENTS, true);
    }

    private File getAppFrameworkConfigDir() {
        File configDir = OpenmrsUtil.getDirectoryInApplicationDataDirectory("configuration");
        File appFrameworkDir = new File(configDir, "appframework");
        return appFrameworkDir;
    }

    private List<File> getConfigFilesBySuffix(String suffix) {
        return getNestedFilesBySuffix(getAppFrameworkConfigDir(), suffix);
    }

    public List<File> getNestedFilesBySuffix(File directory, String suffix) {
        List<File> files = new ArrayList<>();
        if (directory.exists() && directory.isDirectory()) {
            File[] filesInDirectory = directory.listFiles();
            if (filesInDirectory != null) {
                for (File f : filesInDirectory) {
                    if (f.isDirectory()) {
                        files.addAll(getNestedFilesBySuffix(f, suffix));
                    } else {
                        if (f.getName().endsWith(suffix)) {
                            files.add(f);
                        }
                    }
                }
            }
        }
        return files;
    }

    @Override
    public List<AppTemplate> getAppTemplates() {
        List<AppTemplate> templates = new ArrayList<>();
        for (File f : getConfigFilesBySuffix("AppTemplates.json")) {
            try {
                List<AppTemplate> l = objectMapper.readValue(f, new TypeReference<List<AppTemplate>>() {});
                templates.addAll(l);
            }
            catch (Exception e) {
                logger.error("Error reading AppTemplates configuration file: {}", f.getName(), e);
            }
        }
        return templates;
    }

    @Override
    public List<AppDescriptor> getAppDescriptors() {
        List<AppDescriptor> descriptors = new ArrayList<>();
        for (File f : getConfigFilesBySuffix("app.json")) {
            try {
                List<AppDescriptor> apps = objectMapper.readValue(f, new TypeReference<List<AppDescriptor>>() {});
                for (int i=0; i<apps.size(); i++) {
                    AppDescriptor app = apps.get(i);
                    // Allow setting the id from the file path, to avoid having to set this directly when not needed
                    if (StringUtils.isBlank(app.getId())) {
                        app.setId(getFileNameAsId(f) + (apps.size() > 1 ? "_" + i : ""));
                    }
                    if (app.getExtensions() != null) {
                        for (int y=0; y<app.getExtensions().size(); y++) {
                            Extension extension = app.getExtensions().get(y);
                            // Allow setting the extension id from the app id
                            if (StringUtils.isBlank(extension.getId())) {
                                extension.setId(app.getId() + (app.getExtensions().size() > 1 ? "_" + y : ""));
                            }
                            // Allow setting the appId of the extension automatically from the containing app id
                            if (StringUtils.isBlank(extension.getAppId())) {
                                extension.setAppId(app.getId());
                            }
                            else {
                                if (!extension.getAppId().equals(app.getId())) {
                                    logger.error("Extension has appId {} that does not match app id {}", extension.getAppId(), app.getId());
                                }
                            }
                        }
                    }
                    descriptors.add(app);
                }
            }
            catch (Exception e) {
                logger.error("Error reading AppDescriptors configuration file: {}", f.getName(), e);
            }
        }
        return descriptors;
    }

    @Override
    public List<Extension> getExtensions() {
        List<Extension> extensions = new ArrayList<>();
        for (File f : getConfigFilesBySuffix("extension.json")) {
            try {
                List<Extension> l = objectMapper.readValue(f, new TypeReference<List<Extension>>() {});
                extensions.addAll(l);
            }
            catch (Exception e) {
                logger.error("Error reading Extension configuration file: {}", f.getName(), e);
            }
        }
        return extensions;
    }

    public String getFileNameAsId(File file) {
        String relativePath = getAppFrameworkConfigDir().toPath().relativize(file.toPath()).toString();
        return relativePath.replaceAll("/", "_").replaceAll(".json", "");
    }
}