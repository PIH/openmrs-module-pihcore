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
import org.openmrs.Drug;
import org.openmrs.Location;
import org.openmrs.PatientIdentifierType;
import org.openmrs.api.ConceptService;
import org.openmrs.api.EncounterService;
import org.openmrs.module.dispensing.DispensingProperties;
import org.openmrs.module.emrapi.EmrApiProperties;
import org.openmrs.module.pihcore.PihEmrConfigConstants;
import org.openmrs.module.pihcore.reporting.library.PihObsDataLibrary;
import org.openmrs.module.reporting.config.DataSetDescriptor;
import org.openmrs.module.reporting.config.factory.DataSetFactory;
import org.openmrs.module.reporting.data.converter.DateConverter;
import org.openmrs.module.reporting.data.converter.ObjectFormatter;
import org.openmrs.module.reporting.data.converter.ObsValueTextAsCodedConverter;
import org.openmrs.module.reporting.data.converter.PropertyConverter;
import org.openmrs.module.reporting.data.encounter.definition.EncounterDatetimeDataDefinition;
import org.openmrs.module.reporting.data.encounter.definition.EncounterLocationDataDefinition;
import org.openmrs.module.reporting.data.encounter.definition.EncounterProviderDataDefinition;
import org.openmrs.module.reporting.data.encounter.definition.ObsForEncounterDataDefinition;
import org.openmrs.module.reporting.data.obs.definition.GroupMemberObsDataDefinition;
import org.openmrs.module.reporting.data.patient.definition.PatientIdentifierDataDefinition;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.ObsDataSetDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.query.obs.definition.BasicObsQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.Date;

/**
 * Data Export of dispensing data.  Migrated from mirebalaisreports full data export report manager
 */
@Component
public class DispensingDataSetManager implements DataSetFactory {

    @Autowired
    PihObsDataLibrary pihObsData;

    @Autowired
    protected EmrApiProperties emrApiProperties;

    @Autowired
    protected EncounterService encounterService;

    @Autowired
    protected ConceptService conceptService;

    @Autowired
    private DispensingProperties dispensingProperties;

    private Parameter getStartDateParameter() {
        return new Parameter("startDate", "mirebalaisreports.parameter.startDate", Date.class);
    }

    private Parameter getEndDateParameter() {
        return new Parameter("endDate", "mirebalaisreports.parameter.endDate", Date.class);
    }

    public DataSetDefinition constructDataSet() {
        return constructDataSetDefinition(null, null);
    }

    @Override
    public DataSetDefinition constructDataSetDefinition(DataSetDescriptor dataSetDescriptor, File baseConfigDir) {

        ObsDataSetDefinition dsd = new ObsDataSetDefinition();
        dsd.addParameter(getStartDateParameter());
        dsd.addParameter(getEndDateParameter());

        BasicObsQuery query = new BasicObsQuery();
        query.addParameter(new Parameter("onOrAfter", "On or after", Date.class));
        query.addParameter(new Parameter("onOrBefore", "On or before", Date.class));
        query.addConcept(dispensingProperties.getDispensingConstructConcept());
        dsd.addRowFilter(query, "onOrAfter=${startDate},onOrBefore=${endDate}");

        dsd.addColumn("visitId", pihObsData.getVisitId(), null);
        dsd.addColumn("encounterId", pihObsData.getEncounterId(), null);
        dsd.addColumn("medication", constructGroupMemberObsDataDefinition(dispensingProperties.getMedicationConcept()), "",  new PropertyConverter(Drug.class, "valueDrug"), new ObjectFormatter());
        dsd.addColumn("dosage", constructGroupMemberObsDataDefinition(dispensingProperties.getDosageConcept()), "", new ObjectFormatter());
        dsd.addColumn("dosageUnits", constructGroupMemberObsDataDefinition(dispensingProperties.getDosageUnitsConcept()), "", new ObjectFormatter());
        dsd.addColumn("frequency", constructGroupMemberObsDataDefinition(dispensingProperties.getMedicationFrequencyConcept()), "", new ObjectFormatter());
        dsd.addColumn("duration", constructGroupMemberObsDataDefinition(dispensingProperties.getMedicationDurationConcept()), "", new ObjectFormatter());
        dsd.addColumn("durationUnits", constructGroupMemberObsDataDefinition(dispensingProperties.getMedicationDurationUnitsConcept()), "", new ObjectFormatter());
        dsd.addColumn("amount", constructGroupMemberObsDataDefinition(dispensingProperties.getDispensedAmountConcept()), "", new ObjectFormatter());
        dsd.addColumn("instructions", constructGroupMemberObsDataDefinition(dispensingProperties.getAdministrationInstructions()), "", new ObjectFormatter());
        dsd.addColumn("patientIdentifier", constructPatientIdentifierDataDefinition(emrApiProperties.getPrimaryIdentifierType()), "", new ObjectFormatter());
        dsd.addColumn("dispensedLocation", new EncounterLocationDataDefinition(), "", new ObjectFormatter());
        dsd.addColumn("dispensedDatetime", new EncounterDatetimeDataDefinition(), "", new DateConverter("dd MMM yyyy hh:mm aa"));

        EncounterProviderDataDefinition dispensedByDef = new EncounterProviderDataDefinition();
        dispensedByDef.setEncounterRole(encounterService.getEncounterRoleByUuid(PihEmrConfigConstants.ENCOUNTERROLE_DISPENSER_UUID));
        dsd.addColumn("dispensedBy", dispensedByDef, "", new ObjectFormatter());

        EncounterProviderDataDefinition prescribedByDef = new EncounterProviderDataDefinition();
        prescribedByDef.setEncounterRole(encounterService.getEncounterRoleByUuid(PihEmrConfigConstants.ENCOUNTERROLE_ORDERINGPROVIDER_UUID));
        dsd.addColumn("prescribedBy", prescribedByDef, "", new ObjectFormatter());

        ObsForEncounterDataDefinition typeOfPrescriptionDef = new ObsForEncounterDataDefinition();
        typeOfPrescriptionDef.setQuestion(conceptService.getConceptByMapping("9292", "PIH"));
        dsd.addColumn("typeOfPrescription", typeOfPrescriptionDef, "", new ObjectFormatter());

        ObsForEncounterDataDefinition locationOfPrescriptionDef = new ObsForEncounterDataDefinition();
        locationOfPrescriptionDef.setQuestion(conceptService.getConceptByMapping("9293", "PIH"));
        dsd.addColumn("locationOfPrescription", locationOfPrescriptionDef, "", new ObsValueTextAsCodedConverter<>(Location.class), new ObjectFormatter());

        return dsd;
    }

    private GroupMemberObsDataDefinition constructGroupMemberObsDataDefinition(Concept concept) {
        GroupMemberObsDataDefinition groupMemberObsDataDefinition = new GroupMemberObsDataDefinition();
        groupMemberObsDataDefinition.setQuestion(concept);
        return groupMemberObsDataDefinition;
    }

    private PatientIdentifierDataDefinition constructPatientIdentifierDataDefinition(PatientIdentifierType type) {
        PatientIdentifierDataDefinition patientIdentifierDataDefinition = new PatientIdentifierDataDefinition();
        patientIdentifierDataDefinition.addType(type);
        patientIdentifierDataDefinition.setIncludeFirstNonNullOnly(true);
        return patientIdentifierDataDefinition;
    }
}
