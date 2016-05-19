package org.openmrs.module.pihcore.reporting.dataset.manager;

import org.openmrs.EncounterType;
import org.openmrs.module.pihcore.metadata.Metadata;
import org.openmrs.module.pihcore.metadata.core.EncounterTypes;
import org.openmrs.module.reporting.dataset.definition.EncounterDataSetDefinition;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

/**
 * Data Export of patient consultation data, limited to an optional period and location
 *
 * TODO: currently used in Liberia, but Haiti uses old SQL export
 */
@Component
public class ConsultationsDataSetManager extends BaseEncounterDataSetManager {

    @Override
    protected List<EncounterType> getEncounterTypes() {
        return Arrays.asList(Metadata.lookup(EncounterTypes.CONSULTATION));
    }

    @Override
    protected String getEncounterColumnPrefix() {
        return "consult";
    }

    @Override
    protected void addPatientNameColumns(EncounterDataSetDefinition dsd) {
        super.addPatientNameColumns(dsd);
    }

    @Override
    protected void addPersonAttributeColumns(EncounterDataSetDefinition dsd) {
        // Do not include person attributes in this export
    }

    @Override
    protected void addObsColumns(EncounterDataSetDefinition dsd) {

        // note: does not include diagnoses--use diagnoses export for thoses
        addObsColumn(dsd, "dispo", "PIH:8620", converters.getObsValueCodedNameConverter());
        addObsColumn(dsd, "death_date", "PIH:DATE OF DEATH", converters.getObsValueDatetimeConverter());
        addObsColumn(dsd, "return_visit_date", "PIH:RETURN VISIT DATE", converters.getObsValueDatetimeConverter());
        addObsColumn(dsd, "clinical_notes", "PIH:CLINICAL IMPRESSION COMMENTS", converters.getObsValueTextConverter());

        // TODO when/if we start to use this in Haiti, we will need to add disposition sub-fields like admission location, etc, that currently aren't used in Pleebo
    }
}
