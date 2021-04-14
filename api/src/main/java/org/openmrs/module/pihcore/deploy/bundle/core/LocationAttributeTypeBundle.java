package org.openmrs.module.pihcore.deploy.bundle.core;

import org.openmrs.module.metadatadeploy.bundle.AbstractMetadataBundle;
import org.openmrs.module.pihcore.metadata.core.LocationAttributeTypes;
import org.springframework.stereotype.Component;

@Component
public class LocationAttributeTypeBundle extends AbstractMetadataBundle {

    @Override
    public void install() throws Exception {
        install(LocationAttributeTypes.LOCATION_CODE);
        install(LocationAttributeTypes.DEFAULT_LABEL_PRINTER);
        install(LocationAttributeTypes.DEFAULT_ID_CARD_PRINTER);
        install(LocationAttributeTypes.DEFAULT_WRISTBAND_PRINTER);
        install(LocationAttributeTypes.NAME_TO_PRINT_ON_ID_CARD);
    }

}
