/**
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

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.openmrs.Location;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifier;
import org.openmrs.PatientIdentifierType;
import org.openmrs.PersonAddress;
import org.openmrs.PersonAttribute;
import org.openmrs.PersonAttributeType;
import org.openmrs.PersonName;
import org.openmrs.api.PatientService;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Custom service for importing patients
 */
@Component
public class ImportPatientFromWebService {

    public static final String FULL_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";
    protected final Log log = LogFactory.getLog(this.getClass());

    private PatientService patientService;

    private Client restClient;
    private Map<String, RemoteServerConfiguration> remoteServers = new HashMap<String, RemoteServerConfiguration>();

    public ImportPatientFromWebService() {
    }

    public ImportPatientFromWebService(Client restClient) {
        this.restClient = restClient;
    }


    @PostConstruct
    public void init() {
        restClient = Client.create();
    }

    public void setPatientService(PatientService patientService) {
        this.patientService = patientService;
    }

    public void registerRemoteServer(String serverName, RemoteServerConfiguration remoteServerConfiguration) {
        remoteServers.put(serverName, remoteServerConfiguration);
    }

    public Map<String, RemoteServerConfiguration> getRemoteServers() {
        Map<String, RemoteServerConfiguration> map = new HashMap<String, RemoteServerConfiguration>();
        for (Map.Entry<String, RemoteServerConfiguration> entry : remoteServers.entrySet()) {
            map.put(entry.getKey(), entry.getValue().copyWithoutPassword());
        }
        return map;
    }

    public RemotePatient toPatient(String jsonString, Map<String, PatientIdentifierType> identifierTypesByUuid, Map<String, Location> locationsByUuid, Map<String, PersonAttributeType> attributeTypesByUuid) throws IOException {
        JsonNode json = new ObjectMapper().readValue(jsonString, JsonNode.class);
        return toPatient(json, identifierTypesByUuid, locationsByUuid, attributeTypesByUuid);
    }

    private RemotePatient toPatient(JsonNode json, Map<String, PatientIdentifierType> identifierTypesByUuid, Map<String, Location> locationsByUuid, Map<String, PersonAttributeType> attributeTypesByUuid) throws IOException {
        JsonNode person = json.get("person");
        if (person == null) {
            throw new IllegalArgumentException("json must contain a \"person\" field");
        }

        RemotePatient remotePatient = new RemotePatient();
        remotePatient.setRemoteUuid(json.get("uuid").getTextValue());
        Patient patient = new Patient();
        patient.setGender(person.get("gender").getTextValue());
        String personBirthdate = person.get("birthdate").getTextValue();
        if(StringUtils.isNotBlank(personBirthdate)){
            patient.setBirthdate(parseDate(personBirthdate));
        }

        for (JsonNode id : json.get("identifiers")) {
            if (id.get("voided").getBooleanValue()) {
                continue;
            }
            String idTypeUuid = id.get("identifierType").get("uuid").getTextValue();
            PatientIdentifierType idType = identifierTypesByUuid.get(idTypeUuid);
            if (idType == null) {
                if (log.isDebugEnabled()) {
                    log.debug("Skipping unmapped identifier type: " + idTypeUuid);
                }
                continue;
            }

            String identifier = id.get("identifier").getTextValue();

            Location location = null;
            JsonNode locationNode = id.get("location");

            // for some reason in Lacolline there are some identifiers without locations
            if (!locationNode.isNull()) {
                location = locationsByUuid.get(locationNode.get("uuid").getTextValue());
                if (location == null) {
                    if (log.isDebugEnabled()) {
                        log.debug("Skipping unmapped location: " + locationNode.get("uuid").getTextValue());
                    }
                    continue;
                }
            }
            else {
                if (log.isDebugEnabled()) {
                    log.debug("Skipping identifier with missing location");
                }
                continue;
            }

            patient.addIdentifier(new PatientIdentifier(identifier, idType, location));
        }

        for (JsonNode nameJson : person.get("names")) {
            if (nameJson.get("voided").getBooleanValue()) {
                continue;
            }
            PersonName name = new PersonName();
            copyStringProperty(name, nameJson, "prefix");
            copyStringProperty(name, nameJson, "givenName");
            copyStringProperty(name, nameJson, "middleName");
            copyStringProperty(name, nameJson, "familyNamePrefix");
            copyStringProperty(name, nameJson, "familyName");
            copyStringProperty(name, nameJson, "familyName2");
            copyStringProperty(name, nameJson, "familyNameSuffix");
            copyStringProperty(name, nameJson, "degree");
            patient.addName(name);
        }

        for (JsonNode addressJson : person.get("addresses")) {
            if (addressJson.get("voided").getBooleanValue()) {
                continue;
            }
            PersonAddress address = new PersonAddress();
            copyStringProperty(address, addressJson, "address1");
            copyStringProperty(address, addressJson, "address2");
            copyStringProperty(address, addressJson, "address3");
            copyStringProperty(address, addressJson, "address4");
            copyStringProperty(address, addressJson, "address5");
            copyStringProperty(address, addressJson, "address6");
            copyStringProperty(address, addressJson, "cityVillage");
            copyStringProperty(address, addressJson, "countyDistrict");
            copyStringProperty(address, addressJson, "stateProvince");
            copyStringProperty(address, addressJson, "country");
            copyStringProperty(address, addressJson, "postalCode");
            copyStringProperty(address, addressJson, "latitude");
            copyStringProperty(address, addressJson, "longitude");
            copyDateProperty(address, addressJson, "startDate");
            copyDateProperty(address, addressJson, "endDate");
            patient.addAddress(address);
        }

        for (JsonNode attributeJson : person.get("attributes")) {
            if (attributeJson.get("voided").getBooleanValue()) {
                continue;
            }
            String attrTypeUuid = attributeJson.get("attributeType").get("uuid").getTextValue();
            PersonAttributeType personAttributeType = attributeTypesByUuid.get(attrTypeUuid);
            if (personAttributeType == null) {
                if (log.isDebugEnabled()) {
                    log.debug("Skipping unmapped attribute type: " + attrTypeUuid);
                }
                continue;
            }
            String value = attributeJson.get("value").getTextValue();
            if (StringUtils.isNotBlank(value)) {
                PersonAttribute personAttribute = new PersonAttribute(personAttributeType, value);
                patient.addAttribute(personAttribute);
            }
        }
        remotePatient.setPatient(patient);
        return remotePatient;
    }

    public List<RemotePatient> searchRemoteServer(String serverName, String name, String gender, Date birthdate, Integer timeout) throws IOException {
        RemoteServerConfiguration remoteServerConfiguration = remoteServers.get(serverName);
        WebResource resource = setUpWebResource(serverName, remoteServerConfiguration, timeout);

        resource = resource.queryParam("name", name);
        if (gender != null) {
            resource = resource.queryParam("gender", gender);
        }
        if (birthdate != null) {
            resource = resource.queryParam("birthdate", formatDate(birthdate));
        }

        return doPatientSearch(remoteServerConfiguration, resource);
    }

    private List<RemotePatient> doPatientSearch(RemoteServerConfiguration remoteServerConfiguration, WebResource resource) throws IOException {
        String json = resource.accept(MediaType.APPLICATION_JSON_TYPE).get(String.class);
        JsonNode results = new ObjectMapper().readValue(json, JsonNode.class).get("results");

        List<RemotePatient> patients = new ArrayList<RemotePatient>();
        for (JsonNode patient : results) {
            RemotePatient candidate = toPatient(patient, remoteServerConfiguration.getIdentifierTypeMap(), remoteServerConfiguration.getLocationMap(), remoteServerConfiguration.getAttributeTypeMap());
            candidate.setLocalPatient(getLocalPatientByAnyIdentifier(candidate.getPatient()));
            patients.add(candidate);
        }
        return patients;
    }
    private Boolean getLocalPatientByAnyIdentifier(Patient patient){
        Boolean localPatient = false;
        if(patient!=null){
            List<PatientIdentifier> activeIdentifiers = patient.getActiveIdentifiers();
            if(activeIdentifiers!=null && activeIdentifiers.size()>0){
                for(PatientIdentifier patientIdentifier : activeIdentifiers){
                    if(patientIdentifier.getIdentifierType()!=null){
                        List<Patient> patients = patientService.getPatients(null, patientIdentifier.getIdentifier(), Arrays.asList(patientIdentifier.getIdentifierType()), true);
                        if(patients!=null && patients.size()>0){
                            return true;
                        }
                    }
                }
            }
        }
        return localPatient;
    }

    private WebResource setUpWebResource(String serverName, RemoteServerConfiguration remoteServerConfiguration, Integer timeout) {
        if (remoteServerConfiguration == null) {
            throw new IllegalArgumentException("Unknown remote server: " + serverName + ". Known servers are " + remoteServers.keySet());
        }
        if (!remoteServerConfiguration.getUrl().startsWith("https://")) {
            log.warn("non-HTTPS connection to " + serverName);
        }

        WebResource resource = restClient.resource(remoteServerConfiguration.getUrl()).path("ws/rest/v1/patient").queryParam("v", "full");
        if(timeout!=null){
            restClient.setReadTimeout(timeout);
        }
        resource.addFilter(new HTTPBasicAuthFilter(remoteServerConfiguration.getUsername(), remoteServerConfiguration.getPassword()));
        return resource;
    }

    public List<RemotePatient> searchRemoteServer(String serverName, String id, Integer timeout) throws IOException {
        RemoteServerConfiguration remoteServerConfiguration = remoteServers.get(serverName);
        WebResource resource = setUpWebResource(serverName, remoteServerConfiguration, timeout);

        resource = resource.queryParam("id", id);

        return doPatientSearch(remoteServerConfiguration, resource);
    }

    private String formatDate(Date date) {
        return new SimpleDateFormat(FULL_DATE_FORMAT).format(date);
    }

    private void copyDateProperty(Object ontoBean, JsonNode fromJson, String field) {
        String asText;
        try {
            asText = fromJson.get(field).getTextValue();
        } catch (Exception ex) {
            // skip fields not contained in the json
            return;
        }
        if (StringUtils.isBlank(asText)) {
            return;
        }
        Date date = parseDate(asText);
        try {
            PropertyUtils.setProperty(ontoBean, field, date);
        } catch (Exception ex) {
            throw new IllegalArgumentException(ex);
        }
    }

    private void copyStringProperty(Object ontoBean, JsonNode fromJson, String field) {
        String asText;
        try {
            asText = fromJson.get(field).getTextValue();
        } catch (Exception ex) {
            // skip fields not contained in the json
            return;
        }
        try {
            PropertyUtils.setProperty(ontoBean, field, asText);
        } catch (Exception ex) {
            throw new IllegalArgumentException(ex);
        }
    }

    private Date parseDate(String date) {
        try {
            DateFormat dateFormat = new SimpleDateFormat(FULL_DATE_FORMAT);
            return dateFormat.parse(date);
        } catch (ParseException e) {
            throw new IllegalArgumentException("Badly formatted date: " + date);
        }
    }

}