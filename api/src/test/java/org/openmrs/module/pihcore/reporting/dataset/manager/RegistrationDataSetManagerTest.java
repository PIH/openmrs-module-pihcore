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


import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openmrs.Location;
import org.openmrs.Patient;
import org.openmrs.module.reporting.common.DateUtil;
import org.openmrs.module.reporting.dataset.DataSetRow;
import org.openmrs.module.reporting.dataset.SimpleDataSet;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.springframework.beans.factory.annotation.Autowired;

public class RegistrationDataSetManagerTest extends EncounterDataSetManagerTest {

    @Autowired
    RegistrationDataSetManager registrationDataSetManager;

    @BeforeEach
    @Override
    public void setup() throws Exception {
        super.setup();
        Patient p = createPatient("X3XK71");
        createRegistrationEncounter(p);
    }

    @Test
    public void testDataSet() throws Exception {
        DataSetDefinition dsd = registrationDataSetManager.constructDataSet();
        EvaluationContext context = new EvaluationContext();
        context.addParameterValue("startDate", DateUtil.getDateTime(2015, 1, 1));
        context.addParameterValue("endDate", DateUtil.getDateTime(2015, 12, 31));
        SimpleDataSet dataSet = (SimpleDataSet)dataSetDefinitionService.evaluate(dsd, context);
        DataSetRow row = dataSet.getRows().get(0);
        Assert.assertEquals(1, dataSet.getRows().size());
        Assert.assertEquals(29, dataSet.getMetaData().getColumnCount());
        Assert.assertEquals("John", row.getColumnValue("GIVEN_NAME"));
        Assert.assertEquals("Smitty", row.getColumnValue("NICKNAME"));
        Assert.assertEquals("Smith", row.getColumnValue("FAMILY_NAME"));
        Assert.assertEquals(DateUtil.getDateTime(1977, 11, 23), row.getColumnValue("BIRTHDATE"));
        Assert.assertEquals(false, row.getColumnValue("BIRTHDATE_ESTIMATED"));
        Assert.assertEquals(37.4, row.getColumnValue("AGE_AT_REGISTRATION"));
        Assert.assertNull(row.getColumnValue("CHECK_IN_VISIT"));
        Assert.assertEquals("M", row.getColumnValue("GENDER"));
        Assert.assertEquals("555-1234", row.getColumnValue("TELEPHONE_NUMBER"));
        Assert.assertEquals("Wichita", row.getColumnValue("BIRTHPLACE"));
        Assert.assertEquals("Isabel", row.getColumnValue("MOTHERS_FIRST_NAME"));
        Assert.assertEquals("MA", row.getColumnValue("HAITICORE.ADDRESS.STATEPROVINCE"));
        Assert.assertEquals("Boston", row.getColumnValue("HAITICORE.ADDRESS.CITYVILLAGE"));
        Assert.assertEquals("JP", row.getColumnValue("HAITICORE.ADDRESS.NEIGHBORHOODCELL"));
        Assert.assertEquals("Pondside", row.getColumnValue("HAITICORE.ADDRESS.ADDRESS1"));
        Assert.assertNull(row.getColumnValue("HAITICORE.ADDRESS.ADDRESS2"));
        Assert.assertEquals("X3XK71", row.getColumnValue("EMR_ID"));
        Assert.assertEquals(DateUtil.getDateTime(2015, 4, 15), row.getColumnValue("REGISTRATION_DATE"));
        Assert.assertEquals("Biwo Resepsyon", row.getColumnValue("REGISTRATION_LOCATION"));
        Assert.assertEquals("Married", row.getColumnValue("CIVIL_STATUS"));
        Assert.assertEquals("true", row.getColumnValue("REGISTRATION_RETROSPECTIVE"));
        Assert.assertEquals("true", row.getColumnValue("BIOMETRICS_COLLECTED"));
    }

    @Test
    public void testDataSetFiltersByVisitLocationWhenSpecified() throws Exception {
        Patient p1 = createPatient("X1AAAD");
        Patient p2 = createPatient("X2CCC6");
        Location biwoResepsyon = locationService.getLocation("Biwo Resepsyon");
        Location lacolline = locationService.getLocation("Lacolline");
        createRegistrationEncounter(p1, createVisit(p1, biwoResepsyon));
        createRegistrationEncounter(p2, createVisit(p2, lacolline));

        DataSetDefinition dsd = registrationDataSetManager.constructDataSet();
        EvaluationContext context = new EvaluationContext();
        context.addParameterValue("startDate", DateUtil.getDateTime(2015, 1, 1));
        context.addParameterValue("endDate", DateUtil.getDateTime(2015, 12, 31));
        context.addParameterValue("visitLocation", biwoResepsyon);

        SimpleDataSet dataSet = (SimpleDataSet) dataSetDefinitionService.evaluate(dsd, context);
        Assert.assertEquals(1, dataSet.getRows().size());
        Assert.assertEquals("X1AAAD", dataSet.getRows().get(0).getColumnValue("EMR_ID"));
    }

    @Test
    public void testDataSetReturnsAllEncountersWhenVisitLocationNotSpecified() throws Exception {
        // setup() already created a registration encounter (with no visit) for patient X3XK71
        Patient p1 = createPatient("X1AAAD");
        Patient p2 = createPatient("X2CCC6");
        createRegistrationEncounter(p1, createVisit(p1, locationService.getLocation("Biwo Resepsyon")));
        createRegistrationEncounter(p2, createVisit(p2, locationService.getLocation("Lacolline")));

        DataSetDefinition dsd = registrationDataSetManager.constructDataSet();
        EvaluationContext context = new EvaluationContext();
        context.addParameterValue("startDate", DateUtil.getDateTime(2015, 1, 1));
        context.addParameterValue("endDate", DateUtil.getDateTime(2015, 12, 31));
        // visitLocation intentionally not set

        SimpleDataSet dataSet = (SimpleDataSet) dataSetDefinitionService.evaluate(dsd, context);
        Assert.assertEquals(3, dataSet.getRows().size());
    }

    @Test
    public void testDataSetExcludesEncountersWithNoVisitWhenVisitLocationSpecified() throws Exception {
        Patient p1 = createPatient("X1AAAD");
        createRegistrationEncounter(p1); // no visit attached

        DataSetDefinition dsd = registrationDataSetManager.constructDataSet();
        EvaluationContext context = new EvaluationContext();
        context.addParameterValue("startDate", DateUtil.getDateTime(2015, 1, 1));
        context.addParameterValue("endDate", DateUtil.getDateTime(2015, 12, 31));
        context.addParameterValue("visitLocation", locationService.getLocation("Biwo Resepsyon"));

        SimpleDataSet dataSet = (SimpleDataSet) dataSetDefinitionService.evaluate(dsd, context);
        Assert.assertEquals(0, dataSet.getRows().size());
    }
}
