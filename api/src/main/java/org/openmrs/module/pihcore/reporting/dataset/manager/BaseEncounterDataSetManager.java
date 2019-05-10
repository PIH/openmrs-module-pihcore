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
import org.openmrs.Visit;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;
import org.openmrs.layout.address.AddressSupport;
import org.openmrs.module.addresshierarchy.AddressField;
import org.openmrs.module.addresshierarchy.AddressHierarchyLevel;
import org.openmrs.module.addresshierarchy.service.AddressHierarchyService;
import org.openmrs.module.haiticore.metadata.HaitiPatientIdentifierTypes;
import org.openmrs.module.pihcore.config.Config;
import org.openmrs.module.pihcore.metadata.Metadata;
import org.openmrs.module.pihcore.reporting.encounter.definition.RetrospectiveEncounterDataDefinition;
import org.openmrs.module.pihcore.reporting.library.DataConverterLibrary;
import org.openmrs.module.pihcore.reporting.library.PihCohortDefinitionLibrary;
import org.openmrs.module.pihcore.reporting.library.PihEncounterDataLibrary;
import org.openmrs.module.pihcore.reporting.library.PihEncounterQueryLibrary;
import org.openmrs.module.pihcore.reporting.library.PihPatientDataLibrary;
import org.openmrs.module.pihcore.reporting.library.PihPersonDataLibrary;
import org.openmrs.module.reporting.common.MessageUtil;
import org.openmrs.module.reporting.common.ObjectUtil;
import org.openmrs.module.reporting.data.converter.BooleanConverter;
import org.openmrs.module.reporting.data.converter.DataConverter;
import org.openmrs.module.reporting.data.converter.PropertyConverter;
import org.openmrs.module.reporting.data.encounter.definition.EncounterDataDefinition;
import org.openmrs.module.reporting.data.encounter.definition.PatientToEncounterDataDefinition;
import org.openmrs.module.reporting.data.encounter.definition.PersonToEncounterDataDefinition;
import org.openmrs.module.reporting.data.encounter.library.BuiltInEncounterDataLibrary;
import org.openmrs.module.reporting.data.patient.definition.PatientDataDefinition;
import org.openmrs.module.reporting.data.patient.library.BuiltInPatientDataLibrary;
import org.openmrs.module.reporting.data.person.definition.PersonDataDefinition;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.EncounterDataSetDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.query.encounter.definition.PatientEncounterQuery;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Base implementation of a manager that produces row-per-encounter data set definitions
 */
public abstract class BaseEncounterDataSetManager {

	@Autowired
	Config config;

	@Autowired
	DataConverterLibrary converters;

	@Autowired
	PihCohortDefinitionLibrary cohortQueries;

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

	@Autowired
	PatientService patientService;

	public DataSetDefinition constructDataSet() {

		EncounterDataSetDefinition dsd = new EncounterDataSetDefinition();
		dsd.addParameter(new Parameter("startDate", "mirebalaisreports.parameter.startDate", Date.class));
		dsd.addParameter(new Parameter("endDate", "mirebalaisreports.parameter.endDate", Date.class));

		// Rows defined as patients who had an encounter of the configured types during the given period
		dsd.addRowFilter(Mapped.mapStraightThrough(new PatientEncounterQuery(cohortQueries.getExcludeTestPatients())));
		dsd.addRowFilter(Mapped.mapStraightThrough(encounterQueries.getEncountersDuringPeriodAtLocation(getEncounterTypes())));

		// Define columns
		addPrimaryIdentifierColumns(dsd);
		addPatientNameColumns(dsd);
		addBirthDateAndAgeColumns(dsd);
		addGenderColumns(dsd);
		addOtherIdentifierColumns(dsd);
		addCoreEncounterColumns(dsd);
        addCoreVisitColumns(dsd);
		addAddressColumns(dsd);
		addPersonAttributeColumns(dsd);
		addObsColumns(dsd);
		addAuditColumns(dsd);

		return dsd;
	}

	/**
	 * @return the encounter types that define what encounters make up the rows of this export
	 */
	protected abstract List<EncounterType> getEncounterTypes();

	/**
	 * @return the prefix for columns that describe the type of encounter (eg. registration)
	 */
	protected abstract String getEncounterColumnPrefix();

	/**
	 * Add any name columns that are desired.  By default, the patient given, family, and nickname are included
	 */
	protected void addPrimaryIdentifierColumns(EncounterDataSetDefinition dsd) {
		addColumn(dsd, "emr_id", pihPatientData.getPreferredPrimaryIdentifier());
	}

	/**
	 * Add any name columns that are desired.  By default, the patient given, family, and nickname are included
	 */
	protected void addPatientNameColumns(EncounterDataSetDefinition dsd) {
		addColumn(dsd, "given_name", builtInPatientData.getPreferredGivenName());
		addColumn(dsd, "family_name", builtInPatientData.getPreferredFamilyName());
		addColumn(dsd, "nickname", builtInPatientData.getPreferredMiddleName());
	}

	/**
	 * Add any birthdate columns that are desired
	 */
	protected void addBirthDateAndAgeColumns(EncounterDataSetDefinition dsd) {
		addColumn(dsd, "birthdate", pihPersonData.getBirthdate());
		addColumn(dsd, "birthdate_estimated", builtInPatientData.getBirthdateEstimated());
		addColumn(dsd, "age_at_"+getEncounterColumnPrefix(), pihEncounterData.getPatientAgeAtEncounter(), converters.getAgeToOneDecimalPlaceConverter());
	}

	/**
	 * Add any gender columns that are desired
	 */
	protected void addGenderColumns(EncounterDataSetDefinition dsd) {
		addColumn(dsd, "gender", builtInPatientData.getGender());
	}

	protected void addOtherIdentifierColumns(EncounterDataSetDefinition dsd) {
		// slight hack, only biometrics on systems where that identifier is present
		if (patientService.getPatientIdentifierTypeByUuid(HaitiPatientIdentifierTypes.BIOMETRIC_REF_NUMBER.uuid()) != null) {
			addColumn(dsd, "biometrics_collected", pihPatientData.getHasBiometricsIdentifier());
		}
	}

	/**
	 * Add columns that describe the common elements of encounters
	 */
	protected void addCoreEncounterColumns(EncounterDataSetDefinition dsd) {
		addColumn(dsd, getEncounterColumnPrefix()+ "_date", builtInEncounterData.getEncounterDatetime());
		addColumn(dsd, getEncounterColumnPrefix()+"_location", builtInEncounterData.getLocationName());
		addColumn(dsd, getEncounterColumnPrefix()+"_provider", pihEncounterData.getEncounterProvider());
        addColumn(dsd, getEncounterColumnPrefix()+"_retrospective", new RetrospectiveEncounterDataDefinition(), new BooleanConverter());
	}

    protected void addCoreVisitColumns(EncounterDataSetDefinition dsd) {
        addColumn(dsd, getEncounterColumnPrefix()+"_visit", builtInEncounterData.getEncounterVisit(), new PropertyConverter(Visit.class, "id"));
    }

    /**
	 * Add columns that describe the information about when the encounter was entered
	 */
	protected void addAuditColumns(EncounterDataSetDefinition dsd) {
		addColumn(dsd, "date_created", pihEncounterData.getDateCreated());
		addColumn(dsd, "created_by", pihEncounterData.getCreatorName());
	}

	/**
	 * Add columns based on the configured person address, except for country
	 */
	protected void addAddressColumns(EncounterDataSetDefinition dsd) {
		Map<String, String> nameMappings = AddressSupport.getInstance().getDefaultLayoutTemplate().getNameMappings();
		List<AddressHierarchyLevel> levels = Context.getService(AddressHierarchyService.class).getAddressHierarchyLevels();
		for (AddressHierarchyLevel level : levels) {
			if (level == null) continue;
			AddressField addressField = level.getAddressField();
			if (addressField == null) continue;
			String addressProperty = addressField.getName();
			if (!"country".equals(addressProperty)) {
				String addressPropertyTranslated = MessageUtil.translate(nameMappings.get(addressProperty), Locale.ENGLISH);
				String columnName = addressPropertyTranslated != null ?
						addressPropertyTranslated.toLowerCase().replace(" ", "_") :
						addressProperty.toLowerCase().replace(" ", "_");
				addColumn(dsd, columnName, pihPersonData.getPreferredAddress(), converters.getAddressComponent(addressProperty));
			}
		}
	}

	/**
	 *  Add columns for any person attributes to include
	 */
	protected void addPersonAttributeColumns(EncounterDataSetDefinition dsd) {
		addColumn(dsd, "telephone_number", pihPersonData.getTelephoneNumber());
		addColumn(dsd, "mothers_first_name", pihPersonData.getMothersFirstName());
	}

	/**
	 *  Add columns for any obs to include
	 */
	protected void addObsColumns(EncounterDataSetDefinition dsd) {
		// Do not include any Obs by default
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

	/**
	 * Adds a column containing the value of an obs in the encounter, assuming a single observation exists for the given concept
	 */
	protected void addObsColumn(EncounterDataSetDefinition dsd, String columnName, String conceptLookup, DataConverter... converters) {
		Concept concept = Metadata.getConcept(conceptLookup);
		addColumn(dsd, columnName, pihEncounterData.getSingleObsInEncounter(concept), converters);
	}
}
