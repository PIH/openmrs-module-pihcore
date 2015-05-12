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
import org.openmrs.module.reporting.common.ObjectUtil;
import org.openmrs.module.reporting.data.converter.DataConverter;
import org.openmrs.module.reporting.data.encounter.definition.EncounterDataDefinition;
import org.openmrs.module.reporting.data.encounter.definition.PatientToEncounterDataDefinition;
import org.openmrs.module.reporting.data.encounter.definition.PersonToEncounterDataDefinition;
import org.openmrs.module.reporting.data.patient.definition.PatientDataDefinition;
import org.openmrs.module.reporting.data.person.definition.PersonDataDefinition;
import org.openmrs.module.reporting.dataset.definition.EncounterDataSetDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.evaluation.parameter.Parameterizable;
import org.openmrs.module.reporting.evaluation.parameter.ParameterizableUtil;

import java.util.Date;

/**
 * Base implementation of a manager that produces row-per-encounter data set definitions
 */
public abstract class BaseEncounterDataSetManager {

	/**
	 * @return a parameter representing start date
	 */
	public Parameter getStartDateParameter() {
		return new Parameter("startDate", "mirebalaisreports.parameter.startDate", Date.class);
	}

	/**
	 * @return a parameter representing end date
	 */
	public Parameter getEndDateParameter() {
		return new Parameter("endDate", "mirebalaisreports.parameter.endDate", Date.class);
	}

	/**
	 * @return a parameter representing location
	 */
	public Parameter getLocationParameter() {
		return new Parameter("location", "mirebalaisreports.parameter.location", Location.class);
	}

	/**
	 * Adds the passed EncounterDataDefinition as a column definition with the given columnName to the given dsd
	 */
	protected void addColumn(EncounterDataSetDefinition dsd, String columnName, EncounterDataDefinition edd, DataConverter... converters) {
		dsd.addColumn(columnName.toUpperCase(), edd, ObjectUtil.toString(Mapped.straightThroughMappings(edd), "=", ","), converters);
	}

	/**
	 * Adds the passed PatientDataDefinition as a column definition with the given columnName to the given dsd
	 */
	protected void addColumn(EncounterDataSetDefinition dsd, String columnName, PatientDataDefinition pdd, DataConverter... converters) {
		addColumn(dsd, columnName, new PatientToEncounterDataDefinition(pdd), converters);
	}

	/**
	 * Adds the passed PersonDataDefinition as a column definition with the given columnName to the given dsd
	 */
	protected void addColumn(EncounterDataSetDefinition dsd, String columnName, PersonDataDefinition pdd, DataConverter... converters) {
		addColumn(dsd, columnName, new PersonToEncounterDataDefinition(pdd), converters);
	}

    public <T extends Parameterizable> Mapped<T> map(T parameterizable, String mappings) {
        if (parameterizable == null) {
            throw new NullPointerException("Programming error: missing parameterizable");
        }
        if (mappings == null) {
            mappings = ""; // probably not necessary, just to be safe
        }
        return new Mapped<T>(parameterizable, ParameterizableUtil.createParameterMappings(mappings));
    }
}
