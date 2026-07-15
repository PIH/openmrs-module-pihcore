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
import org.junit.jupiter.api.Test;
import org.openmrs.Location;
import org.openmrs.Patient;
import org.openmrs.module.reporting.common.DateUtil;
import org.openmrs.module.reporting.dataset.DataSetRow;
import org.openmrs.module.reporting.dataset.SimpleDataSet;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.springframework.beans.factory.annotation.Autowired;

public class HaitiEncounterDataSetManagerTest extends EncounterDataSetManagerTest {

    @Autowired
    HaitiEncounterDataSetManager haitiEncounterDataSetManager;

    @Test
    public void testDataSet() throws Exception {
        Patient p = createPatient("X3XK71");
        Location biwoResepsyon = locationService.getLocation("Biwo Resepsyon");
        createRegistrationEncounter(p, createVisit(p, biwoResepsyon));

        DataSetDefinition dsd = haitiEncounterDataSetManager.constructDataSet();
        EvaluationContext context = new EvaluationContext();
        context.addParameterValue("startDate", DateUtil.getDateTime(2015, 1, 1));
        context.addParameterValue("endDate", DateUtil.getDateTime(2015, 12, 31));

        SimpleDataSet dataSet = (SimpleDataSet) dataSetDefinitionService.evaluate(dsd, context);
        Assert.assertEquals(1, dataSet.getRows().size());
        DataSetRow row = dataSet.getRows().get(0);
        Assert.assertEquals("X3XK71", row.getColumnValue("zlEmrId"));
    }

    @Test
    public void testDataSetFiltersByVisitLocationWhenSpecified() throws Exception {
        Patient p1 = createPatient("X1AAAD");
        Patient p2 = createPatient("X2CCC6");
        Location biwoResepsyon = locationService.getLocation("Biwo Resepsyon");
        Location lacolline = locationService.getLocation("Lacolline");
        createRegistrationEncounter(p1, createVisit(p1, biwoResepsyon));
        createRegistrationEncounter(p2, createVisit(p2, lacolline));

        DataSetDefinition dsd = haitiEncounterDataSetManager.constructDataSet();
        EvaluationContext context = new EvaluationContext();
        context.addParameterValue("startDate", DateUtil.getDateTime(2015, 1, 1));
        context.addParameterValue("endDate", DateUtil.getDateTime(2015, 12, 31));
        context.addParameterValue("visitLocation", biwoResepsyon);

        SimpleDataSet dataSet = (SimpleDataSet) dataSetDefinitionService.evaluate(dsd, context);
        Assert.assertEquals(1, dataSet.getRows().size());
        Assert.assertEquals("X1AAAD", dataSet.getRows().get(0).getColumnValue("zlEmrId"));
    }

    @Test
    public void testDataSetReturnsAllEncountersWhenVisitLocationNotSpecified() throws Exception {
        Patient p1 = createPatient("X1AAAD");
        Patient p2 = createPatient("X2CCC6");
        createRegistrationEncounter(p1, createVisit(p1, locationService.getLocation("Biwo Resepsyon")));
        createRegistrationEncounter(p2, createVisit(p2, locationService.getLocation("Lacolline")));

        DataSetDefinition dsd = haitiEncounterDataSetManager.constructDataSet();
        EvaluationContext context = new EvaluationContext();
        context.addParameterValue("startDate", DateUtil.getDateTime(2015, 1, 1));
        context.addParameterValue("endDate", DateUtil.getDateTime(2015, 12, 31));
        // visitLocation intentionally not set

        SimpleDataSet dataSet = (SimpleDataSet) dataSetDefinitionService.evaluate(dsd, context);
        Assert.assertEquals(2, dataSet.getRows().size());
    }

    @Test
    public void testDataSetExcludesEncountersWithNoVisitWhenVisitLocationSpecified() throws Exception {
        Patient p = createPatient("X1AAAD");
        createRegistrationEncounter(p); // no visit attached

        DataSetDefinition dsd = haitiEncounterDataSetManager.constructDataSet();
        EvaluationContext context = new EvaluationContext();
        context.addParameterValue("startDate", DateUtil.getDateTime(2015, 1, 1));
        context.addParameterValue("endDate", DateUtil.getDateTime(2015, 12, 31));
        context.addParameterValue("visitLocation", locationService.getLocation("Biwo Resepsyon"));

        SimpleDataSet dataSet = (SimpleDataSet) dataSetDefinitionService.evaluate(dsd, context);
        Assert.assertEquals(0, dataSet.getRows().size());
    }
}
