package org.openmrs.module.pihcore.reporting.encounter.definition.evaluator;

import org.junit.Before;
import org.junit.Test;
import org.openmrs.Concept;
import org.openmrs.Encounter;
import org.openmrs.contrib.testdata.builder.EncounterBuilder;
import org.openmrs.module.pihcore.metadata.Metadata;
import org.openmrs.module.pihcore.reporting.BaseReportTest;
import org.openmrs.module.pihcore.reporting.converter.CodedObsConverter;
import org.openmrs.module.pihcore.reporting.encounter.definition.MultipleObsForEncounterDataDefinition;
import org.openmrs.module.reporting.cohort.PatientIdSet;
import org.openmrs.module.reporting.common.DateUtil;
import org.openmrs.module.reporting.dataset.DataSetRow;
import org.openmrs.module.reporting.dataset.SimpleDataSet;
import org.openmrs.module.reporting.dataset.definition.EncounterDataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.service.DataSetDefinitionService;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.springframework.beans.factory.annotation.Autowired;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

public class MultipleObsForEncounterDataEvaluatorTest extends BaseReportTest {

    @Autowired
    DataSetDefinitionService dataSetDefinitionService;

    @Before
    public void setup() throws Exception {
        super.setup();
    }

    @Test
    public void shouldCalculateSymptomPresentAndAbsent() throws Exception {

        EncounterBuilder eb = data.encounter();
        eb.patient(createPatient());
        eb.encounterDatetime(DateUtil.getDateTime(2015, 4, 15));
        eb.location(locationService.getLocation("CDI Klinik Ekstèn Jeneral"));
        eb.encounterType(getVitalsEncounterType());

        // We'll simulate "symptom present" by using the diagnosis concept, "symptom absent" with "reason for visit" concept
        Concept diagnosisConcept = Metadata.getConcept("PIH:DIAGNOSIS");
        Concept reasonForVisitConcept = Metadata.getConcept("Reason for HUM visit");
        Concept malariaConcept = Metadata.getConcept("PIH:MALARIA");
        Concept dengueConcept = Metadata.getConcept("PIH:Dengue");
        Concept typhoidConcept = Metadata.getConcept("PIH:TYPHOID FEVER");
        Concept tbConcept = Metadata.getConcept("PIH:TUBERCULOSIS");

        eb.obs(diagnosisConcept, malariaConcept);
        eb.obs(diagnosisConcept, dengueConcept);
        eb.obs(reasonForVisitConcept, typhoidConcept);
        Encounter enc = eb.save();

        EvaluationContext context = new EvaluationContext();
        context.setBaseCohort(new PatientIdSet(enc.getPatient().getPatientId()));

        EncounterDataSetDefinition dsd = new EncounterDataSetDefinition();
        addSymptomPresentOrAbsentColumn(dsd, "malaria", "PIH:DIAGNOSIS",
                "PIH:Reason for HUM Visit", "PIH:MALARIA",
                "Diagnosis", "Reason");
        addSymptomPresentOrAbsentColumn(dsd, "dengue", "PIH:DIAGNOSIS",
                "PIH:Reason for HUM Visit", "PIH:Dengue",
                "Diagnosis", "Reason");
        addSymptomPresentOrAbsentColumn(dsd, "typhoid", "PIH:DIAGNOSIS",
                "PIH:Reason for HUM Visit", "PIH:TYPHOID FEVER",
                "Diagnosis", "Reason");
        addSymptomPresentOrAbsentColumn(dsd, "tb", "PIH:DIAGNOSIS",
                "PIH:Reason for HUM Visit", "PIH:TUBERCULOSIS",
                "Diagnosis", "Reason");

        SimpleDataSet ds = (SimpleDataSet)dataSetDefinitionService.evaluate(dsd, context);
        DataSetRow row = ds.getRows().get(0);
        assertThat(row.getColumnValue("malaria"), is("Diagnosis"));
        assertThat(row.getColumnValue("dengue"), is("Diagnosis"));
        assertThat(row.getColumnValue("typhoid"), is("Reason"));
        assertNull(row.getColumnValue("tb"));
    }

    protected void addSymptomPresentOrAbsentColumn(EncounterDataSetDefinition dsd, String columnName,
            String symptomPresentQuestion, String symptomAbsentQuestion, String symptomAnswer,
            Object valueIfPresent, Object valueIfAbsent) {

        Concept presentConcept = Metadata.getConcept(symptomPresentQuestion);
        Concept absentConcept = Metadata.getConcept(symptomAbsentQuestion);
        Concept answerConcept = Metadata.getConcept(symptomAnswer);

        MultipleObsForEncounterDataDefinition d = new MultipleObsForEncounterDataDefinition();
        d.addQuestion(presentConcept);
        d.addQuestion(absentConcept);
        CodedObsConverter converter = new CodedObsConverter();
        converter.addOption(presentConcept.getUuid(), answerConcept.getUuid(), valueIfPresent);
        converter.addOption(absentConcept.getUuid(), answerConcept.getUuid(), valueIfAbsent);
        dsd.addColumn(columnName.toUpperCase(), d, "", converter);
    }
}
