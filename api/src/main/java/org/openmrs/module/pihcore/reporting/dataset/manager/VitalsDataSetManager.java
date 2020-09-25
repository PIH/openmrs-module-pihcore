package org.openmrs.module.pihcore.reporting.dataset.manager;

import org.openmrs.EncounterType;
import org.openmrs.module.pihcore.config.ConfigDescriptor;
import org.openmrs.module.pihcore.metadata.Metadata;
import org.openmrs.module.pihcore.metadata.core.EncounterTypes;
import org.openmrs.module.pihcore.reporting.encounter.definition.BmiEncounterDataDefinition;
import org.openmrs.module.reporting.dataset.definition.EncounterDataSetDefinition;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class VitalsDataSetManager extends BaseEncounterDataSetManager {

    @Override
    protected List<EncounterType> getEncounterTypes() {
        return Arrays.asList(Metadata.lookup(EncounterTypes.VITALS));
    }

    @Override
    protected String getEncounterColumnPrefix() {
        return "vitals";
    }

    @Override
    protected void addPatientNameColumns(EncounterDataSetDefinition dsd) {
        // don't include names in this report
    }

    @Override
    protected void addAddressColumns(EncounterDataSetDefinition dsd) {
        // don't include addresses in this report
    }

    @Override
    protected void addPersonAttributeColumns(EncounterDataSetDefinition dsd) {
        // don't include person attributes in this report
    }

    @Override
    protected void addObsColumns(EncounterDataSetDefinition dsd) {
        addObsColumn(dsd, "WEIGHT_KG", "PIH:WEIGHT (KG)", converters.getObsValueNumericConverter());
        addObsColumn(dsd, "HEIGHT_CM", "PIH:HEIGHT (CM)", converters.getObsValueNumericConverter());
        addColumn(dsd, "BMI", new BmiEncounterDataDefinition(), null);
        addObsColumn(dsd, "MUAC", "PIH:MIDDLE UPPER ARM CIRCUMFERENCE (MM)", converters.getObsValueNumericConverter());
        addObsColumn(dsd, "TEMP_C", "PIH:TEMPERATURE (C)", converters.getObsValueNumericConverter());
        addObsColumn(dsd, "HEART_RATE", "PIH:PULSE", converters.getObsValueNumericConverter());
        addObsColumn(dsd, "RESP_RATE", "PIH:RESPIRATORY RATE", converters.getObsValueNumericConverter());
        addObsColumn(dsd, "SYS_BP", "PIH:SYSTOLIC BLOOD PRESSURE", converters.getObsValueNumericConverter());
        addObsColumn(dsd, "DIA_BP", "PIH:DIASTOLIC BLOOD PRESSURE", converters.getObsValueNumericConverter());
        addObsColumn(dsd, "O2_SAT", "PIH:BLOOD OXYGEN SATURATION", converters.getObsValueNumericConverter());
        addObsColumn(dsd, "CHIEF_COMPLAINT", "CIEL:160531", converters.getObsValueTextConverter());

        if (config.getCountry().equals(ConfigDescriptor.Country.SIERRA_LEONE)) {
            addSymptomPresentOrAbsentColumn(dsd, "TB_SCREENING_FEVER_NIGHT_SWEATS",
                    "PIH:11563", "PIH:11564",
                    "PIH:11565", "Yes", "No");
            addSymptomPresentOrAbsentColumn(dsd, "TB_SCREENING_LOSS_OF_APPETITE",
                    "PIH:11563", "PIH:11564",
                    "PIH:ANOREXIA", "Yes", "No");
            addSymptomPresentOrAbsentColumn(dsd, "TB_SCREENING_WEIGHT_LOSS",
                    "PIH:11563", "PIH:11564",
                    "PIH:11566", "Yes", "No");
            addSymptomPresentOrAbsentColumn(dsd, "TB_SCREENING_COUGH",
                    "PIH:11563", "PIH:11564",
                    "PIH:COUGH LASTING GREATER THAN TWO WEEKS", "Yes", "No");
            addSymptomPresentOrAbsentColumn(dsd, "TB_SCREENING_CHEST_PAIN",
                    "PIH:11563", "PIH:11564",
                    "PIH:1579", "Yes", "No");
        }
    }
}
