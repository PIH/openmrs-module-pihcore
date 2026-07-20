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

package org.openmrs.module.pihcore.reporting.query.encounter.evaluator;

import org.openmrs.Encounter;
import org.openmrs.annotation.Handler;
import org.openmrs.module.pihcore.reporting.query.encounter.definition.EncounterVisitLocationQuery;
import org.openmrs.module.reporting.common.ObjectUtil;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.evaluation.querybuilder.HqlQueryBuilder;
import org.openmrs.module.reporting.evaluation.service.EvaluationService;
import org.openmrs.module.reporting.query.encounter.EncounterQueryResult;
import org.openmrs.module.reporting.query.encounter.definition.EncounterQuery;
import org.openmrs.module.reporting.query.encounter.evaluator.EncounterQueryEvaluator;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * Evaluates an {@link EncounterVisitLocationQuery}, filtering encounters to those whose
 * associated Visit has the configured location. If no location is configured, all encounters match.
 */
@Handler(supports = EncounterVisitLocationQuery.class)
public class EncounterVisitLocationQueryEvaluator implements EncounterQueryEvaluator {

	@Autowired
	EvaluationService evaluationService;

	@Override
	public EncounterQueryResult evaluate(EncounterQuery definition, EvaluationContext context) throws EvaluationException {
		context = ObjectUtil.nvl(context, new EvaluationContext());
		EncounterVisitLocationQuery query = (EncounterVisitLocationQuery) definition;
		EncounterQueryResult result = new EncounterQueryResult(query, context);

		HqlQueryBuilder q = new HqlQueryBuilder();
		q.select("e.encounterId");
		q.from(Encounter.class, "e");
		q.whereEqual("e.visit.location", query.getVisitLocation());
		q.whereEncounterIn("e.encounterId", context);

		List<Integer> results = evaluationService.evaluateToList(q, Integer.class, context);
		result.getMemberIds().addAll(results);
		return result;
	}
}
