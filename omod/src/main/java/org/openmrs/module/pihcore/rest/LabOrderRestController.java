package org.openmrs.module.pihcore.rest;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.CareSetting;
import org.openmrs.EncounterRole;
import org.openmrs.EncounterType;
import org.openmrs.api.EncounterService;
import org.openmrs.api.OrderService;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.web.response.ResponseException;
import org.openmrs.util.ConfigUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Provides configuration and other endpoints for lab order entry
 */
@Controller
public class LabOrderRestController {

    protected final Log log = LogFactory.getLog(getClass());

    @Autowired
    OrderService orderService;

    @Autowired
    EncounterService encounterService;

    @RequestMapping(value = "/rest/v1/pihcore/labOrderConfig", method = RequestMethod.GET)
    @ResponseBody
    public Object getLabOrderConfig(HttpServletRequest request, HttpServletResponse response) throws ResponseException {
        SimpleObject ret = new SimpleObject();

        // Add care settings based on type
        SimpleObject careSettings = new SimpleObject();
        for (CareSetting careSetting : orderService.getCareSettings(false)) {
            careSettings.put(careSetting.getCareSettingType().name(), careSetting.getUuid());
        }
        ret.add("careSetting", careSettings);

        // Add configured default encounter type
        String encounterTypeProp = ConfigUtil.getGlobalProperty("orderentryowa.encounterType");
        EncounterType encounterType = null;
        if (StringUtils.isNotBlank(encounterTypeProp)) {
            encounterType = encounterService.getEncounterTypeByUuid(encounterTypeProp);
            if (encounterType == null) {
                encounterType = encounterService.getEncounterType(encounterTypeProp);
            }
        }
        if (encounterType == null) {
            log.warn("Invalid orderEntryowa.encounterType configuration: " + encounterTypeProp);
        }
        ret.add("encounterType", encounterType == null ? null : encounterType.getUuid());

        // Add provider role to associate with providers for new encounters
        String encounterRoleProp = ConfigUtil.getGlobalProperty("orderentryowa.encounterRole");
        EncounterRole encounterRole = null;
        if (StringUtils.isNotBlank(encounterRoleProp)) {
            encounterRole = encounterService.getEncounterRoleByUuid(encounterRoleProp);
            if (encounterRole == null) {
                encounterRole = encounterService.getEncounterRoleByName(encounterRoleProp);
            }
        }
        if (encounterRole == null) {
            log.warn("Invalid orderEntryowa.encounterRole configuration: " + encounterTypeProp);
        }
        ret.add("encounterRole", encounterRole == null ? null : encounterRole.getUuid());

        String autoExpireDaysProp = ConfigUtil.getGlobalProperty("orderentryowa.labOrderAutoExpireTimeInDays");
        int autoExpireDays = 30;
        if (StringUtils.isNotBlank(autoExpireDaysProp)) {
            try {
                autoExpireDays = Integer.parseInt(autoExpireDaysProp);
            }
            catch (NumberFormatException e) {
                log.warn("Invalid orderEntryowa.labOrderAutoExpireTimeInDays: " + autoExpireDaysProp);
            }
        }
        ret.add("autoExpireDays", autoExpireDays);
        return ret;
    }
}