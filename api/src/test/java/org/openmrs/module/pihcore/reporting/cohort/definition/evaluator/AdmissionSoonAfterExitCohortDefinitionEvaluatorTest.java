package org.openmrs.module.pihcore.reporting.cohort.definition.evaluator;

import org.junit.jupiter.api.Test;
import org.openmrs.module.pihcore.reporting.cohort.definition.AdmissionSoonAfterExitCohortDefinition;
import org.openmrs.module.pihcore.reporting.BaseInpatientReportTest;
import org.openmrs.module.reporting.cohort.EvaluatedCohort;
import org.openmrs.module.reporting.cohort.definition.service.CohortDefinitionService;
import org.openmrs.module.reporting.common.DateUtil;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.test.SkipBaseSetup;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.openmrs.module.pihcore.reporting.ReportingMatchers.isCohortWithExactlyIds;

@SkipBaseSetup
public class AdmissionSoonAfterExitCohortDefinitionEvaluatorTest extends BaseInpatientReportTest {

    @Autowired
    CohortDefinitionService cohortDefinitionService;

    @Test
    public void testEvaluate() throws Exception {
        Date startDate = DateUtil.parseDate("2013-10-03 00:00:00", "yyyy-MM-dd HH:mm:ss");
        Date endDate = DateUtil.parseDate("2013-10-03 23:59:59", "yyyy-MM-dd HH:mm:ss");

        AdmissionSoonAfterExitCohortDefinition definition = new AdmissionSoonAfterExitCohortDefinition();
        definition.setOnOrAfter(startDate);
        definition.setOnOrBefore(endDate);

        EvaluatedCohort result = cohortDefinitionService.evaluate(definition, new EvaluationContext());
        assertThat(result, isCohortWithExactlyIds(patient3.getId()));
    }

}
