package org.openmrs.module.pihcore.metadata.core;

import org.openmrs.EncounterRole;
import org.openmrs.EncounterType;
import org.openmrs.Form;
import org.openmrs.Location;
import org.openmrs.VisitType;
import org.openmrs.module.emrapi.EmrApiConstants;
import org.openmrs.module.metadatadeploy.descriptor.MetadataSourceDescriptor;
import org.openmrs.module.metadatadeploy.descriptor.MetadataTermMappingDescriptor;
import org.openmrs.module.pihcore.deploy.bundle.core.EncounterRoleBundle;
import org.openmrs.module.pihcore.deploy.bundle.core.VisitTypeBundle;

public class MetadataTermMappings {

    public static final class Forms {
        public static final String ADMISSION = "43acf930-eb1b-11e2-91e2-0800200c9a66";  // TODO: Install in bundle
        public static final String TRANSFER_WITHIN_HOSPITAL = "d068bc80-fb95-11e2-b778-0800200c9a66";  // TODO: Install in bundle
        public static final String EXIT_FROM_INPATIENT = "e0a26c20-fba6-11e2-b778-0800200c9a66";  // TODO: Install in bundle
    }

    public static MetadataTermMappingDescriptor unknownLocation = new MetadataTermMappingDescriptor() {
        public MetadataSourceDescriptor metadataSource() { return MetadataSources.emrApiSource; }
        public String code() { return EmrApiConstants.GP_UNKNOWN_LOCATION; }
        public String metadataClass() { return Location.class.getName(); }
        public String metadataUuid() { return Locations.UNKNOWN.uuid(); }
        public String uuid() { return "5b3804ae-2069-432c-83d4-310f4b1a363f"; }
    };

    public static MetadataTermMappingDescriptor atFacilityVisitType = new MetadataTermMappingDescriptor() {
        public MetadataSourceDescriptor metadataSource() { return MetadataSources.emrApiSource; }
        public String code() { return EmrApiConstants.GP_AT_FACILITY_VISIT_TYPE; }
        public String metadataClass() { return VisitType.class.getName(); }
        public String metadataUuid() { return VisitTypeBundle.VisitTypes.CLINIC_OR_HOSPITAL_VISIT; }
        public String uuid() { return "26cc06ea-d97d-400a-a528-7868b86370be"; }
    };

    public static MetadataTermMappingDescriptor clinicianEncounterRole = new MetadataTermMappingDescriptor() {
        public MetadataSourceDescriptor metadataSource() { return MetadataSources.emrApiSource; }
        public String code() { return EmrApiConstants.GP_CLINICIAN_ENCOUNTER_ROLE; }
        public String metadataClass() { return EncounterRole.class.getName(); }
        public String metadataUuid() { return EncounterRoleBundle.EncounterRoles.CONSULTING_CLINICIAN; }
        public String uuid() { return "527fca63-3ad5-4449-9c14-e2cd752c6927"; }
    };

    public static MetadataTermMappingDescriptor orderingProviderEncounterRole = new MetadataTermMappingDescriptor() {
        public MetadataSourceDescriptor metadataSource() { return MetadataSources.emrApiSource; }
        public String code() { return EmrApiConstants.GP_ORDERING_PROVIDER_ENCOUNTER_ROLE; }
        public String metadataClass() { return EncounterRole.class.getName(); }
        public String metadataUuid() { return EncounterRoleBundle.EncounterRoles.ORDERING_PROVIDER; }
        public String uuid() { return "6f4680ff-f4c8-476c-a7bf-e59241f56375"; }
    };

    public static MetadataTermMappingDescriptor checkInClerkEncounterRole = new MetadataTermMappingDescriptor() {
        public MetadataSourceDescriptor metadataSource() { return MetadataSources.emrApiSource; }
        public String code() { return EmrApiConstants.GP_CHECK_IN_CLERK_ENCOUNTER_ROLE; }
        public String metadataClass() { return EncounterRole.class.getName(); }
        public String metadataUuid() { return EncounterRoleBundle.EncounterRoles.ADMINISTRATIVE_CLERK; }
        public String uuid() { return "3fb59c90-4bc9-40c9-a038-f6256828b682"; }
    };

    public static MetadataTermMappingDescriptor visitNoteEncounterType = new MetadataTermMappingDescriptor() {
        public MetadataSourceDescriptor metadataSource() { return MetadataSources.emrApiSource; }
        public String code() { return EmrApiConstants.GP_VISIT_NOTE_ENCOUNTER_TYPE; }
        public String metadataClass() { return EncounterType.class.getName(); }
        public String metadataUuid() { return EncounterTypes.CONSULTATION.uuid(); }
        public String uuid() { return "238b2494-6f1c-47f6-9b8f-294596163b95"; }
    };

    public static MetadataTermMappingDescriptor checkInEncounterType = new MetadataTermMappingDescriptor() {
        public MetadataSourceDescriptor metadataSource() { return MetadataSources.emrApiSource; }
        public String code() { return EmrApiConstants.GP_CHECK_IN_ENCOUNTER_TYPE; }
        public String metadataClass() { return EncounterType.class.getName(); }
        public String metadataUuid() { return EncounterTypes.CHECK_IN.uuid(); }
        public String uuid() { return "4d3e39fd-ccbb-4536-8275-9f30491972c6"; }
    };

    public static MetadataTermMappingDescriptor admissionEncounterType = new MetadataTermMappingDescriptor() {
        public MetadataSourceDescriptor metadataSource() { return MetadataSources.emrApiSource; }
        public String code() { return EmrApiConstants.GP_ADMISSION_ENCOUNTER_TYPE; }
        public String metadataClass() { return EncounterType.class.getName(); }
        public String metadataUuid() { return EncounterTypes.ADMISSION.uuid(); }
        public String uuid() { return "83991a94-c731-44e5-b6cf-33b69f528c36"; }
    };

    public static MetadataTermMappingDescriptor exitFromInpatientEncounterType = new MetadataTermMappingDescriptor() {
        public MetadataSourceDescriptor metadataSource() { return MetadataSources.emrApiSource; }
        public String code() { return EmrApiConstants.GP_EXIT_FROM_INPATIENT_ENCOUNTER_TYPE; }
        public String metadataClass() { return EncounterType.class.getName(); }
        public String metadataUuid() { return EncounterTypes.EXIT_FROM_CARE.uuid(); }
        public String uuid() { return "f65572b2-516b-4b21-9ef7-291463cf6b24"; }
    };

    public static MetadataTermMappingDescriptor transferWithinHospitalEncounterType = new MetadataTermMappingDescriptor() {
        public MetadataSourceDescriptor metadataSource() { return MetadataSources.emrApiSource; }
        public String code() { return EmrApiConstants.GP_TRANSFER_WITHIN_HOSPITAL_ENCOUNTER_TYPE; }
        public String metadataClass() { return EncounterType.class.getName(); }
        public String metadataUuid() { return EncounterTypes.TRANSFER.uuid(); }
        public String uuid() { return "e38534a6-2813-4c8d-bcb0-b3118e449f48"; }
    };

    public static MetadataTermMappingDescriptor admissionForm = new MetadataTermMappingDescriptor() {
        public MetadataSourceDescriptor metadataSource() { return MetadataSources.emrApiSource; }
        public String code() { return EmrApiConstants.GP_ADMISSION_FORM; }
        public String metadataClass() { return Form.class.getName(); }
        public String metadataUuid() { return Forms.ADMISSION; }
        public String uuid() { return "aab81043-9716-44df-920e-1730be0fe124"; }
    };

    public static MetadataTermMappingDescriptor transferWithinHospitalForm = new MetadataTermMappingDescriptor() {
        public MetadataSourceDescriptor metadataSource() { return MetadataSources.emrApiSource; }
        public String code() { return EmrApiConstants.GP_TRANSFER_WITHIN_HOSPITAL_FORM; }
        public String metadataClass() { return Form.class.getName(); }
        public String metadataUuid() { return Forms.TRANSFER_WITHIN_HOSPITAL; }
        public String uuid() { return "9b277615-34c4-4794-833a-3f19ea8b086b"; }
    };

    public static MetadataTermMappingDescriptor exitFromInpatientForm = new MetadataTermMappingDescriptor() {
        public MetadataSourceDescriptor metadataSource() { return MetadataSources.emrApiSource; }
        public String code() { return EmrApiConstants.GP_EXIT_FROM_INPATIENT_FORM; }
        public String metadataClass() { return Form.class.getName(); }
        public String metadataUuid() { return Forms.EXIT_FROM_INPATIENT; }
        public String uuid() { return "88c4201a-a3a7-4965-a102-9d9c479bfe91"; }
    };

}

