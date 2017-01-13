package org.openmrs.module.pihcore.metadata.haiti;

import org.openmrs.PatientIdentifierType;
import org.openmrs.module.emrapi.EmrApiConstants;
import org.openmrs.module.metadatadeploy.descriptor.MetadataSetDescriptor;
import org.openmrs.module.metadatadeploy.descriptor.MetadataSetMemberDescriptor;
import org.openmrs.module.metadatadeploy.descriptor.MetadataSourceDescriptor;
import org.openmrs.module.metadatadeploy.descriptor.MetadataTermMappingDescriptor;
import org.openmrs.module.metadatamapping.MetadataSet;
import org.openmrs.module.pihcore.metadata.core.MetadataSources;

;

public class HaitiMetadataMappings {

    public static MetadataTermMappingDescriptor PRIMARY_IDENTIFIER_TYPE = new MetadataTermMappingDescriptor() {
        public MetadataSourceDescriptor metadataSource() { return MetadataSources.emrApiSource; }
        public String code() { return EmrApiConstants.PRIMARY_IDENTIFIER_TYPE; }
        public String metadataClass() { return PatientIdentifierType.class.getName(); }
        public String metadataUuid() { return HaitiPatientIdentifierTypes.ZL_EMR_ID.uuid(); }
        public String uuid() { return "ddbb2b35-f5be-4d18-a630-c58d1606b562"; }
    };

    public static MetadataSetDescriptor EXTRA_IDENTIFIER_TYPES_SET = new MetadataSetDescriptor() {
        public String uuid() { return "55bcbaca-5144-49ee-9aa2-c7e525c2ffcb"; }
    };

    public static MetadataSetDescriptor MENTAL_HEALTH_EXTRA_IDENTIFIER_TYPES_SET = new MetadataSetDescriptor() {
        public String uuid() { return "ce7ebfb5-178b-4d5a-8481-78a74ab16d4e"; }
    };

    public static MetadataSetMemberDescriptor DOSSIER_NUMBER_METADATA_SET_MEMBER = new MetadataSetMemberDescriptor() {
        public MetadataSetDescriptor metadataSet() { return EXTRA_IDENTIFIER_TYPES_SET; }
        public String metadataClass() { return PatientIdentifierType.class.getName(); }
        public String metadataUuid() { return HaitiPatientIdentifierTypes.DOSSIER_NUMBER.uuid();}
        public Double sortWeight() { return null; }
        public String uuid() { return "7d1d470a-7236-408f-900f-7d7a5e2690da"; }
    };

    public static MetadataSetMemberDescriptor HIVEMR_V1_METADATA_SET_MEMBER = new MetadataSetMemberDescriptor() {
        public MetadataSetDescriptor metadataSet() { return EXTRA_IDENTIFIER_TYPES_SET; }
        public String metadataClass() { return PatientIdentifierType.class.getName(); }
        public String metadataUuid() { return HaitiPatientIdentifierTypes.HIVEMR_V1.uuid();}
        public Double sortWeight() { return null; }
        public String uuid() { return "8b3aec54-5c1f-4d50-b4a8-9fd90aa73486"; }
    };

    public static MetadataSetMemberDescriptor USER_ENTERED_REF_NUMBER_METADATA_SET_MEMBER = new MetadataSetMemberDescriptor() {
        public MetadataSetDescriptor metadataSet() { return MENTAL_HEALTH_EXTRA_IDENTIFIER_TYPES_SET; }
        public String metadataClass() { return PatientIdentifierType.class.getName(); }
        public String metadataUuid() { return HaitiPatientIdentifierTypes.USER_ENTERED_REF_NUMBER.uuid();}
        public Double sortWeight() { return null; }
        public String uuid() { return "7a2f4308-f2d1-4632-8cb0-d671a43bab18"; }
    };

    public static MetadataTermMappingDescriptor EXTRA_IDENTIFIER_TYPES = new MetadataTermMappingDescriptor() {
        public MetadataSourceDescriptor metadataSource() { return MetadataSources.emrApiSource; }
        public String code() { return EmrApiConstants.GP_EXTRA_PATIENT_IDENTIFIER_TYPES; }
        public String metadataClass() { return MetadataSet.class.getName(); }
        public String metadataUuid() { return EXTRA_IDENTIFIER_TYPES_SET.uuid(); }
        public String uuid() { return "f789cecc-9dbf-4794-8af5-d6a9793edef3"; }
    };

    public static MetadataTermMappingDescriptor MENTAL_HEALTH_EXTRA_IDENTIFIER_TYPES = new MetadataTermMappingDescriptor() {
        public MetadataSourceDescriptor metadataSource() { return MetadataSources.emrApiSource; }
        public String code() { return EmrApiConstants.GP_EXTRA_PATIENT_IDENTIFIER_TYPES; }
        public String metadataClass() { return MetadataSet.class.getName(); }
        public String metadataUuid() { return MENTAL_HEALTH_EXTRA_IDENTIFIER_TYPES_SET.uuid(); }
        public String uuid() { return "9b603ac6-5947-4671-9ac9-7a96fa96c7a7"; }
    };

}

