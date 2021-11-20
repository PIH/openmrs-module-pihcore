package org.openmrs.module.pihcore.rest;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Patient;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;
import org.openmrs.module.pihcore.status.StatusDataEvaluator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

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
                throw new IllegalArgumentException("Please specify patient.  No patient found with id: " + patientId);
            }
            if (StringUtils.isEmpty(definitionId)) {
                return statusDataEvaluator.evaluate(p, path);
            }
            return statusDataEvaluator.evaluate(p, path, definitionId);
        }
        else {
            return HttpStatus.UNAUTHORIZED;
        }
    }
}
