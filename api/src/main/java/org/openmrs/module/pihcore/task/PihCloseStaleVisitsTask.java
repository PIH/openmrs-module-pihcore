package org.openmrs.module.pihcore.task;

import org.apache.commons.lang.time.DateUtils;
import org.openmrs.Encounter;
import org.openmrs.Location;
import org.openmrs.LocationTag;
import org.openmrs.Visit;
import org.openmrs.api.LocationService;
import org.openmrs.api.VisitService;
import org.openmrs.api.context.Context;
import org.openmrs.module.emrapi.EmrApiConstants;
import org.openmrs.module.emrapi.adt.AdtService;
import org.openmrs.module.pihcore.metadata.core.EncounterTypes;
import org.openmrs.module.pihcore.metadata.haiti.mirebalais.MirebalaisLocations;
import org.openmrs.scheduler.tasks.AbstractTask;
import org.openmrs.util.OpenmrsUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Our own custom implementation to close stale visits--this does logic similar to those found in AdtService
 * closeInactiveVisits(), but contains our special logic for the ED use case
 */
public class PihCloseStaleVisitsTask extends AbstractTask {

    private final Logger log = LoggerFactory.getLogger(getClass());

    private static int ED_VISIT_EXPIRE_TIME_IN_HOURS = 48;

    private static final List<String> ED_LOCATION_UUIDS = Arrays.asList(MirebalaisLocations.EMERGENCY.uuid(),
            MirebalaisLocations.EMERGENCY_DEPARTMENT_RECEPTION.uuid(),
            MirebalaisLocations.WOMENS_TRIAGE.uuid());

    private static final String ENCOUNTER_TYPE_CHECK_IN_UUID = EncounterTypes.CHECK_IN.uuid();

    private static final String ENCOUNTER_TYPE_ED_TRIAGE_UUID = EncounterTypes.EMERGENCY_TRIAGE.uuid();

    public void execute() {

        AdtService adtService = Context.getService(AdtService.class);
        VisitService visitService = Context.getVisitService();
        LocationService locationService = Context.getLocationService();

        LocationTag visitLocationTag =  locationService.getLocationTagByName(EmrApiConstants.LOCATION_TAG_SUPPORTS_VISITS);
        List<Location> locations = locationService.getLocationsByTag(visitLocationTag);


        List<Visit> openVisits = visitService.getVisits(null, null, locations, null, null, null, null, null, null, false, false);
        for (Visit visit : openVisits) {
            if (adtService.shouldBeClosed(visit) && !isActiveEDVisit(visit)) {
                try {
                    adtService.closeAndSaveVisit(visit);
                } catch (Exception ex) {
                    log.warn("Failed to close inactive visit " + visit, ex);
                }
            }
        }
    }

    private boolean isActiveEDVisit(Visit visit) {

        if (hasCheckInAtEDLocation(visit) || hasEDTriageEncounter(visit)) {
            Date now = new Date();
            Date mustHaveSomethingAfter = DateUtils.addHours(now, -ED_VISIT_EXPIRE_TIME_IN_HOURS);
            // we don't test the visit start time because we know there must be at least one encounter (the check-in encounter)
            if (visit.getEncounters() != null) {
                for (Encounter candidate : visit.getEncounters()) {
                    if (OpenmrsUtil.compare(candidate.getEncounterDatetime(), mustHaveSomethingAfter) >= 0) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    private boolean hasCheckInAtEDLocation(Visit visit) {
        for (Encounter encounter : visit.getEncounters()) {
            if (!encounter.isVoided() && encounter.getEncounterType().getUuid().equals(ENCOUNTER_TYPE_CHECK_IN_UUID)
                    && (encounter.getLocation() != null && ED_LOCATION_UUIDS.contains(encounter.getLocation().getUuid()))) {
                return true;
            }
        }
        return false;
    }

    private boolean hasEDTriageEncounter(Visit visit) {
        for (Encounter encounter : visit.getEncounters()) {
            if (!encounter.isVoided() && encounter.getEncounterType().getUuid().equals(ENCOUNTER_TYPE_ED_TRIAGE_UUID)) {
                return true;
            }
        }
        return false;
    }

}
