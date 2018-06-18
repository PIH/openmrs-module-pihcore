package org.openmrs.module.pihcore.deploy.bundle.mexico;


import org.openmrs.module.metadatadeploy.bundle.AbstractMetadataBundle;
import org.openmrs.module.metadatadeploy.bundle.Requires;
import org.openmrs.module.pihcore.deploy.bundle.core.PihCoreMetadataBundle;
import org.openmrs.module.pihcore.metadata.mexico.MexicoLocations;
import org.springframework.stereotype.Component;

@Component
@Requires({ PihCoreMetadataBundle.class } )
public class MexicoLocationsBundle extends AbstractMetadataBundle {

    @Override
    public void install() throws Exception {
        install(MexicoLocations.CHIAPAS);
    }
}
