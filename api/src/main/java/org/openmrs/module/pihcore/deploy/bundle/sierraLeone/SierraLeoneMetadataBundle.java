package org.openmrs.module.pihcore.deploy.bundle.sierraLeone;

import org.openmrs.GlobalProperty;
import org.openmrs.module.coreapps.CoreAppsConstants;
import org.openmrs.module.emrapi.EmrApiConstants;
import org.openmrs.module.metadatadeploy.bundle.AbstractMetadataBundle;
import org.openmrs.module.metadatadeploy.bundle.Requires;
import org.openmrs.module.pihcore.deploy.bundle.core.PihCoreMetadataBundle;
import org.openmrs.module.pihcore.metadata.core.Locations;
import org.openmrs.module.pihcore.metadata.sierraLeone.SierraLeoneMetadataMappings;
import org.openmrs.util.OpenmrsConstants;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.Map;

@Component
@Requires({ PihCoreMetadataBundle.class, SierraLeonePatientIdentifierTypeBundle.class, SierraLeoneLocationsBundle.class, SierraLeoneAddressBundle.class } )
public class SierraLeoneMetadataBundle extends AbstractMetadataBundle {

    @Override
    public void install() throws Exception {

        log.info("Setting Global Properties");

        Map<String, String> properties = new LinkedHashMap<String, String>();

        // OpenMRS Core
        properties.put(OpenmrsConstants.GLOBAL_PROPERTY_LOCALE_ALLOWED_LIST, "en");
        properties.put(OpenmrsConstants.GLOBAL_PROPERTY_DEFAULT_LOCALE, "en");

        // Core Apps
        properties.put(CoreAppsConstants.GP_DEFAULT_PATIENT_IDENTIFIER_LOCATION, Locations.UNKNOWN.uuid());

        setGlobalProperties(properties);

        // EMR API mappings
        install(SierraLeoneMetadataMappings.PRIMARY_IDENTIFIER_TYPE);
        uninstall(possible(GlobalProperty.class, EmrApiConstants.PRIMARY_IDENTIFIER_TYPE), "replaced by metadata mapping");
    }
}
