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
import org.openmrs.layout.web.address.AddressSupport;
import org.openmrs.layout.web.address.AddressTemplate;
import org.openmrs.module.metadatadeploy.MetadataUtils;
import org.openmrs.module.pihcore.config.Config;
import org.openmrs.module.pihcore.deploy.bundle.core.concept.SocioEconomicConcepts;
import org.openmrs.module.pihcore.metadata.Metadata;
import org.openmrs.module.pihcore.metadata.core.EncounterTypes;
import org.openmrs.module.pihcore.reporting.library.DataConverterLibrary;
import org.openmrs.module.pihcore.reporting.library.PihEncounterDataLibrary;
import org.openmrs.module.pihcore.reporting.library.PihEncounterQueryLibrary;
import org.openmrs.module.pihcore.reporting.library.PihPatientDataLibrary;
import org.openmrs.module.pihcore.reporting.library.PihPersonDataLibrary;
import org.openmrs.module.reporting.common.MessageUtil;
import org.openmrs.module.reporting.data.converter.DataConverter;
import org.openmrs.module.reporting.data.encounter.library.BuiltInEncounterDataLibrary;
import org.openmrs.module.reporting.data.patient.library.BuiltInPatientDataLibrary;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.EncounterAndObsDataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.EncounterDataSetDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Data Export of patient registration data, limited to an optional period and location
 */
@Component
public class RegistrationDataSetManager extends BaseEncounterDataSetManager {

    @Autowired
    Config config;

    @Autowired
    DataConverterLibrary converters;

    @Autowired
    PihEncounterQueryLibrary encounterQueries;

    @Autowired
    BuiltInPatientDataLibrary builtInPatientData;

    @Autowired
    PihPersonDataLibrary pihPersonData;

    @Autowired
    PihPatientDataLibrary pihPatientData;

    @Autowired
    BuiltInEncounterDataLibrary builtInEncounterData;

    @Autowired
    PihEncounterDataLibrary pihEncounterData;

    public DataSetDefinition constructDataSet() {

        EncounterDataSetDefinition dsd = new EncounterDataSetDefinition();
        dsd.addParameter(getStartDateParameter());
        dsd.addParameter(getEndDateParameter());

        List<EncounterType> registrationTypes = new ArrayList<EncounterType>();
        registrationTypes.add(Metadata.lookup(EncounterTypes.PATIENT_REGISTRATION));

        // Rows defined as patients who had a registration encounter during the given period at the given location

        dsd.addRowFilter(Mapped.mapStraightThrough(encounterQueries.getEncountersDuringPeriodAtLocation(registrationTypes)));

        // Define columns

        //addColumn(dsd, "patient_id", builtInPatientData.getPatientId());
        addColumn(dsd, "emr_id", pihPatientData.getPreferredPrimaryIdentifier());

        // TODO: For now, commenting out the below, as they don't really seem useful to this export and aren't univerally applicable

        //addColumn(dsd, "emr_id_location", pihPatientData.getPreferredPrimaryIdentifierLocation());
        //addColumn(dsd, "num emr id", pihPatientData.getNumberOfPrimaryIdentifiers());
        //addColumn(dsd, "unknown_patient", pihPatientData.getUnknownPatient());

        addColumn(dsd, "given_name", builtInPatientData.getPreferredGivenName());
        addColumn(dsd, "family_name", builtInPatientData.getPreferredFamilyName());
        addColumn(dsd, "nickname", builtInPatientData.getPreferredMiddleName());
        addColumn(dsd, "birthdate", builtInPatientData.getBirthdate());
        addColumn(dsd, "birthdate_estimated", builtInPatientData.getBirthdateEstimated());
        addColumn(dsd, "gender", builtInPatientData.getGender());

        addColumn(dsd, "registration_date", builtInEncounterData.getEncounterDatetime());
        addColumn(dsd, "registration_location", builtInEncounterData.getLocationName());
        addColumn(dsd, "registered_by", pihEncounterData.getCreatorName());

        addColumn(dsd, "age_at_registration", pihEncounterData.getPatientAgeAtEncounter(), converters.getAgeToOneDecimalPlaceConverter());

        // Include configured person address columns, except for country

        AddressTemplate addressTemplate = AddressSupport.getInstance().getDefaultLayoutTemplate();
        for (Map.Entry<String, String> addressComponent : addressTemplate.getNameMappings().entrySet()) {
            String addressProperty = addressComponent.getKey();
            if (!"country".equals(addressProperty)) {
                String columnName = MessageUtil.translate(addressComponent.getValue(), Locale.ENGLISH).toLowerCase().replace(" ", "_");
                addColumn(dsd, columnName, pihPersonData.getPreferredAddress(), converters.getAddressComponent(addressProperty));
            }
        }

        addColumn(dsd, "telephone_number", pihPersonData.getTelephoneNumber());
        addColumn(dsd, "mothers_first_name", pihPersonData.getMothersFirstName());
        addColumn(dsd, "birthplace", pihPersonData.getBirthplace());

        addObsColumn(dsd, "civil_status", SocioEconomicConcepts.Concepts.CIVIL_STATUS, converters.getObsValueCodedNameConverter());
        addObsColumn(dsd, "occupation", SocioEconomicConcepts.Concepts.MAIN_ACTIVITY_NON_CODED, converters.getObsValueTextConverter());

        // TODO: Add in religion question here conditionally when appropriate

        // TODO: If we end up supporting 1+ contacts, will need to restructure this

        addObsColumn(dsd, "contact_person_name", SocioEconomicConcepts.Concepts.NAMES_AND_FIRSTNAMES_OF_CONTACT, converters.getObsValueTextConverter());
        addObsColumn(dsd, "contact_person_relationship", SocioEconomicConcepts.Concepts.RELATIONSHIPS_OF_CONTACT, converters.getObsValueTextConverter());
        addObsColumn(dsd, "contact_person_address", SocioEconomicConcepts.Concepts.ADDRESS_OF_CONTACT, converters.getObsValueTextConverter());
        addObsColumn(dsd, "contact_person_telephone", SocioEconomicConcepts.Concepts.TELEPHONE_NUMBER_OF_CONTACT, converters.getObsValueTextConverter());

        return dsd;
    }

    private void addObsColumn(EncounterDataSetDefinition dsd, String columnName, String conceptUuid, DataConverter... converters) {
        Concept concept = MetadataUtils.existing(Concept.class, conceptUuid);
        addColumn(dsd, columnName, pihEncounterData.getSingleObsInEncounter(concept), converters);
    }

}
