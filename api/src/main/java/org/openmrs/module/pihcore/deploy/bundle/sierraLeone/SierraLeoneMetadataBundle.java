package org.openmrs.module.pihcore.deploy.bundle.sierraLeone;

import org.openmrs.module.metadatadeploy.bundle.AbstractMetadataBundle;
import org.openmrs.module.metadatadeploy.bundle.Requires;
import org.openmrs.module.pihcore.deploy.bundle.core.PihCoreMetadataBundle;
import org.springframework.stereotype.Component;

@Component
@Requires({ PihCoreMetadataBundle.class,
        SierraLeonePatientIdentifierTypeBundle.class,
        SierraLeoneLocationsBundle.class,
        SierraLeoneEncounterTypeBundle.class } )
public class SierraLeoneMetadataBundle extends AbstractMetadataBundle {

    @Override
    public void install() throws Exception {
    }
}
