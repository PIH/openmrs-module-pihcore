package org.openmrs.module.pihcore.setup;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Location;
import org.openmrs.PatientIdentifierType;
import org.openmrs.PersonAttributeType;
import org.openmrs.api.context.Context;
import org.openmrs.module.pihcore.RuntimeProperties;
import org.openmrs.module.pihcore.ZlConfigConstants;
import org.openmrs.module.pihcore.metadata.Metadata;
import org.openmrs.module.pihcore.mpi.ImportPatientFromWebService;
import org.openmrs.module.pihcore.mpi.RemoteServerConfiguration;

import java.util.HashMap;
import java.util.Map;

public class LegacyMasterPatientIndexSetup {

    protected static Log log = LogFactory.getLog(LegacyMasterPatientIndexSetup.class);

    public static final String LACOLLINE_LOCATION_UUID = "23e7bb0d-51f9-4d5f-b34b-2fbbfeea1960";

    public static void setupConnectionToMasterPatientIndex(RuntimeProperties customProperties) {
        String url = customProperties.getLacollineServerUrl();
        String username = customProperties.getLacollineUsername();
        String password = customProperties.getLacollinePassword();

        if (url == null || username == null || password == null) {
            log.warn("Not configuring link to Lacolline server (url, username, and password are required)");
            return;
        }

        Map<String, PatientIdentifierType> identifierTypeMap = new HashMap<String, PatientIdentifierType>();
        identifierTypeMap.put("a541af1e-105c-40bf-b345-ba1fd6a59b85", Context.getPatientService().getPatientIdentifierTypeByUuid(ZlConfigConstants.PATIENTIDENTIFIERTYPE_ZLEMRID_UUID));
        // TODO create PatientIdentifierType for Lacolline KE dossier number
        identifierTypeMap.put("e66645eb-03a8-4991-b4ce-e87318e37566", Context.getPatientService().getPatientIdentifierTypeByUuid(ZlConfigConstants.PATIENTIDENTIFIERTYPE_EXTERNALDOSSIERNUMBER_UUID));
        // TODO create PatientIdentifierType for Lacolline dental dossier number

        Map<String, Location> locationMap = new HashMap<String, Location>();
        locationMap.put("23e7bb0d-51f9-4d5f-b34b-2fbbfeea1960", Context.getLocationService().getLocationByUuid(LACOLLINE_LOCATION_UUID));

        Map<String, PersonAttributeType> attributeTypeMap = new HashMap<String, PersonAttributeType>();
        attributeTypeMap.put("340d04c4-0370-102d-b0e3-001ec94a0cc1", Metadata.getPhoneNumberAttributeType());

        RemoteServerConfiguration config = new RemoteServerConfiguration();
        config.setUrl(url);
        config.setUsername(username);
        config.setPassword(password);
        config.setIdentifierTypeMap(identifierTypeMap);
        config.setLocationMap(locationMap);
        config.setAttributeTypeMap(attributeTypeMap);

        Context.getService(ImportPatientFromWebService.class).registerRemoteServer("lacolline", config);
    }

}
