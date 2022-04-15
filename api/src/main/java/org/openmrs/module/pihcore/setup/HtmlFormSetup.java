package org.openmrs.module.pihcore.setup;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.FormService;
import org.openmrs.api.context.Context;
import org.openmrs.module.htmlformentry.HtmlForm;
import org.openmrs.module.htmlformentry.HtmlFormEntryService;
import org.openmrs.module.htmlformentryui.HtmlFormUtil;
import org.openmrs.module.pihcore.PihCoreConstants;
import org.openmrs.module.pihcore.PihCoreUtil;
import org.openmrs.module.pihcore.htmlformentry.CauseOfDeathListTagHandler;
import org.openmrs.module.pihcore.htmlformentry.FamilyHistoryRelativeCheckboxesTagHandler;
import org.openmrs.module.pihcore.htmlformentry.PastMedicalHistoryCheckboxTagHandler;
import org.openmrs.ui.framework.resource.ResourceFactory;

import java.io.File;
import java.io.IOException;
import java.util.Collection;

public class HtmlFormSetup {

    protected static Log log = LogFactory.getLog(HtmlFormSetup.class);

    public static void setupHtmlFormEntryTagHandlers() throws Exception {
        HtmlFormEntryService htmlFormEntryService = Context.getService(HtmlFormEntryService.class);
        htmlFormEntryService.addHandler(PihCoreConstants.HTMLFORMENTRY_CAUSE_OF_DEATH_LIST_TAG_NAME, new CauseOfDeathListTagHandler());
        htmlFormEntryService.addHandler(PihCoreConstants.HTMLFORMENTRY_PAST_MEDICAL_HISTORY_CHECKBOX_TAG_NAME, new PastMedicalHistoryCheckboxTagHandler());
        htmlFormEntryService.addHandler(PihCoreConstants.HTMLFORMENTRY_FAMILY_HISTORY_RELATIVE_CHECKBOXES_TAG_NAME, new FamilyHistoryRelativeCheckboxesTagHandler());
    }

    public static void loadHtmlForms(Boolean testingContext) {

        try {
            ResourceFactory resourceFactory = ResourceFactory.getInstance();
            FormService formService = Context.getFormService();
            HtmlFormEntryService htmlFormEntryService = Context.getService(HtmlFormEntryService.class);

            File htmlformDir = new File(PihCoreUtil.getFormDirectory());
            Collection<File> files = FileUtils.listFiles(htmlformDir, null, true);

            for (File file : files) {
                try {
                    HtmlForm form = HtmlFormUtil.getHtmlFormFromUiResource(resourceFactory, formService, htmlFormEntryService, "file:" + file.getAbsolutePath());
                    // If the form is not already configured and saved as published, do so here
                    if (BooleanUtils.isNotTrue(form.getForm().getPublished())) {
                        form.getForm().setPublished(true);
                        htmlFormEntryService.saveHtmlForm(form);
                    }
                } catch (IOException e) {
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
}
