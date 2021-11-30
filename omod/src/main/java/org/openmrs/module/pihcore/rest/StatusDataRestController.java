package org.openmrs.module.pihcore.rest;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Patient;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;
import org.openmrs.module.pihcore.status.StatusDataDefinition;
import org.openmrs.module.pihcore.status.StatusDataEvaluator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Controller
public class StatusDataRestController {

    protected Log log = LogFactory.getLog(getClass());

    private static String REQUIRED_PRIVILEGE = "App: coreapps.systemAdministration";

    @Autowired
    private PatientService patientService;

    @Autowired
    private StatusDataEvaluator statusDataEvaluator;

    @RequestMapping(value = "/rest/v1/pihcore/statusData", method = RequestMethod.GET)
    @ResponseBody
    public Object getConfig(
            @RequestParam("patientId") String patientId,
            @RequestParam("path") String path,
            @RequestParam(value = "definitionId", required = false) String definitionId) {
        if (Context.hasPrivilege(REQUIRED_PRIVILEGE)) {
            Patient p = patientService.getPatientByUuid(patientId);
            if (p == null) {
                try {
                    p = patientService.getPatient(Integer.parseInt(patientId));
                }
                catch (Exception e){}
            }
            if (p == null) {
                return errorResponse("Please specify patient.  No patient found with id: " + patientId);
            }
            try {
                if (StringUtils.isEmpty(definitionId)) {
                    return statusDataEvaluator.evaluate(p, path);
                }
                return statusDataEvaluator.evaluate(p, path, definitionId);
            }
            catch (Exception e) {
                return errorResponse(e);
            }
        }
        else {
            return HttpStatus.UNAUTHORIZED;
        }
    }

    @RequestMapping(value = "/rest/v1/pihcore/statusData", method = RequestMethod.POST)
    @ResponseBody
    public Object postConfig(
            @RequestParam("patientId") String patientId,
            @RequestParam("statusDataQuery") String statusDataQuery,
            @RequestParam(value = "expression", required = false) String expression) {
        if (Context.hasPrivilege(REQUIRED_PRIVILEGE)) {
            Patient p = patientService.getPatientByUuid(patientId);
            if (p == null) {
                try {
                    p = patientService.getPatient(Integer.parseInt(patientId));
                }
                catch (Exception e){}
            }
            if (p == null) {
                return errorResponse("Please specify patient.  No patient found with id: " + patientId);
            }
            StatusDataDefinition definition = new StatusDataDefinition();
            definition.setStatusDataQuery(statusDataQuery);
            definition.setValueExpression(expression);
            try {
                return statusDataEvaluator.evaluate(p, definition);
            }
            catch (Exception e) {
                return errorResponse(e);
            }
        }
        else {
            return HttpStatus.UNAUTHORIZED;
        }
    }

    protected ResponseEntity<Map<String, Object>> errorResponse(String message) {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("errorMessages", Collections.singletonList(message));
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(data);
    }

    protected ResponseEntity<Map<String, Object>> errorResponse(Throwable t) {
        Map<String, Object> data = new LinkedHashMap<>();
        List<String> errorMessages = new ArrayList<>();
        while (t != null && !errorMessages.contains(t.getMessage())) {
            errorMessages.add(t.getMessage());
            t = t.getCause();
        }
        data.put("errorMessages",errorMessages);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(data);
    }
}
