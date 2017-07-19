package org.openmrs.module.pihcore.reporting.cohort.evaluator;


import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.openmrs.Cohort;
import org.openmrs.EncounterType;
import org.openmrs.annotation.Handler;
import org.openmrs.module.emrapi.EmrApiProperties;
import org.openmrs.module.pihcore.reporting.cohort.definition.ActiveVisitsCohortDefinition;
import org.openmrs.module.pihcore.reporting.cohort.definition.InpatientLocationCohortDefinition;
import org.openmrs.module.reporting.cohort.EvaluatedCohort;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.evaluator.CohortDefinitionEvaluator;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.List;

@Handler(supports = ActiveVisitsCohortDefinition.class)
public class ActiveVisitsCohortDefinitionEvaluator implements CohortDefinitionEvaluator {

    @Autowired
    EmrApiProperties emrApiProperties;

    @Autowired
    private SessionFactory sessionFactory;

    @Override
    public EvaluatedCohort evaluate(CohortDefinition cohortDefinition, EvaluationContext context) throws EvaluationException {

        ActiveVisitsCohortDefinition cd = (ActiveVisitsCohortDefinition) cohortDefinition;

        Date onDate = cd.getEffectiveDate();
        if (onDate == null) {
            onDate = new Date();
        }
        EncounterType admissionEncounterType = emrApiProperties.getCheckInEncounterType();

        StringBuilder sb = new StringBuilder("select distinct v.patient_id " +
                "from visit v");

        sb.append(" where v.voided = false");
        sb.append(" and v.date_started <= :onDate ");
        sb.append(" and (v.date_stopped is null or v.date_stopped > :onDate) ");

        SQLQuery query = sessionFactory.getCurrentSession().createSQLQuery(sb.toString());
        query.setTimestamp("onDate", onDate);

        Cohort c = new Cohort();
        for (Integer i : (List<Integer>) query.list()){
            c.addMember(i);
        }
        return new EvaluatedCohort(c, cohortDefinition, context);
    }
}
