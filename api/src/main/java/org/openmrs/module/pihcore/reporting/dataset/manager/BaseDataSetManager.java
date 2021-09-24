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

package org.openmrs.module.pihcore.reporting.dataset.manager;

import org.openmrs.Location;
import org.openmrs.api.ConceptService;
import org.openmrs.api.EncounterService;
import org.openmrs.api.LocationService;
import org.openmrs.module.emrapi.adt.AdtService;
import org.openmrs.module.reporting.config.factory.DataSetFactory;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;

/**
 * Base implementation of a DataSetFactory that provides some common method implementations
 */
public abstract class BaseDataSetManager implements DataSetFactory {

    @Autowired
    protected EncounterService encounterService;

    @Autowired
    protected LocationService locationService;

    @Autowired
    protected ConceptService conceptService;

    @Autowired
    protected AdtService adtService;

    public Parameter getStartDateParameter() {
        return new Parameter("startDate", "mirebalaisreports.parameter.startDate", Date.class);
    }

    public Parameter getEndDateParameter() {
        return new Parameter("endDate", "mirebalaisreports.parameter.endDate", Date.class);
    }

    public Parameter getLocationParameter() {
        return new Parameter("location", "mirebalaisreports.parameter.location", Location.class);
    }

    public Parameter getDayParameter() {
        return new Parameter("day", "mirebalaisreports.parameter.day", Date.class);
    }
}
