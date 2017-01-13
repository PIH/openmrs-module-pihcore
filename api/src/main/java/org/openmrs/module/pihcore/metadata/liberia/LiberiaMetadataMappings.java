package org.openmrs.module.pihcore.metadata.liberia;

import org.openmrs.PatientIdentifierType;
import org.openmrs.module.emrapi.EmrApiConstants;
import org.openmrs.module.metadatadeploy.descriptor.MetadataSourceDescriptor;
import org.openmrs.module.metadatadeploy.descriptor.MetadataTermMappingDescriptor;
import org.openmrs.module.pihcore.metadata.core.MetadataSources;

public class LiberiaMetadataMappings {

    public static MetadataTermMappingDescriptor PRIMARY_IDENTIFIER_TYPE = new MetadataTermMappingDescriptor() {
        public MetadataSourceDescriptor metadataSource() { return MetadataSources.emrApiSource; }
        public String code() { return EmrApiConstants.PRIMARY_IDENTIFIER_TYPE; }
        public String metadataClass() { return PatientIdentifierType.class.getName(); }
        public String metadataUuid() { return LiberiaPatientIdentifierTypes.PLEEBO_EMR_ID.uuid(); }
        public String uuid() { return "3150d76b-be68-4912-851d-1da4531577c6"; }
    };

}
