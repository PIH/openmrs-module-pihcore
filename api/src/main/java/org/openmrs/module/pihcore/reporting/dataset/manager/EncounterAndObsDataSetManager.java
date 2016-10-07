package org.openmrs.module.pihcore.reporting.dataset.manager;

import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.EncounterAndObsDataSetDefinition;
import org.springframework.stereotype.Component;

@Component
public class EncounterAndObsDataSetManager {

    public DataSetDefinition constructDataSet() {
        EncounterAndObsDataSetDefinition dsd = new EncounterAndObsDataSetDefinition();
        return dsd;
    }
}
