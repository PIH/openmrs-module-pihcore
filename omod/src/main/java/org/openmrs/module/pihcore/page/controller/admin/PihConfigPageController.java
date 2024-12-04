package org.openmrs.module.pihcore.page.controller.admin;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.ObjectWriter;
import org.openmrs.api.context.Context;
import org.openmrs.module.pihcore.config.PihConfigService;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.openmrs.ui.framework.page.PageModel;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Simple page to allow viewing the current PIH Config
 */
public class PihConfigPageController {

    private static final String REQUIRED_PRIVILEGE = "App: coreapps.systemAdministration";

    public void get(PageModel model, @SpringBean PihConfigService pihConfigService) throws IOException {
        String configJson;
        ObjectWriter writer = new ObjectMapper().writer();
        if (Context.hasPrivilege(REQUIRED_PRIVILEGE)) {
            configJson = writer.writeValueAsString(pihConfigService.getPihConfig());
        }
        else {
            Map<String, String> data = new HashMap<>();
            data.put("error", "Privilege required: " + REQUIRED_PRIVILEGE);
            configJson = writer.writeValueAsString(data);
        }
        model.addAttribute("configJson", configJson);
    }
}