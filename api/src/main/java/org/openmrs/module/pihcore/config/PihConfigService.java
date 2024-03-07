package org.openmrs.module.pihcore.config;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.apache.commons.lang.StringUtils;
import org.openmrs.module.appframework.config.AppFrameworkConfig;
import org.openmrs.module.appframework.domain.AppDescriptor;
import org.openmrs.module.appframework.domain.AppTemplate;
import org.openmrs.module.appframework.domain.Extension;
import org.openmrs.module.appframework.service.AppFrameworkService;
import org.openmrs.module.initializer.Domain;
import org.openmrs.module.initializer.api.ConfigDirUtil;
import org.openmrs.module.initializer.api.InitializerService;
import org.openmrs.module.initializer.api.loaders.Loader;
import org.openmrs.module.pihcore.config.model.AppAndExtensionConfig;
import org.openmrs.module.pihcore.config.model.ChecksumConfigFile;
import org.openmrs.module.pihcore.config.model.ConfigDomain;
import org.openmrs.module.pihcore.config.model.ConfigFile;
import org.openmrs.module.pihcore.config.model.InitializerConfig;
import org.openmrs.module.pihcore.config.model.PihConfig;
import org.openmrs.module.pihcore.setup.InitializerSetup;
import org.openmrs.module.reporting.config.ReportLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.Collection;
import java.util.List;

@Component
public class PihConfigService {

    @Autowired
    Config config;

    @Autowired
    AppFrameworkConfig appFrameworkConfig;

    @Autowired
    AppFrameworkService appFrameworkService;

    @Autowired
    InitializerService initializerService;

    public PihConfig getPihConfig() {
        PihConfig c = new PihConfig();
        c.setConfigDir(ConfigLoader.getPihConfigurationDirRuntimeProperty(""));
        c.setConfigProperty(ConfigLoader.getRuntimeConfiguration(""));
        c.setConfigDescriptor(config.getDescriptor());
        c.setInitializerConfig(getInitializerConfig());
        c.getDomains().put("reports", getReportsConfig());
        c.setAppAndExtensionConfig(getAppAndExtensionConfig());
        return c;
    }

    public InitializerConfig getInitializerConfig() {
        InitializerConfig c = new InitializerConfig();
        c.setConfigDir(new File(initializerService.getConfigDirPath()).getAbsolutePath());
        for (Domain d : Domain.values()) {
            String domainName = d.getName().toLowerCase();
            c.getDomains().put(domainName, new ConfigDomain(domainName));
        }
        for (Loader loader : initializerService.getLoaders()) {
            try {
                ConfigDirUtil dirUtil = loader.getDirUtil();
                File domainDir = new File(dirUtil.getDomainDirPath());
                String domainName = loader.getDomainName().toLowerCase();
                ConfigDomain configDomain = c.getDomains().get(domainName);
                if (configDomain == null) {
                    configDomain = new ConfigDomain(domainName);
                    c.getDomains().put(domainName, configDomain);
                }
                List<String> exclusionsForLoader = InitializerSetup.getExclusionsForLoader(loader, config);
                if (domainDir.exists() && domainDir.isDirectory()) {
                    for (File f : FileUtils.listFiles(domainDir, FileFilterUtils.trueFileFilter(), TrueFileFilter.INSTANCE)) {
                        String previousChecksum = dirUtil.readSavedChecksum(f);
                        String currentChecksum = dirUtil.getChecksumIfChanged(f);
                        ChecksumConfigFile checksumConfigFile = new ChecksumConfigFile();
                        checksumConfigFile.setPath(domainDir.toPath().relativize(f.toPath()).toString());
                        checksumConfigFile.setEnabled(!exclusionsForLoader.contains(f.getName().toLowerCase()));
                        checksumConfigFile.setPreviousChecksum(previousChecksum);
                        checksumConfigFile.setCurrentChecksum(StringUtils.isEmpty(currentChecksum) ? previousChecksum : currentChecksum);
                        configDomain.getConfigFiles().add(checksumConfigFile);
                    }
                }
            }
            catch (Exception e) {
                throw new IllegalStateException("Unable to load files for loader: " + loader, e);
            }
        }
        return c;
    }

    public ConfigDomain getReportsConfig() {
        ConfigDomain reportsDomain = new ConfigDomain("reports");
        File reportDir = new File(ReportLoader.getReportingDescriptorsConfigurationDir());
        if (reportDir.exists()) {
            for (File f : FileUtils.listFiles(reportDir, FileFilterUtils.suffixFileFilter("yml"), TrueFileFilter.INSTANCE)) {
                ConfigFile reportFile = new ConfigFile();
                reportFile.setEnabled(true);
                reportFile.setPath(reportDir.toPath().relativize(f.toPath()).toString());
                reportsDomain.getConfigFiles().add(reportFile);
            }
        }
        return reportsDomain;
    }

    public AppAndExtensionConfig getAppAndExtensionConfig() {
        AppAndExtensionConfig c = new AppAndExtensionConfig();
        c.setLoadAppsFromClasspath(appFrameworkConfig.getLoadAppsFromClasspath());
        c.setAppsEnabledByDefault(appFrameworkConfig.getAppsEnabledByDefault());
        c.setExtensionsEnabledByDefault(appFrameworkConfig.getExtensionsEnabledByDefault());
        for (AppTemplate appTemplate : appFrameworkService.getAllAppTemplates()) {
            c.getAppTemplates().put(appTemplate.getId(), appTemplate);
        }
        List<AppDescriptor> enabledApps = appFrameworkService.getAllEnabledApps();
        for (AppDescriptor app : appFrameworkService.getAllApps()) {
            c.getApps().put(app.getId(), app);
            if (enabledApps.contains(app)) {
                c.getEnabledApps().add(app.getId());
            }
        }

        List<Extension> enabledExtensions = appFrameworkService.getAllEnabledExtensions();
        for (Extension extension : appFrameworkService.getAllExtensions(null)) {
            c.getExtensions().put(extension.getId(), extension);
            if (enabledExtensions.contains(extension)) {
                c.getEnabledExtensions().add(extension.getId());
            }
        }

        return c;
    }
}
