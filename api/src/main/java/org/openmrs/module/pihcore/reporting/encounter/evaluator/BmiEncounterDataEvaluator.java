package org.openmrs.module.pihcore.reporting.encounter.evaluator;

import org.openmrs.Obs;
import org.openmrs.annotation.Handler;
import org.openmrs.module.pihcore.metadata.Metadata;
import org.openmrs.module.pihcore.reporting.encounter.definition.BmiEncounterDataDefinition;
import org.openmrs.module.reporting.data.encounter.EvaluatedEncounterData;
import org.openmrs.module.reporting.data.encounter.definition.EncounterDataDefinition;
import org.openmrs.module.reporting.data.encounter.definition.ObsForEncounterDataDefinition;
import org.openmrs.module.reporting.data.encounter.evaluator.EncounterDataEvaluator;
import org.openmrs.module.reporting.data.encounter.service.EncounterDataService;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

@Handler(supports=BmiEncounterDataDefinition.class, order=50)
public class BmiEncounterDataEvaluator implements EncounterDataEvaluator {

    @Autowired
    EncounterDataService encounterDataService;

    @Override
    public EvaluatedEncounterData evaluate(EncounterDataDefinition definition, EvaluationContext context) throws EvaluationException {

        ObsForEncounterDataDefinition weightDataDefinition = new ObsForEncounterDataDefinition();
        weightDataDefinition.setQuestion(Metadata.getConcept("PIH:WEIGHT (KG)"));
        weightDataDefinition.setSingleObs(true);
        EvaluatedEncounterData weights = encounterDataService.evaluate(weightDataDefinition, context);

        ObsForEncounterDataDefinition heightDataDefinition = new ObsForEncounterDataDefinition();
        heightDataDefinition.setQuestion(Metadata.getConcept("PIH:HEIGHT (CM)"));
        heightDataDefinition.setSingleObs(true);
        EvaluatedEncounterData heights = encounterDataService.evaluate(heightDataDefinition, context);

        EvaluatedEncounterData ret = new EvaluatedEncounterData(definition, context);
        for (Map.Entry<Integer, Object> entry : weights.getData().entrySet()) {
            Integer encId = entry.getKey();

            Obs height = (Obs) heights.getData().get(encId);
            Obs weight = (Obs) weights.getData().get(encId);

            if (height != null && height.getValueNumeric() != null && weight != null && weight.getValueNumeric() != null) {
                Double bmi =  Math.rint((weight.getValueNumeric() / ((height.getValueNumeric() /100) * (height.getValueNumeric() / 100))) * 10) / 10;
                ret.addData(encId, bmi);
            }
            else {
                ret.addData(encId, null);
            }
        }
        return ret;
    }
}


