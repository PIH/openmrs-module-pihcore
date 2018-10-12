package org.openmrs.module.pihcore.reporting.dataset.manager;

import org.openmrs.Concept;
import org.openmrs.api.context.Context;
import org.openmrs.layout.address.AddressSupport;
import org.openmrs.module.addresshierarchy.AddressHierarchyLevel;
import org.openmrs.module.addresshierarchy.service.AddressHierarchyService;
import org.openmrs.module.pihcore.config.Config;
import org.openmrs.module.pihcore.metadata.Metadata;
import org.openmrs.module.pihcore.reporting.library.DataConverterLibrary;
import org.openmrs.module.pihcore.reporting.library.PihCohortDefinitionLibrary;
import org.openmrs.module.pihcore.reporting.library.PihObsDataLibrary;
import org.openmrs.module.pihcore.reporting.library.PihObsQueryLibrary;
import org.openmrs.module.pihcore.reporting.library.PihPatientDataLibrary;
import org.openmrs.module.pihcore.reporting.library.PihPersonDataLibrary;
import org.openmrs.module.reporting.common.MessageUtil;
import org.openmrs.module.reporting.common.ObjectUtil;
import org.openmrs.module.reporting.data.converter.DataConverter;
import org.openmrs.module.reporting.data.obs.definition.ObsDataDefinition;
import org.openmrs.module.reporting.data.obs.definition.PatientToObsDataDefinition;
import org.openmrs.module.reporting.data.obs.definition.PersonToObsDataDefinition;
import org.openmrs.module.reporting.data.patient.definition.PatientDataDefinition;
import org.openmrs.module.reporting.data.patient.library.BuiltInPatientDataLibrary;
import org.openmrs.module.reporting.data.person.definition.PersonDataDefinition;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.ObsDataSetDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.query.obs.definition.PatientObsQuery;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public abstract class BaseObsDataSetManager {

    @Autowired
    Config config;

    @Autowired
    DataConverterLibrary converters;

    @Autowired
    PihCohortDefinitionLibrary cohortQueries;

    @Autowired
    PihObsQueryLibrary obsQueries;

    @Autowired
    PihPatientDataLibrary pihPatientData;

    @Autowired
    PihPersonDataLibrary pihPersonData;

    @Autowired
    PihObsDataLibrary pihObsData;

    @Autowired
    BuiltInPatientDataLibrary builtInPatientData;



    public DataSetDefinition constructDataSet() {

        ObsDataSetDefinition dsd = new ObsDataSetDefinition();
        dsd.addParameter(new Parameter("startDate", "mirebalaisreports.parameter.startDate", Date.class));
        dsd.addParameter(new Parameter("endDate", "mirebalaisreports.parameter.endDate", Date.class));

        // Rows defined as patients who had an encounter of the configured types during the given period
        dsd.addRowFilter(Mapped.mapStraightThrough(new PatientObsQuery(cohortQueries.getExcludeTestPatients())));
        dsd.addRowFilter(Mapped.mapStraightThrough(obsQueries.getObsDuringPeriod(getQuestionConcepts())));

        // Define columns
        addPrimaryIdentifierColumns(dsd);
        addPatientNameColumns(dsd);
        addBirthDateAndAgeColumns(dsd);
        addGenderColumns(dsd);
        addCoreEncounterColumns(dsd);
        addAddressColumns(dsd);
        addPersonAttributeColumns(dsd);
        addObsColumns(dsd);

        return dsd;
    }

    /**
     * @return the encounter types that define what encounters make up the rows of this export
     */
    protected abstract List<Concept> getQuestionConcepts();

    /**
     * Add any name columns that are desired.  By default, the patient given, family, and nickname are included
     *
     */
    protected void addPrimaryIdentifierColumns(ObsDataSetDefinition dsd) {
        addColumn(dsd, "emr_id", pihPatientData.getPreferredPrimaryIdentifier());
    }

    /**
     * Add any name columns that are desired.  By default, the patient given, family, and nickname are included
     */
    protected void addPatientNameColumns(ObsDataSetDefinition dsd) {
        addColumn(dsd, "given_name", builtInPatientData.getPreferredGivenName());
        addColumn(dsd, "family_name", builtInPatientData.getPreferredFamilyName());
        addColumn(dsd, "nickname", builtInPatientData.getPreferredMiddleName());
    }

    /**
     * Add any birthdate columns that are desired
     */
    protected void addBirthDateAndAgeColumns(ObsDataSetDefinition dsd) {
        addColumn(dsd, "birthdate", pihPersonData.getBirthdate());
        addColumn(dsd, "birthdate_estimated", builtInPatientData.getBirthdateEstimated());
    }

    /**
     * Add any gender columns that are desired
     */
    protected void addGenderColumns(ObsDataSetDefinition dsd) {
        addColumn(dsd, "gender", builtInPatientData.getGender());
    }

    /**
     * Add columns that describe the common elements of encounters
     */
    protected void addCoreEncounterColumns(ObsDataSetDefinition dsd) {
        addColumn(dsd, "encounter_date", pihObsData.getEncounterDatetime());
        addColumn(dsd, "encounter_location", pihObsData.getLocationName());
        addColumn(dsd, "encounter_provider", pihObsData.getEncounterProvider());
    }

    protected void addOtherIdentifierColumns(ObsDataSetDefinition dsd) {
        addColumn(dsd, "biometrics_collected", pihPatientData.getHasBiometricsIdentifier());
    }

    /**
     * Add columns based on the configured person address, except for country
     */
    protected void addAddressColumns(ObsDataSetDefinition dsd) {
        Map<String, String> nameMappings = AddressSupport.getInstance().getDefaultLayoutTemplate().getNameMappings();
        List<AddressHierarchyLevel> levels = Context.getService(AddressHierarchyService.class).getAddressHierarchyLevels();
        for (AddressHierarchyLevel level : levels) {
            String addressProperty = level.getAddressField().getName();
            if (!"country".equals(addressProperty)) {
                String columnName = MessageUtil.translate(nameMappings.get(addressProperty), Locale.ENGLISH).toLowerCase().replace(" ", "_");
                addColumn(dsd, columnName, pihPersonData.getPreferredAddress(), converters.getAddressComponent(addressProperty));
            }
        }
    }

    /**
     *  Add columns for any person attributes to include
     */
    protected void addPersonAttributeColumns(ObsDataSetDefinition dsd) {
        addColumn(dsd, "telephone_number", pihPersonData.getTelephoneNumber());
        addColumn(dsd, "mothers_first_name", pihPersonData.getMothersFirstName());
    }

    /**
     * Adds the passed ObsDataDefinition as a column definition with the given columnName to the given dsd
     */
    protected void addColumn(ObsDataSetDefinition dsd, String columnName, ObsDataDefinition edd, DataConverter... converters) {
        dsd.addColumn(columnName.toUpperCase(), edd, ObjectUtil.toString(Mapped.straightThroughMappings(edd), "=", ","), converters);
    }

    /*
     * Adds the passed PatientDataDefinition as a column definition with the given columnName to the given dsd
     */
    protected void addColumn(ObsDataSetDefinition dsd, String columnName, PatientDataDefinition pdd, DataConverter... converters) {
        addColumn(dsd, columnName, new PatientToObsDataDefinition(pdd), converters);
    }

    /**
     * Adds the passed PersonDataDefinition as a column definition with the given columnName to the given dsd
     */
    protected void addColumn(ObsDataSetDefinition dsd, String columnName, PersonDataDefinition pdd, DataConverter... converters) {
        addColumn(dsd, columnName, new PersonToObsDataDefinition(pdd), converters);
    }

    /**
     *  Add columns for any obs to include
     */
    protected void addObsColumns(ObsDataSetDefinition dsd) {
        // Do not include any Obs by default
    }

    /**
     * Adds a column containing the value of an obs in the encounter, assuming a single observation exists for the given concept
     */
    protected void addObsColumn(ObsDataSetDefinition dsd, String columnName, String conceptLookup, DataConverter... converters) {
        Concept concept = Metadata.getConcept(conceptLookup);
        addColumn(dsd, columnName, pihObsData.getSingleObsInGroup(concept), converters);
    }


}
