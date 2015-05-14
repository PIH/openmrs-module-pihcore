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

import org.openmrs.Concept;
import org.openmrs.EncounterType;
import org.openmrs.module.metadatadeploy.MetadataUtils;
import org.openmrs.module.pihcore.deploy.bundle.core.concept.SocioEconomicConcepts;
import org.openmrs.module.pihcore.metadata.Metadata;
import org.openmrs.module.pihcore.metadata.core.EncounterTypes;
import org.openmrs.module.reporting.data.converter.DataConverter;
import org.openmrs.module.reporting.dataset.definition.EncounterDataSetDefinition;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

/**
 * Data Export of patient registration data, limited to an optional period and location
 */
@Component
public class RegistrationDataSetManager extends BaseEncounterDataSetManager {

    @Override
    protected List<EncounterType> getEncounterTypes() {
        return Arrays.asList(Metadata.lookup(EncounterTypes.PATIENT_REGISTRATION));
    }

    @Override
    protected String getEncounterColumnPrefix() {
        return "registration";
    }

    @Override
    protected void addObsColumns(EncounterDataSetDefinition dsd) {
        addObsColumn(dsd, "civil_status", SocioEconomicConcepts.Concepts.CIVIL_STATUS, converters.getObsValueCodedNameConverter());
        addObsColumn(dsd, "occupation", SocioEconomicConcepts.Concepts.MAIN_ACTIVITY_NON_CODED, converters.getObsValueTextConverter());

        // TODO: Add in religion question here conditionally when appropriate

        // TODO: If we end up supporting 1+ contacts, will need to restructure this

        addObsColumn(dsd, "contact_person_name", SocioEconomicConcepts.Concepts.NAMES_AND_FIRSTNAMES_OF_CONTACT, converters.getObsValueTextConverter());
        addObsColumn(dsd, "contact_person_relationship", SocioEconomicConcepts.Concepts.RELATIONSHIPS_OF_CONTACT, converters.getObsValueTextConverter());
        addObsColumn(dsd, "contact_person_address", SocioEconomicConcepts.Concepts.ADDRESS_OF_CONTACT, converters.getObsValueTextConverter());
        addObsColumn(dsd, "contact_person_telephone", SocioEconomicConcepts.Concepts.TELEPHONE_NUMBER_OF_CONTACT, converters.getObsValueTextConverter());
    }
}
