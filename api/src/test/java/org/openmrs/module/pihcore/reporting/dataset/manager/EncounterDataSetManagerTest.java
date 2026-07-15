/*
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */

package org.openmrs.module.pihcore.reporting.dataset.manager;

import org.junit.jupiter.api.BeforeEach;
import org.openmrs.Encounter;
import org.openmrs.Location;
import org.openmrs.Patient;
import org.openmrs.Visit;
import org.openmrs.api.context.Context;
import org.openmrs.contrib.testdata.builder.EncounterBuilder;
import org.openmrs.module.pihcore.deploy.bundle.core.concept.InsuranceConcepts;
import org.openmrs.module.pihcore.deploy.bundle.core.concept.SocioEconomicConcepts;
import org.openmrs.module.pihcore.metadata.Metadata;
import org.openmrs.module.pihcore.reporting.BaseReportTest;
import org.openmrs.module.pihcore.reporting.MockConcepts;
import org.openmrs.module.reporting.common.DateUtil;
import org.openmrs.module.reporting.dataset.definition.service.DataSetDefinitionService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Locale;

public abstract class EncounterDataSetManagerTest extends BaseReportTest {

    @Autowired
    protected DataSetDefinitionService dataSetDefinitionService;

    @Autowired
    protected SocioEconomicConcepts socioEconomicConcepts;

    @Autowired
    protected InsuranceConcepts insuranceConcepts;

    @Autowired
    protected MockConcepts mockConcepts;

    @BeforeEach
    @Override
    public void setup() throws Exception {
        super.setup();
        Context.setLocale(Locale.ENGLISH);
        deployService.installBundle(socioEconomicConcepts);
        deployService.installBundle(insuranceConcepts);
        deployService.installBundle(mockConcepts);

    }

    protected Encounter createRegistrationEncounter(Patient p) {
        return createRegistrationEncounter(p, null);
    }

    protected Encounter createRegistrationEncounter(Patient p, Visit visit) {
        EncounterBuilder eb = data.encounter();
        eb.patient(p);
        eb.encounterDatetime(DateUtil.getDateTime(2015, 4, 15));
        eb.location(locationService.getLocation("Biwo Resepsyon"));
        eb.encounterType(getRegistrationEncounterType());
        if (visit != null) {
            eb.visit(visit);
        }

        // TODO: Add More Obs to test
        eb.obs(Metadata.getConcept(SocioEconomicConcepts.Concepts.CIVIL_STATUS), Metadata.getConcept(SocioEconomicConcepts.Concepts.MARRIED));
        eb.obs(Metadata.getConcept(SocioEconomicConcepts.Concepts.BIRTHPLACE), "Wichita");
        return eb.save();
    }

    protected Encounter createCheckInEncounter(Patient p) {
        return createCheckInEncounter(p, null);
    }

    protected Encounter createCheckInEncounter(Patient p, Visit visit) {
        EncounterBuilder eb = data.encounter();
        eb.patient(p);
        eb.encounterDatetime(DateUtil.getDateTime(2015, 4, 15));
        eb.location(locationService.getLocation("Klinik Ekstèn"));
        eb.encounterType(getCheckInEncounterType());
        if (visit != null) {
            eb.visit(visit);
        }
        eb.obs("REASON FOR VISIT", "PIH", Metadata.getConcept("PIH:MALNUTRITION PROGRAM"));
        return eb.save();
    }

    protected Visit createVisit(Patient p, Location location) {
        return data.visit().patient(p).visitType(emrApiProperties.getAtFacilityVisitType())
                .started(DateUtil.getDateTime(2015, 4, 15)).location(location).save();
    }

}
