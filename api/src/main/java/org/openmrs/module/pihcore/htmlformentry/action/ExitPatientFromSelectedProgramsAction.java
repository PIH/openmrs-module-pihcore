package org.openmrs.module.pihcore.htmlformentry.action;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.joda.time.DateTime;
import org.openmrs.Concept;
import org.openmrs.Encounter;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.PatientProgram;
import org.openmrs.Program;
import org.openmrs.api.ProgramWorkflowService;
import org.openmrs.api.context.Context;
import org.openmrs.module.htmlformentry.CustomFormSubmissionAction;
import org.openmrs.module.htmlformentry.FormEntrySession;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class ExitPatientFromSelectedProgramsAction implements CustomFormSubmissionAction {

    protected Log log = LogFactory.getLog(getClass());

    @Override
    public void applyAction(FormEntrySession formEntrySession) {
        Encounter encounter = formEntrySession.getEncounter();
        for (Obs candidate : encounter.getObsAtTopLevel(false)) {
            if (candidate.isObsGrouping()
                    && conceptHasMapping(candidate.getConcept(), "PIH", "Exit from program construct")
                    && !candidate.getGroupMembers(false).isEmpty()) {
                Obs programObs = getGroupMemberByConceptMapping(candidate, "PIH", "Service or program");
                Obs outcomeObs = getGroupMemberByConceptMapping(candidate, "PIH", "Program outcome");
                if (programObs == null) {
                    log.warn("'PIH:Exit from program construct' is present as an obsgroup in the form and was " +
                            "submitted with members, but no member obs with concept 'PIH:Service or program' was " +
                            "present. This is needed to specify which program the patient should be un-enrolled from," +
                            "along with 'PIH:Program outcome'. The patient will not be un-enrolled from any program.");
                    continue;
                }
                if (outcomeObs == null) {
                    log.warn("'PIH:Exit from program construct' is present as an obsgroup in the form and was " +
                            "submitted with members, but no member obs with concept 'PIH:Program outcome' was " +
                            "present. They will be un-enrolled with no outcome set.");
                }
                closeProgram(
                        formEntrySession,
                        Context.getProgramWorkflowService(),
                        programObs.getValueCoded(),
                        outcomeObs == null ? null : outcomeObs.getValueCoded());
            }
        }
    }

    private void closeProgram(FormEntrySession formEntrySession,
                              ProgramWorkflowService programWorkflowService,
                              Concept selectedProgram,
                              Concept outcome) {
        Patient patient = formEntrySession.getPatient();
        List<Program> programsWithThisConcept = programWorkflowService.getProgramsByConcept(selectedProgram);
        Encounter encounter = formEntrySession.getEncounter();
        // use the encounter date *without* the time component
        Date encounterDate = (new DateTime(encounter.getEncounterDatetime())).withTimeAtStartOfDay().toDate();

        // we will update a program started on or before the encounter date AND EITHER closed on or after encounter date OR not closed at all
        // note that this should handle moving a completion date *earlier* but *not later*:
        // ie if a patient is enrolled on Jan 11 and program is completed on Jan 14 and discharge form is entered on Jan 12, we assume
        // Jan 12 is the correct completion date and update
        // however, if a discharge form is entered on Jan 16, the program from Jan 11 to Jan 14 is *not* updated

        List<PatientProgram> candidates = programsWithThisConcept
                .stream()
                .map(p ->
                    programWorkflowService.getPatientPrograms(patient, p, null,
                        encounterDate, encounterDate, null, false))
                .flatMap(Collection::stream)
                .collect(Collectors.toList());


        if (!candidates.isEmpty()) {

            if (candidates.size() > 1) {
                log.warn("More than one " + selectedProgram.getFullySpecifiedName(Context.getLocale()) +
                         " enrollment for patient " + patient.getId() +
                         " on date " + encounterDate + ". Now un-enrolling from all of them.");
            }

            for (PatientProgram patientProgram : candidates) {
                patientProgram.setDateCompleted(encounterDate);
                if (outcome != null) {
                    patientProgram.setOutcome(outcome);
                }
                programWorkflowService.savePatientProgram(patientProgram);
            }
        }
    }

    private boolean conceptHasMapping(Concept concept, String source, String code) {
        return concept
                .getConceptMappings()
                .stream()
                .anyMatch(m ->
                        m.getConceptReferenceTerm().getConceptSource().getName().equals(source) &&
                                m.getConceptReferenceTerm().getCode().equals(code));
    }

    private Obs getGroupMemberByConceptMapping(Obs obsgroup, String source, String code) {
        return obsgroup
                .getGroupMembers(false)
                .stream()
                .filter(m -> conceptHasMapping(m.getConcept(), source, code))
                .findAny()
                .orElse(null);
    }
}
