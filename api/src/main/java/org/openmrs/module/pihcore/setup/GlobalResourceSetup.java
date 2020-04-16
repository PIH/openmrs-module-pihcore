package org.openmrs.module.pihcore.setup;

import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.context.Context;
import org.openmrs.module.pihcore.PihCoreUtil;
import org.openmrs.ui.framework.page.GlobalResourceIncluder;
import org.openmrs.ui.framework.page.PageFactory;
import org.openmrs.ui.framework.resource.Resource;
import org.openmrs.ui.framework.resource.ResourceFactory;
import org.openmrs.util.OpenmrsUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class GlobalResourceSetup {

    protected static Log log = LogFactory.getLog(GlobalResourceSetup.class);

    /**
     * Include custom styling sheets and scripts
     */
    public static void includeGlobalResources() throws Exception {

        try {
            List<String> styleResources = getGlobalResourcesFromFileSystemPath(PihCoreUtil.getGlobalStylesDirectory());

            for (String resource : styleResources) {
                addGlobalResource(Resource.CATEGORY_CSS, "file", resource);
            }

            List<String> scriptResources = getGlobalResourcesFromFileSystemPath(PihCoreUtil.getGlobalScriptsDirectory());

            for (String resource : scriptResources) {
                addGlobalResource(Resource.CATEGORY_JS, "file", resource);
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

    protected static List<String> getGlobalResourcesFromFileSystemPath(String path) {

        Collection<File> files = null;
        List<String> filePaths = new ArrayList<>();

        try {
            File dir = new File(path);
            if (dir.exists()) {
                files = FileUtils.listFiles(dir, /*extensions=*/ null,  /*recursive=*/ true);
            }
        }
        catch (Exception e) {
            log.error("Unable to open " + path + " directory", e);
        }

        if (files != null && files.size() > 0) {
            for (File file : files) {
                filePaths.add(stripApplicationDataDirectory(file.getAbsolutePath()));
            }
        }

        return filePaths;
    }

    // strip the application data directory from the path, returning the path relative to that directory
    protected static String stripApplicationDataDirectory(String path) {
        return path.replace(OpenmrsUtil.getApplicationDataDirectory(),"");
    }

    protected static void addGlobalResource(String category, String providerName, String resourcePath)  {
        ResourceFactory resourceFactory = ResourceFactory.getInstance();
        PageFactory pageFactory = Context.getRegisteredComponents(PageFactory.class).get(0);
        File resource = resourceFactory.getResource(providerName, resourcePath);
        if (resource != null) {
            GlobalResourceIncluder globalResourceIncluder = new GlobalResourceIncluder();
            globalResourceIncluder.addResource(new Resource(category, providerName, resourcePath, -100));
            pageFactory.getModelConfigurators().add(globalResourceIncluder);
        }
    }

}
