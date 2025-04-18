package org.openmrs.module.pihcore.page.controller.admin;

import lombok.Data;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.module.appframework.context.AppContextModel;
import org.openmrs.module.appframework.domain.AppDescriptor;
import org.openmrs.module.appframework.domain.Extension;
import org.openmrs.module.appframework.service.AppFrameworkService;
import org.openmrs.module.appui.UiSessionContext;
import org.openmrs.module.coreapps.contextmodel.AppContextModelGenerator;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.openmrs.ui.framework.page.PageModel;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * Administrative tool to facilitate authoring and testing app and extension expressions
 */
public class ExpressionTesterPageController {

    private static String REQUIRED_PRIVILEGE = "App: coreapps.systemAdministration";

    public void controller(PageModel model,
                    @RequestParam(required = false, value = "patientId") Patient patient,
                    @RequestParam(required = false, value = "dashboard") String dashboard,
                    @RequestParam(required = false, value = "extensionPoint") String extensionPoint,
                    @RequestParam(required = false, value = "extension") String extension,
                    @RequestParam(required = false, value = "expressionToTest") String expressionToTest,
                    @RequestParam(required = false, value = "app") AppDescriptor app,
                    @SpringBean("appContextModelGenerator") AppContextModelGenerator appContextModelGenerator,
                    @SpringBean("appFrameworkService") AppFrameworkService appFrameworkService,
                    UiSessionContext sessionContext) throws IOException {

        Context.requirePrivilege(REQUIRED_PRIVILEGE);

        model.addAttribute("patientId", patient == null ? "" : patient.getUuid());
        model.addAttribute("app", app == null ? "" : app.getId());
        model.addAttribute("dashboard", dashboard);
        model.addAttribute("extensionPoint", extensionPoint);
        model.addAttribute("extension", extension);

        AppContextModel contextModel = appContextModelGenerator.generateAppContextModel(sessionContext, patient);

        List<EvaluationResult> evaluationResults = new ArrayList<>();
        Set<String> extensionPoints = new TreeSet<>();
        for (Extension e : appFrameworkService.getAllEnabledExtensions()) {
            extensionPoints.add(e.getExtensionPointId());
            if (e.getId().equalsIgnoreCase(extension) && StringUtils.isBlank(expressionToTest)) {
                expressionToTest = e.getRequire();
            }
            if (e.getExtensionPointId().equalsIgnoreCase(extensionPoint)) {
                EvaluationResult result = new EvaluationResult();
                result.setExtension(e);
                try {
                    result.setPassed(appFrameworkService.checkRequireExpressionStrict(e, contextModel));
                }
                catch (Exception exception) {
                    result.setException(exception);
                }
                evaluationResults.add(result);
            }
        }
        evaluationResults.sort(Comparator.comparing(r -> r.getExtension().getId()));
        model.addAttribute("extensionPoints", extensionPoints);
        model.addAttribute("evaluationResults", evaluationResults);

        String requireExpressionContext = null;
        String requireExpressionContextException = null;
        try {
            requireExpressionContext = appFrameworkService.getRequireExpressionContext(contextModel);
        }
        catch (Exception e) {
            requireExpressionContextException = e.getMessage();
        }
        model.addAttribute("requireExpressionContext", requireExpressionContext);
        model.addAttribute("requireExpressionContextException", requireExpressionContextException);

        if (StringUtils.isNotBlank(expressionToTest)) {
            expressionToTest = StringEscapeUtils.unescapeHtml(expressionToTest);
        }

        model.addAttribute("expressionToTest", expressionToTest == null ? "" : expressionToTest.trim());

        boolean expressionToTestResult = false;
        Exception expressionToTestException = null;
        try {
            Extension extensionToTest = new Extension();
            extensionToTest.setRequire(expressionToTest);
            expressionToTestResult = appFrameworkService.checkRequireExpressionStrict(extensionToTest, contextModel);
        }
        catch (Exception exception) {
            expressionToTestException = exception;
        }
        model.addAttribute("expressionToTestResult", expressionToTestResult);
        model.addAttribute("expressionToTestException", expressionToTestException);
    }

    @Data
    static class EvaluationResult {
        private Extension extension;
        private boolean passed;
        private Exception exception;
    }
}
