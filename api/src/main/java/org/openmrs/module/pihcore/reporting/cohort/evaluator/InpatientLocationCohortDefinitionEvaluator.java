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
import org.openmrs.EncounterType;
import org.openmrs.Location;
import org.openmrs.annotation.Handler;
import org.openmrs.module.emrapi.EmrApiProperties;
import org.openmrs.module.emrapi.adt.AdtService;
import org.openmrs.module.pihcore.reporting.cohort.definition.InpatientLocationCohortDefinition;
import org.openmrs.module.reporting.cohort.EvaluatedCohort;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.evaluator.CohortDefinitionEvaluator;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.List;

@Handler(supports = InpatientLocationCohortDefinition.class)
public class InpatientLocationCohortDefinitionEvaluator implements CohortDefinitionEvaluator {

    @Autowired
    AdtService adtService;

    @Autowired
    EmrApiProperties emrApiProperties;

    @Autowired
    private SessionFactory sessionFactory;

    @Override
    public EvaluatedCohort evaluate(CohortDefinition cohortDefinition, EvaluationContext context) throws EvaluationException {
        InpatientLocationCohortDefinition cd = (InpatientLocationCohortDefinition) cohortDefinition;

        Date onDate = cd.getEffectiveDate();
        if (onDate == null) {
            onDate = new Date();
        }

        Location ward = cd.getWard();

        Location visitLocation = null ;
        if (ward != null ) {
            visitLocation = adtService.getLocationThatSupportsVisits(ward);
        }

        EncounterType admissionEncounterType = emrApiProperties.getAdmissionEncounterType();
        EncounterType dischargeEncounterType = emrApiProperties.getExitFromInpatientEncounterType();
        EncounterType transferEncounterType = emrApiProperties.getTransferWithinHospitalEncounterType();
        StringBuilder sb = new StringBuilder("select distinct v.patient_id " +
                "from visit v " +
                "inner join encounter admission " +
                "  on v.visit_id = admission.visit_id " +
                "  and admission.voided = false " +
                "  and admission.encounter_type = :admissionEncounterType " +
                "  and admission.encounter_datetime <= :onDate " +
                "inner join encounter mostRecentAdt " +
                "  on v.visit_id = mostRecentAdt.visit_id " +
                "  and mostRecentAdt.encounter_id = ( " +
                "    select encounter_id " +
                "    from encounter " +
                "    where visit_id = v.visit_id " +
                "    and voided = false " +
                "    and encounter_type in (:adtEncounterTypes) " +
                "    and encounter_datetime <= :onDate " +
                "    order by encounter_datetime desc, date_created desc limit 1" +
                "  ) ");
        sb.append("where v.voided = false");
        if (visitLocation != null) {
            sb.append("  and v.location_id = :visitLocation ");
        }
        sb.append("  and v.date_started <= :onDate ");
        sb.append("  and (v.date_stopped is null or v.date_stopped > :onDate) ");
        if (ward != null ){
            sb.append("  and mostRecentAdt.location_id = :ward ");
        }
        sb.append("  and mostRecentAdt.encounter_type in (:admitOrTransferEncounterTypes)");
        SQLQuery query = sessionFactory.getCurrentSession().createSQLQuery(sb.toString());
       
        query.setInteger("admissionEncounterType", admissionEncounterType.getId());
        query.setTimestamp("onDate", onDate);
        if (visitLocation != null) {
            query.setInteger("visitLocation", visitLocation.getId());
        }
        if (ward != null ) {
            query.setInteger("ward", ward.getId());
        }
        query.setParameterList("adtEncounterTypes", new Integer[]{admissionEncounterType.getId(), dischargeEncounterType.getId(), transferEncounterType.getId()});
        query.setParameterList("admitOrTransferEncounterTypes", new Integer[] { admissionEncounterType.getId(), transferEncounterType.getId() });

        // This does not actually work: org.hibernate.hql.ast.QuerySyntaxException: with-clause referenced two different from-clause elements
//        Query hql = sessionFactory.getCurrentSession().createQuery("select distinct(v.patient.id) " +
//                "from Visit v " +
//                "join v.encounters as mostRecentAdt " +
//                "    with mostRecentAdt.voided = false " +
//                "    and mostRecentAdt.encounterType in (:adtEncounterTypes) " +
//                "    and mostRecentAdt.encounterDatetime = ( " +
//                "        select max(encounterDatetime)" +
//                "        from Encounter " +
//                "        where visit = v " +
//                "        and voided = false " +
//                "        and encounterType in (:adtEncounterTypes) " +
//                "        and encounterDatetime <= :onDate " +
//                "    ) " +
//                "where v.voided = false " +
//                "and v.location = :visitLocation " +
//                "and v.startDatetime <= :onDate " +
//                "and (v.stopDatetime is null or v.stopDatetime > :onDate) " +
//                "and exists ( " +
//                "    from Encounter admission " +
//                "    where admission.visit = v " +
//                "    and admission.voided = false " +
//                "    and admission.encounterType = :admissionEncounterType " +
//                "    and admission.encounterDatetime <= :onDate " +
//                ") " +
//                "and mostRecentAdt.location = :ward " +
//                "and mostRecentAdt.encounterType in (:admitOrTransferEncounterTypes) ");
//
//        hql.setParameter("onDate", onDate);
//        hql.setParameter("visitLocation", visitLocation);
//        hql.setParameter("ward", ward);
//        hql.setParameter("admissionEncounterType", admissionEncounterType);
//        hql.setParameterList("adtEncounterTypes", adtEncounterTypes);
//        hql.setParameterList("admitOrTransferEncounterTypes", admitOrTransferEncounterTypes);

        Cohort c = new Cohort();
        for (Integer i : (List<Integer>) query.list()){
            c.addMember(i);
        }
        return new EvaluatedCohort(c, cohortDefinition, context);
    }

}
