package org.openmrs.module.pihcore.reporting.encounter.definition;

import org.openmrs.module.reporting.data.BaseDataDefinition;
import org.openmrs.module.reporting.data.encounter.definition.EncounterDataDefinition;
import org.openmrs.module.reporting.definition.configuration.ConfigurationPropertyCachingStrategy;
import org.openmrs.module.reporting.evaluation.caching.Caching;

/**
 * Calculates whether an encounter was entered retrospectively or not, where we consider a retrospective encounter
 * one in which the the encounter date is 30 minutes or more before the date created
 */
@Caching(strategy=ConfigurationPropertyCachingStrategy.class)
public class RetrospectiveEncounterDataDefinition extends BaseDataDefinition implements EncounterDataDefinition {

    public static final long serialVersionUID = 1L;

    /**
     * Default Constructor
     */
    public RetrospectiveEncounterDataDefinition() {
        super();
    }

    /**
     * Constructor to populate name only
     */
    public RetrospectiveEncounterDataDefinition(String name) {
        super(name);
    }

    //***** INSTANCE METHODS *****

    public Class<?> getDataType() {
        return Boolean.class;
    }
}