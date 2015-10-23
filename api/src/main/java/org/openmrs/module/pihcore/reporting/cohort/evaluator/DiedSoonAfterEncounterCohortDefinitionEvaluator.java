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

package org.openmrs.module.pihcore.reporting.cohort.evaluator;

import org.hibernate.SQLQuery;
import org.openmrs.Cohort;
import org.openmrs.annotation.Handler;
import org.openmrs.api.db.hibernate.DbSessionFactory;
import org.openmrs.module.pihcore.reporting.cohort.definition.DiedSoonAfterEncounterCohortDefinition;
import org.openmrs.module.reporting.cohort.EvaluatedCohort;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.evaluator.CohortDefinitionEvaluator;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Handler(supports = DiedSoonAfterEncounterCohortDefinition.class)
public class DiedSoonAfterEncounterCohortDefinitionEvaluator implements CohortDefinitionEvaluator {

    @Autowired
    private DbSessionFactory sessionFactory;

    @Override
    public EvaluatedCohort evaluate(CohortDefinition def, EvaluationContext context) throws EvaluationException {
        DiedSoonAfterEncounterCohortDefinition cohortDefinition = (DiedSoonAfterEncounterCohortDefinition) def;
        String sql = " select distinct p.patient_id \n" +
                " from patient p \n" +
                " inner join person per \n" +
                "   on p.patient_id = per.person_id \n" +
                "   and per.voided = false \n" +
                " inner join encounter admit \n" +
                "   on admit.patient_id = p.patient_id \n" +
                "   and admit.encounter_type = :encounterType \n" +
                // was mysql-specific "   and admit.encounter_datetime > DATE_SUB(per.death_date, INTERVAL :windowInHours HOUR) \n" +
                "   and admit.encounter_datetime > TIMESTAMPADD(HOUR, -:windowInHours, per.death_date) \n" +
                " where per.death_date between :diedOnOrAfter and :diedOnOrBefore \n" +
                "   and p.voided = false ";

        SQLQuery query = sessionFactory.getCurrentSession().createSQLQuery(sql);
        query.setTimestamp("diedOnOrAfter", cohortDefinition.getDiedOnOrAfter());
        query.setTimestamp("diedOnOrBefore", cohortDefinition.getDiedOnOrBefore());
        query.setInteger("encounterType", cohortDefinition.getEncounterType().getId());
        query.setInteger("windowInHours", cohortDefinition.getWindowInHours());

        Cohort c = new Cohort();
        for (Integer i : (List<Integer>) query.list()){
            c.addMember(i);
        }
        return new EvaluatedCohort(c, cohortDefinition, context);
    }

}
