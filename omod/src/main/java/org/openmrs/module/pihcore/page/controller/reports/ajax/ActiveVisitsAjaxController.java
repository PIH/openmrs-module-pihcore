package org.openmrs.module.pihcore.page.controller.reports.ajax;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.jackson.map.ObjectMapper;
import org.openmrs.Cohort;
import org.openmrs.api.APIException;
import org.openmrs.api.context.Context;
import org.openmrs.module.pihcore.config.Config;
import org.openmrs.module.pihcore.config.ConfigDescriptor;
import org.openmrs.module.reporting.cohort.definition.VisitCohortDefinition;
import org.openmrs.module.reporting.data.patient.definition.PatientDataDefinition;
import org.openmrs.module.reporting.dataset.DataSet;
import org.openmrs.module.reporting.dataset.DataSetUtil;
import org.openmrs.module.reporting.dataset.definition.PatientDataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.service.DataSetDefinitionService;
import org.openmrs.module.reporting.definition.library.AllDefinitionLibraries;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

@Controller
public class ActiveVisitsAjaxController {

    protected final Log log = LogFactory.getLog(getClass());

    @Autowired
    private Config config;

    @Autowired
    @Qualifier("reportingDataSetDefinitionService")
    private DataSetDefinitionService dsdService;

    @RequestMapping(value = "/module/pihcore/reports/ajax/activeVisitsList.form", method = RequestMethod.GET,  headers = "Accept=application/json")
    public void getActiveVisits( @RequestParam(value = "patientIds", required = false) Integer[] patientIds,
                                                HttpServletResponse response) throws EvaluationException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        AllDefinitionLibraries definitionLibraries = Context.getRegisteredComponents(AllDefinitionLibraries.class).get(0);

        EvaluationContext context = new EvaluationContext();
        Cohort c = null;
        if (patientIds != null && patientIds.length > 0 ) {
            if ( patientIds[0] == null ) {
                // there are no patients with last encounter at this specific location
                return;
            }
            c = new Cohort("active", "Patients with active visits", patientIds);
        }

        DataSet result = null ;
        PatientDataSetDefinition dsd = new PatientDataSetDefinition();

        VisitCohortDefinition visitCohortDefinition = new VisitCohortDefinition();
        visitCohortDefinition.setActive(true);
        if (c != null) {
            context.setBaseCohort(c);
        }

        dsd.addRowFilter(visitCohortDefinition, null);
        dsd.addColumn("patientId", definitionLibraries.getDefinition(PatientDataDefinition.class, "reporting.library.patientDataDefinition.builtIn.patientId"), "");
        dsd.addColumn("familyName", definitionLibraries.getDefinition(PatientDataDefinition.class, "reporting.library.patientDataDefinition.builtIn.preferredName.familyName"), "");
        dsd.addColumn("givenName", definitionLibraries.getDefinition(PatientDataDefinition.class, "reporting.library.patientDataDefinition.builtIn.preferredName.givenName"), "");

        // TODO: change this to not have to rely on an if/then
        if (config.getCountry().equals(ConfigDescriptor.Country.HAITI)) {
            dsd.addColumn("identifier", definitionLibraries.getDefinition(PatientDataDefinition.class, "mirebalais.patientDataCalculation.preferredZlEmrId.identifier"), "");
        }
        else {
            dsd.addColumn("identifier", definitionLibraries.getDefinition(PatientDataDefinition.class, "reporting.library.patientDataDefinition.builtIn.preferredIdentifier.identifier"), "");
        }

        dsd.addColumn("firstCheckinLocation", definitionLibraries.getDefinition(PatientDataDefinition.class, "mirebalais.patientDataCalculation.checkin.location"), "");
        dsd.addColumn("checkinDateTime", definitionLibraries.getDefinition(PatientDataDefinition.class, "mirebalais.patientDataCalculation.checkin.encounterDatetime"), "");
        dsd.addColumn("lastEncounterType", definitionLibraries.getDefinition(PatientDataDefinition.class, "mirebalais.patientDataCalculation.lastEncounter.type"), "");
        dsd.addColumn("lastEncounterLocation", definitionLibraries.getDefinition(PatientDataDefinition.class, "mirebalais.patientDataCalculation.lastEncounter.location"), "");
        dsd.addColumn("lastEncounterDateTime", definitionLibraries.getDefinition(PatientDataDefinition.class, "mirebalais.patientDataCalculation.lastEncounter.encounterDatetime"), "");
        dsd.addColumn("visitUuid", definitionLibraries.getDefinition(PatientDataDefinition.class, "mirebalais.patientDataCalculation.lastEncounter.visit.uuid"), "");

        result = dsdService.evaluate(dsd, context);
        String json = toJson(DataSetUtil.simplify(result));
        PrintWriter out = response.getWriter();
        out.print(json);
    }

    public static String toJson(Object obj) {
        ObjectMapper mapper = new ObjectMapper();
        StringWriter sw = new StringWriter();
        try {
            mapper.writeValue(sw, obj);
        }
        catch (Exception e) {
            throw new APIException("Error converting to JSON", e);
        }
        return sw.toString();
    }
}
