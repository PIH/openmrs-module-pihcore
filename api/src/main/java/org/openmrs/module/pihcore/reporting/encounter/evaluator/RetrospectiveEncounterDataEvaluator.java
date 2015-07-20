package org.openmrs.module.pihcore.reporting.encounter.evaluator;

import org.joda.time.DateTime;
import org.joda.time.Minutes;
import org.openmrs.annotation.Handler;
import org.openmrs.module.pihcore.reporting.encounter.definition.RetrospectiveEncounterDataDefinition;
import org.openmrs.module.reporting.data.encounter.EvaluatedEncounterData;
import org.openmrs.module.reporting.data.encounter.definition.EncounterDataDefinition;
import org.openmrs.module.reporting.data.encounter.evaluator.EncounterDataEvaluator;
import org.openmrs.module.reporting.data.encounter.library.BuiltInEncounterDataLibrary;
import org.openmrs.module.reporting.data.encounter.service.EncounterDataService;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

@Handler(supports=RetrospectiveEncounterDataDefinition.class, order=50)
public class RetrospectiveEncounterDataEvaluator implements EncounterDataEvaluator {

    @Autowired
    private BuiltInEncounterDataLibrary builtInEncounterData;

    @Autowired
    private EncounterDataService encounterDataService;


    @Override
    public EvaluatedEncounterData evaluate(EncounterDataDefinition definition, EvaluationContext context) throws EvaluationException {

        EvaluatedEncounterData encounterDatetimes = encounterDataService.evaluate(builtInEncounterData.getEncounterDatetime(), context);
        EvaluatedEncounterData encounterDatesCreated = encounterDataService.evaluate(builtInEncounterData.getDateCreated(), context);

        EvaluatedEncounterData ret = new EvaluatedEncounterData(definition, context);
        for (Map.Entry<Integer, Object> entry : encounterDatetimes.getData().entrySet()) {
            Integer encId = entry.getKey();
            DateTime encounterDatetime = new DateTime(encounterDatetimes.getData().get(encId));
            DateTime dateCreated = new DateTime(encounterDatesCreated.getData().get(encId));
            ret.addData(encId, Minutes.minutesBetween(encounterDatetime, dateCreated).getMinutes() > 30);
        }
        return ret;
    }
}


