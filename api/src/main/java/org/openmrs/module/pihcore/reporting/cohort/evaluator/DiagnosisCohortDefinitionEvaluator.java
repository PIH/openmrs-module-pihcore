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

import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.openmrs.Cohort;
import org.openmrs.Obs;
import org.openmrs.annotation.Handler;
import org.openmrs.module.pihcore.reporting.cohort.definition.DiagnosisCohortDefinition;
import org.openmrs.module.emrapi.EmrApiProperties;
import org.openmrs.module.emrapi.diagnosis.DiagnosisMetadata;
import org.openmrs.module.reporting.cohort.EvaluatedCohort;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.evaluator.CohortDefinitionEvaluator;
import org.openmrs.module.reporting.common.DateUtil;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Handler(supports = DiagnosisCohortDefinition.class)
public class DiagnosisCohortDefinitionEvaluator implements CohortDefinitionEvaluator {

    @Autowired
    private SessionFactory sessionFactory;

    @Autowired
    private EmrApiProperties emrApiProperties;

    @Override
    public EvaluatedCohort evaluate(CohortDefinition cohortDefinition, EvaluationContext context) throws EvaluationException {
        DiagnosisMetadata dmd = emrApiProperties.getDiagnosisMetadata();

        DiagnosisCohortDefinition cd = (DiagnosisCohortDefinition) cohortDefinition;

        if (cd.isIncludeAllCodedDiagnoses() && cd.getCodedDiagnoses() != null) {
            throw new IllegalArgumentException("Cannot specify both includeAllCodedDiagnoses, and specific coded diagnoses");
        }

        Criteria crit = sessionFactory.getCurrentSession().createCriteria(Obs.class, "obsgroup");
        crit.setProjection(Projections.distinct(Projections.property("person.id")));

        crit.add(Restrictions.eq("voided", false));
        crit.createCriteria("person").add(Restrictions.eq("voided", false));

        // we're looking for an obs group whose grouping concept is VISIT DIAGNOSES (or the equivalent)
        crit.add(Restrictions.eq("concept", dmd.getDiagnosisSetConcept()));

        if (cd.getOnOrAfter() != null) {
            crit.add(Restrictions.ge("obsDatetime", cd.getOnOrAfter()));
        }
        if (cd.getOnOrBefore() != null) {
            crit.add(Restrictions.le("obsDatetime", DateUtil.getEndOfDayIfTimeExcluded(cd.getOnOrBefore())));
        }

        if (cd.getDiagnosisOrder() != null) {
            DetachedCriteria orderClause = DetachedCriteria.forClass(Obs.class, "orderObs");
            orderClause.add(Restrictions.eq("voided", false));
            orderClause.add(Restrictions.eq("concept", dmd.getDiagnosisOrderConcept()));
            orderClause.add(Restrictions.eq("valueCoded", dmd.getConceptFor(cd.getDiagnosisOrder())));
            orderClause.add(Restrictions.eqProperty("obsGroup", "obsgroup.id"));
            orderClause.setProjection(Projections.property("id"));
            crit.add(Subqueries.exists(orderClause));
        }

        if (cd.getCertainty() != null) {
            DetachedCriteria certaintyClause = DetachedCriteria.forClass(Obs.class, "certaintyObs");
            certaintyClause.add(Restrictions.eq("voided", false));
            certaintyClause.add(Restrictions.eq("concept", dmd.getDiagnosisCertaintyConcept()));
            certaintyClause.add(Restrictions.eq("valueCoded", dmd.getConceptFor(cd.getCertainty())));
            certaintyClause.add(Restrictions.eqProperty("obsGroup", "obsgroup.id"));
            certaintyClause.setProjection(Projections.property("id"));
            crit.add(Subqueries.exists(certaintyClause));
        }

        if (cd.isIncludeAllCodedDiagnoses()) {
            // Note: since every diagnosis is either coded or non-coded if they want to include all coded *and* non-coded
            // diagnoses, we can just ignore both clauses
            if (!cd.isIncludeAllNonCodedDiagnoses()) {
                DetachedCriteria anyCodedClause = DetachedCriteria.forClass(Obs.class, "codedDiagnosisObs");
                anyCodedClause.add(Restrictions.eq("voided", false));
                anyCodedClause.add(Restrictions.eq("concept", dmd.getCodedDiagnosisConcept()));
                anyCodedClause.add(Restrictions.eqProperty("obsGroup", "obsgroup.id"));
                anyCodedClause.setProjection(Projections.property("id"));
                crit.add(Subqueries.exists(anyCodedClause));
            }
        }
        else if (cd.getCodedDiagnoses() != null || cd.getExcludeCodedDiagnoses() != null) {
            if (cd.isIncludeAllNonCodedDiagnoses()) {
                throw new IllegalArgumentException("Not Yet Implemented: handling both all-non-coded and specific coded diagnoses together");
            }
            if (!cd.isIncludeAllNonCodedDiagnoses()) {
                DetachedCriteria specificCodedClause = DetachedCriteria.forClass(Obs.class, "codedDiagnosisObs");
                specificCodedClause.add(Restrictions.eq("voided", false));
                specificCodedClause.add(Restrictions.eq("concept", dmd.getCodedDiagnosisConcept()));
                if (cd.getCodedDiagnoses() != null) {
                    specificCodedClause.add(Restrictions.in("valueCoded", cd.getCodedDiagnoses()));
                }
                if (cd.getExcludeCodedDiagnoses() != null) {
                    specificCodedClause.add(Restrictions.not(Restrictions.in("valueCoded", cd.getExcludeCodedDiagnoses())));
                }
                specificCodedClause.add(Restrictions.eqProperty("obsGroup", "obsgroup.id"));
                specificCodedClause.setProjection(Projections.property("id"));
                crit.add(Subqueries.exists(specificCodedClause));
            }
        }
        else if (cd.isIncludeAllNonCodedDiagnoses()) {
            DetachedCriteria anyNonCodedClause = DetachedCriteria.forClass(Obs.class, "nonCodedDiagnosisObs");
            anyNonCodedClause.add(Restrictions.eq("voided", false));
            anyNonCodedClause.add(Restrictions.eq("concept", dmd.getNonCodedDiagnosisConcept()));
            anyNonCodedClause.add(Restrictions.eqProperty("obsGroup", "obsgroup.id"));
            anyNonCodedClause.setProjection(Projections.property("id"));
            crit.add(Subqueries.exists(anyNonCodedClause));
        }

        Cohort c = new Cohort();
        for (Integer i : (List<Integer>) crit.list()){
            c.addMember(i);
        }
        return new EvaluatedCohort(c, cohortDefinition, context);
    }

}
