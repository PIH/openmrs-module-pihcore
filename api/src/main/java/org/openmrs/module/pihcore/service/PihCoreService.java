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

import org.openmrs.Encounter;
import org.openmrs.EncounterType;
import org.openmrs.Obs;
import org.openmrs.Order;
import org.openmrs.Patient;
import org.openmrs.Provider;
import org.openmrs.User;
import org.openmrs.annotation.Authorized;
import org.openmrs.api.OpenmrsService;
import org.openmrs.api.db.OrderDAO;
import org.openmrs.module.emrapi.adt.InpatientAdmission;
import org.openmrs.module.emrapi.adt.InpatientRequest;
import org.openmrs.module.pihcore.config.Config;
import org.openmrs.module.pihcore.model.Vaccination;
import org.openmrs.util.PrivilegeConstants;
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

    /**
     * Observations whose audit trail names the given user, most recent action first. Voided
     * observations are included, since they are the whole point of a voidedBy search and are what
     * an auditor most wants to see in a createdBy one.
     *
     * <p>A date range bounds when the audit action happened rather than the observation's own
     * datetime, and runs inclusively at both ends.
     *
     * @param createdBy restrict to observations this user created, or null not to
     * @param voidedBy restrict to observations this user voided, or null not to
     * @param fromDate restrict to actions at or after this moment, or null not to
     * @param toDate restrict to actions at or before this moment, or null not to
     * @param startIndex the row to start at, or null to start at the first
     * @param limit how many rows to return, or null for all of them
     * @return the matching observations
     * @throws org.openmrs.api.APIException if neither user is given
     */
    @Authorized(PrivilegeConstants.GET_OBS)
    List<Obs> getObsByAuditUser(User createdBy, User voidedBy, Date fromDate, Date toDate, Integer startIndex,
            Integer limit);

    /**
     * @see #getObsByAuditUser(User, User, Date, Date, Integer, Integer)
     * @return how many observations that search would return were it not paged
     */
    @Authorized(PrivilegeConstants.GET_OBS)
    Long getCountOfObsByAuditUser(User createdBy, User voidedBy, Date fromDate, Date toDate);

    /**
     * Encounters whose audit trail names the given user, or that name the given provider, most
     * recent action first. Voided encounters are included, for the same reason voided observations
     * are in {@link #getObsByAuditUser(User, User, Date, Date, Integer, Integer)}.
     *
     * <p>A date range bounds when the audit action happened rather than the encounter's own
     * datetime, and runs inclusively.
     *
     * @param createdBy restrict to encounters this user created, or null not to
     * @param changedBy restrict to encounters this user changed, or null not to
     * @param voidedBy restrict to encounters this user voided, or null not to
     * @param provider restrict to encounters this provider is recorded on, or null not to
     * @param encounterType restrict to encounters of this type, or null not to
     * @param fromDate restrict to actions at or after this moment, or null not to
     * @param toDate restrict to actions at or before this moment, or null not to
     * @param startIndex the row to start at, or null to start at the first
     * @param limit how many rows to return, or null for all of them
     * @return the matching encounters
     * @throws org.openmrs.api.APIException if no user and no provider is given
     */
    @Authorized(PrivilegeConstants.GET_ENCOUNTERS)
    List<Encounter> getEncountersByAuditUser(User createdBy, User changedBy, User voidedBy, Provider provider,
            EncounterType encounterType, Date fromDate, Date toDate, Integer startIndex, Integer limit);

    /**
     * @see #getEncountersByAuditUser(User, User, User, Provider, EncounterType, Date, Date, Integer, Integer)
     * @return how many encounters that search would return were it not paged
     */
    @Authorized(PrivilegeConstants.GET_ENCOUNTERS)
    Long getCountOfEncountersByAuditUser(User createdBy, User changedBy, User voidedBy, Provider provider,
            EncounterType encounterType, Date fromDate, Date toDate);

    void updateHealthCenter(Patient patient);

    // just for mocking during tests, don't use outside of test context
    void setConfig(Config config);
}
