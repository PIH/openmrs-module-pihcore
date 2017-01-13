package org.openmrs.module.pihcore.metadata.sierraLeone;

import org.openmrs.PatientIdentifierType;
import org.openmrs.module.emrapi.EmrApiConstants;
import org.openmrs.module.metadatadeploy.descriptor.MetadataSourceDescriptor;
import org.openmrs.module.metadatadeploy.descriptor.MetadataTermMappingDescriptor;
import org.openmrs.module.pihcore.metadata.core.MetadataSources;

;

public class SierraLeoneMetadataMappings {

    public static MetadataTermMappingDescriptor PRIMARY_IDENTIFIER_TYPE = new MetadataTermMappingDescriptor() {
        public MetadataSourceDescriptor metadataSource() { return MetadataSources.emrApiSource; }
        public String code() { return EmrApiConstants.PRIMARY_IDENTIFIER_TYPE; }
        public String metadataClass() { return PatientIdentifierType.class.getName(); }
        public String metadataUuid() { return SierraLeonePatientIdentifierTypes.WELLBODY_EMR_ID.uuid(); }
        public String uuid() { return "f4d31d4e-7da9-4b05-8ca2-c74d681965eb"; }
    };
}
