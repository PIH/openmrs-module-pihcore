package org.openmrs.module.pihcore.rest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.context.Context;
import org.openmrs.module.pihcore.config.PihConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Note: There is another version of this class in Mirebalais, with methods specific to code that is in Mirebalais
 * Move those methods here when we migrate
 */
@Controller
public class PihConfigRestController {

    protected Log log = LogFactory.getLog(getClass());

    private static String REQUIRED_PRIVILEGE = "App: coreapps.systemAdministration";

    @Autowired
    PihConfigService configService;

    @RequestMapping(value = "/rest/v1/pihcore/config", method = RequestMethod.GET)
    @ResponseBody
    public Object getConfig() {
        if (Context.hasPrivilege(REQUIRED_PRIVILEGE)) {
            return configService.getPihConfig();
        }
        else {
            return HttpStatus.UNAUTHORIZED;
        }
    }
}
