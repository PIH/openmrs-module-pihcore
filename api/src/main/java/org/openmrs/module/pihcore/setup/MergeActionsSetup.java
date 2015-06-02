package org.openmrs.module.pihcore.setup;

import org.openmrs.api.context.Context;
import org.openmrs.module.emrapi.adt.AdtService;
import org.openmrs.module.pihcore.merge.PihPatientMergeActions;

public class MergeActionsSetup {

    public static void registerMergeActions() {
        Context.getService(AdtService.class)
                .addPatientMergeAction(Context.getRegisteredComponent("pihPatientMergeActions", PihPatientMergeActions.class));
    }

    public static void deregisterMergeActions() {
        Context.getService(AdtService.class)
                .removePatientMergeAction(Context.getRegisteredComponent("pihPatientMergeActions", PihPatientMergeActions.class));
    }
}
