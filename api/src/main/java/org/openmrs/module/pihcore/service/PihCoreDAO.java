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

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.LockOptions;
import org.hibernate.criterion.Restrictions;
import org.openmrs.GlobalProperty;
import org.openmrs.api.APIException;
import org.openmrs.api.db.OrderDAO;
import org.openmrs.api.db.hibernate.DbSessionFactory;
import org.openmrs.module.pihcore.PihCoreConstants;

/**
 * Data Access Layer for PihCore
 */
public class PihCoreDAO {

    protected final Log log = LogFactory.getLog(this.getClass());

    private DbSessionFactory sessionFactory;

    /**
     * @see OrderDAO#getNextOrderNumberSeedSequenceValue() ()
     */
    public Long getNextRadiologyOrderNumberSeedSequenceValue() {
        String gp = PihCoreConstants.GP_NEXT_RADIOLOGY_ORDER_NUMBER_SEED;
        Criteria searchCriteria = sessionFactory.getCurrentSession().createCriteria(GlobalProperty.class);
        searchCriteria.add(Restrictions.eq("property", gp));
        GlobalProperty globalProperty = (GlobalProperty) sessionFactory.getCurrentSession().get(GlobalProperty.class, gp, LockOptions.UPGRADE);

        if (globalProperty == null) {
            throw new APIException("Missing global property named: " + gp);
        }

        String gpTextValue = globalProperty.getPropertyValue();
        if (StringUtils.isBlank(gpTextValue)) {
            throw new APIException("Invalid value for global property named: " + gp);
        }

        Long gpNumericValue;
        try {
            gpNumericValue = Long.parseLong(gpTextValue);
        } catch (NumberFormatException ex) {
            throw new APIException("Invalid value for global property named: " + gp);
        }

        globalProperty.setPropertyValue(String.valueOf(gpNumericValue + 1));

        sessionFactory.getCurrentSession().save(globalProperty);

        return gpNumericValue;
    }

    public void setSessionFactory(DbSessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public DbSessionFactory getSessionFactory() {
        return sessionFactory;
    }
}