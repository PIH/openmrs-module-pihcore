package org.openmrs.module.pihcore.deploy.bundle.mexico;

import org.openmrs.module.coreapps.CoreAppsConstants;
import org.openmrs.module.metadatadeploy.bundle.AbstractMetadataBundle;
import org.openmrs.module.metadatadeploy.bundle.Requires;
import org.openmrs.module.pihcore.deploy.bundle.core.PihCoreMetadataBundle;
import org.openmrs.module.pihcore.metadata.core.Locations;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.Map;

@Component
@Requires({ PihCoreMetadataBundle.class,
        MexicoLocationsBundle.class,
        MexicoPatientIdentifierTypeBundle.class} )
public class MexicoMetadataBundle extends AbstractMetadataBundle {


    @Override
    public void install() throws Exception {

        log.info("Setting Global Properties");

        Map<String, String> properties = new LinkedHashMap<String, String>();

        // OpenMRS Core

        // Core Apps
        properties.put(CoreAppsConstants.GP_DEFAULT_PATIENT_IDENTIFIER_LOCATION, Locations.UNKNOWN.uuid());

        setGlobalProperties(properties);

    }

}
