package org.openmrs.module.pihcore.reporting.cohort.evaluator;


import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.openmrs.Cohort;
import org.openmrs.Encounter;
import org.openmrs.EncounterType;
import org.openmrs.Visit;
import org.openmrs.annotation.Handler;
import org.openmrs.module.emrapi.EmrApiProperties;
import org.openmrs.module.pihcore.reporting.cohort.definition.ActiveVisitsWithEncountersCohortDefinition;
import org.openmrs.module.reporting.cohort.EvaluatedCohort;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.evaluator.CohortDefinitionEvaluator;
import org.openmrs.module.reporting.common.TimeQualifier;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.evaluation.querybuilder.HqlQueryBuilder;
import org.openmrs.module.reporting.evaluation.service.EvaluationService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Handler(supports = ActiveVisitsWithEncountersCohortDefinition.class)
public class ActiveVisitsWithEncountersCohortDefinitionEvaluator implements CohortDefinitionEvaluator {

    @Autowired
    EmrApiProperties emrApiProperties;

    @Autowired
    EvaluationService evaluationService;

    @Autowired
    private SessionFactory sessionFactory;

    @Override
    public EvaluatedCohort evaluate(CohortDefinition cohortDefinition, EvaluationContext context) throws EvaluationException {

        ActiveVisitsWithEncountersCohortDefinition cd = (ActiveVisitsWithEncountersCohortDefinition) cohortDefinition;

        HqlQueryBuilder encQuery = new HqlQueryBuilder();
        encQuery.select("e.patient.id, e.encounterId");
        encQuery.from(Encounter.class, "e");
        encQuery.whereEqual("e.voided", false);
        encQuery.whereEqual("e.visit.voided", false);
        encQuery.wherePatientIn("e.patient.id", context);
        if (cd.isActive() != null) {
            encQuery.whereNull("e.visit.stopDatetime");
        }

        if (cd.getWhichEncounter() == TimeQualifier.LAST) {
            encQuery.orderAsc("e.encounterDatetime").orderAsc("e.dateCreated"); // Ascending since we take last value found
        }
        else if (cd.getWhichEncounter() == TimeQualifier.FIRST) {
            encQuery.orderDesc("e.encounterDatetime").orderDesc("e.dateCreated"); // Descending since we take last value found
        }
        Set<Integer> encountersToInclude = new HashSet<Integer>();
        if (cd.getWhichEncounter() == TimeQualifier.LAST || cd.getWhichEncounter() == TimeQualifier.FIRST) {
            Map<Integer, Integer> encountersByPatient = evaluationService.evaluateToMap(encQuery, Integer.class, Integer.class, context);
            encountersToInclude.addAll(encountersByPatient.values());
        }
        else {
            List<Object[]> rawResults = evaluationService.evaluateToList(encQuery, context);
            for (Object[] resultRow : rawResults) {
                encountersToInclude.add((Integer)resultRow[1]);
            }
        }

        HqlQueryBuilder q = new HqlQueryBuilder();
        q.select("e.patient.id");
        q.from(Encounter.class, "e");
        q.whereIn("e.location", cd.getLocationList());
        q.whereIdIn("e.encounterId", encountersToInclude);

        Cohort c = new Cohort(evaluationService.evaluateToList(q, Integer.class, context));
        return new EvaluatedCohort(c, cohortDefinition, context);
    }
}
