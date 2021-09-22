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

import org.openmrs.Visit;
import org.openmrs.module.pihcore.reporting.library.DataConverterLibrary;
import org.openmrs.module.pihcore.reporting.library.PihEncounterDataLibrary;
import org.openmrs.module.pihcore.reporting.library.PihPatientDataLibrary;
import org.openmrs.module.reporting.config.DataSetDescriptor;
import org.openmrs.module.reporting.config.factory.DataSetFactory;
import org.openmrs.module.reporting.data.converter.AgeConverter;
import org.openmrs.module.reporting.data.converter.PropertyConverter;
import org.openmrs.module.reporting.data.encounter.definition.ConvertedEncounterDataDefinition;
import org.openmrs.module.reporting.data.encounter.definition.EncounterDataDefinition;
import org.openmrs.module.reporting.data.encounter.definition.EncounterLocationDataDefinition;
import org.openmrs.module.reporting.data.encounter.definition.PatientToEncounterDataDefinition;
import org.openmrs.module.reporting.data.encounter.library.BuiltInEncounterDataLibrary;
import org.openmrs.module.reporting.data.patient.definition.PatientDataDefinition;
import org.openmrs.module.reporting.data.patient.library.BuiltInPatientDataLibrary;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.EncounterDataSetDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.query.encounter.definition.BasicEncounterQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.Date;
import java.util.Locale;

/**
 * Data Export of encounter data.  Migrated from mirebalaisreports full data export report manager
 */
@Component
public class HaitiEncounterDataSetManager implements DataSetFactory {

    @Autowired
    BuiltInPatientDataLibrary builtInPatientData;

    @Autowired
    PihPatientDataLibrary pihPatientData;

    @Autowired
    BuiltInEncounterDataLibrary builtInEncounterData;

    @Autowired
    PihEncounterDataLibrary pihEncounterData;

    @Autowired
    DataConverterLibrary converterLibrary;

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

        EncounterDataSetDefinition dsd = new EncounterDataSetDefinition();
        dsd.addParameter(getStartDateParameter());
        dsd.addParameter(getEndDateParameter());

        BasicEncounterQuery query = new BasicEncounterQuery();
        query.addParameter(new Parameter("onOrAfter", "On or after", Date.class));
        query.addParameter(new Parameter("onOrBefore", "On or before", Date.class));
        dsd.addRowFilter(query, "onOrAfter=${startDate},onOrBefore=${endDate}");

        dsd.addColumn("zlEmrId", patientData(pihPatientData.getPreferredZlEmrIdIdentifier()), null);
        dsd.addColumn("patientId", builtInEncounterData.getPatientId(), null);
        dsd.addColumn("age", pihEncounterData.getPatientAgeAtEncounter(), null, new AgeConverter(AgeConverter.YEARS_TO_ONE_DECIMAL_PLACE));
        dsd.addColumn("gender", patientData(builtInPatientData.getGender()), null);
        dsd.addColumn("visitId", builtInEncounterData.getEncounterVisit(), null, new PropertyConverter(Visit.class, "visitId"));
        dsd.addColumn("visitStart", builtInEncounterData.getEncounterVisit(), null, new PropertyConverter(Visit.class, "startDatetime"));
        dsd.addColumn("visitStop", builtInEncounterData.getEncounterVisit(), null, new PropertyConverter(Visit.class, "stopDatetime"));
        dsd.addColumn("encounterId", builtInEncounterData.getEncounterId(), null);
        dsd.addColumn("encounterType", builtInEncounterData.getEncounterTypeName(), null);
        dsd.addColumn("encounterLocation", new ConvertedEncounterDataDefinition(new EncounterLocationDataDefinition(), new PropertyConverter(String.class, "name")), null);  // the "encounterLocation.name" converter is very inefficent
        dsd.addColumn("encounterDatetime", builtInEncounterData.getEncounterDatetime(), null);
        dsd.addColumn("disposition", pihEncounterData.getDisposition(), null, converterLibrary.getObsValueCodedNameConverterInLocale(Locale.FRENCH));
        dsd.addColumn("enteredBy", pihEncounterData.getCreatorName(), null);
        dsd.addColumn("allProviders", pihEncounterData.getAllProviders(), null);
        dsd.addColumn("numberOfProviders", pihEncounterData.getNumberOfProviders(), null);
        dsd.addColumn("administrativeClerk", pihEncounterData.getClerk(), null);
        dsd.addColumn("nurse", pihEncounterData.getNurse(), null);
        dsd.addColumn("consultingClinician", pihEncounterData.getConsultingClinician(), null);
        dsd.addColumn("dispenser", pihEncounterData.getDispenser(), null);
        dsd.addColumn("radiologyTech", pihEncounterData.getRadiologyTechnician(), null);
        dsd.addColumn("orderingProvider", pihEncounterData.getOrderingProvider(), null);
        dsd.addColumn("principalResultsInterpreter", pihEncounterData.getPrincipalResultsInterpreter(), null);
        dsd.addColumn("attendingSurgeon", pihEncounterData.getAttendingSurgeonName(), null);
        dsd.addColumn("assistingSurgeon", pihEncounterData.getAssistingSurgeon(), null);
        dsd.addColumn("anesthesiologist", pihEncounterData.getAnesthesiologist(), null);
        dsd.addColumn("birthdate", patientData(builtInPatientData.getBirthdate()), null);
        dsd.addColumn("birthdate_estimated", patientData(builtInPatientData.getBirthdateEstimated()), null);
        dsd.addColumn("admissionStatus", pihEncounterData.getAdmissionStatus(), null);
        dsd.addColumn("requestedAdmissionLocation", pihEncounterData.getRequestedAdmissionLocationName(), null);
        dsd.addColumn("requestedTransferLocation", pihEncounterData.getRequestedTransferLocationName(), null);
        dsd.addColumn("department", patientData(pihPatientData.getPreferredAddressDepartment()), "");
        dsd.addColumn("commune", patientData(pihPatientData.getPreferredAddressCommune()), "");
        dsd.addColumn("section", patientData(pihPatientData.getPreferredAddressSection()), "");
        dsd.addColumn("locality", patientData(pihPatientData.getPreferredAddressLocality()), "");
        dsd.addColumn("street_landmark", patientData(pihPatientData.getPreferredAddressStreetLandmark()), "");
        
        return dsd;
    }

    private EncounterDataDefinition patientData(PatientDataDefinition definition) {
        return new PatientToEncounterDataDefinition(definition);
    }
}
