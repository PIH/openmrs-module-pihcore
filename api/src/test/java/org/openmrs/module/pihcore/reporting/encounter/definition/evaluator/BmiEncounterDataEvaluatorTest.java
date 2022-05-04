package org.openmrs.module.pihcore.reporting.encounter.definition.evaluator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openmrs.Encounter;
import org.openmrs.contrib.testdata.builder.EncounterBuilder;
import org.openmrs.module.pihcore.metadata.Metadata;
import org.openmrs.module.pihcore.reporting.BaseReportTest;
import org.openmrs.module.pihcore.reporting.encounter.definition.BmiEncounterDataDefinition;
import org.openmrs.module.reporting.common.DateUtil;
import org.openmrs.module.reporting.data.encounter.EvaluatedEncounterData;
import org.openmrs.module.reporting.data.encounter.service.EncounterDataService;
import org.openmrs.module.reporting.evaluation.context.EncounterEvaluationContext;
import org.openmrs.module.reporting.query.encounter.EncounterIdSet;
import org.springframework.beans.factory.annotation.Autowired;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertNull;

public class BmiEncounterDataEvaluatorTest extends BaseReportTest {

    @Autowired
    EncounterDataService encounterDataService;

    @BeforeEach
    public void setup() throws Exception {
        super.setup();
    }

    @Test
    public void shouldCalculateBmi() throws Exception {

        EncounterBuilder eb = data.encounter();
        eb.patient(createPatient());
        eb.encounterDatetime(DateUtil.getDateTime(2015, 4, 15));
        eb.location(locationService.getLocation("CDI Klinik Ekstèn Jeneral"));
        eb.encounterType(getVitalsEncounterType());
        eb.obs(Metadata.getConcept("PIH:WEIGHT (KG)"), 45.4);
        eb.obs(Metadata.getConcept("PIH:HEIGHT (CM)"), 152.4);
        Encounter enc = eb.save();

        EncounterEvaluationContext context = new EncounterEvaluationContext();
        context.setBaseEncounters(new EncounterIdSet(enc.getEncounterId()));

        EvaluatedEncounterData res = encounterDataService.evaluate(new BmiEncounterDataDefinition(), context);

        assertThat((Double) res.getData().get(enc.getEncounterId()), is(19.5));
    }

    @Test
    public void shouldReturnNullIfNoWeight() throws Exception {

        EncounterBuilder eb = data.encounter();
        eb.patient(createPatient());
        eb.encounterDatetime(DateUtil.getDateTime(2015, 4, 15));
        eb.location(locationService.getLocation("CDI Klinik Ekstèn Jeneral"));
        eb.encounterType(getVitalsEncounterType());
        eb.obs(Metadata.getConcept("PIH:WEIGHT (KG)"), 45.4);
        Encounter enc = eb.save();

        EncounterEvaluationContext context = new EncounterEvaluationContext();
        context.setBaseEncounters(new EncounterIdSet(enc.getEncounterId()));

        EvaluatedEncounterData res = encounterDataService.evaluate(new BmiEncounterDataDefinition(), context);

        assertNull(res.getData().get(enc.getEncounterId()));
    }

    @Test
    public void shouldReturnNullIfNoHeight() throws Exception {

        EncounterBuilder eb = data.encounter();
        eb.patient(createPatient());
        eb.encounterDatetime(DateUtil.getDateTime(2015, 4, 15));
        eb.location(locationService.getLocation("CDI Klinik Ekstèn Jeneral"));
        eb.encounterType(getVitalsEncounterType());
        eb.obs(Metadata.getConcept("PIH:HEIGHT (CM)"), 152.4);
        Encounter enc = eb.save();

        EncounterEvaluationContext context = new EncounterEvaluationContext();
        context.setBaseEncounters(new EncounterIdSet(enc.getEncounterId()));

        EvaluatedEncounterData res = encounterDataService.evaluate(new BmiEncounterDataDefinition(), context);

        assertNull(res.getData().get(enc.getEncounterId()));
    }

}
