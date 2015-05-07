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
import org.openmrs.Concept;
import org.openmrs.Location;
import org.openmrs.OpenmrsObject;
import org.openmrs.annotation.Handler;
import org.openmrs.module.emrapi.EmrApiProperties;
import org.openmrs.module.emrapi.disposition.DispositionService;
import org.openmrs.module.pihcore.reporting.cohort.definition.LastDispositionBeforeExitCohortDefinition;
import org.openmrs.module.reporting.cohort.EvaluatedCohort;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.evaluator.CohortDefinitionEvaluator;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

@Handler(supports = LastDispositionBeforeExitCohortDefinition.class)
public class LastDispositionBeforeExitCohortDefinitionEvaluator implements CohortDefinitionEvaluator {

    @Autowired
    SessionFactory sessionFactory;

    @Autowired
    private EmrApiProperties emrApiProperties;

    @Autowired
    private DispositionService dispositionService;

    @Override
    public EvaluatedCohort evaluate(CohortDefinition cohortDefinition, EvaluationContext context) throws EvaluationException {
        LastDispositionBeforeExitCohortDefinition cd = (LastDispositionBeforeExitCohortDefinition) cohortDefinition;

        Location exitFromWard = cd.getExitFromWard();
        List<Concept> dispositions = cd.getDispositions();
        List<Concept> dispositionsToConsider = cd.getDispositionsToConsider();

        String sql = "select distinct v.patient_id " +
                "from visit v " +
                "inner join encounter exit_encounter " +
                " on exit_encounter.visit_id = v.visit_id " +
                " and exit_encounter.voided = false " +
                " and exit_encounter.encounter_type = :exitEncounterType " +
                " and exit_encounter.encounter_datetime between :exitOnOrAfter and :exitOnOrBefore ";
        if (exitFromWard != null) {
            sql += " and exit_encounter.location_id = :exitFromWard ";
        }
        sql += "inner join encounter obs_encounter " +
                " on obs_encounter.visit_id = v.visit_id " +
                " and obs_encounter.encounter_id = (" +
                "   select find_obs_encounter.encounter_id " +
                "   from encounter find_obs_encounter " +
                "   inner join obs has_obs " +
                "     on has_obs.encounter_id = find_obs_encounter.encounter_id " +
                "     and has_obs.voided = false " +
                "     and has_obs.concept_id = :dispositionConcept ";
        if (dispositionsToConsider != null) {
            sql += "     and has_obs.value_coded in (:dispositionsToConsider) ";
        }
        sql += "    where find_obs_encounter.visit_id = v.visit_id " +
                "     and find_obs_encounter.voided = false " +
                "    order by find_obs_encounter.encounter_datetime desc, find_obs_encounter.date_created desc limit 1 " +
                // if we wanted to require disposition at the same location as exit
                // "     and find_obs_encounter.location_id = :exitFromWard " +
                " )" +
                "inner join obs o " +
                " on o.voided = false " +
                " and o.concept_id = :dispositionConcept " +
                " and o.encounter_id = obs_encounter.encounter_id " +
                "where v.voided = false " +
                " and o.value_coded in (:dispositions) ";

        SQLQuery query = sessionFactory.getCurrentSession().createSQLQuery(sql);
        query.setInteger("dispositionConcept", dispositionService.getDispositionDescriptor().getDispositionConcept().getId());
        query.setParameterList("dispositions", idList(dispositions));
        query.setInteger("exitEncounterType", emrApiProperties.getExitFromInpatientEncounterType().getId());
        query.setTimestamp("exitOnOrAfter", cd.getExitOnOrAfter());
        query.setTimestamp("exitOnOrBefore", cd.getExitOnOrBefore());
        if (exitFromWard != null) {
            query.setInteger("exitFromWard", exitFromWard.getId());
        }
        if (dispositionsToConsider != null) {
            query.setParameterList("dispositionsToConsider", idList(dispositionsToConsider));
        }

        Cohort c = new Cohort();
        for (Integer i : (List<Integer>) query.list()){
            c.addMember(i);
        }
        return new EvaluatedCohort(c, cohortDefinition, context);
    }

    private List<Integer> idList(List<? extends OpenmrsObject> list) {
        List<Integer> asIds = new ArrayList<Integer>();
        for (OpenmrsObject o : list) {
            asIds.add(o.getId());
        }
        return asIds;
    }

}
