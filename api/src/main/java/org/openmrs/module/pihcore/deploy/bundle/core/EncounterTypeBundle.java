package org.openmrs.module.pihcore.deploy.bundle.core;

import org.openmrs.module.pihcore.deploy.bundle.PihMetadataBundle;
import org.openmrs.module.pihcore.descriptor.EncounterTypeDescriptor;
import org.openmrs.module.pihcore.metadata.core.EncounterTypes;
import org.springframework.stereotype.Component;

import static org.openmrs.module.metadatadeploy.bundle.CoreConstructors.encounterType;

@Component
public class EncounterTypeBundle extends PihMetadataBundle {

    @Override
    public void install() throws Exception {
        install(EncounterTypes.PATIENT_REGISTRATION);
        install(EncounterTypes.CHECK_IN);
        install(EncounterTypes.PAYMENT);
        install(EncounterTypes.VITALS);
        install(EncounterTypes.PRIMARY_CARE_VISIT);
        install(EncounterTypes.CONSULTATION);
        install(EncounterTypes.MEDICATION_DISPENSED);
        install(EncounterTypes.POST_OPERATIVE_NOTE);
        install(EncounterTypes.TRANSFER);
        install(EncounterTypes.ADMISSION);
        install(EncounterTypes.CANCEL_ADMISSION);
        install(EncounterTypes.EXIT_FROM_CARE);
        install(EncounterTypes.PRIMARY_CARE_HISTORY);
        install(EncounterTypes.PRIMARY_CARE_EXAM);
        install(EncounterTypes.CONSULTATION_PLAN);
        install(EncounterTypes.DEATH_CERTIFICATE);
    }

    protected void install(EncounterTypeDescriptor d) {
        install(encounterType(d.name(), d.description(), d.uuid()));
    }
}
