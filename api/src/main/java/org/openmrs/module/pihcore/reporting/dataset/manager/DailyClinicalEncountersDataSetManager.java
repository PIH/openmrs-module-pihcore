package org.openmrs.module.pihcore.reporting.dataset.manager;

import org.openmrs.EncounterType;
import org.openmrs.Location;
import org.openmrs.module.pihcore.PihEmrConfigConstants;
import org.openmrs.module.pihcore.reporting.library.PihCohortDefinitionLibrary;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CompositionCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.EncounterCohortDefinition;
import org.openmrs.module.reporting.config.DataSetDescriptor;
import org.openmrs.module.reporting.dataset.definition.CohortsWithVaryingParametersDataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.Date;

@Component
public class DailyClinicalEncountersDataSetManager  extends DailyIndicatorByLocationDataSetManager {

    @Autowired
    PihCohortDefinitionLibrary pihCohorts;

    @Override
    public DataSetDefinition constructDataSetDefinition(DataSetDescriptor dataSetDescriptor, File file) {

        String messagePrefix = "mirebalaisreports.dailyClinicalEncounters.";

        EncounterType vitalsEncounterType = encounterService.getEncounterTypeByUuid(PihEmrConfigConstants.ENCOUNTERTYPE_VITALS_UUID);
        EncounterType consultEncounterType = encounterService.getEncounterTypeByUuid(PihEmrConfigConstants.ENCOUNTERTYPE_CONSULTATION_UUID);

        CohortDefinition clinicalCheckIns = pihCohorts.getClinicalCheckInAtLocation();
        clinicalCheckIns.setName("clinicalCheckIns");
        clinicalCheckIns.setDescription(messagePrefix + "clinicalCheckIns");
        Mapped<CohortDefinition> mappedClinicalCheckIns = Mapped.map(clinicalCheckIns, "startDate=${day},endDate=${day+1d-1ms},location=${location}");

        EncounterCohortDefinition vitals = new EncounterCohortDefinition();
        vitals.setName("vitals");
        vitals.setDescription("ui.i18n.EncounterType.name." + vitalsEncounterType.getUuid());
        vitals.addParameter(new Parameter("locationList", "Location", Location.class));
        vitals.addParameter(new Parameter("onOrAfter", "On Or After", Date.class));
        vitals.addParameter(new Parameter("onOrBefore", "On Or Before", Date.class));
        vitals.addEncounterType(vitalsEncounterType);
        Mapped<CohortDefinition> mappedVitals = Mapped.map(vitals, "onOrAfter=${day},onOrBefore=${day+1d-1ms},locationList=${location}");

        EncounterCohortDefinition consults = new EncounterCohortDefinition();
        consults.setName("consults");
        consults.setDescription("ui.i18n.EncounterType.name." + consultEncounterType.getUuid());
        consults.addParameter(new Parameter("locationList", "Location", Location.class));
        consults.addParameter(new Parameter("onOrAfter", "On Or After", Date.class));
        consults.addParameter(new Parameter("onOrBefore", "On Or Before", Date.class));
        consults.addEncounterType(consultEncounterType);
        Mapped<CohortDefinition> mappedConsults = Mapped.map(consults, "onOrAfter=${day},onOrBefore=${day+1d-1ms},locationList=${location}");

        CompositionCohortDefinition consultWithoutVitals = new CompositionCohortDefinition();
        consultWithoutVitals.setName("consultWithoutVitals");
        consultWithoutVitals.setDescription(messagePrefix + "consultWithoutVitals");
        consultWithoutVitals.addParameter(getDayParameter());
        consultWithoutVitals.addParameter(getLocationParameter());
        consultWithoutVitals.addSearch("consult", mappedConsults);
        consultWithoutVitals.addSearch("vitals", mappedVitals);
        consultWithoutVitals.setCompositionString("consult AND NOT vitals");

        CohortsWithVaryingParametersDataSetDefinition byLocationDsd = new CohortsWithVaryingParametersDataSetDefinition();
        byLocationDsd.setName("byLocation");
        byLocationDsd.addParameter(getDayParameter());
        byLocationDsd.addColumn(mappedClinicalCheckIns);
        byLocationDsd.addColumn(mappedVitals);
        byLocationDsd.addColumn(mappedConsults);
        byLocationDsd.addColumn(Mapped.mapStraightThrough(consultWithoutVitals));
        byLocationDsd.setVaryingParameters(getParameterOptions());
        byLocationDsd.setRowLabelTemplate("{{ location.name }}");

        return byLocationDsd;
    }
}
