package org.openmrs.module.pihcore.deploy.bundle.mexico;

import org.openmrs.module.coreapps.CoreAppsConstants;
import org.openmrs.module.metadatadeploy.bundle.AbstractMetadataBundle;
import org.openmrs.module.metadatadeploy.bundle.Requires;
import org.openmrs.module.pihcore.deploy.bundle.core.PihCoreMetadataBundle;
import org.openmrs.module.pihcore.metadata.core.Locations;
import org.openmrs.module.pihcore.metadata.mexico.MexicoLocations;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.Map;

@Component
@Requires({ PihCoreMetadataBundle.class,
        MexicoLocationsBundle.class,
        MexicoPatientIdentifierTypeBundle.class} )
public class MexicoMetadataBundle extends AbstractMetadataBundle {
    public static final String DEFAULT_LOCALE = "es";
    public static final String ALLOWED_LOCALES = "es, en";

    @Override
    public void install() throws Exception {

        log.info("Setting Global Properties");

        Map<String, String> properties = new LinkedHashMap<String, String>();

        // OpenMRS Core
        // (we have to do this rigamarole because of new validations in 2.x that
        // confirms that the allowed list contains the default locale, making it a two-step process to change)
    		// (this is also a direct copy of code in LiberiaMetadataBundle, we should abstract this out)
    		if (ALLOWED_LOCALES.contains(LocaleUtility.getDefaultLocale().toString())) {
    			properties.put(OpenmrsConstants.GLOBAL_PROPERTY_LOCALE_ALLOWED_LIST, ALLOWED_LOCALES);
    		}
    		else {
    			properties.put(OpenmrsConstants.GLOBAL_PROPERTY_LOCALE_ALLOWED_LIST,
            ALLOWED_LOCALES +", " + LocaleUtility.getDefaultLocale().toString());
    		}
    		properties.put(OpenmrsConstants.GLOBAL_PROPERTY_DEFAULT_LOCALE, DEFAULT_LOCALE);
    		setGlobalProperties(properties);

    		properties.put(OpenmrsConstants.GLOBAL_PROPERTY_LOCALE_ALLOWED_LIST, ALLOWED_LOCALES);

        // Core Apps
        properties.put(CoreAppsConstants.GP_DEFAULT_PATIENT_IDENTIFIER_LOCATION, MexicoLocations.JALTENANGO.uuid());

        setGlobalProperties(properties);

    }

}
