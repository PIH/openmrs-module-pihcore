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
import org.openmrs.Location;
import org.openmrs.module.pihcore.PihEmrConfigConstants;
import org.openmrs.module.pihcore.reporting.cohort.definition.AdmissionSoonAfterExitCohortDefinition;
import org.openmrs.module.pihcore.reporting.cohort.definition.DiedSoonAfterEncounterCohortDefinition;
import org.openmrs.module.pihcore.reporting.cohort.definition.LastDispositionBeforeExitCohortDefinition;
import org.openmrs.module.pihcore.reporting.library.PihCohortDefinitionLibrary;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.EncounterCohortDefinition;
import org.openmrs.module.reporting.config.DataSetDescriptor;
import org.openmrs.module.reporting.dataset.definition.CohortIndicatorDataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.indicator.CohortIndicator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Component
public class InpatientStatsDailyDataSetManager extends BaseDataSetManager {

    @Autowired
    PihCohortDefinitionLibrary pihCohorts;

    public static final String EMERGENCY_DEPARTMENT_UUID = "f3a5586e-f06c-4dfb-96b0-6f3451a35e90";
    public static final String EMERGENCY_RECEPTION_UUID = "afa09010-43b6-4f19-89e0-58d09941bcbd";

    @Override
    public DataSetDefinition constructDataSetDefinition(DataSetDescriptor dataSetDescriptor, File file) {

        List<Location> inpatientLocations = adtService.getInpatientLocations();
        EncounterType admissionEncounterType = encounterService.getEncounterTypeByUuid(PihEmrConfigConstants.ENCOUNTERTYPE_ADMISSION_UUID);

        Concept dischargedDisposition = conceptService.getConceptByMapping("DISCHARGED", "PIH");
        Concept deathDisposition = conceptService.getConceptByMapping("DEATH", "PIH");
        Concept transferOutDisposition = conceptService.getConceptByMapping("Transfer out of hospital", "PIH");
        Concept leftWithoutCompletionOfTreatmentDisposition = conceptService.getConceptByMapping("Departed without medical discharge", "PIH");
        Concept leftWithoutSeeingClinicianDisposition = conceptService.getConceptByMapping("Left without seeing a clinician", "PIH");
        List<Concept> dispositionsToConsider = Arrays.asList(dischargedDisposition, deathDisposition, transferOutDisposition, leftWithoutCompletionOfTreatmentDisposition, leftWithoutSeeingClinicianDisposition);
        // Dispositions we're currently ignoring: "Transfer within hospital", "Admit to hospital", "Discharged", "Emergency Department observation", "Home"

        CohortIndicatorDataSetDefinition cohortDsd = new CohortIndicatorDataSetDefinition();
        cohortDsd.addParameter(getStartDateParameter());
        cohortDsd.addParameter(getEndDateParameter());

        DiedSoonAfterEncounterCohortDefinition diedSoonAfterAdmission = new DiedSoonAfterEncounterCohortDefinition();
        diedSoonAfterAdmission.setEncounterType(admissionEncounterType);
        diedSoonAfterAdmission.addParameter(new Parameter("diedOnOrAfter", "Died on or after", Date.class));
        diedSoonAfterAdmission.addParameter(new Parameter("diedOnOrBefore", "Died on or before", Date.class));

        for (Location location : inpatientLocations) {

            addIndicator(cohortDsd, "censusAtStart", "Census at start", location, Mapped.map(pihCohorts.getInpatientAtLocationOnDate(), "date=${startDate}"));
            addIndicator(cohortDsd, "censusAtEnd", "Census at end", location, Mapped.map(pihCohorts.getInpatientAtLocationOnDate(), "date=${endDate}"));
            addIndicator(cohortDsd, "admissions", "Admission", location, Mapped.mapStraightThrough(pihCohorts.getAdmissionAtLocationDuringPeriod()));
            addIndicator(cohortDsd, "transfersIn", "Transfer In", location, Mapped.mapStraightThrough(pihCohorts.getTransferInToLocationDuringPeriod()));
            addIndicator(cohortDsd, "transfersOut", "Transfer Out", location, Mapped.mapStraightThrough(pihCohorts.getTransferOutOfLocationDuringPeriod()));
            addIndicator(cohortDsd, "discharged", "Discharged", location, Mapped.mapStraightThrough(pihCohorts.getDischargeExitFromLocationDuringPeriod()));
            addIndicator(cohortDsd, "deathsWithin48", "Deaths within 48h", location, Mapped.mapStraightThrough(pihCohorts.getDiedExitFromLocationDuringPeriodSoonAfterAdmission()));
            addIndicator(cohortDsd, "deathsAfter48", "Deaths after 48h", location, Mapped.mapStraightThrough(pihCohorts.getDiedExitFromLocationDuringPeriodNotSoonAfterAdmission()));
            addIndicator(cohortDsd, "transfersOutOfHUM", "Transfer Outs", location, Mapped.mapStraightThrough(pihCohorts.getTransferOutOfHumExitFromLocationDuringPeriod()));

            // number left without completing treatment

            LastDispositionBeforeExitCohortDefinition leftWithoutCompletingTreatment = new LastDispositionBeforeExitCohortDefinition();
            leftWithoutCompletingTreatment.addParameter(new Parameter("exitOnOrAfter", "Exit on or after", Date.class));
            leftWithoutCompletingTreatment.addParameter(new Parameter("exitOnOrBefore", "Exit on or before", Date.class));
            leftWithoutCompletingTreatment.setExitFromWard(location);
            leftWithoutCompletingTreatment.setDispositionsToConsider(dispositionsToConsider);
            leftWithoutCompletingTreatment.addDisposition(leftWithoutCompletionOfTreatmentDisposition);

            addIndicator(cohortDsd, "leftWithoutCompletingTx", "Left Without Completing Treatment", location, Mapped.map(leftWithoutCompletingTreatment, "exitOnOrAfter=${startDate},exitOnOrBefore=${endDate}"));

            LastDispositionBeforeExitCohortDefinition leftWithoutSeeingClinician = new LastDispositionBeforeExitCohortDefinition();
            leftWithoutSeeingClinician.addParameter(new Parameter("exitOnOrAfter", "Exit on or after", Date.class));
            leftWithoutSeeingClinician.addParameter(new Parameter("exitOnOrBefore", "Exit on or before", Date.class));
            leftWithoutSeeingClinician.setExitFromWard(location);
            leftWithoutSeeingClinician.setDispositionsToConsider(dispositionsToConsider);
            leftWithoutSeeingClinician.addDisposition(leftWithoutSeeingClinicianDisposition);

            addIndicator(cohortDsd, "leftWithoutSeeingClinician", "Left Without Seeing Clinician", location, Mapped.map(leftWithoutSeeingClinician, "exitOnOrAfter=${startDate},exitOnOrBefore=${endDate}"));
        }

        // number of ED check-ins
        // TODO change this to count by visits or by encounters, instead of by patients
        EncounterCohortDefinition edCheckIn = new EncounterCohortDefinition();
        edCheckIn.addParameter(new Parameter("onOrAfter", "On or after", Date.class));
        edCheckIn.addParameter(new Parameter("onOrBefore", "On or before", Date.class));
        edCheckIn.addEncounterType(encounterService.getEncounterTypeByUuid(PihEmrConfigConstants.ENCOUNTERTYPE_CONSULTATION_UUID));
        edCheckIn.addLocation(locationService.getLocationByUuid(EMERGENCY_DEPARTMENT_UUID));
        edCheckIn.addLocation(locationService.getLocationByUuid(EMERGENCY_RECEPTION_UUID));

        addIndicator(cohortDsd, "edcheckin", "ED Check In", Mapped.map(edCheckIn, "onOrAfter=${startDate},onOrBefore=${endDate}"));

        // number of surgical op-notes entered
        EncounterCohortDefinition surgicalNotes = new EncounterCohortDefinition();
        surgicalNotes.addParameter(new Parameter("onOrAfter", "On or after", Date.class));
        surgicalNotes.addParameter(new Parameter("onOrBefore", "On or before", Date.class));
        surgicalNotes.addEncounterType(encounterService.getEncounterTypeByUuid(PihEmrConfigConstants.ENCOUNTERTYPE_POST_OPERATIVE_NOTE_UUID));

        addIndicator(cohortDsd, "orvolume", "OR Volume", Mapped.map(surgicalNotes, "onOrAfter=${startDate},onOrBefore=${endDate}"));

        // potential readmissions
        AdmissionSoonAfterExitCohortDefinition readmission = new AdmissionSoonAfterExitCohortDefinition();
        readmission.addParameter(new Parameter("onOrAfter", "On or after", Date.class));
        readmission.addParameter(new Parameter("onOrBefore", "On or before", Date.class));

        addIndicator(cohortDsd, "possiblereadmission", "Possible Readmission", Mapped.map(readmission, "onOrAfter=${startDate},onOrBefore=${endDate}"));

        return cohortDsd;
    }

    public void addIndicator(CohortIndicatorDataSetDefinition dsd, String key, String name, Location location, Mapped<CohortDefinition> cd) {
        String nameWithLocation = name + ": " + location.getName();
        CohortIndicator indicator = new CohortIndicator(nameWithLocation);
        indicator.addParameter(getStartDateParameter());
        indicator.addParameter(getEndDateParameter());
        if (cd.getParameterizable().getParameter("location") != null) {
            cd.addParameterMapping("location", location);
        }
        indicator.setCohortDefinition(cd);
        dsd.addColumn(key + location.getUuid(), nameWithLocation, Mapped.map(indicator, "startDate=${day},endDate=${day+1d-1ms}"), "");
    }

    public void addIndicator(CohortIndicatorDataSetDefinition dsd, String key, String name, Mapped<CohortDefinition> cd) {
        CohortIndicator indicator = new CohortIndicator(name);
        indicator.addParameter(getStartDateParameter());
        indicator.addParameter(getEndDateParameter());
        indicator.setCohortDefinition(cd);
        dsd.addColumn(key, name, Mapped.map(indicator, "startDate=${day},endDate=${day+1d-1ms}"), "");
    }
}
