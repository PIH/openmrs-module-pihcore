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

public class CheckInDataSetManagerTest extends EncounterDataSetManagerTest {

    @Autowired
    CheckInDataSetManager checkInDataSetManager;

    @Before
    @Override
    public void setup() throws Exception {
        super.setup();
        Patient p = createPatient("X3XK71");
        createRegistrationEncounter(p);
        createCheckInEncounter(p);
    }

    @Test
    public void testDataSet() throws Exception {
        DataSetDefinition dsd = checkInDataSetManager.constructDataSet();
        EvaluationContext context = new EvaluationContext();
        context.addParameterValue("startDate", DateUtil.getDateTime(2015, 1, 1));
        context.addParameterValue("endDate", DateUtil.getDateTime(2015, 12, 31));
        SimpleDataSet dataSet = (SimpleDataSet)dataSetDefinitionService.evaluate(dsd, context);
        DataSetRow row = dataSet.getRows().get(0);
        Assert.assertEquals(1, dataSet.getRows().size());
        Assert.assertEquals(16, dataSet.getMetaData().getColumnCount());
        Assert.assertEquals("X3XK71", row.getColumnValue("EMR_ID"));
        Assert.assertEquals(DateUtil.getDateTime(1977, 11, 23), row.getColumnValue("BIRTHDATE"));
        Assert.assertEquals(false, row.getColumnValue("BIRTHDATE_ESTIMATED"));
        Assert.assertEquals(37.4, row.getColumnValue("AGE_AT_CHECK_IN"));
        Assert.assertEquals("M", row.getColumnValue("GENDER"));
        Assert.assertEquals(DateUtil.getDateTime(2015, 4, 15), row.getColumnValue("CHECK_IN_DATE"));
        Assert.assertEquals(MirebalaisLocations.OUTPATIENT_CLINIC.name(), row.getColumnValue("CHECK_IN_LOCATION"));
        Assert.assertEquals("Malnutrition program", row.getColumnValue("TYPE_OF_VISIT"));
    }
}
