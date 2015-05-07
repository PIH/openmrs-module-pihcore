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
package org.openmrs.module.pihcore.reporting.library;

import org.openmrs.Concept;
import org.openmrs.Location;
import org.openmrs.api.ConceptService;
import org.openmrs.module.emrapi.EmrApiProperties;
import org.openmrs.module.pihcore.metadata.Metadata;
import org.openmrs.module.pihcore.metadata.core.EncounterTypes;
import org.openmrs.module.pihcore.reporting.cohort.definition.DiagnosisCohortDefinition;
import org.openmrs.module.pihcore.reporting.cohort.definition.DiedSoonAfterEncounterCohortDefinition;
import org.openmrs.module.pihcore.reporting.cohort.definition.InpatientLocationCohortDefinition;
import org.openmrs.module.pihcore.reporting.cohort.definition.InpatientTransferCohortDefinition;
import org.openmrs.module.pihcore.reporting.cohort.definition.LastDispositionBeforeExitCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CompositionCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.EncounterCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.EncounterWithCodedObsCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.MappedParametersCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.PersonAttributeCohortDefinition;
import org.openmrs.module.reporting.definition.library.BaseDefinitionLibrary;
import org.openmrs.module.reporting.definition.library.DocumentedDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Component
public class PihCohortDefinitionLibrary extends BaseDefinitionLibrary<CohortDefinition> {

    public static final String PREFIX = "mirebalais.cohortDefinition.";

    @Autowired
    private EmrApiProperties emrApiProperties;

    @Autowired
    private ConceptService conceptService;

    @Override
    public Class<? super CohortDefinition> getDefinitionType() {
        return CohortDefinition.class;
    }

    @Override
    public String getKeyPrefix() {
        return PREFIX;
    }

    @DocumentedDefinition(value = "specificCodedDiagnosesBetweenDates")
    public DiagnosisCohortDefinition getSpecificCodedDiagnosesBetweenDates() {
        DiagnosisCohortDefinition cd = new DiagnosisCohortDefinition();
        cd.addParameter(new Parameter("onOrAfter", "reporting.parameter.onOrAfter", Date.class));
        cd.addParameter(new Parameter("onOrBefore", "reporting.parameter.onOrBefore", Date.class));
        cd.addParameter(new Parameter("codedDiagnoses", "mirebalaisreports.parameter.codedDiagnoses", Concept.class, List.class, null));
        return cd;
    }

    @DocumentedDefinition(value = "testPatients")
    public CohortDefinition getTestPatients() {
        PersonAttributeCohortDefinition test = new PersonAttributeCohortDefinition();
        test.setAttributeType(emrApiProperties.getTestPatientPersonAttributeType());
        //the method add value has a bug, using set values for now
        test.setValues(Arrays.asList("true"));
        return test;
    }

    @DocumentedDefinition(value = "excludeTestPatients")
    public CohortDefinition getExcludeTestPatients() {
        CohortDefinition test = getTestPatients();

        CompositionCohortDefinition excludeTestPatientsCohortDefinition = new CompositionCohortDefinition();
        excludeTestPatientsCohortDefinition.addSearch("test", map(test, ""));
        excludeTestPatientsCohortDefinition.setCompositionString("NOT test");
        return excludeTestPatientsCohortDefinition;
    }

    @DocumentedDefinition(value = "clinicalCheckInAtLocation")
    public CohortDefinition getClinicalCheckInAtLocation() {
        EncounterWithCodedObsCohortDefinition cd = new EncounterWithCodedObsCohortDefinition();
        cd.addParameter(new Parameter("onOrAfter", "reporting.parameter.onOrAfter", Date.class));
        cd.addParameter(new Parameter("onOrBefore", "reporting.parameter.onOrBefore", Date.class));
        cd.addParameter(new Parameter("locationList", "reporting.parameter.locationList", Location.class));
        cd.addEncounterType(Metadata.lookup(EncounterTypes.CHECK_IN));
        cd.setConcept(conceptService.getConceptByMapping("Type of HUM visit", "PIH"));
        cd.addIncludeCodedValue(conceptService.getConceptByMapping("CLINICAL", "PIH"));

        return new MappedParametersCohortDefinition(cd, "onOrAfter", "startDate", "onOrBefore", "endDate", "locationList", "location");
    }

    @DocumentedDefinition(value = "inpatientAtLocationOnDate")
    public CohortDefinition getInpatientAtLocationOnDate() {
        InpatientLocationCohortDefinition cd = new InpatientLocationCohortDefinition();
        cd.addParameter(parameter(Date.class, "effectiveDate"));
        cd.addParameter(parameter(Location.class, "ward"));
        return new MappedParametersCohortDefinition(cd, "ward", "location", "effectiveDate", "date");
    }

    @DocumentedDefinition(value = "admissionAtLocationDuringPeriod")
    public CohortDefinition getAdmissionAtLocationDuringPeriod() {
        EncounterCohortDefinition cd = new EncounterCohortDefinition();
        cd.addParameter(new Parameter("onOrAfter", "reporting.parameter.onOrAfter", Date.class));
        cd.addParameter(new Parameter("onOrBefore", "reporting.parameter.onOrBefore", Date.class));
        cd.addParameter(new Parameter("locationList", "reporting.parameter.location", Location.class));
        cd.addEncounterType(Metadata.lookup(EncounterTypes.ADMISSION));
        return new MappedParametersCohortDefinition(cd, "onOrAfter", "startDate", "onOrBefore", "endDate", "locationList", "location");
    }

    @DocumentedDefinition(value = "transferInToLocationDuringPeriod")
    public CohortDefinition getTransferInToLocationDuringPeriod() {
        InpatientTransferCohortDefinition cd = new InpatientTransferCohortDefinition();
        cd.addParameter(new Parameter("onOrAfter", "reporting.parameter.onOrAfter", Date.class));
        cd.addParameter(new Parameter("onOrBefore", "reporting.parameter.onOrBefore", Date.class));
        cd.addParameter(new Parameter("inToWard", "mirebalaisreports.parameter.inToWard", Location.class));
        return new MappedParametersCohortDefinition(cd, "onOrAfter", "startDate", "onOrBefore", "endDate", "inToWard", "location");
    }

    @DocumentedDefinition(value = "transferOutOfLocationDuringPeriod")
    public CohortDefinition getTransferOutOfLocationDuringPeriod() {
        InpatientTransferCohortDefinition cd = new InpatientTransferCohortDefinition();
        cd.addParameter(new Parameter("onOrAfter", "reporting.parameter.onOrAfter", Date.class));
        cd.addParameter(new Parameter("onOrBefore", "reporting.parameter.onOrBefore", Date.class));
        cd.addParameter(new Parameter("outOfWard", "mirebalaisreports.parameter.outOfWard", Location.class));
        return new MappedParametersCohortDefinition(cd, "onOrAfter", "startDate", "onOrBefore", "endDate", "outOfWard", "location");
    }

    @DocumentedDefinition(value = "diedSoonAfterAdmissionDuringPeriod")
    public CohortDefinition getDiedSoonAfterAdmissionDuringPeriod() {
        DiedSoonAfterEncounterCohortDefinition cd = new DiedSoonAfterEncounterCohortDefinition();
        cd.setEncounterType(emrApiProperties.getAdmissionEncounterType());
        cd.addParameter(new Parameter("diedOnOrAfter", "mirebalaisreports.parameter.diedOnOrAfter", Date.class));
        cd.addParameter(new Parameter("diedOnOrBefore", "mirebalaisreports.parameter.diedOnOrBefore", Date.class));
        return new MappedParametersCohortDefinition(cd, "diedOnOrAfter", "startDate", "diedOnOrBefore", "endDate");
    }

    @DocumentedDefinition(value = "dischargeExitFromLocationDuringPeriod")
    public CohortDefinition getDischargeExitFromLocationDuringPeriod() {
        return lastDispositionBeforeExitHelper(conceptService.getConceptByMapping("DISCHARGED", "PIH"));
    }

    @DocumentedDefinition(value = "transferOutOfHumExitFromLocationDuringPeriod")
    public CohortDefinition getTransferOutOfHumExitFromLocationDuringPeriod() {
        return lastDispositionBeforeExitHelper(conceptService.getConceptByMapping("Transfer out of hospital", "PIH"));
    }

    @DocumentedDefinition(value = "leftWithoutCompletingTreatmentExitFromLocationDuringPeriod")
    public CohortDefinition getLeftWithoutCompletingTreatmentExitFromLocationDuringPeriod() {
        return lastDispositionBeforeExitHelper(conceptService.getConceptByMapping("Departed without medical discharge", "PIH"));
    }

    @DocumentedDefinition(value = "leftWithoutSeeingClinicianExitFromLocationDuringPeriod")
    public CohortDefinition getLeftWithoutSeeingClinicianExitFromLocationDuringPeriod() {
        return lastDispositionBeforeExitHelper(conceptService.getConceptByMapping("Left without seeing a clinician", "PIH"));
    }

    @DocumentedDefinition(value = "diedExitFromLocationDuringPeriod")
    public CohortDefinition getDiedExitFromLocationDuringPeriod() {
        return lastDispositionBeforeExitHelper(conceptService.getConceptByMapping("DEATH", "PIH"));
    }

    @DocumentedDefinition(value = "diedExitFromLocationDuringPeriodSoonAfterAdmission")
    public CohortDefinition getDiedExitFromLocationDuringPeriodSoonAfterAdmission() {
        CohortDefinition diedSoonAfterAdmission = getDiedSoonAfterAdmissionDuringPeriod();
        CohortDefinition diedExit = getDiedExitFromLocationDuringPeriod();

        CompositionCohortDefinition deathsEarly = new CompositionCohortDefinition();
        deathsEarly.addParameter(parameter(Date.class, "startDate"));
        deathsEarly.addParameter(parameter(Date.class, "endDate"));
        deathsEarly.addParameter(parameter(Location.class, "location"));
        deathsEarly.addSearch("died", Mapped.mapStraightThrough(diedExit));
        deathsEarly.addSearch("diedSoon", Mapped.mapStraightThrough(diedSoonAfterAdmission));
        deathsEarly.setCompositionString("died AND diedSoon");

        return diedExit;
    }

    @DocumentedDefinition(value = "diedExitFromLocationDuringPeriodNotSoonAfterAdmission")
    public CohortDefinition getDiedExitFromLocationDuringPeriodNotSoonAfterAdmission() {
        CohortDefinition diedSoonAfterAdmission = getDiedSoonAfterAdmissionDuringPeriod();
        CohortDefinition diedExit = getDiedExitFromLocationDuringPeriod();

        CompositionCohortDefinition deathsEarly = new CompositionCohortDefinition();
        deathsEarly.addParameter(parameter(Date.class, "startDate"));
        deathsEarly.addParameter(parameter(Date.class, "endDate"));
        deathsEarly.addParameter(parameter(Location.class, "location"));
        deathsEarly.addSearch("died", Mapped.mapStraightThrough(diedExit));
        deathsEarly.addSearch("diedSoon", Mapped.mapStraightThrough(diedSoonAfterAdmission));
        deathsEarly.setCompositionString("died AND NOT diedSoon");

        return diedExit;
    }

    private List<Concept> getDispositionsToConsiderAsExit() {
        Concept dischargedDisposition = conceptService.getConceptByMapping("DISCHARGED", "PIH");
        Concept deathDisposition = conceptService.getConceptByMapping("DEATH", "PIH");
        Concept transferOutDisposition = conceptService.getConceptByMapping("Transfer out of hospital", "PIH");
        Concept leftWithoutCompletionOfTreatmentDisposition = conceptService.getConceptByMapping("Departed without medical discharge", "PIH");
        Concept leftWithoutSeeingClinicianDisposition = conceptService.getConceptByMapping("Left without seeing a clinician", "PIH");
        return Arrays.asList(dischargedDisposition, deathDisposition, transferOutDisposition, leftWithoutCompletionOfTreatmentDisposition, leftWithoutSeeingClinicianDisposition);
    }

    private CohortDefinition lastDispositionBeforeExitHelper(Concept disposition) {
        LastDispositionBeforeExitCohortDefinition cd = new LastDispositionBeforeExitCohortDefinition();
        cd.addParameter(new Parameter("exitOnOrAfter", "mirebalaisreports.parameter.exitOnOrAfter", Date.class));
        cd.addParameter(new Parameter("exitOnOrBefore", "mirebalaisreports.parameter.exitOnOrBefore", Date.class));
        cd.addParameter(new Parameter("exitFromWard", "mirebalaisreports.parameter.exitFromWard", Location.class));
        cd.setDispositionsToConsider(getDispositionsToConsiderAsExit());
        cd.addDisposition(disposition);
        return new MappedParametersCohortDefinition(cd, "exitOnOrAfter", "startDate", "exitOnOrBefore", "endDate", "exitFromWard", "location");
    }

    public Parameter parameter(Class<?> clazz, String name) {
        return new Parameter(name, "mirebalaisreports.parameter." + name, clazz);
    }

}
