package org.openmrs.module.pihcore.reporting.library;

import org.openmrs.Concept;
import org.openmrs.module.emrapi.EmrApiProperties;
import org.openmrs.module.reporting.data.converter.DataConverter;
import org.openmrs.module.reporting.data.encounter.definition.AgeAtEncounterDataDefinition;
import org.openmrs.module.reporting.data.encounter.definition.AuditInfoEncounterDataDefinition;
import org.openmrs.module.reporting.data.encounter.definition.ConvertedEncounterDataDefinition;
import org.openmrs.module.reporting.data.encounter.definition.EncounterDataDefinition;
import org.openmrs.module.reporting.data.encounter.definition.EncounterProviderDataDefinition;
import org.openmrs.module.reporting.data.encounter.definition.ObsForEncounterDataDefinition;
import org.openmrs.module.reporting.definition.library.BaseDefinitionLibrary;
import org.openmrs.module.reporting.definition.library.DocumentedDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PihEncounterDataLibrary extends BaseDefinitionLibrary<EncounterDataDefinition> {

    @Autowired
    EmrApiProperties emrApiProperties;

    @Autowired
    DataConverterLibrary converters;

    @Override
    public Class<? super EncounterDataDefinition> getDefinitionType() {
        return EncounterDataDefinition.class;
    }

    @Override
    public String getKeyPrefix() {
        return "pihcore.encounterData.";
    }

    @DocumentedDefinition
    public EncounterDataDefinition getAuditInfo() {
        return new AuditInfoEncounterDataDefinition();
    }

    @DocumentedDefinition
    public EncounterDataDefinition getCreatorName() {
        return convert(getAuditInfo(), converters.getAuditInfoCreatorNameConverter());
    }

    @DocumentedDefinition
    public EncounterDataDefinition getDateCreated() {
        return convert(getAuditInfo(), converters.getAuditInfoDateCreatedConverter());
    }

    @DocumentedDefinition
    public EncounterDataDefinition getEncounterProvider() {
        EncounterProviderDataDefinition epdd = new EncounterProviderDataDefinition();
        epdd.setSingleProvider(true);
        return epdd;
    }

    @DocumentedDefinition
    public EncounterDataDefinition getPatientAgeAtEncounter() {
        return new AgeAtEncounterDataDefinition();
    }

    @DocumentedDefinition
    public EncounterDataDefinition getSingleObsInEncounter(Concept concept) {
        ObsForEncounterDataDefinition d = new ObsForEncounterDataDefinition();
        d.setQuestion(concept);
        d.setSingleObs(true);
        return d;
    }

    protected ConvertedEncounterDataDefinition convert(EncounterDataDefinition d, DataConverter... converters) {
        return new ConvertedEncounterDataDefinition(d, converters);
    }
}
