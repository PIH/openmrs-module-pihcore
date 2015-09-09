package org.openmrs.module.pihcore.htmlformentry.action;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Concept;
import org.openmrs.Encounter;
import org.openmrs.Obs;
import org.openmrs.api.context.Context;
import org.openmrs.module.htmlformentry.CustomFormSubmissionAction;
import org.openmrs.module.htmlformentry.FormEntrySession;

import java.util.Date;

/**
 * Searches for a specimen collection date obs, and, if it exists, set the obs date of all obs (except
 * those of datatype Date or DateTime) to the specimen collection date
 * Used to enforce our business logic that the obs date of a test result should be set to the date the
 * specimen was collected, not the test result date, if the specimen collected date is known
 *
 * TODO potentially make this more granular, to handle forms that only have test results as a component of the form?
 */
public class SetObsDatesToSpecimenCollectionDateAction implements CustomFormSubmissionAction {

    private static final String SPECIMEN_DATE_COLLECTED_CONCEPT_CODE_PIH = "3038";

    protected final Log log = LogFactory.getLog(getClass());

    @Override
    public void applyAction(FormEntrySession formEntrySession) {

        Concept specimenCollectionDateConcept = Context.getConceptService().getConceptByMapping(SPECIMEN_DATE_COLLECTED_CONCEPT_CODE_PIH, "PIH");
        Date specimenCollectionDate = null;

        Encounter encounter = formEntrySession.getEncounter();

        if (specimenCollectionDateConcept != null) {

            for (Obs candidate : encounter.getAllObs(false)) {
                if (candidate.getConcept().equals(specimenCollectionDateConcept)) {
                    specimenCollectionDate = candidate.getValueDate();
                    candidate.setObsDatetime(specimenCollectionDate);
                    break;
                }
            }

            if (specimenCollectionDate != null) {
                for (Obs candidate : encounter.getAllObs(false)) {
                    if (!candidate.getConcept().getDatatype().containsDate()) {  // the logic here is that we don't want to change the obs date of other dates like "Test Result Date"
                        candidate.setObsDatetime(specimenCollectionDate);
                    }
                }
                Context.getEncounterService().saveEncounter(encounter);
            }
        }
        else {
            log.error("Unable to final specimen collection date cnept when applying setObsDatesToSpecimenCollectionDateAction");
        }

    }
}
