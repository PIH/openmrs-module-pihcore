package org.openmrs.module.pihcore.deploy.bundle.liberia;

import org.openmrs.EncounterType;
import org.openmrs.GlobalProperty;
import org.openmrs.module.coreapps.CoreAppsConstants;
import org.openmrs.module.emrapi.EmrApiConstants;
import org.openmrs.module.metadatadeploy.bundle.AbstractMetadataBundle;
import org.openmrs.module.metadatadeploy.bundle.Requires;
import org.openmrs.module.paperrecord.PaperRecordConstants;
import org.openmrs.module.pihcore.deploy.bundle.core.GlobalPropertiesBundle;
import org.openmrs.module.pihcore.deploy.bundle.core.PihCoreMetadataBundle;
import org.openmrs.module.pihcore.metadata.liberia.LiberiaLocations;
import org.openmrs.module.pihcore.metadata.liberia.LiberiaPatientIdentifierTypes;
import org.openmrs.module.pihcore.metadata.mexico.MexicoEncounterTypes;
import org.openmrs.module.pihcore.metadata.sierraLeone.SierraLeoneEncounterTypes;
import org.openmrs.util.LocaleUtility;
import org.openmrs.util.OpenmrsConstants;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.Map;

@Component
@Requires({ PihCoreMetadataBundle.class,
            LiberiaLocationsBundle.class,
            LiberiaPatientIdentifierTypeBundle.class,
            LiberiaAddressBundle.class } )
public class LiberiaMetadataBundle extends AbstractMetadataBundle {

    public static final String DEFAULT_LOCALE = "en";
    public static final String ALLOWED_LOCALES = "en";

    @Override
    public void install() throws Exception {

        log.info("Setting Global Properties");

        Map<String, String> properties = new LinkedHashMap<String, String>();

        // OpenMRS Core
        // (we have to do this rigamarole because of new validations in 2.x that confirms that the allowed list contains the default locale, making it a two-step process to change)
        // (this is also a direct copy of code in HaitiMetadataBundle, we should abstract this out)
        if (ALLOWED_LOCALES.contains(LocaleUtility.getDefaultLocale().toString())) {
            properties.put(OpenmrsConstants.GLOBAL_PROPERTY_LOCALE_ALLOWED_LIST, ALLOWED_LOCALES);
        }
        else {
            properties.put(OpenmrsConstants.GLOBAL_PROPERTY_LOCALE_ALLOWED_LIST, ALLOWED_LOCALES +", " + LocaleUtility.getDefaultLocale().toString());
        }
        properties.put(OpenmrsConstants.GLOBAL_PROPERTY_DEFAULT_LOCALE, DEFAULT_LOCALE);
        setGlobalProperties(properties);

        properties.put(OpenmrsConstants.GLOBAL_PROPERTY_LOCALE_ALLOWED_LIST, ALLOWED_LOCALES);

        // EMR API: most global properties have been moved to metadata mappings, but evidently not this one
        properties.put(EmrApiConstants.GP_DIAGNOSIS_SET_OF_SETS, GlobalPropertiesBundle.Concepts.LIBERIA_DIAGNOSIS_SET_OF_SETS);

        // Core Apps

        properties.put(PaperRecordConstants.GP_PAPER_RECORD_IDENTIFIER_TYPE, LiberiaPatientIdentifierTypes.SAMPLE_DOSSIER_NUMBER_ID.uuid());
        properties.put(CoreAppsConstants.GP_DEFAULT_PATIENT_IDENTIFIER_LOCATION, LiberiaLocations.PLEEBO.uuid());

        // Order Entry OWA
        properties.put("orderentryowa.labOrderablesConceptSet","d75d3f5b-6777-41cf-ad9f-dce07a577eb3");

        // Lab Workflow OWA
        properties.put("labworkflowowa.locationOfLaboratory", "76c4e6f2-d885-4d1f-8bad-f8320f20ee85");

        setGlobalProperties(properties);

        uninstall(possible(GlobalProperty.class, EmrApiConstants.PRIMARY_IDENTIFIER_TYPE), "replaced by metadata mapping");

        // uninstall Mexico and Sierra Leone encounter types that were originally installed globally
        uninstall(possible(EncounterType.class, MexicoEncounterTypes.MEXICO_CONSULT.uuid()), "now installed only in Mexico");
        uninstall(possible(EncounterType.class, SierraLeoneEncounterTypes.SIERRA_LEONE_OUTPATIENT_INITIAL.uuid()), "now installed only in Sierra Leone");
        uninstall(possible(EncounterType.class, SierraLeoneEncounterTypes.SIERRA_LEONE_OUTPATIENT_FOLLOWUP.uuid()), "now installed only in Sierra Leone");
    }
}
