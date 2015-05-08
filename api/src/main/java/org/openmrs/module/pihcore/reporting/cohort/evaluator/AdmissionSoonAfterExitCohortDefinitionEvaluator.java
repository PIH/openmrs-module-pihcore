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
import org.hibernate.SessionFactory;
import org.openmrs.Cohort;
import org.openmrs.annotation.Handler;
import org.openmrs.module.emrapi.EmrApiProperties;
import org.openmrs.module.pihcore.reporting.cohort.definition.AdmissionSoonAfterExitCohortDefinition;
import org.openmrs.module.reporting.cohort.EvaluatedCohort;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.evaluator.CohortDefinitionEvaluator;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 *
 */
@Handler(supports = AdmissionSoonAfterExitCohortDefinition.class)
public class AdmissionSoonAfterExitCohortDefinitionEvaluator implements CohortDefinitionEvaluator {

    @Autowired
    SessionFactory sessionFactory;

    @Autowired
    private EmrApiProperties emrApiProperties;

    @Override
    public EvaluatedCohort evaluate(CohortDefinition cohortDefinition, EvaluationContext context) throws EvaluationException {
        AdmissionSoonAfterExitCohortDefinition cd = (AdmissionSoonAfterExitCohortDefinition) cohortDefinition;

        String sql = "select distinct admit.patient_id \n" +
                "from encounter admit \n" +
                "inner join encounter discharge \n" +
                " on discharge.patient_id = admit.patient_id \n" +
                " and discharge.voided = false \n" +
                " and discharge.encounter_type = :dischargeEncounterType \n" +
                // was mysql-specific " and discharge.encounter_datetime >= DATE_SUB(admit.encounter_datetime, INTERVAL :windowInDays DAY) \n" +
                " and discharge.encounter_datetime >= TIMESTAMPADD(DAY, -:windowInDays, admit.encounter_datetime) \n" +
                " and discharge.encounter_datetime < admit.encounter_datetime \n" +
                "where admit.voided = false \n" +
                " and admit.encounter_type = :admitEncounterType \n" +
                " and admit.encounter_datetime between :onOrAfter and :onOrBefore ";
        if (cd.getAdmissionLocation() != null) {
            sql += " and admit.location_id = :admissionLocation ";
        }

        SQLQuery query = sessionFactory.getCurrentSession().createSQLQuery(sql);
        query.setInteger("admitEncounterType", emrApiProperties.getAdmissionEncounterType().getId());
        query.setInteger("dischargeEncounterType", emrApiProperties.getExitFromInpatientEncounterType().getId());
        query.setTimestamp("onOrAfter", cd.getOnOrAfter());
        query.setTimestamp("onOrBefore", cd.getOnOrBefore());
        query.setInteger("windowInDays", cd.getWindowInDays());
        if (cd.getAdmissionLocation() != null) {
            query.setInteger("admissionLocation", cd.getAdmissionLocation().getId());
        }

        Cohort c = new Cohort();
        for (Integer i : (List<Integer>) query.list()){
            c.addMember(i);
        }
        return new EvaluatedCohort(c, cohortDefinition, context);
    }

}
