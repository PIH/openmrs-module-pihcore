package org.openmrs.module.pihcore.setup;

import org.openmrs.module.emrapi.adt.AdtService;
import org.openmrs.module.pihcore.merge.PihPatientMergeActions;
import org.openmrs.module.pihcore.merge.PihRadiologyOrdersMergeActions;
import org.openmrs.module.pihcore.merge.PihTestOrdersMergeActions;
import org.openmrs.module.pihcore.merge.PihVisitMergeActions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MergeActionsSetup {

    @Autowired
    AdtService adtService;

    @Autowired
    PihPatientMergeActions pihPatientMergeActions;

    @Autowired
    PihRadiologyOrdersMergeActions pihRadiologyOrdersMergeActions;

    @Autowired
    PihTestOrdersMergeActions pihTestOrdersMergeActions;

    @Autowired
    PihVisitMergeActions pihVisitMergeActions;

    public void registerMergeActions() {
        adtService.addPatientMergeAction(pihPatientMergeActions);
        adtService.addPatientMergeAction(pihRadiologyOrdersMergeActions);
        adtService.addPatientMergeAction(pihTestOrdersMergeActions);
        adtService.addVisitMergeAction(pihVisitMergeActions);
    }

    public void deregisterMergeActions() {
        adtService.removePatientMergeAction(pihPatientMergeActions);
        adtService.removePatientMergeAction(pihRadiologyOrdersMergeActions);
        adtService.removePatientMergeAction(pihTestOrdersMergeActions);
        adtService.removeVisitMergeAction(pihVisitMergeActions);
    }
}
