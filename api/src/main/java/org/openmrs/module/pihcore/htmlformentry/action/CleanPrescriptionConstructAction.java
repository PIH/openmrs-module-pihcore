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

public class CleanPrescriptionConstructAction implements CustomFormSubmissionAction {

    public static final String PRESCRIPTION_CONSTRUCT_CONCEPT = "Prescription construct";
    public static final String MEDICATION_ORDERS_CONCEPT = "MEDICATION ORDERS";


    protected final Log log = LogFactory.getLog(getClass());

    @Override
    public void applyAction(FormEntrySession formEntrySession) {

        Concept prescriptionConstructConcept = Context.getConceptService().getConceptByMapping(CleanPrescriptionConstructAction.PRESCRIPTION_CONSTRUCT_CONCEPT, "PIH");
        Concept medicationOrdersConcept = Context.getConceptService().getConceptByMapping(CleanPrescriptionConstructAction.MEDICATION_ORDERS_CONCEPT, "PIH");

        Encounter encounter = formEntrySession.getEncounter();
        boolean updateEncounter = false;
        for (Obs obsGroup : encounter.getObsAtTopLevel(false)) {
            if (obsGroup.getConcept().equals(prescriptionConstructConcept)) {
                //we found a prescription medication construct
                Set<Obs> groupMembers = obsGroup.getGroupMembers();
                if (groupMembers !=null && groupMembers.size() > 0 ) {
                    boolean hasMedicationOrders = false;
                    for (Obs member : groupMembers) {
                        if (member.getConcept().equals(medicationOrdersConcept) ) {
                            hasMedicationOrders = true;
                        }
                    }
                    if (!hasMedicationOrders) {
                        //we do not have a medication order but have other dangling construct obs
                        removeGivenObsAndTheirGroupMembersFromEncounter(Arrays.asList(obsGroup), encounter);
                        updateEncounter = true;
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
