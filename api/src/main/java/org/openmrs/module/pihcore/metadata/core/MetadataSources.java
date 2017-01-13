package org.openmrs.module.pihcore.metadata.core;

import org.openmrs.module.emrapi.EmrApiConstants;
import org.openmrs.module.metadatadeploy.descriptor.MetadataSourceDescriptor;

public class MetadataSources {

    public static MetadataSourceDescriptor emrApiSource = new MetadataSourceDescriptor() {
        public String name() { return EmrApiConstants.EMR_METADATA_SOURCE_NAME; }
        public String description() { return EmrApiConstants.EMR_METADATA_SOURCE_DESCRIPTION; }
        public String uuid() { return EmrApiConstants.EMR_METADATA_SOURCE_UUID; }
    };

}
