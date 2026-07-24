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

package org.openmrs.module.pihcore.reporting.query.encounter.definition;

import org.openmrs.Encounter;
import org.openmrs.Location;
import org.openmrs.module.reporting.definition.configuration.ConfigurationProperty;
import org.openmrs.module.reporting.query.BaseQuery;
import org.openmrs.module.reporting.query.encounter.definition.EncounterQuery;

/**
 * Filters encounters to those whose associated Visit has the given Location.
 * If visitLocation is null, no filtering is applied.
 */
public class EncounterVisitLocationQuery extends BaseQuery<Encounter> implements EncounterQuery {

	public static final long serialVersionUID = 1L;

	@ConfigurationProperty
	private Location visitLocation;

	public Location getVisitLocation() {
		return visitLocation;
	}

	public void setVisitLocation(Location visitLocation) {
		this.visitLocation = visitLocation;
	}
}
