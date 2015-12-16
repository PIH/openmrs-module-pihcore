package org.openmrs.module.pihcore.deploy.bundle.core;

import org.openmrs.module.metadatadeploy.bundle.AbstractMetadataBundle;
import org.openmrs.module.pihcore.metadata.core.EncounterTypes;
import org.springframework.stereotype.Component;

@Component
public class EncounterTypeBundle extends AbstractMetadataBundle {

    @Override
    public void install() throws Exception {
        install(EncounterTypes.PATIENT_REGISTRATION);
        install(EncounterTypes.CHECK_IN);
        install(EncounterTypes.PAYMENT);
        install(EncounterTypes.VITALS);
        install(EncounterTypes.PRIMARY_CARE_VISIT);
        install(EncounterTypes.CONSULTATION);
        install(EncounterTypes.ONCOLOGY_CONSULT);
        install(EncounterTypes.ONCOLOGY_INITIAL_VISIT);
        install(EncounterTypes.CHEMOTHERAPY_SESSION);
        install(EncounterTypes.NCD_CONSULT);
        install(EncounterTypes.MEDICATION_DISPENSED);
        install(EncounterTypes.POST_OPERATIVE_NOTE);
        install(EncounterTypes.TRANSFER);
        install(EncounterTypes.ADMISSION);
        install(EncounterTypes.CANCEL_ADMISSION);
        install(EncounterTypes.EXIT_FROM_CARE);
        install(EncounterTypes.PRIMARY_CARE_HISTORY);
        install(EncounterTypes.PRIMARY_CARE_EXAM);
        install(EncounterTypes.PRIMARY_CARE_DIAGNOSIS);
        install(EncounterTypes.LAB_RESULTS);
        install(EncounterTypes.CONSULTATION_PLAN);
        install(EncounterTypes.DEATH_CERTIFICATE);
        install(EncounterTypes.PRIMARY_CARE_DISPOSITION);
        install(EncounterTypes.PRIMARY_CARE_PEDS_FEEDING);
        install(EncounterTypes.PRIMARY_CARE_PEDS_SUPPLEMENTS);
    }

}
