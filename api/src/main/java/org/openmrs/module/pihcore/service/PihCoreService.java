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
import org.openmrs.Person;
import org.openmrs.api.OpenmrsService;
import org.openmrs.api.db.OrderDAO;
import org.openmrs.module.pihcore.account.PihAccountDomainWrapper;
import org.springframework.transaction.annotation.Transactional;

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

    /**
     * @return a new bean instance of a PihAccountDomainWrapper
     */
    PihAccountDomainWrapper newPihAccountDomainWrapper(Person person);
}
