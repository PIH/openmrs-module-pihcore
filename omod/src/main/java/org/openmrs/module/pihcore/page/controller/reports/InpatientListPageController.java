package org.openmrs.module.pihcore.page.controller.reports;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.module.coreapps.CoreAppsProperties;
import org.openmrs.module.pihcore.PihEmrConfigConstants;
import org.openmrs.module.pihcore.reporting.cohort.definition.InpatientLocationCohortDefinition;
import org.openmrs.module.reporting.data.patient.definition.PatientDataDefinition;
import org.openmrs.module.reporting.dataset.DataSet;
import org.openmrs.module.reporting.dataset.DataSetUtil;
import org.openmrs.module.reporting.dataset.definition.PatientDataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.service.DataSetDefinitionService;
import org.openmrs.module.reporting.definition.library.AllDefinitionLibraries;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.openmrs.ui.framework.page.PageModel;

import java.util.Date;

public class InpatientListPageController {
    private final Log log = LogFactory.getLog(getClass());

    public void get(PageModel model,
                                 @SpringBean CoreAppsProperties coreAppsProperties,
                                 @SpringBean AllDefinitionLibraries libraries,
                                 @SpringBean DataSetDefinitionService dsdService) throws EvaluationException {
        EvaluationContext context = new EvaluationContext();
        Date today = new Date();
        context.addParameterValue("day", today);

        DataSet result = null ;
        PatientDataSetDefinition dsd = new PatientDataSetDefinition();
        InpatientLocationCohortDefinition inpatientLocationCohortDefinition = new InpatientLocationCohortDefinition();
        dsd.addRowFilter(inpatientLocationCohortDefinition ,null);
        dsd.addColumn("patientId", libraries.getDefinition(PatientDataDefinition.class, "reporting.library.patientDataDefinition.builtIn.patientId"), "");
        dsd.addColumn("familyName", libraries.getDefinition(PatientDataDefinition.class, "reporting.library.patientDataDefinition.builtIn.preferredName.familyName"), "");
        dsd.addColumn("givenName", libraries.getDefinition(PatientDataDefinition.class, "reporting.library.patientDataDefinition.builtIn.preferredName.givenName"), "");
        dsd.addColumn("zlEmrId", libraries.getDefinition(PatientDataDefinition.class, "mirebalais.patientDataCalculation.preferredZlEmrId.identifier"), "");
        dsd.addColumn("dossierNumber", libraries.getDefinition(PatientDataDefinition.class, "mirebalais.patientDataCalculation.mostRecentDossierNumber.identifier"), "");
        dsd.addColumn("firstAdmittedLocation", libraries.getDefinition(PatientDataDefinition.class, "mirebalais.patientDataCalculation.admission.location"), "");
        dsd.addColumn("admissionDateTime", libraries.getDefinition(PatientDataDefinition.class, "mirebalais.patientDataCalculation.admission.encounterDatetime"), "");
        dsd.addColumn("inpatientLocation", libraries.getDefinition(PatientDataDefinition.class, "mirebalais.patientDataCalculation.inpatient.location"), "");
        dsd.addColumn("inpatientDateTime", libraries.getDefinition(PatientDataDefinition.class, "mirebalais.patientDataCalculation.inpatient.encounterDatetime"), "");

        result = dsdService.evaluate(dsd, context);

        model.addAttribute("inpatientsList", DataSetUtil.simplify(result));
        model.addAttribute("dashboardUrl", coreAppsProperties.getDashboardUrl());
        model.put("privilegePatientDashboard", PihEmrConfigConstants.PRIVILEGE_APP_COREAPPS_PATIENT_DASHBOARD);  // used to determine if we display links to patient dashboard)
    }


}
