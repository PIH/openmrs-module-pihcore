package org.openmrs.module.pihcore.setup;

import org.apache.commons.io.IOUtils;
import org.openmrs.api.context.Context;
import org.openmrs.module.appframework.service.AppFrameworkService;
import org.openmrs.util.OpenmrsUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Objects;

public class AppFrameworkExpressionFunctionsSetup {

    public static void loadExpressionFunctions() {

        AppFrameworkService appFrameworkService = Context.getService(AppFrameworkService.class);

        File configDir = OpenmrsUtil.getDirectoryInApplicationDataDirectory("configuration");
        File appFrameworkDir = new File(configDir, "appframework");
        if (appFrameworkDir.exists() ) {
            File scriptsDir = new File(appFrameworkDir, "scripts");
            if (scriptsDir.exists()) {
                File[] functions = scriptsDir.listFiles();
                if (functions != null && functions.length > 0) {
                    for (File function : functions) {
                        String functionName = function.getName();
                        try (FileInputStream fileInputStream = new FileInputStream(function)) {
                            String value = IOUtils.toString(Objects.requireNonNull(fileInputStream), "UTF-8");
                            appFrameworkService.addRequireExpressionScript(functionName.substring(0, functionName.indexOf(".")), value);
                        } catch (IOException e) {
                            throw new RuntimeException("Failed to read appframework require expression: " + functionName + " from " + scriptsDir.getAbsolutePath(), e);
                        }
                    }
                }
            }
        }
    }
}
