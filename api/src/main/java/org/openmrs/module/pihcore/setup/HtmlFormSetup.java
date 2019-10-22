package org.openmrs.module.pihcore.setup;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.context.Context;
import org.openmrs.module.htmlformentry.HtmlFormEntryService;
import org.openmrs.module.pihcore.PihCoreConstants;
import org.openmrs.module.pihcore.handler.HtmlFormIncludeHandler;
import org.openmrs.module.pihcore.htmlformentry.CauseOfDeathListTagHandler;
import org.openmrs.module.pihcore.htmlformentry.FamilyHistoryRelativeCheckboxesTagHandler;
import org.openmrs.module.pihcore.htmlformentry.PastMedicalHistoryCheckboxTagHandler;

public class HtmlFormSetup {

    protected static Log log = LogFactory.getLog(HtmlFormSetup.class);

    public static void setupHtmlFormEntryTagHandlers() throws Exception {
        HtmlFormEntryService htmlFormEntryService = Context.getService(HtmlFormEntryService.class);
        htmlFormEntryService.addHandler(PihCoreConstants.HTMLFORMENTRY_CAUSE_OF_DEATH_LIST_TAG_NAME, new CauseOfDeathListTagHandler());
        htmlFormEntryService.addHandler(PihCoreConstants.HTMLFORMENTRY_PAST_MEDICAL_HISTORY_CHECKBOX_TAG_NAME, new PastMedicalHistoryCheckboxTagHandler());
        htmlFormEntryService.addHandler(PihCoreConstants.HTMLFORMENTRY_FAMILY_HISTORY_RELATIVE_CHECKBOXES_TAG_NAME, new FamilyHistoryRelativeCheckboxesTagHandler());
        htmlFormEntryService.addHandler(PihCoreConstants.HTMLFORMENTRY_INCLUDE_SECTION_TAG_NAME, new HtmlFormIncludeHandler());
    }

}
