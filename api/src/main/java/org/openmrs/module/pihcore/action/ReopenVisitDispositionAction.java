package org.openmrs.module.pihcore.action;

import org.openmrs.Obs;
import org.openmrs.Visit;
import org.openmrs.module.emrapi.disposition.actions.DispositionAction;
import org.openmrs.module.emrapi.encounter.EncounterDomainWrapper;
import org.openmrs.module.pihcore.PihCoreUtil;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component("reopenVisitDispositionAction")
public class ReopenVisitDispositionAction implements DispositionAction {

    @Override
    public void action(EncounterDomainWrapper encounterDomainWrapper, Obs obs, Map<String, String[]> map) {
        Visit visit = encounterDomainWrapper.getVisit();
        if (visit != null) {
            PihCoreUtil.reopenVisit(visit);
        }
    }
}
