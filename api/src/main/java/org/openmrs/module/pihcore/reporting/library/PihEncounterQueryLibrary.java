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
package org.openmrs.module.pihcore.reporting.library;

import org.openmrs.EncounterType;
import org.openmrs.Location;
import org.openmrs.module.reporting.common.ObjectUtil;
import org.openmrs.module.reporting.common.TimeQualifier;
import org.openmrs.module.reporting.definition.library.BaseDefinitionLibrary;
import org.openmrs.module.reporting.definition.library.DocumentedDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.query.encounter.definition.BasicEncounterQuery;
import org.openmrs.module.reporting.query.encounter.definition.EncounterQuery;
import org.openmrs.module.reporting.query.encounter.definition.MappedParametersEncounterQuery;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

@Component
public class PihEncounterQueryLibrary extends BaseDefinitionLibrary<EncounterQuery> {

    public static final String PREFIX = "pihcore.encounterQuery.";

    @Override
    public Class<? super EncounterQuery> getDefinitionType() {
        return EncounterQuery.class;
    }

    @Override
    public String getKeyPrefix() {
        return PREFIX;
    }

    @DocumentedDefinition(value = "encountersDuringPeriodAtLocation")
    public EncounterQuery getEncountersDuringPeriodAtLocation(List<EncounterType> encounterTypes) {
        BasicEncounterQuery q = new BasicEncounterQuery();
        q.setWhich(TimeQualifier.ANY);
        q.setEncounterTypes(encounterTypes);
        q.addParameter(parameter(Date.class, "onOrAfter"));
        q.addParameter(parameter(Date.class, "onOrBefore"));
        q.addParameter(parameter(Location.class, "locationList"));
        return new MappedParametersEncounterQuery(q, ObjectUtil.toMap("onOrAfter=startDate,onOrBefore=endDate,locationList=location"));
    }

    public Parameter parameter(Class<?> clazz, String name) {
        return new Parameter(name, "mirebalaisreports.parameter." + name, clazz);
    }
}
