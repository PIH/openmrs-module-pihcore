package org.openmrs.module.pihcore.encounter;

import org.openmrs.Encounter;
import org.openmrs.annotation.Handler;
import org.openmrs.module.emrapi.adt.AdtService;
import org.openmrs.module.emrapi.adt.EmrApiVisitAssignmentHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;


/**
 * This Validator does two keys things:
 *
 * 1) It checks to make sure that if there is a visit within the encounter, the encounter_datetime falls within the
 *    visit start and end time.  If not, it removes the visit from the encounter.
 *
 * 2) Then, if there is no visit associated with the encounter (or it has been removed in because of #1) it calls the EMR-API
 *    visit assignment handler (this would automatically happen when creating an encounter, but doing it here assures
 *    that it also happened when editing an encounter)
 *
 *  So, prior to the existence of this validator, the EMR API visit assignment handler would assign any NEW encounters
 *  created to the appropriate visit; this validator expands that functionality so that an encounter can be *reassigned*
 *  to a different visit as needed.
 *
 *  The primary initial use case for this was when encounters are editing outside of a visit context, and
 *  specifically the Specimen Collection encounter created as part of the Lab Results workflow.  However, it
 *  seems beneficial to use this in all cases, and may allow us to relax some of our constraints upon editing the encounter
 *  datetime of other encounter types in the future
 *
 *  Also note that we've added functionality to the visit assignment handler so that when assigning an encounter to the visit
 *  if the handler finds a visit on the same day as the encounter, but the encounter does not fall within it's start/stop date,
 *  it automatically adjusts the encounter_datetime to the date_started of the visit and assigns the encounter to the visit;
 *  this was also added for the case when an encounter like Specimen Collection is not added in the context of
 *  a visit; note that the assumption here is that there is only one visit per location per day; this seems relatively
 *  safe (or, at least in the low number of cases where this isn't true, the error implications are small)
 *
 * (If we discover any problems with this behavior, we could modify this validator to only operate on specific
 * encounter types, like the Specimen Collection encounter type)
 *
 *  Note that we have to do this in a validator instead of a save handler because validators are called before save
 *  handlers and the generic OpenMRS EncounterValidator will throw an error if the visit and the encounter don't matchup
 *  (this Validator is prioritized to run before that one)
 */

//@Handler(supports = { Encounter.class }, order = 10)
public class PihEncounterValidator implements Validator {

    @Autowired
    AdtService adtService;

    EmrApiVisitAssignmentHandler visitAssignmentHandler;

    @Override
    public boolean supports(Class<?> aClass) {
        return Encounter.class.isAssignableFrom(aClass);
    }

    @Override
    public void validate(Object obj, Errors errors) {

        if (obj == null || !(obj instanceof Encounter)) {
            throw new IllegalArgumentException("The parameter obj should not be null and must be of type " + Encounter.class);
        }

        Encounter encounter = (Encounter) obj;

        // if there's a currently associated visit and it isn't "suitable", remove it from the encounter and call the EmrApi Assignment Handler to re-assign a viist
       if (encounter.getVisit() != null) {
           // perhaps a hack, but if visit = null don't run the check (ie don't remove a visit just because it doesn't have a location)
           if (encounter.getVisit().getLocation() != null) {
               if (!adtService.isSuitableVisit(encounter.getVisit(), encounter.getLocation(), encounter.getEncounterDatetime())) {
                   encounter.getVisit().getEncounters().remove(encounter);
                   encounter.setVisit(null);
               }
           }
       }

       // use the visit assignment handler to assign a visit to an encounter (we do this here instead of just relying on the wired in EmrApiVisitAssignmentHandler so that it works in the edit case)
        if (encounter.getVisit() == null) {
            // a bit hacky to instantiate like this
            if (visitAssignmentHandler == null) {
                visitAssignmentHandler = new EmrApiVisitAssignmentHandler();
            }

            visitAssignmentHandler.beforeCreateEncounter(encounter);
        }

    }

    public void setAdtService(AdtService adtService) {
        this.adtService = adtService;
    }

    public void setVisitAssignmentHandler(EmrApiVisitAssignmentHandler visitAssignmentHandler) {
        this.visitAssignmentHandler = visitAssignmentHandler;
    }
}
