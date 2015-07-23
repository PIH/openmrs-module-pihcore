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
import org.openmrs.Visit;
import org.openmrs.api.ConceptService;
import org.openmrs.module.pihcore.config.Config;
import org.openmrs.module.pihcore.config.ConfigDescriptor;
import org.openmrs.module.pihcore.deploy.bundle.core.concept.SocioEconomicConcepts;
import org.openmrs.module.pihcore.metadata.Metadata;
import org.openmrs.module.pihcore.metadata.core.EncounterTypes;
import org.openmrs.module.reporting.dataset.definition.EncounterDataSetDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

/**
 * Data Export of patient registration data, limited to an optional period and location
 */
@Component
public class RegistrationDataSetManager extends BaseEncounterDataSetManager {

    @Autowired
    private Config config;

    @Autowired
    private ConceptService conceptService;

    @Override
    protected List<EncounterType> getEncounterTypes() {
        return Arrays.asList(Metadata.lookup(EncounterTypes.PATIENT_REGISTRATION));
    }

    @Override
    protected String getEncounterColumnPrefix() {
        return "registration";
    }

    @Override
    protected void addCoreVisitColumns(EncounterDataSetDefinition dsd) {
        // registration encounters are not associated with a visit, so override default core visit columns
    }

    @Override
    protected void addObsColumns(EncounterDataSetDefinition dsd) {

        // we collect full birthplace address hierarchy in Haiti,not elsewhere
        if (config != null && config.getCountry().equals(ConfigDescriptor.Country.HAITI)) {
            addObsColumn(dsd, "birthplace_country", SocioEconomicConcepts.Concepts.BIRTHPLACE_ADDRESS_CONSTRUCT,
                    converters.getObsFromObsGroupConverter(conceptService.getConceptByUuid(SocioEconomicConcepts.Concepts.COUNTRY)),
                    converters.getObsValueTextConverter());
            addObsColumn(dsd, "birthplace_department", SocioEconomicConcepts.Concepts.BIRTHPLACE_ADDRESS_CONSTRUCT,
                    converters.getObsFromObsGroupConverter(conceptService.getConceptByUuid(SocioEconomicConcepts.Concepts.STATE_PROVINCE)),
                    converters.getObsValueTextConverter());
            addObsColumn(dsd, "birthplace_commune", SocioEconomicConcepts.Concepts.BIRTHPLACE_ADDRESS_CONSTRUCT,
                    converters.getObsFromObsGroupConverter(conceptService.getConceptByUuid(SocioEconomicConcepts.Concepts.CITY_VILLAGE)),
                    converters.getObsValueTextConverter());
            addObsColumn(dsd, "birthplace_section_communale", SocioEconomicConcepts.Concepts.BIRTHPLACE_ADDRESS_CONSTRUCT,
                    converters.getObsFromObsGroupConverter(conceptService.getConceptByUuid(SocioEconomicConcepts.Concepts.ADDRESS_3)),
                    converters.getObsValueTextConverter());
            addObsColumn(dsd, "birthplace_locality", SocioEconomicConcepts.Concepts.BIRTHPLACE_ADDRESS_CONSTRUCT,
                    converters.getObsFromObsGroupConverter(conceptService.getConceptByUuid(SocioEconomicConcepts.Concepts.ADDRESS_1)),
                    converters.getObsValueTextConverter());
            addObsColumn(dsd, "birthplace_address", SocioEconomicConcepts.Concepts.BIRTHPLACE_ADDRESS_CONSTRUCT,
                    converters.getObsFromObsGroupConverter(conceptService.getConceptByUuid(SocioEconomicConcepts.Concepts.ADDRESS_2)),
                    converters.getObsValueTextConverter());
        }
        else {
            addObsColumn(dsd, "birthplace", SocioEconomicConcepts.Concepts.BIRTHPLACE, converters.getObsValueTextConverter());
        }

        addObsColumn(dsd, "civil_status", SocioEconomicConcepts.Concepts.CIVIL_STATUS, converters.getObsValueCodedNameConverter());
        addObsColumn(dsd, "occupation", SocioEconomicConcepts.Concepts.MAIN_ACTIVITY, converters.getObsValueCodedNameConverter());

        // Add in religion question here conditionally when appropriate
        if (config != null && config.getCountry().equals(ConfigDescriptor.Country.HAITI)) {
            addObsColumn(dsd, "religion", SocioEconomicConcepts.Concepts.RELIGION, converters.getObsValueCodedNameConverter());
        }

        // TODO: If we end up supporting 1+ contacts, will need to restructure this
        addObsColumn(dsd, "contact_person_name", SocioEconomicConcepts.Concepts.NAMES_AND_FIRSTNAMES_OF_CONTACT, converters.getObsValueTextConverter());
        addObsColumn(dsd, "contact_person_relationship", SocioEconomicConcepts.Concepts.RELATIONSHIPS_OF_CONTACT, converters.getObsValueTextConverter());
        addObsColumn(dsd, "contact_person_telephone", SocioEconomicConcepts.Concepts.TELEPHONE_NUMBER_OF_CONTACT, converters.getObsValueTextConverter());

        // we collect contact address hierarchy in Haiti,not elsewhere
        if (config != null && config.getCountry().equals(ConfigDescriptor.Country.HAITI)) {
            addObsColumn(dsd, "contact_person_country", SocioEconomicConcepts.Concepts.PATIENT_CONTACTS_CONSTRUCT,
                    converters.getObsFromObsGroupConverter(conceptService.getConceptByUuid(SocioEconomicConcepts.Concepts.COUNTRY)),
                    converters.getObsValueTextConverter());
            addObsColumn(dsd, "contact_person_department", SocioEconomicConcepts.Concepts.PATIENT_CONTACTS_CONSTRUCT,
                    converters.getObsFromObsGroupConverter(conceptService.getConceptByUuid(SocioEconomicConcepts.Concepts.STATE_PROVINCE)),
                    converters.getObsValueTextConverter());
            addObsColumn(dsd, "contact_person_commune",  SocioEconomicConcepts.Concepts.PATIENT_CONTACTS_CONSTRUCT,
                    converters.getObsFromObsGroupConverter(conceptService.getConceptByUuid(SocioEconomicConcepts.Concepts.CITY_VILLAGE)),
                    converters.getObsValueTextConverter());
            addObsColumn(dsd, "contact_person_section_communale",  SocioEconomicConcepts.Concepts.PATIENT_CONTACTS_CONSTRUCT,
                    converters.getObsFromObsGroupConverter(conceptService.getConceptByUuid(SocioEconomicConcepts.Concepts.ADDRESS_3)),
                    converters.getObsValueTextConverter());
            addObsColumn(dsd, "contact_person_locality",  SocioEconomicConcepts.Concepts.PATIENT_CONTACTS_CONSTRUCT,
                    converters.getObsFromObsGroupConverter(conceptService.getConceptByUuid(SocioEconomicConcepts.Concepts.ADDRESS_1)),
                    converters.getObsValueTextConverter());
            addObsColumn(dsd, "contact_person_address",  SocioEconomicConcepts.Concepts.PATIENT_CONTACTS_CONSTRUCT,
                    converters.getObsFromObsGroupConverter(conceptService.getConceptByUuid(SocioEconomicConcepts.Concepts.ADDRESS_2)),
                    converters.getObsValueTextConverter());
        }
        else {
            addObsColumn(dsd, "contact_person_address", SocioEconomicConcepts.Concepts.ADDRESS_OF_CONTACT, converters.getObsValueTextConverter());
        }

    }
}
