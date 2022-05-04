/*
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */

package org.openmrs.module.pihcore.mpi;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.openmrs.Location;
import org.openmrs.PatientIdentifierType;
import org.openmrs.PersonAttributeType;
import org.openmrs.api.context.Context;
import org.openmrs.module.pihcore.PihCoreContextSensitiveTest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 */
@Disabled // Boston server is no longer running
public class LiveWebServiceTest extends PihCoreContextSensitiveTest {

    public final static String url="http://boston.pih-emr.org:8080/openmrs";
    public final static String username="wsuser";
    public final static String password="wsuser123";

    private ImportPatientFromWebService service;

    @BeforeEach
    public void setUp() throws Exception {
        PatientIdentifierType patientIdentifierType = Context.getPatientService().getPatientIdentifierType(2);
        Map<String, PatientIdentifierType> identifierTypes = new HashMap<String, PatientIdentifierType>();
        identifierTypes.put("a541af1e-105c-40bf-b345-ba1fd6a59b85", patientIdentifierType);

        Location xanadu = Context.getLocationService().getLocation(2);
        Map<String, Location> locationMap = new HashMap<String, Location>();
        locationMap.put("23e7bb0d-51f9-4d5f-b34b-2fbbfeea1960", xanadu);

        PersonAttributeType birthplace = Context.getPersonService().getPersonAttributeType(2);
        Map<String, PersonAttributeType> attributeTypeMap = new HashMap<String, PersonAttributeType>();
        attributeTypeMap.put("340d04c4-0370-102d-b0e3-001ec94a0cc1", birthplace);

        RemoteServerConfiguration remoteServerConfiguration = new RemoteServerConfiguration();
        remoteServerConfiguration.setUrl(url);
        remoteServerConfiguration.setUsername(username);
        remoteServerConfiguration.setPassword(password);
        remoteServerConfiguration.setIdentifierTypeMap(identifierTypes);
        remoteServerConfiguration.setLocationMap(locationMap);
        remoteServerConfiguration.setAttributeTypeMap(attributeTypeMap);

        service = Context.getService(ImportPatientFromWebService.class);
        service.registerRemoteServer("testing", remoteServerConfiguration);
    }

    @Test
    public void testLiveWebserviceSearchByName() throws Exception {
        List<RemotePatient> patients = service.searchRemoteServer("testing", "Test", "F", null, 30000);

        System.out.println("=== Found " + patients.size() + " patients by name ===");
        for (RemotePatient remotePatient : patients) {
            System.out.println(remotePatient.getPatient().getPatientIdentifier() + " - " + remotePatient.getPatient().getPersonName());
        }
    }

    @Test
    public void testLiveWebserviceSearchByNameAndGender() throws Exception {
        List<RemotePatient> patients = service.searchRemoteServer("testing", "ellen bal", "M", null, null);

        System.out.println("=== Found " + patients.size() + " patients by name and gender===");
        for (RemotePatient remotePatient : patients) {
            System.out.println(remotePatient.getPatient().getPatientIdentifier() + " - " + remotePatient.getPatient().getPersonName());
        }
    }

    @Test
    public void testLiveWebserviceSearchById() throws Exception {
        List<RemotePatient> patients = service.searchRemoteServer("testing", "2ALH69", null);

        System.out.println("=== Found " + patients.size() + " patients by ID ===");
        for (RemotePatient remotePatient : patients) {
            System.out.println(remotePatient.getPatient().getPatientIdentifier() + " - " + remotePatient.getPatient().getPersonName());
        }
    }

}
