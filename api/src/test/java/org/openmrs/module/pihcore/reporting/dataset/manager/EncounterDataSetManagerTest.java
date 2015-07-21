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

import org.junit.Before;
import org.openmrs.Encounter;
import org.openmrs.Patient;
import org.openmrs.PersonAddress;
import org.openmrs.PersonName;
import org.openmrs.api.context.Context;
import org.openmrs.contrib.testdata.TestDataManager;
import org.openmrs.contrib.testdata.builder.EncounterBuilder;
import org.openmrs.contrib.testdata.builder.PatientBuilder;
import org.openmrs.module.emrapi.EmrApiProperties;
import org.openmrs.module.pihcore.deploy.bundle.AddressComponent;
import org.openmrs.module.pihcore.deploy.bundle.core.concept.SocioEconomicConcepts;
import org.openmrs.module.pihcore.deploy.bundle.haiti.HaitiAddressBundle;
import org.openmrs.module.pihcore.deploy.bundle.haiti.HaitiPatientIdentifierTypeBundle;
import org.openmrs.module.pihcore.metadata.Metadata;
import org.openmrs.module.pihcore.metadata.core.EncounterTypes;
import org.openmrs.module.pihcore.metadata.core.PersonAttributeTypes;
import org.openmrs.module.pihcore.metadata.haiti.HaitiPatientIdentifierTypes;
import org.openmrs.module.pihcore.metadata.haiti.mirebalais.MirebalaisLocations;
import org.openmrs.module.pihcore.reporting.BaseReportTest;
import org.openmrs.module.pihcore.reporting.MockConcepts;
import org.openmrs.module.reporting.common.DateUtil;
import org.openmrs.module.reporting.common.ReflectionUtil;
import org.openmrs.module.reporting.dataset.definition.service.DataSetDefinitionService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Locale;

public abstract class EncounterDataSetManagerTest extends BaseReportTest {

    @Autowired
    protected DataSetDefinitionService dataSetDefinitionService;

    @Autowired
    protected SocioEconomicConcepts socioEconomicConcepts;

    @Autowired
    protected MockConcepts mockConcepts;

    @Autowired
    protected HaitiPatientIdentifierTypeBundle haitiPatientIdentifierTypeBundle;;

    @Before
    @Override
    public void setup() throws Exception {
        super.setup();
        Context.setLocale(Locale.ENGLISH);
        deployService.installBundle(haitiPatientIdentifierTypeBundle);
        deployService.installBundle(socioEconomicConcepts);
        deployService.installBundle(mockConcepts);;
    }

    protected Patient createPatient(String identifier) {
        PatientBuilder pb = data.patient();
        pb.name(new PersonName("John", "Smitty", "Smith"));
        pb.birthdate("1977-11-23").birthdateEstimated(false);
        pb.male();
        pb.personAttribute(Metadata.lookup(PersonAttributeTypes.TELEPHONE_NUMBER), "555-1234");
        pb.personAttribute(Metadata.lookup(PersonAttributeTypes.UNKNOWN_PATIENT), "false");
        pb.personAttribute(Metadata.lookup(PersonAttributeTypes.MOTHERS_FIRST_NAME), "Isabel");
        address(pb, haitiAddressBundle.getAddressComponents(), "USA", "MA", "Boston", "JP", "Pondside", "");
        pb.identifier(Metadata.lookup(HaitiPatientIdentifierTypes.ZL_EMR_ID), identifier, Metadata.lookup(MirebalaisLocations.MIREBALAIS_CDI_PARENT));
        return pb.save();
    }

    protected Encounter createRegistrationEncounter(Patient p) {
        EncounterBuilder eb = data.encounter();
        eb.patient(p);
        eb.encounterDatetime(DateUtil.getDateTime(2015, 4, 15));
        eb.location(Metadata.lookup(MirebalaisLocations.CLINIC_REGISTRATION));
        eb.encounterType(Metadata.lookup(EncounterTypes.PATIENT_REGISTRATION));

        // TODO: Add More Obs to test
        eb.obs(Metadata.getConcept(SocioEconomicConcepts.Concepts.CIVIL_STATUS), Metadata.getConcept(SocioEconomicConcepts.Concepts.MARRIED));
        eb.obs(Metadata.getConcept(SocioEconomicConcepts.Concepts.BIRTHPLACE), "Wichita");
        return eb.save();
    }

    protected Encounter createCheckInEncounter(Patient p) {
        EncounterBuilder eb = data.encounter();
        eb.patient(p);
        eb.encounterDatetime(DateUtil.getDateTime(2015, 4, 15));
        eb.location(Metadata.lookup(MirebalaisLocations.OUTPATIENT_CLINIC));
        eb.encounterType(Metadata.lookup(EncounterTypes.CHECK_IN));
        eb.obs("REASON FOR VISIT", "PIH", Metadata.getConcept("PIH:MALNUTRITION PROGRAM"));
        return eb.save();
    }

    protected PatientBuilder address(PatientBuilder pb, List<AddressComponent> addressComponents, String... values) {
        PersonAddress a = new PersonAddress();
        int index = 0;
        for (AddressComponent property : addressComponents) {
            ReflectionUtil.setPropertyValue(a, property.getField().getName(), values[index]);
            index++;
        }
        return pb.address(a);
    }
}
