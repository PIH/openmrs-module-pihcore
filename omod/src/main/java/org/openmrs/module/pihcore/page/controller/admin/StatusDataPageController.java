package org.openmrs.module.pihcore.page.controller.admin;

import org.apache.commons.lang.StringUtils;
import org.openmrs.api.PatientService;
import org.openmrs.module.pihcore.status.StatusDataDefinition;
import org.openmrs.module.pihcore.status.StatusDataLoader;
import org.openmrs.ui.framework.UiUtils;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.openmrs.ui.framework.page.PageModel;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Administrative tool to facilitate authoring and testing status data
 */
public class StatusDataPageController {

    public void get(PageModel model, UiUtils ui,
                      @RequestParam(required = false, value = "patientId") String patientId,
                      @RequestParam(required = false, value = "path") String path,
                      @RequestParam(required = false, value = "definitionId") String definitionId,
                      @SpringBean("patientService") PatientService patientService) throws IOException {

        model.addAttribute("patientId", patientId);
        model.addAttribute("path", path);
        model.addAttribute("definitionId", definitionId);

        List<String> paths = StatusDataLoader.getStatusDataConfigurationPaths();
        model.addAttribute("configPaths", paths);

        List<StatusDataDefinition> definitions = new ArrayList<>();
        StatusDataDefinition selectedDefinition = null;

        if (StringUtils.isNotBlank(path)) {
            definitions = StatusDataLoader.getStatusDataDefinitions(path);
            if (StringUtils.isNotBlank(definitionId)) {
                for (StatusDataDefinition definition : definitions) {
                    if (definition.getId().equalsIgnoreCase(definitionId)) {
                        selectedDefinition = definition;
                    }
                }
            }
        }

        model.addAttribute("definitions", definitions);
        model.addAttribute("definition", selectedDefinition);
    }
}
