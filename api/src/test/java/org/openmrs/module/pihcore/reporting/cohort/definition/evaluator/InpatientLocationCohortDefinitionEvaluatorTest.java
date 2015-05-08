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

package org.openmrs.module.pihcore.reporting.cohort.definition.evaluator;

import org.junit.Test;
import org.openmrs.Location;
import org.openmrs.api.LocationService;
import org.openmrs.module.pihcore.metadata.Metadata;
import org.openmrs.module.pihcore.metadata.haiti.mirebalais.MirebalaisLocations;
import org.openmrs.module.pihcore.reporting.BaseInpatientReportTest;
import org.openmrs.module.pihcore.reporting.cohort.definition.InpatientLocationCohortDefinition;
import org.openmrs.module.reporting.cohort.EvaluatedCohort;
import org.openmrs.module.reporting.cohort.definition.service.CohortDefinitionService;
import org.openmrs.module.reporting.common.DateUtil;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.test.SkipBaseSetup;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;

import static org.junit.Assert.assertThat;
import static org.openmrs.module.pihcore.reporting.ReportingMatchers.isCohortWithExactlyIds;

@SkipBaseSetup
public class InpatientLocationCohortDefinitionEvaluatorTest extends BaseInpatientReportTest {

    @Autowired
    LocationService locationService;

    @Autowired
    CohortDefinitionService cohortDefinitionService;

    @Test
    public void testEvaluate() throws Exception {
        InpatientLocationCohortDefinition definition = new InpatientLocationCohortDefinition();
        definition.addParameter(new Parameter("ward", "Ward", Location.class));
        definition.addParameter(new Parameter("effectiveDate", "Date", Date.class));

        Location womensInternalMedicine = Metadata.lookup(MirebalaisLocations.WOMENS_INTERNAL_MEDICINE);

        EvaluationContext ec = new EvaluationContext();
        ec.addParameterValue("ward", womensInternalMedicine);
        ec.addParameterValue("effectiveDate", DateUtil.parseDate("2013-10-03", "yyyy-MM-dd"));

        EvaluatedCohort result = cohortDefinitionService.evaluate(definition, ec);

        assertThat(result, isCohortWithExactlyIds(patient1.getId(), patient5.getId()));
    }

    @Test
    public void testThatTransferOutsAreNotIncluded() throws Exception {
        InpatientLocationCohortDefinition definition = new InpatientLocationCohortDefinition();
        definition.addParameter(new Parameter("ward", "Ward", Location.class));
        definition.addParameter(new Parameter("effectiveDate", "Date", Date.class));

        Location womensInternalMedicine = Metadata.lookup(MirebalaisLocations.WOMENS_INTERNAL_MEDICINE);

        EvaluationContext ec = new EvaluationContext();
        ec.addParameterValue("ward", womensInternalMedicine);
        Date endOfDay = DateUtil.parseDate("2013-10-03 23:59:59", "yyyy-MM-dd HH:mm:ss");
        ec.addParameterValue("effectiveDate", endOfDay);

        EvaluatedCohort result = cohortDefinitionService.evaluate(definition, ec);

        assertThat(result, isCohortWithExactlyIds(patient1.getId()));
    }
}
