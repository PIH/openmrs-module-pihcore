package org.openmrs.module.pihcore.reporting.library;

import org.openmrs.module.emrapi.EmrApiProperties;
import org.openmrs.module.reporting.common.AuditInfo;
import org.openmrs.module.reporting.data.converter.DataConverter;
import org.openmrs.module.reporting.data.converter.PropertyConverter;
import org.openmrs.module.reporting.data.encounter.definition.AgeAtEncounterDataDefinition;
import org.openmrs.module.reporting.data.encounter.definition.AuditInfoEncounterDataDefinition;
import org.openmrs.module.reporting.data.encounter.definition.ConvertedEncounterDataDefinition;
import org.openmrs.module.reporting.data.encounter.definition.EncounterDataDefinition;
import org.openmrs.module.reporting.data.patient.definition.ConvertedPatientDataDefinition;
import org.openmrs.module.reporting.data.patient.definition.EncountersForPatientDataDefinition;
import org.openmrs.module.reporting.data.patient.definition.PatientDataDefinition;
import org.openmrs.module.reporting.data.patient.definition.PersonToPatientDataDefinition;
import org.openmrs.module.reporting.data.person.definition.AgeAtDateOfOtherDataDefinition;
import org.openmrs.module.reporting.data.person.definition.PersonDataDefinition;
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
        return convert(getAuditInfo(), new PropertyConverter(AuditInfo.class, "creator"), converters.getUserAsNameConverter());
    }

    @DocumentedDefinition
    public EncounterDataDefinition getPatientAgeAtEncounter() {
        return new AgeAtEncounterDataDefinition();
    }

    protected ConvertedEncounterDataDefinition convert(EncounterDataDefinition d, DataConverter... converters) {
        return new ConvertedEncounterDataDefinition(d, converters);
    }
}
