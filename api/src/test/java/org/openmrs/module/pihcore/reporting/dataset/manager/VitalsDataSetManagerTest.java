package org.openmrs.module.pihcore.reporting.dataset.manager;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Encounter;
import org.openmrs.Patient;
import org.openmrs.Visit;
import org.openmrs.contrib.testdata.builder.EncounterBuilder;
import org.openmrs.contrib.testdata.builder.VisitBuilder;
import org.openmrs.module.pihcore.metadata.Metadata;
import org.openmrs.module.pihcore.metadata.core.EncounterTypes;
import org.openmrs.module.pihcore.metadata.haiti.mirebalais.MirebalaisLocations;
import org.openmrs.module.reporting.common.DateUtil;
import org.openmrs.module.reporting.dataset.DataSetRow;
import org.openmrs.module.reporting.dataset.SimpleDataSet;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;

public class VitalsDataSetManagerTest extends EncounterDataSetManagerTest {

    @Autowired
    private VitalsDataSetManager vitalsDataSetManager;

    private Visit visit;

    @Before
    @Override
    public void setup() throws Exception {
        super.setup();
        Patient p = createPatient("X3XK71");
        Encounter e = createVitalsEncounter(p);
        visit = createVisit(e);
    }

    @Test
    public void testDataSet() throws Exception {
        DataSetDefinition dsd = vitalsDataSetManager.constructDataSet();
        EvaluationContext context = new EvaluationContext();
        context.addParameterValue("startDate", DateUtil.getDateTime(2015, 1, 1));
        context.addParameterValue("endDate", DateUtil.getDateTime(2015, 12, 31));
        SimpleDataSet dataSet = (SimpleDataSet)dataSetDefinitionService.evaluate(dsd, context);
        DataSetRow row = dataSet.getRows().get(0);
        Assert.assertEquals(1, dataSet.getRows().size());
        Assert.assertEquals(23, dataSet.getMetaData().getColumnCount());
        Assert.assertEquals(visit.getId(), row.getColumnValue("VITALS_VISIT"));
        Assert.assertEquals(DateUtil.getDateTime(1977, 11, 23), row.getColumnValue("BIRTHDATE"));
        Assert.assertEquals(false, row.getColumnValue("BIRTHDATE_ESTIMATED"));
        //Assert.assertEquals(37.4, row.getColumnValue("AGE_AT_VITALS"));  // we are testing real-time in this case, so ignore this parameter here
        Assert.assertEquals("M", row.getColumnValue("GENDER"));
        Assert.assertEquals(45.4, row.getColumnValue("WEIGHT_KG"));
        Assert.assertEquals(152.4, row.getColumnValue("HEIGHT_CM"));
        Assert.assertEquals(19.5, row.getColumnValue("BMI"));
        Assert.assertEquals(25.0, row.getColumnValue("MUAC"));
        Assert.assertEquals(35.5, row.getColumnValue("TEMP_C"));
        Assert.assertEquals(120.0, row.getColumnValue("HEART_RATE"));
        Assert.assertEquals(80.0, row.getColumnValue("RESP_RATE"));
        Assert.assertEquals(90.0, row.getColumnValue("SYS_BP"));
        Assert.assertEquals(110.0, row.getColumnValue("DIA_BP"));
        Assert.assertEquals(95.0, row.getColumnValue("O2_SAT"));
        Assert.assertEquals("headache", row.getColumnValue("CHIEF_COMPLAINT"));
        Assert.assertEquals("false", row.getColumnValue("VITALS_RETROSPECTIVE"));

    }

    private Encounter createVitalsEncounter(Patient p) {
        EncounterBuilder eb = data.encounter();
        eb.patient(p);
        eb.encounterDatetime(new Date());
        eb.location(Metadata.lookup(MirebalaisLocations.CDI_KLINIK_EKSTEN_JENERAL));
        eb.encounterType(Metadata.lookup(EncounterTypes.VITALS));

        eb.obs(Metadata.getConcept("PIH:WEIGHT (KG)"), 45.4);
        eb.obs(Metadata.getConcept("PIH:HEIGHT (CM)"), 152.4);
        eb.obs(Metadata.getConcept("PIH:MIDDLE UPPER ARM CIRCUMFERENCE (MM)"), 25);
        eb.obs(Metadata.getConcept("PIH:TEMPERATURE (C)"), 35.5);
        eb.obs(Metadata.getConcept("PIH:PULSE"), 120);
        eb.obs(Metadata.getConcept("PIH:RESPIRATORY RATE"), 80);
        eb.obs(Metadata.getConcept("PIH:SYSTOLIC BLOOD PRESSURE"), 90);
        eb.obs(Metadata.getConcept("PIH:DIASTOLIC BLOOD PRESSURE"), 110);
        eb.obs(Metadata.getConcept("PIH:BLOOD OXYGEN SATURATION"), 95);
        eb.obs(Metadata.getConcept("CIEL:160531"), "headache");

        return eb.save();
    }

    private Visit createVisit(Encounter enc) {
        VisitBuilder vb = data.visit();
        vb.patient(enc.getPatient());
        vb.started(enc.getEncounterDatetime());
        vb.location(enc.getLocation());
        vb.encounter(enc);
        vb.visitType(1);

        return vb.save();
    }

}
