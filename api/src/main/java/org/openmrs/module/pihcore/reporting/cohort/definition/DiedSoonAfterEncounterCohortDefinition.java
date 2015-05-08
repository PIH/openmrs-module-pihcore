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

package org.openmrs.module.pihcore.reporting.cohort.definition;

import org.openmrs.EncounterType;
import org.openmrs.module.reporting.cohort.definition.BaseCohortDefinition;
import org.openmrs.module.reporting.definition.configuration.ConfigurationProperty;
import org.openmrs.module.reporting.definition.configuration.ConfigurationPropertyCachingStrategy;
import org.openmrs.module.reporting.evaluation.caching.Caching;

import java.util.Date;

/**
 *
 */
@Caching(strategy=ConfigurationPropertyCachingStrategy.class)
public class DiedSoonAfterEncounterCohortDefinition extends BaseCohortDefinition {

    public static final long serialVersionUID = 1L;

    @ConfigurationProperty(required = true)
    private Date diedOnOrAfter;

    @ConfigurationProperty(required = true)
    private Date diedOnOrBefore;

    @ConfigurationProperty(required = true)
    private EncounterType encounterType;

    @ConfigurationProperty(required = true)
    private Integer windowInHours = 48;

    public Date getDiedOnOrAfter() {
        return diedOnOrAfter;
    }

    public void setDiedOnOrAfter(Date diedOnOrAfter) {
        this.diedOnOrAfter = diedOnOrAfter;
    }

    public Date getDiedOnOrBefore() {
        return diedOnOrBefore;
    }

    public void setDiedOnOrBefore(Date diedOnOrBefore) {
        this.diedOnOrBefore = diedOnOrBefore;
    }

    public EncounterType getEncounterType() {
        return encounterType;
    }

    public void setEncounterType(EncounterType encounterType) {
        this.encounterType = encounterType;
    }

    public Integer getWindowInHours() {
        return windowInHours;
    }

    public void setWindowInHours(Integer windowInHours) {
        this.windowInHours = windowInHours;
    }

}
