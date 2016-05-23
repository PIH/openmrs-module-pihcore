package org.openmrs.module.pihcore.reporting.dataset.manager;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.openmrs.Patient;
import org.openmrs.module.reporting.common.DateUtil;
import org.openmrs.module.reporting.dataset.SimpleDataSet;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.springframework.beans.factory.annotation.Autowired;

@Ignore
public class DiagnosesDataSetManagerTest extends ObsDataSetManagerTest {

    @Autowired
    private DiagnosesDataSetManager diagnosesDataSetManager;

    @Before
    @Override
    public void setup() throws Exception {
        super.setup();
        Patient p = createPatient("X3XK71");
        createConsultEncounter(p);
    }

    @Test
    public void testDataSet() throws Exception {
        DataSetDefinition dsd = diagnosesDataSetManager.constructDataSet();
        EvaluationContext context = new EvaluationContext();
        context.addParameterValue("startDate", DateUtil.getDateTime(2015, 1, 1));
        context.addParameterValue("endDate", DateUtil.getDateTime(2015, 12, 31));
        SimpleDataSet dataSet = (SimpleDataSet)dataSetDefinitionService.evaluate(dsd, context);

        // TODO: get this to test right parameters and work?
    }

}
