package org.openmrs.module.pihcore.metadata.mexico;

import org.openmrs.module.metadatadeploy.descriptor.LocationDescriptor;
import org.openmrs.module.metadatadeploy.descriptor.LocationTagDescriptor;

import java.util.Collections;
import java.util.List;

public class MexicoLocations {

    public static LocationDescriptor CHIAPAS = new LocationDescriptor() {
        public String uuid() { return "5a49103e-a263-4a83-a29a-490788226787"; }
        public String name() { return "Chiapas"; }
        public String description() { return "Chiapas"; }
        public List<LocationTagDescriptor> tags() { return Collections.emptyList(); }
    };

}
