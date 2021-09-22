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

package org.openmrs.module.pihcore.reporting.dataset.evaluator;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.openmrs.Concept;
import org.openmrs.PatientIdentifierType;
import org.openmrs.Provider;
import org.openmrs.User;
import org.openmrs.annotation.Handler;
import org.openmrs.api.PatientService;
import org.openmrs.api.UserService;
import org.openmrs.module.emrapi.EmrApiProperties;
import org.openmrs.module.pihcore.reporting.dataset.definition.NonCodedDiagnosisDataSetDefinition;
import org.openmrs.module.reporting.common.DateUtil;
import org.openmrs.module.reporting.common.ObjectUtil;
import org.openmrs.module.reporting.dataset.DataSet;
import org.openmrs.module.reporting.dataset.DataSetColumn;
import org.openmrs.module.reporting.dataset.DataSetRow;
import org.openmrs.module.reporting.dataset.SimpleDataSet;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.evaluator.DataSetEvaluator;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.List;

@Handler(supports = NonCodedDiagnosisDataSetDefinition.class)
public class NonCodedDiagnosisDataSetEvaluator implements DataSetEvaluator {

    @Autowired
    private SessionFactory sessionFactory;

    @Autowired
    private EmrApiProperties emrApiProperties;

    @Autowired
    private UserService userService;

    @Autowired
    private PatientService patientService;

    private final Log log = LogFactory.getLog(getClass());

	@Override
	public DataSet evaluate(DataSetDefinition dataSetDefinition, EvaluationContext context) throws EvaluationException {
        Long startTime = new Date().getTime();
		NonCodedDiagnosisDataSetDefinition dsd = (NonCodedDiagnosisDataSetDefinition) dataSetDefinition;

		Date fromDate = ObjectUtil.nvl(dsd.getFromDate(), DateUtils.addDays(new Date(), -7));
		Date toDate = ObjectUtil.nvl(dsd.getToDate(), new Date());
		fromDate = DateUtil.getStartOfDay(fromDate);
		toDate = DateUtil.getEndOfDay(toDate);
        String nonCoded = ObjectUtil.nvl(dsd.getNonCoded(),null);
        Provider provider = ObjectUtil.nvl(dsd.getProvider(), null);
        Integer userId = null;
        if (provider != null) {
            List<User> users = userService.getUsersByPerson(provider.getPerson(), true);
            if (users !=null && users.size() > 0){
                userId = users.get(0).getId();
            }
        }

        PatientIdentifierType primaryIdentifierType = emrApiProperties.getPrimaryIdentifierType();
        Concept nonCodedConcept = emrApiProperties.getDiagnosisMetadata().getNonCodedDiagnosisConcept();

        StringBuilder sqlQuery = new StringBuilder("select " +
                "    o.value_text as 'nonCodedDiagnosis', " +
                "    o.creator as 'creatorId', " +
                "    n.given_name as 'creatorFirstName', " +
                "    n.family_name as 'creatorLastName', " +
                "    o.date_created as 'dateCreated', " +
                "    o.person_id as 'patientId', " +
                "    id1.identifier as 'patientIdentifier', " +
                "    o.obs_id as 'obsId', " +
                "    e.visit_id as 'visitId', " +
                "    e.encounter_datetime as 'encounterDateTime', " +
                "    n1.given_name as 'patientFirstName', " +
                "    n1.family_name as 'patientLastName'");
        sqlQuery.append(" from obs o ");
        sqlQuery.append(" inner join patient_identifier as id1 on (o.person_id = id1.patient_id and id1.identifier_type = :primaryIdentifierType ) ");
        sqlQuery.append(" inner join encounter as e on (o.encounter_id = e.encounter_id) ");
        sqlQuery.append(" inner join users as u on (o.creator = u.user_id) ");
        sqlQuery.append(" inner join person_name as n on (u.person_id = n.person_id and n.voided=0) " );
        sqlQuery.append(" inner join person_name as n1 on (o.person_id = n1.person_id and n1.voided=0) " );
        sqlQuery.append(" ");
        sqlQuery.append(" where o.voided = 0  ");
        sqlQuery.append(" and o.concept_id = :nonCodedConcept " );
        if (fromDate != null) {
            sqlQuery.append(" and o.date_created > :startDate " );
        }
        if (toDate != null) {
            sqlQuery.append(" and o.date_created < :endDate " );
        }
        if (userId != null){
            sqlQuery.append(" and o.creator = :userId " );
        }
        if (StringUtils.isNotBlank(nonCoded)) {
            sqlQuery.append(" and o.value_text like '%").append(nonCoded).append("%'");
        }

        SQLQuery query = sessionFactory.getCurrentSession().createSQLQuery(sqlQuery.toString());
        query.setInteger("primaryIdentifierType", primaryIdentifierType.getId());
        query.setInteger("nonCodedConcept", nonCodedConcept.getId());
        if (fromDate != null) {
            query.setTimestamp("startDate", fromDate);
        }
        if (toDate != null) {
            query.setTimestamp("endDate", toDate);
        }
        if (userId != null){
            query.setInteger("userId", userId);
        }
        List<Object[]> list = query.list();
        SimpleDataSet dataSet = new SimpleDataSet(dataSetDefinition, context);
        for (Object[] o : list) {
            DataSetRow row = new DataSetRow();
            row.addColumnValue(new DataSetColumn("nonCodedDiagnosis", "nonCodedDiagnosis", String.class), o[0]);
            row.addColumnValue(new DataSetColumn("creatorId", "creatorId", String.class), o[1]);
            row.addColumnValue(new DataSetColumn("creatorFirstName", "creatorFirstName", String.class), o[2]);
            row.addColumnValue(new DataSetColumn("creatorLastName", "creatorLastName", String.class), o[3]);
            row.addColumnValue(new DataSetColumn("dateCreated", "dateCreated", String.class), o[4]);
            row.addColumnValue(new DataSetColumn("patientId", "patientId", String.class), o[5]);
            row.addColumnValue(new DataSetColumn("patientIdentifier", "patientIdentifier", String.class), o[6]);
            row.addColumnValue(new DataSetColumn("obsId", "obsId", String.class), o[7]);
            row.addColumnValue(new DataSetColumn("visitId", "visitId", String.class), o[8]);
            row.addColumnValue(new DataSetColumn("encounterDateTime", "encounterDateTime", String.class), o[9]);
            row.addColumnValue(new DataSetColumn("patientFirstName", "patientFirstName", String.class), o[10]);
            row.addColumnValue(new DataSetColumn("patientLastName", "patientLastName", String.class), o[11]);
            dataSet.addRow(row);
        }
		return dataSet;
	}
}
