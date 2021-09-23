package org.openmrs.module.pihcore.reporting.dataset.manager;

import org.openmrs.Location;
import org.openmrs.module.pihcore.PihEmrConfigConstants;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.EncounterCohortDefinition;
import org.openmrs.module.reporting.config.DataSetDescriptor;
import org.openmrs.module.reporting.dataset.definition.CohortCrossTabDataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.CohortsWithVaryingParametersDataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.Date;

@Component
public class DailyRegistrationsDataSetManager extends DailyIndicatorByLocationDataSetManager {

    @Override
    public DataSetDefinition constructDataSetDefinition(DataSetDescriptor dataSetDescriptor, File file) {

        String messagePrefix = "mirebalaisreports.dailyRegistrations.";

        if ("overall".equalsIgnoreCase(dataSetDescriptor.getKey())) {
            CohortCrossTabDataSetDefinition overallDsd = new CohortCrossTabDataSetDefinition();
            overallDsd.setName("overall");
            overallDsd.addParameter(getDayParameter());
            EncounterCohortDefinition overall = new EncounterCohortDefinition();
            overall.addEncounterType(encounterService.getEncounterTypeByUuid(PihEmrConfigConstants.ENCOUNTERTYPE_PATIENT_REGISTRATION_UUID));
            overall.addParameter(new Parameter("onOrAfter", "On Or After", Date.class));
            overall.addParameter(new Parameter("onOrBefore", "On Or Before", Date.class));
            overallDsd.addColumn(messagePrefix + "overall", Mapped.map(overall, "onOrAfter=${day},onOrBefore=${day+1d-1ms}"));
            return overallDsd;
        }
        else if ("byLocation".equalsIgnoreCase(dataSetDescriptor.getKey())) {
            CohortsWithVaryingParametersDataSetDefinition byLocationDsd = new CohortsWithVaryingParametersDataSetDefinition();
            byLocationDsd.setName("byLocation");
            byLocationDsd.addParameter(getDayParameter());
            byLocationDsd.setVaryingParameters(getParameterOptions());
            byLocationDsd.setRowLabelTemplate("{{ location.name }}");

            EncounterCohortDefinition regByLocation = new EncounterCohortDefinition();
            regByLocation.setName("registrations");
            regByLocation.setDescription("ui.i18n.EncounterType.name.873f968a-73a8-4f9c-ac78-9f4778b751b6");
            regByLocation.addEncounterType(encounterService.getEncounterTypeByUuid(PihEmrConfigConstants.ENCOUNTERTYPE_PATIENT_REGISTRATION_UUID));
            regByLocation.addParameter(new Parameter("locationList", "Location", Location.class));
            regByLocation.addParameter(new Parameter("onOrAfter", "On Or After", Date.class));
            regByLocation.addParameter(new Parameter("onOrBefore", "On Or Before", Date.class));
            Mapped<CohortDefinition> mappedRegByLocation = Mapped.map(regByLocation, "onOrAfter=${day},onOrBefore=${day+1d-1ms},locationList=${location}");
            byLocationDsd.addColumn(mappedRegByLocation);

            return byLocationDsd;
        }
        else {
            throw new IllegalArgumentException("You must configure this DataSet with either 'overall' or 'byLocation'");
        }
    }

}
