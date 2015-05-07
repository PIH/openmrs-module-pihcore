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

import org.openmrs.Location;
import org.openmrs.module.reporting.cohort.definition.BaseCohortDefinition;
import org.openmrs.module.reporting.definition.configuration.ConfigurationProperty;
import org.openmrs.module.reporting.definition.configuration.ConfigurationPropertyCachingStrategy;
import org.openmrs.module.reporting.evaluation.caching.Caching;

import java.util.Date;

@Caching(strategy=ConfigurationPropertyCachingStrategy.class)
public class InpatientTransferCohortDefinition extends BaseCohortDefinition {

    public static final long serialVersionUID = 1L;

    @ConfigurationProperty
    private Location outOfWard;

    @ConfigurationProperty
    private Location inToWard;

    @ConfigurationProperty(required = true)
    private Date onOrAfter;

    @ConfigurationProperty(required = true)
    private Date onOrBefore;

    public InpatientTransferCohortDefinition() {
    }

    public Location getOutOfWard() {
        return outOfWard;
    }

    public void setOutOfWard(Location outOfWard) {
        this.outOfWard = outOfWard;
    }

    public Location getInToWard() {
        return inToWard;
    }

    public void setInToWard(Location inToWard) {
        this.inToWard = inToWard;
    }

    public Date getOnOrAfter() {
        return onOrAfter;
    }

    public void setOnOrAfter(Date onOrAfter) {
        this.onOrAfter = onOrAfter;
    }

    public Date getOnOrBefore() {
        return onOrBefore;
    }

    public void setOnOrBefore(Date onOrBefore) {
        this.onOrBefore = onOrBefore;
    }

}
