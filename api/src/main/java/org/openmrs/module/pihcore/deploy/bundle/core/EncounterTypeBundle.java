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
        install(EncounterTypes.NCD_INITIAL_CONSULT);
        install(EncounterTypes.NCD_FOLLOWUP_CONSULT);
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
        install(EncounterTypes.PATHOLOGY_SPECIMEN_COLLECTION);
        install(EncounterTypes.LAB_SPECIMEN_COLLECTION);
        install(EncounterTypes.HIV_INTAKE);
        install(EncounterTypes.HIV_FOLLOWUP);
        install(EncounterTypes.VCT);
        install(EncounterTypes.HIV_DISPENSING);
        install(EncounterTypes.DRUG_ORDER_DOCUMENTATION);
        install(EncounterTypes.SOCIO_ECONOMICS);
        install(EncounterTypes.ANC_INTAKE);
        install(EncounterTypes.ANC_FOLLOWUP);
        install(EncounterTypes.MCH_DELIVERY);
        install(EncounterTypes.OB_GYN);
        install(EncounterTypes.VACCINATION);
        install(EncounterTypes.ECHOCARDIOGRAM);
        install(EncounterTypes.PRENATAL_HOME_ASSESSMENT);
        install(EncounterTypes.PEDIATRIC_HOME_ASSESSMENT);
        install(EncounterTypes.MATERNAL_POST_PARTUM_HOME_ASSESSMENT);
        install(EncounterTypes.MATERNAL_FOLLOWUP_HOME_ASSESSMENT);
        install(EncounterTypes.COVID19_INTAKE);
        install(EncounterTypes.COVID19_FOLLOWUP);
        install(EncounterTypes.COVID19_DISCHARGE);
        install(EncounterTypes.OVC_INTAKE);
        install(EncounterTypes.OVC_FOLLOWUP);
        install(EncounterTypes.TB_INTAKE);
        install(EncounterTypes.COMMENT);

        uninstall(possible(EncounterType.class, AttachmentsConstants.ENCOUNTER_TYPE_UUID), "not used");
        uninstall(possible(EncounterType.class, EncounterTypes.MEXICO_CLINIC_VISIT.uuid()), "replaced with Mexico Consult");
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
