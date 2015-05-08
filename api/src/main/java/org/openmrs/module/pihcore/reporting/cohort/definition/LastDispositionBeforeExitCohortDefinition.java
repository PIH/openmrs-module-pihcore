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

import org.openmrs.Concept;
import org.openmrs.Location;
import org.openmrs.module.reporting.cohort.definition.BaseCohortDefinition;
import org.openmrs.module.reporting.definition.configuration.ConfigurationProperty;
import org.openmrs.module.reporting.definition.configuration.ConfigurationPropertyCachingStrategy;
import org.openmrs.module.reporting.evaluation.caching.Caching;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Checks whether the last disposition in the visit before the exit encounter has the given value. (Note that we
 * look for the most recent disposition in the same visit, but not necessarily between exitOnOrAfter and exitOnOrBefore.)
 *
 */
@Caching(strategy=ConfigurationPropertyCachingStrategy.class)
public class LastDispositionBeforeExitCohortDefinition extends BaseCohortDefinition {

    public static final long serialVersionUID = 1L;

    @ConfigurationProperty(required = true)
    private List<Concept> dispositions;

    @ConfigurationProperty
    private List<Concept> dispositionsToConsider;

    @ConfigurationProperty(required = true)
    private Date exitOnOrAfter;

    @ConfigurationProperty(required = true)
    private Date exitOnOrBefore;

    @ConfigurationProperty
    private Location exitFromWard;

    public List<Concept> getDispositions() {
        return dispositions;
    }

    public void setDispositions(List<Concept> dispositions) {
        this.dispositions = dispositions;
    }

    public List<Concept> getDispositionsToConsider() {
        return dispositionsToConsider;
    }

    public void setDispositionsToConsider(List<Concept> dispositionsToConsider) {
        this.dispositionsToConsider = dispositionsToConsider;
    }

    public Date getExitOnOrAfter() {
        return exitOnOrAfter;
    }

    public void setExitOnOrAfter(Date exitOnOrAfter) {
        this.exitOnOrAfter = exitOnOrAfter;
    }

    public Date getExitOnOrBefore() {
        return exitOnOrBefore;
    }

    public void setExitOnOrBefore(Date exitOnOrBefore) {
        this.exitOnOrBefore = exitOnOrBefore;
    }

    public Location getExitFromWard() {
        return exitFromWard;
    }

    public void setExitFromWard(Location exitFromWard) {
        this.exitFromWard = exitFromWard;
    }

    public void addDisposition(Concept disposition) {
        if (dispositions == null) {
            dispositions = new ArrayList<Concept>();
        }
        dispositions.add(disposition);
    }

}
