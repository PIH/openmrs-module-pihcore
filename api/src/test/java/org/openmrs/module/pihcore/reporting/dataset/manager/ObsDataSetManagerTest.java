package org.openmrs.module.pihcore.reporting.dataset.manager;

import org.junit.Before;
import org.openmrs.Encounter;
import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.contrib.testdata.builder.EncounterBuilder;
import org.openmrs.module.pihcore.metadata.Metadata;
import org.openmrs.module.pihcore.metadata.core.EncounterTypes;
import org.openmrs.module.pihcore.reporting.BaseReportTest;
import org.openmrs.module.pihcore.reporting.MockConcepts;
import org.openmrs.module.reporting.common.DateUtil;
import org.openmrs.module.reporting.dataset.definition.service.DataSetDefinitionService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Locale;

public abstract class ObsDataSetManagerTest extends BaseReportTest {

    @Autowired
    protected DataSetDefinitionService dataSetDefinitionService;

    @Autowired
    protected MockConcepts mockConcepts;

    @Before
    @Override
    public void setup() throws Exception {
        super.setup();
        Context.setLocale(Locale.ENGLISH);
        deployService.installBundle(mockConcepts);;
    }

    protected Encounter createConsultEncounter(Patient p) {
        EncounterBuilder eb = data.encounter();
        eb.patient(p);
        eb.encounterDatetime(DateUtil.getDateTime(2015, 4, 15));
        eb.location(locationService.getLocation("Klinik Ekstèn"));
        eb.encounterType(Metadata.lookup(EncounterTypes.CONSULTATION));
        eb.obs("DIAGNOSIS", "PIH", Metadata.getConcept("PIH:ASTHMA"));
        return eb.save();
    }


}
