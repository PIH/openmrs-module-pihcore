package org.openmrs.module.pihcore.deploy.bundle.core;

import org.openmrs.EncounterType;
import org.openmrs.module.attachments.AttachmentsConstants;
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
        // TODO can primary care visit be retired?
        install(EncounterTypes.PRIMARY_CARE_VISIT);
        install(EncounterTypes.CONSULTATION);
        install(EncounterTypes.ONCOLOGY_CONSULT);
        install(EncounterTypes.ONCOLOGY_INITIAL_VISIT);
        install(EncounterTypes.CHEMOTHERAPY_SESSION);
        install(EncounterTypes.NCD_ADULT_INITIAL_CONSULT);
        install(EncounterTypes.NCD_ADULT_FOLLOWUP_CONSULT);
        install(EncounterTypes.MEDICATION_DISPENSED);
        install(EncounterTypes.POST_OPERATIVE_NOTE);
        install(EncounterTypes.TRANSFER);
        install(EncounterTypes.ADMISSION);
        install(EncounterTypes.CANCEL_ADMISSION);
        install(EncounterTypes.EXIT_FROM_CARE);
        install(EncounterTypes.LAB_RESULTS);
        install(EncounterTypes.DEATH_CERTIFICATE);
        install(EncounterTypes.MENTAL_HEALTH_ASSESSMENT);
        install(EncounterTypes.PRIMARY_CARE_PEDS_INITIAL_CONSULT);
        install(EncounterTypes.PRIMARY_CARE_PEDS_FOLLOWUP_CONSULT);
        install(EncounterTypes.PRIMARY_CARE_ADULT_INITIAL_CONSULT);
        install(EncounterTypes.PRIMARY_CARE_ADULT_FOLLOWUP_CONSULT);
        install(EncounterTypes.EMERGENCY_TRIAGE);
        install(EncounterTypes.TEST_ORDER);
        install(EncounterTypes.SPECIMEN_COLLECTION);
        install(EncounterTypes.ZL_HIV_INTAKE);
        install(EncounterTypes.ZL_HIV_FOLLOWUP);
        install(EncounterTypes.VCT);
        install(EncounterTypes.SOCIO_ECONOMICS);
        install(EncounterTypes.ANC_INTAKE);
        install(EncounterTypes.ANC_FOLLOWUP);
        install(EncounterTypes.MCH_DELIVERY);
        install(EncounterTypes.MEXICO_CLINIC_VISIT);

        uninstall(possible(EncounterType.class, AttachmentsConstants.ENCOUNTER_TYPE_UUID), "not used");
        uninstall(possible(EncounterType.class, EncounterTypes.PRIMARY_CARE_DISPOSITION.uuid()), "never used");
        uninstall(possible(EncounterType.class, EncounterTypes.PRIMARY_CARE_PEDS_FEEDING.uuid()), "never used");
        uninstall(possible(EncounterType.class, EncounterTypes.PRIMARY_CARE_PEDS_SUPPLEMENTS.uuid()), "never used");
        uninstall(possible(EncounterType.class, EncounterTypes.PRIMARY_CARE_HISTORY.uuid()), "never used");
        uninstall(possible(EncounterType.class, EncounterTypes.PRIMARY_CARE_EXAM.uuid()), "never used");
        uninstall(possible(EncounterType.class, EncounterTypes.PRIMARY_CARE_DIAGNOSIS.uuid()), "never used");
        uninstall(possible(EncounterType.class, EncounterTypes.CONSULTATION_PLAN.uuid()), "never used");
        uninstall(possible(EncounterType.class, EncounterTypes.ADULT_HIV_INTAKE.uuid()), "never used");
        uninstall(possible(EncounterType.class, EncounterTypes.ADULT_HIV_FOLLOWUP.uuid()), "never used");
        uninstall(possible(EncounterType.class, EncounterTypes.PEDS_HIV_INTAKE.uuid()), "never used");
        uninstall(possible(EncounterType.class, EncounterTypes.PEDS_HIV_FOLLOWUP.uuid()), "never used");
        uninstall(possible(EncounterType.class, EncounterTypes.ZL_PEDS_HIV_INTAKE.uuid()), "never used");
        uninstall(possible(EncounterType.class, EncounterTypes.ZL_PEDS_HIV_FOLLOWUP.uuid()), "never used");
        uninstall(possible(EncounterType.class, EncounterTypes.ART_ADHERENCE.uuid()), "never used");
    }
}
