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
import org.openmrs.module.pihcore.reporting.cohort.definition.InpatientTransferCohortDefinition;
import org.openmrs.module.reporting.cohort.EvaluatedCohort;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.evaluator.CohortDefinitionEvaluator;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Handler(supports = InpatientTransferCohortDefinition.class)
public class InpatientTransferCohortDefinitionEvaluator implements CohortDefinitionEvaluator {

    @Autowired
    AdtService adtService;

    @Autowired
    EmrApiProperties emrApiProperties;

    @Autowired
    private SessionFactory sessionFactory;

    @Override
    public EvaluatedCohort evaluate(CohortDefinition cohortDefinition, EvaluationContext context) throws EvaluationException {
        InpatientTransferCohortDefinition cd = (InpatientTransferCohortDefinition) cohortDefinition;

        Location outOfWard = cd.getOutOfWard();
        Location inToWard = cd.getInToWard();
        if (inToWard == null && outOfWard == null) {
            throw new IllegalArgumentException("Must specify outOfWard and/or inToWard");
        }
        Location visitLocation = adtService.getLocationThatSupportsVisits(outOfWard != null ? outOfWard : inToWard);
        if (visitLocation == null) {
            throw new IllegalArgumentException(outOfWard + " and its ancestor locations don't support visits");
        }

        EncounterType admissionEncounterType = emrApiProperties.getAdmissionEncounterType();
        EncounterType dischargeEncounterType = emrApiProperties.getExitFromInpatientEncounterType();
        EncounterType transferEncounterType = emrApiProperties.getTransferWithinHospitalEncounterType();

        String sql = "select distinct v.patient_id " +
                "from visit v " +
                "inner join encounter admission " +
                "  on v.visit_id = admission.visit_id " +
                "  and admission.voided = false " +
                "  and admission.encounter_type = :admissionEncounterType " +
                "  and admission.encounter_datetime <= :onOrBefore " +
                "inner join encounter transfer " +
                "  on v.visit_id = transfer.visit_id " +
                "  and transfer.voided = false " +
                "  and transfer.encounter_type = :transferEncounterType " +
                "  and transfer.encounter_datetime between :onOrAfter and :onOrBefore " +
                "  and transfer.encounter_datetime > admission.encounter_datetime ";

        if (inToWard != null) {
            sql += " and transfer.location_id = :inToWard ";
        }

        sql += "inner join encounter adtBeforeTransfer " +
                "  on v.visit_id = adtBeforeTransfer.visit_id " +
                "  and adtBeforeTransfer.voided = false " +
                "  and adtBeforeTransfer.encounter_type in (:adtEncounterTypes) " +
                "  and adtBeforeTransfer.encounter_id = ( " +
                "    select encounter_id " +
                "    from encounter " +
                "    where visit_id = v.visit_id " +
                "    and voided = false " +
                "    and encounter_type in (:adtEncounterTypes) " +
                "    and encounter_datetime < transfer.encounter_datetime " +
                "    order by encounter_datetime desc, date_created desc limit 1" +
                "  ) " +
                "where v.voided = false" +
                "  and v.location_id = :visitLocation " +
                "  and adtBeforeTransfer.encounter_type in (:admitOrTransferEncounterTypes)";
        if (outOfWard != null) {
            sql += "  and adtBeforeTransfer.location_id = :outOfWard ";
        }

        SQLQuery query = sessionFactory.getCurrentSession().createSQLQuery(sql);

        query.setInteger("admissionEncounterType", admissionEncounterType.getId());
        query.setInteger("transferEncounterType", transferEncounterType.getId());
        query.setTimestamp("onOrBefore", cd.getOnOrBefore());
        query.setTimestamp("onOrAfter", cd.getOnOrAfter());
        query.setInteger("visitLocation", visitLocation.getId());
        if (outOfWard != null) {
            query.setInteger("outOfWard", outOfWard.getId());
        }
        if (inToWard != null) {
            query.setInteger("inToWard", inToWard.getId());
        }
        query.setParameterList("adtEncounterTypes", new Integer[] { admissionEncounterType.getId(), dischargeEncounterType.getId(), transferEncounterType.getId() });
        query.setParameterList("admitOrTransferEncounterTypes", new Integer[] { admissionEncounterType.getId(), transferEncounterType.getId() });

        Cohort c = new Cohort();
        for (Integer i : (List<Integer>) query.list()){
            c.addMember(i);
        }
        return new EvaluatedCohort(c, cohortDefinition, context);
    }

}
