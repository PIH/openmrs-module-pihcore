package org.openmrs.module.pihcore.setup;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.PatientIdentifierType;
import org.openmrs.api.PatientService;
import org.openmrs.module.emrapi.EmrApiConstants;
import org.openmrs.module.metadatamapping.api.MetadataMappingService;
import org.openmrs.module.pihcore.CesConfigConstants;
import org.openmrs.module.pihcore.LiberiaConfigConstants;
import org.openmrs.module.pihcore.SierraLeoneConfigConstants;
import org.openmrs.module.pihcore.ZlConfigConstants;
import org.openmrs.module.pihcore.config.Config;
import org.openmrs.module.pihcore.config.ConfigDescriptor;

public class MetadataMappingsSetup {

    protected static Log log = LogFactory.getLog(MetadataMappingsSetup.class);

    public static void setupPrimaryIdentifierTypeBasedOnCountry(MetadataMappingService mms, PatientService ps, Config config) {
        if (config.isCountry(ConfigDescriptor.Country.HAITI)) {
            setupPrimaryIdentifierType(mms, ps, ZlConfigConstants.PATIENTIDENTIFIERTYPE_ZLEMRID_UUID);
        }
        else if (config.isCountry(ConfigDescriptor.Country.LIBERIA)) {
            setupPrimaryIdentifierType(mms, ps, LiberiaConfigConstants.PATIENTIDENTIFIERTYPE_LIBERIAEMRID_UUID);
        }
        else if (config.isCountry(ConfigDescriptor.Country.SIERRA_LEONE)) {
            if (config.isWellbody()) {
                setupPrimaryIdentifierType(mms, ps, SierraLeoneConfigConstants.PATIENTIDENTIFIERTYPE_WELLBODYEMRID_UUID);
            }
            else if (config.isKgh()) {
                setupPrimaryIdentifierType(mms, ps, SierraLeoneConfigConstants.PATIENTIDENTIFIERTYPE_KGHEMRID_UUID);
            }
            else if (config.getSite() != null) {
                throw new IllegalStateException("Unable to setup primary identifier type for site: " + config.getSite());
            }
            else {
                // no site configured (e.g. a generic, country-only base) -- nothing to map yet
                log.warn("No site configured for SIERRA_LEONE -- skipping primary identifier type mapping. Expected for a generic, country-only base config; a facility config must be layered on before patients can be registered.");
            }
        }
        else if (config.isCountry(ConfigDescriptor.Country.MEXICO)) {
            setupPrimaryIdentifierType(mms, ps, CesConfigConstants.PATIENTIDENTIFIERTYPE_CHIAPASEMRID_UUID);
        }
    }

    public static void setupPrimaryIdentifierType(MetadataMappingService metadataMappingService, PatientService patientService, String identifierTypeUuid) {
        PatientIdentifierType patientIdentifierType = patientService.getPatientIdentifierTypeByUuid(identifierTypeUuid);
        metadataMappingService.mapMetadataItem(patientIdentifierType, EmrApiConstants.EMR_CONCEPT_SOURCE_NAME, EmrApiConstants.PRIMARY_IDENTIFIER_TYPE);
    }
}


