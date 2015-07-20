package org.openmrs.module.pihcore.reporting.encounter.definition;

import org.openmrs.module.reporting.data.BaseDataDefinition;
import org.openmrs.module.reporting.data.encounter.definition.EncounterDataDefinition;
import org.openmrs.module.reporting.definition.configuration.ConfigurationPropertyCachingStrategy;
import org.openmrs.module.reporting.evaluation.caching.Caching;

@Caching(strategy=ConfigurationPropertyCachingStrategy.class)
public class BmiEncounterDataDefinition extends BaseDataDefinition implements EncounterDataDefinition {

    public static final long serialVersionUID = 1L;


    public BmiEncounterDataDefinition() {
    }

    public BmiEncounterDataDefinition(String name) {
        super(name);
    }

    @Override
    public Class<?> getDataType() {
        return Double.class;
    }
}
