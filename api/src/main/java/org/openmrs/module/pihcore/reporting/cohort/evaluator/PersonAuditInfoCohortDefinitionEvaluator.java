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
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.openmrs.Cohort;
import org.openmrs.Patient;
import org.openmrs.annotation.Handler;
import org.openmrs.module.pihcore.reporting.cohort.definition.PersonAuditInfoCohortDefinition;
import org.openmrs.module.reporting.cohort.EvaluatedCohort;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.evaluator.CohortDefinitionEvaluator;
import org.openmrs.module.reporting.common.DateUtil;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Handler(supports = PersonAuditInfoCohortDefinition.class)
public class PersonAuditInfoCohortDefinitionEvaluator implements CohortDefinitionEvaluator {

    @Autowired
    SessionFactory sessionFactory;

    @Override
    public EvaluatedCohort evaluate(CohortDefinition cohortDefinition, EvaluationContext context) throws EvaluationException {
        PersonAuditInfoCohortDefinition cd = (PersonAuditInfoCohortDefinition) cohortDefinition;

        Criteria crit = sessionFactory.getCurrentSession().createCriteria(Patient.class);
        crit.setProjection(Projections.property("id"));

        if (!cd.isIncludeVoided()) {
            crit.add(Restrictions.eq("voided", false));
        }

        if (cd.getCreatedOnOrAfter() != null) {
            crit.add(Restrictions.ge("dateCreated", cd.getCreatedOnOrAfter()));
        }

        if (cd.getCreatedOnOrBefore() != null) {
            crit.add(Restrictions.le("dateCreated", DateUtil.getEndOfDayIfTimeExcluded(cd.getCreatedOnOrBefore())));
        }

        if (cd.getCreatedByUsers() != null) {
            crit.add(Restrictions.in("creator", cd.getCreatedByUsers()));
        }

        if (cd.getChangedOnOrAfter() != null) {
            crit.add(Restrictions.ge("dateChanged", cd.getChangedOnOrAfter()));
        }

        if (cd.getChangedOnOrBefore() != null) {
            crit.add(Restrictions.le("dateChanged", DateUtil.getEndOfDayIfTimeExcluded(cd.getChangedOnOrBefore())));
        }

        if (cd.getChangedByUsers() != null) {
            crit.add(Restrictions.in("changedBy", cd.getChangedByUsers()));
        }

        if (cd.getVoidedOnOrAfter() != null) {
            crit.add(Restrictions.ge("dateVoided", cd.getVoidedOnOrAfter()));
        }

        if (cd.getVoidedOnOrBefore() != null) {
            crit.add(Restrictions.le("dateVoided", DateUtil.getEndOfDayIfTimeExcluded(cd.getVoidedOnOrBefore())));
        }

        if (cd.getVoidedByUsers() != null) {
            crit.add(Restrictions.in("voidedBy", cd.getVoidedByUsers()));
        }

        Cohort c = new Cohort();
        for (Integer personId : (List<Integer>) crit.list()) {
            c.addMember(personId);
        }
        return new EvaluatedCohort(c, cohortDefinition, context);
    }

}
