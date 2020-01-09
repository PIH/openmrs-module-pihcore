package org.openmrs.module.pihcore.setup;

import org.openmrs.Form;
import org.openmrs.api.EncounterService;
import org.openmrs.api.LocationService;
import org.openmrs.api.PatientService;
import org.openmrs.api.VisitService;
import org.openmrs.module.emrapi.EmrApiConstants;
import org.openmrs.module.metadatamapping.api.MetadataMappingService;
import org.openmrs.module.pihcore.config.Config;
import org.openmrs.module.pihcore.config.ConfigDescriptor;
import org.openmrs.module.pihcore.deploy.bundle.core.EncounterRoleBundle;
import org.openmrs.module.pihcore.deploy.bundle.core.VisitTypeBundle;
import org.openmrs.module.pihcore.metadata.core.EncounterTypes;
import org.openmrs.module.pihcore.metadata.core.Locations;
import org.openmrs.module.pihcore.metadata.haiti.PihHaitiPatientIdentifierTypes;
import org.openmrs.module.pihcore.metadata.liberia.LiberiaPatientIdentifierTypes;
import org.openmrs.module.pihcore.metadata.mexico.MexicoPatientIdentifierTypes;
import org.openmrs.module.pihcore.metadata.peru.PeruPatientIdentifierTypes;
import org.openmrs.module.pihcore.metadata.sierraLeone.SierraLeonePatientIdentifierTypes;

public class MetadataMappingsSetup {

    public static final class Forms {
        public static final String ADMISSION = "43acf930-eb1b-11e2-91e2-0800200c9a66";  // TODO: Install in bundle
        public static final String TRANSFER_WITHIN_HOSPITAL = "d068bc80-fb95-11e2-b778-0800200c9a66";  // TODO: Install in bundle
        public static final String EXIT_FROM_INPATIENT = "e0a26c20-fba6-11e2-b778-0800200c9a66";  // TODO: Install in bundle
    }

    public static void setupGlobalMetadataMappings(MetadataMappingService metadataMappingService,
                                                   LocationService locationService,
                                                   EncounterService encounterService,
                                                   VisitService visitService) {

        metadataMappingService.mapMetadataItem(locationService.getLocationByUuid(Locations.UNKNOWN.uuid()), EmrApiConstants.EMR_METADATA_SOURCE_NAME, EmrApiConstants.GP_UNKNOWN_LOCATION);

        metadataMappingService.mapMetadataItem(encounterService.getEncounterTypeByUuid(EncounterTypes.CONSULTATION.uuid()), EmrApiConstants.EMR_METADATA_SOURCE_NAME, EmrApiConstants.GP_VISIT_NOTE_ENCOUNTER_TYPE);
        metadataMappingService.mapMetadataItem(encounterService.getEncounterTypeByUuid(EncounterTypes.CHECK_IN.uuid()), EmrApiConstants.EMR_METADATA_SOURCE_NAME, EmrApiConstants.GP_CHECK_IN_ENCOUNTER_TYPE);
        metadataMappingService.mapMetadataItem(encounterService.getEncounterTypeByUuid(EncounterTypes.ADMISSION.uuid()), EmrApiConstants.EMR_METADATA_SOURCE_NAME, EmrApiConstants.GP_ADMISSION_ENCOUNTER_TYPE);
        metadataMappingService.mapMetadataItem(encounterService.getEncounterTypeByUuid(EncounterTypes.TRANSFER.uuid()), EmrApiConstants.EMR_METADATA_SOURCE_NAME, EmrApiConstants.GP_TRANSFER_WITHIN_HOSPITAL_ENCOUNTER_TYPE);
        metadataMappingService.mapMetadataItem(encounterService.getEncounterTypeByUuid(EncounterTypes.EXIT_FROM_CARE.uuid()), EmrApiConstants.EMR_METADATA_SOURCE_NAME, EmrApiConstants.GP_EXIT_FROM_INPATIENT_ENCOUNTER_TYPE);

        metadataMappingService.mapMetadataItem(encounterService.getEncounterRoleByUuid(EncounterRoleBundle.EncounterRoles.CONSULTING_CLINICIAN), EmrApiConstants.EMR_METADATA_SOURCE_NAME, EmrApiConstants.GP_CLINICIAN_ENCOUNTER_ROLE);
        metadataMappingService.mapMetadataItem(encounterService.getEncounterRoleByUuid(EncounterRoleBundle.EncounterRoles.ORDERING_PROVIDER), EmrApiConstants.EMR_METADATA_SOURCE_NAME, EmrApiConstants.GP_ORDERING_PROVIDER_ENCOUNTER_ROLE);
        metadataMappingService.mapMetadataItem(encounterService.getEncounterRoleByUuid(EncounterRoleBundle.EncounterRoles.ADMINISTRATIVE_CLERK), EmrApiConstants.EMR_METADATA_SOURCE_NAME, EmrApiConstants.GP_CHECK_IN_CLERK_ENCOUNTER_ROLE);

        metadataMappingService.mapMetadataItem(visitService.getVisitTypeByUuid(VisitTypeBundle.VisitTypes.CLINIC_OR_HOSPITAL_VISIT), EmrApiConstants.EMR_METADATA_SOURCE_NAME, EmrApiConstants.GP_AT_FACILITY_VISIT_TYPE);

    }

    public static void setupPrimaryIdentifierTypeBasedOnCountry(MetadataMappingService metadataMappingService, PatientService patientService, Config config) {
        if (config.getCountry().equals(ConfigDescriptor.Country.HAITI)) {
            metadataMappingService.mapMetadataItem(patientService.getPatientIdentifierTypeByUuid(PihHaitiPatientIdentifierTypes.ZL_EMR_ID.uuid()), EmrApiConstants.EMR_CONCEPT_SOURCE_NAME, EmrApiConstants.PRIMARY_IDENTIFIER_TYPE);
        }
        else if (config.getCountry().equals(ConfigDescriptor.Country.LIBERIA)) {
            metadataMappingService.mapMetadataItem(patientService.getPatientIdentifierTypeByUuid(LiberiaPatientIdentifierTypes.LIBERIA_EMR_ID.uuid()), EmrApiConstants.EMR_CONCEPT_SOURCE_NAME, EmrApiConstants.PRIMARY_IDENTIFIER_TYPE);
        }
        else if (config.getCountry().equals(ConfigDescriptor.Country.SIERRA_LEONE)) {
            metadataMappingService.mapMetadataItem(patientService.getPatientIdentifierTypeByUuid(SierraLeonePatientIdentifierTypes.WELLBODY_EMR_ID.uuid()), EmrApiConstants.EMR_CONCEPT_SOURCE_NAME, EmrApiConstants.PRIMARY_IDENTIFIER_TYPE);
        }
        else if (config.getCountry().equals(ConfigDescriptor.Country.MEXICO)) {
            metadataMappingService.mapMetadataItem(patientService.getPatientIdentifierTypeByUuid(MexicoPatientIdentifierTypes.CHIAPAS_EMR_ID.uuid()), EmrApiConstants.EMR_CONCEPT_SOURCE_NAME, EmrApiConstants.PRIMARY_IDENTIFIER_TYPE);
        }
        else if (config.getCountry().equals(ConfigDescriptor.Country.PERU)) {
            metadataMappingService.mapMetadataItem(patientService.getPatientIdentifierTypeByUuid(PeruPatientIdentifierTypes.PERU_EMR_ID.uuid()), EmrApiConstants.EMR_CONCEPT_SOURCE_NAME, EmrApiConstants.PRIMARY_IDENTIFIER_TYPE);
        }
    }

    public static void setupFormMetadataMappings(MetadataMappingService metadataMappingService) {
        metadataMappingService.mapMetadataItem(Forms.ADMISSION, Form.class.getCanonicalName(), EmrApiConstants.EMR_METADATA_SOURCE_NAME, EmrApiConstants.GP_ADMISSION_FORM);
        metadataMappingService.mapMetadataItem(Forms.TRANSFER_WITHIN_HOSPITAL, Form.class.getCanonicalName(), EmrApiConstants.EMR_METADATA_SOURCE_NAME, EmrApiConstants.GP_TRANSFER_WITHIN_HOSPITAL_FORM);
        metadataMappingService.mapMetadataItem(Forms.EXIT_FROM_INPATIENT, Form.class.getCanonicalName(), EmrApiConstants.EMR_METADATA_SOURCE_NAME, EmrApiConstants.GP_EXIT_FROM_INPATIENT_FORM);
    }

}


