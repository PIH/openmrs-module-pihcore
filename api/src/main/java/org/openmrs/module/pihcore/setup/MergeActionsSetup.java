package org.openmrs.module.pihcore.setup;

import org.openmrs.api.context.Context;
import org.openmrs.module.emrapi.adt.AdtService;
import org.openmrs.module.pihcore.merge.PihPatientMergeActions;
import org.openmrs.module.pihcore.merge.PihRadiologyOrdersMergeActions;
import org.openmrs.module.pihcore.merge.PihTestOrdersMergeActions;

public class MergeActionsSetup {

    public static void registerMergeActions() {
        Context.getService(AdtService.class)
                .addPatientMergeAction(Context.getRegisteredComponent("pihPatientMergeActions", PihPatientMergeActions.class));
        Context.getService(AdtService.class)
                .addPatientMergeAction(Context.getRegisteredComponent("pihRadiologyOrdersMergeActions", PihRadiologyOrdersMergeActions.class));
        Context.getService(AdtService.class)
                .addPatientMergeAction(Context.getRegisteredComponent("pihTestOrdersMergeActions", PihTestOrdersMergeActions.class));
    }

    public static void deregisterMergeActions() {
        Context.getService(AdtService.class)
                .removePatientMergeAction(Context.getRegisteredComponent("pihPatientMergeActions", PihPatientMergeActions.class));
        Context.getService(AdtService.class)
                .removePatientMergeAction(Context.getRegisteredComponent("pihRadiologyOrdersMergeActions", PihRadiologyOrdersMergeActions.class));
        Context.getService(AdtService.class)
                .removePatientMergeAction(Context.getRegisteredComponent("pihTestOrdersMergeActions", PihTestOrdersMergeActions.class));

    }
}
