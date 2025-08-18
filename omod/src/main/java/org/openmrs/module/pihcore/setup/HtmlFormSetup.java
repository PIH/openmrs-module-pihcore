package org.openmrs.module.pihcore.setup;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.FormResource;
import org.openmrs.api.FormService;
import org.openmrs.api.context.Context;
import org.openmrs.customdatatype.datatype.FreeTextDatatype;
import org.openmrs.module.appframework.domain.Extension;
import org.openmrs.module.appframework.service.AppFrameworkService;
import org.openmrs.module.htmlformentry.HtmlForm;
import org.openmrs.module.htmlformentry.HtmlFormEntryService;
import org.openmrs.module.pihcore.PihCoreConstants;
import org.openmrs.module.pihcore.PihCoreUtil;
import org.openmrs.module.pihcore.htmlformentry.CauseOfDeathListTagHandler;
import org.openmrs.module.pihcore.htmlformentry.FamilyHistoryRelativeCheckboxesTagHandler;
import org.openmrs.module.pihcore.htmlformentry.PastMedicalHistoryCheckboxTagHandler;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HtmlFormSetup {

    protected static Log log = LogFactory.getLog(HtmlFormSetup.class);

    public static final String FORM_ENGINE_RESOURCE_NAME = "formEngine";

    public static final String FORM_ENGINE_RESOURCE_VALUE = "htmlformentry";

    public static final String UI_STYLE_RESOURCE_NAME = "uiStyle";

    public static final String UI_STYLE_RESOURCE_VALUE = "simple";

    public static final Map<String, String> CONFIG_FORMS = new HashMap<>();

    public static void setupHtmlFormEntryTagHandlers() throws Exception {
        HtmlFormEntryService htmlFormEntryService = Context.getService(HtmlFormEntryService.class);
        htmlFormEntryService.addHandler(PihCoreConstants.HTMLFORMENTRY_CAUSE_OF_DEATH_LIST_TAG_NAME, new CauseOfDeathListTagHandler());
        htmlFormEntryService.addHandler(PihCoreConstants.HTMLFORMENTRY_PAST_MEDICAL_HISTORY_CHECKBOX_TAG_NAME, new PastMedicalHistoryCheckboxTagHandler());
        htmlFormEntryService.addHandler(PihCoreConstants.HTMLFORMENTRY_FAMILY_HISTORY_RELATIVE_CHECKBOXES_TAG_NAME, new FamilyHistoryRelativeCheckboxesTagHandler());
    }

    public static void loadHtmlForms(Boolean testingContext) {
        try {
            // Get all htmlform extension from the appframework
            AppFrameworkService appFrameworkService = Context.getService(AppFrameworkService.class);
            List<Extension> htmlformExtensions = new ArrayList<Extension>();
            appFrameworkService.getAllEnabledExtensions().forEach(extension -> {
                if (extension.getUrl() != null && extension.getUrl().contains("htmlformentryui")) {
                    htmlformExtensions.add(extension);
                }
            });

            HtmlFormEntryService htmlFormEntryService = Context.getService(HtmlFormEntryService.class);
            FormService formService = Context.getService(FormService.class);
            File htmlformDir = new File(PihCoreUtil.getFormDirectory());
            Collection<File> files = FileUtils.listFiles(htmlformDir,  new String[] {"xml"}, true);
            for (File file : files) {
                try {
                    String xmlData = FileUtils.readFileToString(file, "UTF-8");
                    HtmlForm htmlForm = htmlFormEntryService.saveHtmlFormFromXml(xmlData);
                    CONFIG_FORMS.put(htmlForm.getUuid(), file.getName());
                    // If the form is not already configured and saved as published, do so here
                    if (BooleanUtils.isNotTrue(htmlForm.getForm().getPublished())) {
                        htmlForm.getForm().setPublished(true);
                        htmlFormEntryService.saveHtmlForm(htmlForm);
                    }
                    // If the form does not already have resource setting formEngine, add here
                    FormResource formEngineResource = formService.getFormResource(htmlForm.getForm(), FORM_ENGINE_RESOURCE_NAME);
                    if (formEngineResource == null) {
                        FormResource resource = new FormResource();
                        resource.setForm(htmlForm.getForm());
                        resource.setName(FORM_ENGINE_RESOURCE_NAME);
                        resource.setValueReferenceInternal(FORM_ENGINE_RESOURCE_VALUE);
                        resource.setDatatypeClassname(FreeTextDatatype.class.getName());
                        formService.saveFormResource(resource);
                    }
                    // If this form is configured with an extension pointing to simple form entry, add this as a resource
                    boolean isSimpleFormUi = false;
                    for (Extension extension : htmlformExtensions) {
                        if (isHtmlFormInUrl(htmlForm, file, extension.getUrl())) {
                            if (extension.getUrl().contains("SimpleUi")) {
                                isSimpleFormUi = true;
                                break;
                            }
                        }
                    }
                    FormResource uiModeResource = formService.getFormResource(htmlForm.getForm(), UI_STYLE_RESOURCE_NAME);
                    if (isSimpleFormUi) {
                        if (uiModeResource == null) {
                            FormResource resource = new FormResource();
                            resource.setForm(htmlForm.getForm());
                            resource.setName(UI_STYLE_RESOURCE_NAME);
                            resource.setValueReferenceInternal(UI_STYLE_RESOURCE_VALUE);
                            resource.setDatatypeClassname(FreeTextDatatype.class.getName());
                            formService.saveFormResource(resource);
                        }
                    }
                    else {
                        if (uiModeResource != null) {
                            formService.purgeFormResource(uiModeResource);
                        }
                    }
                }
                catch (IOException e) {
                    throw new RuntimeException("Unable to load HTML Form at path: " + file, e);
                }
            }
        }
        catch (Exception e) {
            if (testingContext) {
                log.error("Unable to load HTML forms--this error is expected when running component tests");
            } else {
                throw e;
            }
        }
    }

    public static boolean isHtmlFormInUrl(HtmlForm htmlForm, File htmlFormFile, String extensionUrl) {
        if (extensionUrl.contains(htmlForm.getUuid())) {
            return true;
        }
        else if (extensionUrl.contains(htmlForm.getForm().getUuid())) {
            return true;
        }
        return extensionUrl.contains(htmlFormFile.getName());
    }
}
