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
import org.openmrs.api.ConceptService;
import org.openmrs.module.pihcore.PihEmrConfigConstants;
import org.openmrs.module.pihcore.config.Config;
import org.openmrs.module.pihcore.config.ConfigDescriptor;
import org.openmrs.module.pihcore.metadata.Metadata;
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

    public static final class Concepts {
        public static final String HAITI_INSURANCE_COMPANY_NAME = "c9131281-e086-41a1-a8cb-4cce9fdefe05";
        public static final String INSURANCE_POLICY_NUMBER = "762fde47-e001-43bd-a808-154452d24cb4";
        public static final String OTHER_INSURANCE_COMPANY_NAME = "ce581a9c-433f-43f4-8a03-58cc83af5e3d";
        public static final String CIVIL_STATUS = "3cd6df26-26fe-102b-80cb-0017a47871b2";
        public static final String MAIN_ACTIVITY = "3cd97286-26fe-102b-80cb-0017a47871b2";
        public static final String MAIN_ACTIVITY_NON_CODED = "3ce41a38-26fe-102b-80cb-0017a47871b2";
        public static final String URBAN = "c50d9651-f76d-4b95-a6cd-6525608852e3";
        public static final String RURAL = "1b27978f-e175-464f-82a5-ac8fc4e7155c";
        public static final String PATIENT_CONTACTS_CONSTRUCT = "3cd9936a-26fe-102b-80cb-0017a47871b2";
        public static final String NAMES_AND_FIRSTNAMES_OF_CONTACT = "3cd997f2-26fe-102b-80cb-0017a47871b2";
        public static final String RELATIONSHIPS_OF_CONTACT = "3cd99a68-26fe-102b-80cb-0017a47871b2";
        public static final String TELEPHONE_NUMBER_OF_CONTACT = "276f8057-55a4-4b1c-8915-69ad090fcffb";
        public static final String ADDRESS_OF_CONTACT = "5190CC3E-83F0-4410-8660-B109086D9A5E";
        public static final String MARRIED = "3cee0aca-26fe-102b-80cb-0017a47871b2";
        public static final String RELIGION = "162929AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
        public static final String BIRTHPLACE = "0b192340-3eb5-4597-8cea-d33c182fc79c";
        public static final String BIRTHPLACE_ADDRESS_CONSTRUCT = "43f20178-4aa3-43e8-99c1-a67b12994cc8";
        public static final String COUNTRY = "b9cf87e0-bf63-43cd-888d-a907d8af47a1";
        public static final String STATE_PROVINCE = "717f4cff-a44d-49b4-9a74-0bc2ae466870";
        public static final String CITY_VILLAGE = "d3dad53c-9d33-476d-9997-b91d39831104";
        public static final String ADDRESS_1 = "f2715dfe-dbf7-4baf-80e6-07f6af0f16a9";
        public static final String ADDRESS_2 = "1cdbfb42-3d46-4201-b355-f94adece8a5a";
        public static final String ADDRESS_3 = "8203294b-e1ff-4b3e-89de-e0b8fa5e2928";
    }

    @Autowired
    private Config config;

    @Autowired
    private ConceptService conceptService;

    @Override
    protected List<EncounterType> getEncounterTypes() {
        return Arrays.asList(Metadata.lookupEncounterType(PihEmrConfigConstants.ENCOUNTERTYPE_PATIENT_REGISTRATION_UUID));
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

            addObsColumn(dsd, "insurance_company", Concepts.HAITI_INSURANCE_COMPANY_NAME, converters.getObsValueCodedNameConverter());
            addObsColumn(dsd, "insurance_policy_number", Concepts.INSURANCE_POLICY_NUMBER, converters.getObsValueTextConverter());
            addObsColumn(dsd, "insurance_company_other", Concepts.OTHER_INSURANCE_COMPANY_NAME, converters.getObsValueTextConverter());

            addObsColumn(dsd, "birthplace_country", Concepts.BIRTHPLACE_ADDRESS_CONSTRUCT,
                    converters.getObsFromObsGroupConverter(conceptService.getConceptByUuid(Concepts.COUNTRY)),
                    converters.getObsValueTextConverter());
            addObsColumn(dsd, "birthplace_department", Concepts.BIRTHPLACE_ADDRESS_CONSTRUCT,
                    converters.getObsFromObsGroupConverter(conceptService.getConceptByUuid(Concepts.STATE_PROVINCE)),
                    converters.getObsValueTextConverter());
            addObsColumn(dsd, "birthplace_commune", Concepts.BIRTHPLACE_ADDRESS_CONSTRUCT,
                    converters.getObsFromObsGroupConverter(conceptService.getConceptByUuid(Concepts.CITY_VILLAGE)),
                    converters.getObsValueTextConverter());
            addObsColumn(dsd, "birthplace_section_communale", Concepts.BIRTHPLACE_ADDRESS_CONSTRUCT,
                    converters.getObsFromObsGroupConverter(conceptService.getConceptByUuid(Concepts.ADDRESS_3)),
                    converters.getObsValueTextConverter());
            addObsColumn(dsd, "birthplace_locality", Concepts.BIRTHPLACE_ADDRESS_CONSTRUCT,
                    converters.getObsFromObsGroupConverter(conceptService.getConceptByUuid(Concepts.ADDRESS_1)),
                    converters.getObsValueTextConverter());
            addObsColumn(dsd, "birthplace_address", Concepts.BIRTHPLACE_ADDRESS_CONSTRUCT,
                    converters.getObsFromObsGroupConverter(conceptService.getConceptByUuid(Concepts.ADDRESS_2)),
                    converters.getObsValueTextConverter());
        }
        else {
            addObsColumn(dsd, "birthplace", Concepts.BIRTHPLACE, converters.getObsValueTextConverter());
        }

        addObsColumn(dsd, "civil_status", Concepts.CIVIL_STATUS, converters.getObsValueCodedNameConverter());
        addObsColumn(dsd, "occupation", Concepts.MAIN_ACTIVITY, converters.getObsValueCodedNameConverter());

        // Add in religion question here conditionally when appropriate
        if (config != null && config.getCountry().equals(ConfigDescriptor.Country.HAITI)) {
            addObsColumn(dsd, "religion", Concepts.RELIGION, converters.getObsValueCodedNameConverter());
        }

        // TODO: If we end up supporting 1+ contacts, will need to restructure this
        addObsColumn(dsd, "contact_person_name", Concepts.NAMES_AND_FIRSTNAMES_OF_CONTACT, converters.getObsValueTextConverter());
        addObsColumn(dsd, "contact_person_relationship", Concepts.RELATIONSHIPS_OF_CONTACT, converters.getObsValueTextConverter());
        addObsColumn(dsd, "contact_person_telephone", Concepts.TELEPHONE_NUMBER_OF_CONTACT, converters.getObsValueTextConverter());

        // we collect contact address hierarchy in Haiti,not elsewhere
        if (config != null && config.getCountry().equals(ConfigDescriptor.Country.HAITI)) {
            addObsColumn(dsd, "contact_person_country", Concepts.PATIENT_CONTACTS_CONSTRUCT,
                    converters.getObsFromObsGroupConverter(conceptService.getConceptByUuid(Concepts.COUNTRY)),
                    converters.getObsValueTextConverter());
            addObsColumn(dsd, "contact_person_department", Concepts.PATIENT_CONTACTS_CONSTRUCT,
                    converters.getObsFromObsGroupConverter(conceptService.getConceptByUuid(Concepts.STATE_PROVINCE)),
                    converters.getObsValueTextConverter());
            addObsColumn(dsd, "contact_person_commune",  Concepts.PATIENT_CONTACTS_CONSTRUCT,
                    converters.getObsFromObsGroupConverter(conceptService.getConceptByUuid(Concepts.CITY_VILLAGE)),
                    converters.getObsValueTextConverter());
            addObsColumn(dsd, "contact_person_section_communale",  Concepts.PATIENT_CONTACTS_CONSTRUCT,
                    converters.getObsFromObsGroupConverter(conceptService.getConceptByUuid(Concepts.ADDRESS_3)),
                    converters.getObsValueTextConverter());
            addObsColumn(dsd, "contact_person_locality",  Concepts.PATIENT_CONTACTS_CONSTRUCT,
                    converters.getObsFromObsGroupConverter(conceptService.getConceptByUuid(Concepts.ADDRESS_1)),
                    converters.getObsValueTextConverter());
            addObsColumn(dsd, "contact_person_address",  Concepts.PATIENT_CONTACTS_CONSTRUCT,
                    converters.getObsFromObsGroupConverter(conceptService.getConceptByUuid(Concepts.ADDRESS_2)),
                    converters.getObsValueTextConverter());
        }
        else {
            addObsColumn(dsd, "contact_person_address", Concepts.ADDRESS_OF_CONTACT, converters.getObsValueTextConverter());
        }

    }
}
