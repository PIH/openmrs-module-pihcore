package org.openmrs.module.pihcore.setup;

import org.openmrs.PatientIdentifierType;
import org.openmrs.api.PatientService;
import org.openmrs.module.emrapi.EmrApiConstants;
import org.openmrs.module.metadatamapping.api.MetadataMappingService;
import org.openmrs.module.pihcore.CesConfigConstants;
import org.openmrs.module.pihcore.LiberiaConfigConstants;
import org.openmrs.module.pihcore.SesConfigConstants;
import org.openmrs.module.pihcore.SierraLeoneConfigConstants;
import org.openmrs.module.pihcore.ZlConfigConstants;
import org.openmrs.module.pihcore.config.Config;
import org.openmrs.module.pihcore.config.ConfigDescriptor;

public class MetadataMappingsSetup {

    public static void setupPrimaryIdentifierTypeBasedOnCountry(MetadataMappingService mms, PatientService ps, Config config) {
        if (config.getCountry().equals(ConfigDescriptor.Country.HAITI)) {
            setupPrimaryIdentifierType(mms, ps, ZlConfigConstants.PATIENTIDENTIFIERTYPE_ZLEMRID_UUID);
        }
        else if (config.getCountry().equals(ConfigDescriptor.Country.LIBERIA)) {
            setupPrimaryIdentifierType(mms, ps, LiberiaConfigConstants.PATIENTIDENTIFIERTYPE_LIBERIAEMRID_UUID);
        }
        else if (config.getCountry().equals(ConfigDescriptor.Country.SIERRA_LEONE)) {
            if ("WELLBODY".equalsIgnoreCase(config.getSite())) {
                setupPrimaryIdentifierType(mms, ps, SierraLeoneConfigConstants.PATIENTIDENTIFIERTYPE_WELLBODYEMRID_UUID);
            }
            else if ("KGH".equalsIgnoreCase(config.getSite())) {
                setupPrimaryIdentifierType(mms, ps, SierraLeoneConfigConstants.PATIENTIDENTIFIERTYPE_KGHEMRID_UUID);
            }
            else {
                throw new IllegalStateException("Unable to setup primary identifier type for site: " + config.getSite());
            }
        }
        else if (config.getCountry().equals(ConfigDescriptor.Country.MEXICO)) {
            setupPrimaryIdentifierType(mms, ps, CesConfigConstants.PATIENTIDENTIFIERTYPE_CHIAPASEMRID_UUID);
        }
        else if (config.getCountry().equals(ConfigDescriptor.Country.PERU)) {
            setupPrimaryIdentifierType(mms, ps, SesConfigConstants.PATIENTIDENTIFIERTYPE_SESEMRID_UUID);
        }
    }

    public static void setupPrimaryIdentifierType(MetadataMappingService metadataMappingService, PatientService patientService, String identifierTypeUuid) {
        PatientIdentifierType patientIdentifierType = patientService.getPatientIdentifierTypeByUuid(identifierTypeUuid);
        metadataMappingService.mapMetadataItem(patientIdentifierType, EmrApiConstants.EMR_CONCEPT_SOURCE_NAME, EmrApiConstants.PRIMARY_IDENTIFIER_TYPE);
    }
}


