package org.openmrs.module.pihcore.handler;

import org.openmrs.Location;
import org.openmrs.annotation.Handler;
import org.openmrs.module.metadatadeploy.ObjectUtils;
import org.openmrs.module.metadatadeploy.handler.impl.LocationDeployHandler;

import java.util.Collections;

@Handler(supports = { Location.class }, order = 50)
public class CustomLocationDeployHandler extends LocationDeployHandler {

    // overwrite the standard overwrite method of the Location Deploy Handler so as to not
    // blow away any existing attributes

    @Override
    public void overwrite(Location incoming, Location existing) {
        Integer existingId = existing.getId();
        ObjectUtils.overwrite(incoming, existing, Collections.singleton("attributes"));
        existing.setId(existingId);
    }


}
