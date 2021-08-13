package org.openmrs.module.pihcore.htmlformentry.action;

import org.openmrs.Patient;
import org.openmrs.Visit;
import org.openmrs.api.context.Context;
import org.openmrs.module.htmlformentry.CustomFormSubmissionAction;
import org.openmrs.module.htmlformentry.FormEntryContext;
import org.openmrs.module.htmlformentry.FormEntrySession;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

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
        Patient patient = formEntrySession.getPatient();

        // if there is a visit associated with this encounter, and we are in "ENTER" mode
        if (visit != null && formEntrySession.getContext().getMode().equals(FormEntryContext.Mode.ENTER)) {

            // get all visits for the patient with a start date time after the current visit at the same location
            List<Visit> moreRecentVisits = Context.getVisitService().getVisits(null, Collections.singletonList(patient), Collections.singletonList(visit.getLocation()), null, visit.getStartDatetime(), null, null, null, null, true, false);

            // exclude the visit we are working with itself
            Iterator<Visit> i = moreRecentVisits.iterator();
            while (i.hasNext()) {
                Visit v = i.next();
                if (v.equals(visit)) {
                    i.remove();
                }
            }

            // if none, and the visit is closed, reopen
            if ((moreRecentVisits == null || moreRecentVisits.size() == 0) && visit.getStopDatetime() != null) {
                visit.setStopDatetime(null);
                Context.getVisitService().saveVisit(visit);
            }
        }
    }
}
