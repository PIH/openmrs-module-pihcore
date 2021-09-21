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

import org.openmrs.EncounterType;
import org.openmrs.module.pihcore.PihEmrConfigConstants;
import org.openmrs.module.pihcore.metadata.Metadata;
import org.openmrs.module.pihcore.reporting.library.PihPatientDataLibrary;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CompositionCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.EncounterCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.VisitCohortDefinition;
import org.openmrs.module.reporting.config.DataSetDescriptor;
import org.openmrs.module.reporting.config.factory.DataSetFactory;
import org.openmrs.module.reporting.data.patient.library.BuiltInPatientDataLibrary;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.PatientDataSetDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.evaluation.parameter.Parameterizable;
import org.openmrs.module.reporting.evaluation.parameter.ParameterizableUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.Date;

/**
 * Data Export of patient data.  Migrated from mirebalaisreports full data export report manager
 */
@Component
public class MirebalaisPatientDataSetManager implements DataSetFactory {

    @Autowired
    BuiltInPatientDataLibrary builtInPatientData;

    @Autowired
    PihPatientDataLibrary pihPatientData;

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

        PatientDataSetDefinition dsd = new PatientDataSetDefinition();
        dsd.addParameter(getStartDateParameter());
        dsd.addParameter(getEndDateParameter());

        CompositionCohortDefinition baseCohortDefinition = new CompositionCohortDefinition();
        baseCohortDefinition.addParameter(getStartDateParameter());
        baseCohortDefinition.addParameter(getEndDateParameter());

        VisitCohortDefinition visitDuringPeriod = new VisitCohortDefinition();
        visitDuringPeriod.addParameter(new Parameter("activeOnOrAfter", "", Date.class));
        visitDuringPeriod.addParameter(new Parameter("activeOnOrBefore", "", Date.class));
        baseCohortDefinition.addSearch("visitDuringPeriod", this.<CohortDefinition>map(visitDuringPeriod, "activeOnOrAfter=${startDate},activeOnOrBefore=${endDate}"));

        EncounterType registrationEncounterType = Metadata.lookupEncounterType(PihEmrConfigConstants.ENCOUNTERTYPE_PATIENT_REGISTRATION_UUID);
        EncounterCohortDefinition registrationEncounterDuringPeriod = new EncounterCohortDefinition();
        registrationEncounterDuringPeriod.addEncounterType(registrationEncounterType);
        registrationEncounterDuringPeriod.addParameter(new Parameter("onOrAfter", "", Date.class));
        registrationEncounterDuringPeriod.addParameter(new Parameter("onOrBefore", "", Date.class));
        baseCohortDefinition.addSearch("registrationEncounterDuringPeriod", this.<CohortDefinition>map(registrationEncounterDuringPeriod, "onOrAfter=${startDate},onOrBefore=${endDate}"));

        baseCohortDefinition.setCompositionString("(visitDuringPeriod OR registrationEncounterDuringPeriod)");
        dsd.addRowFilter(this.<CohortDefinition>map(baseCohortDefinition, "startDate=${startDate},endDate=${endDate}"));

        dsd.addColumn("patient_id", builtInPatientData.getPatientId(), "");
        dsd.addColumn("zlemr", pihPatientData.getPreferredZlEmrIdIdentifier(), "");
        dsd.addColumn("loc_registered", pihPatientData.getMostRecentZlEmrIdLocation(), "");
        dsd.addColumn("unknown_patient", pihPatientData.getUnknownPatient(), "");
        dsd.addColumn("numzlemr", pihPatientData.getNumberOfZlEmrIds(), "");
        dsd.addColumn("numero_dossier", pihPatientData.getAllDossierNumberIdentifiers(), "");
        dsd.addColumn("num_nd", pihPatientData.getNumberOfDossierNumbers(), "");
        dsd.addColumn("hivemr", pihPatientData.getMostRecentHivEmrIdIdentifier(), "");
        dsd.addColumn("num_hiv", pihPatientData.getNumberOfHivEmrIds(), "");
        dsd.addColumn("birthdate", builtInPatientData.getBirthdateYmd(), "");
        dsd.addColumn("birthdate_estimated", builtInPatientData.getBirthdateEstimated(), "");
        dsd.addColumn("gender", builtInPatientData.getGender(), "");
        dsd.addColumn("dead", builtInPatientData.getVitalStatusDead(), "");
        dsd.addColumn("death_date", builtInPatientData.getVitalStatusDeathDate(), "");
        dsd.addColumn("department", pihPatientData.getPreferredAddressDepartment(), "");
        dsd.addColumn("commune", pihPatientData.getPreferredAddressCommune(), "");
        dsd.addColumn("section", pihPatientData.getPreferredAddressSection(), "");
        dsd.addColumn("locality", pihPatientData.getPreferredAddressLocality(), "");
        dsd.addColumn("street_landmark", pihPatientData.getPreferredAddressStreetLandmark(), "");
        dsd.addColumn("date_registered", pihPatientData.getRegistrationDatetime(), "");
        dsd.addColumn("reg_location", pihPatientData.getRegistrationLocation(), "");
        dsd.addColumn("reg_by", pihPatientData.getRegistrationCreatorName(), "");
        dsd.addColumn("age_at_reg", pihPatientData.getRegistrationAge(), "");

        return dsd;
    }

    private <T extends Parameterizable> Mapped<T> map(T parameterizable, String mappings) {
        if (parameterizable == null) {
            throw new NullPointerException("Programming error: missing parameterizable");
        }
        if (mappings == null) {
            mappings = ""; // probably not necessary, just to be safe
        }
        return new Mapped<T>(parameterizable, ParameterizableUtil.createParameterMappings(mappings));
    }
}
