package org.openmrs.module.pihcore.htmlformentry.action;

import org.openmrs.Visit;
import org.openmrs.module.htmlformentry.CustomFormSubmissionAction;
import org.openmrs.module.htmlformentry.FormEntryContext;
import org.openmrs.module.htmlformentry.FormEntrySession;
import org.openmrs.module.pihcore.PihCoreUtil;

/**
 * If this is the most recent visit for the patient at this location and we are in "ENTER" mode,
 * make sure the visit is opened
 *
 * Intended to be used with "Admission" forms so that if an admission form is added
 * to old visit, that visit is reopened
 */
public class ReopenVisitAction implements CustomFormSubmissionAction {

    @Override
    public void applyAction(FormEntrySession formEntrySession) {
        Visit visit = formEntrySession.getEncounter().getVisit();

        // if there is a visit associated with this encounter, and we are in "ENTER" mode
        if (visit != null && formEntrySession.getContext().getMode().equals(FormEntryContext.Mode.ENTER)) {
            PihCoreUtil.reopenVisit(visit);
        }
    }
}
