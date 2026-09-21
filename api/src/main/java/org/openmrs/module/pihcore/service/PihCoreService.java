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
package org.openmrs.module.pihcore.service;

import org.openmrs.Order;
import org.openmrs.Patient;
import org.openmrs.api.OpenmrsService;
import org.openmrs.api.db.OrderDAO;
import org.openmrs.module.emrapi.adt.InpatientAdmission;
import org.openmrs.module.emrapi.adt.InpatientRequest;
import org.openmrs.module.pihcore.config.Config;
import org.openmrs.module.pihcore.model.Vaccination;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * Service for PIH Core
 */
@Transactional
public interface PihCoreService extends OpenmrsService {

    /**
     * @return the next available radiology order number seed
     */
    Long getNextRadiologyOrderNumberSeedSequenceValue();

    /**
     * @see OrderDAO#saveOrder(Order)
     */
    Order saveOrder(Order order);

    List<InpatientAdmission> getStaleInpatientAdmissions(Date admittedOnOrBefore, int mostRecentEncounterThresholdInDays);

    List<InpatientRequest> getStaleAdmissionRequests(Date admissionRequestOnOrBefore, int mostRecentEncounterThresholdInDays);

    List<Vaccination> getVaccinations(Patient patient);

    void updateHealthCenter(Patient patient);

    // just for mocking during tests, don't use outside of test context
    void setConfig(Config config);
}
