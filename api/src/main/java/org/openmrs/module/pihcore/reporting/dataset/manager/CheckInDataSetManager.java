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

import org.openmrs.EncounterType;
import org.openmrs.module.pihcore.PihEmrConfigConstants;
import org.openmrs.module.pihcore.metadata.Metadata;
import org.openmrs.module.reporting.dataset.definition.EncounterDataSetDefinition;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

/**
 * Data Export of patient check-in data, limited to an optional period and location
 *
 * TODO: currently used in Liberia, but Haiti uses old SQL export
 */
@Component
public class CheckInDataSetManager extends BaseEncounterDataSetManager {

    @Override
    protected List<EncounterType> getEncounterTypes() {
        return Arrays.asList(Metadata.lookupEncounterType(PihEmrConfigConstants.ENCOUNTERTYPE_CHECK_IN_UUID));
    }

    @Override
    protected String getEncounterColumnPrefix() {
        return "check_in";
    }

    @Override
    protected void addPatientNameColumns(EncounterDataSetDefinition dsd) {
        super.addPatientNameColumns(dsd);
    }

    @Override
    protected void addPersonAttributeColumns(EncounterDataSetDefinition dsd) {
        // Do not include person attributes in this export
    }

    @Override
    protected void addObsColumns(EncounterDataSetDefinition dsd) {
        addObsColumn(dsd, "type_of_visit", "PIH:REASON FOR VISIT", converters.getObsValueCodedNameConverter());
    }
}
