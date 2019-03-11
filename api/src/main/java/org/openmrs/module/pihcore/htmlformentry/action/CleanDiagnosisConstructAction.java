package org.openmrs.module.pihcore.htmlformentry.action;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Concept;
import org.openmrs.Encounter;
import org.openmrs.Obs;
import org.openmrs.module.emrapi.EmrApiConstants;
import org.openmrs.module.htmlformentry.CustomFormSubmissionAction;
import org.openmrs.module.htmlformentry.FormEntrySession;
import org.openmrs.api.context.Context;

import java.util.Arrays;
import java.util.Collection;
import java.util.Set;

public class CleanDiagnosisConstructAction implements CustomFormSubmissionAction {

    protected final Log log = LogFactory.getLog(getClass());

    @Override
    public void applyAction(FormEntrySession formEntrySession) {

        Concept diagnosisConstructConcept = Context.getConceptService().getConceptByMapping(EmrApiConstants.CONCEPT_CODE_DIAGNOSIS_CONCEPT_SET, EmrApiConstants.EMR_CONCEPT_SOURCE_NAME);
        Concept codedDiagnosisConcept = Context.getConceptService().getConceptByMapping(EmrApiConstants.CONCEPT_CODE_CODED_DIAGNOSIS, EmrApiConstants.EMR_CONCEPT_SOURCE_NAME);
        Concept nonCodedDiagnosisConcept = Context.getConceptService().getConceptByMapping(EmrApiConstants.CONCEPT_CODE_NON_CODED_DIAGNOSIS, EmrApiConstants.EMR_CONCEPT_SOURCE_NAME);
        Concept orderDiagnosisConcept = Context.getConceptService().getConceptByMapping(EmrApiConstants.CONCEPT_CODE_DIAGNOSIS_ORDER, EmrApiConstants.EMR_CONCEPT_SOURCE_NAME);

        Encounter encounter = formEntrySession.getEncounter();
        boolean updateEncounter = false;
        for (Obs obsGroup : encounter.getObsAtTopLevel(false)) {
            if (obsGroup.getConcept().equals(diagnosisConstructConcept)) {
                //we found a diagnosis construct
                Set<Obs> groupMembers = obsGroup.getGroupMembers();
                if (groupMembers !=null && groupMembers.size() > 0 ) {
                    boolean hasDiagnosis = false;
                    boolean hasOrder = false;
                    for (Obs member : groupMembers) {
                        if (member.getConcept().equals(codedDiagnosisConcept) || member.getConcept().equals(nonCodedDiagnosisConcept)) {
                            hasDiagnosis = true;
                        } else if (member.getConcept().equals(orderDiagnosisConcept) ){
                            hasOrder = true;
                        }
                    }
                    if (!hasDiagnosis) {
                        //we do not have a diagnosis but have other dangling construct obs
                        removeGivenObsAndTheirGroupMembersFromEncounter(Arrays.asList(obsGroup), encounter);
                        updateEncounter = true;
                    } else if (!hasOrder) {
                        //add by default SECONDARY order
                        Obs order = new Obs();
                        order.setConcept(Context.getConceptService().getConceptByMapping(EmrApiConstants.CONCEPT_CODE_DIAGNOSIS_ORDER, EmrApiConstants.EMR_CONCEPT_SOURCE_NAME));
                        order.setValueCoded(Context.getConceptService().getConceptByMapping(EmrApiConstants.CONCEPT_CODE_DIAGNOSIS_ORDER_SECONDARY, EmrApiConstants.EMR_CONCEPT_SOURCE_NAME));
                        order.setObsDatetime(encounter.getEncounterDatetime());
                        order.setPerson(encounter.getPatient());
                        obsGroup.addGroupMember(order);
                        Context.getObsService().saveObs(obsGroup, "add diagnosis order");
                    }
                }
            }
        }

        if (updateEncounter) {
            Context.getEncounterService().saveEncounter(encounter);
        }
    }

    private void removeGivenObsAndTheirGroupMembersFromEncounter(Collection<Obs> obsToRemove, Encounter encounter) {
        for (Obs o : obsToRemove) {
            o.setVoided(true);
            o.setVoidReason("dangling diagnosis obs");
            encounter.removeObs(o);
            Set<Obs> groupMembers = o.getGroupMembers(true);
            if (CollectionUtils.isNotEmpty(groupMembers)) {
                removeGivenObsAndTheirGroupMembersFromEncounter(groupMembers, encounter);
            }
        }
    }
}
