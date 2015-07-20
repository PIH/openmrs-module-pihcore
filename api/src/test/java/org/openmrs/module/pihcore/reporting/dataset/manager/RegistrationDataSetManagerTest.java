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
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Patient;
import org.openmrs.module.pihcore.metadata.haiti.mirebalais.MirebalaisLocations;
import org.openmrs.module.reporting.common.DateUtil;
import org.openmrs.module.reporting.dataset.DataSetRow;
import org.openmrs.module.reporting.dataset.SimpleDataSet;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.springframework.beans.factory.annotation.Autowired;

public class RegistrationDataSetManagerTest extends EncounterDataSetManagerTest {

    @Autowired
    RegistrationDataSetManager registrationDataSetManager;

    @Before
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
        Assert.assertEquals("MA", row.getColumnValue("MIREBALAIS.ADDRESS.STATEPROVINCE"));
        Assert.assertEquals("Boston", row.getColumnValue("MIREBALAIS.ADDRESS.CITYVILLAGE"));
        Assert.assertEquals("JP", row.getColumnValue("MIREBALAIS.ADDRESS.NEIGHBORHOODCELL"));
        Assert.assertEquals("Pondside", row.getColumnValue("MIREBALAIS.ADDRESS.ADDRESS1"));
        Assert.assertNull(row.getColumnValue("MIREBALAIS.ADDRESS.ADDRESS2"));
        Assert.assertEquals("X3XK71", row.getColumnValue("EMR_ID"));
        Assert.assertEquals(DateUtil.getDateTime(2015, 4, 15), row.getColumnValue("REGISTRATION_DATE"));
        Assert.assertEquals(MirebalaisLocations.CLINIC_REGISTRATION.name(), row.getColumnValue("REGISTRATION_LOCATION"));
        Assert.assertEquals("Married", row.getColumnValue("CIVIL_STATUS"));
        Assert.assertEquals("true", row.getColumnValue("REGISTRATION_RETROSPECTIVE"));
    }
}
