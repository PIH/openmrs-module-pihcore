package org.openmrs.module.pihcore.setup;

import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.context.Context;
import org.openmrs.ui.framework.page.GlobalResourceIncluder;
import org.openmrs.ui.framework.page.PageFactory;
import org.openmrs.ui.framework.resource.Resource;
import org.openmrs.ui.framework.resource.ResourceFactory;
import org.openmrs.util.OpenmrsUtil;

import java.io.File;

public class GlobalResourceSetup {

    protected static Log log = LogFactory.getLog(GlobalResourceSetup.class);

    public static final String GLOBAL_STYLES_DIR = "pih/styles/global";
    public static final String GLOBAL_SCRIPTS_DIR = "pih/scripts/global";

    /**
     * Include custom styling sheets and scripts
     */
    public static void includeGlobalResources() throws Exception {

        try {
            File configDir = OpenmrsUtil.getDirectoryInApplicationDataDirectory("configuration");
            File styleDir = new File(configDir, GLOBAL_STYLES_DIR);
            if (styleDir.exists()) {
                for (File cssFile : FileUtils.listFiles(styleDir,null,true)) {
                    String relativeResourcePath = configDir.toPath().relativize(cssFile.toPath()).toString();
                    addGlobalResource(Resource.CATEGORY_CSS, "file", relativeResourcePath);
                    log.warn("Added global style resource: " + relativeResourcePath);
                }
            }
            File scriptDir = new File(configDir, GLOBAL_SCRIPTS_DIR);
            if (scriptDir.exists()) {
                for (File jsFile : FileUtils.listFiles(scriptDir,null,true)) {
                    String relativeResourcePath = configDir.toPath().relativize(jsFile.toPath()).toString();
                    addGlobalResource(Resource.CATEGORY_JS, "file", relativeResourcePath);
                    log.warn("Added global script resource: " + relativeResourcePath);
                }
            }
        }
        // this entire catch is a hack to get component test to pass until we find the proper way to mock this
        // (see HtmlFormSetup where we do something similar)
        catch (Exception e) {
            if (ResourceFactory.getInstance().getResourceProviders() == null) {
                log.error("Unable to load GlobalResourcs--this error is expected when running component tests");
            } else {
                throw e;
            }
        }
    }

    protected static void addGlobalResource(String category, String providerName, String resourcePath) {
        ResourceFactory resourceFactory = ResourceFactory.getInstance();
        PageFactory pageFactory = Context.getRegisteredComponents(PageFactory.class).get(0);
        File resource = resourceFactory.getResource(providerName, resourcePath);
        if (resource != null && pageFactory != null) {
            GlobalResourceIncluder globalResourceIncluder = new GlobalResourceIncluder();
            int priority = -100;
            globalResourceIncluder.addResource(new Resource(category, providerName, resourcePath, priority));
            pageFactory.getModelConfigurators().add(globalResourceIncluder);
        }
    }

}
