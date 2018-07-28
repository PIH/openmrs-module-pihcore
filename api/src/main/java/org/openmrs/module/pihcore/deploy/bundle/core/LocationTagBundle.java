package org.openmrs.module.pihcore.deploy.bundle.core;

import org.openmrs.LocationTag;
import org.openmrs.module.metadatadeploy.bundle.AbstractMetadataBundle;
import org.openmrs.module.pihcore.metadata.core.LocationTags;
import org.springframework.stereotype.Component;

@Component
public class LocationTagBundle extends AbstractMetadataBundle {

    @Override
    public void install() throws Exception {

        install(LocationTags.LOGIN_LOCATION);
        install(LocationTags.MEDICAL_RECORD_LOCATION);
        install(LocationTags.ARCHIVES_LOCATION);
        install(LocationTags.IDENTIFIER_ASSIGNMENT_LOCATION);
        install(LocationTags.VISIT_LOCATION);
        install(LocationTags.ADMISSION_LOCATION);
        install(LocationTags.TRANSFER_LOCAITON);
        install(LocationTags.CONSULT_NOTE_LOCATION);
        install(LocationTags.SURGERY_NOTE_LOCATION);
        install(LocationTags.ED_NOTE_LOCATION);
        install(LocationTags.ADMISSION_NOTE_LOCATION);
        install(LocationTags.DISPENSING_LOCATION);
        install(LocationTags.APPOINTMENT_LOCATION);
        install(LocationTags.VITALS_LOCATION);
        install(LocationTags.INPATIENTS_APP_LOCATION);
        install(LocationTags.CHECKIN_LOCATION);
        install(LocationTags.REGISTRATION_LOCATION);
        install(LocationTags.ED_REGISTRATION_LOCATION);
        install(LocationTags.ORDER_RADIOLOGY_STUDY_LOCATION);
        install(LocationTags.ONCOLOGY_CONSULT_LOCATION);
        install(LocationTags.CHEMOTHERAPY_LOCATION);
        install(LocationTags.NCD_CONSULT_LOCATION);
        install(LocationTags.HIV_CONSULT_LOCATION);
        install(LocationTags.LAB_RESULTS_LOCATION);
        install(LocationTags.ED_TRIAGE_LOCATION);
        install(LocationTags.ORDER_PATHOLOGY_LOCATION);

        log.info("Retiring old LocationTags");

        uninstall(possible(LocationTag.class, LocationTags.RETIRED_OUTPATIENT_TRANSFER_LOCATION.uuid()), "never used");
        uninstall(possible(LocationTag.class, LocationTags.RETIRED_INPATIENT_TRANSFER_LOCATION.uuid()), "never used");

    }

}
