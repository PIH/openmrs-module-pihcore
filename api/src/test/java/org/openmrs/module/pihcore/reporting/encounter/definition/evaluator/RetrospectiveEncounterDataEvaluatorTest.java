package org.openmrs.module.pihcore.reporting.encounter.definition.evaluator;

import org.junit.Before;
import org.junit.Test;
import org.openmrs.Encounter;
import org.openmrs.contrib.testdata.builder.EncounterBuilder;
import org.openmrs.module.pihcore.metadata.Metadata;
import org.openmrs.module.pihcore.metadata.haiti.mirebalais.MirebalaisLocations;
import org.openmrs.module.pihcore.reporting.BaseReportTest;
import org.openmrs.module.pihcore.reporting.encounter.definition.RetrospectiveEncounterDataDefinition;
import org.openmrs.module.reporting.common.DateUtil;
import org.openmrs.module.reporting.data.encounter.EvaluatedEncounterData;
import org.openmrs.module.reporting.data.encounter.service.EncounterDataService;
import org.openmrs.module.reporting.evaluation.context.EncounterEvaluationContext;
import org.openmrs.module.reporting.query.encounter.EncounterIdSet;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class RetrospectiveEncounterDataEvaluatorTest extends BaseReportTest {

    @Autowired
    EncounterDataService encounterDataService;

    @Before
    public void setup() throws Exception {
        super.setup();
    }

    @Test
    public void shouldFindEncounterToBeRetrospective() throws Exception {
        EncounterBuilder eb = data.encounter();
        eb.patient(createPatient());
        eb.encounterDatetime(DateUtil.getDateTime(2015, 4, 15));
        eb.location(Metadata.lookup(MirebalaisLocations.CDI_KLINIK_EKSTEN_JENERAL));
        Encounter enc = eb.save();

        EncounterEvaluationContext context = new EncounterEvaluationContext();
        context.setBaseEncounters(new EncounterIdSet(enc.getEncounterId()));

        EvaluatedEncounterData res = encounterDataService.evaluate(new RetrospectiveEncounterDataDefinition(), context);
        assertTrue((Boolean) res.getData().get(enc.getEncounterId()));
    }

    @Test
    public void shouldFindEncounterNotToBeRetrospective() throws Exception {
        EncounterBuilder eb = data.encounter();
        eb.patient(createPatient());
        eb.encounterDatetime(new Date());
        eb.location(Metadata.lookup(MirebalaisLocations.CDI_KLINIK_EKSTEN_JENERAL));
        Encounter enc = eb.save();

        EncounterEvaluationContext context = new EncounterEvaluationContext();
        context.setBaseEncounters(new EncounterIdSet(enc.getEncounterId()));

        EvaluatedEncounterData res = encounterDataService.evaluate(new RetrospectiveEncounterDataDefinition(), context);
        assertFalse((Boolean) res.getData().get(enc.getEncounterId()));
    }
}
