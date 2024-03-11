package org.openmrs.module.pihcore.rest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.context.Context;
import org.openmrs.messagesource.MessageSourceService;
import org.openmrs.module.initializer.InitializerMessageSource;
import org.openmrs.module.pihcore.setup.ConfigurationSetup;
import org.openmrs.module.pihcore.config.PihConfigService;
import org.openmrs.module.pihcore.setup.HtmlFormSetup;
import org.openmrs.module.reporting.config.ReportDescriptor;
import org.openmrs.module.reporting.config.ReportLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import static org.springframework.http.HttpStatus.OK;

@Controller
public class PihConfigRestController {

    protected Log log = LogFactory.getLog(getClass());

    private static String REQUIRED_PRIVILEGE = "App: coreapps.systemAdministration";

    @Autowired
    ConfigurationSetup configurationSetup;

    @Autowired
    PihConfigService configService;

    @Autowired
    MessageSourceService messageSourceService;

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

    @RequestMapping(value = "/rest/v1/pihcore/config", method = RequestMethod.PUT)
    @ResponseBody
    public Object updateConfig() {
        if (!Context.hasPrivilege(REQUIRED_PRIVILEGE)) {
            return HttpStatus.UNAUTHORIZED;
        }
        try {
            configurationSetup.configureDependencies();
            updateMessageProperties();
            return OK;
        }
        catch (Exception e) {
            log.error(e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @RequestMapping(value = "/rest/v1/pihcore/config/reports/{reportKey}", method = RequestMethod.PUT)
    @ResponseBody
    public Object updateReport(@PathVariable("reportKey") String reportKey) {
        if (!Context.hasPrivilege(REQUIRED_PRIVILEGE)) {
            return HttpStatus.UNAUTHORIZED;
        }
        for (ReportDescriptor descriptor : ReportLoader.loadReportDescriptors()) {
            if (reportKey.equalsIgnoreCase(descriptor.getKey()) || reportKey.equalsIgnoreCase(descriptor.getUuid())) {
                ReportLoader.loadReportFromDescriptor(descriptor);
            }
        }
        return OK;
    }

    @RequestMapping(value = "/rest/v1/pihcore/config/reports", method = RequestMethod.PUT)
    @ResponseBody
    public Object updateAllReports() {
        if (!Context.hasPrivilege(REQUIRED_PRIVILEGE)) {
            return HttpStatus.UNAUTHORIZED;
        }
        ReportLoader.loadReportsFromConfig();
        return OK;
    }

    @RequestMapping(value = "/rest/v1/pihcore/config/htmlforms", method = RequestMethod.PUT)
    @ResponseBody
    public Object updateHtmlForms() {
        if (!Context.hasPrivilege(REQUIRED_PRIVILEGE)) {
            return HttpStatus.UNAUTHORIZED;
        }
        HtmlFormSetup.loadHtmlForms(false);
        return OK;
    }

    @RequestMapping(value = "/rest/v1/pihcore/config/appframework", method = RequestMethod.PUT)
    @ResponseBody
    public Object updateAppsAndExtensions() {
        if (!Context.hasPrivilege(REQUIRED_PRIVILEGE)) {
            return HttpStatus.UNAUTHORIZED;
        }
        configurationSetup.reloadAppsAndExtensions();
        return OK;
    }

    @RequestMapping(value = "/rest/v1/pihcore/config/messageproperties", method = RequestMethod.PUT)
    @ResponseBody
    public Object updateMessageProperties() {
        if (!Context.hasPrivilege(REQUIRED_PRIVILEGE)) {
            return HttpStatus.UNAUTHORIZED;
        }
        try {
            InitializerMessageSource messageSource = (InitializerMessageSource)Context.getMessageSourceService().getActiveMessageSource();
            messageSource.refreshCache();
            return OK;
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
}
