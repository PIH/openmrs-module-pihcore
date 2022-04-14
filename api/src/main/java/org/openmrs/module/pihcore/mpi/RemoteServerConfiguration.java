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

import org.openmrs.Location;
import org.openmrs.PatientIdentifierType;
import org.openmrs.PersonAttributeType;

import java.util.Map;

public class RemoteServerConfiguration {

    private String url;
    private String username;
    private String password;
    private Map<String, PatientIdentifierType> identifierTypeMap;
    private Map<String, Location> locationMap;
    private Map<String, PersonAttributeType> attributeTypeMap;

    public RemoteServerConfiguration() {
    }

    public RemoteServerConfiguration(String url, String username, String password, Map<String, PatientIdentifierType> identifierTypeMap, Map<String, Location> locationMap, Map<String, PersonAttributeType> attributeTypeMap) {
        this.url = url;
        this.username = username;
        this.password = password;
        this.identifierTypeMap = identifierTypeMap;
        this.locationMap = locationMap;
        this.attributeTypeMap = attributeTypeMap;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Map<String, PatientIdentifierType> getIdentifierTypeMap() {
        return identifierTypeMap;
    }

    public void setIdentifierTypeMap(Map<String, PatientIdentifierType> identifierTypeMap) {
        this.identifierTypeMap = identifierTypeMap;
    }

    public Map<String, Location> getLocationMap() {
        return locationMap;
    }

    public void setLocationMap(Map<String, Location> locationMap) {
        this.locationMap = locationMap;
    }

    public Map<String, PersonAttributeType> getAttributeTypeMap() {
        return attributeTypeMap;
    }

    public void setAttributeTypeMap(Map<String, PersonAttributeType> attributeTypeMap) {
        this.attributeTypeMap = attributeTypeMap;
    }

    public RemoteServerConfiguration copyWithoutPassword() {
        RemoteServerConfiguration copy = new RemoteServerConfiguration();
        copy.setUrl(getUrl());
        copy.setUsername(getUsername());
        copy.setIdentifierTypeMap(getIdentifierTypeMap());
        copy.setLocationMap(getLocationMap());
        copy.setAttributeTypeMap(getAttributeTypeMap());
        return copy;
    }
}
