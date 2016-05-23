package org.openmrs.module.pihcore.reporting.library;

import org.openmrs.Concept;
import org.openmrs.module.reporting.data.converter.DataConverter;
import org.openmrs.module.reporting.data.obs.definition.ConvertedObsDataDefinition;
import org.openmrs.module.reporting.data.obs.definition.EncounterToObsDataDefinition;
import org.openmrs.module.reporting.data.obs.definition.GroupMemberObsDataDefinition;
import org.openmrs.module.reporting.data.obs.definition.ObsDataDefinition;
import org.openmrs.module.reporting.definition.library.BaseDefinitionLibrary;
import org.openmrs.module.reporting.definition.library.DocumentedDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PihObsDataLibrary extends BaseDefinitionLibrary<ObsDataDefinition> {

    @Autowired
    DataConverterLibrary converters;

    @Override
    public Class<? super ObsDataDefinition> getDefinitionType() {
        return ObsDataDefinition.class;
    }

    @Override
    public String getKeyPrefix() {
        return "pihcore.obsData.";
    }

    @DocumentedDefinition
    public ObsDataDefinition getEncounter() {
        return new EncounterToObsDataDefinition();
    }

    @DocumentedDefinition
    public ObsDataDefinition getEncounterDatetime() {
        return convert(getEncounter(), converters.getEncounterDatetimeConverter());
    }

    @DocumentedDefinition
    public ObsDataDefinition getLocationName() {
        return convert(getEncounter(), converters.getEncounterLocationNameConverter());
    }

    @DocumentedDefinition
    public ObsDataDefinition getSingleObsInGroup(Concept question) {
        GroupMemberObsDataDefinition d = new GroupMemberObsDataDefinition();
        d.setQuestion(question);
        d.setSingleObs(true);
        return d;
    }

    protected ConvertedObsDataDefinition convert(ObsDataDefinition d, DataConverter... converters) {
        return new ConvertedObsDataDefinition(d, converters);
    }

}




