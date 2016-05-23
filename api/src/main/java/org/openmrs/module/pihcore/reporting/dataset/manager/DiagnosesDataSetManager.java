package org.openmrs.module.pihcore.reporting.dataset.manager;

import org.openmrs.Concept;
import org.openmrs.module.pihcore.config.ConfigDescriptor;
import org.openmrs.module.pihcore.metadata.Metadata;
import org.openmrs.module.reporting.dataset.definition.ObsDataSetDefinition;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

/**
 * Data Export of diagnosis data, limited to an optional period and location
 *
 * TODO: currently used in Liberia, but Haiti uses old SQL export
 */
@Component
public class DiagnosesDataSetManager extends BaseObsDataSetManager {

    @Override
    protected List<Concept> getQuestionConcepts() {
        return Arrays.asList(Metadata.getConcept("PIH:Visit Diagnoses"));
    }

    @Override
    protected void addObsColumns(ObsDataSetDefinition dsd) {

        addObsColumn(dsd, "diagnosis", "PIH:DIAGNOSIS", converters.getObsValueCodedNameConverter());
        addObsColumn(dsd, "diagnosis_datetime", "PIH:DIAGNOSIS", converters.getObsDatetimeConverter());
        addObsColumn(dsd, "diagnosis_non_coded", "PIH:Diagnosis or problem, non-coded", converters.getObsValueTextConverter());
        addObsColumn(dsd, "diagnosis_non_coded_datetime", "PIH:Diagnosis or problem, non-coded", converters.getObsDatetimeConverter());
        addObsColumn(dsd, "certainty", "PIH:CLINICAL IMPRESSION DIAGNOSIS CONFIRMED", converters.getObsValueCodedNameConverter());
        addObsColumn(dsd, "order", "PIH:Diagnosis order", converters.getObsValueCodedNameConverter());

        // hack, have a better way of configuring this
        if (config.getCountry().equals(ConfigDescriptor.Country.LIBERIA)) {
            addObsColumn(dsd, "diagnosis_code", "PIH:DIAGNOSIS", converters.getObsValueCodedConceptCode("Liberia MoH"));
        }
    }
}
