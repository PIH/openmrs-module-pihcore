package org.openmrs.module.pihcore.reporting.dataset.manager;

import org.openmrs.Location;
import org.openmrs.module.pihcore.PihEmrConfigConstants;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CompositionCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.EncounterCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.EncounterWithCodedObsCohortDefinition;
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
public class DailyCheckInsDataSetManager extends DailyIndicatorByLocationDataSetManager {

    @Override
    public DataSetDefinition constructDataSetDefinition(DataSetDescriptor dataSetDescriptor, File file) {

        String messagePrefix = "mirebalaisreports.dailyCheckInEncounters.";

        if ("overall".equalsIgnoreCase(dataSetDescriptor.getKey())) {

            CohortCrossTabDataSetDefinition overallDsd = new CohortCrossTabDataSetDefinition();
            overallDsd.setName("overall");
            overallDsd.addParameter(getDayParameter());

            EncounterCohortDefinition overall = new EncounterCohortDefinition();
            overall.addEncounterType(encounterService.getEncounterTypeByUuid(PihEmrConfigConstants.ENCOUNTERTYPE_CHECK_IN_UUID));
            overall.addParameter(new Parameter("onOrAfter", "On Or After", Date.class));
            overall.addParameter(new Parameter("onOrBefore", "On Or Before", Date.class));
            overallDsd.addColumn(messagePrefix + "overall", Mapped.map(overall, "onOrAfter=${day},onOrBefore=${day+1d-1ms}"));

            EncounterCohortDefinition multipleCheckIns = new EncounterCohortDefinition();
            multipleCheckIns.addEncounterType(encounterService.getEncounterTypeByUuid(PihEmrConfigConstants.ENCOUNTERTYPE_CHECK_IN_UUID));
            multipleCheckIns.setAtLeastCount(2);
            multipleCheckIns.addParameter(new Parameter("onOrAfter", "On Or After", Date.class));
            multipleCheckIns.addParameter(new Parameter("onOrBefore", "On Or Before", Date.class));
            overallDsd.addColumn(messagePrefix + "dataQuality.multipleCheckins", Mapped.map(multipleCheckIns, "onOrAfter=${day},onOrBefore=${day+1d-1ms}"));

            return overallDsd;
        }
        else if ("byLocation".equalsIgnoreCase(dataSetDescriptor.getKey())) {

            CohortsWithVaryingParametersDataSetDefinition byLocationDsd = new CohortsWithVaryingParametersDataSetDefinition();
            byLocationDsd.setName("byLocation");
            byLocationDsd.addParameter(getDayParameter());

            EncounterCohortDefinition priorConsultAtLocation = new EncounterCohortDefinition();
            priorConsultAtLocation.addEncounterType(encounterService.getEncounterTypeByUuid(PihEmrConfigConstants.ENCOUNTERTYPE_CONSULTATION_UUID));
            priorConsultAtLocation.addParameter(new Parameter("locationList", "Location List", Location.class));
            priorConsultAtLocation.addParameter(new Parameter("onOrBefore", "On or before", Date.class));
            Mapped<CohortDefinition> mappedPriorConsultAtLocation = Mapped.map(priorConsultAtLocation, "onOrBefore=${day-1ms},locationList=${location}");

            byLocationDsd.addColumn(checkInSplitByPriorConsultation(messagePrefix + "CLINICAL_new", checkInWithReason("PIH", "CLINICAL"), mappedPriorConsultAtLocation, false));
            byLocationDsd.addColumn(checkInSplitByPriorConsultation(messagePrefix + "CLINICAL_return", checkInWithReason("PIH", "CLINICAL"), mappedPriorConsultAtLocation, true));
            byLocationDsd.addColumn(checkInWithReason("PIH", "Lab only"));
            byLocationDsd.addColumn(checkInWithReason("PIH", "Pharmacy only"));
            byLocationDsd.addColumn(checkInWithReason("PIH", "Procedure only"));
            byLocationDsd.addColumn(checkInWithReason("PIH", "Social assistance and psychosocial support"));
            byLocationDsd.addColumn(checkInWithReason("PIH", "Request scheduled appointment"));
            byLocationDsd.addColumn(checkInWithReason("PIH", "ID card only"));
            byLocationDsd.addColumn(checkInWithOtherOrMissingReasons(
                    "CLINICAL", "Lab only", "Pharmacy only", "Procedure only",
                    "Social assistance and psychosocial support", "Request scheduled appointment", "ID card only"
            ));
            byLocationDsd.setVaryingParameters(getParameterOptions());
            byLocationDsd.setRowLabelTemplate("{{ location.name }}");
            return byLocationDsd;
        }
        else {
            throw new IllegalArgumentException("You must configure this DataSet with either 'overall' or 'byLocation'");
        }
    }

    private Mapped<CohortDefinition> checkInSplitByPriorConsultation(String columnName, Mapped<CohortDefinition> checkInWithReason, Mapped<CohortDefinition> priorConsult, boolean included) {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.setName(columnName);
        cd.setDescription(columnName);
        cd.addParameter(getDayParameter());
        cd.addParameter(getLocationParameter());
        cd.addSearch("checkInWithReason", checkInWithReason);
        cd.addSearch("priorConsult", priorConsult);
        cd.setCompositionString("checkInWithReason " + (included ? "AND" : "AND NOT") + " priorConsult");
        return Mapped.mapStraightThrough(cd);
    }

    private Mapped<CohortDefinition> checkInWithReason(String source, String code) {
        EncounterWithCodedObsCohortDefinition cd = new EncounterWithCodedObsCohortDefinition();
        cd.setName(code);
        cd.setDescription("{{ conceptName \"" + source + ":" + code + "\" }}");
        cd.addParameter(new Parameter("onOrAfter", "On or after", Date.class));
        cd.addParameter(new Parameter("onOrBefore", "On or before", Date.class));
        cd.addParameter(new Parameter("locationList", "Locations", Location.class));
        cd.addEncounterType(encounterService.getEncounterTypeByUuid(PihEmrConfigConstants.ENCOUNTERTYPE_CHECK_IN_UUID));
        cd.setConcept(conceptService.getConceptByMapping("Type of HUM visit", "PIH"));
        cd.addIncludeCodedValue(conceptService.getConceptByMapping(code, source));
        return Mapped.map(cd, "onOrAfter=${day},onOrBefore=${day+1d-1ms},locationList=${location}");
    }

    private Mapped<CohortDefinition> checkInWithOtherOrMissingReasons(String... excludeValues) {
        EncounterWithCodedObsCohortDefinition cd = new EncounterWithCodedObsCohortDefinition();
        cd.setName("mirebalaisreports.otherOrUnspecified");
        cd.setDescription("{{ message \"mirebalaisreports.otherOrUnspecified\" }}");
        cd.addParameter(new Parameter("onOrAfter", "On or after", Date.class));
        cd.addParameter(new Parameter("onOrBefore", "On or before", Date.class));
        cd.addParameter(new Parameter("locationList", "Locations", Location.class));
        cd.addEncounterType(encounterService.getEncounterTypeByUuid(PihEmrConfigConstants.ENCOUNTERTYPE_CHECK_IN_UUID));
        cd.setConcept(conceptService.getConceptByMapping("Type of HUM visit", "PIH"));
        cd.setIncludeNoObsValue(true);
        for (String excludeValue : excludeValues) {
            cd.addExcludeCodedValue(conceptService.getConceptByMapping(excludeValue, "PIH"));
        }
        return Mapped.map(cd, "onOrAfter=${day},onOrBefore=${day+1d-1ms},locationList=${location}");
    }
}
