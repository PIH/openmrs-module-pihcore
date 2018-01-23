/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.pihcore.biometrics;

import org.codehaus.jackson.map.ObjectMapper;
import org.openmrs.module.pihcore.config.Config;
import org.openmrs.module.registrationcore.api.biometrics.BiometricEngine;
import org.openmrs.module.registrationcore.api.biometrics.model.BiometricEngineStatus;
import org.openmrs.module.registrationcore.api.biometrics.model.BiometricMatch;
import org.openmrs.module.registrationcore.api.biometrics.model.BiometricSubject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

/**
 * Base implementation of a Biometric Engine that is accessible over HTTP via REST
 */
@Component
public class RestBiometricEngine implements BiometricEngine {

    @Autowired
    Config config;

    public String getSubjectUrl() {
        return config.getBiometricsConfig().getSubjectUrl();
    }

    public String getMatchUrl() {
        return config.getBiometricsConfig().getMatchUrl();
    }

    @Override
    public BiometricEngineStatus getStatus() {
        BiometricEngineStatus status = new BiometricEngineStatus();
        status.setEnabled(true);
        status.setDescription("REST Biometric Engine");
        return status;
    }

    @Override
    public BiometricSubject enroll(BiometricSubject subject) {
        RestTemplate restTemplate = new RestTemplate();
        String url = getSubjectUrl();
        ResponseEntity<BiometricSubject> response = restTemplate.postForEntity(url, subject, BiometricSubject.class);
        if (HttpStatus.OK.equals(response.getStatusCode()) || HttpStatus.CREATED.equals(response.getStatusCode())) {
            return response.getBody();
        }
        else {
            throw new IllegalStateException("Error enrolling biometric subject at URL <" + url + ">.  Response status code: " + response.getStatusCode());
        }
    }

    @Override
    public BiometricSubject update(BiometricSubject subject) {
        RestTemplate restTemplate = new RestTemplate();
        String url = getSubjectUrl();
        ResponseEntity<BiometricSubject> response = restTemplate.exchange(url, HttpMethod.PUT, new HttpEntity<BiometricSubject>(subject), BiometricSubject.class);
        if (HttpStatus.OK.equals(response.getStatusCode()) || HttpStatus.CREATED.equals(response.getStatusCode())) {
            return response.getBody();
        }
        else {
            throw new IllegalStateException("Error updating biometric subject at URL <" + url + ">.  Response status code: " + response.getStatusCode());
        }
    }

    @Override
    public BiometricSubject updateSubjectId(String oldId, String newId) {
        BiometricSubject subject = lookup(oldId);
        if (subject == null) {
            throw new IllegalStateException("Error updating subject id.  Existing subject with id of " + oldId + " is not found");
        }
        subject.setSubjectId(newId);
        enroll(subject);
        delete(oldId);
        return subject;
    }

    @Override
    public List<BiometricMatch> search(BiometricSubject subject) {
        List<BiometricMatch> ret = new ArrayList<BiometricMatch>();
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
        String url = getMatchUrl();
        ResponseEntity<List> response = restTemplate.postForEntity(url, subject, List.class);
        if (HttpStatus.OK.equals(response.getStatusCode())) {
            ObjectMapper mapper = new ObjectMapper();
            List<Object> matches = response.getBody();
            for (Object o : matches) {
                ret.add(mapper.convertValue(o, BiometricMatch.class));
            }
        }
        else if (!HttpStatus.NOT_FOUND.equals(response.getStatusCode())) {
            throw new IllegalStateException("Error searching for matches of biometric subject at URL <" + url + ">.  Response status code: " + response.getStatusCode());
        }
        return ret;
    }

    @Override
    public BiometricSubject lookup(String subjectId) {
        RestTemplate restTemplate = new RestTemplate();
        String url = getSubjectUrl() + "/" + subjectId;
        ResponseEntity<BiometricSubject> response = restTemplate.getForEntity(url, BiometricSubject.class);
        if (HttpStatus.OK.equals(response.getStatusCode())) {
            return response.getBody();
        }
        else if (HttpStatus.NOT_FOUND.equals(response.getStatusCode())) {
            return null;
        }
        else {
            throw new IllegalStateException("Error looking up biometric subject at URL <" + url + ">.  Response status code: " + response.getStatusCode());
        }
    }

    @Override
    public void delete(String subjectId) {
        RestTemplate restTemplate = new RestTemplate();
        String url = getSubjectUrl() + "/" + subjectId;
        restTemplate.delete(url);
    }
}