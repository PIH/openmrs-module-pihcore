package org.openmrs.module.pihcore.web;

import org.openmrs.module.appframework.domain.AppDescriptor;
import org.openmrs.module.appframework.service.AppFrameworkService;
import org.openmrs.module.pihcore.config.Config;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
public class ConfigController {

    @Autowired
    private Config config;

    @RequestMapping(value = "/module/pihcore/config.json", method = RequestMethod.GET)
    @ResponseBody
    public Config getConfig() {
        return config;
    }


}
