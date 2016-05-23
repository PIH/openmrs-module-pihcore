package org.openmrs.module.pihcore.reporting.library;

import org.openmrs.Concept;
import org.openmrs.module.reporting.common.ObjectUtil;
import org.openmrs.module.reporting.definition.library.BaseDefinitionLibrary;
import org.openmrs.module.reporting.definition.library.DocumentedDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.query.obs.definition.BasicObsQuery;
import org.openmrs.module.reporting.query.obs.definition.MappedParametersObsQuery;
import org.openmrs.module.reporting.query.obs.definition.ObsQuery;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

@Component
public class PihObsQueryLibrary extends BaseDefinitionLibrary<ObsQuery> {


    public static final String PREFIX = "pihcore.obsQuery.";

    @Override
    public Class<? super ObsQuery> getDefinitionType() {
        return ObsQuery.class;
    }

    @Override
    public String getKeyPrefix() {
        return PREFIX;
    }

    @DocumentedDefinition(value = "obsDuringPeriod")
    public ObsQuery getObsDuringPeriod(List<Concept> questionConcepts) {
        BasicObsQuery q = new BasicObsQuery();
        q.setConceptList(questionConcepts);
        q.addParameter(parameter(Date.class, "onOrAfter"));
        q.addParameter(parameter(Date.class, "onOrBefore"));
        return new MappedParametersObsQuery(q, ObjectUtil.toMap("onOrAfter=startDate,onOrBefore=endDate"));
    }

    public Parameter parameter(Class<?> clazz, String name) {
        return new Parameter(name, "mirebalaisreports.parameter." + name, clazz);
    }

}
