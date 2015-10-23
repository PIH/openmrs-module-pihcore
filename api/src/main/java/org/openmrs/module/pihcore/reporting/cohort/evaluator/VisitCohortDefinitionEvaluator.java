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
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.openmrs.Cohort;
import org.openmrs.Visit;
import org.openmrs.annotation.Handler;
import org.openmrs.api.context.Context;
import org.openmrs.api.db.hibernate.DbSessionFactory;
import org.openmrs.module.pihcore.reporting.cohort.definition.VisitCohortDefinition;
import org.openmrs.module.reporting.cohort.EvaluatedCohort;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.evaluator.CohortDefinitionEvaluator;
import org.openmrs.module.reporting.common.DateUtil;
import org.openmrs.module.reporting.common.TimeQualifier;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Handler(supports = VisitCohortDefinition.class)
public class VisitCohortDefinitionEvaluator implements CohortDefinitionEvaluator {

    @Autowired
    DbSessionFactory sessionFactory;

    @Override
    public EvaluatedCohort evaluate(CohortDefinition cohortDefinition, EvaluationContext context) throws EvaluationException {
        VisitCohortDefinition cd = (VisitCohortDefinition) cohortDefinition;

        // TODO need to exclude voided patients (just in case there are non-voided visits for voided patients)

        if (cd.getTimeQualifier() != TimeQualifier.ANY) {
            throw new IllegalArgumentException("Currently only timeQualifier=ANY is implemented");
        }

        Criteria crit = sessionFactory.getCurrentSession().createCriteria(Visit.class);
        crit.add(Restrictions.eq("voided", false));
        if (cd.getStartedOnOrAfter() != null) {
            crit.add(Restrictions.ge("startDatetime", cd.getStartedOnOrAfter()));
        }
        if (cd.getStartedOnOrBefore() != null) {
            crit.add(Restrictions.le("startDatetime", DateUtil.getEndOfDayIfTimeExcluded(cd.getStartedOnOrBefore())));
        }
        if (cd.getStoppedOnOrAfter() != null) {
            crit.add(Restrictions.ge("stopDatetime", cd.getStoppedOnOrAfter()));
        }
        if (cd.getStoppedOnOrBefore() != null) {
            crit.add(Restrictions.le("stopDatetime", DateUtil.getEndOfDayIfTimeExcluded(cd.getStoppedOnOrBefore())));
        }
        if (cd.getActiveOnOrAfter() != null) {
            crit.add(Restrictions.or(
                    Restrictions.ge("stopDatetime", cd.getActiveOnOrAfter()),
                    Restrictions.isNull("stopDatetime")));
        }
        if (cd.getActiveOnOrBefore() != null) {
            crit.add(Restrictions.le("startDatetime", DateUtil.getEndOfDayIfTimeExcluded(cd.getActiveOnOrBefore())));
        }
        if (cd.getVisitTypeList() != null) {
            crit.add(Restrictions.in("visitType", cd.getVisitTypeList()));
        }
        if (cd.getLocationList() != null) {
            crit.add(Restrictions.in("location", cd.getLocationList()));
        }
        if (cd.getIndicationList() != null) {
            crit.add(Restrictions.in("indication", cd.getIndicationList()));
        }
        if (cd.getCreatedBy() != null) {
            crit.add(Restrictions.eq("creator", cd.getCreatedBy()));
        }
        if (cd.getCreatedOnOrAfter() != null) {
            crit.add(Restrictions.ge("dateCreated", cd.getCreatedOnOrAfter()));
        }
        if (cd.getCreatedOnOrBefore() != null) {
            crit.add(Restrictions.le("dateCreated", DateUtil.getEndOfDayIfTimeExcluded(cd.getCreatedOnOrBefore())));
        }
        if (cd.isActive() != null) {
            if(cd.isActive()) {
                crit.add(Restrictions.isNull("stopDatetime"));
            }
        }
        crit.setProjection(Projections.distinct(Projections.property("patient.id")));

        Cohort c = new Cohort();
        for (Integer i : (List<Integer>) crit.list()){
            c.addMember(i);
        }

        if (cd.getReturnInverse()) {
            Cohort baseCohort = context.getBaseCohort();
            if (baseCohort == null) {
                baseCohort = Context.getPatientSetService().getAllPatients();
            }
            c = Cohort.subtract(baseCohort, c);
        }

        return new EvaluatedCohort(c, cohortDefinition, context);
    }

}
