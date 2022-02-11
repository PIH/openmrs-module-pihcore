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
import org.openmrs.Person;
import org.openmrs.api.OrderService;
import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.module.pihcore.account.PihAccountDomainWrapper;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service for PIH Core
 */
public class PihCoreServiceImpl extends BaseOpenmrsService implements PihCoreService, ApplicationContextAware {
	
	protected final Log log = LogFactory.getLog(this.getClass());

    private ApplicationContext applicationContext;
	private PihCoreDAO dao;

    /**
     * @see OrderService#getNextOrderNumberSeedSequenceValue()
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public synchronized Long getNextRadiologyOrderNumberSeedSequenceValue() {
        return dao.getNextRadiologyOrderNumberSeedSequenceValue();
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
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    public void setDao(PihCoreDAO dao) {
        this.dao = dao;
    }

    public PihCoreDAO getDao() {
        return dao;
    }
}
