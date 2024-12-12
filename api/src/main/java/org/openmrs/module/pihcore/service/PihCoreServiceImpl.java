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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.openmrs.Encounter;
import org.openmrs.Order;
import org.openmrs.Person;
import org.openmrs.api.OrderService;
import org.openmrs.api.context.Context;
import org.openmrs.api.db.OrderDAO;
import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.module.emrapi.adt.AdtService;
import org.openmrs.module.emrapi.adt.InpatientAdmission;
import org.openmrs.module.emrapi.adt.InpatientAdmissionSearchCriteria;
import org.openmrs.module.emrapi.adt.InpatientRequest;
import org.openmrs.module.emrapi.adt.InpatientRequestSearchCriteria;
import org.openmrs.module.emrapi.disposition.DispositionType;
import org.openmrs.module.emrapi.domainwrapper.DomainWrapperFactory;
import org.openmrs.module.pihcore.account.PihAccountDomainWrapper;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Service for PIH Core
 */
public class PihCoreServiceImpl extends BaseOpenmrsService implements PihCoreService, ApplicationContextAware {
	
	protected final Log log = LogFactory.getLog(this.getClass());

    private ApplicationContext applicationContext;
	private PihCoreDAO dao;
    private OrderDAO orderDAO;
    private DomainWrapperFactory domainWrapperFactory;


    /**
     * @see OrderService#getNextOrderNumberSeedSequenceValue()
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public synchronized Long getNextRadiologyOrderNumberSeedSequenceValue() {
        return dao.getNextRadiologyOrderNumberSeedSequenceValue();
    }

    /**
     * @see OrderDAO#saveOrder(Order)
     */
    @Transactional
    public Order saveOrder(Order order) {
        return orderDAO.saveOrder(order);
    }

    @Override
    @Transactional(readOnly = true)
    public PihAccountDomainWrapper newPihAccountDomainWrapper(Person person) {
        PihAccountDomainWrapper accountDomainWrapper = new PihAccountDomainWrapper();
        applicationContext.getAutowireCapableBeanFactory().autowireBean(accountDomainWrapper);
        accountDomainWrapper.initializeWithPerson(person);
        return accountDomainWrapper;
    }

    @Override
    @Transactional(readOnly = true)
    public List<InpatientAdmission> getStaleInpatientAdmissions(Date admittedOnOrBefore, int mostRecentEncounterThresholdInDays) {

        List<InpatientAdmission> staleInpatientAdmissions = new ArrayList<>();

        AdtService adtService = Context.getService(AdtService.class);

        List<InpatientAdmission> inpatientAdmissions = adtService.getInpatientAdmissions(new InpatientAdmissionSearchCriteria());

        for (InpatientAdmission inpatientAdmission : inpatientAdmissions) {
            Encounter admissionEncounter = inpatientAdmission.getFirstAdmissionOrTransferEncounter();
            if (admissionEncounter != null) {
                if (admissionEncounter.getEncounterDatetime().before(new DateTime(admittedOnOrBefore).plusDays(1).withTimeAtStartOfDay().toDate())) {
                    Duration timeSinceLastEncounter = new Duration(new DateTime(inpatientAdmission.getLatestEncounter().getEncounterDatetime()), new DateTime());
                    if (timeSinceLastEncounter.getStandardDays() >= mostRecentEncounterThresholdInDays) {
                        staleInpatientAdmissions.add(inpatientAdmission);
                    }
                }
            }
        }
        return staleInpatientAdmissions;
    }

    @Override
    @Transactional(readOnly = true)
    public List<InpatientRequest> getStaleAdmissionRequests(Date admissionRequestOnOrBefore, int mostRecentEncounterThresholdInDays) {

        List<InpatientRequest> staleInpatientRequests = new ArrayList<>();
        AdtService adtService = Context.getService(AdtService.class);

        InpatientRequestSearchCriteria searchCriteria = new InpatientRequestSearchCriteria();
        searchCriteria.addDispositionType(DispositionType.ADMIT);

        List<InpatientRequest> inpatientRequests = adtService.getInpatientRequests(searchCriteria);

        for (InpatientRequest inpatientRequest : inpatientRequests) {
            Encounter dispositionEncounter = inpatientRequest.getDispositionEncounter();
            if (dispositionEncounter != null) {
                if (dispositionEncounter.getEncounterDatetime().before(new DateTime(admissionRequestOnOrBefore).plusDays(1).withTimeAtStartOfDay().toDate())) {
                    Duration timeSinceLastEncounter = new Duration(new DateTime(inpatientRequest.getLatestEncounter().getEncounterDatetime()), new DateTime());
                    if (timeSinceLastEncounter.getStandardDays() >= mostRecentEncounterThresholdInDays) {
                        staleInpatientRequests.add(inpatientRequest);
                    }
                }
            }

        }
        return staleInpatientRequests;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    public void setDao(PihCoreDAO dao) {
        this.dao = dao;
    }

    public PihCoreDAO getDao() {
        return dao;
    }

    public void setOrderDAO(OrderDAO orderDAO) {
        this.orderDAO = orderDAO;
    }
}
