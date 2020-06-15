package org.openmrs.module.pihcore.htmlformentry.action;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.joda.time.DateTime;
import org.openmrs.Encounter;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.PatientProgram;
import org.openmrs.Program;
import org.openmrs.api.ConceptService;
import org.openmrs.api.ProgramWorkflowService;
import org.openmrs.api.context.Context;
import org.openmrs.module.emrapi.disposition.Disposition;
import org.openmrs.module.emrapi.disposition.DispositionDescriptor;
import org.openmrs.module.emrapi.disposition.DispositionService;
import org.openmrs.module.htmlformentry.CustomFormSubmissionAction;
import org.openmrs.module.htmlformentry.FormEntrySession;

import java.util.Date;
import java.util.List;

public class ExitPatientFromCovidProgramAction implements CustomFormSubmissionAction {

    // TODO if we decide we want to use this pattern to other programs, would make sense to see if we can pass in
    // TODO the program uuid to this action; would probably mean expanding the PostSubmissionAction tag to accept parameters

    protected Log log = LogFactory.getLog(getClass());

    private static String COVID_PROGRAM_UUID = "dc227c44-d68e-431b-a20d-a60bb3496543";

    private static String DISPOSITION_TRANSFER_OUT = "transferOutOfHospital";

    private static String DISPOSITION_DEATH = "markPatientDead";

    private static String DISPOSITION_DISCHARGE = "discharge";

    private static String CONCEPT_TRANSFER_OUT = "PATIENT TRANSFERRED OUT";

    private static String CONCEPT_DIED = "PATIENT DIED";

    private static String CONCEPT_DISCHARGE = "TREATMENT STOPPED";

    @Override
    public void applyAction(FormEntrySession formEntrySession) {
        closeCovidProgram(formEntrySession, Context.getProgramWorkflowService(), Context.getService(DispositionService.class), Context.getConceptService());
    }


    private void closeCovidProgram(FormEntrySession formEntrySession,
                                   ProgramWorkflowService programWorkflowService,
                                   DispositionService dispositionService,
                                   ConceptService conceptService) {
        Patient patient = formEntrySession.getPatient();
        Program covid = programWorkflowService.getProgramByUuid(COVID_PROGRAM_UUID);
        Encounter encounter = formEntrySession.getEncounter();
        // use the encounter date *without* the time component
        Date encounterDate = (new DateTime(encounter.getEncounterDatetime())).withTimeAtStartOfDay().toDate();

        // we will update any COVID program started on or before the encounter date AND EITHER closed on or after encounter date OR not closed at all
        // note that this should handle moving a completion date *earlier* but *not later*:
        // ie if a patient is enrolled on Jan 11 and program is completed on Jan 14 and discharge form is entered on Jan 12, we assume
        // Jan 12 is the correct completion date and update
        // however, if a discharge form is entered on Jan 16, the program from Jan 11 to Jan 14 is *not* updated

        List<PatientProgram> candidates = programWorkflowService.getPatientPrograms(patient, covid, null,
                encounterDate, encounterDate, null, false);

        if (candidates != null) {

            if (candidates.size() > 1) {
                log.warn("More than one COVID program enrollment for patient " + patient.getId() +
                         " on date " + encounterDate + ". Now unenrolling from all of them.");
            }

            for (PatientProgram patientProgram : candidates) {

                // set the date completed to the encounter date
                patientProgram.setDateCompleted(encounterDate);

                // find the disposition
                DispositionDescriptor dispositionDescriptor = dispositionService.getDispositionDescriptor();

                Disposition disposition = null;

                for (Obs candidate : encounter.getObsAtTopLevel(false)) {
                    if (dispositionDescriptor.isDisposition(candidate)) {
                        disposition = dispositionService.getDispositionFromObsGroup(candidate);
                        break;
                    }
                }

                // set program outcome based on dispositoion
                if (disposition != null) {
                    // note, we set our "uuids" for dispositions to keys; this is probably preferrable, but inconsistent
                    // with the name "uuid": https://github.com/PIH/openmrs-config-zl/blob/master/configuration/pih/pih-dispositions-haiti.json#L50
                    if (disposition.getUuid().equals(DISPOSITION_DEATH)) {
                        patientProgram.setOutcome(conceptService.getConceptByMapping(CONCEPT_DIED, "PIH"));
                    }
                    else if (disposition.getUuid().equals(DISPOSITION_TRANSFER_OUT)) {
                        patientProgram.setOutcome(conceptService.getConceptByMapping(CONCEPT_TRANSFER_OUT, "PIH"));
                    }
                    else if (disposition.getUuid().equals(DISPOSITION_DISCHARGE)) {
                        patientProgram.setOutcome(conceptService.getConceptByMapping(CONCEPT_DISCHARGE, "PIH"));
                    }
                    else {
                        log.warn("When closing COVID program no concept mapping rule found for disposition " + disposition.getUuid());
                    }
                }
                else {
                    log.warn("No disposition found on COVID discharge note for patient " + patient.getId());
                }

                programWorkflowService.savePatientProgram(patientProgram);
            }
        }
    }
}
