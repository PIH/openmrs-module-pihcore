package org.openmrs.module.pihcore.deploy.bundle;

import org.openmrs.module.pihcore.descriptor.LocationAttributeDescriptor;
import org.openmrs.module.pihcore.descriptor.LocationDescriptor;
import org.openmrs.module.pihcore.descriptor.LocationTagDescriptor;

import java.util.ArrayList;
import java.util.List;

import static org.openmrs.module.metadatadeploy.bundle.CoreConstructors.location;
import static org.openmrs.module.metadatadeploy.bundle.CoreConstructors.locationAttribute;

public abstract class PihLocationsMetadataBundle extends PihMetadataBundle {


    protected void installLocation(LocationDescriptor location) {

        // First install the location and it's tags
        String parentUuid = location.parent() == null ? null : location.parent().uuid();
        List<String> tagUuids = new ArrayList<String>();
        if (location.tags() != null) {
            for (LocationTagDescriptor tagDescriptor : location.tags()) {
                tagUuids.add(tagDescriptor.uuid());
            }
        }
        install(location(location.name(), location.description(), location.uuid(), parentUuid, tagUuids));

        // Then, install the location attribute(s) if applicable
        if (location.attributes() != null) {
            for (LocationAttributeDescriptor lad : location.attributes()) {
                if (!lad.location().uuid().equals(location.uuid())) {
                    throw new IllegalStateException("Location Attribute with uuid " + lad.uuid() + " is configured with a different location than it the Location it is associated with");
                }
                install(locationAttribute(lad.location().uuid(), lad.type().uuid(), lad.value(), lad.uuid()));
            }
        }
    }
}
