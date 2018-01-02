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
import org.openmrs.Patient;
import org.openmrs.PersonName;
import org.openmrs.api.PatientService;
import org.openmrs.api.UserService;
import org.openmrs.module.pihcore.reporting.cohort.definition.PersonAuditInfoCohortDefinition;
import org.openmrs.module.pihcore.reporting.cohort.evaluator.PersonAuditInfoCohortDefinitionEvaluator;
import org.openmrs.module.reporting.cohort.EvaluatedCohort;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.common.DateUtil;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.Date;

import static org.junit.Assert.assertThat;
import static org.openmrs.module.pihcore.reporting.ReportingMatchers.hasExactlyIds;

public class PersonAuditInfoCohortDefinitionEvaluatorTest extends BaseModuleContextSensitiveTest {

    @Autowired
    PersonAuditInfoCohortDefinitionEvaluator evaluator;

    @Autowired
    UserService userService;

    @Autowired
    PatientService patientService;

    @Test
    public void testEvaluateWithNoParameters() throws Exception {
        PersonAuditInfoCohortDefinition cd = new PersonAuditInfoCohortDefinition();
        EvaluatedCohort actual = evaluator.evaluate((CohortDefinition) cd, new EvaluationContext());
        assertThat(actual, hasExactlyIds(2, 6, 7, 8));
    }

    @Test
    public void testEvaluateIncludingVoided() throws Exception {
        PersonAuditInfoCohortDefinition cd = new PersonAuditInfoCohortDefinition();
        cd.setIncludeVoided(true);
        EvaluatedCohort actual = evaluator.evaluate((CohortDefinition) cd, new EvaluationContext());
        assertThat(actual, hasExactlyIds(2, 6, 7, 8, 432, 999));
    }

    @Test
    public void testEvaluateByCreationDetails() throws Exception {
        PersonAuditInfoCohortDefinition cd = new PersonAuditInfoCohortDefinition();
        cd.setCreatedOnOrAfter(DateUtil.parseDate("2005-09-22", "yyyy-MM-dd"));
        cd.setCreatedOnOrBefore(DateUtil.parseDate("2005-09-22", "yyyy-MM-dd"));
        cd.setCreatedByUsers(Arrays.asList(userService.getUser(1)));
        EvaluatedCohort actual = evaluator.evaluate((CohortDefinition) cd, new EvaluationContext());
        assertThat(actual, hasExactlyIds(2));
    }

    @Test
    public void testEvaluateByChangedDetails() throws Exception {
        PersonAuditInfoCohortDefinition cd = new PersonAuditInfoCohortDefinition();
        cd.setChangedOnOrAfter(DateUtil.parseDate("2008-08-18 12:25", "yyyy-MM-dd HH:mm"));
        cd.setChangedOnOrBefore(DateUtil.parseDate("2008-08-18 12:26", "yyyy-MM-dd HH:mm"));
        EvaluatedCohort actual = evaluator.evaluate((CohortDefinition) cd, new EvaluationContext());
        assertThat(actual, hasExactlyIds(6, 7));
    }

    @Test
    public void testEvaluateByVoidedDetails() throws Exception {
        Date today = DateUtil.getStartOfDay(new Date());

        // in standardTestDataset.xml patient 999 is voided, but has no dateVoided. Fix this
        Patient voidedPatient = patientService.getPatient(999);
        voidedPatient.addName(new PersonName("A", "Non-voided", "Name"));
        patientService.voidPatient(voidedPatient, "testing");

        PersonAuditInfoCohortDefinition cd = new PersonAuditInfoCohortDefinition();
        cd.setIncludeVoided(true);
        cd.setVoidedOnOrAfter(today);
        cd.setVoidedOnOrBefore(today);
        EvaluatedCohort actual = evaluator.evaluate((CohortDefinition) cd, new EvaluationContext());
        assertThat(actual, hasExactlyIds(999));
    }

}
