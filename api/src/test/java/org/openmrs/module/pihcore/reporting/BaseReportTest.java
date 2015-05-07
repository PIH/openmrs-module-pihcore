package org.openmrs.module.pihcore.reporting;

import org.junit.Before;
import org.openmrs.contrib.testdata.TestDataManager;
import org.openmrs.module.emrapi.EmrApiProperties;
import org.openmrs.module.reporting.report.definition.service.ReportDefinitionService;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.openmrs.test.SkipBaseSetup;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Sets up basic Mirebalais metadata (instead of the standardTestDataset.xml from openmrs-core)
 */
@SkipBaseSetup // because of TRUNK-4051, this annotation will not be picked up, and you need to declare this on your concrete subclass
public abstract class BaseReportTest extends BaseModuleContextSensitiveTest {

    @Autowired
    protected ReportDefinitionService reportDefinitionService;

    @Autowired
    protected EmrApiProperties emrApiProperties;

    @Autowired
    protected TestDataManager data;

    @Before
    public void setup() throws Exception {
        executeDataSet("org/openmrs/module/pihcore/coreMetadata.xml");
        authenticate();
    }

}
