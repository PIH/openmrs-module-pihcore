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

import org.openmrs.Provider;
import org.openmrs.module.pihcore.reporting.dataset.definition.NonCodedDiagnosisDataSetDefinition;
import org.openmrs.module.reporting.config.DataSetDescriptor;
import org.openmrs.module.reporting.config.factory.DataSetFactory;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.Date;

/**
 * Returns a NonCodedDiagnosisDataSetDefinition
 */
@Component
public class NonCodedDiagnosisDataSetManager implements DataSetFactory {

    @Override
    public DataSetDefinition constructDataSetDefinition(DataSetDescriptor dataSetDescriptor, File baseConfigDir) {
        NonCodedDiagnosisDataSetDefinition dsd = new NonCodedDiagnosisDataSetDefinition();
        dsd.addParameter(new Parameter("fromDate", "From Date", Date.class));
        dsd.addParameter(new Parameter("toDate", "To Date", Date.class));
        dsd.addParameter(new Parameter("nonCoded", "Non-Coded", String.class));
        dsd.addParameter(new Parameter("provider", "Provider", Provider.class));
        return dsd;
    }
}
